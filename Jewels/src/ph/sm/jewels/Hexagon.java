package ph.sm.jewels;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGPanel;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.L;
import ch.aplu.android.PointD;

public class Hexagon extends Actor implements GGTouchListener {
	
	private GGPanel p;
	private int angle = 0;
	private final int PER_PERIOD_ANGLE = 10;

	public Hexagon(GGPanel p) {
		super(true, "hexagon_transparent");
		this.p = p;		
	}

	public void act() {
		turn(angle);
	}
	
	@Override
	public boolean touchEvent(GGTouch touch) {
		if (touch.getEvent() == GGTouch.press) { // turn on rotating state
			PointD userPoint = p.toUserPoint(new Point(touch.getX(), touch.getY()));
			if (userPoint.x > 0) //touch on the right side of display
				angle = PER_PERIOD_ANGLE;
			else angle = -PER_PERIOD_ANGLE;
		} // deactivate rotation on release
		else {
			angle = 0;
		}
		return true;
	}
	
	
}
