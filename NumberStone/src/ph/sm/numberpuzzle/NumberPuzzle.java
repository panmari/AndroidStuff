// MinimalPong.java

package ph.sm.numberpuzzle;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGActorTouchListener;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GameGrid;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class NumberPuzzle extends GameGrid implements GGActorTouchListener {
	
	private Location initialLoc;
	private Actor dragActor;
	
	public NumberPuzzle() {
		super(4, 4, cellZoom(60));
	}

	public void main() {
		getBg().clear(DKGRAY);
		setSimulationPeriod(30);
		NumberStone[] stones = new NumberStone[15];
		for (int i = 0; i < 15; i++) {
			stones[i] = new NumberStone(i);
			stones[i].addActorTouchListener(this, GGTouch.drag
					| GGTouch.release | GGTouch.press);
			addActorNoRefresh(stones[i], getRandomEmptyLocation());
		}
		doRun();
	}
	/**
	 * Only (before) empty locations in a 4-neighbourhood of the initial location 
	 * @param loc
	 * @return
	 */
	private boolean invalidMoveLocation(Location loc) {
		return getNumberOfActorsAt(loc) > 1 || //only the dragged Number is there
				!initialLoc.getNeighbourLocations(0.5).contains(loc); 
	}

	@Override
	public void actorTouched(Actor actor, GGTouch touch, Point spot) {
		switch (touch.getEvent()) {
			case GGTouch.press:
				initialLoc = actor.getLocation();
				dragActor = actor;
				dragActor.setOnTop();
				break;
			case GGTouch.drag:
				dragActor.setPixelLocation(new Point(touch.getX(), touch.getY()));
				break;
			case GGTouch.release:
				// When finger moves out of screen, two release events get spawned 
				// => need to ignore second one 
				if (dragActor != null) { 
					if (invalidMoveLocation(dragActor.getLocation()))
						dragActor.setLocation(initialLoc);
					dragActor.setLocationOffset(new Point(0,0));
					dragActor = null;
					initialLoc = null;
				} else L.d("A second release event was spawned!");
				break;
		}	
	}
}

class NumberStone extends Actor {
	public NumberStone(int id) {
		super("stone", 15);
		show(id);
	}
}