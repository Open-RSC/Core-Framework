import java.awt.Point;
import java.util.Locale;

public final class S_HerblawShop extends Script {
    
    /*
     * params can be the following:
     * vials: buy only vials
     * eyes: buy only eyes
     * prefer vials: buy both vials and eyes, but prefer vials
     * otherwise, it will buy both vials and eyes, but prefer eyes
     * 
     * if the params contain "fill", vials will be filled before banking
     * 
     * ---
     * 
     * rewritten for the 5th time. maybe this time i've got it right
     */
    
    // ids
    private static final int VIAL = 465;
    private static final int VIAL_FILLED = 464;
    private static final int EYE = 270;
    // (object)
    private static final int MEMBER_GATE = 137;
    private static final int JATIX = 230;
    private static final int FOUNTAIN = 26;
    // door (wallobj)
    private static final int SHOP_CLOSED = 2;
    // doors (object)
    private static final int BANK_CLOSED = 64;
    
    private static final Point shop_door = new Point(371, 506);
    
    private static final Point bank_pos = new Point(328, 552);
    // gate walk position coming from bank
    private static final Point gate_b_pos = new Point(341, 488);
    // gate walk position coming from shop
    private static final Point gate_s_pos = new Point(342, 488);
    
    // approximate location of shop (not inside building)
    private static final Point shop_pos = new Point(364, 498);
    
    // approximate position of fountain
    private static final Point water_pos = new Point(326, 545);
    
    // it's not a rectangle...
    private static final Point[] ext_shop_points = {
        new Point(368, 502),
        new Point(369, 503),
        new Point(368, 503),
        new Point(367, 503),
        new Point(369, 509),
        new Point(368, 509),
        new Point(367, 509),
        new Point(368, 510)
    };
 
    private boolean buy_vials;
    private boolean buy_eyes;
    private boolean prefer_vials;
    private boolean fill_vials;
    
    private int banked_vials;
    private int banked_eyes;
    
    private PathWalker pw;
    private PathWalker.Path bank_to_gate;
    private PathWalker.Path gate_to_bank;
    private PathWalker.Path shop_to_gate;
    private PathWalker.Path gate_to_shop;
    private long start_time;
    private long bank_time;
    private long shop_time;

    public S_HerblawShop(Extension ex) {
        super(ex);
        pw = new PathWalker(ex);
    }

