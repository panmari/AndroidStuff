package ph.sm.sliceIt;

import java.util.Random;

import ch.aplu.android.Actor;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class FruitFactory extends Actor{
	
	private SliceIt gg;
	private final int yMax = 200;
	private final int yMin = 0;
	private int intervall;
	private Random rnd = new Random();
	private int counter;
	
	public FruitFactory(SliceIt gg, int intervall) {
		this.gg = gg;
		this.intervall = intervall;
		this.counter = 0;
	}
	
	public void act() {
		if (counter > intervall) {
			float velX = rnd.nextFloat()*20F + 50F;
			Fruit f = new Fruit("fruit", gg, velX);
			int y = rnd.nextInt(yMax-yMin) + yMin;
			L.d("" + velX);
			gg.addActor(f, new Location(0, y));
			counter = 0;
		} else counter++;
	}
}

class Melon extends Fruit {
	public Melon(SliceIt gg, float xVel) {
		super("melon", gg, xVel);
	}
}

class Orange extends Fruit {
	public Orange(SliceIt gg, float xVel) {
		super("orange", gg, xVel);
	}	
}

class Strawberry extends Fruit {
	public Strawberry(SliceIt gg, float xVel) {
		super("strawberry", gg, xVel);
	}	
}