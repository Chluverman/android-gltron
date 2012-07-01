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

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

import com.glTron.R;
import com.glTron.Game.Camera.CamType;
import com.glTron.Sound.SoundManager;
import com.glTron.Video.*;
import com.glTron.Video.Lighting.LightType;


public class GLTronGame {

	// Define Time data
	public long TimeLastFrame;
	public long TimeCurrent;
	public long TimeDt;
	
	// Define arena setting 
	private static float mCurrentGridSize;

	public static final int MAX_PLAYERS = 6;
	public static final int OWN_PLAYER = 0;
	public static int mCurrentPlayers = 0;
	
	// Define game textures
	private GLTexture ExplodeTex;
	private GLTexture SplashScreen;
	
	private Model LightBike;
	private Model RecognizerModel;
	private Video Visual;
	private WorldGraphics World;
    private Lighting Lights = new Lighting();

	private Player Players[] = new Player[MAX_PLAYERS];
	
	private Recognizer mRecognizer;
	
	// Camera data
	private Camera Cam;
	
	Trails_Renderer TrailRenderer;
	
	// input processing FIXME make enum type instead of bunch of flags
	boolean boProcessInput = false;
	boolean boProcessReset = false;
	int inputDirection;
	
	boolean boInitialState = true;
	private boolean boLoading = true;
	
	// sound index
	public static int CRASH_SOUND = 1;
	public static int ENGINE_SOUND = 2;
	public static int MUSIC_SOUND = 3;
	public static int RECOGNIZER_SOUND = 4;
	
	float mEngineSoundModifier = 1.0f;
	long mEngineStartTime = 0;
	
	// Font
	HUD tronHUD;
	
	// Ads
	Handler _handler;
	
	Context mContext;
	GL10 gl;
	
	public Segment Walls[] = {
			new Segment(),
			new Segment(),
			new Segment(),
			new Segment()
	};
	
	private int aiCount = 1;
	
	// Preferences
	public static UserPrefs mPrefs;
	
	public GLTronGame()
	{
		initWalls();
	}
	
	public void initialiseGame()
	{
		int player;
		
		// Load sounds
	    SoundManager.getInstance();
	    SoundManager.initSounds(mContext);
	    SoundManager.addSound(ENGINE_SOUND, R.raw.game_engine);
	    SoundManager.addSound(CRASH_SOUND, R.raw.game_crash);
	    SoundManager.addSound(RECOGNIZER_SOUND, R.raw.game_recognizer);
	    SoundManager.addMusic(R.raw.song_revenge_of_cats);

	    // Load HUD
	    tronHUD = new HUD(gl,mContext);
	    
		//Load preferences
	    mPrefs = new UserPrefs(mContext);
	    mCurrentPlayers = mPrefs.NumberOfPlayers();
	    mCurrentGridSize = mPrefs.GridSize();
	    
	    initWalls();
	    
		// Load Models
		LightBike = new Model(mContext,R.raw.lightcyclehigh);
		RecognizerModel = new Model(mContext,R.raw.recognizerhigh);
		World = new WorldGraphics(gl, mContext, mCurrentGridSize);
		TrailRenderer = new Trails_Renderer(gl,mContext);
		
		for(player = 0; player < mCurrentPlayers; player++)
		{
			Players[player] = new Player(player, mCurrentGridSize, LightBike, tronHUD);
		}
		
		mRecognizer = new Recognizer(mCurrentGridSize);
		
		Cam = new Camera(Players[OWN_PLAYER], CamType.E_CAM_TYPE_CIRCLING);
		ExplodeTex = new  GLTexture(gl,mContext, R.drawable.gltron_impact);

		ComputerAI.initAI(Walls,Players,mCurrentGridSize);

		// Setup perspective
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
		Visual.doPerspective(gl, mCurrentGridSize);
	    gl.glMatrixMode(GL10.GL_MODELVIEW);

	    // Initialise sounds
	    if(mPrefs.PlayMusic())
	    	SoundManager.playMusic(true);
	    if(mPrefs.PlaySFX())
	    	SoundManager.playSoundLoop(RECOGNIZER_SOUND, 1.0f);
	    
		ResetTime();

		boLoading = false;
	}

