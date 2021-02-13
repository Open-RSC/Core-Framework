import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/*
 * To use this utiliy:
 *
 * Insert "StateTracker.tick(this);" under paint() in scripts.
 * Insert "StateTracker.messageReceived(this, str);" under onServerMessage in scripts.
 */
public final class StateTracker {

	public static final int NSKILLS = 18;
	public static final int INVSIZE = 30;
	public static final int[] last_skills = new int[NSKILLS];
	public static final double[] last_xp = new double[NSKILLS];
	public static final int[] last_items = new int[INVSIZE];
	public static final int[] last_stacks = new int[INVSIZE];
	public static int last_inv_count = 0;
	public static double last_fatigue = 0;
	public static FileOutputStream out = null;
	public static String old_name = "";
	public static Script old_script = null;

	private static void log(Script s, String str, Object... args)
	{
		String name = s.getPlayerName(0);
		if (name == null) return;
		if (!s.equals(old_script)) {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				out = null;
			}
			old_script = s;
		}
		if (!name.equals(old_name)) {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
				out = null;
			}
			old_name = name;
			for (int i = 0; i < NSKILLS; ++i) {
				last_skills[i] = s.getCurrentLevel(i);
				last_xp[i] = s.getAccurateXpForLevel(i);
			}
			int count = s.getInventoryCount();
			for (int i = 0; i < count; ++i) {
				last_items[i] = s.getInventoryId(i);
				last_stacks[i] = s.getInventoryStack(i);
			}
			last_inv_count = count;
			last_fatigue = s.getAccurateFatigue();
		}
		if (out == null) {
			String filename = String.format("log-%s-%s.txt",
				s.toString().replace(' ', '_'),
				name.replace('\0', ' '));
			try {
				out = new FileOutputStream(filename, true);
			} catch (IOException e) {
				System.err.printf("Error opening file for writing %s\n", filename);
			}
			if (out == null) return;
			System.err.printf("Opened %s for writing\n", filename);
		}
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		str = String.format(str, args);
		str = String.format("[%s UTC] %s\n", df.format(new Date()), str);
		try {
			out.write(str.getBytes("UTF-8"));
			out.flush();
		} catch (IOException e) {
			System.err.printf("Error writing to log\n");
		}
	}

	public static void messageReceived(Script s, String message)
	{
		log(s, "MESSAGE %s", message);
	}

	public static void tick(Script s)
	{
		for (int i = 0; i < NSKILLS; ++i) {
			if (last_skills[i] != s.getCurrentLevel(i)) {
				log(s, "SKILL_TEMP_LEVEL_CHANGED_BY %s,%d",
					Script.SKILL[i],
					s.getCurrentLevel(i) - last_skills[i]);
				last_skills[i] = s.getCurrentLevel(i);
			}
			if (s.getAccurateXpForLevel(i) > last_xp[i]) {
				log(s, "SKILL_XP_CHANGED_BY %s,%f",
					Script.SKILL[i],
					s.getAccurateXpForLevel(i) - last_xp[i]);
				last_xp[i] = s.getAccurateXpForLevel(i);
			}
		}
		if (s.getAccurateFatigue() > last_fatigue) {
			log(s, "FATIGUE_CHANGED_BY %f",
				s.getAccurateFatigue() - last_fatigue);
			last_fatigue = s.getAccurateFatigue();
		}
		int count = s.getInventoryCount();
		if (count < last_inv_count) {
			/* Inventory smaller */
			for (int i = (count - 1); i < last_inv_count; ++i) {
				log(s, "INVENTORY_REMOVED %s",
					s.getItemNameId(last_items[i]));
			}
		} else if (count > last_inv_count) {
			/* Inventory bigger */
			for (int i = (last_inv_count - 1); i < count; ++i) {
				log(s, "INVENTORY_ADDED %s",
					s.getItemName(i));
			}
		} else {
			/* Inventory size same, check if any items are different */
			for (int i = 0; i < count; ++i) {
				if (s.getInventoryId(i) != last_items[i]) {
					log(s, "INVENTORY_REMOVED %s",
						s.getItemNameId(last_items[i]));
					log(s, "INVENTORY_ADDED %s",
						s.getItemName(i));
				}
			}
		}
		for (int i = 0; i < last_inv_count; ++i) {
			/* Check stacks */
			int index = s.getInventoryIndex(last_items[i]);
			if (index == -1) continue;
			int stack = s.getInventoryStack(index);
			if (last_stacks[i] != stack) {
				log(s, "STACK_CHANGED_BY %s,%d",
					s.getItemNameId(last_items[i]),
					stack - last_stacks[i]);
				last_stacks[i] = stack;
			}
		}
		for (int i = 0; i < count; ++i) {
			last_items[i] = s.getInventoryId(i);
			last_stacks[i] = s.getInventoryStack(i);
		}
		last_inv_count = count;
	}
}