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

	public SliceIt() {
		super(0, 0, 1, Color.RED);
	}

	public void main() {
		setStatusText("SliceIt started");
		addTouchListener(this, GGTouch.release | GGTouch.press);
		addActor(new Fruit(), new Location(10, 10));
	}

	public boolean touchEvent(GGTouch touch) {
		switch (touch.getEvent()) {
		case GGTouch.press:
			sliceStart = getTouchPoint(touch);
			break;
		case GGTouch.release:
			this.getCanvas().drawLine(sliceStart.x, sliceStart.y, touch.getX(),
					touch.getY(), new Paint());
			break;
		}

		return false;
	}

	private Point getTouchPoint(GGTouch touch) {
		return new Point(touch.getX(), touch.getY());
	}
}
