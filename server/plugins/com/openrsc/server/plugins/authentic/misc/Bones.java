package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

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

		int[] prayerSkillIds;
		if (player.getConfig().DIVIDED_GOOD_EVIL) {
			// per Rab, historically gave same xp to praygood and prayevil
			// This was also confirmed by Gugge when both skills leveled same time to 5
			prayerSkillIds = new int[]{Skill.PRAYGOOD.id(), Skill.PRAYEVIL.id()};
		} else {
			prayerSkillIds = new int[]{Skill.PRAYER.id()};
		}
		int factor = player.getConfig().OLD_PRAY_XP ? 3 : 2; // factor to divide by modern is 2 / 2 or 1

		int skillXP = 0;
		switch (ItemId.getById(item.getCatalogId())) {
			case BONES:
				skillXP = 2 * 15; // divided by factor below for 3.75
				break;
			case BAT_BONES:
				skillXP = 2 * 18; // divided by factor below for 4.5
				break;
			case BIG_BONES:
				skillXP = 2 * 50; // divided by factor below for 12.5
				break;
			case DRAGON_BONES:
				skillXP = 2 * 240; // divided by factor below for 60
				break;
			default:
				player.message("Nothing interesting happens");
				break;
		}
		if (skillXP > 0) {
			for (int praySkillId : prayerSkillIds) {
				player.incExp(praySkillId, skillXP / factor, true);
			}
		}
	}
}
