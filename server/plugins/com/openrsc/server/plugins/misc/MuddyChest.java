package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MuddyChest implements ObjectActionExecutiveListener, ObjectActionListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	private final int MUDDY_CHEST = 222;
	private final int MUDDY_CHEST_OPEN = 221;

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == MUDDY_CHEST) {
			p.message("the chest is locked");
		}
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == MUDDY_CHEST && item.getID() == ItemId.MUDDY_KEY.id()) {
			int respawnTime = 3000;
			p.message("you unlock the chest with your key");
			replaceObjectDelayed(obj, respawnTime, MUDDY_CHEST_OPEN);
			p.message("You find some treasure in the chest");

			removeItem(p, ItemId.MUDDY_KEY.id(), 1); // remove the muddy key.
			addItem(p, ItemId.UNCUT_RUBY.id(), 1);
			addItem(p, ItemId.MITHRIL_BAR.id(), 1);
			addItem(p, ItemId.LAW_RUNE.id(), 2);
			addItem(p, ItemId.ANCHOVIE_PIZZA.id(), 1);
			addItem(p, ItemId.MITHRIL_DAGGER.id(), 1);
			addItem(p, ItemId.COINS.id(), 50);
			addItem(p, ItemId.DEATH_RUNE.id(), 2);
			addItem(p, ItemId.CHAOS_RUNE.id(), 10);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == MUDDY_CHEST;
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return obj.getID() == MUDDY_CHEST && item.getID() == ItemId.MUDDY_KEY.id();
	}
}
