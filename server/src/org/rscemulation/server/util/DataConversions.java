package org.rscemulation.server.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Random;

import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Point;
import org.rscemulation.server.net.RSCPacket;

import com.bombaydigital.vault.HexString;

public final class DataConversions {
	private static MessageDigest md5, sha1, sha512;
	private static Random random = new Random();
	private static char characters[] = {' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']', '{', '}', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	private static final BigInteger key = new BigInteger("730546719878348732291497161314617369560443701473303681965331739205703475535302276087891130348991033265134162275669215460061940182844329219743687403068279");
	private static final BigInteger modulus = new BigInteger("1549611057746979844352781944553705273443228154042066840514290174539588436243191882510185738846985723357723362764835928526260868977814405651690121789896823");

	public static String ucwords(String s) { // PHP's ucwords() function 
		try
		{
			s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
			for (int i = 0; i < s.length(); i++)
			{
				if (!Character.isLetterOrDigit(s.charAt(i)))
				{
					// Quick-Fix 3.9.2013, 6:46PM EST
					// Fixes "underscores in players' usernames" (during follow / trade / etc)
					s = s.replace(s.charAt(i), ' ');
					// Quick-Fix 3.9.2013, 6:46PM EST
					if (i + 1 < s.length())
					{
						s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
					}
				}
			}
			return s;
		} catch(Exception ex) {
			System.out.println("Exception with ucwords(): " + s);
			ex.printStackTrace();
			return s;
		}
	}
	
