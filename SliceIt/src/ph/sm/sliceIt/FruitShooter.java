package ph.sm.sliceIt;

import java.util.Random;

import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class FruitShooter extends Actor{
	
	private SliceIt gg;
	private final int yMax = 200;
	private final int yMin = 0;
	private int intervall;
	private Random rnd = new Random();
	
	public FruitShooter(SliceIt gg, int intervall) {
		this.gg = gg;
		this.intervall = intervall;
	}
	
	public void act() {
		if (gg.getSimulationPeriod() % intervall == 0) {
			float velX = (float) (rnd.nextFloat()*2 + 0.2);
			Fruit f = new Fruit(gg, velX);
			int y = rnd.nextInt(yMax-yMin) + yMin;
			gg.addActor(f, new Location(0, y));
		}
	}
}
