import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.BoxLayout;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

public final class S_FishingTrawler extends Script
    implements ActionListener {
    
    private static final class TrawlerLoot {
        private String name;
        private int id;
        private int banked_count;
        private Checkbox drop_cb;
        
        TrawlerLoot(String name, int id, boolean drop) {
            this.name = name;
            this.id = id;
            this.drop_cb = new Checkbox("Drop " + name, drop);
        }
        
        boolean drop() {
            return drop_cb.getState();
       }
    }
    
    private static final int MAX_MILLIS = 30 * 60 * 1000;
    
    private static final int HOP_MIN_MS = 20000;
    private static final int HOP_MAX_MS = 45000;
    
    private static final int WITHDRAW_GP = 150000;
    private static final int WITHDRAW_CASTS = 100;
    private static final int BUY_ROPE = 12;
    private static final int BUY_PASTE = 150;
    private static final int NET_CHECK_MIN_MS = 13000;
    private static final int NET_CHECK_MAX_MS = 19000;
    private static final int WATER_RUNE = 32;
    private static final int LAW_RUNE = 42;
    private static final int TELE_WATER_COUNT = 2;
    private static final int TELE_LAW_COUNT = 2;
    // seaweed is to be eaten
    private static final int EDIBLE_SEAWEED = 1245;
    // oysters are to be opened
    private static final int OYSTER = 793;
    private static final int COINS = 10;
    private static final int BAILING_BUCKET = 1282;
    private static final int ROPE = 237;
    private static final int SWAMP_PASTE = 785;
    private static final int ESCAPE_BARREL = 1070;
    
    private static final Point bank_pos = new Point(551, 612);
    private static final Point tele_pos = new Point(587, 621);
    private static final Point dock_pos = new Point(548, 703);
    // next to the nets
    private static final Point boat_normal = new Point(325, 742);
    private static final Point land_pos = new Point(550, 711);
    private static final Point boat_flooded = new Point(300, 729);
    private static final Point boat_crashed = new Point(302, 759);
    
    private static final Point done_pos = new Point(538, 703);
    
    private static final Point shop_pos = new Point(553, 706);
    private static final Point shop_door = shop_pos; // hey, convenient
    private static final Point shop_outside = new Point(553, 705);
    
    private static final int SHOP_DOOR_CLOSED = 2;
    
    private static final int MURPHY = 733;
    private static final int MURPHY_BOAT = 734;
    private static final int SHOPKEEPER = 391;
    
    private static final int TELEPORT = 26;
    
    private static final int WORLD = 2;

    private static final Point[] catch_nets = {
        new Point(537, 703), new Point(536, 702), new Point(536, 703)
    };
    
    private static final int[] trawler_nets = {
        1102, 1101
    };
    
    private static final int[] leaks = {
        1071, 1077  
    };
    
    private int buy_rope;
    private int buy_paste;
    private int net_check_min_ms;
    private int net_check_max_ms;
    
    private TrawlerLoot[] loot;
    
    // when the trawler nets were last checked
    private long[] tnets_checked;
    // if the catch nets in the docks have been checked this round
    private boolean[] cnets_checked;
    
    private PathWalker pw;
    private PathWalker.Path to_bank;
    private PathWalker.Path from_bank;
    private PathWalker.Path tele_to_bank;
    
    private long move_time;
    private long click_time;
    private long menu_time;
    private long bank_time;
    private long shop_time;
    private long start_time;
    
    private boolean out_of_money;
    
    private int withdraw_gp;
    private int withdraw_casts;
    
    private Frame frame;
    private Panel cb_panel;
    
    private TextField tf_gp;
    private TextField tf_casts;
    private TextField tf_rope;
    private TextField tf_paste;
    private TextField tf_check_min;
    private TextField tf_check_max;
    private boolean bank_run;
    private boolean hop;
    private long hop_time;
    private int hop_min;
    private int hop_max;
    private Checkbox cb_veteran;
    private TextField tf_hop_min;
    private TextField tf_hop_max;
    private long entry_time;
    private int max_millis;

    private TextField tf_max_millis;

    public S_FishingTrawler(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    public static void main(String[] argv) {
        new S_FishingTrawler(null).init(null);
    }

    @Override
    public void init(String params) {
        entry_time = -1L;
        move_time = -1L;
        start_time = -1L;
        click_time = -1L;
        menu_time = -1L;
        bank_time = -1L;
        shop_time = -1L;
        out_of_money = false;
        bank_run = false;
        hop = false;
        loot = new TrawlerLoot[] {
            new TrawlerLoot("edible seaweed",EDIBLE_SEAWEED, false),
            new TrawlerLoot("oyster",OYSTER, true),
            new TrawlerLoot("oyster pearls", 792, true),
            new TrawlerLoot("empty oyster",791, true),
            new TrawlerLoot("broken glass",778, true),
            new TrawlerLoot("seaweed",622, true),
            new TrawlerLoot("old boot",1155, true),
            new TrawlerLoot("belt buckle",1151, true),
            new TrawlerLoot("pot",135, true),
            new TrawlerLoot("broken arrow",1165, true),
            new TrawlerLoot("broken staff",1167, true),
            new TrawlerLoot("buttons",1166, true),
            new TrawlerLoot("damaged armour1",1157, true),
            new TrawlerLoot("damaged armour2",1158, true),
            new TrawlerLoot("rusty sword", 1159, true),
            new TrawlerLoot("ceramic remains", 1169, true),
            new TrawlerLoot("raw shrimp", 349, true),
            new TrawlerLoot("raw anchovies", 351, true),
            new TrawlerLoot("raw sardine", 354, true),
            new TrawlerLoot("raw tuna", 366, true),
            new TrawlerLoot("raw swordfish", 369, true),
            new TrawlerLoot("raw lobster", 372, true),
            new TrawlerLoot("raw shark", 545, true),
            new TrawlerLoot("raw manta ray", 1190, false),
            new TrawlerLoot("raw sea turtle", 1192, false) 
        };
        cnets_checked = new boolean[catch_nets.length];
        tnets_checked = new long[trawler_nets.length];
        if (frame == null) {
            Panel bpanel = new Panel();
            Button button;
            button = new Button("OK");
            button.addActionListener(this);
            bpanel.add(button);
            button = new Button("Cancel");
            button.addActionListener(this);
            bpanel.add(button);
            
            Panel npanel = new Panel();
            npanel.setLayout(new BoxLayout(npanel, BoxLayout.Y_AXIS));
            npanel.add(new Label("Oysters will be opened, seaweed eaten."), BorderLayout.NORTH);
            npanel.add(cb_veteran = new Checkbox("Veteran (World 1 access)", false));
            
            Panel tfpanel = new Panel(new GridLayout(0, 2));
            tfpanel.add(new Label("Coins to withdraw:"));
            tfpanel.add(tf_gp = new TextField(String.valueOf(WITHDRAW_GP)));
            tfpanel.add(new Label("Teleports (optional) to withdraw:"));
            tfpanel.add(tf_casts = new TextField(String.valueOf(WITHDRAW_CASTS)));
            tfpanel.add(new Label("Rope to buy:"));
            tfpanel.add(tf_rope = new TextField(String.valueOf(BUY_ROPE)));
            tfpanel.add(new Label("Paste to buy:"));
            tfpanel.add(tf_paste = new TextField(String.valueOf(BUY_PASTE)));
            tfpanel.add(new Label("Check broken net min millis:"));
            tfpanel.add(tf_check_min = new TextField(String.valueOf(NET_CHECK_MIN_MS)));
            tfpanel.add(new Label("Check broken net max millis:"));
            tfpanel.add(tf_check_max = new TextField(String.valueOf(NET_CHECK_MAX_MS)));
            tfpanel.add(new Label("World hop (buying) min millis:"));
            tfpanel.add(tf_hop_min = new TextField(String.valueOf(HOP_MIN_MS)));
            tfpanel.add(new Label("World hop (buying) max millis:"));
            tfpanel.add(tf_hop_max = new TextField(String.valueOf(HOP_MAX_MS)));
            tfpanel.add(new Label("Max fishing millis:"));
            tfpanel.add(tf_max_millis = new TextField(String.valueOf(MAX_MILLIS)));
            
            Panel epanel = new Panel(new BorderLayout());
            epanel.add(npanel, BorderLayout.NORTH);            
            epanel.add(tfpanel, BorderLayout.CENTER);
            
            ScrollPane escroll = new ScrollPane();
            escroll.add(epanel);
            
            cb_panel = new Panel(new GridLayout(0, 1));
            
            ScrollPane wscroll = new ScrollPane();
            wscroll.setPreferredSize(new Dimension(175, 200));
            wscroll.add(cb_panel);
            
            frame = new Frame(getClass().getSimpleName());
            frame.addWindowListener(
                new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
            );
            frame.setIconImages(Constants.ICONS);
            frame.add(wscroll, BorderLayout.WEST);
            frame.add(escroll, BorderLayout.CENTER);
            frame.add(bpanel, BorderLayout.SOUTH);
            frame.setSize(585, 400);
        }
        cb_panel.invalidate();
        cb_panel.removeAll();
        for (TrawlerLoot l : loot) {
            cb_panel.add(l.drop_cb);
        }
        cb_panel.validate();
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
        if (isQuestMenu()) {
            String[] as = questMenuOptions();
            int count = questMenuCount();
            for (int i = 0; i < count; ++i) {
                String str = as[i].toLowerCase(Locale.ENGLISH);
                if (str.contains("access")) {
                    answer(i);
                    bank_time = System.currentTimeMillis();
                    return random(1000, 2000);
                } else if (str.contains("selling")) {
                    answer(i);
                    shop_time = System.currentTimeMillis();
                    return random(1000, 2000);
                } else if (str.contains("could i help") || str.contains("be fine") || str.contains("do it")) {
                    answer(i);
                    menu_time = System.currentTimeMillis();
                    return random(1000, 2000);
                } else if (str.contains("west please")) {
                    answer(i);
                    entry_time = System.currentTimeMillis();
                    menu_time = -1L;
                    return random(3000, 5000);
                } else if (str.contains("insist")) {
                    System.out.println("Returning");
                    menu_time = -1L;
                    answer(i);
                    return random(3000, 5000);
                } else if (str.contains("take me back")) {
                    menu_time = System.currentTimeMillis();
                    answer(i);
                    return random(3000, 5000);
                }
            }
            return random(1000, 2000);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
                menu_time = -1L;
            }
            return random(700, 900);
        }
        if (isBanking()) {
            bank_run = false;
            for (TrawlerLoot l : loot) {
                int count = getInventoryCount(l.id);
                if (count > 0) {
                    deposit(l.id, count);
                    l.banked_count += count;
                    return random(2000, 3000);
                }
            }
            if (withdraw_casts != 0) {
                int iwater = getInventoryCount(WATER_RUNE);
                if (iwater < TELE_WATER_COUNT) {
                    int bc = bankCount(WATER_RUNE);
                    if (bc > 0) {
                        int d = (withdraw_casts * TELE_WATER_COUNT);
                        if (bc > d) bc = d;
                        withdraw(WATER_RUNE, bc);
                        return random(2000, 3000);
                    }
                }
                int ilaw = getInventoryCount(LAW_RUNE);
                if (ilaw < TELE_LAW_COUNT) {
                    int bc = bankCount(LAW_RUNE);
                    if (bc > 0) {
                        int d = (withdraw_casts * TELE_LAW_COUNT);
                        if (bc > d) bc = d;
                        withdraw(WATER_RUNE, bc);
                        return random(2000, 3000);
                    }
                }
            }
            if (out_of_money) {
                int bc = bankCount(COINS);
                if (bc <= 0) {
                    return _end("Out of money");
                }
                if (bc > withdraw_gp) bc = withdraw_gp;
                withdraw(COINS, bc);
                out_of_money = false;
                return random(2000, 3000);
            }
            pw.setPath(from_bank);
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(700, 900);
        }
        if (isShopOpen()) {
            if (!out_of_money) {
                int inv = getInventoryCount(ROPE);
                if (inv < buy_rope) {
                    int buy_rope = (this.buy_rope - inv);
                    int rope = getShopItemById(ROPE);
                    if (rope == -1) return _shopHop();
                    int count = getShopItemAmount(rope);
                    if (count <= 0) return _shopHop();
                    if (count > buy_rope) count = buy_rope;
                    buyShopItem(rope, count);
                    return random(2000, 3500);
                }
                inv = getInventoryCount(SWAMP_PASTE);
                if (inv < buy_paste) {
                    int buy_paste = (this.buy_paste - inv);
                    int paste = getShopItemById(SWAMP_PASTE);
                    if (paste == -1) return _shopHop();
                    int count = getShopItemAmount(paste);
                    if (count <= 0) return _shopHop();
                    if (count > buy_paste) count = buy_paste;
                    buyShopItem(paste, count);
                    return random(2000, 3500);
                }
            }
            closeShop();
            return random(1000, 2000);
        } else if (shop_time != -1L) {
            if (System.currentTimeMillis() >= (shop_time + 8000L)) {
                shop_time = -1L;
            }
            return random(700, 900);
        }
        if (click_time != -1L) {
            if (System.currentTimeMillis() >= click_time) {
                click_time = -1L;
            }
            return 0;
        }
        if (hop) {
            if (cb_veteran.getState()) {
                switch (getWorld()) {
                    case 1:
                        hop(3);
                        break;
                    case 2:
                        hop(1);
                        break;
                    case 3:
                        hop(2);
                        break;
                }
            } else {
                switch (getWorld()) {
                    case 2:
                        hop(3);
                        break;
                    case 3:
                        hop(2);
                        break;
                }
            }
            hop = false;
            hop_time = System.currentTimeMillis() + random(hop_min, hop_max);
            return random(2000, 3000);
        }
        if (move_time != -1L) {
            if (System.currentTimeMillis() >= move_time) {
                System.out.println("Moving for 5 min timer");
                _walkApprox(getX(), getY(), 1);
                move_time = -1L;
                return random(1500, 2500);
            }
            return 0;
        }
        if (getFatigue() > 80) {
            useSleepingBag();
            return random(1500, 2500);
        }
        {
            int ioyster = getInventoryIndex(OYSTER);
            if (ioyster != -1) {
                useItem(ioyster);
                return random(1000, 2000);
            }
        }
        {
            int iseaweed = getInventoryIndex(EDIBLE_SEAWEED);
            if (iseaweed != -1) {
                useItem(iseaweed);
                return random(1000, 2000);
            }
        }
        for (TrawlerLoot l : loot) {
            if (!l.drop()) continue;
            int index = getInventoryIndex(l.id);
            if (index != -1) {
                dropItem(index);
                return random(1600, 2300);
            }
        }
        if (pw.walkPath()) return 0;
        if (_insideShop()) {
            if (_mustShop()) {
                int[] shopkeep = getNpcByIdNotTalk(SHOPKEEPER);
                if (shopkeep[0] != -1) {
                    talkToNpc(shopkeep[0]);
                    return random(3000, 3500);
                }
                return random(600, 1000);
            } else {
                if (_shopDoorClosed()) {
                    atWallObject(shop_door.x, shop_door.y);
                    return random(1500, 2500);
                }
                if (_mustBank()) {
                    _gotoBank();
                    return random(1000, 2000);
                } else if (!isWalking()) {
                    walkTo(dock_pos.x, dock_pos.y);
                    return random(1500, 2500);
                }
                return random(1000, 2000);
            }
        } else if (isAtApproxCoords(bank_pos.x, bank_pos.y, 15)) {
            if (!_mustBank()) {
                pw.setPath(from_bank);
                return random(1000, 2000);
            }
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
                talkToNpc(banker[0]);
                return random(3000, 3500);
            }
            return random(600, 1000);
        } else if (isAtApproxCoords(tele_pos.x, tele_pos.y, 5)) {
            pw.setPath(tele_to_bank);
            return random(1000, 2000);
        } else if (isAtApproxCoords(land_pos.x, land_pos.y, 2)) {
            if (!isWalking()) {
                walkTo(done_pos.x, done_pos.y);
            }
            return random(1500, 2500);
        } else if (isAtApproxCoords(done_pos.x, done_pos.y, 3)) {
            if (getInventoryCount() != MAX_INV_SIZE) {
                for (TrawlerLoot l : loot) {
                    if (l.drop()) continue;
                    int[] item = getItemById(l.id);
                    if (item[0] != -1 && item[1] == getX() && item[2] == getY()) {
                        pickupItem(item[0], item[1], item[2]);
                        return random(1200, 2000);
                    }
                }
            }
            for (int i = 0; i < catch_nets.length; ++i) {
                if (cnets_checked[i]) continue;
                atObject(catch_nets[i].x, catch_nets[i].y);
                cnets_checked[i] = true;
                return random(1200, 2000);
            }
            return _doDocks();
        } else if (_xdist(boat_normal.x) < 9 && _ydist(boat_normal.y) < 4) {
            Arrays.fill(cnets_checked, false); // because fuck you
            if (_doLeaks()) {
                return random(700, 900);
            }
            if (!_doTrawlerNets()) {
                if (_assuredPosition(boat_normal)) {
                    return random(1000, 2000);
                }
            }
            return random(700, 900);
        } else if (_xdist(boat_flooded.x) < 9 && _ydist(boat_flooded.y) < 4) {
            if (_doLeaks()) {
               return random(700, 900); 
            }
            if (_doTrawlerNets()) {
                return random(700, 900);
            }
            if (_assuredPosition(boat_flooded)) {
                return random(1000, 2000);
            }
            int bucket = getInventoryIndex(BAILING_BUCKET);
            if (bucket == -1) {
                System.out.println("No bailing bucket!");
                return random(1000, 2000);
            }
            useItem(bucket);
            return random(400, 600);
        } else if (isAtApproxCoords(boat_crashed.x, boat_crashed.y, 3)) {
            int[] barrel = getObjectById(ESCAPE_BARREL);
            if (_objectValid(barrel)) {
                atObject(barrel[1], barrel[2]);
            } else {
                System.out.println("ERROR: Escape barrel not found!");
            }
            return random(3000, 5000);
        } else if (getX() <= dock_pos.x) {
            return _doDocks();
        }
        if (!isWalking()) {
            System.out.println("What to do?");
        }
        return random(1000, 2000);
    }
    
    private int _shopHop() {
        if (System.currentTimeMillis() >= hop_time) {
            closeShop();
            hop = true;
        }
        return random(600, 1000);
    }
    
    private int _doDocks() {
        if (getWorld() != WORLD) {
            hop(WORLD);
            return random(3000, 5000);
        }
        if (_mustBank()) {
            _gotoBank();
            return random(1500, 2500);
        }
        if (_mustShop()) {
            return _gotoShop();
        }
        int[] murphy = getNpcByIdNotTalk(MURPHY);
        if (murphy[0] != -1) {
            talkToNpc(murphy[0]);
            menu_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        return random(600, 1000);
    }
    
    private boolean _assuredPosition(Point p) {
        if (!isWalking() && (getX() != p.x || getY() != p.y)) {
            walkTo(p.x, p.y);
            return true;
        }
        return false;
    }
    
    private int _gotoShop() {
        if (_shopDoorClosed()) {
            if (getX() != shop_outside.x && getY() != shop_outside.y) {
                if (!isWalking()) {
                    walkTo(shop_outside.x, shop_outside.y);
                }
                return random(1000, 2000);
            }
            atWallObject(shop_door.x, shop_door.y);
            return random(1500, 2500);
        } else if (!isWalking()) {
            walkTo(shop_pos.x, shop_pos.y);
        }
        return random(1000, 2000);
    }
    
    private boolean _shopDoorClosed() {
        return getWallObjectIdFromCoords(shop_door.x, shop_door.y) == SHOP_DOOR_CLOSED;
    }
    
    private void _gotoBank() {
        if (getInventoryCount(WATER_RUNE) < TELE_WATER_COUNT) {
            pw.setPath(to_bank);
        } else if (getInventoryCount(LAW_RUNE) < TELE_LAW_COUNT) {
            pw.setPath(from_bank);
        } else {
            castOnSelf(TELEPORT);
        }
    }
    
    private boolean _mustShop() {
        return getInventoryCount(ROPE) < buy_rope || getInventoryCount(SWAMP_PASTE) < buy_paste;
    }
    
    private boolean _insideShop() {
        int x = getX();
        int y = getY();
        return x > 551 && y > 705 && x < 555 && y < 710;
    }
    
    private boolean _objectValid(int[] o) {
        if (o[0] == -1) return false;
        return distanceTo(o[1], o[2]) < 16;
    }
    
    private boolean _doLeaks() {
        if (System.currentTimeMillis() > (entry_time + max_millis)) {
            int[] murphy = getNpcByIdNotTalk(MURPHY_BOAT);
            if (murphy[0] != -1) {
                talkToNpc(murphy[0]);
                menu_time = System.currentTimeMillis();
            }
            return true;
        }
        int[] leak = getObjectById(leaks);
        if (_objectValid(leak)) {
            if (getInventoryIndex(SWAMP_PASTE) != -1) {
                atObject(leak[1], leak[2]);
                return true;
            }
        }
        return false;
    }
    
    private boolean _doTrawlerNets() {
        for (int i = 0; i < trawler_nets.length; ++i) {
            int id = trawler_nets[i];
            if (System.currentTimeMillis() < tnets_checked[i]) {
                continue;
            }
            int[] net = getObjectById(id);
            if (net[0] != -1) {
                int rope = getInventoryIndex(ROPE);
                if (rope != -1) {
                    atObject(net[1], net[2]);
                    tnets_checked[i] = System.currentTimeMillis() +
                        random(net_check_min_ms, net_check_max_ms);
                    return true;
                } else {
                    return false;
                }
            } else {
                System.out.println("ERROR: Trawler nets not found!");
                return false;
            }
        }
        return false;
    }
    
    private boolean _mustBank() {
        if (bank_run) return true;
        if (out_of_money) return true;
        if (getInventoryCount() == MAX_INV_SIZE) return true;
        return false;
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("money")) {
            if (withdraw_gp <= 0) {
                _end("Out of money");
            } else {
                out_of_money = true;
                pw.setPath(to_bank);
            }
        } else if (str.contains("standing here")) {
            move_time = System.currentTimeMillis() + random(1500, 1800);
        } else if (str.contains("you inspect") ||
                    str.contains("you climb") ||
                    str.contains("you search")) {
            click_time = System.currentTimeMillis() + random(5000, 7000);
        } else if (str.contains("manage") ||
                str.contains("make it") ||
                str.contains("not damaged") ||
                str.contains("fail") ||
                str.contains("empty")) {
            click_time = System.currentTimeMillis() + random(100, 200);
        } else if (str.contains("lot")) {
            bank_run = true;
            click_time = System.currentTimeMillis() + random(100, 200);
        }
    }
    
    private int _xdist(int x) {
        return Math.abs(getX() - x);
    }
    
    private int _ydist(int y) {
        return Math.abs(getY() - y);
    }
    
    private boolean _walkApprox(int x, int y, int range) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-range, range);
            dy = y + random(-range, range);
            if ((++loop) > 1000) return false;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
        return true;
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
    
    @Override
    public void paint() {
        int y = 25;
        drawString("S Fishing Trawler", 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, 0xFFFFFF);
        y += 15;
        for (TrawlerLoot l : loot) {
            if (l.banked_count <= 0) continue;
            drawString("Banked " + l.name + ": " + l.banked_count,
                25, y, 1, 0xFFFFFF);
            y += 15;
        }
    }
    
    private int _end(String reason) {
        System.out.println(reason);
        _printOut();
        stopScript(); setAutoLogin(false);
        return 0;
    }
    
    private void _printOut() {
        System.out.println("Runtime: " + _getRuntime());
        for (TrawlerLoot l : loot) {
            System.out.println("Banked " + l.name + ": " + l.banked_count);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("OK")) {
            try {
                if (to_bank == null) {
                    pw.init(null);
                    to_bank = pw.calcPath(
                        dock_pos.x, dock_pos.y,
                        bank_pos.x, bank_pos.y);
                    
                    tele_to_bank = pw.calcPath(
                        tele_pos.x, tele_pos.y,
                        bank_pos.x, bank_pos.y);
                    
                    from_bank = pw.calcPath(
                        bank_pos.x, bank_pos.y,
                        dock_pos.x, dock_pos.y);
                }
                withdraw_gp = Integer.parseInt(tf_gp.getText());
                withdraw_casts = Integer.parseInt(tf_casts.getText());
                buy_rope = Integer.parseInt(tf_rope.getText());
                buy_paste = Integer.parseInt(tf_paste.getText());
                net_check_min_ms = Integer.parseInt(tf_check_min.getText());
                net_check_max_ms = Integer.parseInt(tf_check_max.getText());
                hop_min = Integer.parseInt(tf_hop_min.getText());
                hop_max = Integer.parseInt(tf_hop_max.getText());
                max_millis = Integer.parseInt(tf_max_millis.getText());
            } catch (Throwable t) {
                System.out.println("ERROR: " + t.getClass().getSimpleName() + " " + t.getMessage());
            }
        }
        frame.setVisible(false);
    }
}
