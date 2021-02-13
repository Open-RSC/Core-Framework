import java.util.Locale;

public final class S_Idle extends Script {
    
    // I LIKE SITTING IN DRAYNOR LISTENING TO RSC PLAYERS HAVE INTELLIGENT DISCUSSIONS.

    private long move_time;
    private boolean idle_move_dir;

    public S_Idle(Extension ex) {
        super(ex);
    }

    @Override
    public void init(String params) {
        move_time = -1L;
    }

    @Override
    public int main() {
        if (move_time != -1L) {
            return _idleMove();
        }
        return 0;
    }
    
    private boolean _idleMoveP1() {
        int x = getX();
        int y = getY();
    	if (isReachable(x + 1, y)) {
    		walkTo(x + 1, y);
    		return true;
    	}
    	if (isReachable(x, y + 1)) {
    		walkTo(x, y + 1);
    		return true;
    	}
    	if (isReachable(x + 1, y + 1)) {
    		walkTo(x + 1, y + 1);
    		return true;
    	}
    	return false;
    }
    
    private boolean _idleMoveM1() {
        int x = getX();
        int y = getY();
    	if (isReachable(x - 1, y)) {
    		walkTo(x - 1, y);
    		return true;
    	}
    	if (isReachable(x, y - 1)) {
    		walkTo(x, y - 1);
    		return true;
    	}
    	if (isReachable(x - 1, y - 1)) {
    		walkTo(x - 1, y - 1);
    		return true;
    	}
    	return false;
    }
    
    private int _idleMove() {
    	if (System.currentTimeMillis() >= move_time) {
            System.out.println("Moving for 5 min timer");

            if (idle_move_dir) {
            	if (!_idleMoveP1()) {
            		_idleMoveM1();
            	}
            } else {
            	if (!_idleMoveM1()) {
            		_idleMoveP1();
            	}
            }
            idle_move_dir = !idle_move_dir;
            move_time = -1L;
            return random(1500, 2500);
        }
    	return 0;
    }
    
    @Override
    public void onServerMessage(String str) {
        if (str.toLowerCase(Locale.ENGLISH).contains("standing")) {
            move_time = System.currentTimeMillis() + random(800, 2500);
        }
    }
}