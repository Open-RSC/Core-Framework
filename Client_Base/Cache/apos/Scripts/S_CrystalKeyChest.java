import java.util.Arrays;
import java.util.Locale;

public final class S_CrystalKeyChest extends Script {
    
    // banks in Catherby
    

    private static final int WITHDRAW_COUNT = 30;
    
    private static final class ChestItem {
        
        final String name;
        final int id;
        int count;
        
        ChestItem(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static final int ID_CHEST = 248;
    private static final int ID_KEY = 525;
    private static final int ID_TEETH = 526;
    private static final int ID_LOOP = 527;
    private static final int DOORS_SHUT = 64;
    private static final int STAIRS_UP = 43;
    private static final int STAIRS_DOWN = 359;
    
    private static final ChestItem[] items = {
        new ChestItem(400, "Rune chain"),
        new ChestItem(402, "Rune legs"),
        new ChestItem(542, "Uncut dragonstone"),
        new ChestItem(408, "Rune bar"),
        new ChestItem(ID_TEETH, "Half key (teeth)"),
        new ChestItem(ID_LOOP, "Half key (loop)"),
        new ChestItem(161, "Cut diamond"),
        new ChestItem(162, "Cut ruby"),
        new ChestItem(37, "Life-Rune"),
        new ChestItem(619, "Blood-Rune"),
        new ChestItem(38, "Death-Rune"),
        new ChestItem(40, "Nature-Rune"),
        new ChestItem(42, "Law-Rune"),
        new ChestItem(825, "Soul-Rune"),
        new ChestItem(46, "Cosmic-Rune"),
        new ChestItem(41, "Chaos-Rune"),
        new ChestItem(33, "Air-Rune"),
        new ChestItem(31, "Fire-Rune"),
        new ChestItem(32, "Water-Rune"),
        new ChestItem(34, "Earth-Rune"),
        new ChestItem(36, "Body-Rune"),
        new ChestItem(35, "Mind-Rune"),
        new ChestItem(517, "Iron cert"),
        new ChestItem(518, "Coal cert"),
        new ChestItem(10, "Coins"),
        new ChestItem(127, "Adamantite Square Shield"),
        new ChestItem(369, "Raw swordfish"),
        new ChestItem(179, "Spinach roll")
    };
    
    private int keys_withdrawn;
    private long start_time;
    private long bank_time;

    private final PathWalker pw;
    private PathWalker.Path chest_to_stairs;
    private PathWalker.Path chest_to_bank;
    private PathWalker.Path stairs_to_chest;
    private PathWalker.Path stairs_to_bank;
    private PathWalker.Path dungeon_to_chest;
    private PathWalker.Path dungeon_to_bank;
    private PathWalker.Path bank_to_stairs;
    private PathWalker.Path bank_to_chest;

    public S_CrystalKeyChest(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
        keys_withdrawn = 0;
        start_time = -1L;
        bank_time = -1L;
        pw.init(null);
        if (fishing_contest_done()) {
            chest_to_bank = null;
            bank_to_chest = null;
            
            chest_to_stairs = pw.calcPath(367, 496, 385, 465);
            stairs_to_chest = pw.calcPath(385, 465, 367, 496);

            bank_to_stairs = pw.calcPath(439, 496, 426, 457);
            stairs_to_bank = pw.calcPath(426, 457, 439, 496);
            
            dungeon_to_chest = pw.calcPath(426, 3293, 387, 3299);
            dungeon_to_bank = pw.calcPath(387, 3299, 426, 3293);
        } else {
            // they have the same y coord! :o
            chest_to_bank = pw.calcPath(367, 496, 439, 496);
            bank_to_chest = pw.calcPath(439, 496, 367, 496);
            
            chest_to_stairs = null;
            stairs_to_chest = null;

            bank_to_stairs = null;
            stairs_to_bank = null;
            
            dungeon_to_chest = null;
            dungeon_to_bank = null;
        }
    }
    
    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (inCombat()) {
            pw.resetWait();
            walkTo(getX(), getY());
            return random(300, 500);
        }
        if (isQuestMenu()) {
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        }
        if (isBanking()) {
            bank_time = -1L;
            int array_sz = items.length;
            for (int i = 0; i < array_sz; ++i) {
                ChestItem item = items[i];
                int count = getInventoryCount(item.id);
                if (count > 0) {
                    deposit(item.id, count);
                    item.count += count;
                    return random(2000, 3000);
                }
            }
            if (getInventoryCount(ID_KEY) > 0) {
                closeBank();
                return random(1000, 2000);
            }
            int banked = bankCount(ID_KEY);
            if (banked <= 0) {
                System.out.println("ERROR: Out of keys!");
                stopScript(); setAutoLogin(false);
                return 0;
            }
            int w = WITHDRAW_COUNT;
            if (w > banked) w = banked;
            withdraw(ID_KEY, w);
            keys_withdrawn += w;
            return random(2000, 3000);
        } else if (bank_time != -1) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        int teeth = getInventoryIndex(ID_TEETH);
        int loop = getInventoryIndex(ID_LOOP);
        if (teeth != -1 && loop != -1) {
            useItemWithItem(teeth, loop);
            return random(600, 800);
        }
        if (pw.walkPath()) return 0;
        // at the chest
        if (isAtApproxCoords(367, 496, 4)) {
                final ChestItem[] ground = get_ground_items();
                final int array_sz = ground.length;
                for (int i = 0; i < array_sz; ++i) {
                    if (getInventoryCount() == MAX_INV_SIZE) {
                        int meow = drop_greater(index_of(ground[i].id));
                        if (meow != 0) return meow;
                    } else {
                        pickupItem(ground[i].id, getX(), getY());
                        return random(1000, 1500);
                    }
                }
            
            int key = getInventoryIndex(ID_KEY);
            if (key != -1) {
                int[] chest = getObjectById(ID_CHEST);
                if (object_valid(chest)) {
                   useSlotOnObject(key, chest[1], chest[2]);
                    return random(1500, 3000);
                }
                return random(600, 1500);
            }
            
            if (chest_to_stairs != null) {
                pw.setPath(chest_to_stairs);
            } else {
                pw.setPath(chest_to_bank);
            }
            return 0;
        }
        // at the bank
        if (isAtApproxCoords(439, 496, 10)) {
            if (!hasInventoryItem(ID_KEY)) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    return random(2000, 3000);
                }
                return random(200, 300);
            }
            if (bank_to_stairs != null) {
                pw.setPath(bank_to_stairs);
            } else {
                pw.setPath(bank_to_chest);
            }
            return 0;
        }
        // at the chest stairs
        if (isAtApproxCoords(385, 465, 4)) {
            if (hasInventoryItem(ID_KEY)) {
                pw.setPath(stairs_to_chest);
                return 0;
            }
            int[] doors = getObjectById(DOORS_SHUT);
            if (object_valid(doors)) {
                at_object(doors);
                return random(1000, 1500);
            }
            int[] stairs = getObjectById(STAIRS_DOWN);
            if (object_valid(stairs)) {
                at_object(stairs);
                return random(1500, 2500);
            }
            System.out.println("wat do???");
            return random(200, 300);
        }
        // at the bank stairs
        if (isAtApproxCoords(426, 457, 4)) {
            if (hasInventoryItem(ID_KEY)) {
                int[] stairs = getObjectById(STAIRS_DOWN);
                if (object_valid(stairs)) {
                    at_object(stairs);
                    return random(1500, 2500);
                }
                System.out.println("wat do???");
                return random(200, 300);
            }
            pw.setPath(stairs_to_bank);
            return 0;
        }
        // at the underground chest stairs
        if (isAtApproxCoords(387, 3299, 5)) {
            if (hasInventoryItem(ID_KEY)) {
                int[] stairs = getObjectById(STAIRS_UP);
                if (object_valid(stairs)) {
                    at_object(stairs);
                    return random(1500, 2500);
                }
                System.out.println("wat do???");
                return random(200, 300);
            }
            pw.setPath(dungeon_to_bank);
            return 0;
        }
        // at the underground bank stairs
        if (isAtApproxCoords(426, 3293, 5)) {
            if (hasInventoryItem(ID_KEY)) {
                pw.setPath(dungeon_to_chest);
                return 0;
            }
            int[] stairs = getObjectById(STAIRS_UP);
            if (object_valid(stairs)) {
                at_object(stairs);
                return random(1500, 2500);
            }
            System.out.println("wat do???");
            return random(200, 300);
        }
        System.out.println("wat do???");
        return random(1000, 2000);
    }

    @Override
    public void paint() {
        final int orangey = 0xFFD900;
        final int white = 0xFFFFFF;
        int x = 25;
        int y = 25;
        drawString("S Crystal Key Chest",
            x, y, 1, orangey);
        y += 15;
        drawString("Runtime: " + get_runtime(),
                x + 10, y, 1, white);
        y += 15;
        drawString("Withdrawn keys: " + keys_withdrawn,
            x + 10, y, 1, white);
        y += 15;
        drawString("Banked items", x, y, 1, orangey);
        y += 15;
        int num = items.length;
        for (int i = 0; i < num; ++i) {
            ChestItem item = items[i];
            if (item.count > 0) {
                drawString(item.count + " " + item.name,
                    x + 10, y, 1, white);
                y += 15;
            }
        }
    }
    
    private void at_object(int[] object) {
        atObject(object[1], object[2]);
    }
    
    private boolean object_valid(int[] object) {
        if (object[0] == -1) return false;
        return distanceTo(object[1], object[2]) < 16;
    }
    
    private int drop_greater(int index) {
        int count = getInventoryCount();
        for (int j = 0; j < count; ++j) {
            if (index_of(getInventoryId(j)) > index) {
                dropItem(j);
                return random(1500, 2000);
            }
        }
        return 0;
    }
    
    private int index_of(int id) {
        int array_sz = items.length;
        for (int i = 0; i < array_sz; ++i) {
            if (items[i].id == id) {
                return i;
            }
        }
        return -1;
    }
    
    private ChestItem[] get_ground_items() {
        final int x = getX();
        final int y = getY();
        final int count = getGroundItemCount();
        final ChestItem[] result = new ChestItem[count];
        int ptr = 0;
        for (int i = 0; i < count; ++i) {
            if (getItemX(i) != x || getItemY(i) != y) {
                continue;
            }
            final int id = getGroundItemId(i);
            final int array_sz = items.length;
            for (int j = 0; j < array_sz; ++j) {
                if (items[j].id == id) {
                    result[ptr++] = items[j];
                    break;
                }
            }
        }
        return Arrays.copyOf(result, ptr);
    }
    
    private boolean fishing_contest_done() {
        int count = getQuestCount();
        for (int i = 0; i < count; ++i) {
            if (getQuestName(i).toLowerCase(Locale.ENGLISH).contains("fishing contest")) {
                return isQuestComplete(i);
            }
        }
        return false;
    }
    
    /*
     * I include this in basically everything now but nothing is becoming part
     * of the API unless it has to. It creates too many problems -_-
     * 
     * also lazy assholes don't upgrade
     */
    
    private String get_runtime() {
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
}
