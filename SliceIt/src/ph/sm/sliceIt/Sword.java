package ph.sm.sliceIt;

import android.graphics.Point;
import ch.aplu.android.Actor;

public class Sword extends Actor{
	
	public Sword() {
		super("sword");
	}
	
	public void reset() {
		this.setCollisionLine(new Point(0,10), new Point(0,-10));
	}
}
