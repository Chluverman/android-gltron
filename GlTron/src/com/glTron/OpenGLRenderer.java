/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 *
 * This file is part of GL TRON.
 *
 * GL TRON is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GL TRON is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GL TRON.  If not, see <http://www.gnu.org/licenses/>.
 *
 */


package com.glTron;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;

import com.glTron.Game.GLTronGame;

import com.glTron.logging.Logger;

public class OpenGLRenderer implements GLSurfaceView.Renderer 
{

	GLTronGame Game;
	
	Context mContext;
	
	private int frameCount = 0;
	
	public OpenGLRenderer(Context context, int win_width, int win_height)
	{
		Logger.v(this, "Setting up the Renderer Object");
		Game = new GLTronGame();
		mContext = context;
		Game.updateScreenSize(win_width, win_height);
	}
	
	
	public void onTouch(float x, float y)
	{
		Logger.v(this, "Renderer received touch co-ordinates, passing it on to the Game object");
		Game.addTouchEvent(x, y);
	}
	
	public void onPause()
	{
		Logger.v(this, "Renderer received the on Pause signal, passing it on to the Game object");
		Game.pauseGame();
	}
	
	public void onResume()
	{
		Logger.v(this, "Renderer received the on Resume signal, passing it on to the Game object");
		Game.resumeGame();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
	    Logger.Debug(this, "Surface Created, Do perspective");

	    Game.drawSplash(mContext, gl);
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{
		Logger.Debug(this, "Surface changed, Update the game screen");
		Game.updateScreenSize(w, h);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) 
	{
		if(frameCount == 1)
		{ Game.initialiseGame(); }
		else if(frameCount > 1) 
		{ Game.RunGame(); }

		frameCount++;
	}

	
}
