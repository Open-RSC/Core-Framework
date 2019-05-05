package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

public final class NpcTalkTo implements PacketHandler {

	public static final World world = World.getWorld();

	public void handlePacket(Packet p, Player player) throws Exception {

		if (player.isBusy()) {
			if (player.inCombat()) {
				player.message("You can't do that whilst you are fighting");
			}
			player.resetPath();
			return;
		}
		player.resetAll();
		final Npc n = world.getNpc(p.readShort());

		if (n == null) {
			return;
		}
		player.setFollowing(n);
		player.setStatus(Action.TALKING_MOB);
		player.setWalkToAction(new WalkToMobAction(player, n, 1) {
			public void execute() {
				player.resetFollowing();
				player.resetPath();
				if (player.isBusy() || player.isRanging() || !player.canReach(n)
					|| player.getStatus() != Action.TALKING_MOB) {
					return;
				}
				player.resetAll();

				if (n.isBusy()) {
					player.message(n.getDef().getName() + " is busy at the moment");
					return;
				}

				n.resetPath();

				if (player.getLocation().equals(n.getLocation())) {
					for (int x = -1; x <= 1; ++x) {
						for (int y = -1; y <= 1; ++y) {
							if (x == 0 || y == 0)
								continue;
							Point destination = canWalk(player.getX() - x, player.getY() - y);
							if (destination != null && destination.inBounds(n.getLoc().minX, n.getLoc().minY, n.getLoc().maxY, n.getLoc().maxY)) {
								n.teleport(destination.getX(), destination.getY());
								break;
							}
						}
					}
				}
				
				if (PluginHandler.getPluginHandler().blockDefaultAction("TalkToNpc", new Object[]{player, n})) {
					player.face(n);
					n.face(player);
					player.setInteractingNpc(n);
					return;
				}
			}

			private Point canWalk(int x, int y) {
				int myX = n.getX();
				int myY = n.getY();
				int newX = x;
				int newY = y;
				boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
				if (myX > x) {
					myXBlocked = checkBlocking(myX - 1, myY, 8); // Check right
					// tiles
					newX = myX - 1;
				} else if (myX < x) {
					myXBlocked = checkBlocking(myX + 1, myY, 2); // Check left
					// tiles
					newX = myX + 1;
				}
				if (myY > y) {
					myYBlocked = checkBlocking(myX, myY - 1, 4); // Check top tiles
					newY = myY - 1;
				} else if (myY < y) {
					myYBlocked = checkBlocking(myX, myY + 1, 1); // Check bottom
					// tiles
					newY = myY + 1;
				}

				if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
					return null;
				}

				if (newX > myX) {
					newXBlocked = checkBlocking(newX, newY, 2);
				} else if (newX < myX) {
					newXBlocked = checkBlocking(newX, newY, 8);
				}

				if (newY > myY) {
					newYBlocked = checkBlocking(newX, newY, 1);
				} else if (newY < myY) {
					newYBlocked = checkBlocking(newX, newY, 4);
				}
				if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
					return null;
				}
				if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
					return null;
				}
				return new Point(newX, newY);
			}

			private boolean checkBlocking(int x, int y, int bit) {
				TileValue t = World.getWorld().getTile(x, y);
				Point p = new Point(x, y);
				for (Npc n : n.getViewArea().getNpcsInView()) {
					if (n.getLocation().equals(p)) {
						return true;
					}
				}
				for (Player areaPlayer : n.getViewArea().getPlayersInView()) {
					if (areaPlayer.getLocation().equals(p)) {
						return true;
					}
				}
				return isBlocking(t.traversalMask, (byte) bit);
			}

			private boolean isBlocking(int objectValue, byte bit) {
				if ((objectValue & bit) != 0) { // There is a wall in the way
					return true;
				}
				if ((objectValue & 16) != 0) { // There is a diagonal wall here:
					// \
					return true;
				}
				if ((objectValue & 32) != 0) { // There is a diagonal wall here:
					// /
					return true;
				}
				if ((objectValue & 64) != 0) { // This tile is unwalkable
					return true;
				}
				return false;
			}
		});
	}
}
