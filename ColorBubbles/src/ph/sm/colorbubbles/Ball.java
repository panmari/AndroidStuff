package ph.sm.colorbubbles;

import ch.aplu.android.Location;

class Ball extends Bubble {
	private double x, y; // in m
	private double vx, vy; // in m/s
	private double dt = 0.030; // in s (simulation period)
	private ColorBubbles app;

	public Ball(ColorBubbles app, int type) {
		super(type);
		this.app = app;
	}

	public void reset() {
		setActEnabled(false);
		setLocation(getLocationStart());
		x = app.p.toUserX(getXStart());
		y = app.p.toUserY(getYStart());
	}

	public void shoot(double vx, double vy) {
		this.vx = vx;
		this.vy = vy;
		setActEnabled(true);
	}

	public void act() {
		x = x + vx * dt;
		y = y + vy * dt;
		setLocation(new Location(app.p.toPixelX(x), app.p.toPixelY(y)));
		if (!isInGrid()) {
			reset();
			app.displayResult();
		}
	}

}
