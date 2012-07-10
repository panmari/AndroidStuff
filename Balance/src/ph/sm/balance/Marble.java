package ph.sm.balance;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class Marble extends Actor{
	
	/**
	 * TODO: set FACTOR dependent on device setting/accuracy of sensor?
	 */
	private static final float FACTOR = 0.005f;
	float xVelocity, yVelocity;
	float xPos, yPos;
	private Balance gg;
	
	public Marble(Balance gg) {
		super("marble");
		this.gg = gg;
	}
	
	public void reset() {
		xPos = getPixelLocation().x;
		yPos = getPixelLocation().y;
		xVelocity = 0;
		yVelocity = 0;
	}
	
	public void act() {
		xVelocity += FACTOR*gg.getXSlope();
		yVelocity += FACTOR*gg.getYSlope();
		xPos += xVelocity;
		yPos += yVelocity;
		setPixelLocation(new Point(Math.round(xPos), Math.round(yPos)));
		if (!isInGrid())
			gg.gameOver();
	}
}
