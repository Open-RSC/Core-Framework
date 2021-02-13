import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JOptionPane;

public final class S_RedDragons extends Script {
    
    private final int[]
    withdraw_food = {
        546, /* shark */
        370, /* swordfish */
        373 /* lobster */
    };
    
    private static final int
    EMPTY_VIAL = 465,
    COINS = 10,
    LUMB_X = 120,
    LUMB_Y = 648,
    DRAGONS_X = 144,
    DRAGONS_Y = 194,
    EDGE_X = 217,
    EDGE_Y = 448,
    ATTACK = 0,
    DEFENCE = 1,
    STRENGTH = 2,
    HITS = 3,
    PRAYER = 5,
    DRAGON_BONES = 814,
    RED_DRAGON = 201,
    CHOCOLATE_CAKE = 332,
    SLEEPING_BAG = 1263,
    ANTI_SHIELD = 420,
    CRYSTAL_KEY_1 = 526,
    CRYSTAL_KEY_2 = 527,
    LEFT_HALF = 1277;
    
    /* Food will be eaten in order to pick these up if the inventory is full. */
    private static final int[]
    drops = {
        DRAGON_BONES, /* dragon bones */
        542, /* uncut dragonstone */
        523, /* cut dragonstone, just in case, some NPCs drop cut... */
        157, /* uncut diamond */
        CRYSTAL_KEY_1, /* crystal key half */
        CRYSTAL_KEY_2, /* crystal key half */
        75, /* rune long sword */
        LEFT_HALF, /* dragon sq left half */
        795, /* dragon med, just in case... */
        42, /* law rune */
        38, /* death rune */
        31, /* fire rune */
        33, /* air rune */
        619, /* blood rune */
        COINS
    };
    
    private final int[]
    drops_count = new int[drops.length];
    
    private final boolean[]
    drops_banked = new boolean[drops.length];

    private long start_time;
    private long menu_time;
    private long bank_time;
    
    private int start_prayer_xp;
    private int prayer_xp;
    
    private int combat_style;
    private static final int eat_at = 79;
    
    private PathWalker.Path
    dragons_to_edge, 
    lumb_to_edge,
    from_edge;
    private final PathWalker pw;
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");
    
    private int trips;

    private boolean action_performed;
    private long east_limit;
    private long west_limit;
    
    /* filled -> least full */
    private static final int[] sup_att = {
        486, 487, 488
    };
    
    private static final int[] sup_def = {
        495, 496, 497
    };
    
    private static final int[] sup_str = {
        492, 493, 494
    };

    public S_RedDragons(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        start_time = -1L;
        Object smode = JOptionPane.showInputDialog(null,
            "Combat style?", "Red Dragons",
            JOptionPane.QUESTION_MESSAGE, null,
            FIGHTMODES, FIGHTMODES[0]);

        combat_style = -1;
        for (int i = 0; i < FIGHTMODES.length; ++i) {
            if (FIGHTMODES[i].equals(smode)) {
                combat_style = i;
                break;
            }
        }

        if (combat_style == -1) {
            writeLine("You must select a combat style...");
            return;
        }
    }

