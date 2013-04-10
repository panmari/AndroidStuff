package ph.sm.jewels;

import android.graphics.Color;
import ch.aplu.android.GGPanel;

public class HealthPointBar {
	private GGPanel p;
	private double length;
	private final double MIN = -9.9;
	private final double MAX = 9.9;
	private final double MAX_LENGTH = MAX - MIN;

	/**
	 * 
	 * @param p
	 * @param initLength, the initial health in percent
	 */
	public HealthPointBar(GGPanel p, double initLengthPercent) {
		this.p = p;
		setHealth(initLengthPercent);
	}
	
	/**
	 * Percent of @MAX_LENGTH gained/lost, depending
	 * if percent is positive or negative.
	 * @param percent
	 */
	public void update(double percent) {
		//clean old rectangle
		p.setPaintColor(Color.WHITE);
		p.rectangle(-9.9, -9.9, length, -9.2, true);
		
		p.setPaintColor(Color.GREEN);
		length += percent*MAX_LENGTH/100;
		length = Math.min(length, MAX);
		length = Math.max(length, MIN);
		p.rectangle(-9.9, -9.9, length, -9.2, true);
	}
	
	public boolean isGameOver(){
		return length <= MIN;
	}

	public void setHealth(double percent) {
		length = MAX_LENGTH*percent/100 + MIN;
		update(0);
	}
	
}
