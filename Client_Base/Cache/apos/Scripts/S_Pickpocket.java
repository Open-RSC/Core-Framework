import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_Pickpocket extends Script
    implements ActionListener, ItemListener {

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	private static final int ID_GNOMEBALL = 981;
	private static final int SKILL_HITS = 3;
	private static final int SKILL_THIEV = 17;

	private static final int BANK_NEVER = 0;
	private static final int BANK_FOOD = 1;

	private static final int COINS = 10;
	private static final int DSQ = 1277;

	private static final Map<String, int[]> map_npcs;
	private static final Map<String, int[]> map_food;

	static {
		map_npcs = new LinkedHashMap<>();
		map_npcs.put("Men (level 1)", new int[] { 11, 72, 318, 750 });
		map_npcs.put("Farmers (level 10)", new int[] { 63, 319 });
		map_npcs.put("Warriors (level 25)", new int[] { 86, 320 });
		map_npcs.put("Rogues (level 32)", new int[] { 342 });
		map_npcs.put("Guards (level 40)", new int[] { 65, 321, 376 });
		map_npcs.put("Knights (level 55)", new int[] { 322 });
		map_npcs.put("Paladins (level 70)", new int[] { 323 });
		map_npcs.put("Gnomes (level 75)", new int[] { 593, 592, 591 });
		map_npcs.put("Heroes (level 80)", new int[] { 324 });

		map_food = new TreeMap<>();
		map_food.put("Shrimp", new int[] { 350 });
		map_food.put("Anchovy", new int[] { 352 });
		map_food.put("Sardine", new int[] { 355 });
		map_food.put("Salmon", new int[] { 357 });
		map_food.put("Trout", new int[] { 359 });
		map_food.put("Herring", new int[] { 362 });
		map_food.put("Pike", new int[] { 364 });
		map_food.put("Tuna", new int[] { 367 });
		map_food.put("Swordfish", new int[] { 370 });
		map_food.put("Lobster", new int[] { 373 });
		map_food.put("Bass", new int[] { 555 });
		map_food.put("Shark", new int[] { 546 });
		map_food.put("Manta ray", new int[] { 1191 });
		map_food.put("Sea turtle", new int[] { 1193 });
		map_food.put("Kebab", new int[] { 210 });
		map_food.put("Cake", new int[] { 330, 333, 335 });
		map_food.put("Chocolate cake", new int[] { 332, 334, 336 });
		map_food.put("Meat pizza", new int[] { 326, 328 });
		map_food.put("Anchovy pizza", new int[] { 327, 329 });
	}

	private static final int[] ids_bank = {
	    COINS, 31, 32, 33, 34, 35, 36, 37, 38, 40, 41, 41, 42, 46, 619,
	    152, 142, 612, 619, 161, DSQ
	};

	private int last_combat_x;
	private int last_combat_y;

	private int[] ids_npcs;
	private int[] ids_food;
	private int sleep_at;
	private long move_time;
	private long bank_time;
	private long menu_time;
	private boolean init_path;
	private int eat_at;
	private int withdraw_food;

	private int[] bank_counts = new int[ids_bank.length];
	private boolean[] has_banked = new boolean[ids_bank.length];

	private Frame frame;
	private Choice ch_fm;
	private Choice ch_bank;
	private Choice ch_npc;
	private Choice ch_food;
	private TextField tf_food;
	private TextField tf_eat;
	private TextField tf_sleep;

	private PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;
	private PathWalker.Location bank;

	private long start_time;
	private long total_success;
	private long cur_success;
	private long total_fails;
	private long cur_fails;
	private int levels_gained;
	private int total_withdraw;
	
	private FileWriter csv;
    private long cur_attempts;

	private int start_x;
	private int start_y;

	private long level_time;
	private boolean walked_in_bank;

	public S_Pickpocket(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
		
		try{
			String fileName	= "pickpocketting_exp.csv";
			boolean fileCreated = false;
			File createCsv = new File(fileName);
			if (!createCsv.exists())
			{
				createCsv.createNewFile();
				fileCreated = true;
			}

			csv	= new FileWriter(createCsv, true);
			if(fileCreated)
			{
				csv.write("npc_id,level,attempts,success_count,fail_count\n");
				csv.flush();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) {
		S_Pickpocket p = new S_Pickpocket(null);
		p.init(null);
	}

	@Override
	public void init(String params) {
		Arrays.fill(has_banked, false);
		Arrays.fill(bank_counts, 0);
		walked_in_bank = false;
		total_withdraw  = 0;
		levels_gained = 0;
		cur_success = 0;
		cur_fails = 0;
		move_time = -1L;
		bank_time = -1L;
		menu_time = -1L;
		start_time = -1L;
		init_path = false;
		if (frame == null) {
			ch_bank = new Choice();
			ch_bank.addItemListener(this);
			ch_bank.add("Never");
			ch_bank.add("For food");
			ch_bank.add("For food or full bag");

			ch_fm = new Choice();
			for (String str : FIGHTMODES) {
				ch_fm.add(str);
			}

			Iterator<String> sit;

			ch_npc = new Choice();
			sit = map_npcs.keySet().iterator();
			while (sit.hasNext()) {
				ch_npc.add(sit.next());
			}

			ch_food = new Choice();
			sit = map_food.keySet().iterator();
			while (sit.hasNext()) {
				ch_food.add(sit.next());
			}

			tf_food = new TextField("20");
			tf_food.setEnabled(false);

			Panel pInput = new Panel(new GridLayout(0, 2, 2, 2));
			pInput.add(new Label("NPC:"));
			pInput.add(ch_npc);
			pInput.add(new Label("Combat style:"));
			pInput.add(ch_fm);
			pInput.add(new Label("Banking mode:"));
			pInput.add(ch_bank);
			pInput.add(new Label("Withdraw food:"));
			pInput.add(ch_food);
			pInput.add(new Label("Withdraw food count:"));
			pInput.add(tf_food);
			pInput.add(new Label("Eat at HP level:"));
			pInput.add(tf_eat = new TextField("10"));
			pInput.add(new Label("Sleep at fatigue %:"));
			pInput.add(tf_sleep = new TextField("95"));

			ch_food.setEnabled(false);

			Button button;
			Panel pButtons = new Panel();
			button = new Button("OK");
			button.addActionListener(this);
			pButtons.add(button);
			button = new Button("Cancel");
			button.addActionListener(this);
			pButtons.add(button);

			frame = new Frame(getClass().getSimpleName());
			frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
			frame.addWindowListener(
			    new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
			);
			frame.setIconImages(Constants.ICONS);
			frame.add(pInput, BorderLayout.NORTH);
			frame.add(new Label(
				"Banking is supported from most ground level locations.",
				Label.CENTER
			), BorderLayout.CENTER);
			frame.add(new Label(
				"Start this script at the NPCs.",
				Label.CENTER
			), BorderLayout.SOUTH);
			frame.add(pButtons, BorderLayout.SOUTH);
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
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			level_time = start_time;
		}
		int ideal_fm = ch_fm.getSelectedIndex();
		if (getFightMode() != ideal_fm) {
			setFightMode(ideal_fm);
			return random(400, 600);
		}
		if (inCombat()) {
			last_combat_x = getX();
			last_combat_y = getY();
			walkTo(getX(), getY());
			return random(400, 600);
		}
		int bank_type = ch_bank.getSelectedIndex();
		if (getCurrentLevel(SKILL_HITS) <= eat_at) {
			int slot = getFoodSlot();
			if (slot != -1) {
				useItem(slot);
				return random(800, 1000);
			}
			if (bank_type == BANK_NEVER) {
				System.out.println("No food!");
				return random(500, 1000);
			}
		}
		if (getFatigue() >= sleep_at) {
			useSleepingBag();
			return random(2000, 3000);
		}
		if (move_time != -1L && System.currentTimeMillis() >= move_time) {
			System.out.println("Moving for 5 min timer");
			walk_approx(getX(), getY());
			move_time = -1L;
			return random(1500, 2500);
		}
		int ball = getInventoryIndex(ID_GNOMEBALL);
		if (ball != -1) {
			dropItem(ball);
			return random(1000, 1200);
		}
		if (bank_type != BANK_NEVER) {
			if (!init_path) {
				pw.init(null);
				start_x = getX();
				start_y = getY();
				bank = pw.getNearestBank(start_x, start_y);
				System.out.println("Nearest bank: " + bank.name);
				to_bank = pw.calcPath(start_x, start_y, bank.x, bank.y);
				if (to_bank == null) {
					stopScript(); return 0;
				}
				from_bank = pw.calcPath(bank.x, bank.y, start_x, start_y);
				if (from_bank == null) {
					stopScript(); return 0;
				}
				init_path = true;
			}
			if (isQuestMenu()) {
				answer(0);
				menu_time = -1L;
				bank_time = System.currentTimeMillis();
				return random(600, 800);
			} else if (menu_time != -1L) {
				if (System.currentTimeMillis() >= (menu_time + 8000L)) {
					menu_time = -1L;
				}
				return random(300, 400);
			}
			if (isBanking()) {
				bank_time = -1L;
				int array_sz = ids_bank.length;
				for (int i = 0; i < array_sz; ++i) {
					int count = getInventoryCount(ids_bank[i]);
					if (count > 0) {
						deposit(ids_bank[i], count);
						if (!has_banked[i]) {
							bank_counts[i] += count;
							has_banked[i] = true;
						}
						return random(1000, 2000);
					}
				}
				int food_count = getInventoryCount(ids_food);
				if (food_count < withdraw_food) {
					for (int id : ids_food) {
						int bank_count = bankCount(id);
						if (bank_count <= 0) continue;
						int w = withdraw_food - food_count;
						if (w > bank_count) w = bank_count;
						total_withdraw += w;
						withdraw(id, w);
						return random(1000, 2000);
					}
					System.out.println("ERROR: Out of food!");
					stopScript(); setAutoLogin(false);
					return 0;
				}
				if (food_count > withdraw_food) {
					int to_deposit = food_count - withdraw_food;
					for (int id : ids_food) {
						int c = getInventoryCount(id);
						if (c <= 0) continue;
						if (c > to_deposit) {
							deposit(id, to_deposit);
						} else {
							deposit(id, c);
						}
						return random(1000, 2000);
					}
				}
				walked_in_bank = false;
				Arrays.fill(has_banked, false);
				pw.setPath(from_bank);
				closeBank();
				return random(600, 800);
			} else if (bank_time != -1L) {
				if (System.currentTimeMillis() >= (bank_time + 8000L)) {
					bank_time = -1L;
				}
				return random(300, 400);
			}
			if (pw.walkPath()) return 0;
			if (shouldBank()) {
				if (!walked_in_bank) {
					pw.setPath(to_bank);
					walked_in_bank = true;
					return random(600, 800);
				}
				int[] banker = getNpcByIdNotTalk(BANKERS);
				if (banker[0] != -1) {
					if (distanceTo(banker[1], banker[2]) > 5) {
						walk_approx(banker[1], banker[2]);
						return random(1500, 2500);
					}
					talkToNpc(banker[0]);
					menu_time = System.currentTimeMillis();
				}
				return random(600, 800);
			}
		}
		int[] item = getItemById(ids_bank);
		if ((item[1] == getX() && item[2] == getY()) ||
		    (item[1] == last_combat_x && item[2] == last_combat_y)) {
			if (getInventoryCount() < MAX_INV_SIZE ||
			    (getInventoryIndex(item[0]) != -1 &&
			    isItemStackableId(item[0]))) {
				pickupItem(item[0], item[1], item[2]);
				return random(600, 1000);
			}
			if (item[0] == DSQ) {
				int slot = getFoodSlot();
				if (slot != -1) {
					useItem(slot);
					return random(800, 1000);
				}
			}
		}
		int[] npc = get_npc_reachable(ids_npcs);
		if (npc[0] != -1) {
			if (distanceTo(npc[1], npc[2]) > 5) {
				walk_approx(npc[1], npc[2]);
				return random(1500, 2500);
			}
			thieveNpc(npc[0]);
			return random(600, 800);
		}
		return random(100, 700);
	}

	private int[] get_npc_reachable(int... ids) {
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

	@Override
	public void paint() {
		final int orangey = 0xFFD900;
		final int white = 0xFFFFFF;
		int x = (getGameWidth() / 2) - 125;
		int y = 50;
		drawString("S Pickpocket", x, y, 1, orangey);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    x + 10, y, 1, white);
		y += 15;
		drawString(String.format("Stats for current level (%d gained)",
		    levels_gained),
		    x, y, 1, orangey);
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
		if (levels_gained > 0) {
			y += 15;
			drawString("Total:", x, y, 1, orangey);
			y += 15;
			drawString("Successful attempts: " +
			    iformat.format(total_success),
			    x + 10, y, 1, white);
			y += 15;
			drawString("Failed attempts: " +
			    iformat.format(total_fails),
			    x + 10, y, 1, white);
		}
		if (ch_bank.getSelectedIndex() == BANK_NEVER) return;
		y += 15;
		drawString("Banked items:", x, y, 1, orangey);
		y += 15;
		if (withdraw_food != 0) {
			drawString(String.format("%s food withdrawn (%s trips)",
			    iformat.format(total_withdraw),
			    iformat.format(total_withdraw / withdraw_food)),
			    x + 10, y, 1, white);
			y += 15;
		}
		int len = ids_bank.length;
		for (int i = 0; i < len; ++i) {
			int count = bank_counts[i];
			if (count <= 0) continue;
			drawString(String.format("%s %s",
			    iformat.format(count),
			    getItemNameId(ids_bank[i])),
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

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("standing here")) {
			move_time = (System.currentTimeMillis() + random(1500, 1800));
		} else if (str.contains("busy")) {
			menu_time = -1L;
		} else if (str.contains("fail")) {
			++cur_fails;
			++total_fails;
			++cur_attempts;
		} else if (str.contains("you pick")) {
			++cur_success;
			++total_success;
			++cur_attempts;
			
			if(cur_attempts >= 1000)
				_csvOut();
		} else if (str.contains("advanced")) {
			System.out.println("You just advanced a level.");
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
			_csvOut();
			level_time = System.currentTimeMillis();
			cur_fails = 0;
			cur_success = 0;
			++levels_gained;
		}
	}
	
	private void _csvOut() {
    	int cursor = ch_npc.getSelectedIndex();
    	List<Entry<String, int[]>> indexedList = new ArrayList<Map.Entry<String, int[]>>(map_npcs.entrySet());
    	// fullname = non_spaced_npc + " " + (level)
    	String fullName = indexedList.get(cursor).getKey();
    	String npcname = fullName.substring(0, fullName.indexOf(' '));
    	
    	try{
			csv.write(
				"\"" + npcname + "\"," +
				getLevel(17) + "," +
				cur_attempts + "," +
				cur_success + "," +
				cur_fails + "\n"
			);
			csv.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
        cur_attempts	= 0;
    }

	private boolean shouldBank() {
		switch (ch_bank.getSelectedIndex()) {
			case BANK_NEVER:
				return false;
			case BANK_FOOD:
				return getFoodSlot() == -1;
			default:
				return getFoodSlot() == -1 ||
				   getInventoryCount() == MAX_INV_SIZE;
		}
	}

	private void walk_approx(int x, int y) {
		int dx, dy;
		int loop = 0;
		do {
			dx = x + random(-1, 1);
			dy = y + random(-1, 1);
			if ((++loop) > 500) return;
		} while (!isReachable(dx, dy) ||
		    (dx == getX() && dy == getY()));
		walkTo(dx, dy);
	}

	private int getFoodSlot() {
		int count = getInventoryCount();
		for (int i = 0; i < count; i++) {
			if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("eat")) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("OK")) {
			eat_at = Integer.parseInt(tf_eat.getText());
			sleep_at = Integer.parseInt(tf_sleep.getText());
			withdraw_food = Integer.parseInt(tf_food.getText());
			ids_npcs = map_npcs.get(ch_npc.getSelectedItem());
			ids_food = map_food.get(ch_food.getSelectedItem());
		}
		frame.setVisible(false);
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		boolean enabled = ch_bank.getSelectedIndex() != BANK_NEVER;
		ch_food.setEnabled(enabled);
		tf_food.setEnabled(enabled);
	}
}