	public void setUI_Handler(Handler handler)
	{
		_handler = handler;
	}
	
	// hooks for android pausing thread
	public void pauseGame()
	{
		SoundManager.getInstance();
    	SoundManager.globalPauseSound();
	}
	
	// hooks for android resuming thread
	public void resumeGame()
	{
		SoundManager.getInstance();
		SoundManager.globalResumeSound();

		if(mPrefs != null)
		{
			mPrefs.ReloadPrefs();

			SoundManager.stopSound(RECOGNIZER_SOUND);

			// Update options
			if(!boInitialState)
			{
				Cam.updateType(mPrefs.CameraType());
				if(mPrefs.PlaySFX() && mPrefs.DrawRecognizer())
					SoundManager.playSoundLoop(RECOGNIZER_SOUND, 1.0f);
			}
			else
			{
				boProcessReset = true;
			}
			
			if(mPrefs.PlayMusic())
				SoundManager.playMusic(true);
			else
				SoundManager.stopMusic();
			
			SoundManager.stopSound(ENGINE_SOUND);
			if(mPrefs.PlaySFX()) {
				SoundManager.playSoundLoop(ENGINE_SOUND,mEngineSoundModifier);
			}
			else
				SoundManager.stopSound(ENGINE_SOUND);
			
			
			ResetTime();
		}
	}
	
