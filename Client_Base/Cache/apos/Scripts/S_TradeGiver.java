import java.util.Arrays;
import java.util.Locale;

public final class S_TradeGiver extends Script {

    private int[] itm;
    private int[] itm_count;
    private boolean[] itm_offered;
    private String name;
    private long bank_time;
    private int ptr;
    private boolean move_to;
	private long menu_time;

    public S_TradeGiver(Extension ex) {
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
            System.out.println("Example: playername,itemid1,id2,id3...");
            return;
        }
        ptr = 0;
        menu_time = -1L;
        bank_time = -1L;
        move_to = false;
        System.out.print("Giving ");
        System.out.print(Arrays.toString(itm));
        System.out.print(" to ");
        System.out.println(name);
    }

    @Override
    public int main() {
        if (isQuestMenu()) {
        	menu_time = -1L;
            answer(0);
            bank_time = System.currentTimeMillis();
            return random(600, 800);
        } else if (menu_time != -1L) {
            if (System.currentTimeMillis() >= (menu_time + 8000L)) {
            	menu_time = -1L;
            }
            return random(300, 400);
        }
        if (isBanking()) {
            bank_time = -1L;
            if (getInventoryCount(itm[ptr]) != 0) {
                closeBank();
                return random(600, 800);
            }
            int w = getEmptySlots();
            if (w > 24) w = 24;
            int bankc = bankCount(itm[ptr]);
            while (bankc <= 23) {
                if (ptr >= (itm.length - 1)) {
                    System.out.println("ERROR: Out of items.");
                    stopScript();
                    return 0;
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
        if (getInventoryCount(itm[ptr]) <= 0) {
            int[] banker = getNpcByIdNotTalk(BANKERS);
            if (banker[0] != -1) {
            	menu_time = System.currentTimeMillis();
                talkToNpc(banker[0]);
            }
            return random(600, 800);
        }
        if (isInTradeConfirm()) {
            confirmTrade();
            return random(1000, 2000);
        }
        if (isInTradeOffer()) {
            if (getLocalTradeItemCount() <= 0) {
                int index = getInventoryIndex(itm[ptr]);
                if (index != -1) {
                    int count = getInventoryCount(itm[ptr]);
                    if (count > 12) count = 12;
                    offerItemTrade(index, count);
                    if (!itm_offered[ptr]) {
                        itm_count[ptr] += count;
                        itm_offered[ptr] = true;
                    }
                    return random(1000, 2000);
                }
            }
            acceptTrade();
            return random(1000, 2000);
        }
        int[] player = getPlayerByName(name);
        if (player[0] == -1) {
            System.out.println("ERROR: Couldn't find player: " + name);
            System.out.println(
                    "Make sure you entered their name properly.");
            return random(1000, 1500);
        }
        if (!isWalking()) {
            if (move_to) {
                walkTo(player[1], player[2]);
                move_to = false;
            } else {
                Arrays.fill(itm_offered, false);
                sendTradeRequest(getPlayerPID(player[0]));
                return random(2000, 3000);
            }
        }
        return random(1000, 2000);
    }
    
    @Override
    public void onServerMessage(String str) {
        str = str.toLowerCase(Locale.ENGLISH);
        if (str.contains("not near")) {
            move_to = true;
        } else if (str.contains("busy")) {
            menu_time = -1L;
        }
    }

    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        int y = 25;
        drawString("S Trade Giver", 25, y, 1, white);
        y += 15;
        int num = itm.length;
        for (int i = 0; i < num; ++i) {
            if (itm_count[i] <= 0) {
                continue;
            }
            drawString("Given " + getItemNameId(itm[i]) + ": " + itm_count[i],
                25, y, 1, white);
            y += 15;
        }
    }
}
