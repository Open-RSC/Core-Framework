import java.text.DecimalFormat;
import java.util.Locale;

public final class S_ArrowMaker extends Script {
    
    private static final int SHAFTS = 280;
    private static final int FEATHER = 381;
    private static final int HEADLESS = 637;
    private static final int SKILL_FLETCH = 9;
    
    private static final int[] heads = {
        669, 670, 671, 672, 673, 674
    };
	private static final int[] beds = {
		14, 15
	};
	private final DecimalFormat iformat = new DecimalFormat("#,##0");
    private boolean idle_move_dir;
    private long move_time;
	private long start_time;
	private int levels_gained;
	private int start_xp;
	private int xp;

    public S_ArrowMaker(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String str) {
        move_time = -1L;
        start_time = -1L;
        levels_gained = 0;
        System.out.println("Start this script next to a bed for faster sleeping.");
    }

    @Override
    public int main() {
    	if (start_time == -1L) {
    		start_time = System.currentTimeMillis();
    		start_xp = xp = getXpForLevel(SKILL_FLETCH);
    	} else {
    		xp = getXpForLevel(SKILL_FLETCH);
    	}
        if (getFatigue() > 85) {
        	int[] bed = getObjectById(beds);
        	if (bed[0] != -1 && distanceTo(bed[1], bed[2]) < 6) {
        		atObject(bed[1], bed[2]);
        	} else {
        		useSleepingBag();
        	}
            return random(1000, 2000);
        }
        if (move_time != -1L) {
            return _idleMove();
        }
        int ifeather = getInventoryIndex(FEATHER);
        int ishafts = getInventoryIndex(SHAFTS);
        if (ifeather != -1 && ishafts != -1) {
            useItemWithItem(ishafts, ifeather);
            return random(800, 2000);
        }
        int iheadless = getInventoryIndex(HEADLESS);
        if (iheadless == -1) {
            System.out.println("No headless and no feathers/shafts");
            stopScript(); setAutoLogin(false);
            return random(1000, 2000);
        }
        for (int id : heads) {
            int iheads = getInventoryIndex(id);
            if (iheads != -1) {
                useItemWithItem(iheadless, iheads);
                return random(800, 2000);
            }
        }
        System.out.println("No heads and no feathers/shafts");
        stopScript(); setAutoLogin(false);
        return random(1000, 2000);
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
        int xp_gained = xp - start_xp;
        drawString("XP gained: " + iformat.format(xp_gained) + " (" + _perHour(xp_gained) + "/h)", x, y, 1, white);
        y += 15;
        if (levels_gained > 0) {
        	drawString("Levels gained: " + levels_gained + " (" + _perHour(levels_gained) + "/h)", x, y, 1, white);
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
    
    @Override
    public void onServerMessage(String str) {
    	str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("standing")) {
            move_time = System.currentTimeMillis() + random(800, 2500);
        } else if (str.contains("advanced")) {
        	++levels_gained;
        }
    }
}
