// Balance.java

package ph.sm.balance;

import java.util.Arrays;

import ch.aplu.android.*;
import ch.aplu.android.GGNavigationListener.ScreenOrientation;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Balance extends GameGrid implements SensorEventListener {
	private final int sensorType = Sensor.TYPE_ORIENTATION;
	private Marble marble;
	private float[] sensorData;

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
		L.d(Arrays.toString(event.values));
		L.d(event.sensor.toString());
		L.d("" + event.sensor.getMaximumRange());
		sensorData = event.values;
	}

	public float getXSlope() {
		return -sensorData[2];
	}

	public float getYSlope() {
		return -sensorData[1];
	}

	public void gameOver() {
		showToast("Marble reset");
		marble.reset();
	}
}