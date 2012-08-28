// Tu16java

package lw.sm.sevendays;

import java.util.Stack;

import turtle.Playground;
import turtle.Turtle;

public class SevenDays extends Playground {
	
	private Stack<Turtle> turtlePool;
	
	public SevenDays() {
		super("tako_trans");
	}

	public void main() {
		clear(WHITE);
		showToast("Click me");
		turtlePool = new Stack<Turtle>();
		for (int i = 0; i < 20; i++) {
			Turtle poli = new Turtle();
			poli.setPenColor(RED);
			poli.hideTurtle();
			turtlePool.push(poli);
		}
	}

	public void playgroundPressed(double x, double y) {
		if (turtlePool.isEmpty()) {
			return;
		}
		Turtle poli = turtlePool.pop();
		poli.setX(x);
		poli.setY(y);
		poli.showTurtle();
		poli.setSpeed(5);
		int length = 55;
		poli.lt(40);
		poli.fd(length);
		for (int i = 0; i < 2; i++) {
			poli.rt(20);
			poli.fd(5);
		}
		arc(poli);
		poli.lt(180);
		//second half:
		arc(poli);
		for (int i = 0; i < 2; i++) {
			poli.fd(5);
			poli.rt(20);
		}
		poli.fd(length + 3);
		//poli.fill(poli.getX(), poli.getY() + 10); //bad idea XD
		poli.hideTurtle();
		poli.setHeading(0);
		turtlePool.push(poli);
	}

	private void arc(Turtle t) {
		for (int i = 0; i < 180 / 3; i++) {
			t.fd(1);
			t.rt(3);
		}
	}
}