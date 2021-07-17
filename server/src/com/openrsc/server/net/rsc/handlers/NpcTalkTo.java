package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.model.world.region.TileValue;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.TargetMobStruct;

public final class NpcTalkTo implements PayloadProcessor<TargetMobStruct, OpcodeIn> {

	public void process(TargetMobStruct payload, Player player) throws Exception {

		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			player.resetPath();
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}

		player.resetAll();
		final Npc n = player.getWorld().getNpc(payload.serverIndex);

		if (n == null) {
			return;
		}

		player.setFollowing(n, 0, false);
		player.setWalkToAction(new WalkToMobAction(player, n, 1) {
			public void executeInternal() {
				getPlayer().resetFollowing();
				getPlayer().resetPath();
				if (getPlayer().isBusy() || getPlayer().isRanging() || !getPlayer().canReach(n)) {
					return;
				}
				getPlayer().resetAll();

				if (n.isBusy() || System.currentTimeMillis() - n.getCombatTimer() < player.getConfig().GAME_TICK * 5) {
					getPlayer().message(n.getDef().getName() + " is busy at the moment");
					return;
				}

				n.resetPath();
				n.resetRange();

				if (getPlayer().getLocation().equals(n.getLocation())) {
					for (int x = -1; x <= 1; ++x) {
						for (int y = -1; y <= 1; ++y) {
							if (x == 0 || y == 0)
								continue;
							Point destination = canWalk(getPlayer().getWorld(), getPlayer().getX() - x, getPlayer().getY() - y);
							if (destination != null && destination.inBounds(n.getLoc().minX, n.getLoc().minY, n.getLoc().maxY, n.getLoc().maxY)) {
								n.walk(destination.getX(), destination.getY());
								n.setTalkToNpcEvent(getPlayer());
								return;
							}
						}
					}
				}

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "TalkNpc", new Object[]{getPlayer(), n});
				if (!getPlayer().getWorld().getServer().getConfig().MEMBER_WORLD
					&& getPlayer().getWorld().getServer().getEntityHandler().getNpcDef(n.getID()).isMembers()) {
					getPlayer().message("you must be on a members' world to do that");
				}
			}

			private Point canWalk(World world, int x, int y) {
				int myX = n.getX();
				int myY = n.getY();
				int newX = x;
				int newY = y;
				boolean myXBlocked = false, myYBlocked = false, newXBlocked = false, newYBlocked = false;
				if (myX > x) {
					myXBlocked = checkBlocking(world,myX - 1, myY, 8); // Check right
					// tiles
					newX = myX - 1;
				} else if (myX < x) {
					myXBlocked = checkBlocking(world,myX + 1, myY, 2); // Check left
					// tiles
					newX = myX + 1;
				}
				if (myY > y) {
					myYBlocked = checkBlocking(world, myX, myY - 1, 4); // Check top tiles
					newY = myY - 1;
				} else if (myY < y) {
					myYBlocked = checkBlocking(world, myX, myY + 1, 1); // Check bottom
					// tiles
					newY = myY + 1;
				}

				if ((myXBlocked && myYBlocked) || (myXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
					return null;
				}

				if (newX > myX) {
					newXBlocked = checkBlocking(world, newX, newY, 2);
				} else if (newX < myX) {
					newXBlocked = checkBlocking(world, newX, newY, 8);
				}

				if (newY > myY) {
					newYBlocked = checkBlocking(world, newX, newY, 1);
				} else if (newY < myY) {
					newYBlocked = checkBlocking(world, newX, newY, 4);
				}
				if ((newXBlocked && newYBlocked) || (newXBlocked && myY == newY) || (myYBlocked && myX == newX)) {
					return null;
				}
				if ((myXBlocked && newXBlocked) || (myYBlocked && newYBlocked)) {
					return null;
				}
				return new Point(newX, newY);
			}

			private boolean checkBlocking(World world, int x, int y, int bit) {
				TileValue t = world.getTile(x, y);
				Point point = new Point(x, y);
				for (Npc n : n.getViewArea().getNpcsInView()) {
					if (n.getLocation().equals(point)) {
						return true;
					}
				}
				for (Player areaPlayer : n.getViewArea().getPlayersInView()) {
					if (areaPlayer.getLocation().equals(point)) {
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
