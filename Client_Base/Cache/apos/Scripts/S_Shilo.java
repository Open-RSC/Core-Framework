import java.awt.Font;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Label;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;
import com.aposbot.StandardCloseHandler;
import com.aposbot.Constants;

public final class S_Shilo extends Script {

	private static final class Stage {
		int id;
		PathWalker.Path path_to;

		String cert_name;
		int cert_input_id;

		Stage(int id) {
			this.id = id;
		}

		Stage(int id, PathWalker.Path p) {
			this(id);
			path_to = p;
		}

		@Override
		public String toString() {
			switch (id) {
			case STAGE_FISH:
				return "Fishing";
			case STAGE_COOK:
				return "Cooking";
			case STAGE_DEPOSIT:
				return "Depositing";
			case STAGE_SELL:
				return "Selling";
			case STAGE_WITHDRAW:
				return "Withdrawing";
			case STAGE_BUY:
				return "Buying";
			default:
				return "Invalid";
			}
		}
	}

	private static final int STAGE_FISH	= 0;
	private static final int STAGE_COOK	= 1;
	private static final int STAGE_DEPOSIT	= 3;
	private static final int STAGE_SELL	= 4;
	private static final int STAGE_WITHDRAW	= 5;
	private static final int STAGE_BUY	= 7;

	// raw fish store
	private static final int FERNAHEI = 616;
	private static final int FERNAHEI_SHOP_X = 397;
	private static final int FERNAHEI_SHOP_Y = 840;

	// general store
	private static final int OBLI = 620;
	private static final int OBLI_SHOP_X = 417;
	private static final int OBLI_SHOP_Y = 850;

	private static final int COOK_X = 412;
	private static final int COOK_Y = 823;

	private static final int RANGE_OBJECT_X = 412;
	private static final int RANGE_OBJECT_Y = 822;

	private static final int BANK_X = 400;
	private static final int BANK_Y = 850;

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	private long start_time;
	private long bank_time;
	private long menu_time;
	private long shop_time;
	private long click_time;

	private int fish_x;
	private int fish_y;
	private int[] raw_ids;
	private int[] discard_ids;
	private int[] cooked_ids;
	private int[] burnt_ids;

	private int move_x;
	private int move_y;

	private boolean click1;
	private boolean discarding;
	private boolean should_sleep;

	private int[] bank_ids;
	private boolean[] has_banked;
	private int[] banked_count;

	private int[] do_not_sell;

	private long fish_level_up_time;
	private long cur_fish_success;
	private long cur_fish_fails;
	private long total_fish_success;
	private long total_fish_fails;
	private int fishing_levels;

	private long cook_level_up_time;
	private long cur_cook_success;
	private long cur_cook_fails;
	private long total_cook_success;
	private long total_cook_fails;
	private int cooking_levels;

	private int box_bottom;

	private PathWalker pw;
	private boolean pw_init;

	private final List<Stage> stages = new ArrayList<>();
	private int stage;

	private Frame frame;
	private Choice cs_choice;
	private Checkbox pickup;
	private ItemListener acquire_box_listener;
	private ItemListener dispose_box_listener;

	public S_Shilo(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
	}

