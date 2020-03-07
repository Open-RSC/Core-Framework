package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MuddyChest implements OpLocTrigger, UseLocTrigger {

	private final int MUDDY_CHEST = 222;
	private final int MUDDY_CHEST_OPEN = 221;

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == MUDDY_CHEST) {
			p.message("the chest is locked");
		}
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (obj.getID() == MUDDY_CHEST && item.getCatalogId() == ItemId.MUDDY_KEY.id()) {
			int respawnTime = 3000;
			p.message("you unlock the chest with your key");
			Functions.changeloc(obj, respawnTime, MUDDY_CHEST_OPEN);
			p.message("You find some treasure in the chest");

			remove(p, ItemId.MUDDY_KEY.id(), 1); // remove the muddy key.
			give(p, ItemId.UNCUT_RUBY.id(), 1);
			give(p, ItemId.MITHRIL_BAR.id(), 1);
			give(p, ItemId.LAW_RUNE.id(), 2);
			give(p, ItemId.ANCHOVIE_PIZZA.id(), 1);
			give(p, ItemId.MITHRIL_DAGGER.id(), 1);
			give(p, ItemId.COINS.id(), 50);
			give(p, ItemId.DEATH_RUNE.id(), 2);
			give(p, ItemId.CHAOS_RUNE.id(), 10);
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == MUDDY_CHEST;
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return obj.getID() == MUDDY_CHEST && item.getCatalogId() == ItemId.MUDDY_KEY.id();
	}
}
