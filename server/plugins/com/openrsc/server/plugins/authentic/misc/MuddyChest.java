package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MuddyChest implements OpLocTrigger, UseLocTrigger {

	private final int MUDDY_CHEST = 222;
	private final int MUDDY_CHEST_OPEN = 221;

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == MUDDY_CHEST) {
			player.message("the chest is locked");
		}
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == MUDDY_CHEST && item.getCatalogId() == ItemId.MUDDY_KEY.id()) {
			if (player.getCarriedItems().remove(new Item(ItemId.MUDDY_KEY.id())) == -1) return;

			int respawnTime = 3000;
			player.message("you unlock the chest with your key");
			changeloc(obj, respawnTime, MUDDY_CHEST_OPEN);
			player.message("You find some treasure in the chest");

			give(player, ItemId.UNCUT_RUBY.id(), 1);
			give(player, ItemId.MITHRIL_BAR.id(), 1);
			give(player, ItemId.LAW_RUNE.id(), 2);
			give(player, ItemId.ANCHOVIE_PIZZA.id(), 1);
			give(player, ItemId.MITHRIL_DAGGER.id(), 1);
			give(player, ItemId.COINS.id(), 50);
			give(player, ItemId.DEATH_RUNE.id(), 2);
			give(player, ItemId.CHAOS_RUNE.id(), 10);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == MUDDY_CHEST;
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == MUDDY_CHEST && item.getCatalogId() == ItemId.MUDDY_KEY.id();
	}
}
