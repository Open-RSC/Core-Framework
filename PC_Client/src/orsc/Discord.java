package orsc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Discord {

	public static DiscordEventHandlers discord;

	public static final String APPLICATION_ID = "811783536914333747";

	public static boolean startedDiscord = false;

	private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	private static final Runnable presenceTask = new PresenceCheck();
	private static final Runnable discordTask = new DiscordUpdate();
	private static ScheduledFuture scheduled;
	private static String lastUpdate = "Open source RSC MMO";

	public static void setInUse(final boolean inuse) {
		try {
			Files.write(Paths.get(Config.F_CACHE_DIR + File.separator + "discord_inuse.txt"), (inuse ? "1" : "0").getBytes());
		} catch (Exception e) {
		}
	}

	public static boolean getInUse() {
		try {
			final String read = Files.readAllLines(Paths.get(Config.F_CACHE_DIR + File.separator + "discord_inuse.txt")).get(0);
			return read.equals("1");
		} catch (Exception e) {
			setInUse(true);
		}
		return false;
	}

	static class PresenceCheck implements Runnable {
		public void run() {
			// discord natives not in use and have not started discord
			if (!startedDiscord && !getInUse()) {
				Runtime.getRuntime().addShutdownHook(new Thread(() -> {
					System.out.println("Closing Discord hook.");
					DiscordRPC.discordShutdown();
					setInUse(false);
				}));
				System.out.println("Starting discord rich presence.");
				discord = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {
					System.out.println("Established discord rich presence.");
					// DiscordRPC.discordRunCallbacks();
				}).build();
				setInUse(true);
				DiscordRPC.discordInitialize(APPLICATION_ID, discord, false);
				DiscordRPC.discordRegister(APPLICATION_ID, "");
				scheduledExecutorService.scheduleAtFixedRate(discordTask, 0L, 5L, TimeUnit.SECONDS);
				startedDiscord = true;
				if (!scheduled.isCancelled()) {
					System.out.println("Discord detection finished.");
					scheduled.cancel(false);
				}
			}
		}
	}

	public static void InitalizeDiscord() {
		// users may (likely) have multiple instances of the game open at once
		// so we have to run a timer task to only have one initialized at a time
		// if that instance later gets closed, one of the other instances
		// will then check to initialize
		// this is done per 15 secs
		scheduled = scheduledExecutorService.scheduleAtFixedRate(presenceTask, 0L, 15L, TimeUnit.SECONDS);
	}

	static class DiscordUpdate implements Runnable {
		public void run() {
			DiscordRPC.discordRunCallbacks();
			DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(lastUpdate);
			presence.setBigImage("openrsc_logo", "Check out rsc.vet!");
			DiscordRPC.discordUpdatePresence(presence.build());
			// This will be the default message if the player hasn't done anything
			// since the last update.
			setLastUpdate("Adventuring");
		}
	}

	public static void setLastUpdate(String update) {
		lastUpdate = update;
	}
}
