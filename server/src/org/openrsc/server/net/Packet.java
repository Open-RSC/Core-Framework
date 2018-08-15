package org.openrsc.server.net;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Packet {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        
	protected int pLength;
	protected byte[] pData;
	protected int caret = 0;
	protected boolean bare;
	protected long time;

	public Packet(byte[] pData, boolean bare) {

		this.pData = pData;
		this.pLength = pData.length;
		this.bare = bare;
		time = System.currentTimeMillis();
	}

	public Packet(byte[] pData) {
		this(pData, false);
	}

	public boolean isBare() {
		return bare;
	}
	
	public long getCreated() {
		return time;
	}

	public int getLength() {
		return pLength;
	}

	public byte[] getData() {
		return pData;
	}
	
	public void decrementCaret(int length) {
		caret = caret - length;
	}
	
	public byte[] readBytes(int length) {
		byte[] data = new byte[length];
		try {
			for (int i = 0;i < length;i++)
				data[i] = pData[i + caret];
		} catch(Exception e) {
			//Player player = (Player)session.getAttachment();
			//Util.banIP(player.getIP(), "Error reading packet (byte)");
			System.out.println(dateFormat.format(date)+": Error reading packet (byte)");
		}
		caret += length;
		return data;
	}
	
	public byte[] getRemainingData() {
		byte[] data = new byte[pLength - caret];
		for (int i = 0; i < data.length; i++)
			data[i] = pData[i + caret];
		caret += data.length;
		return data;
	}

	public byte readByte() {
		return pData[caret++];
	}

	public short readShort() {
		try {
			return (short) ((short) ((pData[caret++] & 0xff) << 8) | (short) (pData[caret++] & 0xff));
		} catch(Exception e) {
			//Player player = (Player)session.getAttachment();
			//Util.banIP(player.getIP(), "Error reading packet (short)");
			System.out.println(dateFormat.format(date)+": Error reading packet (short)");
			return 0;
		}
	}

	public int readInt() {
		try {
			return ((pData[caret++] & 0xff) << 24) | ((pData[caret++] & 0xff) << 16) | ((pData[caret++] & 0xff) << 8) | (pData[caret++] & 0xff);
		} catch(Exception e) {
			//Player player = (Player)session.getAttachment();
			//Util.banIP(player.getIP(), "Error reading packet (int)");
			System.out.println(dateFormat.format(date)+": Error reading packet (int)");
			return 0;
		}
	}

	public long readLong() {
		try {
			return (long) ((long) (pData[caret++] & 0xff) << 56) | ((long) (pData[caret++] & 0xff) << 48) | ((long) (pData[caret++] & 0xff) << 40) | ((long) (pData[caret++] & 0xff) << 32) | ((long) (pData[caret++] & 0xff) << 24) | ((long) (pData[caret++] & 0xff) << 16) | ((long) (pData[caret++] & 0xff) << 8) | ((long) (pData[caret++] & 0xff));
		} catch(Exception e) {
			//Player player = (Player)session.getAttachment();
			//Util.banIP(player.getIP(), "Error reading packet (long)");
			System.out.println(dateFormat.format(date)+": Error reading packet (long)");
			return 0;
		}
	}

	public String readString() {
		return readString(pLength - caret);
	}

	public String readString(int length) {
		String rv = new String(pData, caret, length);
		caret += length;
		return rv;
	}

	public void skip(int x) {
		caret += x;
	}

	public int remaining() {
		return pData.length - caret;
	}
	
	public String printData() {
		//System.out.println(dateFormat.format(date)+": printdata");
		if (pLength == 0)
			return "";
		String data = "";
		for (int i = 0;i < pLength;i++)
			data += " " + pData[i];
		return data.substring(1);
	}

} 