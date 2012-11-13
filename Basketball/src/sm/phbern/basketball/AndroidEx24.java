// AndroidEx24.java

package sm.phbern.basketball;

import ch.aplu.android.*;
import android.graphics.Point;

public class AndroidEx24 extends GameGrid implements GGFlingListener, GGActorCollisionListener
{
  private final double vFactor = 1 / 50.0; 
  private int screenW;
  private int screenH;
  private Basket basket;
  
    
  //private GGStatusBar status;
  
  public AndroidEx24()
  {
    super(WHITE, false, true, windowZoom(600)); 
    setScreenOrientation(LANDSCAPE);
    //status = addStatusBar(20);
  }

  public void main()
  {
    addFlingListener(this);
    getBg().setPaintColor(GREEN);
    screenW = getNbHorzCells();
    screenH = getNbVertCells();
    getBg().setLineWidth(4); 
    getBg().drawLine(screenW * 3/4, screenH / 8, screenW * 3/4, screenH * 7/8);
    basket = new Basket();
    basket.setCollisionRectangle(new Point(0, 0), 40, 20); 
    // basket.setCollisionLine(new Point(5, -205), new Point(45, 205));
    addActor(basket, new Location(70, 250));
    setSimulationPeriod(50);
    doRun();
    //status.setText("Fling the ball!");
  }

  public boolean flingEvent(Point start, Point end, GGVector velocity)
  {
    Ball ball =  new Ball(vFactor * velocity.x, vFactor * velocity.y);
    addActorNoRefresh(ball, new Location(end.x, end.y));
    ball.addCollisionActor(basket);
    ball.addActorCollisionListener(this);
    return true;
  }
  
  public int collide(Actor actor1, Actor actor2)
  {
    actor1.setDirection(90);
    playTone(1200, 20);
    return 10;
  }   
}



class Ball extends Actor 
{
  private final double g = 9.81; // in m/s^2
  private double x, y;  // in m 
  private double vx, vy; // in  m/s
  private double dt = 0.05;  // in s
  int fieldSize = 24;

  public Ball(double vx, double vy)
  {
    super("ball");
    this.vx = vx;
    this.vy = vy; 
    
  }
  
  public void reset()
  {
    x = getX() / fieldSize;  
    y = getY() / fieldSize;
  }
  
  public void act()
  {
    
    vy = vy + g * dt;
    x = x + vx * dt;
    y = y + vy * dt;
    setLocation(new Location(fieldSize * x, fieldSize * y));
    if (fieldSize * x < this.getWidth(0))
    	vx = -vx;
    if (!isInGrid())
      removeSelf();
  }
}

class Basket extends Actor
{
  public Basket()
  {
    super("basket");
  }
}  
