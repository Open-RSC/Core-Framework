package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Inventory;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemUseOnNpc implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		int npcIndex = p.readShort();
		final Npc affectedNpc = player.getWorld().getNpc(npcIndex);
		int itemID = p.readShort();
		if (player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && itemID > Inventory.MAX_SIZE) {
			player.message("Please unequip your item and try again.");
			return;
		}
		final Item item = player.getInventory().get(itemID);
		if (affectedNpc == null || item == null) {
			return;
		}
		player.setFollowing(affectedNpc, 0);
		player.setStatus(Action.USING_Item_ON_NPC);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, 1) {
			public void executeInternal() {
				getPlayer().resetPath();
				getPlayer().resetFollowing();
				if (!getPlayer().getInventory().contains(item) || getPlayer().isBusy()
					|| getPlayer().isRanging() || !getPlayer().canReach(affectedNpc)
					|| affectedNpc.isBusy()
					|| getPlayer().getStatus() != Action.USING_Item_ON_NPC) {
					return;
				}
				getPlayer().resetAll();
				getPlayer().face(affectedNpc);
				if (getPlayer().getWorld().getServer().getPluginHandler().blockDefaultAction(
					getPlayer(),
					"InvUseOnNpc",
					new Object[]{getPlayer(), affectedNpc, item}, true, this))
					return;

				switch (affectedNpc.getID()) {

					default:
						getPlayer().message("Nothing interesting happens");
						break;
				}
				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getWorld().getServer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}
			}
		});
	}

}
