/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version; provided that the above copyright notice appear 
 * in all copies and that both that copyright notice and this permission 
 * notice appear in supporting documentation
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-1.0.html
 * 
 * THE COPYRIGHT HOLDERS DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */

package com.glTron.Sound;

import java.io.IOException;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundManager {

	// music
	private static MediaPlayer mPlayer;
	
	// sound effects
	private static SoundManager _instance;
	private static SoundPool mSoundPool;
	private static HashMap<Integer, Integer> mSoundPoolMap;
	private static AudioManager mAudioManager;
	private static Context mContext;
	
	private static final int MAX_SOUNDS = 4;
	
	private static int MAX_INDEX = 10;
	
	private static int mIndexToStream[] = new int[MAX_INDEX];

	
	private SoundManager()
	{
	}

	/*
	 * Requests the instance if the sound manager and creates it
	 * if it does not exist
	 */
	static synchronized public SoundManager getInstance()
	{
		if(_instance == null)
			_instance = new SoundManager();
		return _instance;
	}
	
	public static void initSounds(Context theContext)
	{
		mContext = theContext;
		mSoundPool = new SoundPool(MAX_SOUNDS,AudioManager.STREAM_MUSIC, 0);
		mSoundPoolMap = new HashMap<Integer, Integer>();
		mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public static void addSound(int Index, int SoundID)
	{
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public static void addMusic(int MusicID)
	{
		// limited to one music stream FIXME
		mPlayer = MediaPlayer.create(mContext, MusicID);
	}
	
	public static void playMusic(boolean boLoop)
	{
		if(mPlayer != null)
		{	
			float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			
			mPlayer.setLooping(boLoop);
			mPlayer.setVolume(streamVolume, streamVolume);
			mPlayer.start();
		}
	}
	
	public static void stopMusic()
	{
		if(mPlayer != null)
		{
			if(mPlayer.isPlaying())
			{
				mPlayer.stop();
				try
				{
					mPlayer.prepare();
					mPlayer.seekTo(0);
				}
				catch (IOException e)
				{
					// do nothing here FIXME
				}
			}
		}
	}
	
	public static void playSound(int index, float speed)
	{
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		streamVolume *= 0.5;
		mIndexToStream[index] = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, speed);
	}
	
	public static void playSoundLoop(int index, float speed)
	{
		float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		streamVolume /= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		streamVolume *= 0.5;
		mIndexToStream[index] = mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, speed);
	}

	public static void stopSound(int index)
	{
		//mSoundPool.stop(mSoundPoolMap.get(index));
		mSoundPool.stop(mIndexToStream[index]);
	}

	public static void changeRate(int index, float rate)
	{
		//mSoundPool.setRate(mSoundPoolMap.get(index), rate);
		mSoundPool.setRate(mIndexToStream[index], rate);
	}
	
	public static void globalPauseSound()
	{
		if(mSoundPool != null)
			mSoundPool.autoPause();
		
		if(mPlayer != null && mPlayer.isPlaying())
			mPlayer.pause();
	}
	
	public static void globalResumeSound()
	{
		if(mSoundPool != null)
			mSoundPool.autoResume();
		
		if(mPlayer != null)
			mPlayer.start();
	}
	
	public static void cleanup()
	{
		mSoundPool.release();
		mSoundPool = null;
		mSoundPoolMap.clear();
		mAudioManager.unloadSoundEffects();
		_instance = null;
	}
	
}
