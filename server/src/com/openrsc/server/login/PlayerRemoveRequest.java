package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to verify save players on the Login thread
 *
 * @author Kenix
 */
public class PlayerRemoveRequest extends LoginExecutorProcess {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Player player;

	public PlayerRemoveRequest(final Server server, final Player player) {
		this.server = server;
		this.player = player;
	}

	public final Player getPlayer() {
		return player;
	}

	public final Server getServer() {
		return server;
	}

	protected void processInternal() {
		getServer().getPacketFilter().removeLoggedInPlayer(getPlayer().getCurrentIP());

		getPlayer().remove();
		getServer().getWorld().getPlayers().remove(getPlayer());
		LOGGER.info("Removed player " + getPlayer().getUsername());
	}
}