	public void drawSplash(Context ctx, GL10 gl1)
	{
		float verts[] = {
			-1.0f, 1.0f, 0.0f,
			1.0f,  1.0f, 0.0f,
			-1.0f, -1.0f,0.0f,
			1.0f,  -1.0f, 0.0f
		};
		
		float texture[] = {
			0.0f, 1.0f, 
			1.0f, 1.0f,
			0.0f, 0.0f, 
			1.0f, 0.0f
		};

		gl = gl1;
		mContext = ctx;

		FloatBuffer vertfb = GraphicUtils.ConvToFloatBuffer(verts);
		FloatBuffer texfb = GraphicUtils.ConvToFloatBuffer(texture);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glLoadIdentity();
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		if(SplashScreen == null)
			SplashScreen = new GLTexture(gl,mContext,R.drawable.gltron_bitmap);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, SplashScreen.getTextureID());

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertfb);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texfb);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
	}
	
	public void updateScreenSize(int width, int height)
	{
		if(Visual == null)
		{
			Visual = new Video(width, height);
		}
		else
		{
			Visual.SetWidthHeight(width, height);
		}
		

	}
	
	public void addTouchEvent(float x, float y)
	{
		if(boLoading)
			return;
		
		if(Players[OWN_PLAYER].getSpeed() > 0.0f)
		{
			if(boInitialState)
			{
				// Change the camera and start movement.
				Cam = new Camera(Players[OWN_PLAYER], mPrefs.CameraType());
				SoundManager.stopMusic();
				
				if(mPrefs.PlayMusic())
					SoundManager.playMusic(true);
				
				if(mPrefs.PlaySFX() && mPrefs.DrawRecognizer())
					SoundManager.playSoundLoop(RECOGNIZER_SOUND, 1.0f);
				
				tronHUD.displayInstr(false);
				boInitialState = false;
			}
			else
			{
				if(x <= (Visual.GetWidth() / 2))
				{
					inputDirection = Players[OWN_PLAYER].TURN_LEFT;
				}
				else
				{
					inputDirection = Players[OWN_PLAYER].TURN_RIGHT;
				}
				boProcessInput = true;
			}
		}
		else
		{
			// Restart the player only once trail is 0
			if(Players[OWN_PLAYER].getTrailHeight() <= 0.0f)
			{
				boProcessReset = true;
			}
		}
	}
	
	public void RunGame()
	{
		int plyr;
		
		UpdateTime();
		ComputerAI.updateTime(TimeDt, TimeCurrent);
		
		if(boProcessInput)
		{
			Players[OWN_PLAYER].doTurn(inputDirection, TimeCurrent);
			mEngineSoundModifier = 1.3f;
			boProcessInput = false;
		}
		
		if(boProcessReset)
		{
			// refresh preferences
			mPrefs.ReloadPrefs();
			mCurrentPlayers = mPrefs.NumberOfPlayers();
			
			if(mPrefs.GridSize() != mCurrentGridSize)
			{
				mCurrentGridSize = mPrefs.GridSize();
				
				// re-init the world
				initWalls();
				
				World = new WorldGraphics(gl, mContext, mCurrentGridSize);

				ComputerAI.initAI(Walls,Players,mCurrentGridSize);

				// Setup perspective
			    gl.glMatrixMode(GL10.GL_MODELVIEW);
				Visual.doPerspective(gl, mCurrentGridSize);
			    gl.glMatrixMode(GL10.GL_MODELVIEW);
			}
			
			for(plyr = 0; plyr < mPrefs.NumberOfPlayers(); plyr++)
			{
				Players[plyr] = new Player(plyr, mCurrentGridSize, LightBike, tronHUD);
				Players[plyr].setSpeed(mPrefs.Speed());
			}
			
			mRecognizer = new Recognizer(mCurrentGridSize);
			
			tronHUD.resetConsole();
			Cam = new Camera(Players[OWN_PLAYER], CamType.E_CAM_TYPE_CIRCLING);
			
			SoundManager.stopSound(ENGINE_SOUND); // ensure sound is stopped before playing again.
			SoundManager.stopSound(RECOGNIZER_SOUND);
			
			tronHUD.displayInstr(true);
			
			if(mPrefs.PlaySFX())
				SoundManager.playSoundLoop(ENGINE_SOUND, 1.0f);
			
			boInitialState = true;
			boProcessReset = false;
		}
		
		// round robin AI to speed up frame time
    	if(Players[aiCount].getTrailHeight() == Players[aiCount].TRAIL_HEIGHT)
		{
			ComputerAI.doComputer(aiCount, OWN_PLAYER);
		}
    
    	// Manage sounds
    	if(Players[OWN_PLAYER].getSpeed() == 0.0f)
    	{
			SoundManager.stopSound(ENGINE_SOUND);
			mEngineStartTime = 0;
			mEngineSoundModifier = 1.0f;
    	}
    	else if(!boInitialState)
    	{
    		if(mPrefs.PlaySFX())
    		{
	    		if(mEngineSoundModifier < 1.5f)
	    		{
	    			if(mEngineStartTime != 0)
	    			{
	    				if((TimeCurrent + 1000) > mEngineStartTime)
	    				{
	    					mEngineSoundModifier += 0.01f;
	    					SoundManager.changeRate(ENGINE_SOUND, mEngineSoundModifier);
	    				}
	    			}
	    		}
    		}
    	}

		mEngineStartTime = TimeCurrent;

    	aiCount++;
    	
    	if(aiCount > (mCurrentPlayers - 1))
    		aiCount = 1;
		
		RenderGame();
	}

	// DT smoothing experiment
	private final int MAX_SAMPLES = 20;
	private long DtHist[] = new long[MAX_SAMPLES];
	private int DtHead = 0;
	private int DtElements = 0;
	
	private void ResetTime()
	{
		TimeLastFrame = SystemClock.uptimeMillis();
		TimeCurrent = TimeLastFrame;
		TimeDt = 0;
		DtHead = 0;
		DtElements = 0;
	}
	
	private void UpdateTime()
	{
		long RealDt;
		int i;
		
		TimeLastFrame = TimeCurrent;
		TimeCurrent = SystemClock.uptimeMillis();
		RealDt = TimeCurrent - TimeLastFrame;
//		TimeDt = RealDt;
		
		DtHist[DtHead] = RealDt;
		
		DtHead++;
		
		if(DtHead >= MAX_SAMPLES)
		{
			DtHead = 0;
		}
		
		if(DtElements == MAX_SAMPLES)
		{
			// Average the last MAX_SAMPLE DT's
			TimeDt = 0;
			for(i = 0; i < MAX_SAMPLES; i++)
			{
				TimeDt += DtHist[i];
			}
			TimeDt /= MAX_SAMPLES;
		}
		else
		{
			TimeDt = RealDt;
			DtElements++;
		}
	}
	
	private void initWalls()
	{
		float raw[][] = {
				{0.0f, 0.0f, 1.0f, 0.0f },
				{ 1.0f, 0.0f, 0.0f, 1.0f },
				{ 1.0f, 1.0f, -1.0f, 0.0f },
				{ 0.0f, 1.0f, 0.0f, -1.0f }
		};
		
		float width = mCurrentGridSize;
		float height = mCurrentGridSize;
		
		int j;
		
		for(j = 0; j < 4; j++)
		{
			Walls[j].vStart.v[0] = raw[j][0] * width;
			Walls[j].vStart.v[1] = raw[j][1] * height;
			Walls[j].vDirection.v[0] = raw[j][2] * width;
			Walls[j].vDirection.v[1] = raw[j][3] * height;
		}
	}

	
	private void RenderGame()
	{
		int player;
		boolean boOwnPlayerActive = true;
		boolean boOtherPlayersActive = false;
		boolean boCheckWinner = false;
		
		if(!boInitialState)
		{
			for(player = 0; player < mCurrentPlayers; player++)
			{
				Players[player].doMovement(TimeDt,TimeCurrent,Walls,Players);
				//check win lose should be in game logic not render - FIXME
				if(player == OWN_PLAYER)
				{
					if(Players[player].getSpeed() == 0.0f)
						boOwnPlayerActive = false;
				}
				else 
				{
					if(Players[player].getSpeed() > 0.0f)
						boOtherPlayersActive = true;
				}
				
				boCheckWinner = true;
					
			}
			
			if(mPrefs.DrawRecognizer())
				mRecognizer.doMovement(TimeDt);
			
		}
		
		Cam.doCameraMovement(Players[OWN_PLAYER],TimeCurrent, TimeDt);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Load identity
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		Visual.doPerspective(gl, mCurrentGridSize);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, Cam.ReturnCamBuffer());

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);
		gl.glEnable(GL10.GL_BLEND);

		Cam.doLookAt(gl);
		
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDepthMask(false);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		
		World.drawSkyBox(gl);
		World.drawFloorTextured(gl);
		
		gl.glDepthMask(true);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		if(mPrefs.DrawRecognizer())
			mRecognizer.draw(gl, RecognizerModel);
		
		World.drawWalls(gl);
		
		Lights.setupLights(gl, LightType.E_WORLD_LIGHTS);

		for(player = 0; player < mCurrentPlayers; player++)
		{
			if(player == 0 || Players[player].isVisible(Cam))
					Players[player].drawCycle(gl, TimeCurrent, TimeDt, Lights, ExplodeTex);
			
			Players[player].drawTrails(TrailRenderer,Cam);
		}
		
		if(boCheckWinner)
		{
			if(!boOwnPlayerActive && boOtherPlayersActive)
			{
				tronHUD.displayLose();
			}
			else if(boOwnPlayerActive && !boOtherPlayersActive)
			{
				tronHUD.displayWin();
				Players[OWN_PLAYER].setSpeed(0.0f);
			}
		}

		tronHUD.draw(Visual,TimeDt,Players[OWN_PLAYER].getScore());
	}
	
}
