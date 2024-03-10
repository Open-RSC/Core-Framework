package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

public final class NpcTalkTo implements PayloadProcessor<TargetMobStruct, OpcodeIn> {

	public void process(TargetMobStruct payload, Player player) throws Exception {

		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			player.resetPath();
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();
		final Npc npc = player.getWorld().getNpc(payload.serverIndex);

		if (npc == null) {
			return;
		}

		player.setFollowing(npc, 1, false, true);
		player.setWalkToAction(new WalkToMobAction(player, npc, 1) {
			public void executeInternal() {
				NpcInteraction interaction = NpcInteraction.NPC_TALK_TO;
				if (getPlayer().isBusy() || getPlayer().isRanging()) {
					return;
				}
				getPlayer().resetAll(true, false);
				Player otherPlayer = npc.getInteractingPlayer();
				if (npc.isBusy() || System.currentTimeMillis() - npc.getCombatTimer() < player.getConfig().GAME_TICK * 5L) {
					if (npc.isBusy()
						&& npc.getNpcInteraction() == NpcInteraction.NPC_TALK_TO
						&& otherPlayer != null && otherPlayer.getMenuHandler() != null
						&& npc.getMultiTimeout() != -1
						&& System.currentTimeMillis() - npc.getMultiTimeout() >= 20000L) {
						otherPlayer.setMultiEndedEarly(true);
						otherPlayer.resetMenuHandler();
						npc.setInteractingPlayer(null);
					}
					else {
						//Flag it so the talking player can kill their own dialogue when the time comes, even if this player doesn't come back.
						npc.setPlayerWantsNpc(true);
						getPlayer().message(npc.getDef().getName() + " is busy at the moment");
						return;
					}
				}

				NpcInteraction.setInteractions(npc, player, interaction);
				npc.setMultiTimeout(-1);
				npc.setPlayerWantsNpc(false);

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(TalkNpcTrigger.class, getPlayer(), new Object[]{getPlayer(), npc});
				if (!getPlayer().getWorld().getServer().getConfig().MEMBER_WORLD
					&& getPlayer().getWorld().getServer().getEntityHandler().getNpcDef(npc.getID()).isMembers()) {
					getPlayer().message("you must be on a members' world to do that");
				}
			}
		});
	}
}
