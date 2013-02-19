package ph.sm.colorbubbles;

import ch.aplu.android.Actor;

public class Bubble extends Actor {

	public final int shitCount = 3;
	public Bubble(int type) {
		super("tinti", "gity", "bebi", "bebibf", "shit", "shit");
		show(type);
	}

	public boolean fits(Bubble ball) {
		if (this.isShit() || ball.isShit())
			return false;
		else
			return this.getIdVisible() != ball.getIdVisible() && this.getIdVisible()/2 == ball.getIdVisible()/2;
	}
	
	public boolean isShit() {
		return getIdVisible() > shitCount;
	}

}