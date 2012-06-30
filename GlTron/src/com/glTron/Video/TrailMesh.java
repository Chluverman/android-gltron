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

import com.glTron.Game.Player;

public class TrailMesh {

	float Vertices[];
	float Normals[];
	float TexCoords[];
	short Indices[];
	byte Colors[];
	int iSize;
	int iUsed;
	int piOffset;
	int pvOffset;
	
	Player playerData;
	
	private enum MeshColourType {
		E_COLOUR_TRAIL,
		E_COLOUR_BRIGHT,
		E_COLOUR_CYCLE
	}
	
	private final float DECAL_WIDTH = 20.0f;
	
	private 	Vec normals[] = {
			new Vec(1.0f, 0.0f, 0.0f),
			new Vec(-1.0f, 0.0f, 0.0f),
			new Vec(0.0f, 1.0f, 0.0f),
			new Vec(0.0f,-1.0f,0.0f)
	};

	private final float DIRS_X[] = {0.0f, -1.0f, 0.0f, 1.0f};
	private final float DIRS_Y[] = {-1.0f, 0.0f, 1.0f, 0.0f};
	
	private final float BOW_LENGTH = 6.0f;
	private final float BOW_DIST2 = 0.85f;
	private final float BOW_DIST3 = 2.0f;
	private final float BOW_DIST1 = 0.4f;
	private final float dists[] = { BOW_DIST2, BOW_DIST3, BOW_DIST1, 0.0f };
	
	String Debug;
	StringBuffer sb = new StringBuffer(40);
	
	public TrailMesh(Player player)
	{
		int TrailOffset = player.getTrailOffset();

		playerData = player;
		
		int vmeshSize = ((TrailOffset * 4) + 8) + ((10 * 2) + 2);
		int imeshSize = ((TrailOffset * 6) + (2 * 6)) + ((10*6) + 6);

		 Vertices = new float[3 * vmeshSize];
		 Normals = new float[3 * vmeshSize];
		TexCoords = new float[2 * vmeshSize];
		Indices = new short[imeshSize];
		Colors = new byte[4 * vmeshSize];
		
		trailGeometry();
		bowGeometry();
	}
	
	private void bowGeometry()
	{
		Segment s = new Segment();
		int bdist = 2;
		int i;
		float trail_height = playerData.getTrailHeight();
		float t, fTop, fFloor;
		int iOffset = piOffset;
		
		s.vStart.v[0] = getSegmentEndX(0) ;
		s.vStart.v[1] = getSegmentEndY(0) ;
		s.vDirection.v[0] = getSegmentEndX(bdist) - s.vStart.v[0];
		s.vDirection.v[1] = getSegmentEndY(bdist) - s.vStart.v[1];
		
		for(i = 0; i < 10; i++)
		{
			t = i * 1.0f / 10.0f;
			fTop =(float) Math.sqrt(1.0f - t * t);
			fFloor = (t < 0.6f) ? 0.0f : 0.5f * (t - 0.6f);
			
			if(fTop < 0.3f) fTop = 0.3f;
			
			storeVertex(pvOffset,s,t,(fFloor * trail_height), (fTop * trail_height), DECAL_WIDTH, 0.0f);
			storeColor(pvOffset, MeshColourType.E_COLOUR_BRIGHT);
			pvOffset += 2;
			
			if(i > 0)
			{
				storeIndices(iOffset,pvOffset - 4);
				iOffset += 6;
			}
			
		}
		storeVertex(pvOffset,s,1.0f,(0.2f * trail_height),( 0.3f * trail_height),DECAL_WIDTH,0.0f);
		storeColor(pvOffset,MeshColourType.E_COLOUR_CYCLE);
		pvOffset += 2;
		storeIndices(iOffset,pvOffset - 4);
		iUsed += iOffset -  piOffset;
		piOffset = iOffset;
	}
	
