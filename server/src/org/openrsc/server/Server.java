package org.openrsc.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.model.World;
import org.openrsc.server.networking.RSCConnectionHandler;
import org.openrsc.server.networking.WebConnectionHandler;

import com.rscdaemon.concurrent.ConfigurableThreadFactory;
import com.rscdaemon.concurrent.ConfigurableThreadFactory.ConfigurationBuilder;

public final class Server
{
	private static GameEngine engine = new GameEngine();
	
	private static Server instance;
	
	public static Server getServer()
	{
		return instance;
	}
	
	private SocketAcceptor webAcceptor;
	
	public static SocketAcceptor acceptor;
	
	public static GameEngine getEngine()
	{
		return engine;
	}

	public Server() throws Exception
	{
		Logger logger = new Logger();
		logger.setDaemon(true);
		logger.start();
		World.load();
		engine.start();
		try
		{
			acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1, Executors.newCachedThreadPool(new ConfigurableThreadFactory(
					new ConfigurationBuilder().setDaemon(true))));
			acceptor.getDefaultConfig().getFilterChain().addLast("threadPool", new ExecutorFilter(Executors.newCachedThreadPool(new ConfigurableThreadFactory(
					new ConfigurationBuilder().setDaemon(true)))));
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setThreadModel(ThreadModel.MANUAL);
			config.setDisconnectOnUnbind(true);
			((SocketSessionConfig)config.getSessionConfig()).setReuseAddress(true);
			((SocketSessionConfig)config.getSessionConfig()).setTcpNoDelay(true);
			acceptor.bind(new InetSocketAddress(Config.SERVER_IP, Config.SERVER_PORT), new RSCConnectionHandler(engine.messageQueue), config);
		}
		catch (Exception e) {
			System.out.println("Unable to bind to: " + Config.SERVER_IP + " (" + Config.SERVER_PORT + ")");
			System.exit(-1);
		}
		try {
			this.webAcceptor = new SocketAcceptor();
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setDisconnectOnUnbind(true);
			((SocketSessionConfig)config.getSessionConfig()).setReuseAddress(true);
			this.webAcceptor.bind(new InetSocketAddress(Config.SERVER_IP, Config.WEB_PORT), new WebConnectionHandler(engine.webMessageQueue), config);
		} catch (Exception e) {
			System.out.println("Unable to bind to: " + Config.SERVER_IP + " (" + Config.WEB_PORT + ")");
			System.exit(-1);
		}

	}

	/**
	 * Gracefully exits the openrsc server instance
	 * 
	 * @param autoRestart should the auto-restart script be invoked?
	 * 
	 */
	public void shutdown(boolean autoRestart)
	{
		System.out.println(Config.SERVER_NAME + " is shutting down...");
		/// Remove all players
		engine.emptyWorld();
		World.getWorldLoader().saveAuctionHouse();
		engine.kill();
		/// Shutdown the networking
		try
		{
			acceptor.unbindAll();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		try
		{
			webAcceptor.unbindAll();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		/// Disconnect from MySQL (waits to finish all pending queries, then shuts down)
		try
		{
			System.out.println("Waiting for database service to close...");
			ServerBootstrap.getDatabaseService().close();
			System.out.println("Database service has shut down...");
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}

		/// Invoke the auto-restart script
		if (autoRestart)
		{
			try
			{
				System.out.println("Running Auto Restart...");
				Runtime.getRuntime().exec("scripts/autorestart.sh");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws Exception {
		if(args.length < 1)
		{
			System.out.println("No configuration file provided - usage: Server <configfile>");
			return;
		}
		try {
			File file = new File(args[0]);
			if(!file.exists())
			{
				System.err.println("Could not find configuration file: " + args[0]);
				return;
			}
			Config.initConfig(file);
		} catch (IOException ex)
		{
			System.err.println("An error has been encountered while loading configuration: ");
			ex.printStackTrace();
		}

		System.out.println(Config.SERVER_NAME + " is starting up...");
		Class.forName("org.openrsc.server.ServerBootstrap");
		try
		{
			instance = new Server();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			System.exit(-1);
		}
		System.out.println(Config.SERVER_NAME + " is now online!");
	}

}
