// AndroidEx24.java



import ch.aplu.android.*;
import android.graphics.Point;

public class AndroidEx24 extends GameGrid implements GGFlingListener,
		GGActorCollisionListener {
	private final double vFactor = 1 / 15.0;
	private int screenW;
	private int screenH;
	private Basket basket;
	private int startLine;

	private GGStatusBar status;

	public AndroidEx24() {
		super(WHITE, false, true, windowZoom(600));
		setScreenOrientation(LANDSCAPE);
		status = addStatusBar(20);
	}

	public void main() {
		addFlingListener(this);
		getBg().setPaintColor(GREEN);
		screenW = getNbHorzCells();
		screenH = getNbVertCells();
		startLine = screenW * 3 / 4;
		getBg().setLineWidth(4);
		getBg().drawLine(startLine, screenH / 8, startLine,
				screenH * 7 / 8);
		basket = new Basket();
		basket.setCollisionRectangle(new Point(0, -30), 45, 6);
		addActor(basket, new Location(screenH/2, 250));
		setSimulationPeriod(50);
		doRun();
		status.setText("Fling the ball!");
	}

	public boolean flingEvent(Point start, Point end, GGVector velocity) {
		if (end.x  < startLine || start.x < startLine) {
			showToast("Stay behind the line!");
			return true;
		}
		Ball ball = new Ball(vFactor * velocity.x, vFactor * velocity.y);
		L.d("" + velocity);
		addActorNoRefresh(ball, new Location(end.x, end.y));
		ball.addCollisionActor(basket);
		ball.addActorCollisionListener(this);
		return true;
	}

	public int collide(Actor actor1, Actor actor2) {
		Ball b = (Ball) actor1;
		if (b.isFalling()) {
			b.drop();
			playTone(1200, 20);
		}
		return 100;
	}
}

class Ball extends Actor {
	private final double g = 9.81; // in m/s^2
	private double x, y; // in m
	private double vx, vy; // in m/s
	private double dt = 0.4; // in s
	int factor = 10;

	public Ball(double vx, double vy) {
		super("ball");
		this.vx = vx;
		this.vy = vy;

	}

	public boolean isFalling() {
		//careful, the left upper corner is (0,0), so a positive vy means falling
		return vy > 0;
	}

	public void drop() {
		vy += Math.abs(vx)/5;
		vx = vx/10;
	}

	public void reset() {
		x = getX();
		y = getY();
	}

	public void act() {

		vy = vy + g * dt;
		x = x + vx * dt;
		y = y + vy * dt;
		setLocation(new Location(x, y));
		//reflect at wall:
		if (x < this.getWidth(0))
			vx = -vx;
		if (!isInGrid())
			removeSelf();
	}
}

class Basket extends Actor {
	public Basket() {
		super("basket");
	}
}
