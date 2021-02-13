import java.util.Locale;

public final class S_GemCutter extends Script {

    private static final int LEVEL_CRAFTING = 12;
    private static final int ID_CHISEL = 167;
    private int[] uncut_ids;
    private int[] cut_ids;
    private long start_time;
    private long bank_time;
    private long menu_time;
    private int cut_count;
    private int xp;
    private int start_xp;

    public S_GemCutter(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        param_error: if (!params.isEmpty()) {
            String[] split = params.split(",");
            if (split.length == 0) break param_error;
            uncut_ids = new int[split.length];
            cut_ids = new int[split.length];
            int i = 0;
            for (String str : split) {
                if (str.startsWith("sap")) {
                    uncut_ids[i] = 160;
                    cut_ids[i++] = 164;
                } else if (str.startsWith("eme")) {
                    uncut_ids[i] = 159;
                    cut_ids[i++] = 163;
                } else if (str.startsWith("rub")) {
                    uncut_ids[i] = 158;
                    cut_ids[i++] = 162;
                } else if (str.startsWith("dia")) {
                    uncut_ids[i] = 157;
                    cut_ids[i++] = 161;
                } else if (str.startsWith("dra")) {
                    uncut_ids[i] = 542;
                    cut_ids[i++] = 523;
                } else if (str.startsWith("top")) {
                    uncut_ids[i] = 889;
                    cut_ids[i++] = 892;
                } else if (str.startsWith("jad")) {
                    uncut_ids[i] = 890;
                    cut_ids[i++] = 893;
                } else if (str.startsWith("opa")) {
                    uncut_ids[i] = 891;
                    cut_ids[i++] = 894;
                } else {
                    System.out.println(str + " was not recognized as a valid gem.");
                    break param_error;
                }
            }
            start_time = -1;
            cut_count = 0;
            bank_time = -1L;
            menu_time = -1L;
            xp = 0;
            return;
        }
        System.out.println("You entered the parameter(s) incorrectly. Example: sapphire,emerald,ruby,diamond,dragonstone,topaz,jade,opal");
    }

    @Override
    public int main() {
        if (start_time == -1) {
            start_time = System.currentTimeMillis();
            start_xp = getXpForLevel(LEVEL_CRAFTING);
        }
        
        int xp = getXpForLevel(LEVEL_CRAFTING);
        if (xp > this.xp) {
            this.xp = xp;
        }
        
        if (isQuestMenu()) {
            answer(0);
            menu_time = -1L;
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
            for (int id : cut_ids) {
                int amount = getInventoryCount(id);
                if (amount > 0) {
                    deposit(id, amount);
                    return random(1000, 2000);
                }
            }
            
            if (getInventoryCount() < MAX_INV_SIZE) {
                for (int id : uncut_ids) {
                    if (hasBankItem(id)) {
                        withdraw(id, getEmptySlots());
                        return random(1000, 2000);
                    }
                }
                System.out.println("Out of gems, stopping script.");
                setAutoLogin(false);
                stopScript();
                return 0;
            }
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (getFatigue() >= 95) {
            useSleepingBag();
            return random(1000, 2000);
        }
        
        int gem = -1;
        for (int id : uncut_ids) {
            gem = getInventoryIndex(id);
            if (gem != -1) break;
        }
        int chisel = getInventoryIndex(ID_CHISEL);
        if (chisel == -1) {
            System.out.println("ERROR: No chisel!");
            stopScript();
            setAutoLogin(false);
            return 0;
        }
        if (gem != -1) {
            useItemWithItem(chisel, gem);
            return random(600, 800);
        }
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1) {
            talkToNpc(banker[0]);
            menu_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        return random(600, 800);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("you cut")) {
            ++cut_count;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    @Override
    public void paint() {
        int y = 40;
        int x = 315;
        drawBoxAlphaFill(315, y, 185, 80, 180, 0x141414);
        drawBoxOutline(315, y, 185, 80, 0xE01E1B);
        y += 15;
        x += 3;
        drawString("Gem Cutter", x, y, 4, 0xE01E1B);
        y += 10;
        x -= 2;
        drawHLine(x, y, 183, 0x9E1816);
        y += 20;
        x += 2;
        drawString("Running for " + _getRuntime(), x, y, 1, 0xE01E1B);
        y += 15;
        drawString("Cut " + cut_count + " (" + perHour(cut_count) + "/h)", x, y, 1, 0xE01E1B);
        y += 15;
        int xp_gained = xp - start_xp;
        drawString("Gained " + xp_gained + " xp (" + perHour(xp_gained) + "/h)", x, y, 1, 0xE01E1B);
        y += 15;
    }
    
    // blood
    private int perHour(int total) {
        try {
            return (int) (((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L));
        } catch (ArithmeticException ex) {
        }
        return 0;
    }

    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
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
}