public final class lowwall extends Script {

    private int[] initial_xp = new int[SKILL.length];
    private int[][] walls = {
        { 495, 559 },
        { 495, 558 }
    };
    private int wall = 0;
    private long time;

    public lowwall(Extension e) {
        super(e);
    }

    public int main() {
        if (getFatigue() >= 90) {
            useSleepingBag();
            return random(1500, 2000);
        }

        if (initial_xp[0] == 0) {
            for (int i = 0; i < SKILL.length; i++) {
                initial_xp[i] = getXpForLevel(i);
            }
            time = System.currentTimeMillis();
        }

        if (!isWalking()) {
            atWallObject(walls[wall][0], walls[wall][1]);
            return random(200, 300);
        }
        return 0;
    }

    public void paint() {
        int x = 12;
        int y = 50;
        int current_xp = getXpForLevel(skillName("agility"));
        int start_xp = initial_xp[skillName("agility")];
        drawString("Blood's Low Wall Jumper", 8, 33, 4, 0x00b500);
        drawString("Agility XP Gained: " + (current_xp - start_xp) + " ("
                + getXpH("agility") + " XP/H)", x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Hopped the wall " + ((current_xp - start_xp) / 5)
                + " times.", x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
        drawVLine(8, 37, y + 3 - 37, 0xFFFFFF);
        drawHLine(8, y + 3, 183, 0xFFFFFF);
    }

    private int getXpH(String skill) {
        long start_xp = initial_xp[skillName(skill)];
        long current_xp = getXpForLevel(skillName(skill));
        try {
            int xph = (int) ((((current_xp - start_xp) * 60L) * 60L) / ((System
                    .currentTimeMillis() - time) / 1000L));
            return xph;
        } catch (ArithmeticException e) {
        }
        return 0;
    }

    public int skillName(String s) {
        for (int i = 0; i <= 17; i++) {
            if (SKILL[i].equalsIgnoreCase(s)) {
                return i;
            }
        }
        return -1;
    }

    private String getRunTime() {
        long ttime = ((System.currentTimeMillis() - time) / 1000);
        if (ttime >= 3600) {
            return (ttime / 3600) + " hour, " + ((ttime % 3600) / 60)
                    + " minutes, " + (ttime % 60) + " seconds.";
        }
        if (ttime >= 60) {
            return ttime / 60 + " minutes, " + (ttime % 60) + " seconds.";
        }
        return ttime + " seconds.";
    }

    public void onServerMessage(String s) {
        if (s.contains("have been standing here")) {
            wall = (wall == 0) ? 1 : 0;
        }
    }
}