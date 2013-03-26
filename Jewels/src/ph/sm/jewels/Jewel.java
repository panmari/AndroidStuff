package ph.sm.jewels;

import ch.aplu.android.Actor;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class Jewel extends Actor {

	public Jewel() {
		super(true, "jewel", 4);
	}
	
	public void act() {
		move();
	}
	
	public void reset() {
		L.d("reset");
		setLocation(new Location(-100, -100)); //out of sight;
		setActEnabled(false);
		this.show((int)(Math.random()*4));
	}
}
