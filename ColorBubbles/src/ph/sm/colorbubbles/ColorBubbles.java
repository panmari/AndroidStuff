// Bubbles.java

package ph.sm.colorbubbles;

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
  private int flingThreshold = 2;
  private int hits = 0;
  private int shots = 0;

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
      b.setCollisionCircle(new Point(0, 0), 21);
      int d = (int)((double)pixelToVirtual(getNbHorzCells()) / (nbBubbles + 1));
      addActorNoRefresh(b, new Location(virtualToPixel((i + 1) * d), virtualToPixel(30)));
    }

    for (int i = 0; i < nbBalls; i++)
    {
      Ball ball = new Ball(this, i);
      Location loc = new Location(p.toPixelX(i - 2.5), p.toPixelY(0.5));
      addActorNoRefresh(ball, loc);
    }
    doRun();
    status.setText("Fling a ball!");
  }

  public boolean flingEvent(Point start, Point end, GGVector velocity)
  {
    double vx = vFactor * velocity.x;
    double vy = -vFactor * velocity.y;
    if (p.toUserY(end.y) < flingThreshold)
    {
      for (Actor b : getActors(Ball.class))
      {
        if (new Location(start.x, start.y).getDistanceTo(b.getLocation()) < virtualToPixel(80))
        {
          b.addActorCollisionListener(this);
          b.setCollisionCircle(new Point(0, 0), 21);
          for (Actor bubble : getActors(Bubble.class))
            b.addCollisionActor(bubble);  
          ((Ball)b).init(p.toUserX(b.getXStart()), p.toUserY(b.getYStart()), vx, vy);
          shots++;
          return true;
        }
      }
      showToast("Fling one of the balls");
    }
    else
      showToast("Stay behind the green line");
    return true;
  }

  public int collide(Actor actor1, Actor actor2)
  {
    if (((Ball)actor1).getType() == ((Bubble)actor2).getType())
    {
      playTone(1200, 20);
      actor2.removeSelf();
      hits++;
    }
    displayResult();
    return 0;
  }

  protected void displayResult()
  {
    status.setText(String.format("#shots: %d   #hits: %d   %%: %4.1f",
      shots, hits, 100.0 * hits / shots));
  }

}

// -----------class Ball-----------------
class Ball extends Bubble
{
  private double x, y;  // in m 
  private double vx, vy; // in  m/s
  private double dt = 0.030;  // in s (simulation period)
  private ColorBubbles app;

  public Ball(ColorBubbles app, int type)
  {
    super(type);
    this.app = app;
    setActEnabled(false);
  }

  public void init(double x, double y, double vx, double vy)
  {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
    setActEnabled(true);
  }

  public void act()
  {
    //vy = vy - g * dt;
    x = x + vx * dt;
    y = y + vy * dt;
    setLocation(new Location(app.p.toPixelX(x), app.p.toPixelY(y)));
    if (!isInGrid())
    {
      removeSelf();
      app.displayResult();
    }
  }

}

// -----------class Bubble -----------------
class Bubble extends Actor
{
  protected int type;
  
  public Bubble(int type)
  {
    super("peg_" + type);
    this.type = type;
  }
  
  public int getType()
  {
    return type;
  }  

}