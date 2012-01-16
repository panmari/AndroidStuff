package ph.sm.sliceIt;

import java.util.ArrayList;

import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Fruit extends Actor {
	private double x,y;
	private final double acc = 9.81;
	private SliceIt gg;
	private final int SIZE = 5;
	
	public Fruit(SliceIt gg) {
		super("fruit");
		this.gg = gg;
	}
	
	public void reset() {
		this.setDirection(Location.SOUTH);
		this.x = getPixelLocation().x;
		this.y = getPixelLocation().y;
	}
	public void act() {
		move();
		if (isSliced())
			splatter();
	}

	private boolean isSliced() {
		ArrayList<Location> fruitLocs = getLocation().getNeighbourLocations(SIZE);
		return fruitLocs.contains(gg.getSliceLoc());
	}
	
	/**
	 * Name is subject to change...
	 */
	private void splatter() {
		System.out.println("boom!");
		removeSelf();
	}
}
