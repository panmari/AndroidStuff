package ph.sm.colorbubbles;

import ch.aplu.android.Actor;

public class Heart extends Actor {
	
	private int deathCount;
	public Heart() {
		super("heart", 5);
		deathCount = 100;
	}
	
	public void act() {
		show(4 - deathCount/21);
		if (deathCount < 0)
			removeSelf();
		else
			deathCount--;
	}
}
