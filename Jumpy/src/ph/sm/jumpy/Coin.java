package ph.sm.jumpy;

import android.graphics.Point;
import ch.aplu.android.Actor;

public class Coin extends Actor {

	public Coin() {
		super("coin");
	}
	
	public void act() {
		if (isInGrid()) {
			setY(getY() + 1);
		}
		else { 
			reset();
		}
	}
	
	public void reset() {
		setCollisionCircle(new Point(0, 0), 15);
		setY((int)(-Math.random()*300));
		int x = (int)(Math.random()*(gameGrid.getNbHorzCells() - 100)) + 100;
		setX(x);
		setActEnabled(true);
	}
}
