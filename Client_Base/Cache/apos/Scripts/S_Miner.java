import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.Set;

import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_Miner extends Script
    implements ActionListener {

	/* rock selection improvements inspired by aero */

	public S_Miner(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
	}

	public static void main(String[] argv) {
		new S_Miner(null).init(null);
	}

	private static final int[] bad_gems = {
		889, 890, 891, 892, 893, 894
	};

	private static final int SLEEPING_BAG = 1263;
	private static final int CHISEL = 167;
	private static final int CRUSHED_GEM = 915;
	private static final int GNOME_BALL = 981;
	private static final int SKILL_MINING = 14;

	private static final int[] pickaxes = {
		1262, 1261, 1260, 1259, 1258, 156
	};

	private static final int[] bank_ids = {
		149, 383, 152, 155, 202, 150, 151, 153, 154, 409,
		160, 159, 158, 157, 542, 889, 890, 891, 
		161, 162, 163, 164, 523, 892, 893, 894, 
	};

	private static final int[] gems = { 
		160, 159, 158, 157, 542, 889, 890, 891, 
	};

	private static final Map<String, int[]> map_rocks = new HashMap<>();

	private GenericImpl[] impls = {
		new DwarfMineScorpImpl(),
		new MiningGuildImpl(),
		new CraftingGuildImpl()
	};

	private final int[] banked_count = new int[bank_ids.length];
	private final boolean[] has_banked = new boolean[bank_ids.length];
	private int[][] rocks;
	private long sleep_time;
	private long start_time;
	private long bank_time;
	private long move_time;
	private long click_time;
	private boolean init_path;
	private boolean died;

	private PathWalker.Location bank;
	private PathWalker.Path lumb_to_bank;
	private PathWalker.Path from_bank;
	private PathWalker.Path to_bank;
	private PathWalker pw;

	private Frame frame;
	private Checkbox cb_bank;
	private List list_rocks;
	private Choice choice_rocks;
	// for gem mining: drop the unusable things
	private Checkbox cb_drop_bad;
	private Choice choice_cmb;

	private long level_time;
	private long cur_fails;
	private long total_fails;
	private long cur_success;
	private long total_success;

	private int levels_gained;

	private GenericImpl impl;

	private final int[] last_used = new int[2];
	private final int[] last_mined = new int[2];
	private long dont_remine;

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	private class GenericImpl {
		// return -1 (to continue) or wait value

		public int pathInit() {
			pw.init(null);
			int cur_x = getX();
			int cur_y = getY();
			bank = pw.getNearestBank(cur_x, cur_y);
			if (bank == null) {
				System.out.println("ERROR: No usable bank found!");
				stopScript(); return 0;
			}
			System.out.println("Nearest bank: " + bank.name);
			to_bank = pw.calcPath(cur_x, cur_y, bank.x, bank.y);
			if (to_bank == null) {
				System.out.println("Failed to calculate to_bank. Unsupproted location?");
				stopScript(); return 0;
			}
			from_bank = pw.calcPath(bank.x, bank.y, cur_x, cur_y);
			if (from_bank == null) {
				System.out.println("Failed to calculate from_bank. Unsupproted location?");
				stopScript(); return 0;
			}

			return -1;
		}

		// for handling detecting when it's time to bank and walking
		// to and from the bank
		public int handleToBank() {
			if (pw.walkPath()) return 0;
			if (isAtApproxCoords(bank.x, bank.y, 20)) {
				boolean shouldb = should_bank();
				if (died) {
					died = false;
					if (!shouldb) {
						pw.setPath(from_bank);
						return random(1000, 2000);
					}
				}
				if (shouldb) {
					int ret = talk_to_banker();
					if (ret != -1) {
						return ret;
					}
					return random(600, 1000);
				}
			} else if (should_bank()) {
				pw.setPath(to_bank);
				return random(600, 800);
			}
			return -1;
		}

		public void setRocksToBank() {
			pw.setPath(to_bank);
		}

		public void bankClosed() {
			pw.setPath(from_bank);
		}

		// check whether the player is inside the appropriate
		// coordinates to initialize this implementation
		public boolean applies() {
			return true;
		}

		public boolean isRockValid(int[] r) {
			return true;
		}
	}

	private final class CraftingGuildImpl extends GenericImpl {

		private final int BROWN_APRON = 191;
		private final int GUILD_DOOR = 68;

		// Copied from my old separate crafting guild script

		@Override
		public int pathInit() {
			pw.init(null);
			to_bank = pw.calcPath(347, 600, 286, 572);
			if (to_bank == null) {
				System.out.println("Failed to calculate to_bank. This is not good.");
				stopScript(); return 0;
			}
			from_bank = pw.calcPath(286, 572, 347, 600);
			if (from_bank == null) {
				System.out.println("Failed to calculate from_bank. This is not good.");
				stopScript(); return 0;
			}
			return -1;
		}

		@Override
		public int handleToBank() {
			int index = getInventoryIndex(BROWN_APRON);
			if (index == -1) {
				return _end("ERROR: No brown apron!");
			}
			if (pw.walkPath()) return 0;
			// at the guild door, sadly the pather can't currently
			// handle the crafting guild building
			if (getX() == 347) {
				if (getY() == 600) {
					if (!should_bank()) {
						return openGuildDoor();
					} else {
						pw.setPath(to_bank);
						return 0;
					}
				} else if (getY() == 601) {
					if (should_bank()) {
						return openGuildDoor();
					} else {
						int x, y;
						int loop = 0;
						do {
							x = 341 + random(-1, 1);
							y = 611 + random(-1, 1);
						} while (!isReachable(x, y) && (loop++) < 1000);
						int dist = distanceTo(x, y);
						walkTo(x, y);
						return dist * random(800, 900);
					}
				}
			}
			if (should_bank()) {
				int ret = talk_to_banker();
				if (ret != -1) {
					return ret;
				}
				// walk to the guild door
				if (distanceTo(347, 601) > 20) {
					// probably in the bank?
					return random(600, 1000);
				}
				walkTo(347, 601);
				return random(1000, 1500);
			}
			return -1;
		}

		@Override
		public void setRocksToBank() {
		}

		@Override
		public void bankClosed() {
			pw.setPath(from_bank);
		}

		@Override
		public boolean applies() {
			return getX() > 335 && getX() < 343 && getY() > 598 && getY() < 616;
		}

		private int openGuildDoor() {
			int[] door = getWallObjectById(GUILD_DOOR);
			if (door[0] != -1) {
				atWallObject(door[1], door[2]);
				return random(1500, 2000);
			}
			return random(200, 400);
		}
	}

	private final class DwarfMineScorpImpl extends GenericImpl {
		private PathWalker.Path rocks_to_stairs;
		private PathWalker.Path stairs_to_rocks;
		private int rocks_x;
		private int rocks_y;
		private final int BANK_X = 286;
		private final int BANK_Y = 571;
		private final int STAIRS_ABOVE = 44;
		private final int STAIRS_ABOVE_X = 251;
		private final int STAIRS_ABOVE_Y = 540;
		private final int STAIRS_BELOW = 43;
		private final int STAIRS_BELOW_X = 254;
		private final int STAIRS_BELOW_Y = 3369;

		@Override
		public int pathInit() {
			pw.init(null);
			rocks_x = getX();
			rocks_y = getY();

			to_bank = pw.calcPath(
				STAIRS_ABOVE_X, STAIRS_ABOVE_Y,
				BANK_X, BANK_Y
			);
			if (to_bank == null)
				return _end("DwarfMineScorpImpl.to_bank pathing error");
			from_bank = pw.calcPath(
				BANK_X, BANK_Y,
				STAIRS_ABOVE_X, STAIRS_ABOVE_Y
			);
			if (from_bank == null)
				return _end("DwarfMineScorpImpl.from_bank pathing error");

			rocks_to_stairs = pw.calcPath(
				rocks_x, rocks_y,
				STAIRS_BELOW_X, STAIRS_BELOW_Y
			);
			if (rocks_to_stairs == null)
				return _end("DwarfMineScorpImpl.rocks_to_stairs pathing error");
			stairs_to_rocks = pw.calcPath(
				STAIRS_BELOW_X, STAIRS_BELOW_Y,
				rocks_x, rocks_y
			);
			if (stairs_to_rocks == null)
				return _end("DwarfMineScorpImpl.stairs_to_rocks pathing error");
			return -1;
		}

		@Override
		public int handleToBank() {
			if (pw.walkPath()) return 0;
			if (isAtApproxCoords(STAIRS_ABOVE_X, STAIRS_ABOVE_Y, 5)) {
				if (should_bank()) {
					pw.setPath(to_bank);
					return random(600, 900);
				} else {
					int[] stairs = getObjectById(STAIRS_ABOVE);
					if (stairs[0] != -1 && distanceTo(stairs[1], stairs[2]) < 8) {
						atObject(stairs[1], stairs[2]);
					}
					return random(1200, 2500);
				}
			} else if (isAtApproxCoords(STAIRS_BELOW_X, STAIRS_BELOW_Y, 5)) {
				if (!should_bank()) {
					pw.setPath(stairs_to_rocks);
					return random(600, 900);
				} else {
					int[] stairs = getObjectById(STAIRS_BELOW);
					if (stairs[0] != -1 && distanceTo(stairs[1], stairs[2]) < 8) {
						atObject(stairs[1], stairs[2]);
					}
					return random(1200, 2500);
				}
			} else if (isAtApproxCoords(BANK_X, BANK_Y, 20)) {
				if (died) {
					died = false;
				}
				if (should_bank()) {
					int ret = talk_to_banker();
					if (ret != -1) {
						return ret;
					}
					return random(600, 1000);
				} else {
					pw.setPath(from_bank);
					return random(600, 900);
				}
			} else {
				if (should_bank()) {
					pw.setPath(rocks_to_stairs);
					return random(600, 900);
				}
			}
			return -1;
		}

		@Override
		public boolean applies() {
			return getY() > 3324 && getY() < 3381 && getX() > 252 && getX() < 283;
		}

		@Override
		public void setRocksToBank() {
			pw.setPath(rocks_to_stairs);
		}

		@Override
		public void bankClosed() {
			pw.setPath(from_bank);
		}

		@Override
		public boolean isRockValid(int[] r) {
			if (r[2] > 3380) {
				return false;
			}
			if (rocks_y > 3360 && r[2] < 3360) {
				return false;
			}
			return super.isRockValid(r);
		}
	}

	private final class MiningGuildImpl extends GenericImpl {

		// Copied from my old separate guild mining script

		private static final int MINE_DOOR_X = 274;
		private static final int MINE_DOOR_Y = 563;

		private static final int ID_BANK_DOORS = 64;
		private static final int ID_MINE_DOOR = 2;

		@Override
		public int pathInit() {
			return -1;
		}

		@Override
		public int handleToBank() {
			if (isUnderground()) {
				if (should_bank()) {
					if (distanceTo(274, 3398) < 4) {
						atObject(274, 3398);
						return random(1500, 2500);
					}
					walkNearLadder();
					return random(1000, 2000);
				} else {
					if (getY() < 3387) {
						walkNearLadder();
						return random(1000, 2000);
					}
					return -1;
				}
			} else if (isInMineEntry()) {
				if (should_bank()) {
					if (getWallObjectIdFromCoords(MINE_DOOR_X, MINE_DOOR_Y) ==
							ID_MINE_DOOR) {
						atWallObject(MINE_DOOR_X, MINE_DOOR_Y);
						return random(1000, 2000);
					}
					walkNearBank(false);
					return random(3000, 5000);
				} else {
					atObject(274, 566);
					return random(1500, 2500);
				}
			} else if (isInBankArea()) {
				if (should_bank()) {
					int ret = talk_to_banker();
					if (ret != -1) {
						return ret;
					}
					return random(600, 1000);
				} else {
					int[] doors = getObjectById(ID_BANK_DOORS);
					if (doors[0] != -1) {
						atObject(doors[1], doors[2]);
						return random(600, 800);
					}
					walkNearMineEntry(false);
					return random(3000, 5000);
				}
			} else {
				if (should_bank()) {
					if (distanceTo(287, 571) < 6) {
						int[] doors = getObjectById(ID_BANK_DOORS);
						if (doors[0] != -1) {
							atObject(doors[1], doors[2]);
							return random(600, 800);
						}
						walkNearBank(true);
						return random(1500, 3000);
					}
					walkNearBank(false);
					return random(3000, 5000);
				} else {
					if (distanceTo(MINE_DOOR_X, MINE_DOOR_Y) < 6) {
						if (getWallObjectIdFromCoords(MINE_DOOR_X, MINE_DOOR_Y) ==
								ID_MINE_DOOR) {
							atWallObject(MINE_DOOR_X, MINE_DOOR_Y);
							return random(1000, 2000);
						}
						walkNearMineEntry(true);
						return random(1500, 3000);
					}
					walkNearMineEntry(false);
					return random(3000, 5000);
				}
			}
		}

		@Override
		public void setRocksToBank() {
		}

		@Override
		public void bankClosed() {
		}

		@Override
		public boolean applies() {
			return getX() > 262 && getX() < 278 && getY() > 3381 && getY() < 3401;
		}

		private void walkNearLadder() {
			int x, y;
			int loop = 0;
			do {
				if (loop++ > 1000) {
					System.out.println("DEBUG: walkNearLadder");
					return;
				}
				x = 273 + random(0, 2);
				y = 3397 + random(0, 2);
			} while (!isReachable(x, y));
			walkTo(x, y);
		}

		private void walkNearBank(boolean exact) {
			int x, y;
			int loop = 0;
			do {
				if (loop++ > 1000) {
					System.out.println("DEBUG: walkNearBank(" + exact + ')');
					return;
				}
				x = 287 - random(0, 4);
				y = 571 - random(0, 4);
			} while (!isReachable(x, y) ||
			    (exact && !isInBankArea(x, y)));
			walkTo(x, y);
		}

		private void walkNearMineEntry(boolean exact) {
			int x, y;
			int loop = 0;
			do {
				if (loop++ > 1000) {
					System.out.println("DEBUG: walkNearMineEntry(" + exact + ')');
					return;
				}
				x = 273 + random(-1, 2);
				y = 562 + random(-1, 2);
			} while (!isReachable(x, y) ||
			    (exact && !isInMineEntry(x, y)));
			walkTo(x, y);
		}

		private boolean isUnderground() {
			return getY() > 3000;
		}

		private boolean inArea(int c_x, int c_y,
		    int x1, int y1, int x2, int y2) {

			if (c_x <= x1 && c_x >= x2 && c_y >= y1 && c_y <= y2) {
				return true;
			}
			if (c_x <= x2 && c_x >= x1 && c_y >= y2 && c_y <= y1) {
				return true;
			}
			if (c_x <= x1 && c_x >= x2 && c_y >= y2 && c_y <= y1) {
				return true;
			}
			if (c_x <= x2 && c_x >= x1 && c_y >= y1 && c_y <= y2) {
				return true;
			}
			return false;
		}

		private boolean isInBankArea(int x, int y) {
			return inArea(x, y, 280, 564, 286, 573);
		}

		private boolean isInBankArea() {
			return isInBankArea(getX(), getY());
		}

		private boolean isInMineEntry(int x, int y) {
			return inArea(x, y, 272, 567, 277, 563);
		}

		private boolean isInMineEntry() {
			return isInMineEntry(getX(), getY());
		}

		@Override
		public boolean isRockValid(int[] r) {
			if (r[2] < 3381) {
				return false;
			}
			return super.isRockValid(r);
		}
	}

	static {
		map_rocks.put("Copper", new int[] { 100, 101 });
		map_rocks.put("Tin", new int[] { 104, 105 });
		map_rocks.put("Clay", new int[] { 195, 196 });
		map_rocks.put("Iron", new int[] { 102, 103 });
		map_rocks.put("Silver", new int[] { 195, 196 });
		map_rocks.put("Coal", new int[] { 110, 111 });
		map_rocks.put("Gold", new int[] { 112, 113 });
		map_rocks.put("Gem rocks", new int[] { 588 });
		map_rocks.put("Mithril", new int[] { 106, 107 });
		map_rocks.put("Adamantite", new int[] { 108, 109 });
		map_rocks.put("Runite", new int[] { 210 });
	}

	@Override
	public void init(String params) {
		died = false;
		sleep_time = -1L;
		bank_time = -1L;
		move_time = -1L;
		start_time = -1L;
		click_time = -1L;
		if (frame == null) {
			Panel rock_panel = new Panel();

			Set<String> set = map_rocks.keySet();
			String[] list = set.toArray(new String[set.size()]);
			Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
			choice_rocks = new Choice();
			for (String str : list) {
				choice_rocks.add(str);
			}
			list = null;

			Button button;

			rock_panel.add(choice_rocks);
			button = new Button("Add");
			button.addActionListener(this);
			rock_panel.add(button);
			button = new Button("Reset");
			button.addActionListener(this);
			rock_panel.add(button);

			Panel button_panel = new Panel();

			button = new Button("OK");
			button.addActionListener(this);
			button_panel.add(button);
			button = new Button("Cancel");
			button.addActionListener(this);
			button_panel.add(button);

			cb_bank = new Checkbox("Enable banking");
			cb_bank.setState(true);

			cb_drop_bad = new Checkbox("Drop unusable gems (Shilo)");
			cb_drop_bad.setState(false);

			list_rocks = new List(7);

			choice_cmb = new Choice();
			for (String str : FIGHTMODES) {
				choice_cmb.addItem(str);
			}

			frame = new Frame(getClass().getSimpleName());
			frame.addWindowListener(
			    new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
			);
			frame.setIconImages(Constants.ICONS);
			frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
			frame.add(new Label(
			    "Preference order (best-worst):", Label.CENTER));
			frame.add(list_rocks);
			frame.add(rock_panel);

			Panel cb_panel = new Panel(new GridLayout(0, 1));
			cb_panel.add(cb_bank);
			cb_panel.add(cb_drop_bad);

			frame.add(cb_panel);

			Panel cmb_choice_panel = new Panel(new FlowLayout(FlowLayout.LEFT));
			cmb_choice_panel.add(new Label("Combat style:"));
			cmb_choice_panel.add(choice_cmb);

			Panel v_panel = new Panel(new GridLayout(0, 1));
			v_panel.add(cmb_choice_panel);

			frame.add(v_panel);

			frame.add(new Label(
			    "Banking is supported from most locations excluding some"
			));
			frame.add(new Label(
			    "such as the Grand Tree. Start at the mining spot."
			));
			frame.add(new Label(
			    "Gems will be cut if there is a chisel in your inventory."
			));
			frame.add(button_panel);
			frame.setResizable(false);
			frame.pack();
		}
		frame.setLocationRelativeTo(null);
		frame.toFront();
		frame.requestFocus();
		frame.setVisible(true);
	}

	@Override
	public int main() {
		int ret = _premain();
		if (ret != -1) {
			return ret;
		}
		if (cb_bank.getState()) {
			if (!init_path) {
				impl = null;
				for (GenericImpl impl : impls) {
					if (impl.applies()) {
						this.impl = impl;
						break;
					}
				}
				if (impl == null) {
					impl = new GenericImpl();
				}
				System.out.println("Using implementation: " + impl.getClass().getSimpleName());
				ret = impl.pathInit();
				if (ret != -1) {
					return ret;
				}
				init_path = true;
			}
			if (isQuestMenu()) {
				answer(0);
				bank_time = System.currentTimeMillis();
				return random(2000, 3000);
			}
			if (isBanking()) {
				bank_time = -1L;
				int len = bank_ids.length;
				for (int i = 0; i < len; ++i) {
					int count = getInventoryCount(bank_ids[i]);
					if (count > 0) {
						deposit(bank_ids[i], count);
						if (!has_banked[i]) {
							banked_count[i] += count;
							has_banked[i] = true;
						}
						return random(1000, 1500);
					}
				}
				if (getInventoryIndex(pickaxes) == -1) {
					len = pickaxes.length;
					for (int i = 0; i < len; ++i) {
						if (bankCount(pickaxes[i]) <= 0) {
							continue;
						}
						System.out.println("Withdrawing pickaxe");
						withdraw(pickaxes[i], 1);
						return random(1700, 3200);
					}
					return _end("Error: no pickaxe!");
				}
				if (!hasInventoryItem(SLEEPING_BAG)) {
					if (bankCount(SLEEPING_BAG) <= 0) {
						return _end("Error: no sleeping bag!");
					}
					System.out.println("Withdrawing sleeping bag");
					withdraw(SLEEPING_BAG, 1);
					return random(1700, 3200);
				}
				Arrays.fill(has_banked, false);
				closeBank();
				impl.bankClosed();
				return random(1000, 2000);
			} else if (bank_time != -1L) {
				if (System.currentTimeMillis() >= (bank_time + 8000L)) {
					bank_time = -1L;
				}
				return random(300, 400);
			}
			if (distanceTo(120, 648) < 12) { // abyte0
				System.out.println("Looks like we died :(");
				if (lumb_to_bank == null) {
					lumb_to_bank = pw.calcPath(getX(), getY(), bank.x, bank.y);
				}
				if (lumb_to_bank != null) {
					pw.setPath(lumb_to_bank);
				} else {
					return _end("lumb_to_bank==null");
				}
				died = true;
				return random(2000, 3000);
			}
			ret = impl.handleToBank();
			if (ret != -1) {
				return ret;
			}
		} else { // powermining
			if (getInventoryIndex(pickaxes) == -1) {
				return _end("Error: no pickaxe!");
			}
		}
		return mine_rocks();
	}

	private int talk_to_banker() {
		int[] banker = getNpcByIdNotTalk(BANKERS);
		if (banker[0] != -1) {
			if (distanceTo(banker[1], banker[2]) > 5) {
				walk_approx(banker[1], banker[2]);
				return random(1500, 2500);
			}
			talkToNpc(banker[0]);
			return random(3000, 3500);
		}
		return -1;
	}

	private int _premain() {
		int style = choice_cmb.getSelectedIndex();
		if (getFightMode() != style) {
			setFightMode(style);
			return random(600, 800);
		}

		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			level_time = start_time;
		}

		if (inCombat()) {
			pw.resetWait();
			walkTo(getX(), getY());
			return random(400, 600);
		}

		if (click_time != -1L) {
			if (System.currentTimeMillis() >= click_time) {
				click_time = -1L;
			}
			return 0;
		}

		if (sleep_time != -1L) {
			if (System.currentTimeMillis() >= sleep_time) {
				int bag = getInventoryIndex(SLEEPING_BAG);
				if (bag != -1) {
					useItem(bag);
				} else {
					if (!cb_bank.getState()) {
						return _end("Error: no sleeping bag!");
					} else {
						impl.setRocksToBank();
					}
				}
				sleep_time = -1L;
				return random(1500, 2500);
			}
			return 0;
		}

		if (move_time != -1L) {
			if (System.currentTimeMillis() >= move_time) {
				System.out.println("Moving for 5 min timer");
				walk_approx(getX(), getY());
				move_time = -1L;
				return random(1500, 2500);
			}
			return 0;
		}

		int chisel = getInventoryIndex(CHISEL);
		if (chisel != -1) {
			int gem = getInventoryIndex(gems);
			if (gem != -1) {
				useItemWithItem(chisel, gem);
				return random(700, 900);
			}
		}

		if (cb_drop_bad.getState()) {
			int bad = getInventoryIndex(bad_gems);
			if (bad != -1) {
				dropItem(bad);
				return random(1200, 2000);
			}
		}

		int crush = getInventoryIndex(CRUSHED_GEM);
		if (crush != -1) {
			dropItem(crush);
			return random(1200, 2000);
		}

		int ball = getInventoryIndex(GNOME_BALL);
		if (ball != -1) {
			System.out.println("Gnome ball!");
			dropItem(ball);
			return random(1200, 2000);
		}

		return -1;
	}

	private boolean is_player_beside(int x, int y) {
		int count = countPlayers();
		for (int i = 1; i < count; ++i) {
			int dist = Math.abs(getPlayerX(i) - x) +
			    Math.abs(getPlayerY(i) - y);
			if (dist <= 1) {
				return true;
			}
		}
		return false;
	}

	private int[] get_closest_rock(int... ids) {
		int[] rock = new int[] { -1, -1, -1 };
		int best_dist = Integer.MAX_VALUE;
		int count = getObjectCount();
		int my_x = getX();
		int my_y = getY();
		for (int i = 0; i < count; ++i) {
			int rock_id = getObjectId(i);
			if (!inArray(ids, rock_id)) {
				continue;
			}
			int rock_x = getObjectX(i);
			int rock_y = getObjectY(i);
			if (last_mined[0] == rock_x &&
			    last_mined[1] == rock_y &&
			    System.currentTimeMillis() < dont_remine) {
				continue;
			}
			if (is_player_beside(rock_x, rock_y)) {
				continue;
			}
			int dist = Math.abs(rock_x - my_x) +
			    Math.abs(rock_y - my_y);
			if (dist < 30 && dist < best_dist) {
				best_dist = dist;
				rock[0] = rock_id;
				rock[1] = rock_x;
				rock[2] = rock_y;
			}
		}
		/* fall back to rocks with players near them */
		if (rock[0] == -1) {
			for (int i = 0; i < count; ++i) {
				int rock_id = getObjectId(i);
				if (!inArray(ids, rock_id)) {
					continue;
				}
				int rock_x = getObjectX(i);
				int rock_y = getObjectY(i);
				if (last_mined[0] == rock_x &&
				    last_mined[1] == rock_y &&
				    System.currentTimeMillis() < dont_remine) {
					continue;
				}
				int dist = Math.abs(rock_x - my_x) +
				    Math.abs(rock_y - my_y);
				if (dist < 30 && dist < best_dist) {
					best_dist = dist;
					rock[0] = rock_id;
					rock[1] = rock_x;
					rock[2] = rock_y;
				}
			}
		}
		return rock;
	}

	private int mine_rocks() {
		int array_sz = rocks.length;
		for (int i = 0; i < array_sz; ++i) {
			int[] rock = get_closest_rock(rocks[i]);
			if (rock[0] == -1 ||
			    (impl != null && !impl.isRockValid(rock))) {
				continue;
			}
			int dist = distanceTo(rock[1], rock[2]);
			if (dist > 5) {
				walk_approx(rock[1], rock[2]);
				return random(1500, 2500);
			}
			atObject(rock[1], rock[2]);
			last_used[0] = rock[1];
			last_used[1] = rock[2];
			return random(750, 950);
		}
		return random(100, 700);
	}

	public boolean should_bank() {
		if (getInventoryCount() == MAX_INV_SIZE) {
			return true;
		}
		if (getInventoryIndex(pickaxes) == -1) {
			return true;
		}
		if (getInventoryIndex(SLEEPING_BAG) == -1) {
			return true;
		}
		return false;
	}

	private void walk_approx(int x, int y) {
		int dx, dy;
		int loop = 0;
		do {
			dx = x + random(-1, 1);
			dy = y + random(-1, 1);
			if ((++loop) > 100) return;
		} while (!isReachable(dx, dy) ||
		    (dx == getX() && dy == getY()));
		walkTo(dx, dy);
	}

	@Override
	public void paint() {
		if (start_time == -1L) {
			return;
		}
		final int orangey = 0xFFD900;
		final int white = 0xFFFFFF;
		int x = (getGameWidth() / 2) - 125;
		int y = 50;
		drawString("S Miner", x, y, 1, orangey);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    x + 10, y, 1, white);
		y += 15;
		drawString(String.format("Stats for current level (%d gained):",
		    levels_gained), x, y, 1, orangey);
		y += 15;
		drawString(String.format("Successful attempts: %s (%s/h)",
		    iformat.format(cur_success),
		    per_hour(cur_success, level_time)),
		    x + 10, y, 1, white);
		y += 15;
		drawString(String.format("Failed attempts: %s (%s/h)",
		    iformat.format(cur_fails),
		    per_hour(cur_fails, level_time)),
		    x + 10, y, 1, white);
		y += 15;
		drawString("Fail rate: " + (float)
		    ((double) cur_fails / (double) cur_success),
		    x + 10, y, 1, white);
		y += 15;
		if (levels_gained > 0) {
			drawString("Total:", x, y, 1, orangey);
			y += 15;
			drawString(String.format("Successful attempts: %s",
			    iformat.format(total_success)),
			    x + 10, y, 1, white);
			y += 15;
			drawString(String.format("Failed attempts: %s",
			    iformat.format(total_fails)),
			    x + 10, y, 1, white);
			y += 15;
		}
		if (!cb_bank.getState()) return;
		boolean header = false;
		int len = bank_ids.length;
		for (int i = 0; i < len; ++i) {
			if (banked_count[i] <= 0) {
				continue;
			}
			if (!header) {
				drawString("Banked items:", x, y, 1, orangey);
				y += 15;
				header = true;
			}
			drawString(String.format("%s %s",
			    iformat.format(banked_count[i]),
			    getItemNameId(bank_ids[i])),
			    x + 10, y, 1, white);
			y += 15;
		}
	}

	private String per_hour(long count, long time) {
		double amount, secs;

		if (count == 0) return "0";
		amount = count * 60.0 * 60.0;
		secs = (System.currentTimeMillis() - time) / 1000.0;
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

	private Window get_window(Component c) {
		if (c == null || c instanceof Window) {
			return c == null ? null : (Window) c;
		}
		Component parent = c;
		do {
			parent = parent.getParent();
		} while (!(parent == null || parent instanceof Window));
		return parent == null ? null : (Window) parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("OK")) {
			if (frame.equals(get_window((Component)e.getSource()))) {
				try {
					int rock_count = list_rocks.getItemCount();
					if (rock_count <= 0) {
						throw new Exception("no rocks selected");
					}
					rocks = new int[rock_count][];
					for (int i = 0; i < rock_count; ++i) {
						rocks[i] = map_rocks.get(list_rocks.getItem(i));
					}
					if (cb_bank.getState()) {
						init_path = false;
					}
				} catch (Throwable t) {
					System.out.println(t.getClass().getSimpleName() + ": " + t.getMessage());
				}
				frame.setVisible(false);
			}
		} else if (command.equals("Cancel")) {
			if (frame.equals(get_window((Component)e.getSource()))) {
				frame.setVisible(false);
			}
		} else if (command.equals("Add")) {
			String selected = choice_rocks.getSelectedItem();
			int count = list_rocks.getItemCount();
			for (int i = 0; i < count; ++i) {
				if (!list_rocks.getItem(i).equals(selected)) {
					continue;
				}
				System.out.println("ERROR: " + selected +
				    " has already been added.");
				return;
			}
			list_rocks.add(selected);
		} else if (command.equals("Reset")) {
			list_rocks.removeAll();
		}
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("standing here")) {
			move_time = System.currentTimeMillis() +
			    random(1500, 1800);
		} else if (str.contains("swing")) {
			click_time = System.currentTimeMillis() +
			    random(5000, 7000);
		} else if (str.contains("scratch") ||
		    str.contains("fail") || str.contains("no ore")) {
			click_time = System.currentTimeMillis() +
			    random(100, 200);
			++cur_fails;
			++total_fails;
		} else if (str.contains("advanced")) {
			System.out.println("You just advanced a level.");
			level_time = System.currentTimeMillis();
			print_out();
			cur_fails = 0;
			cur_success = 0;
			++levels_gained;
		} else if (str.contains("manage") || str.contains("gem") ||
		    str.contains("just mined") || str.contains("found")) {
			click_time = System.currentTimeMillis() +
			    random(100, 200);
			++cur_success;
			++total_success;
			last_mined[0] = last_used[0];
			last_mined[1] = last_used[1];
			dont_remine = System.currentTimeMillis() + 5000L;
		} else if (str.contains("tired")) {
			sleep_time = System.currentTimeMillis() +
			    random(800, 2500);
		}
	}

	private void print_out() {
		System.out.print("Runtime: ");
		System.out.println(get_time_since(start_time));
		System.out.print("Old success count: ");
		System.out.println(cur_success);
		System.out.print("Old fail count: ");
		System.out.println(cur_fails);
		System.out.print("Old fail rate: ");
		System.out.println((double) cur_fails / (double) cur_success);
		System.out.print("Fail total: ");
		System.out.println(total_fails);
		System.out.print("Success total: ");
		System.out.println(total_success);
		if (cb_bank.getState()) {
			for (int i = 0; i < bank_ids.length; ++i) {
				if (banked_count[i] <= 0) continue;
				System.out.println("Banked " + getItemNameId(bank_ids[i]) + ": " + banked_count[i]);
			}
		}
	}

	private int _end(String reason) {
		print_out();
		System.out.println(reason);
		stopScript(); setAutoLogin(false);
		return 0;
	}
}
