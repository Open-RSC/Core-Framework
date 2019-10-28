package orsc.util;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.ImageIcon;

public class Utils {
	private static DateFormat df;
	private static long timeCorrection;
	private static long lastTimeUpdate;

	public static Font getFont(final String fontName, final int type, final float size) {
		try {
			Font font = Font.createFont(0, Utils.class.getResource("/res/" + fontName).openStream());
			final GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			genv.registerFont(font);
			font = font.deriveFont(type, size);
			return font;
		} catch (FontFormatException | IOException ex2) {
			((Exception) null).printStackTrace();
			return null;
		}
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

	public static synchronized long currentTimeMillis() {
		final long l = System.currentTimeMillis();
		if (l < Utils.lastTimeUpdate) {
			Utils.timeCorrection += Utils.lastTimeUpdate - l;
		}
		Utils.lastTimeUpdate = l;
		return l + Utils.timeCorrection;
	}

	public static ImageIcon getImage(final String name) {
		return new ImageIcon(Utils.class.getResource("/res/" + name));
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
