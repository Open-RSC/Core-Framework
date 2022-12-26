package launcher.Utils;

import java.awt.*;
import java.net.URI;

public class LinkOpener implements Runnable {
	private String url;

	public LinkOpener(String url) {
		this.url = url;
	}

	public void run() {
		boolean hasXdgOpen = Utils.detectBinaryAvailable("xdg-open", "URL opening");
		try {
			/*
			if (Utils.notMacWindows() && Settings.PREFERS_XDG_OPEN.get(Settings.currentProfile)) {
				if (!hasXdgOpen) {
					Logger.Warn("Don't think you have xdg-open, but will try to use it anyway...");
				}
				Logger.Info("Using xdg-open to open url \"" + url + "\".");
				Utils.execCmd(new String[] {"xdg-open", url});
				return;
			}
			*/


			if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
				// Tested to work on Windows XP+, Mac OS, and Linux,
				// but Linux needs some GNOME library for this to work.
				Logger.Info("Opening \"" + url + "\".");
				Desktop.getDesktop().browse(new URI(url.replaceAll(" ", "%20")));
			} else {
				if (Utils.isMacOS()) {
					// not sure, but maybe some version of Mac OS isn't supported by Desktop
					Logger.Info("Opening \"" + url + "\".");
					Utils.execCmd(new String[] {"open", url}, Utils.getWorkingDirectoryFile(), false);
				} else {
					if (hasXdgOpen) {
						Logger.Info("Using xdg-open to open url \"" + url + "\".");
						Utils.execCmd(new String[] {"xdg-open", url}, Utils.getWorkingDirectoryFile(), false);
					} else {
						Logger.Warn("Please install xdg-open to reliably open URLs on your system.");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
