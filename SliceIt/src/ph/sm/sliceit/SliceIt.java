// SliceIt.java

package ph.sm.sliceit;

import ch.aplu.android.GGTextField;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;
import ch.aplu.android.TextActor;

/**
 * A simple clone of the well known app "fruit ninja"
 * @author panmari
 */
public class SliceIt extends GameGrid implements GGTouchListener {

	private final int totalNbFruit = 100;
	private Sword sword;
	private FruitFactory ff;
	private GGTextField status;
	private String oldMsg;

	public SliceIt() {
		super(true);
		setScreenOrientation(LANDSCAPE);
	}

	public void main() {
		setStatusText("FruitSlicer started, GG v " + getVersion());
		sword = new Sword();
		ff = new FruitFactory(sword, 35, totalNbFruit);
		addActor(ff, new Location(0,0));
		addActor(sword, new Location(-100, -100));
		int screenHeight = getNbVertCells();
		status = new GGTextField("", new Location(10, screenHeight - 10), false);
		status.show();
		addTouchListener(this, GGTouch.drag | GGTouch.release);
		setSimulationPeriod(30);
		doRun();
		ff.enable();
	}
	
	public boolean touchEvent(GGTouch touch) {
		switch (touch.getEvent()) {
		case GGTouch.drag:
			Location loc = new Location(touch.getX(),touch.getY());
			sword.setLocation(loc);
			break;
		case GGTouch.release:
			sword.setLocation(new Location(-100,-100)); //out of sight
		}
		return true;
	}

	public void act() {
	    // For performance boost don't show message if same
	    String msg = "#hit: " + FruitFactory.nbHit
	      + "     #missed: " + FruitFactory.nbMissed;
	    if (!msg.equals(oldMsg))
	    {
	      status.setText(msg);
	      oldMsg = msg;
	    }
		if (ff.isOutOfFruits() && getActors(Fruit.class).isEmpty())
			gameOver();
	}

	public void gameOver() {
		TextActor text = new TextActor("You smashed "
			      + FruitFactory.nbHit + " out of " + totalNbFruit + " fruits");
			    addActor(text, new Location(10, 10));
		doPause();
	}

	public Sword getSword() {
		return this.sword;
	}
}
