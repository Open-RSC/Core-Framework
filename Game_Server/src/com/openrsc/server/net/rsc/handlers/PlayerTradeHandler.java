package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.player.PlayerSettings;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.query.logs.TradeLog;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;

public class PlayerTradeHandler implements PacketHandler {

	private boolean busy(Player player) {
		return player.isBusy() || player.isRanging() || player.accessingBank() || player.getDuel().isDuelActive();
	}

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
		int packetOne = OpcodeIn.PLAYER_TRADE.getOpcode();
		int packetTwo = OpcodeIn.TRADE_ACCEPTED.getOpcode();
		int packetThree = OpcodeIn.TRADE_DECLINED.getOpcode();
		int packetFour = OpcodeIn.TRADE_OFFER.getOpcode();
		int packetFive = OpcodeIn.TRADE_CONFIRM_ACCEPTED.getOpcode();

		Player affectedPlayer = null;

		if (busy(player)) {
			player.getTrade().resetAll();
			return;
		}

		if (pID == packetOne) { // Sending trade request
			if (player.getWorld() != null) {
				affectedPlayer = player.getWorld().getPlayer(p.readShort());
			}

			if (affectedPlayer == null) {
				return;
			}
			if (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3)) {
				player.message("You are an Iron Man. You stand alone.");
				player.getTrade().resetAll();
				return;
			}
			if (affectedPlayer.isIronMan(1) || affectedPlayer.isIronMan(2) || affectedPlayer.isIronMan(3)) {
				player.message(affectedPlayer.getUsername() + " is an Iron Man. He stands alone.");
				player.getTrade().resetAll();
				return;
			}
			if (affectedPlayer.getTrade().isTradeActive()) {
				player.message("That person is already trading");
				return;
			}
			if (affectedPlayer == null || affectedPlayer.getDuel().isDuelActive()
				|| player.getTrade().isTradeActive()) {
				player.getTrade().resetAll();
				return;
			}
			if (player.equals(affectedPlayer)) {
				player.setSuspiciousPlayer(true, "player trading themselves");
				player.getTrade().resetAll();
				return;
			}
			if ((affectedPlayer.getSettings().getPrivacySetting(PlayerSettings.PRIVACY_BLOCK_TRADE_REQUESTS)
				&& !affectedPlayer.getSocial().isFriendsWith(player.getUsernameHash()))
				|| affectedPlayer.getSocial().isIgnoring(player.getUsernameHash())) {
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
				ActionSender.sendMessage(player, null, 0, MessageType.INVENTORY, affectedPlayer.getTrade().isTradeActive()
					? affectedPlayer.getUsername() + " is already in a trade" : "Sending trade request", 0);

				ActionSender.sendMessage(affectedPlayer, player, 1, MessageType.TRADE, "", player.getIcon());

			}
		} else if (pID == packetTwo) { // Trade accepted
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

		} else if (pID == packetFive) { // Second Confirm accepted
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
				ArrayList<Item> myOffer = player.getTrade().getTradeOffer().getItems();
				ArrayList<Item> theirOffer = affectedPlayer.getTrade().getTradeOffer().getItems();

				int myRequiredSlots = player.getInventory().getRequiredSlots(theirOffer);
				int myAvailableSlots = (30 - player.getInventory().size())
					+ player.getInventory().getFreedSlots(myOffer);

				int theirRequiredSlots = affectedPlayer.getInventory().getRequiredSlots(myOffer);
				int theirAvailableSlots = (30 - affectedPlayer.getInventory().size())
					+ affectedPlayer.getInventory().getFreedSlots(theirOffer);

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

				for (Item item : myOffer) {
					Item affectedItem = player.getInventory().get(item);
					if (affectedItem == null) {
						player.setSuspiciousPlayer(true, "trade item is null");
						player.getTrade().resetAll();
						return;
					}
					if (affectedItem.isWielded()) {
						player.getInventory().unwieldItem(affectedItem, false);
					}
					player.getInventory().remove(item);
				}
				for (Item item : theirOffer) {
					Item affectedItem = affectedPlayer.getInventory().get(item);
					if (affectedItem == null) {
						affectedPlayer.setSuspiciousPlayer(true, "other trade item is null");
						player.getTrade().resetAll();
						return;
					}
					if (affectedItem.isWielded()) {
						affectedPlayer.getInventory().unwieldItem(affectedItem, false);
					}
					affectedPlayer.getInventory().remove(item);
				}

				for (Item item : myOffer) {
					affectedPlayer.getInventory().add(item);
				}
				for (Item item : theirOffer) {
					player.getInventory().add(item);
				}

				player.getWorld().getServer().getGameLogger().addQuery(
					new TradeLog(player.getWorld(), player.getUsername(), affectedPlayer.getUsername(), myOffer, theirOffer, player.getCurrentIP(), affectedPlayer.getCurrentIP()).build());
				player.save();
				affectedPlayer.save();
				player.message("Trade completed successfully");

				affectedPlayer.message("Trade completed successfully");
				player.getTrade().resetAll();
			}
		} else if (pID == packetThree) { // Trade declined
			affectedPlayer = player.getTrade().getTradeRecipient();
			if (affectedPlayer == null || busy(affectedPlayer) || !player.getTrade().isTradeActive()
				|| !affectedPlayer.getTrade().isTradeActive()) {
				player.setSuspiciousPlayer(true, "declined trade isn't active or affected player is null or busy");
				player.getTrade().resetAll();
				return;
			}
			affectedPlayer.message("Other player has declined trade");

			player.getTrade().resetAll();
		} else if (pID == packetFour) { // Receive offered item data
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

			player.getTrade().setTradeAccepted(false);
			player.getTrade().setTradeConfirmAccepted(false);
			affectedPlayer.getTrade().setTradeAccepted(false);
			affectedPlayer.getTrade().setTradeConfirmAccepted(false);


			player.getTrade().resetOffer();
			int count = (int) p.readByte();
			for (int slot = 0; slot < count; slot++) {
				Item tItem = new Item(p.readShort(), p.readInt());

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
				if (tItem.getDef(player.getWorld()).isMembersOnly() && !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
					player.setRequiresOfferUpdate(true);
					continue;
				}

				if (tItem.getAmount() > player.getInventory().countId(tItem.getID())) {
					player.setSuspiciousPlayer(true, "trade item amount greater than inventory countid");
					player.getTrade().resetAll();
					return;
				}
				player.getTrade().addToOffer(tItem);
			}

			affectedPlayer.setRequiresOfferUpdate(true);
			player.setRequiresOfferUpdate(true);
		}
	}

}
