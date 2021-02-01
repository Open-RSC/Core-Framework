package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

import java.util.Optional;

public final class ItemDropHandler implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception{
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			player.resetPath();
			return;
		}
		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();
		int inventorySlot = (int) packet.readShort();
		int amount;
		boolean respectDropX = !player.isUsingAuthenticClient() && player.getWorld().getServer().getConfig().WANT_DROP_X;
		if (respectDropX) {
			amount = packet.readInt();
		} else {
			amount = 0;
		}

		if (inventorySlot < -1 || inventorySlot >= player.getCarriedItems().getInventory().size()) {
			player.setSuspiciousPlayer(true, "item drop item inventorySlot < -1 or inventorySlot >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to drop the item from equipment tab
		if (inventorySlot == -1 && !player.isUsingAuthenticClient() && player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			int realid = (int) packet.readShort();
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

		if (inventorySlot != -1) {
			if (amount > player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()))) {
				amount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()));
			}
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
