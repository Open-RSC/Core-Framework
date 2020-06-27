package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.Packet;
import com.openrsc.server.net.rsc.PacketHandler;

public class ItemUseOnPlayer implements PacketHandler {

	public void handlePacket(Packet packet, Player player) throws Exception {
		if (player.inCombat()) {
			return;
		}
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Player affectedPlayer = player.getWorld().getPlayer(packet.readShort());
		final Item item = player.getCarriedItems().getInventory().get(packet.readShort());
		if (affectedPlayer == null || item == null || item.getItemStatus().getNoted()) {
			return;
		}
		if (System.currentTimeMillis() - affectedPlayer.getCombatTimer() < player.getConfig().GAME_TICK * 5) {
			player.resetPath();
			return;
		}

		int radius = 1;
		if (item.getCatalogId() == ItemId.GNOME_BALL.id())
			radius = 10;

		player.setFollowing(affectedPlayer);
		player.setWalkToAction(new WalkToMobAction(player, affectedPlayer, radius) {
			public void executeInternal() {
				getPlayer().resetPath();
				getPlayer().resetFollowing();
				if (!getPlayer().getCarriedItems().getInventory().contains(item)
					|| !getPlayer().canReach(affectedPlayer) || getPlayer().isBusy()
					|| getPlayer().isRanging()) {
					return;
				}
				getPlayer().resetAll();
				getPlayer().face(affectedPlayer);
				if (item.getDef(getPlayer().getWorld()).isMembersOnly()
					&& !getPlayer().getConfig().MEMBER_WORLD) {
					getPlayer().message(getPlayer().MEMBER_MESSAGE);
					return;
				}

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(getPlayer(), "UsePlayer", new Object[]{getPlayer(), affectedPlayer, item}, this);
			}
		});
	}
}
