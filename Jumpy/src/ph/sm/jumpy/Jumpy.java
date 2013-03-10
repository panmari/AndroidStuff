package ph.sm.jumpy;

import ch.aplu.android.Actor;
import ch.aplu.android.GGSensor;

public class Jumpy extends Actor{
	
	float x;
	float y;
	float vx;
	float vy;
	float ax;
	float ay = -9.81f;
	private GGSensor sensor;
	
	
	public Jumpy(GGSensor sensor) {
		super("ball");
		this.sensor = sensor;
	}
	public void act() {
		float[] values = sensor.getValues();
		x = x + vx;
		y = x + vy;
		vx = vx + ax;
		vy = vy + ay;
	}
}
