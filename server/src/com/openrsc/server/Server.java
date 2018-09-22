package com.openrsc.server;

import static org.apache.logging.log4j.util.Unbox.box;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.content.clan.ClanManager;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.event.rsc.impl.combat.scripts.CombatScriptLoader;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.RSCConnectionHandler;
import com.openrsc.server.net.RSCProtocolDecoder;
import com.openrsc.server.net.RSCProtocolEncoder;
import com.openrsc.server.plugins.PluginHandler;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.util.NamedThreadFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class Server implements Runnable {

	private final ScheduledExecutorService scheduledExecutor = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("GameEngine"));

	private final GameStateUpdater gameUpdater = new GameStateUpdater();

	private final GameTickEventHandler tickEventHandler = new GameTickEventHandler();

	private final ServerEventHandler eventHandler = new ServerEventHandler();

	private static PlayerDatabaseExecutor playerDataProcessor;

	private long lastClientUpdate;
	
	private static Server server = null;

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER;

	static {
		try {
			System.setProperty("log4j.configurationFile", "conf/server/log4j2.xml"); 
			/* Enables asynchronous, garbage-free logging. */
			System.setProperty("Log4jContextSelector", 
					"org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

			LOGGER = LogManager.getLogger();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static void main(String[] args) throws IOException {
		LOGGER.info("Launching Open RSC Game Server...");
		if (args.length == 0) {
			Constants.GameServer.initConfig("members.conf");
			LOGGER.info("Server Configuration file not provided. Default: free.conf");
		} else {
			Constants.GameServer.initConfig(args[0]);
			LOGGER.info("Server Configuration file: " + args[0]);
			LOGGER.info("\t Game Tick Cycle: {}", box(Constants.GameServer.GAME_TICK));
			LOGGER.info("\t Client Version: {}", box(Constants.GameServer.CLIENT_VERSION));
			LOGGER.info("\t Server type: " + (Constants.GameServer.MEMBER_WORLD ? "MEMBER" : "FREE" + " world."));
			LOGGER.info("\t Combat Experience Rate: {}", box(Constants.GameServer.COMBAT_EXP_RATE));
			LOGGER.info("\t Skilling Experience Rate: {}", box(Constants.GameServer.SKILLING_EXP_RATE));
			LOGGER.info("\t Standard Subscription Rate: {}", box(Constants.GameServer.SUBSCRIBER_EXP_RATE));
			LOGGER.info("\t Premium Subscription Rate: {}", box(Constants.GameServer.PREMIUM_EXP_RATE));
			LOGGER.info("\t Wilderness Experience Boost: {}", box(Constants.GameServer.WILDERNESS_BOOST));
			LOGGER.info("\t Skull Experience Boost: {}", box(Constants.GameServer.SKULL_BOOST)); 
			LOGGER.info("\t Double experience: " + (Constants.GameServer.IS_DOUBLE_EXP ? "Enabled" : "Disabled")); 
			LOGGER.info("\t View Distance: {}", box(Constants.GameServer.VIEW_DISTANCE));
		}
		if(server == null) {
			server = new Server();
			server.initialize();
			server.start();
		}
	}

	private boolean running;

	private DelayedEvent updateEvent;

	public Server() {
		running = true;
		playerDataProcessor = new PlayerDatabaseExecutor();
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
			
			//ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
			final EventLoopGroup bossGroup = new NioEventLoopGroup();
			final EventLoopGroup workerGroup = new NioEventLoopGroup();
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
			try

			{
				PluginHandler.getPluginHandler().handleAction("Startup", new Object[] {});
				serverChannel = bootstrap.bind(new InetSocketAddress(Constants.GameServer.SERVER_PORT)).sync();
				LOGGER.info("Open RSC channel is now online on port {}!", box(Constants.GameServer.SERVER_PORT));
			} catch (final InterruptedException e) {
				e.printStackTrace();
			} 

		} catch (Exception e) {
			LOGGER.catching(e);
			System.exit(1);
		}
	}
	private ChannelFuture serverChannel;

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
		updateEvent = new SingleEvent(null, (seconds - 1) * 1000) {
			public void action() {
				unbind();
				saveAndShutdown();
			}
		};
		Server.getServer().getEventHandler().add(updateEvent);
		return true;
	}

	public void saveAndShutdown() {
		ClanManager.saveClans();
		for (Player p : World.getWorld().getPlayers()) {
			p.unregister(true, "Server shutting down.");
		}
		
		SingleEvent up = new SingleEvent(null, 6000) {
			public void action() {
				kill();
				DatabaseConnection.getDatabase().close();
			}
		};
		Server.getServer().getEventHandler().add(up);
	}

	public int timeTillShutdown() {
		if (updateEvent == null) {
			return -1;
		}
		return updateEvent.timeTillNextRun();
	}

	public void unbind() {
		try {
			serverChannel.channel().disconnect();
		} catch (Exception e) {
		}
	}

	public static Server getServer() {
		return server;
	}

	public static PlayerDatabaseExecutor getPlayerDataProcessor() {
		return playerDataProcessor;
	}

	public void run() {
		for (Player p : World.getWorld().getPlayers()) {
			p.processIncomingPackets();
		}
		getEventHandler().doEvents();
		try {
			if (System.currentTimeMillis() - lastClientUpdate >= Constants.GameServer.GAME_TICK) {
				lastClientUpdate = System.currentTimeMillis();
				tickEventHandler.doGameEvents();
				gameUpdater.doUpdates();
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}

		for (Player p : World.getWorld().getPlayers()) {
			p.sendOutgoingPackets();
		}
	}

	public ServerEventHandler getEventHandler() {
		return eventHandler;
	}

	public GameTickEventHandler getGameEventHandler() {
		return tickEventHandler;
	}

	public void submitTask(Runnable r) {
		scheduledExecutor.submit(r);
	}

	public void start() {
		scheduledExecutor.scheduleAtFixedRate(this, 0, 50, TimeUnit.MILLISECONDS);
	}
}
