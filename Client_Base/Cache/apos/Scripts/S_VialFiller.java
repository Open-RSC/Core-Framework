
public final class S_VialFiller extends Script {
    
    private static final int EMPTY = 465;
    private static final int FILLED = 464;
    private static final int FOUNTAIN = 26;
    private static final int DOORS_CLOSED = 64;
    private long bank_time;
    private long start_time;
    private int banked_count;

    public S_VialFiller(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String params) {
        bank_time = -1L;
        start_time = -1L;
        banked_count = 0;
    }
    
    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        if (isBanking()) {
            int count = getInventoryCount(FILLED);
            if (count > 0) {
                banked_count += count;
                deposit(FILLED, count);
                return random(1000, 2000);
            }
            if (getInventoryIndex(EMPTY) != -1) {
                closeBank();
                return random(1000, 2000);
            }
            int bc = bankCount(EMPTY);
            if (bc <= 0) {
                System.out.println("Out of vials.");
                System.out.println("Filled: " + banked_count);
                System.out.println("Runtime: " + _getRuntime());
                stopScript(); return 0;
            }
            int w = getEmptySlots();
            if (w > bc) {
                w = bc;
            }
            withdraw(EMPTY, w);
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(800, 1000);
        }
        int index = getInventoryIndex(EMPTY);
        if (index != -1) {
            int[] fountain = getObjectById(FOUNTAIN);
            if (distanceTo(fountain[1], fountain[2]) > 2) {
                if (checkDoors()) {
                    return random(1000, 2000);
                }
            }
            useSlotOnObject(index, fountain[1], fountain[2]);
            return random(600, 900);
        }
        int[] banker = getNpcByIdNotTalk(BANKERS);
        if (banker[0] != -1) {
            if (distanceTo(banker[1], banker[2]) > 2) {
                if (checkDoors()) {
                    return random(1000, 2000);
                }
            }
            talkToNpc(banker[0]);
            return random(1000, 2000);
        }
        return random(600, 800);
    }
    
    private boolean checkDoors() {
        int[] closed = getObjectById(DOORS_CLOSED);
        if (closed[0] == -1) return false;
        atObject(closed[1], closed[2]);
        return true;
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
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
        final int gray = 0xC4C4C4;
        int y = 25;
        drawString("S VialFiller", 25, y, 1, gray);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, gray);
        y += 15;
        if (banked_count > 0) {
            drawString("Banked " + banked_count + " vials", 25, y, 1, gray);
        }
    }
}
