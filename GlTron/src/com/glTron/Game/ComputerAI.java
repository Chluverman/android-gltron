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

import com.glTron.Video.Segment;
import com.glTron.Video.Vec;

public class ComputerAI {

	private static final int E_FRONT = 0;
	private static final int E_LEFT = 1;
	private static final int E_RIGHT = 2;
	private static final int E_BACKLEFT = 3;
	private static final int E_MAX = 4;
	
	private static  float front, left, right, backleft;
	
	private static float distance;
	
	private static final float FLT_MAX = 10000.0f;
	
	private static final int TURN_TIME_LEVEL = 1;
	private static final int MIN_TURN_TIME[] = { 600, 400, 200, 100 };
	private static final float MAX_SEG_LENGTH[] = { 0.6f, 0.3f, 0.3f, 0.3f };
	private static final float CRITICAL[] = { 0.2f, 0.08037f, 0.08037f, 0.0837f };
	private static final int SPIRAL[] = { 10, 10, 10, 10 };
	private static final int RL_DELTA[] = { 0, 10, 20, 30 };
	
	private static Segment Walls[];
	private static Player Players[];
	
	private static long Current;
	private static float GridSize;
	
	private static int tdiff[] = {0,0,0,0,0,0};
	private static long aiTime[] = { 0,0,0,0,0,0};
	
	private static float SAVE_T_DIFF = 0.500f;
	private static float HOPELESS_T = 0.80f;
	
	
	private static int agressive_action[][] = {
			{ 2, 0, 2, 2 },
			{ 0, 1, 1, 2 },
			{ 0, 1, 1, 2 },
			{ 0, 1, 1, 2 },
			{ 0, 2, 2, 1 },
			{ 0, 2, 2, 1 },
			{ 0, 2, 2, 1 },
			{ 1, 1, 1, 0 }
		};

	private static int evasive_action[][] = {
			{ 1, 1, 2, 2 },
			{ 1, 1, 2, 0 },
			{ 1, 1, 2, 0 },
			{ 1, 1, 2, 0 },
			{ 2, 0, 1, 1 },
			{ 2, 0, 1, 1 },
			{ 2, 0, 1, 1 },
			{ 2, 2, 1, 1 }
		};

	
	public static void initAI(Segment walls[], Player players[], float gSize)
	{
		Walls = walls;
		Players = players;
		GridSize = gSize;
	}
	
	public static void updateTime(long dt, long current)
	{
//		Dt = dt;
		Current = current;
	}
	
	public static void doComputer( int player, int target)
	{
		int closestOpp;
		
		// avoid short turns
		if((Current - aiTime[player]) < MIN_TURN_TIME[TURN_TIME_LEVEL])
			return;

		calculateDistances(player);
		closestOpp = getClosestOpponent(player);
		
		if(closestOpp == -1 ||
		   distance > 48.0f ||
		   front < distance)
		{
			doComputerSimple(player,target);
		}
		else
		{
			doComputerActive(player,target);
		}
		
	}
	
	private static void doComputerActive(int plyr, int target)
	{
		int location = -1;
		Segment player = new Segment();
		Segment opponent = new Segment();
		float t_player, t_opponent;
		
		player.vStart.v[0] = Players[plyr].getXpos();
		player.vStart.v[1] = Players[plyr].getYpos();
		
		opponent.vStart.v[0] = Players[target].getXpos();
		opponent.vStart.v[1] = Players[target].getYpos();
		
		player.vDirection.v[0] = 
				Players[plyr].DIRS_X[Players[plyr].getDirection()] *
					Players[plyr].getSpeed();
		player.vDirection.v[1] = 
				Players[plyr].DIRS_Y[Players[plyr].getDirection()] * 
					Players[plyr].getSpeed();
		
		opponent.vDirection.v[0] = 
				Players[target].DIRS_X[Players[target].getDirection()] *
					Players[target].getSpeed();
		
		opponent.vDirection.v[1] = 
				Players[target].DIRS_Y[Players[target].getDirection()] *
					Players[target].getSpeed();
		
		// Compute sector
		Vec diff = player.vStart.Sub(opponent.vStart);
		Vec v1 = new Vec(diff.v[0],diff.v[1],0.0f);
		Vec v2 = new Vec(opponent.vDirection.v[0],opponent.vDirection.v[1],0.0f);
		
		v1.Normalise();
		v2.Normalise();
		
		Vec v3 = v1.Cross(v2);
		v3.Normalise();
		
		float cosphi,phi;
		
		cosphi = v1.Dot(v2);
		
		if(cosphi < -1)
			cosphi = -1;
		else if(cosphi > 1)
			cosphi = 1;
		
		phi = (float)Math.acos(cosphi);
		
		Vec up = new Vec(0.0f,0.0f, 1.0f);
		if(v3.Dot(up) > 0.0f)
		{
			phi = 2.0f * (float)Math.PI - phi;
		}
		
		int i;
		
		for(i=0; i < 8; i++)
		{
			phi -= (float)Math.PI / 4.0f;
			if(phi < 0.0f)
			{
				location = i;
				break;
			}
		}
		
		// Compute intersection
		Segment seg1 = new Segment();
		Segment seg2 = new Segment();
		
		seg1.vStart.Copy(opponent.vStart);
		seg1.vDirection.Copy(opponent.vDirection);
		seg2.vStart.Copy(player.vStart);
		
		seg2.vDirection = opponent.vDirection.Orthogonal();
		seg2.vDirection.Normalise();
		
		seg2.vDirection.Scale(player.vDirection.Length2());
		
		seg1.Intersect(seg2);
		
		t_opponent = seg1.t1;
		t_player = seg1.t2;
		
		if(t_player < 0)
			t_player *= -1;
		
		switch(location)
		{
			case 0: case 7:
			case 1: case 6:
				if(t_player < t_opponent)
				{
					// boost stuff
					//if(t_player - t_opponent < SAVE_T_DIFF)
					ai_aggressive(plyr,target,location);	
				}
				else
				{
					if(t_opponent < HOPELESS_T)
					{
						ai_evasive(plyr,target,location);
					}
					else if(t_opponent - t_player < SAVE_T_DIFF)
					{
						// boost
						ai_aggressive(plyr,target,location);
					}
					else
					{
						ai_evasive(plyr,target,location);
					}
				}
				break;
			case 2: case 4:
			case 3: case 5:
				doComputerSimple(plyr,target);
				break;
		}
		
	}
	
