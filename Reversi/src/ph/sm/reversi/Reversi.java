// Reversi.java

package ph.sm.reversi;

import java.util.ArrayList;

import android.graphics.Color;
import ch.aplu.android.Actor;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;
//Reversi.java

public class Reversi extends GameGrid implements GGTouchListener {
	
	/**
	 * 0 is white
	 * 1 is black
	 */
	int currentPlayer = 0;
	String[] playerNames = { "White", "Black" };
	private GGStatusBar status;
	
	public void main() {
		getBg().clear(Color.argb(255, 245, 245, 220));
		status.setText("Reversi - White plays first");

		addActor(new PlayStone(0), new Location(3, 3));	
		addActor(new PlayStone(0), new Location(4, 4));
		addActor(new PlayStone(1), new Location(4, 3));
		addActor(new PlayStone(1), new Location(3, 4));

		addTouchListener(this, GGTouch.click);
	}
	
	public Reversi() {
		super(8, 8, cellZoom(60), Color.BLACK);
		status = addStatusBar(30);
	}

	public boolean touchEvent(GGTouch touch) {
		Location location = toLocationInGrid(touch.getX(), touch.getY());

		// target location must be empty and have neighbour
		if (getActorsAt(location).isEmpty() && hasNeighbours(location)) {
			// ...set stone
			PlayStone newStone = new PlayStone(currentPlayer);
			addActor(newStone, location);			
			turnCorneredStones(newStone);

			//check if game has ended
			if (!endOfGame()) {
				// Change player if not
				currentPlayer = (currentPlayer + 1) % 2;
				status.setText(playerNames[currentPlayer] + " plays");
			}
			
			refresh();
		}
		return true;
	}

	private void turnCorneredStones(PlayStone newStone) {
		// Check for stones in all 8 directions and if they can be turned
		Location newStoneLoc = newStone.getLocation();
		for (int c = 0; c <= 315; c += 45) {
			ArrayList<Actor> stonesInLine = new ArrayList<Actor>();
			Location inspectedLoc = newStoneLoc.getNeighbourLocation(c);
			Actor inspectedStone = getOneActorAt(inspectedLoc);
			boolean hasSamePlayerId = false;

			while (inspectedStone != null && !hasSamePlayerId) {
				if (inspectedStone.getIdVisible() != currentPlayer) {
					stonesInLine.add(inspectedStone);
					inspectedLoc = inspectedLoc.getNeighbourLocation(c);
					inspectedStone = getOneActorAt(inspectedLoc);
				} else {
					hasSamePlayerId = true;
				}
			}

			// Turn stones along this line
			if (hasSamePlayerId) {
				for (Actor s : stonesInLine)
					s.show(currentPlayer);
			}
		}
	}

	/**
	 * Checks if cell has at least on neighbour in the north, east, south or west
	 * @param location the examined location
	 * @return true if such a neighbour exists, false if not
	 */
	private boolean hasNeighbours(Location location) {
		for (Location l : location.getNeighbourLocations(0.5)) {
			if (getOneActorAt(l) != null)
				return true;
		}
		return false;
	}

	// Checks if board is full and counts score
	public boolean endOfGame() {
		int countBlack = 0;
		int countWhite = 0;
		ArrayList<Actor> playStones = getActors(PlayStone.class);
		
		for (Actor a : playStones) {
			if (a.getIdVisible() == 0)
				countWhite++;
			else
				countBlack++;
		}

		if (playStones.size() == 64) {
			if (countBlack > countWhite)
				status.setText("Black wins! - " + countBlack + ":" + countWhite);
			else if (countBlack < countWhite)
				status.setText("White wins! - " + countWhite + ":" + countBlack);
			else
				status.setText("The game ended in a tie!");
			return true;
		}
		else return false;
	}

}


