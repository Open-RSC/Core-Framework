package com.openrsc.server.plugins.custom.misc.PeelingTheOnionItems;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.multi;
import static com.openrsc.server.plugins.RuneScript.mes;

public class YellowgreenClay implements OpInvTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		mes("Would you like to shape the clay?");
		int multi = multi(player, "Yes", "No");
		if (multi == 0) {
			if (player.getLevel(Skill.CRAFTING.id()) < 5) {
				mes("You must have at least level 5 crafting to successfully sculpt Ogre Ears");
				return;
			}
			if (player.getCarriedItems().remove(item) != -1) {
				give(player, ItemId.OGRE_EARS.id(), 1);
				player.incExp(Skill.CRAFTING.id(), 40, true);
				mes("You sculpt the clay into a beautiful pair of ogre ears");
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.YELLOWGREEN_CLAY.id();
	}
}
