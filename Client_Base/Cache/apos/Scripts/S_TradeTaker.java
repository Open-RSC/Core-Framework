import java.util.Arrays;
import java.util.Locale;

public final class S_TradeTaker extends Script {

    private int[] itm;
    private int[] itm_count;
    private boolean[] itm_banked;
    private String name;
    private long bank_time;
    private long menu_time;
    private boolean move_to;

    public S_TradeTaker(Extension ex) {
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
            itm_banked = new boolean[len - 1];
            for (int i = 1; i < len; ++i) {
                itm[i - 1] = Integer.parseInt(split[i]);
            }
        } catch (Throwable t) {
            System.out.println("ERROR: Failed to parse parameters.");
            System.out.println("Example: playername,itemid1,id2,id3...");
            return;
        }
        menu_time = -1L;
        bank_time = -1L;
        move_to = false;
        System.out.print("Taking ");
        System.out.print(Arrays.toString(itm));
        System.out.print(" from ");
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
            int itm_sz = itm.length;
            for (int i = 0; i < itm_sz; ++i) {
                int id = itm[i];
                int count = getInventoryCount(id);
                if (count > 0) {
                    if (!itm_banked[i]) {
                        itm_count[i] += count;
                        itm_banked[i] = true;
                    }
                    deposit(id, count);
                    return random(600, 800);
                }
            }
            closeBank();
            return random(600, 800);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (getInventoryCount() > 18) {
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
            acceptTrade();
            return random(1000, 2000);
        }
        int[] player = getPlayerByName(name);
        if (player[0] == -1) {
            System.out.println("Couldn't find player: " + name);
            System.out.println(
                    "Make sure you entered their name properly.");
            return random(1000, 1500);
        }
        if (!isWalking()) {
            if (move_to) {
                walkTo(player[1], player[2]);
                move_to = false;
            } else {
                Arrays.fill(itm_banked, false);
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
        drawString("S Trade Taker", 25, y, 1, white);
        y += 15;
        int num = itm.length;
        for (int i = 0; i < num; ++i) {
            if (itm_count[i] <= 0) {
                continue;
            }
            drawString("Banked " + getItemNameId(itm[i]) + ": " + itm_count[i],
                25, y, 1, white);
            y += 15;
        }
    }
}
