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

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import com.glTron.R;

public class WorldGraphics {

	float _grid_size;
	FloatBuffer _WallVertexBuffer[] = new FloatBuffer[4];
	ByteBuffer _IndicesBuffer;
	FloatBuffer _TexBuffer;
	FloatBuffer _FloorTexBuffer;
	FloatBuffer _SkyBoxVertexBuffers[] = new FloatBuffer[6];
	int _NumOfIndices;
	
	// Textures
	GLTexture _SkyBoxTextures[];
	GLTexture _Walls[];
	GLTexture _Floor;
	
	public WorldGraphics(GL10 gl, Context context, float grid_size)
	{
		// Save Grid Size
		_grid_size = grid_size;
		
		initWalls();
		initSkyBox();
		loadTextures(gl,context);
		
		// Setup standard square index and tex buffers
		// Define indices and tex coords
		float t = grid_size / 240.0f;
		byte indices[] = {0,1,3, 0,3,2};
		//float texCoords[] = {0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 0.0f,  1.0f, 1.0f};
		float texCoords[] = {t, 1.0f,   0.0f, 1.0f,  t, 0.0f,  0.0f, 0.0f};
		
		float l = grid_size / 4;
		t = l / 12;
		float florTexCoords [] = {0.0f, 0.0f,  t,0.0f,   0.0f,t,  t,t};

		_FloorTexBuffer = GraphicUtils.ConvToFloatBuffer(florTexCoords);
		_TexBuffer = GraphicUtils.ConvToFloatBuffer(texCoords);
		_IndicesBuffer = GraphicUtils.ConvToByteBuffer(indices);
		_NumOfIndices = indices.length;
		
	}
	
	private void loadTextures(GL10 gl, Context context)
	{
		GLTexture skyBoxTextures[] = { 
				new GLTexture(gl,context, R.drawable.skybox0),
				new GLTexture(gl,context, R.drawable.skybox1),
				new GLTexture(gl,context, R.drawable.skybox2),
				new GLTexture(gl,context, R.drawable.skybox3),
				new GLTexture(gl,context, R.drawable.skybox4),
				new GLTexture(gl,context, R.drawable.skybox5) };
		
		_SkyBoxTextures = skyBoxTextures;
		
		GLTexture Walltex[] = { 
				  new GLTexture(gl,context,R.drawable.gltron_wall_1),
			      new GLTexture(gl,context,R.drawable.gltron_wall_2),
		    	  new GLTexture(gl,context,R.drawable.gltron_wall_3),
				  new GLTexture(gl,context,R.drawable.gltron_wall_4) };
		
		_Walls = Walltex;

		_Floor = new GLTexture(gl,context,R.drawable.gltron_floor);
	}
	
	private void initSkyBox()
	{
		float d = (float)_grid_size * 3;
		
		float sides[][] = {
		  { d, d, -d,    d, -d, -d,    d, -d, d,    d, d, d  }, /* front */
		  { d, d, d,    -d, d, d,     -d, -d, d,    d, -d, d }, /* top */
		  { -d, d, -d,   d, d, -d,     d, d, d,    -d, d, d  }, /* left */
		  { d, -d, -d,  -d, -d, -d,   -d, -d, d,    d, -d, d }, /* right */
		  { -d, d, -d,  -d, -d, -d,    d, -d, -d,   d, d, -d }, /* bottom */
		  { -d, -d, -d, -d, d, -d,    -d, d, d,    -d, -d, d } };/* back */

		for(int i=0; i<6; i++)
		{
			_SkyBoxVertexBuffers[i] = GraphicUtils.ConvToFloatBuffer(sides[i]);
		}
	}
	
	private void initWalls()
	{
		// Setup Wall buffers
		//float t = _grid_size / 240.0f;
		float h =  48.0f; // Wall height
		
		float WallVertices[][] = 
		{
			{ // Wall 1
				0.0f,       0.0f, h,
				_grid_size, 0.0f, h,
				0.0f,       0.0f, 0.0f,
				_grid_size, 0.0f, 0.0f
			},
			{ // Wall 2
				_grid_size, 0.0f,       h,
				_grid_size, _grid_size, h,
				_grid_size, 0.0f,       0.0f,
				_grid_size, _grid_size, 0.0f
			},
			{ // Wall 3
				_grid_size, _grid_size, h,
				0.0f,       _grid_size, h,
				_grid_size, _grid_size, 0.0f,
				0.0f,       _grid_size, 0.0f
			},
			{ // Wall 4
				0.0f,     _grid_size, h,
				0.0f,     0.0f,       h,
				0.0f,     _grid_size, 0.0f,
				0.0f,     0.0f,       0.0f
			}
		};
		
		_WallVertexBuffer[0] = GraphicUtils.ConvToFloatBuffer(WallVertices[0]);
		_WallVertexBuffer[1] = GraphicUtils.ConvToFloatBuffer(WallVertices[1]);
		_WallVertexBuffer[2] = GraphicUtils.ConvToFloatBuffer(WallVertices[2]);
		_WallVertexBuffer[3] = GraphicUtils.ConvToFloatBuffer(WallVertices[3]);
		
	}
	
	public void drawWalls(GL10 gl)
	{
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);
		
		for(int Walls=0; Walls<4; Walls++) {
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, _Walls[Walls].getTextureID());
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _WallVertexBuffer[Walls]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _TexBuffer);
			//Draw the vertices as triangles, based on the Index Buffer information
			gl.glDrawElements(GL10.GL_TRIANGLES, _NumOfIndices, GL10.GL_UNSIGNED_BYTE, _IndicesBuffer);
			//Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		
	}
	
	public void drawFloorTextured(GL10 gl)
	{
		int i,j,l;
		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, _Floor.getTextureID());
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		l = (int)(_grid_size / 4);
		
		for(i = 0; i < (int)_grid_size; i += l) {
			for(j = 0; j < (int)_grid_size; j += l) {
				float rawVertices[] = new float[4 * 3];
				
				rawVertices[0] = (float)i;
				rawVertices[1] = (float)j;
				rawVertices[2] = 0.0f;
				
				rawVertices[3] = (float)(i + l);
				rawVertices[4] = (float)j;
				rawVertices[5] = 0.0f;
				
				rawVertices[6] = (float)i;
				rawVertices[7] = (float)(j + l);
				rawVertices[8] = 0.0f;

				rawVertices[9] = (float)(i + l);
				rawVertices[10] = (float)(j + l);
				rawVertices[11] = 0.0f;

				FloatBuffer VertexBuffer = GraphicUtils.ConvToFloatBuffer(rawVertices);
				
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
				//gl.glFrontFace(GL10.GL_CCW);

				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, VertexBuffer);
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _FloorTexBuffer);
				
				//Draw the vertices as triangles, based on the Index Buffer information
				gl.glDrawElements(GL10.GL_TRIANGLES, _NumOfIndices, GL10.GL_UNSIGNED_BYTE, _IndicesBuffer);
				
				//Disable the client state before leaving
				gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
		}
	}
	
	public void drawSkyBox(GL10 gl)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDepthMask(false);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		for(int i=0; i<6; i++)
		{
			gl.glBindTexture(GL10.GL_TEXTURE_2D, _SkyBoxTextures[i].getTextureID());
			
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			//gl.glFrontFace(GL10.GL_CCW);

			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, _SkyBoxVertexBuffers[i]);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, _TexBuffer);
			
			gl.glDrawElements(GL10.GL_TRIANGLES, _NumOfIndices, GL10.GL_UNSIGNED_BYTE, _IndicesBuffer);

			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		}
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDepthMask(true);

	}
	
	
}
