package ph.sm.balance;

import android.util.FloatMath;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Marble extends Actor{
	
	/**
	 * TODO: set FACTOR dependent on device setting/accuracy of sensor?
	 */
	private static final float FACTOR = 0.1f;
	float xVelocity, yVelocity;
	float xPos, yPos;
	float radius;
	private Balance gg;
	
	public Marble(Balance gg) {
		super("marble");
		this.gg = gg;
	}
	
	public void reset() {
		setLocation(getLocationStart());
		xPos = getLocation().x;
		yPos = getLocation().y;
		xVelocity = 0;
		yVelocity = 0;
		radius = getImage().getWidth()/2.0f - 1;
	}
	
	public void act() {
		xVelocity += FACTOR*FloatMath.cos(gg.getXSlope())*Math.signum(gg.getXSlope());
		yVelocity += FACTOR*FloatMath.cos(gg.getYSlope())*Math.signum(gg.getYSlope());
		xPos += xVelocity;
		yPos += yVelocity;
		setLocation(new Location(Math.round(xPos), Math.round(yPos)));
		if (	xPos < radius || 
				xPos > getNbHorzCells() - radius ||
				yPos < radius ||
				yPos > getNbVertCells() - radius) 
			gg.gameOver();
	}
}
