// Memory.java

package ph.sm.memory;

import java.util.Arrays;

import android.graphics.Color;
import ch.aplu.android.Actor;
import ch.aplu.android.GGInputDialog;
import ch.aplu.android.GGNavigationListener.ScreenOrientation;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.L;
import ch.aplu.android.Location;
import ch.aplu.android.TextActor;
import ch.aplu.tcp.*;
import ch.aplu.util.Monitor;

public class TCPMemory extends GameGrid implements GGTouchListener,
		TcpNodeListener {
	interface Command {
		char start = 's';
		char move = 'm';
	}

	private boolean firstCard = true;
	private final String VERSION = "1.0";
	private MemoryCard card1;
	private MemoryCard card2;
	/* For tcp communication */
	private final TcpNode tcpNode = new TcpNode();
	private final String nickname = "memoryPlayer";
	private String sessionID = "TcpMemoryAndroid**fc";
	private String roomID = "";
	private final String moveInfo = "Your turn. Tap two cards";
	private boolean isMyMove = false;
	private int points;

	public TCPMemory() {
		super(4, 4, cellZoom(115));
		setScreenOrientation(ScreenOrientation.PORTRAIT);
	}

	public void main() {
		addTouchListener(this, GGTouch.click);
		tcpNode.addTcpNodeListener(this);
		reset();
		connect();
		while (true) {
			Monitor.putSleep(); // Wait until there is something to do
			delay(1000);
			card1.show(1); // Flip cards back
			card2.show(1);
			setTouchEnabled(true); // Rearm mouse events
			refresh();
		}
	}

	private void connect() {
		while (roomID.length() < 3)
			roomID = requestEntry("Enter unique game room name (more than 2 characters):");
		sessionID = sessionID + roomID;
		showToast("Connecting to  relay...");
		tcpNode.connect(sessionID, nickname);
	}

	private String requestEntry(String prompt) {
		return GGInputDialog.show("TcpMemory V" + VERSION, prompt, "m23");
	}

	public void nodeStateChanged(TcpNodeState state) {
	}

	/**
	 * Extracted from TcpFourInARow
	 */
	public void statusReceived(String text) {
		if (text.contains("In session:--- (0)")) // We are first player
		{
			showToast("Connected. Waiting for a partner...");
			addActor(new TextActor("Waiting in room " + roomID, Color.BLACK,
					Color.TRANSPARENT, 18), new Location(0, 0));
		} else if (text.contains("In session:--- (1)")) // We are second player
		{
			showToast(moveInfo);
			isMyMove = true; // Second player starts
			tcpNode.sendMessage("" + Command.start + " " + getCardArrangement());
		} else if (text.contains("In session:--- ")) // We are next player
		{
			showToast("Game in progress. Terminating now...");
			TcpTools.delay(4000);
			killProcess();
		} else if (text.equals("Disconnected:--- " + nickname)
				|| text.equals("Disconnected:--- " + nickname + "(1)")) {
			showToast("Partner disconnected");
			showToast("Waiting for a partner");
			addActor(new TextActor("Waiting in room " + roomID, Color.BLACK,
					Color.TRANSPARENT, 18), new Location(0, 0));
			reset();
			isMyMove = false;
		}
	}

	/**
	 * Makes a string of the arrangement of the cards for easy
	 * transmitting via tcp.
	 * @return
	 */
	private String getCardArrangement() {
		String arrangement = "";
		for (int y = 0; y < getNbVertCells(); y++) {
			for (int x = 0; x < getNbHorzCells(); x++) {
				MemoryCard c = (MemoryCard) getOneActorAt(new Location(x, y));
				arrangement += "" + c.getId() + ",";
			}
		}
		return arrangement.substring(0, arrangement.length() - 1);
	}

	/**
	 * Takes apart the String generated by the method above and 
	 * arranges the cards accordingly.
	 * @param arrangement a String generated by getCardArrangement()
	 */
	private void setCardArrangement(String arrangement) {
		removeAllActors();
		int x = 0;
		int y = 0;
		L.d(Arrays.toString(arrangement.split(",")));
		for (String id: arrangement.split(",")) {
			MemoryCard card = new MemoryCard(Integer.parseInt(id));
			addActorNoRefresh(card, new Location(x, y));
			x++;
			if (x % getNbHorzCells() == 0) {
				y++;
				x = 0;
			}
		}
		refresh();
	}
	
	public void messageReceived(String sender, String text) {
		char command = text.charAt(0);
		switch (command) {
		case Command.start:
			setCardArrangement(text.split(" ")[1]);
			showToast("Partner connected, wait to play.");
			break;
		case Command.move:
			String[] locString = text.substring(1).split(",");
			Location loc = new Location(Integer.parseInt(locString[0]), 
										Integer.parseInt(locString[1]));
			MemoryCard card = (MemoryCard) getOneActorAt(loc);
			revealCard(card);
			break;
		}
	}

	public void reset() {
		removeAllActors();
		for (int i = 0; i < 8; i++) {
			for (int k = 0; k < 2; k++) {
				MemoryCard card = new MemoryCard(i);
				addActorNoRefresh(card, getRandomEmptyLocation());
			}
		}
		points = 0;
		L.d("reseting");
		refresh();
		Monitor.wakeUp();
	}

	public void flipCardsBack() {
		setTouchEnabled(false); // Disable mouse events
		Monitor.wakeUp();
	}

	public boolean touchEvent(GGTouch mouse) {
		if (!isMyMove)
			return true;
		Location location = toLocation(mouse.getX(), mouse.getY());
		MemoryCard card = (MemoryCard) getOneActorAt(location);
		// Outside of grid or card already flipped -> no action
		if (card == null || card.getIdVisible() == 0) 
			return true;
		tcpNode.sendMessage("" + Command.move + location.x + "," + location.y);
		revealCard(card);
		return true;
	}

	/**
	 * reveals the given card and also flips both
	 * cards back if they don't match. 
	 * @param card the card to be revealed.
	 */
	private void revealCard(MemoryCard card) {
		card.show(0); // Show picture
		if (firstCard) {
			firstCard = false;
			card1 = card;
		} else {
			firstCard = true;
			card2 = card;
			if (card1.getId() != card2.getId()) {
				flipCardsBack();
				changePlayer();
			}
			else {
				if (isMyMove)
					points++;
				if (isGameOver())
					showToast("Game Over, you have " + points + " points");
			}
		}
		refresh();
	}

	/**
	 * The game is over when all cards have their
	 * front visible. So this method returns false 
	 * when it finds a card that has the backside visible.
	 * @return
	 */
	private boolean isGameOver() {
		for(Actor a: getActors())
			if (a.getIdVisible() == 1)
				return false;
		return true;
	}

	private void changePlayer() {
		isMyMove = !isMyMove;
		showToast((isMyMove ? "   It is you to play." : "   Wait to play.") + " Points: " + points);
	}
}

class MemoryCard extends Actor {
	private int id;

	public MemoryCard(int id) {
		super("card" + id, "cardback");
		this.id = id;
		show(1);
	}

	public int getId() {
		return id;
	}
}
