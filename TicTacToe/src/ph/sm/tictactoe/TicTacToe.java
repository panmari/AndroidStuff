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
	
	public TicTacToe() {
		super(3, 3, cellZoom(60), Color.WHITE);
		status = addStatusBar(30);
	}
	
	public void main() {
		status.setText("Touch on a field to place your sign. X starts.");
		int currentPlayer = 0;
		addTouchListener(this, GGTouch.click);
	}

	@Override
	public boolean touchEvent(GGTouch touch) {
		Location touchLoc = toLocationInGrid(touch.getX(), touch.getY());
		addActor(new TicTacToeMark(currentPlayer), touchLoc);
		nextPlayers();
		return true;
	}
	
	private void nextPlayers() {
		currentPlayer = (currentPlayer + 1) % 2;
	}
}

class TicTacToeMark extends Actor {
	public TicTacToeMark(int player) {
		super("mark_x", "mark_o");
		show(player);
	}
}
