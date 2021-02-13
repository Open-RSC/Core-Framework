import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Locale;

public final class S_ArdMinePickup extends Script {

	// in order of preference, diamonds first
	private static final int[] items = {
		157, 158, 159, 160, 151
	};

	private static final int
	BANK_X = 551,
	BANK_Y = 612,
	DEST_X = 616,
	DEST_Y = 653;

	private final DecimalFormat ifmt = new DecimalFormat("#,##0");
	private final HashMap<Integer, Integer> gained = new HashMap<>();
	private int last_inv_count;
	private long bank_time;
	private long menu_time;
	private long start_time;
	private final PathWalker pw;
	private PathWalker.Path to_bank;
	private PathWalker.Path from_bank;

	public S_ArdMinePickup(Extension ex)
	{
		super(ex);
		pw = new PathWalker(ex);
	}

	@Override
	public void init(String params)
	{
		bank_time = menu_time = start_time = -1L;
		last_inv_count = -1;
		pw.init(null);
		to_bank = pw.calcPath(DEST_X, DEST_Y, BANK_X, BANK_Y);
		from_bank = pw.calcPath(BANK_X, BANK_Y, DEST_X, DEST_Y);
	}

	@Override
	public int main()
	{
		if (start_time == -1L) {
			start_time = System.currentTimeMillis();
			gained.clear();
		} else {
			int count = getInventoryCount();
			if (count > 0 && count > last_inv_count) {
				int id = getInventoryId(count - 1);
				Integer collected = gained.get(id);
				if (collected != null) {
					gained.put(id, collected + 1);
				} else {
					gained.put(id, 1);
				}
			}
			last_inv_count = count;
		}

		if (isBanking()) {
			bank_time = -1L;
			for (int id : items) {
				int count = getInventoryCount(id);
				if (count > 0) {
					deposit(id, count);
					return random(600, 800);
				}
			}
			closeBank();
			pw.setPath(from_bank);
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

		if (_insideBank() && getInventoryCount() == MAX_INV_SIZE) {
			pw.setPath(null);
			int[] banker = getNpcByIdNotTalk(BANKERS);
			if (banker[0] != -1) {
				talkToNpc(banker[0]);
				menu_time = System.currentTimeMillis();
			}
			return random(600, 800);
		}

		if (pw.walkPath()) return 0;

		if (_insideBank() && getInventoryCount() != MAX_INV_SIZE) {
			pw.setPath(from_bank);
			return 0;
		}

		// not inside bank

		if (getInventoryCount() == MAX_INV_SIZE) {
			pw.setPath(to_bank);
			return 0;
		}

		for (int id : items) {
			int[] g = getItemById(id);
			if (distanceTo(DEST_X, DEST_Y, g[1], g[2]) > 15) {
				continue;
			}
			pickupItem(id, g[1], g[2]);
			break;
		}
		return random(600, 1000);
	}

	@Override
	public void paint()
	{
		int x = 310;
		int y = 45;

		drawString("S Ardougne Mine Pickup", x, y, 2, 0xFFFFFF);
		y += 15;
		for (int id : gained.keySet()) {
			int count = gained.get(id);
			drawString(String.format("%s gained: %s (%s/h)",
				getItemNameId(id), ifmt.format(count), per_hour(count)),
				x, y, 2, 0xFFFFFF);
			y += 15;
		}
	}

	@Override
	public void onServerMessage(String str)
	{
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		}
	}

	private String per_hour(int count)
	{
		if (count == 0) return "0";
		double amount = count * 60.0 * 60.0;
		double secs = (System.currentTimeMillis() - start_time) / 1000.0;
		return ifmt.format((long) (amount / secs));
	}

	private boolean _insideBank()
	{
		return getX() <= 554 && getX() >= 551 &&
			getY() <= 616 && getY() >= 609;
	}
}