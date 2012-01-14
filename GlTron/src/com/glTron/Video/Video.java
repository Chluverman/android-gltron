package com.glTron.Video;

import javax.microedition.khronos.opengles.GL10;

public class Video {

	float _height, _width;
	int _vp_x, _vp_y;
	int _vp_h, _vp_w;
	
	int _onScreen;
	
	public Video(int width, int height)
	{
		SetWidthHeight(width,height);
		_vp_x = 0;
		_vp_y = 0;
		_vp_w = Float.floatToIntBits(_width);
		_vp_h = Float.floatToIntBits(_height);
	}
	
	public void SetWidthHeight(int width, int height)
	{
		_height = Float.intBitsToFloat(height);
		_width = Float.intBitsToFloat(width);
	}
	
	public void rasonly(GL10 gl)
	{
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0.0f, (float)_vp_w, 0.0f, (float)_vp_h, 0.0f, 1.0f);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glViewport(0, 0, _vp_w, _vp_h);
	}
	
	public void doPerspective(GL10 gl, float GridSize)
	{
		int w,h;
		
//		gl.glMatrixMode(GL10.GL_PROJECTION);
//		float size = .01f * (float) Math.tan(Math.toRadians(45.0) / 2); 
//	    float ratio = _width / _height;
//	    // perspective:
//	    gl.glFrustumf(-size, size, -size / ratio, size / ratio, 0.01f, 100.0f);
//		w = Float.floatToIntBits(_width);
//	    h = Float.floatToIntBits(_height);
//	    gl.glViewport(0, 0, w, h);
		
		float top;
		float left;
		float ratio = _width / _height;
		//float znear = 0.5f;
		float znear = 1.0f;
		float zfar = (float)(GridSize * 6.5f);
		//float fov = 120.0f;
		float fov = 105.0f;
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		top = (float)Math.tan(fov * Math.PI / 360.0f) * (float)znear;
		left = (float)(((float)-top)*((float)ratio));
		gl.glFrustumf(left, -left, -top,  top, znear, zfar);
		
		w = Float.floatToIntBits(_width);
	    h = Float.floatToIntBits(_height);
		gl.glViewport(0, 0, w, h);
		
		
	}
	
	public int GetWidth()
	{
		return Float.floatToIntBits(_width);
	}
	
	public int GetHeight()
	{
		return Float.floatToIntBits(_height);
	}
	
}
