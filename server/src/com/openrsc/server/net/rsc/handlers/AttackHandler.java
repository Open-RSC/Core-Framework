package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Server;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.event.rsc.impl.RangeEvent;
import com.openrsc.server.event.rsc.impl.ThrowingEvent;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.OpcodeIn;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

import static com.openrsc.server.plugins.Functions.transform;

public class AttackHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

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
			affectedMob = world.getPlayer(serverIndex);
		} else if (pID == packetTwo) {
			affectedMob = world.getNpc(serverIndex);
		}
		if (affectedMob == null || affectedMob.equals(player)) {
			player.resetPath();
			return;
		}
		if (affectedMob.getID() == 236 && affectedMob.isNpc() && !player.checkAttack(affectedMob, true)) {
			return;
		}

		/*if (System.currentTimeMillis() - player.getLastRun() < 600) {
			return;
		}*/
		if (affectedMob.isPlayer()) {
			if (affectedMob.getLocation().inBounds(220, 108, 225, 111)) { // mage arena block real rsc.
				player.message("Here kolodion protects all from your attack");
				player.resetPath();
				return;
			}
			Player pl = (Player) affectedMob;
			if (pl.getLocation().inWilderness() && System.currentTimeMillis() - pl.getLastRun() < 3000) {
				//player.resetPath();
				return;
			}
		}
		if (affectedMob.isNpc()) {
			Npc n = (Npc) affectedMob;
			if (n.getLocation().inWilderness() && !player.getLocation().inWilderness() && (n.getID() == 210 || n.getID() == 236)){
				player.message("You must be in the wilderness to attack this NPC");
				player.resetPath();
				return;
			}
			if(n.getPetNpc() > 0) {
			player.message("This npc belongs too " + n.getPetOwnerA2() + ". You may not attack it.");
			player.resetPath();
			return;
			}
			if (n.getX() == 0 && n.getY() == 0)
				return;
			if (n.getID() == 525 && player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
				player.message("these ogres are for range combat training only");
				return;
			}
		}

		player.setStatus(Action.ATTACKING_MOB);
		if (player.getRangeEquip() < 0 && player.getThrowingEquip() < 0) {
			if (affectedMob.isNpc())
				player.setFollowing(affectedMob);
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
						if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerAttackNpc",
							new Object[]{player, (Npc) mob})) {
							return;
						}
					}
					if (mob.isPlayer()) {
						if (PluginHandler.getPluginHandler().blockDefaultAction("PlayerAttack",
							new Object[]{player, mob})) {
							return;
						}
					}
					player.startCombat(mob);
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
			Server.getServer().getEventHandler().add(new MiniEvent(player) {
				@Override
				public void action() {
					owner.setStatus(Action.RANGING_MOB);
					if (target.isPlayer()) {
						Player affectedPlayer = (Player) target;
						owner.setSkulledOn(affectedPlayer);
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
					if (player.getRangeEquip() > 0) {
						owner.setRangeEvent(new RangeEvent(owner, target));
					} else {
						owner.setThrowingEvent(new ThrowingEvent(owner, target));
					}
				}
			});
		}
	}

}
