package com.openrsc.server.util.rsc;

import com.openrsc.server.model.Point;
import com.openrsc.server.net.Packet;
import com.openrsc.server.util.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;


public final class DataConversions {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	private static final char[] special_characters = "~`!@#$%^&*()_-+={}[]|\'\";:?><,./".toCharArray();
	public static StringEncryption encryption = new StringEncryption();
	private static char characters[] = {' ', 'e', 't', 'a', 'o', 'i',
		'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g',
		'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4',
		'5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(',
		')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$',
		'%', '"', '[', ']', '{', '}', '~', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
		'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
		'U', 'V', 'W', 'X', 'Y', 'Z', '<', '>', '^', '_', '|', '/'};
	private static final int bcryptWorkFactor = 10;
	private static final String bcryptTest = "$2y$"+bcryptWorkFactor+"$";
	private static SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss dd-MM-yy");
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMMM yyyy");
	private static MessageDigest md5, sha1, sha512;
	private static Random rand = new Random();
	private static SecureRandom secureRandom = new SecureRandom();

	/**
	 * Creates an instance of the message digest used for creating md5 hashes
	 */
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
			sha1 = MessageDigest.getInstance("SHA-1");
			sha512 = MessageDigest.getInstance("SHA-512");
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}

	/** Returns a virtual like influence from given quest points */
	public static int questPointsToInfluence(int numberPoints, int maxLevel) {
		// based on May 10 2001 conversion, about max influence 22 where max QP became 30
		return Math.min(Math.max(1, (int)Math.floor(numberPoints * 0.75)), maxLevel);
	}

	public static String getDateFromMsec(long diffMSec) {
		int left = 0;
		int ss = 0;
		int mm = 0;
		int hh = 0;
		int dd = 0;
		left = (int) (diffMSec / 1000);
		ss = left % 60;
		left = (int) left / 60;
		if (left > 0) {
			mm = left % 60;
			left = (int) left / 60;
			if (left > 0) {
				hh = left % 24;
				left = (int) left / 24;
				if (left > 0) {
					dd = left;
				}
			}
		}
		return Integer.toString(dd) + " days " + Integer.toString(hh) + " hours " + Integer.toString(mm)
			+ " minutes " + Integer.toString(ss) + " seconds";

	}

	/**
	 * returns the md5 hash of a string
	 */
	private static String md5(String s) {
		synchronized (md5) {
			md5.reset();
			md5.update(s.getBytes());
			return toHex(md5.digest());
		}
	}

	public static String sha1(String s) {
		synchronized (sha1) {
			sha1.reset();
			sha1.update(s.getBytes());
			return toHex(sha1.digest()).toLowerCase();
		}
	}

	private static String sha512(String s) {
		synchronized (sha512) {
			sha512.reset();
			sha512.update(s.getBytes());
			return toHex(sha512.digest()).toLowerCase();
		}
	}

	public static String generateSalt() {
		// This is legacy code since we moved to bcrypt
		int len = 30;
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			int ran = secureRandom.nextInt(characters.length);
			char character = characters[ran];
			if (character == '\\') character = '`'; // replace backslash because MySQL escape makes this into a new line
			sb.append(character);
		}
		return sb.toString();
	}

	private static String hashPasswordCompatibility(final String password, final String salt) {
		boolean doCompatibility = salt != null && !salt.isEmpty();
		return doCompatibility ? DataConversions.sha512(salt + DataConversions.md5(password)) : password;
	}

	private static String bcryptHashPassword(final String passwordPlainText) {
		return BCrypt.hashpw(passwordPlainText, BCrypt.gensalt(bcryptWorkFactor, secureRandom));
	}

	public static final String hashPassword(final String passwordPlainText, final String salt) {
		if(passwordPlainText == null || passwordPlainText.isEmpty())
			return null;

		final String passwordCompatHashed = hashPasswordCompatibility(passwordPlainText, salt);
		return bcryptHashPassword(passwordCompatHashed);
	}

	public static final boolean checkPassword(final String passwordPlainText, final String salt, final String passwordHashed) {
		if(passwordPlainText == null || passwordPlainText.isEmpty() || passwordHashed == null || passwordHashed.isEmpty())
			return false;

		final String plainTextCompatHashed = hashPasswordCompatibility(passwordPlainText, salt);

		// Password is in old DB format.
		if(passwordNeedsRehash(passwordHashed)) {
			return plainTextCompatHashed.equals(passwordHashed);
		}

		return BCrypt.checkpw(plainTextCompatHashed, passwordHashed);
	}

	public static final boolean passwordNeedsRehash(final String passwordHashed) {
		return passwordHashed.length() < bcryptTest.length() || !passwordHashed.substring(0, bcryptTest.length()).equals(bcryptTest);
	}

	private static String toHex(byte[] bytes) {
		// change below to lower or uppercase X to control case of output
		return String.format("%0" + (bytes.length << 1) + "x", new BigInteger(1, bytes));
	}

	public static boolean isValidEmailAddress(String email) {
		boolean stricterFilter = true;
		String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
		String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
		String emailRegex = stricterFilter ? stricterFilterString : laxString;
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	public static String updateIfEmpty(String checkedS, String otherS) {
		return (checkedS == null || checkedS.length() < 2) ? otherS : checkedS;
	}

	public static String normalize(String s, int len) {
		String res = addCharacters(s, len);
		res = res.replaceAll("[\\s_]+","_");
		char[] chars = res.trim().toCharArray();
		if (chars.length > 0 && chars[0] == '_')
			chars[0] = ' ';
		if (chars.length > 0 && chars[chars.length-1] == '_')
			chars[chars.length-1] = ' ';
		return String.valueOf(chars).toLowerCase().trim();
	}

	public static String maxLenString(String s, int len, boolean trim) {
		String res = s;
		if (trim) res = s.trim();
		if (res.length() > len) {
			res = res.substring(0, len);
		}
		return res;
	}

	public static String addCharacters(String s, int i) {
		String s1 = "";
		for (int j = 0; j < i; j++)
			if (j >= s.length()) {
				s1 = s1 + " ";
			} else {
				char c = s.charAt(j);
				if (c >= 'a' && c <= 'z')
					s1 = s1 + c;
				else if (c >= 'A' && c <= 'Z')
					s1 = s1 + c;
				else if (c >= '0' && c <= '9')
					s1 = s1 + c;
				else
					s1 = s1 + '_';
			}

		return s1;
	}

	public static int getDaysSinceTime(Long time) {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - time) / 86400);
	}

	public static String stripBadCharacters(String value) {
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < value.length(); i++) {
			if (getCharCode(value.charAt(i)) > 0) {
				s.append(value.charAt(i));
			} else {
				s.append(" ");
			}
		}
		return s.toString();
	}

	public static String upperCaseAllFirst(String value) {

		// TODO: Code at some point before this should strip colour codes (to be put back in after).
		//   This is because the first letter of the string, regardless of colour codes, should be
		//   a capital letter. But unless we strip and replace codes, we can't find that as easily.
		//   Colour codes may also have capitals within them, regardless of position. Examples:
		//      @rAn@ , @RAN@ , @Ora@ , @orA@ , etc. for capitals in colour codes.
		//      @ran@------------This should be what the text looks like. Capital at beginning.

		Character[] array = value.chars().mapToObj(c -> (char)c).toArray(Character[]::new);

		StringBuilder s = new StringBuilder();
		int i = 0;
		while (array[i].equals(" ") || !Character.isLetter(array[i])) { // Skip spaces and non-letters.
			s.append(String.valueOf(array[i]));
			i++;
			if (s.length() == array.length) return s.toString();
		}

		// Uppercase first letter.
		if (!Character.isUpperCase(array[i]))
			s.append(String.valueOf(Character.toUpperCase(array[i])));
		else
			s.append(String.valueOf(array[i]));

		i++;

		// Keep all letters that follow a whitespace character, if already capital, uppercase.
		// Also any character following a '.', '!', '?' should be transformed into a capital.
		// Also optionally any character following a:
		// "'", ":", "#", "@", "_", "-"
		// can be a capital if user entered.
		for (; i < array.length; i++) {
			Character c = array[i - 1];
			if (c.equals('.') || c.equals('!') || c.equals('?') ||
				((Character.isWhitespace(array[i - 1])
						|| c.equals('\'') || c.equals(':') || c.equals('_')
						|| c.equals('#') || c.equals('@') || c.equals('-')
						|| c.equals('/') || c.equals('\\') || c.equals('|'))
					&& Character.isUpperCase(array[i]))
				) {
				s.append(String.valueOf(Character.toUpperCase(array[i])));
			} else {
				s.append(String.valueOf(Character.toLowerCase(array[i])));
			}
		}

		return s.toString();
	}

	public static String speakTongues(String commonTongue) {
		char[] vowels = {'a', 'e', 'i', 'o', 'u'};
		char[] vowelsUpper = {'A', 'E', 'I', 'O', 'U'};
		char[] babble = commonTongue.toCharArray();
		for (int idx = 0; idx < babble.length; idx++) {

			if (babble[idx] == '@') {
				idx += 5;
				if (idx >= babble.length) break;
			}

			if (random(0, random(7,13)) == 0) {
				babble[idx] = '\'';
			}

			switch (babble[idx]) {
				case 'A':
				case 'E':
				case 'I':
				case 'O':
				case 'U':
					babble[idx] = vowelsUpper[random(0,4)];
					break;
				case 'a':
				case 'e':
				case 'i':
				case 'o':
				case 'u':
					babble[idx] = vowels[random(0,4)];
					break;
				default:
					if (babble[idx] > 'A' && babble[idx] <= 'Z') {
						if (random(0,5) == 0) {
							babble[idx] = (char)random((int)'A', (int)'Z');
						}
					} else if (babble[idx] > 'a' && babble[idx] <= 'z') {
						if (random(0,5) == 0) {
							babble[idx] = (char)random((int)'a', (int)'z');
						}
					}
			}
		}
		return new String(babble);
	}

	/**
	 * Calculates the average of all values in the array
	 */
	public static int average(int[] values) {
		int total = 0;
		for (int value : values) {
			total += value;
		}
		return (int) (total / values.length);
	}

	public static String getEncryptedString(Packet src) {
		try {
			int count = src.getSmart08_16();

			byte[] srct = src.readRemainingData();
			byte[] dest = new byte[count];
			encryption.decryptString(srct, dest, 0, 0, -1, count);

			return getStringFromBytes(dest, 0, count);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "Cabbage";
		}
	}

	private static String getStringFromBytes(byte[] src, int offset, int count) {
		char[] dest = new char[count];
		int dh = 0;

		for (int i = 0; i < count; ++i) {
			int codepoint = 255 & src[offset + i];
			if (codepoint != 0) {
				if (codepoint >= 128 && codepoint < 160) {
					char c = StringUtil.specialCharLookup[codepoint - 128];
					if (c == 0) {
						c = '?';
					}
					codepoint = c;
				}
				dest[dh++] = (char) codepoint;
			}
		}
		return new String(dest, 0, dh);
	}

	/**
	 * returns the code used to represent the given character in our byte array
	 * encoding methods
	 */
	private static int getCharCode(char c) {
		for (int x = 0; x < characters.length; x++) {
			if (c == characters[x]) {
				return x;
			}
		}
		return 0;
	}

	private static byte getMobCoordOffset(int coord1, int coord2) {
		byte offset = (byte) (coord1 - coord2);
		if (offset < 0) {
			offset += 64;
		}
		return offset;
	}

	public static byte[] getMobPositionOffsets(Point p1, Point p2) {
		byte[] rv = new byte[2];
		rv[0] = getMobCoordOffset(p1.getX(), p2.getX());
		rv[1] = getMobCoordOffset(p1.getY(), p2.getY());
		return rv;
	}

	private static byte getObjectCoordOffset(int coord1, int coord2) {
		return (byte) (coord1 - coord2);
	}

	/**
	 * Returns the random number generator
	 */
	public static Random getRandom() {
		return rand;
	}

	public static int random(int range) {
		int number = (int) (Math.random() * (range + 1));
		return number < 0 ? 0 : number;
	}

	/**
	 * Converts a usernames hash back to the username
	 */
	public static String hashToUsername(long l) {
		if (l < 0L)
			return "invalid_name";
		StringBuilder s = new StringBuilder();
		while (l != 0L) {
			int i = (int) (l % 37L);
			l /= 37L;
			if (i == 0)
				s.insert(0, " ");
			else if (i < 27) {
				if (l % 37L == 0L)
					s.insert(0, (char) ((i + 65) - 1));
				else
					s.insert(0, (char) ((i + 97) - 1));
			} else {
				s.insert(0, (char) ((i + 48) - 27));
			}
		}
		return s.toString();
	}

	/**
	 * removes invalid characters and replaces with spaces, then trims the username
	 * @param username username input
	 * @return sanitized username
	 */
	public static String sanitizeUsername(String username) {
		return username
				.replaceAll("[^=,\\da-zA-Z\\s]|(?<!,)\\s", " ")
				.trim();
	}

	/**
	 * Checks if the given int is in the array
	 */
	public static boolean inArray(int[] haystack, int needle) {
		for (int option : haystack) {
			if (needle == option) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns the max of the 2 values
	 */
	public static int max(int i1, int i2) {
		return i1 > i2 ? i1 : i2;
	}

	/**
	 * returns a random number within the given bounds
	 */
	public static double random(double low, double high) {
		return high - (rand.nextDouble() * low);
	}

	/**
	 * returns a random number within the given bounds
	 */
	public static int random(int low, int high) {
		return low + rand.nextInt(high - low + 1);
	}

	public static double round(double value, int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
		return (bd.doubleValue());
	}

	public static int roundUp(double val) {
		return (int) Math.round(val + 0.5D);
	}

	/**
	 * Returns a ByteBuffer containing everything available from the given
	 * InputStream
	 */
	public static ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
		byte[] buffer = new byte[in.available()];
		in.read(buffer, 0, buffer.length);
		return ByteBuffer.wrap(buffer);
	}

	public static String timeFormat(long l) {
		return timeFormatter.format(l);
	}

	public static String dateFormat(long l) {
		return dateFormatter.format(l);
	}

	public static String timeSince(long time) {
		int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
		int minutes = (int) (seconds / 60);
		int hours = (int) (minutes / 60);
		int days = (int) (hours / 24);
		return days + " days " + (hours % 24) + " hours " + (minutes % 60) + " mins";
	}

	/**
	 * Converts a username to a unique hash
	 */
	public static long usernameToHash(String s) {
		s = s.toLowerCase();
		StringBuilder s1 = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 'a' && c <= 'z')
				s1.append(c);
			else if (c >= '0' && c <= '9')
				s1.append(c);
			else
				s1.append(' ');
		}

		s1 = new StringBuilder(s1.toString().trim());
		if (s1.length() > 12)
			s1 = new StringBuilder(s1.substring(0, 12));
		long l = 0L;
		for (int j = 0; j < s1.length(); j++) {
			char c1 = s1.charAt(j);
			l *= 37L;
			if (c1 >= 'a' && c1 <= 'z')
				l += (1 + c1) - 97;
			else if (c1 >= '0' && c1 <= '9')
				l += (27 + c1) - 48;
		}
		return l;
	}

	public static byte[] stringToBytes(CharSequence str) {
		int len = str.length();
		byte[] out = new byte[len];

		for (int i = 0; i < len; ++i) {
			char c = str.charAt(i);
			if ((c <= 0 || c >= 128) && (c < 160 || c > 255)) {
				if (c != 8364) {
					if (c == 8218) {
						out[i] = -126;
					} else if (c != 402) {
						if (c != 8222) {
							if (c == 8230) {
								out[i] = -123;
							} else if (c != 8224) {
								if (c != 8225) {
									if (c != 710) {
										if (c == 8240) {
											out[i] = -119;
										} else if (c != 352) {
											if (c == 8249) {
												out[i] = -117;
											} else if (c == 338) {
												out[i] = -116;
											} else if (c != 381) {
												if (c != 8216) {
													if (c != 8217) {
														if (c == 8220) {
															out[i] = -109;
														} else if (c == 8221) {
															out[i] = -108;
														} else if (c == 8226) {
															out[i] = -107;
														} else if (c == 8211) {
															out[i] = -106;
														} else if (c == 8212) {
															out[i] = -105;
														} else if (c != 732) {
															if (c != 8482) {
																if (c != 353) {
																	if (c != 8250) {
																		if (c != 339) {
																			if (c != 382) {
																				if (c == 376) {
																					out[i] = -97;
																				} else {
																					out[i] = 63;
																				}
																			} else {
																				out[i] = -98;
																			}
																		} else {
																			out[i] = -100;
																		}
																	} else {
																		out[i] = -101;
																	}
																} else {
																	out[i] = -102;
																}
															} else {
																out[i] = -103;
															}
														} else {
															out[i] = -104;
														}
													} else {
														out[i] = -110;
													}
												} else {
													out[i] = -111;
												}
											} else {
												out[i] = -114;
											}
										} else {
											out[i] = -118;
										}
									} else {
										out[i] = -120;
									}
								} else {
									out[i] = -121;
								}
							} else {
								out[i] = -122;
							}
						} else {
							out[i] = -124;
						}
					} else {
						out[i] = -125;
					}
				} else {
					out[i] = -128;
				}
			} else {
				out[i] = (byte) c;
			}
		}

		return out;
	}

	public static String numberFormat(int i) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		return numberFormat.format(i);
	}

	public static boolean parseBoolean(String str) throws NumberFormatException {
		if (str.equalsIgnoreCase("true")
			|| str.equalsIgnoreCase("t")
			|| str.equalsIgnoreCase("yes")
			|| str.equalsIgnoreCase("y")
			|| str.equalsIgnoreCase("1")) {
			return true;
		}

		if (str.equalsIgnoreCase("false")
			|| str.equalsIgnoreCase("f")
			|| str.equalsIgnoreCase("no")
			|| str.equalsIgnoreCase("n")
			|| str.equalsIgnoreCase("0")) {
			return false;
		}

		throw new NumberFormatException();
	}

	public static String formatBytes(final long memorySize)
	{
		final DecimalFormat df = new DecimalFormat("0");
		final double base = Math.log(memorySize) / Math.log(1024);
		final String suffixes[] = {"B", "kB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

		return df.format(Math.pow(1024, base - Math.floor(base))) + suffixes[(int)Math.floor(base)];
	}

	public static String formatTimeString(final int minutes) {
		if (minutes < 60) {
			return minutes + " minutes";
		} else if (minutes < 24 * 60) {
			return (minutes / 60) + " hours";
		} else {
			return (minutes / (24 * 60)) + " days";
		}
	}
}
