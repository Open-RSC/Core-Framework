package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.impl.RangeEvent;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;

import static com.openrsc.server.plugins.Functions.inArray;

public class AttackHandler implements PayloadProcessor<TargetMobStruct, OpcodeIn> {
	public void process(TargetMobStruct payload, Player player) throws Exception {
		OpcodeIn pID = payload.getOpcode();

		if (player.inCombat()) {
			player.message("You are already busy fighting");
			player.resetPath();
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
			if (System.currentTimeMillis() - pl.getCombatTimer() < player.getConfig().GAME_TICK * 5) {
				if (pl.getLocation().inWilderness()) {
					player.resetPath();
				}
				return;
			}
		}
		if (affectedMob.isNpc()) {
			Npc n = (Npc) affectedMob;
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
			}
		}

		if (player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
			player.setFollowing(affectedMob, 0, false);
			player.setWalkToAction(new WalkToMobAction(player, affectedMob, 1, false) {
				public void executeInternal() {
					getPlayer().resetPath();
					getPlayer().resetFollowing();

					if (mob.inCombat() && getPlayer().getRangeEquip() < 0 && getPlayer().getThrowingEquip() < 0) {
						getPlayer().message("I can't get close enough");
						return;
					}
					if (getPlayer().isBusy() || mob.isBusy() || !getPlayer().canReach(mob)
						|| !getPlayer().checkAttack(mob, false)) {
						return;
					}
					if (mob.isNpc()) {
						if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "AttackNpc", new Object[]{getPlayer(), (Npc) mob}, this)) {
							return;
						}
					}
					if (mob.isPlayer()) {
						if (getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "AttackPlayer", new Object[]{getPlayer(), mob}, this)) {
							return;
						}
					}
				}
			});
		} else {
			if (!player.checkAttack(affectedMob, true)) {
				return;
			}
			final Mob target = affectedMob;
			player.resetPath();
			player.resetAll();
			int radius = player.getProjectileRadius(5); // default radius of 5
			player.setFollowing(affectedMob, 0, false);
			player.setWalkToAction(new WalkToMobAction(player, affectedMob, radius, false) {
				public void executeInternal() {
					if(getPlayer().isBusy() || getPlayer().inCombat()) return;
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
						getPlayer().setRangeEvent(new RangeEvent(getPlayer().getWorld(), getPlayer(), target));
					} else {
						getPlayer().setThrowingEvent(new ThrowingEvent(getPlayer().getWorld(), getPlayer(), target));
					}
				}
			});
		}
	}
}
