public final class S_NatureChest extends Script {
    
    private static final int ID_CHEST_EMPTY = 340;
    private static final int ID_CHEST_FULL = 335;
    private static final int ID_BED = 14;
    private long move_time;
    private long fake_attempt;

    public S_NatureChest(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        move_time = -1L;
        fake_attempt = -1L;
    }

    @Override
    public int main() {
        if (getFatigue() > 90) {
            int[] bed = getObjectById(ID_BED);
            if (bed[0] != -1 && distanceTo(bed[1], bed[2]) < 7) {
                atObject(bed[1], bed[2]);
            } else {
                useSleepingBag();
            }
            return random(2000, 3000);
        }
        if (move_time != -1L) {
            if (System.currentTimeMillis() >= move_time) {
                System.out.println("Moving for 5 min timer");
                myWalkApprox(getX(), getY());
                move_time = -1L;
                return random(2500, 4000);
            }
            return 0;
        }
        int[] actual = getObjectById(ID_CHEST_FULL);
        if (actual[0] != -1) {
            atObject2(actual[1], actual[2]);
        } else {
            long cur_time = System.currentTimeMillis();
            if (cur_time >= fake_attempt) {
                int[] fake = getObjectById(ID_CHEST_EMPTY);
                if (fake[0] != -1) {
                    atObject2(fake[1], fake[2]);
                    fake_attempt = cur_time + random(3000, 10000);
                }
            }
        }
        // from abyte's script, agreed value
        return random(random(126, 567), 1142);
    }

    @Override
    public void paint() {
    }
    
    @Override
    public void onServerMessage(String str) {
        if (str.contains("standing here")) {
            move_time = (System.currentTimeMillis() + random(1500, 1800));
            return;
        }
    }
    
    private void myWalkApprox(int x, int y) {
        int dx, dy;
        int loop = 0;
        do {
            dx = x + random(-1, 1);
            dy = y + random(-1, 1);
            if ((++loop) > 100) return;
        } while (!isReachable(dx, dy));
        walkTo(dx, dy);
    }
}
