package ph.sm.jewels;

import java.util.LinkedList;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGPanel;
import ch.aplu.android.L;
import ch.aplu.android.Location;
import ch.aplu.android.PointD;

public class Jewel extends Actor {

	private LinkedList<Actor> jewels;
	private GGPanel p;
	private PointD hexagonSpawnPoint;

	public Jewel(LinkedList<Actor> jewels, GGPanel p, PointD hexagonSpawnPoint) {
		super(true, "jewel", 4);
		this.jewels = jewels;
		this.p = p;
		this.hexagonSpawnPoint = hexagonSpawnPoint;
	}
	
	public void act() {
		move();
		//reset jewel if it's to close to center (and was not eaten)
		double dist = distanceToHexagon(p.toUserX(getX()), p.toUserY(getY()));
		L.d("" + dist);
		if (dist < 1)
			reset();
			
	}
	
	public void reset() {
		jewels.addLast(this);
		setLocation(new Location(-100, -100)); //out of sight;
		setActEnabled(false);
		this.show((int)(Math.random()*4));
	}
	
	private double distanceToHexagon(double x, double y) {
		double distSquare = Math.pow(hexagonSpawnPoint.x - x, 2) + Math.pow(hexagonSpawnPoint.y - y, 2);
		return Math.sqrt(distSquare);
	}
}
