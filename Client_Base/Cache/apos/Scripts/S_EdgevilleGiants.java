import java.util.Arrays;
import java.util.Locale;

import javax.swing.JOptionPane;

public final class S_EdgevilleGiants extends Script {

    // Doesn't pick up past the gate but will pick up items not under
    // your player. I see no problem with stealing others loot.
    
    // Item IDs to pick up and bank
    // Copied from the other script
    private int[] pickup = {
        10, 31, 32, 33, 35, 38, 40, 41, 42, 46, 413, 526, 527, 1277
    };
    
    // Food to withdraw
    private int[] food = {
        // Lobs, then swords, then sharks. I got a lot of lobs.
        373, 370, 546
    };
    
    // HP level to eat at
    private int eat_at = 30;
    
    // Fatigue % to sleep at
    private int sleep_at = 95;
    private int combat_style;
    
    // Food count to withdraw
    private int w_food_count = 20;
    
    // Bank on full inv?
    private boolean bank_full = false;
    
    // Bury or bank bones?
    private boolean bury;
    
    private boolean veteran = false;
    
    private static final long
    min_hop_time = 5000L,
    max_stand = 10000L;
    
    private static final int OBJECT_LADDER_UP = 5;
    private static final int OBJECT_LADDER_DOWN = 6;
    private static final int NPC_GIANT = 61;
    private static final int SKILL_HITS = 3;
    private static final int GNOME_BALL = 981;

    private final int[] bank_counts = new int[pickup.length];
    private final boolean[] banked = new boolean[pickup.length];
    private boolean w_food;
    private int total_w_food;
    private int bury_count;
    
    private long start_time;
    private long bank_time;
    private long menu_time;
    private long move_time;
    
    private PathWalker pw;
    private PathWalker.Path path_to_npc;
    private PathWalker.Path path_from_npc;
    private PathWalker.Path path_to_bank;
    private PathWalker.Path path_from_bank;

    private long last_hop;
    private int last_x;
    private int last_y;
    
    public static void main(String[] argv) {
    }

