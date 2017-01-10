package com.gameinbucket.thanatopsis;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicExecutor 
{
	private static float faderate = 0.1f;

	private final Context context;

	public float fullvolume;
	private float volume;

	private int raw;
	private MediaPlayer music;

	private int next_r;

	public MusicExecutor(Context c)
	{
		context = c;

		fullvolume = 1;
		volume = fullvolume;

		raw = -1;
		music = null;

		next_r = -1;
	}

	public boolean switchtrack(int r)
	{
		if (raw == r) return false;
		else return true;
	}

	public void load(int r)
	{
		if (music != null)
			end();

		raw = r;
		music = MediaPlayer.create(context, raw);
		music.setLooping(true);
		music.setVolume(fullvolume, fullvolume);

		music.start();
	}

	public void fadeload(int r)
	{
		if (music != null)
		{
			next_r = r;

			volume = fullvolume - faderate;
			music.setVolume(volume, volume);
		}
		else
		{
			raw = r;
			music = MediaPlayer.create(context, raw);
			music.setLooping(true);

			volume = 0;
			music.setVolume(volume, volume);

			music.start();
		}
	}

	public void integrate()
	{
		if (volume < fullvolume)
		{
			if (next_r > -1)
			{
				volume -= faderate;

				if (volume <= 0)
				{
					volume = 0;

					music.stop();
					music.release();
					music = null;

					raw = next_r;
					next_r = -1;

					music = MediaPlayer.create(context, raw);
					music.setLooping(true);
					music.setVolume(volume, volume);

					music.start();
				}
				else
					music.setVolume(volume, volume);
			}
			else
			{
				volume += faderate;

				if (volume >= fullvolume)
					volume = fullvolume;

				music.setVolume(volume, volume);
			}
		}
	}

	public void volume(float v)
	{
		fullvolume = v;

		if (fullvolume < 0) fullvolume = 0;
		else if (fullvolume > 1) fullvolume = 1;

		if (music != null)
			music.setVolume(fullvolume, fullvolume);
	}

	public void shiftvolume(float i)
	{
		fullvolume += i;

		if (fullvolume < 0) fullvolume = 0;
		else if (fullvolume > 1) fullvolume = 1;

		if (music != null)
			music.setVolume(fullvolume, fullvolume);
	}

	public void pause()
	{
		if (music != null)
		{
			if (music.isPlaying())
				music.pause();
		}
	}

	public void resume()
	{
		if (music != null)
		{
			if (!music.isPlaying())
				music.start();
		}
	}

	public void end()
	{
		raw = -1;

		music.stop();
		music.release();
		music = null;
	}
}