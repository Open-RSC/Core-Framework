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
		final Item item = player.getCarriedItems().getInventory().get(p.readShort());
		if (affectedNpc == null || item == null) {
			return;
		}
		player.setFollowing(affectedNpc);
		player.setStatus(Action.USING_Item_ON_NPC);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, 1) {
			public void executeInternal() {
				getPlayer().resetPath();
				getPlayer().resetFollowing();
				if (!getPlayer().getCarriedItems().getInventory().contains(item) || getPlayer().isBusy()
					|| getPlayer().isRanging() || !getPlayer().canReach(affectedNpc)
					|| affectedNpc.isBusy()
					|| getPlayer().getStatus() != Action.USING_Item_ON_NPC) {
					return;
				}
				getPlayer().resetAll();

				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
					getPlayer(),
					"InvUseOnNpc",
					new Object[]{getPlayer(), affectedNpc, item}, this))
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
