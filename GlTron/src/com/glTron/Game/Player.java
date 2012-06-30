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

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import com.glTron.Sound.SoundManager;
import com.glTron.Video.*;

public class Player {


	private Model Cycle;
	private int Player_num;
	private int Direction;
	private int LastDirection;
	private Explosion Explode;
	
	private int Score;
	
	GLTexture _ExplodeTex;
	
	private Segment[] Trails = new Segment[1000] ;

	private HUD tronHUD; // Allow messages to be added to console
	
	
	private int trailOffset;
	private float trailHeight;
	
	private float Speed;
	public long TurnTime;
	public final float DIRS_X[] = {0.0f, -1.0f, 0.0f, 1.0f};
	public final float DIRS_Y[] = {-1.0f, 0.0f, 1.0f, 0.0f};
	private final float SPEED_OZ_FREQ = 1200.0f;
	private final float SPEED_OZ_FACTOR = 0.09f;
	
	private final float dirangles[] = { 0.0f, -90.0f, -180.0f, 90.0f, 180.0f, -270.0f };
	public final int TURN_LEFT = 3;
	public final int TURN_RIGHT = 1;
	public final int TURN_LENGTH = 200;
	public final float TRAIL_HEIGHT = 3.5f;
	private final float EXP_RADIUS_MAX = 30.0f;
	private final float EXP_RADIUS_DELTA = 0.01f;
	
	private TrailMesh Trailmesh;
	
	private float exp_radius;
	
	private final float START_POS[][] = {
			{ 0.5f, 0.25f},
			{0.75f, 0.5f},
			{0.5f, 0.4f},
			{0.25f, 0.5f},
			{0.25f, 0.25f},
			{0.65f, 0.35f}
	};

	private final float ColourDiffuse[][] = {
			{ 0.0f, 0.1f, 0.900f, 1.000f},      // Blue
			{ 1.00f, 0.550f, 0.140f, 1.000f},   // Yellow
			{ 0.750f, 0.020f, 0.020f, 1.000f},  // Red
			{ 0.800f, 0.800f, 0.800f, 1.000f},  // Grey
			{ 0.120f, 0.750f, 0.0f, 1.000f},    // Green
			{ 0.750f, 0.0f, 0.35f, 1.000f}      // Purple
	};

	private final float ColourSpecular[][] = {
			{ 0.0f, 0.1f, 0.900f, 1.000f},    // Blue
			{0.500f, 0.500f, 0.000f, 1.000f}, // Yellow
			{0.750f, 0.020f, 0.020f, 1.000f}, // Red
			{1.00f, 1.00f, 1.00f, 1.000f},    // Grey
			{0.050f, 0.500f, 0.00f, 1.00f},   // Green
			{0.500f, 0.000f, 0.500f, 1.00f},  // Purple
	};
	
	private final float ColourAlpha[][] = {
			{0.0f, 0.1f, 0.900f, 0.600f},      // Blue
			 {1.000f, 0.850f, 0.140f, 0.600f}, // Yellow
			 {0.750f, 0.020f, 0.020f, 0.600f}, // Red
			 {0.700f, 0.700f, 0.700f, 0.600f}, // Grey
			 {0.120f, 0.700f, 0.000f, 0.600f}, // Green
			 {0.720f, 0.000f, 0.300f, 0.600f}  // Purple
	};

	private static boolean ColourTaken[] = {false,false,false,false,false,false};
	private int mPlayerColourIndex;
	
//	private final int MAX_LOD_LEVEL = 3;
	private final int LOD_DIST[][] = {
			{ 1000, 1000, 1000 },
			{100, 200, 400},
			{30,100,200},
			{10,30,150}
	};
	
