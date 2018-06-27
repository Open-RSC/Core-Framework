package org.openrsc.loader.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

public final class DataConversions {
    private static Random rand = new Random();
	private static char characters[] = {
		' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r',
		'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p',
		'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2',
		'3', '4', '5', '6', '7', '8', '9', ' ', '!', '?',
		'.', ',', ':', ';', '(', ')', '-', '&', '*', '\\',
		'\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[',
		']', '{', '}', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
		'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
		'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};

    /**
     * Returns a ByteBuffer containing everything available from the given InputStream
     */
    public static final ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
        byte[] buffer = new byte[in.available()];
        in.read(buffer, 0, buffer.length);
        return ByteBuffer.wrap(buffer);
    }

    /**
     * Calculates the average of all values in the array
     */
    public static int average(int[] values) {
        int total = 0;
        for (int c = 0; c < values.length; c++) {
            total += values[c];
        }
        return (int) (total / values.length);
    }

    /**
     * returns the code used to represent the given character
     * in our byte array encoding methods
     */
    private static int getCharCode(char c) {
        for (int x = 0; x < characters.length; x++) {
            if (c == characters[x]) {
                return x;
            }
        }
        return 0;
    }

    /**
     * Encodes a string into a byte array
     */
    public static byte[] stringToByteArray(String message) {
        byte[] buffer = new byte[100];
        if (message.length() > 80) {
            message = message.substring(0, 80);
        }
        message = message.toLowerCase();
        int length = 0;
        int j = -1;
        for (int k = 0; k < message.length(); k++) {
            int code = getCharCode(message.charAt(k));
            if (code > 12) {
                code += 195;
            }
            if (j == -1) {
                if (code < 13)
                    j = code;
                else
                    buffer[length++] = (byte) code;
            } else if (code < 13) {
                buffer[length++] = (byte) ((j << 4) + code);
                j = -1;
            } else {
                buffer[length++] = (byte) ((j << 4) + (code >> 4));
                j = code & 0xf;
            }
        }
        if (j != -1) {
            buffer[length++] = (byte) (j << 4);
        }
        byte[] string = new byte[length];
        System.arraycopy(buffer, 0, string, 0, length);
        return string;
    }

    /**
     * Decodes a byte array back into a string
     */
    public static String byteToString(byte[] data, int offset, int length) {
        char[] buffer = new char[100];
        try {
            int k = 0;
            int l = -1;
            for (int i1 = 0; i1 < length; i1++) {
                int j1 = data[offset++] & 0xff;
                int k1 = j1 >> 4 & 0xf;
                if (l == -1) {
                    if (k1 < 13) {
                        buffer[k++] = characters[k1];
                    } else {
                        l = k1;
                    }
                } else {
                    buffer[k++] = characters[((l << 4) + k1) - 195];
                    l = -1;
                }
                k1 = j1 & 0xf;
                if (l == -1) {
                    if (k1 < 13) {
                        buffer[k++] = characters[k1];
                    } else {
                        l = k1;
                    }
                } else {
                    buffer[k++] = characters[((l << 4) + k1) - 195];
                    l = -1;
                }
            }
            boolean flag = true;
            for (int l1 = 0; l1 < k; l1++) {
                char c = buffer[l1];
                /*if (l1 > 4 && c == '@') {
                    buffer[l1] = ' ';
                }*/
                if (c == '%') {
                    buffer[l1] = ' ';
                }
                if (flag && c >= 'a' && c <= 'z') {
                    buffer[l1] += '\uFFE0';
                    flag = false;
                }
                if (c == '.' || c == '!' || c == ':') {
                    flag = true;
                }
            }
            return new String(buffer, 0, k);
        }
        catch (Exception e) {
            return ".";
        }
    }

