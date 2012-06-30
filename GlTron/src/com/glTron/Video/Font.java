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

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class Font {

	//private int _nTextures;
	public int _texwidth;
	public int _width;
	public int _lower;
	public int _upper;
	
	private GL10 gl;
	
	// Hard code to only 2 textures as thats
	// what both fonts use in the default art pack
	private GLTexture Tex1;
	private GLTexture Tex2;
	
	public Font(GL10 gl1, Context context, int tex1, int tex2)
	{
		gl = gl1;
		Tex1 = new GLTexture(gl,context,tex1,GL10.GL_CLAMP_TO_EDGE,GL10.GL_CLAMP_TO_EDGE,false);
		Tex2 = new GLTexture(gl,context,tex2,GL10.GL_CLAMP_TO_EDGE,GL10.GL_CLAMP_TO_EDGE,false);
		//_nTextures = 2;
	}
	
	public void drawText(int x, int y, int size, String text)
	{
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glPushMatrix();
		gl.glTranslatef(x, y, 0);
		gl.glScalef(size, size, size);
		
		renderString(text);
		
		gl.glPopMatrix();
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
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
					gl.glBindTexture(GL10.GL_TEXTURE_2D, Tex1.getTextureID());
				else
					gl.glBindTexture(GL10.GL_TEXTURE_2D, Tex2.getTextureID());
				
				gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
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
			
			gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuff);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
			gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 6);
			
		}
	}
}
