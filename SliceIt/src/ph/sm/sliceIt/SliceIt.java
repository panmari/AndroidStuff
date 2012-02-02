// SliceIt.java

package ph.sm.sliceIt;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGActorTouchListener;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;
import ch.aplu.android.TextActor;

/** 
 * 
 */
public class SliceIt extends GameGrid implements GGActorTouchListener {
	private int points;
	private final int FRUITSNR = 100;
	private final FruitFactory ff = new FruitFactory(this, 35, FRUITSNR);

	public SliceIt() {
		super();
	}

	public void main() {
		setStatusText("SliceIt started, GG v " + getVersion());
		addActor(ff, new Location(0,0));
		setSimulationPeriod(20);
		doRun();
	}
	
	public void increasePoints() {
		points++;
		setStatusText(points + " Points!");
	}
	
	public void act() {
		if (ff.outOfFruits() && getActors(Fruit.class).isEmpty())
			gameOver();
	}
	
	public void gameOver() {
		TextActor text = new TextActor("You sliced " + points + " out of " + FRUITSNR);
		TextActor perfect = new TextActor("Perfect round!");
		addActor(text, new Location(10, 10));
		if (points == FRUITSNR)
			addActor(perfect, new Location(10, 33));
		doPause();
	}
	
	public void actorTouched(Actor actor, GGTouch touch, Point spot) {
		((Fruit)actor).splatter();
	}
}
