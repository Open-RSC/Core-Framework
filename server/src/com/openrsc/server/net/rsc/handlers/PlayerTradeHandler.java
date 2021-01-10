package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.database.impl.mysql.queries.logging.TradeLog;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.List;
import java.util.Optional;

public class PlayerTradeHandler implements PacketHandler {

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.getDuel().isDuelActive() || player.inCombat();
	}

	public void handlePacket(Packet packet, Player player) throws Exception {

		/**
		 * Opcodes covered by this handler
		 * PLAYER_INIT_TRADE_REQUEST
		 * PLAYER_ACCEPTED_INIT_TRADE_REQUEST
		 * PLAYER_DECLINED_TRADE
		 * PLAYER_ADDED_ITEMS_TO_TRADE_OFFER
		 * PLAYER_ACCEPTED_TRADE
		 */

		Player affectedPlayer = null;

		if (busy(player)) {
			player.getTrade().resetAll();
			return;
		}

		player.interruptPlugins();

		OpcodeIn opcode = OpcodeIn.getFromList(packet.getID(),
			OpcodeIn.PLAYER_INIT_TRADE_REQUEST, OpcodeIn.PLAYER_ACCEPTED_INIT_TRADE_REQUEST,
			OpcodeIn.PLAYER_ADDED_ITEMS_TO_TRADE_OFFER, OpcodeIn.PLAYER_DECLINED_TRADE,
			OpcodeIn.PLAYER_ACCEPTED_TRADE);

		if (opcode == null)
			return;

		switch (opcode) {
			case PLAYER_INIT_TRADE_REQUEST:
				if (player.getWorld() != null) {
					affectedPlayer = player.getWorld().getPlayer(packet.readShort());
				}

				if (affectedPlayer == null) {
					return;
				}
				if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
					player.message("You are an Iron Man. You stand alone.");
					player.getTrade().resetAll();
					return;
				}
				if (affectedPlayer.isIronMan(IronmanMode.Ironman.id()) || affectedPlayer.isIronMan(IronmanMode.Ultimate.id())
					|| affectedPlayer.isIronMan(IronmanMode.Hardcore.id()) || affectedPlayer.isIronMan(IronmanMode.Transfer.id())) {
					player.message(affectedPlayer.getUsername() + " is an Iron Man. They stand alone.");
					player.getTrade().resetAll();
					return;
				}
				if (affectedPlayer.getTrade().isTradeActive()) {
					player.message("That person is already trading");
					return;
				}
				if (affectedPlayer.getDuel().isDuelActive()
					|| player.getTrade().isTradeActive()) {
					player.getTrade().resetAll();
					return;
				}
				if (player.equals(affectedPlayer)) {
					player.setSuspiciousPlayer(true, "player trading themselves");
					player.getTrade().resetAll();
					return;
				}
				boolean blockAll = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, affectedPlayer.isUsingAuthenticClient())
					== PlayerSettings.BlockingMode.All.id();
				boolean blockNonFriends = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, affectedPlayer.isUsingAuthenticClient())
					== PlayerSettings.BlockingMode.NonFriends.id();
				if ((blockAll || (blockNonFriends && !affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()))
					|| affectedPlayer.getSocial().isIgnoring(player.getUsernameHash())) && !player.isMod()) {
					return;
				}

				if (!affectedPlayer.withinRange(player.getLocation(), 4)) {
					player.message("I'm not near enough");
					player.getTrade().resetAll();
					return;
				}

				if (!PathValidation.checkPath(player.getWorld(), player.getLocation(), affectedPlayer.getLocation())) {
					player.message("There is an obstacle in the way");
					player.getTrade().resetAll();
					player.resetPath();
					return;
				}
				player.getTrade().setTradeRecipient(affectedPlayer);

				if (!player.getTrade().isTradeActive() && affectedPlayer.getTrade().getTradeRecipient() != null
					&& affectedPlayer.getTrade().getTradeRecipient().equals(player)
					&& !affectedPlayer.getTrade().isTradeActive()) {
					player.getTrade().setTradeActive(true);
					player.resetPath();
					player.resetAllExceptTrading();
					affectedPlayer.getTrade().setTradeActive(true);
					affectedPlayer.resetPath();
					affectedPlayer.resetAllExceptTrading();

					ActionSender.sendTradeWindowOpen(player);
					ActionSender.sendTradeWindowOpen(affectedPlayer);
				} else {
					ActionSender.sendMessage(player, null,  MessageType.INVENTORY, affectedPlayer.getTrade().isTradeActive()
						? affectedPlayer.getUsername() + " is already in a trade" : "Sending trade request", 0, null);

					ActionSender.sendMessage(affectedPlayer, player,  MessageType.TRADE, "", player.getIcon(), null);

				}
				break;
			case PLAYER_ACCEPTED_INIT_TRADE_REQUEST:
				affectedPlayer = player.getTrade().getTradeRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getTrade().isTradeActive()
					|| !affectedPlayer.getTrade().isTradeActive()) {
					player.setSuspiciousPlayer(true, "accepted trade isn't active or affected player is null or busy");
					player.getTrade().resetAll();
					return;
				}
				player.getTrade().setTradeAccepted(true);
				ActionSender.sendTradeAcceptUpdate(affectedPlayer);

				if (affectedPlayer.getTrade().isTradeAccepted()) {
					ActionSender.sendSecondTradeScreen(player);
					ActionSender.sendSecondTradeScreen(affectedPlayer);
				}
				break;
			case PLAYER_ADDED_ITEMS_TO_TRADE_OFFER:
				affectedPlayer = player.getTrade().getTradeRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getTrade().isTradeActive()
					|| !affectedPlayer.getTrade().isTradeActive()
					|| (player.getTrade().isTradeAccepted() && affectedPlayer.getTrade().isTradeAccepted())
					|| player.getTrade().isTradeConfirmAccepted()
					|| affectedPlayer.getTrade().isTradeConfirmAccepted()) { // This
					player.setSuspiciousPlayer(true, "offered item player isn't active or affected player is null or busy");
					player.getTrade().resetAll();
					return;
				}

				if (player.getTrade().isTradeAccepted()) {
					player.getTrade().setTradeAccepted(false);
					ActionSender.sendOwnTradeAcceptUpdate(player);
				}
				if (affectedPlayer.getTrade().isTradeAccepted()) {
					affectedPlayer.getTrade().setTradeAccepted(false);
					ActionSender.sendOwnTradeAcceptUpdate(affectedPlayer);
				}
				player.getTrade().setTradeConfirmAccepted(false);
				affectedPlayer.getTrade().setTradeConfirmAccepted(false);

				player.getTrade().resetOffer();
				int count = (int) packet.readByte();
				for (int slot = 0; slot < count; slot++) {
					Item tItem;
					if (player.isUsingAuthenticClient()) {
						tItem = new Item(packet.readShort(), packet.readInt(), false);
					} else {
						tItem = new Item(packet.readShort(), packet.readInt(), packet.readShort() == 1);
					}

					if (tItem.getAmount() < 1) {
						player.setSuspiciousPlayer(true, "item less than 0");
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (tItem.getDef(player.getWorld()).isUntradable() && !player.isAdmin()) {
						player.message("This object cannot be traded with other players");
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (tItem.getDef(player.getWorld()).isMembersOnly() && !player.getConfig().MEMBER_WORLD) {
						player.setRequiresOfferUpdate(true);
						continue;
					}

					if (tItem.getAmount() > player.getCarriedItems().getInventory().countId(tItem.getCatalogId(), Optional.of(tItem.getNoted()))) {
						player.setSuspiciousPlayer(true, "trade item amount greater than inventory countid");
						player.getTrade().resetAll();
						return;
					}
					player.getTrade().addToOffer(tItem);
				}

				affectedPlayer.setRequiresOfferUpdate(true);
				player.setRequiresOfferUpdate(true);
				break;
			case PLAYER_DECLINED_TRADE:
				affectedPlayer = player.getTrade().getTradeRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getTrade().isTradeActive()
					|| !affectedPlayer.getTrade().isTradeActive()) {
					player.setSuspiciousPlayer(true, "declined trade isn't active or affected player is null or busy");
					player.getTrade().resetAll();
					return;
				}
				affectedPlayer.message("Other player has declined trade");

				player.getTrade().resetAll();
				break;
			case PLAYER_ACCEPTED_TRADE:
				affectedPlayer = player.getTrade().getTradeRecipient();
				if (affectedPlayer == null || busy(affectedPlayer) || !player.getTrade().isTradeActive()
					|| !affectedPlayer.getTrade().isTradeActive() || !player.getTrade().isTradeAccepted()
					|| !affectedPlayer.getTrade().isTradeAccepted()) {
					player.setSuspiciousPlayer(true, "confirm trade isn't active or affected player is null or busy");
					player.getTrade().resetAll();
					return;
				}
				player.getTrade().setTradeConfirmAccepted(true);

				if (affectedPlayer.getTrade().isTradeConfirmAccepted()) {
					List<Item> myOffer = player.getTrade().getTradeOffer().getItems();
					List<Item> theirOffer = affectedPlayer.getTrade().getTradeOffer().getItems();

					synchronized(myOffer) {
						synchronized(theirOffer) {
							int myRequiredSlots = player.getCarriedItems().getInventory().getRequiredSlots(theirOffer);
							int myAvailableSlots = (30 - player.getCarriedItems().getInventory().size())
								+ player.getCarriedItems().getInventory().getFreedSlots(myOffer);

							int theirRequiredSlots = affectedPlayer.getCarriedItems().getInventory().getRequiredSlots(myOffer);
							int theirAvailableSlots = (30 - affectedPlayer.getCarriedItems().getInventory().size())
								+ affectedPlayer.getCarriedItems().getInventory().getFreedSlots(theirOffer);

							if (theirRequiredSlots > theirAvailableSlots) {
								player.message("Other player doesn't have enough inventory space to receive the objects");
								affectedPlayer.message("You don't have enough inventory space to receive the objects");
								player.getTrade().resetAll();
								return;
							}
							if (myRequiredSlots > myAvailableSlots) {
								player.message("You don't have enough inventory space to receive the objects");
								affectedPlayer.message("Other player doesn't have enough inventory space to receive the objects");
								player.getTrade().resetAll();
								return;
							}

							if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null
								|| affectedPlayer.getWorld().getPlayer(DataConversions.usernameToHash(affectedPlayer.getUsername())) == null) {
								break;
							}

							for (Item item : myOffer) {
								Item affectedItem = player.getCarriedItems().getInventory().get(item);
								if (affectedItem == null) {
									player.setSuspiciousPlayer(true, "trade item is null");
									player.getTrade().resetAll();
									return;
								}
								if (affectedItem.isWielded() && !player.getConfig().WANT_EQUIPMENT_TAB) {
									player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, affectedItem, UnequipRequest.RequestType.CHECK_IF_EQUIPMENT_TAB, false));
								}

								// Create item to be traded.
								int amount = Math.min(affectedItem.getAmount(), item.getAmount());

								// Create item to be traded.
								affectedItem = new Item(affectedItem.getCatalogId(), amount, affectedItem.getNoted(), affectedItem.getItemId());

								// Remove item to be traded quantity from inventory.
								player.getCarriedItems().getInventory().remove(affectedItem, true);
							}

							for (Item item : theirOffer) {
								Item affectedItem = affectedPlayer.getCarriedItems().getInventory().get(item);
								if (affectedItem == null) {
									affectedPlayer.setSuspiciousPlayer(true, "other trade item is null");
									player.getTrade().resetAll();
									return;
								}
								if (affectedItem.isWielded() && !player.getConfig().WANT_EQUIPMENT_TAB) {
									affectedPlayer.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(affectedPlayer, affectedItem, UnequipRequest.RequestType.CHECK_IF_EQUIPMENT_TAB, false));
								}

								int amount = Math.min(affectedItem.getAmount(), item.getAmount());

								// Create item to be traded.
								affectedItem = new Item(affectedItem.getCatalogId(), amount, affectedItem.getNoted(), affectedItem.getItemId());

								// Remove item to be traded quantity from inventory.
								affectedPlayer.getCarriedItems().getInventory().remove(affectedItem, true);
							}

							for (Item item : myOffer) {
								if (affectedPlayer.getWorld().getPlayer(DataConversions.usernameToHash(affectedPlayer.getUsername())) == null) {
									break;
								}
								item = new Item(item.getCatalogId(), item.getAmount(), item.getNoted());
								affectedPlayer.getCarriedItems().getInventory().add(item);
							}
							for (Item item : theirOffer) {
								if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
									break;
								}
								item = new Item(item.getCatalogId(), item.getAmount(), item.getNoted());
								player.getCarriedItems().getInventory().add(item);
							}

							player.getWorld().getServer().getGameLogger().addQuery(
								new TradeLog(player.getWorld(), player.getUsername(), affectedPlayer.getUsername(), myOffer, theirOffer, player.getCurrentIP(), affectedPlayer.getCurrentIP()).build());
							player.save();
							affectedPlayer.save();
							player.message("Trade completed successfully");

							affectedPlayer.message("Trade completed successfully");
							player.getTrade().resetAll();
							affectedPlayer.getTrade().resetAll();
						}
					}
				}
				break;
			default:
				return;
		}
	}

}
