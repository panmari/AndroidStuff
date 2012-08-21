// Balance.java

package ph.sm.balance;

import android.graphics.Color;
import ch.aplu.android.GGNavigationListener.ScreenOrientation;
import ch.aplu.android.GGOrientationSensor;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;

public class Balance extends GameGrid {
	private Marble marble;
	private GGOrientationSensor sensor;
	private int pitch, roll;

	/**
	 * Reads out the orientation sensor every 0.1 seconds and
	 * saves the values of pitch and roll in the respective variables.
	 */
	private class PollThread extends Thread {
		public void run() {
			while (true) {
				pollSensor();
				delay(100);
			}
		}

		private void pollSensor() {
			pitch = sensor.getPitch();
			roll = sensor.getRoll();
		}

	}

	public Balance() {
		super(true, windowZoom(600));
		setScreenOrientation(ScreenOrientation.LANDSCAPE);
	}

	public void main() {
		getBg().clear(Color.BLACK);
		setStatusText("Balance started");
		if (sensor == null) {
			sensor = GGOrientationSensor.create(this);
			new PollThread().start();
		}
		marble = new Marble(this);
		addActor(marble, new Location(getNbHorzCells() / 2,
				getNbVertCells() / 2));
		setSimulationPeriod(30);
		doRun();
	}

	public float getXSlope() {
		return (float) Math.toRadians(-roll);
	}

	public float getYSlope() {
		return (float) Math.toRadians(pitch);
	}

	public void gameOver() {
		showToast("Marble reset");
		marble.reset();
	}
}
