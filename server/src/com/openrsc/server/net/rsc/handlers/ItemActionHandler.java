package com.openrsc.server.net.rsc.handlers;

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

	public void handlePacket(Packet p, Player player) throws Exception {

		int idx = (int) p.readShort();
		int amount = p.readInt();
		int commandIndex;

		if (player == null || player.getInventory() == null) {
			return;
		}

		if (idx < -1 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true, "item idx < -1 or idx >= inv size");
			return;
		}
		Item tempitem = null;

		//User wants to use the item from equipment tab
		if (idx == -1)
		{
			idx = (int) p.readShort();
			if (player.getEquipment().searchEquipmentForItem(idx) != -1)
				tempitem = new Item(idx);
			commandIndex = p.readByte();

		} else {
			tempitem = player.getInventory().get(idx);
			commandIndex = p.readByte();
		}

		final Item item = tempitem;
		if (item == null || item.getDef(player.getWorld()).getCommand() == null
		|| commandIndex < 0 || commandIndex >= item.getDef(player.getWorld()).getCommand().length) {
			player.setSuspiciousPlayer(true, "item action item null or null item def");
			return;
		}

		item.setAmount(player.getWorld().getServer().getDatabase(), amount);

		if (item.getDef(player.getWorld()).isMembersOnly() && !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			player.message("You need to be a member to use this object");
			return;
		}

		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			return;
		}

		player.resetAll();

		final String command = item.getDef(player.getWorld()).getCommand()[commandIndex];

		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "InvAction", new Object[]{item, player, command});
	}
}
