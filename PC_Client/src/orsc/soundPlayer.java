package orsc;

import orsc.util.GenUtil;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.io.File;

public class soundPlayer {
	public static void playSoundFile(String key) {
		try {
			if (!mudclient.optionSoundDisabled) {
				File sound = mudclient.soundCache.get(key + ".wav");
				if (sound == null)
					return;
				try {
					// PC sound code:
					final Clip clip = AudioSystem.getClip();
					clip.addLineListener(myLineEvent -> {
						if (myLineEvent.getType() == LineEvent.Type.STOP)
							clip.close();
					});
					clip.open(AudioSystem.getAudioInputStream(sound));
					clip.start();

					// Android sound code:
					//int dataLength = DataOperations.getDataFileLength(key + ".pcm", soundData);
					//int offset = DataOperations.getDataFileOffset(key + ".pcm", soundData);
					//clientPort.playSound(soundData, offset, dataLength);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (RuntimeException var6) {
			throw GenUtil.makeThrowable(var6, "client.SC(" + "dummy" + ',' + (key != null ? "{...}" : "null") + ')');
		}
	}
}
