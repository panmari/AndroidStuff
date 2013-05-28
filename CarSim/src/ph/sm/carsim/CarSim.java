// MinimalPong.java

package ph.sm.carsim;

import ch.aplu.android.*;
import android.graphics.Point;
import android.hardware.Sensor;

public class CarSim extends GameGrid implements GGActorCollisionListener {
	public CarSim() {
		super("town", windowZoom(500));
		setScreenOrientation(LANDSCAPE);
	}

	public void main() {
		setSimulationPeriod(30);
		GGSensor sensor = new GGSensor(this, Sensor.TYPE_ACCELEROMETER);
		doRun();
		
	}

	public int collide(Actor actor1, Actor actor2) {
		actor2.removeSelf(); 
		playTone(1200, 20);
		return 0;
	}
}


class Car extends Actor {
	
	private static final float FACTOR = 0.05f;
	private GGSensor sensor;
	private float vx;
	private float x;
	
	public Car(GGSensor sensor) {
		super("balloon");
		this.sensor = sensor;
	}
	
	public void act() {
		float[] values = GGSensor.toDeviceRotation(gameGrid, sensor.getValues(), Sensor.TYPE_ACCELEROMETER);
		L.d("" + values[0]);
		vx = -values[0]*FACTOR*30;
	}
}