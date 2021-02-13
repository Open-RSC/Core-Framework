import java.text.DecimalFormat;
import java.util.Locale;
import java.awt.Point;

public final class S_DummyTrainer extends Script {

	private static final class Alcohol {
		final String name;
		final int id;
		final int empty_id;

		public Alcohol(String name, int id, int empty_id)
		{
			this.name = name;
			this.id = id;
			this.empty_id = empty_id;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	private static final Alcohol[] drink_types = {
		new Alcohol("Wine", 142, 140),
		new Alcohol("Dwarven Stout", 269, 620),
		new Alcohol("Asgarnian Ale", 267, 620)
	};

	private static final Point[] dummies = {
		new Point(105, 504),
		new Point(105, 502),
		new Point(106, 501),
		new Point(104, 501),
		new Point(102, 501)
	};

	private static final int ATTACK = 0;
	private static final int DUMMY = 49;

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	private Alcohol drink;
	private int target_level;

	private long start_time;
	private long menu_time;
	private long bank_time;
	private long click_time;
	private int start_xp;
	private int walk_x;
	private int walk_y;

	private int withdrawn_count;
	private boolean has_withdrawn;

	public S_DummyTrainer(Extension ex)
	{
		super(ex);
	}

	@Override
	public void init(String params)
	{
		params = params.trim();
		try {
			target_level = Integer.parseInt(params);
			System.out.printf("Stopping at %d attack.\n", target_level);
		} catch (NumberFormatException e) {
			System.out.println("ERROR: Params must be attack level to stop at.");
		}
		start_time = -1L;
	}

	@Override
	public int main()
	{
		if (start_time == -1L) {
			drink = null;
			for (Alcohol type : drink_types) {
				if (getInventoryIndex(type.id) != -1) {
					drink = type;
					break;
				}
			}
			if (drink == null) {
				System.out.println("ERROR: No alcohol found in inv!");
				stopScript();
				setAutoLogin(false);
				return 0;
			}
			start_time = System.currentTimeMillis();
			start_xp = getXpForLevel(ATTACK);
			bank_time = -1L;
			menu_time = -1L;
			walk_x = 0;
			walk_y = 0;
			withdrawn_count = 0;
			has_withdrawn = false;
			click_time = -1L;
		}
		if (getLevel(ATTACK) >= target_level) {
			System.out.printf("%d attack reached!\n",
			    target_level);
			setAutoLogin(false);
			logout();
			stopScript();
			return 0;
		}
		if (getFightMode() != 2) {
			System.out.printf("Set combat style to %s.\n",
			    FIGHTMODES[2]);
			setFightMode(2);
			return random(600, 800);
		}
		if (getFatigue() > 99) {
			useSleepingBag();
			return random(1000, 2000);
		}
		if (getY() > 509) {
			return inside_bank();
		}
		if (getY() < 506) {
			return inside_training_room();
		}
		return on_road();
	}

	@Override
	public void paint()
	{
		if (start_time == -1L) {
			return;
		}
		int x = 125;
		int y = 60;
		drawBoxAlphaFill(x - 8, y - 18, 300, 85, 120, 0x0);
		drawString(getClass().getSimpleName(), x, y, 1, 0xFFD900);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    x, y, 1, 0xFFFFFF);
		y += 15;
		int gained = getXpForLevel(ATTACK) - start_xp;
		drawString(String.format("XP gained: %s (%s/h)",
		    iformat.format(gained),
		    per_hour(gained)),
		    x, y, 1, 0xFFFFFF);
		y += 15;
		drawString(String.format("%s withdrawn: %s (%s/h)",
		    drink.name,
		    iformat.format(withdrawn_count),
		    per_hour(withdrawn_count)),
		    x, y, 1, 0xFFFFFF);
	}

	@Override
	public void onServerMessage(String str)
	{
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy at the moment")) {
			menu_time = -1L;
		} else if (str.contains("standing here")) {
			walk_x = 103 + random(-1, 1);
			walk_y = 503 + random(-1, 1);
		} else if (str.contains("hit the dummy") ||
		    str.contains("nothing more")) {
			click_time = -1L;
		}
	}

