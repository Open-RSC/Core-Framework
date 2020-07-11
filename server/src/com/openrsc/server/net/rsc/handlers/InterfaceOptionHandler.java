package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.content.clan.Clan;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.clan.ClanPlayer;
import com.openrsc.server.content.clan.ClanRank;
import com.openrsc.server.content.party.Party;
import com.openrsc.server.content.party.PartyInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.content.party.PartyRank;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class InterfaceOptionHandler implements PacketHandler {


	private static String[] badWords = {
		"fuck", "ass", "bitch", "admin", "mod", "dev", "developer", "nigger", "niger",
		"whore", "pussy", "porn", "penis", "chink", "faggot", "cunt", "clit", "cock"};

	@Override
	public void handlePacket(Packet packet, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}
		switch (packet.readByte()) {
			case 0:
				player.setAttribute("swap_cert", packet.readByte() == 1);
				break;
			case 1:
				player.setAttribute("swap_note", packet.readByte() == 1);
				break;
			case 2:
				handleBankSwap(player, packet);
				break;
			case 3:
				handleBankInsert(player, packet);
				break;
			case 4:
				handleInventoryInsert(player, packet);
				break;
			case 5:
				handleInventorySwap(player, packet);
				break;
			case 6:
				// Cancel Batch
				if (player.getConfig().BATCH_PROGRESSION) {
					player.interruptPlugins();
				}
				break;
			case 7:
				handleIronmanMode(player, packet);
				break;
			case 8:
				handleBankPinEntry(player, packet);
				break;
			case 10:
				if (!player.getConfig().SPAWN_AUCTION_NPCS) return;
				handleAuction(player, packet);
				break;
			case 11: // Clan Actions
				if (!player.getConfig().WANT_CLANS) return;
				handleClan(player, packet);
				break;
			case 12: // Party
				if (!player.getConfig().WANT_PARTIES) return;
				handleParty(player, packet);
				break;
		}
	}

	private void handleBankSwap(Player player, Packet packet) {
		int slot = packet.readInt();
		int to = packet.readInt();

		if (player.getBank().swap(slot, to)) {
			ActionSender.updateBankItem(player, slot, player.getBank().get(slot).getCatalogId(),
				player.getBank().get(slot).getAmount());
			ActionSender.updateBankItem(player, to, player.getBank().get(to).getCatalogId(),
				player.getBank().get(to).getAmount());
		}
	}

	private void handleBankInsert(Player player, Packet packet) {
		int slot = packet.readInt();
		int to = packet.readInt();
		if (player.getBank().insert(slot, to)) {
			ActionSender.showBank(player);
		}
	}

	private void handleInventoryInsert(Player player, Packet packet) {
		int slot = packet.readInt();
		int to = packet.readInt();

		player.getCarriedItems().getInventory().swap(slot, to);
	}

	private void handleInventorySwap(Player player, Packet packet) {
		int slot = packet.readInt();
		int to = packet.readInt();

		player.getCarriedItems().getInventory().insert(slot, to);
		ActionSender.sendInventory(player);
	}

	private void handleIronmanMode(Player player, Packet packet) {
		int secondary = (int) packet.readByte();
		if (secondary == 0) {
			int mode = (int) packet.readByte();
			if (mode < 0 || mode > 3) {
				player.setSuspiciousPlayer(true, "mode < 0 or mode > 3");
				return;
			}
			if (mode > -1) {
				if (mode == IronmanMode.Ironman.id()) {
					if (!player.getLocation().onTutorialIsland() && (player.getIronMan() <= IronmanMode.None.id() || player.getIronMan() > IronmanMode.Transfer.id())) {
						player.message("You cannot become an Iron Man after leaving Tutorial Island.");
						return;
					}
					if (player.getIronMan() == IronmanMode.Ironman.id()) {
						return;
					}
					if (!player.getLocation().onTutorialIsland()
						&& (player.getIronMan() == IronmanMode.Ultimate.id() || player.getIronMan() == IronmanMode.Hardcore.id())) {
						if (player.getIronManRestriction() == 0) {
							if (player.getCache().hasKey("bank_pin")) {
								Npc npc = ifnearvisnpc(player, 11, 799, 800, 801);
								if (npc != null) {
									ActionSender.sendHideIronManInterface(player);
									player.setAttribute("ironman_delete", true);
									player.setAttribute("ironman_mode", mode);
									npc.initializeTalkScript(player);
								} else {
									player.message("The Iron Men are currently busy");
								}
							}
						} else {
							player.message("Your account is set to permanent - you cannot remove your status");
						}
						return;
					}
					player.setIronMan(IronmanMode.Ironman.id());
				} else if (mode == IronmanMode.Ultimate.id()) {
					if (!player.getLocation().onTutorialIsland() && player.getIronMan() != IronmanMode.Ultimate.id()) {
						player.message("You cannot become an Ultimate Iron Man after leaving Tutorial Island.");
						return;
					}
					if (player.getIronMan() == IronmanMode.Ultimate.id()) {
						return;
					}
					player.setIronMan(IronmanMode.Ultimate.id());
				} else if (mode == IronmanMode.Hardcore.id()) {
					if (!player.getLocation().onTutorialIsland() && player.getIronMan() != IronmanMode.Hardcore.id()) {
						player.message("You cannot become a Hardcore Iron Man after leaving Tutorial Island.");
						return;
					}
					if (player.getIronMan() == IronmanMode.Hardcore.id()) {
						return;
					}
					player.setIronMan(IronmanMode.Hardcore.id());
				} else {
					if (player.getIronMan() == IronmanMode.None.id()) {
						return;
					}
					if (!player.getLocation().onTutorialIsland()
						&& (player.getIronMan() == IronmanMode.Ironman.id() || player.getIronMan() == IronmanMode.Ultimate.id()
						|| player.getIronMan() == IronmanMode.Hardcore.id())) {
						if (player.getIronManRestriction() == 0) {
							if (player.getCache().hasKey("bank_pin")) {
								Npc npc = ifnearvisnpc(player, 11, 799, 800, 801);
								if (npc != null) {
									ActionSender.sendHideIronManInterface(player);
									player.setAttribute("ironman_delete", true);
									player.setAttribute("ironman_mode", mode);
									npc.initializeTalkScript(player);
								} else {
									player.message("The Iron Men are currently busy");
								}
							}
						} else {
							player.message("Your account is set to permanent - you cannot remove your status");
						}
						return;
					}
					player.setIronMan(IronmanMode.None.id());
				}
				ActionSender.sendIronManMode(player);
			}
		} else if (secondary == 1) {
			int setting = (int) packet.readByte();
			if (setting < 0 || setting > 1) {
				player.setSuspiciousPlayer(true, "setting < 0 or setting > 1");
				return;
			}
			if (!player.getLocation().onTutorialIsland()) {
				player.message("You cannot change this setting now that you have completed the Tutorial.");
				return;
			}
			if (setting > -1) {
				if (player.getIronMan() == IronmanMode.None.id()) {
					return;
				}
				if (setting == 0) {
					if (!player.getCache().hasKey("bank_pin")) {
						Npc npc = ifnearvisnpc(player, 11, 799, 800, 801);
						if (npc != null) {
							ActionSender.sendHideIronManInterface(player);
							player.setAttribute("ironman_pin", true);
							npc.initializeTalkScript(player);
						} else {
							player.message("The Iron Men are currently busy");
						}
					} else {
						player.setIronManRestriction(0);
					}
				} else {
					player.setIronManRestriction(1);
				}
				ActionSender.sendIronManMode(player);
			}
		}
	}

	private void handleBankPinEntry(Player player, Packet packet) {
		int action = packet.readByte();
		if (action == 0) {
			String bankpin = packet.readString();
			if (bankpin.length() != 4) {
				return;
			}
			player.setAttribute("bank_pin_entered", bankpin);
		} else if (action == 1) {
			player.setAttribute("bank_pin_entered", "cancel");
		}
	}

	private void handleAuction(Player player, Packet packet) {
		if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
			|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
			player.message("As an Iron Man, you cannot use the Auction.");
			return;
		}
		if (player.getWorld().getServer().getTimeUntilShutdown() > 0) {
			player.message("Auction house is disabled until server restart!");
			return;
		}
		/* Set true when auctioneer opens AH window for the player */
		if (!player.getAttribute("auctionhouse", false)) {
			return;
		}

		if (player.getTotalLevel() < 100) {
			ActionSender.sendBox(player,"You must have 100 total skill before using the auction house.", false);
			return;
		}

		int type = packet.readByte();
		switch (type) {
			case 0: /* Buy */
				auctionBuyItem(player, packet);
				break;

			case 1: /* Create auction */
				auctionCreate(player, packet);
				break;
			case 2:
				auctionCancel(player, packet);
				break;
			case 3:
				auctionRefresh(player);
				break;
			case 4:
				// Close Auction House
				player.setAttribute("auctionhouse", false);
				break;
			case 5:
				auctionModeratorDelete(player, packet);
				break;
		}

		player.setLastExchangeTime();
	}

	private void auctionBuyItem(Player player, Packet packet) {
		int auctionBuyID = packet.readInt();
		int amountBuy = packet.readInt();
		if (System.currentTimeMillis() - player.getLastExchangeTime() < 3000) {
			ActionSender.sendBox(player, "@ora@[Auction House - Warning] % @whi@ You are acting too quickly, please wait 3 seconds.", false);
			return;
		}

		player.getWorld().getMarket().addBuyAuctionItemTask(player, auctionBuyID, amountBuy);
	}

	private void auctionCreate(Player player, Packet packet) {
		int catalogID = packet.readInt();
		int amount = packet.readInt();
		int price = packet.readInt();
		if (System.currentTimeMillis() - player.getLastExchangeTime() < 3000) {
			ActionSender.sendBox(player,"@ora@[Auction House - Warning]@whi@ You are acting too quickly, please wait 3 seconds.", false);
			return;
		}
		player.getWorld().getMarket().addNewAuctionItemTask(player, catalogID, amount, price);
	}

	private void auctionCancel(Player player, Packet packet) {
		int auctionID = packet.readInt();
		if (System.currentTimeMillis() - player.getLastExchangeTime() < 3000) {
			ActionSender.sendBox(player,"@ora@[Auction House - Warning]@whi@ You are acting too quickly, please wait 3 seconds.", false);
			return;
		}
		player.getWorld().getMarket().addCancelAuctionItemTask(player, auctionID);
	}

	private void auctionRefresh(Player player) {
		if (System.currentTimeMillis() - player.getAttribute("ah_refresh", (System.currentTimeMillis() - 5000)) < 5000) {
			ActionSender.sendBox(player,"@ora@[Auction House - Warning]@whi@ You are acting too quickly, please wait 5 seconds.", false);
			return;
		}

		player.setAttribute("ah_refresh", System.currentTimeMillis());

		player.message("@gre@[Auction House]@whi@ List has been refreshed!");
		ActionSender.sendOpenAuctionHouse(player);
	}

	private void auctionModeratorDelete(Player player, Packet packet) {
		int auctionID = packet.readInt();
		player.getWorld().getMarket().addModeratorDeleteItemTask(player, auctionID);
	}

	private void handleClan(Player player, Packet packet) {
		int actionType = packet.readByte();
		switch (actionType) {
			case 0: // CREATE CLAN
				String clanName = packet.readString();
				String clanTag = packet.readString();
				if (clanName.length() < 2) {
					ActionSender.sendBox(player, "Clan name must be at least 2 characters in length", false);
					return;
				} else if (clanName.length() > 16) {
					ActionSender.sendBox(player, "Clan name length cannot exceed 16 characters in length", false);
					return;
				} else if (clanTag.length() < 2) {
					ActionSender.sendBox(player, "Clan tag need to be minimum 2 characters", false);
					return;
				} else if (clanTag.length() > 5) {
					ActionSender.sendBox(player, "Clan tag maximum length is 5 characters", false);
					return;
				} else if (!clanName.matches("^[\\p{IsAlphabetic}\\p{IsDigit}\\p{Space}]+$") || !clanTag.matches("^[\\p{IsAlphabetic}\\p{IsDigit}\\p{Space}]+$")) {
					ActionSender.sendBox(player, "Clan name and Clan tag can only contain regular letters and numbers", false);
					return;
				}
				for (String s : badWords) { // check every word
					if (clanName.contains(s) || clanTag.contains(s)) {
						ActionSender.sendBox(player, "Bad clan name or clan tag, try with something else", false);
						return;
					}
				}
				if (player.getClan() != null) {
					ActionSender.sendBox(player, "You are already in a clan", false);
					return;
				}

				if (player.getWorld().getClanManager().getClan(clanName) == null && player.getWorld().getClanManager().getClan(clanTag) == null) {
					Clan clan = new Clan(player.getWorld());
					clan.setClanName(clanName);
					clan.setClanTag(clanTag);

					ClanPlayer clanMember = clan.addPlayer(player);
					clanMember.setRank(ClanRank.LEADER);
					clan.setLeader(clanMember);

					player.getWorld().getClanManager().createClan(clan);
					player.message("You have created clan: " + clanName);
				} else {
					ActionSender.sendBox(player, "There is already a clan with this Clan Name or Clan Tag", false);
				}

				break;
			case 1:
				if (player.getClan() != null) {
					player.getClan().removePlayer(player.getUsername());
				}
				break;
			case 2:
				String playerInvited = packet.readString();
				Player invited = player.getWorld().getPlayer(DataConversions.usernameToHash(playerInvited));
						/*if (!player.getClan().isAllowed(1, player)) {
							player.message("You are not allowed to invite into clan.");
							return;
						}*/
				if (invited != null) {
					ClanInvite.createClanInvite(player, invited);
				} else {
					ActionSender.sendBox(player, "Player is not online or could not be found!", false);
				}
				break;
			case 3:
				if (player.getActiveClanInvite() != null) {
					player.getActiveClanInvite().accept();
				}
				break;
			case 4:
				if (player.getActiveClanInvite() != null) {
					player.getActiveClanInvite().decline();
				}
				break;
			case 5: // KICK
				if (player.getClan() != null) {
					String playerToKick = packet.readString();
					if (!player.getClan().isAllowed(0, player)) {
						player.message("You are not allowed to kick.");
						return;
					}
					if (player.getClan().getLeader().getUsername().equals(playerToKick)) {
						player.message("You can't kick the leader.");
						return;
					}
					player.getClan().removePlayer(playerToKick);
				}
				break;
			case 6: // RANK plaayer
				if (player.getClan() != null) {
					String playerRank = packet.readString();
					int rank = packet.readByte();
					if (rank >= 3) {
						rank = 0;
					}
					if (!player.getClan().getLeader().getUsername().equals(player.getUsername().replaceAll("_", " "))) {
						player.message("You are not the leader of this clan");
						return;
					}
					if (player.getClan().getLeader().getUsername().equals(playerRank)) {
						player.message("You are already the leader of the clan");
						return;
					}
					player.getClan().updateRankPlayer(player, playerRank, rank);
				}
				break;
			case 7: // CLAN SETTINGS
				if (player.getClan() != null) {
					int settingPreference = packet.readByte();
					if (settingPreference > 3) {
						return;
					}
					int state = packet.readByte();
					if (state >= 3) {
						state = 0;
					}
					if (!player.getClan().getLeader().getUsername().equals(player.getUsername())) {
						player.message("You are not the leader of this clan");
						return;
					}
					if (settingPreference == 0) {
						if (player.getClan().getKickSetting() == state) {
							return;
						}
						player.getClan().setKickSetting(state);
					} else if (settingPreference == 1) {
						if (player.getClan().getInviteSetting() == state) {
							return;
						}
						player.getClan().setInviteSetting(state);
					} else if (settingPreference == 2) {
						if (player.getClan().getAllowSearchJoin() == state) {
							return;
						}
						player.getClan().setAllowSearchJoin(state);
					}
					player.message("[CLAN]: You have updated clan settings");
					player.getClan().updateClanSettings();

				}
				break;
			case 8:
				ActionSender.sendClans(player);
				break;
			case 9:

				break;
		}
	}

	private void handleParty(Player player, Packet packet) {
		int actionType2 = packet.readByte();
		switch (actionType2) {
			case 0: // CREATE PARTY
				if (player.getParty() != null) {
					ActionSender.sendBox(player, "Leave your current party before joining another", false);
					return;
				}
				if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
					player.message("You are an Iron Man. You stand alone.");
					return;
				}

				Party party = new Party(player.getWorld());
				//party.setPartyName(partyName);
				//party.setPartyTag(partyTag);

				PartyPlayer partyMember = party.addPlayer(player);
				partyMember.setRank(PartyRank.LEADER);
				party.setLeader(partyMember);

				player.getWorld().getPartyManager().createParty(party);
				player.message("You have created a party: ");

				break;
			case 1:
				if (player.getParty() != null) {
					player.getParty().removePlayer(player.getUsername());
				}
				break;
			case 2:
				Player invited = player.getWorld().getPlayer(packet.readShort());
				if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
					player.message("You are an Iron Man. You stand alone.");
					return;
				}
				if (player.getParty() == null) {
					String partyName = packet.readString();
					String partyTag = packet.readString();
					Party party1 = new Party(player.getWorld());
					party1.setPartyName(partyName);
					party1.setPartyTag(partyTag);

					PartyPlayer partyMember1 = party1.addPlayer(player);
					partyMember1.setRank(PartyRank.LEADER);
					party1.setLeader(partyMember1);

					player.getWorld().getPartyManager().createParty(party1);
					player.message("You have created a party: ");
				}
				if (player.getParty().getInviteSetting() == 1 && !player.getParty().getLeader().getUsername().equalsIgnoreCase(player.getUsername())) {
					player.message("Only the party owner can invite players to this party");
					return;
				}
				if (player.getParty().getInviteSetting() == 2 && !player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.GENERAL) && !player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
					player.message("Only the party owner can invite players to this party");
					return;
				}
				if (invited != null) {
					PartyInvite.createPartyInvite(player, invited);
				} else {
					ActionSender.sendBox(player, "Player is not online or could not be found!", false);
					return;
				}
				break;
			case 3:
				if (player.getActivePartyInvite() != null) {
					player.getActivePartyInvite().accept();
				}
				break;
			case 4:
				if (player.getActivePartyInvite() != null) {
					player.getActivePartyInvite().decline();
				}
				break;
			case 5: // kick
				if (player.getParty() != null) {
					String playerToKick = packet.readString();
							/*if (!player.getParty().isAllowed(0, player)) {
								player.message("You are not allowed to kick from this party.");
								return;
							}*/
					if (player.getParty().getLeader().getUsername().equals(playerToKick)) {
						player.message("You can't kick the leader of the party.");
						return;
					}
					player.getParty().removePlayer(playerToKick);
				}
				break;
			case 6: // rank
				if (player.getParty() != null) {
					String playerRank = packet.readString();
					int rank = packet.readByte();
					if (rank >= 3) {
						rank = 0;
					}
					if (!player.getParty().getLeader().getUsername().equals(player.getUsername().replaceAll("_", " "))) {
						player.message("You are not the leader of this party");
						return;
					}
					if (player.getParty().getLeader().getUsername().equals(playerRank)) {
						player.message("You are already the leader of the party");
						return;
					}
					player.getParty().updateRankPlayer(player, playerRank, rank);
				}
				break;
			case 7: // Party SETTINGS
				if (player.getParty() != null) {
					int settingPreference = packet.readByte();
					if (settingPreference > 3) {
						return;
					}
					int state = packet.readByte();
					if (state >= 3) {
						state = 0;
					}
					if (!player.getParty().getLeader().getUsername().equals(player.getUsername())) {
						player.message("You are not the leader of this party");
						return;
					}
					if (settingPreference == 0) {
						if (player.getParty().getKickSetting() == state) {
							return;
						}
						player.getParty().setKickSetting(state);
					} else if (settingPreference == 1) {
						if (player.getParty().getInviteSetting() == state) {
							return;
						}
						player.getParty().setInviteSetting(state);
					} else if (settingPreference == 2) {
						if (player.getParty().getAllowSearchJoin() == state) {
							return;
						}
						player.getParty().setAllowSearchJoin(state);
					}
					player.message("[PARTY]: You have updated party settings");
					player.getParty().updatePartySettings();

				}
				break;
			case 8:
				ActionSender.sendParties(player);
				break;
			case 9:
				String playerInvited2 = packet.readString();
				Player invited2 = player.getWorld().getPlayer(DataConversions.usernameToHash(playerInvited2));

				if(player.getParty() != null){
					if (player.getParty().getInviteSetting() == 1 && !player.getParty().getLeader().getUsername().equalsIgnoreCase(player.getUsername())) {
						player.message("Only the party owner can invite players to this party");
						return;
					}
					if (player.getParty().getInviteSetting() == 2 && !player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.GENERAL) && !player.getParty().getPlayer(player.getUsername()).getRank().equals(PartyRank.LEADER)) {
						player.message("Only the party owner can invite players to this party");
						return;
					}
				}
				if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
					player.message("You are an Iron Man. You stand alone.");
					return;
				}
				if (player.getParty() == null) {
					if (invited2 == null) {
						ActionSender.sendBox(player,
							"@lre@Party: %"
								+ " %"
								+ "This player is not online or does not exist %"
							, true);
						return;
					}
					if (invited2.equals(player)) {
						ActionSender.sendBox(player,
							"@lre@Party: %"
								+ " %"
								+ "You cannot invite yourself %"
							, true);
						return;
					}
					if (invited2.getParty() != null) {
						if (player.getParty() == invited2.getParty()) {
							player.message(invited2.getUsername() + " is already in your party");
							return;
						} else {
							invited2.message("@yel@" + player.getUsername() + "@whi@ tried to send you a party invite, but you are already in a party");
							ActionSender.sendBox(player,
								"@lre@Party: %"
									+ " %"
									+ invited2.getUsername() + " is already in a party %"
								, true);
							return;
						}
					} else {
						String partyName = packet.readString();
						String partyTag = packet.readString();
						Party party1 = new Party(player.getWorld());
						party1.setPartyName(partyName);
						party1.setPartyTag(partyTag);

						PartyPlayer partyMember1 = party1.addPlayer(player);
						partyMember1.setRank(PartyRank.LEADER);
						party1.setLeader(partyMember1);

						player.getWorld().getPartyManager().createParty(party1);
						player.message("You have created a party: ");
					}
				} else {
					if (invited2 == null) {
						ActionSender.sendBox(player,
							"@lre@Party: %"
								+ " %"
								+ "This player is not online or does not exist %"
							, true);
						return;
					}
				}
				if (invited2 != null) {
					PartyInvite.createPartyInvite(player, invited2);
				} else {
					ActionSender.sendBox(player, "Player is not online or could not be found!", false);
					return;
				}
				break;
		}
	}
}
