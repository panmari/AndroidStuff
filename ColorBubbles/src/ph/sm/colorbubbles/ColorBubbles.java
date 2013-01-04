// Bubbles.java

package ph.sm.colorbubbles;

import ch.aplu.android.*;
import android.graphics.Point;

public class ColorBubbles extends GameGrid implements GGFlingListener, GGActorCollisionListener
{
  private final double vFactor = 50;
  private double roomHeight = 5;
  protected GGStatusBar status;
  protected GGPanel p;
  Ball ball;
  Bubble[] bubbles;
  protected static int nbBubbles = 25;
  protected int hits = 0;
  protected int shots = 0;

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
    p.arc(2, 0, 180, false);

    bubbles = new Bubble[nbBubbles];
    for (int i = 0; i < nbBubbles; i++)
    {
      bubbles[i] = new Bubble();
      int d = (int)((double)pixelToVirtual(getNbHorzCells()) / (nbBubbles + 1));
      addActor(bubbles[i], new Location(virtualToPixel((i + 1) * d ), virtualToPixel(30)));
      // addActor(bubbles[i], new Location(40 + i * 50 , 30));
      bubbles[i].setCollisionCircle(new Point(0, 0), 21);
      bubbles[i].addActorCollisionListener(this);
    }

    doRun();
    status.setText("Fling the ball!");
  }

  public boolean flingEvent(Point start, Point end, GGVector velocity)
  {
    double x = p.toUserX(end.x);
    double y = p.toUserY(end.y);
    double vx = vFactor * velocity.x;
    double vy = -vFactor * velocity.y;
    if (Math.sqrt(x * x + y * y) < 2)
    {
      ball = new Ball(this, x, y, vx, vy);
      addActorNoRefresh(ball, new Location(end.x, end.y));
      ball.setCollisionCircle(new Point(0, 0), 21);
      for (int i = 0; i < nbBubbles; i++)
        bubbles[i].addCollisionActor(ball);
      shots++;
    }
    else
    {
      showToast("Stay inside the arc");
    }

    return true;
  }

  public int collide(Actor actor1, Actor actor2)
  {
    hits++;
    playTone(1200, 20);
    actor2.removeSelf();
    actor1.removeSelf();
    displayResult();
    return 10;
  }

  protected void displayResult()
  {
    status.setText(String.format("#shots: %d   #hits: %d   %%: %4.1f",
      shots, hits, 100.0 * hits / shots));
  }
}

// -----------class Ball-----------------
class Ball extends Actor
{
  private double x, y;  // in m 
  private double vx, vy; // in  m/s
  private double dt = 0.030;  // in s (simulation period)
  private ColorBubbles app;

  public Ball(ColorBubbles app, double x, double y, double vx, double vy)
  {
    super("peg_0");
    this.app = app;
    // Initial conditions:
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
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

class Bubble extends Actor
{
  public Bubble()
  {
    super("peg", 6);
    show((int)(Math.random()*6));
  }
}