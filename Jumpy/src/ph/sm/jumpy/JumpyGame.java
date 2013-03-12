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
	setWakeLockEnabled(true);
	setSimulationPeriod(30);
    GGSensor sensor = new GGSensor(this, Sensor.TYPE_ACCELEROMETER);
    
    jumpy = new Jumpy(sensor, status);
    jumpy.addActorCollisionListener(jumpy);
    addActor(jumpy, new Location(300, 100));
    for (int i = 0; i < 8; i++) {
    	Pad pad = new Pad();
    	addActorNoRefresh(pad, new Location(i*100 + 100, i*100));
    	jumpy.addCollisionActor(pad);
    }
    
    for (int i = 0; i < 3; i++) {
    	Coin c = new Coin();
    	addActorNoRefresh(c, getRandomLocation());
    	jumpy.addCollisionActor(c);
    }
    setActOrder(Jumpy.class, Coin.class, Pad.class);
    doRun();
  }
}