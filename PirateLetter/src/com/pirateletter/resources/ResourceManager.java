/**
 * Squong, free Android/AndEngine game
 * Copyright (C) 2013 Martin Varga <android@kul.is>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.pirateletter.resources;


import org.andengine.audio.music.Music;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import com.pirateletter.base.GameActivity;
import com.pirateletter.game.Game;

import android.view.Display;



public class ResourceManager {
	private static final ResourceManager INSTANCE = new ResourceManager();

	public float SCALE_CAMERA_WIDTH, SCALE_CAMERA_HEIGHT;
	public float SCALED_CAMERA_WIDTH, SCALED_CAMERA_HEIGHT;

	// common objects
	public GameActivity activity;
	public Engine engine;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	public Music mMusic;
	
	//game
	public Thread t;
	public Game game = Game.getInstance();

	private ResourceManager() {
	}

	public static ResourceManager getInstance() {
		return INSTANCE;
	}

	public void init(GameActivity activity) {
		this.activity = activity;
		this.engine = activity.getEngine();
		this.camera = engine.getCamera();
		this.vbom = engine.getVertexBufferObjectManager();
		
		SCALE_CAMERA_WIDTH = activity.mScreenWidth/activity.CW;
		SCALE_CAMERA_HEIGHT = activity.mScreenHeight/activity.CH;
		Debug.i("w"+activity.mScreenWidth+" h"+activity.mScreenHeight);
		Debug.i("w"+activity.CW+" h"+activity.CH);
		Debug.i("w"+SCALE_CAMERA_WIDTH+" h"+SCALE_CAMERA_HEIGHT);
		SCALED_CAMERA_WIDTH = activity.mScreenWidth;
		SCALED_CAMERA_HEIGHT = activity.mScreenHeight;
		Debug.i("w"+SCALED_CAMERA_WIDTH+" h"+SCALED_CAMERA_HEIGHT);
	}
}
