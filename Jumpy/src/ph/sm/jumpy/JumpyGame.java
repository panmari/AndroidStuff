// AndroidEx25a.java, Artificial horizon

package ph.sm.jumpy;

import ch.aplu.android.*;
import android.graphics.Color;
import android.hardware.Sensor;
import static java.lang.Math.*;

public class JumpyGame extends GameGrid
{
  private GGStatusBar status;
  private Jumpy jumpy;

  public JumpyGame()
  {
    super(WHITE, windowZoom(600));
    status = addStatusBar(30);
  }

  public void main()
  {
	setSimulationPeriod(30);
    GGSensor sensor = new GGSensor(this, Sensor.TYPE_ACCELEROMETER);
    
    jumpy = new Jumpy(sensor);
    jumpy.addActorCollisionListener(jumpy);
    addActor(jumpy, new Location(300, 100));
    for (int i = 0; i < 10; i++) {
    	Pad pad = new Pad();
    	addActor(pad, new Location(i*200 + 100, i*200));
    	jumpy.addCollisionActor(pad);
    }
    doRun();
  }
}