package com.openrsc.server.plugins.misc;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.DropListener;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.DropExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class KittenToCat implements DropListener, DropExecutiveListener, InvActionListener, InvActionExecutiveListener {

	@Override
	public boolean blockDrop(Player p, Item i) {
		return i.getID() == ItemId.KITTEN.id();
	}

	@Override
	public void onDrop(Player p, Item i) {
		if (i.getID() == ItemId.KITTEN.id()) {
			removeItem(p, ItemId.KITTEN.id(), 1);
			message(p, 1200, "you drop the kitten");
			message(p, 0, "it's upset and runs away");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		return item.getID() == ItemId.KITTEN.id();
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if (item.getID() == ItemId.KITTEN.id()) {
			message(p, "you softly stroke the kitten",
				"@yel@kitten:..purr..purr..");
			message(p, 600, "the kitten appreciates the attention");
		}
	}
}
