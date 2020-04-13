package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public final class ItemDropHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception{
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		if (player.getStatus() == Action.DROPPING_GITEM) {
			return;
		}
		player.resetAll();
		int idx = (int) p.readShort();
		int amount = p.readInt();

		if (idx < -1 || idx >= player.getCarriedItems().getInventory().size()) {
			player.setSuspiciousPlayer(true, "item drop item idx < -1 or idx >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to drop the item from equipment tab
		if (idx == -1) {
			int realid = (int) p.readShort();
			int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(realid);
			if (slot != -1)
				tempitem = player.getCarriedItems().getEquipment().get(slot);
		} else {
			tempitem = player.getCarriedItems().getInventory().get(idx);
		}

		if (tempitem == null) {
			player.setSuspiciousPlayer(true, "item drop null item");
			return;
		}
		final Item item = tempitem.clone();

		if (amount <= 0) {
			return;
		}

		if (item.getDef(player.getWorld()).isStackable() || item.getItemStatus().getNoted()) {
			if (amount > item.getAmount()) {
				amount = item.getAmount();
			}
		} else if (idx != -1) {
			if (amount > player.getCarriedItems().getInventory().countId(item.getCatalogId())) {
				amount = player.getCarriedItems().getInventory().countId(item.getCatalogId());
			}
		}

		final int finalAmount = amount;
		final boolean fromInventory = idx != -1;

		// Set temporary amount until event executes and double checks.
		item.getItemStatus().setAmount(amount);

		// Set up our player to drop an item after walking
		if (!player.getWalkingQueue().finished()) {
			player.setDropItemEvent(item);
		}
		else {
			player.setDropItemEvent(item);
			player.runDropEvent(fromInventory);
		}
	}
}
