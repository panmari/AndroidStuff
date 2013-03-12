package ph.sm.jumpy;

import ch.aplu.android.Actor;

public class Pad extends Actor {

	public Pad() {
		super("pad");
	}
	
	public void act() {
		if (isInGrid()) {
			setY(getY() + 1);
		}
		else {  
			setY(0);
			int x = (int)(Math.random()*(gameGrid.getNbHorzCells() - 100)) + 100;
			setX(x);
		}
	}
}
