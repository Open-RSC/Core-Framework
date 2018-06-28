package com.runescape.client.cache;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class CacheUtil {

	public static int getShort(byte[] buffer, int offset) {
		return ((buffer[offset] & 0xff) << 8) + (buffer[offset + 1] & 0xff);
	}

	public static byte[] loadArchive(String file) {
		int decomp = 0;
		int comp = 0;
		byte archive[] = null;
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(file));
			DataInputStream dis = new DataInputStream(in);
			byte head[] = new byte[6];
			dis.readFully(head, 0, 6);
			decomp = ((head[0] & 0xff) << 16) + ((head[1] & 0xff) << 8) + (head[2] & 0xff);
			comp = ((head[3] & 0xff) << 16) + ((head[4] & 0xff) << 8) + (head[5] & 0xff);
			int offset = 0;
			archive = new byte[comp];
			while (offset < comp) {
				int len = comp - offset;
				if (len > 1000) {
					len = 1000;
				}
				dis.readFully(archive, offset, len);
				offset += len;
			}
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (comp != decomp) {
			byte decompArc[] = new byte[decomp];
			DataFileDecrypter.unpackData(decompArc, decomp, archive, comp, 0);
			return decompArc;
		} else {
			return archive;
		}
	}

	public static int fileOffset(String file, byte[] arc) {
		int entries = getShort(arc, 0);
		int hash = 0;

		file = file.toUpperCase();

		for (int i = 0; i < file.length(); i++) {
			hash = (hash * 61 + file.charAt(i)) - 32;
		}

		int offset = 2 + entries * 10;
		for (int i = 0; i < entries; i++) {
			int entryHash = (arc[i * 10 + 2] & 0xff) * 0x1000000 + (arc[i * 10 + 3] & 0xff) * 0x10000 + (arc[i * 10 + 4] & 0xff) * 256 + (arc[i * 10 + 5] & 0xff);
			int entryLen = (arc[i * 10 + 9] & 0xff) * 0x10000 + (arc[i * 10 + 10] & 0xff) * 256 + (arc[i * 10 + 11] & 0xff);

			if (entryHash == hash) {
				return offset;
			}
			offset += entryLen;
		}
		return 0;
	}

	public static int fileLength(String file, byte[] arc) {
		int entries = getShort(arc, 0);
		int hash = 0;
		file = file.toUpperCase();

		for (int i = 0; i < file.length(); i++) {
			hash = (hash * 61 + file.charAt(i)) - 32;
		}

		for (int i = 0; i < entries; i++) {
			int entryHash = (arc[i * 10 + 2] & 0xff) * 0x1000000 + (arc[i * 10 + 3] & 0xff) * 0x10000 + (arc[i * 10 + 4] & 0xff) * 256 + (arc[i * 10 + 5] & 0xff);
			int entryLen = (arc[i * 10 + 6] & 0xff) * 0x10000 + (arc[i * 10 + 7] & 0xff) * 256 + (arc[i * 10 + 8] & 0xff);

			if (entryHash == hash) {
				return entryLen;
			}
		}
		return 0;
	}

	public static byte[] fileContents(String file, byte[] arc) {
		int entries = getShort(arc, 0);
		int hash = 0;
		file = file.toUpperCase();

		for (int i = 0; i < file.length(); i++) {
			hash = (hash * 61 + file.charAt(i)) - 32;
		}

		int offset = 2 + entries * 10;
		for (int i = 0; i < entries; i++) {
			int entryHash = (arc[i * 10 + 2] & 0xff) * 0x1000000 + (arc[i * 10 + 3] & 0xff) * 0x10000 + (arc[i * 10 + 4] & 0xff) * 256 + (arc[i * 10 + 5] & 0xff);
			int entryDecompLen = (arc[i * 10 + 6] & 0xff) * 0x10000 + (arc[i * 10 + 7] & 0xff) * 256 + (arc[i * 10 + 8] & 0xff);
			int entryCompLen = (arc[i * 10 + 9] & 0xff) * 0x10000 + (arc[i * 10 + 10] & 0xff) * 256 + (arc[i * 10 + 11] & 0xff);

			if (entryHash == hash) {
				byte[] dest = new byte[entryDecompLen];
				if (entryDecompLen != entryCompLen) {
					DataFileDecrypter.unpackData(dest, entryDecompLen, arc, entryCompLen, offset);
				} else {
					for (int j = 0; j < entryDecompLen; j++) {
						dest[j] = arc[offset + j];
					}
				}
				return dest;
			}
			offset += entryCompLen;
		}
		return null;
	}

}
