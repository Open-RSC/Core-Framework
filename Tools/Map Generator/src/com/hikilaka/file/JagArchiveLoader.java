package com.hikilaka.file;

import com.hikilaka.bz2.Bzip2Decompressor;
import com.hikilaka.file.jag.JagArchive;

public final class JagArchiveLoader implements FileLoader {

	private static final BinaryFileLoader binaryLoader = new BinaryFileLoader();
	
	/**
	 * Loads the specified JAGeX archive into a raw byte array
	 */
	@Override
	public byte[] load(String file) {
		byte[] fileData = binaryLoader.load(file);
		
		// reading the archive headers
		int decompLen = ((fileData[0] & 0xff) << 16) + ((fileData[1] & 0xff) << 8) + (fileData[2] & 0xff);
		int compLen = ((fileData[3] & 0xff) << 16) + ((fileData[4] & 0xff) << 8) + (fileData[5] & 0xff);
		
		byte data[] = new byte[fileData.length - 6];
		
		for(int j1 = 0; j1 < fileData.length - 6; j1++) {
			data[j1] = fileData[j1 + 6];
		}
		
		if (decompLen != compLen) {
			byte[] decomp = new byte[decompLen];
			// decompress the archive
			Bzip2Decompressor.unpackData(decomp, decompLen, data, compLen, 0);
			return decomp;
		}
		return data;
	}
	
	/**
	 * Loads the specified JAGeX archive into a <code>JagArchive</code> object
	 */
	public JagArchive loadArchive(String file) {
		byte[] archive = load(file);
		return new JagArchive(archive);
	}

}
