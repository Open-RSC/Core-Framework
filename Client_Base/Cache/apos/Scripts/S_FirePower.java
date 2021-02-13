import java.text.DecimalFormat;
import java.util.Locale;

public final class S_FirePower extends Script {
    
    private static final int
    LOGS = 14,
    TINDERBOX = 166,
    WOODCUT = 8,
    FIREMAKING = 11;
    
    private static final int[] axe_ids = {
        12, 87, 88, 203, 204, 405, 428
    };
    
    private static final int[] ids_tree = {
        0, 1, 70
    };
    
    private final DecimalFormat int_format = new DecimalFormat("#,##0");

    private long sleep_time;
    private long start_time;
    private long click_time;
    private long last_click;
    
    private int start_fire_xp;
    private int fire_xp;
    
    private int start_wood_xp;
    private int wood_xp;
    
    private int start_x;
    private int start_y;
    
    private final PathWalker pw;
    private boolean path_init;
    private boolean slept;

    private int mode;

    public S_FirePower(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
        try {
            mode = Integer.parseInt(params);
        } catch (Throwable t) {
            System.out.println("Error getting combat style id from params, will use controlled");
            mode = 0;
        }
        System.out.println("Run with a full inventory!");
        slept = false;
        path_init = false;
        sleep_time = -1L;
        start_time = -1L;
        click_time = -1L;
    }
    
    @Override
    public int main() {
        if (slept) {
            slept = false;
            last_click = System.currentTimeMillis();
        }
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            start_fire_xp = fire_xp = getXpForLevel(FIREMAKING);
            start_wood_xp = wood_xp = getXpForLevel(WOODCUT);
            start_x = getX();
            start_y = getY();
            last_click = System.currentTimeMillis();
            System.out.println("Starting on tile: " + start_x + "," + start_y);
        } else {
            fire_xp = getXpForLevel(FIREMAKING);
            wood_xp = getXpForLevel(WOODCUT);
        }
        if (getLevel(FIREMAKING) < 99 && (fire_xp + 200) >= 13034431) {
            return _end("Stopping to let you take over so you can get 99 firemaking at a party, or something.");
        }
        if (getLevel(WOODCUT) < 99 && (wood_xp + 30) >= 13034431) {
            return _end("Stopping to let you take over so you can get 99 woodcut at a party, or something.");
        }
        if (inCombat()) {
            pw.resetWait();
            if (getFightMode() != mode) {
                setFightMode(mode);
            } else {
                walkTo(getX(), getY());
            }
            return random(300, 500);
        }
        if (click_time != -1L) {
            if (System.currentTimeMillis() >= click_time) {
                click_time = -1L;
            }
            return 0;
        }
        if (sleep_time != -1L) {
            if (System.currentTimeMillis() >= sleep_time) {
                sleep_time = -1L;
                slept = true;
                useSleepingBag();
                return random(1000, 2000);
            }
            return 0;
        }
        if (pw.walkPath()) {
            last_click = System.currentTimeMillis();
            return 0;
        }
        if ((System.currentTimeMillis() - last_click) >= 10000L) {
            if (distanceTo(start_x, start_y) <= 10 && isReachable(start_x, start_y)) {
                last_click = System.currentTimeMillis();
                if (!isWalking()) {
                    walkTo(start_x, start_y);
                }
                return random(1000, 2000);
            }
            if (!path_init) {
                path_init = true;
                pw.init(null);
            }
            PathWalker.Path path = pw.calcPath(start_x, start_y);
            if (path == null) {
                System.out.println("Null path... trying to move a bit");
                walk_approx(getX(), getY(), 10);
                return random(1000, 2000);
            }
            pw.setPath(path);
            return 0;
        }
        int[] logs = getItemById(LOGS);
        int x = getX();
        int y = getY();
		int box = getInventoryIndex(TINDERBOX);
        if (box != -1 && logs[1] == x && logs[2] == y && !isObjectAt(x, y)) {
            //int box = getInventoryIndex(TINDERBOX);
            //if (box == -1) {
            //    return _end("No tinderbox :(");
            //}
			useItemOnGroundItem(box, LOGS, x, y);
			return random(600, 800);
        }
        int axe = getInventoryIndex(axe_ids);
        if (axe == -1) {
            return _end("No axe :(");
        }
        int[] tree = getObjectById(ids_tree);
        if (tree[0] != -1) {
            atObject(tree[1], tree[2]);
            return random(800, 1200);
        }
        return random(200, 300);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("swing your")) {
            click_time = System.currentTimeMillis() + random(5000, 7000);
            last_click = System.currentTimeMillis();
        } else if (str.contains("get some wood")) {
            click_time = System.currentTimeMillis() + random(100, 200);
            last_click = System.currentTimeMillis();
        } else if (str.contains("attempt to light")) {
            click_time = System.currentTimeMillis() + random(5000, 7000);
            last_click = System.currentTimeMillis();
        } else if (str.contains("logs begin to burn") || str.contains("fail")) {
            click_time = System.currentTimeMillis() + random(100, 200);
            last_click = System.currentTimeMillis();
        } else if (str.contains("tired")) {
            sleep_time = System.currentTimeMillis() + random(1000, 2500);
        }
    }
    
    @Override
    public void paint() {
        final int font = 2;
        final int orangey = 0xFFD900;
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("S Fire Power", x, y, font, orangey);
        y += 15;
        drawString("Runtime: " + get_runtime(), x, y, font, white);
        y += 15;
        int gained = fire_xp - start_fire_xp;
        drawString("Firemaking XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
        y += 15;
        gained = wood_xp - start_wood_xp;
        drawString("Woodcutting XP: " + int_format(gained) + " (" + per_hour(gained) + "/h)", x, y, font, white);
    }
    
    private int _end(String message) {
        System.out.println(message);
        stopScript(); setAutoLogin(false);
        return 0;
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
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
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
}
