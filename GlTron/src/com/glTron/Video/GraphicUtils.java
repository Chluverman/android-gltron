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
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GraphicUtils {

	public class vec2 {
		public float v[] = new float[2];
	}
	
	public class vec3 {
		public float v[] = new float[3];
	}
	
	public class vec4 {
		public float v[] = new float[4];
	}

	
	public static vec2 vec2Add(vec2 Result, vec2 v1, vec2 v2) 
	{
		Result.v[0] = v1.v[0] + v2.v[0];
		Result.v[1] = v1.v[1] + v2.v[1];
		
		return Result;
	}
	
	public static FloatBuffer ConvToFloatBuffer(float buf[])
	{
		FloatBuffer ReturnBuffer;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(buf.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		ReturnBuffer = vbb.asFloatBuffer();
		ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		
		return ReturnBuffer;
	}
	
	public static ByteBuffer ConvToByteBuffer(byte buf[])
	{
		ByteBuffer ReturnBuffer = ByteBuffer.allocateDirect(buf.length);
		
		ReturnBuffer.order(ByteOrder.nativeOrder());
		ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		
		return ReturnBuffer;
	}
	
	public static ShortBuffer ConvToShortBuffer(short buf[])
	{
		ShortBuffer ReturnBuffer = ShortBuffer.allocate(buf.length);
		ReturnBuffer.put(buf);
		ReturnBuffer.position(0);
		return ReturnBuffer;
	}
	
}
