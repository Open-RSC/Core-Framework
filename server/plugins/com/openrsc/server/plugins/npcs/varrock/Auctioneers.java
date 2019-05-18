package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.Constants;
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

public class Auctioneers implements TalkToNpcExecutiveListener, TalkToNpcListener, NpcCommandListener, NpcCommandExecutiveListener {

	public static int AUCTIONEER = 794;
	public static int AUCTION_CLERK = 795;

	@Override
	public boolean blockTalkToNpc(final Player player, final Npc npc) {
		if (npc.getID() == AUCTIONEER) {
			return true;
		}
		if (npc.getID() == AUCTION_CLERK) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player player, final Npc npc) {
		npcTalk(player, npc, "Hello");
		int menu;
		if (npc.getID() == AUCTION_CLERK) {
			menu = showMenu(player, npc, "I'd like to browse the auction house", "Can you teleport me to Varrock Centre");
		} else {
			menu = showMenu(player, npc, "I'd like to browse the auction house");
		}
		if (menu == 0) {
			if (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3)) {
				player.message("As an Iron Man, you cannot use the Auction.");
				return;
			}
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
						e.printStackTrace();
					}
					if (!player.getCache().getString("bank_pin").equals(pin)) {
						ActionSender.sendBox(player, "Incorrect bank pin", false);
						return;
					}
					player.setAttribute("bankpin", true);
					ActionSender.sendBox(player, "Bank pin correct", false);
				}
			}
			npcTalk(player, npc, "Certainly " + (player.isMale() ? "Sir" : "Miss"));
			player.setAttribute("auctionhouse", true);
			ActionSender.sendOpenAuctionHouse(player);
		} else if (menu == 1) {
			npcTalk(player, npc, "Yes of course " + (player.isMale() ? "Sir" : "Miss"),
				"the costs is 1,000 coins");
			int tMenu = showMenu(player, npc, "Teleport me", "I'll stay here");
			if (tMenu == 0) {
				if (hasItem(player, 10, 1000)) {
					removeItem(player, 10, 1000);
					player.teleport(133, 508);
				} else {
					player.message("You don't seem to have enough coins");
				}
			}
		}
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		if ((n.getID() == AUCTIONEER) && command.equalsIgnoreCase("Auction")) {
			return true;
		}
		if (n.getID() == AUCTION_CLERK && (command.equalsIgnoreCase("Teleport") || command.equalsIgnoreCase("Auction"))) {
			return true;
		}
		return false;
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (n.getID() == AUCTIONEER) {
			if (command.equalsIgnoreCase("Auction")) {
				if (p.isIronMan(1) || p.isIronMan(2) || p.isIronMan(3)) {
					p.message("As an Iron Man, you cannot use the Auction.");
					return;
				}
				if (Constants.GameServer.WANT_BANK_PINS) {
					if (p.getCache().hasKey("bank_pin") && !p.getAttribute("bankpin", false)) {
						String pin = getBankPinInput(p);
						if (pin == null) {
							return;
						}
						try {
							PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
							statement.setString(1, p.getUsername());
							ResultSet result = statement.executeQuery();
							if (result.next()) {
								pin = DataConversions.hashPassword(pin, result.getString("salt"));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						if (!p.getCache().getString("bank_pin").equals(pin)) {
							ActionSender.sendBox(p, "Incorrect bank pin", false);
							return;
						}
						p.setAttribute("bankpin", true);
						ActionSender.sendBox(p, "Bank pin correct", false);
					}
				}
				p.message("Welcome to the auction house " + (p.isMale() ? "Sir" : "Miss") + "!");
				p.setAttribute("auctionhouse", true);
				ActionSender.sendOpenAuctionHouse(p);
			}
		} else if (n.getID() == AUCTION_CLERK) {
			if (command.equalsIgnoreCase("Auction")) {
				if (p.isIronMan(1) || p.isIronMan(2) || p.isIronMan(3)) {
					p.message("As an Iron Man, you cannot use the Auction.");
					return;
				}
				if (Constants.GameServer.WANT_BANK_PINS) {
					if (p.getCache().hasKey("bank_pin") && !p.getAttribute("bankpin", false)) {
						String pin = getBankPinInput(p);
						if (pin == null) {
							return;
						}
						try {
							PreparedStatement statement = DatabaseConnection.getDatabase().prepareStatement("SELECT salt FROM " + Constants.GameServer.MYSQL_TABLE_PREFIX + "players WHERE `username`=?");
							statement.setString(1, p.getUsername());
							ResultSet result = statement.executeQuery();
							if (result.next()) {
								pin = DataConversions.hashPassword(pin, result.getString("salt"));
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
						if (!p.getCache().getString("bank_pin").equals(pin)) {
							ActionSender.sendBox(p, "Incorrect bank pin", false);
							return;
						}
						p.setAttribute("bankpin", true);
						ActionSender.sendBox(p, "Bank pin correct", false);
					}
				}
				p.message("Welcome to the auction house " + (p.isMale() ? "Sir" : "Miss") + "!");
				p.setAttribute("auctionhouse", true);
				ActionSender.sendOpenAuctionHouse(p);
			} else if (command.equalsIgnoreCase("Teleport")) {
				n.face(p);
				p.face(n);
				message(p, n, 1300, "Would you like to be teleport to Varrock centre for 1000 gold?");
				int yesOrNo = showMenu(p, "Yes please!", "No thanks.");
				if (yesOrNo == 0) {
					if (hasItem(p, 10, 1000)) {
						removeItem(p, 10, 1000);
						p.teleport(133, 508);
						p.message("You have been teleported to the Varrock Centre");
					} else {
						p.message("You don't seem to have enough coins");
					}
				} else if (yesOrNo == 1) {
					p.message("You decide to stay where you are located.");
				}
			}
		}
	}
}
