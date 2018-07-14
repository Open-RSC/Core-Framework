package com.runescape.client.sound;

import java.util.HashMap;
import java.util.Map;

import com.runescape.client.util.Buffer;

public final class SoundTrack {
	
	private static final Map<String, SoundTrack> cache = new HashMap<String, SoundTrack>();

	public static SoundTrack fromBuffer(String name, Buffer buffer) throws SoundTrackFormatException {
		if (cache.containsKey(name)) {
			return cache.get(name);
		}
		
		SoundTrack track = new SoundTrack();
		track.rawData = buffer.payload;

		track.chunkId = buffer.getInt();
		track.chunkSize = buffer.getLEInt();
		track.format = buffer.getInt();
		
		// useful resources
		// midi: http://faydoc.tripod.com/formats/mid.htm
		// wave: http://soundfile.sapp.org/doc/WaveFormat/	
		switch (track.chunkId) {
		case SoundConfiguration.MIDI_CHUNK_ID:
			// do nothing
			break;
		case SoundConfiguration.WAVE_CHUNK_ID:
			while ((buffer.payload.length - buffer.offset) > 0) {
				int subchunkId = buffer.getInt();
				int subchunkSize = buffer.getLEInt();

				if (subchunkId == 0x666d7420) { // "fmt "
					int audioFormat = buffer.getLEShort();
					track.channels = buffer.getLEShort();
					track.sampleRate = buffer.getLEInt();
					track.byteRate = buffer.getLEInt();
					track.blockAlign = buffer.getLEShort();
					track.bitsPerSample = buffer.getLEShort();

					int skipAmount = Math.min(subchunkSize - 16, 0);

					if (skipAmount > 0) {
						buffer.skip(skipAmount);
						System.out.printf("Excess data in sub-chunk 0x%x, dropping %d bytes%n", subchunkId, skipAmount);
					}

					if (audioFormat != 1) {
						throw new SoundTrackFormatException();
					}
				} else if (subchunkId == 0x64617461) { // "data"
					byte[] subchunkData = new byte[subchunkSize];
					buffer.getBytes(subchunkData, 0, subchunkSize);
					track.data = subchunkData;
				} else {
					throw new SoundTrackFormatException();
				}
			}
			break;
		default:
			System.out.printf("Unknown audio format 0x%x%n", track.chunkId);
			throw new SoundTrackFormatException();
		}
		
		cache.put(name, track);
		return track;
	}

	public int chunkId, chunkSize, format, channels, sampleRate, byteRate, blockAlign, bitsPerSample;

	public byte[] rawData, data;

}