	private static void doComputerSimple(int player, int target)
	{
		int level = 3;
		Segment trail = Players[player].getTrail(Players[player].getTrailOffset());
		
		// First check if we are in danger or should turn...
		if(front > CRITICAL[level] * GridSize && trail.Length() < MAX_SEG_LENGTH[level] * GridSize)
			return;
			 
		// Decide where to turn
		if(front > right  && front > left)
			return; // no way out
		
		if( left > RL_DELTA[level] && 
		       Math.abs(right - left) < RL_DELTA[level] &&
		       backleft > left && tdiff[player] < SPIRAL[level])
		{
			Players[player].doTurn(Players[player].TURN_LEFT, Current);
			tdiff[player]++;
		}
		else if(right > left && tdiff[player] > -SPIRAL[level])
		{
			Players[player].doTurn(Players[player].TURN_RIGHT, Current);
			tdiff[player]--;
		}
		else if( right < left && tdiff[player] < SPIRAL[level])
		{
			Players[player].doTurn(Players[player].TURN_LEFT, Current);
			tdiff[player]++;
		}
		else
		{
			if(tdiff[player] > 0)
			{
				Players[player].doTurn(Players[player].TURN_RIGHT, Current);
				tdiff[player]--;
			}
			else
			{
				Players[player].doTurn(Players[player].TURN_LEFT, Current);
				tdiff[player]++;
			}
		}
		aiTime[player] = Current;
	} 
	
	private static int getClosestOpponent( int player)
	{
		Vec v_player =  new Vec(Players[player].getXpos(), Players[player].getYpos(), 0.0f);
		Vec v_opponent;
		Vec diff;
		int i;
		int retVal = -1;
		float d;
		
		distance = FLT_MAX;
		
		for(i = 0; i < GLTronGame.mCurrentPlayers; i++)
		{
			if(i == player)
				continue;
			
			if(Players[i].getSpeed() > 0)
			{
				v_opponent = new Vec(Players[i].getXpos(),Players[i].getYpos(),0.0f);
				diff = v_player.Sub(v_opponent);
				d = Math.abs(diff.v[0]) + Math.abs(diff.v[1]);
				if(d < distance)
				{
					distance = d;
					retVal = i;
				}
				
			}
		}
		return retVal;
		
	}
	
