// Memory.java

package ph.sm.memory;

import ch.aplu.android.*;
import ch.aplu.util.Monitor;
import android.graphics.Color;

public class Memory extends GameGrid implements GGTouchListener {
	private boolean isReady = true;
	private MemoryCard card1;
	private MemoryCard card2;
	private MemoryCard[] cards = new MemoryCard[16];

	public Memory() {
		super(4, 4, cellZoom(115));
	}

	public void main() {

		for (int i = 0; i < 16; i++) {
			if (i < 8)
				cards[i] = new MemoryCard(i);
			else
				cards[i] = new MemoryCard(i - 8);
		}
		addTouchListener(this, GGTouch.click);

		while (true) {
			Monitor.putSleep(); // Wait until there is something to do
			delay(1000);
			card1.show(1); // Flip cards back
			card2.show(1);
			isReady = true;
			setTouchEnabled(true); // Rearm mouse events
			refresh();
		}
	}

	public void reset() {
		for (int i = 0; i < 16; i++) {
			cards[i].show(1);
			addActorNoRefresh(cards[i], getRandomEmptyLocation());
		}
		refresh();
	}

	public boolean touchEvent(GGTouch mouse) {
		Location location = toLocation(mouse.getX(), mouse.getY());
		MemoryCard card = (MemoryCard) getOneActorAt(location);
		if (card == null || card.getIdVisible() == 0) // Card already
														// flipped->no action
			return true;

		card.show(0); // Show picture
		refresh();
		if (isReady) {
			isReady = false;
			card1 = card;
		} else {
			card2 = card;
			if (card1.getId() == card2.getId()) // Pair found, let them visible
				isReady = true;
			else {
				setTouchEnabled(false); // Disable mouse events until
										// application thread flipped back cards
				Monitor.wakeUp();
			}
		}
		return true;
	}
}

// --------------------- class MemoryCard
// -------------------------------------------
class MemoryCard extends Actor {
	private int id;

	public MemoryCard(int id) {
		super("card" + id, "cardback");
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
