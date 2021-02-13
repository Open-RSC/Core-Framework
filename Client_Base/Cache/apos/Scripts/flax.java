/*
Bloods Power Flax to Bowstring
November 1, 2012
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class flax extends Script {
    private static final int flax_id = 675;
    private static final int bowstring_id = 676;
    private static final int[] ladder = {
        5, 684, 520
    };
    private static final int[] wheel = {
        121, 686, 1464
    };
    private static final int[] flax = {
        313, 687, 519
    };

    private int flax_made = 0;
    private int xp_to_stop_at = 13034200; // go on, have a party
    private static final int[] next_level = {
        0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107,
        2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740,
        9730, 10824, 12031, 13363, 14833, 16456, 18247, 20224, 22406, 24815,
        27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983,
        75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636,
        184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599, 407015,
        449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
        1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818,
        2421087, 2673114, 2951373, 3258594, 3597792, 3972294, 4385776, 4842295,
        5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577,
        10692629, 11805606, 13034431
    };
    private long start_xp = 0;
    // for (play);
    private long time; // oh god i'm so funny@@@@
    private boolean hop = false;

    public flax(Extension e) {
        super(e);
    }

    public void init(String p) {
        flax_made = 0;
        start_xp = 0;
        time = 0L;

        if (p.equals(""))
            print("Power Flax to Bowstring by Blood started!");
        else if (p.equalsIgnoreCase("debug")) {
            print("Power Flax to Bowstring by Blood started! (Debug ON)"); // Doesn't
                                                                           // actually
                                                                           // do
                                                                           // anything,
                                                                           // never
                                                                           // implemented
                                                                           // it
        } else
            print(p + " is an invalid parameter, starting with defaults.");

        File file = new File(".\\logs\\");
        boolean exists = file.exists();
        if (!exists) {
            File f = new File(".\\logs");
            f.mkdir();
            print("Created \"logs\" directory in the APOS directory, chat will be stored here.");
        }
    }

    public int main() {
        if (start_xp <= 0) {
            start_xp = getXpForLevel(skillName("crafting"));
            time = System.currentTimeMillis();
            print("Stored starting variables.");
            return 1000;
        }
        if (getXpForLevel(skillName("crafting")) > xp_to_stop_at) {
            setAutoLogin(false);
            stopScript();
            logout();
            print("Passed set experience cap, stopping script.");
        }
        if (getFatigue() > 95) {
            useSleepingBag();
            return 3000;
        }
        if (hop) {
            if (getWorld() == 1) {
                hop(2);
                hop = false;
                return 3000;
            }
            if (getWorld() == 2) {
                hop(3);
                hop = false;
                return 3000;
            }
            /*
             * if(getWorld() == 3) {
             * hop(1);
             * hop = false;
             * return 3000;
             * }
             */
            if (getWorld() == 3) {
                hop(2);
                hop = false;
                return 3000;
            }
            return 3000;
        }
        if (getY() > 1000) {
            if (getInventoryCount(flax_id) > 0) {
                useItemOnObject(flax_id, wheel[1], wheel[2]);
                return random(400, 600);
            }
            if (getInventoryCount(flax_id) == 0
                    && getInventoryCount(bowstring_id) > 0) {
                dropItem(getInventoryIndex(bowstring_id));
                return random(300, 500);
            } else {
                atObject(ladder[1], ladder[2] + 944);
                return random(300, 500);
            }
        }
        if (getY() < 1000) {
            if (getEmptySlots() > 0) {
                atObject2(flax[1], flax[2]);
                return random(300, 500);
            }
            if (getInventoryCount(981) > 0) {
                dropItem(getInventoryIndex(981));
                print("Someone gave me a gnomeball, how kind of them. *drop*");
                log("Someone gave me a gnomeball, dropping it.", "SystemLog");
                return random(500, 1000);
            } else {
                atObject(ladder[1], ladder[2]);
                return random(300, 500);
            }
        }
        return random(800, 1200);
    }

    public int skillName(String s) {
        for (int i = 0; i < 18; i++) {
            if (SKILL[i].equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    public void print(String s) {
        System.out.println("\n" + s + "\n");
    }

    public void log(String msg, String name) {
        Date date = new Date();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(".\\logs\\"
                    + name + ".txt", true));
            out.write(date.toString() + " " + name + " : " + msg + "\r\n");
            out.close();
        } catch (IOException e) {
        }
    }

    public void onKeyPress(int k) {
        if (k == 192) {
            flax_made = 0;
            start_xp = 0;
            time = 0L;
            print("Variables reset.");
        }
    }

    public void onServerMessage(String s) {
        if (s.contains("make the flax into"))
            flax_made++;
        /*
         * else if(s.contains("have been standing")) {
         * AutoLogin.setAutoLogin(false); stopScript(); logout();
         * print(
         * "Something went wrong! We stood in one spot for too long. Stopping script"
         * );
         * }
         */}

    public void onChatMessage(String msg, String name, boolean pmod,
            boolean jmod) {
        log(msg, name);
        msg = msg.toLowerCase();
        /*
         * if(msg.contains("bot") || msg.contains("your username")) { //
         * Uncomment and change if you'd like.
         * print(name + " said \"" + msg + "\", hopping.");
         * log(name + " said \"" + msg + "\", hopping.","SystemLog");
         * hop = true;
         * }
         */
        if (pmod) {
            print(name + ", a player mod, said \"" + msg + "\", hopping.");
            log(name + ", a player mod, said \"" + msg + "\", hopping.",
                    "SystemLog");
            hop = true;
        }
        if (jmod) {
            print(name + " said \"" + msg + "\", logging out.");
            log(name + " said \"" + msg + "\", logging out.", "SystemLog");
            AutoLogin.setAutoLogin(false);
            stopScript();
            logout();
        }
    }

    public void onPrivateMessage(String msg, String name, boolean pmod,
            boolean jmod) {
        log(msg + " (PM)", name);
        msg = msg.toLowerCase();
        if (jmod) {
            print(name + " messaged us \"" + msg + "\", logging out.");
            log(name + " messaged us \"" + msg + "\", logging out.",
                    "SystemLog");
            AutoLogin.setAutoLogin(false);
            stopScript();
            logout();
        }
        if (msg.contains("many bowstrings"))
            sendPrivateMessage(flax_made + "", name);
        if (msg.contains("long have you been"))
            sendPrivateMessage(getRunTime(), name);
        if (msg.contains("crafting level"))
            sendPrivateMessage(getLevel(skillName("crafting")) + "", name);
    }

    public void paint() {
        double xp_til_next_level = next_level[getLevel(skillName("crafting"))]
                - getXpForLevel(skillName("crafting"));
        double xp_between_levels = next_level[getLevel(skillName("crafting"))]
                - next_level[getLevel(skillName("crafting")) - 1];
        double boxWidthDouble = (1 - (xp_til_next_level / xp_between_levels)) * 182;
        int boxWidth = (int) boxWidthDouble;
        int x = 12;
        int y = 50;
        drawString("Blood's Power Flax to Bowstring", 8, 33, 4, 0x00b500);
        drawString("XP Gained: "
                + (getXpForLevel(skillName("crafting")) - start_xp) + " ("
                + getXpH() + " XP/H)", x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Current level: " + getLevel(skillName("crafting")) + " ("
                + getXpForLevel(skillName("crafting")) + " XP)", x, y, 1,
                0xFFFFFF);
        y += 15;
        drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Crafted " + flax_made + " bowstrings", x, y, 1, 0xFFFFFF);
        drawBoxFill(9, 101, boxWidth, 14, 0x00b500);
        drawVLine(9 + boxWidth, 101, 14, 0xff0000);
        drawVLine(8, 37, 62, 0xFFFFFF);
        drawHLine(8, 99, 183, 0xFFFFFF);
        drawBoxOutline(8, 100, 183, 16, 0xFFFFFF);
        drawString(
                (next_level[getLevel(skillName("crafting"))] - getXpForLevel(skillName("crafting")))
                        + " XP til level "
                        + (getLevel(skillName("crafting")) + 1), 12, 112, 1,
                0xFFFFFF);
    }

    private long getXpH() {
        try {
            long xph = (((getXpForLevel(skillName("crafting")) - start_xp) * 60) * 60)
                    / (((System.currentTimeMillis() - time) / 1000));
            return xph;
        } catch (ArithmeticException e) {
        }
        return 0;
    }

    private String getRunTime() {
        long ttime = ((System.currentTimeMillis() - time) / 1000);
        if (ttime >= 7200)
            return new String((ttime / 3600) + " hours, "
                    + ((ttime % 3600) / 60) + " minutes, " + (ttime % 60)
                    + " seconds.");
        if (ttime >= 3600 && ttime < 7200)
            return new String((ttime / 3600) + " hour, "
                    + ((ttime % 3600) / 60) + " minutes, " + (ttime % 60)
                    + " seconds.");
        if (ttime >= 60)
            return new String(ttime / 60 + " minutes, " + (ttime % 60)
                    + " seconds.");
        return new String(ttime + " seconds.");
    }
}