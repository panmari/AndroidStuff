package lw.sm;

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

