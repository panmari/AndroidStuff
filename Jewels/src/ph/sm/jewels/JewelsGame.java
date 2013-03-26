package ph.sm.jewels;

import ch.aplu.android.GGPanel;
import ch.aplu.android.GGPushButton;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GameGrid;
import ch.aplu.android.PointD;

public class JewelsGame extends GameGrid {
	
	private GGStatusBar status;
	private GGPanel p;

	public void main() {
		getBg().clear(WHITE);
		p = getPanel(-10, 10, 0.5);
		Hexagon hexagon = new Hexagon(p);
		addActor(hexagon, toLocation(p.toPixelPoint(new PointD(0,0))));
		setSimulationPeriod(30);
		doRun();
		status.setText("This is how it begins");
		addTouchListener(hexagon, GGTouch.press | GGTouch.release);
		addActor( new GGPushButton("Test"), toLocation(p.toPixelPoint(new PointD(0,0))));
	}
	
	public JewelsGame() {
		super(WHITE, windowZoom(700));
	    status = addStatusBar(30);
	}
}
