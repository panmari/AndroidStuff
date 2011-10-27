// nimAndroid.java

package ch.mazzzy.nimAndroid;

import android.graphics.Color;
import ch.aplu.android.Actor;
import ch.aplu.android.GGNavigationEvent;
import ch.aplu.android.GGNavigationListener;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;
public class nimAndroid extends GameGrid implements GGTouchListener,
	GGNavigationListener {
private int nbPearls = 0;
private int nbTakenPearls;
private int nbRows;
private int activeRow;
private ComputerPlayer cp;
private final boolean misereMode = true;

public nimAndroid() {
	//size decides how many pearls are placed
	super(5,5,60);
}

public void main() {
	getBg().clear(Color.rgb(80, 15, 247));
	addTouchListener(this, GGTouch.click);
	addNavigationListener(this);
	cp = new ComputerPlayer(this, misereMode);
	nbRows = getNbVertCells();
	init();
}

public void init() {
	nbPearls = 0;
	removeAllActors();
	int nb = getNbHorzCells();
	cp.reset();
	for (int k = 0; k < nbRows; k++) {
		for (int i = 0; i < nb; i++) {
			Pearl pearl = new Pearl();
			addActor(pearl, new Location(i, k));
			cp.updatePearlArrangement(k, +1);
			nbPearls++;
		}
		nb--;
	}
	prepareNextHumanMove(); // human starts
	refresh();
	setStatusText(nbPearls + " Pearls. Remove any number of pearls " +
			"from same row and press the menu-Button to continue..");
}

public boolean touchEvent(GGTouch touch) {
	Location loc = toLocationInGrid(touch.getX(), touch.getY());
	Actor pearlAtClick = getOneActorAt(new Location(loc), Pearl.class);
	if (pearlAtClick != null) {
		int y = pearlAtClick.getY();

		if (activeRow != 0 && activeRow != y)
			showToast("You must remove pearls from the same row.");
		else {
			activeRow = y;
			pearlAtClick.removeSelf();
			nbPearls--;
			setStatusText(nbPearls + " pearls. Menu-Button to continue.");
			nbTakenPearls++;
			cp.updatePearlArrangement(y, -1);
			if (nbPearls == 0) {
				if (misereMode)
					gameOver(false);
				else gameOver(true);
			}
			refresh();
		}
	}
	return true;
}

public void gameOver(boolean humanWins) {
	setStatusText("Press return to play again."); 
	if (humanWins) {
		showToast("You won!");
		addActor(new YouWin(), new Location(2, 2));
	} else showToast("You lost!");
}

private void prepareNextHumanMove() {
	nbTakenPearls = 0;
	setStatusText(nbPearls + " pearls remaining. Your move now.");
	activeRow = 0; // Spieler darf neue "Ziehreihe" bestimmen		
}

public void navigationEvent(GGNavigationEvent event) {
	switch (event)
    {
      case MENU_DOWN:
    	if (nbPearls == 0){
    		showToast("Game Over! Press return to play again");
    	} else if (nbTakenPearls == 0)
  			setStatusText("You have to remove at least 1 Pearl!");
  		else {
  			cp.makeMove();
  			refresh();
  			nbPearls = getNumberOfActors(Pearl.class);
  			if (nbPearls == 0) {
  				if (misereMode)
  					gameOver(true);
  				else gameOver(false);
  			}
  			else prepareNextHumanMove();
  		}
        break;
      case BACK_DOWN:
        init();
        break;
  
    }
}

}

class Pearl extends Actor {
	public Pearl() {
		super("pearl");
	}
}
class YouWin extends Actor {
	public YouWin() {
		super("you_win");
	}

}


