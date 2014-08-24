package com.pirateletter.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Player implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3093086484998971243L;
	List<Card> currCards = new ArrayList<Card>();
    List<Card> playedCards = new ArrayList<Card>();
    public String clientName;

    boolean outOfRound = false;
    boolean protectedForRound = false;

    public Player() {
        // TODO Auto-generated constructor stub
    }

    public Player(Player p) {
        this.currCards = p.currCards;
        this.playedCards = p.playedCards;
        this.outOfRound = p.outOfRound;
        this.protectedForRound = p.protectedForRound;
        // TODO Auto-generated constructor stub
    }

    public List<Card> getCards(List<Card> c) {
        return null;
    }

    @Override
    public String toString() {
        return clientName; //To change body of generated methods, choose Tools | Templates.
    }
    
    abstract public void turnMessage();
    
}
