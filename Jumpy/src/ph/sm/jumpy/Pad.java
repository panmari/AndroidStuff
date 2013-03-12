package ph.sm.jumpy;

import android.graphics.Point;
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
			int x = (int)(Math.random()*(gameGrid.getNbHorzCells() - 200)) + 100;
			setX(x);
		}
	}
	
	public void reset() {
		setCollisionLine(new Point(-50, -8), new Point(50, -8));
	}
}
