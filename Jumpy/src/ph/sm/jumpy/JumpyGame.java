// AndroidEx25a.java, Artificial horizon

package ph.sm.jumpy;

import ch.aplu.android.*;
import android.graphics.Color;
import android.hardware.Sensor;
import static java.lang.Math.*;

public class JumpyGame extends GameGrid implements GGActorCollisionListener
{
  private final double r = 100;
  private GGStatusBar status;

  public JumpyGame()
  {
    super(WHITE, windowZoom(600));
    status = addStatusBar(30);
  }

  public void main()
  {
	setSimulationPeriod(30);
    GGPanel p = getPanel(0,0,1000,1000);
    p.setAutoRefreshEnabled(false);
    GGSensor sensor = new GGSensor(this, Sensor.TYPE_ACCELEROMETER);
    
    Jumpy jumpy = new Jumpy(sensor);
    jumpy.addActorCollisionListener(this);
    addActor(jumpy, new Location(300, 100));
    for (int i = 0; i < 3; i++) {
    	Pad pad = new Pad();
    	addActor(pad, new Location(i*200 + 100, i*200));
    	jumpy.addCollisionActor(pad);
    }
    doRun();
  }

	@Override
	public int collide(Actor arg0, Actor arg1) {
		
		return 0;
	}
}