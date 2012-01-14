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
