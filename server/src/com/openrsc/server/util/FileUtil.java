package com.openrsc.server.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

public class FileUtil {
    public static void mkdir(String path) {
        new File(path).mkdirs();
    }

    public static boolean writeFull(String fname, byte[] data) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(fname));
            os.write(data);
            os.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] readFull(File f) {
        try {
            return Files.readAllBytes(f.toPath());
        } catch (Exception e) {
            return null;
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        if (source.getAbsolutePath().equals(dest.getAbsolutePath())) {
            return;
        }
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } finally {
            sourceChannel.close();
            destChannel.close();
        }
    }

}
