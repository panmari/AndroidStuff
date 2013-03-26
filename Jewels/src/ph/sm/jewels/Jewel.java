package ph.sm.jewels;

import java.util.LinkedList;

import ch.aplu.android.Actor;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class Jewel extends Actor {

	private LinkedList<Actor> jewels;

	public Jewel(LinkedList<Actor> jewels) {
		super(true, "jewel", 4);
		this.jewels = jewels;
	}
	
	public void act() {
		// TODO: check if jewel has flown out of window due to not being consumed
		move();
	}
	
	public void reset() {
		L.d("reset");
		jewels.addLast(this);
		setLocation(new Location(-100, -100)); //out of sight;
		setActEnabled(false);
		this.show((int)(Math.random()*4));
	}
}
