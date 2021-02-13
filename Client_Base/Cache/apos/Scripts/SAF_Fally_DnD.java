import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
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
 * Taverly Blue Dragons, Black Demons, Black Dragons, or King Black Dragons in wild
 * - Picks up the loot :
 * rune battleaxe
 * rune 2-h
 * rune long
 * rune chain
 * rune med
 * rune kite
 * rune sq
 * dmed
 * d sq half
 * half keys
 * chaos, deaths, blood, fire runes
 * dragon stones 
 * dragon bones (buries them also)
 * - teles falador to bank.
 * - option to use ss
 * - option to use food or pray pots
 * 
 * shauder notes:
 * start by the west bank in fally or the ladder to the dungeon
 * @author shauder 
 * with much of it stolen from @Storms S_TravBlackDemons script (pretty much all of it)
 */
public final class SAF_Fally_DnD extends Script
    implements ActionListener {
    
    // user settings. edited from GUI
    private int
    eat_at = 50,
    pray_at = 20,
    food_count = 2,
    ppot_count = 10,
    food_id = 546, /* shark */
    sleep_at_bank = 20,
    min_bank_hp = 80,
    min_bank_pray = 50,
    min_att = 99,
    min_def = 99,
    min_str = 99,
    starting_world = 2;
    
    private long
    min_hop_time = 5000L,
    max_stand = 10000L;
    
    private final TextField
    tf_eat_at = new TextField(String.valueOf(eat_at)),
    tf_pray_at = new TextField(String.valueOf(pray_at)),
    tf_food_count = new TextField(String.valueOf(food_count)),
    tf_ppot_count = new TextField(String.valueOf(ppot_count)),
    tf_food_id = new TextField(String.valueOf(food_id)),
    tf_sleep_at_bank = new TextField(String.valueOf(sleep_at_bank)),
    tf_min_bank_hp = new TextField(String.valueOf(min_bank_hp)),
    tf_min_bank_pray = new TextField(String.valueOf(min_bank_pray)),
    tf_min_att = new TextField(String.valueOf(min_att)),
    tf_min_def = new TextField(String.valueOf(min_def)),
    tf_min_str = new TextField(String.valueOf(min_str)),
    tf_max_stand = new TextField(String.valueOf(max_stand));
    
    private final Choice combat_style = new Choice();
    private final Choice destination = new Choice();
    
    private final Checkbox
    drink_ss = new Checkbox("Use super sets", true),
    drop_vials = new Checkbox("Drop vials", true),
    in_bank = new Checkbox("Start at bank", true),
    bank_dbones = new Checkbox("Bank Dragon Bones", true),
    veteran = new Checkbox("Veteran (World 1 access)", true);
    
    // script constants
    private int 
    WALK_BACK_X = 0,
    WALK_BACK_Y = 0;

    private static final String DESTINATION_LIST[] = {"Blue Dragons", "Black Demons", "Black Dragons", "King Black Dragons"};
    private static final int
    KBD_GATE_X = 285,
    KBD_GATE_Y = 185,
    KBD_GATE_ABOVE_Y = 185,
    KBD_GATE_ABOVE_X_W = 285,
    KBD_GATE_ABOVE_X_E =  284,
    KBD_LADDER_X = 282,
    KBD_LADDER_Y = 185,
    KBD_LADDER_ABOVE_X = 283,
    KBD_LADDER_ABOVE_Y = 185,
    KBD_LADDER_DOWN_X = 283,
    KBD_LADDER_DOWN_Y = 3017,
    KBD_GO_TO_LEVER_X = 282,
    KBD_GO_TO_LEVER_Y = 3020,
    KBD_FINAL_X = 282,
    KBD_FINAL_Y = 3029,
    KBD = 477,
    BANK_X = 328,
    BANK_Y = 552,
    MEMB_GATE_WALK_X_W = 342,
    MEMB_GATE_WALK_X_E = 341,
    MEMB_GATE_WALK_Y = 488,
    MEMB_GATE_X = 341,
    MEMB_GATE_Y = 487,
    LADDER_DOWN_WALK_X = 376,
    LADDER_DOWN_WALK_Y = 521,
    LADDER_DOWN_X = 376,
    LADDER_DOWN_Y = 520,
    LADDER_UP_X = 376,
    LADDER_UP_Y = 3352,
    DUNG_DOOR_WALK_X_E = 354,
    DUNG_DOOR_WALK_X_W = 355,
    DUNG_DOOR_WALK_Y = 3353,
    DUNG_DOOR_ID = 84,
    DUSTY_KEY = 596,
    DRAGON_BONE = 814,
    SKILL_ATT = 0,
    SKILL_DEF = 1,
    SKILL_STR = 2,
    SKILL_HP = 3,
    SKILL_PRAY = 5,
    PARA_MONSTER = 12,
    FALLY_TELE = 18,
    BANK_MENU_OPTION = 0,
    PPOT_FULL = 483,
    BLUE_DRAGON = 202,
    BLACK_DEMON = 290,
    BLACK_DRAGON = 291,
    BANK_DOOR_CLOSED = 64,
    LUMB_X = 128,
    LUMB_Y = 640,
    EMPTY_VIAL = 465,
    WATER_RUNE = 32,
    WATER_RUNE_COUNT = 1,
    AIR_RUNE = 33,
    AIR_RUNE_COUNT = 3,
    LAW_RUNE = 42,
    LAW_RUNE_COUNT = 1,
    SLEEPING_BAG = 1263,
    GNOME_BALL = 981;

    
    // pathwalker isn't really appropriate here because we need fine
    // control over where we're going
    // this is demons -> ladder, for some reason
    private static Point[] dung_path;

    private static final Point[] dung_path_black_dragon = {
        new Point(409, 3344), new Point(404, 3347), new Point(398, 3343), 
        new Point(397, 3348), new Point(391, 3354), new Point(387, 3364), 
        new Point(385, 3369), new Point(381, 3371), new Point(377, 3368), 
        new Point(374, 3364), new Point(372, 3360), new Point(367, 3358), 
        new Point(366, 3357), new Point(361, 3356), new Point(356, 3354), 
        new Point(355, 3353), new Point(354, 3353), new Point(351, 3354), 
        new Point(350, 3359), new Point(348, 3364), new Point(348, 3369), 
        new Point(350, 3374), new Point(349, 3375), new Point(347, 3376), 
        new Point(342, 3376), new Point(342, 3371), new Point(342, 3366), 
        new Point(338, 3367), new Point(337, 3370), new Point(336, 3372), 
        new Point(332, 3371), new Point(331, 3366), new Point(332, 3364), 
        new Point(333, 3363), new Point(338, 3360), new Point(340, 3355), 
        new Point(342, 3350), new Point(344, 3345), new Point(345, 3341), 
        new Point(349, 3338), new Point(349, 3333), new Point(349, 3328), 
        new Point(350, 3323), new Point(355, 3321), new Point(360, 3321), 
        new Point(365, 3321), new Point(370, 3321), new Point(374, 3325), 
        new Point(375, 3330), new Point(375, 3335), new Point(375, 3340), 
        new Point(375, 3345), new Point(375, 3350), new Point(376, 3351)
    };

    private static final Point[] dung_path_black_demon = {
        new Point(381, 3371), new Point(377, 3368), new Point(374, 3364),
        new Point(372, 3360), new Point(367, 3358), new Point(366, 3357),
        new Point(361, 3356), new Point(356, 3354), new Point(355, 3353),
        new Point(354, 3353), new Point(351, 3354), new Point(350, 3359),
        new Point(348, 3364), new Point(348, 3369), new Point(350, 3374),
        new Point(349, 3375), new Point(347, 3376), new Point(342, 3376),
        new Point(342, 3371), new Point(342, 3366), new Point(338, 3367),
        new Point(337, 3370), new Point(336, 3372), new Point(332, 3371),
        new Point(331, 3366), new Point(332, 3364), new Point(333, 3363),
        new Point(338, 3360), new Point(340, 3355), new Point(342, 3350),
        new Point(344, 3345), new Point(345, 3341), new Point(349, 3338),
        new Point(349, 3333), new Point(349, 3328), new Point(350, 3323),
        new Point(355, 3321), new Point(360, 3321), new Point(365, 3321),
        new Point(370, 3321), new Point(374, 3325), new Point(375, 3330),
        new Point(375, 3335), new Point(375, 3340), new Point(375, 3345),
        new Point(375, 3350), new Point(376, 3351)
    };

    private static final Point[] dung_path_blue_dragon = {
        new Point(370, 3356), new Point(367, 3356), new Point(364, 3356),
        new Point(361, 3356), new Point(356, 3354), new Point(355, 3353),
        new Point(354, 3353), new Point(351, 3354), new Point(350, 3359),
        new Point(348, 3364), new Point(348, 3369), new Point(350, 3374),
        new Point(349, 3375), new Point(347, 3376), new Point(342, 3376),
        new Point(342, 3371), new Point(342, 3366), new Point(338, 3367),
        new Point(337, 3370), new Point(336, 3372), new Point(332, 3371),
        new Point(331, 3366), new Point(332, 3364), new Point(333, 3363),
        new Point(338, 3360), new Point(340, 3355), new Point(342, 3350),
        new Point(344, 3345), new Point(345, 3341), new Point(349, 3338),
        new Point(349, 3333), new Point(349, 3328), new Point(350, 3323),
        new Point(355, 3321), new Point(360, 3321), new Point(365, 3321),
        new Point(370, 3321), new Point(374, 3325), new Point(375, 3330),
        new Point(375, 3335), new Point(375, 3340), new Point(375, 3345),
        new Point(375, 3350), new Point(376, 3351)
    };
    
    // 1 dose -> 2 dose -> 3 dose

    private static final int[] pray_pots = {
        PPOT_FULL, 484, 485, 
    };
    
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

    private static final int[] cp_pots = {
        568, 567, 566
    };
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");

    private PathWalker pw;
    private PathWalker.Path bank_to_gate;
    private PathWalker.Path bank_to_kbdgate;
    private PathWalker.Path gate_to_ladder;
    private PathWalker.Path lumby_to_fally;
    private long start_time;
    private long menu_time;
    private long bank_time;
    private int path_index;
    private boolean walk_inside_bank;
    private boolean poisoned;
    private int target_npc;

    private static final int[] base_items = {
        SLEEPING_BAG
    };

    private static final int[] equipt_items = {
        388, 389, 420, 597, 594, 795
    };

    private static final int[] pickup_ids = {
        10, 31, 32, AIR_RUNE, 34, 35, 36, 37, 38, 40, 41, LAW_RUNE, 46, 75, 81, 93, 157, 158, 159, 160, 161, 162, 163, 164, 399, 400, 403, 404, 405, 523, 526, 527, 541, 619, 795, DRAGON_BONE, 1090, 1091, 1092, 1277
    };

    private static final int[] rare_drops = {
        795, 1277
    };
    // stuff also in pickup_ids that shouldn't be banked
    private static final int[] dont_bank = {
        32, AIR_RUNE, LAW_RUNE
    };
    // stuff also in pickup_ids that must be obtained twice before it can be banked
    private static final int[] pickup_equipment = {
        400, 404, 795
    };

    private boolean[] banked_loot = new boolean[pickup_ids.length];
    private boolean died = false;
    private int[] banked_counts = new int[pickup_ids.length];
    private Frame frame;
    
    private long last_hop;
    private long last_moved;
    private int last_x;
    private int last_y;
    

    public SAF_Fally_DnD(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new SAF_Fally_DnD(null).init(null);
    }

    @Override
    public void init(String params) {
        if (frame == null) {
            for (String str : FIGHTMODES) {
                combat_style.add(str);
            }

            for (String str : DESTINATION_LIST) {
                destination.add(str);
            }

            Panel col_pane = new Panel(new GridLayout(0, 2, 2, 2));
            col_pane.add(new Label("Target NPC:"));
            col_pane.add(destination);
            col_pane.add(new Label("Combat style:"));
            col_pane.add(combat_style);
            col_pane.add(new Label("Food id:"));
            col_pane.add(tf_food_id);
            col_pane.add(new Label("Eat at:"));
            col_pane.add(tf_eat_at);
            col_pane.add(new Label("Drink ppot at:"));
            col_pane.add(tf_pray_at);
            col_pane.add(new Label("Food withdraw count:"));
            col_pane.add(tf_food_count);
            col_pane.add(new Label("Ppot withdraw count:"));
            col_pane.add(tf_ppot_count);
            col_pane.add(new Label("Sleep at (when banking):"));
            col_pane.add(tf_sleep_at_bank);
            col_pane.add(new Label("Min hp (leaving bank):"));
            col_pane.add(tf_min_bank_hp);
            col_pane.add(new Label("Min pray (leaving bank):"));
            col_pane.add(tf_min_bank_pray);
            col_pane.add(new Label("Min attack (potting):"));
            col_pane.add(tf_min_att);
            col_pane.add(new Label("Min defence (potting):"));
            col_pane.add(tf_min_def);
            col_pane.add(new Label("Min strength (potting):"));
            col_pane.add(tf_min_str);
            col_pane.add(new Label("Max time stand before hop (ms, blocked by NPCs?):"));
            col_pane.add(tf_max_stand);
            
            Panel button_pane = new Panel();
            Button button = new Button("OK");
            button.addActionListener(this);
            button_pane.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            button_pane.add(button);
            
            Panel cb_pane = new Panel();
            cb_pane.add(drop_vials);
            cb_pane.add(drink_ss);
            cb_pane.add(in_bank);
            cb_pane.add(bank_dbones);
                        
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            frame.add(col_pane);
            frame.add(cb_pane);
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
        if (start_time == -1L){
            start_time = last_moved = last_hop = System.currentTimeMillis();
            if (in_bank.getState())
            {
                walk_inside_bank = true;
            }
        } else {
            if (getX() != last_x || getY() != last_y || inCombat()) {
                last_x = getX();
                last_y = getY();
                last_moved = System.currentTimeMillis();
            }
        }
        
        if (isQuestMenu()) {
            menu_time = -1L;
            answer(BANK_MENU_OPTION);
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

            int vial_count = getInventoryCount(EMPTY_VIAL);
            if (vial_count > 0) {
                deposit(EMPTY_VIAL, vial_count);
                return random(600, 800);
            }

            if (poisoned)
            {
                int cppot = getInventoryIndex(cp_pots);
                if (cppot != -1) {
                    useItem(cppot);
                    return random(600, 800);
                }
            }

            for (int i = 0; i < pickup_ids.length; ++i) {
                int id = pickup_ids[i];
                int count = getInventoryCount(id);
                if (count <= 0) continue;
                if (inArray(dont_bank, id)) continue;
                if (inArray(pickup_equipment, id)) {
                    if (count >= 2) {
                        if (!banked_loot[i]) {
                            banked_counts[i] += (count - 1);
                            banked_loot[i] = true;
                        }
                        deposit(id, count - 1);
                        return random(2000, 3000);
                    }
                } else {
                    if (!banked_loot[i]) {
                        banked_counts[i] += count;
                        banked_loot[i] = true;
                    }
                    deposit(id, count);
                    return random(600, 800);
                }
            }

            for (int i = 0; i < base_items.length; ++i) {
                int id = base_items[i];
                int count = getInventoryCount(id);
                if (count == 1) continue;
                if (bankCount(id) >= 1)
                {
                    if (count <= 0) {
                        if (bank_dbones.getState()) {
                            if (id == SLEEPING_BAG && getFatigue() == 0)
                            {
                                
                            }
                            else
                            {
                                withdraw(id, 1);
                                return random(2000, 3000);
                            }
                        }
                        else
                        {
                            withdraw(id, 1);
                            return random(2000, 3000);
                        } 
                    }
                    if (count > 1) {
                        deposit(id, count - 1);
                        return random(600, 800);
                    }
                } else {
                    return _end("out of item id: " + id);
                }
            }

            if (getFatigue() >= sleep_at_bank) {
                int bag = getInventoryIndex(SLEEPING_BAG);
                if (bag != -1) {
                    useItem(bag);
                    return random(1000, 2000);
                }
            }

            if (bank_dbones.getState() && getFatigue() == 0)
            {

                deposit(SLEEPING_BAG, 1);
            }

            if (target_npc == BLACK_DRAGON && getInventoryCount(cp_pots[0]) < 1 && getInventoryCount(cp_pots[1]) < 1 && getInventoryCount(cp_pots[2]) < 1 
                || target_npc == KBD && getInventoryCount(cp_pots[0]) < 1 && getInventoryCount(cp_pots[1]) < 1 && getInventoryCount(cp_pots[2]) < 1)
            {
                if (bankCount(cp_pots[0]) > 1) 
                {
                    withdraw(cp_pots[0], 1);
                    return random(600, 800);
                } else if (bankCount(cp_pots[1]) > 1) 
                {
                    withdraw(cp_pots[1], 1);
                    return random(600, 800);
                } else if (bankCount(cp_pots[2]) > 1) 
                {
                    withdraw(cp_pots[2], 1);
                    return random(600, 800);
                } else {
                    return _end("out of anti-poison");
                }
            }

            if (target_npc != KBD) {
                if (getInventoryCount(DUSTY_KEY) < 1) {
                    if (bankCount(DUSTY_KEY) <= 0) {
                        return _end("out of dusty keys");
                    }
                    withdraw(DUSTY_KEY, 1);
                    return random(600, 800);
                }
            }
            
            if (drink_ss.getState()) {
                if (getInventoryCount(att_pots) < 1) {
                    for (int id : att_pots) {
                        if (bankCount(id) <= 0) continue;
                        withdraw(id, 1);
                        return random(1300, 1700);
                    }
                    System.out.println("Out of att pots?");
                }
                if (getInventoryCount(def_pots) < 1) {
                    for (int id : def_pots) {
                        if (bankCount(id) <= 0) continue;
                        withdraw(id, 1);
                        return random(1300, 1700);
                    }
                    System.out.println("Out of def pots?");
                }
                if (getInventoryCount(str_pots) < 1) {
                    for (int id : str_pots) {
                        if (bankCount(id) <= 0) continue;
                        withdraw(id, 1);
                        return random(1300, 1700);
                    }
                    System.out.println("Out of str pots?");
                }
            }
            
            int cur_food_count = 0;
            int inv_size = getInventoryCount();
            for (int i = 0; i < inv_size; ++i) {
                if ("eat".equals(getItemCommand(i).toLowerCase(Locale.ENGLISH))) {
                    ++cur_food_count;
                }
            }
            if (cur_food_count < food_count) {
                int w = food_count - cur_food_count;
                if (bankCount(food_id) < w) return _end("out of food");
                withdraw(food_id, w);
                return random(1300, 1700);
            } else if (cur_food_count > food_count) {
                int d = cur_food_count - food_count;
                int ic = getInventoryCount(food_id);
                if (d > ic) d = ic;
                if (d >= 0) {
                    deposit(food_id, d);
                    return random(1300, 1700);
                }
            }
            
            int cur_ppot_count = getInventoryCount(pray_pots);
            if (cur_ppot_count < ppot_count) {
                int w = ppot_count - cur_ppot_count;
                if (bankCount(PPOT_FULL) < w) return _end("out of ppots");
                withdraw(PPOT_FULL, w);
                return random(1300, 1700);
            } else if (cur_ppot_count > ppot_count) {
                int d = cur_ppot_count - ppot_count;
                int ic = getInventoryCount(PPOT_FULL);
                if (d > ic) d = ic;
                if (d >= 0) {
                    deposit(PPOT_FULL, d);
                    return random(1300, 1700);
                }
            }

            if (getInventoryCount(WATER_RUNE) < WATER_RUNE_COUNT) {
                if (bankCount(WATER_RUNE) <= WATER_RUNE_COUNT) {
                    return _end("out of waters");
                }
                withdraw(WATER_RUNE, WATER_RUNE_COUNT);
                return random(600, 800);
            }
            
            if (getInventoryCount(AIR_RUNE) < AIR_RUNE_COUNT) {
                if (bankCount(AIR_RUNE) <= AIR_RUNE_COUNT) {
                    return _end("out of airs");
                }
                withdraw(AIR_RUNE, AIR_RUNE_COUNT);
                return random(600, 800);
            }
            
            if (getInventoryCount(LAW_RUNE) < LAW_RUNE_COUNT) {
                if (bankCount(LAW_RUNE) <= LAW_RUNE_COUNT) {
                    return _end("out of laws");
                }
                withdraw(LAW_RUNE, LAW_RUNE_COUNT);
                return random(600, 800);
            }
            
            if (food_count > 0 && getCurrentLevel(SKILL_HP) < min_bank_hp) {
            } else if (ppot_count > 0 && getCurrentLevel(SKILL_PRAY) < min_bank_pray) {
            } else {
                if (target_npc == KBD)
                {
                    pw.setPath(bank_to_kbdgate);
                }
                else
                {
                    pw.setPath(bank_to_gate);
                    path_index = dung_path.length - 1;
                }
                walk_inside_bank = false;
            }
            Arrays.fill(banked_loot, false);
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }

        int target_fm = combat_style.getSelectedIndex();
        if (getFightMode() != target_fm) {
            setFightMode(target_fm);
            return random(300, 400);
        }
        if (inCombat()) {
            if (ppot_count > 0 && !isPrayerEnabled(PARA_MONSTER) && getCurrentLevel(SKILL_PRAY) > 0) {
                enablePrayer(PARA_MONSTER);
                return random(300, 400);
            }
            if ((food_count > 0 && getCurrentLevel(SKILL_HP) <= eat_at) ||
                (ppot_count > 0 && getCurrentLevel(SKILL_PRAY) <= pray_at) ||
                !in_fight_area()) {
                
                walkTo(getX(), getY());
            }
            pw.resetWait();
            return random(300, 400);
        }
        
        if (isAtApproxCoords(LUMB_X, LUMB_Y, 40)) {
            if (!died)
            {
                last_moved = System.currentTimeMillis();
                died = true;
                return random(600, 800);
            }
            if((System.currentTimeMillis() - last_moved) >= 10000) {
                pw.setPath(lumby_to_fally);
            }
        }

        if (isAtApproxCoords(BANK_X, BANK_Y, 10) && died) {
            pw.setPath(null);
            died = false;
            walk_inside_bank = true;
        }

        for (int i = 0; i < equipt_items.length; ++i) {
            int id = equipt_items[i];
            int index = getInventoryIndex(id);
            int count = getInventoryCount(id);
            if (count < 1) continue;
            if (!isItemEquipped(index) && count >= 1) {
                wearItem(index);
                return random(600, 800);
            }
        }
        
        int food_index = -1;
        int inv_count = getInventoryCount();
        for (int i = 0; i < inv_count; ++i) {
            if ("eat".equals(getItemCommand(i).toLowerCase(Locale.ENGLISH))) {
                food_index = i;
                break;
            }
        }

        if (food_index != -1 && food_count > 0 && getCurrentLevel(SKILL_HP) <= eat_at || in_fight_area() && !inCombat() && getCurrentLevel(SKILL_HP) <= getLevel(SKILL_HP) - 20) {
            useItem(food_index);
            return random(600, 800);
        }
        
        int ppot_index = getInventoryIndex(pray_pots);
        if (ppot_index != -1 && ppot_count > 0 && getCurrentLevel(SKILL_PRAY) <= pray_at) {
            if (getInventoryIndex(485) != -1)
            {
                useItem(getInventoryIndex(485));
            } 
            else if (getInventoryIndex(484) != -1)
            {
                useItem(getInventoryIndex(484));
            } 
            else
            {
                useItem(getInventoryIndex(PPOT_FULL));
            }
            return random(600, 800);
        }
        
        if (walk_inside_bank) {
            if (is_underground()) {
                castOnSelf(FALLY_TELE);
                return random(1000, 2000);
            }
            if (isPrayerEnabled(PARA_MONSTER)) {
                disablePrayer(PARA_MONSTER);
                return random(300, 400);
            }
            int ball = getInventoryIndex(GNOME_BALL);
            if (ball != -1) {
                System.out.println("oh hey a gnomeball");
                dropItem(ball);
                return random(2000, 3000);
            }
            if (getFatigue() >= sleep_at_bank) {
                int bag = getInventoryIndex(SLEEPING_BAG);
                if (bag != -1) {
                    useItem(bag);
                    return random(1000, 2000);
                }
            }
            if (getCurrentLevel(SKILL_HP) < min_bank_hp) {
                int count = getInventoryCount();
                for (int i = 0; i < count; ++i) {
                    if ("eat".equals(getItemCommand(i).toLowerCase(Locale.ENGLISH))) {
                        useItem(i);
                        return random(600, 800);
                    }
                }
            }
            if (getCurrentLevel(SKILL_PRAY) < min_bank_pray) {
                int pot = getInventoryIndex(pray_pots);
                if (pot != -1) {
                    useItem(pot);
                    return random(600, 800);
                }
            }
            if (getX() >= BANK_X) {
                int[] npc = getNpcByIdNotTalk(BANKERS);
                if (npc[0] != -1) {
                    talkToNpc(npc[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            }
            if (!isWalking()) {
                int[] door = getObjectById(BANK_DOOR_CLOSED);
                if (door[0] != -1 && distanceTo(door[1], door[2]) < 20) {
                    if (!isAtApproxCoords(door[1], door[2], 4)) {
                        int x, y;
                        do {
                            x = door[1] + random(-3, 3);
                            y = door[2] + random(-3, 3);
                        } while (!isReachable(x, y));
                        walkTo(x, y);
                    } else {
                        atObject(door[1], door[2]);
                    }
                } else {
                    walkTo(BANK_X, BANK_Y);
                }
            }
            return random(1000, 2000);
        }
        
        if (target_npc == KBD)
        {
            if (getY() == KBD_GATE_ABOVE_Y) {
                if (getX() == KBD_GATE_ABOVE_X_W) {
                    pw.setPath(null);
                    atObject(KBD_GATE_X, KBD_GATE_Y);
                    return random(1000, 2000);
                } else if (isAtApproxCoords(KBD_LADDER_X, KBD_LADDER_Y, 3)) {
                    atObject(KBD_LADDER_X, KBD_LADDER_Y);
                }
            }
        }
        else
        {
            if (isAtApproxCoords(LADDER_DOWN_X, LADDER_DOWN_Y, 2)) {
                pw.setPath(null);
                atObject(LADDER_DOWN_X, LADDER_DOWN_Y);
                return random(1000, 2000);
            }
            
            if (getY() == MEMB_GATE_WALK_Y) {
                if (getX() == MEMB_GATE_WALK_X_E) {
                    atObject(MEMB_GATE_X, MEMB_GATE_Y);
                    return random(1000, 2000);
                } else if (getX() == MEMB_GATE_WALK_X_W) {
                    pw.setPath(gate_to_ladder);
                }
            }
        }

        if (pw.walkPath()) return 0;
        
        if (is_underground()) {
            
            for (int id : pickup_equipment) {
                if (getInventoryCount(id) != 1) continue;
                int index = getInventoryIndex(id);
                if (index == -1) continue;
                if (!isItemEquipped(index)) {
                    wearItem(index);
                    return random(600, 800);
                }
            }
            
            if (target_npc == KBD)
            {
                if (isAtApproxCoords(KBD_LADDER_DOWN_X, KBD_LADDER_DOWN_Y, 6)) {
                    atObject(KBD_GO_TO_LEVER_X, KBD_GO_TO_LEVER_Y);
                    return random(1000, 2000);
                }
            } 
            else
            {
                if (getX() == DUNG_DOOR_WALK_X_E && getY() == DUNG_DOOR_WALK_Y) {
                    int key = getInventoryIndex(DUSTY_KEY);
                    if (key == -1) {
                        return _end("uh oh, no key...");
                    }
                    int[] door = getWallObjectById(DUNG_DOOR_ID);
                    if (door[0] == -1) {
                        return _end("uh oh, no door...");
                    }
                    useItemOnWallObject(key, door[1], door[2]);
                    for (int i = 0; i < dung_path.length; ++i) {
                        Point p = dung_path[i];
                        if (p.x == DUNG_DOOR_WALK_X_W && p.y == DUNG_DOOR_WALK_Y) {
                            path_index = i - 1;
                            break;
                        }
                    }
                    return random(600, 800);
                }
                
                if (path_index >= 0) {
                    if ((System.currentTimeMillis() - last_moved) >= max_stand &&
                            System.currentTimeMillis() >= (last_hop + min_hop_time)) {
                        if (!inCombat())
                        {
                            _hop();
                        }
                        return random(2000, 3000);
                    }
                    boolean changed = false;
                    int x = dung_path[path_index].x;
                    int y = dung_path[path_index].y;
                    if (getX() == x && getY() == y) {
                        --path_index;
                        changed = true;
                    }
                    if (path_index >= 0) {
                        x = dung_path[path_index].x;
                        y = dung_path[path_index].y;
                        if (!isWalking() || changed) {
                            walkTo(x, y);
                        }
                        return random(600, 900);
                    }
                }
                
                if (isAtApproxCoords(LADDER_UP_X, LADDER_UP_Y, 2)) {
                    path_index = dung_path.length - 1;
                    return 0;
                }
            }

            if (getWorld() != starting_world)
            {
                hop(starting_world);
            }

            int[] item = getItemById(pickup_ids);
            if (item[0] != -1 && should_take_item(item)) {
                if (bank_dbones.getState())
                {
                    if (getInventoryIndex(food_id) != -1) {
                        useItem(getInventoryIndex(food_id));
                    }
                }
                pickupItem(item[0], item[1], item[2]);
                return random(600, 800);
            }

            if ((food_count > 0 && food_index == -1 && !inCombat())
                    || (ppot_count > 0 && ppot_index == -1 && !inCombat())) {
                castOnSelf(FALLY_TELE);
                walk_inside_bank = true;
                return random(1000, 2000);
            }
            
            if (ppot_count > 0 && in_fight_area() && inCombat() && !isPrayerEnabled(PARA_MONSTER)) {
                enablePrayer(PARA_MONSTER);
                return random(300, 400);
            }
            int[] npc = getNpcById(target_npc);
            if (!inCombat() && isPrayerEnabled(PARA_MONSTER) && npc[1] != getX() && npc[2] != getY() && (System.currentTimeMillis() - last_moved) >= 1000)
            {
                disablePrayer(PARA_MONSTER);
            }
            if(!inCombat() && getInventoryCount(DRAGON_BONE) > 0 && !bank_dbones.getState())
            {
                int bone = getInventoryIndex(DRAGON_BONE);
                if (bone != -1) {
                    useItem(bone);
                    return random(600, 800);
                }
            }
            if (getCurrentLevel(SKILL_ATT) <= min_att) {
                int attpot = getInventoryIndex(att_pots);
                if (attpot != -1) {
                    useItem(attpot);
                    return random(600, 800);
                }
            }
            
            if (getCurrentLevel(SKILL_DEF) <= min_def) {
                int defpot = getInventoryIndex(def_pots);
                if (defpot != -1) {
                    useItem(defpot);
                    return random(600, 800);
                }
            }
            
            if (getCurrentLevel(SKILL_STR) <= min_str) {
                int strpot = getInventoryIndex(str_pots);
                if (strpot != -1) {
                    useItem(strpot);
                    return random(600, 800);
                }
            }
            
            if (drop_vials.getState()) {
                int vial = getInventoryIndex(EMPTY_VIAL);
                if (vial != -1) {
                    dropItem(vial);
                    return random(800, 1200);
                }
            }
            
            if (poisoned)
            {
                int cppot = getInventoryIndex(cp_pots);
                if (cppot != -1) {
                    useItem(cppot);
                    return random(600, 800);
                }
            }

            if (npc[0] != -1) {
                attackNpc(npc[0]);

                if ((System.currentTimeMillis() - last_moved) < 1000)
                {
                    enablePrayer(PARA_MONSTER);
                }
            }

            if ((System.currentTimeMillis() - last_moved) > 1000 && !inCombat())
            {
                if (getX() != WALK_BACK_X && getY() != WALK_BACK_Y && target_npc != KBD)
                {
                    walkTo(WALK_BACK_X, WALK_BACK_Y);
                }
            }

            if(getFatigue() > 98)
            {
                useSleepingBag();
                return 1000;
            }
            return random(600, 800);
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        final int font = 2;
        int x = 325;
        int y = 50;
        final int white = 0xFFFFFF;
        drawString("SAF_Fally_DnD", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        boolean header = false;
        x += 10;
        for (int i = 0; i < pickup_ids.length; ++i) {
            int count = banked_counts[i];
            if (count <= 0) continue;
            if (!header) {
                drawString("Items banked:", x - 10, y, font, white);
                y += 15;
                header = true;
            }
            drawString(getItemNameId(pickup_ids[i]) + ": " + int_format(count) +
                    " (" + per_hour(banked_counts[i]) + "/h)",
                    x, y, font, white);
            y += 15;
        }
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("welcome to runescape")) {
            last_hop = last_moved = System.currentTimeMillis();
        } else if (str.contains("poisioned!")) {
            poisoned = true;
        } else if (str.contains("cure poison potion")) {
            poisoned = false;
        } else if (str.contains("you can't logout")) {
            //poisoned = false;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            try {
                eat_at = get_int(tf_eat_at);
                pray_at = get_int(tf_pray_at);
                food_count = get_int(tf_food_count);
                ppot_count = get_int(tf_ppot_count);
                food_id = get_int(tf_food_id);
                sleep_at_bank = get_int(tf_sleep_at_bank);
                min_bank_hp = get_int(tf_min_bank_hp);
                min_bank_pray = get_int(tf_min_bank_pray);
                min_att = get_int(tf_min_att);
                min_def = get_int(tf_min_def);
                min_str = get_int(tf_min_str);
                max_stand = Long.parseLong(tf_max_stand.getText());

                switch (destination.getSelectedIndex()) 
                {
                    case 0:
                        target_npc = BLUE_DRAGON;
                        dung_path = dung_path_blue_dragon;
                        WALK_BACK_X = 370;
                        WALK_BACK_Y = 3354;
                        break;
                    case 1:
                        target_npc = BLACK_DEMON;
                        dung_path = dung_path_black_demon;
                        WALK_BACK_X = 381;
                        WALK_BACK_Y = 3371;
                        break;
                    case 2:
                        target_npc = BLACK_DRAGON;
                        dung_path = dung_path_black_dragon;
                        WALK_BACK_X = 409;
                        WALK_BACK_Y = 3344;
                        break;
                    case 3:
                        target_npc = KBD;
                        dung_path = dung_path_black_dragon;
                        break;
                }
                    
                pw.init(null);
                bank_to_kbdgate = pw.calcPath(
                    BANK_X, BANK_Y,
                    KBD_GATE_ABOVE_X_W, KBD_GATE_ABOVE_Y
                );
                bank_to_gate = pw.calcPath(
                    BANK_X, BANK_Y,
                    MEMB_GATE_WALK_X_E, MEMB_GATE_WALK_Y
                );
                gate_to_ladder = pw.calcPath(
                    MEMB_GATE_WALK_X_W, MEMB_GATE_WALK_Y,
                    LADDER_DOWN_WALK_X, LADDER_DOWN_WALK_Y
                );
                lumby_to_fally = pw.calcPath(
                    LUMB_X, LUMB_Y,
                    BANK_X, BANK_Y
                );
                
                path_index = -1;
                menu_time = -1L;
                bank_time = -1L;
                walk_inside_bank = false;
                start_time = -1L;

                starting_world = getWorld();
                
                Arrays.fill(banked_loot, false);
                Arrays.fill(banked_counts, 0);
            } catch (Throwable t) {
                System.out.println("Error parsing field. Script cannot start. Check your inputs.");
            }
        }
        frame.setVisible(false);
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
                if (veteran.getState())
                    hop(1);
                else
                    hop(2);
                break;
        }
    }
    
    private void print_out() {
        System.out.println("Runtime: " + get_runtime());
        boolean header = false;
        for (int i = 0; i < pickup_ids.length; ++i) {
            int count = banked_counts[i];
            if (count <= 0) continue;
            if (!header) {
                System.out.println("Items banked:");
                header = true;
            }
            System.out.println(getItemNameId(pickup_ids[i]) + ": " + int_format(count) +
                    " (" + per_hour(banked_counts[i]) + "/h)");
        }
    }
    
    private static int get_int(TextField tf) {
        return Integer.parseInt(tf.getText());
    }
    
    private int _end(String message) {
        print_out();
        System.out.println(message);
        stopScript(); setAutoLogin(false);
        return 0;
    }
    
    private boolean should_take_item(int[] item) {
        if (getInventoryCount() == MAX_INV_SIZE) {
            for (int i = 0; i < rare_drops.length; ++i)
            {
                if (item[0] == rare_drops[i])
                {
                    if (getInventoryIndex(food_id) != -1) {
                        useItem(getInventoryIndex(food_id));
                        return true;
                    }

                    if (getInventoryIndex(DRAGON_BONE) != -1)
                    {
                        useItem(getInventoryIndex(DRAGON_BONE));
                        return true;
                    }
                }
            }
            if (!isItemStackableId(item[0]) || getInventoryIndex(item[0]) == -1) {
                return false;
            }
        }
        if (item[0] == -1) return false;
        if (item[1] == getX() && item[2] == getY()) {
            return true;
        }
        if (item[0] == 10)
        {
            return false;
        }
        if (in_fight_area(item[1], item[2], target_npc)) {
            return true;
        }
        return false;
    }
    
    private boolean is_underground() {
        return is_underground(getX(), getY());
    }
    
    private static boolean is_underground(int x, int y) {
        return y > 1000;
    }
    
    private boolean in_fight_area() {
        return in_fight_area(getX(), getY(), target_npc);
    }
    
    private static boolean in_fight_area(int x, int y, int npc) {
        switch(npc) {
            case BLUE_DRAGON:
                return x > 355 && x < 375 && y > 3345 && y < 3360;
            case BLACK_DEMON:
                return x > 375 && x < 397 && y > 3355 && y < 3380;
            case BLACK_DRAGON:
                return x > 380 && x < 430 && y > 3323 && y < 3353;
            case KBD:
                return x > 562 && x < 572 && y > 3316 && y < 3332;
        }
        return false;
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
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 3600L) {
            return int_format((secs / 3600L)) + " hours, " +
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