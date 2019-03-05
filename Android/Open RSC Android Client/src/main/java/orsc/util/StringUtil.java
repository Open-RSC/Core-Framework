package orsc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import orsc.enumerations.MessageType;

public class StringUtil {

	private static char[] squareBracketsAndPound = new char[]{'[', ']', '#'};
	private static char[] accentedCharacterLookup = new char[]{' ', '\u00a0', '_', '-', '\u00e0', '\u00e1', '\u00e2',
			'\u00e4', '\u00e3', '\u00c0', '\u00c1', '\u00c2', '\u00c4', '\u00c3', '\u00e8', '\u00e9', '\u00ea',
			'\u00eb', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00ed', '\u00ee', '\u00ef', '\u00cd', '\u00ce',
			'\u00cf', '\u00f2', '\u00f3', '\u00f4', '\u00f6', '\u00f5', '\u00d2', '\u00d3', '\u00d4', '\u00d6',
			'\u00d5', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00e7',
			'\u00c7', '\u00ff', '\u0178', '\u00f1', '\u00d1', '\u00df'};
	public static char[] specialCharLookup = new char[]{'\u20ac', '\u0000', '\u201a', '\u0192', '\u201e', '\u2026',
			'\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\u0000', '\u017d', '\u0000',
			'\u0000', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122',
			'\u0161', '\u203a', '\u0153', '\u0000', '\u017e', '\u0178'};
	private static char[] characterLookupTable = new char[256];

	static {
		for (int i = 0; i < 256; ++i) {
			StringUtil.characterLookupTable[i] = (char) i;
		}
	}

