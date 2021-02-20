package launcher.Gameupdater;

import launcher.Gameupdater.UpdaterGui.MainUpdaterGui;
import launcher.Utils.Defaults;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Downloader {

	private final ArrayList<String> _EXCLUDED_FILES = new ArrayList<>();
	private final ArrayList<String> _REFUSE_UPDATE = new ArrayList<>();
	private final String _GAMEFOLDER;
	private final MainUpdaterGui _UPDATERGUI;

	public Downloader(String gameFolder) {
		this._EXCLUDED_FILES.add(Defaults._MD5_TABLE_FILENAME);
		this._EXCLUDED_FILES.add("android_version.txt");
		this._EXCLUDED_FILES.add("android_version_pk.txt");
		this._EXCLUDED_FILES.add("openrsc.apk");
		this._EXCLUDED_FILES.add("openpk.apk");
		this._EXCLUDED_FILES.add("credentials.txt");
		this._EXCLUDED_FILES.add("config.txt");
		this._EXCLUDED_FILES.add("discord_inuse.txt");
		this._EXCLUDED_FILES.add("OpenRSC.jar");
		this._GAMEFOLDER = gameFolder;
		this._UPDATERGUI = new MainUpdaterGui();
		this._UPDATERGUI.init();
	}

	public void initUpdate() {
		this._UPDATERGUI.build();
		try {
			MainUpdaterGui.get().setDownloadProgress("Checking for updates", 100.0f);
			// Populate MD5 checksums
			File currentMd5Table = new File(this._GAMEFOLDER + File.separator + Defaults._MD5_TABLE_FILENAME);
			if (currentMd5Table.exists())
				currentMd5Table.delete();
			Download(new File(Defaults._MD5_TABLE_FILENAME));
			Md5Handler localCache = new Md5Handler(currentMd5Table.getParentFile(), this._GAMEFOLDER);
			Md5Handler remoteCache = new Md5Handler(currentMd5Table, this._GAMEFOLDER);
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
				Download(entry.getDownloadRef());
			}
		} catch (Exception error) {
			System.out.println("Unable to load checksums.");
			error.printStackTrace();
		}
		_UPDATERGUI.hideWin();
	}

	private void Download(File file) {
		try {
			String filename = file.toString().replaceAll("\\\\", "/");
			String completeFileUrl = Defaults._GAME_FILES_SERVER + filename;
			URLConnection connection = new URL(completeFileUrl).openConnection();

			// File metadata
			String description = getDescription(file);
			int fileSize = connection.getContentLength();

			try (BufferedInputStream inputStream = new BufferedInputStream(new URL(completeFileUrl).openStream());
				 FileOutputStream fileOS = new FileOutputStream(this._GAMEFOLDER + File.separator + filename)) {
				byte[] data = new byte[1024];
				int byteContent;
				int totalRead = 0;
				while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
					totalRead += byteContent;
					fileOS.write(data, 0, byteContent);
					MainUpdaterGui.get().setDownloadProgress(description, (float) (totalRead * 100 / fileSize));
				}
			} catch (Exception error) {
				error.printStackTrace();
			}
		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	private String getDescription(File ref) {
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
}
