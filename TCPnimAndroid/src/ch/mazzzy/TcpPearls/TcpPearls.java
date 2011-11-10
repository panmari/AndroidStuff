
package ch.mazzzy.TcpPearls;

import android.graphics.Color;
import ch.aplu.android.Actor;
import ch.aplu.android.GGInputDialog;
import ch.aplu.android.GGNavigationEvent;
import ch.aplu.android.GGNavigationListener;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;
import ch.aplu.android.TextActor;
import ch.aplu.tcp.TcpNode;
import ch.aplu.tcp.TcpNodeListener;
import ch.aplu.tcp.TcpNodeState;
import ch.aplu.tcp.TcpTools;
import ch.aplu.util.Monitor;

public class TcpPearls extends GameGrid implements TcpNodeListener,
		GGTouchListener, GGNavigationListener {
	
	 interface Command
	  {
	    char change = 'c'; // change player
	    char move = 'm';   // move pearl
	    char over = 'o';   // game over
	    char start = 's';  // start game
	    char terminate = 't'; // terminate game
	  }
	 
	private String roomID = "";
	private TcpNode node = new TcpNode();
	private final static int size = 6;
	private boolean isMyMove = false;
	private int activeRow;
	private int nbPearl = 0;
  private String sessionID = "PearlGame: &41*()";
  private final String nickname = "tic";
  private int nbTakenPearl = 0;
  private int nbRows = 4;
  private final String moveInfo =
    "Click to remove any number of pearls from same row and press OK.";


	public TcpPearls() {
		super(size, size, 52);
	}

	public void main() {
		getBg().clear(Color.rgb(80, 15, 247));
		addNavigationListener(this);
		init();
		node.addTcpNodeListener(this);
		showToast("Connecting to  relay...");
		connect();
		Monitor.putSleep(4000);
		if (node.getNodeState() == TcpNodeState.CONNECTED) {
			setStatusText("Connection established.");
		} else
			setStatusText("Connection failed");
	}

	public void init() {
		int nb = 6;
		for (int k = 0; k < nbRows; k++) {
			for (int i = 0; i < nb; i++) {
				Actor pearl = new Actor("pearl");
				addActor(pearl, new Location(i+1, k+1));
				addTouchListener(this, GGTouch.click);
				nbPearl++;
			}
			nb--;
		}
		activeRow = -1;
		nbTakenPearl = 0;
		refresh();
	}

	private void connect() {
		while (roomID.length() < 3)
			roomID = requestEntry("Enter unique game room name (more than 2 characters):");
		sessionID = sessionID + roomID;
		node.connect(sessionID, nickname);
	}

	private String requestEntry(String prompt) {
		return new GGInputDialog("TCPnim", prompt, "n44").show();
	}

	public void nodeStateChanged(TcpNodeState state) {
		if (state == TcpNodeState.DISCONNECTED)
			setStatusText("Connection broken.");
	}

	public boolean touchEvent(GGTouch touch) {
		if (!isMyMove)
			return true;

		Location loc = toLocationInGrid(touch.getX(), touch.getY());
		int x = loc.x;
		int y = loc.y;
		if (activeRow != -1 && activeRow != y)
			showToast("You must remove pearls from the same row");
		else {
			Actor actor = getOneActorAt(loc);
			if (actor != null) {
				actor.removeSelf();
				nbPearl--;
				node.sendMessage("" + x + y);
				activeRow = y;
				nbTakenPearl++;
				System.out.println("nbPearl " + nbPearl);
				if (nbPearl == 0) {
					showToast("You lost!");
					isMyMove = false;
				}
			}
		}
		refresh();
		return true;
	}

	public void messageReceived(String sender, String text)
	  {
	    char command = text.charAt(0);
	    switch (command)
	    {
	      case Command.start:
	        init();
	        if (isMyMove) {
	          setStatusText("Game started. " + moveInfo);
	        } else {
	          setStatusText("Game started. Wait for the partner's move.");
	        }
	        break;
	      case Command.terminate:
	        setStatusText("Partner exited game room. Terminating now...");
	        TcpTools.delay(4000);
	        System.exit(0);
	        break;
	      case Command.move:
	        int x = text.charAt(1) - 48; // We get ASCII code of number
	        int y = text.charAt(2) - 48;
	        Location loc = new Location(x, y);
	        getOneActorAt(loc).removeSelf();
	        nbPearl--;
	        setStatusText(nbPearl + " pearls remaining. Wait for the partner's move.");
	        break;
	      case Command.over:
	        setStatusText("You won. Press 'New Game' to play again.");
	        isMyMove = true;
	        break;
	      case Command.change:
	        isMyMove = true;
	        setStatusText(nbPearl + " pearls remaining. " + moveInfo);
	        nbTakenPearl = 0;
	        activeRow = 0;
	        break;
	    }
	    refresh();
	  }

	public void statusReceived(String text) {
		System.out.println("Status: " + text);
		if (text.contains("(0)")) {
			showToast("Connected. Waiting for a partner...");
			addActor(new TextActor("Waiting in room " + roomID, Color.BLACK,
					Color.TRANSPARENT, 18), new Location(0, 0));
			isMyMove = true;
		} else if (text.contains("(1)")) {
			showToast("Partner connected" + (isMyMove ? " Play" : " Wait"));
			removeActors(TextActor.class);
		} else if (text.contains("In session:--- ")) // third or more
	    {
	      showToast("Game in progress. Terminating now...");
	      TcpTools.delay(4000);
	      System.exit(0);
	    }
	}

	public void navigationEvent(GGNavigationEvent event) {
		if (!isMyMove) {
			showToast("Wait for your partner to move");
			return;
		}
		switch (event) {
		case MENU_DOWN:
			if (nbTakenPearl == 0)
				setStatusText("You must remove at least 1 pearl.");
			else {
				node.sendMessage("88");
				isMyMove = false;
				setStatusText("Wait.");
				activeRow = -1;
				nbTakenPearl = 0;
			}
			break;
		}
	}
}
