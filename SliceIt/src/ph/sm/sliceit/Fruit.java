package ph.sm.sliceit;
import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.Location;

/**
 * A fruit has a initial x and y coordinate and x velocity.
 * The velocity in y direction starts at zero and accelerates more or less
 * physically correct through the method @movePhysically
 * @author panmari
 */
public abstract class Fruit extends Actor implements GGActorCollisionListener {
	private double x,y, vy, vx;
	private final double acc = 9.81F;
	
	public Fruit(String sprite, double xVel) {
		super(true, sprite, 2);
		this.vx = xVel;
	}
	
	public void reset() {
		this.x = getLocation().x;
		this.y = getLocation().y;
		this.vy = 0;
	}
	
	public void act() {
		movePhysically();
		turn(10); 
	}

	private void movePhysically() {
	    double dt = 0.002 * gameGrid.getSimulationPeriod();
	    vy = vy + acc * dt;
	    x = x + vx*dt;
	    y = y + vy*dt;
	    setLocation(new Location((int)(x + 0.5), (int)(y + 0.5)));
	    cleanUp();
	}

	/**
	 * Removes itself if it's outside of the grid,
	 * showing a message if it wasn't sliced until then.
	 */
	private void cleanUp() {
		if (!isInGrid()) {
	    	if (isNotSliced())
	    		FruitFactory.nbMissed++;
	    	removeSelf();
	    }
	}
	
	private boolean isNotSliced() {
		return getIdVisible() == 0;
	}

	public void splatter() {
		if (isNotSliced()) {
			show(1);
			FruitFactory.nbHit++;
		}
	}
	
	public int collide(Actor actor1, Actor actor2) {
		((Fruit) actor1).splatter();
		return 1000;
	}
}
