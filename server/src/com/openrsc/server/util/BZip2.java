package com.openrsc.server.util;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.ByteArrayInputStream;

public class BZip2 {
    public static byte[] decompress(byte data[], byte uncompressedData[], int offset, int length, int uncompressedLength) {
        try {
            BZip2CompressorInputStream in = new BZip2CompressorInputStream(new ByteArrayInputStream(data, offset, length + 4));
            in.read(uncompressedData);
            in.close();
        } catch (Exception e) {
            return null;
        }
        return uncompressedData;
    }
}
