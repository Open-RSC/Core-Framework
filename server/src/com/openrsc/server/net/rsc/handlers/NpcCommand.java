package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.external.NPCDef;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public final class NpcCommand implements PacketHandler {

	public void handlePacket(Packet p, Player player) {
		int pID = p.getID();
		int serverIndex = p.readShort();
		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			return;
		}
		final int click = pID == OpcodeIn.NPC_COMMAND1.getOpcode() ? 0 : 1;
		player.click = click;
		final Mob affectedMob = player.getWorld().getNpc(serverIndex);
		final Npc affectedNpc = (Npc) affectedMob;
		if (affectedNpc == null || affectedMob == null || player == null)
			return;

		player.setFollowing(affectedNpc, 0);
		player.setWalkToAction(new WalkToMobAction(player, affectedMob, 1) {
			public void executeInternal() {
				getPlayer().resetFollowing();
				getPlayer().resetPath();
				if (getPlayer().isBusy() || getPlayer().isRanging()
					|| !getPlayer().canReach(affectedNpc)) {
					return;
				}
				getPlayer().resetAll();
				NPCDef def = affectedNpc.getDef();
				String command = (click == 0 ? def.getCommand1() : def
					.getCommand2()).toLowerCase();
				affectedNpc.resetPath();
				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "NpcCommand", new Object[]{affectedNpc, command, getPlayer()}, this);
			}
		});
		return;
	}

}
