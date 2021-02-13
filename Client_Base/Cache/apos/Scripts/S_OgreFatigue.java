import java.awt.Point;
import java.text.DecimalFormat;
import java.util.Locale;

import javax.swing.JOptionPane;

public final class S_OgreFatigue extends Script {

	private static final int[][] trees = {
		{ 621, 791 },
		{ 609, 781 },
		{ 609, 776 },
		{ 605, 776 },
		{ 606, 775 },
		{ 598, 776 },
		{ 598, 771 },
		{ 596, 770 },
		{ 592, 774 },
		{ 589, 771 },
	};
	private static final int[] weapons = {
		593, 594
	};
	private static final boolean DEBUG = false;
	private int mode_id;
	private int food_id;
	private final PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;
	private long
	click_time,
	bank_time,
	menu_time,
	start_time;
	private int[] start_xp = new int[4];
	private final DecimalFormat iformat = new DecimalFormat("#,##0");
	private boolean lighting;

	public S_OgreFatigue(Extension ex)
	{
		super(ex);
		pw = new PathWalker(ex);
	}

	@Override
	public void init(String params)
	{
		setTrickMode(true);
		click_time = bank_time = menu_time = start_time = -1L;
		lighting = false;

		String[] food_types = {
			"Lobster", "Swordfish", "Shark"
		};
		Object sfood = JOptionPane.showInputDialog(null,
			"What kind of food?", "Ogre Fatigue",
			JOptionPane.QUESTION_MESSAGE, null,
			food_types, food_types[0]);

		if (sfood == null) {
			writeLine("You must select a type of food...");
			return;
		}

		switch ((String)sfood) {
		case "Lobster":
			food_id = 373;
			break;
		case "Swordfish":
			food_id = 370;
			break;
		case "Shark":
			food_id = 546;
			break;
		}

		Object smode = JOptionPane.showInputDialog(null,
			"Combat style?", "Ogre Fatigue",
			JOptionPane.QUESTION_MESSAGE, null,
			FIGHTMODES, FIGHTMODES[0]);

		mode_id = -1;
		for (int i = 0; i < FIGHTMODES.length; ++i) {
			if (FIGHTMODES[i].equals(smode)) {
				mode_id = i;
				break;
			}
		}

		if (mode_id == -1) {
			writeLine("You must select a combat style...");
			return;
		}

		pw.init(null);

		to_bank = pw.calcPath(
			617 + random(-3, 3),
			780 + random(-3, 3),
			587 + random(-1, 1),
			754 + random(-1, 1));

		from_bank = pw.calcPath(
			587 + random(-1, 1),
			754 + random(-1, 1),
			617 + random(-3, 3),
			780 + random(-3, 3));

		if (to_bank == null || from_bank == null) {
			writeLine("Error calculating path");
		}
	}

	@Override
	public int main()
	{
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			for (int i = 0; i < start_xp.length; ++i) {
				start_xp[i] = getXpForLevel(i);
			}
		}

		if (getFightMode() != mode_id) {
			setFightMode(mode_id);
			return random(600, 800);
		}

		if (inCombat()) {
			pw.resetWait();
			click_time = -1L;
			int weapon = getInventoryIndex(weapons);
			if (getAccurateFatigue() < 98.98 || getFatigue() == 100) {
				if (isItemEquipped(weapon)) {
					removeItem(weapon);
					return random(600, 800);
				}
				walkTo(getX(), getY());
				return random(400, 600);
			}
			if (!isItemEquipped(weapon)) {
				wearItem(weapon);
				return random(600, 800);
			}
			if (getCurrentLevel(3) <= 30) {
				walkTo(getX(), getY());
				return random(400, 600);
			}
			return random(600, 800);
		}

		if (isBanking()) {
			bank_time = -1L;
			menu_time = -1L;
			int food_needed = getEmptySlots();
			if (food_needed == 0) {
				closeBank();
				pw.setPath(from_bank);
				return random(600, 800);
			}
			if (bankCount(food_id) < food_needed) {
				writeLine("Out of food!");
				stopScript();
				setAutoLogin(false);
				return 0;
			}
			withdraw(food_id, food_needed);
			return random(600, 1000);
		}

