package org.openrsc.client;

import java.math.BigInteger;
import java.util.zip.CRC32;

public class DataEncryption {

    public DataEncryption(byte abyte0[]) {
        packet = abyte0;
        offset = 0;
    }

    public void addByte(int i) {
        packet[offset++] = (byte) i;
    }
	
	public void add2ByteInt(int i) { //bit mapping isn't really my strong suite.
		packet[offset++] = (byte) (i >> 8);
		packet[offset++] = (byte) i;
	}
	
    public void add4ByteInt(int i) {
        packet[offset++] = (byte) (i >> 24);
        packet[offset++] = (byte) (i >> 16);
        packet[offset++] = (byte) (i >> 8);
        packet[offset++] = (byte) i;
    }

    @SuppressWarnings("deprecation")
	public void addString(String s) {
        s.getBytes(0, s.length(), packet, offset);
        offset += s.length();
        packet[offset++] = 10;
    }

    public void addBytes(byte abyte0[], int i, int j) {
        for (int k = i; k < i + j; k++)
            packet[offset++] = abyte0[k];

    }

    public int getByte() {
        return packet[offset++] & 0xff;
    }

    public int get2ByteInt() {
        offset += 2;
        return ((packet[offset - 2] & 0xff) << 8) + (packet[offset - 1] & 0xff);
    }

    public int get4ByteInt() {
        offset += 4;
        return ((packet[offset - 4] & 0xff) << 24) + ((packet[offset - 3] & 0xff) << 16) + ((packet[offset - 2] & 0xff) << 8) + (packet[offset - 1] & 0xff);
    }

    public void getBytes(byte abyte0[], int i, int j) {
        for (int k = i; k < i + j; k++)
            abyte0[k] = packet[offset++];

    }

    public void encryptPacketWithKeys(BigInteger biginteger, BigInteger biginteger1) {
        int i = offset;
        offset = 0;
        byte dummyPacket[] = new byte[i];
        getBytes(dummyPacket, 0, i);
        BigInteger biginteger3 = new BigInteger(dummyPacket).modPow(biginteger, biginteger1);
        byte encryptedPacket[] = biginteger3.toByteArray();
        offset = 0;
        addByte(encryptedPacket.length);
        addBytes(encryptedPacket, 0, encryptedPacket.length);
    }

    public byte packet[];
    public int offset;
    static CRC32 crc = new CRC32();

}
