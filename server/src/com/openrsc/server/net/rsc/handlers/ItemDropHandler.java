package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemCommandStruct;

import java.util.Optional;

public final class ItemDropHandler implements PayloadProcessor<ItemCommandStruct, OpcodeIn> {

	public void process(ItemCommandStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			player.resetPath();
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		if (player.getTrade().isTradeActive() || (player.getDuel().isDuelActive() && !player.inCombat())) {
			// prevent dropping of items during trade & duels windows
			return;
		}

		player.resetAll();
		int inventorySlot = payload.index;
		int amount;
		boolean respectDropX = player.isUsingCustomClient() && player.getWorld().getServer().getConfig().WANT_DROP_X;
		if (respectDropX) {
			amount = payload.amount;
		} else {
			amount = 0;
		}

		if (inventorySlot < -1 || inventorySlot >= player.getCarriedItems().getInventory().size()) {
			player.setSuspiciousPlayer(true, "item drop item inventorySlot < -1 or inventorySlot >= inv size");
			return;
		}
		Item tempitem = null;

		// User wants to drop the item from equipment tab
		if (inventorySlot == -1 && player.isUsingCustomClient() && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			int realid = payload.realIndex;
			int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(realid);
			if (slot != -1) {
				tempitem = player.getCarriedItems().getEquipment().get(slot);
				if (!respectDropX) {
					amount = tempitem.getAmount();
				}
			}
		} else {
			if (inventorySlot != -1) {
				tempitem = player.getCarriedItems().getInventory().get(inventorySlot);
				if (!respectDropX) {
					amount = tempitem.getAmount();
				}
			}
		}

		if (tempitem == null || tempitem.getCatalogId() == ItemId.NOTHING.id()) {
			return;
		}
		final Item item = new Item(tempitem.getCatalogId(), amount, tempitem.getNoted(), tempitem.getItemId());

		if (amount <= 0) {
			return;
		}

		if (item.getNoted() && !player.getConfig().WANT_BANK_NOTES) {
			player.message("Notes have been disabled; you cannot drop them anymore.");
			player.message("You may either deposit it in the bank or sell to a shop instead.");
			return;
		}

		if (item.getCatalogId() > player.getClientLimitations().maxItemId) {
			player.message("You don't even know what that is...!");
			player.message("Definitely update your client before trying to drop that item.");
			return;
		}

		if (inventorySlot != -1) {
			final int idCount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()));

			if (amount > idCount)
				amount = idCount;
		}

		final boolean fromInventory = inventorySlot != -1;

		// Set temporary amount until event executes and double checks.
		item.getItemStatus().setAmount(amount);

		// Set up our player to drop an item after walking
		if (!player.getWalkingQueue().finished()) {
			player.setDropItemEvent(inventorySlot, item);
		}
		else {
			player.setDropItemEvent(inventorySlot, item);
			player.runDropEvent(fromInventory);
		}
	}
}
