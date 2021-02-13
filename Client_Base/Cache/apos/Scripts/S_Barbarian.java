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
import java.util.List;
import java.util.ArrayList;
import java.text.DecimalFormat;
import com.aposbot.StandardCloseHandler;
import com.aposbot.Constants;

public final class S_Barbarian extends Script {

	private static final int ID_TINDERBOX = 166;
    private static final int ID_FIRE = 97;
    private static final int ID_LOGS = 14;
    private static final int ID_TREE = 0;
    private static final int[] ids_axe = {
        12, 87, 88, 203, 204, 405
    };
	
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
			default:
				return "Invalid";
			}
		}
	}
	
	// id of the possible combined fishes
		public static final int[][] fish_spots_ids = {
				// trout/salmon
				{358, 356},
				// pike
				{363}
		};
			
		public static final String[][] fish_spots_names = {
				{"trout", "salmon"},
				{"pike"}
		};
		
		public static final String[] combined_fish_spots = {
				"Trout/salmon",
				"Pike"
		};
		
		public static int fish_cursor;
			
		// fish spots shared on at most 2
		public static final long[] fish_success = new long[2];
		public static final long[] cook_success = new long[2];
		public static final long[] cook_failure = new long[2];
		private static final int LEVEL_COOKING = 7;
		private static final int LEVEL_FISHING = 10;
		private static final int LEVEL_WOODCUTTING = 8;
		private static final int LEVEL_FIREMAKING = 11;

	private static final int STAGE_FISH	= 0;
	private static final int STAGE_COOK	= 1;

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	private long start_time;
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
	
	private FileWriter fishcsv;
	private FileWriter cookcsv;
	private FileWriter woodcsv;
	private FileWriter firecsv;
	
	private long cur_logs;
	private long cur_logsburn;
	private long cur_attempts;
	private long cur_cattempts;
	private long cur_wcattempts;
	private long cur_fmattempts;

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

	public S_Barbarian(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
		
		try{
			String fileName	= "fishing_exp.csv";
			boolean fileCreated = false;
			File createCsv = new File(fileName);
			if (!createCsv.exists())
			{
				createCsv.createNewFile();
				fileCreated = true;
			}

			fishcsv	= new FileWriter(createCsv, true);
			if(fileCreated)
			{
				// barbarian just gets max 2 fish but big netting does 3 since both may save to same
				// file i add dummy third column so csv readers dont have troubles
				fishcsv.write("fish_spot,level,attempts,fish1_count,fish2_count,fish3_count\n");
				fishcsv.flush();
			}
			
			fileName = "cooking_exp.csv";
			fileCreated = false;
			createCsv = new File(fileName);
			if (!createCsv.exists())
			{
				createCsv.createNewFile();
				fileCreated = true;
			}

			cookcsv	= new FileWriter(createCsv, true);
			if(fileCreated)
			{
				cookcsv.write("raw_fish_id,fish_name,level,attempts,success,fails\n");
				cookcsv.flush();
			}
			
			fileName	= "woodcutting_exp.csv";
			fileCreated = false;
			createCsv = new File(fileName);
			if (!createCsv.exists())
			{
				createCsv.createNewFile();
				fileCreated = true;
			}

			woodcsv	= new FileWriter(createCsv, true);
			if(fileCreated)
			{
				woodcsv.write("tree_id,level,attempts,log_count,fail_count\n");
				woodcsv.flush();
			}
			
			fileName	= "firemaking_exp.csv";
			fileCreated = false;
			createCsv = new File(fileName);
			if (!createCsv.exists())
			{
				createCsv.createNewFile();
				fileCreated = true;
			}

			// altough in classic only regular logs can be burned just for file consistency i add it
			firecsv	= new FileWriter(createCsv, true);
			if(fileCreated)
			{
				firecsv.write("log_type,level,attempts,success_count,fail_count\n");
				firecsv.flush();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] argv) {
		S_Barbarian s = new S_Barbarian(null);
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
		}
		stopScript();
		throw new RuntimeException("invalid stage");
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("fishing bait")) {
			System.out.println("Out of bait");
			setAutoLogin(false);
			stopScript();
		} else if (str.contains("feather")) {
			System.out.println("Out of feathers");
			setAutoLogin(false);
			stopScript();
		}
		// jump to drop here since bank is far
		else if(str.contains("need a cooking level")) {
			int id;
			boolean dropped = false;
			if(raw_ids.length > 1) {
				id = raw_ids[1];
				int index = getInventoryIndex(id);
				if (index != -1) {
					dropItem(index);
					dropped = true;
					random(1000, 1500);
				}
			}
			if(!dropped) {
				id = raw_ids[0];
				int index = getInventoryIndex(id);
				if (index != -1) {
					dropItem(index);
					dropped = true;
					random(1000, 1500);
				}
			}
		}
		else if (str.contains("tired")) {
			should_sleep = true;
		} else if (str.contains("you catch")) {
			click_time = -1L;
			String fish;
			for(int i=0; i<fish_spots_names[fish_cursor].length; i++) {
				fish = fish_spots_names[fish_cursor][i];
				if (str.contains(fish)) {
					++fish_success[i];
					break;
				}
			}
			++cur_fish_success;
			++total_fish_success;
			++cur_attempts;
			
			if(cur_attempts >= 1000)
				_fishCsvOut();
		} else if (str.contains("you fail to catch")) {
			click_time = -1L;
			++cur_fish_fails;
			++total_fish_fails;
			++cur_attempts;
		} else if (str.contains("nicely cooked")) {
			click_time = -1L;
			String fish;
			for(int i=0; i<fish_spots_names[fish_cursor].length; i++) {
				fish = fish_spots_names[fish_cursor][i];
				if (str.contains(fish)) {
					++cook_success[i];
					break;
				}
			}
			++cur_cook_success;
			++total_cook_success;
			++cur_cattempts;
			
			if(cur_cattempts >= 1000)
				_cookCsvOut();
		} else if (str.contains("accidentally burn")) {
			click_time = -1L;
			String fish;
			for(int i=0; i<fish_spots_names[fish_cursor].length; i++) {
				fish = fish_spots_names[fish_cursor][i];
				if (str.contains(fish)) {
					++cook_failure[i];
					break;
				}
			}
			++cur_cook_fails;
			++total_cook_fails;
			++cur_cattempts;
		} else if (str.contains("slip and fail")) {
			click_time = -1L;
            // woodcut fail
            ++cur_wcattempts;
        } else if (str.contains("get some wood")) {
        	click_time = -1L;
        	++cur_logs;
            ++cur_wcattempts;
            
            // set to 35 since once the wc level is adequate for constant
            // failure rate of almost 0 on regular trees, it equals to about 1000 fish
            if(cur_wcattempts >= 35)
				_woodcutCsvOut();
        } else if (str.contains("fail to light")) {
        	click_time = -1L;
            // firemake fail
            ++cur_fmattempts;
        } else if (str.contains("logs begin to burn")) {
        	click_time = -1L;
        	++cur_logsburn;
            ++cur_fmattempts;
            
            // same here as wc logic
            if(cur_fmattempts >= 35)
				_firemakeCsvOut();
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
			_fishCsvOut();
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
			_cookCsvOut();
			cook_level_up_time = System.currentTimeMillis();
			cur_cook_success = 0;
			cur_cook_fails = 0;
		} else if (str.contains("advanced 1 woodcutting")) {
			_woodcutCsvOut();
		} else if (str.contains("advanced 1 firemaking")) {
			_firemakeCsvOut();
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
				default:
					System.out.printf("WARNING: Idle here! (%d,%d). Please report this!\n", getX(), getY());
					break loop;
				}
			} while (!isReachable(move_x, move_y) ||
			    (move_x == getX() && move_y == getY()));
		}
	}
	
	private void _fishCsvOut() {
		try{
			fishcsv.write(
				combined_fish_spots[fish_cursor] + "," +
				getLevel(LEVEL_FISHING) + "," +
				cur_attempts + "," +
				fish_success[0] + "," + fish_success[1] + "," + "0" + "\n"
			);
			fishcsv.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		cur_attempts = 0;
		fish_success[0] = fish_success[1] = 0;
    }
	
	private void _cookCsvOut() {
		try{
			cookcsv.write(
					fish_spots_ids[fish_cursor][0] + "," +
					fish_spots_names[fish_cursor][0] + "," +
					getLevel(LEVEL_COOKING) + "," +
					(cook_success[0]+cook_failure[0]) + "," +
					cook_success[0] + "," + cook_failure[0] + "\n"
				);
				if(fish_spots_ids[fish_cursor].length > 1) {
					cookcsv.write(
							fish_spots_ids[fish_cursor][1] + "," +
							fish_spots_names[fish_cursor][1] + "," +
							getLevel(LEVEL_COOKING) + "," +
							(cook_success[1]+cook_failure[1]) + "," +
							cook_success[1] + "," + cook_failure[1] + "\n"
						);
				}
			cookcsv.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		cur_cattempts = 0;
		cook_success[0] = cook_success[1] = 0;
		cook_failure[0] = cook_failure[1] = 0;
    }
	
	private void _woodcutCsvOut() {
		try{
			woodcsv.write(
					"\"Normal\"," +
					getLevel(LEVEL_WOODCUTTING) + "," +
					cur_wcattempts + "," +
					cur_logs + "," +
					(cur_wcattempts-cur_logs) + "\n"
			);
			woodcsv.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		cur_wcattempts = 0;
		cur_logs = 0;
    }
	
	private void _firemakeCsvOut() {
		try{
			firecsv.write(
					"\"Normal\"," +
					getLevel(LEVEL_FIREMAKING) + "," +
					cur_fmattempts + "," +
					cur_logsburn + "," +
					(cur_fmattempts-cur_logsburn) + "\n"
			);
			firecsv.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		cur_fmattempts = 0;
		cur_logsburn = 0;
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
		drawString("Barbarian Script - @whi@" +
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
					"Successful fishing attempts: %s (%s/h), %s (%s/h), %s (%s/h)",
				    iformat.format(cur_fish_success),
				    per_hour(cur_fish_success, fish_level_up_time),
				    iformat.format(fish_success[0]),
				    per_hour(fish_success[0], fish_level_up_time),
				    iformat.format(fish_success[1]),
				    per_hour(fish_success[1], fish_level_up_time)),
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
					"Successful cooking attempts: %s (%s/h), %s (%s/h), %s (%s/h)",
				    iformat.format(cur_cook_success),
				    per_hour(cur_cook_success, cook_level_up_time),
				    iformat.format(cook_success[0]),
				    per_hour(cook_success[0], cook_level_up_time),
				    iformat.format(cook_success[1]),
				    per_hour(cook_success[1], cook_level_up_time)),
				    x + 10, y, 2, white);
			y += 15;
			drawString(String.format(
					"Failed cooking attempts: %s (%s/h), %s (%s/h), %s (%s/h)",
				    iformat.format(cur_cook_fails),
				    per_hour(cur_cook_fails, cook_level_up_time),
				    iformat.format(cook_failure[0]),
				    per_hour(cook_failure[0], cook_level_up_time),
				    iformat.format(cook_failure[1]),
				    per_hour(cook_failure[1], cook_level_up_time)),
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

		click_time = -1L;

		cur_fish_success = 0;
		cur_fish_fails = 0;
		total_fish_success = 0;
		total_fish_fails = 0;
		fishing_levels = 0;
		
		fish_success[0] = fish_success[1] = 0;

		cur_cook_success = 0;
		cur_cook_fails = 0;
		total_cook_success = 0;
		total_cook_fails = 0;
		cooking_levels = 0;
		
		cook_success[0] = cook_success[1] = 0;
		cook_failure[0] = cook_failure[1] = 0;
		
		cur_logs = 0;
		cur_logsburn = 0;
		
		cur_attempts = 0;
		cur_cattempts = 0;
		cur_wcattempts = 0;
		cur_fmattempts = 0;

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
			if (true) {
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
		int id = -1;
        for (int i = 0; i < raw_ids.length; ++i) {
            if (getInventoryCount(raw_ids[i]) > 0) {
                id = raw_ids[i];    
                break;
            }
        }
        int x = getX();
        int y = getY();
        if (getObjectIdFromCoords(x, y) == ID_FIRE) {
        	useItemOnObject(id, x, y);
            return random(600, 800);
        }
        int logs[] = getItemById(ID_LOGS);
        if (logs[1] == x && logs[2] == y) {
            int box_slot = getInventoryIndex(ID_TINDERBOX);
            if (box_slot == -1) {
                System.out.println("ERROR: No tinderbox!");
                setAutoLogin(false);
                stopScript(); return 0;
            }
            useItemOnGroundItem(box_slot, ID_LOGS, x, y);
            return random(600, 800);
        }
        if (getX() < 223) {
            if (!isWalking()) {
                walkTo(224 + random(-1, 1), 486 + random(-1, 1));
            }
            return random(800, 1200);
        }
        if (getInventoryIndex(ids_axe) == -1) {
            System.out.println("ERROR: No axe!");
            setAutoLogin(false);
            stopScript(); return 0;
        }
        int tree[] = getObjectById(ID_TREE);
        if (distanceTo(tree[1], tree[2]) < 5) {
            atObject(tree[1], tree[2]);
            return random(1000, 1500);
        }
        return random(5, 10);
	}

	private void next_stage() {
		if (stage == (stages.size() - 1)) {
			stage = 0;
		} else {
			++stage;
		}

		Stage s = stages.get(stage);
		boolean found = false;

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

		final CheckboxGroup dispose_group = new CheckboxGroup();
		final List<Checkbox> dispose_boxes = new ArrayList<>();

		final Checkbox power = new Checkbox(
		    "Power fish or eat cooked fish",
		    dispose_group, true);
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

				int change = e.getStateChange();
				if (change == ItemEvent.SELECTED) {
					power.setEnabled(true);
				} else {
				}
			}
		});

		acquire_box_listener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				int change = e.getStateChange();
				if (change != ItemEvent.SELECTED) {
					return;
				}
				if (cook.getState() || e.getSource() == fish) {
					power.setEnabled(true);
				} else {
					power.setEnabled(false);
				}
			}
		};

		for (Checkbox c : acquire_boxes) {
			c.addItemListener(acquire_box_listener);
		}

		final List<Checkbox> non_keepies = new ArrayList<>(3);
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

		checkboxes.add(dispose_label);
		checkboxes.add(power);

		checkboxes.add(space_saver_b);
		checkboxes.add(space_saver_c);

		final java.awt.List list = new java.awt.List();
		list.add("Trout/salmon");
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

				acquire_group.setSelectedCheckbox(fish);
				acquire_box_listener
				    .itemStateChanged(new ItemEvent(fish,
				    ItemEvent.ITEM_FIRST, fish,
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
				case "Trout/salmon":
					fish_cursor = 0;
					fish_x = 208;
					fish_y = 501;
					click1 = true;
					raw_ids = new int[] { 358, 356 };
					cooked_ids = new int[] { 359, 357 };
					burnt_ids = new int[] { 360 };
					discard_ids = new int[] {};
					break;
				case "Pike":
					fish_cursor = 1;
					fish_x = 208;
					fish_y = 501;
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
				} else {
					throw new RuntimeException();
				}
				if (cook.getState()) {
					stages.add(new Stage(STAGE_COOK));
				}
				if (do_not_sell == null) {
					do_not_sell = new int[] {};
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
