package launcher.Utils;
/**
 * rscplus
 *
 * <p>This file is part of rscplus.
 *
 * <p>rscplus is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * <p>rscplus is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with rscplus. If not,
 * see <http://www.gnu.org/licenses/>.
 *
 * <p>Authors: see <https://github.com/RSCPlus/rscplus>
 */


import launcher.Settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** A simple logger */
public class Logger {
	private static PrintWriter m_logWriter;
	private static int levelFixedWidth = 0;
	private static String m_uncoloredMessage = "";

	public enum Type {
		ERROR(0, "error", true, true),
		WARN(1, "warn", true, true),
		INFO(3, "info", true, true),
		DEBUG(4, "debug", true, true);

		Type (int id, String name, boolean showLevel, boolean showTimestamp) {
			this.id = id;
			this.name = name;
			this.showLevel = showLevel;
			this.showTimestamp = showTimestamp;

			levelFixedWidth = (levelFixedWidth < name.length()) ? name.length() : levelFixedWidth;
		}

		public int id;
		public String name;
		public boolean showLevel;
		public boolean showTimestamp;
	}

	public static void start() {
		File file = new File("log.txt");
		try {
			m_logWriter = new PrintWriter(new FileOutputStream(file));
		} catch (Exception e) {
		}
	}

	public static void stop() {
		try {
			m_logWriter.close();
		} catch (Exception e) {
		}
	}

	public static void Log(Type type, String message) {
		try {
			if (type.id > Settings.logVerbosity || message == null) return;

			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			String msg = message;
			String extra = "";

			if (type.showLevel) {
				// Uppercase and pad level for monospace fonts
				String levelText = type.name.toUpperCase();
				while (levelText.length() < levelFixedWidth) levelText = " " + levelText;

				extra += "[" + levelText + "]";
			}
			if (type.showTimestamp) {
				extra += "[" + dateFormat.format(new Date()) + "]";
			}

			if (extra.length() > 0) msg = extra + " " + msg;

			if (type != Type.ERROR) System.out.println(msg);
			else System.err.println(msg);

			try {
				if (m_uncoloredMessage.length() > 0) {
					msg = m_uncoloredMessage;
					m_uncoloredMessage = "";
				}

				// Output to log file
				m_logWriter.write(msg + "\r\n");
				m_logWriter.flush();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			try {
				System.out.println("Logger died, heres the report:");
				e.printStackTrace();
			} catch (Exception e2) {
			}
		}
	}

	// String variants

	public static void Error(String message) {
		Log(Type.ERROR, message);
	}

	public static void Warn(String message) {
		Log(Type.WARN, message);
	}

	public static void Info(String message) {
		Log(Type.INFO, message);
	}

	public static void Debug(String message) {
		Log(Type.DEBUG, message);
	}

}
