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
package com.pirateletter.scene;


import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.util.debug.Debug;

import com.pirateletter.base.GameActivity;
import com.pirateletter.resources.ResourceManager;

/** 
 * Base for all my scenes
 * @author Martin Varga
 *
 */
public abstract class AbstractScene extends Scene {

    protected ResourceManager res = ResourceManager.getInstance();

    protected Engine engine;
    protected GameActivity activity;
    protected VertexBufferObjectManager vbom;
    protected Camera camera;
    
    public void initialize(GameActivity activity, ResourceManager res) {
    	this.res = res;
    	this.activity = activity;
    	this.vbom = activity.getVertexBufferObjectManager();
    	this.engine = activity.getEngine();
    	this.camera = engine.getCamera();
    }
    
	public abstract void loadResources();
	public abstract void create();
	public abstract void unloadResources();
	public abstract void destroy();
	public void onBackKeyPressed() {
		this.unloadResources();
		this.destroy();
		Debug.d("Back key pressed");
	}

    public abstract void onPause();
    public abstract void onResume();

}
