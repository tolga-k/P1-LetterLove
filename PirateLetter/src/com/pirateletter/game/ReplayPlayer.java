package com.pirateletter.game;

public class ReplayPlayer {
	
	String clientname;
	boolean outOfRound;
	boolean protectedForFound;
	int round;
	int points;
	
	public ReplayPlayer(String clientname, boolean outOfRound,
			boolean protectedForFound, int round, int points) {
		super();
		this.clientname = clientname;
		this.outOfRound = outOfRound;
		this.protectedForFound = protectedForFound;
		this.round = round;
		this.points = points;
	}

	public String getClientname() {
		return clientname;
	}

	public boolean isOutOfRound() {
		return outOfRound;
	}

	public boolean isProtectedForFound() {
		return protectedForFound;
	}

	public int getRound() {
		return round;
	}

	public int getPoints() {
		return points;
	}
	
	
}
