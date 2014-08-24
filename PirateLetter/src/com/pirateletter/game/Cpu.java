/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pirateletter.game;

import java.util.Random;

/**
 *
 * @author Tolga
 */
public class Cpu extends Player {

    Game game;
    String enemyName;
    ReplayPlayer beforePlayer,beforeEnemy,afterPlayer,afterEnemy;
    Play p;
    public Cpu(Game game, Player p) {
        super(p);
        this.game = game;
    }

    public void startAI() {

    }

    private void playCard() {
        System.out.println("cpu playcard:" + super.clientName + this.currCards.toString());
        game.drawCard(this);
        System.out.println("cpu playcard:" + super.clientName + this.currCards.toString());
        Card c = currCards.get(new Random().nextInt(1));
        boolean played = false;
        switch (c.power) {
            case 1:
                played = false;

                for (int i = 0; i < game.getPlayersSize(); i++) {
                    if (!game.isPlayerProtected(i) && i != game.getPlayerNr(this)) {
                    	enemyName = game.getPlayerClientNameByNr(i);
                    	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	beforeEnemy = new ReplayPlayer(game.getPlayerClientNameByNr(i), game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));
                    	int randomint = new Random().nextInt(7)+1;
                    	game.playCard1(this, i, randomint);
                    	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                    	p = new Play(game.getNextPlayNr(), clientName, 1,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                    	game.addPlay(p);
                    	played = true;
                        break;
                    }
                }
                if (!played) {
                	enemyName = game.getPlayerClientNameByNr(0);
                	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                    game.playCard1(this, 0, 1);
                    afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                	p = new Play(game.getNextPlayNr(), clientName, 1, enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                	game.addPlay(p);
                
                }
                break;
            case 2:
                played = false;
                for (int i = 0; i < game.getPlayersSize(); i++) {
                    if (!game.isPlayerProtected(i)) {
                    	enemyName = game.getPlayerClientNameByNr(i);
                    	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                        game.playCard2(this, i);
                        afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                    	p = new Play(game.getNextPlayNr(), clientName, 2,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                    	game.addPlay(p);
                        played = true;
                        break;
                    }
                }
                if (!played) {
                	enemyName = clientName;
                	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                    game.playCard2(this, 0);
                    afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                	//System.out.println("played card 1 to " + i + " :" + game.playCard1(this, i, ));
                	p = new Play(game.getNextPlayNr(), clientName, 2,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                	game.addPlay(p);
                }
                break;
            case 3:
                played = false;
                for (int i = 0; i < game.getPlayersSize(); i++) {
                    if (!game.isPlayerProtected(i)) {
                    	enemyName = game.getPlayerClientNameByNr(i);
                    	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                       	game.playCard3(this, i);
                       	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                    	p = new Play(game.getNextPlayNr(), clientName, 3,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                    	game.addPlay(p);
                       	played = true;
                        break;
                    }
                }
                if (!played) {
                	enemyName = clientName;
                	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                    game.playCard3(this, 0);
                    afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                	p = new Play(game.getNextPlayNr(), clientName, 3,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                	game.addPlay(p);
                }
                break;
            case 4:
            	enemyName = clientName;
            	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
            	game.playCard4(this);
            	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
           		afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
           		p = new Play(game.getNextPlayNr(), clientName, 4,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
           		game.addPlay(p);
                break;
            case 5:
            	enemyName = clientName;
            	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
            	game.playCard5Self(this);
            	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
            	//System.out.println("played card 1 to " + i + " :" + game.playCard1(this, i, ));
            	p = new Play(game.getNextPlayNr(), clientName, 5,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
            	game.addPlay(p);
                break;
            case 6:
                played = false;
                for (int i = 0; i < game.getPlayersSize(); i++) {
                    if (!game.isPlayerProtected(i)) {
                    	enemyName = game.getPlayerClientNameByNr(i);
                    	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                    	game.playCard6(this, i);
                    	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                    	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                    	//System.out.println("played card 1 to " + i + " :" + game.playCard1(this, i, ));
                    	p = new Play(game.getNextPlayNr(), clientName, 6,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                    	game.addPlay(p);
                        played = true;
                        break;
                    }
                }
                if (!played) {
                	enemyName = game.getPlayerClientNameByNr(0);
                	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
                	game.playCard6(this, 0);
                	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
                	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
                	//System.out.println("played card 1 to " + i + " :" + game.playCard1(this, i, ));
                	p = new Play(game.getNextPlayNr(), clientName, 6,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
                	game.addPlay(p);
                }
                break;
            case 7:
            	enemyName = clientName;
            	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
            	game.playCard7(this);
            	afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
            	//System.out.println("played card 1 to " + i + " :" + game.playCard1(this, i, ));
            	p = new Play(game.getNextPlayNr(), clientName, 7,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
            	game.addPlay(p);
                break;

            case 8:
            	enemyName = clientName;
            	beforePlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	beforeEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));                	
            	//System.out.println("played card 8 : :(");
                game.playCard8(this);
                afterPlayer = new ReplayPlayer(clientName,outOfRound, protectedForRound, game.getRound(), game.getPlayerPoint(clientName));
            	afterEnemy = new ReplayPlayer(enemyName, game.isPlayerProtectedExplicit(enemyName), game.isPlayerOutOfRoundExplicit(enemyName), game.currRound, game.getPlayerPoint(enemyName));        
            	//System.out.println("played card 1 to " + i + " :" + game.playCard1(this, i, ));
            	p = new Play(game.getNextPlayNr(), clientName, 8,enemyName, beforePlayer, beforeEnemy, afterPlayer, afterEnemy);
            	game.addPlay(p);
                break;
        }
        game.endTurn(this);

    }

    @Override
    public void turnMessage() {
        
        //System.out.println("------Player turn:" + game.getPlayerNr(this));
        playCard();
    }

}
