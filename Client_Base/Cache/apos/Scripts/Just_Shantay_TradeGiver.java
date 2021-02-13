//S_TradeGiver edited to use Shantay Pass bank chest (by Stone. April 11, 2017)
//Edited by "Just reading the forums" for a better anti-logout and transfer rate tracker (4/17/2017)
//Edited by "Just reading the forums" for a 10-second timeout if trade becomes stuck (8/18/2017)
//Edited by "Just reading the forums" for two-way trading between 2 tradegiver scripts (10/22/2017)

import java.util.Arrays;
import java.util.Locale;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;

public final class Just_Shantay_TradeGiver extends Script {

  private int BANK_CHEST_X = 58;
  private int BANK_CHEST_Y = 731;
  private int[] itm;
  private int[] itm_count;
  private boolean[] itm_offered;
  private String name;
  private long bank_time;
  private long start_time;
  private int ptr;
  private int items_traded;
  private int last_item_id;
  private boolean move_to;
  private long menu_time;
  private long accept_time;
  private boolean is_idle;
  private boolean hasLocalConfirmedTrade;
  private boolean hasLocalAcceptedTrade;
  private boolean canPlayerContinue;
  private boolean isPartnerDone;
  private boolean isPartnerTempFriend;
  private final DecimalFormat iformat = new DecimalFormat("#,##0");
  private static final int GNOMEBALL_ID = 981;
  private static final int CHEST_ID = 942;
  private List<Integer> banked_items = new ArrayList<>();
  private List<Integer> banked_count = new ArrayList<>();

  public Just_Shantay_TradeGiver(Extension ex) {
      super(ex);
  }

  @Override
  public void init(String params) {      
      try {
          if (params == null || params.isEmpty()) {
              throw new Exception();
          }
          String[] split = params.split(",");
          name = split[0];
          int len = split.length;
          if (len == 1) throw new Exception();
          itm = new int[len - 1];
          itm_count = new int[len - 1];
          itm_offered = new boolean[len - 1];
          for (int i = 1; i < len; ++i) {
              itm[i - 1] = Integer.parseInt(split[i]);
          }
      } catch (Throwable t) {
          System.out.println("ERROR: Failed to parse parameters.");
          System.out.println("Example: Player Name,ItemID1,ID2,ID3...");
          return;
      }
       start_time = System.currentTimeMillis();
      ptr = 0;
       items_traded = 0;
       last_item_id = -1;
      menu_time = -1L;
      bank_time = -1L;
       accept_time = -1L;
      move_to = false;
       hasLocalConfirmedTrade = hasLocalAcceptedTrade = false;
       canPlayerContinue = true;
       isPartnerDone = false;
      System.out.print("Giving ");
      System.out.print(Arrays.toString(itm));
      System.out.print(" to ");
      System.out.println(name);
  }

