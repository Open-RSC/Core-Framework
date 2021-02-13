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
 * Magic fatigue.
 *   
 * - Crandor isle
 * - Wind bolts hob goblins till 99%
 * - Kills Lesser demon, hops worlds if none spawned.
 * - Teles to ardounge to bank
 * // market bank
 * /// goes to karamja through the ship on ard port.
 * //// goes to volcano, pushes oddlooking wall and goes to crandor from there.
 * (when banking, doesnt sleep because many hostile NPCs on the route)
 * 
 * air staff!
 * you should be able to change the spell this uses by editing "WIND_BOLT", "STAFF_OF_AIR", "runes", "rune_req_counts"
 * 
 * @author Storm
 */
public final class S_FatigueCrandor extends Script
    implements ActionListener {
    
    // d sword, d axe. have only ONE.
    private static final int[] weapons = {
        593, 594
    };
    
    private boolean kill_giants = true;
    
    // properly calculated
    private double min_fatigue = kill_giants ? 98.92 : 98.665;
    
    private static final int
    LESSER_DEMON = 22,
    MOSS_GIANT = 104,
    HOBGOBLIN = 67,
    WIND_BOLT = 8,
    ATT = 0, DEF = 1, STR = 2,
    HITS = 3,
    MAGIC = 6,
    ARDY_TELE = 26,
    STAIRS_UP = 41,
    STAIRS_UP_APPROX_X = 419,
    STAIRS_UP_APPROX_Y = 3463,
    STAIRS_DOWN = 42,
    S_WALL_APPROX_X = 406,
    S_WALL_APPROX_Y = 3518,
    S_WALL_AFTER_APPROX_X = S_WALL_APPROX_X,
    S_WALL_AFTER_APPROX_Y = S_WALL_APPROX_Y - 1,
    LADDER_UP_APPROX_X = 421,
    LADDER_UP_APPROX_Y = 3527,
    STRANGE_WALL = 58,
    LADDER_DOWN = 6,
    MEMBERS_GATE = 254,
    GATE_APPROX_X = 435,
    GATE_APPROX_Y = 682,
    SHIP = 157,
    B_DOCK_APPROX_X = 467,
    B_DOCK_APPROX_Y = 657,
    STAFF_OF_AIR = 101,
    BANK_X = 551,
    BANK_Y = 612,
    TELE_X = 588,
    TELE_Y = 621,
    COINS = 10,
    COINS_NEEDED = 30,
    RUNE_CHAIN = 400,
    RUNE_KITE = 404,
    BANK_DOOR_CLOSED = 64;
    
    private static final int[] lewt = {
        399, 1277, 526, 527, 542
    };
    
    private final boolean[] banked = new boolean[lewt.length];
    private final int[] banked_counts = new int[lewt.length];
    
    private static final int[] runes = {
        41, 32, 42
    };
    
    private static final int[] rune_wd_counts = {
        500, 2, 2
    };
    
    private static final int[] rune_req_counts = {
        1, 2, 2
    };
    
    private static final long MAX_STAND = 8000L;
    
    private int min_hp = 50;
    private int food_wd_id = 546;
    private int food_wd_count = 10;
    
    private final TextField
    tf_min_hp = new TextField(String.valueOf(min_hp)),
    tf_food_wd_id = new TextField(String.valueOf(food_wd_id)),
    tf_food_wd_count = new TextField(String.valueOf(food_wd_count));

    private boolean use_ship;
    private long arrive_time;
    private long bank_time;
    private long menu_time;
    private long last_moved;
    private long start_time;
    private long last_hop;
    
    private int last_x;
    private int last_y;

    private int att, def, str, hp, magic;
    
    private int start_att;
    private int start_str;
    private int start_def;
    private int start_hp;
    private int start_magic;

    private int trips;

    private final Checkbox veteran = new Checkbox("Veteran", false);
    private final Choice cb_style = new Choice();

    private final DecimalFormat int_format = new DecimalFormat("#,##0");

    private static final long MIN_HOP = 5000L;
    
    private final PathWalker pw;
    private PathWalker.Path tele_to_bank;
    private PathWalker.Path brim_dock_to_gate;
    private PathWalker.Path ladder_to_wall;
    private PathWalker.Path wall_to_stairs;
    
    private static final Point[] safespots = {
        new Point(416, 610), new Point(417, 610), new Point(418, 610),
        new Point(419, 610), new Point(420, 611), new Point(421, 613),
        new Point(422, 617), new Point(423, 617), new Point(424, 618)
    };
    
    private Frame frame;
    
    public static void main(String[] argv) {
        new S_FatigueCrandor(null).init(null);
    }

    public S_FatigueCrandor(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        if (frame == null) {
            for (String str : FIGHTMODES) {
                cb_style.add(str);
            }
            cb_style.select(1);
            
            Panel button_pane = new Panel();
            Button button = new Button("OK");
            button.addActionListener(this);
            button_pane.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            button_pane.add(button);
            
            Panel grid_pane = new Panel(new GridLayout(0, 2, 0, 2));
            grid_pane.add(new Label("Combat style (NOT controlled)"));
            grid_pane.add(cb_style);
            grid_pane.add(new Label("Eat at"));
            grid_pane.add(tf_min_hp);
            grid_pane.add(new Label("Food withdraw id"));
            grid_pane.add(tf_food_wd_id);
            grid_pane.add(new Label("Food withdraw count"));
            grid_pane.add(tf_food_wd_count);
            
            frame = new Frame(getClass().getSimpleName());
            frame.setIconImages(Constants.ICONS);
            frame.addWindowListener(new StandardCloseHandler(frame, StandardCloseHandler.HIDE));
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            frame.add(grid_pane);
            frame.add(veteran);
            frame.add(button_pane);
            frame.setResizable(false);
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
        frame.toFront();
        frame.requestFocus();
        frame.setVisible(true);
        start_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            setTrickMode(true);
            
            Arrays.fill(banked, false);
            Arrays.fill(banked_counts, 0);
            
            arrive_time = bank_time = menu_time = -1;
            last_hop = last_moved = start_time = System.currentTimeMillis();
            
            start_att = att = getXpForLevel(ATT);
            start_def = def = getXpForLevel(DEF);
            start_str = str = getXpForLevel(STR);
            start_hp = hp = getXpForLevel(HITS);
            start_magic = magic = getXpForLevel(MAGIC);
            
            trips = 0;
        } else {
            int x = getX(); int y = getY();
            if (x != last_x || y != last_y) {
                last_x = x;
                last_y = y;
                last_moved = System.currentTimeMillis();
            }
            att = getXpForLevel(ATT);
            def = getXpForLevel(DEF);
            str = getXpForLevel(STR);
            hp = getXpForLevel(HITS);
            magic = getXpForLevel(MAGIC);
        }
        
        int cs = cb_style.getSelectedIndex();
        if (getFightMode() != cs) {
            setFightMode(cs);
            return random(400, 600);
        }
        
        if (inCombat()) {
            last_moved = System.currentTimeMillis();
            pw.resetWait();
            int[] target = getAllNpcById(kill_giants ? MOSS_GIANT : LESSER_DEMON);
            if (getCurrentLevel(HITS) < min_hp ||
                    getAccurateFatigue() < min_fatigue ||
                    !is_on_crandor() ||
                    target[0] == -1 || !isNpcInCombat(target[0]) ||
                    target[1] != getX() ||
                    target[2] != getY()) {
                walkTo(getX(), getY());
            } else {
                int weap = getInventoryIndex(weapons);
                if (weap != -1 && !isItemEquipped(weap)) {
                    wearItem(weap);
                    return random(800, 1200);
                }
            }
            return random(400, 600);
        }
        
        if (getCurrentLevel(HITS) < min_hp) {
            int index = get_food_index();
            if (index != -1) {
                useItem(index);
                return random(600, 800);
            }
        }
        
        if (isQuestMenu()) {
            String[] options = questMenuOptions();
            int count = questMenuCount();
            for (int i = 0; i < count; ++i) {
                options[i] = options[i].toLowerCase(Locale.ENGLISH);
                if (should_bank()) {
                    if (options[i].contains("access")) {
                        answer(i);
                        bank_time = System.currentTimeMillis();
                        break;
                    }
                } else {
                    if (options[i].contains("yes please")) {
                        answer(i);
                        arrive_time = System.currentTimeMillis();
                        break;
                    }
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
            for (int i = 0; i < lewt.length; ++i) {
                int id = lewt[i];
                if (id == COINS || id == RUNE_KITE || id == RUNE_CHAIN || inArray(runes, id)) {
                    continue;
                }
                int count = getInventoryCount(id);
                if (count <= 0) continue;
                if (!banked[i]) {
                    banked_counts[i] += count;
                    banked[i] = true;
                }
                deposit(id, count);
                return random(600, 800);
            }
            
            int cc = getInventoryCount(COINS);
            if (cc < COINS_NEEDED) {
                int w = COINS_NEEDED - cc;
                if (w > 0) {
                    if (getEmptySlots() <= 0 && getInventoryIndex(COINS) == -1) {
                        return _end("invalid inventory, can't withdraw coins");
                    }
                    if (bankCount(COINS) <= w) {
                        return _end("Out of coins");
                    }
                    withdraw(COINS, w);
                    return random(600, 800);
                }
            }
            
            for (int i = 0; i < runes.length; ++i) {
                int count = getInventoryCount(runes[i]);
                if (count >= rune_wd_counts[i]) {
                    continue;
                }
                int w = rune_wd_counts[i] - count;
                if (w > 0) {
                    if (getEmptySlots() <= 0 && getInventoryIndex(runes[i]) == -1) {
                        return _end("invalid inventory, can't withdraw runes");
                    }
                    if (bankCount(runes[i]) <= w) {
                        return _end("Out of runes");
                    }
                    withdraw(runes[i], w);
                    return random(800, 1200);
                }
            }
            
            int ifc = get_food_count();
            if (ifc < food_wd_count) {
                int w = food_wd_count - ifc;
                int e = getEmptySlots();
                if (w > e) {
                    w = e;
                }
                if (w > 0) {
                    if (bankCount(food_wd_id) <= w) {
                        return _end("Out of food");
                    }
                    withdraw(food_wd_id, w);
                    return random(1500, 2000);
                }
            }
            Arrays.fill(banked, false);
            closeBank();
            use_ship = true;
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (pw.walkPath()) {
            if ((System.currentTimeMillis() - last_moved) >= MAX_STAND) {
                _hop();
                return random(2000, 3000);
            }
            return 0;
        }
        
        if (arrive_time != -1L) {
            if (System.currentTimeMillis() >= (arrive_time + 10000L)) {
                arrive_time = -1L;
            }
            return 0;
        }
        
        if (isAtApproxCoords(TELE_X, TELE_Y, 10)) {
            pw.setPath(tele_to_bank);
            return 0;
        }
        
        if (inside_bank()) {
            if (!should_bank()) {
                use_ship = true;
                if (!isWalking()) {
                    if (inside_bank()) {
                        int[] door = getObjectById(BANK_DOOR_CLOSED);
                        if (object_valid(door)) {
                            atObject(door[1], door[2]);
                            return random(1000, 1500);
                        }
                    }
                    walkTo(541 + random(-1, 1), 615);
                }
                return random(600, 800);
            }
            int[] npc = getNpcByIdNotTalk(BANKERS);
            if (npc[0] != -1) {
                talkToNpc(npc[0]);
                menu_time = System.currentTimeMillis();
            }
            return random(600, 800);
        }
        
        if (use_ship) {
            if (getY() == 615 && getX() < 543) {
                int weapon = getInventoryIndex(weapons);
                if (weapon != -1 && isItemEquipped(weapon)) {
                    // no unnecessary xp from aggressive monsters, ever.
                    removeItem(weapon);
                } else {
                    int[] ship = getObjectById(SHIP);
                    if (object_valid(ship)) {
                        atObject(ship[1], ship[2]);
                        menu_time = System.currentTimeMillis();
                    }
                }
            } else if (!isWalking()) {
                walkTo(541 + random(-1, 1), 615);
            }
            return random(600, 800);
        }
        
        if (getX() == B_DOCK_APPROX_X && (getY() < B_DOCK_APPROX_Y && getY() > 645)) {
            // on the brimhaven dock
            pw.setPath(brim_dock_to_gate);
            return 0;
        }
        
        if (getX() == GATE_APPROX_X && getY() == GATE_APPROX_Y) {
            // at the members gate
            int[] gate = getObjectById(MEMBERS_GATE);
            if (object_valid(gate)) {
                atObject(gate[1], gate[2]);
            }
            return random(1500, 1800);
        } else {
            int x = getX();
            int y = getY();
            if (x > 422 && x < 435 && y > 681 && y < 696 ) {
                // on karamja, past the members gate
                if (isAtApproxCoords(422, 694, 5)) {
                    // at the volcano ladder
                    int[] ladder = getObjectById(LADDER_DOWN);
                    if (object_valid(ladder)) {
                        atObject(ladder[1], ladder[2]);
                    } else {
                        System.out.println("invalid ladder?");
                    }
                } else if (!isWalking()) {
                    walk_approx(422, 694, 1);
                }
                return random(1000, 2000);
            }
        }
        
        if (isAtApproxCoords(LADDER_UP_APPROX_X, LADDER_UP_APPROX_Y, 3)) {
            // just gone down the volcano ladder
            pw.setPath(ladder_to_wall);
            return 0;
        }
        
        if (getX() == S_WALL_APPROX_X && getY() == S_WALL_APPROX_Y) {
            // strange wall entrance
            int[] wall = getWallObjectById(STRANGE_WALL);
            if (wall[0] != -1) {
                atWallObject(wall[1], wall[2]);
            }
            return random(1000, 1500);
        }
        
        if (getX() == S_WALL_AFTER_APPROX_X && getY() == S_WALL_AFTER_APPROX_Y) {
            // strange wall -> crandor walk
            pw.setPath(wall_to_stairs);
            return 0;
        }
        
        if (isAtApproxCoords(STAIRS_UP_APPROX_X, STAIRS_UP_APPROX_Y, 3)) {
            // going-up-to-crandor-stairs
            if (getFatigue() >= 100) {
                useSleepingBag();
                return random(1000, 2000);
            }
            int[] stairs = getObjectById(STAIRS_UP);
            if (object_valid(stairs)) {
                atObject(stairs[1], stairs[2]);
            }
            return random(1500, 2000);
        }
        
        if (is_on_crandor()) {
            int gic = getGroundItemCount();
            for (int i = 0; i < gic; ++i) {
                int id = getGroundItemId(i);
                int x = getItemX(i);
                int y = getItemY(i);
                if (inArray(lewt, id) && distanceTo(x, y) <= 5 && isReachable(x, y)) {
                    if (getInventoryCount() < MAX_INV_SIZE ||
                            (isItemStackableId(id) && getInventoryIndex(id) != -1)) {
                        
                        pickupItem(id, x, y);
                        return random(600, 1000);
                    } else {
                        int food = get_food_index();
                        if (food != -1) {
                            useItem(food);
                            return random(600, 800);
                        }
                    }
                }
            }
            if (getFatigue() >= 100) {
                if (isAtApproxCoords(419, 627, 3)) {
                    int[] stairs = getObjectById(STAIRS_DOWN);
                    if (object_valid(stairs)) {
                        atObject(stairs[1], stairs[2]);
                    }
                } else if (!isWalking()) {
                    walk_approx(419, 627, 3);
                }
                return random(1000, 1500);
            }
            if (should_bank()) {
                castOnSelf(ARDY_TELE);
                return random(1000, 2000);
            }
            if (getAccurateFatigue() >= min_fatigue) {
                int weap = getInventoryIndex(weapons);
                if (weap != -1 && !isItemEquipped(weap)) {
                    wearItem(weap);
                    return random(800, 1200);
                }
                if (kill_giants) {
                    if (getY() < 619) {
                        if (!isWalking()) {
                            walk_approx(426, 621, 1);
                        }
                        return random(1000, 2000);
                    }
                    if (getY() < 631) {
                        if (!isWalking()) {
                            walk_approx(423, 637, 1);
                        }
                        return random(1000, 2000);
                    }
                }
                int[] npc = getNpcById(kill_giants ? MOSS_GIANT : LESSER_DEMON);
                if (npc[0] != -1) {
                    if (distanceTo(npc[1], npc[2]) > 5) {
                        walkTo(npc[1], npc[2]);
                    } else {
                        attackNpc(npc[0]);
                        // hack to stop disappearing-npc-hop weirdness
                        last_hop = System.currentTimeMillis();
                    }
                } else {
                    _hop();
                }
            } else {
                int staff = getInventoryIndex(STAFF_OF_AIR);
                if (staff != -1 && !isItemEquipped(staff)) {
                    wearItem(staff);
                    return random(800, 1200);
                }
                if (kill_giants) {
                    if (getY() > 622) {
                        if (!isWalking()) {
                            walk_approx(426, 621, 1);
                        }
                        return random(1000, 2000);
                    }
                }
                if (getY() > 618) {
                    if (!isWalking()) {
                        walk_approx(419, 613, 1);
                    }
                    return random(600, 1000);
                }
                int[] npc = get_goblin();
                if (npc[0] != -1) {
                    Point p = get_safespot(npc[1], npc[2]);
                    if (p == null) {
                        p = new Point(416, 610);
                    }
                    boolean is_safe = false;
                    for (Point s : safespots) {
                        if (s.x == getX() && s.y == getY()) {
                            is_safe = true;
                            break;
                        }
                    }
                    if (!is_safe) {
                        walkTo(p.x, p.y);
                    } else {
                        mageNpc(npc[0], WIND_BOLT);
                    }
                } else {
                    _hop();
                }
            }
        }
        return random(600, 1000);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("arrive")) {
            arrive_time = -1L;
            use_ship = false;
            ++trips;
        } else if (str.contains("welcome")) {
            last_hop = last_moved = System.currentTimeMillis();
            pw.resetWait();
        }
    }
    
    @Override
    public void paint() {
        final int font = 2;
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S Crandor Fatigue", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        drawString("Trips: " + trips + " (" + per_hour(trips) + "/h)", x, y, font, white);
        y += 15;
        int gained = att - start_att;
        if (gained > 0) {
            drawString("Attack XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
            y += 15;
        }
        gained = def - start_def;
        if (gained > 0) {
            drawString("Defence XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
            y += 15;
        }
        gained = str - start_str;
        if (gained > 0) {
            drawString("Strength XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
            y += 15;
        }
        gained = hp - start_hp;
        if (gained > 0) {
            drawString("Hits XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
            y += 15;
        }
        gained = magic - start_magic;
        if (gained > 0) {
            drawString("Magic XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
            y += 15;
        }
        boolean header = false;
        x += 10;
        for (int i = 0; i < lewt.length; ++i) {
            int count = banked_counts[i];
            if (count <= 0) continue;
            if (!header) {
                drawString("Items banked:", x - 10, y, font, white);
                y += 15;
                header = true;
            }
            drawString(getItemNameId(lewt[i]) + ": " + int_format(count) +
                    " (" + per_hour(banked_counts[i]) + "/h)",
                    x, y, font, white);
            y += 15;
        }
    }
    
    public void print_out() {
        System.out.println("S Crandor Fatigue");
        System.out.println("Runtime: " + get_runtime());
        System.out.println("Trips: " + trips + " (" + per_hour(trips) + ")");
        int gained = att - start_att;
        if (gained > 0) {
            System.out.println("Attack XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)");
        }
        gained = def - start_def;
        if (gained > 0) {
            System.out.println("Defence XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)");
        }
        gained = str - start_str;
        if (gained > 0) {
            System.out.println("Strength XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)");
        }
        gained = hp - start_hp;
        if (gained > 0) {
            System.out.println("Hits XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)");
        }
        gained = magic - start_magic;
        if (gained > 0) {
            System.out.println("Magic XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)");
        }
        boolean header = false;
        for (int i = 0; i < lewt.length; ++i) {
            int count = banked_counts[i];
            if (count <= 0) continue;
            if (!header) {
                System.out.println("Items banked:");
                header = true;
            }
            System.out.println(getItemNameId(lewt[i]) + ": " + int_format(count) +
                    " (" + per_hour(banked_counts[i]) + "/h)");
        }
    }
    
    private int _end(String reason) {
        System.out.println(reason);
        print_out();
        setAutoLogin(false);
        stopScript();
        return 0;
    }

    private boolean inside_bank() {
        return inside_bank(getX(), getY());
    }
    
    private static boolean inside_bank(int x, int y) {
        return x >= 551 && x <= 554 && y >= 609 && y <= 616; 
    }

    private int get_food_index() {
        int count = getInventoryCount();
        for (int i = 0; i < count; ++i) {
            if ("eat".equals(getItemCommand(i).toLowerCase(Locale.ENGLISH))) {
                return i;
            }
        }
        return -1;
    }
    
    private int get_food_count() {
        int count = getInventoryCount();
        int n = 0;
        for (int i = 0; i < count; ++i) {
            if ("eat".equals(getItemCommand(i).toLowerCase(Locale.ENGLISH))) {
                ++n;
            }
        }
        return n;
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
    
    private boolean object_valid(int[] o) {
        return o[0] != -1 && distanceTo(o[1], o[2]) < 20;
    }

    private void _hop() {
        if ((System.currentTimeMillis() - last_hop) < MIN_HOP) {
            return;
        }
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

    private int[] get_goblin() {
        int[] best = { -1, -1, -1 };
        int best_dist = Integer.MAX_VALUE;
        int count = countNpcs();
        for (int i = 0; i < count; ++i) {
            int id = getNpcId(i);
            int x = getNpcX(i);
            int y = getNpcY(i);
            if (id != HOBGOBLIN) continue;
            if (!can_safespot_npc(x, y)) {
                continue;
            }
            int dist = distanceTo(x, y);
            if (dist < best_dist) {
                best[0] = i;
                best[1] = x;
                best[2] = y;
                best_dist = dist;
            }
        }
        return best;
    }
    
    private static boolean can_safespot_npc(int x, int y) {
        if (x < 416) {
            return true;
        }
        Point p = get_safespot(x, y);
        if (p == null) {
            return false;
        }
        if (y >= p.y) {
            return false;
        }
        return true;
    }
    
    private static Point get_safespot(int x, int y) {
        Point best = null;
        int best_dist = Integer.MAX_VALUE;
        for (Point p : safespots) {
            int dist = distanceTo(x, y, p.x, p.y);
            if (dist < best_dist) {
                best = p;
                best_dist = dist;
            }
        }
        return best;
    }

    private boolean should_bank() {
        for (int i = 0; i < runes.length; ++i) {
            if (getInventoryCount(runes[i]) < rune_req_counts[i]) {
                return true;
            }
        }
        return get_food_index() == -1;
    }
    
    private boolean is_on_crandor() {
        return is_on_crandor(getX(), getY());
    }

    private static boolean is_on_crandor(int x, int y) {
        return y >= 603 && y < 650 && x >= 404 && x <= 430;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            try {
                min_hp = Integer.parseInt(tf_min_hp.getText());
                food_wd_id = Integer.parseInt(tf_food_wd_id.getText());
                food_wd_count = Integer.parseInt(tf_food_wd_count.getText());
            } catch (Throwable t) {
                System.out.println("error parsing fields, check your inputs");
            }
            
            pw.init(null);
            tele_to_bank = pw.calcPath(TELE_X, TELE_Y, BANK_X, BANK_Y);
            brim_dock_to_gate = pw.calcPath(B_DOCK_APPROX_X, B_DOCK_APPROX_Y,
                    GATE_APPROX_X, GATE_APPROX_Y);
            ladder_to_wall = pw.calcPath(LADDER_UP_APPROX_X, LADDER_UP_APPROX_Y,
                    S_WALL_APPROX_X, S_WALL_APPROX_Y);
            wall_to_stairs = pw.calcPath(S_WALL_AFTER_APPROX_X, S_WALL_AFTER_APPROX_Y,
                    STAIRS_UP_APPROX_X, STAIRS_UP_APPROX_Y);
        }
        frame.setVisible(false);
    }
}
