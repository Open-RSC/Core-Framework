package com.openrsc.server.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class JContentFile {
	private static final Logger LOGGER = LogManager.getLogger();

    private byte[] m_data;
    private int m_position;

    public JContentFile(byte[] data) {
        m_data = data;
        m_position = 0;
    }

    public void skip(int amount) {
        m_position += amount;
    }

    public int tell()
	{
		return m_position;
	}

    public byte readByte() {
        return m_data[m_position++];
    }

	public byte readByte(int offset) {
		return m_data[offset];
	}

    public int readUnsignedByte() {
        return m_data[m_position++] & 0xFF;
    }

    public int readUnsignedShort() {
        return (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public int readUnsignedInt() {
        return (readUnsignedByte() << 24) | (readUnsignedByte() << 16) | (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public String readString() {
        int length = 0;
        while(m_data[m_position + length] != '\0')
            length++;
        String ret;
        ret = new String(m_data, m_position, length);
        m_position += length + 1;
        return ret;
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

    public void close() {
        m_data = null;
    }
}
