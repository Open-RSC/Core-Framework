package com.runescape;

import java.io.InputStream;

import sun.audio.AudioPlayer;

public class AudioReader extends InputStream {

	private byte[] dataArray;
	private int offset;
	private int length;

	public AudioReader() {
		AudioPlayer.player.start(this);
	}

	public void stopAudio() {
		AudioPlayer.player.stop(this);
	}

	public void loadData(byte[] abyte0, int i, int j) {
		dataArray = abyte0;
		offset = i;
		length = i + j;
	}

	public int read(byte[] abyte0, int i, int j) {
		try {
		for (int k = 0; k < j; k++) {
			if (offset < length) {
				abyte0[i + k] = dataArray[offset++];
			} else {
				abyte0[i + k] = -1;
			}
		}
		return j;
		}catch(ArrayIndexOutOfBoundsException e ) {
			return 1;
		}
	}

	public int read() {
		byte abyte0[] = new byte[1];
		read(abyte0, 0, 1);
		return abyte0[0];
	}
}