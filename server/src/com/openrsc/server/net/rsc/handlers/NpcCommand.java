package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;

public final class NpcCommand implements PayloadProcessor<TargetMobStruct, OpcodeIn> {

	public void process(TargetMobStruct payload, Player player) throws Exception {
		OpcodeIn pID = payload.getOpcode();
		int serverIndex = payload.serverIndex;
		if (player == null) return;
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}
		if (player.isBusy()) {
			return;
		}

		final boolean click = pID == OpcodeIn.NPC_COMMAND;
		player.click = click ? 0 : 1;
		final Npc affectedNpc = player.getWorld().getNpc(serverIndex);
		if (affectedNpc == null) return;
		int radius = 1;
		if (click && player.withinRange(affectedNpc, 1)
			&& affectedNpc.getDef().getCommand1().equalsIgnoreCase("pickpocket")) {
			radius = 0;
		}
		player.setFollowing(affectedNpc, 0);
		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, radius) {
			public void executeInternal() {
				getPlayer().resetFollowing();
				getPlayer().resetPath();
				if (getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().canReach(affectedNpc)) {
					return;
				}
				getPlayer().resetAll();
				NPCDef def = affectedNpc.getDef();
				String command = (click ? def.getCommand1() : def.getCommand2()).toLowerCase();
				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "OpNpc", new Object[]{getPlayer(), affectedNpc, command}, this);
			}
		});
	}

}
