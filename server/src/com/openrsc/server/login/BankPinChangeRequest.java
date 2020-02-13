package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Used to change bank pins on the Login thread
 *
 * @author Kenix
 */
public class BankPinChangeRequest extends LoginExecutorProcess {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Player player;
	private final String newBankPin;
	private final String oldBankPin;

	public BankPinChangeRequest(final Server server, final Player player, final String oldBankPin, final String newBankPin) {
		this.server = server;
		this.player = player;
		this.newBankPin = newBankPin;
		this.oldBankPin = oldBankPin;
	}

	public final String getIpAddress() {
		return getPlayer().getCurrentIP();
	}

	public final Player getPlayer() {
		return player;
	}

	public final String getOldBankPin() { return oldBankPin; }

	public final String getNewBankPin() { return newBankPin; }

	public final Server getServer() {
		return server;
	}

	protected void processInternal() {
		try {
			if(!getServer().getPacketFilter().shouldAllowLogin(getIpAddress(), false)) {
				getPlayer().playerServerMessage(MessageType.QUEST, "Bank pin not able to be reset due to previous requests. Please try again.");
				return;
			}

			if(getNewBankPin() == null && getOldBankPin() == null) {
				getPlayer().setSuspiciousPlayer(true, "no new or old bank pin set when attempting to change bank pin");
				return;
			}

			if(getPlayer().getCache().hasKey("bank_pin") && getOldBankPin() == null) {
				getPlayer().playerServerMessage(MessageType.QUEST, "You already have a bank pin");
				return;
			}

			if(!getPlayer().getCache().hasKey("bank_pin") && getNewBankPin() == null) {
				getPlayer().playerServerMessage(MessageType.QUEST, "You do not have a bank pin to remove");
				return;
			}

			if(!getPlayer().getCache().hasKey("bank_pin") && getNewBankPin() != null && getOldBankPin() != null) {
				getPlayer().playerServerMessage(MessageType.QUEST, "You do not have a bank pin to change");
				return;
			}

			if(getOldBankPin() == "cancel") {
				getPlayer().playerServerMessage(MessageType.QUEST, "Can not change bank pin: No old bank pin entered");
				return;
			}

			if(getNewBankPin() == "cancel") {
				getPlayer().playerServerMessage(MessageType.QUEST, "You have not entered a new bank pin. No bank pin set");
				return;
			}

			String hashedBankPin = null;
			String salt = null;
			PreparedStatement statement = getPlayer().getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
			statement.setString(1, getPlayer().getUsername());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				salt = result.getString("salt");
			}
			else {
				getPlayer().playerServerMessage(MessageType.QUEST, "There was an error while attempting to change your bank pin");
				// TODO: Database logging
				LOGGER.info("Skipping bank pin change for Player " + getPlayer() + " " + getPlayer().getCurrentIP() + " because query results not found.");
				return;
			}

			if(getOldBankPin() != null && getPlayer().getCache().hasKey("bank_pin") && !getPlayer().getAttribute("bankpin", false)) {
				if(!DataConversions.checkPassword(getOldBankPin(), salt, getPlayer().getCache().getString("bank_pin"))) {
					getPlayer().playerServerMessage(MessageType.QUEST, "Can not change bank pin: Invalid old bank pin");
					// TODO: Database logging
					LOGGER.info("Bank pin guess fail for " + getPlayer() + " " + getPlayer().getCurrentIP());
					return;
				}
			}

			if(getNewBankPin() != null) {
				hashedBankPin = DataConversions.hashPassword(getNewBankPin(), salt);

				// TODO: Database logging
				LOGGER.info("Changing bank pin for " + getPlayer() + " " + getPlayer().getCurrentIP());
				getPlayer().playerServerMessage(MessageType.QUEST, getOldBankPin() == null ? "Bank pin set" : "Bank pin changed");
				getPlayer().getCache().store("bank_pin", hashedBankPin);
			} else {
				LOGGER.info("Removing bank pin for " + getPlayer() + " " + getPlayer().getCurrentIP());
				getPlayer().playerServerMessage(MessageType.QUEST, "Your bank pin has been removed");
				getPlayer().getCache().remove("bank_pin");
			}

			getPlayer().setAttribute("bankpin", true);
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