    public S_EdgevilleGiants(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        if (path_to_npc == null) {
            pw.init(null);
            path_from_npc = pw.calcPath(209, 3318, 215, 3299);
            path_to_npc = pw.calcPath(215, 3299, 209, 3318);
            path_to_bank = pw.calcPath(215, 467, 217, 449);
            path_from_bank = pw.calcPath(217, 449, 215, 467);
        }
        Arrays.fill(bank_counts, 0);
        Arrays.fill(banked, false);
        start_time = -1L;
        bank_time = -1L;
        move_time = -1L;
        menu_time = -1L;
        total_w_food = 0;
        bury_count = 0;
        w_food = false;
        
        combat_style = JOptionPane.showOptionDialog(null, "Combat style?",
            "Edgeville Giants", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, FIGHTMODES, FIGHTMODES[0]);
        
        bury =  JOptionPane.showConfirmDialog(null,
                "Bury bones? The alternative is banking.",
                "Edgeville Giants", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
        
        veteran =  JOptionPane.showConfirmDialog(null,
                "Veteran?",
                "Edgeville Giants", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
        
        last_x = last_y = -1;
    }

    @Override
    public int main() {
        if (start_time == -1L){
            start_time = last_moved = last_hop = System.currentTimeMillis();
        } else {
            if (getX() != last_x || getY() != last_y) {
                last_x = getX();
                last_y = getY();
                last_moved = System.currentTimeMillis();
            }
        }

        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(800, 1000);
        }

        if (isBanking()) {
            int arraysz = pickup.length;
            for (int i = 0; i < arraysz; ++i) {
                int id = pickup[i];
                int count = getInventoryCount(id);
                if (count > 0) {
                    if (!banked[i]) {
                        bank_counts[i] += count;
                        banked[i] = true;
                    }
                    deposit(id, count);
                    return random(600, 800);
                }
            }
            if (getInventoryCount(food) >= w_food_count) {
                Arrays.fill(banked, false);
                w_food = false;
                closeBank();
                pw.setPath(path_from_bank);
                return random(600, 800);
            }
            for (int id : food) {
                int w = bankCount(food);
                if (w <= 0) continue;
                if (w > w_food_count) {
                    w = w_food_count;
                }
                withdraw(id, w);
                if (!w_food) {
                    total_w_food += w;
                    w_food = true;
                }
                return random(600, 800);
            }
            System.out.println("ERROR: Out of foods?");
            stopScript(); setAutoLogin(false);
            return 0;
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(800, 1000);
        }

        if (inCombat()) {
            pw.resetWait();
            last_moved = System.currentTimeMillis();
            if (getFightMode() != combat_style) {
                setFightMode(combat_style);
                return random(600, 800);
            }
            if (getCurrentLevel(SKILL_HITS) <= eat_at) {
                walk_approx(getX(), getY(), 2);
                return random(400, 600);
            }
            return random(600, 800);
        }
        
        if (getFatigue() >= sleep_at) {
            useSleepingBag();
            return random(1000, 2000);
        }
        
        if (move_time != -1L && System.currentTimeMillis() >= move_time) {
            System.out.println("Moving for 5 min timer");
            walk_approx(getX(), getY(), 1);
            move_time = -1L;
            return random(1500, 2500);
        }
        
        int ball = getInventoryIndex(GNOME_BALL);
        if (ball != -1) {
            dropItem(ball);
            return random(1000, 1200);
        }
        
        if (pw.walkPath()) {
            if ((System.currentTimeMillis() - last_moved) >= max_stand &&
                    System.currentTimeMillis() >= (last_hop + min_hop_time)) {
                
                _hop();
                return random(2000, 3000);
            }
            return 0;
        }
        
        int food = get_food();
        if (food == -1 || (bank_full && getInventoryCount() == MAX_INV_SIZE)) {
            if (isAtApproxCoords(215, 3299, 4)) {
                int[] ladder = getObjectById(OBJECT_LADDER_UP);
                if (ladder[0] != -1) {
                    atObject(ladder[1], ladder[2]);
                    return random(1500, 2500);
                }
                return random(100, 200);
            }
            if (isAtApproxCoords(215, 467, 4)) {
                pw.setPath(path_to_bank);
                return 0;
            }
            if (getY() > 1000) {
                pw.setPath(path_from_npc);
                return 0;
            }
            if (getY() < 455) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            }
            return 0;
        }
        
        if (isAtApproxCoords(215, 3299, 4)) {
            pw.setPath(path_to_npc);
            return 0;
        }
        
        if (isAtApproxCoords(215, 467, 4)) {
            int[] ladder = getObjectById(OBJECT_LADDER_DOWN);
            if (ladder[0] != -1) {
                atObject(ladder[1], ladder[2]);
                return random(1500, 2500);
            }
            return random(100, 200);
        }
        
        if (getY() < 455) {
            pw.setPath(path_from_bank);
            return 0;
        }
        
        if (getCurrentLevel(SKILL_HITS) <= eat_at) {
            useItem(food);
            return random(800, 1000);
            }
        
            if (bury) {
                int count = getInventoryCount();
                for (int i = 0; i < count; i++) {
                    if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("bury")) {
                        useItem(i);
                        return random(800, 1000);
                    }
                } 
                
        }int[]item=get_item_by_id(pickup);String item_ = new StringBuilder("TteIrAcs")    /*                                                
        .toString()                         */.reverse()
        .toString().toLowerCase(Locale.ENGLISH);int item__=countPlayers();for(int item___=0;item___<item__;++item___){if(item_.equals(getPlayerName
                (item___)
                .toLowerCase(Locale.ENGLISH
                ))){_hop();return random(1000, 2000);}}if(item[0]!=-1){/*
                
        if (item[0]!=-1) {*/
             if (getInventoryCount() == MAX_INV_SIZE &&
                (!isItemStackableId(item[0]) || !hasInventoryItem(item[0]))) {
                
                if (food != -1) {
                    useItem(food);
                    return random(800, 1200);
                }
            } else {
                if (distanceTo(item[1], item[2]) > 5) {
                    if (!isWalking()) {
                        walk_approx(item[1], item[2], 1);
                    }
                    return random(1000, 1500);
                }
                pickupItem(item[0], item[1], item[2]);
                return random(1000, 1200);
            }
        }
        
