package ph.sm.jewels;

import java.util.LinkedList;

import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.GGPanel;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGVector;
import ch.aplu.android.GameGrid;
import ch.aplu.android.L;
import ch.aplu.android.Location;
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
			Jewel j = new Jewel();
			jewels.add(j);
			addActorNoRefresh(j, new Location(-100, -100)); //out of sight
			hexagon.addCollisionActor(j);
		}
		hexagon.addActorCollisionListener(this);
		setSimulationPeriod(30);
		doRun();
		setPaintOrder(Hexagon.class, Jewel.class);
		status.setText("This is how it begins");
		addTouchListener(hexagon, GGTouch.press | GGTouch.release);
	}
	
	public void act() {
		if (Math.random() < 0.01 && !jewels.isEmpty()) {
			GGVector v = new GGVector(10, 10);
			v.rotate(Math.random()*Math.PI*2);
			PointD spawnPoint = new PointD(v);
			Jewel spawnJewel = jewels.pollFirst();
			spawnJewel.setLocation(toLocation(p.toPixelPoint(spawnPoint)));
			spawnJewel.setDirection(spawnJewel.getLocation().getDirectionTo(hexagon.getLocation()));
			spawnJewel.setActEnabled(true);
		}
	}
	
	public JewelsGame() {
		super(WHITE, windowZoom(700));
	    status = addStatusBar(30);
	}

	@Override
	public int collide(Actor arg0, Actor jewel) {
		L.d("collision!");
		hexagon.eat();
		jewel.reset();
		score++;
		return 0;
	}
}
