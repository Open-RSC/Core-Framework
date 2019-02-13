package com.openrsc.server.login;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.ThrottleFilter;

public class LoginTask extends ThrottleFilter implements Runnable {

	private final LoginRequest loginRequest;
	private final Player loadedPlayer;

	public LoginTask(LoginRequest request, Player loadedPlayer) {
		this.loginRequest = request;
		this.loadedPlayer = loadedPlayer;
	}

	@Override
	public void run() {
		loginRequest.loadingComplete(loadedPlayer);
	}

}
