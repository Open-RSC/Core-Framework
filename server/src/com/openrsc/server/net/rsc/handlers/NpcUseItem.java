package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemOnMobStruct;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

public class NpcUseItem implements PayloadProcessor<ItemOnMobStruct, OpcodeIn> {

	public void process(ItemOnMobStruct payload, Player player) throws Exception {
		NpcInteraction interaction = NpcInteraction.NPC_USE_ITEM;
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		int npcIndex = payload.serverIndex;
		final Npc affectedNpc = player.getWorld().getNpc(npcIndex);
		final Item item = player.getCarriedItems().getInventory().get(payload.slotIndex);
		if (affectedNpc == null || item == null) {
			return;
		}
		player.setFollowing(affectedNpc, 1, false, true);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, 1) {
			public void executeInternal() {
				if (!getPlayer().getCarriedItems().getInventory().contains(item) || getPlayer().isBusy()
					|| getPlayer().isRanging() || affectedNpc.isBusy()) {
					return;
				}
				getPlayer().resetAll(true, false);

				NpcInteraction.setInteractions(affectedNpc, getPlayer(), interaction);

				if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(
						UseNpcTrigger.class,
						getPlayer(),
						new Object[]{getPlayer(), affectedNpc, item}, this)) {
					return;
				}

				switch (affectedNpc.getID()) {

					default:
						getPlayer().message("Nothing interesting happens");
						break;
				}
				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}
			}
		});
	}
}
