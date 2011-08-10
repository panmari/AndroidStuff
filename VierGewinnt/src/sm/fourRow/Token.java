package sm.fourRow;

// Token.java

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class Token extends Actor {
	private int player, nb;
	private VierGewinnt gg;

	public Token(int player, VierGewinnt gg) {
		super(false, "sprites/token.png", 2);
		this.player = player;
		this.gg = gg;
		setActEnabled(false);
		show(player); // 0 = yellow , 1 = red
	}

	public void act() {
		Location nextLoc = new Location(getX(), getY() + 1);
		if (gameGrid.getOneActorAt(nextLoc) == null && isMoveValid()) {
			if (nb == 6) {
				nb = 0;
				setLocationOffset(new Point(0, 0));
				move();
			} else
				setLocationOffset(new Point(0, 10 * nb));
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
