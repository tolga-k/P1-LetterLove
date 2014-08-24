package com.pirateletter.base;


import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.IResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.debug.Debug;

import com.pirateletter.resources.ResourceManager;

import android.util.DisplayMetrics;
import android.view.KeyEvent;

public class GameActivity extends BaseGameActivity  {
	/** Screen width, standard 720p */
	public static final float CW = 1920;
	/** Screen height, non-standard - will make it look ok on most 16:9 screens with or without soft buttons */
	public static final float CH = 1080;
	/** FPS limit of the engine */
	public static final int FPS_LIMIT = 60;
	
	protected static final long SPLASH_DURATION = 1000;
	
	Camera camera;
	public float mScreenWidth;
	public float mScreenHeight;
	
	@Override
	public synchronized void onResumeGame() {
		ResourceManager.getInstance().mMusic.play();
		super.onResumeGame();
	}

	@Override
	public synchronized void onPauseGame() {
		ResourceManager.getInstance().mMusic.pause();
		super.onPauseGame();
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight= displayMetrics.heightPixels;
        
		// create simple camera
		camera = new Camera(0, 0, mScreenWidth, mScreenHeight);
		// use the easiest resolution policy
		IResolutionPolicy resolutionPolicy = new RatioResolutionPolicy(CW, CH);
		// play only in portrait mode, we will need music, sound and no multitouch, dithering just in case
		
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, resolutionPolicy, camera);
		
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);

		

        
		return engineOptions;
	}
	
	

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		Engine engine = new LimitedFPSEngine(pEngineOptions, FPS_LIMIT);
		return engine;
	}

	@Override
	public void onCreateResources(
			OnCreateResourcesCallback pOnCreateResourcesCallback)
			throws IOException {
		ResourceManager.getInstance().init(this);
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {
		pOnCreateSceneCallback.onCreateSceneFinished(null);
	}

	@Override
	public void onPopulateScene(Scene pScene,
			OnPopulateSceneCallback pOnPopulateSceneCallback)
			throws IOException {
		SceneManager.getInstance().showSplash();
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
			} catch (NullPointerException ne) {
				// in highly unlikely situation when this there is no scene, do nothing
				Debug.e("The current scene is null", ne);
			}
		}
		return false;
	}

}
