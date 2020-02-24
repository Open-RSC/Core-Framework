package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public final class ItemDropHandler implements PacketHandler {

	public void handlePacket(Packet p, Player player) {
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

		if (idx < -1 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true, "item drop item idx < -1 or idx >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to drop the item from equipment tab
		if (idx == -1) {
			int realid = (int) p.readShort();
			int slot = player.getEquipment().searchEquipmentForItem(realid);
			if (slot != -1)
				tempitem = player.getEquipment().get(slot);
		} else {
			tempitem = player.getInventory().get(idx);
		}
		final Item item = tempitem;

		if (item == null) {
			player.setSuspiciousPlayer(true, "item drop null item");
			return;
		}
		if (amount <= 0) {
			return;
		}

		if (item.getDef(player.getWorld()).isStackable() || item.getItemStatus().getNoted()) {
			if (amount > item.getAmount()) {
				amount = item.getAmount();
			}
		} else if (idx != -1) {
			if (amount > player.getInventory().countId(item.getCatalogId())) {
				amount = player.getInventory().countId(item.getCatalogId());
			}
		}

		final int finalAmount = amount;
		final boolean fromInventory = idx != -1;
		if (player.finishedPath()) {
			player.setStatus(Action.DROPPING_GITEM);

			if (item.getDef(player.getWorld()).isStackable() || item.getItemStatus().getNoted() || finalAmount == 1) {
				int dropAmount = finalAmount;
				if(!(item.getDef(player.getWorld()).isStackable() || item.getItemStatus().getNoted())) {
					dropAmount = 1;
				}
				item.setAmount(dropAmount);

				if ((!player.getInventory().contains(item) && fromInventory)  || player.getStatus() != Action.DROPPING_GITEM) {
					player.setStatus(Action.IDLE);
					return;
				}

				player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Drop", new Object[]{player, item, fromInventory});
				player.setStatus(Action.IDLE);
			} else {
				player.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(player.getWorld(), player, 640, "Player Batch Drop") {
					int dropCount = 0;
					public void run() {
						if ((!getOwner().getInventory().contains(item) && fromInventory) || getOwner().getStatus() != Action.DROPPING_GITEM) {
							stop();
							player.setStatus(Action.IDLE);
							return;
						}
						if (getOwner().hasMoved()) {
							stop();
							player.setStatus(Action.IDLE);
							return;
						}
						if (dropCount >= finalAmount) {
							stop();
							player.setStatus(Action.IDLE);
							return;
						}
						if ((fromInventory && !player.getInventory().hasItemId(item.getCatalogId())) ||
							(!fromInventory && (player.getEquipment().searchEquipmentForItem(item.getCatalogId())) == -1)) {
							player.message("You don't have the entered amount to drop");
							stop();
							player.setStatus(Action.IDLE);
							return;
						}
						if (player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Drop", new Object[]{player, item, fromInventory})) {
							stop();
							player.setStatus(Action.IDLE);
							return;
						}
						dropCount++;
						player.message("Dropped " + dropCount + "/" + finalAmount);
					}
				});
			}
		}
	}
}
