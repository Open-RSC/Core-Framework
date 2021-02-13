/*
 * Created with IntelliJ IDEA.
 * User: taylor
 * Project: APOS
 * Date: 3/9/14
 * Time: 3:10 PM
 */

import java.util.Date;

public final class OgreRanger extends Script {

    private static final int OGRE = 525;
    private static final int X = 662;
    private static final int Y = 532;
    private static final int[] ARROWS = {11, 638, 640, 642, 644, 646};
    private int current_x;
    private int current_y;
    private int y_offset;
    private int initial_arrows;
    private int[] initial_xp;
    private long time;
    private long trading;
    private long move;
    private long[] screenshot;
    private String filename;
    private String partner_name;
    
    private static final int COMBAT_STYLE = 1;

    public OgreRanger(Extension e) {
        super(e);
    }

    @Override
    public void init(String s) {
        if (s.length() > 12 || s.contains(",")) {
            System.out.println("Error: Parameter should be your partners name");
            stop();
        }
        screenshot = new long[4];
        current_x = 0;
        current_y = 0;
        y_offset = 0;
        initial_arrows = 0;
        initial_xp = new int[SKILL.length];
        time = -1L;
        trading = -1L;
        move = -1L;
        partner_name = s;
        if (!partner_name.isEmpty()) {
            System.out.println("Accepting trades from " + partner_name);
        }
    }

    @Override
    public int main() {
        if (getFightMode() != COMBAT_STYLE) {
            setFightMode(COMBAT_STYLE);
            return random(600, 800);
        }
        if (initial_xp[0] == 0) {
            for (int i = 0; i < SKILL.length; i++) {
                initial_xp[i] = getXpForLevel(i);
            }
            initial_arrows = getInventoryCount(ARROWS);
            time = System.currentTimeMillis();
            return random(800, 1200);
        }
        int x = getX();
        int y = getY();
        if (x == 0) {
            return 50;
        }
        long now = System.currentTimeMillis();
        if (screenshot[0] != -1L) {
            return screenshot(now);
        }
        int[] partner = getPlayerByName(partner_name);
        if (partner[0] == -1) {
            return random(800, 1200);
        }
        if (getFatigue() > 95) {
            useSleepingBag();
            return random(800, 1200);
        }
        if (move != -1L) {
            if (current_x == x && current_y == y) {
                walkTo(X, Y + y_offset);
                if (++y_offset > 2) y_offset = 0;
            } else {
                move = -1L;
                return 0;
            }
            return random(800, 1200);
        }
        if (trading != -1L) {
            if (now > trading + 20000L) {
                trading = -1L;
                return random(800, 1200);
            }
            if (isInTradeOffer()) {
                acceptTrade();
                return random(800, 1200);
            }
            if (isInTradeConfirm()) {
                confirmTrade();
                return random(800, 1200);
            }
            sendTradeRequest(getPlayerPID(partner[0]));
            return 2000;
        }
        if (x != X || y != Y + y_offset) {
            walkTo(X, Y + y_offset);
            return random(800, 1200);
        }
        int bone = getInventoryIndex(BONES);
        if (bone != -1) {
            useItem(bone);
            return random(600, 800);
        }
        int[] npc = getNpcById(OGRE);
        if (npc[0] != -1) {
            attackNpc(npc[0]);
            return random(600, 800);
        }
        return 200;
    }

    @Override
    public void paint() {
        int x = 12;
        int y = 50;
        int arrows = initial_arrows - getInventoryCount(ARROWS);
        if (arrows == 0) {
            arrows = 1;
        }
        int[] xpStats;
        drawString("Blood's Ogre Ranger", x - 4, y - 17, 4, 0x00b500);
        xpStats = getXpStatistics(4);
        drawString("Ranged XP Gained: " + xpStats[2] + " (" + xpStats[3] + " XP/h)", x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Arrows spent: " + arrows + " (" + (xpStats[2] / arrows) + " XP/arrow)", x, y, 1, 0xFFFFFF);
        y += 15;
        xpStats = getXpStatistics(5);
        drawString("Prayer XP Gained: " + xpStats[2] + " (" + xpStats[3] + " XP/h)", x, y, 1, 0xFFFFFF);
        y += 15;
        drawString("Runtime: " + getRunTime(), x, y, 1, 0xFFFFFF);
        drawVLine(8, 37, y + 3 - 37, 0xFFFFFF);
        drawHLine(8, y + 3, 183, 0xFFFFFF);
    }

    public int[] getNpcById(int... ids) {
        final int[] npc = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < countNpcs(); i++) {
            if (inArray(ids, getNpcId(i)) && !isNpcInCombat(i)) {
                final int x = getNpcX(i);
                final int y = getNpcY(i);
                final int dist = distanceTo2(x, y, getX(), getY());
                if (dist < max_dist) {
                    npc[0] = i;
                    npc[1] = x;
                    npc[2] = y;
                    max_dist = dist;
                }
            }
        }
        return npc;
    }

