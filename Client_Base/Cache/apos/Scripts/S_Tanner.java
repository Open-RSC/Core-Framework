import java.util.Locale;

public final class S_Tanner extends Script {
    
    private static final int ID_LEATHER = 148;
    private static final int ID_HIDE = 147;
    private static final int ID_TANNER = 172;
    private static final int ID_COINS = 10;
    private PathWalker pw;
    private PathWalker.Path to_bank;
    private PathWalker.Path from_bank;
    private long bank_time;

    public S_Tanner(Extension ex) {
        super(ex);
        this.pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
        if (to_bank == null) {
            pw.init(null);
            to_bank = pw.calcPath(84, 674, 87, 695);
            from_bank = pw.calcPath(87, 695, 84, 674);
        }
        pw.setPath(from_bank);
        bank_time = -1L;
    }
    
    @Override
    public int main() {
        if (isQuestMenu()) {
            String[] options = questMenuOptions();
            int count = questMenuCount();
            for (int i = 0; i < count; ++i) {
                String lower = options[i].toLowerCase(Locale.ENGLISH);
                if (lower.contains("access")) {
                    answer(i);
                    bank_time = System.currentTimeMillis();
                    return random(2000, 3000);
                } else if (lower.contains("buy some leather now")) {
                    answer(i);
                    pw.setPath(to_bank);
                    return random(4000, 5000);
                }
            }
            return 0;
        }
        if (isBanking()) {
            bank_time = -1L;
            int count = getInventoryCount(ID_LEATHER);
            if (count > 0) {
                deposit(ID_LEATHER, count);
                return random(1000, 2000);
            }
            if (getInventoryCount(ID_HIDE) > 0) {
                closeBank();
                pw.setPath(from_bank);
                return random(1000, 2000);
            }
            count = bankCount(ID_HIDE);
            if (count == 0) {
                System.out.println("ERROR: Out of hides!");
                stopScript();
                setAutoLogin(false);
                return 0;
            }
            int w = getEmptySlots();
            if (w > count) w = count;
            withdraw(ID_HIDE, w);
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }

        int num_leather = getInventoryCount(ID_LEATHER);
        int num_hide = getInventoryCount(ID_HIDE);
        if (num_leather > 0 && num_hide > 0) {
            // wait for the job to be complete
            return random(600, 800);
        }

        if (pw.walkPath()) return 0;
        
        if (num_hide <= 0) {
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                talkToNpc(banker[0]);
                return random(3000, 3500);
            }
            return 0;
        } else {
            int coins = getInventoryCount(ID_COINS);
            if (coins < num_hide) {
                System.out.println("ERROR: Out of coins!");
                stopScript();
                return 0;
            }
            int[] tanner = getNpcByIdNotTalk(ID_TANNER);
            if (tanner[0] != -1) {
                talkToNpc(tanner[0]);
                return random(3000, 3500);
            }
            return 0;
        }
    }
}
