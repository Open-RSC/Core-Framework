package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.constants.custom.*;
import com.openrsc.server.content.clan.Clan;
import com.openrsc.server.content.clan.ClanInvite;
import com.openrsc.server.content.clan.ClanPlayer;
import com.openrsc.server.content.clan.ClanRank;
import com.openrsc.server.content.party.Party;
import com.openrsc.server.content.party.PartyInvite;
import com.openrsc.server.content.party.PartyPlayer;
import com.openrsc.server.content.party.PartyRank;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.OptionsStruct;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.ifnearvisnpc;

public class InterfaceOptionHandler implements PayloadProcessor<OptionsStruct, OpcodeIn> {


	private static String[] badWords = {
		"fuck", "ass", "bitch", "admin", "mod", "dev", "developer", "nigger", "niger",
		"whore", "pussy", "porn", "penis", "chink", "faggot", "cunt", "clit", "cock"};

	@Override
	public void process(OptionsStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}
		final InterfaceOptions option = InterfaceOptions.getById(payload.index);
		switch (option) {
			case SWAP_CERT:
				player.setAttribute("swap_cert", payload.value == 1);
				break;
			case SWAP_NOTE:
				player.setAttribute("swap_note", payload.value == 1);
				break;
			case BANK_SWAP:
				handleBankSwap(player, payload);
				break;
			case BANK_INSERT:
				handleBankInsert(player, payload);
				break;
			case INVENTORY_INSERT:
				handleInventoryInsert(player, payload);
				break;
			case INVENTORY_SWAP:
				handleInventorySwap(player, payload);
				break;
			case CANCEL_BATCH:
				// Cancel Batch
				if (player.getConfig().BATCH_PROGRESSION) {
					player.interruptPlugins();
				}
				break;
			case IRONMAN_MODE:
				handleIronmanMode(player, payload);
				break;
			case BANK_PIN:
				handleBankPinEntry(player, payload);
				break;
			case AUCTION:
				if (!player.getConfig().SPAWN_AUCTION_NPCS) return;
				handleAuction(player, payload);
				break;
			case CLAN: // Clan Actions
				if (!player.getConfig().WANT_CLANS) return;
				handleClan(player, payload);
				break;
			case PARTY: // Party
				if (!player.getConfig().WANT_PARTIES) return;
				handleParty(player, payload);
				break;
			case POINTS: //OpenPK Points
				if (!player.getConfig().WANT_OPENPK_POINTS) return;
				handlePoints(player, payload);
				break;
		}
	}

	private void handleBankSwap(Player player, OptionsStruct payload) {
		int slot = payload.slot;
		int to = payload.to;

		if (player.getBank().swap(slot, to)) {
			ActionSender.updateBankItem(player, slot, player.getBank().get(slot),
				player.getBank().get(slot).getAmount());
			ActionSender.updateBankItem(player, to, player.getBank().get(to),
				player.getBank().get(to).getAmount());
		}
	}

	private void handleBankInsert(Player player, OptionsStruct payload) {
		int slot = payload.slot;
		int to = payload.to;
		if (player.getBank().insert(slot, to)) {
			ActionSender.showBank(player);
		}
	}

	private void handleInventoryInsert(Player player, OptionsStruct payload) {
		int slot = payload.slot;
		int to = payload.to;

		player.getCarriedItems().getInventory().swap(slot, to);
	}

	private void handleInventorySwap(Player player, OptionsStruct payload) {
		int slot = payload.slot;
		int to = payload.to;

		player.getCarriedItems().getInventory().insert(slot, to);
		ActionSender.sendInventory(player);
	}

	private void handleIronmanMode(Player player, OptionsStruct payload) {
		int secondary = payload.value;
		if (secondary == 0) {
			int mode = payload.value2;
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
			int setting = payload.value2;
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

	private void handleBankPinEntry(Player player, OptionsStruct payload) {
		int action = payload.value;
		if (action == 0) {
			String bankpin = payload.pin;
			if (bankpin.length() != 4) {
				return;
			}
			player.setAttribute("bank_pin_entered", bankpin);
		} else if (action == 1) {
			player.setAttribute("bank_pin_entered", "cancel");
		}
	}

	private void handleAuction(Player player, OptionsStruct payload) {
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

		int type = payload.value;
		final AuctionOptions auctionOption = AuctionOptions.getById(type);
		switch (auctionOption) {
			case BUY:
				auctionBuyItem(player, payload);
				break;

			case CREATE:
				auctionCreate(player, payload);
				break;
			case ABORT:
				auctionCancel(player, payload);
				break;
			case REFRESH:
				auctionRefresh(player);
				break;
			case CLOSE:
				// Close Auction House
				player.setAttribute("auctionhouse", false);
				break;
			case DELETE:
				auctionModeratorDelete(player, payload);
				break;
		}

		player.setLastExchangeTime();
	}

	private void auctionBuyItem(Player player, OptionsStruct payload) {
		int auctionBuyID = payload.id;
		int amountBuy = payload.amount;
		if (System.currentTimeMillis() - player.getLastExchangeTime() < 3000) {
			ActionSender.sendBox(player, "@ora@[Auction House - Warning] % @whi@ You are acting too quickly, please wait 3 seconds.", false);
			return;
		}

		player.getWorld().getMarket().addBuyAuctionItemTask(player, auctionBuyID, amountBuy);
	}

	private void auctionCreate(Player player, OptionsStruct payload) {
		int catalogID = payload.id;
		int amount = payload.amount;
		int price = payload.price;
		if (System.currentTimeMillis() - player.getLastExchangeTime() < 3000) {
			ActionSender.sendBox(player,"@ora@[Auction House - Warning]@whi@ You are acting too quickly, please wait 3 seconds.", false);
			return;
		}
		player.getWorld().getMarket().addNewAuctionItemTask(player, catalogID, amount, price);
	}

	private void auctionCancel(Player player, OptionsStruct payload) {
		int auctionID = payload.id;
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

	private void auctionModeratorDelete(Player player, OptionsStruct payload) {
		int auctionID = payload.id;
		player.getWorld().getMarket().addModeratorDeleteItemTask(player, auctionID);
	}

	private void handleClan(Player player, OptionsStruct payload) {
		int actionType = payload.value;
		final ClanOptions clanOption = ClanOptions.getById(actionType);
		switch (clanOption) {
			case CREATE:
				String clanName = payload.name;
				String clanTag = payload.tag;
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
			case LEAVE:
				if (player.getClan() != null) {
					player.getClan().removePlayer(player.getUsername());
				}
				break;
			case INVITE_PLAYER:
				String playerInvited = payload.player;
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
			case ACCEPT_INVITE:
				if (player.getActiveClanInvite() != null) {
					player.getActiveClanInvite().accept();
				}
				break;
			case DECLINE_INVITE:
				if (player.getActiveClanInvite() != null) {
					player.getActiveClanInvite().decline();
				}
				break;
			case KICK_PLAYER:
				if (player.getClan() != null) {
					String playerToKick = payload.player;
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
			case RANK_PLAYER:
				if (player.getClan() != null) {
					String playerRank = payload.player;
					int rank = payload.value2;
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
			case CLAN_SETTINGS:
				if (player.getClan() != null) {
					int settingPreference = payload.value2;
					if (settingPreference > 3) {
						return;
					}
					int state = payload.value3;
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
			case SEND_CLAN_INFO:
				ActionSender.sendClans(player);
				break;
		}
	}

	private void handleParty(Player player, OptionsStruct payload) {
		int actionType2 = payload.value;
		final PartyOptions partyOption = PartyOptions.getById(actionType2);
		switch (partyOption) {
			case INIT: // CREATE PARTY
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
			case LEAVE:
				if (player.getParty() != null) {
					player.getParty().removePlayer(player.getUsername());
				}
				break;
			case CREATE_OR_INVITE:
				Player invited = player.getWorld().getPlayer(payload.id);
				if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
					player.message("You are an Iron Man. You stand alone.");
					return;
				}
				if (player.getParty() == null) {
					String partyName = payload.name;
					String partyTag = payload.tag;
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
			case ACCEPT_INVITE:
				if (player.getActivePartyInvite() != null) {
					player.getActivePartyInvite().accept();
				}
				break;
			case DECLINE_INVITE:
				if (player.getActivePartyInvite() != null) {
					player.getActivePartyInvite().decline();
				}
				break;
			case KICK_PLAYER:
				if (player.getParty() != null) {
					String playerToKick = payload.player;
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
			case RANK_PLAYER:
				if (player.getParty() != null) {
					String playerRank = payload.player;
					int rank = payload.value2;
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
			case PARTY_SETTINGS:
				if (player.getParty() != null) {
					int settingPreference = payload.value2;
					if (settingPreference > 3) {
						return;
					}
					int state = payload.value3;
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
			case SEND_PARTY_INFO:
				ActionSender.sendParties(player);
				break;
			case INVITE_PLAYER_OR_MAKE:
				String playerInvited2 = payload.player;
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
						String partyName = payload.name;
						String partyTag = payload.tag;
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

	private void handlePoints(Player player, OptionsStruct payload) {
		int option = payload.value;
		if (payload.amount < 0) {
			player.message("Please enter a positive number.");
			return;
		}
		switch (PointsOptions.getById(option)) {
			case REDUCE_DEFENSE:
				int amount1 = payload.amount;
				int amountx1 = amount1 * 4;
				if (!checkReduceLevelReqs(player, amountx1, Skill.DEFENSE.id())) {
					return;
				}
				player.getSkills().reduceExperience(Skill.DEFENSE.id(), amountx1);
				player.getSkills().reduceExperience(Skill.HITS.id(), amountx1 / 3);
				if(player.getSkills().getMaxStat(Skill.HITS.id()) < 10) {
					player.getSkills().setSkill(Skill.HITS.id(), 10, 4616);
				}
				player.addOpenPkPoints(amount1);
				ActionSender.sendPoints(player);
				player.checkEquipment();
			break;
			case INCREASE_DEFENSE:
				int amount = payload.amount;
				int amountx = amount * 4;
				if (!checkIncreaseLevelReqs(player, amount)) {
					return;
				}
				player.getSkills().addExperience(Skill.DEFENSE.id(), amountx);
				player.getSkills().addExperience(Skill.HITS.id(), amountx / 3);
				player.subtractOpenPkPoints(amount);
				ActionSender.sendPoints(player);
			break;
			case INCREASE_ATTACK:
				int amount0 = payload.amount;
				int amountx0 = amount0 * 4;
				if (!checkIncreaseLevelReqs(player, amount0)) {
					return;
				}
				player.getSkills().addExperience(Skill.ATTACK.id(), amountx0);
				player.getSkills().addExperience(Skill.HITS.id(), amountx0 / 3);
				player.subtractOpenPkPoints(amount0);
				ActionSender.sendPoints(player);
			break;
			case INCREASE_STRENGTH:
				int amount2 = payload.amount;
				int amountx2 = amount2 * 4;
				if (!checkIncreaseLevelReqs(player, amount2)) {
					return;
				}
				player.getSkills().addExperience(Skill.STRENGTH.id(), amountx2);
				player.getSkills().addExperience(Skill.HITS.id(), amountx2 / 3);
				player.subtractOpenPkPoints(amount2);
				ActionSender.sendPoints(player);
			break;
			case INCREASE_RANGED:
				int amount3 = payload.amount;
				int amountx3 = amount3 * 4;
				if (!checkIncreaseLevelReqs(player, amount3)) {
					return;
				}
				player.getSkills().addExperience(Skill.RANGED.id(), amountx3);
				player.subtractOpenPkPoints(amount3);
				ActionSender.sendPoints(player);
			break;
			case INCREASE_PRAYER:
				int amount4 = payload.amount;
				int amountx4 = amount4 * 4;
				if (!checkIncreaseLevelReqs(player, amount4)) {
					return;
				}
				player.getSkills().addExperience(Skill.PRAYER.id(), amountx4);
				player.subtractOpenPkPoints(amount4);
				ActionSender.sendPoints(player);
			break;
			case INCREASE_MAGIC:
				int amount5 = payload.amount;
				int amountx5 = amount5 * 4;
				if (!checkIncreaseLevelReqs(player, amount5)) {
					return;
				}
				player.getSkills().addExperience(Skill.MAGIC.id(), amountx5);
				player.subtractOpenPkPoints(amount5);
				ActionSender.sendPoints(player);
			break;
			case REDUCE_ATTACK:
				int amount00 = payload.amount;
				int amountx00 = amount00 * 4;
				if (!checkReduceLevelReqs(player, amountx00, Skill.ATTACK.id())) {
					return;
				}
				player.getSkills().reduceExperience(Skill.ATTACK.id(), amountx00);
				player.getSkills().reduceExperience(Skill.HITS.id(), amountx00 / 3);
				if(player.getSkills().getMaxStat(Skill.HITS.id()) < 10) {
					player.getSkills().setSkill(Skill.HITS.id(), 10, 4616);
				}
				player.addOpenPkPoints(amount00);
				ActionSender.sendPoints(player);
				player.checkEquipment();
			break;
			case REDUCE_STRENGTH:
				int amount22 = payload.amount;
				int amountx22 = amount22 * 4;
				if (!checkReduceLevelReqs(player, amountx22, Skill.STRENGTH.id())) {
					return;
				}
				player.getSkills().reduceExperience(Skill.STRENGTH.id(), amountx22);
				player.getSkills().reduceExperience(Skill.HITS.id(), amountx22 / 3);
				if(player.getSkills().getMaxStat(Skill.HITS.id()) < 10) {
					player.getSkills().setSkill(Skill.HITS.id(), 10, 4616);
				}
				player.addOpenPkPoints(amount22);
				ActionSender.sendPoints(player);
				player.checkEquipment();
			break;
			case REDUCE_RANGED:
				int amount33 = payload.amount;
				int amountx33 = amount33 * 4;
				if (!checkReduceLevelReqs(player, amountx33, Skill.RANGED.id())) {
					return;
				}
				player.getSkills().reduceExperience(Skill.RANGED.id(), amountx33);
				player.addOpenPkPoints(amount33);
				ActionSender.sendPoints(player);
				player.checkEquipment();
			break;
			case REDUCE_PRAYER:
				int amount44 = payload.amount;
				int amountx44 = amount44 * 4;
				if (!checkReduceLevelReqs(player, amountx44, Skill.PRAYER.id())) {
					return;
				}
				player.getSkills().reduceExperience(Skill.PRAYER.id(), amountx44);
				player.addOpenPkPoints(amount44);
				ActionSender.sendPoints(player);
				player.checkEquipment();
			break;
			case REDUCE_MAGIC:
				int amount55 = payload.amount;
				int amountx55 = amount55 * 4;
				if (!checkReduceLevelReqs(player, amountx55, Skill.MAGIC.id())) {
					return;
				}
				player.getSkills().reduceExperience(Skill.MAGIC.id(), amountx55);
				player.addOpenPkPoints(amount55);
				ActionSender.sendPoints(player);
				player.checkEquipment();
			break;
			case POINTS_TO_GP:
				int amount28 = payload.amount;
				if(player.getDuel().isDuelActive()){
					player.message("You cannot do that while dueling");
					return;
				}
				if(player.inCombat()){
					player.message("You cannot do that whilst fighting");
					return;
				}
				if(player.getOpenPkPoints() < amount28 * player.getConfig().OPENPK_POINTS_TO_GP_RATIO){
					player.message("You do not have enough points");
					return;
				}
				Item item = new Item(ItemId.COINS.id(), amount28);
				if(player.getCarriedItems().getInventory().canHold(item)){
					player.getCarriedItems().getInventory().add(item, false);
					ActionSender.sendInventory(player);
				} else {
					player.getWorld().registerItem(
					new GroundItem(player.getWorld(), ItemId.COINS.id(), player.getX(), player.getY(), amount28, player),
					player.getConfig().GAME_TICK * 145);
					player.message("You don't have room to hold the gp. It falls to the ground!");
				}
				player.subtractOpenPkPoints(amount28 * player.getConfig().OPENPK_POINTS_TO_GP_RATIO);
				ActionSender.sendPoints(player);
			break;
		}
	}

	private final boolean checkReduceLevelReqs(Player player, int exp, int stat) {
		if(player.getLocation().inWilderness()){
			player.message("You cannot do that in the wilderness");
			return false;
		}
		if(player.getDuel().isDuelActive()){
			player.message("You cannot do that while dueling");
			return false;
		}
		if(player.inCombat()){
			player.message("You cannot do that whilst fighting");
			return false;
		}
		if (System.currentTimeMillis() - player.getCombatTimer() < 10000){
			player.message("You must be out of combat for 10 seconds before changing stats");
			return false;
		}
		if(player.getSkills().getExperience(stat) < exp){
			player.message("You do not have that much exp in that stat");
			return false;
		}
		return true;
	}
	private final boolean checkIncreaseLevelReqs(Player player, int points) {
		if(player.getLocation().inWilderness()){
			player.message("You cannot do that in the wilderness");
			return false;
		}
		if(player.getDuel().isDuelActive()){
			player.message("You cannot do that while dueling");
			return false;
		}
		if(player.inCombat()){
			player.message("You cannot do that whilst fighting");
			return false;
		}
		if (System.currentTimeMillis() - player.getCombatTimer() < 10000){
			player.message("You must be out of combat for 10 seconds before changing stats");
			return false;
		}
		if(player.getOpenPkPoints() < points){
			player.message("You do not have enough points");
			return false;
		}
		return true;
	}
}
