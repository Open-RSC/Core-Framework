package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.external.SpellDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.handlers.SpellHandler;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.PlayerMageItemListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.sleep;

public class EnchantDragonstoneRing implements PlayerMageItemListener, PlayerMageItemExecutiveListener {
	@Override
	public boolean blockPlayerMageItem(Player p, Integer itemID, Integer spellID) {
		return (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB && itemID.intValue() == ItemId.DRAGONSTONE_RING.id() && spellID.intValue() == 42);
	}

	@Override
	public void onPlayerMageItem(Player p, Integer itemID, Integer spellID) {
		SpellDef spellDef = p.getWorld().getServer().getEntityHandler().getSpellDef(spellID.intValue());
		if (spellDef == null)
			return;

		if (itemID.intValue() == ItemId.DRAGONSTONE_RING.id()) {
			p.message("What type of dragonstone ring would you like to make?");
			sleep(600);
			int choice = Functions.showMenu(p, "Ring of Wealth", "Ring of Avarice");
			int item;
			if (choice == 0) {
				item = ItemId.RING_OF_WEALTH.id();
			} else if (choice == 1) {
				item = ItemId.RING_OF_AVARICE.id();
			} else {
				return;
			}
			SpellHandler.checkAndRemoveRunes(p,spellDef);
			p.getInventory().remove(ItemId.DRAGONSTONE_RING.id(), 1, false);
			p.getInventory().add(new Item(item));
			SpellHandler.finalizeSpell(p, spellDef, "You succesfully enchant the ring");
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
			int choice = Functions.showMenu(player, "Ring of Wealth", "Ring of Avarice");
			if (choice == 0) {
				itemID = ItemId.RING_OF_WEALTH.id();
			} else if (choice == 1) {
				itemID = ItemId.RING_OF_AVARICE.id();
			} else {
				player.message("No choice made.");
				return;
			}
			SpellHandler.checkAndRemoveRunes(player,spellDef);
			player.getInventory().remove(ItemId.DRAGONSTONE_RING.id());
			player.getInventory().add(new Item(itemID));
			SpellHandler.finalizeSpell(player, spellDef);
		}
	}*/
}
