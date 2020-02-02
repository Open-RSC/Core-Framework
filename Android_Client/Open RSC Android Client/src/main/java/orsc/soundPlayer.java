package orsc;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;

import orsc.mudclient;
import orsc.util.GenUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class soundPlayer {
	public static void playSoundFile(String key) {
		try {
			if (!mudclient.optionSoundDisabled) {
				File sound = mudclient.soundCache.get(key + ".wav");
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
					player.setOnCompletionListener((mediaPlayer) -> {
						mediaPlayer.release();
					});

					/*
					Alternative with bare metal AudioTrack, prefered MediaPlayer
					int i = 0;
					byte[] music = null;
					InputStream is = new FileInputStream(sound);

					AudioAttributes audioAttrib = new AudioAttributes.Builder()
						.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
						.setUsage(AudioAttributes.USAGE_MEDIA)
						.build();

					AudioFormat audioForm = new AudioFormat.Builder()
						.setSampleRate(8000)
						.setEncoding(AudioFormat.ENCODING_PCM_16BIT)
						.build();

					int minBufferSize = AudioTrack.getMinBufferSize(8000,
						AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

					AudioTrack at = new AudioTrack(audioAttrib, audioForm, minBufferSize, AudioTrack.MODE_STREAM, 0);

					music = new byte[512];
					at.play();

					while((i = is.read(music)) != -1)
						at.write(music, 0, i);

					at.stop();
					at.release();
					is.close();*/

					/*
					Attempt of using SoundPool but no sound either because file mono not supported
					or it attempts to play as 44k or use distinct encoding
					AudioAttributes audioAttrib = new AudioAttributes.Builder()
						.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
						.setUsage(AudioAttributes.USAGE_MEDIA)
						.build();
					SoundPool soundPool = new SoundPool.Builder()
						.setMaxStreams(2)
						.setAudioAttributes(audioAttrib)
						.build();

					int sound1 = soundPool.load(sound.getPath(), 1);
					soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
						public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
							soundPool.play(sound1, 1.0f, 1.0f, 1, 0, 1.0f);
						}
					});*/

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
