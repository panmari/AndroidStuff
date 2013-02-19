package ph.sm.boxgame;
import ch.aplu.android.GGVector;

public enum StrokeDirection {
		HORIZONTAL(new GGVector(0,-1)), VERTICAL(new GGVector(-1,0));
		
		private GGVector offset;
		
		StrokeDirection(GGVector offset) {
			this.offset = offset;
		}
		
		public GGVector getOffset() {
			return offset;
		}
}
