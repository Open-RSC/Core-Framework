package com.runescape.client.sound;

import java.io.ByteArrayInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;

public final class SoundProducer {
	
	/*public static Sequencer sequencer;
	
	static
	{
		try
		{
			sequencer = MidiSystem.getSequencer();
		}
		catch (MidiUnavailableException e)
		{
			System.err.println("Error initializing sound effects");
		}
	}*/

	public static void play(SoundTrack track) {
		if (track == null) {
			return;
		}

		switch (track.chunkId) {
		case SoundConfiguration.MIDI_CHUNK_ID:
			try {
				//ByteArrayInputStream bis = new ByteArrayInputStream(track.rawData);
				//Sequence sequence = MidiSystem.getSequence(bis);
				//sequencer.open();
				//sequencer.setSequence(sequence);
				//sequencer.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			break;
		case SoundConfiguration.WAVE_CHUNK_ID:
			AudioFormat format = new AudioFormat(Encoding.PCM_SIGNED, track.sampleRate,
					track.bitsPerSample, track.channels, track.blockAlign, track.byteRate, true);
			Info info = new Info(SourceDataLine.class, format);

			SourceDataLine line = null;
			try {
				line = (SourceDataLine)AudioSystem.getLine(info);
				if (line == null || line.isActive()) {
					return;
				}
				line.open(format);
				line.start();
				ByteArrayInputStream bis = new ByteArrayInputStream(track.rawData);
				AudioInputStream stream = null;
				try {
					stream = AudioSystem.getAudioInputStream(bis);
					byte[] buffer = new byte[1 << 10];

					for (int n = 0; n != -1; n = stream.read(buffer, 0, buffer.length)) {
						line.write(buffer, 0, n);
					}
				}
				finally
				{
					if(stream != null)
					{
						stream.close();
					}
				}

				line.drain();
				line.stop();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			finally
			{
				if(line != null)
				{
					line.close();
				}
			}
			break;
		}
	}

}
