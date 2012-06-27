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

package com.glTron;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class glTron extends Activity {
    /** Called when the activity is first created. */
	private OpenGLView _View;
	
	private Boolean _FocusChangeFalseSeen = false;
	private Boolean _Resume = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
		WindowManager w = getWindowManager();
	    Display d = w.getDefaultDisplay();
	    int width = d.getWidth();
	    int height = d.getHeight();
	   
	    super.onCreate(savedInstanceState);
	    
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
        _View = new OpenGLView(this, width, height);
        setContentView(_View);

    }
    
    
    @Override
    public void onPause() {
    	_View.onPause();
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	if(!_FocusChangeFalseSeen)
    	{
    		_View.onResume();
    	}
    	_Resume = true;
    	super.onResume();
    }
    
    @Override
    public void onWindowFocusChanged(boolean focus) {
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
    
    //open menu when key pressed
     public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
        	this.startActivity(new Intent(this, Preferences.class));
        }
        return super.onKeyUp(keyCode, event);
    }
}