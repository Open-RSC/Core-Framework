import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;


public final class S_Smelter extends Script
    implements ActionListener {
    
    private static final int NONE = -1;
    private static final int COAL = 155;
    private static final int IRON = 151;
    private static final int SMITHING = 13;
    private static final int GNOME_BALL = 981;
    private static final int FURNACE = 118;
    
    private static final BarType[] bars = {
        new BarType("Bronze (Level 1)", 169, 150, 202, 1),
        new BarType("Iron (Level 15)", 170, IRON, NONE, NONE),
        new BarType("Silver (Level 20)", 384, 383, NONE, NONE),
        new BarType("Steel (Level 30)", 171, IRON, COAL, 2),
        new BarType("Gold (Level 40)", 172, 152, NONE, NONE),
        new BarType("Mithril (Level 50)", 173, 153, COAL, 4),
        new BarType("Adamantite (Level 70)", 174, 154, COAL, 6),
        new BarType("Runite (Level 85)", 408, 409, COAL, 8)
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
    
    private static final class BarType {
        String name;
        int id;
        int ore_id;
        int coal_id; // for bronze
        int coal_count;
        
        BarType(String name, int bar_id, int ore_id, int coal_id, int coal_count) {
            this.name = name;
            this.id = bar_id;
            this.ore_id = ore_id;
            this.coal_id = coal_id;
            this.coal_count = coal_count;
        }
    }
    
    public static void main(String[] argv) {
        new S_Smelter(null).init(null);
    }
    
    private BarType bar;
    private int smelted_count;
    private long bank_time;
    private long sleep_time;
    private long lvl_time;
    private long start_time;
    private long click_time;
    private long menu_time;
    
    private PathWalker pw;    
    private PathWalker.Path to_bank;
    private PathWalker.Path from_bank;
    private Point furnace_point;
    
    private Frame frame;
    private Choice choice_loc;
    private List list_bar;
    
    public S_Smelter(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
        menu_time = -1L;
        click_time = -1L;
        bank_time = -1L;
        sleep_time = -1L;
        lvl_time = -1L;
        start_time = -1L;
        smelted_count = 0;
        if (frame == null) {
            list_bar = new List();
            for (BarType t : bars) {
                list_bar.add(t.name);
            }
            
            choice_loc = new Choice();
            for (String str : loc_names) {
                choice_loc.add(str);
            }
            
            Panel panel_b = new Panel();
            Button button;
            button = new Button("OK");
            button.addActionListener(this);
            panel_b.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            panel_b.add(button);
            
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.add(choice_loc, BorderLayout.NORTH);
            frame.add(list_bar, BorderLayout.CENTER);
            frame.add(panel_b, BorderLayout.SOUTH);
            frame.setSize(270, 240);
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
        }
        if (lvl_time != -1L) {
            if (System.currentTimeMillis() >= lvl_time) {
                System.out.print("Congrats on level ");
                System.out.print(getLevel(SMITHING));
                System.out.println(" smithing!");
                lvl_time = -1L;
            }
        }
        if (isQuestMenu()) {
            answer(0);
            menu_time = -1L;
            bank_time = System.currentTimeMillis();
            return random(1000, 2000);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            int ic = getInventoryCount(bar.id);
            if (ic > 0) {
                deposit(bar.id, ic);
                return random(600, 900);
            }
            int ncount = (bar.coal_count != NONE) ? ((MAX_INV_SIZE - 2) / (bar.coal_count + 1)) : MAX_INV_SIZE;
            ic = getInventoryCount(bar.ore_id);
            if (ic <= 0) {
                int w = ncount - ic;
                int bc = bankCount(bar.ore_id);
                if (w > bc) w = bc;
                if (w > 0) {
                    withdraw(bar.ore_id, w);
                    return random(600, 900);
                } else {
                    return _end("Out of primary ore?");
                }
            } else if (ic > ncount) {
                deposit(bar.ore_id, ic - ncount);
                return random(2000, 3000);
            }
            if (bar.coal_count != NONE) {
                ncount *= bar.coal_count;
                ic = getInventoryCount(bar.coal_id);
                if (ic < ncount) {
                    int w = ncount - ic;
                    int bc = bankCount(bar.coal_id);
                    if (w > bc) w = bc;
                    if (w > 0) {
                        withdraw(bar.coal_id, w);
                        return random(600, 900);
                    } else {
                        return _end("Out of secondary ore?");
                    }
                } else if (ic > ncount) {
                    deposit(bar.coal_id, ic - ncount);
                    return random(2000, 3000);
                }
            }
            pw.setPath(from_bank);
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        int ball = getInventoryIndex(GNOME_BALL);
        if (ball != -1) {
            System.out.println("Gnome ball!");
            dropItem(ball);
            return random(1200, 2000);
        }
        if (pw.walkPath()) return 0;
        if (click_time != -1L) {
            if (System.currentTimeMillis() >= click_time) {
                click_time = -1L;
            }
            return 0;
        }
        if (sleep_time != -1L) {
            if (System.currentTimeMillis() >= sleep_time) {
                useSleepingBag();
                sleep_time = -1L;
                return random(1500, 2500);
            }
            return 0;
        }
        if (isAtApproxCoords(furnace_point.x, furnace_point.y, 5)) {
            if (getInventoryIndex(bar.ore_id) != -1 && getInventoryCount(bar.coal_id) >= bar.coal_count) {
                useItemOnObject(bar.ore_id, FURNACE);
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
    
    private int _end(String reason) {
        System.out.println(reason);
        _printOut();
        stopScript(); setAutoLogin(false);
        return 0;
    }

    @Override
    public void paint() {
        int y = 25;
        drawString("S Smelter", 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Smelted: " + smelted_count, 25, y, 1, 0xFFFFFF);
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
        } else if (str.contains("place")) {
            click_time = System.currentTimeMillis() + random(5000, 7000);
        } else if (str.contains("retrieve")) {
            click_time = System.currentTimeMillis() + random(100, 200);
            ++smelted_count;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private void _printOut() {
        System.out.println("Runtime: " + _getRuntime());
        System.out.println("Smelted: " + smelted_count);
    }
    
    private String _getRuntime() {
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            int li = choice_loc.getSelectedIndex();
            Point fp = furnace_locs[li];
            Point bp = bank_locs[li];
            furnace_point = fp;
            bar = bars[list_bar.getSelectedIndex()];
            pw.init(null);
            to_bank = pw.calcPath(fp.x, fp.y, bp.x, bp.y);
            from_bank = pw.calcPath(bp.x, bp.y, fp.x, fp.y);
        }
        frame.setVisible(false);
    }
}
