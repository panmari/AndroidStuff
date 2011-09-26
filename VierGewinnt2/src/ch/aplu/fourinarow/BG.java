// BG.java

package ch.aplu.fourinarow;

import ch.aplu.android.*;

public class BG extends Actor
{

  public BG()
  {
    super(false, "fourinarow");
  }

  public void reset()
  {
    setLocationOffset(new android.graphics.Point(0, 4 * 70));
    setOnTop();
  }
}
