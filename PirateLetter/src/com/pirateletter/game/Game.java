package com.pirateletter.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.andengine.engine.handler.runnable.RunnableHandler;
import org.apache.http.entity.SerializableEntity;

import com.pirateletter.scene.PlayerImpl;

/*
 * 16 cards
 * token
 * reference method
 * random start
 * 
 * 
 * end = highest card nr wins
 * 
 * 2 players = 7 tokens
 * 3 players = 5 tokens
 * 4 players = 4 tokens
 * 
 * 
 * 8: Highest, if you lose you lose.
 * 7: if you have 6/5 you must discard, no effects
 * 6: trade card w/ player, else do nothing
 * 5: discard all cards, pick a player.Else pick self.
 * 4: Protected for 1 round
 * 3: compare cards with other one, in a draw = nothing
 * 2: look to other player cards
 * 1: choose player + card (can't choose guard), if right out for 1 round
 *   
 */
public class Game implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7690998945126382455L;
	private static final Game INSTANCE = new Game();
    private final static Logger LOGGER = Logger.getLogger(Game.class.getName()); 
    // static cards
    List<Card> staticCards = new ArrayList<Card>();
    int MAX_PLAYERS = 4;
    int ROUND_END_MAX = 4;
    // dynamic cards
    List<Card> deck = new ArrayList<Card>();
    List<Card> discarded = new ArrayList<Card>();
    public Map<Player, Integer> scoreboard = new Hashtable<Player, Integer>();

    
    // current players, cant change
    List<Player> players = new ArrayList<Player>();
    int currPlayerTurn = 0;
    int lastWonPlayer = 0;
    int currRound = 0;
    private boolean gameStarted = false;
    
	Player p2 = new PlayerImpl();
    Player p3 = new PlayerImpl();
    Player p4 = new PlayerImpl();
    
    int amountPlays;
    List<Play> plays = new ArrayList<Play>();
    
    public boolean isGameStarted() {
        return gameStarted;
    }
    
    public static Game getInstance()
    {
    	return INSTANCE;
    }

    public Game() {
        // TODO Auto-generated constructor stub
        staticCards.add(new Card(1, "1"));
        staticCards.add(new Card(2, "2"));
        staticCards.add(new Card(3, "3"));
        staticCards.add(new Card(4, "4"));
        staticCards.add(new Card(5, "5"));
        staticCards.add(new Card(6, "6"));
        staticCards.add(new Card(7, "7"));
        staticCards.add(new Card(8, "8"));
        
    }

    // shuffle and deal cards
    public void startGame() {
        deck = new ArrayList<Card>();

        // 5x 1
        deck.add(staticCards.get(0));
        deck.add(staticCards.get(0));
        deck.add(staticCards.get(0));
        deck.add(staticCards.get(0));
        deck.add(staticCards.get(0));
        // 2x 2
        deck.add(staticCards.get(1));
        deck.add(staticCards.get(1));
        // 2x 3
        deck.add(staticCards.get(2));
        deck.add(staticCards.get(2));
        // 2x 4
        deck.add(staticCards.get(3));
        deck.add(staticCards.get(3));
        // 2x 5
        deck.add(staticCards.get(4));
        deck.add(staticCards.get(4));
        // 1x 6
        deck.add(staticCards.get(5));
        // 1x 7
        deck.add(staticCards.get(6));
        // 1x 8
        deck.add(staticCards.get(7));
        // shuffling cards
        shuffleCards();
        
        currPlayerTurn = 0;
        gameStarted = true;
        //deck.remove(0);
    }

    public boolean addPlayer(Player p) {
        if (players.size() < MAX_PLAYERS && !gameStarted) {
            players.add(p);
            scoreboard.put(p, 0);
            return true;
        }
        return false;
    }

    public void startNewRound() {

        for (Player player : players) {
            player.currCards = new ArrayList<Card>();
            player.playedCards = new ArrayList<Card>();
            player.outOfRound = false;
            player.protectedForRound = false;
        }
        startGame();
        for (Player player : players) {
            Card c = deck.get(0);
            player.currCards.add(c);
            deck.remove(0);
        }
        if (isThereAWinner()) {
            System.out.println("================ "+getWinner().clientName + "===============");
            gameStarted = false;
        } else {
            currRound++;
            LOGGER.log(Level.OFF, "*************** NEW ROUND ****************{0}", currRound);
            //System.out.println();
            for (Map.Entry<Player, Integer> entry : scoreboard.entrySet()) {
                System.out.println(entry.getKey().clientName + ": score = " + entry.getValue());
            }
            if (currRound == 1) {
                players.get(currPlayerTurn).turnMessage();
            }
        }

    }

    private boolean isThereAWinner() {
        for (Map.Entry<Player, Integer> entry : scoreboard.entrySet()) {
            if (entry.getValue() == ROUND_END_MAX) {
                return true;
            }
        }
        return false;
    }

    private Player getWinner() {
        for (Map.Entry<Player, Integer> entry : scoreboard.entrySet()) {
            if (entry.getValue() == ROUND_END_MAX) {
                return entry.getKey();
            }

        }
        return null;
    }

    private void checkWinner() {
        Player highestPlayerCard = null;
        for (Player p : players) {
            if (!p.outOfRound) {
                if (null == highestPlayerCard || hasHighestCard(p, highestPlayerCard)) {
                    if (highestPlayerCard != null) {
                        highestPlayerCard.outOfRound = true;
                    }
                    highestPlayerCard = p;

                } else {
                    p.outOfRound = true;
                }
            }
        }
        if (isEndOfRound()) {
            for (Map.Entry<Player, Integer> entry : scoreboard.entrySet()) {
                if (entry.getKey().equals(highestPlayerCard)) {
                    entry.setValue(entry.getValue() + 1);
                }
            }
            startNewRound();
        }
    }

    private boolean isEndOfRound() {
        int max = MAX_PLAYERS;
        for (Player p : players) {
            if (p.outOfRound) {
                max--;
            }
        }
        return max == 1;
    }

    private boolean hasHighestCard(Player p, Player p2) {
        return getHighestCard(p) > getHighestCard(p2);
    }

    private int getHighestCard(Player p) {
        int highestCard = 0;
        for (Card card : p.currCards) {
            if (highestCard < card.power) {
                highestCard = card.power;
            }
        }
        return highestCard;
    }

    public boolean amIAlone(Player p)
    {
        for (Player player : players) {
            if (!p.equals(player) && !player.outOfRound) {
                return false;
            }
        }
        return true;
    }
    
    public Card drawCard(Player p) {
        if (deck.size() <= 1) {
            checkWinner();
        }else if(isEndOfRound())
        {
        checkWinner();
        }else {
            if (isMyTurn(p) && p.currCards.size() == 1) {
                Card card = new Card(deck.get(0));
                deck.remove(0);
                p.currCards.add(card);
                return card;
            }
        }
        return null;
    }

    private void shuffleCards() {
   Collections.shuffle(deck);
   Collections.shuffle(deck);
   Collections.shuffle(deck);
    }

    public List<Card> getPlayerCards(Player p) {
        return p.currCards;
    }

    public List<Card> getPlayerDiscardedCards(Player p) {
        return p.playedCards;
    }

    public Card pickNewCard() {

        return null;
    }

    public boolean isAllProtected() {
        for (Player p : players) {
            if (p.protectedForRound && !p.outOfRound) {
                return true;
            }
        }
        return false;
    }

    public void nextNextPlayer() {
        if (currPlayerTurn == (players.size() - 1)) {
            removeProtection();
            currPlayerTurn = 0;
            
        } else {
            currPlayerTurn++;
        }
    }
    
    private void removeProtection()
    {
        for (Player player : players) {
            player.protectedForRound = false;
        }
    }

    public void nextPlayer() {
        nextNextPlayer();
       
        while (players.get(currPlayerTurn).outOfRound == true) {
            nextNextPlayer();
        }
        if (deck.size() <= 1) {
            checkWinner();
        }else if(isEndOfRound())
        {
        checkWinner();
        }
        if (gameStarted) {
            players.get(currPlayerTurn).turnMessage();
        }
        
    }
   
    
    public boolean isMyTurn(Player p) {
        return currPlayerTurn == getPlayerNr(p);
    }

    public boolean endTurn(Player p) {
    	
        if (currPlayerTurn == getPlayerNr(p)) {
            nextPlayer();
            return true;
        }
        return false;

    }

    private boolean guessCard(int i, int c) {

        for (Card temp_card : players.get(i).currCards) {
            if (temp_card.power == c) {
                return true;
            }
        }
        return false;
    }

    /*
     * 8: Highest, if you lose you lose.
     * 7: if you have 6/5 you must discard, no effects
     * 6: trade card w/ player, else do nothing
     * 5: discard all cards, pick a player.Else pick self.
     * 4: Protected for 1 round
     * 3: compare cards with other one, in a draw = nothing
     * 2: look to other player cards
     * 1: choose player + card (can't choose guard), if right out for 1 round
     *   
     *   
     *  return 
     *  0 = nothing happend
     *  1 = you won they lost
     *  2 = you lost;
     *  
     */
    /**
     * playCard8: Highest, if you lose you lose 1 round.
     *
     * @param p
     */
    public void playCard8(Player p) {
        Player player = getCurrPlayer(p);
        playerOutOfRound(player);
    }

    /**
     * playCard7: if you have 6/5 you must discard, no effects
     *
     * @param p you
     * @return 1 = you have 6/5 and you discarded 7, 0 = you dont have 6/5,
     * nothing happend.
     */
    public int playCard7(Player p) {
        Player player = getCurrPlayer(p);

        if (hasCard(player, 6) != null || hasCard(player, 5) != null) {
            discardCard(player, 7);
            return 1;
        }
        discardCard(player, 7);
        return 0;
    }

    /**
     * playCard6: trade card w/ player, else do nothing
     *
     * @param p you
     * @param victimId Id of the victim you want to attack
     * @return list of card you currently have
     */
    public List<Card> playCard6(Player p, int victimId) {
        Player player = getCurrPlayer(p);
        Player victim = getPlayer(victimId);

        discardCard(p, 6);
        if (victim.protectedForRound) {
            return null;
        }
        tradeCards(player, victim);
        return player.currCards;
    }

    /**
     * playCard5Victim: discard all cards, pick a player
     *
     * @param p you
     * @param victimId victimId of the victim you want to attack
     * @return 0 = protected for round, 1 = succesful
     */
    public int playCard5Victim(Player p, int victimId) {
        Player player = getCurrPlayer(p);
        Player victim = getPlayer(victimId);

        //discardCard(player, 5);
        if (victim.protectedForRound) {
            return 0;
        }

        discardCards(victim);
        return 1;
    }

    /**
     * playCard5Self: discard all cards.
     *
     * @param p you
     * @return list of current cards
     */
    public List<Card> playCard5Self(Player p) {
        Player player = getCurrPlayer(p);
        discardCards(p);
        return player.currCards;
    }

    /**
     * playCard4: Protected for 1 round
     *
     * @param p you
     * @return boolean
     */
    public boolean playCard4(Player p) {
        Player player = getCurrPlayer(p);
        discardCard(p, 4);
        return (player.protectedForRound = true);

    }

    /**
     * playCard 3: compare cards with other one, in a draw = nothing
     *
     * @param p you
     * @param victimid victimId you want to attack
     * @return 0 = the same card, nothing happens 1 = your card is higher,
     * player if out for 1 round 2 = victims card is higher, you are out for 1
     * round -1 = nothing happend, you lost card
     */
    public int playCard3(Player p, int victimid) {
        Player player = getCurrPlayer(p);
        Player victim = getPlayer(victimid);
        discardCard(player, 3);
        if (victim.protectedForRound) {
            return 0;
        }
        switch (compareCard(player, victim)) {
            //same
            case 0:
                return 0;
            //you win
            case 1:
                playerOutOfRound(victim);
                return 1;
            //you lost
            case 2:
                playerOutOfRound(player);
                return 2;
        }
        return -1;
    }

    /**
     * playCard2: look to other player cards
     *
     *
     * @param p you
     * @param victim victimId you want to attack
     * @return list of cards else null
     */
    public List<Card> playCard2(Player p, int victim) {
        Player victimPlayer = getPlayer(victim);
        Player userPlayer = getCurrPlayer(p);

        discardCard(userPlayer, 2);
        if (victimPlayer.protectedForRound) {
            return null;
        } else {
            return victimPlayer.currCards;
        }

    }

    /**
     * playCard1 = pick a victim with a card, if the guessed card is correct:
     *
     *
     * 1 = victim is out of round, you guessed correct 0 = nothing happend, you
     * guessed wrong -1 = victim is protected for round, nothing happend
     *
     *
     * @param p you
     * @param victimId id of victim you want to attack
     * @param selectedcardId which card power you are guessing the other player
     * has
     * @return
     */
    public int playCard1(Player p, int victimId, int selectedcardId) {
        Player player = getCurrPlayer(p);
        Player victim = getPlayer(victimId);

        discardCard(getCurrPlayer(p), 1);
        if (victim.protectedForRound) {

            return -1;
        }
        if (guessCard(victimId, selectedcardId)) {
            victim.outOfRound = true;

            //discardCard(player, 1);
            return 1;
        } else {
         // discardCard(player, 1);
            return 0;
        }

    }

    private Card hasCard(Player p, int cardPower) {
        for (Card card : p.currCards) {
            if (card.power == cardPower) {
                return card;
            }
        }
        return null;
    }

    private void switchCards(Player p1, Player p2) {
        List<Card> pc1 = new ArrayList<Card>(p1.currCards);
        List<Card> pc2 = new ArrayList<Card>(p2.currCards);
        p1.currCards = pc2;
        p2.currCards = pc1;
    }

    private void discardCards(Player p) {
        List<Card> pc1 = new ArrayList<Card>(p.currCards);
        discarded.addAll(pc1);
        p.currCards = new ArrayList<Card>();
        p.currCards.add(deck.get(0));
        deck.remove(0);
    }

    private void discardCard(Player p, int cardId) {
        boolean found = false;
        for (int i = 0; i < p.currCards.size(); i++) {
            if (p.currCards.get(i).power == cardId && found != true) {
                p.currCards.remove(i);
                drawCard(p);
                found = true;
            }
        }

    }

    private Player getCurrPlayer(Player p) {
        for (Player player : players) {
            if (player.equals(p)) {
                return player;
            }
        }
        return null;
    }

    // 1 = player 1
    // 2 = player 2
    // 0 = draw
    private int compareCards(Player p1, int p1Card, Player p2, int p2Card) {
        if (p1.currCards.get(p1Card - 1).power > p2.currCards.get(p2Card - 1).power) {
            return 1;
        } else if (p1.currCards.get(p1Card - 1).power < p2.currCards.get(p2Card - 1).power) {
            return 2;
        } else {
            return 0;
        }
    }

    private int compareCard(Player p1, Player p2) {
        if (p1.currCards.get(0).power > p2.currCards.get(0).power) {
            return 1;
        } else if (p1.currCards.get(0).power < p2.currCards.get(0).power) {
            return 2;
        } else {
            return 0;
        }
    }

    private void playerOutOfRound(Player p) {
        p.outOfRound = true;
    }

    private Player getPlayerbyName(String s) {
        for (Player player : players) {
            if (player.clientName.equals(s)) {
                return player;
            }
        }
        return null;
    }

    public int getPlayerNr(Player p) {
        for (int i = 0; i < players.size(); i++) {
            if (p.equals(players.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public boolean isPlayerProtected(int i) {
        if (players.get(i).outOfRound) {
            return true;
        }
        return players.get(i).protectedForRound;
    }
    
    public boolean isPlayerProtected(String name)
    {
    	if (getPlayerbyName(name).outOfRound) {
			return true;
		}
    	return getPlayerbyName(name).protectedForRound;
    }
    
    public boolean isPlayerProtectedExplicit(String name)
    {
    	return getPlayerbyName(name).protectedForRound;
    }
    
    public boolean isPlayerOutOfRoundExplicit(String name)
    {
    	return getPlayerbyName(name).outOfRound;
    }

    public int getPlayersSize() {
        return players.size();
    }

    private Player getPlayer(int i) {
        return players.get(i);
    }

    public Card getStaticCard(int i) {
        return null;

    }
    
    public int getPlayerPoint(String name)
    {
    	for (Player mPlayer : players) {
			if (name.equals(mPlayer.clientName)) {
				return scoreboard.get(mPlayer);
			}
		}
    	return 0;
    }

    private void tradeCards(Player p1, Player p2) {
        List<Card> p1Cards = p1.currCards;
        List<Card> p2Cards = p2.currCards;
        p1.currCards = p2Cards;
        p2.currCards = p1Cards;
    }
    
    public void startSinglePlayer()
    {
        p2 = new Cpu(this, p2);
        p2.clientName = "p2";
        p3 = new Cpu(this, p3);
        p3.clientName = "p3";
        p4 = new Cpu(this, p4);
        p4.clientName = "p4";
        addPlayer(p2);
        addPlayer(p3);
        addPlayer(p4);
    }
    
    public int getRound()
    {
    	return currRound;
    }
    
    public String getPlayerTurn()
    {
    	return getPlayer(currPlayerTurn).clientName;
    }

    public void addPlay(Play newPlay)
    {
    	plays.add(newPlay);
    	amountPlays++;
    }
    
    public int getNextPlayNr()
    {
    	return (amountPlays+1);
    }
    
    public String getPlayerClientNameByNr(int i)
    {
    	return getPlayer(i).clientName;
    }
    
    public List<Play> getRemainingPlays(int i)
    {
    	//int currentPlay = amountPlays - i;
    	List<Play> remainingPlays = new ArrayList<Play>();
    	if (amountPlays != i) {
			for (Play play : plays) {
				if (i < play.getPlayNr()) {
					remainingPlays.add(play);
				}
			}
    		return remainingPlays;
		}
    	return remainingPlays;
    }
    
    public int getNrByName(String s)
    {	
    	for (int i = 0; i < players.size(); i++) {
			if (players.get(i).clientName.equals(s)) {
				return i;
			}
		}
    	return -1;
    }
    
    public String getScoreBoard()
    {
    	StringBuilder s = new StringBuilder();
    	for (Player mPlayer : players) {
			s.append(mPlayer.clientName+":"+ scoreboard.get(mPlayer)+"\n");
		}
    	return s.toString();
    }

	@Override
	public void run() {
		
		
	}
    

}
