package ph.sm.sliceIt;

import java.util.ArrayList;

import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public abstract class Fruit extends Actor {
	private float x,y, yVel;
	private final float acc = 9.81F;
	private SliceIt gg;
	private int size = 20;
	private float xVel;
	
	public Fruit(String sprite, SliceIt gg, float xVel) {
		super(true, sprite, 2);
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
		movePhysically();
		turn(10); //for fun effects!
	}

	private void movePhysically() {
	    float dt = 2 * gg.getSimulationPeriod() / 1000F;
	    yVel = yVel + acc * dt;
	    x = x + xVel*dt;
	    y = y + yVel*dt;
	    setLocation(new Location(Math.round(x), Math.round(y)));
	    //TODO: make neater
	    if (!isInGrid()) {
	    	removeSelf();
	    	gg.showToast("You missed one!");
	    }
	}

	private boolean isSliced() {
		if (isAlreadySliced())
			return false;
		ArrayList<Location> fruitLocs = getLocation().getNeighbourLocations(size);
		for (Location l: gg.getSliceLocs())
			if (fruitLocs.contains(l))
				return true;
		return false;
	}
	
	private boolean isAlreadySliced() {
		return getIdVisible() == 1;
	}

	/**
	 * Name is subject to change...
	 */
	private void splatter() {
		showNextSprite();
	}
	
	protected void setSize(int size) {
		this.size = size;
	}
}
