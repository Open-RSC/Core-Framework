package launcher.Gameupdater;

import launcher.Main;
import launcher.Utils.Defaults;
import launcher.Utils.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Downloader implements Runnable {

	private final ArrayList<String> _EXCLUDED_FILES = new ArrayList<>();
	private final ArrayList<String> _REFUSE_UPDATE = new ArrayList<>();
	private final String _GAMEFOLDER;

	public static boolean offline_start = false;
	public static boolean currently_updating = false;

	private List<File> FILE_LIST = new ArrayList<>();

	public Downloader(String gameFolder, List<File> fileList) {
		this._EXCLUDED_FILES.add(Defaults._MD5_TABLE_FILENAME);
		this._EXCLUDED_FILES.add("android_version.txt");
		this._EXCLUDED_FILES.add("android_version_pk.txt");
		this._EXCLUDED_FILES.add("openrsc.apk");
		this._EXCLUDED_FILES.add("openpk.apk");
		this._EXCLUDED_FILES.add("credentials.txt");
		this._EXCLUDED_FILES.add("config.txt");
		this._EXCLUDED_FILES.add("discord_inuse.txt");
		this._EXCLUDED_FILES.add("uid.dat");
		this._EXCLUDED_FILES.add("OpenRSC.jar");
		this._EXCLUDED_FILES.add("ip.txt");
		this._EXCLUDED_FILES.add("port.txt");
		this._EXCLUDED_FILES.add("Open_RSC_Client_dev.jar"); // possibly would want to download this separate
		this._GAMEFOLDER = gameFolder;
		this.FILE_LIST = fileList;
	}

	public void initOpenRSCClientUpdate() {
		currently_updating = true;
		ProgressBar.initProgressBar();
		try {
			ProgressBar.setDownloadProgress("Checking for updates", 100.0f);
			// Populate MD5 checksums
			File currentMd5Table = new File(this._GAMEFOLDER + File.separator + Defaults._MD5_TABLE_FILENAME);
			if (currentMd5Table.exists())
				currentMd5Table.delete();
			downloadOne(new File(Defaults._MD5_TABLE_FILENAME));
			Md5Handler localCache = new Md5Handler(currentMd5Table.getParentFile(), this._GAMEFOLDER);
			Md5Handler remoteCache = new Md5Handler(currentMd5Table, this._GAMEFOLDER);

			List<File> downloadList = new ArrayList();
			for (Md5Handler.Entry entry : remoteCache.entries) {
				if (_EXCLUDED_FILES.contains(entry.getRef().getName()))
					continue;
				entry.getRef().getParentFile().mkdirs();
				String localSum = localCache.getRefSum(entry.getRef());
				if (localSum != null) {
					if (_REFUSE_UPDATE.contains(entry.getRef().getName()) ||
						localSum.equalsIgnoreCase(entry.getSum())) {
						continue;
					}
				}

				downloadList.add(entry.getDownloadRef());
			}
			download(downloadList);

		} catch (Exception error) {
			Logger.Error("Unable to load checksums.");
			error.printStackTrace();
		}
	}

	private void download(List<File> fileList) {
		Thread t = new Thread(new Downloader(Main.configFileLocation, fileList));
		t.start();
	}

	private String getDescription(File ref) {
		if (true) { // TODO: make a setting I guess
			return ref.getName();
		}
		int index = ref.getName().lastIndexOf('.');
		if (index == -1)
			return "General";
		else {
			String extension = ref.getName().substring(index + 1);
			if (extension.equalsIgnoreCase("ospr"))
				return "Graphics";
			else if (extension.equalsIgnoreCase("wav"))
				return "Audio";
			else if (extension.equalsIgnoreCase("orsc"))
				return "Graphics";
			else if (extension.equalsIgnoreCase("jar"))
				return "Executable";
			else if (extension.equalsIgnoreCase("xm"))
				return "Module";
			else
				return "General";

		}
	}

	private void downloadOne(File file) {
		try {
			String filename = file.toString().replaceAll("\\\\", "/");
			String completeFileUrl = Defaults._GAME_FILES_SERVER + filename;
			Logger.Info("Downloading: " + completeFileUrl);

			URLConnection connection = new URL(completeFileUrl).openConnection();

			// File metadata
			String description = getDescription(file);
			long fileSize = connection.getContentLength();

			try (BufferedInputStream inputStream = new BufferedInputStream(new URL(completeFileUrl).openStream());
				 FileOutputStream fileOS = new FileOutputStream(this._GAMEFOLDER + File.separator + filename)) {
				byte[] data = new byte[1024];
				int byteContent;
				double totalRead = 0;
				while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
					totalRead += byteContent;
					fileOS.write(data, 0, byteContent);
          float percent = (float) (totalRead / fileSize) * 100;
					ProgressBar.setDownloadProgress(description, percent);
				}
			} catch (UnknownHostException uhe) {
				offline_start = true;
			} catch (Exception error) {
				error.printStackTrace();
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	@Override
	public void run() {
		Logger.Info(this.FILE_LIST.size() + " files to download");
		for (File file : this.FILE_LIST) {
			downloadOne(file);
		}
		ProgressBar.setDownloadProgress(ProgressBar.doneText, ProgressBar.donePercent);
		currently_updating = false;
	}
}
