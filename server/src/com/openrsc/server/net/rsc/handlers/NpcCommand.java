package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;

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
		NPCDef def = affectedNpc.getDef();
		String command = (click ? def.getCommand1() : def.getCommand2()).toLowerCase();
		int followRadius = command.equalsIgnoreCase("pickpocket")
			&& player.withinRange(affectedNpc, 1)
			&& PathValidation.checkAdjacentDistance(player.getWorld(), player.getLocation(), affectedNpc.getLocation(), true)
			? 0 : 1;
		player.setFollowing(affectedNpc, followRadius, true, true);
		//Custom behaviour causes a lot of issues with ops happening from more than 1 tile away (authentically happens from 2).
		//Servers with these configs enabled will have them start from 0/1 tile away instead.
		//TODO: Actually fix this so batching/custom ops don't break using the authentic logic.
		final String[] authenticOps = new String[] {
			"pickpocket",
			"tackle",
			"pass to",
			"watch"
		};
		boolean isAuthenticOp = false;
		for(String op : authenticOps) {
			if (op.equalsIgnoreCase(command)) {
				isAuthenticOp = true;
				break;
			}
		}

		boolean isCustomOp = !isAuthenticOp || player.getConfig().BATCH_PROGRESSION;
		boolean isGnomeballOp = command.equalsIgnoreCase("tackle") || command.equalsIgnoreCase("pass to");
		int radius = isCustomOp ? 1 : 2;
		if (click && player.withinRange(affectedNpc, 1)
			&& affectedNpc.getDef().getCommand1().equalsIgnoreCase("pickpocket") && isCustomOp) {
			radius = 0;
		}

		player.setWalkToAction(new WalkToMobAction(player, affectedNpc, radius) {
			public void executeInternal() {
				NpcInteraction interaction = isGnomeballOp ? NpcInteraction.NPC_GNOMEBALL_OP : NpcInteraction.NPC_OP;
				if (getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().canReach(affectedNpc)) {
					return;
				}
				if (isCustomOp) {
					getPlayer().resetFollowing();
					getPlayer().resetPath();
					getPlayer().resetAll();
				} else {
					getPlayer().resetAll(true, false);
				}
				NpcInteraction.setInteractions(affectedNpc, getPlayer(), interaction);

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(OpNpcTrigger.class, getPlayer(), new Object[]{getPlayer(), affectedNpc, command}, this);
			}
		});
	}

}
