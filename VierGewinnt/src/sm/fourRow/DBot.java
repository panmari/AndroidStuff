package sm.fourRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class DBot extends ComputerPlayer {

	public DBot(ArrayManager am, int nbPlayer) {
		super(am, nbPlayer);
	}

	@Override
	public int getColumn() {
		ArrayList<Integer> possibleSolutions = new ArrayList<Integer>();
		ArrayList<Integer> lastResort = new ArrayList<Integer>();
		for (int x = 0; x < xMax; x++)
			lastResort.add(x);
		
		int enemyPlayer = (thisPlayer + 1) % 2;

		if (isBoardEmpty()) {
			debugInfo("me first, me choose middle!");
			return 4;
		}

		// Can I win in this turn?
		for (int column = 0; column < xMax; column++) {
			if (insertToken(thisPlayer, column)) {
				if (getLines(4, thisPlayer) > 0) {
					debugInfo("Found something that makes me win: " + column);
					removeTopmostToken(column);
					return column;
				}
				removeTopmostToken(column);
			}

		}

		// Can enemy win in his next turn?
		for (int column = 0; column < xMax; column++) {
			if (insertToken(enemyPlayer, column)) {
				if (getLines(4, enemyPlayer) > 0) {
					debugInfo("Found something that makes enemy win: " + column);
					removeTopmostToken(column);
					return column;
				}
				removeTopmostToken(column);
			}
		}

		int currentTripples = getLines(3, enemyPlayer);
		int currentDoubles = getLines(2, enemyPlayer);
		
		// stay defensive! try to destroy enemies chances to win:
		// put all these possibilities into ArrayList: possibleSolutions
		for (int column = 0; column < xMax; column++) {
			if (insertToken(enemyPlayer, column)) {
				if (getLines(3, enemyPlayer) > currentTripples) {
					debugInfo("Found something good: " + column);
					possibleSolutions.add(column);
				}
				if (getLines(2, enemyPlayer) > currentDoubles) {
					debugInfo("Found something (maybe) valuable: " + column);
					possibleSolutions.add(column);
				}
				removeTopmostToken(column);
			}
		}

		// does any solution enable my enemy to win next turn?
		Iterator<Integer> posSolu = possibleSolutions.iterator();
		int possibleColumn;
		while (posSolu.hasNext()) {
			possibleColumn = posSolu.next();
			if (insertToken(thisPlayer, possibleColumn)) {
				if (insertToken(enemyPlayer, possibleColumn)) {
					if (getLines(4, enemyPlayer) > 0) {
						posSolu.remove();
						lastResort.remove(new Integer(possibleColumn));
						debugInfo("removed solutionzzz, left is: " + possibleSolutions);
					}
					removeTopmostToken(possibleColumn);
				}
				removeTopmostToken(possibleColumn);
			}
		}

		// prefer solutions in the middle of field:
		int nrOfSolutions = possibleSolutions.size();
		for (int i = 0; i < nrOfSolutions; i++) {
			if (possibleSolutions.get(i) > 1 && possibleSolutions.get(i) < 5)
				possibleSolutions.add(possibleSolutions.get(i));
		}

		// if there are any solutions left, return a random one
		// One column may be in there multiple times
		// -> it's a better move -> it's probability is higher!
		if (!possibleSolutions.isEmpty()) {
			Collections.shuffle(possibleSolutions);
			debugInfo("Chose first of: " + possibleSolutions);
			return (int) possibleSolutions.get(0);
		} else if (!lastResort.isEmpty()) {
			Collections.shuffle(lastResort);
			debugInfo("There are no good ideas, choosing from last resort: " + lastResort);
			return (int) possibleSolutions.get(0);
		} else return getRandomNotFullColumn();
	}

	private int getRandomNotFullColumn() {
		Random rnd = new Random();
		do {
			int rndColumn = rnd.nextInt(xMax);
			if (insertToken(thisPlayer, rndColumn)) {
				debugInfo("Chose randomly: " + rndColumn);
				removeTopmostToken(rndColumn);
				return rndColumn;
			}

		} while (!isBoardFull());
		return -1; // this shouldn't happen
	}

	@Override
	public String getNameAndDescription() {
		return "DBot - simple algorithm for determining good and bad columns";
	}

}
