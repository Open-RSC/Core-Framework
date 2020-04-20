package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to verify bank pins on the Login thread
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
			PlayerLoginData playerLoginData = getPlayer().getWorld().getServer().getDatabase().getPlayerLoginData(getPlayer().getUsername());
			salt = playerLoginData.salt;

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
			getPlayer().message("There was an error while attempting to verify your bank pin.");
			LOGGER.catching(e);
		}
	}
}
