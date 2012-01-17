package ph.sm.sliceIt;

import java.util.ArrayList;

import ch.aplu.android.Actor;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class Fruit extends Actor {
	private float x,y, yVel;
	private final float acc = 9.81F;
	private SliceIt gg;
	private final int SIZE = 20;
	private float xVel;
	
	public Fruit(SliceIt gg, float xVel) {
		super("fruit");
		this.gg = gg;
		this.xVel = xVel;
	}
	
	public void reset() {
		this.setDirection(Location.SOUTH);
		this.x = getPixelLocation().x;
		this.y = getPixelLocation().y;
	}
	public void act() {
		if (isSliced())
			splatter();
		else fallPhysically();
	}

	private void fallPhysically() {
	    float dt = 2 * gg.getSimulationPeriod() / 1000F;
	    yVel = yVel + acc * dt;
	    x = x + xVel*dt;
	    y = y + yVel*dt;
	    setLocation(new Location(Math.round(x), Math.round(y)));
	    //TODO: make neater
	    if (!isInGrid())
	    	removeSelf();
	}

	private boolean isSliced() {
		ArrayList<Location> fruitLocs = getLocation().getNeighbourLocations(SIZE);
		//L.d("fruit locs: " + fruitLocs);
		//L.d("Slice locs: " + gg.getSliceLocs());
		for (Location l: gg.getSliceLocs())
			if (fruitLocs.contains(l))
				return true;
		return false;
	}
	
	/**
	 * Name is subject to change...
	 */
	private void splatter() {
		System.out.println("boom!");
		removeSelf();
	}
}
