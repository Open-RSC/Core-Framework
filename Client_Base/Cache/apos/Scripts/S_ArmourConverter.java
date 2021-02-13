import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public final class S_ArmourConverter extends Script {
    
    // edit the behaviour of the script by changing this
    // second to last = body_to_top (true or false)
    // last = max_count (max number to convert)
    private Armour[] armours = {
        new Armour("Bronze", 308, 117, true, Integer.MAX_VALUE),
        new Armour("Iron", 312, 8, true, Integer.MAX_VALUE),
        new Armour("Steel", 309, 118, true, 0),
        new Armour("Black", 313, 196, true, Integer.MAX_VALUE),
        new Armour("Mithril", 310, 119, true, Integer.MAX_VALUE),
        new Armour("Adamant", 311, 120, true, Integer.MAX_VALUE),
        new Armour("Rune", 407, 401, true, Integer.MAX_VALUE)
    };
    
    private static final int THRANDER = 160;
    private static final int BANK_DOOR_SHUT = 64;
    private static final int DOOR_SHUT = 2;
    private static final Point door = new Point(104, 518);
    private static final Point out_room = new Point(104, 517);
    private static final Point out_bank = new Point(103, 509);
    private final DecimalFormat iformat = new DecimalFormat("#,##0");
    
    private static final class Armour {
        final String name;
        final int top_id;
        final int body_id;
        boolean body_to_top;
        int count;
        int max_count;

        Armour(String name, int top_id, int body_id, boolean body_to_top, int max_count) {
            this.name = name;
            this.top_id = top_id;
            this.body_id = body_id;
            this.body_to_top = body_to_top;
            this.max_count = max_count;
        }
    }
    private long wait_time;
    private long menu_time;
    private long bank_time;
    private long start_time;
    private long move_time;
    private boolean idle_move_dir;
    private int used;
    
    public S_ArmourConverter(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        wait_time = -1L;
        menu_time = -1L;
        bank_time = -1L;
        start_time = -1L;
        move_time = -1L;
        for (Armour a : armours) {
            a.count = 0;
        }
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (wait_time != -1L) {
            if (System.currentTimeMillis() >= (wait_time + 10000L)) {
                wait_time = -1L;
            }
            return random(600, 800);
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
            for (Armour a : armours) {
                int id;
                if (a.body_to_top) {
                    id = a.top_id;
                } else {
                    id = a.body_id;
                }
                int count = getInventoryCount(id);
                if (count > 0) {
                    deposit(id, count);
                    return random(600, 900);
                }
            }
            if (getInventoryCount() >= MAX_INV_SIZE) {
                closeBank();
                return random(600, 900);
            }
            for (Armour a : armours) {
                if ((a.max_count - a.count) <= 0) {
                    continue;
                }
                int id;
                if (a.body_to_top) {
                    id = a.body_id;
                } else {
                    id = a.top_id;
                }
                int w = bankCount(id);
                if (w <= 0) continue;
                int e = getEmptySlots();
                if (w > e) w = e;
                int c = (a.max_count - a.count);
                if (w > c) w = c;
                withdraw(id, w);
                return random(800, 1200);
            }
            if (getInventoryIndex(_armourToArray()) == -1) {
                stopScript(); setAutoLogin(false);
            } else {
                closeBank();
            }
            return random(600, 900);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (move_time != -1L) {
            return _idleMove();
        }
        if (_inRoom()) {
            int index = -1;
            for (int i = 0; i < armours.length; ++i) {
                Armour a = armours[i];
                if ((a.max_count - a.count) <= 0) {
                    continue;
                }
                index = getInventoryIndex(a.body_to_top ? a.body_id : a.top_id);
                if (index != -1) {
                    used = i;
                    break;
                }
            }
            if (index != -1) {
                int[] npc = getNpcByIdNotTalk(THRANDER);
                if (npc[0] != -1) {
                    useOnNpc(npc[0], index);
                    wait_time = System.currentTimeMillis();
                }
                return random(1000, 2000);
            } else {
                if (getWallObjectIdFromCoords(door.x, door.y) == DOOR_SHUT) {
                    atWallObject(door.x, door.y);
                    return random(1500, 2300);
                }
                if (!isWalking()) {
                    Point p = _getBankPoint();
                    if (isReachable(p.x, p.y)) {
                        walkTo(p.x, p.y);
                    } else {
                        walkTo(out_bank.x, out_bank.y);
                    }
                }
                return random(1000, 2000);
            }
        } else if (_inBank()) {
            int index = getInventoryIndex(_armourToArray());
            if (index == -1) {
                int[] npc = getNpcByIdNotTalk(BANKERS);
                if (npc[0] != -1) {
                    talkToNpc(npc[0]);
                    menu_time = System.currentTimeMillis();
                }
                return random(1000, 2000);
            } else {
                int[] door = getObjectById(BANK_DOOR_SHUT);
                if (door[0] != -1 && distanceTo(door[1], door[2]) < 16) {
                    atObject(door[1], door[2]);
                    return random(1500, 2300);
                }
                if (!isWalking()) {
                    Point p = _getRoomPoint();
                    if (isReachable(p.x, p.y)) {
                        walkTo(p.x, p.y);
                    } else {
                        walkTo(out_room.x, out_room.y);
                    }
                }
                return random(1000, 2000);
            }
        } else {
            if (!isWalking()) {
                if (getInventoryIndex(_armourToArray()) != -1) {
                    Point p = _getRoomPoint();
                    if (isReachable(p.x, p.y)) {
                        walkTo(p.x, p.y);
                    } else if (distanceTo(door.x, door.y) < 5 &&
                            getWallObjectIdFromCoords(door.x, door.y) == DOOR_SHUT) {
                        atWallObject(door.x, door.y);
                    } else {
                        walkTo(out_room.x, out_room.y);
                    }
                } else {
                    int[] door = getObjectById(BANK_DOOR_SHUT);
                    if (door[0] != -1 && distanceTo(door[1], door[2]) < 5) {
                        atObject(door[1], door[2]);
                        return random(1500, 2300);
                    }
                    Point p = _getBankPoint();
                    if (isReachable(p.x, p.y)) {
                        walkTo(p.x, p.y);
                    } else {
                        walkTo(out_bank.x, out_bank.y);
                    }
                }
            }
            return random(1000, 2000);
        }
    }

    @Override
    public void paint() {
        int x = 25;
        int y = 25;
        final int white = 0xFFFFFF;
        drawString("S Arrow Maker", x, y, 1, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(), x, y, 1, white);
        y += 15;
        for (Armour a : armours) {
            if (a.max_count <= 0) {
                continue;
            }
            drawString(a.name + " to " + (a.body_to_top ? "tops" : "bodies") + ": " +
                    iformat.format(a.count) + "/" + iformat.format(a.max_count) +
                    " (" + _perHour(a.count) + "/h)", x, y, 1, white);
            y += 15;
        }
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
    
    private boolean _idleMoveP1() {
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
    
    private boolean _idleMoveM1() {
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
    
    private int _idleMove() {
        if (System.currentTimeMillis() >= move_time) {
            System.out.println("Moving for 5 min timer");

            if (idle_move_dir) {
                if (!_idleMoveP1()) {
                    _idleMoveM1();
                }
            } else {
                if (!_idleMoveM1()) {
                    _idleMoveP1();
                }
            }
            idle_move_dir = !idle_move_dir;
            move_time = -1L;
            return random(1500, 2500);
        }
        return 0;
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("busy")) {
            wait_time = -1L;
            menu_time = -1L;
        } else if (str.contains("standing")) {
            move_time = System.currentTimeMillis() + random(800, 2500);
        } else if (str.contains("gives you")) {
            ++armours[used].count;
            wait_time = -1L;
        }
    }
    
    private int[] _armourToArray() {
        ArrayList<Integer> list = new ArrayList<>();
        for (Armour a : armours) {
            if ((a.max_count - a.count) <= 0) {
                continue;
            }
            if (a.body_to_top) {
                list.add(a.body_id);
            } else {
                list.add(a.top_id);
            }
        }
        int[] array = new int[list.size()];
        int i = 0;
        for (int id : list) {
            array[i++] = id;
        }
        return array;
    }
    
    private boolean _inRoom() {
        return _inRoom(getX(), getY());
    }
    
    private static boolean _inRoom(int x, int y) {
        return y > 517;
    }
    
    private Point _getRoomPoint() {
        return new Point(104 + random(-1, 1), 520 + random(-1, 1));
    }
    
    private boolean _inBank() {
        return _inBank(getX(), getY());
    }
    
    private static boolean _inBank(int x, int y) {
        return x < 107 && y < 516 && y > 509;
    }
    
    private Point _getBankPoint() {
        return new Point(102 + random(-3, 3), 512 + random(-1, 1));
    }
}
