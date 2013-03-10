package ph.sm.jumpy;

import ch.aplu.android.Actor;
import ch.aplu.android.GGSensor;
import ch.aplu.android.Location;

public class Jumpy extends Actor{
	
	private static final float JUMP_HEIGHT = -7;
	private static final float FACTOR = 0.01f;
	float x;
	float y;
	float vx;
	float vy;
	float ax;
	float ay = 9.81f*FACTOR;
	private GGSensor sensor;
	
	
	public Jumpy(GGSensor sensor) {
		super("ball");
		this.sensor = sensor;
	}
	public void act() {
		float[] values = sensor.getValues();
		ax = -values[0]*FACTOR;
		x = x + vx;
		y = y + vy;
		vx = vx + ax;
		vy = vy + ay;
		
		updateLocation();
	}
	
	private void updateLocation() {
		Location newLoc = new Location(Math.round(x), Math.round(y));
		setLocation(newLoc);
	}
	
	public void jump() {
		vy = JUMP_HEIGHT;
	}
	
	/**
	 * Initialize positions
	 */
	public void reset() {
		x = getX();
		y = getY();
	}
}
