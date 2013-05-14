package ph.sm.tictactoe;

import android.graphics.Color;
import ch.aplu.android.Actor;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;

public class TicTacToe extends GameGrid implements GGTouchListener{
	
	private GGStatusBar status;
	int currentPlayer;
	private boolean gameOver;
	
	public TicTacToe() {
		super(3, 3, cellZoom(70), Color.BLACK);
		status = addStatusBar(30);
	}
	
	public void main() {
		getBg().clear(Color.LTGRAY);
		currentPlayer = 0;
		addTouchListener(this, GGTouch.click);
		status.setText("Touch a field to place your mark.");
		refresh();
	}

	@Override
	public boolean touchEvent(GGTouch touch) {
		Location touchLoc = toLocationInGrid(touch.getX(), touch.getY());
		if (gameOver || !isEmpty(touchLoc))
			return true;
		TicTacToeMark newMark = new TicTacToeMark(currentPlayer);
		addActor(newMark, touchLoc);
		if (!isGameFinished(newMark))
			nextPlayer();
		else {
			status.setText("Game finished!");
			addActor(new Actor("gameover"), new Location(1,1));
			gameOver = true;
		}
		return true;
	}
	
	/**
	 * checks row and column of the new mark and both diagonals
	 * (even if new mark does not lie on a diagonal)
	 * @param mark, the new mark
	 * @return true, if a row of 3 is achieved
	 */
	private boolean isGameFinished(TicTacToeMark mark) {
		int hitX = 0, hitY = 0, hitD1 = 0, hitD2 = 0;
		
		//is board full?
		if (getActors().size() == getNbHorzCells()*getNbVertCells())
			return true;
		
		for (int x = 0; x < getNbHorzCells(); x++) {
			TicTacToeMark markX = getMarkAt(x, mark.getY());
			if (mark.belongsToSamePlayerAs(markX))
				hitX++;
		}
		for (int y = 0; y < getNbVertCells(); y++) {
			TicTacToeMark markY = getMarkAt(mark.getX(), y);
			if (mark.belongsToSamePlayerAs(markY))
				hitY++;
		}
		// assumes square playground
		int size = getNbVertCells();
		for (int i = 0; i < size; i++) {
			TicTacToeMark markD1 = getMarkAt(i, i);
			if (mark.belongsToSamePlayerAs(markD1))
				hitD1++;
			TicTacToeMark markD2 = getMarkAt(size - 1 - i, i);
			if (mark.belongsToSamePlayerAs(markD2))
				hitD2++;
		}		
		return hitX == 3 || hitY == 3 || hitD1 == 3 || hitD2 == 3;
	}
	
	private TicTacToeMark getMarkAt(int x, int y) {
		return (TicTacToeMark) getOneActorAt(new Location(x,y));
	}

	private void nextPlayer() {
		currentPlayer = (currentPlayer + 1) % 2;
	}
}

class TicTacToeMark extends Actor {
	
	public TicTacToeMark(int player) {
		super("mark_x", "mark_o");
		show(player);
	}
	
	public boolean belongsToSamePlayerAs(TicTacToeMark mark) {
		if (mark == null)
			return false;
		else 
			return this.getIdVisible() == mark.getIdVisible();
	}
}