	public static String insertCommas(String str) {  
		if (str.length() < 4)
			return str;  
		return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());  
	}
	
	public static String number_format(String s) { // PHP's number_format() function
		if (s.length() < 4)
			return s;  
		return number_format(s.substring(0, s.length() - 3)) + "," + s.substring(s.length() - 3, s.length());  
	}
	
	public static String getPlayerHeader(Player player) {
		switch (player.getGroupID()) {
		case 1:
			return "#adm#@yel@";
		case 2:
			return "#mod#@whi@";
		case 3:
			return "#dev#@red@";
		case 7:
			return "@or2@";
		default:
			return "@yel@";
		}
	}
 
	public static final ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
		byte[] buffer = new byte[in.available()];
		in.read(buffer, 0, buffer.length);
		return ByteBuffer.wrap(buffer);
	}	
	
	static {
		try {
			md5 = MessageDigest.getInstance("MD5");
			sha1 = MessageDigest.getInstance("SHA-1");
			sha512 = MessageDigest.getInstance("SHA-512");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String timeSince(long time) {
		int seconds = (int)((System.currentTimeMillis() - time) / 1000);
		int minutes = (seconds / 60);
		int hours = (minutes / 60);
		int days = (hours / 24);
		return days + " days " + (hours % 24) + " hours " + (minutes % 60) + " mins";
	}
	
	public static int roundUp(double val) {
		return (int)Math.round(val + 0.5D);
	}
	
	public static double round(double value, int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return(bd.doubleValue());
	}
	
	public static int getTimeStamp() {
		long time = System.currentTimeMillis() / 1000;
		return (int)time;
	}
	
	public static byte[] getObjectPositionOffsets(Point p1, Point p2) {
		byte[] rv = new byte[2];
		rv[0] = (byte)(p1.getX() - p2.getX());
		rv[1] = (byte)(p1.getY() - p2.getY());
		return rv;
	}
	
	public static byte[] getItemPositionOffsets(Point p1, Point p2) {
		byte[] rv = new byte[2];
		rv[0] = (byte)(p1.getX() - p2.getX());
		rv[1] = (byte)(p1.getY() - p2.getY());
		return rv;
	}	
	
	public static byte[] getMobPositionOffsets(Point p1, Point p2) {
		byte[] rv = new byte[2];
		rv[0] = getMobCoordOffset(p1.getX(), p2.getX());
		rv[1] = getMobCoordOffset(p1.getY(), p2.getY());
		return rv;
	}

	private static byte getMobCoordOffset(int coord1, int coord2) {
		byte offset = (byte)(coord1 - coord2);
		if (offset < 0)
			offset += 32;
		return offset;
	}

	public static RSCPacket decryptRSA(byte[] data) {
		try {
			BigInteger bigInteger = new BigInteger(data);
			data = bigInteger.modPow(key, modulus).toByteArray();
			return new RSCPacket(0, data, true);
		} catch (Exception e) {
			return null;
		}
	}

	private static int getCharCode(char c) {
  		for (int x = 0; x < characters.length; x++) {
  			if (c == characters[x])
  				return x;
  		}
  		
  		return 0;
	}

	public static byte[] stringToByteArray(String message) {
		byte[] buffer = new byte[100];
		if (message.length() > 80)
			message = message.substring(0, 80);
		message = message.toLowerCase();
		int length = 0;
		int j = -1;
		for (int k = 0; k < message.length();k++) {
			int code = getCharCode(message.charAt(k));
			if (code > 12)
				code += 195;
			if (j == -1) {
				if (code < 13)
					j = code;
				else
					buffer[length++] = (byte)code;
			} else if (code < 13) {
				buffer[length++] = (byte) ((j << 4) + code);
				j = -1;
			} else {
				buffer[length++] = (byte) ((j << 4) + (code >> 4));
				j = code & 0xf;
			}
		}
		if (j != -1)
			buffer[length++] = (byte)(j << 4);
		byte[] string = new byte[length];
		System.arraycopy(buffer, 0, string, 0, length);
		
		return string;
	}

	public static String byteToString(byte[] data, int offset, int length) {
		char[] buffer = new char[100];
		try {
			int k = 0;
			int l = -1;
			for (int i1 = 0;i1 < length;i1++) {
				int j1 = data[offset++] & 0xff;
				int k1 = j1 >> 4 & 0xf;
				if (l == -1) {
					if (k1 < 13)
						buffer[k++] = characters[k1];
					else
						l = k1;
				} else {
					buffer[k++] = characters[((l << 4) + k1) - 195];
					l = -1;
				}
				k1 = j1 & 0xf;
				if (l == -1) {
					if (k1 < 13)
						buffer[k++] = characters[k1];
					else
						l = k1;
				} else {
					buffer[k++] = characters[((l << 4) + k1) - 195];
					l = -1;
				}
			}
			boolean flag = true;
			for(int l1 = 0; l1 < k; l1++) {
				char c = buffer[l1];
				if (l1 > 4 && c == '@')
					buffer[l1] = ' ';
				if (c == '%')
					buffer[l1] = ' ';
				if (flag && c >= 'a' && c <= 'z') {
					buffer[l1] += '\uFFE0';
					flag = false;
				}
				if (c == '.' || c == '!' || c == ':')
					flag = true;
			}
			
			return new String(buffer, 0, k);
		} catch (Exception e) {
			return ".";
		}
	}

	public static boolean percentChance(int percent) {
		return random(1, 100) <= percent;
	}

	public static boolean inArray(int[] haystack, int needle) {
		for (int option : haystack) {
			if (needle == option)
				return true;
		}
		return false;
	}

	public static Random getRandom() {
		return random;
	}

	/**
	 * Generates a pseudo-random integral value in the range of [low, high] 
	 * inclusively bound on both ends.
	 * 
	 * @param low the lowest value to generate (inclusive)
	 * 
	 * @param high the highest value to generate (inclusive)
	 * 
	 * @return a pseudo-random integral value in the range of [low, high] 
	 * inclusively bound on both ends
	 * 
	 * @version 1.0
	 * 
	 * @since 1.0
	 * 
	 */
	public static int random(int low, int high)
	{
		return low + random.nextInt(high - low + 1);
	}

	public static int randomWeighted(int low, int dip, int peak, int max) {
		int total = 0;
		int probability = 100;
		int[] probArray = new int[max + 1];
		for(int x = 0;x < probArray.length;x++) {
			total += probArray[x] = probability;
			if (x < dip || x > peak)
				probability -= 3;
			else
				probability += 3;
		}
		int hit = random(0, total);
		total = 0;
		for (int x = 0;x < probArray.length;x++) {
			if (hit >= total && hit < (total + probArray[x]))
				return x;
			total += probArray[x];
		}
		
		return 0;	
	}

	public static long usernameToHash(String s) {
		try {
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
					l += ((1 + c1) - 97);
				else if (c1 >= '0' && c1 <= '9')
					l += ((27 + c1) - 48);
			}
			
			return l;
		} catch(Exception ex) {
			System.out.println("Error encoding username " + s);
		}
		return -1;
	}

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
			} else
				s = (char) ((i + 48) - 27) + s;
		}
		
		return s;
	}

	public static String md5(String s) {
		md5.reset();
		md5.update(s.getBytes());
		
		return HexString.bufferToHex(md5.digest()).toLowerCase();
	}
	
    public static String sha1(String s) {
		sha1.reset();
		sha1.update(s.getBytes());
		
		return HexString.bufferToHex(sha1.digest()).toLowerCase();
    }
    
    public static String sha512(String s) {
		sha512.reset();
		sha512.update(s.getBytes());
		
		return HexString.bufferToHex(sha512.digest()).toLowerCase();
    }	    
	
	public static String IPToString(long ip) {
		String result = "0.0.0.0";
		for(int x = 0; x < 4; x++) {
			int octet = (int)(ip / Math.pow(256, 3 - x));
			ip -= octet * Math.pow(256, 3 - x);
			if (x == 0)
				result = String.valueOf(octet);
			else
				result += ("." + octet);
		}
		return result;
	}
}