// SliceIt.java

package ph.sm.sliceIt;

import ch.aplu.android.*;
import android.graphics.Color;
import android.graphics.Point;

/** 
 * 
 */
public class SliceIt extends GameGrid implements GGTouchListener {
	private GGPanel p;
	private Location sliceLoc;
	private int points;

	public SliceIt() {
		super();
	}

	public void main() {
		setStatusText("SliceIt started, GG v " + getVersion());
		addTouchListener(this, GGTouch.drag | GGTouch.release);
		addActor(new FruitFactory(this, 50), new Location(0,0));
		this.p = getPanel();
		p.setPaintColor(Color.RED);
		setSimulationPeriod(20);
		doRun();
	}

	public boolean touchEvent(GGTouch touch) {
		switch (touch.getEvent()) {
		case GGTouch.drag:
			sliceLoc = toLocation(touch);
			p.drawPoint(sliceLoc.getX(), sliceLoc.getY());
			break;
		case GGTouch.release:
			getBg().clear();
			sliceLoc = null;
			break;
		}
		refresh();
		return false;
	}

	private Location toLocation(GGTouch touch) {
		return toLocation(new Point(touch.getX(), touch.getY()));
	}

	public Location getSliceLoc() {
		return sliceLoc;
	}
	
	public void increasePoints() {
		points++;
		setStatusText(points + " Points!");
	}
}
