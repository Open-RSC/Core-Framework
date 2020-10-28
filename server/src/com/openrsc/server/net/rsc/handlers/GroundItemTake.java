package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.model.PathValidation;
import com.openrsc.server.model.Point;
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

		final int itemId = packet.readShort();
		if (itemId < 0 || itemId >= player.getWorld().getServer().getEntityHandler().getItemCount()) {
			return;
		}

		final GroundItem item = player.getViewArea().getGroundItem(itemId, location);

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

		if (player.isBusy() || player.isRanging() || item == null || item.isRemoved()
			|| player.getRegion().getItem(itemId, location, player) == null || !player.canReach(item)
			|| item.getAmount() < 1) {
			return;
		}

		if (item.getDef().isMembersOnly() && !player.getConfig().MEMBER_WORLD) {
			player.sendMemberErrorMessage();
			return;
		}
		if (item.getLocation().inWilderness() && !item.belongsTo(player) && item.getAttribute("playerKill", false)
			&& (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
			|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id()))) {
			player.message("You're an Iron Man, so you can't loot items from players.");
			return;
		}
		if (!item.belongsTo(player)
			&& (player.isIronMan(IronmanMode.Ironman.id()) || player.isIronMan(IronmanMode.Ultimate.id())
			|| player.isIronMan(IronmanMode.Hardcore.id()) || player.isIronMan(IronmanMode.Transfer.id()))) {
			player.message("You're an Iron Man, so you can't take items that other players have dropped.");
			return;
		}

		player.resetAll();

		player.getWorld().getServer().getPluginHandler().handlePlugin(player, "TakeObj", new Object[]{player, item});
	}
}
