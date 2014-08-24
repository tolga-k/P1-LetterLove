/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pirateletter.game;

import java.io.IOException;

import com.pirateletter.scene.PlayerImpl;

/**
 *
 * @author Tolga
 */
public class Loveletter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException {

        Game game;
        Player p1 = new PlayerImpl();
        Player p2 = new PlayerImpl();
        Player p3 = new PlayerImpl();
        Player p4 = new PlayerImpl();
        game = new Game();
        p1 = new Cpu(game, p1);
        p1.clientName = "p1";
        p2 = new Cpu(game, p2);
        p2.clientName = "p2";
        p3 = new Cpu(game, p3);
        p3.clientName = "p3";
        p4 = new Cpu(game, p4);
        p4.clientName = "p4";
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);
        game.addPlayer(p4);
        game.startGame();
        game.startNewRound();
        System.out.println(p1.currCards.toString());
        System.out.println(p2.currCards.toString());
        System.out.println(p3.currCards.toString());
        System.out.println(p4.currCards.toString());
/*
        byte[] b = null;
        
        if (game.isMyTurn(p1)) {
            game.drawCard(p1);
            System.out.println("ja" + p1.currCards.toString());
            System.in.read();
            System.out.println(game.playCard2(p1, "p2").toString());
            game.endTurn(p1);
        } else {
            System.out.println("nope");
        }*/
        
        System.out.println(p1.outOfRound);
        System.out.println(p2.outOfRound);
        System.out.println(p3.outOfRound);
        System.out.println(p4.outOfRound);

    }

    

}
