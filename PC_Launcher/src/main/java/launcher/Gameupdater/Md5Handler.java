package launcher.Gameupdater;

import launcher.Utils.Defaults;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Md5Handler {

    List<Entry> entries = new ArrayList<>();
    String configDir;

    public Md5Handler(File file, String configDir) {
        this.configDir = configDir;
        if (file.isFile()) {
            loadFromMd5Table(file);
        } else if (file.isDirectory()) {
            loadFromDirectory(file);
        }
    }

    public static String getMD5Checksum(File file) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            FileInputStream fIS = new FileInputStream(file);
            byte[] fileData = new byte[(int) file.length()];
            fIS.read(fileData);
            fIS.close();
            md.update(fileData);
            byte[] hashData = md.digest();

            StringBuilder hexString = new StringBuilder();

            for (byte hashDatum : hashData) {
                if ((0xff & hashDatum) < 0x10) {
                    hexString.append("0").append(Integer.toHexString((0xFF & hashDatum)));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hashDatum));
                }
            }

            return hexString.toString();
        } catch (Exception a) {
            a.printStackTrace();
            return null;
        }
    }

    private void loadFromMd5Table(File file) {
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                Entry newEntry = new Entry(scanner.nextLine(), this.configDir);
                if (!newEntry.getRef().getName().equals(Defaults._MD5_TABLE_FILENAME))
                    entries.add(newEntry);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void loadFromDirectory(File directory) {
        try {
            File[] files = directory.listFiles();

            for (File file : Objects.requireNonNull(files)) {
                if (file.isDirectory()) {
                    loadFromDirectory(file);
                } else if (file.isFile()) {
                    entries.add(new Entry(file));
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public String getRefSum(File file) {
        for (Entry entry : entries) {
            if (entry.getRef().compareTo(file) == 0)
                return entry.getSum();
        }

        return null;
    }

    public boolean hasRef(File ref) {
        for (Entry entry : entries) {
            if (entry.getRef().compareTo(ref) == 0)
                return true;
        }

        return false;
    }

    public static class Entry {
        private String sum;
        private File ref;
        private String configDir;

        public Entry(String mixedLine, String configDir) {
            this.configDir = configDir;
            sum = mixedLine.substring(0, 32);
            String path = mixedLine.substring(35);
            path.replace(Pattern.quote("/"), File.separator);
            ref = new File(configDir + path);
        }

        public Entry(File file) {
            ref = file;
            sum = getMD5Checksum(file);
        }

        public String getSum() {
            return this.sum;
        }

        public File getRef() {
            return this.ref;
        }

        public File getDownloadRef() {
            String curRef = this.ref.toString();
            String newRef = curRef.replace(this.configDir, "").replaceAll("\\\\", "/").replaceFirst("/", "");
            File finalRef = new File(newRef);
            return finalRef;
        }

    }


}
