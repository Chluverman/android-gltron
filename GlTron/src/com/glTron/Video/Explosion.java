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

public class Explosion {

	private float Radius;
	
	private final float IMPACT_RADIUS_DELTA = 0.025f;
	private final float IMPACT_MAX_RADIUS = 25.0f;
	
	// Shockwave behaviour constants 
	private final float SHOCKWAVE_MIN_RADIUS  = 0.0f;
	private final float SHOCKWAVE_MAX_RADIUS = 45.0f;
	private final float SHOCKWAVE_WIDTH = 0.2f;
	private final float SHOCKWAVE_SPACING = 6.0f;
	private final float SHOCKWAVE_SPEED = 1.2f; // relative to impact radius delta
	private final int SHOCKWAVE_SEGMENTS = 25;
	private final int NUM_SHOCKWAVES = 3;

	// Glow contants
	private final float GLOW_START_OPACITY = 1.2f;
	private final float GLOW_INTENSITY = 1.0f;
	
	// Spire contants
	private final float SPIRE_WIDTH = 0.40f;
	private final int NUM_SPIRES = 21;
	
	private GLTexture ExplodeTex;
	
	public Explosion(float radius)
	{
		Radius = radius;
	}
	
	public float getRadius()
	{
		return Radius;
	}
	
	public boolean runExplode()
	{
		boolean retVal = true;
		if(Radius > IMPACT_MAX_RADIUS)
		{
			retVal = false;
		}
		return retVal;
	}
	
	public void Draw(GL10 gl, long GameDeltaTime, GLTexture tex)
	{
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glPushMatrix();
		gl.glRotatef(90,90,0,1);
		gl.glTranslatef(0.0f, -0.5f, -0.5f);
		gl.glColor4f(0.68f, 0.0f, 0.0f, 1.0f);
		
		ExplodeTex = tex;
		
		drawShockwaves(gl);
		
		if(Radius < IMPACT_MAX_RADIUS)
		{
			drawImpactGlow(gl);
			drawSpires(gl);
		}
		
		Radius += (GameDeltaTime * IMPACT_RADIUS_DELTA);
		
		gl.glPopMatrix();
		gl.glEnable(GL10.GL_LIGHTING);

	}
	
	private void drawSpires(GL10 gl)
	{
		int i;
		
		Vec zunit = new Vec(0.0f, 0.0f, 1.0f);
		Vec right,left;
		
		Vec vectors[] = {
				 new Vec(1.00f, 0.20f, 0.00f),
				 new Vec(0.80f, 0.25f, 0.00f),
				 new Vec(0.90f, 0.50f, 0.00f),
				 new Vec(0.70f, 0.50f, 0.00f),
				 new Vec(0.52f, 0.45f, 0.00f),
				 new Vec(0.65f, 0.75f, 0.00f),
				 new Vec(0.42f, 0.68f, 0.00f),
				 new Vec(0.40f, 1.02f, 0.00f),
				 new Vec(0.20f, 0.90f, 0.00f),
				 new Vec(0.08f, 0.65f, 0.00f),
				 new Vec(0.00f, 1.00f, 0.00f),
				 new Vec(-0.08f, 0.65f, 0.00f),
				 new Vec(-0.20f, 0.90f, 0.00f),
				 new Vec(-0.40f, 1.02f, 0.00f),
				 new Vec(-0.42f, 0.68f, 0.00f),
				 new Vec(-0.65f, 0.75f, 0.00f),
				 new Vec(-0.52f, 0.45f, 0.00f),
				 new Vec(-0.70f, 0.50f, 0.00f),
				 new Vec(-0.90f, 0.50f, 0.00f),
				 new Vec(-0.80f, 0.30f, 0.00f),
				 new Vec(-1.00f, 0.20f, 0.00f)
		};
		
		float TriList[] = new float[3*3];
		FloatBuffer SpireBuffer;
		
		
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE);
		
