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

public final class S_TavChaosDruids extends Script
    implements ActionListener {
    
    private static final int
    LUMB_X = 128,
    LUMB_Y = 640,
    BANK_X = 328,
    BANK_Y = 552,
    MEMB_GATE_WALK_X_W = 342,
    MEMB_GATE_WALK_X_E = 341,
    MEMB_GATE_WALK_Y = 488,
    MEMB_GATE_X = 341,
    MEMB_GATE_Y = 487,
    LADDER_UP_WALK_X = 376,
    LADDER_UP_WALK_Y = 3351,
    LADDER_UP_X = 376,
    LADDER_UP_Y = 3352,
    LADDER_DOWN_WALK_X = 376,
    LADDER_DOWN_WALK_Y = 521,
    LADDER_DOWN_X = 376,
    LADDER_DOWN_Y = 520,
    DRUIDS_X = 349,
    DRUIDS_Y = 3320,
    CHAOS_DRUID = 270,
    BANK_OPTION = 0,
    ATTACK = 0,
    DEFENCE = 1,
    STRENGTH = 2,
    HITS = 3,
    PRAYER = 5,
    BONES = 20,
    COINS = 10;
    
    private int food_id = 373;
    private int food_wd_count = 0;
    private int world = 3;
    
    private final Choice combat_style = new Choice();
    
    private final Checkbox
    veteran = new Checkbox("Veteran (World 1 access)", true),
    take_low_level = new Checkbox("Take low level herbs (guam, tar, mar)", false),
    autobury = new Checkbox("Bury bones", false),
    sleep = new Checkbox("Sleep", true);
    
    private final TextField
    tf_world = new TextField(String.valueOf(world)),
    tf_food_id = new TextField(String.valueOf(food_id)),
    tf_food_wd_count = new TextField(String.valueOf(food_wd_count));

    
    private static final int[] pickup = {
        31, 32, 33, 34, 35, 36, 37, 38, 40, 41, 42, 46, 70, 157, 158, 159, 160, 619, 825, COINS, 469, 464, 526, 527,
        437, 438, 439, 440, 441, 442, 443, 933, 1277
    };
    
    private static final int[] rare_drops = {
        70, 526, 527, 1277
    };
    
    private final boolean[] banked = new boolean[pickup.length];
    private final int[] banked_count = new int[pickup.length];
    
    // only picked up if another player is watching
    private static final int[] low_level_herbs = {
        165, 435, 436
    };
    
    private final boolean[] banked_ll = new boolean[low_level_herbs.length];
    private final int[] banked_ll_count = new int[low_level_herbs.length];
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");

    private long bank_time;
    private long menu_time;
    private long start_time;
    private long last_moved;
    private long last_hop;
    
    private int last_x;
    private int last_y;
    
    private int pray_xp;
    private int start_pray_xp;
    private int att_xp;
    private int start_att_xp;
    private int def_xp;
    private int start_def_xp;
    private int str_xp;
    private int start_str_xp;
    private int hits_xp;
    private int start_hits_xp;
    
    private final PathWalker pw;
    private PathWalker.Path bank_to_gate;
    private PathWalker.Path gate_to_bank;
    private PathWalker.Path ladder_to_gate;
    private PathWalker.Path gate_to_ladder;
    private PathWalker.Path ladder_to_druids;
    
    private Frame frame;
    
    // pathwalker cannot get this exactly right as of me writing this script
    private static final Point[] druids_to_ladder = {
        new Point(349, 3321), new Point(354, 3319), new Point(359, 3319),
        new Point(364, 3319), new Point(369, 3320), new Point(373, 3323),
        new Point(376, 3327), new Point(376, 3332), new Point(376, 3337),
        new Point(376, 3342), new Point(376, 3347), new Point(376, 3351)
    };
    private int path_index;

    private boolean walking;

    private static final long
    min_hop_time = 5000L,
    max_stand = 10000L;

    public S_TavChaosDruids(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new S_TavChaosDruids(null).init(null);
    }

    @Override
    public void init(String params) {
        if (frame == null) {
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
            
            Panel grid_pane = new Panel(new GridLayout(0, 2, 0, 2));
            grid_pane.add(new Label("Combat style"));
            grid_pane.add(combat_style);
            grid_pane.add(new Label("Food ID"));
            grid_pane.add(tf_food_id);
            grid_pane.add(new Label("Food withdraw count"));
            grid_pane.add(tf_food_wd_count);
            grid_pane.add(new Label("World"));
            grid_pane.add(tf_world);
            
            frame = new Frame(getClass().getSimpleName());
            frame.setIconImages(Constants.ICONS);
            frame.addWindowListener(new StandardCloseHandler(frame, StandardCloseHandler.HIDE));
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            frame.add(grid_pane);
            frame.add(take_low_level);
            frame.add(autobury);
            frame.add(sleep);
            frame.add(veteran);
            frame.add(button_pane);
            frame.setResizable(false);
            frame.pack();
        }
        frame.setLocationRelativeTo(null);
        frame.toFront();
        frame.requestFocus();
        frame.setVisible(true);
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = last_moved = last_hop = System.currentTimeMillis();
            start_pray_xp = pray_xp = getXpForLevel(PRAYER);
            start_att_xp = att_xp = getXpForLevel(ATTACK);
            start_def_xp = def_xp = getXpForLevel(DEFENCE);
            start_str_xp = str_xp = getXpForLevel(STRENGTH);
            start_hits_xp = hits_xp = getXpForLevel(HITS);
        } else {
            if (getX() != last_x || getY() != last_y) {
                last_x = getX();
                last_y = getY();
                last_moved = System.currentTimeMillis();
            }
            pray_xp = getXpForLevel(PRAYER);
            att_xp = getXpForLevel(ATTACK);
            def_xp = getXpForLevel(DEFENCE);
            str_xp = getXpForLevel(STRENGTH);
            hits_xp = getXpForLevel(HITS);
        }
        int target_combat_style = combat_style.getSelectedIndex();
        if (getFightMode() != target_combat_style) {
            setFightMode(target_combat_style);
            return random(400, 600);
        }
        if (inCombat()) {
            if (!in_fight_area()) {
                walkTo(getX(), getY());
            }
            pw.resetWait();
            return random(400, 600);
        }
        if (isQuestMenu()) {
            answer(BANK_OPTION);
            bank_time = System.currentTimeMillis();
            menu_time = -1L;
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            for (int i = 0; i < pickup.length; ++i) {
                int id = pickup[i];
                int count = getInventoryCount(id);
                if (count > 0) {
                    if (!banked[i]) {
                        banked[i] = true;
                        banked_count[i] += count;
                    }
                    deposit(id, count);
                    return random(600, 800);
                }
            }
            for (int i = 0; i < low_level_herbs.length; ++i) {
                int id = low_level_herbs[i];
                int count = getInventoryCount(id);
                if (count > 0) {
                    if (!banked_ll[i]) {
                        banked_ll[i] = true;
                        banked_ll_count[i] += count;
                    }
                    deposit(id, count);
                    return random(600, 800);
                }
            }
            int food_count = getInventoryCount(food_id);
            if (food_count < food_wd_count) {
                int count = food_wd_count - food_count;
                if (bankCount(food_id) < count) {
                    System.out.println("out of food");
                    setAutoLogin(false); stopScript();
                    return 0;
                }
                withdraw(food_id, count);
                return random(1000, 2000);
            } else if (food_count > food_wd_count) {
                deposit(food_id, food_count - food_wd_count);
                return random(1000, 2000);
            }
            closeBank();
            Arrays.fill(banked, false);
            Arrays.fill(banked_ll, false);
            last_moved = System.currentTimeMillis();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (sleep.getState() && getFatigue() >= 95) {
            useSleepingBag();
            return random(1000, 2000);
        }
        if (isAtApproxCoords(LUMB_X, LUMB_Y, 40)) {
            System.out.println("died :(");
            setAutoLogin(false); stopScript();
            return 0;
        }
        if (walking) {
            if ((System.currentTimeMillis() - last_moved) >= max_stand &&
                    System.currentTimeMillis() >= (last_hop + min_hop_time)) {
                
                _hop();
                return random(2000, 3000);
            }
        }
        if (pw.walkPath()) {
            walking = true;
            return 0;
        } else {
            walking = false;
        }
        if (in_bank()) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(600, 800);
            }
            pw.setPath(bank_to_gate);
            return 0;
        }
        if (isAtApproxCoords(LADDER_DOWN_X, LADDER_DOWN_Y, 5)) {
            if (getInventoryCount() != MAX_INV_SIZE) {
                atObject(LADDER_DOWN_X, LADDER_DOWN_Y);
                return random(1000, 2000);
            }
            pw.setPath(ladder_to_gate);
            return 0;
        }
        if (isAtApproxCoords(MEMB_GATE_X, MEMB_GATE_Y, 5)) {
            if (getX() >= MEMB_GATE_WALK_X_W) {
                if (getInventoryCount() == MAX_INV_SIZE) {
                    atObject(MEMB_GATE_X, MEMB_GATE_Y);
                } else {
                    pw.setPath(gate_to_ladder);
                    return 0;
                }
            } else if (getX() <= MEMB_GATE_WALK_X_E) {
                if (getInventoryCount() == MAX_INV_SIZE) {
                    pw.setPath(gate_to_bank);
                    return 0;
                } else {
                    atObject(MEMB_GATE_X, MEMB_GATE_Y);
                }
            }
            return random(1000, 2000);
        }
        if (underground()) {
            if (isAtApproxCoords(LADDER_UP_X, LADDER_UP_Y, 3)) {
                if (getInventoryCount() == MAX_INV_SIZE) {
                    atObject(LADDER_UP_X, LADDER_UP_Y);
                    return random(1000, 2000);
                }
                pw.setPath(ladder_to_druids);
                path_index = druids_to_ladder.length;
                return 0;
            }
            if (path_index < druids_to_ladder.length) {
                if (inCombat()) {
                    walkTo(getX(), getY());
                    return random(400, 600);
                }
                boolean changed = false;
                int x = druids_to_ladder[path_index].x;
                int y = druids_to_ladder[path_index].y;
                if (getX() == x && getY() == y) {
                    ++path_index;
                    changed = true;
                }
                if (path_index < druids_to_ladder.length) {
                    x = druids_to_ladder[path_index].x;
                    y = druids_to_ladder[path_index].y;
                    if (!isWalking() || changed) {
                        walkTo(x, y);
                    }
                    return random(600, 900);
                }
            }
            if (autobury.getState()) {
                int bones = getInventoryIndex(BONES);
                if (bones != -1) {
                    useItem(bones);
                    return random(600, 800);
                }
            }
            int item_count = getGroundItemCount();
            for (int i = 0; i < item_count; ++i) {
                int id = getGroundItemId(i);
                int x = getItemX(i);
                int y = getItemY(i);
                int inv_count = getInventoryCount();
                if (inv_count == MAX_INV_SIZE && inArray(rare_drops, id)) {
                    for (int j = 0; j < inv_count; ++j) {
                        int iid = getInventoryId(j);
                        if (!inArray(rare_drops, iid) && inArray(pickup, iid)) {
                            dropItem(j);
                            return random(1500, 2000);
                        }
                    }
                }
                if (inArray(pickup, id) && should_take(id, x, y)) {
                    pickupItem(id, x, y);
                    return random(600, 800);
                }
            }
            if (getInventoryCount() == MAX_INV_SIZE) {
                path_index = 0;
                return 0;
            }
            int[] item = getItemById(pickup);
            if (should_take(item[0], item[1], item[2])) {
                pickupItem(item[0], item[1], item[2]);
                return random(600, 800);
            }
            if (autobury.getState()) {
                item = getItemById(BONES);
                if (should_take(item[0], item[1], item[2])) {
                    pickupItem(item[0], item[1], item[2]);
                    return random(600, 800);
                }
            }
            if (take_low_level.getState() || countPlayers() > 1) {
                item = getItemById(low_level_herbs);
                if (should_take(item[0], item[1], item[2])) {
                    pickupItem(item[0], item[1], item[2]);
                    return random(600, 800);
                }
            }
            if (getWorld() != world) {
                hop(world);
                return random(2000, 3000);
            }
            if (getCurrentLevel(HITS) < (getLevel(HITS) / 2)) {
                int count = getInventoryCount();
                for (int i = 0; i < count; ++i) {
                    if (getItemCommand(i).toLowerCase(Locale.ENGLISH).equals("eat")) {
                        useItem(i);
                        break;
                    }
                }
            } else {
                int[] npc = getNpcById(CHAOS_DRUID);
                if (npc[0] != -1) {
                    attackNpc(npc[0]);
                }
            }
        }
        return random(600, 1000);
    }

    @Override
    public void paint() {
        final int font = 2;
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S Taverly Chaos Druids", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        if (sleep.getState()) {
            int gained = att_xp - start_att_xp;
            if (gained > 0) {
                drawString("Attack XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
                y += 15;
            }
            gained = def_xp - start_def_xp;
            if (gained > 0) {
                drawString("Defence XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
                y += 15;
            }
            gained = str_xp - start_str_xp;
            if (gained > 0) {
                drawString("Strength XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
                y += 15;
            }
            gained = hits_xp - start_hits_xp;
            if (gained > 0) {
                drawString("Hits XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
                y += 15;
            }
            if (autobury.getState()) {
                gained = pray_xp - start_pray_xp;
                if (gained > 0) {
                    drawString("Prayer XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
                    y += 15;
                }
            }
        }
        boolean header = false;
        x += 10;
        for (int i = 0; i < pickup.length; ++i) {
            int count = banked_count[i];
            if (count <= 0) continue;
            if (!header) {
                drawString("Items banked:", x - 10, y, font, white);
                y += 15;
                header = true;
            }
            drawString(getItemNameId(pickup[i]) + ": " + int_format(count) +
                    " (" + per_hour(banked_count[i]) + "/h)",
                    x, y, font, white);
            y += 15;
        }
        for (int i = 0; i < low_level_herbs.length; ++i) {
            int count = banked_ll_count[i];
            if (count <= 0) continue;
            if (!header) {
                drawString("Items banked:", x - 10, y, font, white);
                y += 15;
                header = true;
            }
            drawString(getItemNameId(low_level_herbs[i]) + ": " +
                    int_format(count) +
                    " (" + per_hour(banked_ll_count[i]) + "/h)",
                    x, y, font, white);
            y += 15;
        }
		
		StateTracker.tick(this);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            menu_time = -1L;
        } else if (str.contains("welcome to runescape")) {
            last_hop = last_moved = System.currentTimeMillis();
        }
		StateTracker.messageReceived(this, str);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            try {
                food_id = Integer.parseInt(tf_food_id.getText());
                food_wd_count = Integer.parseInt(tf_food_wd_count.getText());
                world = Integer.parseInt(tf_world.getText());
            } catch (Throwable t) {
                System.out.println("Error parsing field, check your inputs");
                return;
            }
            pw.init(null);
            bank_to_gate = pw.calcPath(
                BANK_X, BANK_Y,
                MEMB_GATE_WALK_X_E, MEMB_GATE_WALK_Y
            );
            gate_to_bank = pw.calcPath(
                MEMB_GATE_WALK_X_E, MEMB_GATE_WALK_Y,
                BANK_X, BANK_Y
            );
            ladder_to_gate = pw.calcPath(
                LADDER_DOWN_WALK_X, LADDER_DOWN_WALK_Y,
                MEMB_GATE_WALK_X_W, MEMB_GATE_WALK_Y
            );
            gate_to_ladder = pw.calcPath(
                MEMB_GATE_WALK_X_W, MEMB_GATE_WALK_Y,
                LADDER_DOWN_WALK_X, LADDER_DOWN_WALK_Y
            );
            ladder_to_druids = pw.calcPath(
                LADDER_UP_WALK_X, LADDER_UP_WALK_Y,
                DRUIDS_X, DRUIDS_Y
            );
            menu_time = bank_time = start_time = -1L;
            path_index = druids_to_ladder.length;
            walking = false;
            Arrays.fill(banked, false);
            Arrays.fill(banked_ll, false);
            Arrays.fill(banked_count, 0);
            Arrays.fill(banked_ll_count, 0);
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
    
    private boolean underground() {
        return getY() > 1000;
    }
    
    private boolean in_bank() {
        return in_bank(getX(), getY());
    }
    
    private static boolean in_bank(int x, int y) {
        return x >= 328 && x <= 334 && y >= 549 && y <= 557;
    }
    
    private boolean in_fight_area() {
        return in_fight_area(getX(), getY());
    }
    
    private static boolean in_fight_area(int x, int y) {
        return y > 1000 && x < 353 && y < 3324;
    }
    
    private boolean should_take(int id, int x, int y) {
        // picking up from the spawn is too slow
        if (id == COINS && x == 350 && y == 3322) {
            return false;
        }
        if (getInventoryCount() == MAX_INV_SIZE) {
            if (!isItemStackableId(id) || getInventoryIndex(id) == -1) {
                return false;
            }
        }
        if (id == -1) return false;
        if (x == getX() && y == getY()) return true;
        if (in_fight_area(x, y)) return true;
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
