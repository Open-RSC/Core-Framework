import java.util.Arrays;
import java.util.Locale;

public final class S_HighAlcher extends Script {

    private String[] itm_name;
    private int[] itm_count;
    private int[] itm;
    private int ptr;
    private long bank_time;
    private int coin_count;
    private long start_time;
    private long menu_time;
    
    private static final int[] fire_staffs = {
        197, 615, 682
    };
    
    private static final int ID_SPELL = 28;
    private static final int ID_NATURE = 40; 
    private static final int ID_COINS = 10;

    public S_HighAlcher(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        try {
            if ("".equals(params)) throw new Exception();
            String[] split = params.split(",");
            int len = split.length;
            if (len == 0) throw new Exception();
            itm = new int[len];
            itm_name = new String[len];
            itm_count = new int[len];
            for (int i = 0; i < len; ++i) {
                itm[i] = Integer.parseInt(split[i]);
            }
        } catch (Throwable t) {
            System.out.println(
                    "Error parsing parameters. " +
                    "Example: itemid1,id2,id3...");
            return;
        }
        System.out.println(Arrays.toString(itm));
        bank_time = -1L;
        coin_count = 0;
        ptr = 0;
        start_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            menu_time = -1L;
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(1000, 2000);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            int inv_num = getInventoryCount(ID_COINS);
            if (inv_num > 0) {
                deposit(ID_COINS, inv_num);
                coin_count += inv_num;
                return random(600, 900);
            }
            inv_num = getInventoryCount(ID_NATURE);
            if (inv_num == 0) {
                // maintain bank order :)
                int bank_count = bankCount(ID_NATURE) - 1;
                if (bank_count <= 0) {
                    System.out.println("ERROR: Out of natures!");
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                int w = 100;
                if (bank_count < w) w = bank_count;
                withdraw(ID_NATURE, w);
                return random(1200, 1700);
            }
            if (getInventoryIndex(fire_staffs) == -1) {
                int len = fire_staffs.length;
                for (int i = 0; i < len; ++i) {
                    if (bankCount(fire_staffs[i]) > 0) {
                        withdraw(fire_staffs[i], 1);
                        return random(600, 900);
                    }
                }
                System.out.println("ERROR: No fire staffs!");
                stopScript(); setAutoLogin(false);
                return 0;
            }
            if (!hasInventoryItem(itm[ptr])) {
                int w = getEmptySlots();
                int bankc = bankCount(itm[ptr]);
                while (bankc <= 0) {
                    if (ptr >= (itm.length - 1)) {
                        System.out.println("ERROR: Out of items!");
                        stopScript(); setAutoLogin(false);
                        return 0;
                    }
                    bankc = bankCount(itm[++ptr]);
                    System.out.println("Next item");
                }
                if (w > bankc) w = bankc;
                withdraw(itm[ptr], w);
                itm_count[ptr] += w;
                return random(600, 900);
            }
            closeBank();
            return random(600, 900);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (getFatigue() > 90) {
            useSleepingBag();
            return random(2000, 3000);
        }
        
        int target = getInventoryIndex(itm[ptr]);
        int staff = getInventoryIndex(fire_staffs);
        if (target == -1 || staff == -1 ||
                !hasInventoryItem(ID_NATURE)) {
            
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                menu_time = System.currentTimeMillis();
                talkToNpc(banker[0]);
                return random(1000, 2000);
            }
            return random(100, 700);
        }
        if (itm_name[ptr] == null) {
            itm_name[ptr] = getItemName(target);
        }
        if (!isItemEquipped(staff)) {
            wearItem(staff);
            return random(1500, 2500);
        }
        castOnItem(ID_SPELL, target);
        return random(600, 800);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private String _getRuntime() {
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
    public void paint() {
        final int gray = 0xC4C4C4;
        int y = 25;
        drawString("Stormy's HighAlch", 25, y, 1, gray);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, gray);
        y += 15;
        int num = itm.length;
        for (int i = 0; i < num; ++i) {
            if (itm_count[i] <= 0) {
                continue;
            }
            String name = itm_name[i];
            if (name == null) name = "[" + itm[i] + "]";
            drawString("Withdrawn " + itm_count[i] + " " + itm_name[i],
                25, y, 1, gray);
            y += 15;
        }
        drawString("Banked " + coin_count + " gp", 25, y, 1, gray);
    }
}
