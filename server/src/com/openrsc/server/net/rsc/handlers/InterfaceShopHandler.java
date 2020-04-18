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

	/**
	 * Author: Imposter
	 */

	public void handlePacket(Packet p, Player player) throws Exception {

		int pID = p.getID();
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
		int categoryID = p.readShort();
		int shopAmount = p.readShort();
		int amount = p.readShort();
		ItemDefinition def = player.getWorld().getServer().getEntityHandler().getItemDef(categoryID);
		if (def.isMembersOnly() && !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		if (pID == buyItem) { // Buy item
			int totalBought = 0;
			int totalMoneySpent = 0;

			int price = shop.getItemBuyPrice(categoryID, def.getDefaultPrice(), 0);
			if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) == price && player.getCarriedItems().getInventory().size() == 30 && amount == 1) {
				if (shop.getItemCount(categoryID) - totalBought < 1) {
					player.message("The shop has ran out of stock");
					return;
				}
				totalBought++;
				totalMoneySpent += price;
				amount = 0;
			}

			for (int i = 0; i < amount; i++) {
				Item itemBeingBought = new Item(categoryID);
				if ((player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
					|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id()))
					&& shop.getItemCount(categoryID) > shop.getStock(categoryID)) {
					player.message("Iron Men may not buy items that are over-stocked in a shop.");
					break;
				}
				if (shop.getItemCount(categoryID) - totalBought < 1) {
					player.message("The shop has ran out of stock");
					break;
				}
				price = shop.getItemBuyPrice(categoryID, def.getDefaultPrice(), totalBought);
				if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) < (totalMoneySpent + price)) {
					player.message("You don't have enough coins");
					break;
				}
				if (!player.getCarriedItems().getInventory().canHold(itemBeingBought, totalBought)) {
					player.message("You can't hold the objects you are trying to buy!");
					break;
				}
				if (player.getCarriedItems().getInventory().size() + totalBought >= 30 && !itemBeingBought.getDef(player.getWorld()).isStackable()) {
					break;
				}
				totalMoneySpent += price;
				totalBought++;
			}

			if (totalBought <= 0 && totalMoneySpent <= 0) {
				return;
			}

			shop.removeShopItem(new Item(categoryID, totalBought));
			player.getCarriedItems().remove(new Item(ItemId.COINS.id(), totalMoneySpent));
			int correctItemsBought = totalBought;
			for (; totalBought > 0; totalBought--) {
				player.getCarriedItems().getInventory().add(new Item(categoryID));
			}

			player.playSound("coins");
			player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " bought " + def.getName() + " x" + correctItemsBought + " for " + totalMoneySpent + "gp" + " at " + player.getLocation().toString()));

		} else if (pID == sellItem) { // Sell item
			if (def.isUntradable() || !shop.shouldStock(categoryID)) {
				player.message("This object can't be sold in shops");
				return;
			}
			if (!shop.canHoldItem(new Item(categoryID))) {
				player.message("The shop is currently full!");
				return;
			}

			// TODO: How to handle this case?
			if (amount < 0) return;

			int totalMoney = 0;
			int totalSold = 0;
			int ticker = 1;
			for (int i = 0; i < amount; amount -= ticker) {
				if (amount % 10 > 0) ticker = 1;
				else if (amount % 100 > 10) ticker = 10;
				else if (amount % 1000 > 100) ticker = 100;
				else if (amount % 10000 > 1000) ticker = 1000;
				else if (amount % 100000 > 10000) ticker = 10000;
				// Start with selling noted and move to normal after.
				Item toSell = player.getCarriedItems().getInventory().get(
					player.getCarriedItems().getInventory().getLastIndexById(categoryID, Optional.of(true))
				);
				if (toSell == null) {
					toSell = player.getCarriedItems().getInventory().get(
						player.getCarriedItems().getInventory().getLastIndexById(categoryID, Optional.of(false))
					);
				}
				ticker = Math.min(ticker, toSell.getAmount());
				if (player.getCarriedItems().remove(new Item(toSell.getCatalogId(), ticker, toSell.getNoted(), toSell.getItemId())) == -1) {
					/* Break, player doesn't have anything. */
					player.message("You don't have that many items");
					break;
				}

				int sellAmount = shop.getItemSellPrice(categoryID, def.getDefaultPrice(), ticker);
				totalMoney += sellAmount;
				totalSold++;

				if (sellAmount > 0) {
					player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), sellAmount));
				}

				shop.addShopItem(new Item(categoryID, ticker));

    			// TODO: Does the authentic code send an update per item?
    			ActionSender.sendInventory(player);
			}

			player.playSound("coins");
			player.getWorld().getServer().getGameLogger().addQuery(new GenericLog(player.getWorld(), player.getUsername() + " sold " + def.getName() + " x" + totalSold
				+ " for " + totalMoney + "gp" + " at " + player.getLocation().toString()));
		}
	}
}
