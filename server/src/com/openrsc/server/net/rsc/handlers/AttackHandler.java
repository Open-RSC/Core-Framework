package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.rsc.impl.RangeEvent;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;

public class AttackHandler implements PacketHandler {
	public void handlePacket(Packet p, Player player) throws Exception {
		int pID = p.getID();
		if (player.isBusy()) {
			if (player.inCombat())
				player.message("You are already busy fighting");


			player.resetPath();

			return;
		}

		player.resetAll();
		Mob affectedMob = null;
		int serverIndex = p.readShort();
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
			if (pl.getLocation().inWilderness() && System.currentTimeMillis() - pl.getLastRun() < 3000) {
				//player.resetPath();
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
			if (n.isPkBot() && !player.getLocation().inWilderness()) {
				player.message("You must be in the wilderness to attack this mob");
				player.resetPath();
				return;
			}
			if (n.isPkBot() && !n.getLocation().inWilderness()) {
				player.message("I can't get close enough");
				player.resetPath();
				return;
			}
			if (n.isPkBot() && System.currentTimeMillis() - n.getCombatTimer() < 3000 && System.currentTimeMillis() - n.getCombatTimer() != 0){
				player.resetPath();
				return;
			}
		}

		player.setStatus(Action.ATTACKING_MOB);
		if (player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
			if (affectedMob.isNpc())
				player.setFollowing(affectedMob, 0);
			player.setWalkToAction(new WalkToMobAction(player, affectedMob, affectedMob.isNpc() ? 1 : 2) {
				public void execute() {
					player.resetPath();
					player.resetFollowing();

					if (mob.inCombat() && player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
						player.message("I can't get close enough");
						return;
					}
					if (player.isBusy() || mob.isBusy() || !player.canReach(mob)
						|| !player.checkAttack(mob, false) || player.getStatus() != Action.ATTACKING_MOB) {
						return;
					}
					if (mob.isNpc()) {
						if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(player, "PlayerAttackNpc",
							new Object[]{player, (Npc) mob})) {
							return;
						}
					}
					if (mob.isPlayer()) {
						if (player.getWorld().getServer().getPluginHandler().blockDefaultAction(player, "PlayerAttack",
							new Object[]{player, mob})) {
							return;
						}
					}
					player.startCombat(mob);
					if (player.getWorld().getServer().getConfig().WANT_PARTIES) {
						if (player.getParty() != null) {
							player.getParty().sendParty();
						}
					}
				}
			});
		} else {
			if (player.isBusy() || !player.checkAttack(affectedMob, true)
				|| player.getStatus() != Action.ATTACKING_MOB) {
				return;
			}
			final Mob target = affectedMob;
			player.resetPath();
			player.resetAll();
			/* To skip the walk packet resetAll() */
			player.getWorld().getServer().getGameEventHandler().add(new MiniEvent(player.getWorld(), player, "Handle Attack") {
				@Override
				public void action() {
					getOwner().setStatus(Action.RANGING_MOB);
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
					if (target.isNpc() && ((Npc) target).isPkBot()) {
						assert target instanceof Npc;
						Npc affectedNpc = (Npc) target;
						getOwner().setSkulledOn(affectedNpc);
					}
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
