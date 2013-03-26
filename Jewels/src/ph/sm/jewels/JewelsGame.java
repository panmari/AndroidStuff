package ph.sm.jewels;

import java.util.LinkedList;

import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.GGPanel;
import ch.aplu.android.GGPushButton;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGVector;
import ch.aplu.android.GameGrid;
import ch.aplu.android.PointD;

public class JewelsGame extends GameGrid implements GGActorCollisionListener {
	
	private GGStatusBar status;
	private Hexagon hexagon;
	private GGPanel p;
	private LinkedList<Jewel> jewels = new LinkedList<Jewel>();
	private int score;

	public void main() {
		getBg().clear(WHITE);
		p = getPanel(-10, 10, 0.5);
		hexagon = new Hexagon(p);
		addActorNoRefresh(hexagon, toLocation(p.toPixelPoint(new PointD(0,0))));
		for (int i = 1; i < 10; i++) {
			Jewel j = new Jewel(hexagon);
			jewels.add(j);
			hexagon.addCollisionActor(j);
		}
		hexagon.addActorCollisionListener(this);
		setSimulationPeriod(30);
		doRun();
		status.setText("This is how it begins");
		addTouchListener(hexagon, GGTouch.press | GGTouch.release);
	}
	
	public void act() {
		if (Math.random() < 0.01 && !jewels.isEmpty()) {
			GGVector v = new GGVector(10, 10);
			v.rotate(Math.random()*Math.PI*2);
			PointD spawnPoint = new PointD(v);
			addActor(jewels.pollFirst(), toLocation(p.toPixelPoint(spawnPoint)));
		}
	}
	
	public JewelsGame() {
		super(WHITE, windowZoom(700));
	    status = addStatusBar(30);
	}

	@Override
	public int collide(Actor arg0, Actor arg1) {
		hexagon.eat();
		score++;
		return 0;
	}
}
