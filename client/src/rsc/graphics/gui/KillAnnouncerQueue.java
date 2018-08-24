package rsc.graphics.gui;

import java.util.LinkedList;

public class KillAnnouncerQueue {

	public LinkedList<KillAnnouncer> Kill = new LinkedList<KillAnnouncer>();

	public void addKill(KillAnnouncer kill) {
		try {
			Kill.addFirst(kill);
			if(Kill.size() >= 10) {
				Kill.removeLast();

			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void clean() {
		try {
			for (KillAnnouncer k : Kill) {
				if (System.currentTimeMillis() - k.displayTime > 8000) {
					Kill.remove(k);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

