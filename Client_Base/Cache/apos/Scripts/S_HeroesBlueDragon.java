import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * Blue Dragon Killer 
 * [2014-03-28]
 * adjusted wait time for dragon visibility
 * [2014-03-26]
 * use pathwalker
 * better prayer management
 * better lure protection
 * set combat style
 * safer banking (check for items being there)
 * randomize more wait times
 * timer-based banking (as opposed to "talk to the npc then hope for the best")
 * optimized banking (reduction in complexity from amount checks)
 * more constants, all of them static
 * banking OR burying bones
 * world hopping
 * fixed wrong init() decl
 * attempted fix for bugs in gate check, death check related to game loading time
 * more drops from rsclassics.org. those not useful for any skill or alching have been left out. modify if you want.
 * alching
 * paint 
 * [2012-07-29]
 * released
 * @author Storm
 */
public final class S_HeroesBlueDragon extends Script
	implements Runnable {

	public static final class Config {
		/*
		 * amount of food to withdraw. you're using prayer anyway so it might as well
		 * be small just to make up for lag where you take damage
		 */
		public int food_amount = 2;
		// Hits to eat at
		public int min_hits = 40;
		// Prayer to recharge at
		public int min_prayer = 20;
		// Fatigue to sleep at
		public int sleep_at = 90;

		// bury or bank bones?
		public boolean autobury = true;

		// can access world 1?
		public boolean veteran = false;

		// you may keep a fire staff in your inventory or use the dropped fire
		// runes.
		public boolean do_alch = false;
	
		public int combat_style = 2; // attack
	
		public int food_id = 546; // sharks
	
		public long min_hop_time = 14000L;
	}

	private final Config cfg = new Config();

	private final JSONgui gui = new JSONgui(getClass().getSimpleName(), cfg, null, this);

	private static final int
	GATE_CLOSED = 57,
	GATE_OPEN = 58,
	SLEEPING_BAG = 1263,
	PARALYZE_PRAYER = 12,
	FIRE_STAFF = 197,
	HIGH_ALCH = 28,
	LEVEL_PRAYER = 5,
	LEVEL_HITS = 3,
	BANK_DOOR = 64,
	FALADOR_TELEPORT = 18,
	BLUE_DRAGON = 202,

	DRAGON_BONES = 814,
	LAW_RUNE = 42,
	WATER_RUNE = 32,
	AIR_RUNE = 33,
	DRAGON_MED = 795,

	DRAGON_LEFT_HALF = 1277,
	LOOP_HALF_1 = 526,
	LOOP_HALF_2 = 527,
	NATURE_RUNE = 40,
	FIRE_RUNE = 31,
	RUNE_SQ = 403,
	MITHRIL_KITE = 130,
	ADAM_LARGE = 111,
	RUNE_DAGGER = 396,
	RUNE_BATTLE = 93,
	STEEL_LEGS = 121,
	MITHRIL_BATTLE = 91,
	ADAM_ORE = 154,
	ADAM_BAR = 174,
	UNCUT_SAPPHIRE = 160,
	UNCUT_EMERALD = 159,
	UNCUT_RUBY = 158,
	UNCUT_DIAMOND = 157,
	COINS = 10,
	DRAGON_SWORD = 593,
	DRAGON_AXE = 594;

	private static final int[] drops = {
		DRAGON_BONES, DRAGON_MED, LOOP_HALF_1, LOOP_HALF_2, DRAGON_LEFT_HALF,
		FIRE_RUNE, NATURE_RUNE, RUNE_SQ, MITHRIL_KITE, ADAM_LARGE, RUNE_DAGGER,
		RUNE_BATTLE, STEEL_LEGS, MITHRIL_BATTLE, ADAM_ORE, ADAM_BAR, COINS,
		UNCUT_SAPPHIRE, UNCUT_EMERALD, UNCUT_RUBY, UNCUT_DIAMOND, WATER_RUNE,
		LAW_RUNE
	};

	private static final int[] drops_no_deposit = {
		RUNE_BATTLE, DRAGON_MED, WATER_RUNE, LAW_RUNE, NATURE_RUNE
	};

	// used for re-equipping after alching
	private static final int[] weapons = {
		DRAGON_AXE, DRAGON_SWORD, RUNE_BATTLE
	};

	private static final int[] to_alch = {
		RUNE_DAGGER, ADAM_LARGE, MITHRIL_KITE, MITHRIL_BATTLE, STEEL_LEGS
	};

	private boolean check_gate;
	private boolean has_teleported;
	private long bank_time;
	private long menu_time;
	private long start_time;

	private final DecimalFormat int_format = new DecimalFormat("#,##0");

	private final int[] banked_counts = new int[drops.length];

	private long last_hop;

	private int prayer_xp;
	private int start_prayer;

	private PathWalker pw;
	private PathWalker.Path gate_to_guild;
	private PathWalker.Path bank_to_gate;

	private static final int
	BANK_X = 328,
	BANK_Y = 552,
	// gate, bank side
	GATE_B_X = 341,
	GATE_B_Y = 488,
	GUILD_X = 372,
	GUILD_Y = 441,
	// gate, guild side
	GATE_G_X = 342,
	GATE_G_Y = 488;

	public static void main(String[] argv)
	{
		new S_HeroesBlueDragon(null).init(null);
	}

	public S_HeroesBlueDragon(Extension ex)
	{
		super(ex);
		pw = new PathWalker(ex);
	}

	@Override
	public void init(String params)
	{
		//gui.showFrame();
		run();
		System.out.println();
		System.out.println("Heroes Guild Blue Dragon Killer");
		System.out.println("Configure the script by editing the variables.");
		System.out.println("Uses falador teleport.");
		System.out.println();
	}

	@Override
	public void run()
	{
		menu_time = -1L;
		bank_time = -1L;
		start_time = -1L;
		check_gate = false;
		has_teleported = false;
		last_hop = System.currentTimeMillis();
		Arrays.fill(banked_counts, 0);
		pw.init(null);
		bank_to_gate = pw.calcPath(BANK_X, BANK_Y, GATE_B_X, GATE_B_Y);
		gate_to_guild = pw.calcPath(GATE_G_X, GATE_G_Y, GUILD_X, GUILD_Y);
	}

	@Override
	public int main()
	{
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			if (cfg.autobury) {
				start_prayer = prayer_xp = getXpForLevel(LEVEL_PRAYER);
			}
		} else {
			prayer_xp = getXpForLevel(LEVEL_PRAYER);
		}

		if (isQuestMenu()) {
			answer(0);
			// stop waiting for menu, start waiting for bank
			bank_time = System.currentTimeMillis();
			menu_time = -1L;
			return random(1000, 2000);
		} else if (menu_time != -1L) {
			if (System.currentTimeMillis() >= (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(300, 400);
		}

		// Entered bank interface
		if (isBanking()) {
			bank_time = -1L; // stop waiting for bank

			// Deposit drops
			for (int i = 0; i < drops.length; i++) {
				if (index_of(drops_no_deposit, drops[i]) != -1) {
					continue;
				}
				int count = getInventoryCount(drops[i]);
				if (count > 0) {
					banked_counts[i] += count;
					deposit(drops[i], count);
					return random(800, 1000);
				}
			}

			// Withdraw food until we have FOOD_AMOUNT
			int shark_count = getInventoryCount(cfg.food_id);
			if (shark_count < cfg.food_amount) {
				int w = cfg.food_amount - shark_count;
				int bc = bankCount(cfg.food_id);
				if (bc <= 0) {
					return _end("Out of food");
				}
				if (bc < w) {
					w = bc;
				}
				withdraw(cfg.food_id, w);
				return random(800, 1000);
			}

			// Withdraw law runes
			if (getInventoryCount(LAW_RUNE) < 1) {
				if (bankCount(LAW_RUNE) < 1) {
					return _end("Out of law runes");
				}
				withdraw(LAW_RUNE, 1);
				return random(800, 1000);
			} else if (getInventoryCount(LAW_RUNE) > 1) {
				int count = getInventoryCount(LAW_RUNE) - 1;

				addBankedCount(LAW_RUNE, count);
				deposit(LAW_RUNE, count);
				return random(800, 1000);
			}

			// Withdraw water runes
			if (getInventoryCount(WATER_RUNE) < 1) {
				if (bankCount(WATER_RUNE) < 1) {
					return _end("Out of water runes");
				}
				withdraw(WATER_RUNE, 1);
				return random(800, 1000);
			} else if (getInventoryCount(WATER_RUNE) > 1) {
				int count = getInventoryCount(WATER_RUNE) - 1;

				addBankedCount(WATER_RUNE, count);
				deposit(WATER_RUNE, count);
				return random(800, 1000);
			}

			// Withdraw air runes
			if (getInventoryCount(AIR_RUNE) < 3) {
				if (bankCount(AIR_RUNE) < 3) {
					return _end("Out of air runes");
				}
				withdraw(AIR_RUNE, 3);
				return random(800, 1000);
			} else if (getInventoryCount(AIR_RUNE) > 3) {
				int count = getInventoryCount(AIR_RUNE) - 3;

				addBankedCount(AIR_RUNE, count);
				deposit(AIR_RUNE, count);
				return random(800, 1000);
			}

			if (!cfg.do_alch) {
				int count = getInventoryCount(NATURE_RUNE);
				if (count > 0) {
					deposit(NATURE_RUNE, count);
					addBankedCount(NATURE_RUNE, count);
					return random(600, 1000);
				}
			}

			has_teleported = false;
			closeBank();
			return random(1000, 2000);
		} else if (bank_time != -1L) {
			if (System.currentTimeMillis() >= (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(300, 400);
		}

		// Entered combat
		if (inCombat()) {
			// Run from combat when the prayer/hp is low.
			if (get_prayer() <= cfg.min_prayer || get_hits() <= cfg.min_hits) {
				walkTo(getX(), getY());
				return random(600, 700);
			}
			// Enable the prayer and wait 600-700 if it is off.
			if (!isPrayerEnabled(PARALYZE_PRAYER)) {
				enablePrayer(PARALYZE_PRAYER);
				return random(600, 700);
			}
			if (getFightMode() != cfg.combat_style) {
				setFightMode(cfg.combat_style);
				return random(300, 400);
			}
			return random(600, 800);
		}

		// Detect death
		if (isAtApproxCoords(128, 640, 20)) {
			return _end("Died, stopping script. :(");
		}

		if (check_gate) {
			check_gate = false;
			if (getObjectById(GATE_OPEN)[0] == -1 && getObjectById(GATE_CLOSED)[0] == -1) {
				System.out.println("Gate bugged. Logging out.");
				logout();
				return random(1000, 2000);
			}
		}

		if (cfg.autobury) {
			int index = getInventoryIndex(DRAGON_BONES);
			if (index != -1) {
				useItem(index);
				return random(600, 800);
			}
		}

		if (pw.walkPath()) return 0;

		// Normal ground level
		if (getY() < 700) {
			if (getY() <= 441) {
				// We are on the ground level of the guild
				return handle_guild_entry();
			} else if (has_teleported) {
				return handle_bank();
			} else if (getX() == 341) {
				// Open members gate
				atObject(341, 487);
				return random(2700, 3300);
			} else if (getX() < 341) {
				pw.setPath(bank_to_gate);
				return random(600, 900);
			} else if (getX() > 341) {
				pw.setPath(gate_to_guild);
				return random(600, 900);
			}
			return random(800, 1200);
		}

		// Underground
		if (getY() > 3000) {
			return handle_underground();
		}

		// Altar level of guild
		if (getY() > 1000) {
			return handle_restore();
		}

		return random(800, 1200);
	}

	private void addBankedCount(int id, int count)
	{
		for (int i = 0; i < drops.length; ++i) {
			if (drops[i] == id) {
				banked_counts[i] += count;
				break;
			}
		}
	}

	/**
	 * Kill dragons
	 */
	private int handle_underground()
	{
		// Open that gate if it is closed
		int[] closed_gate = getObjectById(GATE_CLOSED);
		if (closed_gate[0] != -1) {
			atObject(closed_gate[1], closed_gate[2]);
			return random(1200, 2000);
		}
		// If we can't find the opened gate, it may be glitched.
		int[] open_gate = getObjectById(GATE_OPEN);
		if (open_gate[0] == -1) {
			check_gate = true;
			return random(2000, 3000);
		}
		if (getX() < 365) {
			walkTo(370 + random(-1, 1), 3279 + random(-1, 1));
			return random(1000, 2000);
		}
		// Go upstairs if we need to recharge or bank
		if ((getFatigue() >= cfg.sleep_at && hasInventoryItem(SLEEPING_BAG)) ||
				get_prayer() <= cfg.min_prayer || should_bank()) {
			atObject(368, 3270);
			return random(1000, 1500);
		}
		// Eat food when the minimum hits is reached
		if (get_hits() <= cfg.min_hits) {
			int foods = getInventoryIndex(cfg.food_id);
			if (foods != -1) {
				useItem(foods);
			}
			return random(600, 800);
		}
		if (!isPrayerEnabled(PARALYZE_PRAYER)) {
			enablePrayer(PARALYZE_PRAYER);
			return random(600, 700);
		}
		// Pick up drops
		int[] item = getItemById(drops);
		if (item[0] != -1 && item[1] > 365) {
			pickupItem(item[0], item[1], item[2]);
			return random(1000, 1500);
		}
		// Attack a dragon
		int[] npc = getNpcById(BLUE_DRAGON);
		if (npc[0] != -1) {
			attackNpc(npc[0]);
			return random(600, 1000);
		}
		if (System.currentTimeMillis() >= (last_hop + cfg.min_hop_time)) {
			_hop();
			return random(2000, 3000);
		}
		return random(800, 1200);
	}

	/**
	 * Recharge prayer at altar
	 */
	private int handle_restore()
	{
		if (isPrayerEnabled(PARALYZE_PRAYER)) {
			disablePrayer(PARALYZE_PRAYER);
			return random(600, 700);
		}
		int ret = _alch();
		if (ret != -1) {
			return ret;
		}
		// Recharge if prayer not 100%
		if (!is_prayer_full()) {
			atObject(369, 1381);
			return random(1000, 2000);
		}
		// Teleport to falador if we need to bank
		if (should_bank()) {
			has_teleported = true;
			castOnSelf(FALADOR_TELEPORT);
			return random(2300, 2700);
		}
		// Go back downstairs
		atObject(375, 1382);
		return random(2700, 3300);
	}

	/**
	 * Do things on the ground level of the guild
	 */
	private int handle_guild_entry()
	{
		if (isPrayerEnabled(PARALYZE_PRAYER)) {
			disablePrayer(PARALYZE_PRAYER);
			return random(600, 700);
		}
		// Open the door
		if (getX() == 372 && getY() == 441) {
			atWallObject(372, 441);
			return random(1700, 2300);
		}
		// Sleep
		if (getFatigue() >= cfg.sleep_at && hasInventoryItem(SLEEPING_BAG)) {
			useSleepingBag();
			return random(1000, 1500);
		}
		// Go upstairs to use the altar
		if (!is_prayer_full()) {
			atObject(375, 438);
			return random(1500, 3000);
		}
		// Teleport to falador if we need to bank
		if (should_bank()) {
			has_teleported = true;
			castOnSelf(FALADOR_TELEPORT);
			return random(2300, 2700);
		}
		// Otherwise go downstairs to kill dragons
		atObject(368, 438);
		return random(2700, 3300);
	}

	private int handle_bank()
	{
		if (isPrayerEnabled(PARALYZE_PRAYER)) {
			disablePrayer(PARALYZE_PRAYER);
			return random(600, 700);
		}
		// Walk near to the bank
		if (getX() < 320) {
			walkTo(325, 553);
			return random(2700, 3300);
		}
		// Open door
		if (getX() < 328) {
			int[] object = getObjectById(BANK_DOOR);
			if (object[0] != -1) {
				atObject(object[1], object[2]);
				return random(1000, 1500);
			}
		}
		int ret = _alch();
		if (ret != -1) {
			return ret;
		}
		int[] npc = getNpcByIdNotTalk(BANKERS);
		if (npc[0] != -1) {
			talkToNpc(npc[0]);
			menu_time = System.currentTimeMillis();
			return random(1000, 2000);
		}
		return random(600, 800);
	}

	private boolean should_bank()
	{
		return getInventoryCount() == MAX_INV_SIZE || !hasInventoryItem(cfg.food_id);
	}

	private int get_prayer()
	{
		return getCurrentLevel(LEVEL_PRAYER);
	}

	private boolean is_prayer_full()
	{
		return getCurrentLevel(LEVEL_PRAYER) == getLevel(LEVEL_PRAYER);
	}

	private int get_hits()
	{
		return getCurrentLevel(LEVEL_HITS);
	}

	private int _end(String reason)
	{
		System.out.println(reason);
		setAutoLogin(false);
		stopScript();
		return 0;
	}

	private void _hop()
	{
		switch (getWorld()) {
			case 1:
				hop(2);
				break;
			case 2:
				hop(3);
				break;
			case 3:
				if (cfg.veteran)
					hop(1);
				else
					hop(2);
				break;
		}
		last_hop = System.currentTimeMillis();
	}

	private int _alch()
	{
		if (isPrayerEnabled(PARALYZE_PRAYER)) {
			disablePrayer(PARALYZE_PRAYER);
			return random(600, 700);
		}
		if (!cfg.do_alch) {
			return -1;
		}
		int index = getInventoryIndex(to_alch);
		if (index == -1) {
			return equip_weapon();
		}
		if (!hasInventoryItem(NATURE_RUNE)) {
			return equip_weapon();
		}
		int fire_staff = getInventoryIndex(FIRE_STAFF);
		if (fire_staff != -1) {
			if (!isItemEquipped(fire_staff)) {
				wearItem(fire_staff);
				return random(800, 1200);
			}
		} else if (getInventoryCount(FIRE_RUNE) < 5) {
			return -1;
		}
		castOnItem(HIGH_ALCH, index);
		return random(1200, 1600);
	}

	private int equip_weapon()
	{
		for (int id : weapons) {
			int index = getInventoryIndex(id);
			if (index == -1) continue;
			if (isItemEquipped(index)) {
				return -1;
			}
			wearItem(index);
			return random(800, 1200);
		}
		return -1;
	}

	private static int index_of(int[] haystack, int needle)
	{
		for (int i = 0; i < haystack.length; ++i) {
			if (needle == haystack[i]) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void onServerMessage(String str)
	{
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		}
	}

	@Override
	public void paint()
	{
		int x = 25;
		int y = 25;
		final int white = 0xFFFFFF;
		drawString("S HeroesBDK", x, y, 1, white);
		y += 15;
		drawString("Runtime: " + get_runtime(), x, y, 1, white);
		y += 15;
		if (cfg.autobury) {
			int dif = prayer_xp - start_prayer; 
			drawString("Prayer XP gained: " + int_format(dif), x, y, 1, white);
			y += 15;
			drawString(per_hour(dif) + " XP per hour", x, y, 1, white);
			y += 15;
		}
		drawString("Items banked:", x, y, 1, white);
		y += 15;
		x += 10;
		for (int i = 0; i < drops.length; ++i) {
			int count = banked_counts[i];
			if (count <= 0) continue;
			drawString(getItemNameId(drops[i]) + ": " +
				int_format(count) +
				" (" + per_hour(banked_counts[i]) + "/h)",
				x, y, 1, white);
			y += 15;
		}
	}

	private String per_hour(int total)
	{
		if (total == 0) return "0";
		double amount = total * 60.0 * 60.0;
		double secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return int_format((long) (amount / secs));
	}

	private String int_format(long l)
	{
		return int_format.format(l);
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
}
