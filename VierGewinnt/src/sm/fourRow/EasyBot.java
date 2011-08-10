package sm.fourRow;

import java.util.Random;

/**
 * Plays randomly apart from the obvious.
 */
public class EasyBot extends ComputerPlayer {

	public EasyBot(ArrayManager am, int nbPlayer) {
		super(am, nbPlayer);
	}

	@Override
	public int getColumn() {
		 // Can I win in this turn?
	    for (int column = 0; column < xMax; column++)
	    {
	    	if(insertToken(thisPlayer, column)) {
	    		if (getLines(4, thisPlayer) > 0) {
			        debugInfo("Found something that makes me win: " + column);
			        removeTopmostToken(column);
			        return column;
	    		}
	    	removeTopmostToken(column);
	    	}
	    	
	    }
		return getRandomNotFullColumn();
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
		return -1; //this shouldn't happen
	}

	@Override
	public String getNameAndDescription() {
		return "EasyBot - plays completely random";
	}
}