	private void trailGeometry()
	{
		int i;
		int curVertex = 0;
		int curIndex = 0;
		int TrailOffset = playerData.getTrailOffset();
		Segment segs[] = playerData.getTrails();
		float trail_height = playerData.getTrailHeight();
		float fTotalLength = 0.0f;
		float fsegLength;
		Segment s = new Segment();
		
		for(i=0; i < TrailOffset; i++)
		{
			fsegLength = segs[i].Length();
			if(i ==0 || cmpdir(segs[i-1],segs[i]) )
			{
				storeVertex(curVertex,segs[i],0.0f,0.0f,trail_height,fsegLength,fTotalLength);
				storeColor(curVertex,MeshColourType.E_COLOUR_TRAIL);
				curVertex += 2;
			}
			
			storeVertex(curVertex,segs[i],1.0f,0.0f,trail_height,fsegLength,fTotalLength);
			storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
			curVertex += 2;
			
			storeIndices(curIndex, curVertex - 4);
			curIndex += 6;
			
			fTotalLength += fsegLength;
		}
		
		s.vStart.v[0] = segs[TrailOffset].vStart.v[0];
		s.vStart.v[1] = segs[TrailOffset].vStart.v[1];
		s.vDirection.v[0] = getSegmentEndX(1) - s.vStart.v[0];
		s.vDirection.v[1] = getSegmentEndY(1) - s.vStart.v[1];
		
		fsegLength = s.Length();
		
		storeVertex(curVertex,s,0.0f,0.0f,trail_height,fsegLength,fTotalLength);
		storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
		curVertex += 2;
		
		storeVertex(curVertex,s,1.0f,0.0f,trail_height,fsegLength,fTotalLength);
		storeColor(curVertex,MeshColourType.E_COLOUR_TRAIL );
		curVertex += 2;
		
		storeIndices(curIndex,curVertex - 4);
		curIndex += 6;
		
		fTotalLength += fsegLength;
		
		s.vStart.v[0] += s.vDirection.v[0];
		s.vStart.v[1] += s.vDirection.v[1];
		s.vDirection.v[0] = getSegmentEndX(0) - s.vStart.v[0];
		s.vDirection.v[1] = getSegmentEndY(0) - s.vStart.v[1];
		fsegLength = s.Length();
		
		storeVertex(curVertex,s,0.0f,0.0f,trail_height,fsegLength,fTotalLength);
		storeColor(curVertex, MeshColourType.E_COLOUR_TRAIL);
		curVertex += 2;
		
		storeVertex(curVertex,s,1.0f,0.0f,trail_height,fsegLength,fTotalLength);
		storeColor(curVertex, MeshColourType.E_COLOUR_BRIGHT);
		curVertex += 2;
		
		storeIndices(curIndex,curVertex - 4);
		curIndex += 6;
		
		iUsed = curIndex;
		piOffset = curIndex;
		pvOffset = curVertex;

	}
	
	private void storeColor(int offset, MeshColourType colourType)
	{
		int colOffset = offset * 4;
		float White[] = {1.0f,1.0f,1.0f,1.0f};
		float color[] = {0.0f, 0.0f, 0.0f, 0.0f};
		
		switch(colourType)
		{
			case E_COLOUR_TRAIL:
				color = playerData.getColorAlpha();
				break;
			case E_COLOUR_BRIGHT:
				color = White;
				break;
			case E_COLOUR_CYCLE:
				color = playerData.getColorDiffuse();
				break;
		}
		
		Colors[colOffset++] = (byte)(color[0] * 255.0f);
		Colors[colOffset++] = (byte)(color[1] * 255.0f);
		Colors[colOffset++] =(byte)( color[2] * 255.0f);
		Colors[colOffset++] = (byte)(color[3] * 255.0f);
		
		
		Colors[colOffset++] = (byte)(color[0] * 255.0f);
		Colors[colOffset++] = (byte)(color[1] * 255.0f);
		Colors[colOffset++] =(byte)( color[2] * 255.0f);
		Colors[colOffset++] = (byte)(color[3] * 255.0f);
		
	}
	
	private float getSegmentEndY( int dist)
	{
		float tlength,blength;
		float retVal;
		int dir = playerData.getDirection();
		int TrailOffset = playerData.getTrailOffset();
		Segment segs[] = playerData.getTrails();
		
		if(DIRS_Y[dir] == 0)
		{
			retVal = segs[TrailOffset].vStart.v[1] + segs[TrailOffset].vDirection.v[1];
		}
		else
		{
			tlength = segs[TrailOffset].Length();
			blength = (tlength < 2 * BOW_LENGTH) ? tlength / 2 : BOW_LENGTH;
			
			retVal = (segs[TrailOffset].vStart.v[1] + segs[TrailOffset].vDirection.v[1] - 
					dists[dist] * blength * DIRS_Y[dir]);
		}
		
		return retVal;
	}
	
