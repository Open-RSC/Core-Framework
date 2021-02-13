import java.util.Arrays;

/*
 * Created with IntelliJ IDEA.
 * User: taylor
 * Project: APOS
 * Date: 3/9/14
 * Time: 3:10 PM
 */

public final class OgrePicker extends Script {

    private static final int X = 665;
    private static final int Y = 531;
    private static final int ARROW = 640;
    private static final int BONE = 413;
    private static final int[] ITEMS = {ARROW, BONE};
    private long trading;
    private long move;
    private final boolean[] traded = new boolean[2];
    private String partner_name;
    
    private static final int COMBAT_STYLE = 1;

    public OgrePicker(Extension e) {
        super(e);
    }

    @Override
    public void init(String s) {
        if (s.length() > 12 || s.contains(",")) {
            System.out.println("Error: Parameter should be your partners name");
            stopScript();
        }
        trading = -1L;
        move = -1L;
        partner_name = s;
        if (!partner_name.isEmpty()) {
            System.out.println("Sending trades to " + partner_name);
        } else {
            System.out.println("No partner");
        }
        Arrays.fill(traded, false);
    }

    @Override
    public int main() {
        if (getFightMode() != COMBAT_STYLE) {
            setFightMode(COMBAT_STYLE);
            return random(600, 800);
        }
        if (isWalking()) {
            return random(800, 1200);
        }
        int x = getX();
        int y = getY();
        if (move != -1L) {
            if (x != X + 1 || y != Y + 1) {
                walkTo(X + 1, Y + 1);
            } else {
                move = -1L;
            }
            return random(800, 1200);
        }
        if (!partner_name.isEmpty()) {
            if (trading != -1L) {
                if (System.currentTimeMillis() > trading + 20000L) {
                    trading = -1L;
                    return random(800, 1200);
                }
                if (isInTradeOffer()) {
                    int free_slots = 12 - getLocalTradeItemCount();
                    if (getInventoryCount(ARROW) > 0 && free_slots > 0 && !traded[0]) {
                        offerItemTrade(getInventoryIndex(ARROW), getInventoryCount(ARROW));
                        traded[0] = true;
                        return random(800, 1200);
                    }
                    if (getInventoryCount(BONES) > 0 && free_slots > 0 && !traded[1]) {
                        int bones = getInventoryCount(BONES);
                        int amount = (bones > free_slots) ? free_slots : bones;
                        offerItemTrade(getInventoryIndex(BONES), amount);
                        traded[1] = true;
                        return random(800, 1200);
                    }
                    acceptTrade();
                    return random(800, 1200);
                }
                if (isInTradeConfirm()) {
                    confirmTrade();
                    return random(800, 1200);
                }
                int[] partner = getPlayerByName(partner_name);
                if (partner[0] != -1) {
                    sendTradeRequest(getPlayerPID(partner[0]));
                }
                return 2000;
            }
            if (getInventoryCount(BONES) > 10) {
                trading = System.currentTimeMillis();
                return random(800, 1200);
            }
        } else {
            if (getFatigue() > 98) {
                useSleepingBag();
                return random(1000, 2000);
            }
            int bones = getInventoryIndex(BONES);
            if (bones != -1) {
                useItem(bones);
                return random(600, 800);
            }
        }
        int[] item = getItemById(ITEMS);
        if (item[0] != -1) {
            pickupItem(item[0], item[1], item[2]);
            return random(800, 1200);
        }
        if (x != X || y != Y) {
            walkTo(X, Y);
            return random(800, 1200);
        }
        return 200;
    }

    @Override
    public int[] getItemById(int... paramVarArgs) {
        int[] arrayOfInt = {-1, -1, -1};
        int i = 2147483647;
        for (int j = 0; j < getGroundItemCount(); j++) {
            int k = getGroundItemId(j);
            if (inArray(paramVarArgs, k)) {
                int m = getItemX(j);
                int n = getItemY(j);
                if (!isReachable(m, n)) {
                    continue;
                }
                int i1 = distanceTo(m, n, getX(), getY());
                if (i1 < i) {
                    arrayOfInt[0] = k;
                    arrayOfInt[1] = m;
                    arrayOfInt[2] = n;
                    i = i1;
                }
            }
        }
        return arrayOfInt;
    }

    @Override
    public void onTradeRequest(String name) {
        if (name.equals(partner_name)) {
            if (trading == -1L) {
                trading = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onServerMessage(String s) {
        if (s.contains("completed") || s.contains("declined")) {
            trading = -1L;
            traded[0] = false;
            traded[1] = false;
            return;
        }
        if (s.contains("near enough") || s.contains("an obstacle") || s.contains("been standing") || s.contains("close enough")) {
            move = System.currentTimeMillis();
        }
    }

    @Override
    public String getPlayerName(int local_index) { // storm
        // did I seriously never fix this? fuck me.
        return super.getPlayerName(local_index)
                .replace((char) 160, ' ');
    }
}
