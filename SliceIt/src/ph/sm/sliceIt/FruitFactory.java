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
			createRandomFruit();
		} else counter++;
	}

	private void createRandomFruit() {
		float velX = rnd.nextFloat()*20F + 50F;
		Fruit f;
		int fruitInt = rnd.nextInt(100);
		if (fruitInt < 30)
			f = new Melon(gg, velX);
		else if (fruitInt < 60)
			f = new Orange(gg, velX);
		else f = new Strawberry(gg, velX);
		int y = rnd.nextInt(yMax-yMin) + yMin;
		L.d("" + velX);
		gg.addActor(f, new Location(0, y));
		counter = 0;
	}
}

class Melon extends Fruit {
	public Melon(SliceIt gg, float xVel) {
		super("melon", gg, xVel);
		setSize(40);
	}
}

class Orange extends Fruit {
	public Orange(SliceIt gg, float xVel) {
		super("orange", gg, xVel);
		setSize(20);
	}	
}

class Strawberry extends Fruit {
	public Strawberry(SliceIt gg, float xVel) {
		super("strawberry", gg, xVel);
		setSize(15);
	}	
}