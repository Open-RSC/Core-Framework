import java.util.Locale;

public final class S_CatherbyBuyer extends Script {

    private static final int SHOPKEEPER = 280;
    private static final int DOORS = 64;
    private static final int COINS = 10;
    private int[] items;
    private int[] item_counts;
    private long bank_time;
    private long shop_time;
    private long start_time;
    private long menu_time;
    private boolean banked;

    public S_CatherbyBuyer(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
       try {
           if (params.isEmpty()) {
               throw new Exception();
           }
           String[] split = params.split(",");
           items = new int[split.length];
           item_counts = new int[split.length];
           for (int i = 0; i < split.length; ++i) {
               items[i] = Integer.parseInt(split[i]);
           }
       } catch (Throwable t) {
           System.out.println("Error parsing parameters id1,id2,id3");
       }
       start_time = -1L;
       bank_time = -1L;
       shop_time = -1L;
       menu_time = -1L;
       banked = false;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            menu_time = -1L;
            if (questMenuOptions()[0].contains("access")) {
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
        if (isBanking()) {
            bank_time = -1L;
            int inv_num;
            for(int i=0; i<items.length; i++) {
            	inv_num = getInventoryCount(items[i]);
            	if (inv_num > 0) {
            		deposit(items[i], inv_num);
            		return random(600, 800);
            	}
            }
            if (!hasInventoryItem(COINS)) {
                int bankc = bankCount(COINS);
                if(bankc <= 0) {
                	System.out.println("ERROR: Out of coins!");
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                else {
                	withdraw(COINS, Math.min(100000, bankc));
                	return random(600, 800);
                }
            }
            banked = false;
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (getInventoryCount() == MAX_INV_SIZE) {
			if (isShopOpen()) {
				closeShop();
			}
			return talk_to(BANKERS);
		}
        if (isShopOpen()) {
            shop_time = -1L;
            for (int id : items) {
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
        } else if (shop_time != -1L) {
            if (System.currentTimeMillis() >= (shop_time + 8000L)) {
                shop_time = -1L;
            }
            return random(300, 400);
        }
        if (getY() < 497) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                return talk_to(BANKERS);
            }
            if (check_doors()) {
                return random(1000, 2000);
            }
            return talk_to(SHOPKEEPER);
        } else {
            if (getInventoryCount() < MAX_INV_SIZE) {
                return talk_to(SHOPKEEPER);
            }
            if (check_doors()) {
                return random(1000, 2000);
            }
            return talk_to(BANKERS);
        }
    }
    
    private boolean check_doors() {
        int[] o = getObjectById(DOORS);
        if (o[0] == -1) return false;
        atObject(o[1], o[2]);
        return true;
    }
    
    private int talk_to(int... ids) {
        int[] npc = getNpcByIdNotTalk(ids);
        if (npc[0] != -1) {
            talkToNpc(npc[0]);
            menu_time = System.currentTimeMillis();
        }
        return random(600, 1000);
    }
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        int y = 25;
        drawString("S CatherbyBuyer", 25, y, 1, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), 25, y, 1, white);
        y += 15;
        int num = items.length;
        for (int i = 0; i < num; ++i) {
            if (item_counts[i] <= 0) {
                continue;
            }
            drawString("Deposited " + item_counts[i] + " " + getItemNameId(items[i]),
                25, y, 1, white);
            y += 15;
        }
    }
}
