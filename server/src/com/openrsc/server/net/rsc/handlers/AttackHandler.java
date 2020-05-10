package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.rsc.impl.RangeEvent;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class AttackHandler implements PacketHandler {
	public void handlePacket(Packet packet, Player player) throws Exception {
		int pID = packet.getID();

		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		if (player.inCombat()) {
			player.message("You are already busy fighting");
			player.resetPath();
			return;
		}


		player.resetAll();
		Mob affectedMob = null;
		int serverIndex = packet.readShort();
		int packetOne = OpcodeIn.PLAYER_ATTACK.getOpcode();
		int packetTwo = OpcodeIn.NPC_ATTACK1.getOpcode();

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
			if (affectedMob.getLocation().inBounds(220, 108, 225, 111)) { // mage arena block real rsc.
				player.message("Here kolodion protects all from your attack");
				player.resetPath();
				return;
			}
			assert affectedMob instanceof Player;
			Player pl = (Player) affectedMob;
			if (pl.getLocation().inWilderness()
				&& System.currentTimeMillis() - pl.getCombatTimer() < player.getWorld().getServer().getConfig().GAME_TICK * 5) {
				player.resetPath();
				return;
			}
		}
		if (affectedMob.isNpc()) {
			Npc n = (Npc) affectedMob;
			if (n.getX() == 0 && n.getY() == 0)
				return;
			if (n.getID() == NpcId.OGRE_TRAINING_CAMP.id() && player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
				player.message("these ogres are for range combat training only");
				return;
			}
		}

		if (player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
			player.setFollowing(affectedMob, 0);
			player.setWalkToAction(new WalkToMobAction(player, affectedMob, 1) {
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
			if (player.isBusy() || !player.checkAttack(affectedMob, true)) {
				return;
			}
			final Mob target = affectedMob;
			player.resetPath();
			player.resetAll();
			/* To skip the walk packet resetAll() */
			player.getWorld().getServer().getGameEventHandler().add(new MiniEvent(player.getWorld(), player, "Handle Attack") {
				@Override
				public void action() {
					if (target.isPlayer()) {
						assert target instanceof Player;
						Player affectedPlayer = (Player) target;
						getOwner().setSkulledOn(affectedPlayer);
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
					getOwner().face(getOwner().getX() + 1, getOwner().getY() - 1);

					if (player.getRangeEquip() > 0) {
						getOwner().setRangeEvent(new RangeEvent(getOwner().getWorld(), getOwner(), target));
					} else {
						getOwner().setThrowingEvent(new ThrowingEvent(getOwner().getWorld(), getOwner(), target));
					}
				}
			});
		}
	}
}
