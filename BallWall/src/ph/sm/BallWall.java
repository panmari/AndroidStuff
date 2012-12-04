// BallWall.java
package ph.sm;

import ch.aplu.android.*;
import android.graphics.*;
import java.util.ArrayList;

public class BallWall extends GameGrid implements GGNavigationListener {
	protected GGComboSensor sensor;
	protected GGPanel p;
	private Ball ball;

	public BallWall() {
		super(windowZoom(600));
	}

	public void main() {
		p = getPanel(-5, 5, 0.5);
		p.clear(Color.LTGRAY);
		setSimulationPeriod(30);
		sensor = GGComboSensor.init(this);
		addNavigationListener(this);

		ball = new Ball(this);
		addActor(ball, new Location(getNbHorzCells() / 2, getNbVertCells() / 2));

		makeWall(ball, new PointD(4, 0), 5, true);
		makeWall(ball, new PointD(-4, 0), 5, true);
		makeWall(ball, new PointD(0, 4), 5, false);
		makeWall(ball, new PointD(0, -4), 5, false);
		makeWall(ball, new PointD(0, -3), 1, true);

		double holeSize = p.toUserDx(ball.getWidth(0) / 2 + 3);
		makeHole(ball, new PointD(3, 4), holeSize, Color.YELLOW);
		makeHole(ball, new PointD(0, 2), holeSize, Color.BLACK);
		/*
		 * makeHole(new PointD(1, 2), holeSize, Color.BLACK); makeHole(new
		 * PointD(2, 2), holeSize, Color.BLACK); makeHole(new PointD(-1, -2),
		 * holeSize, Color.BLACK);
		 */
		doRun();
	}

	private void makeWall(Ball ball, PointD center, double length,
			boolean isVertical) {
		Wall wall;
		if (isVertical)
			wall = new VerticalWall(this, center, length);
		else
			wall = new HorizontalWall(this, center, length);
		addActorNoRefresh(wall,
				new Location(p.toPixelX(center.x), p.toPixelY(center.y)));
		ball.addWall(wall);
	}

	private void makeHole(Ball ball, PointD center, double radius, int color) {
		Hole hole = new Hole(this, center, radius, color);
		addActorNoRefresh(hole,
				new Location(p.toPixelX(center.x), p.toPixelY(center.y)));
		ball.addHole(hole);
	}

	public void navigationEvent(GGNavigationEvent event) {
		if (event == GGNavigationEvent.BACK_DOWN && !isRunning()) {
			ball.reset();
			doRun();
		}
	}

}

// -------------- class Ball -------------------
class Ball extends Actor {
	private BallWall app;
	private ArrayList<Wall> walls = new ArrayList<Wall>();
	private ArrayList<Hole> holes = new ArrayList<Hole>();
	private int nbAct = 0;

	// Physical variables (all in physical units)
	private GGVector r = new GGVector(); // Position (m)
	private GGVector v = new GGVector(); // Velocity (m/s)
	private GGVector a = new GGVector(); // Acceleration (m/s^2)
	private GGVector aHole = new GGVector();
	private final double dt = 0.03; // Integration interval (s)
	private final double f = 0.2; // Friction (s^-1)

	public Ball(BallWall app) {
		super("marble");
		this.app = app;
	}

	protected void addWall(Wall wall) {
		walls.add(wall);
	}

	protected void addHole(Hole hole) {
		holes.add(hole);
	}

