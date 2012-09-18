// Bauernkrieg.java

package ph.sm.bauernkrieg;

import android.graphics.Color;
import android.graphics.Point;
import ch.aplu.android.Location;
import ch.aplu.android.TextActor;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardGame;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;
import ch.aplu.jcardgame.Hand.SortType;
import ch.aplu.jcardgame.RowLayout;
import ch.aplu.jcardgame.StackLayout;
import ch.aplu.jcardgame.TargetArea;
import ch.aplu.util.Monitor;

public class Bauernkrieg extends CardGame {

	public enum Suit {
		KREUZ, HERZ, KARO, PIK
	}

	public enum Rank {
		ASS, KOENIG, DAME, BAUER, ZEHN, NEUN, ACHT, SIEBEN, SECHS
	}

	private Deck deck;
	private final int nbPlayers = 2;
	private final int nbCards = 18;
	private boolean blindRound;
	private final Location[] handLocations = { new Location(210, 440),
			new Location(390, 440), };
	private final Location[] bidLocations = { new Location(210, 200),
			new Location(390, 200), };
	private final Location[] stockLocations = { new Location(90, 400),
			new Location(510, 400), };
	private Hand[] hands;
	private Hand[] bids = new Hand[nbPlayers];
	private Hand[] stocks = new Hand[nbPlayers];
	private int currentPlayer = 0;

	public Bauernkrieg() {
		super(GREEN, TRANSPARENT, BoardType.HORZ_SQUARE,
				windowZoom(600));
	}

	public void main() {
		deck = new Deck(Suit.values(), Rank.values(), "cover");
		initBids();
		initStocks();
		initHands();
		showToast("Tap to play card" + ". Starting player: "
				+ ((currentPlayer == 0) ? "left" : "right"), true);
		
		/**
		 * This thread is used to evaluate the highest card & transfer all cards from the
		 * bid to the winners stock. 
		 * Additionally, it activates the touch listener for the winning player.
		 */
		while (true) {
			Monitor.putSleep();
			delay(2000);
			Hand eval = new Hand(deck);
			for (int i = 0; i < nbPlayers; i++)
				eval.insert(bids[i].getLast(), false);
			int nbWinner = eval.getMaxPosition(Hand.SortType.RANKPRIORITY);
			transferBidsToStock(nbWinner);
			currentPlayer = nbWinner;
			if (hands[currentPlayer].isEmpty()) {
				delay(2000);
				gameOver();
				}
			else {
				showToast("Current player: " + ((currentPlayer == 0) ? "left" : "right"));
				setTouchEnabled(true);
			}
		}
	}

	private int otherPlayer(int player) {
		return (player + 1) % nbPlayers;
	}

	/**
	 * Initializes the hands. Almost all functionality of the game is implemented
	 * here as CardAdapter. 
	 * TODO: refactor CardAdapter into own class
	 */
	private void initHands() {
		hands = deck.dealingOut(nbPlayers, nbCards);
		for (int i = 0; i < nbPlayers; i++) {
			//hands[i].sort(SortType.RANKPRIORITY, true); //for debugging "wars"
			hands[i].setView(this, new StackLayout(handLocations[i]));
			hands[i].setVerso(true);
			hands[i].setTouchEnabled(true);
			final int k = i;
			hands[i].addCardListener(new CardAdapter() {
				public void pressed(Card card) {
					if (card.getHand() != hands[currentPlayer])
						return;
					card.setVerso(false);
					card.transferNonBlocking(bids[k], true);
					currentPlayer = otherPlayer(currentPlayer);
					blindRound = false;
				}
				
				public void atTarget(Card card, Location loc) {
					if (allPlayersLaidCard() && !blindRound)
					{
						if (isSameRank()) {
							blindRound = true;
							if (hands[currentPlayer].isEmpty()) {
								gameOver();
								return;
							}
							for (int i = 0; i < nbPlayers; i++) {
								Card c = hands[i].getLast();
								c.transferNonBlocking(bids[i], true);
							}
							showToast("Same rank! Draw another card!");
							setTouchEnabled(true);
						} else {
							showToast("Evaluating round...");
							transferToWinner();
						} 
					} else hands[currentPlayer].setTouchEnabled(true);
				}
				
				private boolean allPlayersLaidCard() {
					int nbCards = bids[0].getNumberOfCards();
					for (Hand h: bids)
						if (nbCards != h.getNumberOfCards())
							return false;
					return true;
				}

				private boolean isSameRank() {
					return bids[0].getLast().getRank() == bids[1].getLast().getRank();
				}

			});
			hands[i].draw();
		}
	}

	private void initBids() {
		for (int i = 0; i < nbPlayers; i++) {
			bids[i] = new Hand(deck);
			bids[i].setView(this, new RowLayout(bidLocations[i], 130));
			bids[i].draw();
		}
	}

	private void gameOver() {
		setTouchEnabled(false);
		int nbCard0 = stocks[0].getNumberOfCards();
		int nbCard1 = stocks[1].getNumberOfCards();
		TextActor winnerLabel = new TextActor("Winner!", Color.YELLOW,
				Color.TRANSPARENT, 16);
		winnerLabel.setLocationOffset(new Point(0, -50));
		if (nbCard0 > nbCard1) {
			addActor(winnerLabel, (stockLocations[0]).toReal());
		} else if (nbCard0 < nbCard1) {
			addActor(winnerLabel, (stockLocations[1]).toReal());
		} else
			addActor(new TextActor("Tie!", Color.YELLOW, Color.TRANSPARENT, 16), (new Location(20,100)).toReal());
		addActor(new TextActor("Game over", Color.YELLOW, Color.TRANSPARENT, 20), (new Location(20, 200)).toReal());
		addActor(new TextActor(nbCard0 + " cards"), (new Location(30, 550)).toReal());
		addActor(new TextActor(nbCard1 + " cards"), (new Location(450, 550)).toReal());
	}

	private void initStocks() {
		for (int i = 0; i < nbPlayers; i++) {
			stocks[i] = new Hand(deck);
			stocks[i].setView(this, new StackLayout(stockLocations[i]));
		}
	}

	/**
	 * Wakes up the thread in the main, which does all the work.
	 * @see main
	 */
	private void transferToWinner() {
		setTouchEnabled(false);
		Monitor.wakeUp();
	}

	private void transferBidsToStock(int player) {
		for (int i = 0; i < nbPlayers; i++) {
			bids[i].setTargetArea(new TargetArea(stockLocations[player]));
			Card c = bids[i].getLast();
			while (c != null) {
				c.setVerso(true);
				bids[i].transferNonBlocking(c, stocks[player], true);
				c = bids[i].getLast();
			}
		}
	}
}