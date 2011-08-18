package sm.connectFour;

// Token.java

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Token extends Actor {
	private int player, nb;
	private VierGewinnt gg;
	private int cellSizeFactor;

	public Token(int player, VierGewinnt gg) {
		super(false, "peg", 2);
		this.player = player;
		this.gg = gg;
		setActEnabled(false);
		show(player); // 0 = yellow , 1 = red
		cellSizeFactor = gg.getCellSize()/6;
	}

	public void act() {
		Location nextLoc = new Location(getX(), getY() + 1);
		if (gameGrid.getOneActorAt(nextLoc) == null && isMoveValid()) {
			if (nb == 6) {
				nb = 0;
				setLocationOffset(new Point(0, 0));
				move();
			} else
				setLocationOffset(new Point(0, nb*cellSizeFactor));
			nb++;
		} else { // token has arrived
			setActEnabled(false);
			gg.tokenArrived(getLocation(), player);
		}
	}

	public int getPlayer() {
		return player;
	}
}
