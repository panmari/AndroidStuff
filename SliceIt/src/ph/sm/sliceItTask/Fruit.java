package ph.sm.sliceItTask;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

/**
 * A fruit has a initial x and y coordinate and x velocity.
 * The velocity in y direction starts at zero and accelerates more or less
 * physically correct through the method movePhysically()
 * @author panmari
 */
public abstract class Fruit extends Actor {
	private float x,y, yVel;
	private final float acc = 9.81F;
	private SliceIt gg;
	private float xVel;
	
	public Fruit(String sprite, SliceIt gg, float xVel) {
		super(true, sprite, 2);
		this.gg = gg;
		this.xVel = xVel;
	}
	
	public void reset() {
		this.x = getPixelLocation().x;
		this.y = getPixelLocation().y;
	}
	
	public void act() {
		movePhysically();
		turn(10); //for pretty effects!
	}

	private void movePhysically() {
	    float dt = 2 * gg.getSimulationPeriod() / 1000F;
	    yVel = yVel + acc * dt;
	    x = x + xVel*dt;
	    y = y + yVel*dt;
	    setLocation(new Location(Math.round(x), Math.round(y)));
	    cleanUp();
	}

	/**
	 * Removes itself if it's outside of the grid,
	 * showing a message if it wasn't sliced until then.
	 */
	private void cleanUp() {
		if (!isInGrid()) {
	    	if (isNotSliced())
	    		gg.showToast("You missed one!");
	    	removeSelf();
	    }
	}
	
	private boolean isNotSliced() {
		return getIdVisible() == 0;
	}

	public void splatter() {
		if (isNotSliced()) {
			show(1);
			gg.increasePoints();
		}
	}	
}