	private static char toLowerCase(char var0) {
		try {
			if (var0 == 32 || var0 == 160 || var0 == 95 || var0 == 45) {
				return '_';
			}
			if (var0 == 91 || var0 == 93 || var0 == 35) {
				return var0;
			}
			if (var0 == 224 || var0 == 225 || var0 == 226 || var0 == 228 || var0 == 227 || var0 == 192 || var0 == 193
					|| var0 == 194 || var0 == 196 || var0 == 195) {
				return 'a';
			}
			if (var0 == 232 || var0 == 233 || var0 == 234 || var0 == 235 || var0 == 200 || var0 == 201 || var0 == 202
					|| var0 == 203) {
				return 'e';
			}
			if (var0 == 237 || var0 == 238 || var0 == 239 || var0 == 205 || var0 == 206 || var0 == 207) {
				return 'i';
			}
			if (var0 == 242 || var0 == 243 || var0 == 244 || var0 == 246 || var0 == 245 || var0 == 210 || var0 == 211
					|| var0 == 212 || var0 == 214 || var0 == 213) {
				return 'o';
			}
			if (var0 == 249 || var0 == 250 || var0 == 251 || var0 == 252 || var0 == 217 || var0 == 218 || var0 == 219
					|| var0 == 220) {
				return 'u';
			}
			if (var0 == 231 || var0 == 199) {
				return 'c';
			}
			if (var0 == 255 || var0 == 376) {
				return 'y';
			}
			if (var0 == 241 || var0 == 209) {
				return 'n';
			}
			if (var0 == 223) {
				return 'b';
			}
			return Character.toLowerCase(var0);
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "ac.A(" + var0 + ',' + -194 + ')');
		}
	}

	static String stringFindReplace(boolean var0, String replace, String find, String input) {
		try {
			for (int index = input.indexOf(find); index != -1; index = input.indexOf(find, index + replace.length())) {
				input = input.substring(0, index) + replace + input.substring(find.length() + index);
			}

			return input;
		} catch (RuntimeException var5) {
			throw var5;
		}
	}

	private static boolean isCharSpacing(char c) {
		try {
			return c == 160 || c == ' ' || c == '_' || c == '-';
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "db.B(" + 32 + ',' + c + ')');
		}
	}

	public static String formatItemCount(int count) {
		try {
			String str = "" + count;

			for (int i = str.length() - 3; i > 0; i -= 3) {
				str = str.substring(0, i) + "," + str.substring(i);
			}

			if (str.length() > 8) {
				str = "@gre@" + str.substring(0, str.length() - 8) + " million @whi@(" + str + ")";
			} else if (str.length() > 4) {
				str = "@cya@" + str.substring(0, str.length() - 4) + "K @whi@(" + str + ")";
			}

			return str;
		} catch (RuntimeException var4) {
			throw GenUtil.makeThrowable(var4, "mb.B(" + count + ',' + 131071 + ')');
		}
	}

	public static String formatMessage(String msg, String sender, MessageType type) {
		return formatMessage(msg, sender, type, (String) null);
	}

	public static String formatMessage(String msg, String sender, MessageType type, String colourOverride) {
		String colour = null != colourOverride ? colourOverride : type.color;

		if ((sender == null || sender.length() == 0) && type != MessageType.TRADE)
			return colour + msg;

		switch (type) {
			case GAME:
				return colour + sender + ": " + colour + msg;
			case PRIVATE_RECIEVE:
				return colour + sender + colour + " tells you: " + msg;
			case PRIVATE_SEND:
				return colour + "You tell " + sender + colour + ": " + msg;
			case QUEST:
				return colour + sender + ": " + colour + msg;
			case CHAT:
				return colour + sender + ": " + colour + msg;
			case FRIEND_STATUS:
				return colour + msg;
			case TRADE:
				return colour + sender + colour + " wishes to trade with you.";
			case INVENTORY:
				return colour + sender + ": " + colour + msg;
			case GLOBAL_CHAT:
				return colour + msg;
			case CLAN_CHAT:
				return colour + msg;
			default:
				return colour;
		}
	}

	private static boolean isAlphaNumeric(char c) {
		try {
			return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
		} catch (RuntimeException var3) {
			throw GenUtil.makeThrowable(var3, "v.B(" + c + ',' + "dummy" + ')');
		}
	}

	public static String displayNameToKey(CharSequence str) {
		try {
			if (str == null) {
				return null;
			} else {
				int strLeft = 0;

				int strRight = str.length();
				while (strRight > strLeft && isCharSpacing(str.charAt(strLeft))) {

				}

				while (strRight > strLeft && isCharSpacing(str.charAt(strRight - 1))) {
					--strRight;
				}

				int var4 = strRight - strLeft;
				if (var4 >= 1 && var4 <= 12) {
					StringBuilder var5 = new StringBuilder(var4);

					for (int i = strLeft; i < strRight; ++i) {
						char var7 = str.charAt(i);
						if (StringUtil.isValidCharacter(var7)) {
							char var8 = toLowerCase(var7);
							if (var8 != 0) {
								var5.append(var8);
							}
						}
					}

					if (var5.length() != 0) {
						return var5.toString();
					} else {
						return null;
					}
				} else {
					return null;
				}
			}
		} catch (RuntimeException var9) {
			throw GenUtil.makeThrowable(var9, "w.A(" + "{...}" + ',' + "dummy" + ')');
		}
	}

	static String formatErrorMessage(Throwable error) throws IOException {
		try {
			String message;
			if (error instanceof RSRuntimeError) {
				RSRuntimeError var3 = (RSRuntimeError) error;
				error = var3.error;
				message = var3.message + " | ";
			} else {
				message = "";
			}

			StringWriter strBuff = new StringWriter();
			PrintWriter pw = new PrintWriter(strBuff);
			error.printStackTrace(pw);
			pw.close();
			String var5 = strBuff.toString();
			BufferedReader liner = new BufferedReader(new StringReader(var5));

			String firstLine = liner.readLine();

			while (true) {
				String oline = liner.readLine();
				if (null == oline) {
					message = message + "| " + firstLine;
					return message;
				}

				int openParen = oline.indexOf('(');
				int closeParen = oline.indexOf(')', openParen + 1);
				String msg;
				if (openParen != -1) {
					msg = oline.substring(0, openParen);
				} else {
					msg = oline;
				}

				msg = msg.trim();
				msg = msg.substring(1 + msg.lastIndexOf(' '));
				msg = msg.substring(msg.lastIndexOf('\t') + 1);
				message = message + msg;
				if (openParen != -1 && closeParen != -1) {
					int lineNo = oline.indexOf(".java:", openParen);
					if (lineNo >= 0) {
						message = message + oline.substring(5 + lineNo, closeParen);
					}
				}

				message = message + ' ';
			}
		} catch (RuntimeException var13) {
			throw var13;
		}
	}

	private static boolean isValidCharacter(char c) {
		try {
			if (!Character.isISOControl(c)) {
				if (!isAlphaNumeric(c)) {
					for (char t : accentedCharacterLookup)
						if (t == c)
							return true;
					for (char t : squareBracketsAndPound)
						if (t == c)
							return true;
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		} catch (RuntimeException var5) {
			throw GenUtil.makeThrowable(var5, "f.A(" + c + ',' + "dummy" + ')');
		}
	}

	public static String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

}