	public Player(int player_number, float gridSize, Model mesh, HUD hud)
	{
		int colour = 0;
		boolean done = false;
		
		Random rand = new Random();
		Direction = rand.nextInt(3); // accepts values 0..3;
		LastDirection = Direction;
		
		Trails[0] = new Segment();
		trailOffset = 0;
		Trails[trailOffset].vStart.v[0] = START_POS[player_number][0] * gridSize;
		Trails[trailOffset].vStart.v[1] = START_POS[player_number][1] * gridSize;
		Trails[trailOffset].vDirection.v[0] = 0.0f;
		Trails[trailOffset].vDirection.v[1] = 0.0f;
		
		trailHeight = TRAIL_HEIGHT;
	
		tronHUD = hud;
		
		Speed = 10.0f;
		exp_radius = 0.0f;
		
		Cycle = mesh;
		Player_num = player_number;
		Score = 0;
		
		// Select Colour
		if(player_number == GLTronGame.OWN_PLAYER)
		{
			// Re-init the colour taken array - must now create players sequentially for this to work
			for(colour = 0; colour < GLTronGame.MAX_PLAYERS; colour++)
			{
				ColourTaken[colour] = false;
			}

			ColourTaken[GLTronGame.mPrefs.PlayerColourIndex()] = true;
			mPlayerColourIndex = GLTronGame.mPrefs.PlayerColourIndex();
		}
		else
		{
			while(!done)
			{
			    if(!ColourTaken[colour])
			    {
			    	ColourTaken[colour] = true;
			    	mPlayerColourIndex = colour;
			    	done = true;
			    }
			    colour++;
			}
		}
		
	}
	

	public void doTurn(int direction, long current_time)
	{
		float x = getXpos();
		float y = getYpos();
		
		trailOffset++;
		Trails[trailOffset] = new Segment();
		Trails[trailOffset].vStart.v[0] = x;
		Trails[trailOffset].vStart.v[1] = y;
		Trails[trailOffset].vDirection.v[0] = 0.0f;
		Trails[trailOffset].vDirection.v[1] = 0.0f;
		
		LastDirection = Direction;
		Direction = (Direction + direction) % 4;
		TurnTime = current_time;
	}
	
	
	public void doMovement(long dt, long current_time, Segment walls[], Player plyers[])
	{
		float fs;
		float t;
		
		if(Speed > 0.0f) // Player is still alive
		{
			fs = (float) (1.0f - SPEED_OZ_FACTOR + SPEED_OZ_FACTOR *
				Math.cos(0.0f * (float)Math.PI / 4.0f + 
						   (current_time % SPEED_OZ_FREQ) *
						   2.0f * Math.PI / SPEED_OZ_FREQ));
			
			t = dt / 100.0f * Speed * fs;
			
			Trails[trailOffset].vDirection.v[0] += t * DIRS_X[Direction];
			Trails[trailOffset].vDirection.v[1] += t * DIRS_Y[Direction];
			
			doCrashTestWalls(walls);
			doCrashTestPlayer(plyers);
			
		}
		else
		{
			if(trailHeight > 0.0f)
			{
				trailHeight -= (dt * TRAIL_HEIGHT) / 1000.0f;
			}
			if(exp_radius < EXP_RADIUS_MAX)
			{
				exp_radius += (dt * EXP_RADIUS_DELTA);
			}
		}
	}
	
	public void drawCycle(GL10 gl, long curr_time, long time_dt, Lighting Lights, GLTexture ExplodeTex)
	{
		gl.glPushMatrix();
		gl.glTranslatef(getXpos(), getYpos(), 0.0f);

		doCycleRotation(gl,curr_time);

		//Lights.setupLights(gl, LightType.E_CYCLE_LIGHTS);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthMask(true);
		if(exp_radius == 0.0f)
		{
			gl.glEnable(GL10.GL_NORMALIZE);
			gl.glTranslatef(0.0f, 0.0f, Cycle.GetBBoxSize().v[2] / 2.0f);
			gl.glEnable(GL10.GL_CULL_FACE);
			//gl.glTranslatef((GridSize/2.0f), (GridSize/2.0f), 0.0f);
			//gl.glTranslatef(_Player._PlayerXpos, _Player._PlayerYpos, 0.0f);
			Cycle.Draw(gl,ColourSpecular[Player_num],ColourDiffuse[mPlayerColourIndex]);
			gl.glDisable(GL10.GL_CULL_FACE);
		}
		else if(exp_radius < EXP_RADIUS_MAX)
		{
			// Draw Crash if crashed..
			if(getExplode() != null)
			{
				if(getExplode().runExplode())
				{
					gl.glEnable(GL10.GL_BLEND);
	
					Explode.Draw(gl, time_dt, ExplodeTex);
				
					gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
					gl.glTranslatef(0.0f, 0.0f, Cycle.GetBBoxSize().v[2] / 2.0f);
					//LightBike.Explode(gl, _Player.getExplode().getRadius());
				}
			}
		}
		
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glPopMatrix();

	}
	
