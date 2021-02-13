import java.util.Locale;

import javax.swing.JOptionPane;

public final class S_EdgevilleDruids extends Script {
    
    private int food_id = 373;
    private int eat_at = 30;
    private int food_to_withdraw = 0;
    private int combat_style = 1;
    private boolean sleep = true;

    private static final int
    SKILL_HITS = 3,
    LUMB_X = 128,
    LUMB_Y = 640,
    EDGE_X = 215,
    EDGE_Y = 450;
    private static final int[] gate_path_x = {
        215, 217, 217, 203, 197, 196
    };
    private static final int[] gate_path_y = {
        3296, 3284, 3276, 3273, 3273, 3266
    };
    private int cur_tile;
    private int[] pickup;
    private boolean walking;
    private final PathWalker pw;
    private PathWalker.Path lumb_to_edge;
    private boolean pw_init;
    private long menu_time;
    private long bank_time;

    public S_EdgevilleDruids(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String args) {
        menu_time = bank_time = -1L;
        pw_init = false;
        lumb_to_edge = null;
        int result = JOptionPane.showConfirmDialog(null, "Pick up low leveled herbs?");
        if (result == JOptionPane.YES_OPTION) {
            pickup = new int[] {
                165, 435, 436, 437, 438, 439, 440, 441, 442, 443, 469, 464, 466, 270, 40, 42
            };
        } else {
            pickup = new int[] {
                438, 439, 440, 441, 442, 443, 466, 40, 42
            };
        }
        cur_tile = 0;
        walking = false;
    }