	public void act() {
		double[] acc = app.sensor.getAcceleration(0);
		GGVector g = new GGVector(-acc[4], acc[3]);

		// New acceleration:
		a = g.sub(v.mult(f)).add(aHole);

		if (nbAct > 0)
			nbAct--;

		GGCircle circle = new GGCircle(new GGVector(getX(), getY()),
				app.virtualToPixel(18));
		if (nbAct == 0) {
			for (Wall wall : walls) // Assumption: only one wall intersecting
			{
				if (wall.line.isIntersecting(circle)) {
					nbAct = 5;
					if (wall.isVertical()) {
						v.x = -v.x;
						a.x = 0; // on vertical wall->no acceleration in x dir
					}
					else {
						v.y = -v.y;
						a.y = 0; // on horizontal wall->no acceleration in y dir
					}
					
				}
				if (circle.isIntersecting(wall.line.getStartVector())
						|| circle.isIntersecting(wall.line.getEndVector())) {
					nbAct = 5;
					if (wall.isVertical()) {
						v.y = -v.y;
						a.y = 0; // on vertical wall->no acceleration in x dir
					}
					else {
						v.x = -v.x;
						a.x = 0; // on horizontal wall->no acceleration in y dir
					}
				}
			}
		}

		// New velocity:
		GGVector vNew = v.add(a.mult(dt));
		// New position:
		GGVector rNew = r.add(vNew.mult(dt));

		setLocation(new Location(app.p.toPixelX(rNew.x), app.p.toPixelY(rNew.y)));
		r = rNew;
		v = vNew;

		boolean isNearHole = false;
		for (Hole hole : holes) // Assumption: only one wall intersecting
		{
			if (hole.inner.isIntersecting(circle)) {
				GGVector distance = new GGVector(hole.center).sub(r);
				aHole = distance.mult(10);
				isNearHole = true;
				if (distance.magnitude() < 0.5) {
					setPixelLocation(app.p.toPixelPoint(hole.center));
					app.showToast("Game over. Press [BACK] to play");
					app.doPause();
				}
			}
		}
		if (!isNearHole) {
			aHole.x = 0;
			aHole.y = 0;
		}

		if (!isInGrid()) {
			app.showToast("Out. Press [BACK] to play");
			app.doPause();
		}
	}

	public void reset() {
		// Physical initial conditions:
		r.x = 0;
		r.y = 0;
		v.x = 0;
		v.y = 0;
		setLocation(getLocationStart());
	}

}

// -------------- class Wall -------------------
abstract class Wall extends Actor {
	protected BallWall app;
	protected PointD center; // In user coordinates
	protected double length; // In user coordinates
	protected GGPanel p;
	protected GGLine line; // Used for collisions

	public Wall(BallWall app, PointD center, double length) {
		super(new GGBitmap(1, 1).getBitmap()); // Dummy actor
		this.app = app;
		p = app.p;
		this.center = center;
		this.length = length;
	}
	
	abstract public boolean isVertical();
}

// -------------- class VerticalWall -------------------
class VerticalWall extends Wall {
	public VerticalWall(BallWall app, PointD center, double length) {
		super(app, center, length);
	}

	public void reset() {
		p.setPaintColor(Color.DKGRAY);
		PointD start = new PointD(center.x, center.y - length / 2);
		PointD end = new PointD(center.x, center.y + length / 2);
		p.setLineWidth(4);
		p.line(start, end);
		p.circle(start, 0.05, true);
		p.circle(end, 0.05, true);

		Point pixStart = p.toPixelPoint(start);
		Point pixEnd = p.toPixelPoint(end);
		line = new GGLine(new GGVector(pixStart), new GGVector(pixEnd));
	}
	
	public boolean isVertical() {
		return true;
	}
}

// -------------- class HorizontalWall -------------------
class HorizontalWall extends Wall {
	public HorizontalWall(BallWall app, PointD center, double length) {
		super(app, center, length);
	}

	public void reset() {
		p.setPaintColor(Color.DKGRAY);
		PointD start = new PointD(center.x - length / 2, center.y);
		PointD end = new PointD(center.x + length / 2, center.y);
		p.setLineWidth(4);
		p.line(start, end);
		p.circle(start, 0.05, true);
		p.circle(end, 0.05, true);

		Point pixStart = p.toPixelPoint(start);
		Point pixEnd = p.toPixelPoint(end);
		line = new GGLine(new GGVector(pixStart), new GGVector(pixEnd));
	}
	
	public boolean isVertical() {
		return false;
	}

}

// -------------- class Hole -------------------
class Hole extends Actor {
	protected PointD center; // In user coordinates
	protected double radius; // In user coordinates
	protected int color;
	private GGPanel p;
	protected GGCircle inner; // Used for collisions

	public Hole(BallWall app, PointD center, double radius, int color) {
		super(new GGBitmap(1, 1).getBitmap()); // Dummy actor
		p = app.p;
		this.center = center;
		this.radius = radius;
		this.color = color;
	}

	public void reset() {
		p.setPaintColor(Color.GRAY);
		p.circle(center, radius + 0.1, true);
		p.setPaintColor(color);
		p.circle(center, radius, true);

		Point pixCenter = p.toPixelPoint(center);
		int pixRadius = p.toPixelDx(radius);

		inner = new GGCircle(new GGVector(pixCenter), pixRadius);
	}

}
