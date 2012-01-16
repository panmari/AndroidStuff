package ph.sm.sliceIt;

import java.util.ArrayList;

import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Fruit extends Actor {
	private float x,y, yVel;
	private final float acc = 9.81F;
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
		fallPhysically();
		if (isSliced())
			splatter();
	}

	private void fallPhysically() {
	    float dt = 2 * gg.getSimulationPeriod() / 1000F;
	    yVel = yVel + acc * dt;
	    y = y + yVel*dt;
	    setLocation(new Location(Math.round(x), Math.round(y)));
	    //TODO: make neater
	    if (!isInGrid())
	    	removeSelf();
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
