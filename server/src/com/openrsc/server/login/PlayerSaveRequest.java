package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to verify save players on the Login thread
 */
public class PlayerSaveRequest extends LoginExecutorProcess {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Player player;
	private final boolean logout;

	public PlayerSaveRequest(final Server server, final Player player, boolean logout) {
		this.server = server;
		this.player = player;
		this.logout = logout;
	}

	public final Player getPlayer() {
		return player;
	}

	public final Server getServer() {
		return server;
	}

	protected void processInternal() {
//		LOGGER.info("Saved player " + player.getUsername() + "");
		try {
			boolean success = getServer().getPlayerService().savePlayer(player);
			if (success && this.logout)
				logoutSaveSuccess();
		} catch (final GameDatabaseException ex) {
			LOGGER.warn("Error saving the player, phantom player may have extra login count on their IP address now...!");
			LOGGER.catching(ex);
		}
	}

	public void logoutSaveSuccess() {

		getPlayer().setLoggedIn(false);

		/* IP Tracking in wilderness removal */
		/*if(player.getLocation().inWilderness())
		{
			wildernessIPTracker.remove(player.getCurrentIP());
		}*/

		final World world = getPlayer().getWorld();
		for (Player other : world.getPlayers()) {
			other.getSocial().alertOfLogout(getPlayer());
		}

		world.getClanManager().checkAndUnattachFromClan(getPlayer());
		world.getPartyManager().checkAndUnattachFromParty(getPlayer());

		getServer().getPacketFilter().removeLoggedInPlayer(getPlayer().getCurrentIP());

		getPlayer().remove();
		getServer().getWorld().getPlayers().remove(getPlayer());
		LOGGER.info("Removed player " + getPlayer().getUsername());
	}

}
