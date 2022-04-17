package com.openrsc.server.plugins.custom.misc.PeelingTheOnionItems;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import static com.openrsc.server.plugins.Functions.compareItemsIds;
import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.RuneScript.mes;

public class LeatherVestCrafting implements UseInvTrigger {
	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (player.getCarriedItems().remove(new Item(ItemId.LEATHER_ARMOUR.id(), 1)) != -1) {
			give(player, ItemId.LEATHER_VEST.id(), 1);
			mes("You slash the leather armour up into a fashionable vest");
			player.incExp(Skill.CRAFTING.id(), 8, true);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return player.getConfig().WANT_CUSTOM_QUESTS && compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.LEATHER_ARMOUR.id());
	}
}
