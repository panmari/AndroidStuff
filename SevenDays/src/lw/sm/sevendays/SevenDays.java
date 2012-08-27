// Tu16java

package lw.sm.sevendays;

import turtle.*;

public class SevenDays extends Playground {
	public SevenDays() {
		super("tako_trans");
	}

	public void main() {
		clear(WHITE);
		showToast("Click me");
	}

	public void playgroundPressed(double x, double y) {
		Turtle poli = new Turtle(x, y);
		poli.setPenColor(RED);
		int length = 55;
		poli.lt(40);
		poli.fd(length);
		poli.rt(40);
		poli.fd(10);
		arc(poli);
		poli.lt(180);
		//second half:
		arc(poli);
		poli.fd(10);
		poli.rt(40);
		poli.fd(length + 5);
		poli.hideTurtle();
	}

	private void arc(Turtle t) {
		for (int i = 0; i < 180 / 3; i++) {
			t.fd(1);
			t.rt(3);
		}
	}
}