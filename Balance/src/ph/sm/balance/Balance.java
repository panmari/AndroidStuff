// Balance.java

package ph.sm.balance;

import java.util.Arrays;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import ch.aplu.android.GGNavigationListener.ScreenOrientation;
import ch.aplu.android.GameGrid;
import ch.aplu.android.L;
import ch.aplu.android.Location;

public class Balance extends GameGrid implements SensorEventListener {
	private Marble marble;
	private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    private float[] tempR = new float[16];
    private float[] mR = new float[16];
    private float[] mOrientation = new float[3];

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

	public void act() {
		//L.d(Arrays.toString(mOrientation));
		L.d(""+getResources().getConfiguration().orientation);
	}
	
	public void registerSensor() {
		SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	    mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing
	}

	public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(tempR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.remapCoordinateSystem(tempR, 
            		SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, mR);
            SensorManager.getOrientation(mR, mOrientation);
            }
    }

	/**
	 * Has to be adapted to specific phone type/android version
	 */
	public float getXSlope() {
		return mOrientation[1];
	}

	/**
	 * Has to be adapted to specific phone type/android version
	 */
	public float getYSlope() {
		return mOrientation[2];
	}

	public void gameOver() {
		showToast("Marble reset");
		marble.reset();
	}
}