		for(i=0; i < NUM_SPIRES;  i++)
		{
			right = vectors[i].Cross(zunit);
			right.Normalise();
			right.Mul(SPIRE_WIDTH);
			
			left = zunit.Cross(vectors[i]);
			left.Normalise();
			left.Mul(SPIRE_WIDTH);
			
			TriList[0] = right.v[0];
			TriList[1] = right.v[1];
			TriList[2] = right.v[2];
			TriList[3] = (Radius * vectors[i].v[0]);
			TriList[4] = (Radius * vectors[i].v[1]);
			TriList[5] = 0.0f;
			TriList[6] = left.v[0];
			TriList[7] = left.v[1];
			TriList[8] = left.v[2];
			
			SpireBuffer = GraphicUtils.ConvToFloatBuffer(TriList);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, SpireBuffer);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 3);
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
	}
	
	private void drawImpactGlow(GL10 gl)
	{
		float opacity;
		float ImpactVertex[] = {
				-1.0f, -1.0f, 0.0f,
				1.0f, -1.0f, 0.0f,
				1.0f, 1.0f, 0.0f,
				1.0f, 1.0f, 0.0f,
				-1.0f, 1.0f, 0.0f,
				-1.0f, -1.0f,0.0f
		};
		float TexVertex[] = {
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f,1.0f,
				1.0f,1.0f,
				0.0f, 1.0f,
				0.0f, 0.0f
		};
		FloatBuffer ImpactBuffer;
		FloatBuffer TexBuffer;
		
		opacity = GLOW_START_OPACITY - (Radius / IMPACT_MAX_RADIUS);
		
		gl.glPushMatrix();
		gl.glScalef(Radius, Radius, 1.0f);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, ExplodeTex.getTextureID());
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glColor4f(GLOW_INTENSITY, GLOW_INTENSITY, GLOW_INTENSITY, opacity);
		gl.glDepthMask(false);
		
		ImpactBuffer = GraphicUtils.ConvToFloatBuffer(ImpactVertex);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, ImpactBuffer);
		TexBuffer = GraphicUtils.ConvToFloatBuffer(TexVertex);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, TexBuffer);
		
		//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glDrawArrays(GL10.GL_TRIANGLES ,0, 6);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDepthMask(true);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glPopMatrix();
	}
	
	private void drawShockwaves(GL10 gl)
	{
		int waves;
		float radius = (Radius * SHOCKWAVE_SPEED);
		
		gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
		
		for(waves=0; waves<NUM_SHOCKWAVES; waves++)
		{
			if(radius > SHOCKWAVE_MIN_RADIUS && radius < SHOCKWAVE_MAX_RADIUS)
			{
				drawWave(gl,radius);
			}
			radius -= SHOCKWAVE_SPACING;
		}
	}
	
	private void drawWave(GL10 gl,float adj_radius)
	{
		int i,j,vertex;
		double angle;
		double delta_radius = SHOCKWAVE_WIDTH / SHOCKWAVE_SEGMENTS;
		double delta_angle = (180.0 / SHOCKWAVE_SEGMENTS) * (Math.PI / 180);
		double start_angle = (270.0 * (Math.PI / 180));
		int NumberOfIndices = (2*(SHOCKWAVE_SEGMENTS + 1));
		
		float WaveVertex[] = new float[(3*(2*(SHOCKWAVE_SEGMENTS + 1)))];
		FloatBuffer WaveBuffer;
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		for(i=0; i < SHOCKWAVE_SEGMENTS; i++)
		{
			angle = start_angle;
			vertex = 0;
			for(j=0; j <= SHOCKWAVE_SEGMENTS; j++)
			{
				WaveVertex[vertex] = (float)((adj_radius + delta_radius) * Math.sin(angle));
				vertex++;
				WaveVertex[vertex] = (float)((adj_radius + delta_radius) * Math.cos(angle));
				vertex++;
				WaveVertex[vertex] = 0.0f;
				vertex++;
				
				WaveVertex[vertex] = (float)(adj_radius * Math.sin(angle));
				vertex++;
				WaveVertex[vertex] = (float)(adj_radius * Math.cos(angle));
				vertex++;
				WaveVertex[vertex] = 0.0f;
				vertex++;
				
				angle += delta_angle;
			}
			
			WaveBuffer = GraphicUtils.ConvToFloatBuffer(WaveVertex);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, WaveBuffer);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, NumberOfIndices);
			adj_radius += delta_radius;
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
