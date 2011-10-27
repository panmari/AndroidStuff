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
import ch.aplu.tcp.TcpNode;
import ch.aplu.tcp.TcpNodeListener;
import ch.aplu.tcp.TcpNodeState;
import ch.aplu.util.Monitor;

public class TcpPearl extends GameGrid implements TcpNodeListener,
		GGTouchListener, GGNavigationListener {
	private final String myNodeName = "Luka";
	private TcpNode node;
	private final static int size = 6;
	private boolean isMyMove = false;
	private int activeRow;
	private int nbPearl = 0;
	private int nbTakenPearl;

	public TcpPearl() {
		super(size, size, 60);
	}

	public void main() {
		getBg().clear(Color.rgb(80, 15, 247));
		setTitle("Remove any number of pearls from same row and right click if finish");
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

		node = new TcpNode();
		String sessionID = requestEntry("Enter unique room name");
		node.addTcpNodeListener(this);
		setStatusText("Connecting to relay '" + node.getRelay() + "'...");
		node.connect("awq" + sessionID, myNodeName);
		Monitor.putSleep(4000);
		if (node.getNodeState() == TcpNodeState.CONNECTED) {
			setStatusText("Connection established.");
		} else
			setStatusText("Connection failed");
	}

	private String requestEntry(String prompt) {
		return new GGInputDialog("TCPnim", prompt, "n44").show();
	}

	public void nodeStateChanged(TcpNodeState state) {
		if (state == TcpNodeState.CONNECTED)
			Monitor.wakeUp();
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
			setStatusText("You mus remove pearls from the same row");
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
					setStatusText("You lost!");
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
			setStatusText("It's your turn");
		} else {
			Location loc = new Location(x, y);
			Actor actor = getOneActorAt(loc);
			actor.removeSelf();
			nbPearl--;
			if (nbPearl == 0)
				setStatusText("You won!");
			refresh();
		}
	}

	public void statusReceived(String text) {
		System.out.println("Status: " + text);
		if (text.contains("(0)"))
			isMyMove = true;
		if (text.contains("(1)"))
			setStatusText("Partner connected" + (isMyMove ? " Play" : " Wait"));
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
