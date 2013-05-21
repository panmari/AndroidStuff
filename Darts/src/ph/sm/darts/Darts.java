// MinimalPong.java

package ph.sm.darts;

import ch.aplu.android.*;
import android.graphics.Point;

public class Darts extends GameGrid implements GGActorCollisionListener {
	public Darts() {
		super("town", windowZoom(500));
		setScreenOrientation(LANDSCAPE);
	}

	public void main() {
		setSimulationPeriod(30);
		Dart dart = new Dart();
		addActor(dart, new Location(-10, -10)); //out of sight
		addTouchListener(dart, GGTouch.press);
		dart.setCollisionSpot(new Point(45, 0));

		for (int i = 0; i < 15; i++) {
			Balloon balloon = new Balloon();
			Location loc = new Location((int) (400 * Math.random() + 100),
					(int) (420 * Math.random() + 40));
			// add balloon to gamegrid with direction southwards
			addActor(balloon, loc.toReal(), Location.SOUTH);
			dart.addCollisionActor(balloon);
			dart.addActorCollisionListener(this);
		}
		doRun();
		getBg().drawLine(100, 0, 100, getNbVertCells());
		showToast("Click to the left of the line to launch a dart arrow.");
	}

	public int collide(Actor actor1, Actor actor2) {
		actor2.removeSelf(); 
		playTone(1200, 20);
		return 0;
	}
}

// ------class Dart ------------------
class Dart extends Actor implements GGTouchListener {
	public Dart() {
		super("dart");
	}

	public boolean touchEvent(GGTouch touch) {
		Location loc = gameGrid.toLocationInGrid(touch.getX(), touch.getY());
		if(loc.x < 100 && this.getX() > getNbHorzCells())
			setLocation(loc);
		return true;
	}
	
	public void act() {
		move(15);
	}
}

class Balloon extends Actor {
	public Balloon() {
		super("balloon");
	}
	
	public void act() {
		move(1);
	}
}