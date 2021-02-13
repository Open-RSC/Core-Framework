import java.text.DecimalFormat;
import java.util.Locale;

public final class S_ShieldCollector extends Script
    implements Runnable {
    
    public static final class Config {
        public boolean take_minds = false;
        public boolean tele_dray = false;
        public boolean tele_lumb = false;
        public int combat_style = 1;
    }
    
    private static final int
    ANTI_DRAGON_SHIELD = 420,
    CHARGED_AMULET = 597,
    UNCHARGED_AMULET = 522,
    BANK_DOOR_SHUT = 64,
    BANK_X = 219,
    BANK_Y = 634,
    LUMB_X = 120,
    LUMB_Y = 648,
    MIND_RUNE = 35,
    MIND_X = 138,
    MIND_Y = 668,
    LADDER_UP_X = 139,
    LADDER_UP_Y = 666,
    LADDER_DOWN_X = 139,
    LADDER_DOWN_Y = 1610,
    TIME_TO_PICKUP = (2 * 60 * 1000),
    LUMB_TELE = 15,
    DUKE = 198;

    private final PathWalker pw;
    private PathWalker.Path
    ladder_to_bank,
    bank_to_ladder,
    lumb_to_ladder;

    private long pickup_time;
    private long menu_time;
    private long bank_time;
    private long start_time;
    private long shield_time;
    private long moved_time;
    
    private int last_x;
    private int last_y;

    private int collected_trip;
    private int collected;
    private int on_ground;
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");
    
    private final Config cfg = new Config();
    
    private final JSONgui gui = new JSONgui(getClass().getSimpleName(),
                cfg, null, this);
    
    private static final int[] rune_ids = { 42, 33, 34 };
    
    private static final int[] rune_counts = { 1, 3, 1 };

    public S_ShieldCollector(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        gui.showFrame();
    }

    @Override
    public int main() {
        if (inCombat()) {
            pw.resetWait();
            if (getFightMode() != cfg.combat_style) {
                setFightMode(cfg.combat_style);
            } else {
                walk_approx(getX(), getY(), 3);
            }
            return random(300, 500);
        }
        
        if (start_time == -1L) {
            start_time = moved_time = pickup_time = 
                    System.currentTimeMillis();
        }
        
        if (isBanking()) {
            bank_time = -1L;
            int count = getInventoryCount(ANTI_DRAGON_SHIELD);
            if (count > 0) {
                deposit(ANTI_DRAGON_SHIELD, count);
                return random(600, 800);
            }
            
            count = getInventoryCount(MIND_RUNE);
            if (count > 0) {
                deposit(MIND_RUNE, count);
                return random(600, 800);
            }
            
            count = getInventoryCount(UNCHARGED_AMULET);
            if (count > 0) {
                deposit(UNCHARGED_AMULET, count);
                return random(600, 800);
            }
            
            if (cfg.tele_dray && getInventoryIndex(CHARGED_AMULET) == -1) {
                if (bankCount(CHARGED_AMULET) > 1) {
                    withdraw(CHARGED_AMULET, 1);
                    return random(2000, 3000);
                } else {
                    System.out.println(
                            "Out of charged amulets - keeping one in bank to maintain order");
                }
            }
            
            if (cfg.tele_lumb) {
                for (int i = 0; i < rune_ids.length; ++i) {
                    if (getInventoryCount(rune_ids[i]) >= rune_counts[i]) {
                        continue;
                    }
                    if (bankCount(rune_ids[i]) <= rune_counts[i]) {
                        System.out.println("Out of " +
                                getItemNameId(rune_ids[i]) +
                                " - keeping one in bank to maintain order");
                    } else {
                        withdraw(rune_ids[i], rune_counts[i]);
                        return random(2000, 3000);
                    }
                }
            }
            
            closeBank();
            collected_trip = 0;
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (isQuestMenu()) {
            menu_time = -1L;
            int count = questMenuCount();
            String[] options = questMenuOptions();
            
            for (int i = 0; i < count; ++i) {
                String str = options[i].toLowerCase(Locale.ENGLISH);
                if (str.contains("access")) {
                    bank_time = System.currentTimeMillis();
                    answer(i);
                    return random(600, 800);
                } else if (str.contains("seek a shield")) {
                    shield_time = System.currentTimeMillis();
                    answer(i);
                    return random(1000, 2000);
                } else if (cfg.tele_dray && str.contains("draynor")) {
                    answer(i);
                    return random(1000, 2000);
                }
            }
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        
        if (pw.walkPath()) {
            int x = getX();
            int y = getY();
            if (x != last_x || y != last_y) {
                last_x = x;
                last_y = y;
                moved_time = System.currentTimeMillis();
            } else if (System.currentTimeMillis() >= (moved_time + 8000L)) {
                pw.resetWait();
                walk_approx(x, y, 4);
                return random(1200, 1800);
            }
            return 0;
        }

        if (getY() > 1000) {
            /* we're upstairs */
            on_ground = get_shield_count();
            
            if (shield_time != -1L) {
                if (System.currentTimeMillis() >= (shield_time + 16000L)) {
                    shield_time = -1L;
                }
                return random(300, 400);
            }
            
            if (getInventoryCount() >= MAX_INV_SIZE) {
                int amulet = getInventoryIndex(CHARGED_AMULET);
                if (cfg.tele_dray && amulet != -1) {
                    useItem(amulet);
                    menu_time = System.currentTimeMillis();
                } else if (distanceTo(LADDER_DOWN_X, LADDER_DOWN_Y) > 3) {
                    if (!isWalking()) {
                        walk_approx(LADDER_DOWN_X, LADDER_DOWN_Y, 1);
                    }
                    return random(1000, 2000);
                } else {
                    atObject(LADDER_DOWN_X, LADDER_DOWN_Y);
                }
                return random(600, 800);
            }
            
            if (on_ground <= 0) {
                pickup_time = System.currentTimeMillis();
            }

            if (on_ground >= getEmptySlots() ||
                    (System.currentTimeMillis() >=
                        (pickup_time + TIME_TO_PICKUP) && on_ground > 0)) {
                int[] item = getItemById(ANTI_DRAGON_SHIELD);
                if (object_valid(item)) {
                    if (distanceTo(item[1], item[2]) > 3) {
                        walk_approx(item[1], item[2], 1);
                        return random(1000, 2000);
                    } else {
                        pickupItem(ANTI_DRAGON_SHIELD, item[1], item[2]);
                    }
                }
            } else {
                int shield = getInventoryIndex(ANTI_DRAGON_SHIELD);
                if (shield == -1) {
                    int[] duke = getNpcByIdNotTalk(DUKE);
                    if (duke[0] != -1) {
                        if (distanceTo(duke[1], duke[2]) > 3) {
                            walk_approx(duke[1], duke[2], 1);
                            return random(1000, 2000);
                        } else {
                            talkToNpc(duke[0]);
                            menu_time = System.currentTimeMillis();
                        }
                    }
                } else {
                    dropItem(shield);
                }
            }
            return random(600, 800);
        } 
        
        if (isAtApproxCoords(LADDER_UP_X, LADDER_UP_Y, 4)) {
            if (cfg.take_minds && (getInventoryCount() < MAX_INV_SIZE ||
                    getInventoryIndex(MIND_RUNE) != -1)) {
                int[] mind = getItemById(MIND_RUNE);
                if (mind[1] == MIND_X && mind[2] == MIND_Y) {
                    pickupItem(MIND_RUNE, MIND_X, MIND_Y);
                    return random(600, 800);
                }
            }
            if (getInventoryCount() < MAX_INV_SIZE) {
                pickup_time = System.currentTimeMillis();
                atObject(LADDER_UP_X, LADDER_UP_Y);
                return random(1000, 2000);
            } else {
                pw.setPath(ladder_to_bank);
                return random(600, 800);
            }
        }
        
        if (inside_bank()) {
            if (getInventoryIndex(ANTI_DRAGON_SHIELD) != -1) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                }
            } else if (cfg.tele_lumb && has_teleport_runes()) {
                castOnSelf(LUMB_TELE);
                pw.setPath(lumb_to_ladder);
                return random(1200, 2000);
            } else {
                pw.setPath(bank_to_ladder);
            }
            return random(600, 800);
        } 
        
        if (isAtApproxCoords(BANK_X, BANK_Y, 20)) {
            int[] door = getObjectById(BANK_DOOR_SHUT);
            if (object_valid(door)) {
                atObject(door[1], door[2]);
            } else if (!isWalking()) {
                int rx, ry;
                int attempts = 0;
                do {
                    if ((++attempts) > 1000) {
                        return random(600, 800);
                    }
                    rx = BANK_X + random(-5, 5);
                    ry = BANK_Y + random(-5, 5);
                } while (!inside_bank(rx, ry) || !isReachable(rx, ry));
                walkTo(rx, ry);
            }
            return random(1000, 2000);
        } 
        
        System.out.println("Unsure what to do.");
        return random(1000, 2000);
    }
    
    private boolean inside_bank(int x, int y) {
        return x >= 216 && x <= 223 && y >= 634 && y <= 638;
    }

    private boolean has_teleport_runes() {
        for (int i = 0; i < rune_ids.length; ++i) {
            if (getInventoryCount(rune_ids[i]) < rune_counts[i]) {
                return false;
            }
        }
        return true;
    }

    private int get_shield_count() {
        int count = getGroundItemCount();
        int shields = 0;
        for (int i = 0; i < count; ++i) {
            int id = getGroundItemId(i);
            int x = getItemX(i);
            int y = getItemY(i);
            if (id == ANTI_DRAGON_SHIELD && object_valid(id, x, y)) {
                ++shields;
            }
        }
        return shields;
    }
    
    private boolean inside_bank() {
        return inside_bank(getX(), getY());
    }

    private boolean walk_approx(int x, int y, int range) {
        int rx, ry;
        int attempts = 0;
        do {
            if ((++attempts) > 1000) {
                return false;
            }
            rx = x + random(-1, 1);
            ry = y + random(-1, 1);
        } while (!isReachable(rx, ry));
        walkTo(rx, ry);
        return true;
    }

    private boolean object_valid(int[] item) {
        return object_valid(item[0], item[1], item[2]);
    }
    
    private boolean object_valid(int id, int x, int y) {
        return id != -1 &&
                distanceTo(x, y) < 24 &&
                isReachable(x, y);
    }
    
    // blood
    private String per_hour(int total) {
        if (total <= 0 || start_time <= 0L) {
            return "0";
        }
        return int_format.format(
            ((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L)
        );
    }
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return int_format.format(secs / 3600) + " hours, " +
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
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        final int font = 2;
        drawString("S Anti Dragon Shield Collector", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        drawString("Shields on ground: " + on_ground,
                x, y, font, white);
        y += 15;
        drawString("Collected: " + int_format.format(collected) +
                " (" + per_hour(collected) + "/h)",
                x, y, font, white);
        y += 15;
        if (collected > collected_trip) {
            drawString("Collected (this trip): " + collected_trip,
                    x, y, font, white);
        }
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("hands you a shield")) {
            ++collected;
            ++collected_trip;
            shield_time = -1L;
        }
    }

    @Override
    public void run() {
        pw.init(null);
        ladder_to_bank = pw.calcPath(
            // - 1 so we're not exactly on the ladder so pw doesn't fail
            LADDER_UP_X - 1, LADDER_UP_Y,
            BANK_X, BANK_Y
        );
        bank_to_ladder = pw.calcPath(
            BANK_X, BANK_Y,
            LADDER_UP_X - 1, LADDER_UP_Y
        );
        lumb_to_ladder = pw.calcPath(
            LUMB_X, LUMB_Y,
            LADDER_UP_X - 1, LADDER_UP_Y
        );
        start_time = -1L;
        menu_time = -1L;
        bank_time = -1L;
        shield_time = -1L;
        collected = 0;
        collected_trip = 0;
        on_ground = 0;
    }
}