package ph.sm.mandelbrot;

import android.graphics.Color;
import android.graphics.Point;
import ch.aplu.android.GGPanel;
import ch.aplu.android.GGTouch;
import ch.aplu.android.GGTouchListener;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;

public class Mandelbrot extends GameGrid implements GGTouchListener{
	
	private final double RADIUS_SQUARE = 2*2;
	private int maxIterations = 50;
	GGPanel p;
	private Point start, end;
	
	public Mandelbrot() {
		super(true, windowZoom(600));
	}
	
	public void main() {
		draw(-2.2, 1.0, -1.2, 1.2);
		addTouchListener(this, GGTouch.press | GGTouch.drag | GGTouch.release);
	}
	
	private void draw(double xmin, double xmax, double ymin, double ymax) {
		// might need some reordering, depending on how selection was made
		p = getPanel(Math.min(xmin, xmax), Math.max(xmin, xmax), 
						Math.min(ymin, ymax), Math.max(ymin, ymax));
		p.setAutoRefreshEnabled(false);
		setTitle(String.format("Mandelbrot -- xmin: %.3f xmax: %.3f ymin: %.3f ymax: %.3f", 
				p.getXmin(), p.getXmax(), p.getYmin(), p.getYmax()));
		
		for (int xPixel = 0; xPixel < getNbHorzPix(); xPixel++) {
			for(int yPixel = 0; yPixel < getNbVertPix(); yPixel++) {
				double x_p = p.toUserX(xPixel);
				double y_p = p.toUserY(yPixel);
				int iterCount = getIterCount(x_p, y_p);
				int drawColor = getColorForIterCount(iterCount);
				p.setPaintColor(drawColor);
				p.drawPoint(xPixel, yPixel);
			}
			if (xPixel % 100 == 0) // refresh every 50 columns
				refresh();
		}
		refresh();
	}

	/**
	 * @param iterCount a number between 0 and maxIterations.
	 * @return a color, that belongs to this number of iterations.
	 */
	private int getColorForIterCount(int iterCount) {
		if (iterCount >= maxIterations)
			return Color.BLACK;
		int c = (int) (255*(iterCount/(float) maxIterations));
		return Color.rgb(c, c, 255);
	}



	private int getIterCount(double x_p, double y_p) {
		if (isStable(x_p, y_p))
			return maxIterations;
		double x = 0, y = 0;
		int iter = 0;
		while (isInCircle(x, y) && iter < maxIterations) {
			double xt = x*x - y*y + x_p;
			double yt = 2*x * y + y_p;
			x = xt;
			y = yt;
			iter++;
		}
		return iter;
	}

	/**
	 * Returns true, if the given points are inside the cardioid (first expression)
	 * or inside the period 2 bulb (second expression). These values are considered
	 * stable, since they'll fulfill insideCircle for every iteration step.
	 */
	private boolean isStable(double x, double y) {
		double p = Math.sqrt(Math.pow(x - 1/4f, 2) + y*y);
		return x < p - 2*(p*p) + 1/4f || (x + 1)*(x + 1) + y*y < 1/16f;
	}
	
	private boolean isInCircle(double x, double y) {
		return Math.pow(x, 2) + Math.pow(y, 2) < RADIUS_SQUARE;
	}

	public static void main(String[] args) {
		new Mandelbrot();
	}



	@Override
	public boolean touchEvent(GGTouch mouse) {
		Location l = toLocation(mouse.getX(), mouse.getY());
		switch (mouse.getEvent()) {
		case GGTouch.press:
			start = new Point(l.x, l.y);
			end = new Point(l.x, l.y);
			break;
		case GGTouch.drag:
			Point current = new Point(l.x, l.y);
			end = current;
			break;
		case GGTouch.release:
			draw(p.toUserX(start.x), p.toUserX(end.x), p.toUserY(start.y), p.toUserY(end.y));
			break;
		}
		return true;
	}

}
