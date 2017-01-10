package com.gameinbucket.rendering;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

public class AudioHandler 
{
	private SoundPool sound_pool;
	private SparseIntArray ids;

	public AudioHandler(int stream_limit)
	{
		ids = new SparseIntArray();
		sound_pool = new SoundPool(stream_limit, AudioManager.STREAM_MUSIC, 0);
	}

	public void load(Context c, int r)
	{
		ids.put(r, sound_pool.load(c, r, 1));
	}

	public void play(int r)
	{
		sound_pool.play(ids.get(r), 1.0f, 1.0f, 1, 0, 1.0f);
	}
	
	/*public void play(int r, float gain)
	{
		sound_pool.play(ids.get(r), gain, gain, 1, 0, 1.0f);
	}*/

	public void play(int r, float gain, float radian)
	{
		if (gain < 0.0f) gain = 0.0f;
		else if (gain > 1.0f) gain = 1.0f;

		float pan = (float)Math.sin(radian);
		float left = Math.max(0.0f, Math.min(1.0f, gain * Math.max(0.0f, Math.min(1.0f, 1.0f - pan))));
        float right = Math.max(0.0f, Math.min(1.0f, gain * Math.max(0.0f, Math.min(1.0f, 1.0f + pan))));

		sound_pool.play(ids.get(r), left * gain, right * gain, 1, 0, 1.0f);
	}

	/*public void play(int r, float gain, float left, float right)
	{
		sound_pool.play(ids.get(r), left * gain, right * gain, 1, 0, 1.0f);
	}*/
	
	/*public void play(int r, float gain, float pan, float pitch, boolean pad)
	{
		float left = Math.max(0.0f, Math.min(1.0f, gain * Math.max(0.0f, Math.min(1.0f, 1.0f - pan))));
        float right = Math.max(0.0f, Math.min(1.0f, gain * Math.max(0.0f, Math.min(1.0f, 1.0f + pan))));
        float rate = Math.max(0.5f, Math.min(2.0f, pitch));
        
		sound_pool.play(ids.get(r), left * gain, right * gain, 1, 0, rate);
	}*/

	public void release()
	{
		sound_pool.release();
		sound_pool = null;
	}
}
