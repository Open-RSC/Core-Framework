package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.event.rsc.handler.GameEventHandler;
import com.openrsc.server.event.rsc.impl.projectile.RangeEvent;
import com.openrsc.server.event.rsc.impl.projectile.ThrowingEvent;
import com.openrsc.server.model.action.ActionType;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.npc.NpcInteraction;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;
import com.openrsc.server.plugins.triggers.AttackNpcTrigger;
import com.openrsc.server.plugins.triggers.AttackPlayerTrigger;

import static com.openrsc.server.plugins.Functions.inArray;

public class AttackHandler implements PayloadProcessor<TargetMobStruct, OpcodeIn> {
	public void process(TargetMobStruct payload, Player player) throws Exception {
		OpcodeIn pID = payload.getOpcode();


		if (player.inCombat()) {
			player.message("You are already busy fighting!");
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
		Mob affectedMob = null;
		int serverIndex = payload.serverIndex;
		OpcodeIn packetOne = OpcodeIn.PLAYER_ATTACK;
		OpcodeIn packetTwo = OpcodeIn.NPC_ATTACK;

		if (pID == packetOne) {
			affectedMob = player.getWorld().getPlayer(serverIndex);
		} else if (pID == packetTwo) {
			affectedMob = player.getWorld().getNpc(serverIndex);
		}
		if (affectedMob == null || affectedMob.equals(player)) {
			player.resetPath();
			return;
		}

		if (affectedMob.isPlayer()) {
			assert affectedMob instanceof Player;
			Player pl = (Player) affectedMob;
			//Immune players cannot be attacked until their immunity wears off.
			if (!pl.canBeReattacked()) {
				if (pl.getLocation().inWilderness() || player.getConfig().USES_PK_MODE) {
					player.resetPath();
				}
				return;
			}
		} else {
			assert affectedMob instanceof Npc;
			Npc n = (Npc) affectedMob;
			long curTick = player.getWorld().getServer().getCurrentTick();
			long runTick = n.getRanAwayTimer();
			if (n.isRespawning()) return;
			if (n.getX() == 0 && n.getY() == 0)
				return;
			if (n.getID() == NpcId.OGRE_TRAINING_CAMP.id()) {
				boolean melee = player.getRangeEquip() < 0 && player.getThrowingEquip() < 0;
				boolean inPen = player.getX() >= 663 && player.getX() <= 668
					&& player.getY() >= 531 && player.getY() <= 535;
				if (melee || inPen) {
					player.message("these ogres are for range combat training only");
					return;
				}
			} else if (inArray(n.getID(), NpcId.BATTLE_MAGE_GUTHIX.id(), NpcId.BATTLE_MAGE_ZAMORAK.id(), NpcId.BATTLE_MAGE_SARADOMIN.id())
				&& (!player.getCache().hasKey("mage_arena") || player.getCache().getInt("mage_arena") < 2)) {
				player.message("you are not yet ready to fight the battle mages");
				return;
			} else if (curTick <= runTick || (curTick <= runTick + 1 && !n.finishedPath())) {
				//Moving retreating enemies are immune from attack requests for an extra tick.
				player.resetPath();
				return;
			}
		}

		if (player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
			player.setFollowing(affectedMob, 0, false, true);

			int radius = affectedMob.isPlayer() ? player.getConfig().PVP_CATCHING_DISTANCE : player.getConfig().PVM_CATCHING_DISTANCE;
			player.setWalkToAction(new WalkToMobAction(player, affectedMob, radius, true, ActionType.ATTACK) {
				public void executeInternal() {
					getPlayer().resetFollowing();

					if (mob.inCombat() && getPlayer().getRangeEquip() < 0 && getPlayer().getThrowingEquip() < 0) {
						getPlayer().message("I can't get close enough");
						return;
					}
					if (getPlayer().isBusy() || mob.isBusy() || !getPlayer().checkAttack(mob, false)) {
						return;
					}
					if (mob.isNpc()) {
						NpcInteraction interaction = NpcInteraction.NPC_ATTACK;
						NpcInteraction.setInteractions(((Npc)mob), getPlayer(), interaction);
						getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(AttackNpcTrigger.class, getPlayer(), new Object[]{getPlayer(), (Npc) mob}, this);
					} else {
						getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(AttackPlayerTrigger.class, getPlayer(), new Object[]{getPlayer(), mob}, this);
					}
				}
			});
		} else {
			if (!player.checkAttack(affectedMob, true)) {
				return;
			}
			final Mob target = affectedMob;
			player.resetPath();
			int radius = player.getProjectileRadius();
			player.setFollowing(affectedMob, radius, false);
			player.setWalkToAction(new WalkToMobAction(player, affectedMob, radius, false, ActionType.ATTACK) {
				public void executeInternal() {
					if (getPlayer().isBusy() || getPlayer().inCombat()) return;
					getPlayer().resetFollowing();
					if (getMob().isPlayer()) {
						Player affectedPlayer = (Player) getMob();
						getPlayer().setSkulledOn(affectedPlayer);
						affectedPlayer.getTrade().resetAll();
						if (affectedPlayer.getMenuHandler() != null) {
							affectedPlayer.resetMenuHandler();
						}
						if (affectedPlayer.accessingBank()) {
							affectedPlayer.resetBank();
						}
						if (affectedPlayer.accessingShop()) {
							affectedPlayer.resetShop();
						}
					}

					// Authentic player always faced NW
					getPlayer().face(getPlayer().getX() + 1, getPlayer().getY() - 1);

					if (getPlayer().getRangeEquip() > 0) {
						final GameEventHandler gameEventHandler = getPlayer().getWorld()
							.getServer()
							.getGameEventHandler();

						RangeEvent rangeEvent = null;

						for (final GameTickEvent gameTickEvent : gameEventHandler.getPlayerEvents(getPlayer())) {
							if (gameTickEvent instanceof RangeEvent) {
								rangeEvent = (RangeEvent) gameTickEvent;
								break;
							}
						}

						if (rangeEvent != null) {
							if (!rangeEvent.getTarget().equals(getMob())) {
								rangeEvent.reTarget(getMob());
							}

							rangeEvent.restart();
							getPlayer().setRangeEvent(rangeEvent);
							return;
						}

						rangeEvent = new RangeEvent(getPlayer().getWorld(), getPlayer(), 1, target);
						getPlayer().setRangeEvent(rangeEvent);
						gameEventHandler.add(rangeEvent);
					} else {
						final GameEventHandler gameEventHandler = getPlayer().getWorld()
							.getServer()
							.getGameEventHandler();

						ThrowingEvent throwingEvent = null;

						for (final GameTickEvent gameTickEvent : gameEventHandler.getPlayerEvents(getPlayer())) {
							if (gameTickEvent instanceof ThrowingEvent) {
								throwingEvent = (ThrowingEvent) gameTickEvent;
								break;
							}
						}

						if (throwingEvent != null) {
							if (!throwingEvent.getTarget().equals(getMob())) {
								throwingEvent.reTarget(getMob());
							}

							throwingEvent.restart();
							getPlayer().setThrowingEvent(throwingEvent);
							return;
						}

						throwingEvent = new ThrowingEvent(getPlayer().getWorld(), getPlayer(), 1, target);
						getPlayer().setThrowingEvent(throwingEvent);
						gameEventHandler.add(throwingEvent);
					}
				}
			});
		}
	}
}