	private static void calculateDistances( int player)
	{
		int i,j;
		int currDir = Players[player].getDirection();
		int dirLeft = (currDir + 3) % 4;
		int dirRight = (currDir + 1) % 4;
		float t1,t2;
		Segment segments[] = new Segment[E_MAX];
		Segment wall[];
		Vec  v;
		Vec vPos  = new Vec(Players[player].getXpos(),Players[player].getYpos(), 0.0f);
		
		for(i = 0; i< E_MAX; i++ )
		{
			segments[i] = new Segment();
			segments[i].vStart.Copy(vPos);
		}
		
		segments[E_FRONT].vDirection.v[0] = Players[player].DIRS_X[currDir];
		segments[E_FRONT].vDirection.v[1] = Players[player].DIRS_Y[currDir];
		segments[E_LEFT].vDirection.v[0] = Players[player].DIRS_X[dirLeft];
		segments[E_LEFT].vDirection.v[1] = Players[player].DIRS_Y[dirLeft];
		segments[E_RIGHT].vDirection.v[0] = Players[player].DIRS_X[dirRight];
		segments[E_RIGHT].vDirection.v[1] = Players[player].DIRS_Y[dirRight];
		segments[E_BACKLEFT].vDirection.v[0] = 
				Players[player].DIRS_X[dirLeft] - Players[player].DIRS_X[currDir];
		segments[E_BACKLEFT].vDirection.v[1] = 
				Players[player].DIRS_Y[dirLeft] - Players[player].DIRS_Y[currDir];
		
		segments[E_BACKLEFT].vDirection.Normalise2();
		
		front = FLT_MAX;
		left = FLT_MAX;
		right = FLT_MAX;
		backleft = FLT_MAX;
		
		for(i=0; i < GLTronGame.mCurrentPlayers; i++)
		{
			wall = Players[i].getTrails();
			
			if(Players[i].getTrailHeight() < Players[i].TRAIL_HEIGHT)
				continue;
			
			for(j=0; j < Players[i].getTrailOffset() + 1; j++)
			{
				if(i == player && j == Players[i].getTrailOffset())
					break;
				
				v = segments[E_FRONT].Intersect(wall[j]);
				t1 = segments[E_FRONT].t1;
				t2 = segments[E_FRONT].t2;
				if( (v != null) && t1 > 0.0f && t1 < front && t2 >= 0.0f && t2 <= 1.0f)
					front = t1;
				
				v = segments[E_LEFT].Intersect(wall[j]);
				t1 = segments[E_LEFT].t1;
				t2 = segments[E_LEFT].t2;
				if( (v != null) && t1 > 0.0f && t1 < left && t2 >= 0.0f && t2 <= 1.0f)
					left = t1;

				v = segments[E_RIGHT].Intersect(wall[j]);
				t1 = segments[E_RIGHT].t1;
				t2 = segments[E_RIGHT].t2;
				if( (v != null) && t1 > 0.0f && t1 < right && t2 >= 0.0f && t2 <= 1.0f)
					right = t1;

				v = segments[E_BACKLEFT].Intersect(wall[j]);
				t1 = segments[E_BACKLEFT].t1;
				t2 = segments[E_BACKLEFT].t2;
				if( (v != null) && t1 > 0.0f && t1 < backleft && t2 >= 0.0f && t2 <= 1.0f)
					backleft = t1;

			}
		}
		
		for(i=0; i < 4; i++)
		{
			v = segments[E_FRONT].Intersect(Walls[i]);
			t1 = segments[E_FRONT].t1;
			t2 = segments[E_FRONT].t2;
			if( (v != null) && t1 > 0.0f && t1 < front && t2 >= 0.0f && t2 <= 1.0f)
				front = t1;
			
			v = segments[E_LEFT].Intersect(Walls[i]);
			t1 = segments[E_LEFT].t1;
			t2 = segments[E_LEFT].t2;
			if( (v != null) && t1 > 0.0f && t1 < left && t2 >= 0.0f && t2 <= 1.0f)
				left = t1;

			v = segments[E_RIGHT].Intersect(Walls[i]);
			t1 = segments[E_RIGHT].t1;
			t2 = segments[E_RIGHT].t2;
			if( (v != null) && t1 > 0.0f && t1 < right && t2 >= 0.0f && t2 <= 1.0f)
				right = t1;

			v = segments[E_BACKLEFT].Intersect(Walls[i]);
			t1 = segments[E_BACKLEFT].t1;
			t2 = segments[E_BACKLEFT].t2;
			if( (v != null) && t1 > 0.0f && t1 < backleft && t2 >= 0.0f && t2 <= 1.0f)
				backleft = t1;
		}
		
		// leave out debug segments?? 
		
	}
	
	private static void ai_action(int action, int player)
	{
		float save_distance = 
				(MIN_TURN_TIME[TURN_TIME_LEVEL] * Players[player].getSpeed() / 1000.0f) + 20.0f;

		switch(action)
		{
			case 0: break;
			case 1:
				// Turn left
				if(left > save_distance)
				{
					Players[player].doTurn(Players[player].TURN_LEFT, Current);
					tdiff[player]++;
					aiTime[player] = Current;
				}
				break;
			case 2:
				// Turn right
				if(right > save_distance)
				{
					Players[player].doTurn(Players[player].TURN_RIGHT, Current);
					tdiff[player]--;
					aiTime[player] = Current;
				}
				break;
		}
	}
	
	private static void ai_aggressive(int player, int target, int location)
	{
		int dirdiff = (
				4 + Players[player].getDirection() -
				Players[target].getDirection()) % 4;
		
		ai_action(agressive_action[location][dirdiff],player);
	}

	private static void ai_evasive(int player, int target, int location)
	{
		int dirdiff = (
				4 + Players[player].getDirection() - 
				Players[target].getDirection()) % 4;
		
		ai_action(evasive_action[location][dirdiff],player);
	}
}
