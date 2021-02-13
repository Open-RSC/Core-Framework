public final class S_DraynorPower extends Script {

    private static final int STAGE_FISH = 0;
    private static final int STAGE_COOK = 1;
    private static final int STAGE_DROP = 2;

    private static final int ID_FISHING_SPOT = 193;
    private static final int ID_TINDERBOX = 166;
    private static final int ID_FIRE = 97;
    private static final int ID_LOGS = 14;
    private static final int ID_TREE = 1;
    private static final int ID_BURNED = 353;
    private static final int ID_NET = 376;
    private static final int[] ids_raw = {
        351, 349
    };
    private static final int[] ids_cooked = {
        352, 350
    };
    private static final int[] ids_axe = {
        12, 87, 88, 203, 204, 405
    };

    private int stage;

    public S_DraynorPower(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        stage = STAGE_FISH;
    }

    @Override
    public int main() {
        if (inCombat()) {
            walkTo(getX(), getY());
            return random(300, 400);
        }
        if (getFatigue() > 80) {
            useSleepingBag();
            return random(1500, 2500);
        }
        switch (stage) {
            case STAGE_FISH: return doFish();
            case STAGE_COOK: return doCook();
            case STAGE_DROP: return doDrop();
        }
        return 0;
    }

    private int doFish() {
        if (getInventoryCount() == MAX_INV_SIZE) {
            stage = STAGE_COOK;
            return 0;
        }
        if (getInventoryIndex(ID_NET) == -1) {
            System.out.println("ERROR: No net!");
            setAutoLogin(false);
            stopScript(); return 0;
        }
        int fish_spot[] = getObjectById(ID_FISHING_SPOT);
        if (fish_spot[0] != -1) {
            if (distanceTo(fish_spot[1], fish_spot[2]) > 3) {
                if (!isWalking()) {
                    walkTo(221 + random(-1, 1), 661 + random(-1, 1));
                }
                return random(800, 1200);
            }
            atObject(fish_spot[1], fish_spot[2]);
            return random(600, 800);
        }
        return 0;
    }

    private int doCook() {
        int id = -1;
        for (int i = 0; i < ids_raw.length; ++i) {
            if (getInventoryCount(ids_raw[i]) > 0) {
                id = ids_raw[i];    
                break;
            }
        }
        if (id == -1) {
            stage = STAGE_DROP;
            return 0;
        }
        int x = getX();
        int y = getY();
        if (getObjectIdFromCoords(x, y) == ID_FIRE) {
            useItemOnObject(id, x, y);
            return random(600, 800);
        }
        int logs[] = getItemById(ID_LOGS);
        if (logs[1] == x && logs[2] == y) {
            int box_slot = getInventoryIndex(ID_TINDERBOX);
            if (box_slot == -1) {
                System.out.println("ERROR: No tinderbox!");
                setAutoLogin(false);
                stopScript(); return 0;
            }
            useItemOnGroundItem(box_slot, ID_LOGS, x, y);
            return random(600, 800);
        }
        if (getX() > 217) {
            if (!isWalking()) {
                walkTo(215 + random(-1, 1), 661 + random(-1, 1));
            }
            return random(800, 1200);
        }
        if (getInventoryIndex(ids_axe) == -1) {
            System.out.println("ERROR: No axe!");
            setAutoLogin(false);
            stopScript(); return 0;
        }
        int tree[] = getObjectById(ID_TREE);
        if (distanceTo(tree[1], tree[2]) < 5) {
            atObject(tree[1], tree[2]);
            return random(1000, 1500);
        }
        return random(5, 10);
    }

    private int doDrop() {
        int item = getInventoryIndex(ids_cooked);
        if (item != -1) {
            useItem(item);
            return random(600, 800);
        }
        item = getInventoryIndex(ID_BURNED);
        if (item != -1) {
            dropItem(item);
            return random(600, 800);
        }
        stage = STAGE_FISH;
        return 0;
    }
    
    @Override
    public String toString() {
        return "Stormy's Draynor Power Shrimp Fisher+Cooker (cut + firemake)";
    }
}