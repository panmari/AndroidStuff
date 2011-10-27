// TCPnim.java

package ch.mazzzy.TCPnim;

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
import ch.aplu.util.Monitor;

public class TcpPearl extends GameGrid implements TcpNodeListener,
		GGTouchListener, GGNavigationListener {
	private final String myNodeName = "Luka";
	private String roomID = "";
	private String sessionID = "awq";
	private TcpNode node = new TcpNode();
	private final static int size = 6;
	private boolean isMyMove = false;
	private int activeRow;
	private int nbPearl = 0;
	private int nbTakenPearl;

	public TcpPearl() {
		super(size, size, 52);
	}

	public void main() {
		getBg().clear(Color.rgb(80, 15, 247));
		setTitle("Remove any number of pearls from same row and right click if finish");
		addNavigationListener(this);
		init();
	}

	public void init() {
		int nb = size;
		for (int k = 0; k < size; k++) {
			for (int i = 0; i < nb; i++) {
				Actor pearl = new Actor("pearl");
				addActor(pearl, new Location(i, k));
				addTouchListener(this, GGTouch.click);
				nbPearl++;
			}
			nb--;
		}
		System.out.println("nbPearl = " + nbPearl);
		activeRow = -1;
		nbTakenPearl = 0;
		refresh();
		System.out.println(node.getVersion());
		node.addTcpNodeListener(this);
		showToast("Connecting to  relay...");
		connect();
		Monitor.putSleep(4000);
		if (node.getNodeState() == TcpNodeState.CONNECTED) {
			setStatusText("Connection established.");
		} else
			setStatusText("Connection failed");
	}

	private void connect() {
		while (roomID.length() < 3)
			roomID = requestEntry("Enter unique game room name (more than 2 characters):");
		sessionID = sessionID + roomID;
		node.connect(sessionID, myNodeName);
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

	public void messageReceived(String sender, String text) {
		System.out.println("Message received: " + text);
		int x = text.charAt(0) - 48; // We get ASCII code of number
		int y = text.charAt(1) - 48;
		if (x == 8) {
			isMyMove = true;
			showToast("It's your turn");
		} else {
			Location loc = new Location(x, y);
			Actor actor = getOneActorAt(loc);
			actor.removeSelf();
			nbPearl--;
			if (nbPearl == 0)
				showToast("You won!");
			refresh();
		}
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
		}
	}

	public void navigationEvent(GGNavigationEvent event) {
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
