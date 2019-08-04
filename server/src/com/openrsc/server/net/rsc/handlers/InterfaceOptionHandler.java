package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.content.clan.*;
import com.openrsc.server.content.party.*;
import com.openrsc.server.content.market.Market;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.util.rsc.DataConversions;

public class InterfaceOptionHandler implements PacketHandler {


	private static String[] badWords = {
		"fuck", "ass", "bitch", "admin", "mod", "dev", "developer", "nigger", "niger",
		"whore", "pussy", "porn", "penis", "chink", "faggot", "cunt", "clit", "cock"};

	@Override
	public void handlePacket(Packet p, Player player) throws Exception {
		switch (p.readByte()) {
			case 0:
				player.setAttribute("swap_cert", p.readByte() == 1);
				break;
			case 1:
				player.setAttribute("swap_note", p.readByte() == 1);
				break;
			case 2:// Swap
				int slot = p.readInt();
				int to = p.readInt();

				if (player.getBank().swap(slot, to)) {
					ActionSender.updateBankItem(player, slot, player.getBank().get(slot).getID(),
						player.getBank().get(slot).getAmount());
					ActionSender.updateBankItem(player, to, player.getBank().get(to).getID(),
						player.getBank().get(to).getAmount());
				}
				break;
			case 3: // Insert
				slot = p.readInt();
				to = p.readInt();
				if (player.getBank().insert(slot, to)) {
					ActionSender.showBank(player);
				}
				break;

			case 4: // Insert
				slot = p.readInt();
				to = p.readInt();

				player.getInventory().swap(slot, to);
				break;
			case 5: // Swap
				slot = p.readInt();
				to = p.readInt();

				player.getInventory().insert(slot, to);
				ActionSender.sendInventory(player);
				break;
			case 6:
				player.checkAndInterruptBatchEvent();
				break;
			case 7:
				int secondary = (int) p.readByte();
				if (secondary == 0) {
					int mode = (int) p.readByte();
					if (mode < 0 || mode > 3) {
						player.setSuspiciousPlayer(true);
						return;
					}
					if (mode > -1) {
						if (mode == 1) {
							if (!player.getLocation().onTutorialIsland() && (player.getIronMan() <= 0 || player.getIronMan() >= 4)) {
								player.message("You cannot become an Iron Man after leaving Tutorial Island.");
								return;
							}
							if (player.getIronMan() == 1) {
								return;
							}
							if (!player.getLocation().onTutorialIsland() && (player.getIronMan() == 2 || player.getIronMan() == 3)) {
								if (player.getIronManRestriction() == 0) {
									if (player.getCache().hasKey("bank_pin")) {
										Npc npc = Functions.getMultipleNpcsInArea(player, 11, 799, 800, 801);
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
							player.setIronMan(1);
						} else if (mode == 2) {
							if (!player.getLocation().onTutorialIsland() && player.getIronMan() != 2) {
								player.message("You cannot become an Ultimate Iron Man after leaving Tutorial Island.");
								return;
							}
							if (player.getIronMan() == 2) {
								return;
							}
							player.setIronMan(2);
						} else if (mode == 3) {
							if (!player.getLocation().onTutorialIsland() && player.getIronMan() != 3) {
								player.message("You cannot become a Hardcore Iron Man after leaving Tutorial Island.");
								return;
							}
							if (player.getIronMan() == 3) {
								return;
							}
							player.setIronMan(3);
						} else {
							if (player.getIronMan() == 0) {
								return;
							}
							if (!player.getLocation().onTutorialIsland() && (player.getIronMan() == 2 || player.getIronMan() == 3 || player.getIronMan() == 1)) {
								if (player.getIronManRestriction() == 0) {
									if (player.getCache().hasKey("bank_pin")) {
										Npc npc = Functions.getMultipleNpcsInArea(player, 11, 799, 800, 801);
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
							player.setIronMan(0);
						}
						ActionSender.sendIronManMode(player);
					}
				} else if (secondary == 1) {
					int setting = (int) p.readByte();
					if (setting < 0 || setting > 1) {
						player.setSuspiciousPlayer(true);
						return;
					}
					if (!player.getLocation().onTutorialIsland()) {
						player.message("You cannot change this setting now that you have completed the Tutorial.");
						return;
					}
					if (setting > -1) {
						if (player.getIronMan() == 0) {
							return;
						}
						if (setting == 0) {
							if (!player.getCache().hasKey("bank_pin")) {
								Npc npc = Functions.getMultipleNpcsInArea(player, 11, 799, 800, 801);
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
				break;
			case 8:
				int action = p.readByte();
				if (action == 0) {
					String bankpin = p.readString();
					if (bankpin.length() != 4) {
						return;
					}
					player.setAttribute("bank_pin_entered", bankpin);
				} else if (action == 1) {
					player.setAttribute("bank_pin_entered", "cancel");
				}
				break;
			case 10:
				if (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3)) {
					player.message("As an Iron Man, you cannot use the Auction.");
					return;
				}
				if (Server.getServer().timeTillShutdown() > 0) {
					player.message("Auction house is disabled until server restart!");
					return;
				}
				/* Set true when auctioneer opens AH window for the player */
				if (!player.getAttribute("auctionhouse", false)) {
					return;
				}

				int type = p.readByte();
				switch (type) {
					case 0: /* Buy */
						int auctionBuyID = p.readInt();
						int amountBuy = p.readInt();

						if (System.currentTimeMillis() - player.getAttribute("ah_buy_item", (long) 0) < 5000) {
							ActionSender.sendBox(player, "@ora@[Auction House - Warning] % @whi@ You recently purchased an item, please wait 5 seconds.", false);
							return;
						}
						player.setAttribute("ah_buy_item", System.currentTimeMillis());

						Market.getInstance().addBuyAuctionItemTask(player, auctionBuyID, amountBuy);
						break;

					case 1: /* Create auction */
						int itemID = p.readInt();
						int amount = p.readInt();
						int price = p.readInt();
						Market.getInstance().addNewAuctionItemTask(player, itemID, amount, price);
						break;
					case 2:
						int auctionID = p.readInt();
						Market.getInstance().addCancelAuctionItemTask(player, auctionID);
						break;
					case 3:
						if (System.currentTimeMillis() - player.getAttribute("ah_refresh", (long) 0) < 5000) {
							player.message("@ora@[Auction House - Warning]@whi@ You recently refreshed, please wait 5 seconds.");
							return;
						}
						player.setAttribute("ah_refresh", System.currentTimeMillis());
						player.message("@gre@[Auction House]@whi@ List has been refreshed!");
						ActionSender.sendOpenAuctionHouse(player);
						break;
					case 4:
						player.setAttribute("auctionhouse", false);
						break;
					case 5:
						auctionID = p.readInt();

						Market.getInstance().addModeratorDeleteItemTask(player, auctionID);
						break;
				}
				break;
			case 11: // Clan Actions
				if (!Constants.GameServer.WANT_CLANS) return;
				int actionType = p.readByte();
				switch (actionType) {
					case 0: // CREATE CLAN
						String clanName = p.readString();
						String clanTag = p.readString();
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

						if (ClanManager.getClan(clanName) == null && ClanManager.getClan(clanTag) == null) {
							Clan clan = new Clan();
							clan.setClanName(clanName);
							clan.setClanTag(clanTag);

							ClanPlayer clanMember = clan.addPlayer(player);
							clanMember.setRank(ClanRank.LEADER);
							clan.setLeader(clanMember);

							ClanManager.createClan(clan);
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
						String playerInvited = p.readString();
						Player invited = World.getWorld().getPlayer(DataConversions.usernameToHash(playerInvited));
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
							String playerToKick = p.readString();
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
							String playerRank = p.readString();
							int rank = p.readByte();
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
							int settingPreference = p.readByte();
							if (settingPreference > 3) {
								return;
							}
							int state = p.readByte();
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
				break;
			case 12: // Party
				if (!Constants.GameServer.WANT_PARTIES) return;
				int actionType2 = p.readByte();
				switch (actionType2) {
					case 0: // CREATE PARTY
						if (player.getParty() != null) {
							ActionSender.sendBox(player, "Leave your current party before joining another", false);
							return;
						}

						Party party = new Party();
						//party.setPartyName(partyName);
						//party.setPartyTag(partyTag);

						PartyPlayer partyMember = party.addPlayer(player);
						partyMember.setRank(PartyRank.LEADER);
						party.setLeader(partyMember);

						PartyManager.createParty(party);
						player.message("You have created a party: ");

						break;
					case 1:
						if (player.getParty() != null) {
							player.getParty().removePlayer(player.getUsername());
						}
						break;
					case 2:
						Player invited = World.getWorld().getPlayer(p.readShort());
						/*if (!player.getParty().isAllowed(1, player)) {
							player.message("You are not allowed to invite others into this party");
							return;
						}*/
						if (player.getParty() == null) {
							String partyName = p.readString();
							String partyTag = p.readString();
							Party party1 = new Party();
							party1.setPartyName(partyName);
							party1.setPartyTag(partyTag);

							PartyPlayer partyMember1 = party1.addPlayer(player);
							partyMember1.setRank(PartyRank.LEADER);
							party1.setLeader(partyMember1);

							PartyManager.createParty(party1);
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
						if (invited.getParty() != null) {
							if (player.getParty() == invited.getParty()) {
								player.message(invited.getUsername() + " is already in your party");
								return;
							} else {
								invited.message("@yel@" + player.getUsername() + " + " + player.getParty().getPlayers().size() + " tried to send you a party invite, but you are already in a party");
								player.message(invited.getUsername() + " is already in a party");
								return;
							}
						}
						if (invited != null) {
							PartyInvite.createPartyInvite(player, invited);
							//ActionSender.sendBox(player, "Yay " + invited.getUsername(), false);
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
							String playerToKick = p.readString();
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
							String playerRank = p.readString();
							int rank = p.readByte();
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
							int settingPreference = p.readByte();
							if (settingPreference > 3) {
								return;
							}
							int state = p.readByte();
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
						String playerInvited2 = p.readString();
						Player invited2 = World.getWorld().getPlayer(DataConversions.usernameToHash(playerInvited2));
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
									invited2.message("@yel@" + player.getUsername() + " tried to send you a party invite, but you are already in a party");
									ActionSender.sendBox(player,
										"@lre@Party: %"
											+ " %"
											+ invited2.getUsername() + " is already in a party %"
										, true);
									return;
								}
							} else {
								String partyName = p.readString();
								String partyTag = p.readString();
								Party party1 = new Party();
								party1.setPartyName(partyName);
								party1.setPartyTag(partyTag);

								PartyPlayer partyMember1 = party1.addPlayer(player);
								partyMember1.setRank(PartyRank.LEADER);
								party1.setLeader(partyMember1);

								PartyManager.createParty(party1);
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
							if (invited2.getParty() != null) {
								if (player.getParty() == invited2.getParty()) {
									player.message(invited2.getUsername() + " is already in your party");
									return;
								} else {
									invited2.message("@yel@" + player.getUsername() + " tried to send you a party invite, but you are already in a party");
									ActionSender.sendBox(player,
										"@lre@Party: %"
											+ " %"
											+ invited2.getUsername() + " is already in a party %"
										, true);
									return;
								}
							}
						}
						if (invited2 != null) {
							PartyInvite.createPartyInvite(player, invited2);
							//ActionSender.sendBox(player, "Yay " + invited2.getUsername(), false);
						} else {
							ActionSender.sendBox(player, "Player is not online or could not be found!", false);
							return;
						}
						break;
				}
				break;
		}
	}
}
