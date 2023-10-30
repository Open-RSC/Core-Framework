package com.openrsc.server.plugins.custom.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class RunecraftPotion implements OpInvTrigger {

	public static final int[] runecraftPotions = new int[] {
		ItemId.FULL_RUNECRAFT_POTION.id(),
		ItemId.TWO_RUNECRAFT_POTION.id(),
		ItemId.ONE_RUNECRAFT_POTION.id(),
		ItemId.FULL_SUPER_RUNECRAFT_POTION.id(),
		ItemId.TWO_SUPER_RUNECRAFT_POTION.id(),
		ItemId.ONE_SUPER_RUNECRAFT_POTION.id()
	};

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		if (!command.equalsIgnoreCase("drink"))
			return false;

		int id = item.getCatalogId();

		return inArray(id, runecraftPotions);
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (!config().WANT_RUNECRAFT)
			return;

		int id = item.getCatalogId();

		if (id == ItemId.FULL_RUNECRAFT_POTION.id())
			useRunecraftPotion(player, item, ItemId.TWO_RUNECRAFT_POTION.id(), false, 2);
		else if (id == ItemId.TWO_RUNECRAFT_POTION.id())
			useRunecraftPotion(player, item, ItemId.ONE_RUNECRAFT_POTION.id(), false, 1);
		else if (id == ItemId.ONE_RUNECRAFT_POTION.id())
			useRunecraftPotion(player, item, ItemId.EMPTY_VIAL.id(), false, 0);
		else if (id == ItemId.FULL_SUPER_RUNECRAFT_POTION.id())
			useRunecraftPotion(player, item, ItemId.TWO_SUPER_RUNECRAFT_POTION.  id(), true, 2);
		else if (id == ItemId.TWO_SUPER_RUNECRAFT_POTION.id())
			useRunecraftPotion(player, item, ItemId.ONE_SUPER_RUNECRAFT_POTION.  id(), true, 1);
		else if (id == ItemId.ONE_SUPER_RUNECRAFT_POTION.id())
			useRunecraftPotion(player, item, ItemId.EMPTY_VIAL.id(), true, 0);
	}

	private static void useRunecraftPotion(Player player, final Item item, final int newItem, final boolean superPot, final int left) {
		int affectedStat = Skill.RUNECRAFT.id();
		if (player.getConfig().WAIT_TO_REBOOST && isstatup(player, affectedStat)) {
			player.playerServerMessage(MessageType.QUEST, "You already have boosted " + player.
getWorld().getServer().getConstants().getSkills().getSkillName(affectedStat));
			return;
		}

		if (player.getCarriedItems().remove(item) == -1) return;
		player.message("You drink some of your " + item.getDef(player.getWorld()).getName().toLowerCase());
		player.getCarriedItems().getInventory().add(new Item(newItem));
		int newStat;
		// TODO Should probably put the boost values in some kind of configuration or definition at some point.
		addstat(player, Skill.RUNECRAFT.id(), superPot ? 6 : 3, 0);
		delay(2);
		if (left <= 0) {
			player.message("You have finished your potion");
		} else {
			player.message("You have " + left + " doses of potion left");
		}
	}
}
