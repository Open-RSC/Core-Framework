package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.content.clan.ClanManager;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.custom.MonitoringEvent;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.DiscordService;
import com.openrsc.server.net.RSCConnectionHandler;
import com.openrsc.server.net.RSCProtocolDecoder;
import com.openrsc.server.net.RSCProtocolEncoder;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.util.NamedThreadFactory;
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
	private static Server server = null;

	private final GameStateUpdater gameUpdater;
	private final GameTickEventHandler tickEventHandler;
	private final DiscordService discordService;
	private final GameTickEvent monitoring;
	private final PlayerDatabaseExecutor playerDataProcessor;
	private final ServerConfiguration config;
	private final ScheduledExecutorService scheduledExecutor;
	private final World world;

	private DelayedEvent updateEvent;
	private ChannelFuture serverChannel;

	private boolean running = false;
	private boolean initialized = false;
	private long lastIncomingPacketsDuration = 0;
	private long lastGameStateDuration = 0;
	private long lastEventsDuration = 0;
	private long lastOutgoingPacketsDuration = 0;
	private long lastTickDuration = 0;
	private long timeLate = 0;
	private long lastClientUpdate = 0;

	private String name;

	private Constants constants;

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

	public Server (String configFile) throws IOException{
		config = new ServerConfiguration();
		getConfig().initConfig(configFile);
		LOGGER.info("Server configuration loaded");

		name = getConfig().SERVER_NAME;

		constants = new Constants(this);
		discordService = new DiscordService(this);
		playerDataProcessor = new PlayerDatabaseExecutor(this);
		world = new World(this);
		tickEventHandler = new GameTickEventHandler(this);
		gameUpdater = new GameStateUpdater(this);
		monitoring = new MonitoringEvent();
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getName()+" : GameThread").build());
	}

	public static void main(String[] args) throws IOException {
		LOGGER.info("Launching Game Server...");
		if (args.length == 0) {
			LOGGER.info("Server Configuration file not provided. Loading from default.conf or local.conf.");
			server = new Server("default.conf");
		} else {
			server = new Server(args[0]);
			if (getServer().getConfig().DEBUG) {
				LOGGER.info("Server Configuration file: " + args[0]);
				LOGGER.info("\t Game Tick Cycle: {}", box(getServer().getConfig().GAME_TICK));
				LOGGER.info("\t Client Version: {}", box(getServer().getConfig().CLIENT_VERSION));
				LOGGER.info("\t Server type: " + (getServer().getConfig().MEMBER_WORLD ? "MEMBER" : "FREE" + " world."));
				LOGGER.info("\t Combat Experience Rate: {}", box(getServer().getConfig().COMBAT_EXP_RATE));
				LOGGER.info("\t Skilling Experience Rate: {}", box(getServer().getConfig().SKILLING_EXP_RATE));
				LOGGER.info("\t Wilderness Experience Boost: {}", box(getServer().getConfig().WILDERNESS_BOOST));
				LOGGER.info("\t Skull Experience Boost: {}", box(getServer().getConfig().SKULL_BOOST));
				LOGGER.info("\t Double experience: " + (getServer().getConfig().IS_DOUBLE_EXP ? "Enabled" : "Disabled"));
				LOGGER.info("\t View Distance: {}", box(getServer().getConfig().VIEW_DISTANCE));
			}
		}

		try {
			if(!server.isRunning()) {
				server.start();
			}
		} catch (Throwable t) {
			LOGGER.catching(t);
		}
	}

	public static Server getServer() {
		return server;
	}

	public PlayerDatabaseExecutor getPlayerDataProcessor() {
		return playerDataProcessor;
	}

	private void initialize() {
		try {
			LOGGER.info("Creating database connection...");
			DatabaseConnection.getDatabase();
			LOGGER.info("\t Database connection created");

			LOGGER.info("Loading game logging manager...");
			GameLogging.load(this);
			LOGGER.info("\t Logging Manager Completed");

			LOGGER.info("Loading Plugins...");
			PluginHandler.getPluginHandler().initPlugins();
			LOGGER.info("\t Plugins Completed");

			LOGGER.info("Loading Combat Scripts...");
			CombatScriptLoader.init();
			LOGGER.info("\t Combat Scripts Completed");

			LOGGER.info("Loading World...");
			getWorld().load();
			LOGGER.info("\t World Completed");

			LOGGER.info("Loading profiling monitoring...");
			// Send monitoring info as a game event so that it can be profiled.
			getGameEventHandler().add(monitoring);
			LOGGER.info("Profiling Completed");

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
				serverChannel = bootstrap.bind(new InetSocketAddress(getServer().getConfig().SERVER_PORT)).sync();
				LOGGER.info("Game world is now online on port {}!", box(getServer().getConfig().SERVER_PORT));
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}

			initialized = true;

		} catch (Throwable t) {
			LOGGER.catching(t);
			System.exit(1);
		}

		lastClientUpdate = System.currentTimeMillis();
	}

	public void kill() {
		stop();
		LOGGER.fatal(getServer().getConfig().SERVER_NAME + " shutting down...");
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
			timeLate = System.currentTimeMillis() - lastClientUpdate - getServer().getConfig().GAME_TICK;
			if (getTimeLate() >= 0) {
				lastClientUpdate += getServer().getConfig().GAME_TICK;

				// Doing the set in two stages here such that the whole tick has access to the same values for profiling information.
				final long tickStart = System.currentTimeMillis();
				final long lastIncomingPacketsDuration = processIncomingPackets();
				final long lastEventsDuration = runGameEvents();
				final long lastGameStateDuration = runGameStateUpdate();
				final long lastOutgoingPacketsDuration = processOutgoingPackets();
				final long tickEnd = System.currentTimeMillis();
				final long lastTickDuration = tickEnd - tickStart;

				this.lastIncomingPacketsDuration = lastIncomingPacketsDuration;
				this.lastEventsDuration = lastEventsDuration;
				this.lastGameStateDuration = lastGameStateDuration;
				this.lastOutgoingPacketsDuration = lastOutgoingPacketsDuration;
				this.lastTickDuration = lastTickDuration;
			}
			else {
				if(getServer().getConfig().WANT_CUSTOM_WALK_SPEED) {
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

	protected final long runGameEvents() {
		return getGameEventHandler().doGameEvents();
	}

	protected final long runGameStateUpdate() throws Exception {
		return getGameUpdater().doUpdates();
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
			"Tick: " + getServer().getConfig().GAME_TICK + "ms, Server: " + getLastTickDuration() + "ms " + getLastIncomingPacketsDuration() + "ms " + getLastEventsDuration() + "ms " + getLastGameStateDuration() + "ms " + getLastOutgoingPacketsDuration() + "ms" + newLine +
			"Game Updater: " + getGameUpdater().getLastProcessPlayersDuration() + "ms " + getGameUpdater().getLastProcessNpcsDuration() + "ms " + getGameUpdater().getLastProcessMessageQueuesDuration() + "ms " + getGameUpdater().getLastUpdateClientsDuration() + "ms " + getGameUpdater().getLastDoCleanupDuration() + "ms " + getGameUpdater().getLastExecuteWalkToActionsDuration() + "ms " + newLine +
			"Events: " + countEvents + ", NPCs: " + World.getWorld().getNpcs().size() + ", Players: " + World.getWorld().getPlayers().size() + ", Shops: " + World.getWorld().getShops().size() + newLine +
			/*"Player Atk Map: " + World.getWorld().getPlayersUnderAttack().size() + ", NPC Atk Map: " + World.getWorld().getNpcsUnderAttack().size() + ", Quests: " + World.getWorld().getQuests().size() + ", Mini Games: " + World.getWorld().getMiniGames().size() + newLine +*/
			s;
	}

	public void start() {
		if(!isInitialized()) {
			initialize();
		}

		running = true;
		scheduledExecutor.scheduleAtFixedRate(this, 0, 1, TimeUnit.MILLISECONDS);
		playerDataProcessor.start();
		discordService.start();
	}

	public void stop() {
		running = false;
		scheduledExecutor.shutdown();
		discordService.stop();
		playerDataProcessor.stop();
	}

	public final long getLastGameStateDuration() {
		return lastGameStateDuration;
	}

	public final long getLastEventsDuration() {
		return lastEventsDuration;
	}

	public final long getLastTickDuration() {
		return lastTickDuration;
	}

	public final GameStateUpdater getGameUpdater() {
		return gameUpdater;
	}

	public final DiscordService getDiscordService() {
		return discordService;
	}

	public final long getLastIncomingPacketsDuration() {
		return lastIncomingPacketsDuration;
	}

	public final long getLastOutgoingPacketsDuration() {
		return lastOutgoingPacketsDuration;
	}

	public final long getTimeLate() {
		return timeLate;
	}

	public void skipTicks(final long ticks) {
		lastClientUpdate += ticks * getServer().getConfig().GAME_TICK;
	}

	public final ServerConfiguration getConfig() {
		return config;
	}

	public final boolean isInitialized() {
		return initialized;
	}

	public final boolean isRunning() {
		return running;
	}

	public final Constants getConstants() {
		return constants;
	}

	public World getWorld() {
		return world;
	}

	public String getName() {
		return name;
	}
}
