/*
 * Copyright © 2012 Ravi Agarwal (flide)
 *
 * Based on Android port of GLtron by Iain Churcher and original source code can be found at :
 * https://github.com/Chluverman/android-gltron.git
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.glTron.logging.Logger;

public class glTron extends Activity 
{

	private OpenGLView _View;
	
	private Boolean _FocusChangeFalseSeen = false;
	private Boolean _Resume = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		Logger.v(this,"Starting up the application");
		super.onCreate(savedInstanceState);

		Logger.v(this,"Setting up fullscreen flags");

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setImmersiveMode();
		    
		Logger.v(this,"Setting up the View");
		_View = new OpenGLView(this);
		setContentView(_View);

		Logger.v(this,"onCreate function ended");
	}
    
    
    	private void setImmersiveMode()
	{
		Logger.v(this, "Starting the setImmersiveMode()");
		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;

		//Set the Flags for maximum Screen utilization
		
		if(Build.VERSION.SDK_INT >= 18)
		{
			//We have Immersive mode available
			newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		if(Build.VERSION.SDK_INT >= 16)
		{
			 newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_FULLSCREEN;
			 newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			 newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
		}

		if(Build.VERSION.SDK_INT >= 14)
		{
			 newUiOptions = newUiOptions | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		if(Build.VERSION.SDK_INT >= 1)
		{
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			 getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		}
		//All flags are a go, let it rip!!
		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);

		Logger.v(this, "Ending the setImmersiveMode()");
	}

	@Override
	public void onPause() 
	{
		Logger.v(this, "Application Paused");
		_View.onPause();
		super.onPause();
	}
    
	@Override
	public void onResume() 
	{
		Logger.v(this, "Application Resumed");
		if(!_FocusChangeFalseSeen)
		{
			_View.onResume();
		}
		setImmersiveMode();
		_Resume = true;
		super.onResume();
	}
    
	@Override
	public void onWindowFocusChanged(boolean focus) 
	{
		Logger.v(this, "Window Focus Changed");
		if(focus)
		{
			if(_Resume)
			{
				_View.onResume();
			}
			
			_Resume = false;
			_FocusChangeFalseSeen = false;
		}
		else
		{
			_FocusChangeFalseSeen = true;
		}
	}   
    
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_MENU) 
		{
			Logger.i(this, "Menu Key Pressed, Calling the Preferences Activity");
			this.startActivity(new Intent(this, Preferences.class));
		}
		return super.onKeyUp(keyCode, event);
	}
}
