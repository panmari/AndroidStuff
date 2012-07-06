// Balance.java

package ph.sm.balance;

import java.util.Arrays;

import ch.aplu.android.*;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Balance extends GameGrid implements SensorEventListener
{
  private final int sensorType = Sensor.TYPE_ORIENTATION;
  private Marble marble;
  private GGTextField debug;
	
  public Balance()
  {
    super(10, 10, 0, Color.RED);
  }

  public void main()
  {
    setStatusText("Balance started");
    registerSensor();
    marble = new Marble();
    addActor(marble, new Location(getNbHorzCells()/2, getNbVertCells()/2));
    debug = new GGTextField(new Location(2,2), true);
  }
  
  public void registerSensor()
  {
    SensorManager sm = (SensorManager)getSystemService(SENSOR_SERVICE);
    sm.registerListener(this, 
       sm.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_NORMAL);
  }

public void onAccuracyChanged(Sensor sensor, int accuracy) {
	
}

public void onSensorChanged(SensorEvent event) {
	if (debug != null)
		debug.setText(Arrays.toString(event.values));
	refresh();
}  
}


