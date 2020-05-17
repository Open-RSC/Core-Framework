package com.openrsc.server.net.rsc.handlers;


import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.model.Path;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class WalkRequest implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {

		int packetOpcode = packet.getID();
		if (player.inCombat()) {
			if (packetOpcode == OpcodeIn.WALK_TO_POINT.getOpcode()) {
				Mob opponent = player.getOpponent();
				if (opponent == null) {
					player.setSuspiciousPlayer(true, "walk request null opponent");
					return;
				}
				if (opponent.getHitsMade() >= 3) {
					if (player.getDuel().isDuelActive() && player.getDuel().getDuelSetting(0)) {
						player.message("You cannot retreat from this duel!");
						return;
					}
					if (player.getDuel().isDuelActive()) {
						if (player.getAttribute("projectile", null) != null) {
							ProjectileEvent projectileEvent = player.getAttribute("projectile");
							projectileEvent.setCanceled(true);
						}
					}

					opponent.setLastOpponent(opponent.getOpponent());
					player.setLastOpponent(player.getOpponent());
					player.setCombatTimer();
					if (player.getOpponent().isPlayer()) {
						Player victimPlayer = ((Player) player.getOpponent());
						victimPlayer.message("Your opponent is retreating!");
						ActionSender.sendSound(victimPlayer, "retreat");
					}
					player.setLastCombatState(CombatState.RUNNING);
					opponent.setLastCombatState(CombatState.WAITING);
					player.resetCombatEvent();
					if (player.getConfig().WANT_PARTIES) {
						if(player.getParty() != null){
							player.getParty().sendParty();
						}
					}
					if (opponent.isPlayer() && opponent.getWorld().getServer().getConfig().WANT_PARTIES) {
						if(((Player) opponent).getParty() != null){
							((Player) opponent).getParty().sendParty();
						}
					}

					if (opponent.isNpc()) {
						player.getWorld().getServer().getPluginHandler().handlePlugin(player, "PlayerNpcRun", new Object[]{player, ((Npc) opponent)});
					}
				} else {
					player.message("You can't retreat during the first 3 rounds of combat");
					return;
				}
			} else {
				return;
			}
		} else if (player.isBusy()) {
			if (player.getConfig().BATCH_PROGRESSION) {
				player.interruptPlugins();
			}
			return;
		}

		player.resetAll();
		player.resetPath();

		int firstStepX = packet.readAnotherShort();
		int firstStepY = packet.readAnotherShort();
		PathType pathType = packetOpcode == OpcodeIn.WALK_TO_POINT.getOpcode() ? PathType.WALK_TO_POINT : PathType.WALK_TO_ENTITY;
		Path path = new Path(player, pathType);
		{
			path.addStep(firstStepX, firstStepY);
			int numWaypoints = packet.getReadableBytes() / 2;
			for (int stepCount = 0; stepCount < numWaypoints; stepCount++) {
				int stepDiffX = packet.readByte();
				int stepDiffY = packet.readByte();
				path.addStep(firstStepX + stepDiffX, firstStepY + stepDiffY);
			}
			path.finish();
		}
		player.getWalkingQueue().setPath(path);
	}
}
