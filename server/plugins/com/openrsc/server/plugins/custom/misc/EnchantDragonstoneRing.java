package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Spells;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.handlers.SpellHandler;
import com.openrsc.server.plugins.triggers.SpellInvTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.multi;

public class EnchantDragonstoneRing implements SpellInvTrigger {
	@Override
	public boolean blockSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum) {
		return (player.getConfig().WANT_EQUIPMENT_TAB && itemID.intValue() == ItemId.DRAGONSTONE_RING.id() && spellEnum == Spells.ENCHANT_LVL5_AMULET);
	}

	@Override
	public void onSpellInv(Player player, Integer invIndex, Integer itemID, Spells spellEnum) {
		SpellDef spellDef = player.getWorld().getServer().getEntityHandler().getSpellDef(spellEnum);
		if (spellDef == null)
			return;
		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(ItemId.DRAGONSTONE_RING.id(), Optional.of(false)));
		if (item == null) return;
		if (itemID.intValue() == ItemId.DRAGONSTONE_RING.id()) {
			player.message("What type of dragonstone ring would you like to make?");
			delay();
			int choice = multi(player, "Ring of Wealth", "Ring of Avarice");
			int i;
			if (choice == 0) {
				i = ItemId.RING_OF_WEALTH.id();
			} else if (choice == 1) {
				i = ItemId.RING_OF_AVARICE.id();
			} else {
				return;
			}
			SpellHandler.checkAndRemoveRunes(player,spellDef);
			Item toRemove = new Item(item.getCatalogId(), 1, false, item.getItemId());
			player.getCarriedItems().remove(toRemove);
			player.getCarriedItems().getInventory().add(new Item(i));
			SpellHandler.finalizeSpell(player, spellDef, "You succesfully enchant the ring");
		}
	}
	/*@Override
	public boolean blockPlayerMageItem(Player player, int itemID, int spell) {
		return (spell == 42 && itemID == ItemId.DRAGONSTONE_RING.id());
	}

	@Override
	public void onPlayerMageItem(Player player, int itemID, int spell) {
		SpellDef spellDef = EntityHandler.getSpellDef(spell);
		if (spellDef == null)
			return;

		if (itemID == ItemId.DRAGONSTONE_RING.id()) {
			player.message("What type of dragonstone ring would you like to make?");
			sleep(600);
			int choice = showMenu(player, "Ring of Wealth", "Ring of Avarice");
			if (choice == 0) {
				itemID = ItemId.RING_OF_WEALTH.id();
			} else if (choice == 1) {
				itemID = ItemId.RING_OF_AVARICE.id();
			} else {
				player.message("No choice made.");
				return;
			}
			SpellHandler.checkAndRemoveRunes(player,spellDef);
			player.getCarriedItems().getInventory().remove(ItemId.DRAGONSTONE_RING.id());
			player.getCarriedItems().getInventory().add(new Item(itemID));
			SpellHandler.finalizeSpell(player, spellDef);
		}
	}*/
}
