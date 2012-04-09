package ph.sm.sliceit;

import ch.aplu.android.Actor;
import ch.aplu.android.Location;

public class FruitFactory extends Actor{
	
	private int amount;
	private int nbGenerated = 0;
	protected static int nbMissed = 0;
	protected static int nbHit = 0;
	private Sword sword;
	
	/**
	 * A factory utility that produces random fruits
	 * in the gamegrid it is added to.
	 * @param sword the instance of the sword that is used for slicing
	 * @param intervall between two created fruits (in SimulationCycles)
	 * @param amount number of fruits produced
	 */
	public FruitFactory(Sword sword, int intervall, int amount) {
		this.amount = amount;
		this.sword = sword;
		this.setActEnabled(false);
		this.setSlowDown(intervall);
	}
	
	public void act() {
		createRandomFruit();
	}

	private void createRandomFruit() {
		if (isOutOfFruits())
			return;
		double vx = Math.random()*20 + 50;
		Fruit fruit;
		double r = Math.random();
		if (r < 0.33)
			fruit = new Melon(vx);
		else if (r < 0.66)
			fruit = new Orange(vx);
		else fruit = new Strawberry(vx);
		int y = (int) (Math.random() * gameGrid.getNbVertCells()/2);
		fruit.addCollisionActor(sword);
		fruit.addActorCollisionListener(fruit);
		gameGrid.addActor(fruit, new Location(0, y));
		nbGenerated++;
	}
	
	public void enable() {
		this.setActEnabled(true);
		}
	
	public void disable() {
		this.setActEnabled(false);
	}
	
	public boolean isOutOfFruits() {
		return nbGenerated == amount;
	}
}


class Melon extends Fruit {
	public Melon(double vx) {
		super("melon", vx);
	}
}

class Orange extends Fruit {
	public Orange(double vx) {
		super("orange", vx);
	}	
}

class Strawberry extends Fruit {
	public Strawberry(double vx) {
		super("strawberry", vx);
	}	
}