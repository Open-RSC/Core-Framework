import java.text.DecimalFormat;
import java.util.Locale;

public final class S_BowStringer extends Script {
    
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    private static final int ID_STRING = 676;
    private static final int SKILL_FLETCHING = 9;
    
    private int id_un;
    private int id_bow;
    
    private int strung_count;
    private long bank_time;
    private long start_time;

    private long menu_time;
    private int xp;
    private int start_xp;

    public S_BowStringer(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        params = params.trim().toLowerCase(Locale.ENGLISH);
        if (params.startsWith("normal short")) {
            id_un = 189;
            id_bow = 189;
        } else if (params.startsWith("normal long")) {
            id_un = 188;
            id_bow = 188;
        } else if (params.startsWith("oak short")) {
            id_un = 659;
            id_bow = 649;
        } else if (params.startsWith("oak long")) {
            id_un = 658;
            id_bow = 648;
        } else if (params.startsWith("willow short")) {
            id_un = 661;
            id_bow = 651;
        } else if (params.startsWith("willow long")) {
            id_un = 660;
            id_bow = 650;
        } else if (params.startsWith("maple short")) {
            id_un = 663;
            id_bow = 653;
        } else if (params.startsWith("maple long")) {
            id_un = 662;
            id_bow = 652;
        } else if (params.startsWith("yew short")) {
            id_un = 665;
            id_bow = 655;
        } else if (params.startsWith("yew long")) {
            id_un = 664;
            id_bow = 654;
        } else if (params.startsWith("magic short")) {
            id_un = 667;
            id_bow = 657;
        } else if (params.startsWith("magic long")) {
            id_un = 666;
            id_bow = 656;
        } else {
            System.out.println("ERROR: Invalid parameter. Example: normal short OR maple long");
            return;
        }
        System.out.println("Stringing " + params);
        strung_count = 0;
        bank_time = -1L;
        start_time = -1L;
        menu_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            xp = start_xp = getXpForLevel(SKILL_FLETCHING);
        } else {
            xp = getXpForLevel(SKILL_FLETCHING);
        }
        if (getFatigue() > 99) {
            useSleepingBag();
            return random(1000, 2000);
        }
        if (isQuestMenu()) {
            answer(0);
            menu_time = -1L;
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(600, 800);
        }
        if (isBanking()) {
            bank_time = -1L;
            final int half = 14;
            int count = getInventoryCount(id_bow);
            if (count > 0) {
                deposit(id_bow, count);
                return random(1000, 2000);
            }
            int inv_str = getInventoryCount(ID_STRING);
            int w = half - inv_str;
            if (inv_str > half) {
                deposit(ID_STRING, inv_str - half);
                return random(1000, 2000);
            } else if (w > 0) {
                int bc = bankCount(ID_STRING);
                if (bc <= 0) {
                    System.out.println("Out of strings");
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                if (w > bc) w = bc;
                withdraw(ID_STRING, w);
                return random(1000, 2000);
            }
            int inv_un = getInventoryCount(id_un);
            w = half - inv_un;
            if (inv_un > half) {
                deposit(id_un, inv_un - half);
                return random(1000, 2000);
            } else if (w > 0) {
                int bc = bankCount(id_un);
                if (bc <= 0) {
                    System.out.println("Out of bows");
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                if (w > bc) w = bc;
                withdraw(id_un, w);
                return random(1000, 2000);
            }
            closeBank();
            return random(600, 1000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(600, 800);
        }
        int is = getInventoryIndex(ID_STRING);
        int iu = getInventoryIndex(id_un);
        if (is == -1 || iu == -1) {
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                talkToNpc(banker[0]);
                menu_time = System.currentTimeMillis();
            }
            return random(1000, 2000);
        }
        useItemWithItem(is, iu);
        return random(600, 800);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("add a string")) {
            ++strung_count;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    @Override
    public void paint() {
        int x = 25;
        int y = 25;
        drawString("S Bow Stringer", x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + _getRuntime(), x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Strung: " + iformat.format(strung_count) +
                " (" + _perHour(strung_count) + "/h)", x, y, 1, 0xFFFFFF);
        y += 15;
        int gained = xp - start_xp;
        drawString("XP gained: " + iformat.format(gained) +
                " (" + _perHour(gained) + "/h)", x, y, 1, 0xFFFFFF);
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return iformat.format((secs / 3600)) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
    
    private String _perHour(int total) {
        if (total <= 0 || start_time <= 0L) {
            return "0";
        }
        return iformat.format(
            ((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L)
        );
    }
}