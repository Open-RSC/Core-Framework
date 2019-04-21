package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToPointAction;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;
import com.openrsc.server.plugins.PluginHandler;

public class GroundItemTake implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();

	private GroundItem getItem(int id, Point location, Player player) {
		int x = location.getX();
		int y = location.getY();
		for (GroundItem i : player.getViewArea().getItemsInView()) {
			if (i.getID() == id && i.visibleTo(player) && i.getX() == x && i.getY() == y) {
				return i;
			}
		}
		return null;
	}

	public void handlePacket(Packet p, Player player) throws Exception {
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Point location = Point.location(p.readShort(), p.readShort());
		final int id = p.readShort();
		final GroundItem item = getItem(id, location, player);

		if (item == null) {
			player.resetPath();
			return;
		}
		player.setStatus(Action.TAKING_GITEM);

		int distance = player.getViewArea().getGameObject(location) != null ? 1 : 0;
		Player onTile = player.getRegion().getPlayer(location.getX(), location.getY());
		if (onTile != null && onTile.inCombat()) {
			distance = 1;
		}
		player.setWalkToAction(new WalkToPointAction(player, item.getLocation(), distance) {
			public void execute() {
				if (player.isBusy() || player.isRanging() || item == null || item.isRemoved()
					|| getItem(id, location, player) == null || !player.canReach(item)
					|| player.getStatus() != Action.TAKING_GITEM || item.getAmount() < 1) {
					return;
				}

				if (item.getDef().isMembersOnly() && !Constants.GameServer.MEMBER_WORLD) {
					player.sendMemberErrorMessage();
					return;
				}
				if (item.getLocation().inWilderness() && item.belongsTo(player) && item.getAttribute("playerKill", false) && (player.isIronMan(2) || player.isIronMan(1) || player.isIronMan(3))) {
					player.message("You're an Iron Man, so you can't loot items from players.");
					return;
				}
				if (!item.belongsTo(player) && (player.isIronMan(1) || player.isIronMan(2) || player.isIronMan(3))) {
					player.message("You're an Iron Man, so you can't take items that other players have dropped.");
					return;
				}

				player.resetAll();

				if (PluginHandler.getPluginHandler().blockDefaultAction("Pickup", new Object[]{player, item})) {
					return;
				}

				player.groundItemTake(item);
			}
		});
	}
}