	private float getSegmentEndX(int dist)
	{
		float tlength, blength;
		float retVal;
		int dir = playerData.getDirection();
		int TrailOffset = playerData.getTrailOffset();
		Segment segs[] = playerData.getTrails();
		
		if(DIRS_X[dir] == 0)
		{
			retVal = segs[TrailOffset].vStart.v[0] + segs[TrailOffset].vDirection.v[0];
		}
		else
		{
			tlength = segs[TrailOffset].Length();
			blength = (tlength < 2 * BOW_LENGTH) ? tlength /2 : BOW_LENGTH;
			
			retVal = (segs[TrailOffset].vStart.v[0] + segs[TrailOffset].vDirection.v[0] -
					dists[dist] * blength * DIRS_X[dir]);
		}
		return retVal;
	}
	
	private boolean cmpdir(Segment s1, Segment s2)
	{
		boolean returnval = true;
		if( (s1.vDirection.v[0] == 0.0f && s2.vDirection.v[0] == 0.0f) ||
			  (s1.vDirection.v[1] == 0.0f && s2.vDirection.v[1] == 0.0f) )
		{
			returnval = false;
		}
		return returnval;
	}
	
	private void storeIndices(int indexOffset, int vertexOffset)
	{
		short ppBase[][] = {
				{ 0, 2, 1, 2, 3, 1 },
				{ 0, 1, 2, 1, 3, 2}
		};
		int i;
		int winding = 0;
		
		if(Vertices[vertexOffset * 3] == Vertices[(vertexOffset + 2) * 3])
		{
			
				winding = (Vertices[(vertexOffset * 3) + 1] <= Vertices[((vertexOffset + 2) * 3) + 1]) ? 0 : 1; 
		}
		else
		{
				winding = (Vertices[vertexOffset * 3] < Vertices[(vertexOffset + 2) * 3]) ? 1 : 0;;
		}
		
		for(i = 0; i < 6; i++)
			Indices[indexOffset + i] =(short)(ppBase[winding][i] + vertexOffset);
	}
	
	private void storeVertex(
			int offset, Segment s, float t, float fFloor, float fTop, 
			float fSegLength, float fTotalLength)
	{
		
		Vec v;
		int texOffset = 2 * offset;
		
		int iNormal;
		float fUStart;
		
		if(s.vDirection.v[0] == 0.0f)
			iNormal = 0;
		else
			iNormal = 2;
		
		fUStart = (fTotalLength / DECAL_WIDTH) - (float)Math.floor(fTotalLength / DECAL_WIDTH);
		
		v = new Vec(
				 (s.vStart.v[0] + t * s.vDirection.v[0]),
				 ( s.vStart.v[1] + t * s.vDirection.v[1]),
				 fFloor);
		
//		sb=null;
//		sb = new StringBuffer(40);
//		Debug = sb.append("Offset = ").append(offset).append("/").append(vmeshSize).toString();
//		Log.e("GLTRON", Debug);
		
		TexCoords[texOffset] = fUStart + t * fSegLength / DECAL_WIDTH;
		TexCoords[texOffset + 1] = 0.0f;
		
		Vertices[offset * 3] = v.v[0];
		Vertices[(offset * 3) + 1] = v.v[1];
		Vertices[(offset * 3) + 2] = v.v[2];
		
		Normals[offset * 3] = normals[iNormal].v[0];
		Normals[(offset * 3) + 1] = normals[iNormal].v[1];
		Normals[(offset * 3) + 2] = normals[iNormal].v[2];
		
		texOffset += 2;
		v.v[2] = fTop;
		TexCoords[texOffset] =  fUStart + t * fSegLength / DECAL_WIDTH;
		TexCoords[texOffset + 1] = 1.0f;
		Vertices[(offset + 1) * 3] = v.v[0];
		Vertices[((offset + 1) * 3) + 1] = v.v[1];
		Vertices[((offset + 1) * 3) + 2] = v.v[2];
		
		Normals[(offset + 1) * 3] = normals[iNormal].v[0];
		Normals[((offset + 1) *3) + 1] = normals[iNormal].v[1];
		Normals[((offset + 1) *3) + 2] = normals[iNormal].v[2];
	}
	
}
