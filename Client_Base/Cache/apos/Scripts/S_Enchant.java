import java.text.DecimalFormat;
import java.util.Locale;

public final class S_Enchant extends Script {

    private static final int MAGIC = 6;
    private int amulet_id;
    private int enchanted_id;
    private int spell_id;
    private int enchanted;
    private boolean init;
    private long start_time;
    private long menu_time;
    private long bank_time;
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    private int start_xp;
    private int xp;

    public S_Enchant(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        if (params.equals("dragonstone")) {
            amulet_id = 610;
            enchanted_id = 522;
            spell_id = 42;
        } else if (params.equals("sapphire")) {
            amulet_id = 302;
            enchanted_id = 314;
            spell_id = 3;
        } else if (params.equals("emerald")) {
            amulet_id = 303;
            enchanted_id = 315;
            spell_id = 13;
        } else if (params.equals("ruby")) {
            amulet_id = 304;
            enchanted_id = 316;
            spell_id = 24;
        } else if (params.equals("diamond")) {
            amulet_id = 305;
            enchanted_id = 317;
            spell_id = 30;
        } else {
            System.out.println("You entered the parameter incorrectly. " +
                    "It must be the gem type in lowercase.");
        }
        init = false;
        enchanted = 0;
    }

    @Override
    public int main() {
        if (!init) {
            start_time = System.currentTimeMillis();
            start_xp = xp = getXpForLevel(MAGIC);
            init = true;
        } else {
            xp = getXpForLevel(MAGIC);
        }
        if (getFatigue() > 95) {
            useSleepingBag();
            return random(1000, 2000);
        }
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
            bank_time = -1L;
            int amount = getInventoryCount(enchanted_id);
            if (amount > 0) {
                deposit(enchanted_id, amount);
                return random(600, 800);
            }
            if (!hasInventoryItem(amulet_id)) {
                int w = getEmptySlots();
                int bc = bankCount(amulet_id);
                if (bc < w) {
                    w = bc;
                }
                if (w > 0) {
                    withdraw(amulet_id, w);
                } else {
                    stopScript();
                    setAutoLogin(false);
                }
                return random(600, 800);
            }
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (!canCastSpell(spell_id)) {
            setAutoLogin(false);
            stopScript(); 
            return 0;
        }
        int index = getInventoryIndex(amulet_id);
        if (index != -1) {
            castOnItem(spell_id, index);
            return random(600, 800);
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
        int y = 40;
        int x = 315;
        drawBoxAlphaFill(315, y, 185, 80, 160, 0x000000);
        drawBoxOutline(315, y, 185, 80, 0x0FA8DB);
        y += 15;
        x += 3;
        drawString("Enchant", x, y, 4, 0x349CBF);
        y += 10;
        drawHLine(x - 2, y, 183, 0x0FA8DB);
        y += 20;
        drawString("Running for " + get_runtime(), x, y, 1, 0x349CBF);
        y += 15;
        drawString("Enchanted " + iformat.format(enchanted) +
                " (" + per_hour(enchanted) + "/h)", x, y, 1, 0x349CBF);
        y += 15;
        int gained = xp - start_xp;
        drawString("Gained " + iformat.format(gained) +
                " xp (" + per_hour(gained) + "/h)", x, y, 1, 0x349CBF);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("succes")) {
            ++enchanted;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private String per_hour(int total) {
        if (total <= 0 || start_time <= 0L) {
            return "0";
        }
        return iformat.format(
            ((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L)
        );
    }

    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return iformat.format(secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
}