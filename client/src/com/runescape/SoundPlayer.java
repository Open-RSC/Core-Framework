package com.runescape;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import com.runescape.client.cache.CacheUtil;
import com.runescape.client.sound.SoundProducer;
import com.runescape.client.sound.SoundTrack;
import com.runescape.client.util.Buffer;

public class SoundPlayer
{
	private final byte[] archive;
	
	public SoundPlayer(String cache)
	{
		this.archive = CacheUtil.loadArchive(cache);
	}

	private final ExecutorService ex = Executors.newCachedThreadPool();
	ReentrantLock lock = new ReentrantLock();
	public void play(String sound)
	{
		try
		{
			byte[] data = CacheUtil.fileContents(sound, archive);
			Buffer buffer = new Buffer(data);
			final SoundTrack track = SoundTrack.fromBuffer(sound, buffer);
			ex.execute(
				new Runnable()
				{
					@Override
					public final void run()
					{
							if(lock.tryLock())
							{
								try
								{
									SoundProducer.play(track);
								}
								finally
								{
									lock.unlock();
								}
							}
					}
				});
		}
		catch(Exception e)
		{
			//System.err.println("Failed to play sound: " + sound);
		}
	}	
}
