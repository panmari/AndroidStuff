package ph.sm.jewels;

import ch.aplu.android.Actor;

public class Jewel extends Actor {

	private Hexagon hexagon;

	public Jewel(Hexagon hexagon) {
		super(true, "jewel", 4);
		this.hexagon = hexagon;
	}
	
	public void act() {
		move();
	}
	
	public void reset() {
		this.show((int)(Math.random()*4));
		setDirection(getLocation().getDirectionTo(hexagon.getLocation()));
	}
}
