public final class S_LowWall extends Script {
    
    // thx blood
    
    private int[][] walls = {
        { 495, 559 },
        { 495, 558 }
    };
    private int wall = 0;
    private long start_time;

    public S_LowWall(Extension ex) {
        super(ex);
    }
    
    @Override
    public void init(String params) {
        start_time = -1L;
    }

    @Override
    public int main() {
        if (start_time == -1L) {
            start_time = System.currentTimeMillis();
        }
        if (getFatigue() >= 90) {
            useSleepingBag();
            return random(1500, 2500);
        }
        if (!isWalking()) {
            int[] w = walls[wall];
            atWallObject(w[0], w[1]);
        }
        return random(600, 800);
    }
    
    @Override
    public void paint() {
        drawString("Runtime: " + _getRuntime(), 25, 25, 1, 0xFFFFFF);
    }
    
    private String _getRuntime() {
        long secs = (System.currentTimeMillis() - start_time) / 1000L;
        if (secs >= 7200)
            return (secs / 3600) + " hours, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        if (secs >= 3600 && secs < 7200)
            return (secs / 3600) + " hour, " +
                    ((secs % 3600) / 60) + " mins, " +
                    (secs % 60) + " secs.";
        if (secs >= 60)
            return secs / 60 + " mins, " +
                    (secs % 60) + " secs.";
        return secs + " secs.";
    }

    @Override
    public void onServerMessage(String str) {
        if (str.contains("standing here")) {
            wall = (wall == 0) ? 1 : 0;
        }
    }
}