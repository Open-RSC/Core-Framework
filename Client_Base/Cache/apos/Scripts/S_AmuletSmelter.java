import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;


public final class S_AmuletSmelter extends Script
    implements ActionListener {
    
    private static final int MOULD = 294;
    private static final int FURNACE = 118;
    private static final int GOLD_ORE = 152;
    private static final int GOLD_BAR = 172;
    private static final int GNOME_BALL = 981;
    
    private static final String[] amulet_names = {
        "normal", "sapphire", "emerald", "ruby", "diamond", "dragonstone"
    };
    
    private static final int[] gems = {
        -1, 164, 163, 162, 161, 523
    };
    
    private static final int[] unstrung = {
        296, 297, 298, 299, 300, 524
    };
    
    private static final String[] loc_names = {
        "Al Kharid", "Ardougne", "Falador"
    };
    
    private static final Point[] bank_locs = {
        new Point(87, 695), new Point(581, 572), new Point(328, 553)
    };
    
    private static final Point[] furnace_locs = {
        new Point(82, 679), new Point(590, 590), new Point(310, 545)
    };
    
    private Checkbox[] checkboxes;
    private Choice choice_loc;
    
    private String name;
    private int gem_id;
    
    private PathWalker pw;
    private Frame frame;
    
    private PathWalker.Path to_bank;
    private PathWalker.Path from_bank;
    private Point furnace_point;
    private long start_time;
    private long sleep_time;
    private long menu_time;
    private long bank_time;
    private int[] banked_counts;
    private long lvl_time;
    private Checkbox cb_ore;

    public S_AmuletSmelter(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new S_AmuletSmelter(null).init(null);
    }
    
    @Override
    public void init(String params) {
        if (banked_counts == null) {
            banked_counts = new int[amulet_names.length];
        } else {
            Arrays.fill(banked_counts, 0);
        }
        lvl_time = -1L;
        start_time = -1L;
        sleep_time = -1L;
        menu_time = -1L;
        bank_time = -1L;
        if (frame == null) {
            pw.init(null);
            
            choice_loc = new Choice();
            for (String str : loc_names) {
                choice_loc.add(str);
            }
            
            Panel ch_panel = new Panel();
            ch_panel.add(new Label("Furnace location:"));
            ch_panel.add(choice_loc);
            
            Panel cb_panel = new Panel(new GridLayout(0, 1));
            checkboxes = new Checkbox[amulet_names.length];
            for (int i = 0; i < amulet_names.length; ++i) {
                cb_panel.add(checkboxes[i] = new Checkbox(amulet_names[i], true));
            }
            
            Panel button_panel = new Panel();
            Button button = new Button("OK");
            button.addActionListener(this);
            button_panel.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            button_panel.add(button);
            
            Panel panel_east = new Panel(new GridLayout(0, 1));
            panel_east.add(cb_ore = new Checkbox("Prefer ore", true));
            panel_east.add(new Label("Selecting multiple amulets will cause"));
            panel_east.add(new Label("them to be made in order of most valuable"));
            panel_east.add(new Label("valuable to least, progressing when you"));
            panel_east.add(new Label("run out of gems."));
            panel_east.add(new Label("Start this script at the furnace with gems + gold."));
            
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.add(ch_panel, BorderLayout.NORTH);
            frame.add(cb_panel, BorderLayout.WEST);
            frame.add(panel_east, BorderLayout.EAST);
            frame.add(button_panel, BorderLayout.SOUTH);
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
                System.out.print(getLevel(12));
                System.out.println(" crafting!");
                lvl_time = -1L;
            }
        }
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            int gem = -1;
            for (int i = (gems.length - 1); i > 0; --i) {
                if (checkboxes[i].getState() && getInventoryIndex(gems[i]) != -1) {
                    gem = i;
                    gem_id = gems[gem];
                    name = amulet_names[gem];
                    break;
                }
            }
            if (gem == -1) {
                if (!checkboxes[0].getState()) {
                    return _end("Out of gems.");
                } else {
                    gem_id = -1;
                    name = amulet_names[0];
                }
            }
            System.out.println("Making " + name + " amulets");
        }
		// sleep if needed
        if (getFatigue() > 90) {
            useSleepingBag();
            return random(1000, 2000);
        }
        if (isBanking()) {
            bank_time = -1L;
            for (int i = 0; i < unstrung.length; ++i) {
                int count = getInventoryCount(unstrung[i]);
                if (count > 0) {
                    banked_counts[i] += count;
                    deposit(unstrung[i], count);
                    return random(1000, 2000);
                }
            }
            int c_bars = getInventoryCount(GOLD_BAR);
            int c_ores = getInventoryCount(GOLD_ORE);
            int wcount = (gem_id > 0 ? 14 : 28);
            if (c_bars > wcount) {
                deposit(GOLD_BAR, c_bars - wcount);
                return random(1000, 2000);
            } else if (c_ores > wcount) {
                deposit(GOLD_ORE, c_ores - wcount);
                return random(1000, 2000);
            } else if (c_bars < wcount && c_ores < wcount) {
                int bc = bankCount(GOLD_ORE);
                if (bc > 0 && cb_ore.getState()) {
                    if (bc > wcount) bc = wcount;
                    if (c_ores > 0) bc = bc - c_ores;
                    if (bc > 0) {
                        withdraw(GOLD_ORE, bc);
                        return random(1000, 2000);
                    }
                } else {
                    bc = bankCount(GOLD_BAR);
                    if (bc > wcount) bc = wcount;
                    if (c_bars > 0) bc = bc - c_bars;
                    if (bc > 0) {
                        withdraw(GOLD_BAR, bc);
                        return random(1000, 2000);
                    } else {
                        return _end("Out of ores and bars.");
                    }
                }
            }
            if (gem_id > 0) {
                int c_gems = getInventoryCount(gem_id);
                if (c_gems <= 0) {
                    int bc = bankCount(gem_id);
                    if (bc > 14) bc = 14;
                    if (c_gems > 0) bc = bc - c_gems;
                    if (bc > 0) {
                        withdraw(gem_id, bc);
                        return random(1000, 2000);
                    } else {
                        int gem = -1;
                        for (int i = (gems.length - 1); i > 0; --i) {
                            if (checkboxes[i].getState() && bankCount(gems[i]) > 0) {
                                gem = i;
                                gem_id = gems[gem];
                                name = amulet_names[gem];
                                break;
                            }
                        }
                        if (gem == -1) {
                            if (!checkboxes[0].getState()) {
                                return _end("Out of gems.");
                            } else {
                                gem_id = -1;
                                name = amulet_names[0];
                            }
                        }
                        System.out.println("Making " + name + " amulets");
                        return random(200, 800);
                    }
                } else if (c_gems > 14) {
                    deposit(gem_id, c_gems - 14);
                    return random(1000, 2000);
                }
            }
            closeBank();
            pw.setPath(from_bank);
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        if (isQuestMenu()) {
            String[] as = questMenuOptions();
            int count = questMenuCount();
            for (int i = 0; i < count; ++i) {
                String str = as[i].toLowerCase(Locale.ENGLISH);
                if (str.contains("access")) {
                    answer(i);
                    menu_time = -1L;
                    bank_time = System.currentTimeMillis();
                    return random(600, 900);
                }
                if (str.contains("amulet")) {
                    answer(i);
                    return random(600, 900);
                }
                if (gem_id != -1) {
                    if (str.contains("yes")) {
                        answer(i);
                        return random(600, 900);
                    }
                    if (str.contains(name)) {
                        menu_time = -1L;
                        answer(i);
                        return random(600, 900);
                    }
                } else {
                    if (str.contains("no")) {
                        menu_time = -1L;
                        answer(i);
                        return random(600, 900);
                    }
                }
            }
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        
        if (sleep_time != -1L) {
            if (System.currentTimeMillis() >= sleep_time) {
                useSleepingBag();
                sleep_time = -1L;
                return random(1500, 2500);
            }
            return 0;
        }
        
        int ball = getInventoryIndex(GNOME_BALL);
        if (ball != -1) {
            System.out.println("Gnome ball!");
            dropItem(ball);
            return random(1200, 2000);
        }
        
        if (pw.walkPath()) return 0;
        
        if (isAtApproxCoords(furnace_point.x, furnace_point.y, 5)) {
            if (getInventoryIndex(MOULD) == -1) {
                return _end("No mould found!");
            }
            if (getInventoryIndex(GOLD_ORE) != -1) {
                useItemOnObject(GOLD_ORE, FURNACE);
            } else if (getInventoryIndex(GOLD_BAR) != -1 && (gem_id == -1 || getInventoryIndex(gem_id) != -1)) {
                useItemOnObject(GOLD_BAR, FURNACE);
                menu_time = System.currentTimeMillis();
            } else {
                pw.setPath(to_bank);
            }
            return random(900, 1300);
        }
        
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1) {
            talkToNpc(banker[0]);
            menu_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        return random(600, 1000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            int i = choice_loc.getSelectedIndex();
            System.out.println("Location: " + choice_loc.getSelectedItem());
            Point bp = bank_locs[i];
            Point fp = furnace_locs[i];
            to_bank = pw.calcPath(fp.x, fp.y, bp.x, bp.y);
            from_bank = pw.calcPath(bp.x, bp.y, fp.x, fp.y);
            furnace_point = fp;
        }
        frame.setVisible(false);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("tired")) {
            sleep_time = System.currentTimeMillis() + random(800, 2500);
        } else if (str.contains("advanced")) {
            System.out.println("You just advanced a level.");
            _printOut();
            lvl_time = System.currentTimeMillis() + 3000L;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    @Override
    public void paint() {
        int y = 25;
        drawString("S Amulet Smelter", 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, 0xFFFFFF);
        y += 15;
        for (int i = 0; i < amulet_names.length; ++i) {
            if (banked_counts[i] <= 0) continue;
            drawString("Banked " + amulet_names[i] + " amulets: " + banked_counts[i],
                25, y, 1, 0xFFFFFF);
            y += 15;
        }
    }
    
    private void _printOut() {
        System.out.println("Runtime: " + _getRuntime());
        for (int i = 0; i < amulet_names.length; ++i) {
            System.out.println("Banked " + amulet_names[i] + " amulets: " + banked_counts[i]);
        }
    }
    
    private int _end(String reason) {
        System.out.println(reason);
        _printOut();
        stopScript(); setAutoLogin(false);
        return 0;
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000L);
        if (secs >= 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 3600 && secs < 7200) {
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
}
