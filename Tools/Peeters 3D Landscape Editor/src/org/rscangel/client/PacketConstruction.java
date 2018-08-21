package org.rscangel.client;

import java.io.IOException;

public class PacketConstruction {

    public void closeStream() {
    }

    public void readInputStream(int length, byte[] abyte0) throws IOException {
        readInputStream(length, 0, abyte0);
    }

    public void formatPacket() {
        if (skip8Offset != 8)
            packetOffset++;
        int j = packetOffset - packetStart - 2;
        if (j >= 160) {
            packetData[packetStart] = (byte) (160 + j / 256);
            packetData[packetStart + 1] = (byte) (j & 0xff);
        } else {
            packetData[packetStart] = (byte) j;
            packetOffset--;
            packetData[packetStart + 1] = packetData[packetOffset];
        }
        if (maxPacketLength <= 10000) {
            int k = packetData[packetStart + 2] & 0xff;
            packetCommandCount[k]++;
            packetCommandLength[k] += packetOffset - packetStart;
        }
        packetStart = packetOffset;
    }

    public void addByte(int i) {
        packetData[packetOffset++] = (byte) i;
    }

    public void addTwo4ByteInts(long l) {
        add4ByteInt((int) (l >> 32));
        add4ByteInt((int) (l & -1L));
    }

    public void add4ByteInt(int i) {
        packetData[packetOffset++] = (byte) (i >> 24);
        packetData[packetOffset++] = (byte) (i >> 16);
        packetData[packetOffset++] = (byte) (i >> 8);
        packetData[packetOffset++] = (byte) i;
    }

    public boolean containsData() {
        return packetStart > 0;
    }

    public void add2ByteInt(int i) {
        packetData[packetOffset++] = (byte) (i >> 8);
        packetData[packetOffset++] = (byte) i;
    }

    public int readByte()
            throws IOException {
        return readInputStream();
    }

    public int readPacket(byte data[]) {
        try {
            packetReadCount++;
            if (maxPacketReadCount > 0 && packetReadCount > maxPacketReadCount) {
                error = true;
                errorText = "time-out";
                maxPacketReadCount += maxPacketReadCount;
                return 0;
            }
            if (length == 0 && inputStreamAvailable() >= 2) {
                length = readInputStream();
                if (length >= 160)
                    length = (length - 160) * 256 + readInputStream();
            }
            if (length > 0 && inputStreamAvailable() >= length) {
                if (length >= 160) {
                    readInputStream(length, data);
                } else {
                    data[length - 1] = (byte) readInputStream();
                    if (length > 1)
                        readInputStream(length - 1, data);
                }
                int readBytes = length;
                length = 0;
                packetReadCount = 0;
                return readBytes;
            }
        }
        catch (IOException ioexception) {
            error = true;
            errorText = ioexception.getMessage();
        }
        return 0;
    }

    public void readInputStream(int length, int offset, byte abyte0[])
            throws IOException {
    }

    public int inputStreamAvailable()
            throws IOException {
        return 0;
    }

    public void finalisePacket()
            throws IOException {
        formatPacket();
        writePacket(0);
    }

    public long read8ByteLong()
            throws IOException {
        long l = read2ByteInt();
        long l1 = read2ByteInt();
        long l2 = read2ByteInt();
        long l3 = read2ByteInt();
        return (l << 48) + (l1 << 32) + (l2 << 16) + l3;
    }

    public int read2ByteInt()
            throws IOException {
        int i = readByte();
        int j = readByte();
        return i * 256 + j;
    }

    public void addBytes(byte bytes[], int offset, int length) {
        for (int k = 0; k < length; k++)
            packetData[packetOffset++] = bytes[offset + k];

    }

    public void writePacket(int i)
            throws IOException {
        if (error) {
            packetStart = 0;
            packetOffset = 3;
            error = false;
            throw new IOException(errorText);
        }
        packetCount++;
        if (packetCount < i)
            return;
        if (packetStart > 0) {
            packetCount = 0;
            writeToOutputBuffer(packetData, 0, packetStart);
        }
        packetStart = 0;
        packetOffset = 3;
    }

    public void addString(String s) {
        s.getBytes(0, s.length(), packetData, packetOffset);
        packetOffset += s.length();
    }

    public void writeToOutputBuffer(byte abyte0[], int i, int j)
            throws IOException {
    }

    public void createPacket(int i) {
        if (packetStart > (maxPacketLength * 4) / 5)
            try {
                writePacket(0);
            }
            catch (IOException ioexception) {
                error = true;
                errorText = ioexception.getMessage();
            }
        if (packetData == null)
            packetData = new byte[maxPacketLength];
        packetData[packetStart + 2] = (byte) i;
        packetData[packetStart + 3] = 0;
        packetOffset = packetStart + 3;
        skip8Offset = 8;
    }

    public int readInputStream()
            throws IOException {
        return 0;
    }

    public PacketConstruction() {
        packetOffset = 3;
        skip8Offset = 8;
        errorText = "";
        maxPacketLength = 5000;
        error = false;
    }

    protected int length;
    public int packetReadCount;
    public int maxPacketReadCount;
    public int packetStart;
    private int packetOffset;
    private int skip8Offset;
    public byte packetData[];
    private static int anIntArray521[] = {
            0, 1, 3, 7, 15, 31, 63, 127, 255, 511,
            1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff,
            0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff,
            0x3fffffff, 0x7fffffff, -1
    };
    final int anInt522 = 61;
    final int anInt523 = 59;
    final int anInt524 = 42;
    final int anInt525 = 43;
    final int anInt526 = 44;
    final int anInt527 = 45;
    final int anInt528 = 46;
    final int anInt529 = 47;
    final int anInt530 = 92;
    final int anInt531 = 32;
    final int anInt532 = 124;
    final int anInt533 = 34;
    static char aCharArray536[];
    public static int packetCommandCount[] = new int[256];
    protected String errorText;
    protected int maxPacketLength;
    protected int packetCount;
    public static int packetCommandLength[] = new int[256];
    protected boolean error;
    public static int anInt543;

    static {
        aCharArray536 = new char[256];
        for (int i = 0; i < 256; i++)
            aCharArray536[i] = (char) i;

        aCharArray536[61] = '=';
        aCharArray536[59] = ';';
        aCharArray536[42] = '*';
        aCharArray536[43] = '+';
        aCharArray536[44] = ',';
        aCharArray536[45] = '-';
        aCharArray536[46] = '.';
        aCharArray536[47] = '/';
        aCharArray536[92] = '\\';
        aCharArray536[124] = '|';
        aCharArray536[33] = '!';
        aCharArray536[34] = '"';
    }
}
