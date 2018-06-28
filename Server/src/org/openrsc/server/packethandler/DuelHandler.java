package org.openrsc.server.packethandler;

import org.apache.mina.common.IoSession;
import org.openrsc.server.Config;
import org.openrsc.server.event.DuelEvent;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.event.WalkToMobEvent;
import org.openrsc.server.logging.Logger;
import org.openrsc.server.logging.model.DuelLog;
import org.openrsc.server.logging.model.ExploitLog;
import org.openrsc.server.model.InvItem;
import org.openrsc.server.model.Inventory;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.TrajectoryHandler;
import org.openrsc.server.model.World;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;
import org.openrsc.server.states.Action;
import org.openrsc.server.util.DataConversions;
import org.openrsc.server.util.Formulae;
public class DuelHandler implements PacketHandler {

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.isTrading();
	}
	
	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player)session.getAttachment();
		if(player != null) {
		int pID = ((RSCPacket)p).getID();
		Player affectedPlayer = player.getWishToDuel();
		if (affectedPlayer == player) {
			Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (1)", DataConversions.getTimeStamp()));
			return;
		}
		if (player.isDuelConfirmAccepted() && affectedPlayer != null && affectedPlayer.isDuelConfirmAccepted()) {
			Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (2)", DataConversions.getTimeStamp()));
			return;
		}
		if (busy(player) || player.getLocation().inWilderness()) {
			unsetOptions(player);
			unsetOptions(affectedPlayer);
			return;
		}
		
		switch(pID) {
			case 54: // Sending duel request
				if (World.dueling) {
					int index = p.readShort();
					affectedPlayer = World.getPlayer(index);
					if (player != affectedPlayer && affectedPlayer != null && !affectedPlayer.isDueling() && !player.isDueling() && !player.tradeDuelThrottling()) {
						if(player.withinRange(affectedPlayer, 8)) {
							if(!TrajectoryHandler.isRangedBlocked(player.getX(), player.getY(), affectedPlayer.getX(), affectedPlayer.getY())) {
								if (!player.getLocation().inBounds(792, 23, 794, 25)) {
									if(affectedPlayer.getPrivacySetting(3) || affectedPlayer.isFriendsWith(player.getUsernameHash()) && !affectedPlayer.isIgnoring(player.getUsernameHash())) {
										player.setWishToDuel(affectedPlayer);
										player.sendMessage("Sending duel request");
										affectedPlayer.sendMessage(DataConversions.ucwords(player.getUsername()).replaceAll("_", " ") + " " + Formulae.getLvlDiffColour(affectedPlayer.getCombatLevel() - player.getCombatLevel()) + "(level-" + player.getCombatLevel() + ")@whi@ wishes to duel with you");
										if (!player.isDueling() && affectedPlayer.getWishToDuel() != null && affectedPlayer.getWishToDuel().equals(player) && !affectedPlayer.isDueling()) {
											player.setDueling(true);
											player.resetPath();
											player.clearDuelOptions();
											player.resetAllExceptDueling();
											affectedPlayer.setDueling(true);
											affectedPlayer.resetPath();
											affectedPlayer.clearDuelOptions();
											affectedPlayer.resetAllExceptDueling();
											player.sendDuelWindowOpen();
											affectedPlayer.sendDuelWindowOpen();
										}
									} else
										player.sendMessage("Sending duel request");
								} else
									player.sendMessage("You cannot duel whilst being jailed.");
							} else
								player.sendMessage("There is an obstacle in the way");
						} else
							player.sendMessage("I'm not near enough");
					}
				} else
					player.sendMessage(Config.PREFIX + "Dueling is currently disabled");
				break;
			case 49: // Duel accepted
				if(System.currentTimeMillis() - player.getLastTradeDuelUpdate() > 50) {
					player.setLastTradeDuelUpdate(System.currentTimeMillis());
					affectedPlayer = player.getWishToDuel();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isDueling() || !affectedPlayer.isDueling()) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (3)", DataConversions.getTimeStamp()));
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					
					player.setDuelOfferAccepted(true);
					
					player.sendDuelAcceptUpdate();
					affectedPlayer.sendDuelAcceptUpdate();
					
					if(affectedPlayer.isDuelOfferAccepted()) {
						player.sendDuelAccept();
						affectedPlayer.sendDuelAccept();
					}
				}
				break;
			case 50: // Confirm accepted
				if(System.currentTimeMillis() - player.getLastTradeDuelUpdate() > 50) {
					player.setLastTradeDuelUpdate(System.currentTimeMillis());				
					affectedPlayer = player.getWishToDuel();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isDueling() || !affectedPlayer.isDueling() || !player.isDuelOfferAccepted() || !affectedPlayer.isDuelOfferAccepted()) { // This shouldn't happen
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (4)", DataConversions.getTimeStamp()));
						
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					player.setDuelConfirmAccepted(true);
					
					if(affectedPlayer.isDuelConfirmAccepted()) {
						player.sendDuelWindowClose();
						player.sendMessage("Commencing duel!");
						affectedPlayer.sendDuelWindowClose();
						affectedPlayer.sendMessage("Commencing duel!");
						player.resetAllExceptDueling();
						player.setBusy(true);
						player.setStatus(Action.DUELING_PLAYER);
						affectedPlayer.resetAllExceptDueling();
						affectedPlayer.setBusy(true);
						affectedPlayer.setStatus(Action.DUELING_PLAYER);
						if (player.getDuelSetting(3)) {
							for (InvItem item : player.getInventory().getItems()) {
								if (item.isWielded()) {
									item.setWield(false);
									player.updateWornItems(item.getWieldableDef().getWieldPos(), player.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
								}
							}
							player.sendSound("click", false);
							player.sendInventory();
							player.sendEquipmentStats();
							for (InvItem item : affectedPlayer.getInventory().getItems()) {
								if (item.isWielded()) {
									item.setWield(false);
									affectedPlayer.updateWornItems(item.getWieldableDef().getWieldPos(), affectedPlayer.getPlayerAppearance().getSprite(item.getWieldableDef().getWieldPos()));
								}
							}
							affectedPlayer.sendSound("click", false);
							affectedPlayer.sendInventory();
							affectedPlayer.sendEquipmentStats();
						}

						if (player.getDuelSetting(2)) {
							for (int x = 0;x < 14;x++) {
								if (player.isPrayerActivated(x)) {
									player.removePrayerDrain(x);
									player.setPrayer(x, false);
								}
								if (affectedPlayer.isPrayerActivated(x)) {
									affectedPlayer.removePrayerDrain(x);
									affectedPlayer.setPrayer(x, false);
								}
							}
							player.sendPrayers();
							affectedPlayer.sendPrayers();
						}
						player.setFollowing(affectedPlayer);
						walkToDuel(player, affectedPlayer);
					}
				}
				break;
				
				case 51: // Decline duel
					affectedPlayer = player.getWishToDuel();
					if (affectedPlayer == null || busy(affectedPlayer) || !player.isDueling() || !affectedPlayer.isDueling()) {
						Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (5)", DataConversions.getTimeStamp()));
						
						unsetOptions(player);
						unsetOptions(affectedPlayer);
						return;
					}
					affectedPlayer.sendMessage("Other player left the duel screen");
					
					unsetOptions(player);
					unsetOptions(affectedPlayer);
					break;
					
				case 53: // Receive offered item data
						player.setLastTradeDuelUpdate(System.currentTimeMillis());
						affectedPlayer = player.getWishToDuel();
						if (affectedPlayer == null || busy(affectedPlayer) || !player.isDueling() || !affectedPlayer.isDueling() || (player.isDuelOfferAccepted() && affectedPlayer.isDuelOfferAccepted()) || player.isDuelConfirmAccepted() || affectedPlayer.isDuelConfirmAccepted()) { // This shouldn't happen
							Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (6)", DataConversions.getTimeStamp()));
							
							unsetOptions(player);
							unsetOptions(affectedPlayer);
							return;
						}
						player.setDuelOfferAccepted(false);
						player.setDuelConfirmAccepted(false);
						affectedPlayer.setDuelOfferAccepted(false);
						affectedPlayer.setDuelConfirmAccepted(false);
						player.sendDuelAcceptUpdate();
						affectedPlayer.sendDuelAcceptUpdate();
						Inventory duelOffer = new Inventory();
						player.resetDuelOffer();
						int count = (int)p.readByte();
						for (int slot = 0; slot < count; slot++) {
							InvItem tItem = new InvItem(p.readShort(), p.readLong());
							if (tItem.getDef().questItem() || tItem.getAmount() < 1) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (7)", DataConversions.getTimeStamp()));
								continue;
							}
							duelOffer.add(tItem);
						}
						for (InvItem item : duelOffer.getItems()) {
							if (duelOffer.countId(item.getID()) > player.getInventory().countId(item.getID())) {
								Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (8)", DataConversions.getTimeStamp()));
								
								unsetOptions(player);
								unsetOptions(affectedPlayer);
								return;
							}
							player.addToDuelOffer(item);
						}
						player.setRequiresOfferUpdate(true);
					break;

				case 52: // Set duel options
					if(System.currentTimeMillis() - player.getLastTradeDuelUpdate() > 50) {
						player.setLastTradeDuelUpdate(System.currentTimeMillis());
						affectedPlayer = player.getWishToDuel();
						if(affectedPlayer == null || busy(affectedPlayer) || !player.isDueling() || !affectedPlayer.isDueling() || (player.isDuelOfferAccepted() && affectedPlayer.isDuelOfferAccepted()) || player.isDuelConfirmAccepted() || affectedPlayer.isDuelConfirmAccepted()) { // This shouldn't happen
							Logger.log(new ExploitLog(player.getUsernameHash(), player.getAccount(), player.getIP(), "DuelHandler (9)", DataConversions.getTimeStamp()));
							
							unsetOptions(player);
							unsetOptions(affectedPlayer);
							return;
						}
						
						player.setDuelOfferAccepted(false);
						player.setDuelConfirmAccepted(false);
						affectedPlayer.setDuelOfferAccepted(false);
						affectedPlayer.setDuelConfirmAccepted(false);
						
						player.sendDuelAcceptUpdate();
						affectedPlayer.sendDuelAcceptUpdate();
						
						for(int i = 0;i < 4;i++) {
							boolean b = p.readByte() == 1;
							player.setDuelSetting(i, b);
							affectedPlayer.setDuelSetting(i, b);
						}
						player.sendDuelSettingUpdate();
						affectedPlayer.sendDuelSettingUpdate();
					}
					break;
			}
		}
	}
	
	private final void walkToDuel(Player player, Player affectedPlayer) {
		WalkToMobEvent walking = new WalkToMobEvent(player, affectedPlayer, 1) {
			public void arrived() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1000) {
					public void action() {
						Player affectedPlayer = (Player)affectedMob;
						if (!owner.nextTo(affectedPlayer)) {
							owner.setBusy(false);
							affectedPlayer.setBusy(false);
							unsetOptions(owner);
							unsetOptions(affectedPlayer);
							owner.resetFollowing();
							affectedPlayer.resetFollowing();
							return;
						}

						for (Player p : owner.getViewArea().getPlayersInView())
							p.removeWatchedPlayer(owner);
							
						Player attacker, opponent;
						
						if (DataConversions.random(0, 1) == 1) {
							attacker = owner;
							opponent = affectedPlayer;
						} else {
							attacker = affectedPlayer;
							opponent = owner;
						}
						
						DuelEvent dueling = new DuelEvent(attacker, opponent);
						attacker.setFightEvent(dueling);
						opponent.setFightEvent(dueling);
						World.getDelayedEventHandler().add(dueling);
						
						DuelLog log = new DuelLog(owner.getUsernameHash(), owner.getAccount(), owner.getIP(), affectedPlayer.getUsernameHash(), affectedPlayer.getAccount(), affectedPlayer.getIP(), owner.getDuelOptions(), DataConversions.getTimeStamp());
						for (InvItem item : owner.getDuelOffer())
							log.addStakedItem(item);
						for (InvItem item : affectedPlayer.getDuelOffer())
							log.addStakedAgainstItem(item);
						Logger.log(log);
					}
				});
			}
				
			public void failed() {
				Player affectedPlayer = (Player)affectedMob;
				unsetOptions(owner);
				unsetOptions(affectedPlayer);
				owner.setBusy(false);
				affectedPlayer.setBusy(false);
				owner.resetFollowing();
				affectedPlayer.resetFollowing();
			}
		};
		walking.setLastRun(System.currentTimeMillis() + 500);
		World.getDelayedEventHandler().add(walking);
	}
	
	private void unsetOptions(Player p) {
		if (p != null)
	      	p.resetDueling();
	}
}
