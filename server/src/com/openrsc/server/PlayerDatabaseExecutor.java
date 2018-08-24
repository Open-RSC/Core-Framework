package com.openrsc.server;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openrsc.server.event.rsc.ImmediateEvent;
import com.openrsc.server.login.LoginRequest;
import com.openrsc.server.login.LoginTask;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.sql.DatabasePlayerLoader;
import com.openrsc.server.util.NamedThreadFactory;

public class PlayerDatabaseExecutor implements Runnable  {
	
	/**
     * The asynchronous logger.
     */
    private static final Logger LOGGER = LogManager.getLogger();
	
	private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("PlayerDataProcessor"));
	
    private Queue<LoginRequest> loadRequests = new ConcurrentLinkedQueue<LoginRequest>();
    
    private Queue<Player> saveRequests = new ConcurrentLinkedQueue<Player>();
    
    private DatabasePlayerLoader database = new DatabasePlayerLoader();
    
	@Override
	public void run() {
		try {
			LoginRequest loginRequest = null;
			while((loginRequest = loadRequests.poll()) != null) {
				int loginResponse = database.validateLogin(loginRequest);
				loginRequest.loginValidated(loginResponse);
				if(loginResponse == 0) {
					final Player loadedPlayer = database.loadPlayer(loginRequest);
					
					LoginTask loginTask = new LoginTask(loginRequest, loadedPlayer);
					Server.getServer().getGameEventHandler().add(new ImmediateEvent() {
						@Override
						public void action() {
							loginTask.run();
						}
					});
				
				}
				LOGGER.info("Processed login request for " + loginRequest.getUsername() + " response: " + loginResponse);
			}
			Player playerToSave = null;
			while((playerToSave = saveRequests.poll()) != null) {
				getDatabase().savePlayer(playerToSave);
				LOGGER.info("Saved player " + playerToSave.getUsername() + "");
			}
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}


	public DatabasePlayerLoader getDatabase() {
		return database;
	}


	public void addLoginRequest(LoginRequest request) {
		loadRequests.add(request);
	}


	public void addSaveRequest(Player player) {
		saveRequests.add(player);
	}
	
	public void start() {
		scheduledExecutor.scheduleAtFixedRate(this, 50, 50, TimeUnit.MILLISECONDS);
	}

}
