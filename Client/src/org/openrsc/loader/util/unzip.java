package org.openrsc.loader.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.openrsc.client.loader.various.AppletUtils;

public class unzip {
	
	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}
	
	public unzip(String unzipFile) {
		Enumeration<?> entries;
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(unzipFile);
			entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
								if (entry.isDirectory()) {
					(new File(
                            AppletUtils.CACHE
							+ System.getProperty("file.separator")
							+ entry.getName())).mkdirs();
					continue;
				}
				copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(
                                AppletUtils.CACHE
								+ System.getProperty("file.separator")
								+ entry.getName())));
			}
			zipFile.close();
		} catch (Exception ioe) {
			ioe.printStackTrace();
			return;
		}
	}
	
}