package ph.sm.balance;

import android.util.FloatMath;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Marble extends Actor{
	
	/**
	 * The difficulty can be varied trough this factor.
	 */
	private static final float FACTOR = 1f;
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
		xVelocity += FACTOR*FloatMath.sin(gg.getXSlope());
		yVelocity += FACTOR*FloatMath.sin(gg.getYSlope());
		xPos += xVelocity;
		yPos += yVelocity;
		setLocation(new Location(Math.round(xPos), Math.round(yPos)));
		if (touchingBoarder()) 
			gg.gameOver();
	}

	private boolean touchingBoarder() {
		return (xPos < radius || 
				xPos > getNbHorzCells() - radius ||
				yPos < radius ||
				yPos > getNbVertCells() - radius);
	}
}
