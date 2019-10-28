package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class NpcUseItem implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		int npcIndex = p.readShort();
		final Npc affectedNpc = player.getWorld().getNpc(npcIndex);
		final Item item = player.getInventory().get(p.readShort());
		if (affectedNpc == null || item == null) {
			return;
		}
		player.setFollowing(affectedNpc);
		player.setStatus(Action.USING_Item_ON_NPC);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, 1) {
			public void execute() {
				player.resetPath();
				player.resetFollowing();
				if (!player.getInventory().contains(item) || player.isBusy()
					|| player.isRanging() || !player.canReach(affectedNpc)
					|| affectedNpc.isBusy()
					|| player.getStatus() != Action.USING_Item_ON_NPC) {
					return;
				}
				player.resetAll();

				if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(
					"InvUseOnNpc",
					new Object[]{player, affectedNpc, item}))
					return;

				switch (affectedNpc.getID()) {

					default:
						player.message("Nothing interesting happens");
						break;
				}
				if (item.getDef(player.getWorld()).isMembersOnly()
					&& !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
					player.message(player.MEMBER_MESSAGE);
					return;
				}
			}
		});
	}
}
