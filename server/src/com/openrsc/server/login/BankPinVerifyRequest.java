package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Used to verify bank pins on the Login thread
 *
 * @author Kenix
 */
public class BankPinVerifyRequest extends LoginExecutorProcess {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Player player;
	private final String bankPin;

	public BankPinVerifyRequest(final Server server, final Player player, final String bankPin) {
		this.server = server;
		this.player = player;
		this.bankPin = bankPin;
	}

	public final String getIpAddress() {
		return getPlayer().getCurrentIP();
	}

	public final Player getPlayer() {
		return player;
	}

	public final String getBankPin() { return bankPin; }

	public final Server getServer() {
		return server;
	}

	protected void processInternal() {
		try {
			if(!getServer().getPacketFilter().shouldAllowLogin(getIpAddress(), false)) {
				getPlayer().message("Bank pin not able to be verified due to previous requests. Please try again.");
				return;
			}

			if(getPlayer().getAttribute("bankpin", false)) {
				return;
			}

			String salt = null;
			PreparedStatement statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
			statement.setString(1, getPlayer().getUsername());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				salt = result.getString("salt");
			}
			else {
				getPlayer().message("There was an error while attempting to verify your bank pin.");
				// TODO: Database logging
				LOGGER.info("Skipping bank pin change for Player " + getPlayer() + " " + getPlayer().getCurrentIP() + " because query results not found.");
				return;
			}

			if(getPlayer().getCache().hasKey("bank_pin")) {
				if(!DataConversions.checkPassword(getBankPin(), salt, getPlayer().getCache().getString("bank_pin"))) {
					getPlayer().message("Bank pin incorrect");
					// TODO: Database logging
					LOGGER.info("Bank pin guess fail for " + getPlayer() + " " + getPlayer().getCurrentIP());
					return;
				}
			}
			else {
				getPlayer().setAttribute("bankpin", true);
				return;
			}

			getPlayer().message("You have correctly entered your bank pin");
			getPlayer().setAttribute("bankpin", true);
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
