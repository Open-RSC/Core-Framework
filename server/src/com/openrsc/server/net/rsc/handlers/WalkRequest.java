package com.openrsc.server.net.rsc.handlers;


import com.openrsc.server.event.rsc.impl.projectile.ProjectileEvent;
import com.openrsc.server.model.Path;
import com.openrsc.server.model.Path.PathType;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.CombatState;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.WalkStruct;
import com.openrsc.server.plugins.triggers.EscapeNpcTrigger;

public class WalkRequest implements PayloadProcessor<WalkStruct, OpcodeIn> {

	@Override
	public void process(final WalkStruct payload, final Player player) throws Exception {

		OpcodeIn packetOpcode = payload.getOpcode();
		if (player.isBusy() && player.getMenuHandler() == null) {
			if (player.getConfig().BATCH_PROGRESSION) {
				player.interruptPlugins();
			}
			return;
		}
		
		if (player.inCombat()) {
			if (packetOpcode == OpcodeIn.WALK_TO_POINT) {
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
						victimPlayer.setRanAwayTimer(); //This player also needs to be immune from player attacks for a while.
					}
					player.setLastCombatState(CombatState.RUNNING);
					opponent.setLastCombatState(CombatState.WAITING);
					player.resetCombatEvent();
					player.setRanAwayTimer();
					ActionSender.sendSound(player, "retreat");

					if (player.getConfig().WANT_PARTIES) {
						if(player.getParty() != null){
							player.getParty().sendParty();
						}
					}
					if (opponent.isPlayer() && opponent.getConfig().WANT_PARTIES) {
						if(((Player) opponent).getParty() != null){
							((Player) opponent).getParty().sendParty();
						}
					}

					if (opponent.isNpc()) {
						player.getWorld().getServer().getPluginHandler().handlePlugin(EscapeNpcTrigger.class, player, new Object[]{player, ((Npc) opponent)});
					}
				} else {
					player.message("You can't retreat during the first 3 rounds of combat");
					return;
				}
			} else {
				return;
			}
		}
		player.resetAll();
		player.resetPath();

		int firstStepX = payload.firstStep.getX();
		int firstStepY = payload.firstStep.getY();
		PathType pathType = packetOpcode == OpcodeIn.WALK_TO_POINT ? PathType.WALK_TO_POINT : PathType.WALK_TO_ENTITY;
		Path path = new Path(player, pathType);
		{
			path.addStep(firstStepX, firstStepY);
			for (Point step : payload.steps) {
				path.addStep(firstStepX + step.getX(), firstStepY + step.getY());
			}
			path.finish();
		}
		player.getWalkingQueue().setPath(path);
	}
}
