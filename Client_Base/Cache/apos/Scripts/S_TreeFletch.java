import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;

import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.FieldPair;
import com.aposbot.StandardCloseHandler;

public final class S_TreeFletch extends Script
    implements ItemListener, ActionListener {
    
    static class ItemType {
        String name;
        int level;
        int id;
        
        ItemType(String name, int id, int level) {
            this.name = name;
            this.level = level;
            this.id = id;
        }
    }
    
    static class TreeType {
        String name;
        int[] ids_trees;
        int id_log;
        int level;
        ItemType[] items;
        
        TreeType(
                String name, int[] ids_trees, int id_log, int lvl,
                ItemType... items) {
            
            this.name = name;
            this.ids_trees = ids_trees;
            this.id_log = id_log;
            this.level = lvl;
            this.items = items;
        }
    }
    
    private static final TreeType[] trees = new TreeType[] {
        new TreeType("Normal",
            new int[] { 0, 1}, 14, 1,
            new ItemType("Shafts", 280, 1),
            new ItemType("Shortbow", 277, 5),
            new ItemType("Longbow", 276, 10)
        ),
        new TreeType("Oak",
            new int[] { 306 }, 632, 15,
            new ItemType("Shortbow", 659, 20),
            new ItemType("Longbow", 658, 25)
        ),
        new TreeType("Willow",
            new int[] { 307 }, 633, 30,
            new ItemType("Shortbow", 661, 35),
            new ItemType("Longbow", 660, 40)
        ),
        new TreeType("Maple",
            new int[] { 308 }, 634, 45,
            new ItemType("Shortbow", 663, 50),
            new ItemType("Longbow", 662, 55)
        ),
        new TreeType("Yew",
            new int[] { 309 }, 635, 60,
            new ItemType("Shortbow", 665, 65),
            new ItemType("Longbow", 664, 70)
        ),
        new TreeType("Magic",
            new int[] { 310 }, 636, 75,
            new ItemType("Shortbow", 667, 80),
            new ItemType("Longbow", 666, 85)
        ),
    };
    
    
    private static final Point
    GNOME_LADDER_N_WALK = new Point(714, 499),
    GNOME_LADDER_N = new Point(714, 500),
    GNOME_LADDER_S_WALK = new Point(714, 517),
    GNOME_LADDER_S = new Point(714, 516);
    
    private static final int
    MODE_CUT = 0,
    MODE_CUT_FLETCH = 1,
    MODE_FLETCH = 2;
    
    private static final int[] axes = {
        428, 405, 204, 203, 88, 87, 12
    };
    
    private static final int
    WOODCUT = 8,
    FLETCH = 9,
    KNIFE = 13;
    
    private Frame frame;
    private Checkbox cbBank;
    private List lTrees;
    private List lItems;

    private TreeType tree;
    private ItemType item;
    private int mode;

    private PathWalker pw;
    private PathWalker.Location bank;
    private PathWalker.Path to_bank;
    private PathWalker.Path from_bank;

    private long start_time;
    private long move_time;
    private long sleep_time;
    private long bank_time;
    private long menu_time;
    private long click_time;

    private long cur_fails;
    private long total_fails;
    private long cur_success;
    private long total_success;
    
    private FileWriter csv;
    private long cur_attempts;
    private long cur_logs;
    
    // the log counts collected from current tree
    private int tree_logs;
    // the tree visited count (to derive avg logs per tree)
    private int trees_visited;
    private int min_logs;
    private int max_logs;
    private int last_x;
    private int last_y;

    private int woodcut_gained;
    private int banked_count;

    private long lvl_time;

    private int start_x;
    private int start_y;
    private int max_range;
    private FieldPair range_field;
    private int wb_max_time;
    private FieldPair standing_time_field;
    
    private boolean pw_init;

    private long last_cut;

    private boolean idle_move_dir;
    private boolean slept;
    private boolean deposit_attempted;
    
    private int wc_xp;
    private int fletch_xp;
    private int start_wc_xp;
    private int start_fletch_xp;
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");

    public S_TreeFletch(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
        
        try{
			String fileName	= "woodcutting_exp_complete.csv";
			boolean fileCreated = false;
			File createCsv = new File(fileName);
			if (!createCsv.exists())
			{
				createCsv.createNewFile();
				fileCreated = true;
			}

			csv	= new FileWriter(createCsv, true);
			if(fileCreated)
			{
				csv.write("tree_id,level,attempts,log_count,fail_count,axe_id,trees_visited,min_logs,max_logs\n");
				csv.flush();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
    }
    
    public static void main(String[] argv) {
        new S_TreeFletch(null).init(null);
    }
    
    @Override
    public void init(String params) {
        start_time = -1L;
        bank_time = -1L;
        click_time = -1L;
        move_time = -1L;
        menu_time = -1L;
        sleep_time = -1L;
        lvl_time = -1L;
        
        cur_fails = 0L;
        total_fails = 0L;
        cur_success = 0L;
        total_success = 0L;
        cur_attempts = 0L;
        cur_logs = 0L;
        
        tree_logs = 0;
        trees_visited = 0;
        // so when a new max comes overrides
        max_logs = 0;
        // so when a new min comes overrides
        min_logs = 9999;
        
        bank = null;
        to_bank = null;
        from_bank = null;
        
        woodcut_gained = 0;
        banked_count = 0;
        
        if (frame == null) {
            lItems = new List(6);
            lItems.add("No fletching");
            add_tree_items(lItems, trees[0]);
            lItems.select(0);

            int array_sz = trees.length;
            lTrees = new List(array_sz + 1);
            lTrees.add("No chopping");
            lTrees.addItemListener(this);
            for (int i = 0; i < array_sz; ++i) {
                lTrees.add(trees[i].name + " (level " + trees[i].level + ")");
            }
            lTrees.select(1);

            Panel pCheckbox = new Panel();
            cbBank = new Checkbox("Bank items");
            cbBank.setState(false);
            pCheckbox.add(cbBank);

            Panel pButtons = new Panel();
            Button button;
            button = new Button("OK");
            button.addActionListener(this);
            pButtons.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            pButtons.add(button);

            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            frame.add(new Label("Tree types", Label.CENTER));
            frame.add(lTrees);
            frame.add(new Label("Item types", Label.CENTER));
            frame.add(lItems);
            frame.add(pCheckbox);
            frame.add(range_field = new FieldPair("Walkback max range:", "40", true));
            frame.add(standing_time_field = new FieldPair("Max standing time (millis):", "120000", true));
            frame.add(new Label(
                "When withdrawing logs from your bank (\"No chopping\"),",
                    Label.CENTER));
            frame.add(new Label(
                "you should start this script at a bank. Otherwise, start",
                    Label.CENTER));
            frame.add(new Label(
                "it near to the trees you want to cut.",
                    Label.CENTER));
            frame.add(pButtons);
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
        
        if (lvl_time != -1L) {
            if (System.currentTimeMillis() >= lvl_time) {
                System.out.print("Congrats on level ");
                System.out.print(getLevel(WOODCUT));
                System.out.println(" woodcutting!");
                lvl_time = -1L;
            }
        }
        
        if (start_time == -1L) {
            if (cbBank.getState() && mode != MODE_FLETCH) {
                pw_init = true;
                pw.init(null);
                pw.setPath(null);
                int cur_x = getX();
                int cur_y = getY();
                bank = get_nearest_bank();
                System.out.println("Nearest bank: " + bank.name);
                to_bank = pw.calcPath(cur_x, cur_y, bank.x, bank.y);
                if (to_bank == null) {
                    stopScript(); return 0;
                }
                from_bank = pw.calcPath(bank.x, bank.y, cur_x, cur_y);
                if (from_bank == null) {
                    stopScript(); return 0;
                }
            }
            start_x = getX();
            start_y = getY();
            System.out.println("Start tile: (" + start_x + "," + start_y + ")");
            last_cut = start_time = System.currentTimeMillis();
            wc_xp = start_wc_xp = getXpForLevel(WOODCUT);
            fletch_xp = start_fletch_xp = getXpForLevel(FLETCH);
        } else {
            wc_xp = getXpForLevel(WOODCUT);
            fletch_xp = getXpForLevel(FLETCH);
        }
        
        if (mode != MODE_FLETCH) {
            if (getLevel(WOODCUT) < 99 && (wc_xp + 300) >= 13034431) {
                return _end("Stopping to let you take over so you can get 99 woodcut at a party, or something.");
            }
        }
        if (mode != MODE_CUT) {
            if (getLevel(FLETCH) < 99 && (fletch_xp + 95) >= 13034431) {
                return _end("Stopping to let you take over so you can get 99 fletch at a party, or something.");
            }
        }
        
        if (inCombat()) {
            walkTo(getX(), getY());
            pw.resetWait();
            return random(400, 600);
        }
        
        if (click_time != -1L) {
            if (System.currentTimeMillis() >= click_time) {
                click_time = -1L;
            }
            return 0;
        }
        
        if (move_time != -1L) {
            return idle_move();
        }
        
        if (slept && getFatigue() <= 10) {
            slept = false;
            last_cut = System.currentTimeMillis();
        }
        
        if (sleep_time != -1L) {
            if (System.currentTimeMillis() >= sleep_time) {
                slept = true;
                useSleepingBag();
                sleep_time = -1L;
                return random(1500, 2500);
            }
            return 0;
        }
        
        if (isQuestMenu()) {
            menu_time = -1L;
            String[] array = questMenuOptions();
            int count = questMenuCount();
            int index;
            if (item != null) {
                index = contains_index(item.name, array, count);
                if (index != -1) {
                    answer(index);
                    click_time = System.currentTimeMillis() + random(5000, 8000);
                    return random(300, 400);
                }
            }
            if (cbBank.getState()) {
                index = contains_index("access", array, count);
                if (index != -1) {
                    answer(index);
                    bank_time = System.currentTimeMillis();
                    return random(600, 800);
                }
            }
            return random(100, 200);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        
        if (isBanking()) {
            bank_time = -1L;
            if (item != null) {
                int count = getInventoryCount(item.id);
                if (count > 0) {
                    if (!deposit_attempted) {
                        banked_count += count;
                        deposit_attempted = true;
                    }
                    deposit(item.id, count);
                    return random(600, 800);
                }
            }

            if (mode != MODE_FLETCH) {
                int count = getInventoryCount(tree.id_log);
                if (count > 0) {
                    if (!deposit_attempted) {
                        banked_count += count;
                        deposit_attempted = true;
                    }
                    deposit(tree.id_log, count);
                    return random(600, 800);
                }
                if (getInventoryIndex(axes) == -1) {
                    for (int i = 0; i < axes.length; ++i) {
                        if (bankCount(axes[i]) <= 0) {
                            continue;
                        }
                        System.out.println("Withdrawn axe");
                        withdraw(axes[i], 1);
                        return random(2000, 3000);
                    }
                    return _end("Error: no axe!");
                }
            }
            
            if (mode != MODE_CUT) {
                if (getInventoryIndex(KNIFE) == -1) {
                    if (bankCount(KNIFE) <= 0) {
                        return _end("Error: no knife!");
                    }
                    System.out.println("Withdrawn knife");
                    withdraw(KNIFE, 1);
                    return random(2000, 3000);
                }
            }

            
            if (mode == MODE_FLETCH) {
                int w = bankCount(tree.id_log);
                if (w <= 0) {
                    return _end("Error: no logs!");
                }
                int e = getEmptySlots();
                if (w > e) w = e;
                if (w > 0) {
                    withdraw(tree.id_log, w);
                    return random(600, 1200);
                }
            }
            if (!doing_grand_tree() && from_bank != null) {
                pw.setPath(from_bank);
            }
            closeBank();
            deposit_attempted = false;
            return random(600, 1200);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(600, 800);
        }
        
        if (pw.walkPath()) {
        	last_cut = System.currentTimeMillis();
        	return 0;
        }
        
        if (bank != null) {
            if (should_bank()) {
                if (doing_grand_tree()) {
                    if (is_at_gnome_bank()) {
                        return talk_banker();
                    } else if (isAtApproxCoords(GNOME_LADDER_N_WALK.x, GNOME_LADDER_N_WALK.y, 3)) {
                        atObject(GNOME_LADDER_N.x, GNOME_LADDER_N.y);
                        return random(1000, 2000);
                    } else if (isAtApproxCoords(GNOME_LADDER_S_WALK.x, GNOME_LADDER_S_WALK.y, 3)) {
                        atObject(GNOME_LADDER_S.x, GNOME_LADDER_S.y);
                        return random(1000, 2000);
                    }
                } else if (isAtApproxCoords(bank.x, bank.y, 15)) {
                    return talk_banker();
                }
            } else if (is_at_gnome_bank()) {
                int[] ladder = getObjectById(6);
                if (ladder[0] != -1) {
                    if (!isAtApproxCoords(ladder[1], ladder[2], 3)) {
                        if (!isWalking()) {
                            walk_approx(ladder[1], ladder[2], 2);
                        }
                    } else {
                        atObject(ladder[1], ladder[2]);
                    }
                }
                return random(1000, 2000);
            } else if (doing_grand_tree()) {
                if (isAtApproxCoords(GNOME_LADDER_N_WALK.x, GNOME_LADDER_N_WALK.y, 3)) {
                    pw.setPath(from_bank); return 0;
                } else if (isAtApproxCoords(GNOME_LADDER_S_WALK.x, GNOME_LADDER_S_WALK.y, 3)) {
                    pw.setPath(from_bank); return 0;
                }
            }
        }
        
        if (mode != MODE_FLETCH &&
            (
                System.currentTimeMillis() >= (last_cut + wb_max_time) ||
                distanceTo(start_x, start_y) >= max_range
            )
            ) {
            
            if (!pw_init) {
                pw.init(null);
                pw_init = true;
            }
            PathWalker.Path path = pw.calcPath(start_x, start_y);
            if (path != null) {
                pw.setPath(path);
                System.out.println("Going back");
                return random(1000, 2000);
            } else {
                System.out.println("Error calculating path, trying to move");
                walk_approx(getX(), getY(), 10);
                return random(1000, 2000);
            }
        }
              
        switch (mode) {
            case MODE_CUT:
                {
                    if (getInventoryCount() == MAX_INV_SIZE && bank != null) {
                        pw.setPath(to_bank);
                        return random(600, 800);
                    }
                    return cut_tree();
                }
            case MODE_CUT_FLETCH:
                {
                    int knife = getInventoryIndex(KNIFE);
                    if (knife == -1) {
                        if (bank != null) {
                            pw.setPath(to_bank);
                            return random(600, 800);
                        } else {
                            return _end("ERROR: No knife.");
                        }
                    }
                    int log = getInventoryIndex(tree.id_log);
                    if (log != -1) {
                        return _fletch(knife, log);
                    }
                    if (bank != null) {
                        if (getInventoryCount() == MAX_INV_SIZE) {
                            pw.setPath(to_bank);
                            return random(600, 800);
                        }
                    } else if (tree != trees[0] && item != trees[0].items[0]){
                        int index = getInventoryIndex(item.id);
                        if (index != -1) {
                            dropItem(index);
                            return random(1000, 2000);
                        }
                    }
                    return cut_tree();
                }
            case MODE_FLETCH:
                {
                    int knife = getInventoryIndex(KNIFE);
                    if (knife != -1) {
                        int log = getInventoryIndex(tree.id_log);
                        if (log != -1) {
                            return _fletch(knife, log);
                        }
                    }
                    return talk_banker();
                }
        }
        System.out.println("No task? That shouldn't happen...");
        return random(1000, 2000);
    }

    private int _end(String message) {
        System.out.println(message);
        setAutoLogin(false); stopScript();
        return 0;
    }

    @Override
    public void paint() {
        final int font = 2;
        final int orangey = 0xFFD900;
        final int white = 0xFFFFFF;
        int x = 105;
        int y = 35;
        drawString("S Tree Fletch", x, y, font, orangey);
        y += 15;
        drawString("Runtime: " + get_runtime(), x + 10, y, font, white);
        y += 15;
        if (banked_count > 0) {
            drawString("Banked count: " + int_format(banked_count) + " (" + per_hour(banked_count) + "/h)",
                    x + 10, y, font, white);
            y += 15;
        }
        int gained = wc_xp - start_wc_xp;
        if (gained > 0) {
            drawString("Woodcut XP gained: " + int_format(gained) + " (" + per_hour(gained) + "/h)",
                    x + 10, y, font, white);
            y += 15;
        }
        gained = fletch_xp - start_fletch_xp;
        if (gained > 0) {
            drawString("Fletch XP gained: " + int_format(gained) + " (" + per_hour(gained) + "/h)",
                    x + 10, y, font, white);
            y += 15;
        }
        if (mode == MODE_FLETCH) return;
        drawString("Stats for woodcut level (" + woodcut_gained + " gained):",
            x, y, font, orangey);
        y += 15;
        drawString("Successful cut attempts: " + cur_success,
            x + 10, y, font, white);
        y += 15;
        drawString("Failed cut attempts: " + cur_fails, x + 10, y, font, white);
        y += 15;
        drawString("Fail rate: " + (float)
                ((double) cur_fails / (double) cur_success),
                x + 10, y, font, white);
        y += 15;
        if (woodcut_gained > 0) {
            drawString("Total:", x, y, font, orangey);
            y += 15;
            drawString("Successful cut attempts: " + total_success,
                x + 10, y, font, white);
            y += 15;
            drawString("Failed cut attempts: " + total_fails, x + 10, y, font, white);
            y += 15;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        Object src = e.getSource();
        if (lTrees.equals(src)) {
            lItems.removeAll();
            int selected = lTrees.getSelectedIndex();
            if (selected != 0) {
                lItems.add("No fletching");
                add_tree_items(lItems, trees[selected - 1]);
                cbBank.setEnabled(true);
            } else {
                int count = trees.length;
                for (int i = 0; i < count; ++i) {
                    add_tree_items(lItems, trees[i]);
                }
                cbBank.setState(true);
                cbBank.setEnabled(false);
            }
            lItems.select(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            
            int si = -1;
            int st = lTrees.getSelectedIndex();
            if (st == 0) {
                mode = MODE_FLETCH;
                String[] as = lItems.getSelectedItem().split(" ");
                for (int i = 0; i < trees.length; ++i) {
                    if (trees[i].name.startsWith(as[0])) {
                        tree = trees[i];
                        break;
                    }
                }
                if (tree != null) {
                    ItemType[] items = tree.items;
                    for (int i = 0; i < items.length; ++i) {
                        if (items[i].name.startsWith(as[1])) {
                            item = items[i];
                            break;
                        }
                    }
                }                
            } else {
                tree = trees[st - 1];
                si = lItems.getSelectedIndex();
                if (si != 0) {
                    item = tree.items[si - 1];
                    mode = MODE_CUT_FLETCH;
                } else {
                    item = null;
                    mode = MODE_CUT;
                }
            }
            
            try {
                max_range = Integer.parseInt(range_field.getValue());
            } catch (Throwable t) {
                System.out.println("Error parsing range");
            }
            
            try {
                wb_max_time = Integer.parseInt(standing_time_field.getValue());
            } catch (Throwable t) {
                System.out.println("Error parsing standing time");
            }
            
            System.out.println(tree != null ? tree.name : "tree==null");
            System.out.println(item != null ? item.name : "item==null");
        }
        frame.setVisible(false);
    }
    
    @Override
    public void onServerMessage(String str) {
        if (str.contains("swing")) {
            // woodcut attempt
            click_time = System.currentTimeMillis() + random(5000, 8000);
            last_cut = System.currentTimeMillis();
        } else if (str.contains("fail")) {
            // woodcut fail
            click_time = System.currentTimeMillis() + random(100, 200);
            ++cur_fails;
            ++total_fails;
            ++cur_attempts;
        } else if (str.contains("get some wood")) {
            click_time = System.currentTimeMillis() + random(100, 200);
            ++cur_success;
            ++total_success;
            ++cur_attempts;
            ++cur_logs;
            ++tree_logs;
            
            if(cur_attempts >= 1000)
				_csvOut();
        } else if (str.contains("carefully cut")) {
            // fletching success
            click_time = System.currentTimeMillis() + random(100, 200);
        } else if (str.contains("tired")) {
            sleep_time = System.currentTimeMillis() + random(800, 2500);
        } else if (str.contains("standing")) {
            move_time = System.currentTimeMillis() + random(800, 2500);
        } else if (str.contains("woodcut")) {
            System.out.println("You just advanced a woodcut level.");
            System.out.print("Runtime: ");
            System.out.println(get_runtime());
            System.out.print("Old success count: ");
            System.out.println(cur_success);
            System.out.print("Old fail count: ");
            System.out.println(cur_fails);
            System.out.print("Old fail rate: ");
            System.out.println((double) cur_fails / (double) cur_success);
            System.out.print("Fail total: ");
            System.out.println(total_fails);
            System.out.print("Success total: ");
            System.out.println(total_success);
            _csvOut();
            lvl_time = System.currentTimeMillis() + 2000;
            cur_fails = 0;
            cur_success = 0;
            ++woodcut_gained;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private void _csvOut() {
    	int cursor = lTrees.getSelectedIndex();
    	
    	int pos_axe;
        int axe_id = -1;
        // iterate over axes, if two ore more are present the best possible is used
        for(int i=axes.length-1; i>=0; i--) {
        	pos_axe = getInventoryIndex(axes[i]);
        	if(pos_axe != -1) {
        		axe_id = getInventoryId(pos_axe);
        	}
        }
    	
    	try{
			csv.write(
				"\"" + trees[cursor].name + "\"," +
				getLevel(WOODCUT) + "," +
				cur_attempts + "," +
				cur_logs + "," +
				(cur_attempts-cur_logs) + "," + 
				axe_id + "," +
				trees_visited + "," +
				min_logs + "," + 
				max_logs + "\n"
			);
			csv.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
        cur_attempts	= 0;
        cur_logs		= 0;
        
        // reset stats
        trees_visited = 0;
        // so when a new max comes overrides
        max_logs = 0;
        // so when a new min comes overrides
        min_logs = 9999;
    }
    
    private boolean should_bank() {
        if (mode != MODE_CUT && getInventoryIndex(KNIFE) == -1) {
            return true;
        }
        if (mode != MODE_FLETCH && getInventoryIndex(axes) == -1) {
            return true;
        }
        if ((mode == MODE_CUT_FLETCH || mode == MODE_CUT) && getInventoryCount() == MAX_INV_SIZE) {
            return true;
        }
        if (mode == MODE_FLETCH && getInventoryIndex(tree.id_log) == -1) {
            return true;
        }
        return false;
    }
    
    private int talk_banker() {
        int[] npc = getNpcByIdNotTalk(BANKERS);
        if (npc[0] != -1) {
            talkToNpc(npc[0]);
            menu_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        return random(300, 400);
    }
    
    private int _fletch(int knife, int log) {
        useItemWithItem(knife, log);
        menu_time = System.currentTimeMillis();
        return random(300, 400);
    }
    
    private static int contains_index(String str, String[] options, int count) {
        str = str.toLowerCase(Locale.ENGLISH);
        for (int i = 0; i < count; ++i) {
            if (options[i].toLowerCase(Locale.ENGLISH).contains(str)) {
                return i;
            }
        }
        return -1;
    }
    
    private static void add_tree_items(List list, TreeType type) {
        ItemType[] array = type.items;
        int count = array.length;
        String name = type.name;
        for (int i = 0; i < count; ++i) {
            ItemType item = array[i];
            list.add(name + " " + item.name + " (" + item.level + ")");
        }
    }
    
    private void walk_approx(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 1000) return;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
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
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return int_format(secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
    
    private boolean doing_grand_tree() {
        if(null == bank)
        	return false;
    	
    	int x = bank.x;
        int y = bank.y;
        if (x == GNOME_LADDER_N_WALK.x && y == GNOME_LADDER_N_WALK.y) {
            return true;
        }
        if (x == GNOME_LADDER_S_WALK.x && y == GNOME_LADDER_S_WALK.y) {
            return true;
        }
        return false;
    }

    private boolean is_at_gnome_bank() {
        return doing_grand_tree() && getY() > 1000;
    }
    
    private int cut_tree() {
        if (getInventoryIndex(axes) == -1) {
            if (bank != null) {
                pw.setPath(to_bank);
                return random(600, 800);
            } else {
                return _end("ERROR: No axe!");
            }
        }
        
        boolean doing_grand_tree = doing_grand_tree();
        int count = getObjectCount();
        int target_x = -1;
        int target_y = -1;
        for (int i = 0; i < count; ++i) {
            int id = getObjectId(i);
            int x = getObjectX(i);
            int y = getObjectY(i);
            if (doing_grand_tree && x > 738 && x < 745) {
                continue;
            }
            if (inArray(tree.ids_trees, id) && distanceTo(x, y) < 30) {
                target_x = x;
                target_y = y;
                break;
            }
        }
        if (target_x != -1) {
            if(target_x != last_x || target_y != last_y) {
            	// targets are distinct from last time, update and reset tree logs
            	if(tree_logs > max_logs && tree_logs != 0) {
            		max_logs = tree_logs;
            	}
            	if(tree_logs < min_logs && tree_logs != 0) {
            		min_logs = tree_logs;
            	}
            	++trees_visited;
            	tree_logs = 0;
            }
        	last_x = target_x;
            last_y = target_y;
        	atObject(target_x, target_y);
        }
        return random(1000, 2000);
    }
    
    private PathWalker.Location get_nearest_bank() {
        PathWalker.Location loc = pw.getNearestBank(getX(), getY());
        int best_dist = distanceTo(loc.x, loc.y);
        int temp_dist = distanceTo(GNOME_LADDER_N_WALK.x, GNOME_LADDER_N_WALK.y);
        if (temp_dist < best_dist) {
            best_dist = temp_dist;
            loc = new PathWalker.Location(
                    "Gnome North Ladder",
                    GNOME_LADDER_N_WALK.x, GNOME_LADDER_N_WALK.y, false);
        }
        temp_dist = distanceTo(GNOME_LADDER_S_WALK.x, GNOME_LADDER_S_WALK.y);
        if (temp_dist < best_dist) {
            loc = new PathWalker.Location(
                    "Gnome South Ladder",
                    GNOME_LADDER_S_WALK.x, GNOME_LADDER_S_WALK.y, false);
        }
        return loc;
    }
}
