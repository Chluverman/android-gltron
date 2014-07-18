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

import android.opengl.GLES10;
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
	
	public WorldGraphics(Context context, float grid_size)
	{

		// Save Grid Size
		_grid_size = grid_size;
		
		initWalls();
		initSkyBox();
		loadTextures(context);
		
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
	
	private void loadTextures(Context context)
	{
		GLTexture skyBoxTextures[] = { 
				new GLTexture(context, R.drawable.skybox0),
				new GLTexture(context, R.drawable.skybox1),
				new GLTexture(context, R.drawable.skybox2),
				new GLTexture(context, R.drawable.skybox3),
				new GLTexture(context, R.drawable.skybox4),
				new GLTexture(context, R.drawable.skybox5) };
		
		_SkyBoxTextures = skyBoxTextures;
		
		GLTexture Walltex[] = { 
				  new GLTexture(context,R.drawable.gltron_wall_1),
			      new GLTexture(context,R.drawable.gltron_wall_2),
		    	  new GLTexture(context,R.drawable.gltron_wall_3),
				  new GLTexture(context,R.drawable.gltron_wall_4) };
		
		_Walls = Walltex;

		_Floor = new GLTexture(context,R.drawable.gltron_floor);
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
	
	public void drawWalls()
	{
		
		GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GLES10.glEnable(GLES10.GL_CULL_FACE);
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		GLES10.glEnable(GLES10.GL_BLEND);
		
		for(int Walls=0; Walls<4; Walls++) {
			
			GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, _Walls[Walls].getTextureID());
			GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
			GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, _WallVertexBuffer[Walls]);
			GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, _TexBuffer);
			//Draw the vertices as triangles, based on the Index Buffer information
			GLES10.glDrawElements(GLES10.GL_TRIANGLES, _NumOfIndices, GLES10.GL_UNSIGNED_BYTE, _IndicesBuffer);
			//Disable the client state before leaving
			GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
			GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		}
		
		GLES10.glDisable(GLES10.GL_CULL_FACE);
		GLES10.glDisable(GLES10.GL_TEXTURE_2D);
		GLES10.glDisable(GLES10.GL_BLEND);
		
	}
	
	public void drawFloorTextured()
	{
		int i,j,l;
		
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, _Floor.getTextureID());
		
		GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
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
				
				GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
				GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
				//GLES10.glFrontFace(GL10.GL_CCW);

				GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, VertexBuffer);
				GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, _FloorTexBuffer);
				
				//Draw the vertices as triangles, based on the Index Buffer information
				GLES10.glDrawElements(GLES10.GL_TRIANGLES, _NumOfIndices, GLES10.GL_UNSIGNED_BYTE, _IndicesBuffer);
				
				//Disable the client state before leaving
				GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
				GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			}
		}
	}
	
	public void drawSkyBox()
	{
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		GLES10.glDepthMask(false);
		GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		for(int i=0; i<6; i++)
		{
			GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, _SkyBoxTextures[i].getTextureID());
			
			GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
			GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			//GLES10.glFrontFace(GL10.GL_CCW);

			GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, _SkyBoxVertexBuffers[i]);
			GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, _TexBuffer);
			
			GLES10.glDrawElements(GLES10.GL_TRIANGLES, _NumOfIndices, GLES10.GL_UNSIGNED_BYTE, _IndicesBuffer);

			GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
			GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

		}
		GLES10.glDisable(GLES10.GL_TEXTURE_2D);
		GLES10.glDepthMask(true);

	}
	
	
}
