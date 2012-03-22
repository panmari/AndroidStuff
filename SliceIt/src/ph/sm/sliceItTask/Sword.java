package ph.sm.sliceItTask;

import android.graphics.Point;
import ch.aplu.android.Actor;

/**
 * The Actor used to slice the fruit with.
 * @author panmari
 */
public class Sword extends Actor{
	
	public Sword() {
		super("sword");
	}
	
	public void reset() {
		setCollisionSpot(new Point(0,10));
	}
}
