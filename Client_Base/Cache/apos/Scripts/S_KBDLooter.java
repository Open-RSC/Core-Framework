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

public final class S_KBDLooter extends Script
    implements ActionListener {
    
    private String[] other_traders = {};
    private final Checkbox autobury = new Checkbox("Autobury", false);
    private final Checkbox veteran = new Checkbox("Veteran", true);
    private final Choice combat_style = new Choice();
    private int world = 1;
    
    private final TextField killer = new TextField();
    private final TextField tf_other_traders = new TextField();
    private final TextField tf_world = new TextField(String.valueOf(world));
    
    private static final int
    SLEEPING_BAG = 1263,
    MIN_SHARKS = 10,
    PRAYER = 5,
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
    DSTONE_CHARGED = 597,
    DSTONE_UNCHARGED = 522,
    BONES = 814,
    SHARK_W_COUNT = 20,
    SUPER_ATT = 486,
    SUPER_DEF = 495,
    SUPER_STR = 492,
    SHARK = 546,
    KBD_INNER_LEVER_X = 567,
    KBD_INNER_LEVER_Y = 3330,
    KBD_GATE = 508,
    KBD_LADDER_DOWN = 6,
    KBD_LEVER_IN = 487,
    BANK_DOOR_CLOSED = 64;
    
    private static final int[] loot = {
        1277, 795, 400, 402, 403, 404, 81, 93, 75, 1092, 405, 31, 33, 38, 41,
        42, 619, 11, 638, 408, 517, 520, 711, 526, 527, 542, SHARK, BONES, DSTONE_UNCHARGED, 523
    };
    
    private final boolean[] banked = new boolean[loot.length];
    private final int[] banked_counts = new int[loot.length];
    
    private static final int[] poison_pots = {
        569, 570, 571, 566, 567, 568
    };
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");
    
    private int inv_inform_stage;
    
    private boolean needs_sap;
    private boolean traded_sap;
    
    private boolean needs_sdp;
    private boolean traded_sdp;
    
    private boolean needs_ssp;
    private boolean traded_ssp;
    
    private int needed_sharks;
    private boolean traded_sharks;
    
    private boolean answer_trade;
    
    private long start_time;
    private long menu_time;
    private long bank_time;
    private long move_time;
    private boolean poisoned;
    private PathWalker pw;
    private PathWalker.Path edge_through_wild;
    private PathWalker.Path lumb_to_dray;

    private int pray_xp;

    private int start_pray_xp;

    private boolean idle_move_dir;
    
    private Frame frame;

    public S_KBDLooter(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new S_KBDLooter(null).init(null);
    }

    @Override
    public void init(String params) {
        if (frame == null) {
            for (String str : FIGHTMODES) {
                combat_style.add(str);
            }
            
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < other_traders.length; ++i) {
                b.append(other_traders[i]);
                if (i != (other_traders.length - 1)) {
                    b.append(',');
                }
            }
            tf_other_traders.setText(b.toString());
            
            Panel grid = new Panel(new GridLayout(0, 2, 0, 2));
            grid.add(new Label("Combat style"));
            grid.add(combat_style);
            grid.add(new Label("Killer"));
            grid.add(killer);
            grid.add(new Label("Other traders (,)"));
            grid.add(tf_other_traders);
            grid.add(new Label("World"));
            grid.add(tf_world);
            
            Panel button_pane = new Panel();
            Button button = new Button("OK");
            button.addActionListener(this);
            button_pane.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            button_pane.add(button);
            
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            frame.add(grid);
            frame.add(autobury);
            frame.add(veteran);
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
            walkTo(getX(), getY());
            return random(300, 400);
        }
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
            for (int i = 0; i < loot.length; ++i) {
                if (loot[i] == SHARK) continue;
                int count = getInventoryCount(loot[i]);
                if (count > 0) {
                    if (!banked[i]) {
                        banked[i] = true;
                        banked_counts[i] += count;
                    }
                    deposit(loot[i], count);
                    return random(600, 800);
                }
            }
            
            int pp_count = getInventoryCount(poison_pots);
            if (pp_count <= 0) {
                for (int id : poison_pots) {
                    int bc = bankCount(id);
                    if (bc <= 0) continue;
                    withdraw(id, 1);
                    return random(1500, 2000);
                }
                System.out.println("No antipoisons left, braving the trip without :( ");
            } else if (pp_count > 1) {
                for (int id : poison_pots) {
                    int count = getInventoryCount(id);
                    if (count > 0) {
                        deposit(id, count);
                        return random(1000, 1500);
                    }
                }
            }
            
            if (getInventoryIndex(SUPER_ATT) == -1) {
                if (bankCount(SUPER_ATT) <= 0) {
                    return _end("No super atts left :(");
                }
                withdraw(SUPER_ATT, 1);
                return random(1500, 2000);
            }
            
            if (getInventoryIndex(SUPER_DEF) == -1) {
                if (bankCount(SUPER_DEF) <= 0) {
                    return _end("No super defs left :(");
                }
                withdraw(SUPER_DEF, 1);
                return random(1500, 2000);
            }
            
            if (getInventoryIndex(SUPER_STR) == -1) {
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
            
            if (autobury.getState() && getInventoryIndex(SLEEPING_BAG) == -1) {
                if (bankCount(SLEEPING_BAG) > 0) {
                    withdraw(SLEEPING_BAG, 1);
                    return random(1000, 1500);
                } else {
                    System.out.println("Out of sleeping bags");
                }
            }
            
            int shark_w = SHARK_W_COUNT - getInventoryCount(SHARK);
            int empty = getEmptySlots();
            if (shark_w > empty) {
                shark_w = empty;
            }
            if (bankCount(SHARK) < shark_w) {
                return _end("No sharks left :( ");
            }
            if (shark_w > 0) {
                withdraw(SHARK, shark_w);
                return random(1000, 1500);
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
            answer_trade = false;
            int index = getInventoryIndex(SUPER_ATT);
            if (needs_sap && !traded_sap && index != -1) {
                traded_sap = true;
                offerItemTrade(index, 1);
                return random(1000, 1500);
            }
            index = getInventoryIndex(SUPER_DEF);
            if (needs_sdp && !traded_sdp && index != -1) {
                traded_sdp = true;
                offerItemTrade(index, 1);
                return random(1000, 1500);
            }
            index = getInventoryIndex(SUPER_STR);
            if (needs_ssp && !traded_ssp && index != -1) {
                traded_ssp = true;
                offerItemTrade(index, 1);
                return random(1000, 1500);
            }
            if (needed_sharks > 0 && !traded_sharks) {
                traded_sharks = true;
                int count = getInventoryCount(SHARK);
                if (needed_sharks > count) {
                    needed_sharks = count;
                }
                if (needed_sharks > 0) {
                    offerItemTrade(getInventoryIndex(SHARK), needed_sharks);
                    return random(1000, 1500);
                }
            }
            acceptTrade();
            return random(600, 800);
        }
        traded_sap = false;
        traded_sdp = false;
        traded_ssp = false;
        traded_sharks = false;
        if (move_time != -1L) {
            return idle_move();
        }
        if (in_wild() && has_bad_players()) {
            pw.resetWait();
            _hop();
            return random(2000, 3000);
        }
        if ((inv_inform_stage--) > 0) {
            sendPrivateMessage("I have " + getEmptySlots() + " empty slots.", killer.getText());
            return random(1000, 1500);
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
            if (!should_bank()) {
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
            if (!should_bank()) {
                int ammy = getInventoryIndex(DSTONE_CHARGED);
                if (ammy != -1) {
                    useItem(ammy);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
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
            if (getX() != KBD_INNER_LEVER_X || getY() != KBD_INNER_LEVER_Y) {
                walkTo(KBD_INNER_LEVER_X, KBD_INNER_LEVER_Y);
                return random(800, 1500);
            }
            
            if (poisoned) {
                int index = getInventoryIndex(poison_pots);
                if (index != -1) {
                    useItem(index);
                    return random(800, 1200);
                }
            }
            
            if (getWorld() != world) {
                hop(world);
                return random(2000, 3000);
            }
            
            if (autobury.getState()) {
                if (getFatigue() >= 95) {
                    int bag = getInventoryIndex(SLEEPING_BAG);
                    if (bag != -1) {
                        useItem(bag);
                        return random(1000, 2000);
                    }
                } else {
                    int index = getInventoryIndex(BONES);
                    if (index != -1) {
                        useItem(index);
                        return random(600, 800);
                    }
                }
            }
            
            if (should_bank()) {
                int ammy = getInventoryIndex(DSTONE_CHARGED);
                if (ammy != -1) {
                    useItem(ammy);
                    menu_time = System.currentTimeMillis();
                    return random(600, 800);
                }
            }
            
            if (answer_trade) {
                int[] player = getPlayerByName(killer.getText());
                if (player[0] != -1) {
                    sendTradeRequest(getPlayerPID(player[0]));
                    return random(1000, 1500);
                }
            }
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        final int font = 2;
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S KBD Looter", x, y, font, white);
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
            System.out.println("Cured poison");
        } else if (str.contains("poisioned")) {
            poisoned = true;
            System.out.println("Got poisoned on tile " + getX() + "," + getY());
            System.out.println("Spider room (expected): " + in_spider_area() + " | kbd room (unexpected): " + in_fight_area());
        } else if (str.contains("enough inventory space to receive")) {
            inv_inform_stage = 2;
        }
    }
    
    @Override
    public void onPrivateMessage(String content, String name, boolean m, boolean jm) {
        if (name.equals(killer.getText())) {
            needs_sap = needs_sdp = needs_ssp = false;
            needed_sharks = 0;
            String[] split = content.split(" ");
            for (int i = 0; i < split.length; ++i) {
                String str = split[i];
                if ("sap".equals(str)) {
                    needs_sap = true;
                } else if ("sdp".equals(str)) {
                    needs_sdp = true;
                } else if ("ssp".equals(str)) {
                    needs_ssp = true;
                } else if (str.startsWith("shark")) {
                    try {
                        needed_sharks = Integer.parseInt(split[i - 1]);
                    } catch (Throwable t) {
                        t.printStackTrace();
                        needed_sharks = 9;
                    }
                }
            }
        }
    }
    
    @Override
    public void onTradeRequest(String name) {
        if (name.equals(killer.getText())) {
            if (!answer_trade) {
                inv_inform_stage = 2;
                answer_trade = true;
            }
        }
    }
    
    private boolean should_bank() {
        if (getInventoryCount(SHARK) < MIN_SHARKS ||
                getInventoryIndex(SUPER_ATT) == -1 ||
                getInventoryIndex(SUPER_DEF) == -1 ||
                getInventoryIndex(SUPER_STR) == -1 ||
                getInventoryIndex(DSTONE_CHARGED) == -1) {
            return true;
        }
        return false;
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

    private boolean in_wild() {
        return in_wild(getX(), getY());
    }
    
    private static boolean in_wild(int x, int y) {
        return in_spider_area(x, y) || y < 427;
    }

    private boolean has_bad_players() {
        int count = countPlayers();
        if (count <= 1) return false;
        for (int i = 1; i < count; ++i) {
            String name = getPlayerName(i);
            if (!name.equals(killer.getText()) && !contains(other_traders, name)) {
                System.out.println("Detected unknown player " + name + "!");
                return true;
            }
        }
        return false;
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
            System.out.println("Banked " + banked_counts[i] + " " + getItemNameId(loot[i]) + " (" + per_hour(banked_counts[i]) + "/h)");
        }
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

    private boolean object_valid(int[] object) {
        return object[0] != -1 && distanceTo(object[1], object[2]) < 40;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            try {
                String str = tf_other_traders.getText();
                if ("".equals(str)) {
                    other_traders = new String[0];
                } else {
                    other_traders = str.split(",");
                }
                world = Integer.parseInt(tf_world.getText());
            } catch (Throwable t) {
                System.out.println("Error parsing fields, check your inputs");
                return;
            }
            
            pw.init(null);
            lumb_to_dray = pw.calcPath(LUMB_X, LUMB_Y, DRAY_BANK_X, DRAY_BANK_Y);
            edge_through_wild = pw.calcPath(EDGE_BANK_X, EDGE_BANK_Y, KBD_NEAR_GATE_X, KBD_NEAR_GATE_Y);
            needs_sap = traded_sap =
            needs_sdp = traded_sdp =
            needs_ssp = traded_ssp =
            answer_trade = poisoned = traded_sharks = false;
            menu_time = move_time = bank_time = start_time = -1L;
            needed_sharks = inv_inform_stage = 0;
            Arrays.fill(banked_counts, 0);
            Arrays.fill(banked, false);
        }
        frame.setVisible(false);
    }
}