package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.states.Action;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemUseOnPlayer implements PacketHandler {

	public void handlePacket(Packet p, Player player) throws Exception {

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Player affectedPlayer = player.getWorld().getPlayer(p.readShort());
		final Item item = player.getInventory().get(p.readShort());
		if (affectedPlayer == null || item == null) {
			return;
		}
		if (System.currentTimeMillis() - affectedPlayer.getLastRun() < 2000) {
			player.resetPath();
			return;
		}

		int radius = 1;
		if (item.getID() == ItemId.GNOME_BALL.id())
			radius = 10;

		player.setFollowing(affectedPlayer);
		player.setStatus(Action.USING_Item_ON_PLAYER);
		player.setWalkToAction(new WalkToMobAction(player, affectedPlayer, radius) {
			public void executeInternal() {
				getPlayer().resetPath();
				getPlayer().resetFollowing();
				if (!getPlayer().getInventory().contains(item)
					|| !getPlayer().canReach(affectedPlayer) || getPlayer().isBusy()
					|| getPlayer().isRanging()
					|| getPlayer().getStatus() != Action.USING_Item_ON_PLAYER) {
					return;
				}
				getPlayer().resetAll();
				getPlayer().face(affectedPlayer);
				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getWorld().getServer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "InvUseOnPlayer", new Object[]{getPlayer(), affectedPlayer, item}, this);
			}
		});
	}
}
