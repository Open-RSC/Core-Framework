import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

/**
 * KBD Killer  - no world hopping
 * - Uses super sets.
 * - Full gear
 * - Switches anti-breath shield to rune kite when been in combat for 2 sec.
 * - If kills the dragon and has less or equal to 5 foods left, will fall back to lever.
 * - Trades loot over to the loot-guys, which ever is available and takes sharks in return.
 * 
 * just so we're in same page.
 * i'd love all of the chars to be in w2 when they are in the kbd room.
 * so when the looters reach there, they cant be like.. "oh im in wrong world, changing"
 * if you know what i mean lol.
 * if someone else comes to w2 when they are in the kbd room
 * it dont matter
 * 
 * @author Storm
 */
public final class S_KBDKiller extends Script
    implements ActionListener {
    
    // user settings
    private int eat_at = 80;
    private int[] gear = { 594, 401, 402, 420, 404 };
    private int shield_id = 404;
    private String[] traders = { /*"T Z U T U"*/ };
    private int world = 1;
    private int min_att = 99, min_def = 99, min_str = 99;
    
    private final Choice combat_style = new Choice();
    private final Checkbox autobury = new Checkbox("Autobury", false);
    private final Checkbox veteran = new Checkbox("Veteran", true);
    
    private final TextField tf_eat_at = new TextField(String.valueOf(eat_at));
    private final TextField tf_gear = new TextField();
    private final TextField tf_shield_id = new TextField(String.valueOf(shield_id));
    private final TextField tf_traders = new TextField();
    private final TextField tf_world = new TextField(String.valueOf(world));
    private final TextField tf_min_att = new TextField(String.valueOf(min_att));
    private final TextField tf_min_def = new TextField(String.valueOf(min_def));
    private final TextField tf_min_str = new TextField(String.valueOf(min_str));
    
    private static final int
    SHARKS_TO_RUN = 2,
    SUPER_ATT = 486,
    SUPER_DEF = 495,
    SUPER_STR = 492,
    SLEEPING_BAG = 1263,
    BONES = 814,
    MIN_SHARKS = 15,
    LUMB_X = 120,
    LUMB_Y = 648,
    EDGE_BANK_X = 217,
    EDGE_BANK_Y = 448,
    EDGE_AMMY_X = 226,
    EDGE_AMMY_Y = 447,
    DRAY_BANK_X = 219,
    DRAY_BANK_Y = 634,
    KBD_NEAR_GATE_X = 286,
    KBD_NEAR_GATE_Y = 186,
    ATTACK = 0,
    DEFENCE = 1,
    STRENGTH = 2,
    HITS = 3,
    PRAYER = 5,
    ANTI_DRAG_SHIELD = 420,
    SHARK = 546,
    DSTONE_CHARGED = 597,
    DSTONE_UNCHARGED = 522,
    KBD = 477,
    VIAL = 465,
    KBD_INNER_LEVER_X = 567,
    KBD_INNER_LEVER_Y = 3331,
    KBD_GATE = 508,
    KBD_LADDER_DOWN = 6,
    //KBD_LADDER_UP = 5,
    KBD_LEVER_IN = 487,
    //KBD_LEVER_OUT = 488,
    BANK_DOOR_CLOSED = 64;
    
    private static final int[] dstone_ammies = {
        DSTONE_CHARGED, 522
    };
    
    private static final int[] loot = {
        1277, 795, 400, 402, 403, 404, 81, 93, 75, 1092, 405, 31, 33, 38, 41,
        42, 619, 11, 638, 408, 517, 520, 711, 526, 527, 542, SHARK, BONES, DSTONE_UNCHARGED, 523
    };
    
    private final boolean[] banked = new boolean[loot.length];
    private final int[] banked_counts = new int[loot.length];
    
    // 3 dose -> 2 dose -> 1 dose
    
    private static final int[] att_pots = {
        486, 487, 488
    };
    
    private static final int[] def_pots = {
        495, 496, 497
    };
    
    private static final int[] str_pots = {
        492, 493, 494
    };
    
    private static final int[] poison_pots = {
        569, 570, 571, 566, 567, 568
    };
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");

    private long menu_time;
    private long bank_time;
    private long combat_time;
    private long start_time;
    private long move_time;
    private boolean re_equip;
    private long[] sent_message;
    private final boolean[] offered_loot = new boolean[loot.length];
    private final boolean[] offered_ppots = new boolean[poison_pots.length];
    private String last_message;
    private boolean idle_move_dir;
    
    private final PathWalker pw;
    private PathWalker.Path edge_through_wild;
    private PathWalker.Path lumb_to_dray;

    private int pray_xp;
    private int start_pray_xp;

    private boolean poisoned;
    private boolean trade_equip;

    private int trader_empty_slots;
    
    private Frame frame;
    
    public S_KBDKiller(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new S_KBDKiller(null).init(null);
    }

    @Override
    public void init(String params) {
        if (frame == null) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < gear.length; ++i) {
                b.append(gear[i]);
                if (i != (gear.length - 1)) {
                    b.append(',');
                }
            }
            tf_gear.setText(b.toString());
            
            b = new StringBuilder();
            for (int i = 0; i < traders.length; ++i) {
                b.append(traders[i]);
                if (i != (traders.length - 1)) {
                    b.append(',');
                }
            }
            tf_traders.setText(b.toString());
            
            for (String str : FIGHTMODES) {
                combat_style.add(str);
            }
            
            Panel button_pane = new Panel();
            Button button = new Button("OK");
            button.addActionListener(this);
            button_pane.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            button_pane.add(button);
            
            Panel grid = new Panel(new GridLayout(0, 2, 0, 2));
            grid.add(new Label("Combat style"));
            grid.add(combat_style);
            grid.add(new Label("Eat at"));
            grid.add(tf_eat_at);
            grid.add(new Label("Equipment (,)"));
            grid.add(tf_gear);
            grid.add(new Label("Shield ID"));
            grid.add(tf_shield_id);
            grid.add(new Label("Traders (,)"));
            grid.add(tf_traders);
            grid.add(new Label("World"));
            grid.add(tf_world);
            grid.add(new Label("Min att (potting)"));
            grid.add(tf_min_att);
            grid.add(new Label("Min def (potting)"));
            grid.add(tf_min_def);
            grid.add(new Label("Min str (potting)"));
            grid.add(tf_min_str);
            
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            frame.add(grid);
            frame.add(autobury);
            frame.add(veteran);
            frame.add(new Label("Equipment may include shield_id, must include anti-dragon shield (" + ANTI_DRAG_SHIELD + ")."));
            frame.add(new Label("If an extra one is not desired Shield ID must not be in equipment and shield_id"));
            frame.add(new Label("must be -1. Equipment may also may include sleeping bag. (" + SLEEPING_BAG + ")"));
            frame.add(button_pane);
            frame.pack();
            frame.setResizable(false);
        }
        frame.setLocationRelativeTo(null);
        frame.toFront();
        frame.requestFocus();
        frame.setVisible(true);
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            start_pray_xp = pray_xp = getXpForLevel(PRAYER);
        } else {
            pray_xp = getXpForLevel(PRAYER);
        }
        int target_cs = combat_style.getSelectedIndex();
        if (getFightMode() != target_cs) {
            setFightMode(target_cs);
            return random(300, 400);
        }
        if (inCombat()) {
            pw.resetWait();
            if (combat_time == -1) {
                combat_time = System.currentTimeMillis();
            } else if (System.currentTimeMillis() >= (combat_time + 2000L)) {
                int d_shield = getInventoryIndex(ANTI_DRAG_SHIELD);
                if (d_shield != -1 && isItemEquipped(d_shield)) {
                    int new_shield = getInventoryIndex(shield_id);
                    if (new_shield != -1) {
                        wearItem(new_shield);
                        return random(600, 800);
                    }
                }
            }
            if (in_fight_area() && getInventoryCount(SHARK) <= SHARKS_TO_RUN) {
                if (run_to_lever()) return random(600, 800);
            }
            if (!in_fight_area() ||
                    getCurrentLevel(HITS) <= eat_at ||
                    getInventoryIndex(SHARK) == -1) {
                walkTo(getX(), getY());
                return random(400, 600);
            }
            return random(300, 400);
        }
        combat_time = -1L;
        if (isQuestMenu()) {
            menu_time = -1L;
            String[] options = questMenuOptions();
            int count = questMenuCount();
            for (int i = 0; i < count; ++i) {
                String str = options[i].toLowerCase(Locale.ENGLISH);
                if (str.contains("access")) {
                    answer(i);
                    bank_time = System.currentTimeMillis();
                } else if (str.contains("edge")) {
                    answer(i);
                }
            }
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            if (traders.length <= 0) {
                for (int i = 0; i < loot.length; ++i) {
                    if (loot[i] == SHARK) continue;
                    int count = getInventoryCount(loot[i]);
                    if (count > 0) {
                        boolean equip = inArray(gear, loot[i]);
                        if (equip && count <= 1) {
                            continue;
                        }
                        if (!banked[i]) {
                            banked[i] = true;
                            banked_counts[i] += count;
                        }
                        re_equip = true;
                        deposit(loot[i], equip ? (count - 1) : count);
                        return random(600, 800);
                    }
                }
                if (getInventoryIndex(att_pots) == -1) {
                    if (bankCount(SUPER_ATT) <= 0) {
                        return _end("No super atts left :(");
                    }
                    withdraw(SUPER_ATT, 1);
                    return random(1500, 2000);
                }
                
                if (getInventoryIndex(def_pots) == -1) {
                    if (bankCount(SUPER_DEF) <= 0) {
                        return _end("No super defs left :(");
                    }
                    withdraw(SUPER_DEF, 1);
                    return random(1500, 2000);
                }
                
                if (getInventoryIndex(str_pots) == -1) {
                    if (bankCount(SUPER_STR) <= 0) {
                        return _end("No super strs left :(");
                    }
                    withdraw(SUPER_STR, 1);
                    return random(1500, 2000);
                }
                
                int ds_count = getInventoryCount(DSTONE_CHARGED);
                if (ds_count <= 0) {
                    if (bankCount(DSTONE_CHARGED) <= 0) {
                        return _end("Out of charged dstone ammies");
                    }
                    withdraw(DSTONE_CHARGED, 1);
                    return random(1000, 1500);
                } else if (ds_count > 1) {
                    deposit(DSTONE_CHARGED, ds_count - 1);
                    return random(1000, 1500);
                }
            } else {
                if (in_dray_bank()) {
                    int count = getInventoryCount(DSTONE_CHARGED);
                    if (count <= 0) {
                        if (bankCount(DSTONE_CHARGED) <= 0) {
                            return _end("Out of charged dstone ammies");
                        }
                        withdraw(DSTONE_CHARGED, 1);
                        return random(1000, 1500);
                    } else if (count > 1) {
                        deposit(DSTONE_CHARGED, count - 1);
                        return random(1000, 1500);
                    }
                } else if (in_edge_bank()) {
                    for (int id : dstone_ammies) {
                        int count = getInventoryCount(id);
                        if (count > 0) {
                            deposit(id, count);
                            return random(600, 800);
                        }
                    }
                }
            }
            for (int id : gear) {
                int count = getInventoryCount(id);
                if (count <= 0) {
                    if (bankCount(id) <= 0) {
                        return _end("Out of " + getItemNameId(id));
                    }
                    withdraw(id, 1);
                    re_equip = true;
                    return random(1000, 1500);
                } else if (count > 1) {
                    deposit(id, count - 1);
                    return random(1000, 1500);
                }
            }
            if (getInventoryIndex(poison_pots) == -1) {
                for (int id : poison_pots) {
                    int bc = bankCount(id);
                    if (bc <= 0) continue;
                    withdraw(id, 1);
                    return random(1500, 2000);
                }
                System.out.println("No antipoisons left, braving the trip without :( ");
            }
            int shark_count = (traders.length <= 0) ? getEmptySlots() : (MIN_SHARKS - getInventoryCount(SHARK));
            if (shark_count > 0) {
                int shark_bc = bankCount(SHARK);
                if (shark_bc < shark_count) {
                    if (traders.length > 0) {
                        System.out.println("Few sharks left, will have to brave the trip without :( ");
                        shark_count = shark_bc;
                    } else {
                        return _end("out of sharks");
                    }
                }
                if (shark_count > 0) {
                    withdraw(SHARK, shark_count);
                    return random(600, 800);
                }
            }
            Arrays.fill(banked, false);
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (isInTradeConfirm()) {
            confirmTrade();
            return random(600, 800);
        }
        if (isInTradeOffer()) {
            int remaining = 12;
            if (remaining > trader_empty_slots) {
                remaining = trader_empty_slots;
            }
            remaining -= getLocalTradeItemCount();
            if (remaining > 0) {
                for (int i = 0; i < loot.length; ++i) {
                    if (offered_loot[i]) continue;
                    if (loot[i] == SHARK) continue;
                    boolean equip = inArray(gear, loot[i]);
                    if (equip && getInventoryCount(loot[i]) <= 1) {
                        continue;
                    }
                    offered_loot[i] = true;
                    int count = getInventoryCount(loot[i]);
                    if (!isItemStackableId(loot[i])) {
                        if (count > remaining) {
                            count = remaining;
                        }
                    }
                    if (count > 0) {
                        if (equip) {
                            trade_equip = true;
                        }
                        offerItemTrade(getInventoryIndex(loot[i]), count);
                        return random(800, 1000);
                    }
                }
                for (int i = 0; i < poison_pots.length; ++i) {
                    if (offered_ppots[i]) continue;
                    offered_ppots[i] = true;
                    int id = poison_pots[i];
                    int count = getInventoryCount(id);
                    if (!isItemStackableId(id)) {
                        if (count > remaining) {
                            count = remaining;
                        }
                    }
                    if (count > 0) {
                        offerItemTrade(getInventoryIndex(id), count);
                        return random(800, 1000);
                    }
                }
            }
            acceptTrade();
            return random(600, 800);
        }
        if (re_equip) {
            for (int id : gear) {
                if (id == shield_id || id == SLEEPING_BAG) continue;
                int index = getInventoryIndex(id);
                if (index != -1) {
                    if (!isItemEquipped(index)) {
                        wearItem(index);
                        return random(600, 800);
                    }
                } else {
                    System.out.println(getItemNameId(id) + " not found? that shouldn't happen...");
                }
            }
            re_equip = false;
        }
        if (move_time != -1L) {
            return idle_move();
        }
        if (in_fight_area()) {
            int ads_index = getInventoryIndex(ANTI_DRAG_SHIELD);
            if (ads_index != -1) {
                if (!isItemEquipped(ads_index)) {
                    wearItem(ads_index);
                    return random(600, 800);
                }
            } else {
                System.out.println("oh dear where is our anti-dragon shield");
            }
        }
        if (getCurrentLevel(HITS) <= eat_at) {
            int index = getInventoryIndex(SHARK);
            if (index != -1) {
                useItem(index);
                return random(600, 800);
            }
        }
        if (in_wild() && has_bad_players()) {
            pw.resetWait();
            _hop();
            return random(2000, 3000);
        }
        if (pw.walkPath()) return 0;
        if (in_spider_area()) {
            int[] object = getObjectById(KBD_LEVER_IN);
            if (object_valid(object)) {
                atObject(object[1], object[2]);
            } else {
                System.out.println("warning: lever not found");
            }
            return random(600, 800);
        } else if (isAtApproxCoords(LUMB_X, LUMB_Y, 40)) {
            // in lumb
            pw.setPath(lumb_to_dray);
            return 0;
        } else if (in_edge_bank()) {
            if ((traders.length > 0 && getInventoryIndex(dstone_ammies) == -1) || (traders.length <= 0 && !should_bank())) {
                pw.setPath(edge_through_wild);
                return 0;
            }
            int[] npc = getNpcByIdNotTalk(BANKERS);
            if (npc[0] != -1) {
                talkToNpc(npc[0]);
                menu_time = System.currentTimeMillis();
            }
            return random(600, 800);
        } else if (isAtApproxCoords(EDGE_AMMY_X, EDGE_AMMY_Y, 40)) {
            if (!isWalking()) {
                int[] door = getObjectById(BANK_DOOR_CLOSED);
                if (object_valid(door)) {
                    if (distanceTo(door[1], door[2]) <= 3) {
                        atObject(door[1], door[2]);
                    } else {
                        int x, y;
                        do {
                            x = EDGE_BANK_X + random(-3, 3);
                            y = EDGE_BANK_Y + random(-3, 3);
                        } while (!isReachable(x, y));
                        walkTo(x, y);
                    }
                } else {
                    walkTo(EDGE_BANK_X, EDGE_BANK_Y);
                }
            }
            return random(1000, 2000);
        } else if (in_dray_bank()) {
            int ammy = getInventoryIndex(DSTONE_CHARGED);
            if (ammy != -1) {
                useItem(ammy);
                menu_time = System.currentTimeMillis();
                return random(600, 800);
            }
            int[] npc = getNpcByIdNotTalk(BANKERS);
            if (npc[0] != -1) {
                talkToNpc(npc[0]);
                menu_time = System.currentTimeMillis();
            }
            return random(600, 800);
        } else if (in_fence_area()) {
            int[] object = getObjectById(KBD_LADDER_DOWN);
            if (object_valid(object)) {
                atObject(object[1], object[2]);
            } else {
                System.out.println("warning: ladder not found");
            }
            return random(600, 800);
        } else if (isAtApproxCoords(KBD_NEAR_GATE_X, KBD_NEAR_GATE_Y, 3)) {
            int[] object = getObjectById(KBD_GATE);
            if (object_valid(object)) {
                atObject(object[1], object[2]);
            } else {
                System.out.println("warning: gate not found");
            }
            return random(600, 800);
        } else if (in_fight_area()) {
            if (traders.length > 0 && getWorld() != world) {
                hop(world);
                return random(2000, 3000);
            }
            if (getCurrentLevel(ATTACK) <= min_att) {
                int index = getInventoryIndex(att_pots);
                if (index != -1) {
                    useItem(index);
                    return random(600, 800);
                }
            }
            if (getCurrentLevel(DEFENCE) <= min_def) {
                int index = getInventoryIndex(def_pots);
                if (index != -1) {
                    useItem(index);
                    return random(600, 800);
                }
            }
            if (getCurrentLevel(STRENGTH) <= min_str) {
                int index = getInventoryIndex(str_pots);
                if (index != -1) {
                    useItem(index);
                    return random(600, 800);
                }
            }
            int[] item = getItemById(loot);
            if (should_take(item)) {
                if (distanceTo(item[1], item[2]) > 7) {
                    walkTo(item[1] + random(-1, 1), item[2] + random(-1, 1));
                } else {
                    pickupItem(item[0], item[1], item[2]);
                }
                return random(600, 800);
            }
            int[] dragon = getNpcById(KBD);
            if (getInventoryCount(SHARK) > SHARKS_TO_RUN) {
                if (dragon[0] != -1) {
                    if (!poisoned && getInventoryCount(SHARK) >= MIN_SHARKS) {
                        if (distanceTo(dragon[1], dragon[2]) > 7) {
                            int x, y;
                            do {
                                x = dragon[1] + random(-1, 1);
                                y = dragon[2] + random(-1, 1);
                            } while (!isReachable(x, y));
                            walkTo(x, y);
                        } else {
                            attackNpc(dragon[0]);
                        }
                        return random(600, 1000);
                    } else if (dragon[1] == getX() && dragon[2] == getY()) {
                        return random(600, 800);
                    }
                }
            }
            // XXX: niko wanted this gone
            // if (run_to_lever()) return random(600, 800);
            if (getCurrentLevel(HITS) < getLevel(HITS)) {
                int index = getInventoryIndex(SHARK);
                if (index != -1) {
                    useItem(index);
                    return random(600, 800);
                }
            }
            if (poisoned) {
                int index = getInventoryIndex(poison_pots);
                if (index != -1) {
                    useItem(index);
                    return random(800, 1200);
                }
            }
            if (getFatigue() >= 95) {
                if (run_to_lever()) return random(600, 800);
                int bag = getInventoryIndex(SLEEPING_BAG);
                if (bag != -1) {
                    useItem(bag);
                    return random(1000, 2000);
                }
            }
            if (autobury.getState()) {
                int index = getInventoryIndex(BONES);
                if (index != -1) {
                    useItem(index);
                    return random(600, 800);
                }
            }
            int vial = getInventoryIndex(VIAL);
            if (vial != -1) {
                dropItem(vial);
                return random(800, 1000);
            }
            if (traders.length > 0) {
                boolean send = false;
                StringBuilder m = new StringBuilder("I need");
                if (getInventoryIndex(att_pots) == -1) {
                    m.append(" sap");
                    send = true;
                }
                if (getInventoryIndex(def_pots) == -1) {
                    m.append(" sdp");
                    send = true;
                }
                if (getInventoryIndex(str_pots) == -1) {
                    m.append(" ssp");
                    send = true;
                }
                int needed_sharks = MIN_SHARKS - getInventoryCount(SHARK);
                if (needed_sharks > 0) {
                    m.append(" ");
                    m.append(needed_sharks);
                    if (needed_sharks == 1) {
                        m.append(" shark");
                    } else {
                        m.append(" sharks");
                    }
                    send = true;
                }
                String message = m.toString();
                if (!message.equals(last_message)) {
                    Arrays.fill(sent_message, 0L);
                    last_message = message;
                }
                if (send) {
                    for (int i = 0; i < traders.length; ++i) {
                        if (System.currentTimeMillis() >= (sent_message[i] + 3000L)) {
                            sent_message[i] = System.currentTimeMillis();
                            sendPrivateMessage(message, traders[i]);
                            return random(1000, 1500);
                        }
                        int[] player = getPlayerByName(traders[i]);
                        if (player[0] != -1) {
                            Arrays.fill(offered_ppots, false);
                            Arrays.fill(offered_loot, false);
                            trade_equip = false;
                            sendTradeRequest(getPlayerPID(player[0]));
                            return random(800, 1300);
                        }
                    }
                }
            } else if (should_bank()) {
                int index = getInventoryIndex(DSTONE_CHARGED);
                if (index != -1) {
                    useItem(index);
                    menu_time = System.currentTimeMillis();
                }
            }
            // XXX
            if (!isAtApproxCoords(567, 3319, 3)) {
                if (!isWalking()) {
                    walkTo(567, 3319);
                }
                return random(1000, 2000);
            }
        }
        return random(600, 800);
    }
    
    private boolean run_to_lever() {
        if (getY() != KBD_INNER_LEVER_Y) {
            walkTo(KBD_INNER_LEVER_X - random(-1, 1), KBD_INNER_LEVER_Y);
            return true;
        } else if (getX() != KBD_INNER_LEVER_X) {
            walkTo(KBD_INNER_LEVER_X, KBD_INNER_LEVER_Y);
            return true;
        }
        return false;
    }

    private boolean should_bank() {
        if (getInventoryCount(SHARK) < MIN_SHARKS ||
                getInventoryIndex(att_pots) == -1 ||
                getInventoryIndex(def_pots) == -1 ||
                getInventoryIndex(str_pots) == -1 ||
                getInventoryIndex(DSTONE_CHARGED) == -1 ||
                getInventoryIndex(DSTONE_UNCHARGED) != -1) {
            return true;
        }
        return false;
    }

    @Override
    public void paint() {
        final int font = 2;
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S KBD Killer", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        if (autobury.getState()) {
            int gained = pray_xp - start_pray_xp;
            drawString("Prayer XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
            y += 15;
        }
        x += 10;
        boolean header = false;
        for (int i = 0; i < loot.length; ++i) {
            if (banked_counts[i] <= 0) continue;
            if (!header) {
                header = true;
                drawString("Banked items", x - 10, y, font, white);
                y += 15;
            }
            drawString(banked_counts[i] + " " + getItemNameId(loot[i]) + " (" + per_hour(banked_counts[i]) + "/h)", x, y, font, white);
            y += 15;
        }
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("standing")) {
            if (in_fight_area()) {
                move_time = System.currentTimeMillis() + random(400, 800);
            }
        } else if (str.contains("cure") || str.contains("antidote")) {
            poisoned = false;
        } else if (str.contains("poisioned")) {
            poisoned = true;
        } else if (str.contains("completed")) {
            if (trade_equip) {
                re_equip = true;
            }
        }
    }
    
    @Override
    public void onPrivateMessage(String content, String name, boolean m, boolean jm) {
        if (contains(traders, name)) {
            String[] split = content.split(" ");
            for (int j = 0; j < split.length; ++j) {
                if ("empty".equals(split[j])) {
                    try {
                        trader_empty_slots = Integer.parseInt(split[j - 1]);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        trader_empty_slots = 0;
                    }
                }
            }
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            try {
                eat_at = get_int(tf_eat_at);
                String[] array = tf_gear.getText().split(",");
                gear = new int[array.length];
                for (int i = 0; i < gear.length; ++i) {
                    gear[i] = Integer.parseInt(array[i]);
                }
                shield_id = get_int(tf_shield_id);
                String str = tf_traders.getText();
                if ("".equals(str)) {
                    traders = new String[0];
                } else {
                    traders = str.split(",");
                }
                sent_message = new long[traders.length];
                world = get_int(tf_world);
                min_att = get_int(tf_min_att);
                min_def = get_int(tf_min_def);
                min_str = get_int(tf_min_str);
            } catch (Throwable t) {
                System.out.println("Error parsing fields, check your inputs");
                return;
            }
            
            
            pw.init(null);
            lumb_to_dray = pw.calcPath(LUMB_X, LUMB_Y, DRAY_BANK_X, DRAY_BANK_Y);
            edge_through_wild = pw.calcPath(EDGE_BANK_X, EDGE_BANK_Y, KBD_NEAR_GATE_X, KBD_NEAR_GATE_Y);
            start_time = menu_time = bank_time = move_time = -1L;
            last_message = "";
            poisoned = trade_equip = re_equip = false;
            trader_empty_slots = 0;
            Arrays.fill(offered_ppots, false);
            Arrays.fill(offered_loot, false);
            Arrays.fill(sent_message, 0L);
            Arrays.fill(banked, false);
            Arrays.fill(banked_counts, 0);
        }
        frame.setVisible(false);
    }
    
    private static int get_int(TextField tf) {
        return Integer.parseInt(tf.getText());
    }
    
    private boolean object_valid(int[] object) {
        return object[0] != -1 && distanceTo(object[1], object[2]) < 40;
    }
    
    private boolean idle_move_p1() {
        int x = getX();
        int y = getY();
        if (isReachable(x + 1, y)) {
            walkTo(x + 1, y);
            return true;
        }
        if (isReachable(x, y + 1)) {
            walkTo(x, y + 1);
            return true;
        }
        if (isReachable(x + 1, y + 1)) {
            walkTo(x + 1, y + 1);
            return true;
        }
        return false;
    }
    
    private boolean idle_move_m1() {
        int x = getX();
        int y = getY();
        if (isReachable(x - 1, y)) {
            walkTo(x - 1, y);
            return true;
        }
        if (isReachable(x, y - 1)) {
            walkTo(x, y - 1);
            return true;
        }
        if (isReachable(x - 1, y - 1)) {
            walkTo(x - 1, y - 1);
            return true;
        }
        return false;
    }
    
    private int idle_move() {
        if (System.currentTimeMillis() >= move_time) {
            System.out.println("Moving for 5 min timer");

            if (idle_move_dir) {
                if (!idle_move_p1()) {
                    idle_move_m1();
                }
            } else {
                if (!idle_move_m1()) {
                    idle_move_p1();
                }
            }
            idle_move_dir = !idle_move_dir;
            move_time = -1L;
            return random(1500, 2500);
        }
        return 0;
    }
    
    private boolean has_bad_players() {
        int count = countPlayers();
        if (count <= 1) return false;
        for (int i = 1; i < count; ++i) {
            String name = getPlayerName(i);
            if (!contains(traders, name)) {
                System.out.println("Detected unknown player " + name + "!");
                return true;
            }
        }
        return false;
    }
    
    private boolean in_wild() {
        return in_wild(getX(), getY());
    }
    
    private static boolean in_wild(int x, int y) {
        return in_spider_area(x, y) || y < 427;
    }

    private boolean should_take(int[] item) {
        if (item[0] == -1) return false;
        if (getInventoryCount() == MAX_INV_SIZE) {
            if (!isItemStackableId(item[0]) || getInventoryIndex(item[0]) == -1) {
                return false;
            }
        }
        if (item[1] == getX() && item[2] == getY()) {
            return true;
        }
        if (in_fight_area(item[1], item[2]) && item[2] <= 3326) {
            return true;
        }
        return false;
    }
    
    private boolean in_edge_bank() {
        return in_edge_bank(getX(), getY());
    }
    
    private static boolean in_edge_bank(int x, int y) {
        return x <= 220 && x >= 212 && y <= 453 && y >= 448;
    }
    
    private boolean in_dray_bank() {
        return in_dray_bank(getX(), getY());
    }
    
    private static boolean in_dray_bank(int x, int y) {
        return x <= 223 && x >= 216 && y <= 638 && y >= 634;
    }
    
    private boolean in_fight_area() {
        return in_fight_area(getX(), getY());
    }
    
    private static boolean in_fight_area(int x, int y) {
        return y <= 3331 && y > 3314;
    }
    
    private boolean in_fence_area() {
        return in_fence_area(getX(), getY());
    }
    
    private static boolean in_fence_area(int x, int y) {
        return y >= 184 && y <= 187 && x < 285 && x > 279;
    }
    
    private boolean in_spider_area() {
        return in_spider_area(getX(), getY());
    }
    
    private static boolean in_spider_area(int x, int y) {
        return x > 278 && x < 284 && y < 3021 && y > 3013;
    }
    
    private static boolean contains(String[] a, String v) {
        for (String temp : a) {
            if (v.equals(temp)) return true;
        }
        return false;
    }
    
    private int _end(String reason) {
        print_out();
        System.out.println(reason);
        stopScript(); setAutoLogin(false);
        return 0;
    }

    private void print_out() {
        System.out.println("Runtime: " + get_runtime());
        if (autobury.getState()) {
            System.out.println("Prayer XP gained: " + int_format(pray_xp - start_pray_xp));
        }
        for (int i = 0; i < loot.length; ++i) {
            if (banked_counts[i] <= 0) continue;
            System.out.println("Banked " + banked_counts[i] + " " + getItemNameId(loot[i]) +
            " (" + per_hour(banked_counts[i]) + "/h)");
        }
    }
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return int_format(secs / 3600L) + " hours, " +
                    ((secs % 3600L) / 60L) + " mins, " +
                    (secs % 60L) + " secs.";
        }
        if (secs >= 60L) {
            return secs / 60L + " mins, " +
                    (secs % 60L) + " secs.";
        }
        return secs + " secs.";
    }
    
    // blood
    private String per_hour(int total) {
        try {
            return int_format(((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L));
        } catch (ArithmeticException ex) {
        }
        return "0";
    }
    
    private String int_format(long l) {
        return int_format.format(l);
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
                hop(veteran.getState() ? 1 : 2);
                break;
        }
    }
}
