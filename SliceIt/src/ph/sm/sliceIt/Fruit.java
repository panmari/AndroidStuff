package ph.sm.sliceIt;

import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Fruit extends Actor {
	double x,y;
	final double acc = 9.81;
	public Fruit() {
		super("fruit");
	}
	
	public void reset() {
		this.setDirection(Location.SOUTH);
		this.x = getPixelLocation().x;
		this.y = getPixelLocation().y;
	}
	public void act() {
		move();
	}
}
