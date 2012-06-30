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

package com.glTron.Video;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLUtils;

public class GLTexture {

	int _texID[];
	boolean boGenMipMap = true;
	
	int WrapS;
	int WrapT;
	
	public int DebugType;
	
	public GLTexture(GL10 gl, Context context, int resource)
	{
		WrapS = GL10.GL_REPEAT;
		WrapT = GL10.GL_REPEAT;
		_texID = new int[1];
		loadTexture(gl,context,resource);
	}

	public GLTexture(GL10 gl, Context context, int resource, int wrap_s, int wrap_t)
	{
		WrapS = wrap_s;
		WrapT = wrap_t;
		_texID = new int[1];
		loadTexture(gl,context,resource);
	}
	
	public GLTexture(GL10 gl, Context context, int resource, int wrap_s, int wrap_t, boolean mipMap)
	{
		WrapS = wrap_s;
		WrapT = wrap_t;
		boGenMipMap = mipMap;
		_texID = new int[1];
		loadTexture(gl,context,resource);
	}

	// Will load a texture out of a drawable resource file, and return an OpenGL texture ID:
	private void loadTexture(GL10 gl, Context context, int resource) {
	    
	    // In which ID will we be storing this texture?
		gl.glGenTextures(1, _texID, 0);
	    
	    // We need to flip the textures vertically:
	    Matrix flip = new Matrix();
	    flip.postScale(1f, -1f);
	    
	    // This will tell the BitmapFactory to not scale based on the device's pixel density:
	    // (Thanks to Matthew Marshall for this bit)
	    BitmapFactory.Options opts = new BitmapFactory.Options();
	    opts.inScaled = false;
	    
	    // Load up, and flip the texture:
	    Bitmap temp = BitmapFactory.decodeResource(context.getResources(), resource, opts);
	    Bitmap bmp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), flip, true);
	    temp.recycle();
	    
	    gl.glBindTexture(GL10.GL_TEXTURE_2D, _texID[0]);
	    
	    // Set all of our texture parameters:
	    if(boGenMipMap)
	    {
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
	    }
	    else
	    {
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
	    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
	    }
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, WrapS);
	    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, WrapT);

	    DebugType = GLUtils.getInternalFormat(bmp);
	    
	    // Generate, and load up all of the mipmaps:
	    if(boGenMipMap)
	    {
		    for(int level=0, height = bmp.getHeight(), width = bmp.getWidth(); true; level++) {
		        // Push the bitmap onto the GPU:
		        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bmp, 0);
		        
		        // We need to stop when the texture is 1x1:
		        if(height==1 && width==1) break;
		        
		        // Resize, and let's go again:
		        width >>= 1; height >>= 1;
		        if(width<1)  width = 1;
		        if(height<1) height = 1;
		        
		        Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, true);
		        bmp.recycle();
		        bmp = bmp2;
		    }
	    }
	    else
	    {
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
	    }
	    
	    bmp.recycle();
	    
	}

	public int getTextureID()
	{
		return _texID[0];
	}
	
}
