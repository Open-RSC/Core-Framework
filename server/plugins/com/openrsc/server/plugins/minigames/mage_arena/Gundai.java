package com.openrsc.server.plugins.minigames.mage_arena;

import com.openrsc.server.Constants;
import com.openrsc.server.content.market.Market;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.openrsc.server.plugins.Functions.*;

public class Gundai implements TalkToNpcExecutiveListener, TalkToNpcListener, NpcCommandListener, NpcCommandExecutiveListener {
	private static final Logger LOGGER = LogManager.getLogger(Gundai.class);
	@Override
	public void onTalkToNpc(final Player player, final Npc n) {
		playerTalk(player, n, "hello, what are you doing out here?");
		npcTalk(player, n, "why i'm a banker, the only one around these dangerous parts");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("cool, I'd like to access my bank account please") {
			@Override
			public void action() {
				player.setAccessingBank(true);
				if (Constants.GameServer.WANT_BANK_PINS) {
					if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
						String pin = getBankPinInput(player);
						if (pin == null) {
							return;
						}
						try {
							PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
							statement.setString(1, player.getUsername());
							ResultSet result = statement.executeQuery();
							if (result.next()) {
								pin = DataConversions.hashPassword(pin, result.getString("salt"));
							}
						} catch (SQLException e) {
							LOGGER.catching(e);
						}
						if (!player.getCache().getString("bank_pin").equals(pin)) {
							ActionSender.sendBox(player, "Incorrect bank pin", false);
							return;
						}
						player.setAttribute("bankpin", true);
						ActionSender.sendBox(player, "Bank pin correct", false);
					}
				}
				npcTalk(player, n, "no problem");
				ActionSender.showBank(player);
			}
		});
		if (Constants.GameServer.WANT_BANK_PINS) {
			defaultMenu.addOption(new Option("I'd like to talk about bank pin") {
				@Override
				public void action() {
					int menu = showMenu(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
					if (menu == 0) {
						if (!player.getCache().hasKey("bank_pin")) {
							String bankPin = getBankPinInput(player);
							if (bankPin == null) {
								return;
							}
							try {
								PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
								statement.setString(1, player.getUsername());
								ResultSet result = statement.executeQuery();
								if (result.next()) {
									bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
									player.getCache().store("bank_pin", bankPin);
									//ActionSender.sendBox(p, "Your new bank pin is " + bankPin, false);
								}
							} catch (SQLException e) {
								LOGGER.catching(e);
							}

						} else {
							ActionSender.sendBox(player, "You already have a bank pin", false);
						}
					} else if (menu == 1) {
						if (player.getCache().hasKey("bank_pin")) {
							String bankPin = getBankPinInput(player);
							if (bankPin == null) {
								return;
							}
							try {
								PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
								statement.setString(1, player.getUsername());
								ResultSet result = statement.executeQuery();
								if (result.next()) {
									bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
								}
							} catch (SQLException e) {
								LOGGER.catching(e);
							}
							if (!player.getCache().getString("bank_pin").equals(bankPin)) {
								ActionSender.sendBox(player, "Incorrect bank pin", false);
								return;
							}
							String changeTo = getBankPinInput(player);
							try {
								PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
								statement.setString(1, player.getUsername());
								ResultSet result = statement.executeQuery();
								if (result.next()) {
									changeTo = DataConversions.hashPassword(changeTo, result.getString("salt"));
								}
							} catch (SQLException e) {
								LOGGER.catching(e);
							}
							player.getCache().store("bank_pin", changeTo);
							//ActionSender.sendBox(p, "Your new bank pin is " + bankPin, false);
						} else {
							player.message("You don't have a bank pin");
						}
					} else if (menu == 2) {
						if (player.getCache().hasKey("bank_pin")) {
							String bankPin = getBankPinInput(player);
							if (bankPin == null) {
								return;
							}
							if (!player.getCache().getString("bank_pin").equals(bankPin)) {
								ActionSender.sendBox(player, "Incorrect bank pin", false);
								return;
							}
							player.getCache().remove("bank_pin");

							ActionSender.sendBox(player, "Your bank pin is removed", false);
						} else {
							player.message("You don't have a bank pin");
						}
					}
				}
			});
		}

		if (Constants.GameServer.SPAWN_AUCTION_NPCS) {
			defaultMenu.addOption(new Option("I'd like to collect my items from auction") {
				@Override
				public void action() {
					if (Constants.GameServer.WANT_BANK_PINS) {
						if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
							String pin = getBankPinInput(player);
							if (pin == null) {
								return;
							}
							try {
								PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
								statement.setString(1, player.getUsername());
								ResultSet result = statement.executeQuery();
								if (result.next()) {
									pin = DataConversions.hashPassword(pin, result.getString("salt"));
								}
							} catch (SQLException e) {
								LOGGER.catching(e);
							}
							if (!player.getCache().getString("bank_pin").equals(pin)) {
								ActionSender.sendBox(player, "Incorrect bank pin", false);
								return;
							}
							player.setAttribute("bankpin", true);
							ActionSender.sendBox(player, "Bank pin correct", false);
						}
					}
					Market.getInstance().addPlayerCollectItemsTask(player);
				}
			});
		}

		defaultMenu.addOption(new Option("Well, now i know") {
			@Override
			public void action() {
				npcTalk(player, n, "knowledge is power my friend");
			}
		});
		defaultMenu.showMenu(player);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 792;
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (n.getID() == 792) {
			if (command.equalsIgnoreCase("Bank")) {
				quickFeature(n, p, false);
			} else if (Constants.GameServer.SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
				quickFeature(n, p, true);
			}
		}
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if (n.getID() == 792 && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (n.getID() == 792 && Constants.GameServer.SPAWN_AUCTION_NPCS && command.equalsIgnoreCase("Collect")) {
			return true;
		}
		return false;
	}

	private void quickFeature(Npc npc, Player player, boolean auction) {
		if (Constants.GameServer.WANT_BANK_PINS) {
			if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
				String pin = getBankPinInput(player);
				if (pin == null) {
					return;
				}
				try {
					PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
					statement.setString(1, player.getUsername());
					ResultSet result = statement.executeQuery();
					if (result.next()) {
						pin = DataConversions.hashPassword(pin, result.getString("salt"));
					}
				} catch (SQLException e) {
					LOGGER.catching(e);
				}
				if (!player.getCache().getString("bank_pin").equals(pin)) {
					ActionSender.sendBox(player, "Incorrect bank pin", false);
					return;
				}
				player.setAttribute("bankpin", true);
				ActionSender.sendBox(player, "Bank pin correct", false);
			}
		}
		if (Constants.GameServer.SPAWN_AUCTION_NPCS && auction) {
			Market.getInstance().addPlayerCollectItemsTask(player);
		} else {
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		}
	}

}
