package ph.sm.reversi;

import ch.aplu.android.Actor;

public class PlayStone extends Actor {

	/**
	 * Creates a playstone with the color of the
	 * given player (either 0 = white or 1 = black)
	 * By calling getIdVisible() you can easily check to
	 * which player the stone belongs.
	 * @param player
	 */
	public PlayStone(int player) {
		super ("playstone", 2);
		show(player);
	}
}
