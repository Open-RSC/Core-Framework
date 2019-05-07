package com.openrsc.server.plugins.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.content.market.Market;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.sql.DatabaseConnection;
import com.openrsc.server.util.rsc.DataConversions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.openrsc.server.plugins.Functions.*;

public class Bankers implements TalkToNpcExecutiveListener, TalkToNpcListener, NpcCommandListener, NpcCommandExecutiveListener {

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

		if (Constants.GameServer.SPAWN_AUCTION_NPCS && Constants.GameServer.WANT_BANK_PINS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin",
				"I'd like to collect my items from auction");
		else if (Constants.GameServer.WANT_BANK_PINS)
			menu = showMenu(player, npc,
				"I'd like to access my bank account please",
				"What is this place?",
				"I'd like to talk about bank pin");
		else if (Constants.GameServer.SPAWN_AUCTION_NPCS)
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
				if (pin == null) {
					return;
				}
				try {
						PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							pin = DataConversions.hashPassword(pin, result.getString("salt"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				if (!player.getCache().getString("bank_pin").equals(pin)) {
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
		} else if (menu == 2 && Constants.GameServer.WANT_BANK_PINS) {
			int bankPinMenu = showMenu(player, "Set a bank pin", "Change bank pin", "Delete bank pin");
			if (bankPinMenu == 0) {
				if (!player.getCache().hasKey("bank_pin")) {
					String bankPin = getBankPinInput(player);
					if (bankPin == null) {
						return;
					}
					try {
						PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
							player.getCache().store("bank_pin", bankPin);
							//ActionSender.sendBox(player, "Your new bank pin is " + bankPin, false);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			} else if (bankPinMenu == 1) {
				if (player.getCache().hasKey("bank_pin")) {
					String bankPin = getBankPinInput(player);
					if (bankPin == null) {
						return;
					}
					try {
						PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (!player.getCache().getString("bank_pin").equals(bankPin)) {
						ActionSender.sendBox(player, "Incorrect bank pin", false);
						return;
					}
					String changeTo = getBankPinInput(player);
					try {
						PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							changeTo = DataConversions.hashPassword(changeTo, result.getString("salt"));
							player.getCache().store("bank_pin", changeTo);
							//ActionSender.sendBox(player, "Your new bank pin is " + changeTo, false);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}


				} else {
					player.message("You don't have a bank pin");
				}
			} else if (bankPinMenu == 2) {
				if (player.getCache().hasKey("bank_pin")) {
					String bankPin = getBankPinInput(player);
					if (bankPin == null) {
						return;
					}
					try {
						PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
						statement.setString(1, player.getUsername());
						ResultSet result = statement.executeQuery();
						if (result.next()) {
							bankPin = DataConversions.hashPassword(bankPin, result.getString("salt"));
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					if (!player.getCache().getString("bank_pin").equals(bankPin)) {
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

		} else if ((menu == 2 || menu == 3) && Constants.GameServer.SPAWN_AUCTION_NPCS) {
			if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
				String pin = getBankPinInput(player);
				if (pin == null) {
					return;
				}
				try {
					PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
					statement.setString(1, player.getUsername());
					ResultSet result = statement.executeQuery();
					if (result.next()) {
						pin = DataConversions.hashPassword(pin, result.getString("salt"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				if (!player.getCache().getString("bank_pin").equals(pin)) {
					ActionSender.sendBox(player, "Incorrect bank pin", false);
					return;
				}
				player.setAttribute("bankpin", true);
				ActionSender.sendBox(player, "Bank pin correct", false);
			}
			Market.getInstance().addPlayerCollectItemsTask(player);
		}
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (inArray(n.getID(), BANKERS)) {
			if (command.equalsIgnoreCase("Bank") && Constants.GameServer.RIGHT_CLICK_BANK) {
				quickFeature(n, p, false);
			} else if (command.equalsIgnoreCase("Collect") && Constants.GameServer.SPAWN_AUCTION_NPCS) {
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
		if (player.getCache().hasKey("bank_pin") && !player.getAttribute("bankpin", false)) {
			String pin = getBankPinInput(player);
			if (pin == null) {
				return;
			}
			try {
				PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM openrsc_players WHERE `username`=?");
				statement.setString(1, player.getUsername());
				ResultSet result = statement.executeQuery();
				if (result.next()) {
					pin = DataConversions.hashPassword(pin, result.getString("salt"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			if (!player.getCache().getString("bank_pin").equals(pin)) {
				ActionSender.sendBox(player, "Incorrect bank pin", false);
				return;
			}
			player.setAttribute("bankpin", true);
			player.message("You have correctly entered your PIN");
		}
		if (auction) {
			Market.getInstance().addPlayerCollectItemsTask(player);
		} else {
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		}
	}
}
