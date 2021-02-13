import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import static java.lang.System.currentTimeMillis;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_Fighter extends Script
    implements ActionListener {

	private final DecimalFormat iformat = new DecimalFormat("#,##0");
	private int[] start_xp = new int[SKILL.length];
	private long start_time;
	private long menu_time;
	private long bank_time;
	private long move_time;

	private int[] banked_count;
	private boolean[] has_banked;
	private boolean access_bank;

	private static final int SKILL_HITS = 3;

	private static final int MELEE = 0;
	private static final int RANGED = 1;

	private Frame frame;
	private TextField tf_npcs;
	private TextField tf_eat;
	private TextField tf_range;
	private TextField tf_pickup;
	private TextField tf_sleep;
	private TextField tf_food;
	private TextField tf_food_amount;
	private Choice ch_fm;
	private Choice ch_spell;
	private Checkbox cb_bones;
	private Checkbox cb_bank;

	private int start_x;
	private int start_y;

	private int[] npc_ids;
	private int[] item_ids;
	private int[] food_ids;
	private int eat_at;
	private int sleep_at;
	private int range;
	private int food_amount;

	private int last_combat_x;
	private int last_combat_y;

	private final PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;
	private boolean pw_init;

	private int paint_max_y;

	private static final class Spawn {
		final int x, y;
		long time;

		public Spawn(int x, int y) {
			this.x = x;
			this.y = y;
			time = currentTimeMillis();
		}
	}

	private List<Integer> last_lot = new ArrayList<>();
	private List<Spawn> spawns = new ArrayList<>();

	private long next_tick;

	public S_Fighter(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
	}

	public static void main(String[] argv) {
		new S_Fighter(null).init(null);
	}

	@Override
	public void init(String params) {
		pw_init = false;
		start_time = menu_time = bank_time = -1L;
		spawns.clear();
		last_lot.clear();
		if (frame == null) {
			ch_fm = new Choice();
			int len = FIGHTMODES.length;
			for (int i = 0; i < len; ++i) {
				ch_fm.add(FIGHTMODES[i]);
			}
			try {
				ch_fm.select(getFightMode());
			} catch (NullPointerException e) {
			}

			ch_spell = new Choice();
			ch_spell.add("Melee");
			ch_spell.add("Ranged");
			len = SPELL.length;
			for (int i = 0; i < len; ++i) {
				ch_spell.add(SPELL[i]);
			}

			Panel pInput = new Panel();
			pInput.setLayout(new GridLayout(0, 2, 0, 2));

			pInput.add(new Label("NPC ids (1,2,3...):"));
			pInput.add(tf_npcs = new TextField());

			pInput.add(new Label("Combat style:"));
			pInput.add(ch_fm);

			pInput.add(new Label("Item ids (1,2,3...):"));
			pInput.add(tf_pickup = new TextField());

			pInput.add(new Label("Walkback range:"));
			pInput.add(tf_range = new TextField("30"));

			pInput.add(new Label("Food ids (1,2,3...):"));
			pInput.add(tf_food = new TextField("546,373"));

			pInput.add(new Label("Food amount:"));
			pInput.add(tf_food_amount = new TextField("0"));

			pInput.add(new Label("Eat at HP level:"));
			pInput.add(tf_eat = new TextField("10"));

			pInput.add(new Label("Spell/combat type:"));
			pInput.add(ch_spell);

			pInput.add(new Label("Sleep at fatigue %:"));
			pInput.add(tf_sleep = new TextField("95"));

			Panel cbPanel = new Panel();
			cbPanel.setLayout(new GridLayout(0, 1));
			cbPanel.add(cb_bones = new Checkbox("Bury bones"));
			cbPanel.add(cb_bank = new Checkbox("Bank"));
			cbPanel.add(new Label("When no food is specified, it will bank on full inventory.", Label.CENTER));
			cbPanel.add(new Label("When food is specified, it will bank when out of food.", Label.CENTER));
			cbPanel.add(new Label("Food will be eaten to make room for loot.", Label.CENTER));
			cbPanel.add(new Label("Supported banking locations include most above-ground spots.", Label.CENTER));
			cbPanel.add(new Label("There are currently problems banking where we have to cross", Label.CENTER));
			cbPanel.add(new Label("gates with walls that extend the area of the map.", Label.CENTER));

			Panel buttonPanel = new Panel();
			Button ok = new Button("OK");
			ok.addActionListener(this);
			buttonPanel.add(ok);
			Button cancel = new Button("Cancel");
			cancel.addActionListener(this);
			buttonPanel.add(cancel);

			frame = new Frame(getClass().getSimpleName());
			frame.setIconImages(Constants.ICONS);
			frame.addWindowListener(
			    new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
			);
			frame.add(pInput, BorderLayout.NORTH);
			frame.add(cbPanel, BorderLayout.CENTER);
			frame.add(buttonPanel, BorderLayout.SOUTH);
			frame.setResizable(false);
			frame.pack();
		}
		frame.setLocationRelativeTo(null);
		frame.toFront();
		frame.requestFocus();
		frame.setVisible(true);
	}

	private void scan_spawn_points() {
		int count = countNpcs();
		for (int i = 0; i < count; ++i) {
			if (!inArray(npc_ids, getNpcId(i))) {
				continue;
			}
			if (last_lot.contains(getNpcServerIndex(i))) {
				continue;
			}
			int x = getNpcX(i);
			int y = getNpcY(i);
			if (distanceTo(start_x, start_y, x, y) > range) {
				continue;
			}
			if (distanceTo(x, y) > 12) {
				continue;
			}
			boolean found = false;
			for (Spawn s : spawns) {
				if (s.x == x && s.y == y) {
					s.time = currentTimeMillis();
					found = true;
					break;
				}
			}
			long time = currentTimeMillis();
			long max_time = 20 * 60 * 1000;
			if (!found && (time - start_time) < max_time) {
				spawns.add(new Spawn(x, y));
			}
		}
		Iterator<Spawn> it = spawns.iterator();
		while (it.hasNext()) {
			Spawn s = it.next();
			long current_time = currentTimeMillis();
			if ((current_time - s.time) >= (7 * 60 * 1000)) {
				it.remove();
			}
		}
	}

	@Override
	public int main() {
		if (start_time == -1L) {
			ingame_init();
		} else {
			scan_spawn_points();
		}
		last_lot.clear();
		int count = countNpcs();
		for (int i = 0; i < count; ++i) {
			if (inArray(npc_ids, getNpcId(i))) {
				last_lot.add(getNpcServerIndex(i));
			}
		}
		if (currentTimeMillis() >= next_tick) {
			next_tick = currentTimeMillis() + perform_actions();
		}
		return 0;
	}

	private int perform_actions() {
		int ideal_fm = ch_fm.getSelectedIndex();
		if (getFightMode() != ideal_fm) {
			setFightMode(ideal_fm);
			return random(400, 600);
		}
		if (isBanking() && cb_bank.getState()) {
			return banking();
		}
		if (bank_time != -1L) {
			if (currentTimeMillis() > (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(100, 300);
		}
		if (isQuestMenu() && cb_bank.getState()) {
			answer(0);
			menu_time = -1L;
			bank_time = currentTimeMillis();
			return random(600, 800);
		}
		if (menu_time != -1L) {
			if (currentTimeMillis() > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(100, 300);
		}
		if (inCombat()) {
			return in_combat();
		}
		if (getFatigue() >= sleep_at) {
			useSleepingBag();
			return random(1000, 1500);
		}
		if (getCurrentLevel(SKILL_HITS) <= eat_at) {
			int food = getInventoryIndex(food_ids);
			if (food != -1) {
				useItem(food);
				pw.resetWait();
				return random(800, 1200);
			}
			System.out.println("Out of food!");
			if (food_amount == 0) {
				System.out.println("Not withdrawing it!");
				return random(800, 1200);
			}
		}
		if (pw_init) {
			if (pw.walkPath()) return 0;
		}
		if (access_bank) {
			int[] banker = getNpcByIdNotTalk(BANKERS);
			if (banker[0] != -1) {
				talkToNpc(banker[0]);
				menu_time = currentTimeMillis();
			}
			return random(600, 1000);
		}
		if (cb_bones.getState()) {
			int count = getInventoryCount();
			for (int i = 0; i < count; i++) {
				if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("bury")) {
					useItem(i);
					return random(800, 1000);
				}
			}
		}
		if (cb_bank.getState()) {
			if (food_amount > 0) {
				if (getInventoryIndex(food_ids) == -1) {
					access_bank = true;
					pw.setPath(to_bank);
					return 0;
				}
			} else if (getInventoryCount() == MAX_INV_SIZE) {
				access_bank = true;
				pw.setPath(to_bank);
				return 0;
			}
		}
		if (!isAtApproxCoords(start_x, start_y, range)) {
			return outside_range();
		}
		if (currentTimeMillis() > move_time) {
			move_time = Long.MAX_VALUE;
			walk_approx(getX(), getY(), 1);
			return random(600, 1000);
		}
		int wait = take_items();
		if (wait != -1) {
			return wait;
		}
		return attack();
	}

	private void ingame_init() {
		start_x = getX();
		start_y = getY();
		start_time = currentTimeMillis();
		for (int i = 0; i < start_xp.length; ++i) {
			start_xp[i] = getXpForLevel(i);
		}
		Arrays.fill(has_banked, false);
		bank_time = menu_time = -1L;
		move_time = Long.MAX_VALUE;
		if (cb_bank.getState()) {
			pw.init(null);
			pw_init = true;
			PathWalker.Location bank = pw.getNearestBank(getX(),
			    getY());
			if (bank == null) {
				System.out.println("ERROR: No usable bank found!");
				start_time = -1L;
				stopScript();
				return;
			}
			System.out.println("Nearest bank: " + bank.name);
			to_bank = pw.calcPath(getX(), getY(), bank.x, bank.y);
			from_bank = pw.calcPath(bank.x, bank.y, getX(), getY());
		}
		access_bank = false;
	}

	private int banking() {
		bank_time = -1L;
		for (int i = 0; i < item_ids.length; ++i) {
			int count = getInventoryCount(item_ids[i]);
			if (count > 0) {
				if (!has_banked[i]) {
					banked_count[i] += count;
					has_banked[i] = true;
				}
				deposit(item_ids[i], count);
				return random(600, 800);
			}
		}
		int food_in_inv = getInventoryCount(food_ids);
		int food_to_withdraw = food_amount - food_in_inv;
		if (food_to_withdraw > 0) {
			for (int i = 0; i < food_ids.length; ++i) {
				int bank_count = bankCount(food_ids[i]);
				if (bank_count <= 1) {
					continue;
				}
				int w = food_to_withdraw;
				if (w >= bank_count) {
					w = bank_count - 1;
				}
				if (w > 0) {
					withdraw(food_ids[i], w);
					return random(1000, 2000);
				}
			}
			System.out.println("Out of food");
			setAutoLogin(false);
			stopScript();
			return 0;
		} else if (food_to_withdraw < 0) {
			int food = getInventoryIndex(food_ids);
			int id = getInventoryId(food);
			int count = getInventoryCount(id);
			int deposit = food_to_withdraw;
			if (deposit > count) {
				deposit = count;
			}
			deposit(id, count);
			return random(1000, 2000);
		}
		Arrays.fill(has_banked, false);
		access_bank = false;
		pw.setPath(from_bank);
		closeBank();
		return random(600, 800);
	}

	private int outside_range() {
		if (distanceTo(start_x, start_y) < 16 &&
		    isReachable(start_x, start_y)) {
			System.out.println("Going back");
			walkTo(start_x, start_y);
			return random(1000, 2000);
		}
		if (!pw_init) {
			pw.init(null);
			pw_init = true;
		}
		PathWalker.Path p = pw.calcPath(start_x, start_y);
		if (p != null) {
			System.out.println("Going back");
			pw.setPath(p);
			return random(600, 800);
		}
		System.out.println("Error calculating path, trying to move");
		if (!isWalking()) {
			walk_approx(getX(), getY(), 10);
		}
		return random(1000, 2000);
	}

	private int in_combat() {
		pw.resetWait();
		last_combat_x = getX();
		last_combat_y = getY();
		int type = ch_spell.getSelectedIndex();
		if (type == RANGED || getCurrentLevel(SKILL_HITS) <= eat_at) {
			walkTo(getX(), getY());
			return random(400, 600);
		}
		// magic in combat
		if (type > RANGED) return attack();
		return random(250, 450);
	}

	private int take_items() {
		int[] item = get_reachable_item(item_ids);
		if (item[0] == -1) {
			return -1;
		}
		if (getInventoryCount() == MAX_INV_SIZE &&
		    (!isItemStackableId(item[0]) ||
		    getInventoryIndex(item[0]) == -1)) {
			int food = getInventoryIndex(food_ids);
			if (food != -1) {
				useItem(food);
				return random(600, 800);
			}
			return -1;
		}
		if (distanceTo(item[1], item[2]) > 5) {
			walk_approx(item[1], item[2], 1);
			return random(1000, 2000);
		}
		pickupItem(item[0], item[1], item[2]);
		return random(600, 1000);
	}

	private int attack() {
		int sp_type = ch_spell.getSelectedIndex();
		if (sp_type != MELEE) {
			int[] npc = getAllNpcById(npc_ids);
			if (npc[0] != -1) {
				if (sp_type == RANGED) {
					attackNpc(npc[0]);
					return random(1500, 2500);
				} else {
					int spell = sp_type - 2;
					if (canCastSpell(spell)) {
						mageNpc(npc[0], spell);
					} else {
						System.out.println("Can't cast spell!");
						stopScript(); setAutoLogin(false);
					}
					return random(600, 1000);
				}
			}
		} else {
			int[] npc = get_reachable_npc(npc_ids);
			if (npc[0] != -1) {
				if (distanceTo(npc[1], npc[2]) > 5) {
					walk_approx(npc[1], npc[2], 1);
					return random(1000, 2000);
				}
				attackNpc(npc[0]);
				return random(600, 1500);
			}
			/*
			 * no NPC is there... search for the spawn point
			 * which we attacked the NPC on the longest time ago
			 */
			long oldest_time = Long.MAX_VALUE;
			Spawn s = null;

			for (Spawn temp : spawns) {
				if (temp.time < oldest_time) {
					oldest_time = temp.time;
					s = temp;
				}
			}
			if (s != null && (s.x != 0 || s.y != 0)) {
				if (getX() != s.x || getY() != s.y) {
					walkTo(s.x, s.y);
					return random(600, 1500);
				}
			}
		}
                return 0;
	}

	private int[] get_reachable_item(int... ids) {
		int[] item = new int[] {
			-1, -1, -1
		};
		int count = getGroundItemCount();
		int max_dist = Integer.MAX_VALUE;
		for (int i = 0; i < count; i++) {
			int id = getGroundItemId(i);
			if (inArray(ids, id)) {
				int x = getItemX(i);
				int y = getItemY(i);
				if (!isReachable(x, y)) continue;
				if (distanceTo(x, y, start_x, start_y) > range) {
					continue;
				}
				int dist = distanceTo(x, y, getX(), getY());
				if (dist < max_dist) {
					item[0] = id;
					item[1] = x;
					item[2] = y;
					max_dist = dist;
				}
			}
		}
		return item;
	}

	private int[] get_reachable_npc(int... ids) {
		int[] npc = new int[] {
			-1, -1, -1
		};
		int max_dist = Integer.MAX_VALUE;
		int count = countNpcs();
		for (int i = 0; i < count; i++) {
			if (isNpcInCombat(i)) continue;
			if (inArray(ids, getNpcId(i))) {
				int x = getNpcX(i);
				int y = getNpcY(i);
				if (!isReachable(x, y)) continue;
				if (distanceTo(x, y, start_x, start_y) > range) {
					continue;
				}
				int dist = distanceTo(x, y, getX(), getY());
				if (dist < max_dist) {
					npc[0] = i;
					npc[1] = x;
					npc[2] = y;
					max_dist = dist;
				}
			}
		}
		return npc;
	}

	private void walk_approx(int x, int y, int range) {
		int dx, dy;
		int loop = 0;
		do {
			dx = x + random(-range, range);
			dy = y + random(-range, range);
			if ((++loop) > 1000) return;
		} while ((dx == getX() && dy == getY()) ||
		    !isReachable(dx, dy));
		walkTo(dx, dy);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("OK")) {
			try {
				String[] array = tf_npcs.getText().trim().split(",");
				int array_sz = array.length;
				npc_ids = new int[array_sz];
				for (int i = 0; i < array_sz; i++) {
					npc_ids[i] = Integer.parseInt(array[i]);
				}
				System.out.println("NPCs: " +
				    Arrays.toString(npc_ids));
			} catch (Throwable t) {
				System.out.println("Couldn't parse npc ids");
				npc_ids = new int[0];
			}
			try {
				String[] array = tf_pickup.getText().trim().split(",");
				int array_sz = array.length;
				item_ids = new int[array_sz];
				banked_count = new int[array_sz];
				has_banked = new boolean[array_sz];
				for (int i = 0; i < array_sz; i++) {
					item_ids[i] = Integer.parseInt(array[i]);
				}
				System.out.println("Items: " +
				    Arrays.toString(item_ids));
			} catch (Throwable t) {
				System.out.println("Couldn't parse item ids");
				item_ids = new int[0];
			}
			try {
				String[] array = tf_food.getText().trim().split(",");
				int array_sz = array.length;
				food_ids = new int[array_sz];
				for (int i = 0; i < array_sz; i++) {
					food_ids[i] = Integer.parseInt(array[i]);
				}
				System.out.println("Food: " +
				    Arrays.toString(food_ids));
			} catch (Throwable t) {
				System.out.println("Couldn't parse food ids");
				food_ids = new int[0];
			}
			try {
				eat_at = Integer.parseInt(tf_eat.getText().trim());
			} catch (Throwable t) {
				System.out.println("Couldn't parse eat at value");
			}
			try {
				range = Integer.parseInt(tf_range.getText().trim());
			} catch (Throwable t) {
				System.out.println("Couldn't parse range value");
			}
			try {
				sleep_at = Integer.parseInt(tf_sleep.getText().trim());
			} catch (Throwable t) {
				System.out.println("Couldn't parse sleep value");
			}
			try {
				food_amount = Integer.parseInt(tf_food_amount.getText().trim());
			} catch (Throwable t) {
				System.out.println("Couldn't parse food amount");
			}
		}
		frame.setVisible(false);
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("out of ammo")) {
			stopScript();
		} else if (str.contains("busy")) {
			menu_time = -1L;
		} else if (str.contains("standing")) {
			move_time = currentTimeMillis() + random(500, 1500);
		}
	}

	@Override
	public void paint() {
		if (start_time == -1L) {
			return;
		}
		int x = (512 / 2) - 50;
		int y = 60;
		drawBoxAlphaFill(x - 8, y - 18, 300, paint_max_y - y, 120, 0x0);
		drawString("S Fighter", x, y, 2, 0xFFD900);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    x, y, 2, 0xFFFFFF);
		y += 15;
		for (int i = 0; i < start_xp.length; ++i) {
			int gained = getXpForLevel(i) - start_xp[i];
			if (gained == 0) continue;
			drawString(String.format("%s XP gained: %s (%s/h)",
			    SKILL[i], iformat.format(gained), per_hour(gained)),
			    x, y, 2, 0xFFFFFF);
			y += 15;
		}
		for (int i = 0; i < item_ids.length; ++i) {
			if (banked_count[i] == 0) continue;
			drawString(String.format("%s banked: %s (%s/h)",
			    getItemNameId(item_ids[i]),
			    iformat.format(banked_count[i]),
			    per_hour(banked_count[i])),
			    x, y, 2, 0xFFFFFF);
			y += 15;
		}
		paint_max_y = y + 15;
	}

	private String per_hour(int count) {
		double amount, secs;

		if (count == 0) return "0";
		amount = count * 60.0 * 60.0;
		secs = (currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}

	private static String get_time_since(long t) {
		long millis = (currentTimeMillis() - t) / 1000;
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
