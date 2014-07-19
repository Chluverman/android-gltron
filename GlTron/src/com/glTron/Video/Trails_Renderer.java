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
import java.nio.ShortBuffer;

import android.opengl.GLES10;

import com.glTron.R;
import com.glTron.Game.*;

import android.content.Context;

public class Trails_Renderer {

	GLTexture Trails;
	
	private final float LX = 2.0f;
	private final float LY = 2.0f;
	
	private final float shadow_matrix[] = 
		{
			LX * LY, 0.0f, 0.0f, 0.0f,
			0.0f, LX * LY, 0.0f, 0.0f,
			-LY, -LX, 0.0f, 0.0f,
			0.0f, 0.0f, 0.0f, LX * LY
		};
	
	FloatBuffer shadowFb;
	
	public Trails_Renderer(Context context)
	{
		Trails = new GLTexture(context,R.drawable.gltron_traildecal,GLES10.GL_REPEAT, GLES10.GL_CLAMP_TO_EDGE);
		shadowFb = GraphicUtils.ConvToFloatBuffer(shadow_matrix);
	}
	
	public void Render(TrailMesh mesh)
	{
		int i;
		
		FloatBuffer vertexFb = GraphicUtils.ConvToFloatBuffer(mesh.Vertices);
		FloatBuffer normalFb = GraphicUtils.ConvToFloatBuffer(mesh.Normals);
		FloatBuffer texFb = GraphicUtils.ConvToFloatBuffer(mesh.TexCoords);
		ShortBuffer indBb = GraphicUtils.ConvToShortBuffer(mesh.Indices);
		ByteBuffer colorFb = GraphicUtils.ConvToByteBuffer(mesh.Colors);
		
		statesNormal();
		
		for(i = 0; i < 2; i++) // change to 2 to draw shadows
		{
			if( i == 0)
				statesNormal();
			else
				statesShadow();
			
			GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, texFb);
			GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexFb);
			GLES10.glNormalPointer(GLES10.GL_FLOAT,	0, normalFb);
			GLES10.glColorPointer(4, GLES10.GL_UNSIGNED_BYTE, 0, colorFb);
	
			GLES10.glDrawElements(GLES10.GL_TRIANGLES, mesh.iUsed, GLES10.GL_UNSIGNED_SHORT, indBb);
			
			GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
			GLES10.glDisableClientState(GLES10.GL_NORMAL_ARRAY);
			GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
			GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
			
			statesRestore();
		}

		GLES10.glTexEnvx(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_REPLACE);
		
	}
	
	private void statesShadow()
	{
//        GLES10.glDisable(GL10.GL_CULL_FACE);
//        GLES10.glDisable(GL10.GL_TEXTURE_2D);
//        GLES10.glDisable(GL10.GL_LIGHTING);

        GLES10.glEnable(GLES10.GL_STENCIL_TEST);
		GLES10.glStencilOp(GLES10.GL_REPLACE, GLES10.GL_REPLACE, GLES10.GL_REPLACE);
		GLES10.glStencilFunc(GLES10.GL_GREATER, 1, 1);
//		GLES10.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);

		GLES10.glPushMatrix();
		GLES10.glMultMatrixf(shadowFb);
		
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_NORMAL_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
		
        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glDisable(GLES10.GL_TEXTURE_2D);
        GLES10.glDisable(GLES10.GL_LIGHTING);
	}
	
	private void statesRestore()
	{
		GLES10.glDisable(GLES10.GL_COLOR_MATERIAL);
		GLES10.glCullFace(GLES10.GL_BACK);
		GLES10.glDisable(GLES10.GL_CULL_FACE);
		GLES10.glDisable(GLES10.GL_TEXTURE_2D);
		GLES10.glDisable(GLES10.GL_BLEND);
		GLES10.glEnable(GLES10.GL_LIGHTING);
		//GLES10.glPolygonMode(GL10.GL_FRONT_AND_BACK, GL10.GL_FILL);
		GLES10.glDisable(GLES10.GL_POLYGON_OFFSET_FILL);
		GLES10.glDisable(GLES10.GL_STENCIL_TEST);
		GLES10.glPopMatrix();
	}
	private void statesNormal()
	{
		GLES10.glEnable(GLES10.GL_POLYGON_OFFSET_FILL);
		GLES10.glPolygonOffset(1,1);
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_NORMAL_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);
		//GLES10.glFrontFace(GL10.GL_CCW);
		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
		
		GLES10.glDisable(GLES10.GL_CULL_FACE);
		GLES10.glShadeModel(GLES10.GL_SMOOTH);
		GLES10.glEnable(GLES10.GL_TEXTURE_2D);
		
		GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, Trails.getTextureID());
		
		GLES10.glTexEnvx(GLES10.GL_TEXTURE_ENV, GLES10.GL_TEXTURE_ENV_MODE, GLES10.GL_DECAL);
		
		float black[] = { 0.0f, 0.0f, 0.0f, 1.0f};
		FloatBuffer fBlack = GraphicUtils.ConvToFloatBuffer(black);
		
		GLES10.glMaterialfv(GLES10.GL_FRONT_AND_BACK,GLES10. GL_SPECULAR, fBlack);

		GLES10.glEnable(GLES10.GL_COLOR_MATERIAL);
		
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		
	}
	
	FloatBuffer trailtopfb;
	static final float trail_top[] = {
		1.0f, 1.0f, 1.0f, .7f,
		1.0f, 1.0f, 1.0f, .7f,
		1.0f, 1.0f, 1.0f, .7f};

	public void drawTrailLines(Segment segs[],int trail_offset, float trail_height, Camera cam)
	{
				
		int segOffset;

		if(trailtopfb == null)
			trailtopfb = GraphicUtils.ConvToFloatBuffer(trail_top);
		
		GLES10.glEnable(GLES10.GL_LINE_SMOOTH);
		GLES10.glEnable(GLES10.GL_BLEND);
		GLES10.glBlendFunc(GLES10.GL_SRC_ALPHA, GLES10.GL_ONE_MINUS_SRC_ALPHA);
		GLES10.glDisable(GLES10.GL_LIGHTING);
		
		GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
		GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
	
		GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, trailtopfb);

		for(segOffset = 0; segOffset <= trail_offset; segOffset++)
		{
			// Dont change alpha based on dist yet
			GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, trailtopfb);
			GLES10.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
			
			float tempvertex[] =
			{
				segs[segOffset].vStart.v[0], 
				segs[segOffset].vStart.v[1], 
				trail_height,
				
				segs[segOffset].vStart.v[0] + segs[segOffset].vDirection.v[0],
				segs[segOffset].vStart.v[1] + segs[segOffset].vDirection.v[1],
				trail_height
			};
			
			FloatBuffer fb = GraphicUtils.ConvToFloatBuffer(tempvertex);
			
			GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, fb);
			GLES10.glDrawArrays(GLES10.GL_LINES, 0, 2);
		}

		// TODO: Draw the final segment
//		GLES10.glColorPointer(4, GL10.GL_FLOAT, 0, trailtopfb);
//		
//		float tempvertex[] = 
//		{
//			segs[trail_offset].vStart.v[0],
//			segs[trail_offset].vStart.v[1],
//			trail_height
//		};
		
//		GLES10.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		GLES10.glDisable(GLES10.GL_BLEND);
		GLES10.glDisable(GLES10.GL_LINE_SMOOTH);
		GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
	}
	
}
