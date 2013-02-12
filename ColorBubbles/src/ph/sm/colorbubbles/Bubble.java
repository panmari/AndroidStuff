package ph.sm.colorbubbles;

import ch.aplu.android.Actor;

public class Bubble extends Actor {

	public Bubble(int type) {
		super("peg", 6);
		show(type);
	}

	public boolean fits(Bubble ball) {
		return this.getIdVisible() == ball.getIdVisible();
	}

}