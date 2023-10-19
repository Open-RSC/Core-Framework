package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.database.impl.mysql.queries.logging.TradeLog;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.model.struct.UnequipRequest;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.PlayerTradeStruct;
import com.openrsc.server.util.rsc.CertUtil;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.List;
import java.util.Optional;

public class PlayerTradeHandler implements PayloadProcessor<PlayerTradeStruct, OpcodeIn> {

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.getDuel().isDuelActive() || player.inCombat();
	}

	public void process(PlayerTradeStruct payload, Player player) throws Exception {

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

		OpcodeIn opcode = payload.getOpcode();

		if (opcode == null)
			return;

		switch (opcode) {
			case PLAYER_INIT_TRADE_REQUEST:
				if (player.getWorld() != null) {
					affectedPlayer = player.getWorld().getPlayer(payload.targetPlayerID);
				}

				if (affectedPlayer == null) {
					return;
				}
				if (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id())) {
					player.message("You are an Ironman. You stand alone.");
					player.getTrade().resetAll();
					return;
				}
				if (affectedPlayer.isIronMan(IronmanMode.Ironman.id()) || affectedPlayer.isIronMan(IronmanMode.Ultimate.id())
					|| affectedPlayer.isIronMan(IronmanMode.Hardcore.id()) || affectedPlayer.isIronMan(IronmanMode.Transfer.id())) {
					player.message(affectedPlayer.getUsername() + " is an Ironman. They stand alone.");
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
				boolean blockAll = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, affectedPlayer.isUsingCustomClient())
					== PlayerSettings.BlockingMode.All.id();
				boolean blockNonFriends = affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS, affectedPlayer.isUsingCustomClient())
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

					boolean warnPlayerNoConfirm, warnPlayerConfirm, warnOtherPlayerNoConfirm, warnOtherPlayerConfirm;
					warnPlayerNoConfirm = player.getClientLimitations().supportsConfirmTrade && player.getConfig().NO_CONFIRM_TRADES;
					warnPlayerConfirm = !player.getClientLimitations().supportsConfirmTrade && !player.getConfig().NO_CONFIRM_TRADES;
					warnOtherPlayerNoConfirm = affectedPlayer.getClientLimitations().supportsConfirmTrade && affectedPlayer.getConfig().NO_CONFIRM_TRADES;
					warnOtherPlayerConfirm = !affectedPlayer.getClientLimitations().supportsConfirmTrade && !affectedPlayer.getConfig().NO_CONFIRM_TRADES;
					if (player.getConfig().NO_CONFIRM_TRADES) {
						if (warnPlayerNoConfirm) {
							ActionSender.sendMessage(player, "Reminder: This world does not support confirm trades");
							ActionSender.sendMessage(player, "Please double check transaction before accepting");
						}
						if (warnOtherPlayerNoConfirm) {
							ActionSender.sendMessage(affectedPlayer, "Reminder: This world does not support confirm trades");
							ActionSender.sendMessage(affectedPlayer, "Please double check transaction before accepting");
						}
					} else {
						int timeSince, timeRemain;
						if (warnPlayerConfirm == warnOtherPlayerConfirm) {
							// both players set to same trade mechanism client, no need to warn
						} else if (warnPlayerConfirm) {
							ActionSender.sendMessage(player, "Reminder: This world requires confirm trades");
							ActionSender.sendMessage(player, "Other player may only finish trade if they use ::oldtrade");
							if (!affectedPlayer.hasNoTradeConfirm(1)) {
								ActionSender.sendMessage(affectedPlayer,"The other player will not be able to complete trade");
								ActionSender.sendMessage(affectedPlayer,"To overcome this use ::oldtrade to temporary disable confirm trade");
							} else {
								timeSince = (int) ((System.currentTimeMillis() - affectedPlayer.getNoTradeConfirmTime()) / 60000);
								timeRemain = Math.max(1, 5 - timeSince);
								ActionSender.sendMessage(affectedPlayer,"The other player cannot confirm trades");
								ActionSender.sendMessage(affectedPlayer,"You still have " + timeRemain + " minutes for no confirm trade");
								ActionSender.sendMessage(affectedPlayer,"You can renew the time with ::oldtrade");
							}
						} else if (warnOtherPlayerConfirm) {
							ActionSender.sendMessage(affectedPlayer, "Reminder: This world requires confirm trades");
							ActionSender.sendMessage(affectedPlayer, "Other player may only finish trade if they use ::oldtrade");
							if (!player.hasNoTradeConfirm(1)) {
								ActionSender.sendMessage(player,"The other player will not be able to complete trade");
								ActionSender.sendMessage(player,"To overcome this use ::oldtrade to temporary disable confirm trade");
							} else {
								timeSince = (int) ((System.currentTimeMillis() - player.getNoTradeConfirmTime()) / 60000);
								timeRemain = Math.max(1, 5 - timeSince);
								ActionSender.sendMessage(player,"The other player cannot confirm trades");
								ActionSender.sendMessage(player,"You still have " + timeRemain + " minutes for no confirm trade");
								ActionSender.sendMessage(player,"You can renew the time with ::oldtrade");
							}
						}
					}
				} else {
					ActionSender.sendMessage(player, null,  MessageType.INVENTORY, affectedPlayer.getTrade().isTradeActive()
						? affectedPlayer.getUsername() + " is already in a trade" : "Sending trade request", 0, null);

					if (affectedPlayer.getClientVersion() <= 204) {
						ActionSender.sendMessage(affectedPlayer, player,  MessageType.INVENTORY, player.getUsername() + ": wishes to trade with you", 0, null);
					} else {
						ActionSender.sendMessage(affectedPlayer, player,  MessageType.TRADE, "", player.getIcon(), null);
					}

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
				boolean ownAccepted = player.getClientLimitations().supportsConfirmTrade || payload.tradeAccepted == 1;
				player.getTrade().setTradeAccepted(ownAccepted);
				ActionSender.sendTradeAcceptUpdate(affectedPlayer);

				boolean willPlayerNoConfirm = !player.getClientLimitations().supportsConfirmTrade || player.hasNoTradeConfirm();
				boolean willOtherPlayerNoConfirm = !affectedPlayer.getClientLimitations().supportsConfirmTrade || affectedPlayer.hasNoTradeConfirm();

				if (affectedPlayer.getTrade().isTradeAccepted()) {
					// check perform trade or send confirm screen
					if (!player.getConfig().NO_CONFIRM_TRADES
						&& player.getClientLimitations().supportsConfirmTrade
						&& affectedPlayer.getClientLimitations().supportsConfirmTrade) {
						// world set to confirm and both players are capable of confirm trade
						ActionSender.sendSecondTradeScreen(player);
						ActionSender.sendSecondTradeScreen(affectedPlayer);
					} else if (player.getConfig().NO_CONFIRM_TRADES
						|| ((willPlayerNoConfirm == willOtherPlayerNoConfirm) && willPlayerNoConfirm)) {
						// world set to not confirm or both players are capable of no confirm trade
						performTrade(player, affectedPlayer);
					} else {
						// world set to confirm and one of the two players not able to confirm trade
						player.getTrade().resetAll();
						affectedPlayer.getTrade().resetAll();
						player.message("Trade could not complete");
						affectedPlayer.message("Trade could not complete");
					}
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

				final int itemCount = Math.min(payload.tradeCount, 12);

				for (int i = 0; i < itemCount; i++) {
					final Item item = new Item(payload.tradeCatalogIDs[i], payload.tradeAmounts[i], payload.tradeNoted[i]);

					if (item.getAmount() < 1) {
						player.setSuspiciousPlayer(true,
							String.format("trading invalid amount of itemId: %d", item.getCatalogId()));
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (item.getNoted() && !player.getConfig().WANT_BANK_NOTES) {
						player.message("Notes can no longer be traded with other players.");
						player.message("You may either deposit it in the bank or sell to a shop instead.");
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (item.getDef(player.getWorld()).isUntradable() && !player.getWorld().getServer().getConfig().CAN_OFFER_UNTRADEABLES) {
						player.message("This object cannot be traded with other players");
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (item.getCatalogId() > affectedPlayer.getClientLimitations().maxItemId) {
						player.message("The other player is unable to receive the offered object");
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (item.getCatalogId() > player.getClientLimitations().maxItemId) {
						player.message("You don't even know what that is...!");
						player.message("Definitely update your client before trying to trade that item.");
						continue;
					}
					if (item.getDef(player.getWorld()).isMembersOnly() && !player.getConfig().MEMBER_WORLD) {
						player.setRequiresOfferUpdate(true);
						continue;
					}
					if (CertUtil.isCert(item.getCatalogId()) && (player.getCertOptOut() || affectedPlayer.getCertOptOut())) {
						if (player.getCertOptOut()) {
							player.message("You have opted out of trading certs with other players");
						}
						if (affectedPlayer.getCertOptOut()) {
							player.message("The other player has opted out of trading certs with players");
						}
						player.setRequiresOfferUpdate(true);
						continue;
					}

					final int invCount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()));
					final int tradeCount = player.getTrade().getTradeOffer().countId(item.getCatalogId());

					if (item.getAmount() > (invCount - tradeCount)) {
						player.setSuspiciousPlayer(true, String.format("trading insufficient amount of itemId: %d", item.getCatalogId()));
						player.getTrade().resetAll();
						return;
					}

					player.getTrade().addToOffer(item);
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
					performTrade(player, affectedPlayer);
				}
				break;
			default:
				return;
		}
	}

	private void performTrade(Player player, Player affectedPlayer) {
		List<Item> myOffer = player.getTrade().getTradeOffer().getItems();
		List<Item> theirOffer = affectedPlayer.getTrade().getTradeOffer().getItems();
		boolean updateOwnAppearance = false, updateOtherAppearance = false;

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
					return;
				}

				for (Item item : myOffer) {
					Item affectedItem = player.getCarriedItems().getInventory().get(item);
					if (affectedItem == null) {
						player.setSuspiciousPlayer(true, "trade item is null");
						player.getTrade().resetAll();
						return;
					}
					ItemDefinition inventoryDef = affectedItem.getDef(player.getWorld());
					if (affectedItem.isWielded() && !player.getConfig().WANT_EQUIPMENT_TAB) {
						player.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(player, affectedItem, UnequipRequest.RequestType.CHECK_IF_EQUIPMENT_TAB, false));
						updateOwnAppearance = true;
					}

					// Create item to be traded.
					int amount = Math.min(affectedItem.getAmount(), item.getAmount());

					// Create item to be traded.
					affectedItem = new Item(affectedItem.getCatalogId(), amount, affectedItem.getNoted(), affectedItem.getItemId());

					// Remove item to be traded quantity from inventory.
					// bypass item id position in case its stackable or noteable to end up with clean stacks
					player.getCarriedItems().getInventory().remove(affectedItem, true, inventoryDef.isStackable() || affectedItem.getNoted());
				}

				for (Item item : theirOffer) {
					Item affectedItem = affectedPlayer.getCarriedItems().getInventory().get(item);
					if (affectedItem == null) {
						affectedPlayer.setSuspiciousPlayer(true, "other trade item is null");
						player.getTrade().resetAll();
						return;
					}
					ItemDefinition inventoryDef = affectedItem.getDef(player.getWorld());
					if (affectedItem.isWielded() && !player.getConfig().WANT_EQUIPMENT_TAB) {
						affectedPlayer.getCarriedItems().getEquipment().unequipItem(new UnequipRequest(affectedPlayer, affectedItem, UnequipRequest.RequestType.CHECK_IF_EQUIPMENT_TAB, false));
						updateOtherAppearance = true;
					}

					int amount = Math.min(affectedItem.getAmount(), item.getAmount());

					// Create item to be traded.
					affectedItem = new Item(affectedItem.getCatalogId(), amount, affectedItem.getNoted(), affectedItem.getItemId());

					// Remove item to be traded quantity from inventory.
					// bypass item id position in case its stackable or noteable to end up with clean stacks
					affectedPlayer.getCarriedItems().getInventory().remove(affectedItem, true, inventoryDef.isStackable() || affectedItem.getNoted());
				}

				// set as next tick to ensure appearance update occurs
				if (updateOwnAppearance) {
					player.getWorld().getServer().getGameEventHandler().add(
						new DelayedEvent(player.getWorld(), player, player.getConfig().GAME_TICK, "Update Appearance") {
							@Override
							public void run() {
								getOwner().getUpdateFlags().setAppearanceChanged(true);
								stop();
							}
						}
					);
				}
				if (updateOtherAppearance) {
					affectedPlayer.getWorld().getServer().getGameEventHandler().add(
						new DelayedEvent(affectedPlayer.getWorld(), affectedPlayer, affectedPlayer.getConfig().GAME_TICK, "Update Appearance") {
							@Override
							public void run() {
								getOwner().getUpdateFlags().setAppearanceChanged(true);
								stop();
							}
						}
					);
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

}
