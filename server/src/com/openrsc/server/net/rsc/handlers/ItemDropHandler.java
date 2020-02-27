package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.database.GameDatabaseException;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		if (player.finishedPath()) {
			player.setStatus(Action.DROPPING_GITEM);

			if (item.getDef(player.getWorld()).isStackable() || item.getItemStatus().getNoted() || finalAmount == 1) {
				int dropAmount = finalAmount;
				if(!(item.getDef(player.getWorld()).isStackable() || item.getItemStatus().getNoted())) {
					dropAmount = 1;
				}

				item.getItemStatus().setAmount(dropAmount);

				if ((!player.getCarriedItems().getInventory().contains(item) && fromInventory)  || player.getStatus() != Action.DROPPING_GITEM) {
					player.setStatus(Action.IDLE);
					return;
				}

				player.getWorld().getServer().getPluginHandler().handlePlugin(player, "Drop", new Object[]{player, item, fromInventory});
				player.setStatus(Action.IDLE);
			} else {
				player.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(player.getWorld(), player, 640, "Player Batch Drop") {
					int dropCount = 0;
					public void run() {
						if ((!getOwner().getCarriedItems().getInventory().contains(item) && fromInventory) || getOwner().getStatus() != Action.DROPPING_GITEM) {
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
						if ((fromInventory && !player.getCarriedItems().hasCatalogID(item.getCatalogId())) ||
							(!fromInventory && (player.getCarriedItems().getEquipment().searchEquipmentForItem(item.getCatalogId())) == -1)) {
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
