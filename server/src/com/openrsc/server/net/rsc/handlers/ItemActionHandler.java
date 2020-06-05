package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.action.WalkToAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemActionHandler implements PacketHandler {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	public void handlePacket(Packet packet, Player player) throws Exception {
		int idx = packet.readShort();
		int amount = packet.readInt();
		int commandIndex;

		if (player == null || player.getCarriedItems().getInventory() == null) {
			return;
		}

		if (idx < -1) {
			player.setSuspiciousPlayer(true, "item idx < -1");
			return;
		}

		if (idx >= player.getCarriedItems().getInventory().size()) {
			player.setSuspiciousPlayer(true, "idx >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to use the item from equipment tab
		if (idx == -1) {
			idx = packet.readShort();
			int slot = player.getCarriedItems().getEquipment().searchEquipmentForItem(idx);
			if (slot != -1) {
				tempitem = player.getCarriedItems().getEquipment().get(slot);
			}
			commandIndex = packet.readByte();
		} else {
			tempitem = player.getCarriedItems().getInventory().get(idx);
			commandIndex = packet.readByte();
		}

		if (tempitem == null) return;

		final Item item = amount > 1 ? new Item(tempitem.getCatalogId(), amount) : tempitem;
		if (item == null || item.getDef(player.getWorld()).getCommand() == null
		|| commandIndex < 0 || commandIndex >= item.getDef(player.getWorld()).getCommand().length) {
			player.setSuspiciousPlayer(true, "item action item null or null item def");
			return;
		}

		if (item.getDef(player.getWorld()).isMembersOnly() && !player.getConfig().MEMBER_WORLD) {
			player.message("You need to be a member to use this object");
			return;
		}

		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}

		if (player.isBusy()) {
			return;
		}

		player.resetAll(false, false);

		// We want to keep walking, but not perform the action when we get there.
		final WalkToAction walkToAction = player.getWalkToAction();
		if (walkToAction != null) {
			walkToAction.finishExecution();
		}

		final String command = item.getDef(player.getWorld()).getCommand()[commandIndex];

		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "OpInv", new Object[]{player, idx, item, command});
	}
}
