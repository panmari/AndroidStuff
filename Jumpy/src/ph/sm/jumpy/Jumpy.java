package ph.sm.jumpy;

import android.graphics.Point;
import android.hardware.Sensor;
import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.GGSensor;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class Jumpy extends Actor implements GGActorCollisionListener {
	
	private static final float FACTOR = 0.05f;
	private static final float JUMP_VELOCITY = -.7f/FACTOR;
	private float x;
	private float y;
	private float vx;
	private float vy;
	private float ax;
	private float ay = 9.81f*FACTOR;
	private GGSensor sensor;
	private int score;
	private GGStatusBar status;
	
	
	public Jumpy(GGSensor sensor, GGStatusBar status) {
		super("sara", 4);
		this.sensor = sensor;
		this.status = status;
	}
	public void act() {
		float[] values = GGSensor.toDeviceRotation(gameGrid, sensor.getValues(), Sensor.TYPE_ACCELEROMETER);
		vx = -values[0]*FACTOR*30;
		x = x + vx;
		y = y + vy;
		vx = vx + ax;
		vy = vy + ay;
		int activeSprite = Math.max(Math.min(Math.round(-vy/8f + 1.5f), 3), 0);
		show(activeSprite);
		setHorzMirror(vx < 0);
		updateLocation();
	}
	
	private void updateLocation() {
		Location newLoc = new Location(Math.round(x), Math.round(y));
		if (newLoc.y > gameGrid.getNbVertCells()) {
			status.setText("Final score: " + score + " -- Touch screen to play again");
			gameGrid.refresh();
			gameGrid.setActEnabled(false);
			gameGrid.setTouchEnabled(true);
		}
		else {
			setLocation(newLoc);
		}
	}
	
	public void jump() {
		vy = JUMP_VELOCITY;
	}
	
	public void reset() {
		x = getX();
		y = getY();
		score = 0;
	}

	@Override
	public int collide(Actor jumpy, Actor colPartner) {
		if (colPartner.getClass() == Pad.class) {
			if (vy > 0)
				jump();
		} else { 
			Coin c = (Coin) (colPartner);
			if (c.isInGrid()) {
				score++;
				status.setText("Score: " + score);
				c.reset();
			}
		}
		return 5;
	}
}
