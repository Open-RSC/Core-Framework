package com.openrsc.server.net.rsc.handlers;


import com.openrsc.server.event.rsc.impl.ProjectileEvent;
import com.openrsc.server.model.Path;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

public class WalkRequest implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		int packetOpcode = p.getID();
		if (player.inCombat()) {
			if (packetOpcode == OpcodeIn.WALK_TO_POINT.getOpcode()) {
				Mob opponent = player.getOpponent();
				if (opponent == null) {
					player.setSuspiciousPlayer(true);
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
					player.setRanAwayTimer();
					player.setLastRun(System.currentTimeMillis());
					player.setLastCombatState(CombatState.RUNNING);
					opponent.setLastCombatState(CombatState.WAITING);
					player.resetCombatEvent();

					if (opponent.isNpc()) {
						if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerNpcRun",
							new Object[]{player, ((Npc) opponent)})) {
							return;
						}
					}
				} else {
					player.message("You can't retreat during the first 3 rounds of combat");
					return;
				}
			} else {
				return;
			}
		} else if (player.isBusy()) {
			return;
		}
		player.checkAndInterruptBatchEvent();

		player.resetAll();
		player.resetPath();

		int firstStepX = p.readAnotherShort();
		int firstStepY = p.readAnotherShort();
		PathType pathType = packetOpcode == OpcodeIn.WALK_TO_POINT.getOpcode() ? PathType.WALK_TO_POINT : PathType.WALK_TO_ENTITY;
		Path path = new Path(player, pathType);
		{
			path.addStep(firstStepX, firstStepY);
			int numWaypoints = p.getReadableBytes() / 2;
			for (int stepCount = 0; stepCount < numWaypoints; stepCount++) {
				int stepDiffX = p.readByte();
				int stepDiffY = p.readByte();
				path.addStep(firstStepX + stepDiffX, firstStepY + stepDiffY);
			}
			path.finish();
		}
		player.getWalkingQueue().setPath(path);

		player.setStatus(Action.IDLE);
	}
}
