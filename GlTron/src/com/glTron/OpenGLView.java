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
import android.view.MotionEvent;

import com.glTron.logging.Logger;
import com.glTron.Game.GLTronGame;

public class OpenGLView extends GLSurfaceView implements GLSurfaceView.Renderer 
{
	
	GLTronGame Game;
	Context mContext;
	private int frameCount = 0;
	
	public OpenGLView(Context context) 
	{
		super(context);
		Logger.v(this, "View's constructor called");

		setRenderer(this);
		Game = new GLTronGame();
		mContext = context;

		Logger.v(this, "All set for View");
	}
	
	public void onPause()
	{
		Logger.v(this, "View Paused");
		//_renderer.onPause();
		Game.pauseGame();
	}
	
	public void onResume()
	{
		Logger.v(this, "View Resumed");
		//_renderer.onResume();
		Game.resumeGame();
	}
	
	public boolean onTouchEvent(final MotionEvent event) 
	{
		Logger.v(this, "Touch Event detected at (x , y) : " + event.getX() + event.getY());

		if(event.getAction() == MotionEvent.ACTION_DOWN) 
		{
			Logger.v(this, "Passing the touchevent to the renderer object");
			//_renderer.onTouch(event.getX(),event.getY());
			Game.addTouchEvent(event.getX(),event.getY());
		}
		
		return true;
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
		{ 
			Logger.v(this, "Drawing the first frame for the game");
			Game.initialiseGame(); 
		}
		else if(frameCount > 1) 
		{ 
			Game.RunGame(); 
		}

		frameCount++;
	}
}
