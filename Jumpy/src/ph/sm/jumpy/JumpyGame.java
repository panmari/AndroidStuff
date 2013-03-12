// AndroidEx25a.java, Artificial horizon

package ph.sm.jumpy;

import android.hardware.Sensor;
import ch.aplu.android.GGSensor;
import ch.aplu.android.GGStatusBar;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;

public class JumpyGame extends GameGrid implements GGTouchListener
{
  private GGStatusBar status;
  private Jumpy jumpy;

  public JumpyGame()
  {
    super(WHITE, windowZoom(700));
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
    status.setText("Tilt your device left/right to control the jumps direction.");
    doRun();
    addTouchListener(this, GGTouch.click);
    setTouchEnabled(false);
  }

	@Override
	public boolean touchEvent(GGTouch arg0) {
		doReset();
		status.setText("Game reset");
		setTouchEnabled(false);
		setActEnabled(true);
		doRun();
		return true;
	}
}