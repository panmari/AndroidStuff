// nimAndroid.java

package ch.mazzzy.nimAndroid;

import ch.aplu.android.*;
import android.graphics.Color;

public class nimAndroid extends GameGrid implements GGTouchListener,
	GGNavigationListener {
private int nbPearl = 0;
private int nbTakenPearl;
private int nbRows;
private int activeRow;
private GGBackground bg;
private ComputerPlayer cp;
private final boolean misereMode = true;

public nimAndroid() {
	//size decides how many pearls are placed
	super(7,8,60);
}

public void main() {
	//getBg().clear(Color.rgb(80, 15, 247));
	getBg().clear(Color.BLUE);
	addTouchListener(this, GGTouch.click);
	addNavigationListener(this);
	cp = new ComputerPlayer(this, misereMode);
	nbRows = getNbVertCells() - 2;
	init();
}

public void init() {
	nbPearl = 0;
	removeActors(Pearl.class);
	int nb = getNbHorzCells() - 2;
	cp.reset();
	bg.clear();
	for (int k = 0; k < nbRows; k++) {
		for (int i = 0; i < nb; i++) {
			Pearl pearl = new Pearl();
			addActor(pearl, new Location(1 + i, 1 + k));
			cp.updatePearlArrangement(k + 1, +1);
			nbPearl++;
		}
		nb--;
	}
	prepareNextHumanMove(); // human starts
	refresh();
	setTitle(nbPearl
			+ " Pearls. Remove any number of pearls from same row and press OK.");
}

public boolean touchEvent(GGTouch touch) {
	Location loc = toLocationInGrid(touch.getX(), touch.getY());
	Actor pearlAtClick = getOneActorAt(new Location(loc), Pearl.class);
	if (pearlAtClick != null) {
		int y = pearlAtClick.getY();

		if (activeRow != 0 && activeRow != y)
			setTitle("You must remove pearls from the same row.");
		else {
			activeRow = y;
			pearlAtClick.removeSelf();
			nbPearl--;
			setTitle(nbPearl + " Pearls remaining. Click 'OK' to continue.");
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
	setTitle("Press 'new Game' to play again."); /*
	bg.setPaintColor(Color.yellow);
	bg.setFont(new Font("Arial", Font.BOLD, 32));
	bg.drawText(msg, new Point(toPoint(new Location(2, 5))));
	*/
	refresh();
}

private void prepareNextHumanMove() {
	nbTakenPearl = 0;
	setTitle(nbPearl + " pearls remaining. Your move now.");
	activeRow = 0; // Spieler darf neue "Ziehreihe" bestimmen		
}

public void navigationEvent(GGNavigationEvent event) {
	switch (event)
    {
      case MENU_DOWN:
    	  if (nbTakenPearl == 0)
  			setTitle("You have to remove at least 1 Pearl!");
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


