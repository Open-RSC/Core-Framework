package orsc.graphics.gui;

public class KillAnnouncer {
	
	public String killString,killerString,killedString;
	public int killPicture;
	public long displayTime;
	
	public KillAnnouncer(String kill) {
		killString = kill;
		displayTime = System.currentTimeMillis();
	}

	public KillAnnouncer(String killer, String killed, int killType) {
		killerString = killer;
		killedString = killed;
		killPicture = killType;
		displayTime = System.currentTimeMillis();
	}
}
