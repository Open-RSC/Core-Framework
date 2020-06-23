package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;

import java.util.Optional;

public final class InterfaceShopHandler implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {

		int pID = packet.getID();
		if (player.isBusy()) {
			player.resetShop();
			return;
		}

		final Shop shop = player.getShop();
		if (shop == null) {
			player.setSuspiciousPlayer(true, "shop is null");
			player.resetShop();
			return;
		}

		int closeShop = OpcodeIn.SHOP_CLOSE.getOpcode();
		int buyItem = OpcodeIn.SHOP_BUY.getOpcode();
		int sellItem = OpcodeIn.SHOP_SELL.getOpcode();

		if (pID == closeShop) { // Close shop
			player.resetShop();
			return;
		}
		int catalogID = packet.readShort();
		int shopAmount = packet.readUnsignedShort();
		int amount = packet.readUnsignedShort();
		ItemDefinition def = player.getWorld().getServer().getEntityHandler().getItemDef(catalogID);
		if (def.isMembersOnly() && !player.getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}

		// Buy item
		if (pID == buyItem) {
			buyShopItem(player, shop, def, catalogID, amount);
		}

		// Sell item
		else if (pID == sellItem) {
			sellShopItem(player, shop, def, catalogID, amount);
		}
	}

	private void buyShopItem(Player player, Shop shop, ItemDefinition def, int catalogID, int amount) {

		// Normalize amount to the minimum shop count if we are trying to purchase more.
		amount = Math.min(amount, shop.getItemCount(catalogID));

		int totalBought = 0;
		int totalMoneySpent = 0;

		Item tempItem = new Item(catalogID);
		if (tempItem.getDef(player.getWorld()).isStackable() || tempItem.getNoted()) {
			// If purchase is valid, proceed to set totals.
			if (!checkPurchaseValidity(player, shop, def, catalogID, amount, 0)) {
				totalMoneySpent = amount * shop.getItemBuyPrice(catalogID, def.getDefaultPrice(), amount);
				totalBought = amount;

				shop.removeShopItem(new Item(catalogID, totalBought));
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), totalMoneySpent));
				player.getCarriedItems().getInventory().add(new Item(catalogID, amount));
			}
		}

		// Not a stack.
		else {
			amount = Math.min(amount, player.getCarriedItems().getInventory().getFreeSlots());
			for (int i = 0; i < amount; i++) {
				totalBought++;

				if (checkPurchaseValidity(player, shop, def, catalogID, totalBought, totalMoneySpent)) {
					break;
				}

				player.getCarriedItems().getInventory().add(new Item(catalogID, 1));

				totalMoneySpent += shop.getItemBuyPrice(catalogID, def.getDefaultPrice(), 1);
			}

			if (totalMoneySpent > 0) {
				shop.removeShopItem(new Item(catalogID, totalBought));
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), totalMoneySpent));
			}
		}

		if (totalBought <= 0 && totalMoneySpent <= 0) {
			return;
		}

		player.playSound("coins");
		player.getWorld().getServer().getGameLogger().addQuery(
			new GenericLog(player.getWorld(),
				player.getUsername() + " bought " + def.getName() + " x" + totalBought
					+ " for " + totalMoneySpent + "gp" + " at " + player.getLocation().toString()));

	}

	private boolean checkPurchaseValidity(Player player, Shop shop, ItemDefinition def, int catalogID, int totalBought, int totalMoneySpent) {
		if ((player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
			|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id()))
			&& shop.getItemCount(catalogID) > shop.getStock(catalogID)) {
			player.message("Iron Men may not buy items that are over-stocked in a shop.");
			return true;
		}
		if (shop.getItemCount(catalogID) - totalBought < 0) {
			player.message("The shop has ran out of stock");
			return true;
		}
		int price = shop.getItemBuyPrice(catalogID, def.getDefaultPrice(), totalBought);
		if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) - totalMoneySpent < price) {
			player.message("You don't have enough coins");
			return true;
		}
		Item tempItem = new Item(catalogID);
		if (!player.getCarriedItems().getInventory().canHold(tempItem, totalBought)) {
			player.message("You can't hold the objects you are trying to buy!");
			return true;
		}

		return false;
	}

	private void sellShopItem(Player player, Shop shop, ItemDefinition def, int catalogID, int amount) {
		if (def.isUntradable() || !shop.shouldStock(catalogID)) {
			player.message("This object can't be sold in shops");
			return;
		}
		if (!shop.canHoldItem(new Item(catalogID))) {
			player.message("The shop is currently full!");
			return;
		}

		// TODO: How to handle this case?
		if (amount < 0) return;

		int totalMoney = 0;
		int totalSold = 0;

		amount = Math.min(amount, player.getCarriedItems().getInventory().countId(catalogID, Optional.empty()));
		Item tempItem = new Item(catalogID);
		tempItem.getItemStatus().setNoted(player.getCarriedItems().getInventory().countId(catalogID, Optional.of(true)) > 0);
		if (tempItem.getDef(player.getWorld()).isStackable() || tempItem.getNoted()) {
			Item toSell = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(catalogID, Optional.of(tempItem.getNoted()))
			);
			if (toSell == null) {
				player.message("You don't have that many items");
				return;
			}
			amount = Math.min(amount, toSell.getAmount());
			int sellAmount = 0;
			for (int i = 1; i <= amount; i++) {
				sellAmount += shop.getItemSellPrice(catalogID, def.getDefaultPrice(), i);
			}

			totalMoney += sellAmount;
			totalSold += amount;

			shop.addShopItem(new Item(catalogID, amount));

			player.getCarriedItems().remove(new Item(toSell.getCatalogId(), amount, toSell.getNoted(), toSell.getItemId()));

			if (sellAmount > 0) {
				player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), sellAmount));
			}
		}
		else {
			for (int i = 0; i < amount; i++) {
				Item toSell = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(catalogID, Optional.of(false))
				);
				if (toSell == null) {
					player.message("You don't have that many items");
					return;
				}

				player.getCarriedItems().remove(new Item(toSell.getCatalogId(), 1, toSell.getNoted(), toSell.getItemId()));

				int sellAmount = shop.getItemSellPrice(catalogID, def.getDefaultPrice(), 1);
				totalMoney += sellAmount;
				totalSold++;

				if (sellAmount > 0) {
					player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), sellAmount));
				}

				shop.addShopItem(new Item(catalogID, 1));
			}
		}

		player.playSound("coins");
		player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " sold " + def.getName() + " x" + totalSold
			+ " for " + totalMoney + "gp" + " at " + player.getLocation().toString()));
	}
}