		if (bank_time != -1L) {
			if (System.currentTimeMillis() > (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(100, 300);
		}

		if (isQuestMenu()) {
			bank_time = System.currentTimeMillis();
			menu_time = -1L;
			answer(0);
			return random(600, 800);
		}

		if (menu_time != -1L) {
			if (System.currentTimeMillis() > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(100, 300);
		}

		if (pw.walkPath()) {
			if (should_bank() && inside_bank()) {
				pw.setPath(null);
			}
			return 0;
		}

		if (inside_bank()) {
			if (should_bank()) {
				int[] npc = getNpcByIdNotTalk(BANKERS);
				if (npc[0] != -1) {
					talkToNpc(npc[0]);
					menu_time = System.currentTimeMillis();
				}
				return random(600, 800);
			}
			pw.setPath(from_bank);
			return 0;
		}

		if (should_bank()) {
			click_time = -1L;
			pw.setPath(to_bank);
			return 0;
		}

		if (getCurrentLevel(3) <= 30) {
			useItem(getInventoryIndex(food_id));
			return random(600, 800);
		}

		if (getFatigue() == 100) {
			click_time = -1L;
			int p1_x = 609;
			int p1_y = 780;
			int p2_x = 622;
			int p2_y = 791;
			if (getObjectIdFromCoords(621, 791) == 4 ||
				distanceTo(p1_x, p1_y) <= distanceTo(p2_x, p2_y)) {
				if (getY() > 781) {
					debug_println("walk to sleep");
					walkTo(p1_x, p1_y);
					return random(1000, 1500);
				}
			} else if (getX() < 622) {
				debug_println("walk to sleep");
				walkTo(p2_x, p2_y);
				return random(1000, 1500);
			}
			debug_println("use bag");
			useSleepingBag();
			return random(1000, 2000);
		}

		if (click_time != -1L) {
			if (System.currentTimeMillis() > (click_time + 8000L)) {
				click_time = -1L;
			}
			// hack to work around lag
			if (lighting) {
				int[] logs = getItemById(14);
				if (logs[1] != getX() || logs[2] != getY()) {
					click_time = -1L;
				}
			}
			return 0;
		}

		lighting = false;

		if (getAccurateFatigue() >= 98.98) {
			int weapon = getInventoryIndex(weapons);
			if (!isItemEquipped(weapon)) {
				wearItem(weapon);
				return random(600, 800);
			}
			int best_npc = -1;
			int best_dist = Integer.MAX_VALUE;
			int count = countNpcs();
			for (int i = 0; i < count; ++i) {
				if (getNpcId(i) != 312) {
					continue;
				}
				int x = getNpcX(i);
				int y = getNpcY(i);
				if (!isReachable(x, y)) {
					continue;
				}
				if (distanceTo(x, y, 610, 782) > 16) {
					continue;
				}
				int dist = distanceTo(x, y);
				if (dist < best_dist) {
					best_npc = i;
					best_dist = dist;
				}
			}
			if (best_npc != -1) {
				attackNpc(best_npc);
				return random(600, 1000);
			}
			// no npc found
			if (getX() < 606) {
				int target_x = getX() >= 601 ? 612 : 604;
				int x, y;
				do {
					x = target_x + random(-3, 3);
					y = 782 + random(-3, 3);
				} while (!isReachable(x, y));
				walkTo(x, y);
			}
			return random(600, 1000);
		}

		int invlogs = getInventoryIndex(14);
		if (invlogs != -1) {
			dropItem(invlogs);
			return random(1000, 1500);
		}

		int[] logs = getItemById(14);
		if (logs[0] != -1 && !isObjectAt(logs[1], logs[2])) {
			// only light logs next to trees
			boolean found = false;
			for (int[] tree : trees) {
				if (distanceTo(logs[1], logs[2], tree[0], tree[1]) < 2) {
					found = true;
					break;
				}
			}
			int box = getInventoryIndex(166);
			if (found && box != -1) {
				useItemOnGroundItem(box, 14, logs[1], logs[2]);
				click_time = System.currentTimeMillis();
				lighting = true;
				return random(600, 800);
			}
		}

		int[] tree = null;
		int walk_x = 0;
		int walk_y = 0;
		int best_dist = Integer.MAX_VALUE;

		for (int i = 0; i < trees.length; ++i) {
			int[] t = trees[i];
			// skip stumps
			if (getObjectIdFromCoords(t[0], t[1]) == 4) {
				continue;
			}
			int dist = distanceTo(t[0], t[1]);
			if (dist >= best_dist) {
				continue;
			}
			Point p = get_walk_point(t[0], t[1]);
			if (p != null) {
				walk_x = p.x;
				walk_y = p.y;
				tree = t;
				best_dist = dist;
			}
		}

		if (tree == null) {
			debug_println("no tree");
			return random(600, 800);
		}

		if (getX() != walk_x || getY() != walk_y) {
			debug_println("walk to tree");
			walkTo(walk_x, walk_y);
			return random(800, 1200);
		}

		debug_println("cut");
		atObject(tree[0], tree[1]);
		click_time = System.currentTimeMillis();
		return random(600, 800);
	}

	@Override
	public void paint()
	{
		int x = 10;
		int y = 215;
		drawString("S Ogre Fatigue", x, y, 2, 0xFFD900);
		y += 15;
		drawString("Runtime: " + get_runtime(), x, y, 2, 0xFFD900);
		y += 15;
		for (int i = 0; i < start_xp.length; ++i) {
			int gained = getXpForLevel(i) - start_xp[i];
			if (gained == 0) continue;
			drawString(String.format("%s XP gained: %s (%s/h)",
				SKILL[i], iformat.format(gained), per_hour(gained)),
				x, y, 2, 0xFFD900);
			y += 15;
		}
	}

	@Override
	public void onServerMessage(String str)
	{
		debug_println(str);
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("get some wood") ||
			str.contains("fire catches") ||
			str.contains("nothing interesting")) {
			click_time = -1L;
		} else if (str.contains("busy")) {
			menu_time = -1L;
		}
	}

	private Point get_walk_point(int tree_x, int tree_y)
	{
		Point best_point = null;
		int best_dist = Integer.MAX_VALUE;
		// try to find a spot around a tree where a fire can be lit
		for (int x = -1; x <= 1; ++x) {
			ly: for (int y = -1; y <= 1; ++y) {
				if (x == 0 && y == 0) {
					continue;
				}
				if (x != 0 && y != 0) {
					// can't cut from a diagonal
					continue;
				}
				int wx = tree_x + x;
				int wy = tree_y + y;
				int dist = distanceTo(wx, wy);
				if (dist >= best_dist) {
					continue;
				}
				int npc_count = countNpcs();
				for (int i = 0; i < npc_count; ++i) {
					if (getNpcX(i) == wx && getNpcY(i) == wy) {
						continue ly;
					}
				}
				if (!isObjectAt(wx, wy) && isReachable(wx, wy)) {
					best_point = new Point(wx, wy);
					best_dist = dist;
				}
			}
		}
		// fall back to checking diagonals
		for (int x = -1; x <= 1; ++x) {
			ly: for (int y = -1; y <= 1; ++y) {
				int wx = tree_x + x;
				int wy = tree_y + y;
				int dist = distanceTo(wx, wy);
				if (dist >= best_dist) {
					continue;
				}
				int npc_count = countNpcs();
				for (int i = 0; i < npc_count; ++i) {
					if (getNpcX(i) == wx && getNpcY(i) == wy) {
						continue ly;
					}
				}
				if (!isObjectAt(wx, wy) && isReachable(wx, wy)) {
					best_point = new Point(wx, wy);
					best_dist = dist;
				}
			}
		}
		return best_point;
	}

	private static void debug_println(String str)
	{
		if (DEBUG) {
			System.out.println(str);
		}
	}

	private boolean inside_bank()
	{
		return getX() <= 590 && getX() >= 585 &&
			getY() >= 750 && getY() <= 758;
	}

	private boolean should_bank()
	{
		return getInventoryIndex(food_id) == -1;
	}

	private String get_runtime()
	{
		long millis = (System.currentTimeMillis() - start_time) / 1000;
		long second = millis % 60;
		long minute = (millis / 60) % 60;
		long hour = (millis / (60 * 60)) % 24;
		long day = (millis / (60 * 60 * 24));

		if (day > 0L) {
			return String.format("%02d days, %02d hrs, %02d mins",
				day, hour, minute);
		}
		if (hour > 0L) {
			return String.format("%02d hours, %02d mins, %02d secs",
				hour, minute, second);
		}
		if (minute > 0L) {
			return String.format("%02d minutes, %02d seconds",
				minute, second);
		}
		return String.format("%02d seconds", second);
	}

	private String per_hour(int count)
	{
		if (count == 0) return "0";
		double amount = count * 60.0 * 60.0;
		double secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}
}