    @Override
    public int main() {
        if (!isFriend(name)) {
            isPartnerTempFriend = true;
            addFriend(name);
            System.out.println("Added trade partner as temporary friend");
            System.out.println("Temporary friend won't be auto-removed if script is stopped before finishing");
            return random(1000, 1500);
        }
        if (isBanking()) {
            bank_time = -1L;
            if (getInventoryCount(itm[ptr]) != 0 || last_item_id == itm[ptr]) {
                closeBank();
                return random(600, 800);
            }
            int inv_count = getInventoryCount();
            for (int i = 6; i < inv_count; i++) {    //Deposit everything beyond the first 6 items in inventory
                int id = getInventoryId(i);
                if (inArray(itm, id)) continue;
                int item_count = getInventoryCount(id);
                deposit(id, item_count);
                if (inList(banked_items, id)) {
                    int index = getListIndex(banked_items, id);
                    if (index != -1) {
                        banked_count.set(index, (banked_count.get(index) + item_count));
                    }
                } else {
                    banked_items.add(id);
                    banked_count.add(item_count);
                }
                if (!canPlayerContinue) {
                    items_traded += item_count;
                }
                return random(800, 1000);
            }
            if (!canPlayerContinue) {
                closeBank();
                return random(800, 1000);
            }
            last_item_id = itm[ptr];    //Prevent multiple withdrawl attempts of the same item id due to missing server ticks - records last item withdrawn
            int w = getEmptySlots();
            if (w > 24) w = 24;
            int bankc = bankCount(itm[ptr]);
            while (bankc <= 23) {
                if (ptr >= (itm.length - 1)) {
                    System.out.println("Out of items to transfer");
                    canPlayerContinue = false;
                    if (isFriend(name)) {
                        sendPrivateMessage("Imdone", name);
                    }
                    if (isPartnerDone) {
                        if (isPartnerTempFriend) {
                            removeFriend(name);
                            System.out.println("Removed trade partner from friends list");
                        }
                        stopScript();
                        return 0;
                    }
                    closeBank();
                    return 1000;
                }
                bankc = bankCount(itm[++ptr]);
                System.out.println("Next item");
            }
            if (w > bankc) w = bankc;
            withdraw(itm[ptr], w);
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (last_item_id != -1) {
            last_item_id = -1; //Reset last item withdrawn - ready for next trade
        }
        if (canPlayerContinue ? getInventoryCount(itm[ptr]) <= 0 : getInventoryCount() > 18) {
            int[] chest_object = getObjectById(CHEST_ID);
            if (chest_object[0] != -1) {
                atObject(chest_object[1], chest_object[2]);
                return random(1000, 1500);
            }
            return random(600, 800);
        }
        if (isInTradeConfirm()) {
            if (isInTradeConfirm()) {
                confirmTrade();
                if (!hasLocalConfirmedTrade) {
                    hasLocalConfirmedTrade = true;
                    accept_time = System.currentTimeMillis();
                } else {
                    if ((System.currentTimeMillis() - accept_time) > 10000L) {
                        declineTrade();
                        accept_time = -1L;
                        hasLocalConfirmedTrade = false;
                    } else {
                        confirmTrade();
                    }
                }
                return random(1000, 1500);
            }
            return random(1000, 1500);
        }
        if (isInTradeOffer()) {
            if (canPlayerContinue) {
                if (getLocalTradeItemCount() <= 0) {
                    int index = getInventoryIndex(itm[ptr]);
                    if (index != -1) {
                        int count = getInventoryCount(itm[ptr]);
                        if (count > 12) count = 12;
                        offerItemTrade(index, count);
                        if (!itm_offered[ptr]) {
                            itm_count[ptr] += count;
                            items_traded += count;
                            itm_offered[ptr] = true;
                        }
                        return random(1000, 1500);
                    }
                }
            }
            if (!hasLocalAcceptedTrade()) {
                acceptTrade();
                if (!hasLocalAcceptedTrade) {
                    hasLocalAcceptedTrade = true;
                    accept_time = System.currentTimeMillis();
                } else {
                    if ((System.currentTimeMillis() - accept_time) > 10000L) {
                        declineTrade();
                        hasLocalAcceptedTrade = false;
                        accept_time = -1L;
                    }
                }
            } else {
                if ((System.currentTimeMillis() - accept_time) > 10000L) {
                    declineTrade();
                    hasLocalAcceptedTrade = false;
                    accept_time = -1L;
                } else {
                    acceptTrade();
                }
            }
            return random(1000, 1500);
        }
        int[] player = getPlayerByName(name);
        if (player[0] == -1) {
            System.out.println("ERROR: Couldn't find player: " + name);
            System.out.println(
                    "Make sure you entered their name properly.");
            return random(1000, 1500);
        }
        if (is_idle) {                 //ANTI LOGOUT
            int x = getX();
            int y = getY();
            if (x == 59 && y == 731) {
                walk_for_idle(x, y);
                return random(1000, 1500);
            } else {
                is_idle = false;
            }
        }
        int ball = getInventoryIndex(GNOMEBALL_ID);
        if (ball != -1) {
            dropItem(ball);
            return random(1000, 1200);
        }
        if (!isWalking()) {
            if (move_to) {
                walkTo(player[1], player[2]);
                move_to = false;
            } else {
                if (canPlayerContinue) {
                    Arrays.fill(itm_offered, false);
                } else if (isPartnerDone) {
                    if (isPartnerTempFriend) {
                        removeFriend(name);
                        System.out.println("Removed trade partner from friends list");
                    }
                    System.out.println("Both player and partner trading completed. Stopping script.");
                    stopScript();
                    return 0;
                }
                sendTradeRequest(getPlayerPID(player[0]));
                return random(1500, 1800);
            }
        }
        return random(1000, 1500);
    }
 
    public static boolean inList(List<Integer> haystack, int needle) {
       for (final int element : haystack) {
               if (element == needle) {
                   return true;
               }
       }
       return false;
   }
    
    public static int getListIndex(List<Integer> haystack, int needle) {
        for (int i = 0; i < haystack.size(); i++) {
            Integer element = haystack.get(i);
            if (element == needle) {
                return i;
            }
        }
        return -1;
    }
    
  @Override
  public void onServerMessage(String str) {
      str = str.toLowerCase(Locale.ENGLISH);
      if (str.contains("This chest")) {
         bank_time = System.currentTimeMillis();
      } else if (str.contains("not near")) {
           move_to = true;
       } else if (str.contains("have been standing")) {
           is_idle = true;
       } else if (str.contains("trade completed") || str.contains("declined trade") || str.contains("welcome to runescape")) {
            hasLocalConfirmedTrade = false;
            hasLocalAcceptedTrade = false;
            accept_time = -1L;
        }
  }

  private String per_hour(long count, long time) {
        double amount, secs;

        if (count == 0) return "0";
        amount = count * 60.0 * 60.0;
        secs = (System.currentTimeMillis() - time) / 1000.0;
        return iformat.format(amount / secs);
    }
    
    private long getTime() {
       long secondsSinceStarted = ((System.currentTimeMillis() - start_time) / 1000);
       if (secondsSinceStarted <= 0) {
           return 1L;
       }
       return secondsSinceStarted;
   }
    
    private String getRunTime() {
       long millis = getTime();
       long second = millis % 60;
       long minute = (millis / 60) % 60;
       long hour = (millis / (60 * 60)) % 24;
       long day = (millis / (60 * 60 * 24));

       if (day > 0L) return String.format("%02d days, %02d hrs, %02d mins", day, hour, minute);
       if (hour > 0L) return String.format("%02d hours, %02d mins, %02d secs", hour, minute, second);
       if (minute > 0L) return String.format("%02d minutes, %02d seconds", minute, second);
       return String.format("%02d seconds", second);
   }
    
    private void walk_for_idle(int x, int y) {
        if (isReachable(x + 1, y) && !is_player_on_pos(x + 1, y)) {
           walkTo(x + 1, y);
        } else if (isReachable(x, y + 1) && !is_player_on_pos(x, y + 1)) {
            walkTo(x, y + 1);
        } else if (isReachable(x + 1, y + 1) && !is_player_on_pos(x + 1, y + 1)) {
            walkTo(x + 1, y + 1);
        }
    }
    
    private boolean is_player_on_pos(int x, int y) {
        int count = countPlayers();
        for (int i = 1; i < count; ++i) {
            if (getPlayerX(i) == x && getPlayerY(i) == y) return true;
        }
        return false;
    }
    
    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        final int cyan = 0x00FFFF;
        int y = 25;
        drawString("Shantay Chest Trade Giver", 25, y, 1, cyan);
        y += 15;
        drawString("Runtime: " + getRunTime(), 25, y, 1, white);
        y += 15;
        int num = itm.length;
        for (int i = 0; i < num; ++i) {
            /*
            if (itm_count[i] <= 0) {
                continue;
            }*/
           drawString("Given " + getItemNameId(itm[i]) + ": " + itm_count[i],
              25, y, 1, white);
            y += 15;
        }
        y += 5;
        int num_banked = banked_items.size();
        for (int i = 0; i < num_banked; ++i) {
            if (banked_items.get(i) <= 0) {
              continue;
            }
           drawString("Taken " + getItemNameId(banked_items.get(i)) + ": " + banked_count.get(i),
              25, y, 1, white);
            y += 15;
        }
        y += 5;
        drawString("Transfer rate: " + per_hour(items_traded, start_time) + "/h", 25, y, 1, white);
        y += 15;
    }
 
    @Override
    public void onPrivateMessage(String msg, String name, boolean pmod, boolean jmod) {
        super.onPrivateMessage(msg, name, pmod, jmod);
        if (msg.equals("Imdone")) {
            if (this.name.equals(name)) {
                isPartnerDone = true;
                System.out.println("All items transferred from: " + this.name);
            } else {
                System.out.println("Warning! Ignored stop signal from wrong person: " + name);
            }
        }
    }
}