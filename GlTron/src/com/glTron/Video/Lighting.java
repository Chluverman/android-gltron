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

public class Lighting {

	FloatBuffer white_fb;
	FloatBuffer gray66_fb;
	FloatBuffer gray10_fb;
	FloatBuffer black_fb;
	FloatBuffer posWorld0_fb;
	FloatBuffer posWorld1_fb;
	FloatBuffer posCycles_fb;
	
	private static final float white[] = { 1.0f, 1.0f, 1.0f, 1.0f };
	private static final float gray66[] = { 0.66f, 0.66f, 0.66f, 1.0f };
	private static final float gray10[] = { 0.1f, 0.1f, 0.1f, 1.0f };
	private static final float black[] = { 0.0f, 0.0f, 0.0f, 1.0f };
	
	private static final float posWorld0[] = {1.0f, 0.8f, 0.0f, 0.0f};
	private static final float posWorld1[] = {-1.0f, -0.8f, 0.0f, 0.0f};
	private static final float posCycles[] = {0.0f, 0.0f, 0.0f, 1.0f};
	
	public enum LightType {
		E_WORLD_LIGHTS,
		E_CYCLE_LIGHTS,
		E_RECOGNISER_LIGHTS
	}
	
	public Lighting()
	{
		white_fb = GraphicUtils.ConvToFloatBuffer(white);
		gray66_fb = GraphicUtils.ConvToFloatBuffer(gray66);
		gray10_fb = GraphicUtils.ConvToFloatBuffer(gray10);
		black_fb = GraphicUtils.ConvToFloatBuffer(black);
		posWorld0_fb = GraphicUtils.ConvToFloatBuffer(posWorld0);
		posWorld1_fb = GraphicUtils.ConvToFloatBuffer(posWorld1);
		posCycles_fb = GraphicUtils.ConvToFloatBuffer(posCycles);
		
	}
	
	public void setupLights(LightType lightType)
	{
		// Turn off global ambient lighting
		GLES10.glLightModelfv(GLES10.GL_LIGHT_MODEL_AMBIENT, black_fb);
		GLES10.glLightModelf(GLES10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
		
		if(lightType == LightType.E_WORLD_LIGHTS) {
			GLES10.glEnable(GLES10.GL_LIGHTING);
			GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
			GLES10.glPushMatrix();
			
			GLES10.glEnable(GLES10.GL_LIGHT0);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_POSITION, posWorld0_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_AMBIENT, gray10_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_SPECULAR, white_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_DIFFUSE, white_fb);
			
			GLES10.glEnable(GLES10.GL_LIGHT1);
			GLES10.glLightfv(GLES10.GL_LIGHT1, GLES10.GL_POSITION, posWorld1_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT1, GLES10.GL_AMBIENT, black_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT1, GLES10.GL_SPECULAR, gray66_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT1, GLES10.GL_DIFFUSE, gray66_fb);
			GLES10.glPopMatrix();
			
		} else {
			GLES10.glEnable(GLES10.GL_LIGHTING);
			GLES10.glEnable(GLES10.GL_LIGHT0);
			
			GLES10.glMatrixMode(GLES10.GL_MODELVIEW);
			GLES10.glPushMatrix();
			GLES10.glLoadIdentity();
			
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_POSITION, posCycles_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_AMBIENT, gray10_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_SPECULAR, white_fb);
			GLES10.glLightfv(GLES10.GL_LIGHT0, GLES10.GL_DIFFUSE, white_fb);
			
			GLES10.glDisable(GLES10.GL_LIGHT1);
			
			GLES10.glPopMatrix();
		}
			
	}
	
}
