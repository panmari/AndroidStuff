package ph.sm.jewels;

import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GameGrid;

public class JewelsGame extends GameGrid {
	
	private GGStatusBar status;

	public void main() {
		doRun();
		status.setText("This is how it begins");
	}
	
	public JewelsGame() {
		super(WHITE, windowZoom(700));
	    status = addStatusBar(30);
	}
}
