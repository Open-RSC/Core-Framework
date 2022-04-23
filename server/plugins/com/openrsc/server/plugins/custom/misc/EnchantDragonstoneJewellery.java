package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Spells;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.handlers.SpellHandler;
import com.openrsc.server.plugins.triggers.SpellInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class EnchantDragonstoneJewellery implements SpellInvTrigger {
	@Override
	public boolean blockSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum) {
		return (player.getConfig().WANT_EQUIPMENT_TAB && inArray(itemID, ItemId.DRAGONSTONE_CROWN.id(), ItemId.DRAGONSTONE_RING.id()) && spellEnum == Spells.ENCHANT_LVL5_AMULET);
	}

	@Override
	public void onSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum) {
		SpellDef spellDef = player.getWorld().getServer().getEntityHandler().getSpellDef(spellEnum);
		if (spellDef == null)
			return;
		int itemMakeID = 0;
		String jewelryType = "";
		int choice = -1;
		switch(ItemId.getById(itemID)) {
			case DRAGONSTONE_CROWN:
				player.message("What type of dragonstone crown would you like to make?");
				delay();
				jewelryType = "crown";
				choice = multi(player, "Crown of the Herbalist", "Crown of the Occult");
				if (choice == 0) {
					itemMakeID = ItemId.CROWN_OF_THE_HERBALIST.id();
				} else if (choice == 1) {
					itemMakeID = ItemId.CROWN_OF_THE_OCCULT.id();
				} else {
					return;
				}
				break;
			case DRAGONSTONE_RING:
				player.message("What type of dragonstone ring would you like to make?");
				delay();
				jewelryType = "ring";
				choice = multi(player, "Ring of Wealth", "Ring of Avarice");
				if (choice == 0) {
					itemMakeID = ItemId.RING_OF_WEALTH.id();
				} else if (choice == 1) {
					itemMakeID = ItemId.RING_OF_AVARICE.id();
				} else {
					return;
				}
				break;
		}
		if (!SpellHandler.checkAndRemoveRunes(player, spellDef)) {
			return;
		}
		if (player.getCarriedItems().remove(new Item(itemID)) == -1) return;
		player.getCarriedItems().getInventory().add(new Item(itemMakeID));
		SpellHandler.finalizeSpell(player, spellDef, "You succesfully enchant the " + jewelryType);
	}
}
