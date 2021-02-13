import java.util.Locale;

public final class S_CatherbySeller extends Script {

    private static final int SHOPKEEPER = 280;
    private static final int DOORS = 64;
    private static final int COINS = 10;
    private int[] items;
    private int[] item_counts;
    private long bank_time;
    private long shop_time;
    private long start_time;
    private int ptr;
    private int coin_count;
    private long menu_time;
    private boolean banked;

    public S_CatherbySeller(Extension ex) {
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
       ptr = 0;
       coin_count = 0;
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
            int inv_num = getInventoryCount(COINS);
            if (inv_num > 0) {
                deposit(COINS, inv_num);
                if (!banked) {
                    coin_count += inv_num;
                    banked = true;
                }
                return random(600, 800);
            }
            if (!hasInventoryItem(items[ptr])) {
                int bankc = bankCount(items[ptr]);
                while (bankc <= 0) {
                    if (ptr >= (items.length - 1)) {
                        System.out.println("ERROR: Out of items! coins: " + coin_count);
                        stopScript(); setAutoLogin(false);
                        return 0;
                    }
                    bankc = bankCount(items[++ptr]);
                    System.out.println("Next item");
                }
                int w = getEmptySlots();
                if (bankc < w) w = bankc;
                withdraw(items[ptr], w);
                item_counts[ptr] += w;
                return random(600, 800);
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
        if (isShopOpen()) {
            shop_time = -1L;
            int id = items[ptr];
            int count = getInventoryCount(id);
            if (count > 0) {
                sellShopItem(getShopItemById(id), count);
                return random(1000, 2000);
            }
            closeShop();
            return random(600, 800);
        } else if (shop_time != -1L) {
            if (System.currentTimeMillis() >= (shop_time + 8000L)) {
                shop_time = -1L;
            }
            return random(300, 400);
        }
        if (getY() < 497) {
            if (!hasInventoryItem(items[ptr])) {
                return talk_to(BANKERS);
            }
            if (check_doors()) {
                return random(1000, 2000);
            }
            return talk_to(SHOPKEEPER);
        } else {
            if (hasInventoryItem(items[ptr])) {
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
        drawString("S CatherbySeller", 25, y, 1, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), 25, y, 1, white);
        y += 15;
        int num = items.length;
        for (int i = 0; i < num; ++i) {
            if (item_counts[i] <= 0) {
                continue;
            }
            drawString("Withdrawn " + item_counts[i] + " " + getItemNameId(items[i]),
                25, y, 1, white);
            y += 15;
        }
        drawString("Banked " + coin_count + " gp", 25, y, 1, white);
    }
}
