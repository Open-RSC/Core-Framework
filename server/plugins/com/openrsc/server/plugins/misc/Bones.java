package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Bones implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return command.equalsIgnoreCase("bury");
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (command.equalsIgnoreCase("bury")) {

			if (item.getNoted()) {
				player.message("You can't bury noted bones");
				return;
			}

			int buryAmount = 1;
			if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
				buryAmount = item.getAmount();
			}

			startbatch(buryAmount);
			buryBones(player, item);
		}
	}


	private void buryBones(Player player, Item item) {
		Item toRemove = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false)));
		if(toRemove == null) return;

		player.message("You dig a hole in the ground");
		delay(player.getWorld().getServer().getConfig().GAME_TICK);
		player.message("You bury the " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(toRemove);
		giveBonesExperience(player, item);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			buryBones(player, item);
		}
	}

	private void giveBonesExperience(Player player, Item item) {

		// TODO: Config for custom sounds.
		//owner.playSound("takeobject");

		switch (ItemId.getById(item.getCatalogId())) {
			case BONES:
				player.incExp(com.openrsc.server.constants.Skills.PRAYER, 15, true); // 3.75
				break;
			case BAT_BONES:
				player.incExp(com.openrsc.server.constants.Skills.PRAYER, 18, true); // 4.5
				break;
			case BIG_BONES:
				player.incExp(com.openrsc.server.constants.Skills.PRAYER, 50, true); // 12.5
				break;
			case DRAGON_BONES:
				player.incExp(com.openrsc.server.constants.Skills.PRAYER, 240, true); // 60
				break;
			default:
				player.message("Nothing interesting happens");
				break;
		}
	}
}
