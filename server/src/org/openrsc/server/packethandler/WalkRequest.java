package org.openrsc.server.packethandler;

import org.openrsc.server.packethandler.PacketHandler;
import org.openrsc.server.model.Mob;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Path;
import org.openrsc.server.states.Action;
import org.openrsc.server.states.CombatState;
import org.openrsc.server.net.Packet;
import org.openrsc.server.net.RSCPacket;

import org.apache.mina.common.IoSession;
public class WalkRequest implements PacketHandler {
	public void handlePacket(Packet p, IoSession session) throws Exception {
		final Player player = (Player)session.getAttachment();
		if (player != null) {
			int pID = ((RSCPacket)p).getID();
			if (player.inCombat()) {
				if (pID == 6) {
					final Mob opponent = player.getOpponent();
					if (opponent == null)
						return;
					if (opponent.getHitsMade() >= 3) {
						if (player.isDueling() && player.getDuelSetting(0)) {
							player.sendMessage("You cannot retreat from this duel!");
							return;
						}
						player.setRunTimer();
						player.resetCombat(CombatState.RUNNING);
						for (Player follower : player.getViewArea().getPlayersInView()) {
							if (follower.isFollowing(player))
								follower.resetFollowing();
						}
						opponent.setRunTimer();
						opponent.resetCombat(CombatState.WAITING);
						if (opponent instanceof Npc) {
							if (((Npc)(opponent)).getDef().follows())
								((Npc)(opponent)).setAggressive(player);
						}
					} else {
						player.sendMessage("You can't retreat during the first 3 rounds of combat");
						return;
					}
				} else
					return;
			} else if (player.isBusy()) {
				player.setCancelBatch(true);
				return;
			}
			player.resetAllExceptDMing();
			int startX = p.readShort();
			int startY = p.readShort();
			int numWaypoints = p.remaining() / 2;
			byte[] waypointXoffsets = new byte[numWaypoints];
			byte[] waypointYoffsets = new byte[numWaypoints];
			for (int x = 0; x < numWaypoints; x++) {
				waypointXoffsets[x] = p.readByte();
				waypointYoffsets[x] = p.readByte();
			}
			if (player.teleport && waypointXoffsets.length >= 1) {
				player.teleport((int)waypointXoffsets[waypointXoffsets.length-1] + startX, (int)waypointYoffsets[waypointYoffsets.length-1] + startY, false);
				return;
			}
			Path path = new Path(startX, startY, waypointXoffsets, waypointYoffsets);
			player.setStatus(Action.IDLE);
			player.setPath(path);
		}
	}
}