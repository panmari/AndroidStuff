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
		super(Color.GREEN, Color.TRANSPARENT, BoardType.VERT_FULL,
				windowZoom(600));
	}

	public void main() {
		deck = new Deck(Suit.values(), Rank.values(), "cover");
		initBids();
		initStocks();
		initHands();
		showToast("Tap to play card" + ". Starting player: "
				+ currentPlayer);
		TextActor player0 = new TextActor("player 0 ", Color.WHITE,
				Color.TRANSPARENT, 16);
		addActor(player0, new Location(182, 530));
		TextActor player1 = new TextActor("player 1 ", Color.WHITE,
				Color.TRANSPARENT, 16);
		addActor(player1, new Location(360, 530));
		hands[0].setTouchEnabled(true);
		while (true) {
			Monitor.putSleep();
			delay(1000);
			Hand eval = new Hand(deck);
			for (int i = 0; i < nbPlayers; i++)
				eval.insert(bids[i].getLast(), false);
			int nbWinner = eval.getMaxPosition(Hand.SortType.RANKPRIORITY);
			transferToStock(nbWinner);
			currentPlayer = nbWinner;
			hands[nbWinner].setTouchEnabled(true);
		}
	}

	private void initHands() {
		hands = deck.dealingOut(nbPlayers, nbCards);
		for (int i = 0; i < nbPlayers; i++) {
			hands[i].setView(this, new StackLayout(handLocations[i]));
			hands[i].setVerso(true);
			final int k = i;
			hands[i].addCardListener(new CardAdapter() {
				public void pressed(Card card) {
					hands[currentPlayer].setTouchEnabled(false);
					card.setVerso(false);
					card.transferNonBlocking(bids[k], true);
					currentPlayer = (currentPlayer + 1) % nbPlayers;
					if (allPlayersLaidCard())
					{
						if (isSameRank()) {
							if (hands[currentPlayer].isEmpty()) {
								gameOver();
								return;
							}
							for (int i = 0; i < nbPlayers; i++) {
								Card c = hands[i].getLast();
								c.transfer(bids[i], true);
							}
						} else {
							showToast("Evaluating round...");
							transferToWinner();
						}
					} 

					if (!hands[currentPlayer].isEmpty()) {
						setStatusText("Current player: " + currentPlayer);
						hands[currentPlayer].setTouchEnabled(true);
					} else
						gameOver();
				}

				private boolean allPlayersLaidCard() {
					int nbCards = bids[0].getNumberOfCards();
					for (Hand h: bids)
						if (nbCards != h.getNumberOfCards())
							return false;
					return true;
				}

				private boolean isSameRank() {
					if (bids[0].getLast() == null || bids[1].getLast() == null)
						return false;
					return bids[0].getLast().getRank() == bids[1].getLast()
							.getRank();
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
		int nbCard0 = stocks[0].getNumberOfCards();
		int nbCard1 = stocks[1].getNumberOfCards();
		TextActor winnerLabel = new TextActor("Winner!", Color.YELLOW,
				Color.TRANSPARENT, 16);
		winnerLabel.setLocationOffset(new Point(-30, 90));
		if (nbCard0 > nbCard1) {
			setStatusText("Game over. Winner: player 0 (" + nbCard0
					+ " cards), player 1 (" + nbCard1 + " cards)");
			addActor(winnerLabel, stockLocations[0]);
		} else if (nbCard0 < nbCard1) {
			setStatusText("Game over. Winner: player 1 (" + nbCard1
					+ " cards), player 0 (" + nbCard0 + " cards)");
			addActor(winnerLabel, stockLocations[1]);
		} else
			setStatusText("Game over. Tie: player 1 (" + nbCard1
					+ " cards), player 0 (" + nbCard0 + " cards)");
	}

	private void initStocks() {
		for (int i = 0; i < nbPlayers; i++) {
			stocks[i] = new Hand(deck);
			stocks[i].setView(this, new StackLayout(stockLocations[i]));
		}
	}

	private void transferToWinner() {
		Monitor.wakeUp();
	}

	private void transferToStock(int player) {
		for (int i = 0; i < nbPlayers; i++) {
			bids[i].setTargetArea(new TargetArea(stockLocations[player]));
			for (Card c: bids[i].getCardList()) {
				c.setVerso(true);
				bids[i].transferNonBlocking(c, stocks[player], true);
			}
		}
	}
}