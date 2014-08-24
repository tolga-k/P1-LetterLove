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


import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.util.color.Color;

import com.pirateletter.base.SceneManager;
import com.pirateletter.resources.ResourceManager;

import android.annotation.SuppressLint;
import android.util.Log;





public class MenuScene extends AbstractScene {

	
	BitmapTextureAtlas backgroundTexture;
	BuildableBitmapTextureAtlas startTexture,settingsTexture;
	ITextureRegion backgroundRegion;
	ITiledTextureRegion startRegion,settingsRegion;
	Sprite BackgroundSprite;
	ButtonSprite startSprite,settingsSprite;
	ResourceManager rm = ResourceManager.getInstance();
	SceneManager sm = SceneManager.getInstance();
	TextureManager tm = null;
	
	@Override
	public void loadResources() {
		tm = rm.activity.getTextureManager();
		try {
			loadGfx();
			loadSfx();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadSfx() throws IllegalStateException, IOException
	{
		if (rm.mMusic == null) {
			rm.mMusic = MusicFactory.createMusicFromAsset(rm.engine.getMusicManager(), rm.activity, "sfx/bgMusic.mid");	
			rm.mMusic.setLooping(true);			
			rm.mMusic.setVolume(rm.engine.getMusicManager().getMasterVolume()* 0.2f);
			rm.mMusic.play();
			
		}
	}
	private void loadGfx() throws TextureAtlasBuilderException {
		// TODO Auto-generated method stub
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		

		
		startTexture = new BuildableBitmapTextureAtlas(tm,804,259,
				TextureOptions.BILINEAR);

		startRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(startTexture, rm.activity, "MainMenuStartTile.png", 2, 1);
		startTexture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
		startTexture.load();
		
	
		
		backgroundTexture = new BitmapTextureAtlas(tm,1920,1080,TextureOptions.BILINEAR);
		backgroundRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(backgroundTexture, rm.activity, "bgMainMenu.png", 0, 0);
		
		backgroundTexture.load();
		
		settingsTexture = new BuildableBitmapTextureAtlas(tm,744,278,TextureOptions.BILINEAR);
		settingsRegion = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(settingsTexture, rm.activity, "MainMenuSettingsTile.png", 2, 1);
		settingsTexture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
		settingsTexture.load();
		
		
		
		
		
//		prepareTextureSprite("MainMenuStart.png", 402, 259, startTexture, startRegion);
//		prepareTextureSprite("bgMainMenu.png", 1920, 1080, backgroundTexture, backgroundRegion);
//		prepareTextureSprite("MainMenuSettings.png", 372, 278, settingsTexture, settingsRegion);
	}
	@SuppressLint("UseValueOf")
	@Override
	public void create() {
		//getBackground().setColor(Color.BLUE);
		Sprite background = new Sprite(0, 0, backgroundRegion,
				rm.vbom);
		background.setScale(rm.SCALE_CAMERA_WIDTH,rm.SCALE_CAMERA_HEIGHT);
		background.setScaleCenter(0, 0);
		background.setPosition(0, 0);

		
		//startSprite = new TiledSprite(0, 0, startRegion,rm.vbom);
		
		startSprite = new ButtonSprite(0, 0, startRegion,rm.vbom){
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				switch(pSceneTouchEvent.getAction()){
                case TouchEvent.ACTION_DOWN:
                        this.setCurrentTileIndex(1);
                        break;
                        case TouchEvent.ACTION_UP:
                                this.setCurrentTileIndex(0);
                                sm.showScene(GameScene.class);
                                break;
                }
                return true;
        
				
			}
		};
		startSprite.setScale(rm.SCALE_CAMERA_WIDTH,rm.SCALE_CAMERA_HEIGHT);
		startSprite.setScaleCenter(0, 0);
		startSprite.setPosition(new Float(rm.SCALED_CAMERA_WIDTH / 1.329) ,new Float(rm.SCALED_CAMERA_HEIGHT / 1.366));
		startSprite.setCurrentTileIndex(0);

		
		settingsSprite = new ButtonSprite(0, 0, settingsRegion,
				rm.vbom){
			
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				
				switch(pSceneTouchEvent.getAction()){
                case TouchEvent.ACTION_DOWN:
                        this.setCurrentTileIndex(1);
                        break;
                        case TouchEvent.ACTION_UP:
                                this.setCurrentTileIndex(0);
                                
                                
                                break;
                }
                return true;
        
				
			}
		};
		settingsSprite.setScale(rm.SCALE_CAMERA_WIDTH,rm.SCALE_CAMERA_HEIGHT);
		settingsSprite.setScaleCenter(0, 0);
		settingsSprite.setPosition(Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.329)) ,Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 27.77)));
		
		
		attachChild(background);
		registerTouchArea(startSprite);
		registerTouchArea(settingsSprite);
		attachChild(startSprite);
		attachChild(settingsSprite);
		//newGameSprite.setPosition(Float.valueOf((float) (CAMERA_WIDTH / 3.521)) ,Float.valueOf((float) (CAMERA_HEIGHT / 3.215)));

		setTouchAreaBindingOnActionDownEnabled(true);
		registerTouchArea(startSprite);
		
	}

	@Override
	public void unloadResources() {
		startTexture.unload();
		backgroundTexture.unload();
		settingsTexture.unload();
	}

	@Override
	public void destroy() {
		detachChild(startSprite);
		detachChild(settingsSprite);
		detachChild(BackgroundSprite);
		unregisterTouchArea(startSprite);
		unregisterTouchArea(settingsSprite);
		
	}

	@Override
	public void onPause() {
		
		rm.mMusic.pause();
	}

	@Override
	public void onResume() {
		rm.mMusic.play();
	}



}
