import java.util.Locale;

public final class BankDepositAll extends Script {

	private long bank_time;
	private long menu_time;

	public BankDepositAll(Extension ex) {
		super(ex);
	}

	@Override
	public void init(String params) {
		bank_time = menu_time = -1L;
	}

	@Override
	public int main() {
		if (isQuestMenu()) {
			menu_time = -1L;
			answer(0);
			bank_time = System.currentTimeMillis();
			return random(600, 800);
		} else if (menu_time != -1L) {
			if (System.currentTimeMillis() >= (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(300, 400);
		}

		if (isBanking()) {
			if (getInventoryCount() > 0) {
				int id = getInventoryId(0);
				int count = getInventoryCount(id);
				deposit(id, count);
				return random(800, 1200);
			}
			stopScript();
			return 0;
		} else if (bank_time != -1L) {
			if (System.currentTimeMillis() >= (bank_time + 8000L)) {
				bank_time = -1L;
			}
			return random(300, 400);
		}

		int[] banker = getNpcByIdNotTalk(BANKERS);
		if (banker[0] != -1) {
			talkToNpc(banker[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	@Override
	public void onServerMessage(String str) {
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		}
	}
}
