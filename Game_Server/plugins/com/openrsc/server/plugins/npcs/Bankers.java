package com.openrsc.server.plugins.npcs;

import com.openrsc.server.event.rsc.GameNotifyEvent;
import com.openrsc.server.event.rsc.GameStateEvent;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.openrsc.server.plugins.Functions.*;

public class Bankers implements TalkToNpcExecutiveListener, TalkToNpcListener, NpcCommandListener, NpcCommandExecutiveListener {
	private static final Logger LOGGER = LogManager.getLogger(Bankers.class);
	public static int[] BANKERS = {95, 224, 268, 540, 617};

	@Override
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		if (inArray(npc.getID(), BANKERS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		npcTalk(player, npc, "Good day" + (npc.getID() == 617 ? " Bwana" : "") + ", how may I help you?");

		int menu;

		if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS && player.getWorld().getServer().getConfig().WANT_BANK_PINS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin",
				"I'd like to collect my items from auction");
		else if (player.getWorld().getServer().getConfig().WANT_BANK_PINS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin");
		else if (player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to collect my items from auction");
		else
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?");

		if (menu == 0) {
			if (player.isIronMan(2)) {
				player.message("As an Ultimate Iron Man, you cannot use the bank.");
				return;
			}
			if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
				String pin = getBankPinInput(player);
				boolean isPinValid = false;
				if (pin == null) {
					return;
				}
				try {
					PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
					statement.setString(1, player.getUsername());
					ResultSet result = statement.executeQuery();
					if (result.next()) {
						isPinValid = DataConversions.checkPassword(pin, result.getString("salt"), player.getCache().getString("bank_pin"));
					}
				} catch (SQLException e) {
					LOGGER.catching(e);
				}
				if (!isPinValid) {
					ActionSender.sendBox(player, "Incorrect bank pin", false);
					return;
				}
				player.setAttribute("bankpin", true);
				player.message("You have correctly entered your PIN");
			}

			npcTalk(player, npc, "Certainly " + (player.isMale() ? "Sir" : "Miss"));
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		} else if (menu == 1) {
			npcTalk(player, npc, "This is a branch of the bank of Runescape", "We have branches in many towns");
			int branchMenu = showMenu(player, npc, "And what do you do?",
				"Didn't you used to be called the bank of Varrock");
			if (branchMenu == 0) {
				npcTalk(player, npc, "We will look after your items and money for you",
					"So leave your valuables with us if you want to keep them safe");
			} else if (branchMenu == 1) {
				npcTalk(player, npc, "Yes we did, but people kept on coming into our branches outside of varrock",
					"And telling us our signs were wrong",
					"As if we didn't know what town we were in or something!");
			}
		} else if (menu == 2 && player.getWorld().getServer().getConfig().WANT_BANK_PINS) {
			int bankPinMenu = showMenu(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
			if (bankPinMenu == 0) {
				if (!player.getCache().hasKey("bank_pin")) {
					String bankPin = getBankPinInput(player);
					if (bankPin == null) {
						return;
					}
					try {
						PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
							player.getCache().store("bank_pin", bankPin);
							//ActionSender.sendBox(player, "Your new bank pin is " + bankPin, false);
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
				} else {
					ActionSender.sendBox(player, "You already have a bank pin", false);
				}
			} else if (bankPinMenu == 1) {
				if (player.getCache().hasKey("bank_pin")) {
					String bankPin = getBankPinInput(player);
					boolean isPinValid = false;
					if (bankPin == null) {
						return;
					}
					try {
						PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							isPinValid = DataConversions.checkPassword(bankPin, result.getString("salt"), player.getCache().getString("bank_pin"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!isPinValid) {
						ActionSender.sendBox(player, "Incorrect bank pin", false);
						return;
					}
					String changeTo = getBankPinInput(player);
					try {
						PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							changeTo = DataConversions.hashPassword(changeTo, result.getString("salt"));
							player.getCache().store("bank_pin", changeTo);
							//ActionSender.sendBox(player, "Your new bank pin is " + changeTo, false);
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}


				} else {
					player.message("You don't have a bank pin");
				}
			} else if (bankPinMenu == 2) {
				if (player.getCache().hasKey("bank_pin")) {
					String bankPin = getBankPinInput(player);
					boolean isPinValid = false;
					if (bankPin == null) {
						return;
					}
					try {
						PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							isPinValid = DataConversions.checkPassword(bankPin, result.getString("salt"), player.getCache().getString("bank_pin"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!isPinValid) {
						ActionSender.sendBox(player, "Incorrect bank pin", false);
						return;
					}
					if (player.getIronMan() > 0 && player.getIronManRestriction() == 0) {
						message(player, npc, 1000, "Deleting your bankpin results in permanent iron man restriction",
							"Are you sure you want to do it?");

						int deleteMenu = showMenu(player, "I want to do it!",
							"No thanks.");
						if (deleteMenu == 0) {
							player.getCache().remove("bank_pin");
							ActionSender.sendBox(player, "Your bank pin is removed", false);
							player.message("Your iron man restriction status is now permanent.");
							player.setIronManRestriction(1);
							ActionSender.sendIronManMode(player);
						} else if (deleteMenu == 1) {
							player.message("You decide to not remove your Bank PIN.");
						}
					} else {
						player.getCache().remove("bank_pin");
						ActionSender.sendBox(player, "Your bank pin is removed", false);
					}
				} else {
					player.message("You don't have a bank pin");
				}
			}

		} else if ((menu == 2 || menu == 3) && player.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
			if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
				String pin = getBankPinInput(player);
				boolean isPinValid = false;
				if (pin == null) {
					return;
				}
				try {
					PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
					statement.setString(1, player.getUsername());
					ResultSet result = statement.executeQuery();
					if (result.next()) {
						isPinValid = DataConversions.checkPassword(pin, result.getString("salt"), player.getCache().getString("bank_pin"));
					}
				} catch (SQLException e) {
					LOGGER.catching(e);
				}
				if (!isPinValid) {
					ActionSender.sendBox(player, "Incorrect bank pin", false);
					return;
				}
				player.setAttribute("bankpin", true);
				ActionSender.sendBox(player, "Bank pin correct", false);
			}
			player.getWorld().getMarket().addPlayerCollectItemsTask(player);
		}
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (inArray(n.getID(), BANKERS)) {
			if (command.equalsIgnoreCase("Bank") && p.getWorld().getServer().getConfig().RIGHT_CLICK_BANK) {
				quickFeature(n, p, false);
			} else if (command.equalsIgnoreCase("Collect") && p.getWorld().getServer().getConfig().SPAWN_AUCTION_NPCS) {
				quickFeature(n, p, true);
			}
		}
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Bank")) {
			return true;
		}
		if (inArray(n.getID(), BANKERS) && command.equalsIgnoreCase("Collect")) {
			return true;
		}
		return false;
	}

	private void quickFeature(Npc npc, Player player, boolean auction) {
		player.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(player.getWorld(), player, 0, "Bank Quick Access") {
			public void init() {
				addState(0, () -> {
					if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
						GameNotifyEvent event = getBankPinInput(player, this);
						return invokeOnNotify(event,1, 0);
					}
					return invoke(2, 0);
				});
				addState(1, () -> {
					String pin;
					boolean isPinValid = false;
					if ((pin=(String)getNotifyEvent().getObjectOut("string_pin")) == null) {
						return null;
					}
					try {
						PreparedStatement statement = player.getWorld().getServer().getDatabaseConnection().prepareStatement("SELECT salt FROM " + player.getWorld().getServer().getConfig().MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							isPinValid = DataConversions.checkPassword(pin, result.getString("salt"), player.getCache().getString("bank_pin"));
						}
					} catch (SQLException e) {
						LOGGER.catching(e);
					}
					if (!isPinValid) {
						ActionSender.sendBox(player, "Incorrect bank pin", false);
						return null;
					}
					player.setAttribute("bankpin", true);
					player.message("You have correctly entered your PIN");

					return invoke(2, 0);
				});
				addState(2, () -> {
					if (auction) {
						player.getWorld().getMarket().addPlayerCollectItemsTask(player);
					} else {
						player.setAccessingBank(true);
						ActionSender.showBank(player);
					}
					return null;
				});
			}
		});

	}
}
