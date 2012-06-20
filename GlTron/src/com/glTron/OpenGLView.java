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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;

public class OpenGLView extends GLSurfaceView {
	
	private OpenGLRenderer _renderer;
	
	private float _x = 0;
	private float _y = 0;
	
	public OpenGLView(Context context,int width, int height) {
		super(context);
		_renderer = new OpenGLRenderer(context, width, height);
		setRenderer(_renderer);
	}
	
	public void setUI_Handler(Handler handler)
	{
		_renderer.setUI_Handler(handler);
	}
	
	public void onPause()
	{
		_renderer.onPause();
	}
	
	public void onResume()
	{
		_renderer.onResume();
	}
	
	public boolean onTouchEvent(final MotionEvent event) {

		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			_x = event.getX();
			_y = event.getY();
			_renderer.onTouch(_x, _y);
			
		}
		
		return true;
	}
}
