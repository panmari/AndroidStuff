
package ph.sm.pong;

import ch.aplu.android.*;
import android.graphics.Point;

public class Pong extends GameGrid implements GGActorCollisionListener, GGMultiTouchListener
{
  private Bar barLeft, barRight;

public Pong()
  {
    super(true, windowZoom(500));
    setScreenOrientation(LANDSCAPE);
  }
  
  public void main()
  {
    setSimulationPeriod(10);
    getBg().clear(DKGRAY);
    Ball ball = new Ball();
    addActor(ball, new Location(500, 100), Math.random()*360);
    barRight = new Bar();
    barLeft = new Bar();
    int offset = getNbHorzCells()/10;
    addActor(barRight, new Location(getNbHorzCells() - offset, getNbVertCells()/2));
    addActor(barLeft, new Location(offset, getNbVertCells()/2));
    addMultiTouchListener(this, GGTouch.drag);
    barLeft.addCollisionActor(ball);
    barRight.addCollisionActor(ball);
    barLeft.addActorCollisionListener(this);
    barRight.addActorCollisionListener(this);
    
    doRun();
  }
  

  public int collide(Actor actor1, Actor actor2)
  {
	actor2.setDirection(180 - actor2.getDirection());
    playTone(1200, 20);
    return 10;
  }
  
  @Override
  public boolean multiTouchEvent(GGMultiTouch touch) {
  	Location loc = toLocation(touch.getX(), touch.getY());
  	if (loc.x < 100)
  		barLeft.setY(loc.y);
  	if (loc.x > getNbHorzCells() - 100) 
  		barRight.setY(loc.y);
  	return true;
  }
}

class Bar extends Actor
{
  public Bar()
  {
    super("stick");
  }
}

// ---------class Ball ---------------
class Ball extends Actor
{
  int radius;
  int stepSize;
  
  public Ball()
  {
    super("ball");
    setCollisionCircle(new Point(0, 0), 20);
  }
  
  /**
   * Gets called when ball is added to GameGrid
   */
  public void reset() {
	  radius = this.getHeight(0)/2;
	  /**
	   * Stepsize s is dependant on zoomfactor z: s = z^2 * 5
	   */
	  stepSize = (int) (Math.pow(gameGrid.getZoomFactor(), 2)*5);
	  L.d("Stepsize: " + stepSize);
  }
  
  public void act()
  {
    Location loc = getLocation();
    double dir = getDirection();
    // through windowZoom() the radius of the ball is set dynamically, depending on the screen size. 
    // This can be used to retrieve it.
    
    if (loc.x < radius)
    {
    	// out to the left -> game over
    	gameGrid.doPause();
    }
    else if (loc.x > getNbHorzCells() - radius)
    {
      // out to the right -> game over
      gameGrid.doPause();
    }
    else if (loc.y < radius)
    {
      dir = 360 - dir;
    }
    else if (loc.y > getNbVertCells() - radius)
    {
      dir = 360 - dir;
    }
    setDirection(dir);
    
    move(stepSize);
  }
}