public final class S_AmuletStringer extends Script {
    
    private static final String[] names = {
        "dragonstone", "diamond", "ruby", "emerald", "sapphire"
    };
    
    private static final int[] unstrung_ids = {
        524, 300, 299, 298, 297
    };
    
    private static final int[] strung_ids = {
        610, 305, 304, 303, 302
    };
    
    private static final int GNOME_BALL = 981;
    private static final int BALL_WOOL = 207;

    private int unstrung_id;
    private int[] banked_counts;
    private long start_time;
    private long bank_time;

    public S_AmuletStringer(Extension e) {
        super(e);
    }

    @Override
    public void init(String params) {
        start_time = -1L;
        bank_time = -1L;
        banked_counts = new int[names.length];
    }

    @Override
    public int main() {
        if (start_time == -1) {
            start_time = System.currentTimeMillis();
            for (int i = 0; i < names.length; ++i) {
                if (getInventoryCount(unstrung_ids[i]) > 0) {
                    System.out.println("Stringing " + names[i] + " amulets");
                    unstrung_id = unstrung_ids[i];
                    break;
                }
            }
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        if (isBanking()) {
            for (int i = 0; i < names.length; ++i) {
                int count = getInventoryCount(strung_ids[i]);
                if (count > 0) {
                    banked_counts[i] += count;
                    deposit(strung_ids[i], count);
                    return random(1200, 2000);
                }
            }
            int ic = getInventoryCount(unstrung_id);
            if (ic <= 0) {
                int w = bankCount(unstrung_id);
                if (w <= 0) {
                    for (int i = 0; i < names.length; ++i) {
                        if (bankCount(unstrung_ids[i]) > 0) {
                            unstrung_id = unstrung_ids[i];
                            System.out.println("Stringing " + names[i] + " amulets");
                            _printOut();
                            return 0;
                        }
                    }
                    return _end("Out of unstrung.");
                }
                if (w > 14) w = 14;
                withdraw(unstrung_id, w);
                return random(1200, 2000);
            } else if (ic > 14) {
                deposit(unstrung_id, ic - 14);
                return random(1700, 3200);
            }
            ic = getInventoryCount(BALL_WOOL);
            if (ic <= 0) {
                int bc = bankCount(BALL_WOOL);
                if (bc < 14) {
                    return _end("Out of wool.");
                }
                withdraw(BALL_WOOL, 14);
                return random(1200, 2000);
            } else if (ic != 14) {
                deposit(BALL_WOOL, ic);
                return random(1700, 3200);
            }
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        int ball = getInventoryIndex(GNOME_BALL);
        if (ball != -1) {
            System.out.println("Gnome ball!");
            dropItem(ball);
            return random(1200, 2000);
        }
        int iu = getInventoryIndex(unstrung_id);
        int iw = getInventoryIndex(BALL_WOOL);
        if (iu != -1 && iw != -1) {
            useItemWithItem(iu, iw);
            return random(500, 1000);
        }
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1) {
            talkToNpc(banker[0]);
            return random(3000, 3500);
        }
        return random(600, 1000);
    }

    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        int y = 25;
        drawString("Amulet Stringer", 25, y, 4, white);
        y += 15;
        drawString("Running for " + _getRuntime(), 25, y, 1, white);
        for (int i = 0; i < names.length; ++i) {
            if (banked_counts[i] <= 0) continue;
            y += 15;
            drawString("Banked " + names[i] + " amulets: " + banked_counts[i],
                25, y, 1, white);
        }
        y += 15;
    }
    
    private void _printOut() {
        System.out.println("Runtime: " + _getRuntime());
        for (int i = 0; i < names.length; ++i) {
            if (banked_counts[i] <= 0) continue;
            System.out.println("Banked " + names[i] + " amulets: " + banked_counts[i]);
        }
    }
    
    private int _end(String reason) {
        System.out.println(reason);
        _printOut();
        stopScript(); setAutoLogin(false);
        return 0;
    }

    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
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
}