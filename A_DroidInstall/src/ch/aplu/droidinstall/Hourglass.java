// Hourglass.java

package ch.aplu.droidinstall;

import ch.aplu.android.*;

public class Hourglass extends Actor
{
  public Hourglass()
  {
    super(true, "hourglass");
  }

  public void act()
  {
    setDirection(getDirection() + 6);
  }
}