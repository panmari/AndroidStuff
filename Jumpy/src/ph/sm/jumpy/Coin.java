package ph.sm.jumpy;

import android.graphics.Point;
import ch.aplu.android.Actor;

public class Coin extends Actor {
	
	public Coin() {
		super("coin");
	}
	
	public void act() {
		if (getY() < gameGrid.getNbVertCells()) {
			setY(getY() + 1);
		}
		else { 
			reset();
		}
	}
	
	public void reset() {
		setCollisionCircle(new Point(0, 0), 14);
		setY((int)(-Math.random()*300) - 50);
		int x = (int)(Math.random()*(gameGrid.getNbHorzCells() - 100)) + 50;
		setX(x);
		setActEnabled(true);
	}
}
