import java.util.Locale;

public final class S_Stitcher extends Script {
    
    private static final int[] items = {
        15, 17, 16
    };
    
    private static final String[] options = {
        "arm", "boot", "glove"
    };
    
    private static final int[] levels = {
        14, 7, 1
    };
    
    private static final int ID_THREAD = 43;
    private static final int ID_NEEDLE = 39;
    private static final int ID_LEATHER = 148;
    private static int SKILL_CRAFTING = -1;
    private long menu_time;
    private long bank_time;

    public S_Stitcher(Extension ex) {
        super(ex);
        for (int i = 0; i < SKILL.length; ++i) {
            if (SKILL[i].toLowerCase(Locale.ENGLISH).contains("craft")) {
                SKILL_CRAFTING = i;
                break;
            }
        }
    }
    
    @Override
    public void init(String params) {
        menu_time = -1L;
        bank_time = -1L;
    }

    @Override
    public int main() {
        if (isBanking()) {
            bank_time = -1L;
            for (int id : items) {
                int count = getInventoryCount(id);
                if (count > 0) {
                    deposit(id, count);
                    return random(1000, 2000);
                }
            }
            if (getInventoryCount(ID_LEATHER) == 0) {
                int count = bankCount(ID_LEATHER);
                if (count == 0) {
                    System.out.println("ERROR: Out of leather!");
                    stopScript();
                    setAutoLogin(false);
                    return 0;
                }
                int w = getEmptySlots();
                if (w > count) w = count;
                withdraw(ID_LEATHER, w);
                return random(1000, 2000);
            }
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (isQuestMenu()) {
            menu_time = -1L;
            String[] cur = questMenuOptions();
            int cur_count = questMenuCount();
            int craft_lvl = getLevel(SKILL_CRAFTING);
            String select = "";
            int craft_count = options.length;
            for (int i = 0; i < craft_count; ++i) {
                if (craft_lvl  >= levels[i]) {
                    select = options[i];
                    break;
                }
            }
            for (int i = 0; i < cur_count; ++i) {
                String lower = cur[i].toLowerCase(Locale.ENGLISH);
                if (lower.contains("access")) {
                    answer(i);
                    bank_time = System.currentTimeMillis();
                    return random(2000, 3000);
                } else if (lower.contains(select)) {
                    answer(i);
                    return random(600, 1000);
                }
            }
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (getFatigue() > 80) {
            useSleepingBag();
            return random(1000, 1500);
        }
        int leather = getInventoryIndex(ID_LEATHER);
        if (leather == -1) {
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] == -1) return 0;
            talkToNpc(banker[0]);
            return random(3000, 3500);
        }
        int needle = getInventoryIndex(ID_NEEDLE);
        int thread = getInventoryIndex(ID_THREAD);
        if (needle == -1) {
            System.out.println("ERROR: No needle!");
            stopScript();
            setAutoLogin(false); return 0;
        }
        if (thread == -1) {
            System.out.println("ERROR: No thread!");
            stopScript();
            setAutoLogin(false); return 0;
        }
        useItemWithItem(needle, leather);
        menu_time = System.currentTimeMillis();
        return random(600, 800);
    }
}
