package ph.sm.sliceIt;

import android.graphics.Point;
import ch.aplu.android.Actor;

public class Sword extends Actor{
	
	public Sword() {
		super("sword");
	}
	
	public void reset() {
		addActorCollisionListener(this);
		this.setCollisionLine(new Point(0,10), new Point(0,-10));
	}
	
	@Override
	public int collide(Actor actor1, Actor actor2) {
		((Fruit) actor2).splatter();
		return 20;
	}
}
