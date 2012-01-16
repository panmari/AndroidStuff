// SliceIt.java

package ph.sm.sliceIt;

import ch.aplu.android.*;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * For now: only slicing direct line between press and release!
 * 
 * @author panmari
 * 
 */
public class SliceIt extends GameGrid implements GGTouchListener {
	private Point sliceStart;
	private GGPanel p;

	public SliceIt() {
		super(0, 0, 1);
	}

	public void main() {
		setStatusText("SliceIt started");
		addTouchListener(this, GGTouch.release | GGTouch.press);
		addActor(new Fruit(), new Location(10, 10));
		this.p = getPanel();
		p.setPaintColor(Color.BLUE);
		doRun();
	}

	public boolean touchEvent(GGTouch touch) {
		switch (touch.getEvent()) {
		case GGTouch.press:
			sliceStart = getTouchPoint(touch);
			break;
		case GGTouch.drag:
			slicedLocations.add(toLocation)
		case GGTouch.release:
			this.toLocation(sliceStart).
			p.drawLine(sliceStart, getTouchPoint(touch));
			break;
		}
		refresh();
		return false;
	}

	private Location getTouchPoint(GGTouch touch) {
		return toLocation(new Point(touch.getX(), touch.getY()));
	}
}