	public void doCrashTestWalls(Segment Walls[])
	{
		Segment Current = Trails[trailOffset];
		Vec V;
		
		for(int j=0; j < 4; j++)
		{
			V = Current.Intersect(Walls[j]);
			
			if(V != null)
			{
				if(Current.t1 >= 0.0f && Current.t1 < 1.0f && Current.t2 >= 0.0f && Current.t2 < 1.0f)
				{
					StringBuilder sb1 = new StringBuilder();
					
					Current.vDirection.v[0] = V.v[0] - Current.vStart.v[0];
					Current.vDirection.v[1] = V.v[1] - Current.vStart.v[1];
					Speed = 0.0f;
					Explode = new Explosion(0.0f);
					
					sb1.append("Player ");
					sb1.append(Player_num);
					sb1.append(" CRASH wall!");
					tronHUD.addLineToConsole(sb1.toString());
					
					if(GLTronGame.mPrefs.PlaySFX())
						SoundManager.playSound(GLTronGame.CRASH_SOUND, 1.0f);
					
					Log.e("GLTRON", "Wall CRASH");
					break;
				}
			}
		}
		
	}
	
	public void doCrashTestPlayer(Player players[])
	{
		int j,k;
		Segment Current  = Trails[trailOffset];
		Segment Wall;
		Vec V;

		for(j = 0; j < GLTronGame.mCurrentPlayers; j++)
		{
			
			if(players[j].getTrailHeight() < TRAIL_HEIGHT)
				continue;
			
			for(k =0; k < players[j].getTrailOffset()  + 1; k++)
			{
				if(players[j] == this && k >= trailOffset - 1)
					break;
				
				Wall = players[j].getTrail(k);
				
				V = Current.Intersect(Wall);
				
				if(V != null)
				{
					if(Current.t1 >= 0.0f && Current.t1 < 1.0f && Current.t2 >= 0.0f && Current.t2 < 1.0f)
					{
						StringBuilder sb1 = new StringBuilder();

						Current.vDirection.v[0] = V.v[0] - Current.vStart.v[0];
						Current.vDirection.v[1] = V.v[1] - Current.vStart.v[1];
						Speed = 0.0f;
						Explode = new Explosion(0.0f);

						sb1.append("Player ");
						sb1.append(Player_num);
						sb1.append(" CRASH trail!");
						tronHUD.addLineToConsole(sb1.toString());
						
						players[j].addScore(10);
						
						if(GLTronGame.mPrefs.PlaySFX())
							SoundManager.playSound(GLTronGame.CRASH_SOUND, 1.0f);
						
						Log.e("GLTRON", "Wall CRASH");
						break;
					}
				}
			}
		}
	}