    /**
     * Checks if the given int is in the array
     */
    public static boolean inArray(int[] haystack, int needle) {
        for (int c = 0; c < haystack.length; c++) {
            if (needle == haystack[c]) {
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
    public static int random(int low, int high) {
        return low + rand.nextInt(high - low + 1);
    }

    /**
     * returns a random number within the given bounds, but
     * allows for certain values to be weighted
     */
    public static int randomWeighted(int low, int dip, int peak, int max) {
        int total = 0;
        int probability = 100;
        int[] probArray = new int[max + 1];
        for (int x = 0; x < probArray.length; x++) {
            total += probArray[x] = probability;
            if (x < dip || x > peak) {
                probability -= 3;
            } else {
                probability += 3;
            }
        }
        int hit = random(0, total);
        total = 0;
        for (int x = 0; x < probArray.length; x++) {
            if (hit >= total && hit < (total + probArray[x])) {
                return x;
            }
            total += probArray[x];
        }
        return 0;
    }

    /**
     * Converts a username to a unique hash
     */
    public static long usernameToHash(String s) {
        s = s.toLowerCase();
        String s1 = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z')
                s1 = s1 + c;
            else if (c >= '0' && c <= '9')
                s1 = s1 + c;
            else
                s1 = s1 + ' ';
        }

        s1 = s1.trim();
        if (s1.length() > 12)
            s1 = s1.substring(0, 12);
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
	
    /**
     * Converts a usernames hash back to the username
     */
    public static String hashToUsername(long l) {
        if (l < 0L)
            return "invalid_name";
        String s = "";
        while (l != 0L) {
            int i = (int) (l % 37L);
            l /= 37L;
            if (i == 0)
                s = " " + s;
            else if (i < 27) {
                if (l % 37L == 0L)
                    s = (char) ((i + 65) - 1) + s;
                else
                    s = (char) ((i + 97) - 1) + s;
            } else {
                s = (char) ((i + 48) - 27) + s;
            }
        }
        return s;
    }
    
	public static boolean containsOnlyNumbers(String str)
	{
		if (str == null || str.length() == 0)
		{
			return false;
		}
		for (int i = 0; i < str.length(); i++)
		{
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	public static String capitalizeString(String string)
	{
		char[] chars = string.toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++)
		{
			if (!found && Character.isLetter(chars[i]))
			{
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
			}
			else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'')
			{
				found = false;
			}
		}
		return String.valueOf(chars);
	}
	
	public static String capitalizeFirstLetters (String s)
	{
		for (int i = 0; i < s.length(); i++)
		{
			if (i == 0)
			{
				s = String.format( "%s%s",
				Character.toUpperCase(s.charAt(0)),
				s.substring(1) );
			}
			if (!Character.isLetterOrDigit(s.charAt(i)))
			{
				if (i + 1 < s.length())
				{
					s = String.format( "%s%s%s", s.subSequence(0, i+1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i+2));
				}
			}
		}
		return s;
	}
	
    public final static String appendUnits(int i)
    {
        String s = String.valueOf(i);
        for (int j = s.length() - 3; j > 0; j -= 3)
        {
            s = s.substring(0, j) + "," + s.substring(j);
        }
        if (s.length() > 8)
        {
            s = "@gre@" + s.substring(0, s.length() - 8) + " million @whi@(" + s + ")";
        }
        else if (s.length() > 4)
        {
            s = "@cya@" + s.substring(0, s.length() - 4) + "K @whi@(" + s + ")";
        }
        return s;
    }
	
	public final static String timeSince(long time)
	{
		int seconds = (int)((System.currentTimeMillis() - time) / 1000);
		int minutes = (int)(seconds / 60);
		int hours = (int)(minutes / 60);
		int days = (int)(hours / 24);
		
		if ((minutes % 60) < 1 && (hours % 24) < 1 && days < 1)
		{
			return null;
		}
		else
		{
			if (days > 0)
			{
				if ((hours % 24) > 0)
				{
					return days + " Day" + ((days == 1) ? "" : "s") + ", " + (hours % 24) + " Hour" + (((hours % 24) == 1) ? "" : "s");
				}
				else
				{
					return days + " Day" + ((days == 1) ? "" : "s") + ", " + (minutes % 60) + " Minute" + (((minutes % 60) == 1) ? "" : "s");
				}
			}
			else
			{
				if ((hours % 24) > 0)
				{
					return (hours % 24) + " Hour" + (((hours % 24) == 1) ? "" : "s") + ", " + (minutes % 60) + " Minute" + (((minutes % 60) == 1) ? "" : "s");
				}
				else
				{
					return (minutes % 60) + " Minute" + (((minutes % 60) == 1) ? "" : "s");
				}
			}
		}
	}
	
	public final static int parseInt(final String s)
	{
		if (s == null)
		{
			throw new NumberFormatException("Null string");
		}
		int num = 0;
		int sign = -1;
		final int len = s.length();
		final char ch = s.charAt(0);
		if (ch == '-')
		{
			if (len == 1)
			{
				throw new NumberFormatException("Missing digits:  " + s);
			}
			sign = 1;
		}
		else
		{
			final int d = ch - '0';
			if (d < 0 || d > 9)
			{
				throw new NumberFormatException("Malformed:  " + s);
			}
			num = -d;
		}
		final int max = (sign == -1) ? -Integer.MAX_VALUE : Integer.MIN_VALUE;
		final int multmax = max / 10;
		int i = 1;
		while (i < len)
		{
			int d = s.charAt(i++) - '0';
			if (d < 0 || d > 9)
			{
				throw new NumberFormatException("Malformed:  " + s);
			}
			if (num < multmax)
			{
				return Integer.MAX_VALUE;
			}
			num *= 10;
			if (num < (max + d))
			{
				throw new NumberFormatException("Over/underflow:  " + s);
			}
			num -= d;
		}
		return sign * num;
	}
}
