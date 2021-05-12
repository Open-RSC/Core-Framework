package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.database.impl.mysql.queries.logging.GenericLog;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ShopStruct;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

public final class InterfaceShopHandler implements PayloadProcessor<ShopStruct, OpcodeIn> {

	public void process(ShopStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			return;
		}
		if (player.isBusy()) {
			player.resetShop();
			return;
		}

		OpcodeIn packetID = payload.getOpcode();
		final Shop shop = player.getShop();
		if (shop == null) {
			player.setSuspiciousPlayer(true, "shop is null");
			player.resetShop();
			return;
		}

		OpcodeIn closeShop = OpcodeIn.SHOP_CLOSE;
		OpcodeIn buyItem = OpcodeIn.SHOP_BUY;
		OpcodeIn sellItem = OpcodeIn.SHOP_SELL;

		if (packetID == closeShop) { // Close shop
			player.resetShop();
			return;
		}
		int catalogID = payload.catalogID;
		// TODO: there should probably be a sanity check here to make sure the client is in sync and selling/buying at the agreed-on price
		int shopAmount = payload.stockAmount;
		int amount = payload.amount;

		if (amount <= 0) return;

		ItemDefinition def = player.getWorld().getServer().getEntityHandler().getItemDef(catalogID);
		if (def == null) {
			return;
		}
		if (def.isMembersOnly() && !player.getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}

		if (player.getWorld().getPlayer(DataConversions.usernameToHash(player.getUsername())) == null) {
			return;
		}

		// Buy item
		if (packetID == buyItem) {
			buyShopItem(player, shop, def, catalogID, amount);
		}

		// Sell item
		else if (packetID == sellItem) {
			sellShopItem(player, shop, def, catalogID, amount);
		}
	}

	private void buyShopItem(Player player, Shop shop, ItemDefinition def, int catalogID, int amount) {

		// Normalize amount to the minimum shop count if we are trying to purchase more.
		int originalAmount = amount;
		int shopStock = shop.getItemCount(catalogID);
		amount = Math.min(amount, shopStock);

		if (amount <= 0) {
			// if (originalAmount > amount) {
				// TODO: need to find if there is a specific error message when trying to buy more than the shop has
			// }
			return;
		}

		int totalBought = 0;
		int totalMoneySpent = 0;

		Item tempItem = new Item(catalogID);
		if (tempItem.getDef(player.getWorld()).isStackable() || tempItem.getNoted()) {
			// If purchase is valid, proceed to set totals.
			totalMoneySpent = 0;
			totalBought = 0;
			for (int i = 0; i < amount; i++) {
				if (checkPurchaseValidity(player, shop, def, catalogID, i, totalMoneySpent, i)) {
					break;
				}
				totalMoneySpent += shop.getItemBuyPrice(catalogID, def.getDefaultPrice(), i);
				totalBought++;
			}

			if (totalBought > 0) {
				shop.removeShopItem(new Item(catalogID, totalBought));
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), totalMoneySpent));
				player.getCarriedItems().getInventory().add(new Item(catalogID, totalBought));
			}
		}

		// Not a stack.
		else {
			amount = Math.min(amount, player.getCarriedItems().getInventory().getFreeSlots());
			if (amount <= 0) {
				if (originalAmount > amount) {
					player.message("You can't hold the objects you are trying to buy!");
				}
				return;
			}
			for (int i = 0; i < amount; i++) {
				if (checkPurchaseValidity(player, shop, def, catalogID, totalBought, totalMoneySpent, 1)) {
					break;
				}
				totalMoneySpent += shop.getItemBuyPrice(catalogID, def.getDefaultPrice(), totalBought);
				totalBought++;

				player.getCarriedItems().getInventory().add(new Item(catalogID, 1));
			}

			// TODO: See if removing this breaks stuff, as it currently allows for a dupe
			// Items that have basePrice of 0 will not deplete from the store when bought.
			if (totalMoneySpent > 0) {
				shop.removeShopItem(new Item(catalogID, totalBought));
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), totalMoneySpent));
			}
		}

		if (totalBought <= 0 && totalMoneySpent <= 0) {
			return;
		}

		// attempted to buy more
		if (originalAmount > totalBought && totalBought < shopStock) {
			player.message("You can't hold the objects you are trying to buy!");
		}

		player.playSound("coins");
		player.getWorld().getServer().getGameLogger().addQuery(
			new GenericLog(player.getWorld(),
				player.getUsername() + " bought " + def.getName() + " x" + totalBought
					+ " for " + totalMoneySpent + "gp" + " at " + player.getLocation().toString()));

	}

	private boolean checkPurchaseValidity(Player player, Shop shop, ItemDefinition def, int catalogID, int totalBought, int totalMoneySpent, int buyingNow) {
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
		if (!player.getCarriedItems().getInventory().canHold(catalogID, buyingNow)) {
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

		int originalAmount = amount;
		int totalMoney = 0;
		int totalSold = 0;

		amount = Math.min(
			Math.min(originalAmount,
			player.getCarriedItems().getInventory().countId(catalogID, Optional.empty())),
			(Short.MAX_VALUE - Short.MIN_VALUE) - shop.currentStock(new Item(catalogID)));

		if (amount <= 0) {
			player.message("You don't have that many items");
			return;
		}

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

		// attempted to sell more
		if (originalAmount > totalSold) {
			player.message("You don't have that many items");
		}

		player.playSound("coins");
		player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " sold " + def.getName() + " x" + totalSold
			+ " for " + totalMoney + "gp" + " at " + player.getLocation().toString()));
	}
}
