import java.text.DecimalFormat;
import java.util.Locale;

public final class S_Use2x14Bank14 extends Script {

    /* unfinished kwuarm potions -> super strengths: 220,461,492,95 */

    private final int[] items = new int[3];
    private final int[] xp_start = new int[SKILL.length];
    private int banked_count;
    private boolean banked;
    private long start_time;
    private long bank_time;
    private long menu_time;
    private final DecimalFormat f = new DecimalFormat("#,##0");
    private int sleep_at;

    public S_Use2x14Bank14(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        try {
            String[] argv = params.split(",");
            items[0] = Integer.parseInt(argv[0]);
            items[1] = Integer.parseInt(argv[1]);
            items[2] = Integer.parseInt(argv[2]);
            sleep_at = Integer.parseInt(argv[3]);
        } catch (Throwable t) {
            System.out.println("Cannnot start. Parameters must be id1,id2,bankid,sleepat.");
        }
        start_time = bank_time = menu_time = -1L;
        banked_count = 0;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            for (int i = 0; i < xp_start.length; ++i) {
                xp_start[i] = getXpForLevel(i);
            }
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
            int count = getInventoryCount(items[2]);
            if (count > 0) {
                if (!banked) {
                    banked_count += count;
                    banked = true;
                }
                deposit(items[2], count);
                return random(600, 800);
            }

            count = getInventoryCount(items[0]);
            if (count > 14) {
                deposit(items[0], count - 14);
                return random(600, 800);
            } else if (count < 14) {
                int w = 14 - count;
                int bc = bankCount(items[0]);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(items[0], w);
                } else if (count == 0) {
                    System.out.println("Out of " + getItemNameId(items[0]));
                    setAutoLogin(false); stopScript();
                    return random(600, 800);
                }
            }

            count = getInventoryCount(items[1]);
            if (count > 14) {
                deposit(items[1], count - 14);
                return random(600, 800);
            } else if (count < 14) {
                int w = 14 - count;
                int bc = bankCount(items[1]);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(items[1], w);
                } else if (count == 0) {
                    System.out.println("Out of " + getItemNameId(items[1]));
                    setAutoLogin(false); stopScript();
                    return random(600, 800);
                }
            }
            closeBank();
            banked = false;
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }

        if (getFatigue() >= sleep_at) {
            useSleepingBag();
            return random(1000, 2000);
        }

        int index1 = getInventoryIndex(items[0]);
        int index2 = getInventoryIndex(items[1]);
        if (index1 != -1 && index2 != -1) {
            useItemWithItem(index1, index2);
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
        final int x = 25;
        int y = 25;
        final int color = 0xFFFFFF;
        final int font = 1;
        drawString("S Use2x14Bank14", x, y, font, color);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, color);
        y += 15;
        drawString("Banked: " + f.format(banked_count), x, y, font, color);
        y += 15;
        for (int i = 0; i < xp_start.length; ++i) {
            int gained = getXpForLevel(i) - xp_start[i];
            if (gained <= 0) {
                continue;
            }
            drawString(SKILL[i] + " XP: " + f.format(gained) +
                    " (" + per_hour(gained) + "/h)", x, y, font, color);
            y += 15;
        }
    }

    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    private String per_hour(int total) {
        long time = ((System.currentTimeMillis() - start_time) / 1000L);
        if (time < 1L) {
            time = 1L;
        }
        return f.format((total * 60L * 60L) / time);
    }

    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return f.format((secs / 3600L)) + " hours, " +
                    ((secs % 3600L) / 60L) + " mins, " +
                    (secs % 60L) + " secs.";
        }
        if (secs >= 60L) {
            return secs / 60L + " mins, " +
                    (secs % 60L) + " secs.";
        }
        return secs + " secs.";
    }
}