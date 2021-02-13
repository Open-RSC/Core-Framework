import java.util.Locale;

public final class S_FaladorSpinner extends Script {

    private static final int BED = 15;
    private static final int BANK_SHUT = 64;
    private static final int SPINNER = 121;
    
    private static final int DOOR_SHUT = 2;
    
    private static final int WOOL = 145;
    private static final int WOOL_BALL = 207;
    private static final int GNOME_BALL = 981;
    
    private static final int CRAFTING = 12;
   
    private long sleep_time;
    private long start_time;
    private long bank_time;
    private long lvl_time;

    private int spun_count;

    public S_FaladorSpinner(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        spun_count = 0;
        sleep_time = -1L;
        start_time = -1L;
        bank_time = -1L;
        lvl_time = -1L;
    }

    @Override
    public int main() {
        if (lvl_time != -1L) {
            if (System.currentTimeMillis() >= lvl_time) {
                System.out.print("Congrats on level ");
                System.out.print(getLevel(CRAFTING));
                System.out.println(" crafting!");
                lvl_time = -1L;
            }
        }
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(1000, 2000);
        }
        if (isBanking()) {
            int count = getInventoryCount(WOOL_BALL);
            if (count > 0) {
                deposit(WOOL_BALL, count);
                return random(1500, 2200);
            }
            if (getInventoryIndex(WOOL) == -1) {
                int w = bankCount(WOOL);
                if (w <= 0) {
                    System.out.println("Out of wool.");
                    _printOut();
                    stopScript(); setAutoLogin(false);
                    return 0;
                }
                int empty = getEmptySlots();
                if (w > empty) w = empty;
                withdraw(WOOL, w);
                return random(1500, 2200);
            }
            closeBank();
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (sleep_time != -1L) {
            if (System.currentTimeMillis() >= sleep_time) {
                int[] bed = getObjectById(BED);
                if (bed[0] != -1 && distanceTo(bed[1], bed[2]) < 16) {
                    atObject(bed[1], bed[2]);
                }
                sleep_time = -1L;
                return random(1500, 2500);
            }
            return 0;
        }
        int gball = getInventoryIndex(GNOME_BALL);
        if (gball != -1) {
            dropItem(gball);
            return random(1500, 2500);
        }
        if (getY() > 576) {
            // inside spinner room
            int wool = getInventoryIndex(WOOL);
            if (wool != -1) {
                useItemOnObject(WOOL, SPINNER);
                return random(500, 1000);
            } else {
                // leave spinner room
                if (!isWalking()) {
                    if (_openSpinnerDoor()) {
                        return random(1000, 2000);
                    }
                    int bankx = 286 - random(0, 5);
                    int banky = 572 - random(0, 5);
                    if (isReachable(bankx, banky)) {
                        walkTo(bankx, banky);
                    } else {
                        walkTo(287, 572);
                    }
                }
                return random(1000, 2000);
            }            
        } else if (getX() < 287) {
            // inside bank
            if (getInventoryIndex(WOOL_BALL) != -1) {
                int[] banker = getAllNpcById(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    return random(3000, 3500);
                }
                return random(600, 1000);
            } else {
                // leave the bank
                if (!isWalking()) {
                    if (_openBankDoors()) {
                        return random(1000, 2000);
                    }
                    if (isReachable(296, 579)) {
                        walkTo(296, 579);
                    } else {
                        walkTo(297, 576);
                    }
                }
                return random(1000, 2000);
            }
        } else {
            // in between spinner room and bank
            if (getInventoryIndex(WOOL_BALL) != -1) {
                // go to bank
                if (!isWalking()) {
                    if (_openBankDoors()) {
                        return random(1000, 2000);
                    }
                    walkTo(286 - random(0, 5),
                           572 - random(0, 5));
                }
                return random(1000, 2000);
            } else {
                // go to spinner
                if (!isWalking()) {
                    if (_openSpinnerDoor()) {
                        return random(1000, 2000);
                    }
                    walkTo(296, 579);
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
        } else if (str.contains("advanced")) {
            System.out.println("You just advanced a level.");
            _printOut();
            lvl_time = System.currentTimeMillis() + 3000L;
        } else if (str.contains("you spin")) {
            ++spun_count;
        }
    }

    @Override
    public void paint() {
        int y = 25;
        drawString("S Falador Spinner", 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Spun count: " + spun_count, 25, y, 1, 0xFFFFFF);
    }
    
    private void _printOut() {
        System.out.println("Runtime: " + _getRuntime());
        System.out.println("Spun count: " + spun_count);
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
    
    private boolean _openSpinnerDoor() {
        if (getWallObjectIdFromCoords(297, 577) != DOOR_SHUT) {
            return false;
        } else {
            atWallObject(297, 577);
            return true;
        }
    }
    
    private boolean _openBankDoors() {
        int[] doors = getObjectById(BANK_SHUT);
        if (doors[0] != -1 && distanceTo(doors[1], doors[2]) < 16) {
            atObject(doors[1], doors[2]);
            return true;
        }
        return false;
    }
}
