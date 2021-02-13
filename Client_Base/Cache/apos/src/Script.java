import com.aposbot.Constants;
import com.aposbot.LocalRouteCalc;
import com.aposbot.RasterOps;
import com.aposbot._default.IClient;
import com.aposbot._default.IScript;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class Script
        implements IScript {

    /**
     * An array of all the Banker NPC IDs.
     */
    public static final int[] BANKERS = {
            95, 224, 268, 485, 540, 617
    };
    /**
     * An array of all the bone item IDs.
     */
    public static final int[] BONES = {
            20, 413, 604, 814
    };
    /**
     * An array of all the skill names.
     */
    public static final String[] SKILL = StaticAccess.get().getSkillNames();
    /**
     * An array of all the spell names.
     */
    public static final String[] SPELL = StaticAccess.get().getSpellNames();
    /**
     * An array containing: "Controlled", "Strength", "Attack", "Defence"
     */
    public static final String[] FIGHTMODES = {
            "Controlled", "Strength", "Attack", "Defence"
    };
    /**
     * The maximum number of items the client's inventory can hold.
     */
    public static final int MAX_INV_SIZE = 30;
    public static final int DIR_NORTH = 0;
    public static final int DIR_NORTHWEST = 1;
    public static final int DIR_WEST = 2;
    public static final int DIR_SOUTHWEST = 3;
    public static final int DIR_SOUTH = 4;
    public static final int DIR_SOUTHEAST = 5;
    public static final int DIR_EAST = 6;
    public static final int DIR_NORTHEAST = 7;
    private static HashMap<Image, BufferedImage> imageCache;
    private final IClient client;
    private String toType = "";
    private int typeOffset;
    private LocalRouteCalc locRouteCalc;
    private boolean trick;

    /**
     * Creates the Script object. Called by the bot on start-up.
     *
     * @param ex the client.
     */
    public Script(Extension ex) {
        this.client = ex;
    }

    public static String getObjectName(int id) {
        return StaticAccess.get().getObjectName(id);
    }

    public static String getObjectDesc(int id) {
        return StaticAccess.get().getObjectDesc(id);
    }

    public static String getWallObjectName(int id) {
        return StaticAccess.get().getBoundName(id);
    }

    public static String getWallObjectDesc(int id) {
        return StaticAccess.get().getBoundDesc(id);
    }

    /**
     * Returns the distance between x1, y1 and x2, y2.
     *
     * @param x1 the first x coordinate.
     * @param y1 the first y coordinate.
     * @param x2 the second x coordinate.
     * @param y2 the second y coordinate.
     * @return the distance between x1, y1 and x2, y2.
     */
    public static int distanceTo(int x1, int y1, int x2, int y2) {
        return (int) Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    private static void drawBuf(BufferedImage image,
                                int start_x, int start_y,
                                int[] pixels, int rw, int rh) {

        final int width = image.getWidth();
        final int height = image.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int d_x = (start_x + x);
                final int d_y = (start_y + y);
                if (d_x > rw || d_y > rh) {
                    continue;
                }
                pixels[d_x + d_y * 512] = 0xff000000 | image.getRGB(x, y);
            }
        }
    }

    public static String getNpcNameId(int id) {
        return StaticAccess.get().getNpcName(id);
    }

    public static String getNpcDescriptionId(int id) {
        return StaticAccess.get().getNpcDesc(id);
    }

    public static int getNpcCombatLevelId(int id) {
        return StaticAccess.get().getNpcLevel(id);
    }

    public static String getItemNameId(int id) {
        return StaticAccess.get().getItemName(id);
    }

    public static String getItemDescriptionId(int id) {
        return StaticAccess.get().getItemDesc(id);
    }

    public static String getItemCommandId(int id) {
        return StaticAccess.get().getItemCommand(id);
    }

    public static boolean isItemTradableId(int id) {
        return StaticAccess.get().isItemTradable(id);
    }

    public static boolean isItemStackableId(int id) {
        return StaticAccess.get().isItemStackable(id);
    }

    public static int getItemBasePriceId(int id) {
        return StaticAccess.get().getItemBasePrice(id);
    }

    /**
     * Generates a psuedo-random number.
     *
     * @param min lowest possible number to generate.
     * @param max highest possible number to generate.
     * @return a psuedo-random number between min and max.
     */
    public static int random(int min, int max) {
        return Constants.RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * Returns true if the int[] contains the int.
     *
     * @param haystack int[] to search.
     * @param needle   int to search for.
     * @return true if the int[] contains the int.
     */
    public static boolean inArray(int[] haystack, int needle) {
        for (final int element : haystack) {
            if (element == needle) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param ms time in milliseconds to sleep for.
     * @deprecated Will cause the entire game to freeze when called in main()'s
     * thread. The RSC servers expect a ping every 5 seconds among
     * other things, so use of this method is definitely not
     * recommended. If you find yourself using this, consider
     * updating your code to make use of System.currentTimeMillis()
     * or System.nanoTime().
     */
    @Deprecated
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (final Throwable t) {
        }
    }

    public static boolean isCombatSpell(int spell) {
        return StaticAccess.get().getSpellType(spell) == 2;
    }

    public static boolean isCastableOnInv(int spell) {
        return StaticAccess.get().getSpellType(spell) == 3;
    }

    public static boolean isCastableOnGroundItem(int spell) {
        return StaticAccess.get().getSpellType(spell) == 3;
    }

    public static boolean isCastableOnSelf(int spell) {
        return StaticAccess.get().getSpellType(spell) < 2;
    }

    public static int getPrayerCount() {
        return StaticAccess.get().getPrayerCount();
    }

    public static String getPrayerName(int i) {
        return StaticAccess.get().getPrayerName(i);
    }

    public static int getPrayerLevel(int i) {
        return StaticAccess.get().getPrayerLevel(i);
    }

    /**
     * Called when the Script is selected by the user - override it and use it
     * to process the parameters.
     *
     * @param params the parameters entered by the user when the script is
     *               selected.
     */
    @Override
    public void init(String params) {
    }

    /**
     * The bot will wait for the milliseconds main() returns before calling it
     * again. It is only called when logged in and not sleeping. Override it and
     * use it for your Script's logic processing.
     */
    @Override
    public int main() {
        return 0;
    }

    /**
     * Called when the game is re-drawn. Override it and use it for your
     * Script's drawing functions.
     */
    @Override
    public void paint() {
    }

    /**
     * Called when a server message is sent, i.e "Welcome to RuneScape!".
     *
     * @param str the message.
     */
    @Override
    public void onServerMessage(String str) {
    }

    /**
     * Called when a trade request is recieved by your player.
     *
     * @param name the display name of the player who sent the request.
     */
    @Override
    public void onTradeRequest(String name) {
        System.out.println(name + " wishes to trade with you.");
    }

    /**
     * Called when a player speaks in public chat.
     *
     * @param msg   the message.
     * @param name  the display name of the player who sent the message.
     * @param mod   true if the sender is a player moderator (silver crown).
     * @param admin true if the sender is a Jagex moderator (gold crown).
     */
    @Override
    public void onChatMessage(String msg, String name, boolean mod,
                              boolean admin) {
        System.out.println(name + ": " + msg);
    }

    /**
     * Called when a player speaks in private chat.
     *
     * @param msg   the message.
     * @param name  the display name of the player who sent the message.
     * @param mod   true if the sender is a player moderator (silver crown).
     * @param admin true if the sender is a jagex moderator (gold crown).
     */
    @Override
    public void onPrivateMessage(String msg, String name, boolean mod,
                                 boolean admin) {
        System.out.println("(PRIV) " + name + ": " + msg);
    }

    /**
     * Called when the user presses a key on the keyboard and the client is the
     * active window. Do not use this to send key events, it does not work that
     * way. Instead use void setTypeLine(String s) and boolean next().
     *
     * @param keycode the key pressed expressed as an integer
     * @see Script#disableKeys()
     */
    @Override
    public void onKeyPress(int keycode) {
    }

    /**
     * Disables keys used by the bot so they can be used by the script. They are
     * enabled again when the user stops the script.
     */
    public void disableKeys() {
        client.setKeysDisabled(true);
    }

    /**
     * Hops to the specified world.
     *
     * @param world the world.
     */
    public void hop(int world) {
        if (world < 0 || world > 5) {
            System.out.println("Invalid world number: " + world);
            logout();
            return;
        }
        client.setServer(world);
        logout();
    }

    /**
     * Hops to the next world in a predefined sequence.
     *
     * @param veteran true to enable hopping to world one
     */
    public void autohop(boolean veteran) {
        switch (getWorld()) {
            case 1:
                hop(2);
                break;
            case 2:
                hop(3);
                break;
            case 3:
                hop(4);
                break;
            case 4:
                hop(5);
                break;
            case 5:
                hop(veteran ? 1 : 2);
                break;
        }
    }

    /**
     * Returns the world the client is logged in to.
     *
     * @return the world the client is logged in to.
     */
    public int getWorld() {
        return client.getServer();
    }

    /**
     * Stops the script.
     *
     * @see Script#setAutoLogin(boolean)
     */
    public void stopScript() {
        client.stopScript();
    }

    /**
     * Attempts to log the client out.
     */
    public void logout() {
        if (client.getCombatTimer() > 450) {
            client.displayMessage("@cya@You can't logout during combat!");
            return;
        }
        if (client.getCombatTimer() > 0) {
            client.displayMessage("@cya@You can't logout for 10 seconds after combat");
            return;
        }
        client.createPacket(Constants.OP_LOGOUT);
        client.finishPacket();
        client.setLogoutTimer(1000);
    }

    /**
     * Returns true if the sleeping/fatigue screen is visible.
     *
     * @return true if the sleeping/fatigue screen is visible.
     */
    @Override
    public boolean isSleeping() {
        return client.isSleeping();
    }

    /**
     * Returns the client's fatigue percentage.
     *
     * @return the client's fatigue percentage.
     */
    @Override
    public int getFatigue() {
        if (isSleeping()) {
            return (int) client.getSleepingFatigue();
        }
        return (int) client.getFatigue();
    }

    public double getAccurateFatigue() {
        if (isSleeping()) {
            return client.getSleepingFatigue();
        }
        return client.getFatigue();
    }

    /**
     * Returns the X position of the tile the client is standing on.
     *
     * @return the X position of the tile the client is standing on.
     */
    public int getX() {
        return client.getLocalX() + client.getAreaX();
    }

    /**
     * Returns the Y position of the tile the client is standing on.
     *
     * @return the Y position of the tile the client is standing on.
     */
    public int getY() {
        return client.getLocalY() + client.getAreaY();
    }

    /**
     * Returns true if the client can log out. This is determined by the time
     * since the client was last in combat.
     *
     * @return true if the client can log out.
     */
    public boolean canLogout() {
        return client.getCombatTimer() <= 0;
    }

    public boolean isTalking() {
        return client.isMobTalking(client.getPlayer());
    }

    public boolean isHpBarShowing() {
        return client.isHpBarVisible(client.getPlayer());
    }

    /**
     * Returns true if the client is in combat.
     *
     * @return true if the client is in combat.
     */
    public boolean inCombat() {
        return client.isMobInCombat(client.getPlayer());
    }

    /**
     * Returns true if an icon is visible over the client's head. An icon is
     * drawn when certain actions such as mining, cutting down a tree, or
     * cooking food are performed. Note that this will not return true if the
     * client only has a skull icon.
     *
     * @return true if the player has a head icon visible.
     */
    public boolean isSkilling() {
        return client.isHeadIconVisible(client.getPlayer());
    }

    /**
     * Returns true if the client is walking.
     *
     * @return true if the client is walking.
     */
    public boolean isWalking() {
        return client.isMobWalking(client.getPlayer());
    }

    /**
     * @return not what you think it returns!
     * @deprecated For internal use. Legacy.
     */
    @Override
    @Deprecated
    public boolean isTricking() {
        if (trick && getSleepingFatigue() < 88) {
            useSleepingBag();
            return false;
        }
        return trick && getSleepingFatigue() < 100;
    }

    /**
     * Enables or disables fatigue tricking, which is disabled by default. If
     * the player kills certain NPCs with 99% fatigue in melee combat, they can
     * gain combat XP without hitpoints XP. If the bot is instructed to use the
     * fatigue trick, it will attempt to keep the fatigue at 99% when it sleeps.
     * The script should take care of the rest.
     *
     * @param flag on/off
     */
    public void setTrickMode(boolean flag) {
        this.trick = flag;
    }

    /**
     * @return the client's current fatigue percentage.
     * @deprecated Use {@link Script#getFatigue()}.
     */
    @Deprecated
    public int getSleepingFatigue() {
        return getFatigue();
    }

    String getTypeLine() {
        return toType;
    }

    /**
     * Sets the line to be typed by next().
     *
     * @param str the line.
     */
    public void setTypeLine(String str) {
        toType = str;
    }

    /**
     * Types the next character of the setTypeLine or newline if it has reached
     * the end of the String.
     *
     * @return false until it has finished typing the set line.
     */
    public boolean next() {
        if (typeOffset >= toType.length()) {
            client.typeChar('\n', 10);
            typeOffset = 0;
            return true;
        }
        final char c = toType.charAt(typeOffset++);
        client.typeChar(c, c);
        return false;
    }

    /**
     * Returns the client's combat style.
     *
     * @return the client's selected combat style (position in the list starting
     * at 0).
     */
    public int getFightMode() {
        return client.getCombatStyle();
    }

    /**
     * Sets the client's combat style.
     *
     * @param style the combat style's position in the list starting at 0.
     */
    public void setFightMode(int style) {
        if (style > 3 || style < 0) {
            System.out.println("combat style must range from zero to three");
            return;
        }
        client.setCombatStyle(style);
        client.createPacket(Constants.OP_SET_COMBAT_STYLE);
        client.put1(style);
        client.finishPacket();
    }

    /**
     * Returns the client's level in the given skill.
     *
     * @param skill the skill's identifer (position in the list starting at 0).
     * @return the player's level in specified skill.
     * @see Script#getCurrentLevel(int)
     */
    public int getLevel(int skill) {
        return client.getBaseLevel(skill);
    }

    /**
     * Returns the client's current level in the given skill. The returned
     * integer, unlike getLevel, will be different if the skill has been drained
     * or raised.
     *
     * @param skill the skill's identifer (position in the list starting at 0).
     * @return the player's current level in specified skill.
     * @see Script#getLevel(int)
     */
    public int getCurrentLevel(int skill) {
        return client.getCurrentLevel(skill);
    }

    /**
     * Returns the client's experience in the given skill.
     *
     * @param skill the skill's identifer (position in the list starting at 0).
     * @return the client's experience in specified skill.
     */
    public int getXpForLevel(int skill) {
        return (int) client.getExperience(skill);
    }

    /**
     * Returns the client's experience in the given skill.
     *
     * @param skill the skill's identifer (position in the list starting at 0).
     * @return the client's experience in specified skill.
     */
    public double getAccurateXpForLevel(int skill) {
        return client.getExperience(skill);
    }

    /**
     * Returns the percentage of hitpoints the client has remaining.
     *
     * @return the percentage of hitpoints the client has remaining.
     */
    public int getHpPercent() {
        final double d = (double) getCurrentLevel(3) / (double) getLevel(3);
        return (int) (d * 100.0D);
    }

    /**
     * Casts a spell with the given id on the local player. For teleport/charge
     * spells.
     *
     * @param spell the spell's identifier (position in the list starting at 0).
     */
    public void castOnSelf(int spell) {
        if (!canCastSpell(spell)) {
            System.out.println("Can't cast that spell.");
            return;
        }
        client.createPacket(Constants.OP_SELF_CAST);
        client.put2(spell);
        client.finishPacket();
    }

    /**
     * Attempts to walk to the given x, y coordinates.
     *
     * @param x the x position of the target tile.
     * @param y the y position of the target tile.
     */
    public void walkTo(int x, int y) {
        if (!isReachable(x, y)) {
            System.out.println("Trying to walk to an unreachable tile: " + x + "," + y);
        } else {
            client.walkDirectly(x - client.getAreaX(), y - client.getAreaY(), false);
            client.setActionInd(24);
        }
    }

    /**
     * Returns the number of individual items in the client's inventory.
     *
     * @return the number of individual items in the client's inventory. Between
     * 0 and MAX_INV_SIZE.
     */
    public int getInventoryCount() {
        return client.getInventorySize();
    }

    /**
     * Returns the number of unoccupied slots in the client's inventory.
     *
     * @return MAX_INV_SIZE - getInventoryCount()
     */
    public int getEmptySlots() {
        return MAX_INV_SIZE - getInventoryCount();
    }

    /**
     * Returns the total number of items in the client's inventory with the
     * specified IDs. Stack sizes and individual items are counted.
     *
     * @param ids the IDs of the items to search for.
     * @return the total number of items in the client's inventory with the
     * specified ids. Stack sizes and individual items are counted.
     */
    public int getInventoryCount(int... ids) {
        int count = 0;
        for (final int id : ids) {
            count += countItem(id);
        }
        return count;
    }

    private int countItem(int id) {
        int count = 0;
        for (int i = 0; i < client.getInventorySize(); i++) {
            if (client.getInventoryId(i) == id) {
                if (!StaticAccess.get().isItemStackable(id)) {
                    count++;
                } else {
                    count += client.getInventoryStack(i);
                }
            }
        }
        return count;
    }

    /**
     * Returns true if the client's inventory contains at least one item with
     * the given ID.
     *
     * @param id the ID of the item to search for.
     * @return true if the client's inventory contains at least one item with
     * the given id.
     */
    public boolean hasInventoryItem(int id) {
        for (int i = 0; i < getInventoryCount(); i++) {
            if (client.getInventoryId(i) == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the position of the item with the given ID in the client's
     * inventory.
     *
     * @param ids the identifiers of the items to search for.
     * @return the position of the first item with the given id(s). May range
     * from 0 to MAX_INV_SIZE.
     */
    public int getInventoryIndex(int... ids) {
        for (int i = 0; i < getInventoryCount(); i++) {
            if (inArray(ids, client.getInventoryId(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Uses the client's sleeping bag. If the client's inventory does not
     * contain a sleeping bag the script will be stopped.
     */
    public void useSleepingBag() {
        final int i = getInventoryIndex(1263);
        if (i == -1) {
            System.out.println("No sleeping bag found.");
            stopScript();
        } else {
            useItem(i);
        }
    }

    public int getGroundItemCount() {
        return client.getGroundItemCount();
    }

    public int getGroundItemId(int index) {
        return client.getGroundItemId(index);
    }

    public int getItemX(int index) {
        return client.getGroundItemLocalX(index) + client.getAreaX();
    }

    public int getItemY(int index) {
        return client.getGroundItemLocalY(index) + client.getAreaY();
    }

    /**
     * Locates the nearest ground item with the given id(s).
     *
     * @param ids the item id(s) to search for.
     * @return always an integer array of size 3. If no item can be found, the
     * array will contain -1, -1, -1. If an item was found, the array
     * will contain the item's ID, X, Y.
     */
    public int[] getItemById(int... ids) {
        final int[] item = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getGroundItemCount(); i++) {
            final int id = client.getGroundItemId(i);
            if (inArray(ids, id)) {
                final int x = client.getGroundItemLocalX(i) + client.getAreaX();
                final int y = client.getGroundItemLocalY(i) + client.getAreaY();
                final int dist = distanceTo(x, y, getX(), getY());
                if (dist < max_dist) {
                    item[0] = id;
                    item[1] = x;
                    item[2] = y;
                    max_dist = dist;
                }
            }
        }
        return item;
    }

    /**
     * Returns true if there is a ground item with the specified id on the
     * specified tile.
     *
     * @param id the item to search for.
     * @param x  the x position of the tile to examine.
     * @param y  the y position of the tile to examine.
     * @return true if the item is on the tile.
     */
    public boolean isItemAt(int id, int x, int y) {
        x -= client.getAreaX();
        y -= client.getAreaY();
        for (int i = 0; i < client.getGroundItemCount(); i++) {
            if (client.getGroundItemId(i) == id && client.getGroundItemLocalX(i) == x && client.getGroundItemLocalY(i) == y) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the specified item is equipped.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @return true if the specified item is equipped.
     * @see Script#getInventoryIndex(int...)
     */
    public boolean isItemEquipped(int slot) {
        return client.isEquipped(slot);
    }

    /**
     * Attempts to perform the primary action on an item in the client's
     * inventory. This can be "eat", "bury", etc.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @see Script#getInventoryIndex(int...)
     */
    public void useItem(int slot) {
        client.createPacket(Constants.OP_INV_ACTION);
        client.put2(slot);
        client.finishPacket();
    }

    /**
     * Attempts to drop an item in the client's inventory.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     */
    public void dropItem(int slot) {
        client.createPacket(Constants.OP_INV_DROP);
        client.put2(slot);
        client.finishPacket();
    }

    /**
     * Attempts to equip an item in the client's inventory.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @see Script#getInventoryIndex(int...)
     */
    public void wearItem(int slot) {
        client.createPacket(Constants.OP_INV_EQUIP);
        client.put2(slot);
        client.finishPacket();
    }

    /**
     * Attemps to unequip an item in the client's inventory.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @see Script#getInventoryIndex(int...)
     */
    public void removeItem(int slot) {
        client.createPacket(Constants.OP_INV_UNEQUIP);
        client.put2(slot);
        client.finishPacket();
    }

    /**
     * Uses one item with another based on their positions in the player's
     * inventory. Useful for cutting gems, fletching, etc.
     *
     * @param slot_1 the position of the item in the client's inventory.
     * @param slot_2 the position of the item in the client's inventory.
     * @see Script#getInventoryIndex(int...)
     */

    public void useItemWithItem(int slot_1, int slot_2) {
        client.createPacket(Constants.OP_INV_USEWITH);
        client.put2(slot_1);
        client.put2(slot_2);
        client.finishPacket();
    }

    /**
     * Casts the specified spell on the specified slot in the player's
     * inventory. Useful for high alchemy, superheat, etc.
     *
     * @param spell the spell's ID (position in the list starting at 0).
     * @param slot  the position of the item in the client's inventory, starting
     *              at 0.
     * @see Script#getInventoryIndex(int...)
     */
    public void castOnItem(int spell, int slot) {
        if (!canCastSpell(spell)) {
            System.out.println("Can't cast that spell.");
            return;
        }
        client.createPacket(Constants.OP_INV_CAST);
        client.put2(slot);
        client.put2(spell);
        client.finishPacket();
    }

    /**
     * Takes an item from the ground by the item's id, x, y
     *
     * @param id the id of the ground item.
     * @param x  the x position of the ground item.
     * @param y  the y position of the ground item.
     */
    public void pickupItem(int id, int x, int y) {
        final int local_x = x - client.getAreaX();
        final int local_y = y - client.getAreaY();
        client.walkAround(local_x, local_y);
        client.createPacket(Constants.OP_GITEM_TAKE);
        client.put2(x);
        client.put2(y);
        client.put2(id);
        client.finishPacket();
    }

    /**
     * Uses an item based on its position in the players inventory with an item
     * on the ground based on its id, x position, y position.
     *
     * @param item_slot the position of the item in the players inventory, starting at
     *                  0.
     * @param ground_id the id of the ground item.
     * @param ground_x  the x position of the ground item.
     * @param ground_y  the y position of the ground item.
     * @see Script#getItemById(int...)
     * @see Script#getInventoryIndex(int...)
     */
    public void useItemOnGroundItem(int item_slot, int ground_id, int ground_x,
                                    int ground_y) {
        final int local_x = ground_x - client.getAreaX();
        final int local_y = ground_y - client.getAreaY();
        client.walkAround(local_x, local_y);
        client.createPacket(Constants.OP_GITEM_USEWITH);
        client.put2(ground_x);
        client.put2(ground_y);
        client.put2(ground_id);
        client.put2(item_slot);
        client.finishPacket();
    }

    /**
     * Casts a spell on a ground item.
     *
     * @param spell   the spell's position in the list starting at 0.
     * @param item_id the item's id.
     * @param item_x  the item's x position.
     * @param item_y  the item's y position.
     * @see Script#getItemById(int...)
     */
    public void castOnGroundItem(int spell, int item_id, int item_x, int item_y) {
        if (!canCastSpell(spell)) {
            System.out.println("Can't cast that spell.");
            return;
        }
        final int local_x = item_x - client.getAreaX();
        final int local_y = item_y - client.getAreaY();
        client.walkAround(local_x, local_y);
        client.createPacket(Constants.OP_GITEM_CAST);
        client.put2(item_x);
        client.put2(item_y);
        client.put2(item_id);
        client.put2(spell);
        client.finishPacket();
    }

    /**
     * Uses an item by id with an object by id - only works if the player has
     * the item, and the object is within distance.
     *
     * @param item_id   the ID of the item to use.
     * @param object_id the ID of the object to use the item on.
     */
    public void useItemOnObject(int item_id, int object_id) {
        final int[] object = getObjectById(object_id);
        if (object[0] != -1) {
            useItemOnObject(item_id, object[1], object[2]);
        }
    }

    private int getObjectIndex(int x, int y) {
        final int lx = x - client.getAreaX();
        final int ly = y - client.getAreaY();
        for (int i = 0; i < client.getObjectCount(); i++) {
            if (lx == client.getObjectLocalX(i) && ly == client.getObjectLocalY(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Uses an item by id with the object at the specified coordinates. This only
     * works if the player has the item, and the object is within distance.
     *
     * @param item_id  the id of the item to use.
     * @param object_x x tile of the object.
     * @param object_y y tile of the object.
     */
    public void useItemOnObject(int item_id, int object_x, int object_y) {
        useSlotOnObject(getInventoryIndex(item_id), object_x, object_y);
    }

    public void useSlotOnObject(int slot, int object_x, int object_y) {
        if (slot == -1)
            return;
        final int index = getObjectIndex(object_x, object_y);
        if (index == -1) {
            System.out.println("Error identifying object at: " + object_x + "," + object_y);
        } else {
            client.walkToObject(object_x - client.getAreaX(), object_y - client.getAreaY(), client.getObjectDir(index), client.getObjectId(index));
            client.createPacket(Constants.OP_OBJECT_USEWITH);
            client.put2(object_x);
            client.put2(object_y);
            client.put2(slot);
            client.finishPacket();
        }
    }

    /**
     * Locates the nearest NPC with the given id(s). This will search all NPCs
     * in the area regardless of whether they are in combat or talking.
     *
     * @param ids the NPC id(s) to search for.
     * @return always an integer array of size 3. If no NPC can be found, the
     * array will contain -1, -1, -1. If an NPC was found, the array
     * will contain the NPC's local index, X, Y.
     * @see Script#getNpcById(int...)
     * @see Script#getNpcByIdNotTalk(int...)
     * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
     * @see Script#getNpcInRadius(int, int, int, int)
     */
    public int[] getAllNpcById(int... ids) {
        final int[] npc = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getNpcCount(); i++) {
            if (inArray(ids, client.getNpcId(client.getNpc(i)))) {
                final int x = client.getMobLocalX(client.getNpc(i)) + client.getAreaX();
                final int y = client.getMobLocalY(client.getNpc(i)) + client.getAreaY();
                final int dist = distanceTo(x, y, getX(), getY());
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

    /**
     * Locates the nearest NPC with the given id(s). This will skip NPCs which
     * are in combat.
     *
     * @param ids the NPC id(s) to search for.
     * @return always an integer array of size 3. If no NPC can be found, the
     * array will contain -1, -1, -1. If an NPC was found, the array
     * will contain the NPC's local index, X, Y.
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcByIdNotTalk(int...)
     * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
     * @see Script#getNpcInRadius(int, int, int, int)
     */
    public int[] getNpcById(int... ids) {
        final int[] npc = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getNpcCount(); i++) {
            if (inArray(ids, client.getNpcId(client.getNpc(i))) && !client.isMobInCombat(client.getNpc(i))) {
                final int x = client.getMobLocalX(client.getNpc(i)) + client.getAreaX();
                final int y = client.getMobLocalY(client.getNpc(i)) + client.getAreaY();
                final int dist = distanceTo(x, y, getX(), getY());
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

    /**
     * Locates the nearest NPC with the given id(s). This will skip NPCs which
     * are talking.
     *
     * @param ids the NPC id(s) to search for.
     * @return always an integer array of size 3. If no NPC can be found, the
     * array will contain -1, -1, -1. If an NPC was found, the array
     * will contain the NPC's local index, X, Y.
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcById(int...)
     * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
     * @see Script#getNpcInRadius(int, int, int, int)
     */
    public int[] getNpcByIdNotTalk(int... ids) {
        final int[] npc = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getNpcCount(); i++) {
            if (inArray(ids, client.getNpcId(client.getNpc(i))) && !client.isMobTalking(client.getNpc(i))) {
                final int x = client.getMobLocalX(client.getNpc(i)) + client.getAreaX();
                final int y = client.getMobLocalY(client.getNpc(i)) + client.getAreaY();
                final int dist = distanceTo(x, y, getX(), getY());
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

    /**
     * Locates the nearest NPC with the given id within a radius. This is
     * equivalent to a call to getNpcInExtendedRadius(id, start_x, start_y,
     * radius, radius).
     *
     * @param id      the ID of the NPC to search for.
     * @param start_x the center X of the circle area.
     * @param start_y the center Y of the circle area.
     * @param radius  the radius of the circle area.
     * @return always an integer array of size 3. If no NPC can be found, the
     * array will contain -1, -1, -1. If an NPC was found, the array
     * will contain the NPC's local index, X, Y.
     * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcById(int...)
     * @see Script#getNpcByIdNotTalk(int...)
     */
    public final int[] getNpcInRadius(int id, int start_x, int start_y,
                                      int radius) {
        return getNpcInExtendedRadius(id, start_x, start_y, radius, radius);
    }

    /**
     * Locates the nearest NPC with the given id(s) within a radius. This will
     * skip NPCs which are in combat.
     *
     * @param id        the ID of the NPC to search for.
     * @param start_x   the center X of the circle area.
     * @param start_y   the center Y of the circle area.
     * @param latitude  the distance east & west of center X's area.
     * @param longitude the distance north & south of center Y's area.
     * @return always an integer array of size 3. If no NPC can be found, the
     * array will contain -1, -1, -1. If an NPC was found, the array
     * will contain the NPC's local index, X, Y.
     * @see Script#getNpcInRadius(int, int, int, int)
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcById(int...)
     * @see Script#getNpcByIdNotTalk(int...)
     */
    public int[] getNpcInExtendedRadius(int id, int start_x, int start_y,
                                        int latitude, int longitude) {
        final int[] npc = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getNpcCount(); i++) {
            final int x = client.getMobLocalX(client.getNpc(i)) + client.getAreaX();
            final int y = client.getMobLocalY(client.getNpc(i)) + client.getAreaY();
            if (id == client.getNpcId(client.getNpc(i)) && !client.isMobInCombat(client.getNpc(i)) && Math.abs(x - start_x) <= latitude && Math.abs(y - start_y) <= longitude) {
                final int dist = distanceTo(x, y, getX(), getY());
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

    /**
     * Attempts to start melee combat with the NPC with the given local index.
     *
     * @param local_index the NPC's local index.
     * @see Script#getNpcById(int...)
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcInRadius(int, int, int, int)
     * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
     */
    public void attackNpc(int local_index) {
        client.walkDirectly(client.getMobLocalX(client.getNpc(local_index)), client.getMobLocalY(client.getNpc(local_index)), true);
        client.createPacket(Constants.OP_NPC_ATTACK);
        client.put2(client.getMobServerIndex(client.getNpc(local_index)));
        client.finishPacket();
    }

    /**
     * Attempts to start talking to the NPC with the given local index.
     *
     * @param local_index the NPC's local index.
     * @see Script#getNpcByIdNotTalk(int...)
     */
    public void talkToNpc(int local_index) {
        client.walkDirectly(client.getMobLocalX(client.getNpc(local_index)), client.getMobLocalY(client.getNpc(local_index)), true);
        client.createPacket(Constants.OP_NPC_TALK);
        client.put2(client.getMobServerIndex(client.getNpc(local_index)));
        client.finishPacket();
    }

    /**
     * Attempts to pickpocket the NPC with the given local index.
     *
     * @param local_index the NPC's local index.
     * @see Script#getNpcById(int...)
     * @see Script#getNpcInRadius(int, int, int, int)
     * @see Script#getNpcInExtendedRadius(int, int, int, int, int)
     */
    public void thieveNpc(int local_index) {
        client.walkDirectly(client.getMobLocalX(client.getNpc(local_index)), client.getMobLocalY(client.getNpc(local_index)), true);
        client.createPacket(Constants.OP_NPC_ACTION);
        client.put2(client.getMobServerIndex(client.getNpc(local_index)));
        client.finishPacket();
    }

    /**
     * Attempts to cast a spell on the NPC with the given local index.
     *
     * @param local_index the NPC's local index.
     * @param spell       the spell's ID (position in the list starting at 0).
     * @see Script#getAllNpcById(int...)
     */
    public void mageNpc(int local_index, int spell) {
        if (!canCastSpell(spell)) {
            System.out.println("Can't cast that spell.");
            return;
        }
        client.walkDirectly(client.getMobLocalX(client.getNpc(local_index)), client.getMobLocalY(client.getNpc(local_index)), true);
        client.createPacket(Constants.OP_NPC_CAST);
        client.put2(client.getMobServerIndex(client.getNpc(local_index)));
        client.put2(spell);
        client.finishPacket();
    }

    /**
     * Attempts to use an item in the client's inventory with a NPC.
     *
     * @param local_index the NPC's local index.
     * @param slot        the item's ID.
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcByIdNotTalk(int...)
     */
    public void useOnNpc(int local_index, int slot) {
        client.walkDirectly(client.getMobLocalX(client.getNpc(local_index)), client.getMobLocalY(client.getNpc(local_index)), true);
        client.createPacket(Constants.OP_NPC_USEWITH);
        client.put2(client.getMobServerIndex(client.getNpc(local_index)));
        client.put2(slot);
        client.finishPacket();
    }

    /**
     * Returns true if a quest menu is visible.
     *
     * @return true if a quest menu is visible.
     */
    public boolean isQuestMenu() {
        return client.isDialogVisible();
    }

    /**
     * Returns an array of the visible quest menu options.
     *
     * @return an array of the visible quest menu options.
     */
    public String[] questMenuOptions() {
        return client.getDialogOptions();
    }

    /**
     * Returns the number of visible quest menu options.
     *
     * @return the number of visible quest menu options.
     */
    public int questMenuCount() {
        return client.getDialogOptionCount();
    }

    public String getQuestMenuOption(int i) {
        return questMenuOptions()[i];
    }

    /**
     * Returns the position of the specified quest menu option, or -1 if the
     * option could not be found. <b>NOT CASE SENSITIVE</b>.
     *
     * @param str the string to compare to the visible options.
     * @return the specified string's position in the array, or -1.
     */
    public int getMenuIndex(String str) {
        if (isQuestMenu()) {
            for (int i = 0; i < questMenuCount(); i++) {
                if (questMenuOptions()[i].equalsIgnoreCase(str)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Selects the quest menu option with the given position.
     *
     * @param i the option's position in the list, starting at 0.
     * @see Script#getMenuIndex(String)
     * @see Script#questMenuOptions()
     */
    public void answer(int i) {
        if (!isQuestMenu()) {
            System.out.println("Trying to answer but not in menu.");
            return;
        }
        client.createPacket(Constants.OP_DIALOG_ANSWER);
        client.put1(i);
        client.finishPacket();
        client.setDialogVisible(false);
    }

    /**
     * Locates the player with the given display name. <b>CASE SENSITIVE</b>.
     *
     * @param name the display name of the player to search for.
     * @return always an integer array of size 3. If no player can be found, the
     * array will contain -1, -1, -1. If a player was found, the array
     * will contain the player's local index, X, Y.
     */
    public int[] getPlayerByName(String name) {
        final int[] player = new int[]{
                -1, -1, -1
        };
        for (int i = 0; i < countPlayers(); i++) {
            final String temp_name = client.getPlayerName(client.getPlayer(i));
            if (temp_name.equals(name)) {
                player[0] = i;
                player[1] = client.getMobLocalX(client.getPlayer(i)) + client.getAreaX();
                player[2] = client.getMobLocalY(client.getPlayer(i)) + client.getAreaY();
                break;
            }
        }
        return player;
    }

    /**
     * @param server_index the server index of the player to search for.
     * @return always an integer array of size 3. If no player can be found, the
     * array will contain -1, -1, -1. If a player was found, the array
     * will contain the player's local index, X, Y.
     * @deprecated Unreliable in comparison to
     * {@link Script#getPlayerByName(String)}.
     */
    @Deprecated
    public int[] getPlayerByPid(int server_index) {
        final int[] player = new int[]{
                -1, -1, -1
        };
        for (int i = 0; i < countPlayers(); i++) {
            if (getPlayerPID(i) == server_index) {
                player[0] = i;
                player[1] = client.getMobLocalX(client.getPlayer(i)) + client.getAreaX();
                player[2] = client.getMobLocalY(client.getPlayer(i)) + client.getAreaY();
                break;
            }
        }
        return player;
    }

    /**
     * Returns the display name of the given player.
     *
     * @param local_index the player's local index.
     * @return the player's display name.
     * @see Script#countPlayers()
     */
    public String getPlayerName(int local_index) {
        return client.getPlayerName(client.getPlayer(local_index));
    }

    /**
     * Returns the server index of the given player.
     *
     * @param local_index the player's local index.
     * @return the player's server index.
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public int getPlayerPID(int local_index) {
        return client.getMobServerIndex(client.getPlayer(local_index));
    }

    public int getNpcServerIndex(int local_index) {
        return client.getMobServerIndex(client.getNpc(local_index));
    }

    public int getPlayerX(int index) {
        return client.getMobLocalX(client.getPlayer(index)) + client.getAreaX();
    }

    public int getPlayerY(int index) {
        return client.getMobLocalY(client.getPlayer(index)) + client.getAreaY();
    }

    /**
     * Returns the direction the given player is facing.
     *
     * @param local_index the player's local index.
     * @return the direction the player is facing: DIR_NORTH, DIR_NORTHWEST,
     * DIR_WEST, DIR_SOUTHWEST, DIR_SOUTH, DIR_SOUTHEAST, DIR_EAST,
     * DIR_NORTHEAST
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public int getPlayerDirection(int local_index) {
        int dir = client.getMobDirection(client.getPlayer(local_index));
        if (dir == 8) {
            dir = DIR_EAST;
        } else if (dir == 9) {
            dir = DIR_WEST;
        }
        return dir;
    }

    /**
     * Returns the combat level of the given player.
     *
     * @param local_index the player's local index.
     * @return the player's combat level.
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public int getPlayerCombatLevel(int local_index) {
        return client.getPlayerCombatLevel(client.getPlayer(local_index));
    }

    /**
     * Returns true if the given player is in combat.
     *
     * @param local_index the player's local index.
     * @return true if the player is in combat.
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public boolean isPlayerInCombat(int local_index) {
        return client.isMobInCombat(client.getPlayer(local_index));
    }

    public boolean isPlayerHpBarVisible(int index) {
        return client.isHpBarVisible(client.getPlayer(index));
    }

    /**
     * Returns true if the given player is talking.
     *
     * @param local_index the player's local index.
     * @return true if the player is in talking.
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public boolean isPlayerTalking(int local_index) {
        return client.isMobTalking(client.getPlayer(local_index));
    }

    /**
     * Returns true if the given player is walking.
     *
     * @param local_index the player's local index.
     * @return true if the player is in walking.
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public boolean isPlayerWalking(int local_index) {
        return client.isMobWalking(client.getPlayer(local_index));
    }

    /**
     * Returns the number of players visible to the client.
     *
     * @return the number of players visible to the client.
     */
    public int countPlayers() {
        return client.getPlayerCount();
    }

    /**
     * Attempts to start melee combat with the given player.
     *
     * @param local_index the player's local index.
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public void attackPlayer(int local_index) {
        client.walkDirectly(client.getMobLocalX(client.getPlayer(local_index)), client.getMobLocalY(client.getPlayer(local_index)), true);
        client.createPacket(Constants.OP_PLAYER_ATTACK);
        client.put2(client.getMobServerIndex(client.getPlayer(local_index)));
        client.finishPacket();
    }

    /**
     * Attempts to cast a spell on the given player.
     *
     * @param local_index the player's local index.
     * @param spell       the spell's identifier (position in the list starting at 0).
     * @see Script#countPlayers()
     * @see Script#getPlayerByName(String)
     */
    public void magePlayer(int local_index, int spell) {
        if (!canCastSpell(spell)) {
            System.out.println("Can't cast the spell: " + spell);
        } else {
            client.walkDirectly(client.getMobLocalX(client.getPlayer(local_index)), client.getMobLocalY(client.getPlayer(local_index)), true);
            client.createPacket(Constants.OP_PLAYER_CAST);
            client.put2(client.getMobServerIndex(client.getPlayer(local_index)));
            client.put2(spell);
            client.finishPacket();
        }
    }

    /**
     * Locates the nearest object with the given id(s).
     *
     * @param ids the object id(s) to search for.
     * @return always an integer array of size 3. If no object can be found, the
     * array will contain -1, -1, -1. If an object was found, the array
     * will contain the object's id, X, Y.
     */
    public int[] getObjectById(int... ids) {
        final int[] object = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getObjectCount(); i++) {
            final int id = client.getObjectId(i);
            if (inArray(ids, id)) {
                final int x = client.getObjectLocalX(i) + client.getAreaX();
                final int y = client.getObjectLocalY(i) + client.getAreaY();
                final int dist = distanceTo(x, y, getX(), getY());
                if (dist < max_dist) {
                    object[0] = id;
                    object[1] = x;
                    object[2] = y;
                    max_dist = dist;
                }
            }
        }
        return object;
    }

    public int getObjectCount() {
        return client.getObjectCount();
    }

    public int getObjectId(int index) {
        return client.getObjectId(index);
    }

    public int getObjectX(int index) {
        return client.getObjectLocalX(index) + client.getAreaX();
    }

    public int getObjectY(int index) {
        return client.getObjectLocalY(index) + client.getAreaY();
    }

    /**
     * Returns the id of the object at the given coordinates, or -1 if no object
     * could be found.
     *
     * @param x the x position of the tile to examine.
     * @param y the y position of the tile to examine.
     * @return the id of the object at the given coordinates, or -1 if no object
     * could be found.
     */
    public int getObjectIdFromCoords(int x, int y) {
        final int lx = x - client.getAreaX();
        final int ly = y - client.getAreaY();
        for (int i = 0; i < client.getObjectCount(); i++) {
            if (client.getObjectLocalX(i) == lx && client.getObjectLocalY(i) == ly) {
                return client.getObjectId(i);
            }
        }
        return -1;
    }

    /**
     * Returns true if the tile at the given coordinates contains an object.
     *
     * @param x the x position of the tile to examine.
     * @param y the y position of the tile to examine.
     * @return true if the tile at the given coordinates contains an object.
     */
    public boolean isObjectAt(int x, int y) {
        final int lx = x - client.getAreaX();
        final int ly = y - client.getAreaY();
        for (int i = 0; i < client.getObjectCount(); i++) {
            if (client.getObjectLocalX(i) == lx && client.getObjectLocalY(i) == ly) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to perform the primary action on the object at the given
     * coordinates.
     *
     * @param x the x position of the object to interact with.
     * @param y the y position of the object to interact with.
     * @see Script#getObjectById(int...)
     * @see Script#atObject2(int, int)
     */
    public void atObject(int x, int y) {
        final int index = getObjectIndex(x, y);
        if (index == -1) {
            System.out.println("Error identifying object at: " + x + "," + y);
        } else {
            final int local_x = x - client.getAreaX();
            final int local_y = y - client.getAreaY();
            client.walkToObject(local_x, local_y, client.getObjectDir(index), client.getObjectId(index));
            client.createPacket(Constants.OP_OBJECT_ACTION1);
            client.put2(x);
            client.put2(y);
            client.finishPacket();
        }
    }

    /**
     * Attempts to perform the secondary action on the object at the given
     * coordinates.
     *
     * @param x the x position of the object to interact with.
     * @param y the y position of the object to interact with.
     * @see Script#getObjectById(int...)
     * @see Script#atObject(int, int)
     */
    public void atObject2(int x, int y) {
        final int index = getObjectIndex(x, y);
        if (index == -1) {
            System.out.println("Error identifying object at: " + x + "," + y);
        } else {
            final int local_x = x - client.getAreaX();
            final int local_y = y - client.getAreaY();
            client.walkToObject(local_x, local_y, client.getObjectDir(index), client.getObjectId(index));
            client.createPacket(Constants.OP_OBJECT_ACTION2);
            client.put2(x);
            client.put2(y);
            client.finishPacket();
        }
    }

    /**
     * Locates the nearest boundary with the given id(s).
     *
     * @param ids the boundary id(s) to search for.
     * @return always an integer array of size 3. If no boundary can be found,
     * the array will contain -1, -1, -1. If a boundary was found, the
     * array will contain the boundary's id, X, Y.
     */
    public int[] getWallObjectById(int... ids) {
        final int[] bound = new int[]{
                -1, -1, -1
        };
        int max_dist = Integer.MAX_VALUE;
        for (int i = 0; i < client.getBoundCount(); i++) {
            final int id = client.getBoundId(i);
            if (inArray(ids, client.getBoundId(i))) {
                final int x = client.getBoundLocalX(i) + client.getAreaX();
                final int y = client.getBoundLocalY(i) + client.getAreaY();
                final int dist = distanceTo(x, y, getX(), getY());
                if (dist < max_dist) {
                    bound[0] = id;
                    bound[1] = x;
                    bound[2] = y;
                    max_dist = dist;
                }
            }
        }
        return bound;
    }

    /**
     * Returns the id of the boundary at the given coordinates, or -1.
     *
     * @param x the x position of the tile to examine.
     * @param y the y position of the tile to examine.
     * @return the id of the boundary at the given coordinates, or -1.
     */
    public int getWallObjectIdFromCoords(int x, int y) {
        final int lx = x - client.getAreaX();
        final int ly = y - client.getAreaY();
        for (int i = 0; i < client.getBoundCount(); i++) {
            if (client.getBoundLocalX(i) == lx && client.getBoundLocalY(i) == ly) {
                return client.getBoundId(i);
            }
        }
        return -1;
    }

    public int getWallObjectCount() {
        return client.getBoundCount();
    }

    public int getWallObjectId(int index) {
        return client.getBoundId(index);
    }

    public int getWallObjectX(int index) {
        return client.getBoundLocalX(index) + client.getAreaX();
    }

    public int getWallObjectY(int index) {
        return client.getBoundLocalY(index) + client.getAreaY();
    }

    private int getBoundIndex(int x, int y) {
        final int lx = x - client.getAreaX();
        final int ly = y - client.getAreaY();
        for (int i = 0; i < client.getBoundCount(); i++) {
            if (client.getBoundLocalX(i) == lx && client.getBoundLocalY(i) == ly) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Attempts to perform the primary action on the boundary at the given
     * coordinates.
     *
     * @param x the x position of the boundary to interact with.
     * @param y the y position of the boundary to interact with.
     * @see Script#getWallObjectById(int...)
     * @see Script#atWallObject2(int, int)
     */
    public void atWallObject(int x, int y) {
        final int index = getBoundIndex(x, y);
        if (index == -1) {
            System.out.println("Error identifying wallobj at: " + x + "," + y);
        } else {
            final int dir = client.getBoundDir(index);
            final int local_x = x - client.getAreaX();
            final int local_y = y - client.getAreaY();
            client.walkToBound(local_x, local_y, dir);
            client.createPacket(Constants.OP_BOUND_ACTION1);
            client.put2(x);
            client.put2(y);
            client.put1(dir);
            client.finishPacket();
        }
    }

    /**
     * Attempts to perform the secondary action on the boundary at the given
     * coordinates.
     *
     * @param x the x position of the boundary to interact with.
     * @param y the y position of the boundary to interact with.
     * @see Script#getWallObjectById(int...)
     * @see Script#atWallObject(int, int)
     */
    public void atWallObject2(int x, int y) {
        final int index = getBoundIndex(x, y);
        if (index == -1) {
            System.out.println("Error identifying wallobj at: " + x + "," + y);
        } else {
            final int dir = client.getBoundDir(index);
            final int local_x = x - client.getAreaX();
            final int local_y = y - client.getAreaY();
            client.walkToBound(local_x, local_y, dir);
            client.createPacket(Constants.OP_BOUND_ACTION2);
            client.put2(x);
            client.put2(y);
            client.put1(dir);
            client.finishPacket();
        }
    }

    /**
     * Attempts to use an item in the client's inventory with the boundary at
     * the given coordinates.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @param x    the x position of the boundary to interact with.
     * @param y    the y position of the boundary to interact with.
     */
    public void useItemOnWallObject(int slot, int x, int y) {
        final int index = getBoundIndex(x, y);
        if (index == -1) {
            System.out.println("Error identifying wallobj at: " + x + "," + y);
        } else {
            final int local_x = x - client.getAreaX();
            final int local_y = y - client.getAreaY();
            final int dir = client.getBoundDir(index);
            client.walkToBound(local_x, local_y, dir);
            client.createPacket(Constants.OP_BOUND_USEWITH);
            client.put2(x);
            client.put2(y);
            client.put1(dir);
            client.put2(slot);
            client.finishPacket();
        }
    }

    /**
     * Attempts to deposit the given amount of the given item in the client's
     * bank.
     *
     * @param id     the id of the item to deposit.
     * @param amount the number of the given item to deposit.
     */
    public void deposit(int id, int amount) {
        if (!isBanking()) {
            System.out.println("Trying to deposit but not in bank screen.");
        } else {
            client.createPacket(Constants.OP_BANK_DEPOSIT);
            client.put2(id);
            client.put4(amount);
            client.put4(-2023406815);
            client.finishPacket();
        }
    }

    /**
     * Attempts to withdraw the given amount of the given item from the client's
     * bank.
     *
     * @param id     the id of the item to withdraw.
     * @param amount the number of the given item to withdraw.
     */
    public void withdraw(int id, int amount) {
        if (!isBanking()) {
            System.out.println("Trying to withdraw but not in bank screen.");
        } else {
            client.createPacket(Constants.OP_BANK_WITHDRAW);
            client.put2(id);
            client.put4(amount);
            client.put4(305419896);
            client.finishPacket();
        }
    }

    /**
     * Returns the number of items with the given ids(s) in the client's bank if
     * the bank is visible.
     *
     * @param ids the item id(s) to search for.
     * @return the number of items with the given id(s) in the client's bank.
     */
    public int bankCount(int... ids) {
        int count = 0;
        if (isBanking()) {
            for (int i = 0; i < client.getBankSize(); i++) {
                if (inArray(ids, client.getBankId(i))) {
                    count += client.getBankStack(i);
                }
            }
        }
        return count;
    }

    public int getBankSize() {
        return client.getBankSize();
    }

    public int getBankId(int i) {
        return client.getBankId(i);
    }

    public int getBankStack(int i) {
        return client.getBankStack(i);
    }

    /**
     * Returns true if the client's bank contains at least one item with the
     * given id if the bank is visible.
     *
     * @param id the item to search for.
     * @return true if the client's bank contains at least one item with the
     * given id.
     */
    public boolean hasBankItem(int id) {
        return bankCount(id) > 0;
    }

    /**
     * Returns true if the bank screen is visible.
     *
     * @return true if the bank screen is visible.
     */
    public boolean isBanking() {
        return client.isBankVisible();
    }

    /**
     * Closes the client's bank.
     */
    public void closeBank() {
        if (!isBanking()) {
            System.out.println("Trying to close bank but not in bank screen.");
        } else {
            client.createPacket(Constants.OP_BANK_CLOSE);
            client.finishPacket();
            client.setBankVisible(false);
        }
    }

    /**
     * Returns the distance between the client and the tile at x, y.
     *
     * @param x the x position of the tile to compare to the client's
     *          position.
     * @param y the y position of the tile to compare to the client's
     *          position.
     * @return the distance between the client and the given tile.
     */
    public int distanceTo(int x, int y) {
        return distanceTo(x, y, getX(), getY());
    }

    /**
     * Returns true if the tile the client is standing on is within radius
     * distance of x, y.
     *
     * @param x      the x position of the comparison tile.
     * @param y      the y position of the comparison tile.
     * @param radius the maximum distance between the client and x, y for the
     *               method to return true.
     * @return true if the tile the client is standing on is within radius
     * distance of x, y.
     */
    public boolean isAtApproxCoords(int x, int y, int radius) {
        return distanceTo(x, y) <= radius;
    }

    /**
     * Returns true if it is possible for the client to walk to x, y.
     *
     * @param x the x position of the comparison tile.
     * @param y the y position of the comparison tile.
     * @return true if it is possible for the client to walk to x, y.
     */

    public boolean isReachable(int x, int y) {
        final int dx = x - client.getAreaX();
        final int dy = y - client.getAreaY();
        if (locRouteCalc == null) {
            locRouteCalc = new LocalRouteCalc();
        }
        return locRouteCalc.calculate(client.getAdjacency(), client.getLocalX(), client.getLocalY(), dx, dy, dx, dy, false) != -1;
    }

    /**
     * Attempts to enable a prayer.
     *
     * @param prayer the position of the prayer in the list, starting at 0.
     */
    public void enablePrayer(int prayer) {
        client.setPrayerEnabled(prayer, true);
        client.createPacket(Constants.OP_PRAYER_ENABLE);
        client.put1(prayer);
        client.finishPacket();
    }

    /**
     * Attempts to disable a prayer.
     *
     * @param prayer the position of the prayer in the list, starting at 0.
     */
    public void disablePrayer(int prayer) {
        client.setPrayerEnabled(prayer, false);
        client.createPacket(Constants.OP_PRAYER_DISABLE);
        client.put1(prayer);
        client.finishPacket();
    }

    /**
     * Returns true if the specified prayer is enabled.
     *
     * @param prayer the position of the prayer in the list, starting at 0.
     * @return true if the specified prayer is enabled.
     */
    public boolean isPrayerEnabled(int prayer) {
        return client.isPrayerEnabled(prayer);
    }

    /**
     * Returns true if the shop screen is visible.
     *
     * @return true if the shop screen is visible.
     */
    public boolean isShopOpen() {
        return client.isShopVisible();
    }

    /**
     * Returns the position of the item with the given id in the shop screen.
     *
     * @param id the item id to search for.
     * @return the position of the item with the given id in the shop screen.
     */
    public int getShopItemById(int id) {
        final int count = client.getShopSize();
        for (int i = 0; i < count; i++) {
            if (client.getShopId(i) == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the ID of the item at the given shop position, starting at 0.
     *
     * @param i the position.
     * @return the id of the item at i position.
     */
    public int getShopItemId(int i) {
        if (i >= client.getShopSize() || i < 0) {
            return -1;
        }
        return client.getShopId(i);
    }

    /**
     * Returns the size of the stack the shop has for the item at the given shop
     * position, starting at 0.
     *
     * @param i the position of the item in the shop screen.
     * @return the size of the stack the shop has for the item at the given shop
     * position.
     * @see Script#getShopItemById(int)
     */
    public int getShopItemAmount(int i) {
        if (i >= client.getShopSize() || i < 0) {
            return -1;
        }
        return client.getShopStack(i);
    }

    /**
     * Attempts to buy the given number of the item at the given position from
     * the visible shop.
     *
     * @param i      the position of the item in the shop screen.
     * @param amount the number to buy.
     */
    public void buyShopItem(int i, int amount) {
        if (i >= client.getShopSize() || i < 0) {
            return;
        }
        if (!isShopOpen()) {
            System.out.println("Trying to buy but not in shop screen.");
        } else {
            client.createPacket(Constants.OP_SHOP_BUY);
            client.put2(client.getShopId(i));
            client.put2(client.getShopStack(i));
            client.put2(amount);
            client.finishPacket();
        }
    }

    /**
     * Attempts to sell the given number of the item at the given position from
     * the visible shop.
     *
     * @param i      the position of the item in the shop screen.
     * @param amount the number to sell.
     */
    public void sellShopItem(int i, int amount) {
        if (i >= client.getShopSize() || i < 0) {
            return;
        }
        if (!isShopOpen()) {
            System.out.println("Trying to sell but not in shop screen.");
        } else {
            client.createPacket(Constants.OP_SHOP_SELL);
            client.put2(client.getShopId(i));
            client.put2(client.getShopStack(i));
            client.put2(amount);
            client.finishPacket();
        }
    }

    /**
     * Attempts to close the shop screen.
     */
    public void closeShop() {
        if (!isShopOpen()) {
            System.out.println("Trying to close shop but not in shop screen.");
        } else {
            client.createPacket(Constants.OP_SHOP_CLOSE);
            client.finishPacket();
            client.setShopVisible(false);
        }
    }

    /**
     * Attempts to send a trade request to the player with the given server
     * index.
     *
     * @param server_index the server index of the player to send the request to.
     * @see Script#getPlayerPID(int)
     */
    public void sendTradeRequest(int server_index) {
        if (server_index == -1)
            return;
        client.createPacket(Constants.OP_PLAYER_TRADE);
        client.put2(server_index);
        client.finishPacket();
    }

    /**
     * Attempts to start following the player with the given server index.
     *
     * @param server_index the server index of the player to start following.
     * @see Script#getPlayerPID(int)
     */
    public void followPlayer(int server_index) {
        if (server_index == -1)
            return;
        client.createPacket(Constants.OP_PLAYER_FOLLOW);
        client.put2(server_index);
        client.finishPacket();
    }

    /**
     * Attempts to use an item in the client's inventory with a player.
     *
     * @param local_index the NPC's local index.
     * @param slot        the item's ID.
     * @see Script#getAllNpcById(int...)
     * @see Script#getNpcByIdNotTalk(int...)
     */
    public void useItemWithPlayer(int local_index, int slot) {
        if (local_index < 0 || slot < 0) {
            return;
        }
        client.walkDirectly(client.getMobLocalX(client.getPlayer(local_index)), client.getMobLocalY(client.getPlayer(local_index)), true);
        client.createPacket(Constants.OP_PLAYER_USEWITH);
        client.put2(client.getMobServerIndex(client.getPlayer(local_index)));
        client.put2(slot);
        client.finishPacket();
    }

    /**
     * Returns true if the trade offer screen is visible.
     *
     * @return true if the trade offer screen is visible.
     * @see Script#isInTradeConfirm()
     */
    public boolean isInTradeOffer() {
        return client.isInTradeOffer();
    }

    /**
     * Returns true if the trade confirm screen is visible.
     *
     * @return true if the trade confirm screen is visible.
     * @see Script#isInTradeOffer()
     */
    public boolean isInTradeConfirm() {
        return client.isInTradeConfirm();
    }

    /**
     * Attempts to add items to the current trade offer.
     *
     * @param slot   the position of the item to offer in the client's inventory,
     *               starting at 0.
     * @param amount the number of the given item to offer.
     */
    public void offerItemTrade(int slot, int amount) {
        client.offerItemTrade(slot, amount);
    }

    /**
     * Returns true if the other player's trade offer contains at least the
     * given number of the given item.
     *
     * @param id     the id of the item to search for.
     * @param amount the number of the item to ensure is there.
     * @return true if the other player has traded at least the given number of
     * the given item.
     */
    public boolean hasOtherTraded(int id, int amount) {
        final int count = client.getRemoteTradeItemCount();
        final boolean stacks = StaticAccess.get().isItemStackable(id);
        for (int i = 0; i < count; i++) {
            if (client.getRemoteTradeItemId(i) != id) {
                continue;
            }
            if (stacks) {
                return client.getRemoteTradeItemStack(i) >= amount;
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the number of items in the client's trade offer.
     *
     * @return the number of items in the client's trade offer.
     * @deprecated use Script#getLocalTradeItemCount()
     */
    @Deprecated
    public int getOurTradedItemCount() {
        return client.getLocalTradeItemCount();
    }

    public int getLocalTradeItemCount() {
        return getOurTradedItemCount();
    }

    public int getLocalTradeItemId(int i) {
        if (i >= client.getLocalTradeItemCount()) {
            return -1;
        }
        return client.getLocalTradeItemId(i);
    }

    public int getLocalTradeItemStack(int i) {
        if (i >= client.getLocalTradeItemCount()) {
            return -1;
        }
        return client.getLocalTradeItemStack(i);
    }

    /**
     * Returns the number of items in the other player's trade offer.
     *
     * @return the number of items in the other player's trade offer.
     * @deprecated use Script#getRemoteTradeItemCount()
     */
    @Deprecated
    public int getTheirTradedItemCount() {
        return client.getRemoteTradeItemCount();
    }

    public int getRemoteTradeItemCount() {
        return getTheirTradedItemCount();
    }

    public int getRemoteTradeItemId(int i) {
        if (i >= client.getRemoteTradeItemCount()) {
            return -1;
        }
        return client.getRemoteTradeItemId(i);
    }

    public int getRemoteTradeItemStack(int i) {
        if (i >= client.getRemoteTradeItemCount()) {
            return -1;
        }
        return client.getRemoteTradeItemStack(i);
    }

    public boolean hasLocalAcceptedTrade() {
        if (isInTradeConfirm()) {
            return client.hasLocalConfirmedTrade();
        }
        return client.hasLocalAcceptedTrade();
    }

    public boolean hasRemoteAcceptedTrade() {
        return client.hasRemoteAcceptedTrade();
    }

    /**
     * Attempts to accept the trade offer.
     *
     * @see Script#confirmTrade()
     * @see Script#declineTrade()
     */
    public void acceptTrade() {
        if (!client.isInTradeOffer()) {
            System.out.println("Trying to accept and not in correct stage of trade.");
            return;
        }
        if (!client.hasLocalAcceptedTrade()) {
            client.createPacket(Constants.OP_TRADE_ACCEPT);
            client.finishPacket();
        } else {
            System.out.println("Trying to accept and already accepted.");
        }
    }

    /**
     * Attempts to confirm the trade offer.
     *
     * @see Script#acceptTrade()
     * @see Script#declineTrade()
     */
    public void confirmTrade() {
        if (!client.isInTradeConfirm()) {
            System.out.println("Trying to confirm and not in correct stage of trade.");
            return;
        }
        if (!client.hasLocalConfirmedTrade()) {
            client.createPacket(Constants.OP_TRADE_CONFIRM);
            client.finishPacket();
        } else {
            System.out.println("Trying to confirm and already confirmed.");
        }
    }

    /**
     * Attempts to decline the trade.
     *
     * @see Script#acceptTrade()
     * @see Script#confirmTrade()
     */
    public void declineTrade() {
        if (!client.isInTradeConfirm() && !client.isInTradeOffer()) {
            System.out.println("Trying to decline and not in trade.");
            return;
        }
        client.setInTradeOffer(false);
        client.setInTradeConfirm(false);
        client.createPacket(Constants.OP_TRADE_DECLINE);
        client.finishPacket();
    }

    /**
     * Draws a string on the game's canvas. Only use in paint().
     *
     * @param str    the string to draw.
     * @param x      the x position to start at.
     * @param y      the y position to start at.
     * @param size   the size of the text.
     * @param colour the hex colour (24-bit RGB) of the text.
     */
    public void drawString(String str, int x, int y, int size, int colour) {
        client.drawString(str, x, y, size, colour);
    }

    /**
     * Draws a horizontal 1px line on the game. Only use in paint().
     *
     * @param x      the x position to start at.
     * @param y      the y position to start at.
     * @param length the length of the line in pixels.
     * @param colour the hex colour (24-bit RGB) of the line.
     */
    public void drawHLine(int x, int y, int length, int colour) {
        RasterOps.drawHLine(client.getPixels(),
                client.getGameWidth(), client.getGameHeight(),
                x, y, length, colour);
    }

    /**
     * Draws a vertical 1px line on the game. Only use in paint().
     *
     * @param x      the x position to start at.
     * @param y      the y position to start at.
     * @param length the length of the line in pixels.
     * @param colour the hex colour (24-bit RGB) of the line.
     */
    public void drawVLine(int x, int y, int length, int colour) {
        RasterOps.drawVLine(client.getPixels(),
                client.getGameWidth(), client.getGameHeight(),
                x, y, length, colour);
    }

    /**
     * Draws a 1px outline of a equiangular quadrilateral on the game. Only use
     * in paint().
     *
     * @param x      the x position to start at.
     * @param y      the y position to start at.
     * @param width  the width of the shape.
     * @param height the height of the shape.
     * @param colour the hex colour (24-bit RGB) of the shape.
     */
    public void drawBoxOutline(int x, int y, int width, int height, int colour) {
        drawHLine(x, y, width, colour);
        drawHLine(x, (y + height) - 1, width, colour);
        drawVLine(x, y, height, colour);
        drawVLine((x + width) - 1, y, height, colour);
    }

    /**
     * Draws a filled equiangular quadrilateral on the game. Only use in
     * paint().
     *
     * @param x      the x position to start at.
     * @param y      the y position to start at.
     * @param width  the width of the shape.
     * @param height the height of the shape.
     * @param colour the colour (24-bit RGB) of the shape.
     */
    public void drawBoxFill(int x, int y, int width, int height, int colour) {
        RasterOps.fillRect(client.getPixels(),
                client.getGameWidth(), client.getGameHeight(),
                x, y, width, height, colour);
    }

    /**
     * Draws a filled equiangular quadrilateral with transparency on the game.
     * Only use in paint().
     *
     * @param x      the x position to start at.
     * @param y      the y position to start at.
     * @param width  the width of the shape.
     * @param height the height of the shape.
     * @param trans  transparency of the shape (255-0).
     * @param colour the colour (24-bit RGB) of the shape.
     */
    public void drawBoxAlphaFill(int x, int y, int width, int height,
                                 int trans, int colour) {
        RasterOps.fillRectAlpha(client.getPixels(),
                client.getGameWidth(), client.getGameHeight(),
                x, y, width, height, trans, colour);
    }

    /**
     * Draws a filled circle with optional transparency (use 255 for solid) on
     * the game.
     *
     * @param x      the x position of the centre of the circle.
     * @param y      the y position of the centre of the circle.
     * @param radius the radius of the circle.
     * @param colour the colour (24-bit RGB) of the shape.
     * @param trans  transparency of the shape (255-0).
     */
    public void drawCircleFill(int x, int y,
                               int radius, int colour, int trans) {
        RasterOps.fillCircle(client.getPixels(),
                client.getGameWidth(), client.getGameHeight(),
                x, y, radius, colour, trans);
    }

    /**
     * Sets a pixel on the game.
     *
     * @param x      the x position of the pixel.
     * @param y      the y position of the pixel.
     * @param colour the colour (24-bit RGB) to set.
     */
    public void setPixel(int x, int y, int colour) {
        RasterOps.setPixel(client.getPixels(),
                client.getGameWidth(), client.getGameHeight(),
                x, y, colour);
    }

    /**
     * Draws an image on the game.
     *
     * @param image   the image to draw.
     * @param start_x the x position to start drawing.
     * @param start_y the y position to start drawing.
     */
    public void drawImage(Image image, int start_x, int start_y) {
        if (image instanceof BufferedImage) {
            drawBuf((BufferedImage) image,
                    start_x, start_y, client.getPixels(),
                    client.getGameWidth(), client.getGameHeight());
            return;
        }
        if (imageCache == null) {
            imageCache = new HashMap<>();
        }
        int width;
        int height;
        BufferedImage buf;
        if ((buf = imageCache.get(image)) != null) {
            width = buf.getWidth();
            height = buf.getHeight();
        } else {
            width = image.getWidth(null);
            height = image.getHeight(null);
            buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            final Graphics g = buf.createGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            imageCache.put(image, buf);
        }
        drawBuf(buf, start_x, start_y, client.getPixels(),
                client.getGameWidth(), client.getGameHeight());
    }

    /**
     * Prints a line of text in the in-game chat box and console.
     *
     * @param str the text to print.
     */
    public void writeLine(String str) {
        System.out.println(str);
        client.displayMessage(str);
    }

    /**
     * Returns the number of NPCs visible to the client.
     *
     * @return the number of NPCs visible to the client.
     */
    public int countNpcs() {
        return client.getNpcCount();
    }

    public int getNpcId(int index) {
        return client.getNpcId(client.getNpc(index));
    }

    public int getNpcX(int index) {
        return client.getMobLocalX(client.getNpc(index)) + client.getAreaX();
    }

    public int getNpcY(int index) {
        return client.getMobLocalY(client.getNpc(index)) + client.getAreaY();
    }

    public boolean isNpcInCombat(int index) {
        return client.isMobInCombat(client.getNpc(index));
    }

    public boolean isNpcTalking(int index) {
        return client.isMobTalking(client.getNpc(index));
    }

    public boolean isNpcHpBarVisible(int index) {
        return client.isHpBarVisible(client.getNpc(index));
    }

    /**
     * Returns the name of the given NPC.
     *
     * @param index the NPC's local index.
     * @return the NPC's name.
     */
    public String getNpcName(int index) {
        return StaticAccess.get().getNpcName(client.getNpcId(client.getNpc((index))));
    }

    /**
     * Returns the description (examine text) of the given NPC.
     *
     * @param index the NPC's local index.
     * @return the NPC's description (examine text).
     */
    public String getNpcDescription(int index) {
        return StaticAccess.get().getNpcDesc(client.getNpcId(client.getNpc((index))));
    }

    /**
     * Returns the combat level of the given NPC.
     *
     * @param index the NPC's local index.
     * @return the NPC's combat level.
     */
    public int getNpcCombatLevel(int index) {
        return StaticAccess.get().getNpcLevel(client.getNpcId(client.getNpc((index))));
    }

    public int getInventoryId(int slot) {
        return client.getInventoryId(slot);
    }

    public int getInventoryStack(int slot) {
        return client.getInventoryStack(slot);
    }

    /**
     * Returns the name of an item in the client's inventory.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @return the item's name.
     */
    public String getItemName(int slot) {
        return StaticAccess.get().getItemName(client.getInventoryId(slot));
    }

    /**
     * Returns the description (examine text) of an item in the client's
     * inventory.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @return the item's description (examine text).
     */
    public String getItemDescription(int slot) {
        return StaticAccess.get().getItemDesc(client.getInventoryId(slot));
    }

    /**
     * Returns the primary action ("eat", "bury") of an item in the client's
     * inventory.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @return the item's primary action ("eat", "bury").
     */
    public String getItemCommand(int slot) {
        return StaticAccess.get().getItemCommand(client.getInventoryId(slot));
    }

    /**
     * Returns true if an item in the client's inventory is tradeable.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @return true if the item is tradeable.
     */
    public boolean isItemTradable(int slot) {
        return StaticAccess.get().isItemTradable(client.getInventoryId(slot));
    }

    public boolean isItemStackable(int slot) {
        return StaticAccess.get().isItemStackable(client.getInventoryId(slot));
    }

    /**
     * Returns the approximate value of an item in the client's inventory.
     * Should generally be used just for comparison.
     *
     * @param slot the position of the item in the client's inventory, starting
     *             at 0.
     * @return the approximate value of the item.
     */
    public int getItemBasePrice(int slot) {
        return StaticAccess.get().getItemBasePrice(client.getInventoryId(slot));
    }

    /**
     * Returns true if the client has the required magic level and reagents to
     * cast the given spell.
     *
     * @param spell the spell's position in the list starting at 0.
     * @return true if the client has the required magic level and reagents to
     * cast the given spell.
     */
    public boolean canCastSpell(int spell) {
        if (getCurrentLevel(6) < StaticAccess.get().getSpellReqLevel(spell)) {
            return false;
        }
        for (int i = 0; i < StaticAccess.get().getReagentCount(spell); i++) {
            if (!ensureRunes(StaticAccess.get().getReagentId(spell, i), StaticAccess.get().getReagentAmount(spell, i))) {
                return false;
            }
        }
        return true;
    }

    private boolean ensureRunes(int id, int amount) {
        switch (id) {
            case 31:
                if (isEquippedId(197) || isEquippedId(615) || isEquippedId(682)) {
                    return true;
                }
                break;
            case 32:
                if (isEquippedId(102) || isEquippedId(616) || isEquippedId(683)) {
                    return true;
                }
                break;
            case 33:
                if (isEquippedId(101) || isEquippedId(617) || isEquippedId(684)) {
                    return true;
                }
                break;
            case 34:
                if (isEquippedId(103) || isEquippedId(618) || isEquippedId(685)) {
                    return true;
                }
                break;
        }
        return getInventoryCount(id) >= amount;
    }

    private boolean isEquippedId(int id) {
        for (int i = 0; i < getInventoryCount(); i++) {
            if (client.getInventoryId(i) == id) {
                return isItemEquipped(i);
            }
        }
        return false;
    }

    /**
     * See {@link Script#setAutoLogin(boolean)}. This checks the set flag.
     *
     * @return true if autologin is enabled.
     */
    public boolean isAutoLogin() {
        return AutoLogin.isAutoLogin();
    }

    /**
     * Enables or disables autologin.
     *
     * @param flag on/off
     * @see Script#stopScript()
     */
    public void setAutoLogin(boolean flag) {
        client.setAutoLogin(flag);
    }

    /**
     * See {@link Script#setRendering(boolean)}. This checks the set flag.
     *
     * @return true if rendering is enabled.
     */
    public boolean isRendering() {
        return client.isRendering();
    }

    /**
     * When disabled (false), may cause the game to enter a non-standard
     * (implemented by the bot) low graphics mode. The graphics buffer may no
     * longer be updated. When re-enabled, there may be a minor delay before the
     * buffer is updated again.
     *
     * @param flag the rendering flag.
     */
    public void setRendering(boolean flag) {
        client.setRendering(flag);
    }

    /**
     * See {@link Script#setSkipLines(boolean)}. This checks the set flag.
     *
     * @return true if skip_lines is enabled.
     */
    public boolean isSkipLines() {
        return client.isSkipLines();
    }

    /**
     * Some implementations support a low graphics mode that skips lines when
     * rendering (usually toggled by pressing F1). This enables or disables that
     * mode.
     *
     * @param flag the skip_lines flag.
     */
    public void setSkipLines(boolean flag) {
        client.setSkipLines(flag);
    }

    /**
     * See {@link Script#setPaintOverlay(boolean)}. This checks the set flag.
     *
     * @return true if paint_overlay is enabled.
     */
    public boolean isPaintOverlay() {
        return PaintListener.isEnabled();
    }

    /**
     * If paint_overlay is disabled, {@link Script#paint()} will not be called
     * and the built-in bot display (if any) will be hidden. May be effected by
     * the rendering flag {@link Script#setRendering(boolean)}.
     *
     * @param flag the paint_overlay flag.
     */
    public void setPaintOverlay(boolean flag) {
        PaintListener.setEnabled(flag);
    }

    /**
     * Attempts to save a screenshot with the specified file name in the
     * Screenshots directory. The file type will always be .png.
     *
     * @param file the file name (excluding extension) to save the screenshot as.
     */
    public void takeScreenshot(String file) {
        client.takeScreenshot(file);
    }

    /**
     * Attempts to send a private message to the player with the given display
     * name.
     *
     * @param msg  the message to send.
     * @param name the display name to send the private message to.
     */
    public void sendPrivateMessage(String msg, String name) {
        if (isFriend(name)) {
            client.sendPrivateMessage(msg, name);
        }
    }

    public void addFriend(String name) {
        final String lname = client.getPlayerName(client.getPlayer());
        if (name.length() > 0 && !name.equalsIgnoreCase(lname)) {
            client.addFriend(name);
        }
    }

    public void removeFriend(String name) {
        if (isFriend(name)) {
            client.removeFriend(name);
        }
    }

    public void addIgnore(String name) {
        final String lname = client.getPlayerName(client.getPlayer());
        if (name.length() > 0 && !name.equalsIgnoreCase(lname)) {
            client.addIgnore(name);
        }
    }

    public void removeIgnore(String name) {
        if (isIgnored(name)) {
            client.removeIgnore(name);
        }
    }

    public int getFriendCount() {
        return StaticAccess.get().getFriendCount();
    }

    public String getFriendName(int i) {
        return StaticAccess.get().getFriendName(i);
    }

    public int getIgnoredCount() {
        return StaticAccess.get().getIgnoredCount();
    }

    public String getIgnoredName(int i) {
        return StaticAccess.get().getIgnoredName(i);
    }

    public boolean isFriend(String name) {
        final int count = StaticAccess.get().getFriendCount();
        for (int i = 0; i < count; ++i) {
            if (StaticAccess.get().getFriendName(i).equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIgnored(String name) {
        final int count = StaticAccess.get().getIgnoredCount();
        for (int i = 0; i < count; ++i) {
            if (StaticAccess.get().getIgnoredName(i).equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoggedIn() {
        return client.isLoggedIn();
    }

    public int getQuestCount() {
        return client.getQuestCount();
    }

    public String getQuestName(int i) {
        return client.getQuestName(i);
    }

    public boolean isQuestComplete(int i) {
        return client.isQuestComplete(i);
    }

    public int getGameWidth() {
        return client.getGameWidth();
    }

    public int getGameHeight() {
        return client.getGameHeight();
    }
}
