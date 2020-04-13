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
		if (player == null) return;
		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			return;
		}
		final boolean click = pID == OpcodeIn.NPC_COMMAND1.getOpcode();
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
				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "OpNpc", new Object[]{affectedNpc, command, getPlayer()}, this);
			}
		});
	}

}
