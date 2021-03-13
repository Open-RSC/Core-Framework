package orsc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class Discord {

	/**
	 * When there is a wrapper for discord presence this class would be equivalent to the PC
	 * version
	 * */

	public static final String APPLICATION_ID = "811783536914333747";

	public static boolean startedDiscord = false;

	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private static ScheduledFuture scheduled;
	private static String lastUpdate = "Open source RSC MMO";

	public static void setLastUpdate(String update) {
		lastUpdate = update;
	}
}
