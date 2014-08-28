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

import java.security.acl.LastOwnerException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.crypto.spec.PSource;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.UpdateHandlerList;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimationData;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.shader.ShaderProgram;
import org.andengine.opengl.shader.ShaderProgramManager;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;
import org.andengine.ui.IGameInterface.OnPopulateSceneCallback;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.algorithm.Spiral;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.util.modifier.IModifier;
import org.andengine.util.modifier.ease.EaseBounceIn;
import org.andengine.util.modifier.ease.EaseCircularIn;
import org.andengine.util.modifier.ease.EaseQuadIn;

import com.pirateletter.base.SceneManager;
import com.pirateletter.game.*;
import com.pirateletter.resources.ResourceManager;

import android.animation.TimeAnimator;
import android.graphics.Point;
import android.graphics.PointF;
import android.text.method.MovementMethod;
import android.transition.Scene;
import android.view.MotionEvent.PointerCoords;

public class GameScene extends AbstractScene {

	static int HASTOUCH = 255123;
	static int CARD1 = 2551;
	static int CARD2 = 2552;
	static int CARD3 = 2553;
	static int CARD4 = 2554;
	static int CARD5 = 2555;
	static int CARD6 = 2556;
	static int CARD7 = 2557;
	static int CARD8 = 2558;

	static float perUpdate = 0.01666f;
	// idle = 0
	// ready = 1
	int STATE = 0;
	int popupCardPick = 0;
	int playerPick = -255;
	int cardPick = 0;
	int playerLeft, playerTop, playerRight, playerSelf;
	BuildableBitmapTextureAtlas ownCard0, ownCard1, ownCard2, ownCard3,
			ownCard4, ownCard5, ownCard6, ownCard7, ownCard8, background,
			backCard, popUp, highLightCard, highLightDeck;
	BitmapTextureAtlas mFontTexture;
	Font mFont;
	Text textTopPlayer, textLeftPlayer, textRightPlayer, textBottomPlayer,
			textGameInfo, textNewRound;
	List<BuildableBitmapTextureAtlas> activeTextures = new ArrayList<BuildableBitmapTextureAtlas>();
	List<Sprite> activeSprites = new ArrayList<Sprite>();
	List<ITiledTextureRegion> activeRegions = new ArrayList<ITiledTextureRegion>();

	ITiledTextureRegion regionOwnCard0, regionOwnCard1, regionOwnCard2,
			regionOwnCard3, regionOwnCard4, regionOwnCard5, regionOwnCard6,
			regionOwnCard7, regionOwnCard8, regionBackground, regionBackCard,
			regionPopUp, regionHighLightCard, regionHighLightDeck;

	Sprite spriteBackground, spriteOwnCard1, spriteOwnCard2,
			spriteLeftEnemyCard, spriteTopEnemyCard, spriteRightEnemyCard,
			spritePopUp, spriteChooseCard, spriteChooseCard1,
			spriteChooseCard2, spriteChooseCard3, spriteChooseCard4,
			spriteChooseCard5, spriteChooseCard6, spriteChooseCard7,
			spriteChooseCard8, spriteOwnCardLeft, spriteOwnCardRight,
			spriteHLLeft, spriteHLRight, spriteHLtop, spriteHLOwn,
			spriteHLDeck, spriteCardAnimation;

	ResourceManager rm = ResourceManager.getInstance();
	SceneManager sm = SceneManager.getInstance();
	TextureManager tm = null;
	FontManager fm = null;
	Game mGame = rm.game;
	float animationCurrTime;
	Play currRunningPlay;
	int playNr;
	int currRound = 1;
	List<Play> plays = new ArrayList<Play>();

	private MoveXModifier modifierX;
	private MoveYModifier modifierY;

	private List<Card> ownCards;
	private Animation runningAnimation;
	private List<Animation> animationQueue = new ArrayList<Animation>();
	boolean animationRunning, showingDraw, showingEndScreen,
			showingRoundScreen, drawCardHighlightIsShown, canInput;

	Player mPlayer = new Player() {

		private static final long serialVersionUID = -6076239573042361892L;

		@Override
		public void turnMessage() {
			canInput = false;
			drawCardHighlightIsShown = false;
			plays = mGame.getRemainingPlays(playNr);
			if (!plays.isEmpty()) {
				animationRunning = true;
				initAnimations();
			} else if (mGame.getRound() > currRound) {
				animationRunning = false;
				showNewRound();

			} else {
				drawCardHighlightIsShown = false;
				animationRunning = false;
				canInput = true;
				// updateText();
			}

		}

	};

	@Override
	public void loadResources() {
		canInput = true;
		drawCardHighlightIsShown = false;
		tm = rm.activity.getTextureManager();
		fm = rm.activity.getFontManager();
		try {
			loadGfx();
			loadFonts();
		} catch (TextureAtlasBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadGfx() throws TextureAtlasBuilderException {
		// TODO Auto-generated method stub
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/Game/");

		background = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);

		regionBackground = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(background, rm.activity, "board.png", 1,
						1);
		background
				.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						0, 0, 0));
		background.load();
		activeTextures.add(background);
		activeRegions.add(regionBackground);

