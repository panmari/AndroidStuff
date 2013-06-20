// MinimalPong.java

package ph.sm.touchstick;

import java.util.LinkedList;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class TouchStickDemo extends GameGrid implements GGActorCollisionListener{
	
	private Balloon balloon;
	
	public TouchStickDemo() {
		super("town", windowZoom(500));
	}

	public void main() {
		setSimulationPeriod(30);
		balloon = new Balloon();
		addActor(balloon, new Location(100, 100));
		addTouchListener(balloon, GGTouch.drag | GGTouch.press | GGTouch.release);
		balloon.addActorCollisionListener(this);
		balloon.setCollisionCircle(new Point(0,0), 20);
		for (int i = 0; i < 50; i++) {
			Dart d = new Dart();
			balloon.addCollisionActor(d);
			addActorNoRefresh(d, new Location(-10, -10)); //out of sight
		}
		doRun();
	}

	public int collide(Actor actor1, Actor actor2) {
		actor1.show(1);
		playTone(1200, 20);
		refresh();
		doPause();
		return 0;
	}

	public void act() {
		int spawnRate = Math.max(getNbCycles()/-50 + 40, 5);
		if (getNbCycles() % spawnRate == 0) {
			Dart.launchNext();
		}
	}
}

class Dart extends Actor {
	
	static private LinkedList<Dart> dartPool =  new LinkedList<Dart>();
	public Dart() {
		super("dart");
		dartPool.add(this);
	}
	
	public void reset() {
		setCollisionSpot(new Point(40, 0));
	}
	
	public static void launchNext() {
		dartPool.poll().launch();
	}
	
	public void launch() {
		int y = (int)(Math.random()*getNbVertCells());
		setLocation(new Location(-10, y));
		setActEnabled(true);
	}
	
	public void act() {
		if (getX() < getNbHorzCells() + 60) //as long as in sight -> fly
			move();
		else {
			setActEnabled(false);
			if (!dartPool.contains(this)) //be reallyreally sure, that its only inserted once
				dartPool.add(this);
			else L.d("There was some timing issue, act was called once too often.");
		}
	}
}

class Balloon extends Actor implements GGTouchListener {
	
	private Location initialLoc;
	private boolean moving = false;
	
	public Balloon() {
		super("balloon", "crash");
	}
	
	public void act() {
		if (isMoveValid() && moving)
			move();
	}

	@Override
	public boolean touchEvent(GGTouch touch) {
		Location loc = new Location(touch.getX(), touch.getY());
		switch (touch.getEvent()) {
		case GGTouch.press:
			initialLoc = loc;
			moving = true;
			break;
		case GGTouch.drag:
			{//if (initialLoc.getDistanceTo(loc) < 50) { 
				double dir = initialLoc.getDirectionTo(loc);
				setDirection(dir);
			}
			break;
		case GGTouch.release:
			moving = false;
			break;
		}
		return true;
	}
}