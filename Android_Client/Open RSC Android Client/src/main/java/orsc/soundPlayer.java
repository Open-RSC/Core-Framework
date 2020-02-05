package orsc;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import java.io.File;

public class soundPlayer {
    public static void playSoundFile(String key) {
        try {
            if (!orsc.mudclient.optionSoundDisabled) {
                File sound = orsc.mudclient.soundCache.get(key + ".wav");
                if (sound == null)
                    return;
                try {
                    MediaPlayer player = new MediaPlayer();
                    AudioAttributes audioAttrib = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build();
                    player.setDataSource(sound.getPath());
                    player.setAudioAttributes(audioAttrib);
                    player.setLooping(false);
                    player.prepare();
                    player.start();
                    player.setOnCompletionListener(MediaPlayer::release);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch (RuntimeException ignored) {
        }
    }
}