	private void doCycleRotation(GL10 gl, long CurrentTime)
	{
		  long time = CurrentTime - TurnTime;
		  float dirAngle;
		  float axis = 1.0f;
		  float Angle;
		  
		  dirAngle = getDirAngle(time);
		  
		  gl.glRotatef(dirAngle, 0.0f, 0.0f, 1.0f);
		
		  if((time < TURN_LENGTH) && (LastDirection != Direction))
		  {
			  if( (Direction < LastDirection) && (LastDirection != 3))
			  {
				  axis = -1.0f;
			  }
			  else if( ((LastDirection == 3) && (Direction == 2)) ||
					          ((LastDirection == 0) && (Direction == 3)) )
			  {
				  axis = -1.0f;
			  }
			  Angle = (float)Math.sin((Math.PI * time / TURN_LENGTH)) * 25.0f;
			  gl.glRotatef(Angle, 0.0f, (axis * -1.0f), 0.0f);
		  }
	}

	public void drawTrails(Trails_Renderer render, Camera cam)
	{
		if(trailHeight > 0.0f)
		{
			Trailmesh = new TrailMesh(this);
			render.Render(Trailmesh);
			Trailmesh = null;
			render.drawTrailLines(Trails,trailOffset,trailHeight,cam);
		}
	}
	
	public boolean isVisible(Camera cam)
	{
		Vec v1;
		Vec v2;
		Vec tmp = new Vec(getXpos(),getYpos(),0.0f);
		int lod_level = 2;
		float d,s;
		int i;
		int LC_LOD = 3;
		float fov = 120;
		
		boolean retValue;
		
		v1 = cam._target.Sub(cam._cam);
		v1.Normalise();
		
		v2 = cam._cam.Sub(tmp);
		
		d = v2.Length();
		
		for(i=0;i<LC_LOD && d >= LOD_DIST[lod_level][i]; i++);
		
		if(i >= LC_LOD)
		{
			retValue = false;
		}
		else
		{
			v2 = tmp.Sub(cam._cam);
			v2.Normalise();
			
			s = v1.Dot(v2);
			d = (float)Math.cos((fov/2) * 2 * Math.PI / 360.0f);
			
			if(s < d - (Cycle.GetBBoxRadius() * 2.0f))
			{
				retValue = false;
			}
			else
			{
				retValue = true;
			}
			
		}
		
		return retValue;
	}
	
	private float getDirAngle(long time)
	{
		int last_dir;
		float dir_angle;
		
		if(time < TURN_LENGTH)
		{
			last_dir = LastDirection;
			if(Direction == 3 && last_dir ==2)
			{
				last_dir = 4;
			}
			if(Direction == 2 && last_dir == 3)
			{
				last_dir = 5;
			}
			dir_angle = ((TURN_LENGTH - time) * dirangles[last_dir] + 
					time * dirangles[Direction]) / TURN_LENGTH;
		}
		else
		{
			dir_angle = dirangles[Direction];
		}
		return dir_angle;
	}
	
	public Explosion getExplode()
	{
		return Explode;
	}
	
	public Segment getTrail(int offset)
	{
		return Trails[offset];
	}
	
	public Segment[] getTrails()
	{
		return Trails;
	}
	
	public float getTrailHeight()
	{
		return trailHeight;
	}
	
	public int getTrailOffset()
	{
		return trailOffset;
	}
	
	public void setExplodeTex(GLTexture tex)
	{
		_ExplodeTex = tex;
	}
	
	public float getXpos()
	{
		return Trails[trailOffset].vStart.v[0] + Trails[trailOffset].vDirection.v[0];
	}
	
	public float getYpos()
	{
		return Trails[trailOffset].vStart.v[1] + Trails[trailOffset].vDirection.v[1];
	}

	public int getDirection()
	{
		return Direction;
	}
	
	public int getLastDirection()
	{
		return LastDirection;
	}

	public float getSpeed()
	{
		return Speed;
	}
	
	public void setSpeed(float sp)
	{
		Speed = sp;
	}
	
	public float[] getColorAlpha()
	{
		return ColourAlpha[mPlayerColourIndex];
	}
	
	public float[] getColorDiffuse()
	{
		return ColourDiffuse[mPlayerColourIndex];
	}
	
	public void addScore(int val)
	{
		if(Speed > 0.0f)
			Score += val;
	}
	
	public int getScore()
	{
		return Score;
	}

}
