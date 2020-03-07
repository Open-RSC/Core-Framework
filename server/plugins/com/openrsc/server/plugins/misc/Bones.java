package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.InvActionListener;

public class Bones implements InvActionListener {

	private void buryBonesHelper(Player owner, Item item) {
		owner.message("You bury the "
			+ item.getDef(owner.getWorld()).getName().toLowerCase());

		switch (ItemId.getById(item.getCatalogId())) {
			case BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 15, true); // 3.75
				break;
			case BAT_BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 18, true); // 4.5
				break;
			case BIG_BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 50, true); // 12.5
				break;
			case DRAGON_BONES:
				owner.incExp(com.openrsc.server.constants.Skills.PRAYER, 240, true); // 60
				break;
			default:
				owner.message("Nothing interesting happens");
				break;
		}
	}

	@Override
	public void onInvAction(Item item, Player player, String command) {
		if(command.equalsIgnoreCase("bury")) {
			if (item.getCatalogId() == 1308 || item.getCatalogId() == 1648 || item.getCatalogId() == 1793 || item.getCatalogId() == 1871 || item.getCatalogId() == 2257) {
				player.message("You can't bury noted bones");
				return;
			}

			if (item.getAmount() > 1) { // bury all
				player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, String.format("Bury %s", item.getDef(player.getWorld()).getName()), item.getAmount(), false) {
					@Override
					public void action() {
						if (getOwner().getCarriedItems().remove(item.getCatalogId(), 1) > -1) {
							player.message("You dig a hole in the ground");
							buryBonesHelper(player, item);
						} else
							interrupt();
					}
				});
			} else {
				player.getWorld().getServer().getGameEventHandler()
					.add(new MiniEvent(player.getWorld(), player, "Bury Bones") {
						public void action() {
							if (getOwner().getCarriedItems().remove(item.getCatalogId(), 1) > -1) {
								player.setBusyTimer(player.getWorld().getServer().getConfig().GAME_TICK);
								player.message("You dig a hole in the ground");
								buryBonesHelper(player, item);
							}
						}
					});
			}
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player, String command) {
		return command.equalsIgnoreCase("bury");
	}
}
