package ph.sm.jumpy;

import ch.aplu.android.Actor;

public class Pad extends Actor {

	public Pad() {
		super("pad");
	}
	
	public void act() {
		if (isInGrid())
			setY(getY() + 1);
		else 
			setY(0);
	}
}