    @Override
    public int main() {
        if (getFightMode() != combat_style) {
            setFightMode(combat_style);
            return random(600, 800);
        }
        
        if (sleep && getFatigue() > 95) {
            useSleepingBag();
            return random(800, 1200);
        }
        
        if (walking) {
            if (isAtApproxCoords(212, 3254, 4)) {
                walking = false;
                return random(600, 800);
            }
            walkTo(212 + random(-2, 2), 3254 + random(-2, 2));
            return random(800, 1200);
        }
        
        if (isBanking()) {
            bank_time = -1L;
            for (int id : pickup) {
                int count = getInventoryCount(id);
                if (count > 0) {
                    deposit(id, count);
                    return random(600, 800);
                }
            }
            int food_count = getInventoryCount(food_id);
            if (food_count > food_to_withdraw) {
                deposit(food_id, food_count - food_to_withdraw);
                return random(600, 800);
            } else if (food_count < food_to_withdraw) {
                int withdraw_count = food_to_withdraw - food_count;
                int bank_count = bankCount(food_id);
                if (withdraw_count > bank_count) {
                    withdraw_count = bank_count;
                }
                int empty_count = getEmptySlots();
                if (withdraw_count > empty_count) {
                    withdraw_count = empty_count;
                }
                if (withdraw_count <= 0) {
                    System.out.println("Can't withdraw required food");
                    setAutoLogin(false); stopScript();
                    return 0;
                }
                withdraw(food_id, withdraw_count);
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
        
        if (pw.walkPath()) return 0;
        if (isAtApproxCoords(LUMB_X, LUMB_Y, 40)) {
            if (!pw_init) {
                pw.init(null);
                pw_init = true;
            }
            if (lumb_to_edge == null) {
                lumb_to_edge = pw.calcPath(LUMB_X, LUMB_Y, EDGE_X, EDGE_Y);
                if (lumb_to_edge == null) {
                    setAutoLogin(false); stopScript();
                    return 0;
                }
            }
            pw.setPath(lumb_to_edge);
            return 0;
        }
        
        if (is_above_ground()) {
            return above_ground();
        }
        return under_ground();
    }

    private int under_ground() {
        int food_index = getInventoryIndex(food_id);
        if ((food_index != -1 || food_to_withdraw <= 0) && getInventoryCount() != MAX_INV_SIZE) {
            if (getY() > 3265) {
                if (inCombat()) {
                    walkTo(getX(), getY());
                    return random(400, 600);
                }
                if (isAtApproxCoords(196, 3266, 2)) {
                    atObject(196, 3266);
                    return random(2000, 3000);
                }
                int[] midgate = getObjectById(57);
                if (midgate[1] == 211) {
                    atObject(211, 3272);
                    return random(900, 1200);
                }
                walk_to_gate();
                return random(1000, 2000);
            }
            cur_tile = 0;
            if (getY() < 3241) {
                walking = true;
                return 1000;
            }
            int[] item = getItemById(pickup);
            if (item[0] != -1 && distanceTo(item[1], item[2]) < 20) {
                pickupItem(item[0], item[1], item[2]);
                return random(1000, 1500);
            }
            if (inCombat()) {
                if (getCurrentLevel(SKILL_HITS) <= eat_at) {
                    walkTo(getX(), getY());
                }
                return random(400, 600);
            }
            if (getCurrentLevel(SKILL_HITS) <= eat_at) {
                useItem(food_index);
                return random(600, 800);
            }
            int[] npc = getNpcById(270);
            if (npc[0] != -1) {
                attackNpc(npc[0]);
                return random(1000, 1500);
            }
            walkTo(212 + random(-2, 2), 3254 + random(-2, 2));
            return random(600, 800);
        }
        if (inCombat()) {
            walkTo(getX(), getY());
            return random(400, 600);
        }
        if (getY() <= 3265) {
            if (getY() == 3265) {
                atObject(196, 3266);
                return random(2000, 3000);
            }
            if (getY() < 3250) {
                walkTo(212, 3254);
                return random(900, 1200);
            }
            if (getX() > 210) {
                walkTo(210, 3253);
                return random(900, 1200);
            }
            walkTo(197, 3265);
            return random(900, 1200);
        }
        if (getY() >= 3287) {
            atObject(215, 3300);
            return random(1000, 2000);
        }
        int[] midgate = getObjectById(57);
        if (midgate[1] == 211) {
            atObject(211, 3272);
            return random(900, 1200);
        }
        walk_to_ladder();
        return random(1000, 2000);
    }

    private int above_ground() {
        cur_tile = 0;
        if (getY() < 454) {
            int[] bankDoor = getObjectById(64);
            if (bankDoor[0] != -1) {
                atObject(bankDoor[1], bankDoor[2]);
                return random(800, 1000);
            }
        }
        if ((food_to_withdraw > 0 && getInventoryIndex(food_id) == -1) || getInventoryCount() == MAX_INV_SIZE) {
            if (getNpcById(BANKERS)[0] != -1) {
                int[] npc = getNpcByIdNotTalk(BANKERS);
                if (npc[0] != -1) {
                    talkToNpc(npc[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            }
            if (getY() > 454) {
                if (getWallObjectIdFromCoords(218, 465) == 2) {
                    atWallObject(218, 465);
                    return random(900, 1200);
                }
                walkTo(221, 454);
            }
            return random(2500, 3000);
        }
        if (getY() < 459) {
            walkTo(217, 459);
            return random(2500, 3000);
        }
        if (getWallObjectIdFromCoords(218, 465) == 2) {
            atWallObject(218, 465);
            return random(900, 1200);
        }
        atObject(215, 468);
        return random(2500, 3000);
    }

    private void walk_to_gate() {
        if (isAtApproxCoords(gate_path_x[cur_tile], gate_path_y[cur_tile], 2)) {
            cur_tile++;
        }
        walkTo(gate_path_x[cur_tile], gate_path_y[cur_tile]);
    }

    private void walk_to_ladder() {
        int len = ((gate_path_x.length - 1) - cur_tile);
        if (isAtApproxCoords(gate_path_x[len], gate_path_y[len], 2)) {
            cur_tile++;
        }
        walkTo(gate_path_x[len], gate_path_y[len]);
    }

    private boolean is_above_ground() {
        return (getY() < 1000);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
}