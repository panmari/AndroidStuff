package lw.sm.tintiadv;

import android.graphics.Point;
import ch.aplu.android.Actor;
import ch.aplu.android.GGActorCollisionListener;
import ch.aplu.android.GGPanel;
import ch.aplu.android.GameGrid;
import ch.aplu.android.Location;
import ch.aplu.android.PointD;

public class TintiServant {

	private static Actor[] cloths;
	static int stylePoints;
	
	public static void spreadClothesRandomly(GameGrid gg, GGPanel p, Ball b) {
		String[] clothesSprites = { "shoe", "shoe", "pants", "shirt", "hat" };
		PointD[] positions = { new PointD(0,-4.5), new PointD(0,0), new PointD(-3,-3), new PointD(4,-2), new PointD(3, 3.5) };
		cloths = new Actor[clothesSprites.length];
		for (int i = 0; i < clothesSprites.length; i ++) {
			Actor a = new Actor(clothesSprites[i]);
			cloths[i] = a;
			if (i == 0)
				a.setHorzMirror(true);
			b.addActorCollisionListener(new ClothGatherer());
			b.addCollisionActor(a);
			gg.addActorNoRefresh(a, new Location(p.toPixelX(positions[i].x), p.toPixelY(positions[i].y)));
		}
	}
	
	public static void resetCloths() {
		stylePoints = 0;
		for (Actor a: cloths)
			a.show();
	}
}
class ClothGatherer implements GGActorCollisionListener {

	@Override
	public int collide(Actor arg0, Actor arg1) {
		arg1.hide();
		TintiServant.stylePoints++;
		return 30;
	}
	
}

class Princess extends Goal {

	public Princess(BallWall app, PointD center, double radius) {
		super(app, center, radius);
		Point pixCenter = app.p.toPixelPoint(center);
		app.addActorNoRefresh(new Actor("princess"), new Location(pixCenter.x, pixCenter.y));
	}
	
	@Override
	public void droppedInto() {
		switch (TintiServant.stylePoints) {
		case 5:
			app.gameOver("Oh, you're dressed up so nicely Sir Tinti! <3");
			break;
		case 4:
			app.gameOver("There you are! Seems like you forgot something on the way..");
			break;
		case 3:
			app.gameOver("Sloppy as always..");
			break;
		case 2:
			app.gameOver("How do you even dare showing up like this?!?");
			break;
		case 1:
			app.gameOver("Are you even trying to make a good impression?!?");
			break;
		case 0:
			app.gameOver("Naked already? That's how I like you best!");
			break;
		}
	}
	
}

