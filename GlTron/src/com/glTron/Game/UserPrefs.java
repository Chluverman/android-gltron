/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 * 
 * Preferences implementation based on work by Noah NZM TECH
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

package com.glTron.Game;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.glTron.Game.Camera;

public class UserPrefs {

	// Pref defaults
	private final int C_PREF_FOLLOW_CAM = 1;
	private final int C_PREF_FOLLOW_CAM_FAR = 2;
	private final int C_PREF_FOLLOW_CAM_CLOSE = 3;
	private final int C_PREF_BIRD_CAM = 4;
	
	private final String C_DEFAULT_CAM_TYPE = "1";
	
	private static final float C_GRID_SIZES[] = {360.0f, 720.0f, 1440.0f};
	
	private static final float C_SPEED[] = {5.0f, 10.0f, 15.0f, 20.0f};
	
	private Context mContext;
	private Camera.CamType mCameraType;
	
	private boolean mMusic;
	private boolean mSFX;
	
	private boolean mFPS;
	
	private int mNumOfPlayers;
	private float mGridSize;
	private float mSpeed;
	private int mPlayerColourIndex;
	
	public UserPrefs(Context ctx)
	{
		mContext = ctx;
		ReloadPrefs();
	}
	
	public void ReloadPrefs()
	{
		int cameraType;
		int gridIndex;
		int speedIndex;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		cameraType = Integer.valueOf(prefs.getString("cameraPref",C_DEFAULT_CAM_TYPE));
		
		switch(cameraType)
		{
			case C_PREF_FOLLOW_CAM:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW;
				break;
			case C_PREF_FOLLOW_CAM_FAR:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW_FAR;
				break;
			case C_PREF_FOLLOW_CAM_CLOSE:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW_CLOSE;
				break;
			case C_PREF_BIRD_CAM:
				mCameraType = Camera.CamType.E_CAM_TYPE_BIRD;
			default:
				mCameraType = Camera.CamType.E_CAM_TYPE_FOLLOW;
				break;
		}
		
		mMusic = prefs.getBoolean("musicOption", true);
		mSFX = prefs.getBoolean("sfxOption", true);
		mFPS = prefs.getBoolean("fpsOption", false);
		mNumOfPlayers = Integer.valueOf(prefs.getString("playerNumber", "4"));
		gridIndex = Integer.valueOf(prefs.getString("arenaSize", "1"));
		mGridSize = C_GRID_SIZES[gridIndex];
		speedIndex = Integer.valueOf(prefs.getString("gameSpeed", "1"));
		mSpeed = C_SPEED[speedIndex];
		mPlayerColourIndex = Integer.valueOf(prefs.getString("playerBike","0"));
	}
	
	public Camera.CamType CameraType()
	{
		
		return mCameraType;
	}
	
	public boolean PlayMusic()
	{
		return mMusic;
	}
	
	public boolean PlaySFX()
	{
		return mSFX;
	}
	
	public boolean DrawFPS()
	{
		return mFPS;
	}
	
	public int NumberOfPlayers()
	{
		return mNumOfPlayers;
	}
	
	public float GridSize()
	{
		return mGridSize;
	}
	
	public float Speed()
	{
		return mSpeed;
	}
	
	public int PlayerColourIndex()
	{
		return mPlayerColourIndex;
	}
}