	private int inside_bank()
	{
		if (isBanking()) {
			bank_time = -1L;
			if (getInventoryIndex(drink.id) != -1) {
				closeBank();
				has_withdrawn = false;
				return random(600, 800);
			}
			if (bankCount(drink.id) < 1) {
				System.out.println("Out of drinks!");
				setAutoLogin(false);
				stopScript();
				return 0;
			}
			if (!has_withdrawn) {
				withdrawn_count += getEmptySlots();
				has_withdrawn = true;
			}
			withdraw(drink.id, getEmptySlots());
			return random(600, 800);
		} else if (bank_time != -1) {
			long time = System.currentTimeMillis();
			if ((time - bank_time) > 8000L) {
				bank_time = -1L;
			}
			return random(300, 400);
		}
		if (isQuestMenu()) {
			answer(0);
			menu_time = -1L;
			bank_time = System.currentTimeMillis();
			return random(600, 800);
		} else if (menu_time != -1) {
			long time = System.currentTimeMillis();
			if ((time - menu_time) > 8000L) {
				menu_time = -1L;
			}
			return random(300, 400);
		}
		if (getInventoryIndex(drink.id) != -1) {
			if (open_bank_door() && open_training_door()) {
				walk_to_unused_dummy();
			}
			return random(1000, 2000);
		}
		int[] banker = getAllNpcById(BANKERS);
		if (banker[0] != -1) {
			talkToNpc(banker[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	private int inside_training_room()
	{
		if (walk_x != 0 && walk_y != 0) {
			if (getX() != walk_x || getY() != walk_y) {
				walkTo(walk_x, walk_y);
				return random(1000, 2000);
			}
			walk_x = 0;
			walk_y = 0;
		}
		if (getCurrentLevel(ATTACK) < 8) {
			long current_time = System.currentTimeMillis();
			int[] dummy = getObjectById(DUMMY);
			if (dummy[0] != -1 && !isWalking() &&
			    current_time > (click_time + 8000L)) {
				atObject(dummy[1], dummy[2]);
				click_time = current_time;
				return random(600, 800);
			}
			return random(300, 400);
		}
		int d = getInventoryIndex(drink.id);
		if (d != -1) {
			useItem(d);
			return random(1000, 1500);
		}
		int empty = getInventoryIndex(drink.empty_id);
		if (empty != -1) {
			dropItem(empty);
			return random(1000, 1500);
		}
		if (open_training_door() && open_bank_door()) {
			int[] banker = getAllNpcById(BANKERS);
			if (banker[0] != -1) {
				talkToNpc(banker[0]);
				menu_time = System.currentTimeMillis();
			} else {
				walk_to_bank();
			}
		}
		return random(1000, 1500);
	}

	private int on_road()
	{
		if (getInventoryIndex(drink.id) != -1) {
			if (open_training_door()) {
				walk_to_unused_dummy();
			}
		} else if (open_bank_door()) {
			int[] banker = getAllNpcById(BANKERS);
			if (banker[0] != -1) {
				talkToNpc(banker[0]);
				menu_time = System.currentTimeMillis();
			} else {
				walk_to_bank();
			}
		}
		return random(1000, 1500);
	}

	private void walk_to_bank()
	{
		walkTo(102 + random(-2, 2), 510 + random(1, 3));
	}

	private void walk_to_unused_dummy()
	{
		for (Point p : dummies) {
			boolean taken = false;
			int count = countPlayers();
			for (int i = 0; i < count; ++i) {
				if (getPlayerX(i) == p.x &&
				    getPlayerY(i) == p.y) {
					taken = true;
					break;
				}
			}
			if (taken) {
				continue;
			}
			walkTo(p.x, p.y);
			return;
		}
		walkTo(dummies[0].x, dummies[0].y);
	}

	private boolean open_training_door()
	{
		if (getWallObjectIdFromCoords(104, 506) == 2) {
			atWallObject(104, 506);
			return false;
		}
		return true;
	}

	private boolean open_bank_door()
	{
		if (getObjectIdFromCoords(102, 509) == 64) {
			atObject(102, 509);
			return false;
		}
		return true;
	}

	private String per_hour(int count)
	{
		double amount, secs;

		if (count == 0) return "0";
		amount = count * 60.0 * 60.0;
		secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}

	private static String get_time_since(long t)
	{
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
