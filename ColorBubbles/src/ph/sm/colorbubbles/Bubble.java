package ph.sm.colorbubbles;

import ch.aplu.android.Actor;

public class Bubble extends Actor {

	public Bubble(int type) {
		super("peg", 6);
		show(type);
	}

	public int getType() {
		return getIdVisible();
	}

}