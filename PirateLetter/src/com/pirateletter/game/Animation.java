package com.pirateletter.game;

import org.andengine.entity.modifier.EntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.ease.EaseQuartIn;
import org.andengine.util.modifier.ease.IEaseFunction;

import android.graphics.PointF;

public class Animation {
	
	IEaseFunction easing = EaseQuartIn.getInstance();
	//
	int animationId;
	String animationName;
	float duration;
	PointF beginPoint;
	PointF endPoint;
	Sprite spriteAnimation;
	MoveXModifier xModifier;
	MoveYModifier yModifier;
	ReplayPlayer player;
	ReplayPlayer enemy;
	
	
	
	public Animation(int animationId, String animationName, float duration,
			PointF beginPoint, PointF endPoint, Sprite spriteAnimation,
			ReplayPlayer player, ReplayPlayer enemy) {
		super();
		this.animationId = animationId;
		this.animationName = animationName;
		this.duration = duration;
		this.beginPoint = beginPoint;
		this.endPoint = endPoint;
		this.spriteAnimation = spriteAnimation;
		this.player = player;
		this.enemy = enemy;
	}
	public int getAnimationId() {
		return animationId;
	}
	public String getAnimationName() {
		return animationName;
	}
	public float getDuration() {
		return duration;
	}
	public PointF getBeginPoint() {
		return beginPoint;
	}
	public PointF getEndPoint() {
		return endPoint;
	}
	public Sprite getSpriteAnimation() {
		return spriteAnimation;
	}
	public MoveXModifier getxModifier() {
		return xModifier;
	}
	public MoveYModifier getyModifier() {
		return yModifier;
	}
	public ReplayPlayer getPlayer() {
		return player;
	}
	public ReplayPlayer getEnemy() {
		return enemy;
	}

	void initXmodifier()
	{
		xModifier = new MoveXModifier(duration, beginPoint.x, endPoint.x, easing);
	}
	
	void initYmodifier()
	{
		yModifier = new MoveYModifier(duration, beginPoint.y, endPoint.y, easing);
	}
	
	public void setModifier()
	{
		if (animationId == 1) {
			initXmodifier();
		}else
		{
			initYmodifier();
		}
	}
	
	
	
	
	
	
}
