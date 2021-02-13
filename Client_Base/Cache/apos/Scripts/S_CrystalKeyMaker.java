public final class S_CrystalKeyMaker extends Script {

    private long bank_time;
    private long start_time;
    private int key_count;
    private static final int ID_KEY = 525;
    private static final int ID_TEETH = 526;
    private static final int ID_LOOP = 527;

    public S_CrystalKeyMaker(Extension ex) {
        super(ex);
    }
    
    public void init(String params) {
        bank_time = -1L;
        start_time = -1L;
        key_count = 0;
        
    }
    
    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        }
        if (isBanking()) {
            bank_time = -1L;
            int inv_keys = getInventoryCount(ID_KEY);
            if (inv_keys > 0) {
                deposit(ID_KEY, inv_keys);
                return random(2000, 3000);
            }
            int inv_teeth = getInventoryCount(ID_TEETH);
            int inv_loops = getInventoryCount(ID_LOOP);
            final int h = (MAX_INV_SIZE / 2);
            if (inv_teeth < h) {
                int banked = bankCount(ID_TEETH);
                if (banked <= 0) {
                    System.out.println("ERROR: Out of teeth!");
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                int w = h - inv_teeth;
                if (w > banked) w = banked;
                withdraw(ID_TEETH, w);
                return random(2000, 3000);
            }
            if (inv_loops < h) {
                int banked = bankCount(ID_LOOP);
                if (banked <= 0) {
                    System.out.println("ERROR: Out of loops!");
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                int w = h - inv_loops;
                if (w > banked) w = banked;
                withdraw(ID_LOOP, w);
                return random(2000, 3000);
            }
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (getFatigue() > 80) {
            useSleepingBag();
            return random(1000, 2000);
        }
        int teeth = getInventoryIndex(ID_TEETH);
        int loop = getInventoryIndex(ID_LOOP);
        if (teeth == -1 || loop == -1) {
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                talkToNpc(banker[0]);
                return random(3000, 3500);
            }
            return random(200, 300);
        }
        useItemWithItem(teeth, loop);
        return random(600, 800);
    }
    
    @Override
    public void paint() {
        final int orangey = 0xFFD900;
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("S Crystal Key Maker",
            x, y, 1, orangey);
        y += 15;
        drawString("Runtime: " + _getRuntime(),
                x + 10, y, 1, white);
        y += 15;
        if (key_count > 0) {
            drawString("Made " + key_count + " keys",
                x + 10, y, 1, white);
            y += 15;
        }
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 3600 && secs < 7200) {
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
        if (str.contains("join")) {
            ++key_count;
        }
    }
    
}
