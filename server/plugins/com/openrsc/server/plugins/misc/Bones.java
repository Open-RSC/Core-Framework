package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.MiniEvent;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

public class Bones implements InvActionListener, InvActionExecutiveListener {

	private void buryBonesHelper(Player owner, Item item) {
		owner.message("You bury the "
			+ item.getDef(owner.getWorld()).getName().toLowerCase());
		owner.getInventory().remove(item);
		switch (ItemId.getById(item.getID())) {
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
			if (item.getID() == 1308 || item.getID() == 1648 || item.getID() == 1793 || item.getID() == 1871 || item.getID() == 2257) {
				player.message("You can't bury noted bones");
				return;
			}
			if (item.getAmount() > 1) { // bury all
				player.message("You dig a hole in the ground");
				player.setBatchEvent(new BatchEvent(player.getWorld(), player, 650, String.format("Bury %s", item.getDef(player.getWorld()).getName()), item.getAmount(), false) {
					@Override
					public void action() {
						buryBonesHelper(player, item);
					}
				});
			} else {
				player.setBusyTimer(640);
				player.message("You dig a hole in the ground");
				player.getWorld().getServer().getGameEventHandler()
					.add(new MiniEvent(player.getWorld(), player, "Bury Bones") {
						public void action() {
							buryBonesHelper(player, item);
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
