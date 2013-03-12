package ph.sm.jumpy;

import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.GGSensor;
import ch.aplu.android.Location;

public class Jumpy extends Actor implements GGActorCollisionListener {
	
	private static final float FACTOR = 0.05f;
	private static final float JUMP_HEIGHT = -.7f/FACTOR;
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float ax;
	private float ay = 9.81f*FACTOR;
	private GGSensor sensor;
	
	
	public Jumpy(GGSensor sensor) {
		super("ball");
		this.sensor = sensor;
	}
	public void act() {
		float[] values = sensor.getValues();
		vx = -values[0]*FACTOR*30;
		x = x + vx;
		y = y + vy;
		vx = vx + ax;
		vy = vy + ay;
		
		updateLocation();
	}
	
	private void updateLocation() {
		Location newLoc = new Location(Math.round(x), Math.round(y));
		if (newLoc.y > gameGrid.getNbVertCells())
			gameGrid.doPause();
		else
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
		//setCollisionSpot(new Point(0, -10));
	}
	

	@Override
	public int collide(Actor jumpy, Actor colPartner) {
		if (colPartner.getClass() == Pad.class) {
			if (vy > 0)
				jump();
		} else { //must be coin
			Coin c = (Coin) (colPartner);
			c.reset();
		}
		return 1;
	}
}
