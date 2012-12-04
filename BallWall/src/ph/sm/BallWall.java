// BallWall.java

package ph.sm;

//BallWall.java
import ch.aplu.android.*;
import android.graphics.*;

public class BallWall extends GameGrid {
	private final double boardSize = 10; // m
	private double length;
	private GGComboSensor sensor;
	private GGPanel p;
	private double holeSize;

	public BallWall() {
		super(windowZoom(600));
	}

	public void main() {
		// Coordinate system in user coordinates, origin in court center
		p = getPanel(-boardSize / 2, boardSize / 2, 0.5);
		p.clear(Color.LTGRAY);
		setSimulationPeriod(30);
		length = 5;
		sensor = GGComboSensor.init(this);
		Ball ball = new Ball(this);
		addActor(ball, new Location(getNbHorzCells() / 2, getNbVertCells() / 2));
		Wall obstacle = new Wall(p, length);
		addActor(obstacle, new Location(p.toPixelX(4), p.toPixelY(0)));
		holeSize = p.toUserDx(ball.getWidth(0)/2 + 3);
		
		//make target area and some holes
		makeHole(new PointD(3,4), Color.GREEN);
		makeHole(new PointD(0, 2), Color.BLACK);
		makeHole(new PointD(1, 2), Color.BLACK);
		makeHole(new PointD(2, 2), Color.BLACK);
		makeHole(new PointD(-1, -2), Color.BLACK);
		
		ball.addCollisionActor(obstacle);
		ball.addActorCollisionListener(ball);
		doRun();
	}
	
	private void makeHole(PointD center, int color) {
		p.setPaintColor(color);
		p.circle(center, holeSize, true);
	}

	public double[] getSensorValues() {
		return sensor.getAcceleration(0);
	}

	public GGPanel getCustomizedPanel() {
		return p;
	}
}

class Ball extends Actor {
	private BallWall app;
	// Physical variables (all in physical units)
	private double x, y; // Position (m)
	private double vx, vy; // Velocity (m/s)
	private double ax, ay; // Acceleration (m/s^2)
	private final double dt = 0.03; // Integration interval (s)
	private final double f = 0.2; // Friction (s^-1)
	private boolean bumped;

	public Ball(BallWall app) {
		super("marble");
		this.setCollisionCircle(new Point(0,0), 16);
		this.app = app;
	}
	
	public void act() {
		double[] a = app.getSensorValues();
		double gx = -a[4];
		double gy = a[3];

		// New acceleration:
		ax = gx - f * vx;
		ay = gy - f * vy;

		// New velocity:
		double vxNew = vx + ax * dt;
		double vyNew = vy + ay * dt;

		// New position:
		double xNew = x + vxNew * dt;
		double yNew = y + vyNew * dt;

		setLocation(new Location(app.getCustomizedPanel().toPixelX(xNew), app.getCustomizedPanel().toPixelY(yNew)));
		
		vx = vxNew;
		vy = vyNew;
		x = xNew;
		y = yNew;
		
		if (bumped) {
			vx = -vx;
			bumped = false;
		}
		
		switch(app.getCustomizedPanel().getColor(this.getLocation())) {
		case Color.BLACK:
			app.showToast("Reset ball");
			reset();
			break;
		case Color.GREEN:
			app.showToast("You win!");
			app.doPause();
			break;
		}
	}
	
	public void reset() {
		// Physical initial conditions:
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;
	}
	
	@Override
	public int collide(Actor ballActor, Actor obstacle) {
		L.d("crash!");
		bumped = true;
		return 1;
	}
}

class Wall extends Actor {
	
	GGPanel p;
	private int length;
	public Wall(GGPanel p, double length) {
		super("marble");
		this.length = p.toPixelDx(length);
		this.p = p;
	}
	
	public void reset() {
		Point start = new Point(getX(), getY() - length/2);
		Point end = new Point(getX(), getY() + length/2);
		setCollisionLine(start, end);
		p.setPaintColor(Color.DKGRAY);
		p.line(p.toUserPoint(start), p.toUserPoint(end));
	}
}