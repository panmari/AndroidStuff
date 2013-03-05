// AndroidEx25a.java, Artificial horizon

package ph.sm.jumpy;

import ch.aplu.android.*;
import android.graphics.Color;
import android.hardware.Sensor;
import static java.lang.Math.*;

public class Jumpy extends GameGrid
{
  private final double r = 100;
  private GGStatusBar status;

  public Jumpy()
  {
    super(WHITE);
    status = addStatusBar(30);
  }

  public void main()
  {
	setSimulationPeriod(30);
    GGPanel p = getPanel(-110, 110, -110, 110);
    p.setAutoRefreshEnabled(false);
    GGSensor sensor = new GGSensor(this, Sensor.TYPE_ACCELEROMETER);
    while (true)
    {
      float[] values = sensor.getValues();
      
      String msg = "";
      for (float f: values) {
    	  msg += f + " - ";
      }
      status.setText(msg);
      refresh();
      delay(50);
    }
  }
}