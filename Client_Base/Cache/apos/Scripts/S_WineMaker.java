import java.text.DecimalFormat;
import java.util.Locale;

public final class S_WineMaker extends Script {

	private static final int JUG = 140;
	private static final int WATER = 141;
	private static final int GRAPES = 143;
	private static final int WINE = 142;

	private static final int SINK = 48;

	private static final int BANK_X = 151;
	private static final int BANK_Y = 506;

	private static final int GUILD_X = 179;
	private static final int GUILD_Y = 489;

	private static final int COOKING = 7;

	/*
	 * for selecting the combat style for the skill we have the most
	 * xp in
	 */
	private static final int[] melee_skills = { 2, 0, 1 };

	private long start_time;
	private long bank_time;
	private long menu_time;
	private long click_time;
	private boolean taken_grapes;

	private int start_xp;
	private int wine_made;

	private PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	public S_WineMaker(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
	}

	@Override
	public void init(String params) {
		start_time = -1L;
		bank_time = -1L;
		menu_time = -1L;
		click_time = Long.MIN_VALUE;
		pw.init(null);
		to_bank = pw.calcPath(GUILD_X, GUILD_Y, BANK_X, BANK_Y);
		from_bank = pw.calcPath(BANK_X, BANK_Y, GUILD_X, GUILD_Y);
	}

	@Override
	public int main() {
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			start_xp = getXpForLevel(COOKING);
		}
		int highest_xp = 0;
		int style = 1;
		for (int i = 0; i < melee_skills.length; ++i) {
			int xp = getXpForLevel(melee_skills[i]);
			if (xp > highest_xp) {
				xp = highest_xp;
				style = i + 1;
			}
		} 
		if (getFightMode() != style) {
			System.out.printf("Setting combat style to %s\n",
			    FIGHTMODES[style]);
			setFightMode(style);
			return random(600, 800);
		}
		if (System.currentTimeMillis() < click_time) {
			return 0;
		}
		if (pw.walkPath()) return 0;
		if (isBanking()) {
			bank_time = -1L;
			int count = getInventoryCount(WINE);
			if (count > 0) {
				deposit(WINE, count);
			} else {
				closeBank();
				pw.setPath(from_bank);
			}
			return random(600, 800);
		} else if (bank_time != -1L) {
			if (System.currentTimeMillis() >= (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(200, 300);
		}
		if (isQuestMenu()) {
			menu_time = -1L;
			answer(0);
			bank_time = System.currentTimeMillis();
			return random(600, 800);
		} else if (menu_time != -1L) {
			if (System.currentTimeMillis() >= (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(200, 300);
		}
		if (getY() < 944) {
			return ground_floor();
		}
		if (getY() < (944 * 2)) {
			return first_floor();
		}
		return second_floor();
	}

	@Override
	public void paint() {
		final int gray = 0xC4C4C4;
		int y = 25;
		drawString("S WineMaker", 25, y, 1, gray);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    25, y, 1, gray);
		y += 15;
		drawString(String.format("Wine made: %s (%s/h)",
		    iformat.format(wine_made), per_hour(wine_made)),
		    25, y, 1, gray);
		y += 15;
		int xp_gained = getXpForLevel(COOKING) - start_xp;
		drawString(String.format("Cooking XP: %s (%s/h)",
		    iformat.format(xp_gained), per_hour(xp_gained)),
		    25, y, 1, gray);
		y += 15;
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.startsWith("you squeeze the grapes into the jug")) {
			click_time = System.currentTimeMillis() + 8000L;
		} else if (str.startsWith("you make some nice wine")) {
			click_time = Long.MIN_VALUE;
			++wine_made;
		} else if (str.contains("busy")) {
			menu_time = -1L;
		}
	}

	private int ground_floor() {
		if (getFatigue() > 95) {
			useSleepingBag();
			return random(1000, 2000);
		}
		int jug = getInventoryIndex(JUG);
		if (jug != -1) {
			useItemOnObject(JUG, SINK);
			return random(600, 1000);
		}
		int water = getInventoryIndex(WATER);
		int grapes = getInventoryIndex(GRAPES);
		if (water != -1 && grapes != -1) {
			useItemWithItem(grapes, water);
			click_time = System.currentTimeMillis() + 8000L;
			return random(600, 800);
		}
		if (get_ids_to_take().length == 0) {
			if (isAtApproxCoords(179, 489, 1)) {
				pw.setPath(to_bank);
				return 0;
			}
			if (getX() > 175 && getY() < 488) {
				/* go outside */
				atWallObject(179, 488);
				return random(600, 1200);
			}
			int[] npc = getNpcByIdNotTalk(BANKERS);
			if (npc[0] != -1) {
				talkToNpc(npc[0]);
				menu_time = System.currentTimeMillis();
			}
			return random(600, 800);
		}
		if (getY() > 487) {
			/* go inside */
			atWallObject(179, 488);
		} else {
			/* go up */
			atObject(180, 486);
		}
		return random(600, 1200);
	}

	private int first_floor() {
		if (get_ids_to_take().length == 0) {
			/* go down */
			atObject(180, 1430);
		} else {
			/* go up */
			atObject(178, 1430);
		}
		return random(600, 1200);
	}

	private int second_floor() {
		int[] ids = get_ids_to_take();

		if (ids.length == 0) {
			atObject(177, 2374);
			return random(600, 1200);
		}

		int[] item = getItemById(ids);

		if (item[0] == -1 || distanceTo(item[1], item[2]) > 12) {
			/* walk near the spawn point */
			if (inArray(ids, JUG) && taken_grapes) {
				if (getX() != 176 && getY() != 2371) {
					walkTo(176, 2371);
					return random(1000, 1500);
				}
			} else if (inArray(ids, GRAPES)) {
				if (getX() != 180 && getY() != 2374) {
					walkTo(180, 2374);
					return random(1000, 1500);
				}
			}
		} else {
			taken_grapes = (item[0] == GRAPES);
			pickupItem(item[0], item[1], item[2]);
		}
		return random(600, 800);
	}

	private int[] get_ids_to_take() {
		if (getInventoryCount() == MAX_INV_SIZE) {
			return new int[] {};
		}

		int grape_count = getInventoryCount(GRAPES);
		int base_count = getInventoryCount(JUG, WATER);

		if (grape_count > base_count) {
			return new int[] { JUG };
		} else if (base_count > grape_count) {
			return new int[] { GRAPES };
		}
		return new int[] { GRAPES, JUG };
	}

	private String per_hour(int count) {
		double amount, secs;

		if (count == 0) return "0";
		amount = count * 60.0 * 60.0;
		secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}

	private static String get_time_since(long t) {
		long millis = (System.currentTimeMillis() - t) / 1000;
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
}
