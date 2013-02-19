// Bubbles.java

package ph.sm.colorbubbles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ch.aplu.android.*;
import android.graphics.Point;

public class ColorBubbles extends GameGrid implements GGFlingListener, GGActorCollisionListener
{
  private final double vFactor = 50;
  private double roomHeight = 5;
  private GGStatusBar status;
  protected GGPanel p;
  private int nbBalls = 6;
  protected int nbBubbles = 25;
  private LinkedList<Bubble> bubbles = new LinkedList<Bubble>();
  private int flingThreshold = 2;
  private int hits = 0;
  private int shots = 0;
private boolean gameOver;

  public ColorBubbles()
  {
    super(WHITE, false, true, windowZoom(600));
    setScreenOrientation(LANDSCAPE);
    status = addStatusBar(30);
  }

  public void main()
  {
    addFlingListener(this);
    setSimulationPeriod(30);
    p = getPanel(0, roomHeight, 0.5);
    p.setAutoRefreshEnabled(false);
    p.setLineWidth(4);
    p.setPaintColor(GREEN);
    p.line(new PointD(-roomHeight, flingThreshold), new PointD(roomHeight, flingThreshold));

    for (int i = 0; i < nbBubbles; i++)
    {
      int type = (int)(Math.random() * 6);
      Bubble b = new Bubble(type);
      bubbles.add(b);
      b.setCollisionCircle(new Point(0, 0), 21);
      int d = (int)((double)pixelToVirtual(getNbHorzCells()) / (nbBubbles + 1));
      addActorNoRefresh(b, new Location(virtualToPixel((i + 1) * d), virtualToPixel(30)));
    }

    List<Ball> balls = new ArrayList<Ball>();
    for (int i = 0; i < nbBalls; i++)
    {
      balls.add(new Ball(this, i));
    }
    Collections.shuffle(balls);
    for (int i = 0; i < nbBalls; i++) {
    	Ball ball = balls.get(i);
	    Location loc = new Location(p.toPixelX(i - 2.5), p.toPixelY(0.5));
	    addActorNoRefresh(ball, loc);
	    for (Bubble b : bubbles) {
	  	  if (b.fits(ball))
	  		  ball.addCollisionActor(b);
	    }
    }
    
    doRun();
    status.setText("Fling someone!");
  }

  public boolean flingEvent(Point start, Point end, GGVector velocity)
  {
    if (gameOver)
    	return true;
	double vx = vFactor * velocity.x;
    double vy = -vFactor * velocity.y;
    if (p.toUserY(end.y) < flingThreshold)
    {
    	Ball b = getBallCloseTo(start);
    	if (b != null) {
          b.addActorCollisionListener(this);
          b.setCollisionCircle(new Point(0, 0), 21);
          b.shoot(vx, vy);
          shots++;
          return true;
        }
    	else showToast("Start on a head");
    }
    else
      showToast("Stay behind the green line");
    return true;
  }
  
  private Ball getBallCloseTo(Point loc) {
	  for (Actor b: getActors(Ball.class))
      {
        if (new Location(loc.x, loc.y).getDistanceTo(b.getLocation()) < virtualToPixel(80))
        	return (Ball)b;
      }
	  return null;
  }

  public int collide(Actor actor1, Actor actor2)
  {
    playTone(1200, 20);
    addActor(new Heart(), getLocationBetween(actor1.getLocation(), actor2.getLocation()));
    removeActor(actor2);
    hits++;
    displayResult();
    return 0;
  }
  
  public Location getLocationBetween(Location loc1, Location loc2) {
	  int x = (loc1.getX() + loc2.getX()) / 2;
	  int y = (loc1.getY() + loc2.getY()) / 2;
	  return new Location(x, y);
  }

  protected void displayResult()
  {
	boolean gameOver = true;
	for (Bubble b: bubbles) {
		if (b.isVisible() && !b.isShit()) {
			gameOver = false;
			break;
		}
	}
	String msg = String.format("#shots: %d   #hits: %d   %%: %4.1f",
		      shots, hits, 100.0 * hits / shots);
	if (gameOver) {
		msg += " -- Game finished!";
		doPause();
		this.gameOver = true;
	}
    status.setText(msg);
  }

}