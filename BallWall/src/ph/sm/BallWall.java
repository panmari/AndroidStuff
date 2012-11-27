// BallWall.java

package ph.sm;

//BallWall.java
import ch.aplu.android.*;
import android.graphics.*;

public class BallWall extends GameGrid implements GGActorCollisionListener {
	protected final double boardSize = 10; // m
	protected double edge;
	protected GGComboSensor sensor;
	protected GGPanel p;
	private double holeSize;

	public BallWall() {
		super(windowZoom(600));
	}

	public void main() {
		// Coordinate system in user coordinates, origin in court center
		p = getPanel(-boardSize / 2, boardSize / 2, 0.5);
		p.clear(GRAY);
		p.setAutoRefreshEnabled(false);
		setSimulationPeriod(30);
		edge = 0.5;
		sensor = GGComboSensor.init(this);
		Ball ball = new Ball(this);
		addActor(ball, new Location(getNbHorzCells() / 2, getNbVertCells() / 2));
		Obstacle obstacle = new Obstacle(p, edge);
		addActor(obstacle, new Location(p.toPixelX(4), p.toPixelY(0)));
		holeSize = p.toUserDx(ball.getWidth(0)/2 + 3);
		makeHole(new PointD(3,4), Color.GREEN);
		makeHole(new PointD(0, 2), Color.BLACK);
		makeHole(new PointD(1, 2), Color.BLACK);
		makeHole(new PointD(2, 2), Color.BLACK);
		makeHole(new PointD(-1, -2), Color.BLACK);
		ball.addCollisionActor(obstacle);
		ball.addActorCollisionListener(this);
		doRun();
	}
	
	private void makeHole(PointD center, int color) {
		p.setPaintColor(color);
		p.circle(center, holeSize, true);
	}

	@Override
	public int collide(Actor ballActor, Actor obstacle) {
		L.d("crash!");
		Ball ball = (Ball)ballActor;
		return 10;
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

	public Ball(BallWall app) {
		super("marble");
		this.app = app;
	}
	
	public void act() {
		double[] a = app.sensor.getAcceleration(0);
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

		setLocation(new Location(app.p.toPixelX(xNew), app.p.toPixelY(yNew)));

		vx = vxNew;
		vy = vyNew;
		x = xNew;
		y = yNew;
		
		switch(app.p.getColor(getLocation())) {
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
}

class Obstacle extends Actor {
	
	GGPanel p;
	private double edge;
	public Obstacle(GGPanel p, double edge) {
		super();
		this.edge = edge;
		this.p = p;
	}
	
	public void reset() {
		//setCollisionRectangle(new Point(0,0), p.toPixelDx(edge), p.toPixelDy(edge));
		p.setPaintColor(Color.BLACK);
		p.move(p.toUserX(getX()), p.toUserY(getY()));
		p.rectangle(edge, edge, true);
	}
}

class Hole {
	public Hole(GGPanel p, PointD center, double radius, int color) {
		
	}
}