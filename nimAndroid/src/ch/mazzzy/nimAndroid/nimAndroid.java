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
private int nbPearl = 0;
private int nbTakenPearl;
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
	//getBg().clear(Color.BLUE);
	addTouchListener(this, GGTouch.click);
	addNavigationListener(this);
	cp = new ComputerPlayer(this, misereMode);
	nbRows = getNbVertCells();
	init();
}

public void init() {
	nbPearl = 0;
	removeActors(Pearl.class);
	int nb = getNbHorzCells();
	cp.reset();
	for (int k = 0; k < nbRows; k++) {
		for (int i = 0; i < nb; i++) {
			Pearl pearl = new Pearl();
			addActor(pearl, new Location(i, k));
			cp.updatePearlArrangement(k, +1);
			nbPearl++;
		}
		nb--;
	}
	prepareNextHumanMove(); // human starts
	refresh();
	setStatusText(nbPearl + " Pearls. Remove any number of pearls " +
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
			nbPearl--;
			setStatusText(nbPearl + " pearls. Menu-Button to continue.");
			nbTakenPearl++;
			cp.updatePearlArrangement(y, -1);
			if (nbPearl == 0) {
				if (misereMode)
					gameOver("You lost!");
				else gameOver("You won!");
			}
			refresh();
		}
	}
	return true;
}

public void gameOver(String msg) {
	setStatusText("Press return-button to play again."); 
	showToast(msg);
}

private void prepareNextHumanMove() {
	nbTakenPearl = 0;
	setStatusText(nbPearl + " pearls remaining. Your move now.");
	activeRow = 0; // Spieler darf neue "Ziehreihe" bestimmen		
}

public void navigationEvent(GGNavigationEvent event) {
	switch (event)
    {
      case MENU_DOWN:
    	  if (nbTakenPearl == 0)
  			setStatusText("You have to remove at least 1 Pearl!");
  		else {
  			cp.makeMove();
  			refresh();
  			nbPearl = getNumberOfActors(Pearl.class);
  			if (nbPearl == 0) {
  				if (misereMode)
  					gameOver("You won!");
  				else gameOver("You lost!");
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


