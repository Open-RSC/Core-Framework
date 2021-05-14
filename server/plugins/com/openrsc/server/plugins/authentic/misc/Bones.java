package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.SkillsEnum;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.util.SkillSolver.getSkillId;

public class Bones implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return command.equalsIgnoreCase("bury") && item.getCatalogId() != ItemId.RASHILIYA_CORPSE.id();
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.RASHILIYA_CORPSE.id()) return;
		if (command.equalsIgnoreCase("bury")) {

			if (item.getNoted()) {
				player.message("You can't bury noted bones");
				return;
			}

			int buryAmount = 1;
			if (config().BATCH_PROGRESSION) {
				int invAmount = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
				buryAmount = (item.getAmount() > invAmount) ? invAmount : item.getAmount();
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
		delay();
		player.message("You bury the " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().remove(toRemove);
		giveBonesExperience(player, item);

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			buryBones(player, item);
		}
	}

	private void giveBonesExperience(Player player, Item item) {

		// TODO: Config for custom sounds.
		//owner.playSound("takeobject");

		SkillsEnum prayerSkill;
		if (player.getConfig().DIVIDED_GOOD_EVIL) {
			prayerSkill = DataConversions.random(0, 1) == 0 ? SkillsEnum.PRAYGOOD : SkillsEnum.PRAYEVIL;
		} else {
			prayerSkill = SkillsEnum.PRAYER;
		}
		int factor = player.getConfig().OLD_PRAY_XP ? 3 : 2; // factor to divide by modern is 2 / 2 or 1

		switch (ItemId.getById(item.getCatalogId())) {
			case BONES:
				player.incExp(getSkillId(player.getWorld(), prayerSkill), 2 * 15 / factor, true); // 3.75
				break;
			case BAT_BONES:
				player.incExp(getSkillId(player.getWorld(), prayerSkill), 2 * 18 / factor, true); // 4.5
				break;
			case BIG_BONES:
				player.incExp(getSkillId(player.getWorld(), prayerSkill), 2 * 50 / factor, true); // 12.5
				break;
			case DRAGON_BONES:
				player.incExp(getSkillId(player.getWorld(), prayerSkill), 2 * 240 / factor, true); // 60
				break;
			default:
				player.message("Nothing interesting happens");
				break;
		}
	}
}
