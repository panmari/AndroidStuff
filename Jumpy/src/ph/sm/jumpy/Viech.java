package ph.sm.jumpy;

import ch.aplu.android.Actor;

public class Viech extends Actor{
	
	float x;
	float y;
	float vx;
	float vy;
	float ax;
	float ay = -9.81f;
	
	
	public Viech() {
		super("ball");
	}
	public void act() {
		
		x = x + vx;
		y = x + vy;
		vx = vx + ax;
		vy = vy + ay;
	}
}
