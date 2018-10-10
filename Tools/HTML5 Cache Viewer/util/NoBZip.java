/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * a shitty bypass for my BZIP2 issues in javascript this will repack the cache with no bzipped data.
 * @author Silabsoft
 */
public class NoBZip {

    public NoBZip() {
    }

    public void unBzip(String file) throws Exception {
        FileOutputStream out = new FileOutputStream("data204_no_bzip/" + file);
        byte[] f = readDataFile("textures" + Version.TEXTURES + ".jag");
        Entry[] e = unpackData(f);
        byte[] payload = this.writeEntries(e);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        this.putTri(bo, payload.length);
        this.putTri(bo, payload.length);
        bo.write(payload);
        out.write(bo.toByteArray());
        out.close();
    }

    public static void main(String[] args) throws Exception {
        NoBZip ub = new NoBZip();
        ub.unBzip("textures" + Version.TEXTURES + ".jag");

    }

    public byte[] readDataFile(String file) {
        //System.out.println("Using default load");
        file = "./data204/" + file;
        int archiveSize = 0;
        int archiveSizeCompressed = 0;
        byte archiveData[] = null;
        byte header[] = new byte[6];
        try {

            java.io.InputStream inputstream = Utility.openFile(file);
            DataInputStream datainputstream = new DataInputStream(inputstream);

            datainputstream.readFully(header, 0, 6);
            archiveSize = ((header[0] & 0xff) << 16) + ((header[1] & 0xff) << 8) + (header[2] & 0xff);

            archiveSizeCompressed = ((header[3] & 0xff) << 16) + ((header[4] & 0xff) << 8) + (header[5] & 0xff);

            int read = 0;
            archiveData = new byte[archiveSizeCompressed];
            while (read < archiveSizeCompressed) {
                int length = archiveSizeCompressed - read;
                if (length > 1000) {
                    length = 1000;
                }
                datainputstream.readFully(archiveData, read, length);
                read += length;

            }
            datainputstream.close();
        } catch (IOException ignored) {
        }

        if (archiveSizeCompressed != archiveSize) {
            byte decompressed[] = new byte[archiveSize];
            BZLib.decompress(decompressed, archiveSize, archiveData, archiveSizeCompressed, 0);
            return decompressed;
        } else {
            return archiveData;
        }
    }

    public byte[] writeEntries(Entry[] entry) throws IOException {
        ByteArrayOutputStream bb = new ByteArrayOutputStream();
        putShort(bb, entry.length);
        for (int i = 0; i < entry.length; i++) {
            putInt(bb, entry[i].getFileHash());
            putTri(bb, entry[i].getData().length);
            putTri(bb, entry[i].getData().length);

        }
        for (int i = 0; i < entry.length; i++) {
            bb.write(entry[i].getData());
        }
        return bb.toByteArray();
    }

    public void putShort(ByteArrayOutputStream out, int i) {
        out.write((byte) (i >> 8));
        out.write((byte) i);
    }

    public void putInt(ByteArrayOutputStream out, int i) {
        out.write((byte) (i >> 24));
        out.write((byte) (i >> 16));
        out.write((byte) (i >> 8));
        out.write((byte) i);

    }

    public void putTri(ByteArrayOutputStream out, int i) {
        out.write((byte) (i >> 16));
        out.write((byte) (i >> 8));
        out.write((byte) i);
    }

    public Entry[] unpackData(byte archiveData[]) {
        int numEntries = (archiveData[0] & 0xff) * 256 + (archiveData[1] & 0xff);
        int offset = 2 + numEntries * 10;
        Entry[] ent = new Entry[numEntries];
        for (int entry = 0; entry < numEntries; entry++) {

            int fileHash = (archiveData[entry * 10 + 2] & 0xff) * 0x1000000 + (archiveData[entry * 10 + 3] & 0xff) * 0x10000 + (archiveData[entry * 10 + 4] & 0xff) * 256 + (archiveData[entry * 10 + 5] & 0xff);
            int fileSize = (archiveData[entry * 10 + 6] & 0xff) * 0x10000 + (archiveData[entry * 10 + 7] & 0xff) * 256 + (archiveData[entry * 10 + 8] & 0xff);
            int fileSizeCompressed = (archiveData[entry * 10 + 9] & 0xff) * 0x10000 + (archiveData[entry * 10 + 10] & 0xff) * 256 + (archiveData[entry * 10 + 11] & 0xff);

            byte[] fileData = new byte[fileSize];
            if (fileSize != fileSizeCompressed) {
                BZLib.decompress(fileData, fileSize, archiveData, fileSizeCompressed, offset);
            } else {
                for (int j = 0; j < fileSize; j++) {
                    fileData[j] = archiveData[offset + j];
                }

            }

            ent[entry] = new Entry(fileHash, fileData);

            offset += fileSizeCompressed;
        }

        return ent;
    }

    public class Entry {

        private final byte[] data;
        private final int fileHash;

        public Entry(int fileHash, byte[] data) {
            this.data = data;
            this.fileHash = fileHash;
        }

        public byte[] getData() {
            return data;
        }

        public int getFileHash() {
            return fileHash;
        }

    }

    public byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
