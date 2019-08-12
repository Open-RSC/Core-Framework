package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.content.clan.ClanManager;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.DiscordSender;
import com.openrsc.server.net.RSCConnectionHandler;
import com.openrsc.server.net.RSCProtocolDecoder;
import com.openrsc.server.net.RSCProtocolEncoder;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.util.NamedThreadFactory;
import com.openrsc.server.util.rsc.MessageType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.logging.log4j.util.Unbox.box;

public final class Server implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER;
	private static PlayerDatabaseExecutor playerDataProcessor;
	private static Server server = null;

	static {
		try {
			Thread.currentThread().setName("InitializationThread");
			System.setProperty("log4j.configurationFile", "conf/server/log4j2.xml");
			/* Enables asynchronous, garbage-free logging. */
			System.setProperty("Log4jContextSelector",
					"org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

			LOGGER = LogManager.getLogger();
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	private final ScheduledExecutorService scheduledExecutor = Executors
		.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("GameThread").build());
	private final GameStateUpdater gameUpdater = new GameStateUpdater();
	private final GameTickEventHandler tickEventHandler = new GameTickEventHandler();
	private final DiscordSender discordSender = new DiscordSender();
	private long lastClientUpdate;
	private boolean running;
	private DelayedEvent updateEvent;
	private ChannelFuture serverChannel;

	private long lastIncomingPacketsDuration	= 0;
	private long lastGameStateDuration			= 0;
	private long lastEventsDuration				= 0;
	private long lastOutgoingPacketsDuration	= 0;
	private long lastTickDuration				= 0;

	public Server() {
		running = true;
		playerDataProcessor = new PlayerDatabaseExecutor();
	}

	public static void main(String[] args) throws IOException {
		LOGGER.info("Launching Game Server...");
		if (args.length == 0) {
			LOGGER.info("Server Configuration file not provided. Loading from default.conf or local.conf.");
			Constants.GameServer.initConfig("default.conf");
		} else {
			Constants.GameServer.initConfig(args[0]);
			if (Constants.GameServer.DEBUG) {
				LOGGER.info("Server Configuration file: " + args[0]);
				LOGGER.info("\t Game Tick Cycle: {}", box(Constants.GameServer.GAME_TICK));
				LOGGER.info("\t Client Version: {}", box(Constants.GameServer.CLIENT_VERSION));
				LOGGER.info("\t Server type: " + (Constants.GameServer.MEMBER_WORLD ? "MEMBER" : "FREE" + " world."));
				LOGGER.info("\t Combat Experience Rate: {}", box(Constants.GameServer.COMBAT_EXP_RATE));
				LOGGER.info("\t Skilling Experience Rate: {}", box(Constants.GameServer.SKILLING_EXP_RATE));
				LOGGER.info("\t Wilderness Experience Boost: {}", box(Constants.GameServer.WILDERNESS_BOOST));
				LOGGER.info("\t Skull Experience Boost: {}", box(Constants.GameServer.SKULL_BOOST));
				LOGGER.info("\t Double experience: " + (Constants.GameServer.IS_DOUBLE_EXP ? "Enabled" : "Disabled"));
				LOGGER.info("\t View Distance: {}", box(Constants.GameServer.VIEW_DISTANCE));
			}
		}
		if (server == null) {
			try {
				server = new Server();
				server.initialize();
				server.start();
			} catch (Throwable t) {
				LOGGER.catching(t);
			}

		}
	}

	public static Server getServer() {
		return server;
	}

	public static PlayerDatabaseExecutor getPlayerDataProcessor() {
		return playerDataProcessor;
	}

	private void initialize() {
		try {
			LOGGER.info("Creating database connection...");
			DatabaseConnection.getDatabase();
			LOGGER.info("\t Database connection created");

			LOGGER.info("Loading game logging manager...");
			GameLogging.load();
			LOGGER.info("\t Logging Manager Completed");

			LOGGER.info("Loading Plugins...");
			PluginHandler.getPluginHandler().initPlugins();
			LOGGER.info("\t Plugins Completed");

			LOGGER.info("Loading Combat Scripts...");
			CombatScriptLoader.init();
			LOGGER.info("\t Combat Scripts Completed");

			LOGGER.info("Loading World...");
			World.getWorld().load();
			LOGGER.info("\t World Completed");

			LOGGER.info("Starting database loader...");
			playerDataProcessor.start();
			LOGGER.info("\t Database Loader Completed");
			//Never run ResourceLeakDetector PARANOID in production.
			//ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
			final EventLoopGroup bossGroup = new NioEventLoopGroup(0, new NamedThreadFactory("IOBossThread"));
			final EventLoopGroup workerGroup = new NioEventLoopGroup(0, new NamedThreadFactory("IOWorkerThread"));
			final ServerBootstrap bootstrap = new ServerBootstrap();

			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(final SocketChannel channel) throws Exception {
						final ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast("decoder", new RSCProtocolDecoder());
						pipeline.addLast("encoder", new RSCProtocolEncoder());
						pipeline.addLast("handler", new RSCConnectionHandler());

					}
				});

			bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
			bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);
			bootstrap.childOption(ChannelOption.SO_RCVBUF, 10000);
			bootstrap.childOption(ChannelOption.SO_SNDBUF, 10000);
			try {
				PluginHandler.getPluginHandler().handleAction("Startup", new Object[]{});
				serverChannel = bootstrap.bind(new InetSocketAddress(Constants.GameServer.SERVER_PORT)).sync();
				LOGGER.info("Game world is now online on port {}!", box(Constants.GameServer.SERVER_PORT));
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}

		} catch (Throwable t) {
			LOGGER.catching(t);
			System.exit(1);
		}

		lastClientUpdate = System.currentTimeMillis();
	}

	public boolean isRunning() {
		return running;
	}

	public void kill() {
		scheduledExecutor.shutdown();
		LOGGER.fatal(Constants.GameServer.SERVER_NAME + " shutting down...");
		running = false;
		System.exit(0);
	}

	public boolean shutdownForUpdate(int seconds) {
		if (updateEvent != null) {
			return false;
		}
		updateEvent = new SingleEvent(null, (seconds - 1) * 1000, "Shutdown for Update") {
			public void action() {
				unbind();
				saveAndShutdown();
			}
		};
		getGameEventHandler().add(updateEvent);
		return true;
	}

	private void saveAndShutdown() {
		ClanManager.saveClans();
		for (Player p : World.getWorld().getPlayers()) {
			p.unregister(true, "Server shutting down.");
		}

		SingleEvent up = new SingleEvent(null, 6000, "Save and Shutdown") {
			public void action() {
				kill();
				DatabaseConnection.getDatabase().close();
			}
		};
		getGameEventHandler().add(up);
	}

	public long timeTillShutdown() {
		if (updateEvent == null) {
			return -1;
		}
		return updateEvent.timeTillNextRun();
	}

	private void unbind() {
		try {
			serverChannel.channel().disconnect();
		} catch (Exception ignored) {
		}
	}

	public void run() {
	    try {
			long timeLate = System.currentTimeMillis() - lastClientUpdate - Constants.GameServer.GAME_TICK;
			if (timeLate >= 0) {
				lastClientUpdate 			+= Constants.GameServer.GAME_TICK;

				lastIncomingPacketsDuration	= processIncomingPackets();
				lastEventsDuration			= getGameEventHandler().doGameEvents();
				lastGameStateDuration		= getGameUpdater().doUpdates();
				lastOutgoingPacketsDuration	= processOutgoingPackets();

				lastTickDuration			= lastEventsDuration + lastGameStateDuration;

				final long ticksLate		= timeLate / Constants.GameServer.GAME_TICK;
				final boolean isServerLate	= ticksLate >= 1;

				// Processing game events and state took longer than the tick
				if(lastTickDuration >= Constants.GameServer.GAME_TICK) {
					final String message = "Can't keep up: " + getLastTickDuration() + "ms " + getLastEventsDuration() + "ms " + getLastGameStateDuration() + "ms";

					// Warn logged in developers
					for (Player p : World.getWorld().getPlayers()) {
						if(!p.isDev()) {
							continue;
						}

						p.playerServerMessage(MessageType.QUEST, Constants.GameServer.MESSAGE_PREFIX + message);
					}
				}

				// Server fell behind, skip ticks
				if (isServerLate) {
					lastClientUpdate 			+= ticksLate * Constants.GameServer.GAME_TICK;
					final String message		= "Can't keep up, we are " + timeLate + "ms behind; Skipping " + ticksLate + " ticks";

					// Warn logged in developers
					for (Player p : World.getWorld().getPlayers()) {
						if(!p.isDev()) {
							continue;
						}

						p.playerServerMessage(MessageType.QUEST, Constants.GameServer.MESSAGE_PREFIX + message);
					}

					getDiscordSender().monitoringSendServerBehind(message);

					if (Constants.GameServer.DEBUG) {
						LOGGER.warn(message);
					}
				}
			}
			else {
				if(Constants.GameServer.WANT_CUSTOM_WALK_SPEED) {
					for (Player p : World.getWorld().getPlayers()) {
						p.updatePosition();
					}

					for (Npc n : World.getWorld().getNpcs()) {
						n.updatePosition();
					}

					processOutgoingPackets();
				}
			}
		} catch (Throwable t) {
			LOGGER.catching(t);
		}
	}

	protected final long processIncomingPackets() {
		final long processPacketsStart	= System.currentTimeMillis();
		for (Player p : World.getWorld().getPlayers()) {
			p.processIncomingPackets();
		}
		final long processPacketsEnd	= System.currentTimeMillis();

		return processPacketsEnd - processPacketsStart;
	}

	protected long processOutgoingPackets() {
		final long processPacketsStart	= System.currentTimeMillis();
		for (Player p : World.getWorld().getPlayers()) {
			p.sendOutgoingPackets();
		}
		final long processPacketsEnd	= System.currentTimeMillis();

		return processPacketsEnd - processPacketsStart;
	}

	public GameTickEventHandler getGameEventHandler() {
		return tickEventHandler;
	}

	public void submitTask(Runnable r) {
		scheduledExecutor.submit(r);
	}

	public boolean restart(int seconds) {
		if (updateEvent != null) {
			return false;
		}
		updateEvent = new SingleEvent(null, (seconds - 1) * 1000, "Restart") {
			public void action() {
				unbind();
				//saveAndRestart();
				saveAndShutdown();
			}
		};
		getGameEventHandler().add(updateEvent);
		return true;
	}

	private void saveAndRestart() {
		ClanManager.saveClans();
		LOGGER.info("Saving players...");
		for (Player p : World.getWorld().getPlayers()) {
			p.unregister(true, "Server shutting down.");
			LOGGER.info("Players saved...");
		}

		SingleEvent up = new SingleEvent(null, 6000, "Save and Restart") {
			public void action() {
				LOGGER.info("Trying to run restart script...");
				try {
					// at this time, no successful method for guaranteed relaunch works so just use a cronjob instead
					Runtime.getRuntime().exec("./run_server.sh");
				} catch (IOException e) {
					LOGGER.catching(e);
				}
			}
		};
		getGameEventHandler().add(up);
	}

	public final String buildProfilingDebugInformation(boolean forInGame) {
		final HashMap<String, Integer> eventsCount 	= new HashMap<String, Integer>();
		final HashMap<String, Long> eventsDuration	= new HashMap<String, Long>();
		int countEvents								= 0;
		long durationEvents							= 0;
		String newLine								= forInGame ? "%" : "\r\n";

		// Show info for game tick events
		for (Map.Entry<String, GameTickEvent> eventEntry : getGameEventHandler().getEvents().entrySet()) {
			GameTickEvent e = eventEntry.getValue();
			String eventName = e.getDescriptor();
			//if (e.getOwner() != null && e.getOwner().isUnregistering()) {
			if (!eventsCount.containsKey(eventName)) {
				eventsCount.put(eventName, 1);
			} else {
				eventsCount.put(eventName, eventsCount.get(eventName) + 1);
			}

			if (!eventsDuration.containsKey(eventName)) {
				eventsDuration.put(eventName, e.getLastEventDuration());
			} else {
				eventsDuration.put(eventName, eventsDuration.get(eventName) + e.getLastEventDuration());
			}
			//}

			// Update Totals
			++countEvents;
			durationEvents	+= e.getLastEventDuration();
		}

		// Sort the Events Hashmap
		List list = new LinkedList(eventsDuration.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return
					String.format("%025d%025d", eventsDuration.get(( (Map.Entry) (o2) ).getKey()), eventsCount.get(( (Map.Entry) (o2) ).getKey()))
					.compareTo(String.format("%025d%025d", eventsDuration.get(( (Map.Entry) (o1) ).getKey()), eventsCount.get(( (Map.Entry) (o1) ).getKey())));
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		eventsDuration.clear();
		eventsDuration.putAll(sortedHashMap);

		int i = 0;
		StringBuilder s = new StringBuilder();
		for (Map.Entry<String, Long> entry : eventsDuration.entrySet()) {
			if(forInGame && i >= 17) // Only display first 17 elements of the hashmap
				break;

			String name		= entry.getKey();
			Long duration	= entry.getValue();
			Integer count	= eventsCount.get(entry.getKey());
			s.append(name).append(" : ").append(duration).append("ms").append(" : ").append(count).append(newLine);
			++i;
		}

		return
			"Tick: " + Constants.GameServer.GAME_TICK + "ms, Server: " + getLastTickDuration() + "ms " + getLastIncomingPacketsDuration() + "ms " + getLastEventsDuration() + "ms " + getLastGameStateDuration() + "ms " + getLastOutgoingPacketsDuration() + "ms" + newLine +
			"Game Updater: " + getGameUpdater().getLastProcessPlayersDuration() + "ms " + getGameUpdater().getLastProcessNpcsDuration() + "ms " + getGameUpdater().getLastProcessMessageQueuesDuration() + "ms " + getGameUpdater().getLastUpdateClientsDuration() + "ms " + getGameUpdater().getLastDoCleanupDuration() + "ms " + getGameUpdater().getLastExecuteWalkToActionsDuration() + "ms " + newLine +
			"Events: " + countEvents + ", NPCs: " + World.getWorld().getNpcs().size() + ", Players: " + World.getWorld().getPlayers().size() + ", Shops: " + World.getWorld().getShops().size() + newLine +
			/*"Player Atk Map: " + World.getWorld().getPlayersUnderAttack().size() + ", NPC Atk Map: " + World.getWorld().getNpcsUnderAttack().size() + ", Quests: " + World.getWorld().getQuests().size() + ", Mini Games: " + World.getWorld().getMiniGames().size() + newLine +*/
			s;
	}

	public void start() {
		scheduledExecutor.scheduleAtFixedRate(this, 0, 1, TimeUnit.MILLISECONDS);
	}

	public long getLastGameStateDuration() {
		return lastGameStateDuration;
	}

	public long getLastEventsDuration() {
		return lastEventsDuration;
	}

	public long getLastTickDuration() {
		return lastTickDuration;
	}

	public GameStateUpdater getGameUpdater() {
		return gameUpdater;
	}

	public DiscordSender getDiscordSender() {
		return discordSender;
	}

	public long getLastIncomingPacketsDuration() {
		return lastIncomingPacketsDuration;
	}

	public long getLastOutgoingPacketsDuration() {
		return lastOutgoingPacketsDuration;
	}
}
