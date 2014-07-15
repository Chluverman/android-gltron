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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;

import com.glTron.logging.Logger;

public class OpenGLView extends GLSurfaceView {
	
	private OpenGLRenderer _renderer;
	
	private float _x = 0;
	private float _y = 0;
	
	public OpenGLView(Context context,int width, int height) {
		super(context);
		Logger.v(this, "View's constructor called");
		Logger.v(this, "Setting up the renderer object");
		_renderer = new OpenGLRenderer(context, width, height);
		setRenderer(_renderer);

		Logger.v(this, "All set for View");
	}
	
	public void onPause()
	{
		Logger.v(this, "View Paused");
		_renderer.onPause();
	}
	
	public void onResume()
	{
		Logger.v(this, "View Resumed");
		_renderer.onResume();
	}
	
	public boolean onTouchEvent(final MotionEvent event) 
	{

		if(event.getAction() == MotionEvent.ACTION_DOWN) 
		{
			_x = event.getX();
			_y = event.getY();
			_renderer.onTouch(_x, _y);
			
		}
		
		return true;
	}
}
