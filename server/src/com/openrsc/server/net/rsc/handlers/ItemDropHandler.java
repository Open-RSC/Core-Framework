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
		int idx = (int) packet.readShort();
		int amount = packet.readInt();

		if (idx < -1 || idx >= player.getCarriedItems().getInventory().size()) {
			player.setSuspiciousPlayer(true, "item drop item idx < -1 or idx >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to drop the item from equipment tab
		if (idx == -1) {
			int realid = (int) packet.readShort();
			int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(realid);
			if (slot != -1)
				tempitem = player.getCarriedItems().getEquipment().get(slot);
		} else {
			tempitem = player.getCarriedItems().getInventory().get(idx);
		}

		if (tempitem == null || tempitem.getCatalogId() == ItemId.NOTHING.id()) {
			return;
		}
		final Item item = new Item(tempitem.getCatalogId(), amount, tempitem.getNoted(), tempitem.getItemId());

		if (amount <= 0) {
			return;
		}

		if (idx != -1) {
			if (amount > player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()))) {
				amount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(item.getNoted()));
			}
		}

		final boolean fromInventory = idx != -1;

		// Set temporary amount until event executes and double checks.
		item.getItemStatus().setAmount(amount);

		// Set up our player to drop an item after walking
		if (!player.getWalkingQueue().finished()) {
			player.setDropItemEvent(idx, item);
		}
		else {
			player.setDropItemEvent(idx, item);
			player.runDropEvent(fromInventory);
		}
	}
}
