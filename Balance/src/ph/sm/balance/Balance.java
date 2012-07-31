// Balance.java

package ph.sm.balance;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import ch.aplu.android.GGNavigationListener.ScreenOrientation;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;

public class Balance extends GameGrid implements SensorEventListener {
	private final int sensorType = Sensor.TYPE_ORIENTATION;
	private Marble marble;
	private float[] sensorData = new float[3];

	public Balance() {
		super(true, windowZoom(600));
		setScreenOrientation(ScreenOrientation.LANDSCAPE);
	}

	public void main() {
		getBg().clear(Color.BLACK);
		setStatusText("Balance started");
		registerSensor();
		marble = new Marble(this);
		addActor(marble, new Location(getNbHorzCells() / 2,
				getNbVertCells() / 2));
		setSimulationPeriod(30);
		doRun();
	}

	public void registerSensor() {
		SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		sm.registerListener(this, sm.getDefaultSensor(sensorType),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing
	}

	public void onSensorChanged(SensorEvent event) {
		for(int i = 0; i < event.values.length; i++)
			sensorData[i] = event.values[i];
	}

	/**
	 * Has to be adapted to specific phone type/android version
	 */
	public float getXSlope() {
		return -sensorData[1];
	}

	/**
	 * Has to be adapted to specific phone type/android version
	 */
	public float getYSlope() {
		return sensorData[2];
	}

	public void gameOver() {
		showToast("Marble reset");
		marble.reset();
	}
}
