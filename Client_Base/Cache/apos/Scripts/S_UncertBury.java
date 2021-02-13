import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Uncerts dragon bones in Yanille, buries them,
 * sleeps in the beds above Sidney Smith. Start
 * in her house. No parameters and no sleeping bag.
 * 
 * Has paint, proper quest menu listener. should
 * be able to handle things properly when less than 5 certs
 * are in the player's inventory.
 * 
 * Stops when very close to 99, when nothing left to do
 * prints out runtime info.
 * 
 * @author S
 */
public final class S_UncertBury extends Script {
    
    private static final int
    PRAYER = 5,
    BONES = 814,
    NPC = 778,
    CERTS = 1270,
    LADDER_UP = 5,
    LADDER_DOWN = 6,
    BED = 15,
    GNOMEBALL = 981;

    private long menu_time;
    private long start_time;
    private int count;
    private int start_xp;
    private int xp;
    
    private final DecimalFormat iformat = new DecimalFormat("#,##0");

    public S_UncertBury(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        start_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
            count = 0;
            start_xp = xp = getXpForLevel(PRAYER);
            menu_time = -1L;
        } else {
            xp = getXpForLevel(PRAYER);
        }
        
        if (xp >= (13034431 - 70)) {
            return _end("really close to 99, i'll let you get it.");
        }
        
        if (isQuestMenu()) {
            menu_time = -1L;
            int item_count = getInventoryCount(CERTS);
            int empty_slots = getEmptySlots();
            String target = "";
            if (item_count >= 5 && empty_slots >= 25) {
                target = "five";
            } else if (item_count >= 4 && empty_slots >= 20) {
                target = "four";
            } else if (item_count >= 3 && empty_slots >= 15) {
                target = "three";
            } else if (item_count >= 2 && empty_slots >= 10) {
                target = "two";
            } else if (item_count >= 1 && empty_slots >= 5) {
                target = "one cert"; // please don't say "n[one] thanks"
            }
            String[] options = questMenuOptions();
            int count = questMenuCount();
            if (!target.equals("")) {
                for (int i = 0; i < count; ++i) {
                    if (options[i].toLowerCase(Locale.ENGLISH).contains(target)) {
                        answer(i);
                        return random(900, 1300);
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
        
        if (getFatigue() > 95) {
            // ground level?
            if (getY() < 1000) {
                int[] ladder = getObjectById(LADDER_UP);
                if (object_valid(ladder)) {
                    atObject(ladder[1], ladder[2]);
                }
            } else {
                int[] bed = getObjectById(BED);
                if (object_valid(bed)) {
                    atObject(bed[1], bed[2]);
                }
            }
            return random(1000, 2000);
        }
        
        int bone = getInventoryIndex(BONES);
        if (bone != -1) {
            useItem(bone);
            return random(300, 500);
        }
        
        int ball = getInventoryIndex(GNOMEBALL);
        if (ball != -1) {
            System.out.println("got a gnomeball, thanks for the gift, whoever you are!");
            dropItem(ball);
            return random(2000, 3000);
        }
        
        int cert = getInventoryIndex(CERTS);
        if (cert == -1) {
            return _end("out of certs!");
        }
        
        if (getY() > 1000) {
            // above ground
            int[] ladder = getObjectById(LADDER_DOWN);
            if (object_valid(ladder)) {
                atObject(ladder[1], ladder[2]);
            }
            return random(1000, 2000);
        }
        
        int[] npc = getNpcByIdNotTalk(NPC);
        if (npc[0] != -1) {
            useOnNpc(npc[0], cert);
            menu_time = System.currentTimeMillis();
        }
        return random(600, 800);
    }

    @Override
    public void paint() {
        int white = 0xFFFFFF;
        int font = 2;
        int x = 25;
        int y = 25;
        drawString("S UncertBury", x, y, font, white);
        y += 15;
        drawString("Runtime: " + get_runtime(),
                x, y, font, white);
        y += 15;
        int gained = xp - start_xp;
        drawString("XP gained: " + int_format(gained) +
                " (" + per_hour(gained) + "/h)",
                x, y, font, white);
        y += 15;
        drawString("Bones used: " + int_format(count) +
                " (" + per_hour(count) + "/h)",
                x, y, font, white);
    }

    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("bury")) {
            ++count;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }
    
    private String int_format(long l) {
        return iformat.format(l);
    }
    
    private String get_runtime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 3600) {
            return int_format(secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
    
    private int _end(String message) {
        System.out.println("Runtime: " + get_runtime());
        int gained = xp - start_xp;
        System.out.println("XP gained: " + int_format(gained) +
                " (" + per_hour(gained) + "/h)");
        System.out.println("Bones used: " + int_format(count) +
                        " (" + per_hour(count) + "/h)");
        System.out.println(message);
        setAutoLogin(false); stopScript();
        return 0;
    }
    
    private String per_hour(int total) {
        long time = ((System.currentTimeMillis() - start_time) / 1000L);
        if (time < 1L) {
            time = 1L;
        }
        return int_format((total * 60L * 60L) / time);
    }
    
    private boolean object_valid(int[] object) {
        return object[0] != -1 && distanceTo(object[1], object[2]) < 40;
    }
}