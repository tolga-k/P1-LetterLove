package com.pirateletter.game;

import java.io.Serializable;

public class Card implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5181406573852259079L;
	int power;
	String description;
	
	public Card(int p,String desc) {
		power = p;
		description = desc;
		
	}
	
	public Card(Card c)
	{
		power = c.power;
		description = c.description;
	}
	
	public int getPower()
	{
		return power;
	}

    @Override
    public String toString() {
        return "Card{" + "power=" + power + ", description=" + description + '}';
    }
        
        
        
}
