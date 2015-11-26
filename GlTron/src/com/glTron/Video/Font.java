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

import java.nio.FloatBuffer;

import android.opengl.GLES10;

import android.content.Context;

public class Font {

	//private int _nTextures;
	public int _texwidth;
	public int _width;
	public int _lower;
	public int _upper;
	
	private GLES10 gl;
	
	// Hard code to only 2 textures as thats
	// what both fonts use in the default art pack
	private GLTexture Tex1;
	private GLTexture Tex2;
	
	public Font(Context context, int tex1, int tex2)
	{
		Tex1 = new GLTexture(context,tex1,GLES10.GL_CLAMP_TO_EDGE,GLES10.GL_CLAMP_TO_EDGE,false);
		Tex2 = new GLTexture(context,tex2,GLES10.GL_CLAMP_TO_EDGE,GLES10.GL_CLAMP_TO_EDGE,false);
		//_nTextures = 2;
	}
	
	public void drawText(int x, int y, int size, String text)
	{
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		
		GLES10.glPushMatrix();
		GLES10.glTranslatef(x, y, 0);
		GLES10.glScalef(size, size, size);
		
		renderString(text);
		
		GLES10.glPopMatrix();
		GLES10.glDisable(GLES10.GL_TEXTURE_2D);
		GLES10.glDisable(GLES10.GL_BLEND);
	}
	
	private void renderString(String str)
	{
		int w = _texwidth / _width;
		float cw = (float)_width / (float)_texwidth;
		float cx,cy;
		int i, index;
		int tex;
		int bound = -1;
		
		float vertex[] = new float[6 * 2];
		float textre[] = new float[6 * 2];
		FloatBuffer vertexBuff;
		FloatBuffer texBuff;
		
		// skip color bit is it used?
		
		for(i = 0; i < str.length(); i++)
		{
			index = str.charAt(i) - _lower + 1;
			
			if(index >= _upper)
				return; // index out of bounds
			
			tex = index / (w * w);
			
			// Bind texture
			if(tex != bound)
			{
				if(tex == 0)
					GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, Tex1.getTextureID());
				else
					GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, Tex2.getTextureID());
				
				GLES10.glTexEnvf(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_MODULATE);
				bound = tex;
			}
			
			// find texture coordinates
			index = index % (w * w);
			cx = (float)(index % w) / (float)w;
			cy = (float)(index / w) / (float)w;
			
			// draw character
			textre[0] = cx;
			textre[1] = 1.0f-cy-cw;
			vertex[0] = (float)i;
			vertex[1] = 0.0f;
			
			textre[2] = cx+cw;
			textre[3] = 1.0f-cy-cw;
			vertex[2] = i + 1.0f;
			vertex[3] = 0.0f;
			
			textre[4] = cx + cw;
			textre[5] = 1.0f - cy;
			vertex[4] = i + 1.0f;
			vertex[5] = 1.0f;
			
			textre[6] = cx + cw;
			textre[7] = 1.0f - cy;
			vertex[6] = i + 1.0f;
			vertex[7] = 1.0f;
			
			textre[8] = cx;
			textre[9] = 1.0f - cy;
			vertex[8] = i;
			vertex[9] = 1.0f;
			
			textre[10] = cx;
			textre[11] = 1.0f - cy - cw;
			vertex[10] = i;
			vertex[11] = 0.0f;
			
			vertexBuff = GraphicUtils.ConvToFloatBuffer(vertex);
			texBuff = GraphicUtils.ConvToFloatBuffer(textre);
			
			GLES10.glVertexPointer(2, GLES10.GL_FLOAT, 0, vertexBuff);
			GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, texBuff);
			GLES10.glDrawArrays(GLES10.GL_TRIANGLES, 0, 6);
			
		}
	}
}
