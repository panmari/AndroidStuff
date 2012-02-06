package ph.sm.sliceIt;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

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
	    if (!isInGrid()) {
	    	if (!isSliced())
	    		gg.showToast("You missed one!");
	    	removeSelf();
	    }
	}
	
	private boolean isSliced() {
		return getIdVisible() == 1;
	}

	public void splatter() {
		if (!isSliced()) {
			show(1);
			gg.increasePoints();
		}
	}	
}