	public static void main(String[] argv) {
		S_Shilo s = new S_Shilo(null);
		s.init(null);
		while (s.frame.isVisible()) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
			}
		}
		System.exit(0);
	}

	@Override
	public void init(String params) {
		if (frame == null) {
			create_frame();
		}
		frame.setLocationRelativeTo(null);
		frame.toFront();
		frame.setVisible(true);
		frame.requestFocus();
		start_time = -1L;
	}

	@Override
	public int main() {
		if (start_time == -1L) {
			ingame_init();
		}
		if (getFightMode() != cs_choice.getSelectedIndex()) {
			setFightMode(cs_choice.getSelectedIndex());
			return random(600, 1000);
		}
		if (pw_init && pw.walkPath()) {
			return 0;
		}
		if (should_sleep) {
			if (getFatigue() == 0) {
				should_sleep = false;
			} else {
				useSleepingBag();
			}
			return random(1000, 2000);
		}
		if (move_x != 0 && move_y != 0) {
			if (getX() == move_x && getY() == move_y) {
				move_x = 0;
				move_y = 0;
				return 0;
			}
			if (isShopOpen()) {
				closeShop();
				return random(600, 800);
			}
			walkTo(move_x, move_y);
			return random(1000, 2000);
		}
		if (click_time != -1L) {
			if (System.currentTimeMillis() > (click_time + 8000L)) {
				click_time = -1L;
			}
			return random(200, 300);
		}
		switch (stages.get(stage).id) {
		case STAGE_FISH:
			return fish();
		case STAGE_COOK:
			return cook();
		case STAGE_DEPOSIT:
			return deposit();
		case STAGE_SELL:
			return sell();
		case STAGE_WITHDRAW:
			return withdraw();
		case STAGE_BUY:
			return buy();
		}
		stopScript();
		throw new RuntimeException("invalid stage");
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		} else if (str.contains("enough money")) {
			System.out.println("Out of money");
			stopScript();
		} else if (str.contains("fishing bait")) {
			System.out.println("Out of bait");
			setAutoLogin(false);
			stopScript();
		} else if (str.contains("feather")) {
			System.out.println("Out of feathers");
			setAutoLogin(false);
			stopScript();
		}
		else if (str.contains("tired")) {
			should_sleep = true;
		} else if (str.contains("you catch")) {
			click_time = -1L;
			++cur_fish_success;
			++total_fish_success;
		} else if (str.contains("you fail to catch")) {
			click_time = -1L;
			++cur_fish_fails;
			++total_fish_fails;
		} else if (str.contains("nicely cooked")) {
			click_time = -1L;
			++cur_cook_success;
			++total_cook_success;
		} else if (str.contains("accidentally burn")) {
			click_time = -1L;
			++cur_cook_fails;
			++total_cook_fails;
		} else if (str.contains("advanced 1 fishing")) {
			++fishing_levels;
			System.out.printf("Congrats on advancing your fishing level %d times since starting this script.\n", fishing_levels);
			System.out.println("Stats for your last level:");
			System.out.printf("Successful attempts: %s (%s/h)\n",
			    iformat.format(cur_fish_success),
			    per_hour(cur_fish_success, fish_level_up_time));
			System.out.printf("Failed attempts: %s\n",
			    iformat.format(cur_fish_fails));
			System.out.printf("Fail rate: %f\n\n",
			    (double)cur_fish_fails / (double)cur_fish_success);
			fish_level_up_time = System.currentTimeMillis();
			cur_fish_success = 0;
			cur_fish_fails = 0;
		} else if (str.contains("advanced 1 cooking")) {
			++cooking_levels;
			System.out.printf("Congrats on advancing your cooking level %d times since starting this script.\n", cooking_levels);
			System.out.println("Stats for your last level:");
			System.out.printf("Successful attempts: %s (%s/h)\n",
			    iformat.format(cur_cook_success),
			    per_hour(cur_cook_success, cook_level_up_time));
			System.out.printf("Failed attempts: %s\n",
			    iformat.format(cur_cook_fails));
			System.out.printf("Fail rate: %f\n\n",
			    (double)cur_cook_fails / (double)cur_cook_success);
			cook_level_up_time = System.currentTimeMillis();
			cur_cook_success = 0;
			cur_cook_fails = 0;
		} else if (str.contains("have been standing")) {
			loop: do {
				int stage_id = stages.get(stage).id;
				switch (stages.get(stage).id) {
				case STAGE_FISH:
					move_x = getX() + random(-2, 2);
					move_y = getY() - random(0, 3);
					break;
				case STAGE_COOK:
					move_x = getX() + 1;
					move_y = getY() + random(-1, 1);
					break;
				case STAGE_BUY:
				case STAGE_SELL:
					int[] npc = getNpcById(FERNAHEI, OBLI);
					if (npc[0] == FERNAHEI) {
						move_x = FERNAHEI_SHOP_X;
						move_y = FERNAHEI_SHOP_Y +
						    random(0, 4);
					} else if (npc[0] == OBLI) {
						move_x = OBLI_SHOP_X +
						    random(-1, 1);
						move_y = OBLI_SHOP_Y -
						    random(0, 3);
					}
					break;
				default:
					System.out.printf("WARNING: Idle here! (%d,%d). Please report this!\n", getX(), getY());
					break loop;
				}
			} while (!isReachable(move_x, move_y) ||
			    (move_x == getX() && move_y == getY()));
		}
	}

	@Override
	public void paint() {
		if (start_time == -1L) {
			return;
		}
		final int orangey = 0xFFD900;
		final int white = 0xFFFFFF;
		int x = (getGameWidth() / 2) - 125;
		int y = 57;
		if (getGameWidth() >= (512 + 7 + 100) &&
		    getGameHeight() >= (346 + 84 + 25)) {
			/* bars are being displayed in corner */
			x = 9;
		}
		drawBoxAlphaFill(x - 6, y - 17,
		    260, box_bottom - 17, 120, 0x0);
		drawString("Shilo Script - @whi@" +
		    stages.get(stage).toString(),
		    x, y, 2, orangey);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time),
		    x + 10, y, 2, white);
		y += 15;
		if (have_stage(STAGE_FISH)) {
			drawString(String.format(
			    "Stats for current fishing level (%d gained)",
			    fishing_levels),
			    x, y, 2, orangey);
			y += 15;
			drawString(String.format(
			    "Successful fishing attempts: %s (%s/h)",
			    iformat.format(cur_fish_success),
			    per_hour(cur_fish_success, fish_level_up_time)),
			    x + 10, y, 2, white);
			y += 15;
			drawString(String.format(
			    "Failed fishing attempts: %s (%s/h)",
			    iformat.format(cur_fish_fails),
			    per_hour(cur_fish_fails, fish_level_up_time)),
			    x + 10, y, 2, white);
			y += 15;
			drawString("Fishing fail rate: " + (float)
			    ((double)cur_fish_fails / (double)cur_fish_success),
			    x + 10, y, 2, white);
			y += 15;
		}
		if (have_stage(STAGE_COOK)) {
			drawString(String.format(
			    "Stats for current cooking level (%d gained)",
			    cooking_levels),
			    x, y, 2, orangey);
			y += 15;
			drawString(String.format(
			    "Successful cooking attempts: %s (%s/h)",
			    iformat.format(cur_cook_success),
			    per_hour(cur_cook_success, cook_level_up_time)),
			    x + 10, y, 2, white);
			y += 15;
			drawString(String.format(
			    "Failed cooking attempts: %s (%s/h)",
			    iformat.format(cur_cook_fails),
			    per_hour(cur_cook_fails, cook_level_up_time)),
			    x + 10, y, 2, white);
			y += 15;
			drawString("Cooking fail rate: " + (float)
			    ((double)cur_cook_fails / (double)cur_cook_success),
			    x + 10, y, 2, white);
			y += 15;
		}
		if (fishing_levels > 0 || cooking_levels > 0) {
			drawString("Total:", x, y, 1, orangey);
			y += 15;
			if (fishing_levels > 0) {
				drawString("Successful fishing attempts: " +
				    iformat.format(total_fish_success),
				    x + 10, y, 2, white);
				y += 15;
				drawString("Failed fishing attempts: " +
				    iformat.format(total_fish_fails),
				    x + 10, y, 2, white);
				y += 15;
			}
			if (cooking_levels > 0) {
				drawString("Successful cooking attempts: " +
				    iformat.format(total_cook_success),
				    x + 10, y, 2, white);
				y += 15;
				drawString("Failed cooking attempts: " +
				    iformat.format(total_cook_fails),
				    x + 10, y, 2, white);
				y += 15;
			}
		}
		if (have_stage(STAGE_DEPOSIT)) {
			drawString("Banked items:", x, y, 1, orangey);
			y += 15;
			for (int i = 0; i < bank_ids.length; ++i) {
				drawString(String.format("%s %s (%s/h)",
				    iformat.format(banked_count[i]),
				    getItemNameId(bank_ids[i]),
				    per_hour(banked_count[i], start_time)),
				    x + 10, y, 2, white);
				y += 15;
			}
		}
		box_bottom = y - 15;
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

	private String per_hour(long count, long start_time) {
		double amount, secs;

		if (count == 0) return "0";
		amount = count * 60.0 * 60.0;
		secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}

	private void ingame_init() {
		start_time = fish_level_up_time = cook_level_up_time =
		    System.currentTimeMillis();

		bank_time = -1L;
		menu_time = -1L;
		shop_time = -1L;
		click_time = -1L;

		cur_fish_success = 0;
		cur_fish_fails = 0;
		total_fish_success = 0;
		total_fish_fails = 0;
		fishing_levels = 0;

		cur_cook_success = 0;
		cur_cook_fails = 0;
		total_cook_success = 0;
		total_cook_fails = 0;
		cooking_levels = 0;

		should_sleep = false;
		discarding = false;
		stage = 0;
	}

	private int fish() {
		if (discarding) {
			int index = getInventoryIndex(discard_ids);
			if (index != -1) {
				dropItem(index);
				return random(1000, 1500);
			}
			discarding = false;
		}
		if (stages.size() > 1 && getInventoryCount() == MAX_INV_SIZE) {
			if (getInventoryIndex(discard_ids) != -1) {
				discarding = true;
				return 0;
			}
			next_stage();
			return 0;
		}
		if (pickup.getState()) {
			int[] item = { -1, -1, -1 };
			if (item[0] == -1) {
				item = getItemById(raw_ids);
			}
			if (item[1] == getX() && item[2] == getY()) {
				pickupItem(item[0], item[1], item[2]);
				return random(800, 1200);
			}
		}
		if (click1) {
			atObject(fish_x, fish_y);
		} else {
			atObject2(fish_x, fish_y);
		}
		click_time = System.currentTimeMillis();
		return random(600, 800);
	}

	private int cook() {
		int raw = getInventoryIndex(raw_ids);
		if (raw == -1) {
			int drop = getInventoryIndex(burnt_ids);
			if (drop != -1) {
				dropItem(drop);
				return random(1000, 1500);
			}
			if (!have_stage(STAGE_DEPOSIT)) {
				int eat = getInventoryIndex(cooked_ids);
				if (eat != -1) {
					useItem(eat);
					return random(600, 800);
				}
			}
			if (pickup.getState() &&
			    getInventoryCount() < MAX_INV_SIZE) {
				int[] item;
				item = getItemById(raw_ids);
				if (item[1] == getX() && item[2] == getY()) {
					pickupItem(item[0], item[1], item[2]);
					return random(600, 1000);
				}
				item = getItemById(cooked_ids);
				if (item[1] == getX() && item[2] == getY()) {
					pickupItem(item[0], item[1], item[2]);
					return random(600, 1000);
				}
			}
			next_stage();
			return 0;
		}
		useSlotOnObject(raw, RANGE_OBJECT_X, RANGE_OBJECT_Y);
		click_time = System.currentTimeMillis();
		return random(600, 800);
	}

	private int access_bank() {
		if (bank_time != -1L) {
			if (System.currentTimeMillis() > (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(200, 300);
		}
		if (isQuestMenu()) {
			answer(0);
			menu_time = -1L;
			bank_time = System.currentTimeMillis();
			return random(600, 800);
		}
		if (menu_time != -1L) {
			if (System.currentTimeMillis() > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(200, 300);
		}
		int[] npc = getNpcByIdNotTalk(BANKERS);
		if (npc[0] != -1) {
			talkToNpc(npc[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	private int deposit() {
		if (isBanking()) {
			bank_time = -1L;
			for (int i = 0; i < bank_ids.length; ++i) {
				int id = bank_ids[i];
				int count = getInventoryCount(id);
				if (count == 0) {
					continue;
				}
				if (!has_banked[i]) {
					has_banked[i] = true;
					banked_count[i] += count;
				}
				deposit(id, count);
				return random(600, 800);
			}
			closeBank();
			Arrays.fill(has_banked, false);
			next_stage();
			return random(600, 800);
		}
		return access_bank();
	}

	private int get_id_to_sell() {
		for (int id : raw_ids) {
			if (inArray(do_not_sell, id)) {
				continue;
			}
			if (getInventoryCount(id) > 0) {
				return id;
			}
		}
		for (int id : cooked_ids) {
			if (inArray(do_not_sell, id)) {
				continue;
			}
			if (getInventoryCount(id) > 0) {
				return id;
			}
		}
		return -1;
	}

	private int sell() {
		int id = get_id_to_sell();
		if (id == -1) {
			if (isShopOpen()) {
				closeShop();
			}
			next_stage();
			return random(600, 800);
		}
		if (isShopOpen()) {
			shop_time = -1L;
			int index = getShopItemById(id);
			if (getShopItemAmount(index) > 65000) {
				System.out.println("The shop is full!");
				setAutoLogin(false);
				stopScript();
				return 0;
			}
			sellShopItem(index, getInventoryCount(id));
			return random(600, 800);
		}
		return access_shop();
	}

	private int access_shop() {
		if (shop_time != -1L) {
			if (System.currentTimeMillis() > (shop_time + 8000L)) {
				shop_time = -1L;
			}
			return random(200, 300);
		}
		if (isQuestMenu()) {
			answer(0);
			menu_time = -1L;
			shop_time = System.currentTimeMillis();
			return random(600, 800);
		}
		if (menu_time != -1L) {
			if (System.currentTimeMillis() > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(200, 300);
		}
		int[] npc = getNpcByIdNotTalk(FERNAHEI, OBLI);
		if (npc[0] != -1) {
			talkToNpc(npc[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	private int withdraw() {
		if (getInventoryCount() == MAX_INV_SIZE) {
			if (isBanking()) {
				closeBank();
			}
			next_stage();
			return random(600, 800);
		}
		if (isBanking()) {
			int[] withdraw_ids;
			if (!Arrays.equals(Arrays.copyOf(bank_ids,
			    raw_ids.length), raw_ids)) {
				withdraw_ids = raw_ids;
			} else {
				withdraw_ids = cooked_ids;
			}
			for (int id : withdraw_ids) {
				int count = bankCount(id);
				if (count == 0) continue;
				int empty = getEmptySlots();
				if (count > empty) {
					count = empty;
				}
				withdraw(id, count);
				return random(600, 800);
			}
			if (getInventoryCount(withdraw_ids) > 0) {
				closeBank();
				next_stage();
				return random(600, 800);
			}
			System.out.println("Out of items");
			setAutoLogin(false);
			stopScript();
			return 0;
		}
		return access_bank();
	}

	private int buy() {
		if (getInventoryCount() == MAX_INV_SIZE) {
			if (isShopOpen()) {
				closeShop();
			}
			next_stage();
			return random(600, 800);
		}
		if (isShopOpen()) {
			int[] buy_ids;
			if (getAllNpcById(FERNAHEI, OBLI)[0] == OBLI) {
				buy_ids = cooked_ids;
			} else {
				buy_ids = raw_ids;
			}
			for (int id : buy_ids) {
				int index = getShopItemById(id);
				if (index == -1) continue;
				int amount = getShopItemAmount(index);
				if (amount == 0) continue;
				int empty = getEmptySlots();
				if (amount > empty) {
					amount = empty;
				}
				buyShopItem(index, amount);
				return random(600, 800);
			}
			return 0;
		}
		return access_shop();
	}

	private void next_stage() {
		if (stage == (stages.size() - 1)) {
			stage = 0;
		} else {
			++stage;
		}

		Stage s = stages.get(stage);
		boolean found = false;

		switch (s.id) {
		case STAGE_DEPOSIT:
			for (int i = 0; i < bank_ids.length; ++i) {
				int id = bank_ids[i];
				if (getInventoryIndex(id) != -1) {
					found = true;
					break;
				}
			}
			if (!found) {
				next_stage();
				return;
			}
			break;
		case STAGE_SELL:
			if (get_id_to_sell() == -1) {
				next_stage();
				return;
			}
			break;
		}
		if (s.path_to != null) {
			pw.setPath(s.path_to);
		}
	}

	private void create_frame() {
		final Panel checkboxes = new Panel(new GridLayout(0, 1));

		final List<Checkbox> acquire_boxes = new ArrayList<>();
		final CheckboxGroup acquire_group = new CheckboxGroup();

		final Checkbox fish = new Checkbox(
		    "Fish raw fish",
		    acquire_group, true);
		acquire_boxes.add(fish);

		final Checkbox withdraw = new Checkbox(
		    "Withdraw raw fish",
		    acquire_group, false);
		acquire_boxes.add(withdraw);

		final Checkbox buy_raw = new Checkbox(
		    "Buy raw fish (Fernahei's Fishing Shop)",
		    acquire_group, false);
		acquire_boxes.add(buy_raw);

		final Checkbox buy_cooked = new Checkbox(
		    "Buy cooked fish (Obli's General Store)",
		    acquire_group, false);
		acquire_boxes.add(buy_cooked);

		final CheckboxGroup dispose_group = new CheckboxGroup();
		final List<Checkbox> dispose_boxes = new ArrayList<>();

		final Checkbox deposit = new Checkbox(
		    "Deposit fish",
		    dispose_group, true);
		dispose_boxes.add(deposit);

		final Checkbox sell_cooked = new Checkbox(
		    "Sell cooked fish (Obli's General Store)",
		    dispose_group, false);
		dispose_boxes.add(sell_cooked);
		sell_cooked.setEnabled(false);

		final Checkbox sell_raw = new Checkbox(
		    "Sell raw fish (Fernahei's Fishing Shop)",
		    dispose_group, false);
		dispose_boxes.add(sell_raw);

		final Checkbox power = new Checkbox(
		    "Power fish or eat cooked fish",
		    dispose_group, false);
		dispose_boxes.add(power);

		pickup = new Checkbox("Pick up raw fish", false);

		final Checkbox cook = new Checkbox("Cook fish", false);
		cook.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				acquire_group.setSelectedCheckbox(fish);
				acquire_box_listener
				    .itemStateChanged(new ItemEvent(fish,
				    ItemEvent.ITEM_FIRST, fish,
				    ItemEvent.SELECTED));

				dispose_group.setSelectedCheckbox(deposit);
				dispose_box_listener
				    .itemStateChanged(new ItemEvent(deposit,
				    ItemEvent.ITEM_FIRST, deposit,
				    ItemEvent.SELECTED));

				int change = e.getStateChange();
				if (change == ItemEvent.SELECTED) {
					sell_cooked.setEnabled(true);
					buy_cooked.setEnabled(false);
					power.setEnabled(true);
				} else {
					sell_cooked.setEnabled(false);
					buy_cooked.setEnabled(true);
				}
			}
		});

		acquire_box_listener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				dispose_group.setSelectedCheckbox(deposit);
				dispose_box_listener
				    .itemStateChanged(new ItemEvent(deposit,
				    ItemEvent.ITEM_FIRST, deposit,
				    ItemEvent.SELECTED));

				int change = e.getStateChange();
				if (change != ItemEvent.SELECTED) {
					return;
				}
				if (cook.getState() || e.getSource() == fish) {
					power.setEnabled(true);
				} else {
					power.setEnabled(false);
				}
				if (e.getSource() == buy_raw) {
					sell_raw.setEnabled(false);
					cook.setEnabled(true);
				} else if (e.getSource() == buy_cooked) {
					sell_raw.setEnabled(false);
					cook.setEnabled(false);
				} else {
					sell_raw.setEnabled(true);
					cook.setEnabled(true);
				}
			}
		};

		for (Checkbox c : acquire_boxes) {
			c.addItemListener(acquire_box_listener);
		}

		final List<Checkbox> non_keepies = new ArrayList<>(3);
		non_keepies.add(sell_raw);
		non_keepies.add(sell_cooked);
		non_keepies.add(power);

		dispose_box_listener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
			}
		};

		for (Checkbox c : dispose_boxes) {
			c.addItemListener(dispose_box_listener);
		}


		cs_choice = new Choice();
		for (String str : FIGHTMODES) {
			cs_choice.add(str);
		}
		try {
			cs_choice.select(getFightMode());
		} catch (NullPointerException e) {
		}

		Panel cs_panel = new Panel(new GridLayout(1, 0));
		cs_panel.add(new Label(" Combat style"));
		cs_panel.add(cs_choice);

		Font bold_title = new Font(Font.SANS_SERIF, Font.BOLD, 14);

		Label acquire_label = new Label("Fish acquisition method",
		    Label.CENTER);
		acquire_label.setFont(bold_title);

		Label dispose_label = new Label("Fish disposition method",
		    Label.CENTER);
		dispose_label.setFont(bold_title);

		final Label space_saver_a = new Label();
		final Label space_saver_b = new Label();
		final Label space_saver_c = new Label();

		checkboxes.add(cs_panel);
		checkboxes.add(cook);
		checkboxes.add(pickup);

		checkboxes.add(acquire_label);
		checkboxes.add(fish);
		checkboxes.add(withdraw);
		checkboxes.add(buy_raw);
		checkboxes.add(buy_cooked);

		checkboxes.add(dispose_label);
		checkboxes.add(deposit);
		checkboxes.add(power);
		checkboxes.add(sell_raw);
		checkboxes.add(sell_cooked);

		checkboxes.add(space_saver_b);
		checkboxes.add(space_saver_c);

		final java.awt.List list = new java.awt.List();
		list.add("Salmon/trout");
		list.add("Pike");
		list.select(0);
		list.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkboxes.invalidate();
				checkboxes.remove(space_saver_a);
				checkboxes.remove(space_saver_b);
				checkboxes.remove(space_saver_c);
				pickup.setEnabled(true);
				if (cook.getState()) {
					sell_cooked.setEnabled(true);
				}

				acquire_group.setSelectedCheckbox(fish);
				acquire_box_listener
				    .itemStateChanged(new ItemEvent(fish,
				    ItemEvent.ITEM_FIRST, fish,
				    ItemEvent.SELECTED));

				dispose_group.setSelectedCheckbox(deposit);
				dispose_box_listener
				    .itemStateChanged(new ItemEvent(deposit,
				    ItemEvent.ITEM_FIRST, deposit,
				    ItemEvent.SELECTED));

				checkboxes.add(space_saver_a);
				checkboxes.add(space_saver_b);
				checkboxes.add(space_saver_c);
				checkboxes.validate();
			}
		});

		Button ok = new Button("OK");
		ok.addActionListener(new ActionListener() {
			private void set_ids() {
				switch (list.getSelectedItem()) {
				case "Salmon/trout":
					// prefer the spot "near" the fire when cooking
					if (cook.getState()) {
						fish_x = 396;
						fish_y = 833;
					}
					else {
						fish_x = 399;
						fish_y = 836;
					}
					click1 = true;
					raw_ids = new int[] { 356, 358 };
					cooked_ids = new int[] { 357, 359 };
					burnt_ids = new int[] { 360 };
					discard_ids = new int[] {};
					break;
				case "Pike":
					// prefer the spot "near" the fire when cooking
					if (cook.getState()) {
						fish_x = 396;
						fish_y = 833;
					}
					else {
						fish_x = 399;
						fish_y = 836;
					}
					click1 = false;
					raw_ids = new int[] { 363 };
					cooked_ids = new int[] { 364 };
					burnt_ids = new int[] { 365 };
					discard_ids = new int[] {};
					break;
				default:
					throw new Error("unknown fish");
				}

			}

			@Override
			public void actionPerformed(ActionEvent e) {
				int start_x, start_y;
				int x, y;

				set_ids();

				stages.clear();

				if (fish.getState()) {
					stages.add(new Stage(STAGE_FISH));
					start_x = x = fish_x + random(0, 1);
					start_y = y = fish_y - random(2, 3);
				} else if (withdraw.getState()) {
					stages.add(new Stage(STAGE_WITHDRAW));
					start_x = x = BANK_X;
					start_y = y = BANK_Y;
				} else if (buy_raw.getState()) {
					stages.add(new Stage(STAGE_BUY));
					start_x = x = FERNAHEI_SHOP_X;
					start_y = y = FERNAHEI_SHOP_Y;
				} else if (buy_cooked.getState()) {
					stages.add(new Stage(STAGE_BUY));
					start_x = x = OBLI_SHOP_X;
					start_y = y = OBLI_SHOP_Y;
				} else {
					throw new RuntimeException();
				}
				if (cook.getState() || buy_cooked.getState()) {
					bank_ids = cooked_ids;
				} else {
					bank_ids = raw_ids;
				}
				if (cook.getState()) {
					stages.add(new Stage(STAGE_COOK,
					    calc_path(x, y, COOK_X, COOK_Y)));
					x = COOK_X;
					y = COOK_Y;
				}
				if (do_not_sell == null) {
					do_not_sell = new int[] {};
				}
				has_banked = new boolean[bank_ids.length];
				banked_count = new int[bank_ids.length];
				if (deposit.getState()) {
					stages.add(new Stage(STAGE_DEPOSIT,
					    calc_path(x, y, BANK_X, BANK_Y)));
					if (stages.get(0).id != STAGE_WITHDRAW) {
						stages.get(0).path_to = calc_path(
						    BANK_X, BANK_Y,
						    start_x, start_y);
					}
				} else if (sell_raw.getState()) {
					stages.add(new Stage(STAGE_SELL,
					    calc_path(x, y,
					    FERNAHEI_SHOP_X, FERNAHEI_SHOP_Y)));
					stages.get(0).path_to = calc_path(
					    FERNAHEI_SHOP_X, FERNAHEI_SHOP_Y,
					    start_x, start_y);
				} else if (sell_cooked.getState()) {
					stages.add(new Stage(STAGE_SELL,
					    calc_path(x, y,
					    OBLI_SHOP_X, OBLI_SHOP_Y)));
					stages.get(0).path_to = calc_path(
					    OBLI_SHOP_X, OBLI_SHOP_Y,
					    start_x, start_y);
				} else if (cook.getState()) {
					stages.get(0).path_to = calc_path(
					    COOK_X, COOK_Y,
					    start_x, start_y);
				}

				System.out.println(stages);
				frame.setVisible(false);
			}
		});

		Button cancel = new Button("Cancel");
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});

		Panel buttons = new Panel();
		buttons.add(ok);
		buttons.add(cancel);

		Panel middle = new Panel(new BorderLayout());
		middle.add(list, BorderLayout.CENTER);
		middle.add(checkboxes, BorderLayout.EAST);

		frame = new Frame(getClass().getSimpleName());
		frame.addWindowListener(new StandardCloseHandler(frame,
		    StandardCloseHandler.HIDE));
		frame.setIconImages(Constants.ICONS);
		frame.add(middle, BorderLayout.CENTER);
		frame.add(buttons, BorderLayout.SOUTH);
		frame.setSize(500, 515);
		frame.setMinimumSize(frame.getSize());
	}

	private PathWalker.Path calc_path(int x1, int y1, int x2, int y2) {
		if (!pw_init) {
			pw.init(null);
			pw_init = true;
		}
		PathWalker.Path p = pw.calcPath(x1, y1, x2, y2);
		if (p == null) {
			System.out.printf("FATAL: failed to calculate path %d,%d to %d,%d\n", x1, y1, x2, y2);
		}
		return p;
	}

	private boolean have_stage(int id) {
		for (Stage s : stages) {
			if (s.id == id) return true;
		}
		return false;
	}
}
