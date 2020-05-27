package com.openrsc.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.openrsc.server.constants.Constants;
import com.openrsc.server.content.achievement.AchievementSystem;
import com.openrsc.server.database.GameDatabase;
import com.openrsc.server.database.impl.mysql.MySqlGameDatabase;
import com.openrsc.server.database.impl.mysql.MySqlGameLogger;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.SingleTickEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.*;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.util.NamedThreadFactory;
import com.openrsc.server.util.rsc.CollisionFlag;
import com.openrsc.server.util.rsc.MessageType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.apache.logging.log4j.util.Unbox.box;

public class Server implements Runnable {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER;

	private final GameStateUpdater gameUpdater;
	private final GameEventHandler gameEventHandler;
	private final DiscordService discordService;
	private final LoginExecutor loginExecutor;
	private final ServerConfiguration config;
	private final ScheduledExecutorService scheduledExecutor;
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

	private GameTickEvent updateEvent;
	private ChannelFuture serverChannel;

	private volatile Boolean running = false;
	private volatile boolean initialized = false;

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

	private int maxItemId;

	/*Used for pathfinding view debugger
	JPanel2 panel = new JPanel2();
	JFrame frame = new JFrame();
	javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
	*/

	static {
		try {
			Thread.currentThread().setName("InitThread");
			System.setProperty("log4j.configurationFile", "conf/server/log4j2.xml");
			// Enables asynchronous, garbage-free logging.
			System.setProperty("Log4jContextSelector",
				"org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

			LOGGER = LogManager.getLogger();
		} catch (final Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static final Server startServer(final String confName) throws IOException {
		final long startTime = System.currentTimeMillis();
		Server server = new Server(confName);

		if (!server.isRunning()) {
			server.start();
		}
		final long endTime = System.currentTimeMillis();
		final long bootTime = (long) Math.ceil((double) (endTime - startTime) / 1000.0);

		LOGGER.info(server.getName() + " started in " + bootTime + "s");

		return server;
	}

	public static void main(String[] args) {
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

		discordService = new DiscordService(this);
		loginExecutor = new LoginExecutor(this);
		world = new World(this);
		gameEventHandler = new GameEventHandler(this);
		gameUpdater = new GameStateUpdater(this);
		gameLogger = new MySqlGameLogger(this, (MySqlGameDatabase)database);
		entityHandler = new EntityHandler(this);
		achievementSystem = new AchievementSystem(this);
		scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat(getName() + " : GameThread").build());

		maxItemId = database.getMaxItemID();
	}

	private void initialize() {
		try {
			// TODO: We need an uninitialize process. Unloads all of these classes. When this is written the initialize() method should synchronize on initialized like run does with running.

			/*
			// Used for pathfinding view debugger
			if (PathValidation.DEBUG) {
				panel.setLayout(layout);
				frame.add(panel);
				frame.setSize(600, 600);
				frame.setVisible(true);
			}*/

			LOGGER.info("Connecting to Database...");
			try {
				getDatabase().open();
			} catch (final Exception ex) {
				LOGGER.catching(ex);
				System.exit(1);
			}
			LOGGER.info("\t Database Connection Completed");

			LOGGER.info("Loading Game Definitions...");
			getEntityHandler().load();
			LOGGER.info("\t Definitions Completed");

			LOGGER.info("Loading Plugins...");
			getPluginHandler().load();
			LOGGER.info("\t Plugins Completed");

			LOGGER.info("Loading Combat Scripts...");
			getCombatScriptLoader().load();
			LOGGER.info("\t Combat Scripts Completed");

			LOGGER.info("Loading World...");
			getWorld().load();
			LOGGER.info("\t World Completed");

			/*LOGGER.info("Loading Achievements...");
			getAchievementSystem().load();
			LOGGER.info("\t Achievements Completed");*/

			LOGGER.info("Profiling Completed");

			//Never run ResourceLeakDetector PARANOID in production.
			//ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
			final EventLoopGroup bossGroup = new NioEventLoopGroup(0, new NamedThreadFactory(getName() + " : IOBossThread"));
			final EventLoopGroup workerGroup = new NioEventLoopGroup(0, new NamedThreadFactory(getName() + " : IOWorkerThread"));
			final ServerBootstrap bootstrap = new ServerBootstrap();
			final Server gameServer = this;

			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(
				new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(final SocketChannel channel) {
						final ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast("decoder", new RSCProtocolDecoder());
						pipeline.addLast("encoder", new RSCProtocolEncoder());
						pipeline.addLast("handler", new RSCConnectionHandler(gameServer));
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
			} catch (final InterruptedException e) {
				LOGGER.catching(e);
			}

			initialized = true;

		} catch (final Throwable t) {
			LOGGER.catching(t);
			System.exit(1);
		}
	}

	public void start() {
		synchronized (running) {
			if (!isInitialized()) {
				initialize();
			}

			running = true;
			lastTickTimestamp = serverStartedTime = System.currentTimeMillis();
			scheduledExecutor.scheduleAtFixedRate(this, 0, 10, TimeUnit.MILLISECONDS);

			loginExecutor.start();
			boolean wantDiscordBot = getConfig().WANT_DISCORD_BOT;
			boolean wantDiscordAuctionUpdates = getConfig().WANT_DISCORD_AUCTION_UPDATES;
			boolean wantDiscordMonitoringUpdates = getConfig().WANT_DISCORD_MONITORING_UPDATES;
			if (wantDiscordBot || wantDiscordAuctionUpdates || wantDiscordMonitoringUpdates) {
				discordService.start();
			}
			gameLogger.start();
		}
	}

	public void stop() {
		synchronized (running) {
			running = false;
			scheduledExecutor.shutdown();

			loginExecutor.stop();
			discordService.stop();
			gameLogger.stop();
		}
	}

	public void kill() {
		synchronized (running) {
			// TODO: Uninitialize server
			stop();
			LOGGER.fatal(getName() + " shutting down...");
			System.exit(0);
		}
	}

	private void unbind() {
		try {
			serverChannel.channel().disconnect();
		} catch (final Exception e) {
			LOGGER.catching(e);
		}
	}

	public static long bench(final Runnable r) {
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
							this.lastIncomingPacketsDuration = this.lastOutgoingPacketsDuration = 0L;
							for (Player player : getWorld().getPlayers()) {
								this.lastIncomingPacketsDuration += bench(player::processIncomingPackets);
							}
							this.lastGameStateDuration = getGameUpdater().doUpdates();
							this.lastEventsDuration = getGameEventHandler().runGameEvents();
							for (Player player : getWorld().getPlayers()) {
								this.lastOutgoingPacketsDuration += bench(player::processOutgoingPackets);
							}
						} catch (final Throwable t) {
							LOGGER.catching(t);
						}
					});

					// Storing the current tick because we will update the time stamp in either monitorTickPerformance or afterward which will cause getCurrentTick() to return the next tick
					final long currentTick = getCurrentTick();
					monitorTickPerformance();

					// Set us to be in the next tick.
					this.lastTickTimestamp += getConfig().GAME_TICK;

					// Clear out the outgoing and incoming packet processing time frames
					incomingTimePerPacketOpcode.clear();
					incomingCountPerPacketOpcode.clear();
					outgoingTimePerPacketOpcode.clear();
					outgoingCountPerPacketOpcode.clear();

					//LOGGER.info("Tick " + currentTick + " processed.");
				} else {
					if (getConfig().WANT_CUSTOM_WALK_SPEED) {
						for (Player p : getWorld().getPlayers()) {
							p.updatePosition();
						}

						for (Npc n : getWorld().getNpcs()) {
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
		getWorld().getServer().getDiscordService().monitoringSendServerBehind(message, showEventData);
	}

	public boolean shutdownForUpdate(final int seconds) {
		if (updateEvent != null) {
			return false;
		}
		updateEvent = new SingleTickEvent(getWorld(), null, (seconds - 1) * 1000 / getConfig().GAME_TICK, "Shutdown for Update") {
			public void action() {
				unbind();
				saveAndShutdown();
			}
		};
		getGameEventHandler().add(updateEvent);
		return true;
	}

	private void saveAndShutdown() {
		LOGGER.info("Saving players for shutdown...");
		if (getConfig().WANT_CLANS) {
			getWorld().getClanManager().saveClans();
		}
		for (Player p : getWorld().getPlayers()) {
			p.unregister(true, "Server shutting down.");
		}
		LOGGER.info("Players saved...");

		SingleTickEvent up = new SingleTickEvent(getWorld(), null, 10, "Save and Shutdown") {
			public void action() {
				LOGGER.info("Killing server process...");
				System.exit(0);
				try {
					LOGGER.info("Closing the database connection...");
					getDatabase().close();
				} catch (final Exception ex) {
					LOGGER.catching(ex);
				}
			}
		};
		getGameEventHandler().add(up);
	}

	public boolean restart(final int seconds) {
		if (updateEvent != null) {
			return false;
		}
		updateEvent = new SingleTickEvent(getWorld(), null, (seconds - 1) * 1000 / getConfig().GAME_TICK, "Restart") {
			public void action() {
				unbind();
				//saveAndRestart();
				saveAndShutdown();
			}
		};
		getGameEventHandler().add(updateEvent);
		return true;
	}

	public long timeTillShutdown() {
		if (updateEvent == null) {
			return -1;
		}
		return Math.max(updateEvent.timeTillNextRun() - System.currentTimeMillis(), 0);
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

	public final boolean isInitialized() {
		return initialized;
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

	class JPanel2 extends JPanel {
		int size = 15;
		int width = 20;
		Color[][] board = null;

		JPanel2() {
			// set a preferred size for the custom panel.
			setPreferredSize(new Dimension(420, 420));
		}

		private void setTile(int x, int y, Color color) {
			if (x < 0 || x >= 2 * size + 1)
				return;
			if (y < 0 || y >= 2 * size + 1)
				return;
			this.board[x][y] = color;
		}

		private void drawBorder(int x, int y, Graphics g) {
			g.setColor(Color.black);
			g.drawRect(x * width, y * width, width, width);
		}

		private void drawBlocks(int x, int y, TileValue tile, Graphics g) {
			x *= width;
			y *= width;
			if ((tile.traversalMask & (CollisionFlag.FULL_BLOCK_A | CollisionFlag.FULL_BLOCK_B | CollisionFlag.FULL_BLOCK_C)) != 0) {
				g.fillRect(x, y, width, width);
				return;
			}
			g.setColor(Color.red);
			if ((tile.traversalMask & CollisionFlag.EAST_BLOCKED) != 0) {
				g.fillRect(x + width - 4, y + 1, 3, width);
			}
			if ((tile.traversalMask & CollisionFlag.WEST_BLOCKED) != 0) {
				g.fillRect(x + 1, y + 1, 3, width);
			}
			if ((tile.traversalMask & CollisionFlag.NORTH_BLOCKED) != 0) {
				g.fillRect(x, y + 1, width, 3);
			}
			if ((tile.traversalMask & CollisionFlag.SOUTH_BLOCKED) != 0) {
				g.fillRect(x, y + width - 4, width, 3);
			}
		}

		private void drawPath(Mob mob, Graphics g) {
			if (mob.getWalkingQueue() != null && mob.getWalkingQueue().path != null && mob.getWalkingQueue().path.size() > 0) {
				Iterator<Point> path = mob.getWalkingQueue().path.iterator();
				if (mob.isPlayer()) {
					g.setColor(Color.BLUE);
				} else {
					g.setColor(Color.ORANGE);
				}
				while (path.hasNext()) {
					Point next = path.next();
					if (mob.isPlayer())
						g.fillRect(((mob.getX() + size) - next.getX()) * width, (next.getY() - (mob.getY() - size)) * width, width, width);
					else
						g.fillRect(((((Npc) mob).getBehavior().getChaseTarget().getX() + size) - next.getX()) * width, (next.getY() - (((Npc) mob).getBehavior().getChaseTarget().getY() - size)) * width, width, width);
				}
			}
		}

		@Override
		public void paintComponent(Graphics g) {
			board = new Color[2 * size + 1][2 * size + 1];
			super.paintComponent(g);
			if (world.getPlayers().size() > 0) {
				Player test = world.getPlayers().get(0);
				int centerx = test.getX();
				int centery = test.getY();

				for (int x = -size; x <= size; x++) {
					for (int y = -size; y <= size; y++) {
						drawBorder(x + size, y + size, g);
						TileValue tile = world.getTile(centerx - x, centery + y);
						if (tile == null) {
							continue;
						}
						drawBlocks(x + size, y + size, tile, g);

					}
				}

				g.setColor(Color.pink);
				g.fillRect(size * width, size * width, width, width);
				drawPath(test, g);
				for (Npc npc : getWorld().getNpcs()) {
					if (npc.isChasing()) {
						g.setColor(Color.red);
						g.fillRect(((centerx + size) - npc.getX()) * width, (npc.getY() - (centery - size)) * width, width, width);
						drawPath(npc, g);
					}

				}
			}
		}
	}

	public synchronized int getMaxItemID() {
		return maxItemId;
	}

	public synchronized void incrementMaxItemID() {
		maxItemId++;
	}
}