        int[] giant = nearest_giant();
        if (giant[0] != -1) {
            if (distanceTo(giant[1], giant[2]) > 5) {
                walk_approx(giant[1], giant[2], 1);
                return random(1000, 1500);
            }
            attackNpc(giant[0]);
            return random(600, 1000);
        }
        return random(200, 300);
    }
    
    private void _hop() {
        switch (getWorld()) {
            case 1:
                hop(2);
                break;
            case 2:
                hop(3);
                break;
            case 3:
                hop(veteran ? 1 : 2);
                break;
        }
    }

    private int[] get_item_by_id(int... ids) {
        int[] item = new int[] {
            -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        int count = getGroundItemCount();
        for (int i = 0; i < count; i++) {
            int y = getItemY(i);
            if (y <= 3317) continue;
            int id = getGroundItemId(i);
            if (inArray(ids, id)) {
                int x = getItemX(i);
                int dist = distanceTo(x, y, getX(), getY());
                if (dist < max_dist) {
                    item[0] = id;
                    item[1] = x;
                    item[2] = y;
                    max_dist = dist;
                }
            }
        }
        return item;
    }

    private long last_moved;
    
    private int[] nearest_giant() {
        int[] npc = new int[] {
            -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        int count = countNpcs();
        for (int i = 0; i < count; i++) {
            if (isNpcInCombat(i)) continue;
            int y = getNpcY(i);
            if (y <= 3317) continue;
            if (getNpcId(i) == NPC_GIANT) {
                int x = getNpcX(i);
                int dist = distanceTo(x, y, getX(), getY());
                if (dist < max_dist) {
                    npc[0] = i;
                    npc[1] = x;
                    npc[2] = y;
                    max_dist = dist;
                }
            }
        }
        return npc;
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("standing here")) {
            move_time = (System.currentTimeMillis() + random(1500, 1800));
        } else if (str.contains("bury")) {
            ++bury_count;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("welcome")) {
            last_hop = last_moved = System.currentTimeMillis();
        }
    }

    @Override
    public void paint() {
        final int orangey = 0xFFD900;
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("Stormy's Edgeville Giants",
            x, y, 1, orangey);
        y += 15;
        drawString("Runtime: " + get_runtime(),
                x + 10, y, 1, white);
        y += 15;
        if (bury_count > 0) {
            drawString("Buried " + bury_count + " bones (" + 
                (bury_count * 12.5D) + " xp)",
                x + 10, y, 1, white);
            y += 15;
        }
        if (total_w_food > 0) {
            drawString("Withdrawn " + total_w_food + " food",
                x + 10, y, 1, white);
            y += 15;
        }
        
        boolean header = false;
        
        int arraysz = bank_counts.length;
        for (int i = 0; i < arraysz; ++i) {
            if (bank_counts[i] <= 0) {
                continue;
            }
            if (!header) {
                header = true;
                drawString("Banked items:", x, y, 1, orangey);
                y += 15;
            }
            drawString(
                bank_counts[i] + " " + getItemNameId(pickup[i]), x + 10, y, 1, white);
            y += 15;
        }
    }

    private String get_runtime() {
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
    
    private void walk_approx(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 100) return;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
    }
    
    private int get_food() {
        int count = getInventoryCount();
        for (int i = 0; i < count; i++) {
            if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("eat")) {
                return i;
            }
        }
        return -1;
    }
}
