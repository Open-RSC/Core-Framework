package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.content.achievement.AchievementSystem;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.database.impl.mysql.MySqlGameDatabase;
import com.openrsc.server.database.impl.mysql.MySqlGameLogger;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.*;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.Crypto;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.NamedThreadFactory;
import com.openrsc.server.util.rsc.CaptchaGenerator;
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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.logging.log4j.util.Unbox.box;

public class Server implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER;

	public static final ConcurrentHashMap<String, Server> serversList = new ConcurrentHashMap<>();

	private final GameStateUpdater gameUpdater;
	private final GameEventHandler gameEventHandler;
	private final DiscordService discordService;
	private final LoginExecutor loginExecutor;
	private final ServerConfiguration config;
	private ScheduledExecutorService scheduledExecutor;
	private final PluginHandler pluginHandler;
	private final CombatScriptLoader combatScriptLoader;
	private final EntityHandler entityHandler;
	private final MySqlGameLogger gameLogger;
	private final GameDatabase database;
	private final AchievementSystem achievementSystem;
	private final Constants constants;
	private final RSCPacketFilter packetFilter;

	private final World world;
	private final String name;

	private GameTickEvent shutdownEvent;
	private ChannelFuture serverChannel;
	private EventLoopGroup workerGroup;
	private EventLoopGroup bossGroup;

	private volatile Boolean running = false;
	private boolean restarting = false;
	private boolean shuttingDown = false;

	private long serverStartedTime = 0;
	private long lastIncomingPacketsDuration = 0;
	private long lastGameStateDuration = 0;
	private long lastEventsDuration = 0;
	private long lastOutgoingPacketsDuration = 0;
	private long lastTickDuration = 0;
	private long timeLate = 0;
	private long lastTickTimestamp = 0;
	private final HashMap<Integer, Long> incomingTimePerPacketOpcode = new HashMap<>();
	private final HashMap<Integer, Integer> incomingCountPerPacketOpcode = new HashMap<>();
	private final HashMap<Integer, Long> outgoingTimePerPacketOpcode = new HashMap<>();
	private final HashMap<Integer, Integer> outgoingCountPerPacketOpcode = new HashMap<>();
	private int privateMessagesSent = 0;

	private volatile int maxItemId;

	static {
		try {
			Thread.currentThread().setName("InitThread");
			// Enables asynchronous, garbage-free logging.
			System.setProperty("log4j.configurationFile", "conf/server/log4j2.xml");
			System.setProperty("Log4jContextSelector",
				"org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

			LOGGER = LogManager.getLogger();
		} catch (final Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static final Server startServer(final String confName) throws IOException {
		final long startTime = System.currentTimeMillis();
		final Server server = new Server(confName);
		if (!server.isRunning()) {
			server.start();
		}
		final long endTime = System.currentTimeMillis();
		final long bootTime = endTime - startTime;
		LOGGER.info(server.getName() + " started in " + bootTime + "ms");

		return server;
	}

	public static boolean closeProcess(final int seconds, final String message) {
		for (final Server server : serversList.values()) {
			if (server.shutdownEvent != null) {
				return false;
			}
		}

		for (final Server server : serversList.values()) {
			if (message != null) {
				for (final Player playerToUpdate : server.getWorld().getPlayers()) {
					ActionSender.sendBox(playerToUpdate, message, false);
				}
			}

			server.shutdown(seconds);
		}

		return true;
	}

	public static void main(final String[] args) {
		LOGGER.info("Launching Game Server...");

		if (args.length == 0) {
			LOGGER.info("Server Configuration file not provided. Loading from default.conf or local.conf.");

			try {
				startServer("default.conf");
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		} else {
			for (int i = 0; i < args.length; i++) {
				try {
					startServer(args[i]);
				} catch (final Throwable t) {
					LOGGER.catching(t);
				}
			}
		}

		while (serversList.size() > 0) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) { }

			for (final Server server : serversList.values()) {
				server.checkShutdown();
			}
		}

		LOGGER.info("Exiting server process...");
		System.exit(0);
	}

	public Server(final String configFile) throws IOException {
		config = new ServerConfiguration();
		getConfig().initConfig(configFile);
		LOGGER.info("Server configuration loaded: " + getConfig().configFile);

		name = getConfig().SERVER_NAME;

		packetFilter = new RSCPacketFilter(this);

		pluginHandler = new PluginHandler(this);
		combatScriptLoader = new CombatScriptLoader(this);
		constants = new Constants(this);
		switch (getConfig().DB_TYPE){
			case MYSQL:
				database = new MySqlGameDatabase(this);
				break;
			default:
				database = null;
				LOGGER.error("No database type");
				System.exit(1);
				break;
		}

		final boolean wantDiscordBot = getConfig().WANT_DISCORD_BOT;
		final boolean wantDiscordAuctionUpdates = getConfig().WANT_DISCORD_AUCTION_UPDATES;
		final boolean wantDiscordMonitoringUpdates = getConfig().WANT_DISCORD_MONITORING_UPDATES;
		discordService = wantDiscordBot || wantDiscordAuctionUpdates || wantDiscordMonitoringUpdates ? new DiscordService(this) : null;
		loginExecutor = new LoginExecutor(this);
		world = new World(this);
		gameEventHandler = new GameEventHandler(this);
		gameUpdater = new GameStateUpdater(this);
		gameLogger = new MySqlGameLogger(this, (MySqlGameDatabase)database);
		entityHandler = new EntityHandler(this);
		achievementSystem = new AchievementSystem(this);

		maxItemId = 0;
	}

	public void checkShutdown() {
		if (isShuttingDown()) {
			stop();
			if (isRestarting()) {
				start();
				restarting = false;
			}
			shuttingDown = false;
		}
	}

	public void start() {
		synchronized (running) {
			try {
				if (isRunning()) {
					return;
				}

				scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getName() + " : GameThread").build());
				scheduledExecutor.scheduleAtFixedRate(this, 0, 10, TimeUnit.MILLISECONDS);

				// Do not allow two servers to be started with the same name
				// We will bypass that if we are restarting because we never removed this server from the list.
				if (!isRestarting() && serversList.get(this.getName()) != null) {
					throw new IllegalArgumentException("Can not initialize. Server " + this.getName() + " already exists.");
				}

				LOGGER.info("Connecting to Database...");
				try {
					getDatabase().open();
				} catch (final Exception ex) {
					LOGGER.catching(ex);
					System.exit(1);
				}
				LOGGER.info("Database Connection Completed");

				LOGGER.info("Checking For Database Structure Changes...");
				if (checkForDatabaseStructureChanges()) {
					LOGGER.info("Database Structure Changes Good");
				} else {
					LOGGER.error("Unable to change database structure!");
					System.exit(1);
				}

				LOGGER.info("Loading Prerendered Sleepword Images...");
                CaptchaGenerator.loadPrerenderedCaptchas();
                LOGGER.info("Loaded " + CaptchaGenerator.prerenderedSleepwordsSize + " Prerendered Sleepword Images");

				LOGGER.info("Loading Game Definitions...");
				getEntityHandler().load();
				LOGGER.info("Definitions Completed");

				LOGGER.info("Loading Game State Updater...");
				getGameUpdater().load();
				LOGGER.info("Game State Updater Completed");

				LOGGER.info("Loading Game Event Handler...");
				getGameEventHandler().load();
				LOGGER.info("Game Event Handler Completed");

				LOGGER.info("Loading Plugins...");
				getPluginHandler().load();
				LOGGER.info("Plugins Completed");

				LOGGER.info("Loading Combat Scripts...");
				getCombatScriptLoader().load();
				LOGGER.info("Combat Scripts Completed");

				LOGGER.info("Loading World...");
				getWorld().load();
				LOGGER.info("World Completed");

				/*LOGGER.info("Loading Achievements...");
				getAchievementSystem().load();
				LOGGER.info("Achievements Completed");*/

				LOGGER.info("Loading LoginExecutor...");
				getLoginExecutor().start();
				LOGGER.info("LoginExecutor Completed");

				if (getDiscordService() != null) {
					LOGGER.info("Loading DiscordService...");
					getDiscordService().start();
					LOGGER.info("DiscordService Completed");
				}

				LOGGER.info("Loading GameLogger...");
				getGameLogger().start();
				LOGGER.info("GameLogger Completed");

				LOGGER.info("Loading Packet Filter...");
				getPacketFilter().load();
				LOGGER.info("Packet Filter Completed");

                Crypto.init();

				maxItemId = getDatabase().getMaxItemID();
				LOGGER.info("Set max item ID to : " + maxItemId);

				bossGroup = new NioEventLoopGroup(0, new NamedThreadFactory(getName() + " : IOBossThread"));
				workerGroup = new NioEventLoopGroup(0, new NamedThreadFactory(getName() + " : IOWorkerThread"));
				final ServerBootstrap bootstrap = new ServerBootstrap();
				final Server serverOwner = this;

				bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
					new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(final SocketChannel channel) {
							final ChannelPipeline pipeline = channel.pipeline();
							pipeline.addLast("decoder", new RSCProtocolDecoder());
							pipeline.addLast("encoder", new RSCProtocolEncoder());
							pipeline.addLast("handler", new RSCConnectionHandler(serverOwner));
						}
					}
				);

				bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
				bootstrap.childOption(ChannelOption.SO_KEEPALIVE, false);
				bootstrap.childOption(ChannelOption.SO_RCVBUF, 10000);
				bootstrap.childOption(ChannelOption.SO_SNDBUF, 10000);
				try {
					getPluginHandler().handlePlugin(getWorld(), "Startup", new Object[]{});
					serverChannel = bootstrap.bind(new InetSocketAddress(getConfig().SERVER_PORT)).sync();
					LOGGER.info("Game world is now online on port {}!", box(getConfig().SERVER_PORT));
                    LOGGER.info("RSA exponent: " + Crypto.getPublicExponent());
                    LOGGER.info("RSA modulus: " + Crypto.getPublicModulus());
				} catch (final InterruptedException e) {
					LOGGER.catching(e);
				}

				// Only add this server to the active servers list if it's not already there
				if (!isRestarting()) {
					serversList.put(this.getName(), this);
				}

				lastTickTimestamp = serverStartedTime = System.currentTimeMillis();
				running = true;
			} catch (final Throwable t) {
				LOGGER.catching(t);
				System.exit(1);
			}
		}
	}

	public void stop() {
		synchronized (running) {
			try {
				if (!isRunning()) {
					return;
				}

				scheduledExecutor.shutdown();
				final boolean terminationResult = scheduledExecutor.awaitTermination(1, TimeUnit.MINUTES);
				if (!terminationResult) {
					throw new Exception("Server thread termination failed");
				}
				getLoginExecutor().stop();
				if (getDiscordService() != null) {
					getDiscordService().stop();
				}
				getGameLogger().stop();
				getGameUpdater().unload();
				getGameEventHandler().unload();
				getEntityHandler().unload();
				getPluginHandler().unload();
				getCombatScriptLoader().unload();
				getPacketFilter().unload();
				//getAchievementSystem().unload();
				getWorld().unload();
				getDatabase().close();
				bossGroup.shutdownGracefully().sync();
				workerGroup.shutdownGracefully().sync();
				serverChannel.channel().closeFuture().sync();

				shutdownEvent = null;
				serverChannel = null;
				bossGroup = null;
				workerGroup = null;
				scheduledExecutor = null;

				maxItemId = 0;
				serverStartedTime = 0;
				lastIncomingPacketsDuration = 0;
				lastGameStateDuration = 0;
				lastEventsDuration = 0;
				lastOutgoingPacketsDuration = 0;
				lastTickDuration = 0;
				timeLate = 0;
				lastTickTimestamp = 0;
				incomingTimePerPacketOpcode.clear();
				incomingCountPerPacketOpcode.clear();
				outgoingTimePerPacketOpcode.clear();
				outgoingCountPerPacketOpcode.clear();

				// Don't remove this server from the active servers list if we are just restarting.
				if (!isRestarting()) {
					serversList.remove(this.getName());
				}

				running = false;

				LOGGER.info("Server unloaded");
			} catch (final Throwable t) {
				LOGGER.catching(t);
				System.exit(1);
			}
		}
	}

	public long bench(final Runnable r) {
		long start = System.currentTimeMillis();
		r.run();
		return System.currentTimeMillis() - start;
	}

	public void run() {
		synchronized (running) {
			try {
				this.timeLate = System.currentTimeMillis() - lastTickTimestamp;
				if (getTimeLate() >= getConfig().GAME_TICK) {
					this.timeLate -= getConfig().GAME_TICK;

					// Doing the set in two stages here such that the whole tick has access to the same values for profiling information.
					this.lastTickDuration = bench(() -> {
						try {
							this.lastIncomingPacketsDuration = processIncomingPackets();
							this.lastEventsDuration = getGameEventHandler().runGameEvents();
							this.lastGameStateDuration = getGameUpdater().doUpdates();
							this.lastOutgoingPacketsDuration = processOutgoingPackets();
						} catch (final Throwable t) {
							LOGGER.catching(t);
						}
					});

					monitorTickPerformance();

					// Set us to be in the next tick.
					this.lastTickTimestamp += getConfig().GAME_TICK;

					// Clear out the outgoing and incoming packet processing time frames
					incomingTimePerPacketOpcode.clear();
					incomingCountPerPacketOpcode.clear();
					outgoingTimePerPacketOpcode.clear();
					outgoingCountPerPacketOpcode.clear();

					//LOGGER.info("Tick " + getCurrentTick() + " processed.");
				} else {
					if (getConfig().WANT_CUSTOM_WALK_SPEED) {
						for (final Player p : getWorld().getPlayers()) {
							p.updatePosition();
						}

						for (final Npc n : getWorld().getNpcs()) {
							n.updatePosition();
						}

						getGameUpdater().executeWalkToActions();
					}
				}
			} catch (final Throwable t) {
				LOGGER.catching(t);
			}
		}
	}

	private long processIncomingPackets() {
		return bench(() -> {
			for (final Player player : getWorld().getPlayers()) {
				player.processIncomingPackets();
			}
		});
	}

	private long processOutgoingPackets() {
		return bench(() -> {
			for (final Player player : getWorld().getPlayers()) {
				player.processOutgoingPackets();
			}
		});
	}

	private void monitorTickPerformance() {
		// Store the current tick because we can modify it by calling skipTicks()
		final long currentTick = getCurrentTick();
		// Check if processing game tick took longer than the tick
		final boolean isLastTickLate = getLastTickDuration() > getConfig().GAME_TICK;
		final long ticksLate = getTimeLate() / getConfig().GAME_TICK;
		final boolean isServerLate = ticksLate >= 1;

		if (isLastTickLate) {
			// Current tick processing took too long.
			final String message = "Tick " + currentTick + " is late: " +
				getLastTickDuration() + "ms " +
				getLastIncomingPacketsDuration() + "ms " +
				getLastEventsDuration() + "ms " +
				getLastGameStateDuration() + "ms " +
				getLastOutgoingPacketsDuration() + "ms";

			sendMonitoringWarning(message, true);
		}
		if (isServerLate) {
			// Server fell behind, skip ticks
			skipTicks(ticksLate);
			final String ticksSkipped = ticksLate>1 ? "ticks (" + (currentTick+1) + " - " + (currentTick+ticksLate) + ")" : "tick (" + (currentTick+ticksLate) + ")";
			final String message = "Tick " + currentTick + " " + getTimeLate() + "ms behind. Skipping " + ticksLate + " " + ticksSkipped;
			sendMonitoringWarning(message, false);
		}
	}

	private void sendMonitoringWarning(final String message, final boolean showEventData) {
		// Warn logged in developers
		for (Player p : getWorld().getPlayers()) {
			if (!p.isDev()) {
				continue;
			}

			p.playerServerMessage(MessageType.QUEST, getWorld().getServer().getConfig().MESSAGE_PREFIX + message);
		}

		LOGGER.warn(message);
		if (getWorld().getServer().getDiscordService() != null) {
			getWorld().getServer().getDiscordService().monitoringSendServerBehind(message, showEventData);
		}
	}

	public boolean shutdown(final int seconds) {
		if (shutdownEvent != null) {
			return false;
		}
		shutdownEvent = new SingleTickEvent(getWorld(), null, seconds * 1000 / getConfig().GAME_TICK, "Shutdown for Update") {
			public void action() {
				shuttingDown = true;
			}
		};
		getGameEventHandler().add(shutdownEvent);

		for (final Player playerToUpdate : getWorld().getPlayers()) {
			ActionSender.startShutdown(playerToUpdate, seconds);
		}

		return true;
	}

	public boolean restart(final int seconds) {
		if (shutdownEvent != null) {
			return false;
		}
		shutdownEvent = new SingleTickEvent(getWorld(), null, (seconds - 1) * 1000 / getConfig().GAME_TICK, "Restart") {
			public void action() {
				shuttingDown = true;
				restarting = true;
			}
		};
		getGameEventHandler().add(shutdownEvent);

		for (final Player playerToUpdate : getWorld().getPlayers()) {
			ActionSender.startShutdown(playerToUpdate, seconds);
		}

		return true;
	}

	public long getTimeUntilShutdown() {
		if (shutdownEvent == null) {
			return -1;
		}
		return Math.max(shutdownEvent.timeTillNextRun() - System.currentTimeMillis(), 0);
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

	public final GameEventHandler getGameEventHandler() {
		return gameEventHandler;
	}

	public final GameStateUpdater getGameUpdater() {
		return gameUpdater;
	}

	public final DiscordService getDiscordService() {
		return discordService;
	}

	public final LoginExecutor getLoginExecutor() {
		return loginExecutor;
	}

	public final RSCPacketFilter getPacketFilter() {
		return packetFilter;
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

	public final long getServerStartedTime() {
		return serverStartedTime;
	}

	public final long getCurrentTick() {
		return (lastTickTimestamp - getServerStartedTime()) / getConfig().GAME_TICK;
	}

	private void skipTicks(final long ticks) {
		lastTickTimestamp += ticks * getConfig().GAME_TICK;
	}

	public final ServerConfiguration getConfig() {
		return config;
	}

	public final boolean isRunning() {
		return running;
	}

	public final Constants getConstants() {
		return constants;
	}

	public synchronized World getWorld() {
		return world;
	}

	public String getName() {
		return name;
	}

	public PluginHandler getPluginHandler() {
		return pluginHandler;
	}

	public CombatScriptLoader getCombatScriptLoader() {
		return combatScriptLoader;
	}

	public MySqlGameLogger getGameLogger() {
		return gameLogger;
	}

	public EntityHandler getEntityHandler() {
		return entityHandler;
	}

	public GameDatabase getDatabase() {
		return database;
	}

	public AchievementSystem getAchievementSystem() {
		return achievementSystem;
	}

	public boolean isRestarting() {
		return restarting;
	}

	public boolean isShuttingDown() {
		return shuttingDown;
	}

	public HashMap<Integer, Long> getIncomingTimePerPacketOpcode() {
		return incomingTimePerPacketOpcode;
	}

	public HashMap<Integer, Integer> getIncomingCountPerPacketOpcode() {
		return incomingCountPerPacketOpcode;
	}

	public HashMap<Integer, Long> getOutgoingTimePerPacketOpcode() {
		return outgoingTimePerPacketOpcode;
	}

	public HashMap<Integer, Integer> getOutgoingCountPerPacketOpcode() {
		return outgoingCountPerPacketOpcode;
	}

	public void addIncomingPacketDuration(final int packetOpcode, final long additionalTime) {
		if (!incomingTimePerPacketOpcode.containsKey(packetOpcode)) {
			incomingTimePerPacketOpcode.put(packetOpcode, 0L);
		}
		incomingTimePerPacketOpcode.put(packetOpcode, incomingTimePerPacketOpcode.get(packetOpcode) + additionalTime);
	}

	public void incrementIncomingPacketCount(final int packetOpcode) {
		if (!incomingCountPerPacketOpcode.containsKey(packetOpcode)) {
			incomingCountPerPacketOpcode.put(packetOpcode, 0);
		}
		incomingCountPerPacketOpcode.put(packetOpcode, incomingCountPerPacketOpcode.get(packetOpcode) + 1);
	}

	public void addOutgoingPacketDuration(final int packetOpcode, final long additionalTime) {
		if (!outgoingTimePerPacketOpcode.containsKey(packetOpcode)) {
			outgoingTimePerPacketOpcode.put(packetOpcode, 0L);
		}
		outgoingTimePerPacketOpcode.put(packetOpcode, outgoingTimePerPacketOpcode.get(packetOpcode) + additionalTime);
	}

	public void incrementOutgoingPacketCount(final int packetOpcode) {
		if (!outgoingCountPerPacketOpcode.containsKey(packetOpcode)) {
			outgoingCountPerPacketOpcode.put(packetOpcode, 0);
		}
		outgoingCountPerPacketOpcode.put(packetOpcode, outgoingCountPerPacketOpcode.get(packetOpcode) + 1);
	}

	public synchronized int getMaxItemID() {
		return maxItemId;
	}

	public synchronized int incrementMaxItemID() {
		return ++maxItemId;
	}

	public synchronized int incrementPrivateMessagesSent() {
		return ++privateMessagesSent;
	}

	// This is used to modify the database when new features may break SQL compatibility while upgrading
	private boolean checkForDatabaseStructureChanges() {
		try {
			if (!getDatabase().columnExists("logins", "clientVersion")) {
				getDatabase().addColumn("logins", "clientVersion", "INT (11)");
			}
			if (!getDatabase().columnExists("players", "transfer")) {
				getDatabase().addColumn("players", "transfer", "INT (11)");
			}
			return true;
		} catch (GameDatabaseException e) {
			LOGGER.error(e.toString());
			return false;
		}
	}
}
