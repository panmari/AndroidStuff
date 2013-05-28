package ph.sm.jewels;

import java.util.LinkedList;

import ch.aplu.android.*;

public class HexaPacmanGame extends GameGrid implements GGActorCollisionListener {
	
	private GGStatusBar status;
	private HexaPacman hexaPacman;
	private GGPanel p;
	private LinkedList<Jewel> availableJewels = new LinkedList<Jewel>();
	private LinkedList<Jewel> allJewels = new LinkedList<Jewel>();
	private HealthPointBar hpBar;
	private int score;
	private final int initialHealth = 50;

	
	public HexaPacmanGame() {
		super(WHITE, windowZoom(700));
	    status = addStatusBar(30);
	}
	
	public void main() {
		getBg().clear(WHITE);
		p = getPanel(-10, 10, 0.5);
		p.setAutoRefreshEnabled(false);
		hexaPacman = new HexaPacman(p);
		PointD hexagonSpawnPoint = new PointD(0,0);
		addActorNoRefresh(hexaPacman, toLocation(p.toPixelPoint(hexagonSpawnPoint)));
		for (int i = 1; i < 10; i++) {
			Jewel j = new Jewel(availableJewels);
			allJewels.add(j);
			//jewel is added to the jewels list in reset() when added to gamegrid
			addActorNoRefresh(j, new Location(-100, -100)); //out of sight
			hexaPacman.addCollisionActor(j);
		}
		hexaPacman.addActorCollisionListener(this);
		setSimulationPeriod(30);
		setPaintOrder(HexaPacman.class, Jewel.class);
		addMultiTouchListener(hexaPacman, GGMultiTouch.press | GGMultiTouch.pointerPress | GGMultiTouch.release);
		hpBar = new HealthPointBar(p, initialHealth);
		status.setText("Tap the screen to the right/left to turn Hexa-Pacman!");
		doRun();
	}
	
	public void act() {
		if (hpBar.isGameOver()) {
			doPause();
			showToast("Game Over, tap screen to reset");
			// ugly way to prevent immediate restart
			setTouchEnabled(false);
			delay(1000);
			setTouchEnabled(true);
		}
		launchJewel();
	}
	
	/**
	 * Launches a jewel from a random direction towards hexa-pacman.
	 * If a jewel is actually launched or not is decided randomly.
	 */
	private void launchJewel() {
		if (!availableJewels.isEmpty() && Math.random() < 0.05 ) {
			GGVector v = new GGVector(10, 10);
			v.rotate(Math.random()*Math.PI*2);
			PointD spawnPoint = new PointD(v);
			Actor spawningJewel = availableJewels.poll();
			spawningJewel.setLocation(toLocation(p.toPixelPoint(spawnPoint)));
			spawningJewel.setDirection(spawningJewel.getLocation().getDirectionTo(hexaPacman.getLocation()));
			spawningJewel.setActEnabled(true);
		}
	}

	/**
	 * Since this is called last by @doReset() (after actors @reset()), we need
	 * to painfully refill the @available_jewels manually, so no jewels turns up
	 * twice in this list.
	 */
	public void reset() {
		status.setText("Tap the screen to the right/left to turn Hexa-Pacman!");
		availableJewels.clear();
		availableJewels.addAll(allJewels);
		score = 0;
		hpBar.setHealth(initialHealth);
		doRun();
	}

	@Override
	public int collide(Actor arg1, Actor collisionPartner) {
		Jewel jewel = (Jewel) collisionPartner;
		if (jewel.isExploding()) 
			return 0;
		
		if (headedTowardsHexagonsMouth(jewel)) {
			hexaPacman.eat();
			hpBar.update(10);
			score++;
			status.setText("Number of jewels eaten: " + score);
			jewel.reset();
		} else 
			{ 
			hpBar.update(-7);
			jewel.explode();
		}
		return 0;
	}
	
	private boolean headedTowardsHexagonsMouth(Jewel jewel) {
		double flyingDirection = hexaPacman.getLocation().getDirectionTo(jewel.getLocation());
		return Math.abs(hexaPacman.getDirection() - flyingDirection) < 30;
	}
}
