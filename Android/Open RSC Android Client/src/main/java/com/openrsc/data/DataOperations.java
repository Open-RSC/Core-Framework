package com.openrsc.data;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DataOperations {
	private static final char[] special_characters = "~`!@#$%^&*()_-+={}[]|'\";:?><,./".toCharArray();

	private static int baseLengthArray[] = {0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383,
		32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff,
		0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1};

	static {
		Arrays.sort(special_characters);
	}

	public static InputStream streamFromPath(String path) throws IOException {
		return new BufferedInputStream(new FileInputStream(path));
	}

	public static void readFromPath(String path, byte abyte0[], int length) throws IOException {
		InputStream inputstream = streamFromPath(path);
		DataInputStream datainputstream = new DataInputStream(inputstream);
		try {
			datainputstream.readFully(abyte0, 0, length);
		} catch (EOFException _ex) {
			_ex.printStackTrace();
		}
		datainputstream.close();
	}

	public static int getUnsignedByte(byte byte0) {
		return byte0 & 0xff;
	}

	public static int getUnsigned2Bytes(byte abyte0[], int i) {
		return ((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff);
	}

	public static int getUnsigned4Bytes(byte abyte0[], int i) {
		return ((abyte0[i] & 0xff) << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8)
			+ (abyte0[i + 3] & 0xff);
	}

	public static long getUnsigned8Bytes(byte abyte0[], int i) {
		return (((long) getUnsigned4Bytes(abyte0, i) & 0xffffffffL) << 32)
			+ ((long) getUnsigned4Bytes(abyte0, i + 4) & 0xffffffffL);
	}

	public static int readInt(byte abyte0[], int i) {
		return ((abyte0[i] & 0xff) << 24) | ((abyte0[i + 1] & 0xff) << 16) | ((abyte0[i + 2] & 0xff) << 8)
			| (abyte0[i + 3] & 0xff);
	}

	public static int getShort(byte abyte0[], int i) {
		int j = getUnsignedByte(abyte0[i]) * 256 + getUnsignedByte(abyte0[i + 1]);
		if (j > 32767) {
			j -= 0x10000;
		}
		return j;
	}

	public static int getSigned4Bytes(byte abyte0[], int i) {
		if ((abyte0[i] & 0xff) < 128) {
			return abyte0[i];
		} else {
			return ((abyte0[i] & 0xff) - 128 << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8)
				+ (abyte0[i + 3] & 0xff);
		}
	}

	public static int getIntFromByteArray(byte byteArray[], int offset, int length) {
		int bitOffset = offset >> 3;
		int bitMod = 8 - (offset & 7);
		int i1 = 0;
		for (; length > bitMod; bitMod = 8) {
			i1 += (byteArray[bitOffset++] & baseLengthArray[bitMod]) << length - bitMod;
			length -= bitMod;
		}

		if (length == bitMod)
			i1 += byteArray[bitOffset] & baseLengthArray[bitMod];
		else
			i1 += byteArray[bitOffset] >> bitMod - length & baseLengthArray[length];
		return i1;
	}

	public static String addCharacters(String s, int i) {
		String s1 = "";
		for (int j = 0; j < i; j++)
			if (j >= s.length()) {
				s1 = s1 + " ";
			} else {
				char c = s.charAt(j);
				if (c >= 'a' && c <= 'z')
					s1 = s1 + c;
				else if (c >= 'A' && c <= 'Z')
					s1 = s1 + c;
				else if (c >= '0' && c <= '9')
					s1 = s1 + c;
				else
					s1 = s1 + '_';
			}

		return s1;
	}

	public static long stringLength12ToLong(String s) {
		String s1 = "";
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 'a' && c <= 'z')
				s1 = s1 + c;
			else if (c >= 'A' && c <= 'Z')
				s1 = s1 + (char) ((c + 97) - 65);
			else if (c >= '0' && c <= '9')
				s1 = s1 + c;
			else
				s1 = s1 + ' ';
		}

		s1 = s1.trim();
		if (s1.length() > 12)
			s1 = s1.substring(0, 12);
		long l = 0L;
		for (int j = 0; j < s1.length(); j++) {
			char c1 = s1.charAt(j);
			l *= 37L;
			if (c1 >= 'a' && c1 <= 'z')
				l += (1 + c1) - 97;
			else if (c1 >= '0' && c1 <= '9')
				l += (27 + c1) - 48;
		}

		return l;
	}

	public static long nameToHash(String arg0) {
		String s = "";
		for (int i = 0; i < arg0.length(); i++) {
			char c = arg0.charAt(i);
			if (c >= 'a' && c <= 'z')
				s = s + c;
			else if (c >= 'A' && c <= 'Z')
				s = s + (char) ((c + 97) - 65);
			else if (c >= '0' && c <= '9')
				s = s + c;
			else
				s = s + ' ';
		}

		s = s.trim();
		if (s.length() > 12)
			s = s.substring(0, 12);
		long l = 0L;
		for (int j = 0; j < s.length(); j++) {
			char c1 = s.charAt(j);
			l *= 37L;
			if (c1 >= 'a' && c1 <= 'z')
				l += (1 + c1) - 97;
			else if (c1 >= '0' && c1 <= '9')
				l += (27 + c1) - 48;
		}

		return l;
	}

	public static String longToString(long l) {
		if (l < 0L)
			return "invalid_name";
		String s = "";
		while (l != 0L) {
			int i = (int) (l % 37L);
			l /= 37L;
			if (i == 0)
				s = " " + s;
			else if (i < 27) {
				if (l % 37L == 0L)
					s = (char) ((i + 65) - 1) + s;
				else
					s = (char) ((i + 97) - 1) + s;
			} else {
				s = (char) ((i + 48) - 27) + s;
			}
		}
		return s;
	}

	public static int getDataFileOffset(String filename, byte data[]) {
		int numEntries = getUnsigned2Bytes(data, 0);
		int wantedHash = 0;
		filename = filename.toUpperCase();
		for (int k = 0; k < filename.length(); k++)
			wantedHash = (wantedHash * 61 + filename.charAt(k)) - 32;

		int offset = 2 + numEntries * 10;
		for (int entry = 0; entry < numEntries; entry++) {
			int fileHash = (data[entry * 10 + 2] & 0xff) * 0x1000000 + (data[entry * 10 + 3] & 0xff) * 0x10000
				+ (data[entry * 10 + 4] & 0xff) * 256 + (data[entry * 10 + 5] & 0xff);
			int fileSize = (data[entry * 10 + 9] & 0xff) * 0x10000 + (data[entry * 10 + 10] & 0xff) * 256
				+ (data[entry * 10 + 11] & 0xff);
			if (fileHash == wantedHash)
				return offset;
			offset += fileSize;
		}

		return 0;
	}

	public static int getDataFileLength(String filename, byte data[]) {
		int numEntries = getUnsigned2Bytes(data, 0);
		int wantedHash = 0;
		filename = filename.toUpperCase();
		for (int k = 0; k < filename.length(); k++)
			wantedHash = (wantedHash * 61 + filename.charAt(k)) - 32;

		int offset = 2 + numEntries * 10;
		for (int i1 = 0; i1 < numEntries; i1++) {
			int fileHash = (data[i1 * 10 + 2] & 0xff) * 0x1000000 + (data[i1 * 10 + 3] & 0xff) * 0x10000
				+ (data[i1 * 10 + 4] & 0xff) * 256 + (data[i1 * 10 + 5] & 0xff);
			int fileSize = (data[i1 * 10 + 6] & 0xff) * 0x10000 + (data[i1 * 10 + 7] & 0xff) * 256
				+ (data[i1 * 10 + 8] & 0xff);
			int fileSizeCompressed = (data[i1 * 10 + 9] & 0xff) * 0x10000 + (data[i1 * 10 + 10] & 0xff) * 256
				+ (data[i1 * 10 + 11] & 0xff);
			if (fileHash == wantedHash)
				return fileSize;
			offset += fileSizeCompressed;
		}

		return 0;
	}

	public static byte[] loadData(String file, int len, byte[] arc) {
		return loadData(file, len, arc, null);
	}

	public static byte[] loadData(String file, int len, byte[] arc, byte[] dest) {
		int arc_length = (arc[0] & 0xff) * 256 + (arc[1] & 0xff);
		int hash = 0;
		file = file.toUpperCase();
		for (int i = 0; i < file.length(); i++)
			hash = hash * 61 + file.charAt(i) - 32;
		int offset = 2 + arc_length * 10;
		for (int i = 0; i < arc_length; i++) {
			int entry_hash = (arc[(i * 10 + 2)] & 0xFF) * 16777216 + (arc[(i * 10 + 3)] & 0xFF) * 65536
				+ (arc[(i * 10 + 4)] & 0xFF) * 256 + (arc[(i * 10 + 5)] & 0xFF);
			int decmp_len = (arc[(i * 10 + 6)] & 0xFF) * 65536 + (arc[(i * 10 + 7)] & 0xFF) * 256
				+ (arc[(i * 10 + 8)] & 0xFF);
			int cmp_len = (arc[(i * 10 + 9)] & 0xFF) * 65536 + (arc[(i * 10 + 10)] & 0xFF) * 256
				+ (arc[(i * 10 + 11)] & 0xFF);

			if (entry_hash == hash) {
				if (dest == null)
					dest = new byte[decmp_len + len];
				if (decmp_len != cmp_len)
					DataFileDecrypter.unpackData(dest, decmp_len, arc, cmp_len, offset);
				else {
					for (int ii = 0; ii < decmp_len; ii++) {
						dest[ii] = arc[(offset + ii)];
					}
				}
				return dest;
			}
			offset += cmp_len;
		}
		return null;
	}

}
