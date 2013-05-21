// MinimalPong.java

package ph.sm.minimalpong;

import ch.aplu.android.*;
import android.graphics.Point;

public class MinimalPong extends GameGrid implements GGActorCollisionListener
{
  public MinimalPong()
  {
    super(windowZoom(500));
  }
  
  public void main()
  {
    setSimulationPeriod(10);
    getBg().clear(DKGRAY);
    Ball ball = new Ball();
    addActor(ball, new Location(300, 100), Math.random()*360);
    Stick stick = new Stick();
    addActor(stick, new Location(getNbHorzCells() - 80, getNbVertCells()/2));
    addTouchListener(stick, GGTouch.drag);
    stick.addCollisionActor(ball);
    stick.addActorCollisionListener(this);
    doRun();
  }

  public int collide(Actor actor1, Actor actor2)
  {
    actor2.setDirection(180 - actor2.getDirection());
    playTone(1200, 20);
    return 10;
  }
}

// --------------class Stick ---------
class Stick extends Actor implements GGTouchListener
{
  public Stick()
  {
    super("stick");
  }

@Override
public boolean touchEvent(GGTouch touch) {
	Location loc = gameGrid.toLocation(touch.getX(), touch.getY());
	setY(loc.y);
	return true;
}
}

// ---------class Ball ---------------
class Ball extends Actor
{

  public Ball()
  {
    super("ball");
    setCollisionCircle(new Point(0, 0), 20);
  }

  public void act()
  {
    Location loc = getLocation();
    double dir = getDirection();
    // through windowZoom() the radius of the ball is set dynamically, depending on the screen size. 
    // This can be used to retrieve it.
    int radius = this.getHeight(0)/2;
    if (loc.x < radius)
    {
      dir = 180 - dir;
    }
    if (loc.x > getNbHorzCells() - radius)
    {
      // out to the right -> game over
      gameGrid.doPause();
    }
    if (loc.y < radius)
    {
      dir = 360 - dir;
    }
    if (loc.y > getNbHorzCells() -radius)
    {
      dir = 360 - dir;
    }
    setDirection(dir);
    move();
  }
}