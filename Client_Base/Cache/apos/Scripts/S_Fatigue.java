/** Change Log:
    25Jan18 - fixed pathing used, specific issue was in "ingame__init" | fixed fatigue tricking, which wasn't being called properly | adjusted minor issues, not causing script failure

*/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Arrays;

import com.aposbot.Constants;
import com.aposbot.StandardCloseHandler;

/* Based on my fighter script. Start in the fight area. */
public final class S_Fatigue extends Script
  implements ActionListener {

   private static final String[] weapons = {
       "battle axe", "2-handed", "dragon axe", "dragon sword"
   };

   /*
    * ensure that we always have enough logs to make the difference
    * in arrow shafts:
    * required_logs = fatigue_tree / fatigue_shafts
    */
   private static final int REQUIRED_LOGS = 4;

   private static final int SKILL_HITS = 3;
   private static final int FIREMAKING = 11;

   private static final int TINDERBOX = 166;
   private static final int KNIFE = 13;
   private static final int LOGS = 14;

   private static final int[] trees = { 0, 1 };

   private Frame frame;
   private TextField tf_npcs;
   private TextField tf_eat;
   private TextField tf_range;
   private TextField tf_pickup;
   private TextField tf_level;
   private TextField tf_food;
   private Choice ch_fm;
   private Checkbox cb_under;
   private Checkbox cb_bank;

   private int start_x;
   private int start_y;
   private boolean init;

   private double min_fatigue;
   private long click_time;
   private long bank_time;
   private long menu_time;
   private boolean lighting;

   private int[] npc_ids;
   private int[] item_ids;
   private int eat_at;
   private int sleep_at;
   private int range;
   private int food_id;

   private PathWalker pw;
   private PathWalker.Path return_walk;
   private PathWalker.Location nearest_bank;
   private boolean pw_init;

   private final DecimalFormat iformat = new DecimalFormat("#,##0");
   private int[] start_xp = new int[SKILL.length];
   private long start_time;

   private int[] banked_count;
   private boolean[] has_banked;
   private boolean walked_to_bank;

   public S_Fatigue(Extension ex) {
       super(ex);
       pw = new PathWalker(ex);
   }

   public static void main(String[] argv) {
       new S_Fatigue(null).init(null);
   }

   @Override
   public void init(String params) {
       pw_init = false;
        start_time = click_time = bank_time = menu_time = -1L;
       init = false;
        setTrickMode(true);
       if (frame == null) {
           create_frame();
       }
       frame.setLocationRelativeTo(null);
       frame.toFront();
       frame.requestFocus();
       frame.setVisible(true);
   }

   @Override
   public int main() {
       if (start_time == -1L) {
            System.out.println("line 101");
           ingame_init();
           init = true;
       }
       int ideal_fm = ch_fm.getSelectedIndex();
       if (getFightMode() != ideal_fm) {
           setFightMode(ideal_fm);
           return random(400, 600);
       }
       if (inCombat()) {
           int weapon = get_weapon();
           if (weapon == -1) {
               System.out.println("No weapon found!");
               setAutoLogin(false);
               logout(); stopScript();
               return 0;
           }
           click_time = menu_time = bank_time = -1L;
           pw.resetWait();
           if (getAccurateFatigue() < min_fatigue) {
               if (isItemEquipped(weapon)) {
                   removeItem(weapon);
                   return random(600, 1000);
               }
               walkTo(getX(), getY());
               return random(400, 600);
           }
           if (!isItemEquipped(weapon)) {
               wearItem(weapon);
               return random(600, 1000);
           }
           if (getCurrentLevel(SKILL_HITS) <= eat_at) {
               walkTo(getX(), getY());
               return random(400, 600);
           }
           return random(250, 450);
       }
       if (isBanking()) {
           return banking();
       }
       if (bank_time != -1L) {
           if (System.currentTimeMillis() > (bank_time + 8000L)) {
               bank_time = -1L;
           }
           return random(100, 300);
       }
       if (isQuestMenu()) {
           answer(0);
           menu_time = -1L;
           if (should_bank()) {
               bank_time = System.currentTimeMillis();
           }
           return random(600, 800);
       }
       if (menu_time != -1L) {
           if (System.currentTimeMillis() > (menu_time + 8000L)) {
               menu_time = -1L;
           }
           return random(100, 300);
       }
       if (getCurrentLevel(SKILL_HITS) <= eat_at) {
           int food = get_food();
           if (food != -1) {
               useItem(food);
               return random(800, 1000);
           }
           System.out.println("No food!");
           return random(500, 1000);
       }
       if (click_time != -1L) {
           if (System.currentTimeMillis() > (click_time + 8000L)) {
               click_time = -1L;
           }
           // hack to work around lag
           if (lighting) {
               int[] logs = getItemById(14);
               if (logs[1] != getX() || logs[2] != getY()) {
                   click_time = -1L;
               }
           }
           return 0;
       }
       lighting = false;
       if (pw_init) {
           if (pw.walkPath()) return 0;
       }
       if (should_bank()) {
           return access_bank();
       }
       if (!isAtApproxCoords(start_x, start_y, range)) {
           if (range <= 10 && isReachable(start_x, start_y)) {
               System.out.println("Going back");
               walkTo(start_x, start_y);
               return random(1000, 2000);
           }
           if (!pw_init) {
               pw.init(null);
               pw_init = true;
           }
           PathWalker.Path p = pw.calcPath(start_x, start_y);
           if (p != null) {
               System.out.println("Going back");
               pw.setPath(p);
               return random(600, 800);
           } else {
               System.out.println("Error calculating path, " +
                   "trying to move");
               walk_approx(getX(), getY(), 10);
               return random(1000, 2000);
           }
       }
       if (!cb_under.getState()) {
           int[] item = get_reachable_item(item_ids);
           if (should_take(item[0], item[1], item[2])) {
               if (distanceTo(item[1], item[2]) > 5) {
                   walk_approx(item[1], item[2], 1);
                   return random(1000, 2000);
               }
               pickupItem(item[0], item[1], item[2]);
               return random(1000, 1200);
           }
       } else {
           int[] item = get_item_fast(item_ids);
           if (should_take(item[0], item[1], item[2])) {
               pickupItem(item[0], item[1], item[2]);
               return random(1000, 1200);
           }
       }
       if (getFatigue() == 100) {
           click_time = -1L;
           useSleepingBag();
           return random(1000, 1500);
       }
       if (getAccurateFatigue() < min_fatigue) {
           return increase_fatigue();
       }
       return attack();
   }

   @Override
   public void actionPerformed(ActionEvent event) {
       if (event.getActionCommand().equals("OK")) {
           parse_fields();
       }
       frame.setVisible(false);
   }

   @Override
   public void onServerMessage(String str) {
       str = str.toLowerCase(Locale.ENGLISH);
       if (str.contains("fire catches") ||
           str.contains("fail to light") ||
           str.contains("nothing interesting")) {
           click_time = -1L;
           menu_time = -1L;
       } else if (str.contains("get some wood")) {
           /*
            * wait a bit (around the length of a server
            * tick plus a hundred ms to make up for lag)
            * so we don't try to instantly cut a stump
            */
           click_time = System.currentTimeMillis() - 7300L;
       } else if (str.contains("busy")) {
           menu_time = -1L;
       }
   }

   @Override
   public void paint() {
       int x = 150;
       int y = 35;
       drawString("S Fatigue", x, y, 2, 0xFFD900);
       y += 15;
        drawString("updated by kRiStOf", x, y, 2, 0x1E90FF);
        y += 15;
       drawString("Runtime: " + get_runtime(), x, y, 2, 0xFFFFFF);
       y += 15;
       for (int i = 0; i < start_xp.length; ++i) {
           int gained = getXpForLevel(i) - start_xp[i];
           if (gained == 0) continue;
           drawString(String.format("%s XP gained: %s (%s/h)",
               SKILL[i], iformat.format(gained), per_hour(gained)),
               x, y, 2, 0xFFFFFF);
           y += 15;
       }
       for (int i = 0; i < item_ids.length; ++i) {
           if (banked_count[i] == 0) continue;
           drawString(String.format("%s banked: %s (%s/h)",
               getItemNameId(item_ids[i]),
               iformat.format(banked_count[i]),
               per_hour(banked_count[i])),
               x, y, 2, 0xFFFFFF);
       }
   }

   private boolean should_take(int id, int x, int y) {
       if (id == -1) return false;
       if (getInventoryCount() == MAX_INV_SIZE) {
           if (isItemStackableId(id) &&
               getInventoryIndex(id) != -1) {
               return true;
           }
           int food = get_food();
           if (food != -1) {
               useItem(food);
           }
           return false;
       }
       return true;
   }

   private int attack() {
       int weapon = get_weapon();
       if (weapon == -1) {
           System.out.println("No weapon found!");
           setAutoLogin(false);
           logout(); stopScript();
           return 0;
       }
       if (!isItemEquipped(weapon)) {
           wearItem(weapon);
           return random(600, 1000);
       }
       int[] npc = get_reachable_npc(npc_ids);
       if (npc[0] != -1) {
           if (distanceTo(npc[1], npc[2]) > 5) {
               walk_approx(npc[1], npc[2], 1);
               return random(1000, 2000);
           }
           attackNpc(npc[0]);
           return random(600, 1000);
       }
       return random(100, 700);
   }

   private int[] get_reachable_item(int... ids) {
       int[] item = new int[] {
           -1, -1, -1
       };
       int count = getGroundItemCount();
       int max_dist = Integer.MAX_VALUE;
       for (int i = 0; i < count; i++) {
           int id = getGroundItemId(i);
           if (!inArray(ids, id)) {
               continue;
           }
           int x = getItemX(i);
           int y = getItemY(i);
           if (!isReachable(x, y)) continue;
           if (distanceTo(x, y, start_x, start_y) > range) {
               continue;
           }
           int dist = distanceTo(x, y, getX(), getY());
           if (dist < max_dist) {
               item[0] = id;
               item[1] = x;
               item[2] = y;
               max_dist = dist;
           }
       }
       return item;
   }

   private int[] get_item_fast(int... ids) {
       int count = getGroundItemCount();
       int x = getX();
       int y = getY();
       for (int i = 0; i < count; ++i) {
           if (getItemX(i) != x || getItemY(i) != y) {
               continue;
           }
           int id = getGroundItemId(i);
           if (inArray(ids, id)) {
               return new int[] { id, x, y };
           }
       }
       return new int[] { -1, -1, -1 };
   }

   private int[] get_reachable_npc(int... ids) {
       int[] npc = new int[] {
           -1, -1, -1
       };
       int max_dist = Integer.MAX_VALUE;
       int count = countNpcs();
       for (int i = 0; i < count; i++) {
           if (isNpcInCombat(i)) continue;
           if (!inArray(ids, getNpcId(i))) {
               continue;
           }
           int x = getNpcX(i);
           int y = getNpcY(i);
           if (!isReachable(x, y)) continue;
           if (distanceTo(x, y, start_x, start_y) > range) {
               continue;
           }
           int dist = distanceTo(x, y, getX(), getY());
           if (dist < max_dist) {
               npc[0] = i;
               npc[1] = x;
               npc[2] = y;
               max_dist = dist;
           }
       }
       return npc;
   }

   private void walk_approx(int x, int y, int range) {
       int dx, dy;
       int loop = 0;
       do {
           dx = x + random(-range, range);
           dy = y + random(-range, range);
           if ((++loop) > 100) return;
       } while (!isReachable(dx, dy));
       walkTo(dx, dy);
   }

   private boolean is_unoccupied_reachable(int x, int y) {
       if (isObjectAt(x, y)) {
           return false;
       }
       int npc_count = countNpcs();
       for (int i = 0; i < npc_count; ++i) {
           if (getNpcX(i) == x && getNpcY(i) == y) {
               return false;
           }
       }
       int player_count = countPlayers();
       for (int i = 0; i < player_count; ++i) {
           if (getPlayerX(i) == x && getPlayerY(i) == y) {
               return false;
           }
       }
       return isReachable(x, y);
   }

   private Point get_nearest_tree() {
       int best_x = 0;
       int best_y = 0;
       int best_dist = Integer.MAX_VALUE;
       int count = getObjectCount();
       for (int i = 0; i < count; ++i) {
           int id = getObjectId(i);
           if (!inArray(trees, id)) {
               continue;
           }
           int x = getObjectX(i);
           int y = getObjectY(i);
           if (distanceTo(x, y, start_x, start_y) > range) {
               continue;
           }
           int dist = distanceTo(x, y);
           if (dist >= best_dist) {
               continue;
           }
           Point p = get_walk_point(x, y);
           if (p != null) {
               best_x = x;
               best_y = y;
               best_dist = dist;
           }
       }
       return new Point(best_x, best_y);
   }

   private Point get_walk_point(int tree_x, int tree_y) {
       Point best_point = null;
       int best_dist = Integer.MAX_VALUE;
       // try to find a spot around a tree where a fire can be lit
       for (int x = -1; x <= 1; ++x) {
           ly: for (int y = -1; y <= 1; ++y) {
               if (x == 0 && y == 0) {
                   continue;
               }
               if (x != 0 && y != 0) {
                   // can't cut from a diagonal
                   continue;
               }
               int wx = tree_x + x;
               int wy = tree_y + y;
               int dist = distanceTo(wx, wy);
               if (dist >= best_dist) {
                   continue;
               }
               if (is_unoccupied_reachable(wx, wy)) {
                   best_point = new Point(wx, wy);
                   best_dist = dist;
               }
           }
       }
       // fall back to checking diagonals
       for (int x = -1; x <= 1; ++x) {
           ly: for (int y = -1; y <= 1; ++y) {
               int wx = tree_x + x;
               int wy = tree_y + y;
               int dist = distanceTo(wx, wy);
               if (getX() == wx && getY() == wy) {
                   best_point = new Point(wx, wy);
                   best_dist = dist;
                   break;
               }
               if (dist >= best_dist) {
                   continue;
               }
               if (is_unoccupied_reachable(wx, wy)) {
                   best_point = new Point(wx, wy);
                   best_dist = dist;
               }
           }
       }
       return best_point;
   }

   private int get_weapon() {
       int count = getInventoryCount();
       for (int i = 0; i < count; ++i) {
           int id = getInventoryId(i);
           String str = getItemNameId(id)
               .toLowerCase(Locale.ENGLISH);
           for (int j = 0; j < weapons.length; ++j) {
               if (str.contains(weapons[j])) {
                   return i;
               }
           }
       }
       return -1;
   }

   private String get_runtime() {
       long millis = (System.currentTimeMillis() - start_time) / 1000;
       long second = millis % 60;
       long minute = (millis / 60) % 60;
       long hour = (millis / (60 * 60)) % 24;
       long day = (millis / (60 * 60 * 24));

       if (day > 0L) {
           return String.format("%02d days, %02d hrs, %02d mins",
               day, hour, minute);
       }
       if (hour > 0L) {
           return String.format("%02d hours, %02d mins, %02d secs",
               hour, minute, second);
       }
       if (minute > 0L) {
           return String.format("%02d minutes, %02d seconds",
               minute, second);
       }
       return String.format("%02d seconds", second);
   }

   private String per_hour(int count) {
       double amount, secs;

       if (count == 0) return "0";
       amount = count * 60.0 * 60.0;
       secs = (System.currentTimeMillis() - start_time) / 1000.0;
       return iformat.format(amount / secs);
   }

   private boolean should_bank() {
       return cb_bank.getState() && getInventoryCount(food_id) == 0;
   }

   private int access_bank() {
       if (!walked_to_bank) {
           PathWalker.Path p;

           if (!pw_init) {
               pw.init(null);
               pw_init = true;
           }
           p = pw.calcPath(nearest_bank.x, nearest_bank.y);
           if (p == null) {
               System.out.println(
                   "Failed to calc path, trying to move...");
               walk_approx(getX(), getY(), 1);
               return random(1000, 2000);
           }
           pw.setPath(p);
           walked_to_bank = true;
           return 0;
       }
       int[] npc = getNpcByIdNotTalk(BANKERS);
       if (npc[0] != -1) {
           talkToNpc(npc[0]);
           menu_time = System.currentTimeMillis();
       }
       return random(600, 800);
   }

   private int banking() {
       bank_time = -1L;
       for (int i = 0; i < item_ids.length; ++i) {
           int count = getInventoryCount(item_ids[i]);
           if (count == 0) continue;
           deposit(item_ids[i], count);
           if (!has_banked[i]) {
               banked_count[i] += count;
               has_banked[i] = true;
           }
           return random(600, 1000);
       }
       int empty;
       int logs = getInventoryCount(LOGS);
       if (logs >= REQUIRED_LOGS) {
           empty = getEmptySlots();
       } else {
           empty = getEmptySlots() - (REQUIRED_LOGS - logs);
       }
       if (empty > 0) {
           int count = bankCount(food_id) - 1;
           if (count <= 0) {
               System.out.println("Out of food!");
               setAutoLogin(false); stopScript();
               return 0;
           }
           if (count > empty) {
               count = empty;
           }
           withdraw(food_id, count);
           return random(1000, 2000);
       }
       if (return_walk == null) {
           if (!pw_init) {
               pw.init(null);
               pw_init = true;
           }
           return_walk = pw.calcPath(
               getX() + random(-1, 1),
               getY() + random(-1, 1),
               start_x + random(-1, 1),
               start_y + random(-1, 1));
           if (return_walk == null) {
               return 0;
           }
       }
       pw.setPath(return_walk);
       Arrays.fill(has_banked, false);
       closeBank();
       walked_to_bank = false;
       return random(600, 800);
   }

   private void ingame_init() {
        System.out.println("ingame_init");
        start_time = System.currentTimeMillis();
        Arrays.fill(has_banked, false);
       start_x = getX();
       start_y = getY();
       lighting = false;
       if (getInventoryIndex(KNIFE) == -1) {
           System.out.println("WARNING: " +
               "Might need to make arrow shafts to get " +
               "right fatigue, but no knife found.");
       }
       if (getInventoryIndex(TINDERBOX) == -1) {
           System.out.println("WARNING: " +
               "No tinderbox found, but having one will " +
               "speed up the process.");
       }
       for (int i = 0; i < start_xp.length; ++i) {
           start_xp[i] = getXpForLevel(i);
       }
       if (cb_bank.getState()) {
            pw.init(null);
            pw_init = true;
            System.out.println("cb_bank");
           nearest_bank = pw.getNearestBank(getX(), getY());
           System.out.println("Nearest bank: " +
               nearest_bank.name);
       }
       walked_to_bank = false;
       return_walk = null;
   }

   private int increase_fatigue() {
       double required_fatigue = min_fatigue - getAccurateFatigue();
       int shafts_needed = (int)Math.ceil(required_fatigue / 0.14);
       if (required_fatigue < 0.533 &&
           getInventoryCount(LOGS) >= shafts_needed) {
           int knife = getInventoryIndex(KNIFE);
           int logs = getInventoryIndex(LOGS);
           if (knife != -1 && logs != -1) {
               useItemWithItem(knife, logs);
               menu_time = System.currentTimeMillis();
               return random(600, 1000);
           }
       }
       int tinderbox = getInventoryIndex(TINDERBOX);
       if (tinderbox != -1 && required_fatigue >= 1.1610 &&
          getInventoryCount(LOGS) >= REQUIRED_LOGS) {
           int[] logs = getItemById(LOGS);
           if (logs[1] == getX() && logs[2] == getY() &&
               !isObjectAt(logs[1], logs[2])) {
               useItemOnGroundItem(tinderbox,
                   logs[0], logs[1], logs[2]);
               click_time = System.currentTimeMillis();
               lighting = true;
               return random(600, 1000);
           }
           if (getInventoryCount(LOGS) > REQUIRED_LOGS) {
               int to_drop = getInventoryIndex(LOGS);
               if (to_drop != -1) {
                   dropItem(to_drop);
                   return random(1000, 2000);
               }
           }
       }
       return cut_tree();
   }

   private int cut_tree() {
       if (getInventoryCount(LOGS) < REQUIRED_LOGS) {
           if (getInventoryCount() == MAX_INV_SIZE) {
               int food = get_food();
               if (food != -1) {
                   useItem(food);
                   return random(800, 1000);
               }
           }
       }
       Point tree = get_nearest_tree();
       Point wp = get_walk_point(tree.x, tree.y);
       if (wp == null) {
           return random(1000, 2000);
       }
       if (getX() != wp.x || getY() != wp.y) {
           walkTo(wp.x, wp.y);
       } else {
           atObject(tree.x, tree.y);
           click_time = System.currentTimeMillis();
       }
       return random(1000, 2000);
   }

   private void create_frame() {
       ch_fm = new Choice();
       int len = FIGHTMODES.length;
       for (int i = 0; i < len; ++i) {
           ch_fm.add(FIGHTMODES[i]);
       }
       ch_fm.select(1);

       Panel pInput = new Panel();
       pInput.setLayout(new GridLayout(0, 2, 0, 2));

       pInput.add(new Label("NPC ids (1,2,3...):"));
       pInput.add(tf_npcs = new TextField());

       pInput.add(new Label("NPC's level"));
       pInput.add(tf_level = new TextField());

       pInput.add(new Label("Combat style:"));
       pInput.add(ch_fm);

       pInput.add(new Label("Walkback range:"));
       pInput.add(tf_range = new TextField("30"));

       pInput.add(new Label("Item ids (1,2,3...):"));
       pInput.add(tf_pickup = new TextField());

       pInput.add(new Label("Eat at HP level:"));
       pInput.add(tf_eat = new TextField("9"));

       pInput.add(new Label("Food ID"));
       pInput.add(tf_food = new TextField("373"));

       Panel cbPanel = new Panel();
       cbPanel.setLayout(new GridLayout(0, 1));
       cbPanel.add(cb_under = new Checkbox(
           "Only pick up items directly underneath the player", true));
       cbPanel.add(cb_bank = new Checkbox("Bank for food", true));

       Panel buttonPanel = new Panel();
       Button ok = new Button("OK");
       ok.addActionListener(this);
       buttonPanel.add(ok);
       Button cancel = new Button("Cancel");
       cancel.addActionListener(this);
       buttonPanel.add(cancel);

       frame = new Frame(getClass().getSimpleName());
       frame.setIconImages(Constants.ICONS);
       frame.addWindowListener(
           new StandardCloseHandler(frame, StandardCloseHandler.HIDE)
       );
       frame.add(pInput, BorderLayout.NORTH);
       frame.add(cbPanel, BorderLayout.CENTER);
       frame.add(buttonPanel, BorderLayout.SOUTH);
       frame.setResizable(false);
       frame.pack();
   }

   private void parse_fields() {
       try {
           String[] array = tf_npcs.getText().trim().split(",");
           int array_sz = array.length;
           npc_ids = new int[array_sz];
           for (int i = 0; i < array_sz; i++) {
               npc_ids[i] = Integer.parseInt(array[i]);
           }
       } catch (Throwable t) {
           System.out.println("Couldn't parse npc ids");
           npc_ids = new int[0];
       }
       try {
           String[] array = tf_pickup.getText().trim().split(",");
           int array_sz = array.length;
           item_ids = new int[array_sz];
           banked_count = new int[array_sz];
           has_banked = new boolean[array_sz];
           for (int i = 0; i < array_sz; i++) {
               item_ids[i] = Integer.parseInt(array[i]);
           }
       } catch (Throwable t) {
           System.out.println("Couldn't parse item ids");
           item_ids = new int[0];
           banked_count = new int[0];
           has_banked = new boolean[0];
       }
       try {
           eat_at = Integer.parseInt(tf_eat.getText().trim());
       } catch (Throwable t) {
           System.out.println("Couldn't parse eat at value");
       }
       try {
           food_id = Integer.parseInt(tf_food.getText().trim());
       } catch (Throwable t) {
           System.out.println("Couldn't parse food value");
       }
       try {
           range = Integer.parseInt(tf_range.getText().trim());
       } catch (Throwable t) {
           System.out.println("Couldn't parse range value");
       }
       double level;
       try {
           level = Integer.parseInt(tf_level.getText().trim());
       } catch (Throwable t) {
           System.out.println("Couldn't parse target's level");
           return;
       }
       double xp = (((level * 2.0) + 20.0) / 4.0) * 3.0;
       min_fatigue = 100.0 - (xp / 100.0);
       System.out.println("Attacking at " + min_fatigue + "%");
   }

   private int get_food() {
       int count = getInventoryCount();
       for (int i = 0; i < count; i++) {
           String cmd = getItemCommand(i)
               .toLowerCase(Locale.ENGLISH);
           if (cmd.equals("eat")) {
               return i;
           }
       }
       return -1;
   }
}