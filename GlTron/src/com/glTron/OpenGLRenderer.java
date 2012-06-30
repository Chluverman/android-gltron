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

public class OpenGLRenderer implements GLSurfaceView.Renderer {

	GLTronGame Game = new GLTronGame();
	
	Context mContext;
	
	String Debug;
	StringBuffer sb = new StringBuffer(40);

	private int frameCount = 0;
	
	public OpenGLRenderer(Context context, int win_width, int win_height)
	{
		mContext = context;
		Log.e("GLTRON", "Renderer Constructor: Create Video Object");
		Debug = sb.append("Screen size = ").append(win_width).append(",").append(win_height).toString();
		Log.e("GLTRON", Debug);
		Game.updateScreenSize(win_width, win_height);
	}
	
	public void setUI_Handler(Handler handler)
	{
		Game.setUI_Handler(handler);
	}
	
	public void onTouch(float x, float y)
	{
		Game.addTouchEvent(x, y);
	}
	
	public void onPause()
	{
		Game.pauseGame();
	}
	
	public void onResume()
	{
		Game.resumeGame();
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

	    Log.e("GLTRON", "Renderer: Surface Created Do perspective");

	    //Game.initialiseGame(mContext, gl);
	    Game.drawSplash(mContext, gl);
	}

	
	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		Log.e("GLTRON", "Renderer: Surface changed");
		sb=null;
		sb = new StringBuffer(40);
		Debug = sb.append("Screen size = ").append(w).append(",").append(h).toString();
		Log.e("GLTRON", Debug);
		Game.updateScreenSize(w, h);
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {

		if(frameCount == 1)
		{
			Game.initialiseGame();
		}
		else if(frameCount > 1)
		{
			Game.RunGame();
		}

		frameCount++;
		
	}

	
}