    @Override
    public void init(String params) {
        params = params.toLowerCase(Locale.ENGLISH);
        if (params.contains("prefer vials")) {
            System.out.println("Buying eyes and vials, vials preferred");
            buy_eyes = true;
            buy_vials = true;
            prefer_vials = true;
        } else if (params.contains("vials")) {
            System.out.println("Buying only vials");
            buy_vials = true;
            buy_eyes = false;
        } else if (params.contains("eyes")) {
            System.out.println("Buying only eyes");
            buy_vials = false;
            buy_eyes = true;
        } else {
            System.out.println("Buying eyes and vials, eyes preferred");
            buy_eyes = true;
            buy_vials = true;
            prefer_vials = false;
        }
        if (params.contains("fill")) {
            System.out.println("also filling bought vials");
            fill_vials = true;
        }
        pw.init(null);
        
        bank_to_gate = pw.calcPath(bank_pos.x, bank_pos.y, gate_b_pos.x, gate_b_pos.y);
        
        if (!fill_vials) {
            gate_to_bank = pw.calcPath(gate_b_pos.x, gate_b_pos.y, bank_pos.x, bank_pos.y);
        } else {
            gate_to_bank = pw.calcPath(gate_b_pos.x, gate_b_pos.y, water_pos.x, water_pos.y);
        }

        shop_to_gate = pw.calcPath(shop_pos.x, shop_pos.y, gate_s_pos.x, gate_s_pos.y);
        gate_to_shop = pw.calcPath(gate_s_pos.x, gate_s_pos.y, shop_pos.x, shop_pos.y);
        
        banked_vials = 0;
        banked_eyes = 0;
        start_time = -1L;
        bank_time = -1L;
        shop_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (isQuestMenu()) {
            if (questMenuOptions()[0].contains("access")) {
                bank_time = System.currentTimeMillis();
            } else {
                shop_time = System.currentTimeMillis();
            }
            answer(0);
            return random(1000, 2000);
        }
        if (isBanking()) {
            bank_time = -1L;
            int count = getInventoryCount(VIAL_FILLED);
            if (count > 0) {
                deposit(VIAL_FILLED, count);
                banked_vials += count;
                return random(2000, 3000);
            }
            count = getInventoryCount(VIAL);
            if (count > 0) {
                deposit(VIAL, count);
                banked_vials += count;
                return random(2000, 3000);
            }
            count = getInventoryCount(EYE);
            if (count > 0) {
                deposit(EYE, count);
                banked_eyes += count;
                return random(2000, 3000);
            }
            closeBank();
            pw.setPath(bank_to_gate);
            return random(1000, 2000);
        } else if (bank_time != -1L) {
            if (System.currentTimeMillis() >= (bank_time + 8000L)) {
                bank_time = -1L;
            }
            return random(300, 400);
        }
        if (isShopOpen()) {
            shop_time = -1L;
            if (getInventoryCount() == MAX_INV_SIZE) {
                closeShop();
                return random(1000, 2000);
            }
            if (prefer_vials) {
                if (buy_vials) {
                    int i = _buy(VIAL);
                    if (i != -1) return i;
                }
                if (buy_eyes) {
                    int i = _buy(EYE);
                    if (i != -1) return i;
                }
            } else {
                if (buy_eyes) {
                    int i = _buy(EYE);
                    if (i != -1) return i;
                }
                if (buy_vials) {
                    int i = _buy(VIAL);
                    if (i != -1) return i;
                }
            }
            return random(600, 1000);
        } else if (shop_time != -1L) {
            if (System.currentTimeMillis() >= (shop_time + 8000L)) {
                shop_time = -1L;
            }
            return random(300, 400);
        }
        if (pw.walkPath()) return 0;
        int[] jatix = getNpcByIdNotTalk(JATIX);
        if (jatix[0] != -1) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                if (insideShop(getX(), getY())) {
                    // check door before exiting
                    if (_getBoundId(shop_door.x, shop_door.y) == SHOP_CLOSED) {
                        atWallObject(shop_door.x, shop_door.y);
                        return random(1500, 2700);
                    }
                }
                pw.setPath(shop_to_gate);
                return random(600, 1000);
            } else {
                boolean j_inside = insideShop(jatix[1], jatix[2]);
                boolean p_inside = insideShop();
                if (j_inside && !p_inside) {
                    // check door before entering
                    if (_getBoundId(shop_door.x, shop_door.y) == SHOP_CLOSED) {
                        atWallObject(shop_door.x, shop_door.y);
                        return random(1500, 2700);
                    }
                } else if (!j_inside && p_inside) {
                    // check door before exiting
                    if (_getBoundId(shop_door.x, shop_door.y) == SHOP_CLOSED) {
                        atWallObject(shop_door.x, shop_door.y);
                        return random(1500, 2700);
                    }
                }
                if (distanceTo(jatix[1], jatix[2]) > 6) {
                    int x = 0;
                    int y = 0;
                    int loop = 0;
                    do {
                        x = jatix[1] + random(-1, 1);
                        y = jatix[2] + random(-1, 1);
                    } while (
                            (loop++) < 2000 &&
                            (!isReachable(x, y) ||
                            (j_inside && !insideShop(x, y)))
                            );
                    walkTo(x, y);
                    return random(2000, 3000);
                }
                talkToNpc(jatix[0]);
                return random(3000, 3500);
            }
        }
        if (getX() == gate_b_pos.x && getY() == gate_b_pos.y) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                pw.setPath(gate_to_bank);
                return random(600, 1000);
            } else {
                int[] gate = getObjectById(MEMBER_GATE);
                if (_objectValid(gate)) {
                    atObject(gate[1], gate[2]);
                    return random(1500, 2700);
                }
                return random(600, 1000);
            }
        } else if (getX() == gate_s_pos.x && getY() == gate_s_pos.y) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                int[] gate = getObjectById(MEMBER_GATE);
                if (_objectValid(gate)) {
                    atObject(gate[1], gate[2]);
                    return random(1500, 2700);
                }
                return random(600, 1000);
            } else {
                pw.setPath(gate_to_shop);
                return random(600, 1000);
            }
        } else if (isAtApproxCoords(water_pos.x, water_pos.y, 3)) {
            if (fill_vials) {
                if (getInventoryIndex(VIAL) != -1) {
                    useItemOnObject(VIAL, FOUNTAIN);
                    return random(600, 1000);
                }
            }
            int[] doors = getObjectById(BANK_CLOSED);
            if (_objectValid(doors)) {
                atObject(doors[1], doors[2]);
                return random(1500, 2700);
            }
            if (!isWalking()) {
                walkTo(329 + random(-1, 1), 552 + random(-1, 1));
            }
            return random(1000, 2000);
        } else if (_insideBank()) {
            if (getInventoryCount() == MAX_INV_SIZE) {
                int[] banker = getNpcByIdNotTalk(BANKERS);
                if (banker[0] != -1) {
                    talkToNpc(banker[0]);
                    return random(2700, 3400);
                }
                return random(600, 1000);
            } else {
                pw.setPath(bank_to_gate);
                return random(600, 1000);
            }
        }
        return random(600, 1000);
    }
    
    @Override
    public void paint() {
        final int white = 0xFFFFFF;
        int y = 25;
        drawString("S HerblawShop", 25, y, 1, white);
        y += 15;
        drawString("Runtime: " + _getRuntime(), 25, y, 1, white);
        y += 15;
        if (banked_vials > 0) {
            drawString("Banked vials: " + banked_vials, 25, y, 1, white);
            y += 15;
        }
        if (banked_eyes > 0) {
            drawString("Banked eyes: " + banked_eyes, 25, y, 1, white);
            y += 15;
        }
    }
    
    @Override
    public void onServerMessage(String str) {
        if (str.contains("enough money")) {
            System.out.println("Runtime: " + _getRuntime());
            System.out.println("Banked vials: " + banked_vials);
            System.out.println("Banked eyes: " + banked_eyes);
            System.out.println("Out of money!");
            stopScript(); setAutoLogin(false);
        }
    }
    
    private int _buy(int id) {
        int i = getShopItemById(id);
        if (i == -1) return -1;
        int count = getShopItemAmount(i);
        if (count <= 0) return -1;
        int e = getEmptySlots();
        if (count > e) count = e;
        buyShopItem(i, count);
        return random(1000, 2700);
    }
    
    private boolean _objectValid(int[] object) {
        return object[0] != -1 && distanceTo(object[1], object[2]) < 16;
    }
    
    private int _getBoundId(int x, int y) {
        return getWallObjectIdFromCoords(x, y);
    }
    
    private boolean insideShop() {
        return insideShop(getX(), getY());
    }
    
    private static boolean insideShop(int x, int y) {
        if (y > 503 && y < 509 && x > 365 && x < 371) {
            return true;
        }
        for (Point p : ext_shop_points) {
            if (x == p.x && y == p.y) {
                return true;
            }
        }
        return false;
    }
    
    private boolean _insideBank() {
        return _insideBank(getX(), getY());
    }
    
    private static boolean _insideBank(int x, int y) {
        return x > 327 && x < 335 && y > 548 && y < 558;
    }
    
    private String _getRuntime() {
        long secs = ((System.currentTimeMillis() - start_time) / 1000);
        if (secs >= 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 3600 && secs < 7200) {
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        }
        if (secs >= 60) {
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        }
        return secs + " secs.";
    }
}