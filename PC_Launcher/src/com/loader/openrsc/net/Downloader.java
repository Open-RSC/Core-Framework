package com.loader.openrsc.net;

import com.loader.openrsc.Constants;
import com.loader.openrsc.frame.AppFrame;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


public class Downloader {

	private ArrayList<String> excludedFiles = new ArrayList<>();
	private ArrayList<String> refuseUpdate = new ArrayList<>();

	public Downloader() {
		excludedFiles.add(Constants.MD5_TABLENAME);
		refuseUpdate.add("config.txt");
	}

	private static boolean checkVersionNumber() {
		try {
			Double currentVersion = 0.0;
			URL updateURL =
				new URL(Constants.VERSION_UPDATE_URL);

			// Open connection
			URLConnection connection = updateURL.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				if (line.contains("VERSION_NUMBER")) {
					currentVersion =
						Double.parseDouble(line.substring(line.indexOf('=') + 1, line.indexOf(';')));
					break;
				}
			}

			// Close connection
			in.close();
			return currentVersion.equals(Constants.VERSION_NUMBER);
		} catch (Exception e) {
			return false;
		}
	}

	public void init() {
		boolean hadUpdate = false;

		try {
			AppFrame.get().getLaunch().setEnabled(false);
			AppFrame.get().setDownloadProgress("Checking for updates...", 100.0f);

			File file = new File(Constants.CONF_DIR);
			if (!file.exists()) {
				file.mkdir();
			}

			File MD5Table = new File(Constants.CONF_DIR, Constants.MD5_TABLENAME);
			if (MD5Table.exists()) {
				MD5Table.delete();
			}

			download(MD5Table);

			md5 localCache = new md5(MD5Table.getParentFile());
			md5 remoteCache = new md5(MD5Table);

			for (md5.Entry entry : remoteCache.entries) {
				if (excludedFiles.contains(entry.getRef().getName()))
					continue;

				entry.getRef().getParentFile().mkdirs();

				String localSum = localCache.getRefSum(entry.getRef());
				if (localSum != null) {
					if (refuseUpdate.contains(entry.getRef().getName()) ||
						localSum.equalsIgnoreCase(entry.getSum())) {
						continue;
					}
				}

				download(entry.getRef());
				hadUpdate = true;
			}

			//Delete unneeded files, while preserving sprite packs
			for (md5.Entry entry : localCache.entries) {
				if (entry.getRef().getParentFile().toString().equalsIgnoreCase(Constants.SPRITEPACK_DIR))
					continue;

				if (!remoteCache.hasRef(entry.getRef()))
					entry.getRef().delete();
			}
		} catch (Exception e) {
			System.out.println("Unable to load checksums.");
			System.exit(1);
		}

		//Verify the cache
		if (hadUpdate)
			init();

	}

	public void updateJar() {
		boolean success = true;
		try {
			if (checkVersionNumber()) // Check if version is the same
				return; // and return false if it is.

			URL url = new URL(Constants.UPDATE_JAR_URL);

			// Open connection
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);

			int size = connection.getContentLength();
			int offset = 0;
			byte[] data = new byte[size];

			InputStream input = url.openStream();

			int readSize;
			while ((readSize = input.read(data, offset, size - offset)) != -1) {
				offset += readSize;
			}

			if (offset != size) {
				success = false;
			} else {
				File file = new File("./" + Constants.JAR_FILENAME);
				FileOutputStream output = new FileOutputStream(file);
				output.write(data);
				output.close();
			}
		} catch (Exception e) {
			success = false;
		}

	}

//	private boolean verifyDownloads(Set<Entry<Object, Object>> set) {
//		boolean verified = true;
//		for (Entry<Object, Object> entry : set) {
//
//			System.out.println("---------------------------------------");
//			String fileName = (String) entry.getKey();
//			String hash = (String) entry.getValue();
//
//			System.out.println(fileName + ": " + hash);
//			File downloadedFile = new File(Constants.CONF_DIR + File.separator + File.separator + fileName);
//			System.out.println(downloadedFile.toPath().toAbsolutePath());
//			String downloadedFileHash = null;
//			try {
//				downloadedFileHash = md5.getMD5Checksum(downloadedFile).toLowerCase();
//			} catch (Exception e) {
//				e.printStackTrace();
//				Launcher.getPopup().setMessage("" + e);
//			}
//
//			assert downloadedFileHash != null;
//			if (!downloadedFileHash.equalsIgnoreCase(hash)) {
//				Launcher.getPopup().setMessage(downloadedFile.getName() + " hash:" + downloadedFileHash + " doesn't match MD5: " + hash + " re-downloading");
//				load(fileName, true);
//				verified = false;
//			}
//			AppFrame.get().getLaunch().setEnabled(true);
//			AppFrame.get().setDownloadProgress("Game is now ready to be started!", 100);
//		}
//		return verified;
//	}

	public void doneLoading() {
		try {
			File old = new File(Constants.CONF_DIR, Constants.MD5_TABLENAME);
			if (old.exists())
				old.delete();

			AppFrame.get().getLaunch().setEnabled(true);
			AppFrame.get().unlockGameSelection();
			AppFrame.get().getSpriteCombo().loadSpritePacks();
			AppFrame.get().setDownloadProgress("Ready to play!", 100.0f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void download(File file) {
		try {
			String fileURL = file.toString().replace(Constants.CONF_DIR + File.separator, Constants.CACHE_URL).replace(File.separator, "/");
			String description = getDescription(file);
			AppFrame.get().setDownloadProgress(description, 0);
			HttpURLConnection connection = (HttpURLConnection) new URL(fileURL).openConnection();
			try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
				 FileOutputStream fileOutputStream = new FileOutputStream(file)) {
				int filesize = connection.getContentLength();
				byte dataBuffer[] = new byte[1024];
				int bytesRead;
				int totalRead = 0;
				while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
					totalRead += bytesRead;
					fileOutputStream.write(dataBuffer, 0, bytesRead);
					AppFrame.get().setDownloadProgress(description, (float)(totalRead * 100 / filesize));
				}
				AppFrame.get().setDownloadProgress(description, 100.0f);
			} catch (IOException e) {
				e.printStackTrace();
			}
			connection.disconnect();
		} catch (Exception a) { a.printStackTrace(); }
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
			else
				return "General";
		}
	}
}
