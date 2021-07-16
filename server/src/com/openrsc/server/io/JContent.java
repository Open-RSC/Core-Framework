package com.openrsc.server.io;

import com.openrsc.server.util.BZLib;
import com.openrsc.server.util.BZip2;
import com.openrsc.server.util.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class JContent {
	private static final Logger LOGGER = LogManager.getLogger();

    private byte[] m_data;
    private boolean m_bzip2;

    private byte[] decompress(byte[] dest, int uncompressedLength, byte[] src, int compressedLength, int offset)
	{
		if (!m_bzip2) {
			BZLib.decompress(dest, uncompressedLength, src, compressedLength, offset);
			return dest;
		} else {
			m_data[2] = 0x42;
			m_data[3] = 0x5A;
			m_data[4] = 0x68;
			m_data[5] = 0x31;
			return BZip2.decompress(m_data, dest, 2, compressedLength + 4, uncompressedLength);
		}
	}

    public boolean open(String fname, boolean useBZip2) {
		m_bzip2 = useBZip2;

        m_data = FileUtil.readFull(new File(fname));

        if (m_data == null)
            return false;

        int uncompressedLength = ((m_data[0] & 0xFF) << 16) | ((m_data[1] & 0xFF) << 8) | (m_data[2] & 0xFF);
        int compressedLength = ((m_data[3] & 0xFF) << 16) | ((m_data[4] & 0xFF) << 8) | (m_data[5] & 0xFF);

        if (uncompressedLength == compressedLength) {
            byte[] newData = new byte[uncompressedLength];
            System.arraycopy(m_data, 6, newData, 0, uncompressedLength);
            m_data = newData;
        } else {
			byte[] newData = new byte[uncompressedLength];
			newData = decompress(newData, uncompressedLength, m_data, compressedLength, 0);
			if (newData == null)
				return false;
			m_data = newData;
        }

        return true;
    }

    public JContentFile unpack(String filename) {
        int entryCount = ((m_data[0] & 0xFF) << 8) | (m_data[1] & 0xFF);
        filename = filename.toUpperCase();

        int hash = 0;
        for (int i = 0; i < filename.length(); i++)
            hash = 61 * hash + (filename.charAt(i) - 32);

        int offset = 2 + (10 * entryCount);
        for (int i = 0; i < entryCount; i++) {
            int entryOffset = i * 10;
            int entryHash = ((m_data[2 + entryOffset] & 0xFF) << 24) | ((m_data[3 + entryOffset] & 0xFF) << 16) |
                            ((m_data[4 + entryOffset] & 0xFF) << 8) | (m_data[5 + entryOffset] & 0xFF);
            int uncompressedLength = ((m_data[6 + entryOffset] & 0xFF) << 16) | ((m_data[7 + entryOffset] & 0xFF) << 8) | (m_data[8 + entryOffset] & 0xFF);
            int compressedLength = ((m_data[9 + entryOffset] & 0xFF) << 16) | ((m_data[10 + entryOffset] & 0xFF) << 8) | (m_data[11 + entryOffset] & 0xFF);

            if (hash == entryHash) {
                byte[] data = new byte[uncompressedLength];
                if (uncompressedLength == compressedLength) {
                    System.arraycopy(m_data, offset, data, 0, uncompressedLength);
                } else {
					data = decompress(data, uncompressedLength, m_data, compressedLength, offset);
					if (data == null)
						return null;
                }
                return new JContentFile(data);
            }
            offset += compressedLength;
        }

        return null;
    }

    public void close() {
        m_data = null;
    }

    public void dump(String fname) {
        File f = new File(fname);
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(f));
            out.write(m_data, 0, m_data.length);
            out.close();
        } catch (Exception e) {
        }
    }
}
