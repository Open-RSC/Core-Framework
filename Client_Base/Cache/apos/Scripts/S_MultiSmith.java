import java.awt.Button;
import java.awt.Choice;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_MultiSmith extends Script
    implements ActionListener {
    
    private static final class Smithable {

        final String[] options;
        final String last_option;
        final int bars_per;
        
        Smithable(int bars_per, String last_option, String... options) {
            this.bars_per = bars_per;
            this.last_option = last_option;
            this.options = options;
        }
        
        @Override
        public String toString() {
            return last_option + " (" + bars_per + " bars)";
        }
    };
    
    /*
     * categories:
     * 
     * - weapon
     * # dagger (1)
     * # throwing knife (1)
     * # sword (1)
     * ~ short sword (1)
     * ~ long sword (2)
     * ~ scimitar (2)
     * ~ 2-handed sword (3)
     * # mace (1)
     * # axe
     * ~ hatchet (1)
     * ~ battle axe (3)
     * 
     * - armour
     * # helmet
     * ~ medium helmet (1)
     * ~ large helmet (2)
     * # shield
     * ~ square shield (2)
     * ~ kite shield (3)
     * # armour
     * ~ chain mail body (3)
     * ~ plate mail body (5)
     * ~ plate mail legs (3)
     * ~ plated skirt (3)
     * 
     * - missile heads
     * # arrow heads (1)
     * # dart tips (1)
     * 
     * - nails (1)
     */
    
    // the last option comes before the rest.
    private static final Smithable[] smithables = {
        new Smithable(1, "medium helmet", "armour", "helmet"),
        new Smithable(2, "large helmet", "armour", "helmet"),
        new Smithable(5, "plate mail body", "armour"),
        new Smithable(3, "plate mail legs", "armour"),
        new Smithable(3, "chain mail body", "armour"),
        new Smithable(3, "plated skirt", "armour"),
        new Smithable(2, "square shield", "armour", "shield"),
        new Smithable(3, "kite shield", "armour", "shield"),
        new Smithable(3, "2-handed sword", "weapon", "sword"),
        new Smithable(2, "long sword", "weapon", "sword"),
        new Smithable(2, "scimitar", "weapon", "sword"),
        new Smithable(3, "battle axe", "weapon", "axe"),
        new Smithable(1, "hatchet", "weapon", "axe"),
        new Smithable(1, "short sword", "weapon", "sword"),
        new Smithable(1, "mace", "weapon"),
        new Smithable(1, "throwing knife", "weapon"),
        new Smithable(1, "dagger", "weapon"),
        new Smithable(1, "dart tips", "missile heads"),
        new Smithable(1, "arrow heads", "missile heads"),
        new Smithable(1, "nails"),
    };
    
    private static final class BarType {
        final String name;
        final int id;
        
        BarType(String name, int id) {
            this.name = name;
            this.id = id;
        }
    };
    
    private static final BarType[] bars = {
        new BarType("Bronze", 169),
        new BarType("Iron", 170),
        new BarType("Steel", 171),
        new BarType("Mithril", 173),
        new BarType("Adamantite", 174),
        new BarType("Runite", 408)
    };
    
    private static final int GNOME_BALL = 981;
    private static final int ANVIL = 50;
    private static final int HAMMER = 168;
    private static final int BANK_CLOSED = 64;
    private static final int DOOR_CLOSED = 2;
    private static final int LEVEL_SMITHING = 13;
    
    private static final Point[] varrock_anvil_points = {
        new Point(148, 512),
        new Point(145, 511)
    };

    private static final int[] bank_ids = {
        0, 1, 2, 3, 5, 6, 7, 8, 9, 11, 12, 28, 31, 32, 33, 34, 35, 36, 37, 38,
        40, 41, 42, 46, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75,
        76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93,
        94, 95, 96, 97, 98, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113,
        114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127,
        128, 129, 130, 131, 151, 153, 154, 156, 203, 204, 205, 206, 214, 215,
        225, 226, 227, 242, 308, 309, 310, 311, 312, 396, 397, 398, 399, 400,
        401, 402, 403, 404, 405, 406, 407, 419, 517, 519, 559, 560, 561, 562,
        563, 564, 574, 619, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647,
        669, 670, 671, 672, 673, 674, 698, 796, 803, 825, 827, 975, 979, 1013,
        1015, 1024, 1062, 1063, 1064, 1065, 1066, 1067, 1068, 1069, 1070, 1075,
        1076, 1077, 1078, 1079, 1080, 1088, 1089, 1090, 1091, 1092, 1097, 1122,
        1123, 1124, 1125, 1126, 1127, 1128, 1129, 1130, 1131, 1133, 1134, 1135,
        1136, 1137, 1138, 1139, 1140, 1258, 1259, 1260, 1261, 1262
    };
    
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    
    private int ptr;
    
    private long start_time;
    private long bank_time;
    private long menu_time;
    private long sleep_time;
    private int start_xp;
    private int xp;

    private Frame frame;
    private Choice bar_choice;
    private Choice item_choice;
    private List awt_list;

    private final ArrayList<Smithable> to_smith = new ArrayList<>();
    private int[] smithed_counts;

    private int bars_used;

    public S_MultiSmith(Extension ex) {
        super(ex);
    }
    
    public static void main(String[] argv) {
        new S_MultiSmith(null).init("");
    }

    @Override
    public void init(String params) {
        to_smith.clear();
        bars_used = 0;
        bank_time = -1L;
        menu_time = -1L;
        start_time = -1L;
        sleep_time = -1L;
        ptr = 0;
        if (frame == null) {
            bar_choice = new Choice();
            for (BarType b : bars) {
                bar_choice.add(b.name);
            }
            
            item_choice = new Choice();
            for (Smithable s : smithables) {
                item_choice.add(s.toString());
            }            
            
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.setLayout(new BoxLayout(frame, BoxLayout.Y_AXIS));
            
            Panel item_panel = new Panel(new GridLayout(0, 2));
            item_panel.add(new Label("Bar type:"));
            item_panel.add(bar_choice);
            item_panel.add(new Label("Item type:"));
            item_panel.add(item_choice);
            
            Panel control_panel = new Panel();
            Button b = new Button("Add");
            b.addActionListener(this);
            control_panel.add(b);
            b = new Button("Remove");
            b.addActionListener(this);
            control_panel.add(b);
            b = new Button("Smithing Wiki");
            b.addActionListener(this);
            control_panel.add(b);
            
            awt_list = new List(10);
            
            Panel button_panel = new Panel();
            b = new Button("OK");
            b.addActionListener(this);
            button_panel.add(b);
            b = new Button("Cancel");
            b.addActionListener(this);
            button_panel.add(b);
            
            frame.add(item_panel);
            frame.add(control_panel);
            frame.add(awt_list);
            frame.add(new Label("Varrock only, sorry.", Label.CENTER));
            frame.add(button_panel);
            frame.setResizable(false);
            frame.pack();
        } else {
            awt_list.removeAll();
        }
        frame.setLocationRelativeTo(null);
        frame.toFront();
        frame.requestFocus();
        frame.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("Add".equals(cmd)) {
            int index = item_choice.getSelectedIndex();
            if (index >= 0 && index < smithables.length) {
                Smithable s = smithables[index];
                awt_list.add(s.toString());
                to_smith.add(s);
            }
        } else if ("Remove".equals(cmd)) {
            int index = awt_list.getSelectedIndex();
            if (index >= 0 && index < to_smith.size()) {
                awt_list.remove(index);
                to_smith.remove(index);
            }
        } else if ("Smithing Wiki".equals(cmd)) {
            String url = "http://runescapeclassic.wikia.com/wiki/Smithing";
            System.out.println("Opening URL: " + url);
            try {
                Desktop.getDesktop().browse(new URL(url).toURI());
            } catch (Throwable t) {
                System.out.println("Failed");
            }
        } else {
            if ("OK".equals(cmd)) {
                smithed_counts = new int[to_smith.size()];
            }
            frame.setVisible(false);
        }
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            start_xp = getXpForLevel(LEVEL_SMITHING);
        }
        
        int xp = getXpForLevel(LEVEL_SMITHING);
        if (xp > this.xp) {
            this.xp = xp;
        }
        
        if (xp < 13034431 && (13034431 - xp) <= 500) {
            System.out.println("very close to 99, stopping for you to take over");
            stopScript(); setAutoLogin(false);
            return 0;
        }
        
        if (isQuestMenu()) {
            int count = questMenuCount();
            String[] options = questMenuOptions();
            Smithable cur = to_smith.get(ptr);
            String[] to_select = cur.options;
            String last_option = cur.last_option;
            for (int i = 0; i < count; ++i) {
                String str = options[i].toLowerCase(Locale.ENGLISH);
                if (str.contains("access")) {
                    answer(i);
                    bank_time = System.currentTimeMillis();
                    return random(1000, 2000);
                } else if (str.contains(last_option)) {
                    if (getInventoryCount(_getBarId()) >= cur.bars_per) {
                        ++smithed_counts[ptr];
                        bars_used += cur.bars_per;
                        ++ptr;
                        if (ptr >= to_smith.size()) {
                            ptr = 0;
                        }
                    }
                    answer(i);
                    menu_time = -1L;
                    return random(600, 900);
                }
            }
            for (int i = 0; i < count; ++i) {
                String str = options[i].toLowerCase(Locale.ENGLISH);
                for (String opt : to_select) {
                    if (str.contains(opt)) {
                        answer(i);
                        menu_time = System.currentTimeMillis();
                        return random(600, 900);
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
            for (int id : bank_ids) {
                int ic = getInventoryCount(id);
                if (ic > 0) {
                    deposit(id, ic);
                    return random(600, 1000);
                }
            }
            if (getInventoryCount() != MAX_INV_SIZE) {
                int bc = bankCount(_getBarId());
                if (bc <= 0) {
                    return _end("Out of bars");
                }
                int e = getEmptySlots();
                if (bc > e) bc = e;
                withdraw(_getBarId(), bc);
                return random(600, 1000);
            }
            closeBank();
            return random(600, 1000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        
        int hammer = getInventoryIndex(HAMMER);
        if (hammer == -1) {
            return _end("No hammer found!");
        }
        
        int ball = getInventoryIndex(GNOME_BALL);
        if (ball != -1) {
            System.out.println("Gnome ball!");
            dropItem(ball);
            return random(1200, 2000);
        }
        
        if (sleep_time != -1L) {
            if (System.currentTimeMillis() >= sleep_time) {
                useSleepingBag();
                sleep_time = -1L;
                return random(1500, 2500);
            }
            return 0;
        }
        
        if (getInventoryCount(_getBarId()) < to_smith.get(ptr).bars_per) {
            if (_insideBank()) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(1000, 2000);
            } else if (_insideAnvils()) {
                Point p = _getBankPoint();
                if (isReachable(p.x, p.y)) {
                    if (!isWalking()) {
                        walkTo(p.x, p.y);
                    }
                    return random(1000, 2000);
                }
                p = _getMidpoint();
                if (isReachable(p.x, p.y)) {
                    if (!isWalking()) {
                        walkTo(p.x, p.y);
                    }
                    return random(1000, 2000);
                }
                int[] door = getWallObjectById(DOOR_CLOSED);
                if (door[0] != -1) {
                    atWallObject(door[1], door[2]);
                    return random(1000, 2000);
                }
                System.out.println("Trapped, what to do?");
                return random(1000, 2000);
            } else {
                int[] doors = getObjectById(BANK_CLOSED);
                if (_objectValid(doors)) {
                    atObject(doors[1], doors[2]);
                    return random(1000, 2000);
                }
                Point p = _getBankPoint();
                if (!isWalking()) {
                    walkTo(p.x, p.y);
                }
                return random(1000, 2000);
            }
        } else {
            if (_insideAnvils()) {
                useItemOnObject(_getBarId(), ANVIL);
                menu_time = System.currentTimeMillis();
                return random(600, 900);
            } else if (_insideBank()) {
                int[] doors = getObjectById(BANK_CLOSED);
                if (_objectValid(doors)) {
                    atObject(doors[1], doors[2]);
                    return random(1000, 2000);
                }
                Point p = _getAnvilsPoint();
                if (isReachable(p.x, p.y)) {
                    if (!isWalking()) {
                        walkTo(p.x, p.y);
                    }
                    return random(1000, 2000);
                }
                p = _getMidpoint();
                if (!isWalking()) {
                    walkTo(p.x, p.y);
                }
                return random(1000, 2000);
            } else {
                Point p = _getAnvilsPoint();
                if (!isReachable(p.x, p.y)) {
                    int[] door = getWallObjectById(DOOR_CLOSED);
                    if (door[0] != -1) {
                        atWallObject(door[1], door[2]);
                        return random(1000, 2000);
                    }
                    System.out.println("Trapped, what to do?");
                    return random(1000, 2000);
                }
                if (!isWalking()) {
                    walkTo(p.x, p.y);
                }
                return random(1000, 2000);
            }
        }
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("tired")) {
            sleep_time = System.currentTimeMillis() + random(800, 2500);
        } else if (str.contains("busy")) {
            menu_time = -1L;
        } else {
        	if (getFatigue() > 85) {
        		sleep_time = System.currentTimeMillis() + random(800, 2500);
            }
        }
    }

    @Override
    public void paint() {
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        final int red =   0xFF0000;
        drawString("S Multi Smith", x, y, 2, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(), x, y, 2, white);
        y += 15;
        int xp_gained = xp - start_xp;
        drawString("XP gained: " + iformat.format(xp_gained) +
                " (" + _perHour(xp_gained) + "/h)", x, y, 2, white);
        y += 15;
        drawString("Bars used: " + iformat.format(bars_used) +
                " (" + _perHour(bars_used) + "/h)", x, y, 2, white);
        y += 15;
        int list_sz = to_smith.size();
        x = 310;
        y = 45;
        for (int i = 0; i < list_sz; ++i) {
            Smithable s = to_smith.get(i);
            int count = smithed_counts[i];
            if (count <= 0) continue;
            drawString(s.last_option + ": " + iformat.format(count) +
                    " (" + _perHour(count) + "/h)", x, y, 2, ptr == i ? red : white);
            y += 15;
        }
    }
    
    private Point _getBankPoint() {
        return new Point(150 + random(-1, 1), 504 + random(-1, 1));
    }
    
    private Point _getMidpoint() {
        return new Point(147 + random(-1, 1), 508 + random(-1, 1));
    }
    
    private Point _getAnvilsPoint() {
        Point best = null;
        int best_dist = Integer.MAX_VALUE;
        for (Point p : varrock_anvil_points) {
            int d = distanceTo(p.x, p.y);
            if (d < best_dist) {
                best = p;
                best_dist = d;
            }
        }
        return best;
    }
    
    private boolean _objectValid(int[] ai) {
        return ai[0] != -1 && distanceTo(ai[1], ai[2]) < 16;
    }
    
    private boolean _insideAnvils() {
        return getY() > 509 && getX() < 149;
    }
    
    private boolean _insideBank() {
        return getY() < 507;
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return iformat.format((secs / 3600)) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
    
    private void _printOut() {
        System.out.print("Runtime: ");
        System.out.println(_getRuntime());
        System.out.print("XP gained: ");
        System.out.println(start_xp - xp);
        System.out.print("Bars used: ");
        System.out.println(bars_used);
    }
    
    private int _end(String reason) {
        _printOut();
        System.out.println(reason);
        stopScript(); setAutoLogin(false);
        return 0;
    }
    
    private int _getBarId() {
        return bars[bar_choice.getSelectedIndex()].id;
    }

    // blood
    private String _perHour(int total) {
        if (total <= 0 || start_time <= 0L) {
            return "0";
        }
        return iformat.format(
            ((total * 60L) * 60L) / ((System.currentTimeMillis() - start_time) / 1000L)
        );
    }
}
