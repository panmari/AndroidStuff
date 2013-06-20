// Sokoban.java

package ph.sm.sokoban;

import ch.aplu.android.*;
import ch.aplu.android.Location.CompassDirection;
import android.graphics.*;

public class Sokoban extends GameGrid implements GGTouchListener {
	private final static SokobanGrid grid = new SokobanGrid(0); // 0, 1, or 2
	private final static int nbHorzCells = grid.getNbHorzCells();
	private final static int nbVertCells = grid.getNbVertCells();
	private final int borderColor = Color.rgb(255, 0, 0);
	private SokobanStone[] stones = new SokobanStone[grid.getNbStones()];
	private SokobanTarget[] targets = new SokobanTarget[grid.getNbStones()];
	private SokobanActor sok;
	private boolean isFinished = false;

	public Sokoban() {
		super(nbHorzCells, nbVertCells, cellZoom(20), Color.LTGRAY);
		setScreenOrientation(LANDSCAPE);
	}

	public void main() {
		GGBackground bg = getBg();
		drawBoard(bg);
		drawActors();
		addTouchListener(this, GGTouch.press);
	}

	private void drawActors() {
		int stoneIndex = 0;
		int targetIndex = 0;

		for (int y = 0; y < nbVertCells; y++) {
			for (int x = 0; x < nbHorzCells; x++) {
				Location location = new Location(x, y);
				int a = grid.getCell(location);
				if (a == 5) // Sokoban actor
				{
					sok = new SokobanActor();
					addActor(sok, location);
				}
				if (a == 3) // Stones
				{
					stones[stoneIndex] = new SokobanStone();
					addActor(stones[stoneIndex], location);
					stoneIndex++;
				}
				if (a == 4) // Targets
				{
					targets[targetIndex] = new SokobanTarget();
					addActor(targets[targetIndex], location);
					targetIndex++;
				}
			}
		}
		setPaintOrder(SokobanTarget.class);
	}

	private void drawBoard(GGBackground bg) {
		bg.clear(Color.WHITE);
		bg.setPaintColor(Color.DKGRAY);
		for (int y = 0; y < nbVertCells; y++) {
			for (int x = 0; x < nbHorzCells; x++) {
				Location location = new Location(x, y);
				int a = grid.getCell(location);
				if (a == 0) // outside
					bg.fillCell(location, Color.LTGRAY);
				if (a == 2) // Border
					bg.fillCell(location, borderColor);
			}
		}
	}

	private boolean canMove(Location location) {
		// Test if try to move into border
		int c = getBg().getColor(location);
		if (c == borderColor)
			return false;
		else // Test if there is a stone
		{
			SokobanStone stone = (SokobanStone) getOneActorAt(location,
					SokobanStone.class);
			if (stone != null) {
				// Try to move the stone
				stone.setDirection(sok.getDirection());
				if (moveStone(stone))
					return true;
				else
					return false;
			}
		}
		return true;
	}

	private boolean moveStone(SokobanStone stone) {
		Location next = stone.getNextMoveLocation();
		// Test if try to move into border
		int c = getBg().getColor(next);
		if (c == borderColor)
			return false;

		// Test if there is another stone
		SokobanStone neighbourStone = (SokobanStone) getOneActorAt(next,
				SokobanStone.class);
		if (neighbourStone != null)
			return false;

		// Move the stone
		stone.setLocation(next);

		// Check if we are at a target
		if (getOneActorAt(next, SokobanTarget.class) != null)
			stone.show(1);
		else
			stone.show(0);
		return true;
	}

	@Override
	public boolean touchEvent(GGTouch touch) {
		if (isFinished)
			return true;
		CompassDirection dir = sok.getLocation().get4CompassDirectionTo(toLocation(touch.getX(), touch.getY()));
		Location next = sok.getLocation().getNeighbourLocation(dir);
		sok.setDirection(dir);
		
		if (canMove(next)) {
			sok.setLocation(next);
		}
		refresh();
		return true;
	}
}

// --------- SokobanActors-------------
class SokobanActor extends Actor {
	public SokobanActor() {
		super(true, "sokoban"); // Rotatable
	}
}

class SokobanTarget extends Actor {
	public SokobanTarget() {
		super("target");
	}
}

class SokobanStone extends Actor {
	public SokobanStone() {
		super("sokobanstone", 2);
	}
}