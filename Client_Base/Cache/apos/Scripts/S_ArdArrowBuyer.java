import java.util.Locale;

public final class S_ArdArrowBuyer extends Script {

	private long next_hop;
	private long menu_time;
	private long shop_time;

	public S_ArdArrowBuyer(Extension ex)
	{
		super(ex);
	}

	@Override
	public void init(String params)
	{
		next_hop = System.currentTimeMillis() + 20000L;
	}

	@Override
	public int main()
	{
		if (getFightMode() != 1) {
			setFightMode(1);
			return random(600, 800);
		}
		if (isShopOpen()) {
			shop_time = -1L;
			if (System.currentTimeMillis() < next_hop) {
				int i = getShopItemById(11);
				int count = getShopItemAmount(i);
				if (count > 0) {
					buyShopItem(i, count);
					return random(1500, 2500);
				}
			} else {
				closeShop();
			}
			return random(600, 800);
		} else if (shop_time != -1) {
			if (System.currentTimeMillis() > (shop_time + 8000L)) {
				shop_time = -1L;
			}
			return random(150, 250);
		}
		if (isQuestMenu()) {
			answer(1);
			menu_time = -1L;
			shop_time = System.currentTimeMillis();
			next_hop = System.currentTimeMillis() +
			    random(30000, 60000);
			return random(600, 800);
		} else if (menu_time != -1) {
			if (System.currentTimeMillis() > (menu_time + 8000L)) {
				menu_time = -1L;
			}
			return random(150, 250);
		}
		if (System.currentTimeMillis() > next_hop) {
			autohop(false);
			next_hop = System.currentTimeMillis() +
			    random(30000, 60000);
			return random(1000, 1500);
		}
		int[] npc = getNpcByIdNotTalk(661);
		if (npc[0] != -1) {
			talkToNpc(npc[0]);
			menu_time = System.currentTimeMillis();
		}
		return random(600, 800);
	}

	@Override
	public void paint()
	{
	}

	@Override
	public void onServerMessage(String str)
	{
		str = str.toLowerCase(Locale.ENGLISH);
		if (str.contains("busy")) {
			menu_time = -1L;
		} else if (str.startsWith("welcome")) {
			menu_time = -1L;
		}
	}
}
