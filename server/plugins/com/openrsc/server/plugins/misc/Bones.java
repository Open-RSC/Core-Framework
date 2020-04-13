package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

public class Bones implements OpInvTrigger {

	private void buryBonesHelper(Player owner, Item item) {
		owner.message("You bury the "
			+ item.getDef(owner.getWorld()).getName().toLowerCase());

		// TODO: Config for custom sounds.
		//owner.playSound("takeobject");

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
	public void onOpInv(Item item, Player player, String command) {
		if(command.equalsIgnoreCase("bury")) {
			if (item.getNoted()) {
				player.message("You can't bury noted bones");
				return;
			}

			int buryAmount = item.getAmount();
			if (buryAmount > 1) {
				if (!player.getWorld().getServer().getConfig().BATCH_PROGRESSION)
					buryAmount = 1;
			}

			player.message("You dig a hole in the ground");

			player.setBatchEvent(new BatchEvent(player.getWorld(), player, player.getWorld().getServer().getConfig().GAME_TICK, String.format("Bury %s", item.getDef(player.getWorld()).getName()), buryAmount, false) {
				@Override
				public void action() {
					if (getOwner().getCarriedItems().remove(item.getCatalogId(), 1) > -1) {
						buryBonesHelper(player, item);
					} else
						interrupt();

					if (!this.isCompleted())
						player.message("You dig a hole in the ground");
				}
			});
		}
	}

	@Override
	public boolean blockOpInv(Item item, Player player, String command) {
		return command.equalsIgnoreCase("bury");
	}
}
