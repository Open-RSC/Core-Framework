import java.text.DecimalFormat;
import java.util.Locale;

public final class S_BankBury extends Script {

	private final DecimalFormat iformat = new DecimalFormat("#,##0");
	private static final int BONES = 814;
	private static final double XP = 60.0;
	private long menu_time;
	private long bank_time;
	private int bury_count;
	private long start_time;

	public S_BankBury(Extension ex) {
		super(ex);
	}

	@Override
	public void init(String params) {
		bank_time = -1L;
		menu_time = -1L;
		start_time = -1L;
		bury_count = 0;
	}

	@Override
	public int main() {
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
		}

		if (isBanking()) {
			int empty_slots = getEmptySlots();
			bank_time = -1L;
			if (empty_slots > 0) {
				withdraw(BONES, empty_slots);
			} else {
				closeBank();
			}
			return random(600, 800);
		} else if (bank_time != -1L) {
			if (System.currentTimeMillis() >= (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(300, 400);
		}

		if (isQuestMenu()) {
			menu_time = -1L;
			bank_time = System.currentTimeMillis();
			answer(0);
			return random(600, 800);
		} else if (menu_time != -1L) {
			if (System.currentTimeMillis() >= (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(300, 400);
		}

		if (getFatigue() > 90) {
			useSleepingBag();
			return random(1000, 2000);
		}

		int bones = getInventoryIndex(BONES);
		if (bones != -1) {
			useItem(bones);
			return random(400, 600);
		}

		int[] banker = getNpcByIdNotTalk(BANKERS);
		if (banker[0] != -1) {
			talkToNpc(banker[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	@Override
	public void paint() {
		final int orangey = 0xFFD900;
		final int white = 0xFFFFFF;
		int x = 25;
		int y = 25;
		drawString("S BankBury", x, y, 1, orangey);
		y += 15;
		drawString("Runtime: " + get_runtime(), x + 10, y, 1, white);
		y += 15;
		if (bury_count > 0) {
			int total_xp = (int) (bury_count * XP);
			drawString("Buried " + bury_count + " bones (" + total_xp + " xp)",
				x + 10, y, 1, white);
			y += 15;
			drawString(per_hour(total_xp) + " XP per hour", x + 10, y, 1, white);
			y += 15;
		}
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("bury")) {
			++bury_count;
		} else if (str.contains("busy")) {
			menu_time = -1L;
		}
	}

	private String per_hour(int total) {
		if (total <= 0 || start_time <= 0L) {
			return "0";
		}
		return iformat.format(((total * 60L) * 60L)
			/ ((System.currentTimeMillis() - start_time) / 1000L));
	}

	private String get_runtime() {
		long secs = ((System.currentTimeMillis() - start_time) / 1000L);
		if (secs >= 3600) {
			return (secs / 3600) + " hours, " + ((secs % 3600) / 60)
				+ " mins, " + (secs % 60) + " secs.";
		}
		if (secs >= 60) {
			return secs / 60 + " mins, " + (secs % 60) + " secs.";
		}
		return secs + " secs.";
	}
}