		ownCard0 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard0 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard0, rm.activity, "ownCard0.png", 1,
						1);
		ownCard0.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard0.load();
		activeTextures.add(ownCard0);
		activeRegions.add(regionOwnCard0);

		ownCard1 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard1 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard1, rm.activity, "ownCard1.png", 1,
						1);
		ownCard1.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard1.load();
		activeTextures.add(ownCard1);
		activeRegions.add(regionOwnCard1);
		// 2
		ownCard2 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard2 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard2, rm.activity, "ownCard2.png", 1,
						1);
		ownCard2.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard2.load();
		activeTextures.add(ownCard2);
		activeRegions.add(regionOwnCard2);

		// 3
		ownCard3 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard3 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard3, rm.activity, "ownCard3.png", 1,
						1);
		ownCard3.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard3.load();
		activeTextures.add(ownCard3);
		activeRegions.add(regionOwnCard3);

		// 4
		ownCard4 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard4 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard4, rm.activity, "ownCard4.png", 1,
						1);
		ownCard4.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard4.load();
		activeTextures.add(ownCard4);
		activeRegions.add(regionOwnCard4);

		// 5
		ownCard5 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard5 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard5, rm.activity, "ownCard5.png", 1,
						1);
		ownCard5.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard5.load();
		activeTextures.add(ownCard5);
		activeRegions.add(regionOwnCard5);

		// 6
		ownCard6 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard6 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard6, rm.activity, "ownCard6.png", 1,
						1);
		ownCard6.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard6.load();
		activeTextures.add(ownCard6);
		activeRegions.add(regionOwnCard6);

		// 7
		ownCard7 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard7 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard7, rm.activity, "ownCard7.png", 1,
						1);
		ownCard7.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard7.load();
		activeTextures.add(ownCard7);
		activeRegions.add(regionOwnCard7);

		// 8
		ownCard8 = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionOwnCard8 = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(ownCard8, rm.activity, "ownCard8.png", 1,
						1);
		ownCard8.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		ownCard8.load();
		activeTextures.add(ownCard8);
		activeRegions.add(regionOwnCard8);

		backCard = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionBackCard = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(backCard, rm.activity, "CardBack.png", 1,
						1);
		backCard.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		backCard.load();
		activeTextures.add(backCard);
		activeRegions.add(regionBackCard);

		// popupTexture
		popUp = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionPopUp = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(popUp, rm.activity, "popUp.png", 1, 1);
		popUp.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		popUp.load();
		activeTextures.add(popUp);
		activeRegions.add(regionPopUp);

		// popupTexture
		highLightCard = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionHighLightCard = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(highLightCard, rm.activity,
						"highLightcards.png", 1, 1);
		highLightCard
				.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						0, 0, 0));
		highLightCard.load();
		activeTextures.add(highLightCard);
		activeRegions.add(regionHighLightCard);

		// popupTexture
		highLightDeck = new BuildableBitmapTextureAtlas(tm, 1920, 1080,
				TextureOptions.BILINEAR);
		regionHighLightDeck = BitmapTextureAtlasTextureRegionFactory
				.createTiledFromAsset(highLightDeck, rm.activity,
						"highLightDeck.png", 1, 1);
		highLightDeck
				.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
						0, 0, 0));
		highLightDeck.load();
		activeTextures.add(highLightDeck);
		activeRegions.add(regionPopUp);
	}

	void loadFonts() {
		FontFactory.setAssetBasePath("fonts/");

		mFont = FontFactory.createFromAsset(fm, tm, 256, 256,
				TextureOptions.BILINEAR, rm.activity.getAssets(),
				"Minecraftia.ttf", 32f, true, Color.WHITE_ARGB_PACKED_INT);
		mFont.load();
	}

	BuildableBitmapTextureAtlas loadBitmapTextureAtlas(
			BuildableBitmapTextureAtlas texture, int w, int h)
			throws TextureAtlasBuilderException {
		texture = new BuildableBitmapTextureAtlas(tm, 804, 259,
				TextureOptions.BILINEAR);
		texture.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(
				0, 0, 0));
		return texture;

	}

	ITiledTextureRegion loadTextureRegion(ITiledTextureRegion region,
			BuildableBitmapTextureAtlas texture, String file, int columns,
			int rows) {
		region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				texture, rm.activity, file, columns, rows);
		return region;
	}

	@Override
	public void create() {
		getBackground().setColor(Color.RED);

		spriteBackground = new Sprite(0, 0, regionBackground, rm.vbom);
		spriteBackground
				.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		spriteBackground.setScaleCenter(0, 0);
		spriteBackground.setPosition(0, 0);
		attachChild(spriteBackground);
		activeSprites.add(spriteBackground);

		spriteOwnCardLeft = new ButtonSprite(0, 0, regionOwnCard0, rm.vbom);
		/*
		 * spriteOwnCardLeft.setScale(rm.SCALE_CAMERA_WIDTH,
		 * rm.SCALE_CAMERA_HEIGHT); spriteOwnCardLeft.setScaleCenter(0, 0);
		 * spriteOwnCardLeft.setPosition( Float.valueOf((float)
		 * (rm.SCALED_CAMERA_WIDTH / 2.545)), Float.valueOf((float)
		 * (rm.SCALED_CAMERA_HEIGHT / 1.531)));
		 * 
		 * attachChild(spriteOwnCardLeft); registerTouchArea(spriteOwnCardLeft);
		 * activeSprites.add(spriteOwnCardLeft);
		 * 
		 * spriteOwnCardRight = new Sprite(0, 0, regionOwnCard0, rm.vbom) {
		 * 
		 * @Override public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
		 * float pTouchAreaLocalX, float pTouchAreaLocalY) {
		 * 
		 * playCard(2); return super .onAreaTouched(pSceneTouchEvent,
		 * pTouchAreaLocalX, pTouchAreaLocalY); } };
		 * spriteOwnCardRight.setScale(rm.SCALE_CAMERA_WIDTH,
		 * rm.SCALE_CAMERA_HEIGHT); spriteOwnCardRight.setScaleCenter(0, 0);
		 * spriteOwnCardRight.setPosition( Float.valueOf((float)
		 * (rm.SCALED_CAMERA_WIDTH / 1.848)), Float.valueOf((float)
		 * (rm.SCALED_CAMERA_HEIGHT / 1.531))); attachChild(spriteOwnCardRight);
		 * activeSprites.add(spriteOwnCardRight);
		 * 
		 * spriteOwnCard2 = new Sprite(0, 0, regionOwnCard1.deepCopy(),
		 * rm.vbom); spriteOwnCard2.setScale(rm.SCALE_CAMERA_WIDTH,
		 * rm.SCALE_CAMERA_HEIGHT); spriteOwnCard2.setScaleCenter(0, 0);
		 * spriteOwnCard2.setPosition( Float.valueOf((float)
		 * (rm.SCALED_CAMERA_WIDTH / 1.848)), Float.valueOf((float)
		 * (rm.SCALED_CAMERA_HEIGHT / 1.531))); attachChild(spriteOwnCard2);
		 * activeSprites.add(spriteOwnCard2);
		 */
		spriteLeftEnemyCard = new Sprite(0, 0, regionBackCard, rm.vbom);
		spriteLeftEnemyCard.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteLeftEnemyCard.setScaleCenter(0, 0);
		spriteLeftEnemyCard.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / -10.309

				)), Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 3.106)));
		spriteLeftEnemyCard.setRotation(180f);
		attachChild(spriteLeftEnemyCard);
		activeSprites.add(spriteLeftEnemyCard);

		spriteTopEnemyCard = new Sprite(0, 0, regionBackCard, rm.vbom);
		spriteTopEnemyCard.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteTopEnemyCard.setScaleCenter(0, 0);
		spriteTopEnemyCard.setPosition(rm.SCALE_CAMERA_WIDTH * 698.181f,
				rm.SCALE_CAMERA_HEIGHT * -189.108f);

		spriteTopEnemyCard.setRotation(-90f);
		attachChild(spriteTopEnemyCard);
		activeSprites.add(spriteTopEnemyCard);

		// RightCard
		spriteRightEnemyCard = new Sprite(0, 0, regionBackCard, rm.vbom);
		spriteRightEnemyCard.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteRightEnemyCard.setScaleCenter(0, 0);
		spriteRightEnemyCard.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.178)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 3.106

				))); // spriteRightEnemyCard.setRotation(-90f);
		attachChild(spriteRightEnemyCard);
		activeSprites.add(spriteRightEnemyCard);

		// Deck
		spriteChooseCard = new Sprite(0, 0, regionBackCard.deepCopy(), rm.vbom);
		spriteChooseCard
				.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard.setScaleCenter(0, 0);
		spriteChooseCard.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.178)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 3.106

				)));

		attachChild(spriteChooseCard);
		activeSprites.add(spriteChooseCard);

		playerLeft = 1;
		playerRight = 3;
		playerTop = 2;
		playerSelf = 0;
		mPlayer.clientName = "p1";

		mGame.addPlayer(mPlayer);
		mGame.startSinglePlayer();

		mGame.startGame();
		mGame.startNewRound();
		updateText();
		updateCards();
		getHand();
		drawOwnCards();
		doneDrawOwnCards();

	}

	void updateOutOfRound(String s) {
		String position = getPositionByNr(mGame.getNrByName(s));
		if (position == "Left") {
			if (mGame.isPlayerOutOfRoundExplicit(mGame
					.getPlayerClientNameByNr(playerLeft))) {
				spriteLeftEnemyCard.setAlpha(0.0f);
			} else {
				spriteLeftEnemyCard.setAlpha(1.0f);
			}
		} else if (position == "Top") {
			if (mGame.isPlayerOutOfRoundExplicit(mGame
					.getPlayerClientNameByNr(playerTop))) {
				spriteTopEnemyCard.setAlpha(0.0f);
			} else {
				spriteTopEnemyCard.setAlpha(1.0f);
			}
		} else if (position == "Right") {
			if (mGame.isPlayerOutOfRoundExplicit(mGame
					.getPlayerClientNameByNr(playerRight))) {
				spriteRightEnemyCard.setAlpha(0.0f);
			} else {
				spriteRightEnemyCard.setAlpha(1.0f);
			}
		} else if (position == "Bot") {
			if (mGame.isPlayerOutOfRoundExplicit(mGame
					.getPlayerClientNameByNr(playerSelf))) {
				if (spriteOwnCardLeft != null) {
					spriteOwnCardLeft.setAlpha(0.0f);
				}
				if (spriteOwnCardRight != null) {
					spriteOwnCardRight.setAlpha(0.0f);
				}
			} else {
				if (spriteOwnCardLeft != null) {
					spriteOwnCardLeft.setAlpha(1.0f);
				}
				if (spriteOwnCardRight != null) {
					spriteOwnCardRight.setAlpha(1.0f);
				}
			}
		}
	}

	void updateCardsWithPlay(Play p, boolean after) {
		if (after) {
			updateOutOfRound(p.getAfterPlayer().getClientname());
			updateOutOfRound(p.getAfterEnemy().getClientname());
		} else {
			updateOutOfRound(p.getBeforePlayer().getClientname());
			updateOutOfRound(p.getBeforeEnemy().getClientname());
		}
	}

	void UpdatePlayerOutOfRound(String clientname, boolean outOfRound) {
		String position = getPositionByNr(mGame.getNrByName(clientname));
		if (position == "Left") {
			if (outOfRound) {
				spriteLeftEnemyCard.setAlpha(0.0f);
			} else {
				spriteLeftEnemyCard.setAlpha(1.0f);
			}
		} else if (position == "Top") {
			if (outOfRound) {
				spriteTopEnemyCard.setAlpha(0.0f);
			} else {
				spriteTopEnemyCard.setAlpha(1.0f);
			}
		} else if (position == "Right") {
			if (outOfRound) {
				spriteRightEnemyCard.setAlpha(0.0f);
			} else {
				spriteRightEnemyCard.setAlpha(1.0f);
			}
		} else if (position == "Bot") {
			if (outOfRound) {
				if (spriteOwnCardLeft != null) {
					spriteOwnCardLeft.setAlpha(0.0f);
				}
				if (spriteOwnCardRight != null) {
					spriteOwnCardRight.setAlpha(0.0f);
				}
			} else {
				if (spriteOwnCardLeft != null) {
					spriteOwnCardLeft.setAlpha(1.0f);
				}
				if (spriteOwnCardRight != null) {
					spriteOwnCardRight.setAlpha(1.0f);
				}
			}
		}

	}

	void updatePlayerText(String clientName, int keys, boolean isProtected) {
		String position = getPositionByNr(mGame.getNrByName(clientName));
		if (position == "Left") {
			textLeftPlayer.setText(keys + "/4  keys\n\n"
					+ (isProtected ? "Protected" : "Unprotected"));
		} else if (position == "Top") {
			textTopPlayer.setText(keys + "/4  keys\n\n"
					+ (isProtected ? "Protected" : "Unprotected"));
		} else if (position == "Right") {
			textRightPlayer.setText(keys + "/4  keys\n\n"
					+ (isProtected ? "Protected" : "Unprotected"));
		} else if (position == "Bot") {
			textBottomPlayer.setText(keys + "/4  keys\n\n"
					+ (isProtected ? "Protected" : "Unprotected"));
		}
	}

	void updateUiWithAnimation(Animation a) {
		UpdatePlayerOutOfRound(a.getPlayer().getClientname(), a.getPlayer()
				.isOutOfRound());
		UpdatePlayerOutOfRound(a.getEnemy().getClientname(), a.getEnemy()
				.isOutOfRound());
		updatePlayerText(a.getPlayer().getClientname(), a.getPlayer()
				.getPoints(), a.getPlayer().isProtectedForFound());
		updatePlayerText(a.getEnemy().getClientname(),
				a.getEnemy().getPoints(), a.getEnemy().isProtectedForFound());
		textGameInfo.setText("Round = " + a.getPlayer().getRound());
	}

	void updateCards() {
		if (mGame.isPlayerOutOfRoundExplicit(mGame
				.getPlayerClientNameByNr(playerLeft))) {
			spriteLeftEnemyCard.setAlpha(0.0f);
		} else {
			spriteLeftEnemyCard.setAlpha(1.0f);
		}
		if (mGame.isPlayerOutOfRoundExplicit(mGame
				.getPlayerClientNameByNr(playerTop))) {
			spriteTopEnemyCard.setAlpha(0.0f);
		} else {
			spriteTopEnemyCard.setAlpha(1.0f);
		}
		if (mGame.isPlayerOutOfRoundExplicit(mGame
				.getPlayerClientNameByNr(playerRight))) {
			spriteRightEnemyCard.setAlpha(0.0f);
		} else {
			spriteRightEnemyCard.setAlpha(1.0f);
		}
		if (mGame.isPlayerOutOfRoundExplicit(mGame
				.getPlayerClientNameByNr(playerSelf))) {
			if (spriteOwnCardLeft != null) {
				spriteOwnCardLeft.setAlpha(0.0f);
			}
			if (spriteOwnCardRight != null) {
				spriteOwnCardRight.setAlpha(0.0f);
			}
		} else {
			if (spriteOwnCardLeft != null) {
				spriteOwnCardLeft.setAlpha(1.0f);
			}
			if (spriteOwnCardRight != null) {
				spriteOwnCardRight.setAlpha(1.0f);
			}
		}
	}

	void updateText() {
		detachChild(textTopPlayer);
		textTopPlayer = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textTopPlayer.setHorizontalAlign(HorizontalAlign.CENTER);
		textTopPlayer.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textTopPlayer.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.605)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 33.333)));
		attachChild(textTopPlayer);

		textTopPlayer.setText(mGame.getPlayerPoint(mGame
				.getPlayerClientNameByNr(playerTop))
				+ "/4  keys\n\n"
				+ (mGame.isPlayerProtected(mGame
						.getPlayerClientNameByNr(playerTop)) ? "Protected"
						: "Unprotected"));

		detachChild(textLeftPlayer);
		textLeftPlayer = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textLeftPlayer.setHorizontalAlign(HorizontalAlign.CENTER);
		textLeftPlayer.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textLeftPlayer.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 22.727)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 7.576)));
		attachChild(textLeftPlayer);

		textLeftPlayer.setText(mGame.getPlayerPoint(mGame
				.getPlayerClientNameByNr(playerLeft))
				+ "/4  keys\n\n"
				+ (mGame.isPlayerProtected(mGame
						.getPlayerClientNameByNr(playerLeft)) ? "Protected"
						: "Unprotected"));

		detachChild(textRightPlayer);
		textRightPlayer = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textRightPlayer.setHorizontalAlign(HorizontalAlign.CENTER);
		textRightPlayer.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textRightPlayer.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.171)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.582)));
		attachChild(textRightPlayer);

		textRightPlayer.setText(mGame.getPlayerPoint(mGame
				.getPlayerClientNameByNr(playerRight))
				+ "/4  keys\n\n"
				+ (mGame.isPlayerProtected(mGame
						.getPlayerClientNameByNr(playerRight)) ? "Protected"
						: "Unprotected"));

		detachChild(textBottomPlayer);
		textBottomPlayer = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textBottomPlayer.setHorizontalAlign(HorizontalAlign.CENTER);
		textBottomPlayer
				.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textBottomPlayer.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 4.831)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.253)));
		attachChild(textBottomPlayer);

		textBottomPlayer.setText(mGame.getPlayerPoint(mGame
				.getPlayerClientNameByNr(playerSelf))
				+ "/4  keys\n\n"
				+ (mGame.isPlayerProtected(mGame
						.getPlayerClientNameByNr(playerSelf)) ? "Protected"
						: "Unprotected"));

		detachChild(textGameInfo);
		textGameInfo = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textGameInfo.setHorizontalAlign(HorizontalAlign.CENTER);
		textGameInfo.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textGameInfo.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 22.727)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 33.333)));
		attachChild(textGameInfo);

		textGameInfo.setText("Round = " + mGame.getRound());

	}

	void getHand() {
		ownCards = new ArrayList<Card>(mGame.getPlayerCards(mPlayer));
	}

	void hideDrawCardHighlight() {

		unregisterTouchArea(spriteHLDeck);
		detachChild(spriteHLDeck);
		drawCardHighlightIsShown = false;
	}

	void showDrawCardHighlight() {
		// TODO Auto-generated method stub
		// highlightDeck
		canInput = false;
		drawCardHighlightIsShown = true;
		STATE = 1;
		spriteHLDeck = new Sprite(0, 0, regionHighLightDeck.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mGame.drawCard(mPlayer);
				getHand();
				drawOwnCards();
				hideDrawCardHighlight();

				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
		spriteHLDeck.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		spriteHLDeck.setScaleCenter(0, 0);
		spriteHLDeck.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 2.915)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 4.717)));

		registerTouchArea(spriteHLDeck);
		attachChild(spriteHLDeck);
		activeSprites.add(spriteHLDeck);
	}

	void showPopupCardPick() {
		// popup
		//
		spritePopUp = new Sprite(0, 0, regionPopUp, rm.vbom);
		spritePopUp.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		spritePopUp.setScaleCenter(0, 0);
		attachChild(spritePopUp);
		activeSprites.add(spritePopUp);

		spriteChooseCard1 = new Sprite(0, 0, regionOwnCard1.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					popupCardPick = 1;
					hidePopupCardPick();
				}

				return true;
			}
		};
		spriteChooseCard1.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard1.setScaleCenter(0, 0);
		spriteChooseCard1.setSize(spriteChooseCard1.getWidthScaled()
				* (float) 0.727, spriteChooseCard1.getHeightScaled()
				* (float) 0.727);
		//
		spriteChooseCard1
				.setPosition(
						Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717
								+ (spriteChooseCard1.getWidth() * 0) + ((spriteChooseCard1
								.getWidth() / 7) * 0))),
						Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 6.410)));
		attachChild(spriteChooseCard1);
		activeSprites.add(spriteChooseCard1);
		registerTouchArea(spriteChooseCard1);

		//
		spriteChooseCard2 = new Sprite(0, 0, regionOwnCard2.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					popupCardPick = 2;
					hidePopupCardPick();
				}
				return true;
			}
		};
		spriteChooseCard2.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard2.setScaleCenter(0, 0);
		spriteChooseCard2.setSize(spriteChooseCard2.getWidthScaled()
				* (float) 0.727, spriteChooseCard2.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard2.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 1)
						+ ((spriteChooseCard1.getWidth() / 7) * 1)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 6.410)));
		attachChild(spriteChooseCard2);
		activeSprites.add(spriteChooseCard2);
		registerTouchArea(spriteChooseCard2);
		//
		spriteChooseCard3 = new Sprite(0, 0, regionOwnCard3.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					popupCardPick = 3;
					hidePopupCardPick();
				}

				return true;
			}
		};
		spriteChooseCard3.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard3.setScaleCenter(0, 0);
		spriteChooseCard3.setSize(spriteChooseCard3.getWidthScaled()
				* (float) 0.727, spriteChooseCard3.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard3.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 2)
						+ ((spriteChooseCard1.getWidth() / 7) * 2)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 6.410)));
		attachChild(spriteChooseCard3);
		activeSprites.add(spriteChooseCard3);
		registerTouchArea(spriteChooseCard3);
		//
		spriteChooseCard4 = new Sprite(0, 0, regionOwnCard4.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				popupCardPick = 4;
				hidePopupCardPick();
				return true;
			}
		};
		spriteChooseCard4.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard4.setScaleCenter(0, 0);
		spriteChooseCard4.setSize(spriteChooseCard4.getWidthScaled()
				* (float) 0.727, spriteChooseCard4.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard4.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 3)
						+ ((spriteChooseCard1.getWidth() / 7) * 3)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 6.410)));
		attachChild(spriteChooseCard4);
		activeSprites.add(spriteChooseCard4);
		registerTouchArea(spriteChooseCard4);
		//
		spriteChooseCard5 = new Sprite(0, 0, regionOwnCard5.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				popupCardPick = 5;
				hidePopupCardPick();
				return true;
			}
		};
		spriteChooseCard5.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard5.setScaleCenter(0, 0);
		spriteChooseCard5.setSize(spriteChooseCard5.getWidthScaled()
				* (float) 0.727, spriteChooseCard5.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard5.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 0)
						+ ((spriteChooseCard1.getWidth() / 7) * 0)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.980)));
		attachChild(spriteChooseCard5);
		activeSprites.add(spriteChooseCard5);
		registerTouchArea(spriteChooseCard5);
		//
		spriteChooseCard6 = new Sprite(0, 0, regionOwnCard6.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				popupCardPick = 6;
				hidePopupCardPick();
				return true;
			}
		};
		spriteChooseCard6.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard6.setScaleCenter(0, 0);
		spriteChooseCard6.setSize(spriteChooseCard6.getWidthScaled()
				* (float) 0.727, spriteChooseCard6.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard6.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 1)
						+ ((spriteChooseCard1.getWidth() / 7) * 1)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.980)));
		attachChild(spriteChooseCard6);
		activeSprites.add(spriteChooseCard6);
		registerTouchArea(spriteChooseCard6);
		//
		spriteChooseCard7 = new Sprite(0, 0, regionOwnCard7.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				popupCardPick = 7;
				hidePopupCardPick();
				return true;
			}
		};
		spriteChooseCard7.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard7.setScaleCenter(0, 0);
		spriteChooseCard7.setSize(spriteChooseCard7.getWidthScaled()
				* (float) 0.727, spriteChooseCard7.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard7.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 2)
						+ ((spriteChooseCard1.getWidth() / 7) * 2)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.980)));
		attachChild(spriteChooseCard7);
		activeSprites.add(spriteChooseCard7);
		registerTouchArea(spriteChooseCard7);
		//
		spriteChooseCard8 = new Sprite(0, 0, regionOwnCard8.deepCopy(), rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				popupCardPick = 8;
				hidePopupCardPick();
				return true;
			}
		};
		spriteChooseCard8.setScale(rm.SCALE_CAMERA_WIDTH,
				rm.SCALE_CAMERA_HEIGHT);
		spriteChooseCard8.setScaleCenter(0, 0);
		spriteChooseCard8.setSize(spriteChooseCard8.getWidthScaled()
				* (float) 0.727, spriteChooseCard8.getHeightScaled()
				* (float) 0.727);

		spriteChooseCard8.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 3.717)
						+ (spriteChooseCard1.getWidth() * 3)
						+ ((spriteChooseCard1.getWidth() / 7) * 3)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.980)));

		attachChild(spriteChooseCard8);
		activeSprites.add(spriteChooseCard8);
		registerTouchArea(spriteChooseCard8);

	}

	void hidePopupCardPick() {
		detachChild(spritePopUp);
		unregisterTouchArea(spriteChooseCard1);
		unregisterTouchArea(spriteChooseCard2);
		unregisterTouchArea(spriteChooseCard3);
		unregisterTouchArea(spriteChooseCard4);
		unregisterTouchArea(spriteChooseCard5);
		unregisterTouchArea(spriteChooseCard6);
		unregisterTouchArea(spriteChooseCard7);
		unregisterTouchArea(spriteChooseCard8);
		detachChild(spriteChooseCard1);
		detachChild(spriteChooseCard2);
		detachChild(spriteChooseCard3);
		detachChild(spriteChooseCard4);
		detachChild(spriteChooseCard5);
		detachChild(spriteChooseCard6);
		detachChild(spriteChooseCard7);
		detachChild(spriteChooseCard8);
		showHightLights(false);
	}

	void playCard() {
		switch (cardPick) {
		case 1:
			showPopupCardPick();
			break;
		case 5:
			showHightLights(true);
			break;
		case 4:
			endPlayCard();
			break;
		case 7:
			endPlayCard();
			break;
		case 8:
			endPlayCard();
			break;
		default:
			showHightLights(false);
			break;
		}
	}

	void endPlayCard() {
		switch (cardPick) {
		case 1:
			// pick card
			mGame.playCard1(mPlayer, playerPick, popupCardPick);
			break;
		case 2:
			mGame.playCard2(mPlayer, playerPick);
			break;
		// showcards
		case 3:
			mGame.playCard3(mPlayer, playerPick);
			break;
		// i won?
		case 4:
			mGame.playCard4(mPlayer);
			break;
		case 5:
			mGame.playCard5Victim(mPlayer, playerPick);
			break;
		case 6:
			mGame.playCard6(mPlayer, playerPick);
			break;
		case 7:
			mGame.playCard7(mPlayer);
			break;
		case 8:
			mGame.playCard8(mPlayer);
			break;
		}
		playerPick = -255;
		popupCardPick = 0;
		cardPick = 0;

		drawOwnCards();
		doneDrawOwnCards();
		canInput = false;
		ReplayPlayer beforePlayer = new ReplayPlayer(mPlayer.clientName,mPlayer.isOutOfRound(), mPlayer.isProtectedForRound(), mGame.getRound(), mGame.getPlayerPoint(mPlayer.getClientName()));
		ReplayPlayer beforeEnemy = new ReplayPlayer(mGame.getPlayerClientNameByNr(playerPick), mGame.isPlayerProtectedExplicit(mGame.getPlayerClientNameByNr(playerPick)), mGame.isPlayerOutOfRoundExplicit(mGame.getPlayerClientNameByNr(playerPick)), mGame.getRound(), mGame.getPlayerPoint(mGame.getPlayerClientNameByNr(playerPick)));
    	ReplayPlayer afterPlayer = new ReplayPlayer(mPlayer.clientName,mPlayer.isOutOfRound(), mPlayer.isProtectedForRound(), mGame.getRound(), mGame.getPlayerPoint(mPlayer.getClientName()));
    	ReplayPlayer afterEnemy = new ReplayPlayer(mGame.getPlayerClientNameByNr(playerPick), mGame.isPlayerProtectedExplicit(mGame.getPlayerClientNameByNr(playerPick)), mGame.isPlayerOutOfRoundExplicit(mGame.getPlayerClientNameByNr(playerPick)), mGame.getRound(), mGame.getPlayerPoint(mGame.getPlayerClientNameByNr(playerPick)));        
    	mGame.addPlay(new Play(mGame.getNextPlayNr(), mPlayer.clientName, 1,mGame.getPlayerClientNameByNr(playerPick), beforePlayer, beforeEnemy, afterPlayer, afterEnemy));
		mGame.endTurn(mPlayer);
	}

	void hideHightLights() {
		unregisterTouchArea(spriteHLOwn);
		unregisterTouchArea(spriteHLLeft);
		unregisterTouchArea(spriteHLRight);
		unregisterTouchArea(spriteHLtop);
		detachChild(spriteHLOwn);
		detachChild(spriteHLLeft);
		detachChild(spriteHLRight);
		detachChild(spriteHLtop);
	}

	void showHightLights(boolean selfAllowed) {

		if (selfAllowed) {
			// highlightOwn
			spriteHLOwn = new Sprite(0, 0, regionHighLightCard.deepCopy(),
					rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						playerPick = playerSelf;
						hideHightLights();
						endPlayCard();
					}

					return true;
				}
			};
			spriteHLOwn.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
			spriteHLOwn.setScaleCenter(0, 0);
			spriteHLOwn.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 2.933)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.689)));
			attachChild(spriteHLOwn);
			activeSprites.add(spriteHLOwn);
			registerTouchArea(spriteHLOwn);

		}
		if (!mGame.isPlayerProtected(playerLeft)) {
			// highlightLeft
			spriteHLLeft = new Sprite(0, 0, regionHighLightCard.deepCopy(),
					rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						playerPick = playerLeft;
						hideHightLights();
						endPlayCard();
					}

					return true;
				}
			};
			spriteHLLeft
					.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
			spriteHLLeft.setScaleCenter(0, 0);
			spriteHLLeft.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / -7.692

					)), Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 6.061

					)));
			spriteHLLeft.setRotation(-90f);
			attachChild(spriteHLLeft);
			activeSprites.add(spriteHLLeft);
			registerTouchArea(spriteHLLeft);
		}

		if (!mGame.isPlayerProtected(playerRight)) {

			// highlightRight
			spriteHLRight = new Sprite(0, 0, regionHighLightCard.deepCopy(),
					rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						playerPick = playerRight;
						hideHightLights();
						endPlayCard();
					}
					return true;
				}
			};
			spriteHLRight.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteHLRight.setScaleCenter(0, 0);
			spriteHLRight.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.335

					)), Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 3.096

					)));
			spriteHLRight.setRotation(90f);
			attachChild(spriteHLRight);
			activeSprites.add(spriteHLRight);
			registerTouchArea(spriteHLRight);
		}
		if (!mGame.isPlayerProtected(playerTop)) {

			// highlightTop
			spriteHLtop = new Sprite(0, 0, regionHighLightCard.deepCopy(),
					rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						playerPick = playerTop;
						hideHightLights();
						endPlayCard();
					}
					return true;
				}
			};
			spriteHLtop.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
			spriteHLtop.setScaleCenter(0, 0);
			spriteHLtop.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 2.551

					)), Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / -4.386

					)));

			attachChild(spriteHLtop);
			activeSprites.add(spriteHLtop);
			registerTouchArea(spriteHLtop);

		}
	}

	void doneDrawOwnCards() {
		unregisterTouchArea(spriteOwnCardLeft);
		unregisterTouchArea(spriteOwnCardRight);
	}

	void drawOwnCards() {
		updateCards();
		if (ownCards.size() == 1) {
			detachChild(spriteOwnCardLeft);
			spriteOwnCardLeft = new Sprite(0, 0, getCardRegionOnNumber(ownCards
					.get(0).getPower()), rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						cardPick = ownCards.get(0).getPower();
						doneDrawOwnCards();
						ownCards.remove(0);
						playCard();

					}
					return true;
				}
			};
			spriteOwnCardLeft.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteOwnCardLeft.setScaleCenter(0, 0);
			spriteOwnCardLeft.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 2.545)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.531)));
			attachChild(spriteOwnCardLeft);
			registerTouchArea(spriteOwnCardLeft);

			detachChild(spriteOwnCardRight);
			spriteOwnCardRight = new Sprite(0, 0, regionOwnCard0, rm.vbom);
			spriteOwnCardRight.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteOwnCardRight.setScaleCenter(0, 0);
			spriteOwnCardRight.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.848)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.531)));
			attachChild(spriteOwnCardRight);

		} else if (ownCards.size() == 2) {
			detachChild(spriteOwnCardLeft);
			spriteOwnCardLeft = new Sprite(0, 0, getCardRegionOnNumber(ownCards
					.get(0).getPower()), rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						cardPick = ownCards.get(0).getPower();
						doneDrawOwnCards();
						ownCards.remove(0);
						playCard();

					}
					return true;
				}
			};
			spriteOwnCardLeft.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteOwnCardLeft.setScaleCenter(0, 0);
			spriteOwnCardLeft.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 2.545)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.531)));
			attachChild(spriteOwnCardLeft);
			registerTouchArea(spriteOwnCardLeft);

			detachChild(spriteOwnCardRight);
			spriteOwnCardRight = new Sprite(0, 0,
					getCardRegionOnNumber(ownCards.get(1).getPower()), rm.vbom) {
				@Override
				public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
						float pTouchAreaLocalX, float pTouchAreaLocalY) {
					if (pSceneTouchEvent.isActionUp()) {
						cardPick = ownCards.get(1).getPower();
						doneDrawOwnCards();
						ownCards.remove(1);
						playCard();
					}
					return true;
				}
			};
			spriteOwnCardRight.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteOwnCardRight.setScaleCenter(0, 0);
			spriteOwnCardRight.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.848)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.531)));
			attachChild(spriteOwnCardRight);
			registerTouchArea(spriteOwnCardRight);

		} else {
			detachChild(spriteOwnCardLeft);
			spriteOwnCardLeft = new Sprite(0, 0, regionOwnCard0, rm.vbom);
			spriteOwnCardLeft.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteOwnCardLeft.setScaleCenter(0, 0);
			spriteOwnCardLeft.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 2.545)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.531)));
			attachChild(spriteOwnCardLeft);

			detachChild(spriteOwnCardRight);
			spriteOwnCardRight = new Sprite(0, 0, regionOwnCard0, rm.vbom);
			spriteOwnCardRight.setScale(rm.SCALE_CAMERA_WIDTH,
					rm.SCALE_CAMERA_HEIGHT);
			spriteOwnCardRight.setScaleCenter(0, 0);
			spriteOwnCardRight.setPosition(
					Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.848)),
					Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 1.531)));
			attachChild(spriteOwnCardRight);
		}

	}

	ITiledTextureRegion getCardRegionOnNumber(int i) {
		for (ITiledTextureRegion currRegion : activeRegions) {
			switch (i) {
			case 0:
				if (currRegion.equals(regionOwnCard0)) {
					return currRegion;
				}
			case 1:
				if (currRegion.equals(regionOwnCard1)) {
					return currRegion;
				}
			case 2:
				if (currRegion.equals(regionOwnCard2)) {
					return currRegion;
				}
			case 3:
				if (currRegion.equals(regionOwnCard3)) {
					return currRegion;
				}
			case 4:
				if (currRegion.equals(regionOwnCard4)) {
					return currRegion;
				}
			case 5:
				if (currRegion.equals(regionOwnCard5)) {
					return currRegion;
				}
			case 6:
				if (currRegion.equals(regionOwnCard6)) {
					return currRegion;
				}
			case 7:
				if (currRegion.equals(regionOwnCard7)) {
					return currRegion;
				}
			case 8:
				if (currRegion.equals(regionOwnCard8)) {
					return currRegion;
				}
			}
		}
		return regionOwnCard0;

	}

	void hideSprite(Sprite sprite) {
		for (Sprite currSprite : activeSprites) {
			if (currSprite.equals(sprite)) {
				currSprite.setVisible(false);
				if (currSprite.getTag() == HASTOUCH) {
					unregisterTouchArea(currSprite);
				}

			}
		}
	}

	void showSprite(Sprite sprite) {
		for (Sprite currSprite : activeSprites) {
			if (currSprite.equals(sprite)) {
				currSprite.setVisible(true);
				if (currSprite.getTag() == HASTOUCH) {
					registerTouchArea(currSprite);
				}

			}
		}
	}

	void showEndScreen() {
		showingEndScreen = true;
		STATE = 5;
		spritePopUp = new Sprite(0, 0, regionPopUp, rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {
					//
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};
		spritePopUp.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		spritePopUp.setScaleCenter(0, 0);
		attachChild(spritePopUp);
		registerTouchArea(spritePopUp);
		// activeSprites.add(spritePopUp);

		textNewRound = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textNewRound.setHorizontalAlign(HorizontalAlign.CENTER);
		textNewRound.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textNewRound.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.605)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 33.333)));

		textNewRound.setText(mGame.getScoreBoard() + "EINDE");
		attachChild(textNewRound);
	}

	private void endAnimation() {
		runningAnimation = null;
		detachChild(spriteCardAnimation);
		animationCurrTime = 0;
		animationQueue.remove(0);
		if (!animationQueue.isEmpty()) {
			Debug.i(animationQueue.size() + "");
			drawAnimation();
			startAnimation();
		} else {
			if (mGame.getRound() > currRound) {
				animationRunning = false;
				showNewRound();
			} else {
				canInput = true;
			}
		}
	}

	private void drawAnimation() {
		spriteCardAnimation = animationQueue.get(0).getSpriteAnimation();

		attachChild(spriteCardAnimation);
		runningAnimation = animationQueue.get(0);
	}

	private void initAnimations() {
		for (Play mPlay : plays) {
			Sprite s = new Sprite(0, 0,
					getCardRegionOnNumber(mPlay.getPlayedCard()), rm.vbom);
			s.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
			s.setScaleCenter(0, 0);
			PointF p = getPointForAnimation(getPositionByNr(mGame
					.getNrByName(mPlay.getClientName())));
			s.setPosition(p.x, p.y);
			addAnimation(getPositionByNr(mGame.getNrByName(mPlay
					.getBeforePlayer().getClientname())), "Mid", s,
					mPlay.getBeforePlayer(), mPlay.getBeforeEnemy());

			Sprite s2 = new Sprite(0, 0,
					getCardRegionOnNumber(mPlay.getPlayedCard()), rm.vbom);
			s2.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
			s2.setScaleCenter(0, 0);
			PointF p2 = getPointForAnimation(getPositionByNr(mGame
					.getNrByName(mPlay.getClientName())));
			s2.setPosition(p2.x, p2.y);
			addAnimation("Mid", getPositionByNr(mGame.getNrByName(mPlay
					.getAfterEnemy().getClientname())), s2,
					mPlay.getAfterPlayer(), mPlay.getAfterEnemy());
			playNr++;
		}
		plays.clear();
		getNextAnimation();
	}

	private void showNewRound() {
		STATE = 5;
		spritePopUp = new Sprite(0, 0, regionPopUp, rm.vbom) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.isActionUp()) {

					hideNewRound();
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};

		spritePopUp.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		spritePopUp.setScaleCenter(0, 0);
		attachChild(spritePopUp);
		registerTouchArea(spritePopUp);
		// activeSprites.add(spritePopUp);

		textNewRound = new Text(0, 0, this.mFont, "TEST FILLER BIG TEXT",
				rm.vbom);
		textNewRound.setHorizontalAlign(HorizontalAlign.CENTER);
		textNewRound.setScale(rm.SCALE_CAMERA_WIDTH, rm.SCALE_CAMERA_HEIGHT);
		textNewRound.setPosition(
				Float.valueOf((float) (rm.SCALED_CAMERA_WIDTH / 1.605)),
				Float.valueOf((float) (rm.SCALED_CAMERA_HEIGHT / 33.333)));

		textNewRound.setText(mGame.getScoreBoard());
		attachChild(textNewRound);

	}

	private void hideNewRound() {
		detachChild(spritePopUp);
		unregisterTouchArea(spritePopUp);
		detachChild(textNewRound);
		currRound = mGame.getRound();
		drawCardHighlightIsShown = false;
		canInput = true;
		// updateText();
	}

	private void startAnimation() {

		if (runningAnimation.getAnimationId() == 1) {
			modifierX = runningAnimation.getxModifier();
			modifierX
					.addModifierListener(new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {

						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {
							// TODO Auto-generated method stub
							updateUiWithAnimation(runningAnimation);
							runningAnimation = null;
							animationQueue.remove(0);
							getNextAnimation();

						}
					});
			spriteCardAnimation.registerEntityModifier(modifierX);

		} else {
			modifierY = runningAnimation.getyModifier();
			runningAnimation.getyModifier().addModifierListener(
					new IEntityModifier.IEntityModifierListener() {

						@Override
						public void onModifierStarted(
								IModifier<IEntity> pModifier, IEntity pItem) {

						}

						@Override
						public void onModifierFinished(
								IModifier<IEntity> pModifier, IEntity pItem) {
							// TODO Auto-generated method stub
							runningAnimation = null;
							animationQueue.remove(0);
							getNextAnimation();

						}
					});
			spriteCardAnimation.registerEntityModifier(modifierY);
		}
		attachChild(spriteCardAnimation);

	}

	void getNextAnimation() {
		animationRunning = true;
		detachChild(spriteCardAnimation);
		if (!animationQueue.isEmpty()) {
			runningAnimation = animationQueue.get(0);
			spriteCardAnimation = runningAnimation.getSpriteAnimation();
			spriteCardAnimation.setPosition(runningAnimation.getBeginPoint().x,
					runningAnimation.getBeginPoint().y);
			startAnimation();
		} else {
			animationRunning = false;
			drawCardHighlightIsShown = false;
			if (mGame.getRound() > currRound) {

				showNewRound();
			} else {
				canInput = true;
			}
		}
	}

	void addAnimation(String begin, String end, Sprite s, ReplayPlayer player,
			ReplayPlayer enemy) {
		float animationSpeed = 1f;
		Animation tempAnimation = null;
		if (begin == "Left" && end == "Mid") {
			tempAnimation = new Animation(1, "LeftToMid", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		} else if (begin == "Right" && end == "Mid") {
			tempAnimation = new Animation(1, "RightToMid", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		} else if (begin == "Top" && end == "Mid") {
			tempAnimation = new Animation(2, "TopToMid", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		} else if (begin == "Mid" && end == "Left") {
			tempAnimation = new Animation(1, "MidToLeft", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		} else if (begin == "Mid" && end == "Right") {
			tempAnimation = new Animation(1, "MidToRight", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		} else if (begin == "Mid" && end == "Top") {
			tempAnimation = new Animation(2, "MidToTop", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		} else if (begin == "Mid" && end == "Bot") {
			tempAnimation = new Animation(2, "MidToBot", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		}else if(begin == "Bot" && end == "Mid")
		{
			tempAnimation = new Animation(2, "BotToMid", animationSpeed,
					getPointForAnimation(begin), getPointForAnimation(end), s,
					player, enemy);
		}
		tempAnimation.setModifier();
		animationQueue.add(tempAnimation);
	}

	String getPositionByNr(int i) {
		if (playerLeft == i) {
			return "Left";
		} else if (playerTop == i) {
			return "Top";
		} else if (playerRight == i) {
			return "Right";
		} else if (playerSelf == i) {
			return "Bot";
		}
		return null;
	}

	private PointF getPointForAnimation(String s) {
		PointF p = new PointF();
		if (s.equals("Left")) {
			p.x = (1);
			p.y = (float) (rm.SCALED_CAMERA_HEIGHT / 3.937);
		} else if (s.equals("Right")) {
			p.x = rm.SCALED_CAMERA_WIDTH;
			p.y = (float) (rm.SCALED_CAMERA_HEIGHT / 3.937);
		} else if (s.equals("Top")) {
			p.x = (float) (rm.SCALED_CAMERA_WIDTH / 1.931);
			p.y = 0;
		} else if (s.equals("Mid")) {
			p.x = (float) (rm.SCALED_CAMERA_WIDTH / 1.931);
			p.y = (float) (rm.SCALED_CAMERA_HEIGHT / 3.937);
		} else if (s.equals("Bot")) {
			p.x = (float) (rm.SCALED_CAMERA_WIDTH / 1.931);
			p.y = rm.SCALED_CAMERA_HEIGHT;
		}

		return p;
	}

	float calculateFloatBetween(float begin, float end) {

		float inBetween;
		if (end > begin) {
			inBetween = end - begin;
			return inBetween;
		} else {
			inBetween = begin - end;
			return inBetween;
		}
	}

	Sprite getSpriteOnSprite(Sprite s) {
		for (Sprite mSprite : activeSprites) {
			if (s.equals(mSprite)) {
				return mSprite;
			}
		}
		return null;
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		// Debug.i("test");

		if (canInput) {
			if (!drawCardHighlightIsShown) {
				showDrawCardHighlight();
			}
		}
		if (!animationRunning && !mGame.isGameStarted()) {
			showEndScreen();
		}
		/*
		 * 
		 * if (!animationRunning) { if (mGame.getRound() > currRound && STATE !=
		 * 5) { currRound = mGame.getRound(); showNewRound(); }else if
		 * (mGame.isMyTurn(mPlayer) && mGame.getRound() == currRound &&
		 * !drawCardHighlightIsShown) { // TODO Auto-generated method stub
		 * showDrawCardHighlight(); } else if (!mGame.isGameStarted()) {
		 * showEndScreen(); } }
		 */
		super.onManagedUpdate(pSecondsElapsed);
	}

	@Override
	public void unloadResources() {
		for (BuildableBitmapTextureAtlas currTexture : activeTextures) {
			currTexture.unload();
		}
	}

	@Override
	public void destroy() {
		for (Sprite currSprite : activeSprites) {
			detachChild(currSprite);
			unregisterTouchArea(currSprite);
		}
	}

	@Override
	public void onBackKeyPressed() {
		sm.showScene(MenuScene.class);
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
