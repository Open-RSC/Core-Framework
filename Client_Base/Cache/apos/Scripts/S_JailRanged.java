import java.awt.Point;
import java.util.HashMap;

public final class S_JailRanged extends Script {

	// no bones yet!

	private static final int ID_PIRATE = 137;
	private static final int ID_WORMBRAIN = 192;
	private static final int ID_MUGGER = 21;
	private static final int ID_THIEF = 64;
	private static final int ID_KNIGHT = 189;

	private static final int[] npc_ids = {
		ID_PIRATE, ID_WORMBRAIN, ID_MUGGER, ID_THIEF/*, ID_KNIGHT*/
	};

	private static final HashMap<Integer, Point[]> tiles;

	private static final int[] invalid_npc = {
		-1, -1, -1
	};

	static {
		// aim spots for the various npcs
		tiles = new HashMap<Integer, Point[]>();
		tiles.put(ID_PIRATE, new Point[] {
			new Point(285, 658),
			new Point(285, 659),
			new Point(285, 661)
		});
		tiles.put(ID_THIEF, new Point[] {
			new Point(285, 655),
			new Point(285, 657)
		});
		tiles.put(ID_WORMBRAIN, new Point[] {
			new Point(285, 662),
			new Point(285, 663),
			new Point(285, 664),
			new Point(284, 665),
			new Point(282, 665)
		});
		tiles.put(ID_MUGGER, new Point[] {
			new Point(281, 665),
			new Point(285, 662),
			new Point(285, 663),
			new Point(285, 664)
		});
		tiles.put(ID_KNIGHT, new Point[] {
			new Point(281, 665),
			new Point(281, 667)
		});
	}

	private int walked;
	private long move_time;
	private long sleep_time;
	private long start_time;
	private long attack_time;

	public S_JailRanged(Extension ex) {
		super(ex);
	}

	@Override
	public void init(String params) {
		start_time = -1L;
		move_time = -1L;
		sleep_time = -1L;
		attack_time = -1L;
	}

	@Override
	public int main() {
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
		}

		if (getFightMode() != 1) {
			setFightMode(1);
			return random(300, 400);
		}

		if (move_time != -1L && System.currentTimeMillis() >= move_time) {
			// shouldn't ever happen, but logging out sucks
			System.out.println("Moving for 5 min timer");
			_walkApprox(getX(), getY(), 1);
			move_time = -1L;
			return random(1500, 2000);
		}

		if (sleep_time != -1L){
			if (System.currentTimeMillis() >= sleep_time) {
				useSleepingBag();
				sleep_time = -1L;
				return random(1500, 2500);
			}
			return 0;
		}

		if (isWalking()) {
			return random(600, 800);
		}

		int dbest = Integer.MAX_VALUE;
		int ibest = -1;
		int[] nbest = invalid_npc;

		for (int id : npc_ids) {
			int[] npc = _getNpc(id);
			if (npc == invalid_npc) {
				continue;
			}
			int d = distanceTo(npc[1], npc[2]);
			if (d < dbest) {
				dbest = d;
				nbest = npc;
				ibest = id;
			}
		}

		if (nbest != invalid_npc) {
			return _attack(nbest, ibest);
		}
		return random(600, 800);
	}

	@Override
	public void paint() {
		drawString("Runtime: " + _getRuntime(), 25, 25, 1, 0xFFFFFF);
	}

	@Override
	public void onServerMessage(String str) {
		if (str.contains("clear")) {
			walked = -1;
		} else if (str.contains("standing")) {
			move_time = System.currentTimeMillis() + random(1500, 2500);
		} else if (str.contains("tired")) {
			sleep_time = System.currentTimeMillis() + random(1500, 2500);
		} else if (str.contains("out of ammo")) {
			stopScript();
		}
	}

	private int[] _getNpc(int id) {
		int[] npc = getAllNpcById(id);
		if (npc[0] == -1) return invalid_npc;
		// "in jail" check
		int x = npc[1];
		int y = npc[2];
		if (x > 284) return invalid_npc;
		if (y > 667) return invalid_npc;
		if (x < 278) return invalid_npc;
		if (y < 655) return invalid_npc;
		/*if (isReachable(npc[1], npc[2])) {
			return invalid_npc;
		}*/
		return npc;
	}

	private int _attack(int[] npc, int id) {
		if (walked != id) {
			_walkNearest(tiles.get(id));
			walked = id;
			attack_time = System.currentTimeMillis();
			return random(1000, 1500);
		}
		if (System.currentTimeMillis() >= attack_time) {
			attackNpc(npc[0]);
			attack_time = System.currentTimeMillis() + random(2500, 4000);
		}
		return random(100, 200);
	}

	private void _walkApprox(int x, int y, int range) {
		int dx, dy;
		int loop = 0;
		do {
			dx = x + random(-range, range);
			dy = y + random(-range, range);
			if ((++loop) > 100) return;
		} while (!isReachable(dx, dy));
		walkTo(dx, dy);
	}

	private void _walkNearest(Point[] points) {
		int bestd = Integer.MAX_VALUE;
		Point bestp = null;
		for (Point p : points) {
			int x = p.x;
			int y = p.y;
			if (!isReachable(x, y)) continue;
			if (getX() == x && getY() == y) continue;
			int d = distanceTo(x, y);
			if (d < bestd) {
				bestd = d;
				bestp = p;
			}
		}
		if (bestp != null) {
			walkTo(bestp.x, bestp.y);
		}
	}

	private String _getRuntime() {
		long secs = ((System.currentTimeMillis() - start_time) / 1000);
		if (secs >= 3600) {
			return (secs / 3600) + " hours, " +
					((secs % 3600) / 60) + " mins, " +
					(secs % 60) + " secs.";
		}
		if (secs >= 60) {
			return secs / 60 + " mins, " +
					(secs % 60) + " secs.";
		}
		return secs + " secs.";
	}
}
