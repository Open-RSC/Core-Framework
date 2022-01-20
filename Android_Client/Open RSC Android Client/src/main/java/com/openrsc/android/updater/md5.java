package com.openrsc.android.updater;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import orsc.osConfig;

public class md5 {
	List<Entry> entries = new ArrayList<>();

	public md5(File file, String prefix) {
		File newFile = new File(prefix, file.toString());
		if (newFile.isFile()) {
			loadFromMD5Table(file);
		} else if (newFile.isDirectory()) {
			loadFromDirectory(file);
		}
	}

	private void loadFromMD5Table(File file) {
		try {
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				Entry newEntry = new Entry(scanner.nextLine());
				if (!newEntry.getRef().getName().equals(osConfig.MD5_TABLENAME))
					entries.add(newEntry);
			}
		} catch (Exception a) { a.printStackTrace(); }
	}

	private void loadFromDirectory(File directory) {
		File[] files = directory.listFiles();

		assert files != null;
		for (File file : files) {
			if (file.isDirectory()) {
				loadFromDirectory(file);
			} else if (file.isFile()){
				entries.add(new Entry(file));
			}
		}
	}

	public String getRefSum(File file) {
		for (Entry entry : entries) {
			if (entry.getRef().compareTo(file) == 0)
				return entry.getSum();
		}

		return null;
	}

	public static class Entry {
		private final String sum;
		private final File ref;

		public Entry(String mixedline) {
			sum = mixedline.substring(0, 32);
			String path = mixedline.substring(35);
			path.replace(Pattern.quote("/"), File.separator);
			ref = new File(path);
		}

		public Entry(File file) {
			ref = file;
			sum = getMD5Checksum(file);
		}

		public String getSum() { return this.sum; }
		public File getRef() { return this.ref; }
	}

	public static String getMD5Checksum(File file) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			FileInputStream fIS = new FileInputStream(file);
			byte[] fileData = new byte[(int) file.length()];
			fIS.read(fileData);
			fIS.close();
			md.update(fileData);
			byte[] hashData = md.digest();

			StringBuffer hexString = new StringBuffer();

			for (byte hashDatum : hashData) {
				if ((0xff & hashDatum) < 0x10) {
					hexString.append("0").append(Integer.toHexString((0xFF & hashDatum)));
				} else {
					hexString.append(Integer.toHexString(0xFF & hashDatum));
				}
			}

			return hexString.toString();
		} catch (Exception a) { a.printStackTrace(); return null; }
	}
}
