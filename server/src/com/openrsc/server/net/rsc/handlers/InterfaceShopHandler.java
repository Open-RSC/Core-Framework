package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.sql.GameLogging;
import com.openrsc.server.sql.query.logs.GenericLog;

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
			player.setSuspiciousPlayer(true);
			player.resetShop();
			return;
		}

		int packetOne = OpcodeIn.SHOP_CLOSE.getOpcode();
		int packetTwo = OpcodeIn.SHOP_BUY.getOpcode();
		int packetThree = OpcodeIn.SHOP_SELL.getOpcode();

		if (pID == packetOne) { // Close shop
			player.resetShop();
			return;
		}
		int itemID = p.readShort();
		int shopAmount = p.readShort();
		int amount = p.readShort();
		ItemDefinition def = EntityHandler.getItemDef(itemID);
		if (def.isMembersOnly() && !Constants.GameServer.MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		if (pID == packetTwo) { // Buy item
			int totalBought = 0;
			int totalMoneySpent = 0;

			int price = shop.getItemBuyPrice(itemID, def.getDefaultPrice(), 0);
			if (player.getInventory().countId(10) == price && player.getInventory().size() == 30 && amount == 1) {
				if (shop.getItemCount(itemID) - totalBought < 1) {
					player.message("The shop has ran out of stock");
					return;
				}
				totalBought++;
				totalMoneySpent += price;
				amount = 0;
			}

			for (int i = 0; i < amount; i++) {
				Item itemBeingBought = new Item(itemID, 1);
				if ((player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3)) && shop.getItemCount(itemID) > shop.getStock(itemID)) {
					player.message("Iron Men may not buy items that are over-stocked in a shop.");
					break;
				}
				if (shop.getItemCount(itemID) - totalBought < 1) {
					player.message("The shop has ran out of stock");
					break;
				}
				price = shop.getItemBuyPrice(itemID, def.getDefaultPrice(), totalBought);
				if (player.getInventory().countId(10) < (totalMoneySpent + price)) {
					player.message("You don't have enough coins");
					break;
				}
				if (!player.getInventory().canHold(itemBeingBought, totalBought)) {
					player.message("You can't hold the objects you are trying to buy!");
					break;
				}
				if (player.getInventory().size() + totalBought >= 30 && !itemBeingBought.getDef().isStackable()) {
					break;
				}
				totalMoneySpent += price;
				totalBought++;
			}

			if (totalBought <= 0 && totalMoneySpent <= 0) {
				return;
			}
			
			shop.removeShopItem(new Item(itemID, totalBought));
			player.getInventory().remove(10, totalMoneySpent);
			int correctItemsBought = totalBought;
			for (; totalBought > 0; totalBought--) {
				player.getInventory().add(new Item(itemID, 1), false);
    			// TODO: Does the authentic code send an update per item?
    			ActionSender.sendInventory(player);
			}
			
			player.playSound("coins");
			GameLogging.addQuery(new GenericLog(player.getUsername() + " bought " + def.getName() + " x" + correctItemsBought + " for " + totalMoneySpent + "gp" + " at " + player.getLocation().toString()));

		} else if (pID == packetThree) { // Sell item
			if (def.isUntradable() || !shop.shouldStock(itemID)) {
				player.message("This object can't be sold in shops");
				return;
			}
			if (!shop.canHoldItem(new Item(itemID, 1))) {
				player.message("The shop is currently full!");
				return;
			}

			int totalMoney = 0;
			int totalSold = 0;
			for (int i = 0; i < amount; i++) {
			    int sellAmount = 0;
				/* If no noted version can be removed */
				if (player.getInventory().remove(def.getNoteID(), 1) == -1) {
					/* Try removing with original item ID */
					if (player.getInventory().remove(itemID, 1) == -1) {
						/* Break, player doesn't have anything. */
						player.message("You don't have that many items");
						break;
					}
					//}

					totalSold++;
					/* if we are selling noted item, calculate price from the original item */
					if (def.getOriginalItemID() != -1) {
						sellAmount += shop.getItemSellPrice(def.getOriginalItemID(),
							EntityHandler.getItemDef(def.getOriginalItemID()).getDefaultPrice(), 1);
					} else {
						sellAmount += shop.getItemSellPrice(itemID, def.getDefaultPrice(), 1);
					}
					
					totalMoney += sellAmount;
				}
				if (sellAmount > 0) {
					player.getInventory().add(new Item(10, sellAmount));
				}
				
				// Determine if we are selling a Noted item
				Item originalItem = null;
				if (def.getOriginalItemID() != -1) {
					originalItem = new Item(def.getOriginalItemID(), 1);
				}
				
				shop.addShopItem(originalItem != null ? originalItem : new Item(itemID, 1));
				
    			// TODO: Does the authentic code send an update per item?
    			ActionSender.sendInventory(player);
			}

			player.playSound("coins");
			GameLogging.addQuery(new GenericLog(player.getUsername() + " sold " + def.getName() + " x" + totalSold
				+ " for " + totalMoney + "gp" + " at " + player.getLocation().toString()));
		}
	}
}
