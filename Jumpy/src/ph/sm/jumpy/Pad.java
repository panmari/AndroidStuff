package ph.sm.jumpy;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Pad extends Actor {

	
	private float x, y;
	private static float vy; //all pads have the same speed

	public Pad() {
		super("pad");
	}
	
	public void act() {	
		if (isInGrid()) {
			y += vy;
		}
		else {  
			y = 0;
			x = (float) (Math.random()*(gameGrid.getNbHorzCells() - 200)) + 100;
		}
		setLocation(new Location(Math.round(x), Math.round(y)));
	}
	
	public static void speedUp(float amount) {
		vy += amount;
	}
	
	public void reset() {
		vy = 3;
		this.x = getX();
		this.y = getY();
		setCollisionLine(new Point(-50, -8), new Point(50, -8));
	}
}
