package launcher.Utils;

import launcher.Main;
import launcher.popup.SavedIndicatorThread;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {
    private static DateFormat df;
    private static long timeCorrection;
    private static long lastTimeUpdate;

    public static volatile boolean outputCommandRunning = false;
    public static String lastCommandOutput = null;

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
            return Main.configFileLocation;
        }
    }

    public static File getWorkingDirectoryFile() {
        return new File(System.getProperty("user.dir"));
    }

    // Some code from the original launcher
    public static ImageIcon getImage(final String name) {
        return new ImageIcon(Utils.class.getResource("/data/images/" + name));
    }

    public static void openWebpage(final String url) {
      Thread t = new Thread(new LinkOpener(url));
      t.start();
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

	public static void execCmd(String[] cmdArray, File workingDirectory) {
    execCmd(cmdArray, workingDirectory, false);
  }

	public static void execCmd(String[] cmdArray, File workingDirectory, boolean needsOutput) {
    if (needsOutput) {
      outputCommandRunning = true;
      lastCommandOutput = null;
		}
		Thread t = new Thread(new CmdRunner(cmdArray, workingDirectory, needsOutput));
		t.start();
	}

	public static boolean isMacOS() {
		String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
		return (os.contains("mac") || os.contains("darwin"));
	}

	public static boolean detectBinaryAvailable(String binaryName, String reason) {
		if (System.getProperty("os.name").contains("Windows")) {
			return false; // don't trust Windows to run the detection code
		}

		try {
			// "whereis" is part of the util-linux package,
			// It is included in pretty much all unix-like operating systems; i.e. safe to use.
			execCmd(new String[] {"whereis", "-b", binaryName}, getWorkingDirectoryFile(), true);

			while (outputCommandRunning) {}
			final String whereis = lastCommandOutput
				.replace("\n", "")
				.replace(binaryName + ": ", "");
			if (whereis.length() < ("/" + binaryName).length()) {
				Logger.Error(
					String.format(
						"@|red !!! Please install %s for %s to work on Linux (or other systems with compatible binary) !!!|@",
						binaryName, reason));
				return false;
			} else {
				Logger.Info(binaryName + ": " + whereis);
				return true;
			}
		} catch (Exception e) {
			Logger.Error("Error while detecting " + binaryName + " binary: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static boolean notMacWindows() {
		if (System.getProperty("os.name").contains("Windows")) {
			return false;
		}
		return !isMacOS();
	}


	public static String stripHtml(final String text) {
        return text.replaceAll("\\<.*?\\>", "");
    }

	public static String generateUserAgent() {
    	StringBuilder sb = new StringBuilder("Mozilla/5.0 (");
		sb.append(System.getProperty("os.name"));
		sb.append("; ");
		sb.append(System.getProperty("os.arch"));
		sb.append("; ");
		sb.append(System.getProperty("os.version"));
		sb.append(") OpenRSCLauncher/");
		sb.append(String.format("%8.6f", Defaults._CURRENT_VERSION));
		return sb.toString();
	}
}
