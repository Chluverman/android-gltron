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
package com.glTron.Game;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

import com.glTron.Video.GraphicUtils;
import com.glTron.Video.Model;
import com.glTron.Video.Vec;

public class Recognizer {

	/*
	 * Private items
	 */
	private float mAlpha;
	private float mGridSize;
	private FloatBuffer mColour;
	private FloatBuffer mShadow;
	
	/*
	 * Constants
	 */
	private final float xv[] = {0.5f, 0.3245f, 0.6f, 0.5f, 0.68f, -0.3f};
	private final float yv[] = {0.8f, 1.0f, 0.0f, 0.2f, 0.2f, 0.0f};
	//private final float colour[] = {0.05f, 0.14f, 0.05f, 0.50f};
	private final float colour[] = {0.6f, 0.16f, 0.2f, 0.50f};
	
	private final float ShadowMatrix[] = {
			4.0f, 0.0f, 0.0f, 0.0f,
			0.0f, 4.0f, 0.0f, 0.0f,
			-2.0f, -2.0f, 0.0f, 0.0f,
			0.0f, 0.0f, 0.0f, 4.0f 
	};
	
	private final float scaleFactor = 0.25f;
	private final float HEIGHT = 40.0f;
	
	public Recognizer(float gridSize)
	{
		mAlpha = 0.0f;
		mGridSize = gridSize;
		mColour = GraphicUtils.ConvToFloatBuffer(colour);
		mShadow = GraphicUtils.ConvToFloatBuffer(ShadowMatrix);
	}
	
	public void doMovement(long dt)
	{
		mAlpha += dt / 2000.0f;
	}
	
	public void reset()
	{
		mAlpha = 0.0f;
	}
	
	public void draw(GL10 gl, Model mesh)
	{
		Vec p,v;
		float dirx;
		
		gl.glPushMatrix();
		
		p = getPosition(mesh);
		v = getVelocity();
		
		dirx = getAngle(v);
		
		gl.glTranslatef(p.v[0], p.v[1], HEIGHT);
		gl.glRotatef(dirx, 0.0f, 0.0f, 1.0f);
		
		gl.glScalef(scaleFactor, scaleFactor, scaleFactor);
		
		gl.glDisable(GL10.GL_LIGHT0);
		gl.glDisable(GL10.GL_LIGHT1);
		gl.glLightfv(GL10.GL_LIGHT2, GL10.GL_SPECULAR, mColour);
		gl.glEnable(GL10.GL_LIGHT2);
		
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_CULL_FACE);
		
		gl.glEnable(GL10.GL_LIGHTING);
		
		gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(1.0f, 1.0f);
		
		gl.glEnable(GL10.GL_NORMALIZE);
		gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
		
		mesh.Draw(gl);
		
		gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL);
		gl.glDisable(GL10.GL_LIGHT2);
		gl.glEnable(GL10.GL_LIGHT1);
		gl.glDisable(GL10.GL_LIGHTING);
		
		// TODO:
		// Original glTron used to render another model in wireframe mode over the existing recognizer
		// OpenGL ES does not support this wireframe rendering mode. Need to come up with a replacement
		
		gl.glDisable(GL10.GL_CULL_FACE);
		
		gl.glPopMatrix();
		
		// Draw the shadow
		gl.glEnable(GL10.GL_STENCIL_TEST);
		gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);
		gl.glStencilFunc(GL10.GL_GREATER, 1, 1);
		gl.glEnable(GL10.GL_BLEND);
		gl.glColor4f(0.0f,0.0f,0.0f,0.8f);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glPushMatrix();
		gl.glMultMatrixf(mShadow);
		gl.glTranslatef(p.v[0], p.v[1], HEIGHT);
		gl.glRotatef(dirx, 0.0f, 0.0f, 1.0f);
		gl.glScalef(scaleFactor,scaleFactor,scaleFactor);
		gl.glEnable(GL10.GL_NORMALIZE);
		mesh.Draw(gl);
		gl.glDisable(GL10.GL_STENCIL_TEST);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glPopMatrix();
		
	}
	
	/*
	 * Private methods
	 */
	private float getAngle(Vec velocity)
	{
		float dxval = velocity.v[0];
		float dyval = velocity.v[0];
		
		float phi = (float)Math.acos(dxval / FloatMath.sqrt(dxval * dxval + dyval * dyval));
		
		if(dyval < 0.0f)
			phi = (float)(2.0f * Math.PI - phi);
		
		return (float)((phi + Math.PI / 2.0f) * 180.0f / Math.PI);
	}
	
	private Vec getPosition(Model mesh)
	{
		float x,y;
		float max = mesh.GetBBoxSize().v[0] * scaleFactor;
		float boundary = mGridSize - max;
		Vec pos;
		
		x = (max + (getx() + 1.0f) * boundary) / 2.0f;
		y = (max + (gety() + 1.0f) * boundary) / 2.0f;
		
		pos = new Vec(x,y,0.0f);
		
		return pos;
	}

	private Vec getVelocity()
	{
		Vec vel = new Vec(getdx() * mGridSize / 100.0f, getdy() * mGridSize / 100.0f,0.0f);
		return vel;
	}
	
	private float getx()
	{
		return (xv[0] * FloatMath.sin(xv[1] * mAlpha + xv[2]) - xv[3] * FloatMath.sin(xv[4] * mAlpha + xv[5]));
	}
	
	private float gety()
	{
		return (yv[0] * FloatMath.cos(yv[1] * mAlpha + yv[2] - yv[3] * FloatMath.sin(yv[4] * mAlpha + yv[5])));
	}
	
	private float getdx()
	{
		return (xv[1] * xv[0] * FloatMath.cos(xv[1] * mAlpha + xv[2]) - xv[4] * xv[3] * FloatMath.cos(xv[4] * mAlpha + xv[5]));
	}
	
	private float getdy()
	{
		return -(yv[1] * yv[0] * FloatMath.sin(yv[1] * mAlpha + yv[2]) - yv[4] * yv[3] * FloatMath.sin(yv[4] * mAlpha + yv[5]));
	}
	
}
