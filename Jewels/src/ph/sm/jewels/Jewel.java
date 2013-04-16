package ph.sm.jewels;

import java.util.LinkedList;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGPanel;
import ch.aplu.android.L;
import ch.aplu.android.Location;
import ch.aplu.android.PointD;

public class Jewel extends Actor {

	private LinkedList<Jewel> jewels;
	private int resetCounter;

	/**
	 * The fifth sprite is a special explosion-sprite!
	 * @param jewels
	 * @param p
	 * @param hexagonSpawnPoint
	 */
	public Jewel(LinkedList<Jewel> jewels) {
		super(true, "jewel", 5);
		this.jewels = jewels;
	}
	
	public void act() {
		//this is quite a bit hacky:
		if (resetCounter == 1)
			reset();
		if (isExploding())
			resetCounter--;
		else move();
	}
	
	public void reset() {
		resetCounter = 0;
		jewels.addLast(this);
		setCollisionCircle(new Point(0, 0), 15);
		setLocation(new Location(-100, -100)); //out of sight;
		setActEnabled(false);
		show((int)(Math.random()*4));
	}

	public void explode() {
		show(4);
		resetCounter = 6;
	}

	public boolean isExploding() {
		return resetCounter > 0;
	}
}
