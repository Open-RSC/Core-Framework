package com.loader.openrsc.net;

import com.loader.openrsc.Constants;
import com.loader.openrsc.OpenRSC;
import com.loader.openrsc.frame.AppFrame;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Properties;
import java.util.Set;

public class Downloader extends Observable {
	private final String[] nicename = new String[]{"Client", "Library", "Landscape",
		"Graphics", "3D Models",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound", "Game Sound", "Game Sound",
		"Game Sound", "Game Sound"};

	private final String[] normalName = new String[]{"Open_RSC_Client.jar", "library.orsc", "Landscape.orsc",
		"Sprites.orsc", "models.orsc",
		"advance.wav", "anvil.wav", "chisel.wav", "click.wav",
		"closedoor.wav", "coins.wav", "takeobject.wav", "victory.wav",
		"combat1a.wav", "combat1b.wav", "combat2a.wav", "combat2b.wav",
		"combat3a.wav", "combat3b.wav", "cooking.wav", "death.wav",
		"dropobject.wav", "eat.wav", "filljug.wav", "fish.wav",
		"foundgem.wav", "recharge.wav", "underattack.wav",
		"mechanical.wav", "mine.wav", "mix.wav", "spellok.wav",
		"opendoor.wav", "out_of_ammo.wav", "potato.wav", "spellfail.wav",
		"prayeroff.wav", "prayeron.wav", "prospect.wav", "shoot.wav",
		"retreat.wav", "secretdoor.wav"};

	public Downloader() {

	}

	private static byte[] createChecksum(File file) throws Exception {
		InputStream fis = new FileInputStream(file);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	private static String getMD5Checksum(File file) throws Exception {
		byte[] b = createChecksum(file);
		StringBuilder result = new StringBuilder();

		for (byte b1 : b) {
			result.append(Integer.toString((b1 & 0xff) + 0x100, 16).substring(1));
		}
		return result.toString();
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
		File file = new File(Constants.CONF_DIR + File.separator);
		if (!file.exists()) {
			file.mkdir();
		}
		load("MD5CHECKSUM");
		Properties old = new Properties();
		Properties new1 = new Properties();
		try {
			File f = new File(Constants.CONF_DIR + File.separator + "MD5CHECKSUM.old");
			if (!f.exists()) {
				f.createNewFile();
			}
			FileInputStream fi = new FileInputStream(f);
			FileInputStream f2 = new FileInputStream(Constants.CONF_DIR + File.separator + "MD5CHECKSUM");
			old.load(fi);
			new1.load(f2);
			fi.close();
			f2.close();
		} catch (Exception e) {
			System.out.println("Unable to load checksums.");
			System.exit(1);
		}

		deleteNonExistant(old.keySet(), new1.keySet());
		updateIfNeeded(old.entrySet(), new1.entrySet());
		downloadNew(old.keySet(), new1.keySet());
		while (true) {
			if (verifyDownloads(new1.entrySet())) break;
			//Verify and re-download until its all good.
		}
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

	private boolean verifyDownloads(Set<Entry<Object, Object>> set) {
		boolean verified = true;
		for (Entry<Object, Object> entry : set) {

			System.out.println("---------------------------------------");
			String fileName = (String) entry.getKey();
			String hash = (String) entry.getValue();
			//AppFrame.get().setDownloadProgress("Verifying file: " + getNiceName(fileName), 95);

			System.out.println(fileName + ": " + hash);
			File downloadedFile = new File(Constants.CONF_DIR + File.separator + File.separator + fileName);

			/*if(!downloadedFile.exists()) {
				System.out.println("Updating file: " + fileName);
				update(fileName);
				verified = false;
			}*/

			String downloadedFileHash = null;
			try {
				downloadedFileHash = getMD5Checksum(downloadedFile).toLowerCase();
			} catch (Exception e) {
				e.printStackTrace();
				OpenRSC.getPopup().setMessage("" + e);
			}

			assert downloadedFileHash != null;
			if (!downloadedFileHash.equalsIgnoreCase(hash)) {
				OpenRSC.getPopup().setMessage(downloadedFile.getName() + " hash:" + downloadedFileHash + " doesn't match MD5: " + hash + " re-downloading");
				load(fileName, true);
				verified = false;
			}
			AppFrame.get().getLaunch().setEnabled(true);
			AppFrame.get().setDownloadProgress("Game is now ready to be started!", 100);
		}
		return verified;
	}

	private String getNiceName(String s) {
		for (int i = 0; i < normalName.length; i++) {
			if (normalName[i].equalsIgnoreCase(s)) {
				return nicename[i];
			}
		}
		return "File";
	}

	private File load(String filename) {
		return load(filename, false);
	}

	private File load(String filename, boolean b) {
		File f = new File(Constants.CONF_DIR + File.separator + filename);
		if (!f.exists() || b) {
			f.delete();
			try {
				download(f, getNiceName(filename));
			} catch (Exception e) {
				e.printStackTrace();
				System.out.printf("Failed to load %s.", f.getName());
				System.exit(1);
			}
			return load(filename);
		} else
			return f;
	}

	private void downloadNew(Set<Object> keySet, Set<Object> keySet2) {
		for (Object o : keySet2) {
			if (!keySet.contains(o)) {
				update((String) o);
			}
		}
	}

	private void updateIfNeeded(Set<Entry<Object, Object>> entrySet,
								Set<Entry<Object, Object>> entrySet2) {
		for (Entry<Object, Object> e : entrySet) {
			for (Entry<Object, Object> e1 : entrySet2) {
				if (e1.getKey().equals(e.getKey()) && !e1.getValue().equals(e.getValue())) {
					update((String) e.getKey());
				}
			}
		}
	}

	private void deleteNonExistant(Set<Object> keySet,
								   Set<Object> keySet2) {
		for (Object o : keySet) {
			if (!keySet2.contains(o)) {
				new File(Constants.CONF_DIR + File.separator + (String) o).delete();
			}
		}
	}

	private void update(String filename) {
		new File(Constants.CONF_DIR + File.separator + filename).delete();
		load(filename);
	}

	public void doneLoading() {
		try {
			File old = new File(Constants.CONF_DIR + File.separator + "MD5CHECKSUM.old");
			File new1 = new File(Constants.CONF_DIR + File.separator + "MD5CHECKSUM");

			old.delete();
			new1.renameTo(old);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void download(File f, String string) throws Exception {

		AppFrame.get().getLaunch().setEnabled(false);
		URL url = new URL(Constants.CACHE_URL + f.getName());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		FileOutputStream fos = new FileOutputStream(Constants.CONF_DIR + File.separator + f.getName());
		try {
			try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
				try {
					try (BufferedOutputStream bout = new BufferedOutputStream(fos, 1024)) {
						int filesize = connection.getContentLength();
						float totalDataRead = 0;

						byte[] data = new byte[4096];
						int i = 0;
						while ((i = in.read(data, 0, 4096)) >= 0) {
							totalDataRead = totalDataRead + i;
							bout.write(data, 0, i);
							float Percent = (totalDataRead * 100) / filesize;
							AppFrame.get().setDownloadProgress(string, Percent);
						}
					}
				} finally {
					fos.close();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
