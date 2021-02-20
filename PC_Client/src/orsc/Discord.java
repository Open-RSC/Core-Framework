package orsc;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class Discord {

	public static DiscordEventHandlers discord;

	public static final String APPLICATION_ID = "811783536914333747";

	public static boolean startedDiscord = false;

	public static void setInUse(boolean inuse) {
		try {
			FileOutputStream fileout = new FileOutputStream(Config.F_CACHE_DIR + File.separator + "discord_inuse.txt");

			OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
			outputWriter.write("" + (inuse ? "1" : "0"));
			outputWriter.close();
		} catch (Exception e) {
		}
	}

	public static boolean getInUse() {
		try {
			FileInputStream in = new FileInputStream(Config.F_CACHE_DIR + File.separator + "discord_inuse.txt");
			InputStreamReader inputStreamReader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			return sb.toString().equals("1");
		} catch (Exception e) {
			setInUse(true);
		}
		return false;
	}

	static class PresenceCheck extends TimerTask {
		public synchronized void run() {
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
				Timer timer = new Timer();
				timer.schedule(new DiscordUpdate(), 0, 5000);
				startedDiscord = true;
			}
		}
	}

	public static void InitalizeDiscord() {
		Timer timer = new Timer();
		// users may (likely) have multiple instances of the game open at once
		// so we have to run a timer task to only have one initialized at a time
		// if that instance later gets closed, one of the other instances
		// will then check to initialize
		// this is done per 15 secs
		timer.schedule(new PresenceCheck(), 0, 15000);
	}

	static class DiscordUpdate extends TimerTask {
		public void run() {
			DiscordRPC.discordRunCallbacks();
			DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder("Open source RSC MMO");
			presence.setBigImage("openrsc_logo", "Check out rsc.vet!");
			DiscordRPC.discordUpdatePresence(presence.build());
		}
	}
}
