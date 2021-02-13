// Original script:
/**
 * Flax spinning power trainer. For use at gnome stronghold.
 * 
 * v1.1
 * 
 * - yomama`
 */

public final class S_GnomeFlaxPower extends Script {
    
    private static final int BOW_STRING = 676;
    private static final int FLAX_ITEM = 675;
    private static final int SPINNER = 121;
    private long start_time;

    public S_GnomeFlaxPower(Extension ex) {
        super(ex);
    }
    
    public void init(String params) {
        start_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        // sleep if needed
        if (getFatigue() > 90) {
            useSleepingBag();
            return random(1000, 2000);
        }
        if (getY() < 1000) {
            // if full inventory + downstairs, go upstairs
            if (getInventoryCount() == MAX_INV_SIZE) {
                atObject(692, 525);
                return random(500, 600);
            }
            // get flax.
            atObject2(693, 524);
            return random(500, 600);
        } else {
            // if we have bowstring -> drop
            int bowstring = getInventoryIndex(BOW_STRING);
            if (bowstring != -1) {
                dropItem(bowstring);
                return random(500, 600);
            }
            // if we have flax -> spin
            if (getInventoryIndex(FLAX_ITEM) != -1) {
                useItemOnObject(FLAX_ITEM, SPINNER);
                return random(500, 600);
            }
            // otherwise go downstairs
            atObject(692, 1469);
            return random(500, 600);
        }
    }
    
    private String _getRuntime() {
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
    
    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("S Power Flax",
            x, y, 1, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(),
                x + 10, y, 1, white);
    }
}