package com.openrsc.client.pc;

import java.io.InputStream;

import sun.audio.AudioPlayer;

public class StreamAudioPlayer extends InputStream {
	byte buffer[];
	int start;
	int end;

	public StreamAudioPlayer() {
		AudioPlayer.player.start(this);
	}

	public void stopPlayer() {
		AudioPlayer.player.stop(this);
	}

	public void writeStream(byte buf[], int off, int len) {
		buffer = buf;
		start = off;
		end = off + len;
		System.out.println("BUFF: " + buffer.length + ", " + start + ", " + end);
	}

	public int read(byte abyte0[], int i, int j) {
		for (int k = 0; k < j; k++)
			if (start < end)
				abyte0[i + k] = buffer[start++];
			else
				abyte0[i + k] = -1;

		return j;
	}

	public int read() {
		byte abyte0[] = new byte[1];
		read(abyte0, 0, 1);
		return abyte0[0];
	}
}