    @Override
    public int main() {
        if (start_time == -1L) {            
            trips = 0;
            start_time = System.currentTimeMillis();
            menu_time = -1L;
            bank_time = -1L;
            start_prayer_xp = prayer_xp = getXpForLevel(PRAYER);
            Arrays.fill(drops_count, 0);
            Arrays.fill(drops_banked, false);
            
            pw.init(null);
            dragons_to_edge = pw.calcPath(DRAGONS_X, DRAGONS_Y, EDGE_X, EDGE_Y);
            lumb_to_edge = pw.calcPath(LUMB_X, LUMB_Y, EDGE_X, EDGE_Y);
            from_edge = pw.calcPath(EDGE_X, EDGE_Y, DRAGONS_X, DRAGONS_Y);
            
            east_limit = -1L;
            west_limit = -1L;
            
            action_performed = false;
        } else {
            prayer_xp = getXpForLevel(PRAYER);
        }
        
        if (inCombat()) {
            pw.resetWait();
            if (getFightMode() != combat_style) {
                setFightMode(combat_style);
                return random(300, 400);
            }
            int cx = getX();
            int cy = getY();
            int player_count = countPlayers();
            for (int i = 1; i < player_count; ++i) {
                if (getPlayerX(i) == cx && getPlayerY(i) == cy) {
                    walkTo(cx, cy);
                    return random(300, 400);
                }
            }
            /* satisfied */
            east_limit = -1L;
            west_limit = -1L;
            if (getCurrentLevel(HITS) <= eat_at || !in_fight_area()) {
                walkTo(cx, cy);
            }
            return random(300, 400);
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
            
            for (int i = 0; i < drops.length; ++i) {
                int count = getInventoryCount(drops[i]);
                if (count > 0) {
                    if (!drops_banked[i]) {
                        drops_count[i] += count;
                        drops_banked[i] = true;
                    }
                    deposit(drops[i], count);
                    return random(600, 800);
                }
            }
            
            if (getInventoryIndex(ANTI_SHIELD) == -1) {
                if (bankCount(ANTI_SHIELD) <= 1) {
                    return _end("Out of shields");
                }
                withdraw(ANTI_SHIELD, 1);
                return random(1500, 2000);
            }
            
            if (getInventoryIndex(SLEEPING_BAG) == -1) {
                if (bankCount(SLEEPING_BAG) <= 1) {
                    return _end("Out of sleeping bags");
                }
                withdraw(SLEEPING_BAG, 1);
                return random(1500, 2000);
            }
            
            if (getInventoryIndex(sup_att) == -1) {
                for (int id : sup_att) {
                    if (bankCount(id) > 1) {
                        withdraw(id, 1);
                        return random(1500, 2000);
                    }
                }
                System.out.println("Out of super attacks");
            }
            
            if (getInventoryIndex(sup_def) == -1) {
                for (int id : sup_def) {
                    if (bankCount(id) > 1) {
                        withdraw(id, 1);
                        return random(1500, 2000);
                    }
                }
                System.out.println("Out of super defence");
            }
            
            if (getInventoryIndex(sup_str) == -1) {
                for (int id : sup_str) {
                    if (bankCount(id) > 1) {
                        withdraw(id, 1);
                        return random(1500, 2000);
                    }
                }
                System.out.println("Out of super strength");
            }
            
            int empty = getEmptySlots();
            if (empty > 0) {
                for (int id : withdraw_food) {
                    int bc = (bankCount(id) - 1);
                    int w = empty;
                    if (w > bc) w = bc;
                    if (w > 0) {
                        withdraw(id, w);
                        return random(600, 800);
                    }
                }
                return _end("Out of food");
            }
            
            Arrays.fill(drops_banked, false);
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (getCurrentLevel(HITS) <= eat_at) {
            int food = get_food_index();
            if (food != -1) {
                useItem(food);
                pw.resetWait();
                return random(800, 1200);
            }
        }

        int bones = getInventoryIndex(DRAGON_BONES);
        if (bones != -1) {
            useItem(bones);
            return random(600, 800);
        }
        
        if (pw.walkPath()) {
            /*
             * if someone wants to lure with left halves they can go ahead
             * it's not like i'm skulled or have anything worth more
             * than a left half :)
             */
            int[] item = getItemById(LEFT_HALF);
            if (item[0] != -1 && isReachable(item[1], item[2])) {
                if (getInventoryCount() != MAX_INV_SIZE) {
                    if (distanceTo(item[1], item[2]) > 6) {
                        walk_approx(item[1], item[2]);
                        return random(800, 1200);
                    } else {
                        pickupItem(item[0], item[1], item[2]);
                        return random(600, 1000);
                    }
                } else {
                    int food = get_food_index();
                    if (food != -1) {
                        useItem(food);
                        return random(800, 1200);
                    }
                }
            }
            
            item = getItemById(drops);
            if (item[1] == getX() && item[2] == getY()) {
                if (getInventoryCount() != MAX_INV_SIZE ||
                        (isItemStackableId(item[0]) && getInventoryCount(item[0]) > 0)) {
                    action_performed = true;
                    pickupItem(item[0], item[1], item[2]);
                    return random(600, 1000);
                } else {
                    int food = get_food_index();
                    if (food != -1) {
                        useItem(food);
                        return random(800, 1200);
                    }
                }
            }
            
            /* it sometimes gets very uncomfy around this part */
            if (!isWalking() && get_food_index() != -1) {
                if (getY() <= 180 && isAtApproxCoords(143, 180, 6)) {
                    pw.setPath(null);
                    walkTo(141, 180);
                    return random(1000, 2000);
                }
            }
            /* also this part */
            if (!isWalking() && get_food_index() == -1) {
                if (isAtApproxCoords(217, 414, 10)) {
                    walk_approx(217, 425);
                    return random(1000, 2000);
                }
            }
            return 0;
        }
        
        if (getX() == 141 && getY() == 180) {
            west_limit = -1L;
            east_limit = -1L;
            if (getFatigue() > 95) {
                useSleepingBag();
            } else {
                atObject(140, 180);
            }
            return random(1000, 2000);
        }
        
        if (in_fight_area()) {            
            int count = getGroundItemCount();
            for (int i = 0; i < count; ++i) {
                int x = getItemX(i);
                int y = getItemY(i);
                int id = getGroundItemId(i);
                if ((id != COINS && inArray(drops, id) && isReachable(x, y)) ||
                        (id == COINS && x == getX() && y == getY())) {
                    if (getInventoryCount() != MAX_INV_SIZE ||
                            (isItemStackableId(id) && getInventoryCount(id) > 0)) {
                        action_performed = true;
                        if (distanceTo(x, y) > 6) {
                            walk_approx(x, y);
                            return random(800, 1200);
                        } else {
                            pickupItem(id, x, y);
                            return random(600, 1000);
                        }
                    } else {
                        int food = get_food_index();
                        if (food != -1) {
                            useItem(food);
                            return random(800, 1200);
                        }
                    }
                }
            }
            
            if (getInventoryCount() < MAX_INV_SIZE) {
                int[] cake = getItemById(CHOCOLATE_CAKE);
                if (cake[0] != -1 && isReachable(cake[1], cake[2])) {
                    action_performed = true;
                    if (distanceTo(cake[1], cake[2]) > 6) {
                        walk_approx(cake[1], cake[2]);
                        return random(800, 1200);
                    } else {
                        pickupItem(cake[0], cake[1], cake[2]);
                        return random(600, 1000);
                    }
                }
            }
            
            if (get_food_index() == -1) {
                east_limit = -1L;
                west_limit = -1L;
                pw.setPath(dragons_to_edge);
                return 0;
            }
            
            if (getFatigue() > 95) {
                if (distanceTo(141, 181) <= 4) {
                    atObject(140, 180);
                    return random(900, 1600);
                }
                if (!isWalking()) {
                    if (isReachable(141, 181)) {
                        walkTo(141, 181);
                    } else {
                        walkTo(145 + random(-1, 1), 197 + random(-1, 1));
                    }
                }
                return random(800, 1200);
            }
            
            if (getCurrentLevel(ATTACK) <= getLevel(ATTACK) ||
                    (getCurrentLevel(STRENGTH) > (getCurrentLevel(ATTACK) + 6)) ||
                    (getCurrentLevel(DEFENCE) > (getCurrentLevel(ATTACK) + 6))) {
                
                int pot = getInventoryIndex(sup_att);
                if (pot != -1) {
                    useItem(pot);
                    return random(800, 1200);
                }
            }
            
            if (getCurrentLevel(DEFENCE) <= getLevel(DEFENCE) ||
                    (getCurrentLevel(STRENGTH) > (getCurrentLevel(DEFENCE) + 6)) ||
                    (getCurrentLevel(ATTACK) > (getCurrentLevel(DEFENCE) + 6))) {
                
                int pot = getInventoryIndex(sup_def);
                if (pot != -1) {
                    useItem(pot);
                    return random(800, 1200);
                }
            }
            
            if (getCurrentLevel(STRENGTH) <= getLevel(STRENGTH) ||
                    (getCurrentLevel(ATTACK) > (getCurrentLevel(STRENGTH) + 6)) ||
                    (getCurrentLevel(DEFENCE) > (getCurrentLevel(STRENGTH) + 6))) {
                
                int pot = getInventoryIndex(sup_str);
                if (pot != -1) {
                    useItem(pot);
                    return random(800, 1200);
                }
            }
            
            int empty = getInventoryIndex(EMPTY_VIAL);
            if (empty != -1) {
                dropItem(empty);
                return random(800, 1200);
            }
            
            int[] dragon = getNpcById(RED_DRAGON);
            if (dragon[0] != -1) {
                action_performed = true;
                if (distanceTo(dragon[1], dragon[2]) > 6) {
                    walk_approx(dragon[1], dragon[2]);
                    return random(800, 1200);
                } else {
                    attackNpc(dragon[0]);
                }
            } else {
                if (getX() < 141) {
                    if (east_limit == -1L) {
                        east_limit = System.currentTimeMillis() + random(7000, 9000);
                    }
                    if (System.currentTimeMillis() > east_limit) {
                        if (!isWalking() || action_performed) {
                            // switch to west
                            west_limit = -1L;
                            walkTo(149 + random(-1, 1), 202 + random(-1, 1));
                            action_performed = false;
                        }
                        return random(1000, 2000);
                    }
                } else {
                    if (west_limit == -1L) {
                        west_limit = System.currentTimeMillis() + random(3000, 5000);
                    }
                    if (System.currentTimeMillis() > west_limit) {
                        if (!isWalking() || action_performed) {
                            // switch to east
                            east_limit = -1L;
                            walkTo(134 + random(-1, 1), 202 + random(-1, 1));
                            action_performed = false;
                        }
                        return random(1000, 2000);
                    }
                }
            }
            return random(600, 1000);
        } 
        
        if (in_edgeville_bank()) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                if (getFatigue() > 0) {
                    useSleepingBag();
                    return random(1000, 2000);
                }
                int shield = getInventoryIndex(ANTI_SHIELD);
                if (!isItemEquipped(shield)) {
                    wearItem(shield);
                    return random(600, 800);
                }
                ++trips;
                pw.setPath(from_edge);
                return 0;
            }
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                talkToNpc(banker[0]);
                menu_time = System.currentTimeMillis();
            }
            return random(600, 800);
        }
        
        if (in_lumbridge()) {
            pw.setPath(lumb_to_edge);
            return 0;
        }

        System.out.println("oh no, where am I? " + getX() + "," + getY());
        return 2000;
    }

    @Override
    public void paint() {
        int x = 120;
        int y = 15;
        final int font = 2;
        final int white = 0xFFFFFF;
        drawString("S Red Dragons", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        drawString("Trips: " + ifmt(trips), x, y, font, white);
        y += 15;
        int pray = prayer_xp - start_prayer_xp;
        drawString("Prayer XP: " + ifmt(pray) + " (" + phr(pray) + "/h)",
                x, y, font, white);
        y += 15;
        for (int i = 0; i < drops.length; ++i) {
            if (drops_count[i] <= 0) continue;
            drawString(getItemNameId(drops[i]) + ": " +
                    ifmt(drops_count[i]) + " (" + phr(drops_count[i]) + "/h)",
                    x, y, font, white);
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
    
    private void walk_approx(int x, int y) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-1, 1);
            dy = y + random(-1, 1);
        } while (!isReachable(dx, dy) && (++loop) < 500);
        walkTo(dx, dy);
    }
    
    private int _end(String message) {
        System.out.println(message);
        setAutoLogin(false); stopScript();
        return 0;
    }

    private boolean in_lumbridge() {
        return isAtApproxCoords(LUMB_X, LUMB_Y, 30);
    }

    private boolean in_fight_area() {
        return getY() < 223;
    }

    private boolean in_edgeville_bank() {
        int x = getX();
        int y = getY();
        return y >= 448 && y <= 453 && x >= 212 && x <= 220;
    }
    
    private int get_food_index() {
        int count = getInventoryCount();
        for (int i = 0; i < count; ++i) {
            if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("eat")) {
                return i;
            }
        }
        return -1;
    }
    
    // per hour
    private String phr(int total) {
        if (total <= 0 || start_time <= 0L) {
            return "0";
        }
        return int_format.format(
            ((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L)
        );
    }
    
    private String ifmt(long l) {
        return int_format.format(l);
    }
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return ifmt((secs / 3600L)) + " hours, " +
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
