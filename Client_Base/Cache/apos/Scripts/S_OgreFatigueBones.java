import java.text.DecimalFormat;
import java.util.Locale;

import javax.swing.JOptionPane;

public final class S_OgreFatigueBones extends Script {

	private int mode_id;
	private int food_id;
	private final PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;
	private long
	bank_time,
	menu_time,
	start_time;
	private int start_xp;
	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	public S_OgreFatigueBones(Extension ex)
	{
		super(ex);
		pw = new PathWalker(ex);
	}

	@Override
	public void init(String params)
	{
		bank_time = menu_time = start_time = -1L;

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
			587 + random(-2, 2),
			754 + random(-1, 1));

		from_bank = pw.calcPath(
			587 + random(-2, 2),
			754 + random(-1, 1),
			617 + random(-3, 3),
			780 + random(-3, 3));
	}

	@Override
	public int main()
	{
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			start_xp = getXpForLevel(5);
		}

		if (getFightMode() != mode_id) {
			setFightMode(mode_id);
			return random(600, 800);
		}

		if (inCombat()) {
			pw.resetWait();
			walkTo(getX(), getY());
			return random(400, 600);
		}

		if (isBanking()) {
			bank_time = -1L;
			menu_time = -1L;
			int food_needed = getEmptySlots() - 1;
			if (food_needed == 0) {
				closeBank();
				pw.setPath(from_bank);
				return random(600, 800);
			} else if (food_needed < 0) {
				deposit(food_id, 1);
				return random(600, 1000);
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
			pw.setPath(to_bank);
			return 0;
		}

		if (getCurrentLevel(3) <= 30) {
			useItem(getInventoryIndex(food_id));
			return random(600, 800);
		}

		if (getFatigue() > 98) {
			if (getX() != 609 || getY() != 780) {
				walkTo(609, 780);
			} else {
				useSleepingBag();
			}
			return random(1000, 2000);
		}

		int bones = getInventoryIndex(413);
		if (bones != -1) {
			useItem(bones);
			return random(600, 800);
		}

		if (getInventoryCount() == MAX_INV_SIZE) {
			useItem(getInventoryIndex(food_id));
			return random(600, 800);
		}

		int[] best_item = { -1, -1 };
		int best_dist = Integer.MAX_VALUE;
		int gitemcount = getGroundItemCount();
		for (int i = 0; i < gitemcount; ++i) {
			if (getGroundItemId(i) != 413) {
				continue;
			}
			int x = getItemX(i);
			int y = getItemY(i);
			int dist = distanceTo(x, y, 610, 782);
			if (dist > 16 || dist >= best_dist) {
				continue;
			}
			if (!isReachable(x, y)) {
				continue;
			}
			best_item[0] = x;
			best_item[1] = y;
			best_dist = dist;
		}
		if (best_item[0] != -1) {
			pickupItem(413, best_item[0], best_item[1]);
			return random(600, 1000);
		}
		if (getX() != 609 || getY() != 780) {
			walkTo(609, 780);
			return random(1000, 2000);
		}
		return random(200, 300);
	}

	@Override
	public void paint()
	{
		int x = 10;
		int y = 18;
		drawString("S Ogre Prayer", x, y, 2, 0xFFFFFF);
		y += 15;
		drawString("Runtime: " + get_runtime(), x, y, 2, 0xFFFFFF);
		y += 15;
		int gained = start_xp - getXpForLevel(5);
		drawString(String.format("Prayer XP gained: %s (%s/h)",
			iformat.format(gained), per_hour(gained)),
			x, y, 2, 0xFFFFFF);
		y += 15;
	}

	@Override
	public void onServerMessage(String str)
	{
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
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