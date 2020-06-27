package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.action.WalkToPointAction;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class GroundItemTake implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();

		final short x = packet.readShort();
		final short y = packet.readShort();
		if (x < 0 || y < 0) return;

		final Point location = Point.location(x, y);

		final int id = packet.readShort();
		if (id < 0 || id >= player.getWorld().getServer().getEntityHandler().getItemCount()) {
			return;
		}

		final GroundItem item = player.getViewArea().getGroundItem(id, location);

		if (item == null) {
			player.resetPath();
			return;
		}

		int distance = item.getRegion().getGameObject(location, player) != null ? 1 : 0;
		Player onTile = item.getRegion().getPlayer(location.getX(), location.getY(), player);
		if (onTile != null && onTile.inCombat()) {
			distance = 1;
		}
		if (PathValidation.isMobBlocking(player, location.getX(), location.getY())) {
			distance = 1;
		}
		player.setWalkToAction(new WalkToPointAction(player, item.getLocation(), distance) {
			public void executeInternal() {
				if (getPlayer().isBusy() || getPlayer().isRanging() || item == null || item.isRemoved()
					|| getPlayer().getRegion().getItem(id, getLocation(), getPlayer()) == null || !getPlayer().canReach(item)
					|| item.getAmount() < 1) {
					return;
				}

				if (item.getDef().isMembersOnly() && !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().sendMemberErrorMessage();
					return;
				}
				if (item.getLocation().inWilderness() && !item.belongsTo(getPlayer()) && item.getAttribute("playerKill", false)
					&& (getPlayer().isIronMan(IronmanMode.Ironman.id()) || getPlayer().isIronMan(IronmanMode.Ultimate.id())
					|| getPlayer().isIronMan(IronmanMode.Hardcore.id()) || getPlayer().isIronMan(IronmanMode.Transfer.id()))) {
					getPlayer().message("You're an Iron Man, so you can't loot items from players.");
					return;
				}
				if (!item.belongsTo(getPlayer())
					&& (getPlayer().isIronMan(IronmanMode.Ironman.id()) || getPlayer().isIronMan(IronmanMode.Ultimate.id())
					|| getPlayer().isIronMan(IronmanMode.Hardcore.id()) || getPlayer().isIronMan(IronmanMode.Transfer.id()))) {
					getPlayer().message("You're an Iron Man, so you can't take items that other players have dropped.");
					return;
				}

				getPlayer().resetAll();

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "TakeObj", new Object[]{getPlayer(), item}, this);
			}
		});
	}
}
