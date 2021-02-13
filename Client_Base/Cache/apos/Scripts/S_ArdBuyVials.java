import java.text.DecimalFormat;
import java.util.Locale;

public final class S_ArdBuyVials extends Script {

	private final DecimalFormat iformat = new DecimalFormat("#,##0");
	private static final int VIAL = 464;
	private long bank_time;
	private long shop_time;
	private long menu_time;
	private long start_time;
	private boolean move;
	private final PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;
	private int banked;
	private int move_x;
	private int move_y;

	public S_ArdBuyVials(Extension ex) {
		super(ex);
		pw = new PathWalker(ex);
	}

	@Override
	public void init(String params) {
		final int BANK_X = 551;
		final int BANK_Y = 612;
		final int SHOP_X = 582;
		final int SHOP_Y = 602;
		pw.init(null);
		from_bank = pw.calcPath(BANK_X, BANK_Y, SHOP_X, SHOP_Y);
		to_bank = pw.calcPath(SHOP_X, SHOP_Y, BANK_X, BANK_Y);

		bank_time = -1L;
		shop_time = -1L;
		menu_time = -1L;
		start_time = -1L;
		move = false;
		banked = 0;
	}

	@Override
	public int main() {
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
		}

		if (isBanking()) {
			bank_time = -1L;
			int count = getInventoryCount(VIAL);
			if (count > 0) {
				deposit(VIAL, count);
			} else {
				banked += (MAX_INV_SIZE - getInventoryCount());
				closeBank();
				pw.setPath(from_bank);
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
			if (getX() < 555) {
				bank_time = System.currentTimeMillis();
			} else {
				shop_time = System.currentTimeMillis();
			}
			answer(0);
			return random(600, 800);
		} else if (menu_time != -1L) {
			if (System.currentTimeMillis() >= (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(300, 400);
		}

		if (isShopOpen()) {
			shop_time = -1L;
			if (move) {
				closeShop();
				return random(600, 800);
			}
			int empty = getEmptySlots();
			if (empty == 0) {
				closeShop();
				pw.setPath(to_bank);
				return random(600, 800);
			}
			int vial = getShopItemById(VIAL);
			if (vial == -1) {
				System.out.println("vial not found!");
				return random(1000, 2000);
			}
			int count = getShopItemAmount(vial);
			if (count > empty) {
				count = empty;
			}
			if (count > 0) {
				buyShopItem(vial, count);
			}
			return random(600, 800);
		} else if (shop_time != -1L) {
			if (System.currentTimeMillis() >= (shop_time + 8000L)) {
				shop_time = -1L;
			}
			return random(300, 400);
		}

		if (pw.walkPath()) return 0;

		if (move) {
			if (getX() != move_x || getY() != move_y) {
				move = false;
				return random(100, 200);
			}
			if (distanceTo(580, 600) > 15) {
				return _odd();
			}
			if (!isWalking()) {
				int x, y;
				do {
					x = 580 + random(-2, 2);
					y = 600 + random(-2, 2);
				} while (x == move_x && y == move_y);
				walkTo(x, y);
				return random(1000, 2000);
			}
			return random(100, 200);
		}

		if (getInventoryCount() == MAX_INV_SIZE) {
			return _talk(BANKERS);
		} else {
			return _talk(337, 336);
		}
	}

	@Override
	public void paint() {
		int x = 10;
		int y = 18;
		int count = getInventoryCount(VIAL) + banked;
		drawString(String.format(
			"S Filled Vial Buyer | Bought: %s (%s/h)",
			iformat.format(count), per_hour(count)),
			x, y, 2, 0xFFFFFF);
		y += 15;
		drawString("Runtime: " + get_runtime(), x, y, 2, 0xFFFFFF);
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		} else if (str.contains("standing")) {
			move = true;
			move_x = getX();
			move_y = getY();
		}
	}

	private int _talk(int... ids) {
		int[] npc = getNpcByIdNotTalk(ids);
		if (npc[0] != -1) {
			if (distanceTo(npc[1], npc[2]) > 15) {
				return _odd();
			}
			talkToNpc(npc[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	private int _odd() {
		System.out.println("something odd happened. i'm here: " +
			getX() + "," + getY());
		return random(1000, 2000);
	}

	private String per_hour(int count) {
		if (count == 0) return "0";
		double amount = count * 60.0 * 60.0;
		double secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return iformat.format((long) (amount / secs));
	}

	private String get_runtime() {
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