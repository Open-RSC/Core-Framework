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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;
import com.aposbot.StandardCloseHandler;
import com.aposbot.Constants;

public final class S_CatherbyBNet extends Script {

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
			case STAGE_CERT:
				return "Certing";
			case STAGE_DEPOSIT:
				return "Depositing";
			case STAGE_SELL:
				return "Selling";
			case STAGE_WITHDRAW:
				return "Withdrawing";
			case STAGE_UNCERT:
				return "Uncerting";
			case STAGE_BUY:
				return "Buying";
			default:
				return "Invalid";
			}
		}
	}

	// id of the possible combined fishes
	public static final int[][] fish_spots_ids = {
			// mackerel/cod/bass
			{ 552, 550, 554 } };

	public static final String[][] fish_spots_names = { { "mackerel", "cod", "bass" } };

	public static final String[] combined_fish_spots = { "Mackerel/cod/bass" };

	public static final String[] other_loot_names = { "seaweed", "gloves", "boots", "oyster", "casket" };

	public static int fish_cursor;

	// spot shared by 3
	public static final long[] fish_success = new long[3];
	// seaweed, gloves, boots, oyster shell, casket
	public static final long[] other_catch = new long[5];
	public static final long[] cook_success = new long[3];
	public static final long[] cook_failure = new long[3];
	private static final int LEVEL_COOKING = 7;
	private static final int LEVEL_FISHING = 10;

	private static final int UNOPENED_OYSTER = 793;
	private static final int CASKET = 549;

	private static final int[] pearl_loot_ids = { 779, 792, 791 };
	private static final int[] casket_loot_ids = { 160, 159, 158, 157, 527, 526, 1277 };

	private static final long[] pearl_loot_count = new long[4];
	// coins is two, how many times coins were received and total coins
	private static final long[] casket_loot_count = new long[10];

	private static final int[] non_fish_net_loot = { 779, 792, /* oysters */
			157, 158, 159, 160, 526, 527, 1277 /* casket stuff */
	};

	private static final int[] bass = { 554, 555 };

	private static final int STAGE_FISH = 0;
	private static final int STAGE_COOK = 1;
	private static final int STAGE_CERT = 2;
	private static final int STAGE_DEPOSIT = 3;
	private static final int STAGE_SELL = 4;
	private static final int STAGE_WITHDRAW = 5;
	private static final int STAGE_UNCERT = 6;
	private static final int STAGE_BUY = 7;

	private static final int HARRY = 250;
	private static final int HARRY_SHOP_X = 418;
	private static final int HARRY_SHOP_Y = 488;

	private static final int ARHEIN = 280;
	private static final int ARHEIN_SHOP_X = 439;
	private static final int ARHEIN_SHOP_Y = 503;

	private static final int CERTER = 299;

	private static final int CERTER_X = 428;
	private static final int CERTER_Y = 484;

	private static final int COOK_X = 435;
	private static final int COOK_Y = 485;

	private static final int RANGE_OBJECT_X = 432;
	private static final int RANGE_OBJECT_Y = 480;

	private static final int BANK_X = 439;
	private static final int BANK_Y = 495;

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
	private boolean big_netting;
	private boolean opening;
	private boolean looting;

	private boolean openedOyster;
	private boolean openedCasket;

	private int[] bank_ids;
	private boolean[] has_banked;
	private int[] banked_count;

	private int[] do_not_sell;
	private String lastStr = "";

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

	private FileWriter fishcsv;
	private FileWriter cookcsv;
	private FileWriter pearllootcsv;
	private FileWriter casketlootcsv;

	private long cur_attempts;
	private long cur_cattempts;

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

	public S_CatherbyBNet(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);

		try {
			String fileName = "big_netting_exp.csv";
			boolean fileCreated = false;
			File createCsv = new File(fileName);
			if (!createCsv.exists()) {
				createCsv.createNewFile();
				fileCreated = true;
			}

			fishcsv = new FileWriter(createCsv, true);
			if (fileCreated) {
				fishcsv.write("fish_spot,level,attempts,mackerel,cod,bass,seaweed,gloves,boots,oyster,casket\n");
				fishcsv.flush();
			}

			fileName = "cooking_exp.csv";
			fileCreated = false;
			createCsv = new File(fileName);
			if (!createCsv.exists()) {
				createCsv.createNewFile();
				fileCreated = true;
			}

			cookcsv = new FileWriter(createCsv, true);
			if (fileCreated) {
				cookcsv.write("raw_fish_id,fish_name,level,attempts,success,fails\n");
				cookcsv.flush();
			}

			fileName = "pearl_loot.csv";
			fileCreated = false;
			createCsv = new File(fileName);
			if (!createCsv.exists()) {
				createCsv.createNewFile();
				fileCreated = true;
			}

			pearllootcsv = new FileWriter(createCsv, true);
			if (fileCreated) {
				pearllootcsv.write("oyster_count,pearl1,pearl2,empty\n");
				pearllootcsv.flush();
			}

			fileName = "casket_loot.csv";
			fileCreated = false;
			createCsv = new File(fileName);
			if (!createCsv.exists()) {
				createCsv.createNewFile();
				fileCreated = true;
			}
			casketlootcsv = new FileWriter(createCsv, true);
			if (fileCreated) {
				casketlootcsv.write(
						"casket_count,num_times_coins,total_coins,sapphire,emerald,ruby,diamond,loop_half,tooth_half,shield_half\n");
				casketlootcsv.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) {
		S_CatherbyBNet s = new S_CatherbyBNet(null);
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
		case STAGE_CERT:
			return cert();
		case STAGE_DEPOSIT:
			return deposit();
		case STAGE_SELL:
			return sell();
		case STAGE_WITHDRAW:
			return withdraw();
		case STAGE_UNCERT:
			return uncert();
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
		} else if (str.contains("tired")) {
			should_sleep = true;
		} else if (str.contains("open the oyster shell")) {
			openedOyster = true;
		} else if (str.contains("find some treasure inside")) {
			openedCasket = true;
		} else if (str.contains("attempt")) {
			// special case for big netting since server may respond with
			// nothing
			// in case of two consecutive attempts, just increment the fail
			if (lastStr.contains("attempt")) {
				++cur_fish_fails;
				++total_fish_fails;
				++cur_attempts;
			}
		} else if (str.contains("you catch")) {
			click_time = -1L;
			String fish;
			for (int i = 0; i < fish_spots_names[fish_cursor].length; i++) {
				fish = fish_spots_names[fish_cursor][i];
				if (str.contains(fish)) {
					++fish_success[i];
					break;
				}
			}
			String other;
			for (int i = 0; i < other_loot_names.length; i++) {
				other = other_loot_names[i];
				if (str.contains(other)) {
					++other_catch[i];
					break;
				}
			}
			++cur_fish_success;
			++total_fish_success;
			++cur_attempts;

			if (cur_attempts >= 1000)
				_fishCsvOut();
		} else if (str.contains("you fail to catch")) {
			click_time = -1L;
			++cur_fish_fails;
			++total_fish_fails;
			++cur_attempts;
		} else if (str.contains("nicely cooked")) {
			click_time = -1L;
			String fish;
			for (int i = 0; i < fish_spots_names[fish_cursor].length; i++) {
				fish = fish_spots_names[fish_cursor][i];
				if (str.contains(fish)) {
					++cook_success[i];
					break;
				}
			}
			++cur_cook_success;
			++total_cook_success;
			++cur_cattempts;

			if (cur_cattempts >= 1000)
				_cookCsvOut();
		} else if (str.contains("accidentally burn")) {
			click_time = -1L;
			String fish;
			for (int i = 0; i < fish_spots_names[fish_cursor].length; i++) {
				fish = fish_spots_names[fish_cursor][i];
				if (str.contains(fish)) {
					++cook_failure[i];
					break;
				}
			}
			++cur_cook_fails;
			++total_cook_fails;
			++cur_cattempts;
		} else if (str.contains("advanced 1 fishing")) {
			++fishing_levels;
			System.out.printf("Congrats on advancing your fishing level %d times since starting this script.\n",
					fishing_levels);
			System.out.println("Stats for your last level:");
			System.out.printf("Successful attempts: %s (%s/h)\n", iformat.format(cur_fish_success),
					per_hour(cur_fish_success, fish_level_up_time));
			System.out.printf("Failed attempts: %s\n", iformat.format(cur_fish_fails));
			System.out.printf("Fail rate: %f\n\n", (double) cur_fish_fails / (double) cur_fish_success);
			_fishCsvOut();
			fish_level_up_time = System.currentTimeMillis();
			cur_fish_success = 0;
			cur_fish_fails = 0;
		} else if (str.contains("advanced 1 cooking")) {
			++cooking_levels;
			System.out.printf("Congrats on advancing your cooking level %d times since starting this script.\n",
					cooking_levels);
			System.out.println("Stats for your last level:");
			System.out.printf("Successful attempts: %s (%s/h)\n", iformat.format(cur_cook_success),
					per_hour(cur_cook_success, cook_level_up_time));
			System.out.printf("Failed attempts: %s\n", iformat.format(cur_cook_fails));
			System.out.printf("Fail rate: %f\n\n", (double) cur_cook_fails / (double) cur_cook_success);
			_cookCsvOut();
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
					int[] npc = getNpcById(ARHEIN, HARRY);
					if (npc[0] == ARHEIN) {
						move_x = ARHEIN_SHOP_X;
						move_y = ARHEIN_SHOP_Y + random(0, 4);
					} else if (npc[0] == HARRY) {
						move_x = HARRY_SHOP_X + random(-1, 1);
						move_y = HARRY_SHOP_Y - random(0, 3);
					}
					break;
				default:
					System.out.printf("WARNING: Idle here! (%d,%d). Please report this!\n", getX(), getY());
					break loop;
				}
			} while (!isReachable(move_x, move_y) || (move_x == getX() && move_y == getY()));
		}
		lastStr = str;
	}

	private void _fishCsvOut() {
		try {
			fishcsv.write(combined_fish_spots[fish_cursor] + "," + getLevel(LEVEL_FISHING) + "," + cur_attempts + ","
					+ fish_success[0] + "," + fish_success[1] + "," + fish_success[2] + "," + other_catch[0] + ","
					+ other_catch[1] + "," + other_catch[2] + "," + other_catch[3] + "," + other_catch[4] + "\n");
			fishcsv.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		cur_attempts = 0;
		fish_success[0] = fish_success[1] = fish_success[2] = 0;
		other_catch[0] = other_catch[1] = other_catch[2] = other_catch[3] = other_catch[4] = 0;
	}

	private void _cookCsvOut() {
		try {
			cookcsv.write(fish_spots_ids[fish_cursor][0] + "," + fish_spots_names[fish_cursor][0] + ","
					+ getLevel(LEVEL_COOKING) + "," + (cook_success[0] + cook_failure[0]) + "," + cook_success[0] + ","
					+ cook_failure[0] + "\n");
			if (fish_spots_ids[fish_cursor].length > 1) {
				cookcsv.write(fish_spots_ids[fish_cursor][1] + "," + fish_spots_names[fish_cursor][1] + ","
						+ getLevel(LEVEL_COOKING) + "," + (cook_success[1] + cook_failure[1]) + "," + cook_success[1]
						+ "," + cook_failure[1] + "\n");
			}
			if (fish_spots_ids[fish_cursor].length > 2) {
				cookcsv.write(fish_spots_ids[fish_cursor][2] + "," + fish_spots_names[fish_cursor][2] + ","
						+ getLevel(LEVEL_COOKING) + "," + (cook_success[2] + cook_failure[2]) + "," + cook_success[2]
						+ "," + cook_failure[2] + "\n");
			}
			cookcsv.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		cur_cattempts = 0;
		cook_success[0] = cook_success[1] = cook_success[2] = 0;
		cook_failure[0] = cook_failure[1] = cook_failure[2] = 0;
	}

	private void _pearlCsvOut() {
		try {
			pearllootcsv.write(pearl_loot_count[0] + "," + pearl_loot_count[1] + "," + pearl_loot_count[2] + ","
					+ pearl_loot_count[3] + "\n");
			pearllootcsv.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		pearl_loot_count[0] = pearl_loot_count[1] = pearl_loot_count[2] = pearl_loot_count[3] = 0;
	}

	private void _casketCsvOut() {
		try {
			casketlootcsv.write(casket_loot_count[0] + "," + casket_loot_count[1] + "," + casket_loot_count[2] + ","
					+ casket_loot_count[3] + "," + casket_loot_count[4] + "," + casket_loot_count[5] + ","
					+ casket_loot_count[6] + "," + casket_loot_count[7] + "," + casket_loot_count[8] + ","
					+ casket_loot_count[9] + "\n");
			casketlootcsv.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		casket_loot_count[0] = casket_loot_count[1] = casket_loot_count[2] = casket_loot_count[3] = 0;
		casket_loot_count[4] = casket_loot_count[5] = casket_loot_count[6] = casket_loot_count[7] = 0;
		casket_loot_count[8] = casket_loot_count[9] = 0;
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
		if (getGameWidth() >= (512 + 7 + 100) && getGameHeight() >= (346 + 84 + 25)) {
			/* bars are being displayed in corner */
			x = 9;
		}
		drawBoxAlphaFill(x - 6, y - 17, 260, box_bottom - 17, 120, 0x0);
		drawString("CatherbyBNet Script - @whi@" + stages.get(stage).toString(), x, y, 2, orangey);
		y += 15;
		drawString("Runtime: " + get_time_since(start_time), x + 10, y, 2, white);
		y += 15;
		if (have_stage(STAGE_FISH)) {
			drawString(String.format("Stats for current fishing level (%d gained)", fishing_levels), x, y, 2, orangey);
			y += 15;
			drawString(
					String.format("Successful fishing attempts: %s (%s/h), %s (%s/h), %s (%s/h), %s (%s/h)",
							iformat.format(cur_fish_success), per_hour(cur_fish_success, fish_level_up_time),
							iformat.format(fish_success[0]), per_hour(fish_success[0], fish_level_up_time),
							iformat.format(fish_success[1]), per_hour(fish_success[1], fish_level_up_time),
							iformat.format(fish_success[2]), per_hour(fish_success[2], fish_level_up_time)),
					x + 10, y, 2, white);
			y += 15;
			drawString(String.format("Failed fishing attempts: %s (%s/h)", iformat.format(cur_fish_fails),
					per_hour(cur_fish_fails, fish_level_up_time)), x + 10, y, 2, white);
			y += 15;
			drawString("Fishing fail rate: " + (float) ((double) cur_fish_fails / (double) cur_fish_success), x + 10, y,
					2, white);
			y += 15;
		}
		if (have_stage(STAGE_COOK)) {
			drawString(String.format("Stats for current cooking level (%d gained)", cooking_levels), x, y, 2, orangey);
			y += 15;
			drawString(
					String.format("Successful cooking attempts: %s (%s/h), %s (%s/h), %s (%s/h), %s (%s/h)",
							iformat.format(cur_cook_success), per_hour(cur_cook_success, cook_level_up_time),
							iformat.format(cook_success[0]), per_hour(cook_success[0], cook_level_up_time),
							iformat.format(cook_success[1]), per_hour(cook_success[1], cook_level_up_time),
							iformat.format(cook_success[2]), per_hour(cook_success[2], cook_level_up_time)),
					x + 10, y, 2, white);
			y += 15;
			drawString(
					String.format("Failed cooking attempts: %s (%s/h), %s (%s/h), %s (%s/h), %s (%s/h)",
							iformat.format(cur_cook_fails), per_hour(cur_cook_fails, cook_level_up_time),
							iformat.format(cook_failure[0]), per_hour(cook_failure[0], cook_level_up_time),
							iformat.format(cook_failure[1]), per_hour(cook_failure[1], cook_level_up_time),
							iformat.format(cook_failure[2]), per_hour(cook_failure[2], cook_level_up_time)),
					x + 10, y, 2, white);
			y += 15;
			drawString("Cooking fail rate: " + (float) ((double) cur_cook_fails / (double) cur_cook_success), x + 10, y,
					2, white);
			y += 15;
		}
		if (fishing_levels > 0 || cooking_levels > 0) {
			drawString("Total:", x, y, 1, orangey);
			y += 15;
			if (fishing_levels > 0) {
				drawString("Successful fishing attempts: " + iformat.format(total_fish_success), x + 10, y, 2, white);
				y += 15;
				drawString("Failed fishing attempts: " + iformat.format(total_fish_fails), x + 10, y, 2, white);
				y += 15;
			}
			if (cooking_levels > 0) {
				drawString("Successful cooking attempts: " + iformat.format(total_cook_success), x + 10, y, 2, white);
				y += 15;
				drawString("Failed cooking attempts: " + iformat.format(total_cook_fails), x + 10, y, 2, white);
				y += 15;
			}
		}
		if (have_stage(STAGE_DEPOSIT)) {
			drawString("Banked items:", x, y, 1, orangey);
			y += 15;
			for (int i = 0; i < bank_ids.length; ++i) {
				drawString(String.format("%s %s (%s/h)", iformat.format(banked_count[i]), getItemNameId(bank_ids[i]),
						per_hour(banked_count[i], start_time)), x + 10, y, 2, white);
				y += 15;
			}
		}
		drawString("Opened items:", x, y, 1, orangey);
		y += 15;
		drawString(String.format("%s %s", iformat.format(pearl_loot_count[0]), "Oyster")
				, x + 10, y, 2, white);
		y += 15;
		drawString(String.format("%s %s", iformat.format(casket_loot_count[0]), "Casket")
				, x + 10, y, 2, white);
		y += 15;
		box_bottom = y - 15;
	}

	private static String get_time_since(long t) {
		long millis = (System.currentTimeMillis() - t) / 1000;
		long second = millis % 60;
		long minute = (millis / 60) % 60;
		long hour = (millis / (60 * 60)) % 24;
		long day = (millis / (60 * 60 * 24));

		if (day > 0L) {
			return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
		}
		if (hour > 0L) {
			return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
		}
		if (minute > 0L) {
			return String.format("%02d minutes, %02d seconds", minute, second);
		}
		return String.format("%02d seconds", second);
	}

	private String per_hour(long count, long start_time) {
		double amount, secs;

		if (count == 0)
			return "0";
		amount = count * 60.0 * 60.0;
		secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format(amount / secs);
	}

	private void ingame_init() {
		start_time = fish_level_up_time = cook_level_up_time = System.currentTimeMillis();

		bank_time = -1L;
		menu_time = -1L;
		shop_time = -1L;
		click_time = -1L;

		cur_fish_success = 0;
		cur_fish_fails = 0;
		total_fish_success = 0;
		total_fish_fails = 0;
		fishing_levels = 0;

		fish_success[0] = fish_success[1] = fish_success[2] = 0;
		other_catch[0] = other_catch[1] = other_catch[2] = other_catch[3] = other_catch[4] = 0;

		cur_cook_success = 0;
		cur_cook_fails = 0;
		total_cook_success = 0;
		total_cook_fails = 0;
		cooking_levels = 0;

		cook_success[0] = cook_success[1] = cook_success[2] = 0;
		cook_failure[0] = cook_failure[1] = cook_failure[2] = 0;

		cur_attempts = 0;
		cur_cattempts = 0;

		pearl_loot_count[0] = pearl_loot_count[1] = pearl_loot_count[2] = pearl_loot_count[3] = 0;
		casket_loot_count[0] = casket_loot_count[1] = casket_loot_count[2] = casket_loot_count[3] = 0;
		casket_loot_count[4] = casket_loot_count[5] = casket_loot_count[6] = casket_loot_count[7] = 0;
		casket_loot_count[8] = casket_loot_count[9] = 0;

		should_sleep = false;
		discarding = false;
		opening = false;
		looting = false;
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
		if(looting) {
			int index = getInventoryIndex(pearl_loot_ids);
			if(index != -1) {
				pearl_loot_count[0]++;
				for(int i=0; i<pearl_loot_ids.length; i++) {
					int count = getInventoryCount(pearl_loot_ids[i]);
		            if (count > 0) {
		            	++pearl_loot_count[i+1];
		            	break;
		            }
				}
				dropItem(index);
				return random(1000, 1500);
			}
			index = getInventoryIndex(casket_loot_ids);
			if(index != -1) {
				casket_loot_count[0]++;
				for(int i=0; i<casket_loot_ids.length; i++) {
					int count = getInventoryCount(casket_loot_ids[i]);
		            if (count > 0) {
		            	++casket_loot_count[i+3];
		            	break;
		            }
				}
				dropItem(index);
				return random(1000, 1500);
			}
			index = getInventoryIndex(10);
			if(index != -1) {
				casket_loot_count[0]++;
				++casket_loot_count[1];
				int coinsStack = getInventoryStack(index);
				casket_loot_count[2] += coinsStack;
				dropItem(index);
				return random(1000, 1500);
			}
			looting = false;
		}
		if(opening) {
			int index = getInventoryIndex(UNOPENED_OYSTER);
			if (index != -1) {
				looting = true;
				useItem(index);
				return random(1000, 1500);
			}
			index = getInventoryIndex(CASKET);
			if (index != -1) {
				looting = true;
				useItem(index);
				return random(1000, 1500);
			}
			opening = false;
		}
		if (stages.size() > 1 && getInventoryCount() == MAX_INV_SIZE) {
			if (getInventoryIndex(discard_ids) != -1) {
				discarding = true;
				return random(1000, 1500);
			}
			if(getInventoryIndex(CASKET, UNOPENED_OYSTER) != -1) {
				opening = true;
				return random(1000, 1500);
			}
			// int index = getInventoryIndex(CASKET,
			// UNOPENED_OYSTER);
			if (pearl_loot_count[0] >= 10) {
				_pearlCsvOut();
			}
			if (casket_loot_count[0] >= 3) {
				_casketCsvOut();
			}
			
			int[] casket = getItemById(CASKET, UNOPENED_OYSTER);
			int index;
			if (casket[1] == getX() && casket[2] == getY()) {
				for (int id : raw_ids) {
					index = getInventoryIndex(id);
					if (index == -1)
						continue;
					dropItem(index);
					return random(1000, 1500);
				}
				for (int id : cooked_ids) {
					index = getInventoryIndex(id);
					if (index == -1)
						continue;
					useItem(index);
					return random(1000, 1500);
				}
			}

			next_stage();
			return 0;
		}
		if (pickup.getState()) {
			int[] item = { -1, -1, -1 };
			if (big_netting) {
				item = getItemById(CASKET, UNOPENED_OYSTER);
			}
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
			if (!have_stage(STAGE_DEPOSIT) && !have_stage(STAGE_CERT)) {
				int eat = getInventoryIndex(cooked_ids);
				if (eat != -1) {
					useItem(eat);
					return random(600, 800);
				}
			}
			if (pickup.getState() && getInventoryCount() < MAX_INV_SIZE) {
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
			int inv_num = getInventoryCount(10);
			if (inv_num > 0) {
				deposit(10, inv_num);
			}
			for (int i = 0; i < bank_ids.length; ++i) {
				int id = bank_ids[i];
				if (big_netting && inArray(bass, id) && have_stage(STAGE_CERT)) {
					continue;
				}
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

	private int access_certer() {
		if (menu_time != -1L) {
			long current_time = System.currentTimeMillis();
			if (current_time > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(200, 300);
		}
		int[] npc = getNpcByIdNotTalk(CERTER);
		if (npc[0] != -1) {
			talkToNpc(npc[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	private int cert() {
		int cert_input_id = stages.get(stage).cert_input_id;
		int item_count = getInventoryCount(cert_input_id);
		if (item_count < 5) {
			next_stage();
			return 0;
		}
		if (!isQuestMenu()) {
			return access_certer();
		}
		menu_time = -1L;
		int count = questMenuCount();
		for (int i = 0; i < count; ++i) {
			String str = getQuestMenuOption(i);
			str = str.toLowerCase(Locale.ENGLISH).trim();
			String cert_name = stages.get(stage).cert_name;
			if (str.contains("fish to trade in") || str.equals(cert_name)) {
				answer(i);
				menu_time = System.currentTimeMillis();
				return random(600, 800);
			}
			if (str.contains("five")) {
				if (item_count >= 25) {
					answer(4);
				} else if (item_count >= 20) {
					answer(3);
				} else if (item_count >= 15) {
					answer(2);
				} else if (item_count >= 10) {
					answer(1);
				} else {
					answer(0);
				}
				return random(1000, 1500);
			}
		}
		return 0;
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
		int[] npc = getNpcByIdNotTalk(HARRY, ARHEIN);
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
			if (!Arrays.equals(Arrays.copyOf(bank_ids, raw_ids.length), raw_ids)) {
				withdraw_ids = raw_ids;
			} else {
				withdraw_ids = cooked_ids;
			}
			for (int id : withdraw_ids) {
				int count = bankCount(id);
				if (count == 0)
					continue;
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

	private int uncert() {
		int cert_input_id = stages.get(stage).cert_input_id;
		int cert_count = getInventoryCount(cert_input_id);
		if (cert_count == 0) {
			System.out.println("Out of certs");
			setAutoLogin(false);
			stopScript();
			return 0;
		}
		int empty = getEmptySlots();
		if (empty < 5) {
			next_stage();
			return 0;
		}
		if (!isQuestMenu()) {
			return access_certer();
		}
		menu_time = -1L;
		int count = questMenuCount();
		for (int i = 0; i < count; ++i) {
			String str = getQuestMenuOption(i);
			str = str.toLowerCase(Locale.ENGLISH).trim();
			String cert_name = stages.get(stage).cert_name;
			if (str.contains("certificates to trade") || str.equals(cert_name)) {
				answer(i);
				menu_time = System.currentTimeMillis();
				return random(600, 800);
			}
			if (str.contains("five")) {
				if (empty >= 25 && cert_count >= 5) {
					answer(4);
				} else if (empty >= 20 && cert_count >= 4) {
					answer(3);
				} else if (empty >= 15 && cert_count >= 3) {
					answer(2);
				} else if (empty >= 10 && cert_count >= 2) {
					answer(1);
				} else {
					answer(0);
				}
				return random(1000, 1500);
			}
		}
		return 0;
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
			if (getAllNpcById(HARRY, ARHEIN)[0] == ARHEIN) {
				buy_ids = cooked_ids;
			} else {
				buy_ids = raw_ids;
			}
			for (int id : buy_ids) {
				int index = getShopItemById(id);
				if (index == -1)
					continue;
				int amount = getShopItemAmount(index);
				if (amount == 0)
					continue;
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
				if (inArray(bass, id) && big_netting && have_stage(STAGE_CERT)) {
					continue;
				}
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
		case STAGE_CERT:
			if (getInventoryCount(s.cert_input_id) < 5) {
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

		final Checkbox fish = new Checkbox("Fish raw fish", acquire_group, true);
		acquire_boxes.add(fish);

		final Checkbox uncert = new Checkbox("Uncert raw fish (sharks/bass only)", acquire_group, false);
		acquire_boxes.add(uncert);
		uncert.setEnabled(false);

		final Checkbox withdraw = new Checkbox("Withdraw raw fish", acquire_group, false);
		acquire_boxes.add(withdraw);

		final Checkbox buy_raw = new Checkbox("Buy raw fish (Harry's Fishing Shack)", acquire_group, false);
		acquire_boxes.add(buy_raw);

		final Checkbox buy_cooked = new Checkbox("Buy cooked fish (Arhein's General Store)", acquire_group, false);
		acquire_boxes.add(buy_cooked);

		final CheckboxGroup dispose_group = new CheckboxGroup();
		final List<Checkbox> dispose_boxes = new ArrayList<>();

		final Checkbox deposit = new Checkbox("Deposit fish", dispose_group, true);
		dispose_boxes.add(deposit);

		final Checkbox cert = new Checkbox("Cert fish (sharks/bass only)", dispose_group, false);
		dispose_boxes.add(cert);
		cert.setEnabled(false);

		final Checkbox sell_cooked = new Checkbox("Sell cooked fish (Arhein's General Store)", dispose_group, false);
		dispose_boxes.add(sell_cooked);
		sell_cooked.setEnabled(false);

		final Checkbox sell_raw = new Checkbox("Sell raw fish (Harry's Fishing Shack)", dispose_group, false);
		dispose_boxes.add(sell_raw);

		final Checkbox power = new Checkbox("Power fish or eat cooked fish", dispose_group, false);
		dispose_boxes.add(power);

		final CheckboxGroup big_net_specials = new CheckboxGroup();

		final Checkbox drop_raw_non_bass = new Checkbox("Special: Exclude non-bass fish / drop after fishing",
				big_net_specials, false);

		final Checkbox sell_raw_non_bass = new Checkbox("Special: Sell raw non-bass fish", big_net_specials, false);

		final Checkbox sell_cooked_non_bass = new Checkbox("Special: Sell cooked non-bass fish", big_net_specials,
				false);
		sell_cooked_non_bass.setEnabled(false);

		pickup = new Checkbox("Pick up raw fish", false);

		final Checkbox cook = new Checkbox("Cook fish", false);
		cook.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				acquire_group.setSelectedCheckbox(fish);
				acquire_box_listener
						.itemStateChanged(new ItemEvent(fish, ItemEvent.ITEM_FIRST, fish, ItemEvent.SELECTED));

				dispose_group.setSelectedCheckbox(deposit);
				dispose_box_listener
						.itemStateChanged(new ItemEvent(deposit, ItemEvent.ITEM_FIRST, deposit, ItemEvent.SELECTED));

				int change = e.getStateChange();
				if (change == ItemEvent.SELECTED) {
					sell_cooked_non_bass.setEnabled(true);
					sell_cooked.setEnabled(true);
					buy_cooked.setEnabled(false);
					power.setEnabled(true);
				} else {
					sell_cooked_non_bass.setEnabled(false);
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
						.itemStateChanged(new ItemEvent(deposit, ItemEvent.ITEM_FIRST, deposit, ItemEvent.SELECTED));

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
					sell_raw_non_bass.setEnabled(false);
					sell_raw.setEnabled(false);
					cook.setEnabled(true);
				} else if (e.getSource() == buy_cooked) {
					sell_raw_non_bass.setEnabled(false);
					sell_raw.setEnabled(false);
					cook.setEnabled(false);
				} else if (e.getSource() == uncert) {
					sell_raw_non_bass.setEnabled(false);
					sell_raw.setEnabled(true);
					cook.setEnabled(true);
				} else {
					sell_raw_non_bass.setEnabled(true);
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
				if (non_keepies.contains(e.getSource()) && e.getStateChange() == ItemEvent.SELECTED) {
					sell_raw_non_bass.setEnabled(false);
					sell_cooked_non_bass.setEnabled(false);
					drop_raw_non_bass.setEnabled(false);
					big_net_specials.setSelectedCheckbox(null);
					return;
				}
				sell_raw_non_bass.setEnabled(true);
				drop_raw_non_bass.setEnabled(true);
				if (cook.getState()) {
					sell_cooked_non_bass.setEnabled(true);
				}
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

		Label acquire_label = new Label("Fish acquisition method", Label.CENTER);
		acquire_label.setFont(bold_title);

		Label dispose_label = new Label("Fish disposition method", Label.CENTER);
		dispose_label.setFont(bold_title);

		final Label space_saver_a = new Label();
		final Label space_saver_b = new Label();
		final Label space_saver_c = new Label();

		checkboxes.add(cs_panel);
		checkboxes.add(cook);
		checkboxes.add(pickup);

		checkboxes.add(acquire_label);
		checkboxes.add(uncert);
		checkboxes.add(fish);
		checkboxes.add(withdraw);
		checkboxes.add(buy_raw);
		checkboxes.add(buy_cooked);

		checkboxes.add(dispose_label);
		checkboxes.add(cert);
		checkboxes.add(deposit);
		checkboxes.add(power);
		checkboxes.add(sell_raw);
		checkboxes.add(sell_cooked);

		checkboxes.add(space_saver_b);
		checkboxes.add(space_saver_c);

		final java.awt.List list = new java.awt.List();
		list.add("Big net");
		list.select(0);
		list.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				checkboxes.invalidate();
				checkboxes.remove(sell_raw_non_bass);
				checkboxes.remove(sell_cooked_non_bass);
				checkboxes.remove(drop_raw_non_bass);
				checkboxes.remove(space_saver_a);
				checkboxes.remove(space_saver_b);
				checkboxes.remove(space_saver_c);
				uncert.setEnabled(false);
				cert.setEnabled(false);
				pickup.setEnabled(true);
				if (cook.getState()) {
					sell_cooked.setEnabled(true);
				}
				big_netting = true;

				acquire_group.setSelectedCheckbox(fish);
				acquire_box_listener
						.itemStateChanged(new ItemEvent(fish, ItemEvent.ITEM_FIRST, fish, ItemEvent.SELECTED));

				dispose_group.setSelectedCheckbox(deposit);
				dispose_box_listener
						.itemStateChanged(new ItemEvent(deposit, ItemEvent.ITEM_FIRST, deposit, ItemEvent.SELECTED));

				switch (list.getSelectedItem()) {
				case "Big net":
					checkboxes.add(drop_raw_non_bass);
					checkboxes.add(sell_raw_non_bass);
					checkboxes.add(sell_cooked_non_bass);
					sell_cooked.setEnabled(false);
					uncert.setEnabled(true);
					cert.setEnabled(true);
					pickup.setState(true);
					pickup.setEnabled(false);
					big_netting = true;
					checkboxes.validate();
					return;
				}
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
				case "Big net":
					fish_cursor = 0;
					fish_x = 406;
					fish_y = 505;
					click1 = true;
					if (drop_raw_non_bass.getState()) {
						raw_ids = new int[] { 554 };
						cooked_ids = new int[] { 555 };
						burnt_ids = new int[] { 368 };
						discard_ids = new int[] { 552, 550, 16, 17, 622 };
					} else {
						raw_ids = new int[] { 552, 550, 554 };
						cooked_ids = new int[] { 553, 551, 555 };
						burnt_ids = new int[] { 360, 365, 368 };
						discard_ids = new int[] { 16, 17, 622 };
					}
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
				} else if (uncert.getState()) {
					Stage s = new Stage(STAGE_UNCERT);
					stages.add(s);
					start_x = x = CERTER_X;
					start_y = y = CERTER_Y;
					if (big_netting) {
						s.cert_name = "raw bass";
						s.cert_input_id = 629;
					}
				} else if (withdraw.getState()) {
					stages.add(new Stage(STAGE_WITHDRAW));
					start_x = x = BANK_X;
					start_y = y = BANK_Y;
				} else if (buy_raw.getState()) {
					stages.add(new Stage(STAGE_BUY));
					start_x = x = HARRY_SHOP_X;
					start_y = y = HARRY_SHOP_Y;
				} else if (buy_cooked.getState()) {
					stages.add(new Stage(STAGE_BUY));
					start_x = x = ARHEIN_SHOP_X;
					start_y = y = ARHEIN_SHOP_Y;
				} else {
					throw new RuntimeException();
				}
				if (sell_raw_non_bass.getState()) {
					do_not_sell = Arrays.copyOf(non_fish_net_loot, non_fish_net_loot.length + 2);
					do_not_sell[do_not_sell.length - 1] = 554;
					do_not_sell[do_not_sell.length - 2] = 555;
					stages.add(new Stage(STAGE_SELL, calc_path(x, y, HARRY_SHOP_X, HARRY_SHOP_Y)));
					x = HARRY_SHOP_X;
					y = HARRY_SHOP_Y;
				}
				if (cook.getState() || buy_cooked.getState()) {
					bank_ids = cooked_ids;
				} else {
					bank_ids = raw_ids;
				}
				if (cook.getState()) {
					stages.add(new Stage(STAGE_COOK, calc_path(x, y, COOK_X, COOK_Y)));
					x = COOK_X;
					y = COOK_Y;
				}
				if (sell_cooked_non_bass.getState()) {
					do_not_sell = Arrays.copyOf(non_fish_net_loot, non_fish_net_loot.length + 1);
					do_not_sell[do_not_sell.length - 1] = 555;
					stages.add(new Stage(STAGE_SELL, calc_path(x, y, ARHEIN_SHOP_X, ARHEIN_SHOP_Y)));
					x = ARHEIN_SHOP_X;
					y = ARHEIN_SHOP_Y;
				}
				if (do_not_sell == null) {
					do_not_sell = new int[] {};
				}
				if (big_netting) {
					int old_len = bank_ids.length;
					bank_ids = Arrays.copyOf(bank_ids, bank_ids.length + non_fish_net_loot.length);
					System.arraycopy(non_fish_net_loot, 0, bank_ids, old_len, non_fish_net_loot.length);
				}
				has_banked = new boolean[bank_ids.length];
				banked_count = new int[bank_ids.length];
				if (deposit.getState()) {
					stages.add(new Stage(STAGE_DEPOSIT, calc_path(x, y, BANK_X, BANK_Y)));
					if (stages.get(0).id != STAGE_WITHDRAW) {
						stages.get(0).path_to = calc_path(BANK_X, BANK_Y, start_x, start_y);
					}
				} else if (cert.getState()) {
					Stage s = new Stage(STAGE_CERT, calc_path(x, y, CERTER_X, CERTER_Y));
					boolean c = cook.getState() || buy_cooked.getState();
					if (big_netting) {
						s.cert_name = c ? "bass" : "raw bass";
						s.cert_input_id = c ? 555 : 554;
						stages.add(new Stage(STAGE_DEPOSIT, calc_path(CERTER_X, CERTER_Y, BANK_X, BANK_Y)));
						stages.get(0).path_to = calc_path(BANK_X, BANK_Y, start_x, start_y);
					} else {
						s.cert_name = c ? "shark" : "raw shark";
						s.cert_input_id = c ? 546 : 545;
						stages.get(0).path_to = calc_path(CERTER_X, CERTER_Y, start_x, start_y);
					}
					stages.add(s);
				} else if (sell_raw.getState()) {
					if (big_netting) {
						stages.add(new Stage(STAGE_DEPOSIT, calc_path(HARRY_SHOP_X, HARRY_SHOP_Y, BANK_X, BANK_Y)));
					}
					stages.add(new Stage(STAGE_SELL, calc_path(x, y, HARRY_SHOP_X, HARRY_SHOP_Y)));
					stages.get(0).path_to = calc_path(HARRY_SHOP_X, HARRY_SHOP_Y, start_x, start_y);
				} else if (sell_cooked.getState()) {
					stages.add(new Stage(STAGE_SELL, calc_path(x, y, ARHEIN_SHOP_X, ARHEIN_SHOP_Y)));
					stages.get(0).path_to = calc_path(ARHEIN_SHOP_X, ARHEIN_SHOP_Y, start_x, start_y);
				} else if (cook.getState()) {
					stages.get(0).path_to = calc_path(COOK_X, COOK_Y, start_x, start_y);
				}

				if (big_netting) {
					if (cert.getState()) {
						System.out.println(
								"NOTE: Big netting is a special case. When certing, everything (that doesn't get sold) other than bass will be banked.");
					} else if (sell_raw.getState()) {
						System.out.println(
								"NOTE: Big netting is a special case. When selling raw fish, casket loot and pearls (if any) will be banked.");
					}
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
		frame.addWindowListener(new StandardCloseHandler(frame, StandardCloseHandler.HIDE));
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
			if (s.id == id)
				return true;
		}
		return false;
	}
}
