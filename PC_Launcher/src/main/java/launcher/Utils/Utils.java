package launcher.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    private static DateFormat df;
    private static long timeCorrection;
    private static long lastTimeUpdate;

    // Simple valid path checker
    public static boolean isValidPath(String path) {
        File checkFile = new File(path);
        try {
            checkFile.getCanonicalPath();
            return true;
        } catch (Exception error) {
            return false;
        }
    }

    // Canonical path getter
    public static String getCanonicalPath(String path) {
        File checkFile = new File(path);
        try {
            return checkFile.getCanonicalPath();
        } catch (Exception error) { // If error, fallback to the default location
            return Defaults._DEFAULT_CONFIG_DIR;
        }
    }

    // Some code from the original launcher
    public static ImageIcon getImage(final String name) {
        return new ImageIcon(Utils.class.getResource("/data/images/" + name));
    }

    public static void openWebpage(final String url) {
        final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URL(url).toURI());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Font getFont(final String fontName, final int type, final float size) {

        try {
            Font font = Font.createFont(0, Utils.class.getResource("/data/fonts/" + fontName).openStream());

            final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();

            genv.registerFont(font);
            font = font.deriveFont(type, size);
            return font;
        } catch (Exception error) {
            error.printStackTrace();
        }
        return null;
    }

    public static String getServerTime() {
        if (Utils.df == null) {
            (Utils.df = new SimpleDateFormat("h:mm:ss a")).setTimeZone(TimeZone.getTimeZone("America/New_York"));
        }
        return Utils.df.format(new Date());
    }

    public static String stripHtml(final String text) {
        return text.replaceAll("\\<.*?\\>", "");
    }

}
