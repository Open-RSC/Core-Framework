package orsc.graphics.gui;

public class KillAnnouncer {

	public int killPicture;
	public String killerString;
	long displayTime;
	private String killedString;

	public KillAnnouncer(String kill) {
		displayTime = System.currentTimeMillis();
	}

	public KillAnnouncer(String killer, String killed, int killType) {
		killPicture = killType;
		displayTime = System.currentTimeMillis();
	}

	public String getKilledString() {
		return killedString;
	}

	public void setKilledString(String killedString) {
		this.killedString = killedString;
	}
}