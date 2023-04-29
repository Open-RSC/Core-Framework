package com.openrsc.server.plugins.custom.itemactions;
			
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.DataConversions; 
import com.openrsc.server.util.rsc.MessageType;
	
import java.util.Optional;
import java.util.stream.IntStream;

import static com.openrsc.server.plugins.Functions.*;
		
public class SaradominPotion implements OpInvTrigger {

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		if (!command.equalsIgnoreCase("drink"))
			return false;

		int id = item.getCatalogId();

		if (inArray(id, ItemId.FULL_POTION_OF_SARADOMIN.id(),
				ItemId.TWO_POTION_OF_SARADOMIN.id(),
				ItemId.ONE_POTION_OF_SARADOMIN.id())) {
			return true;
		}

		return false;
	}       

	@Override	       
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		int id = item.getCatalogId();

		if (id == ItemId.FULL_POTION_OF_SARADOMIN.id())
			useSaradominPotion(player, item, ItemId.TWO_POTION_OF_SARADOMIN.id(), 2);
		else if (id == ItemId.TWO_POTION_OF_SARADOMIN.id())
			useSaradominPotion(player, item, ItemId.ONE_POTION_OF_SARADOMIN.id(), 1);
		else if (id == ItemId.ONE_POTION_OF_SARADOMIN.id())
			useSaradominPotion(player, item, ItemId.EMPTY_VIAL.id(), 0);
	}


	private static void useSaradominPotion(Player player, final Item item, final int newItem, final int left) {
		int affectedStat = Skill.DEFENSE.id();
		if (player.getConfig().WAIT_TO_REBOOST && isstatup(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of the cleansed liquid");
		player.getCarriedItems().getInventory().add(new Item(newItem));
		boolean isLastDose = item.getCatalogId() == ItemId.ONE_POTION_OF_SARADOMIN.id();
		int[] commonAffectedStats = {Skill.ATTACK.id(),
			Skill.DEFENSE.id(),
			Skill.STRENGTH.id(),
			Skill.HITS.id(),
			Skill.RANGED.id()};
		Skill[] magicStats = player.getSkills().getMagicSkills();
		int[] magicIds = new int[magicStats.length];
		for (int i = 0; i < magicStats.length; i++) {
			magicIds[i] = magicStats[i].id();
		}
		int[] affectedStats = concat(commonAffectedStats, magicIds);
		int[] percentageIncrease = concat(new int[]{-10, 20, -10, 15, -10}, IntStream.of(magicIds).map(x -> -10).toArray());
		int[] modifier = concat(new int[]{-1, 1, -1, 1, -1}, IntStream.of(magicIds).map(x -> -1).toArray());
		if (isLastDose) {
			for (int i=0; i<affectedStats.length; i++) modifier[i] *= 3;
		}

		for (int i=0; i<affectedStats.length; i++) {
			boolean isBoost = percentageIncrease[i] >= 0;
			if (isBoost) {
				addstat(player, affectedStats[i], modifier[i], percentageIncrease[i]);
			} else {
				substat(player, affectedStats[i], -modifier[i], -percentageIncrease[i]);
			}
		}

		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " dose" + (left == 1 ? "" : "s") + " of potion left");
		}
	}
}
