/*
 * Copyright © 2012 Iain Churcher
 *
 * Based on GLtron by Andreas Umbach (www.gltron.org)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version; provided that the above copyright notice appear 
 * in all copies and that both that copyright notice and this permission 
 * notice appear in supporting documentation
 * 
 * http://www.gnu.org/licenses/old-licenses/gpl-1.0.html
 * 
 * THE COPYRIGHT HOLDERS DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY SPECIAL, INDIRECT OR
 * CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE,
 * DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 * TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
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
