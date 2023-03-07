package com.openrsc.server.net.rsc.handlers;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.action.WalkToMobAction;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.PayloadProcessor;
import com.openrsc.server.net.rsc.enums.OpcodeIn;
import com.openrsc.server.net.rsc.struct.incoming.ItemOnMobStruct;
import com.openrsc.server.plugins.triggers.UsePlayerTrigger;

public class ItemUseOnPlayer implements PayloadProcessor<ItemOnMobStruct, OpcodeIn> {

	public void process(ItemOnMobStruct payload, Player player) throws Exception {
		if (player.inCombat()) {
			player.message("You can't do that whilst you are fighting");
			return;
		}

		if (player.getDuel().isDueling()) {
			return;
		}

		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final Player affectedPlayer = player.getWorld().getPlayer(payload.serverIndex);
		final Item item = player.getCarriedItems().getInventory().get(payload.slotIndex);
		if (affectedPlayer == null || item == null || item.getItemStatus().getNoted()) {
			player.message("Nothing interesting happens");
			return;
		}
		if (!affectedPlayer.canBeReattacked()) {
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

				getPlayer().getWorld().getServer().getPluginHandler().handlePlugin(UsePlayerTrigger.class, getPlayer(), new Object[]{getPlayer(), affectedPlayer, item}, this);
			}
		});
	}
}
