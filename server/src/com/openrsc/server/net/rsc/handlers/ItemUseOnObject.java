package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToObjectAction;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemUseOnObject implements PacketHandler {

	private void handleDoor(final Player player, final Point location,
							final GameObject object, final int dir, final Item item) {
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void executeInternal() {
				getPlayer().resetPath();
				GameObject obj = getPlayer().getViewArea().getWallObjectWithDir(
					object.getLocation(), object.getDirection());
				if (getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().getCarriedItems().hasCatalogID(item.getCatalogId()) || obj == null
					|| !obj.equals(object)) {
					return;
				}
				getPlayer().resetAll();

				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}
				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
					getPlayer(),
					"UseBound",
					new Object[]{getPlayer(), object, item}, this))
					return;
			}
		});
	}

	private void handleObject(final Player player, final Point location,
							  final GameObject object, final Item item) {
		if ((object.getID() == 226 || object.getID() == 232) && player.withinRange(object, 2)) {
			player.resetPath();
			player.resetAll();
			if (player.getWorld().getServer().getPluginHandler().handlePlugin(
				player, "UseLoc", new Object[]{player, object, item}))
				return;
		}
		player.setWalkToAction(new WalkToObjectAction(player, object) {
			public void executeInternal() {
				getPlayer().resetPath();
				GameObject obj = getPlayer().getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (obj == null || getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().getCarriedItems().getInventory().contains(item)
					|| !getPlayer().atObject(object) || obj == null) {
					return;
				}
				getPlayer().resetAll();

				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}

				if (getPlayer().getWorld().getServer().getPluginHandler()
					.handlePlugin(getPlayer(), "UseLoc",
						new Object[]{getPlayer(), (GameObject) object, item}, this))
					return;
			}
		});
	}

	public void handlePacket(Packet packet, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}
		if (player.isBusy()) {
			player.resetPath();// sendSound
			return;
		}
		int pID = packet.getID();
		player.resetAll();
		GameObject object;
		Item item;
		int packetOne = OpcodeIn.USE_WITH_BOUNDARY.getOpcode();
		int packetTwo = OpcodeIn.USE_ITEM_ON_SCENERY.getOpcode();

		if (pID == packetOne) { // Use Item on Boundary
			object = player.getViewArea().getWallObjectWithDir(Point.location(packet.readShort(), packet.readShort()), packet.readByte());
			if (object == null) {
				player.setSuspiciousPlayer(true, "item on null door");
				player.resetPath();
				return;
			}
			int dir = object.getDirection();
			int slotID = packet.readShort();
			if (player.getConfig().WANT_EQUIPMENT_TAB && slotID == -1)
			{
				//they used the item from their equipment slot
				if (!player.isUsingAuthenticClient()) {
					int itemID = packet.readShort();
					int realSlot = player.getCarriedItems().getEquipment().searchEquipmentForItem(itemID);
					if (realSlot == -1)
						return;
					item = player.getCarriedItems().getEquipment().get(realSlot);
					if (item == null)
						return;
				} else {
					player.message("authentic client can't use item from non-existent equipment slot.");
					return;
				}
			} else
				item = player.getCarriedItems().getInventory().get(slotID);
			if (object.getType() == 0 || item == null || item.getItemStatus().getNoted()) { // This
				player.setSuspiciousPlayer(true, "item on null equipment slot or something");
				return;
			}
			handleDoor(player, object.getLocation(), object, dir, item);
		} else if (pID == packetTwo) { // Use Item on Scenery
			object = player.getViewArea().getGameObject(Point.location(packet.readShort(), packet.readShort()));
			if (object == null) {
				player.setSuspiciousPlayer(true, "item on null GameObject");
				player.resetPath();
				return;
			}
			int slotID = packet.readShort();
			if (player.getConfig().WANT_EQUIPMENT_TAB && slotID > Inventory.MAX_SIZE) {
				item = player.getCarriedItems().getEquipment().get(slotID - Inventory.MAX_SIZE);
			} else
				item = player.getCarriedItems().getInventory().get(slotID);
			if (object.getType() == 1 || item == null || item.getItemStatus().getNoted()) { // This
				player.setSuspiciousPlayer(true, "null item or object");
				return;
			}
			handleObject(player, object.getLocation(), object, item);
		}
	}

}
