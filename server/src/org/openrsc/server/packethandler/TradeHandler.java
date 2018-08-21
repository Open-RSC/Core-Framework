package org.openrsc.server.packethandler;

import java.util.ArrayList;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.ServerBootstrap;
import org.openrsc.server.database.game.Save;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.logging.model.TradeLog;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Inventory;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.TrajectoryHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.util.DataConversions;
public class TradeHandler implements PacketHandler {
	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.isDueling();
	}
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			Player affectedPlayer;
			if (busy(player)) {
				affectedPlayer = player.getWishToTrade();
				unsetOptions(player);
				unsetOptions(affectedPlayer);
				return;
			}
			
			switch (pID) {
				case 43: // Sending trade request
					int index = p.readShort();
					affectedPlayer = World.getPlayer(index);
					if (affectedPlayer != null) {
						if (!player.tradeDuelThrottling()) {
							if (affectedPlayer.getUsernameHash() != player.getUsernameHash() && !player.isTrading()) {
								if (player.withinRange(affectedPlayer, 8)) {
									if (!TrajectoryHandler.isRangedBlocked(player.getX(), player.getY(), affectedPlayer.getX(), affectedPlayer.getY())) {
										if (player.getInDMWith() == null) {
											if (affectedPlayer.getInDMWith() == null) {
												if (!player.getLocation().inBounds(792, 23, 794, 25)) {
													if ((affectedPlayer.getPrivacySetting(2) || affectedPlayer.isFriendsWith(player.getUsernameHash())) && !affectedPlayer.isIgnoring(player.getUsernameHash())) {
														player.setWishToTrade(affectedPlayer);
														player.sendMessage("Sending trade request");
														affectedPlayer.sendMessage(DataConversions.ucwords(player.getUsername()).replaceAll("_", " ") + " wishes to trade with you.");
														if (!player.isTrading() && affectedPlayer.getWishToTrade() != null && affectedPlayer.getWishToTrade().equals(player) && !affectedPlayer.isTrading()) {
															player.setTrading(true);
															player.resetPath();
															player.resetAllExceptTrading();
															affectedPlayer.setTrading(true);
															affectedPlayer.resetPath();
															affectedPlayer.resetAllExceptTrading();
															player.sendTradeWindowOpen();
															affectedPlayer.sendTradeWindowOpen();
														}
													} else
													player.sendMessage("Sending trade request");
												} else
													player.sendMessage(Config.getPrefix() + "You cannot trade whilst in jail");
											} else
												player.sendMessage(Config.getPrefix() + "You cannot trade someone who's in a DM");
										} else
											player.sendMessage(Config.getPrefix() + "You cannot trade whilst in a DM");
									} else
										player.sendMessage("There is an obstacle in the way");
								} else
									player.sendMessage("I'm not near enough");
							}
						} else
							unsetOptions(player);
					}
					break;
				case 39: // Trade accepted
					player.setLastTradeDuelUpdate(System.currentTimeMillis());
					affectedPlayer = player.getWishToTrade();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading()) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (1)", DataConversions.getTimeStamp()));
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					player.setTradeOfferAccepted(true);
					player.sendTradeAcceptUpdate();
					affectedPlayer.sendTradeAcceptUpdate();
					if (affectedPlayer.isTradeOfferAccepted()) {
						player.sendTradeAccept();
						affectedPlayer.sendTradeAccept();
					}
					break;
				case 40: // Confirm accepted
					affectedPlayer = player.getWishToTrade();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading() || !player.isTradeOfferAccepted() || !affectedPlayer.isTradeOfferAccepted()) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (2)", DataConversions.getTimeStamp()));
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					player.setTradeConfirmAccepted(true);
					
					if (affectedPlayer.isTradeConfirmAccepted()) {
						ArrayList<InvItem> myOffer = player.getTradeOffer();
						ArrayList<InvItem> theirOffer = affectedPlayer.getTradeOffer();
						
						int myRequiredSlots = player.getInventory().getRequiredSlots(theirOffer);
						int myAvailableSlots = (30 - player.getInventory().size()) + player.getInventory().getFreedSlots(myOffer);
						
						int theirRequiredSlots = affectedPlayer.getInventory().getRequiredSlots(myOffer);
						int theirAvailableSlots = (30 - affectedPlayer.getInventory().size()) + affectedPlayer.getInventory().getFreedSlots(theirOffer);
						
						if (theirRequiredSlots > theirAvailableSlots) {
							player.sendMessage("Other player doesn't have enough inventory space to receive the objects");
							affectedPlayer.sendMessage("You don't have enough inventory space to receive the objects");
							unsetOptions(player);
							unsetOptions(affectedPlayer);
							return;
						}
						if (myRequiredSlots > myAvailableSlots) {
							player.sendMessage("You don't have enough inventory space to receive the objects");
							affectedPlayer.sendMessage("Other player doesn't have enough inventory space to receive the objects");
							unsetOptions(player);
							unsetOptions(affectedPlayer);
							return;
						}

						for (InvItem item : myOffer) {
							InvItem affectedItem = player.getInventory().get(item);
							if (affectedItem == null) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (3)", DataConversions.getTimeStamp()));
								
								unsetOptions(player);
								unsetOptions(affectedPlayer);
								return;
							}
							if (affectedItem.isWielded()) {
								affectedItem.setWield(false);
								player.updateWornItems(affectedItem.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
							}
							player.getInventory().remove(item);
						}
						for (InvItem item : theirOffer) {
							InvItem affectedItem = affectedPlayer.getInventory().get(item);
							if (affectedItem == null) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (4)", DataConversions.getTimeStamp()));
								unsetOptions(player);
								unsetOptions(affectedPlayer);
								return;
							}
							if (affectedItem.isWielded()) {
								affectedItem.setWield(false);
								affectedPlayer.updateWornItems(affectedItem.getWieldableDef().getWieldPos(), affectedPlayer.getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
							}
							affectedPlayer.getInventory().remove(item);
						}
						for(InvItem item : myOffer)
							affectedPlayer.getInventory().add(item);
						for (InvItem item : theirOffer)
							player.getInventory().add(item);
						Save s = new Save(player);
						ServerBootstrap.getDatabaseService().submit(s, s.new DefaultSaveListener());
						player.sendInventory();
						player.sendEquipmentStats();
						player.sendMessage("Trade completed successfully");
						
						s = new Save(affectedPlayer);
						ServerBootstrap.getDatabaseService().submit(s, s.new DefaultSaveListener());
						affectedPlayer.sendInventory();
						affectedPlayer.sendEquipmentStats();
						affectedPlayer.sendMessage("Trade completed successfully");
						if (player.getTradeOffer() != null || affectedPlayer.getTradeOffer() != null) {
							TradeLog log = new TradeLog(player.getUsernameHash(), player.getAccount(), player.getIP(), affectedPlayer.getUsernameHash(), affectedPlayer.getAccount(), affectedPlayer.getIP(), DataConversions.getTimeStamp());
							for (InvItem item : player.getTradeOffer())
								log.addItemToTradeList(item);
							for(InvItem item : affectedPlayer.getTradeOffer())
								log.addItemToRecievedList(item);
							Logger.log(log);
						}
						unsetOptions(player);
						unsetOptions(affectedPlayer);
					}
					break;
				case 41: // Trade declined
					affectedPlayer = player.getWishToTrade();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading()) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (5)", DataConversions.getTimeStamp()));
						
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					affectedPlayer.sendMessage("Other player has declined trade");
					
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					break;
				case 42: // Receive offered item data
						player.setLastTradeDuelUpdate(System.currentTimeMillis());
						affectedPlayer = player.getWishToTrade();
						if (affectedPlayer == null || busy(affectedPlayer) || !player.isTrading() || !affectedPlayer.isTrading() || (player.isTradeOfferAccepted() && affectedPlayer.isTradeOfferAccepted()) || player.isTradeConfirmAccepted() || affectedPlayer.isTradeConfirmAccepted()) { // This shouldn't happen
							Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (6)", DataConversions.getTimeStamp()));
							unsetOptions(player);
							unsetOptions(affectedPlayer);
							return;
						}
						
						player.setTradeOfferAccepted(false);
						player.setTradeConfirmAccepted(false);
						affectedPlayer.setTradeOfferAccepted(false);
						affectedPlayer.setTradeConfirmAccepted(false);
						
						player.sendTradeAcceptUpdate();
						affectedPlayer.sendTradeAcceptUpdate();
						
						Inventory tradeOffer = new Inventory();
						player.resetTradeOffer();
						int count = (int)p.readByte();
						for (int slot = 0;slot < count;slot++) {
							InvItem tItem = new InvItem(p.readShort(), p.readLong());
							if (tItem.getAmount() < 1) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (7)", DataConversions.getTimeStamp()));
								continue;
							}
							//System.out.println(tItem.getAmount());
							tradeOffer.add(tItem);
						}
						for (InvItem item : tradeOffer.getItems()) {
							if (!item.getDef().isTradable() || tradeOffer.countId(item.getID()) > player.getInventory().countId(item.getID())) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "TradeHandler (8)", DataConversions.getTimeStamp()));
								unsetOptions(player);
								unsetOptions(affectedPlayer);
								return;
							}
							player.addToTradeOffer(item);
						}
						player.setRequiresOfferUpdate(true);
					break;
			}
		}
	}
	
	private void unsetOptions(Player p) {
		if (p == null)
			return;
		p.resetTrading();
	}
}