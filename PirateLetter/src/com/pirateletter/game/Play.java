package com.pirateletter.game;

public class Play {
	private int playNr;
	String clientName;
	int playedCard;
	String toPlayedPlayer;
	//before
	ReplayPlayer beforePlayer;
	ReplayPlayer beforeEnemy;
	//after
	ReplayPlayer afterPlayer;
	ReplayPlayer afterEnemy;
	public Play(int playNr, String clientName, int playedCard,
			String toPlayedPlayer, ReplayPlayer beforePlayer,
			ReplayPlayer beforeEnemy, ReplayPlayer afterPlayer,
			ReplayPlayer afterEnemy) {
		super();
		this.playNr = playNr;
		this.clientName = clientName;
		this.playedCard = playedCard;
		this.toPlayedPlayer = toPlayedPlayer;
		this.beforePlayer = beforePlayer;
		this.beforeEnemy = beforeEnemy;
		this.afterPlayer = afterPlayer;
		this.afterEnemy = afterEnemy;
	}
	public ReplayPlayer getAfterPlayer() {
		return afterPlayer;
	}
	public void setAfterPlayer(ReplayPlayer afterPlayer) {
		this.afterPlayer = afterPlayer;
	}
	public ReplayPlayer getAfterEnemy() {
		return afterEnemy;
	}
	public void setAfterEnemy(ReplayPlayer afterEnemy) {
		this.afterEnemy = afterEnemy;
	}
	public int getPlayNr() {
		return playNr;
	}
	public String getClientName() {
		return clientName;
	}
	public int getPlayedCard() {
		return playedCard;
	}
	public String getToPlayedPlayer() {
		return toPlayedPlayer;
	}
	public ReplayPlayer getBeforePlayer() {
		return beforePlayer;
	}
	public ReplayPlayer getBeforeEnemy() {
		return beforeEnemy;
	}
	
	
	
}
