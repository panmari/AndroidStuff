package ph.sm.jewels;

import java.util.LinkedList;

import ch.aplu.android.GGPanel;
import ch.aplu.android.GGPushButton;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGVector;
import ch.aplu.android.GameGrid;
import ch.aplu.android.PointD;

public class JewelsGame extends GameGrid {
	
	private GGStatusBar status;
	private GGPanel p;
	private LinkedList<Jewel> jewels = new LinkedList<Jewel>();

	public void main() {
		getBg().clear(WHITE);
		p = getPanel(-10, 10, 0.5);
		Hexagon hexagon = new Hexagon(p);
		addActorNoRefresh(hexagon, toLocation(p.toPixelPoint(new PointD(0,0))));
		for (int i = 1; i < 10; i++) {
			jewels.add(new Jewel(hexagon));
		}
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
}
