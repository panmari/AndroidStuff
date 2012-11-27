// AndroidEx18.java

package sm.phbern.basketball;

import ch.aplu.android.*;
import android.graphics.Point;

public class AndroidEx18 extends GameGrid implements GGFlingListener,
		GGActorCollisionListener {
	private final double vFactor = 1 / 120.0;
	private double dimFactor;
	private double z; // zoom factor
	private double roomHeight = 5;
	private double roomWidth;
	private Basket basket;
	protected GGStatusBar status;
	protected int hits = 0;

	public AndroidEx18() {
		super(WHITE, false, true, windowZoom(600));
		setScreenOrientation(LANDSCAPE);
		status = addStatusBar(40);
	}

	public void main() {
		dimFactor = 600.0 / roomHeight;
		z = getZoomFactor();
		roomWidth = toPhysical(getNbHorzCells());
		addFlingListener(this);
		setSimulationPeriod(30);
		basket = new Basket();
		addActor(basket, new Location(toPix(0.5), toPix(1.8)));
		basket.setCollisionRectangle(new Point(0, -20), 40, 20);
		drawBg();
		doRun();
		//status.setText("Fling the ball!");
	}

	public boolean flingEvent(Point start, Point end, GGVector velocity) {
		if (end.x > toPix(6)) {
			Ball ball = new Ball(this, vFactor * velocity.x, vFactor
					* velocity.y);
			addActorNoRefresh(ball, new Location(end.x, end.y));
			ball.addCollisionActor(basket);
			ball.addActorCollisionListener(this);
			ball.setCollisionCircle(new Point(0, 0), 24);
		}
		return true;
	}

	public void drawBg() {
		GGBackground bg = getBg();
		bg.setLineWidth(4);
		bg.setPaintColor(GREEN);
		bg.drawLine(toPix(6), 0, toPix(6), toPix(roomHeight));
	}

	protected int toPix(double x) // x physical coordinates
	{
		return (int) (dimFactor * z * x);
	}

	protected double toPhysical(int x) // x real coordinates (pixels)
	{
		return x / dimFactor / z;
	}

	public int collide(Actor actor1, Actor actor2) {
		if (((Ball) actor1).getVelocityY() > 0.1) {
			hits++;
			status.setText("Hits " + hits);
			playTone(1200, 20);
			((Ball) actor1).setVelocityX(0);
		}
		return 10;
	}
}

class Ball extends Actor {
	private final double g = 9.81; // in m/s^2
	private double x, y; // in m
	private double vx, vy; // in m/s
	private double dt = 0.025; // in s
	private AndroidEx18 app;

	public Ball(AndroidEx18 app, double vx, double vy) {
		super("ball");
		this.app = app;
		this.vx = vx;
		this.vy = vy;

	}

	public void reset() {
		x = app.toPhysical(getX());
		y = app.toPhysical(getY());
	}

	public void act() {
		vy = vy + g * dt;
		x = x + vx * dt;
		y = y + vy * dt;
		setLocation(new Location(app.toPix(x), app.toPix(y)));
		if (app.toPix(x) < getWidth(0))
			vx = -vx;
		if (!isInGrid())
			removeSelf();
	}

	protected void setVelocityX(double vx) {
		this.vx = vx;
	}

	protected double getVelocityY() {
		return vy;
	}
}

class Basket extends Actor {
	public Basket() {
		super("basket");
	}
}