    public int distanceTo2(int x1, int y1, int x2, int y2) { // prefer adjacent tiles over diagonal
        int x = x2 - x1;
        int y = y2 - y1;
        x = (x < 0 ? -x : x);
        y = (y < 0 ? -y : y);
        return x + y;
    }

    @Override
    public void onTradeRequest(String name) {
        if (name.equals(partner_name)) {
            if (trading == -1L) {
                trading = System.currentTimeMillis();
                current_x = getX();
                current_y = getY();
                move = trading;
            }
        }
    }

    @Override
    public void onServerMessage(String s) {
        if (s.contains("completed") || s.contains("declined")) {
            trading = -1L;
            return;
        }
        if (s.contains("near enough") || s.contains("an obstacle") || s.contains("been standing") || s.contains("close enough")) {
            move = System.currentTimeMillis();
            current_x = getX();
            current_y = getY();
            return;
        }
        if (s.contains("just advanced")) {
            screenshot[0] = System.currentTimeMillis();
            filename = new Date().getTime() + " - " + s;
        }
    }

    @Override
    public void onChatMessage(String msg, String name, boolean pmod, boolean jmod) {
        System.out.println(name + ": " + msg);
        if (jmod) {
            stop();
        }
    }

    @Override
    public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
        System.out.println(name + ": " + msg + " (PM)");
        if (jmod) {
            stop();
        }
    }

    @Override
    public String getPlayerName(int local_index) { // storm
        // did I seriously never fix this? fuck me.
        return super.getPlayerName(local_index)
                .replace((char) 160, ' ');
    }

    private int[] getXpStatistics(int skill) {
        long time = ((System.currentTimeMillis() - this.time) / 1000L);
        if (time < 1L) {
            time = 1L;
        }
        int start_xp = initial_xp[skill];
        int current_xp = getXpForLevel(skill);
        int[] intArray = new int[4];
        intArray[0] = current_xp;
        intArray[1] = start_xp;
        intArray[2] = intArray[0] - intArray[1];
        intArray[3] = (int) ((((current_xp - start_xp) * 60L) * 60L) / time);
        return intArray;
    }

    private void stop() {
        setAutoLogin(false);
        logout();
        stopScript();
    }

    private String getRunTime() {
        long millis = (System.currentTimeMillis() - time) / 1000;
        long second = millis % 60;
        long minute = (millis / 60) % 60;
        long hour = (millis / (60 * 60)) % 24;
        long day = (millis / (60 * 60 * 24));

        if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
        if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
        if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
        return String.format("%02d seconds", second);
    }

    private int screenshot(long now) {
        if (now > screenshot[0] + 3000L) {
            screenshot[0] = -1L;
            return 50;
        }
        if (screenshot[1] == 0) {
            if (isPaintOverlay()) { // Get state of Paint
                screenshot[2] = 1L; // If it's on, remember this
                setPaintOverlay(false); // If it's on, turn it off for the screenshot
            } else {
                screenshot[2] = -1L; // If it's off, remember this
            }
            if (isRendering()) { // Get state of Graphics
                screenshot[3] = 1L; // If it's on, remember this
            } else {
                screenshot[3] = -1L; // If it's off, remember this
                setRendering(true); //If it's off, turn it on for the screenshot
            }
            screenshot[1] = 1;
            return 50;
        }
        if (isSkipLines()) { // to be uncommented 2.4
            setSkipLines(false);
        }
        if (now < screenshot[0] + 1000L) { // time for paint() to redraw...
            return 50;
        }
        takeScreenshot(filename); // Take screenshot
        screenshot[0] = -1L;
        screenshot[1] = 0;
        if (screenshot[2] == 1L) { // If Paint was enabled before the screenshot, turn it back on
            PaintListener.toggle();
        }
        if (screenshot[2] == 1L) { // If Paint was enabled before the screenshot, turn it back on
            setPaintOverlay(true);
        }
        if (screenshot[3] != 1L) { // If the Graphics were off before the screenshot, turn them back off
            setRendering(false);
        }
        return 50;
    }
}