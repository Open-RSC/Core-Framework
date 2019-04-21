package com.openrsc.server.plugins.itemactions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;

public class SandPit implements InvUseOnObjectListener,
InvUseOnObjectExecutiveListener {
	
	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return obj.getID() == 302 && item.getID() == ItemId.BUCKET.id();
	}
	
	@Override
	public void onInvUseOnObject(GameObject obj, final Item item, Player player) {
		final int itemID = item.getID();
		final int refilledID = ItemId.SAND.id();
		if (item.getID() != ItemId.BUCKET.id()) {
			player.message("Nothing interesting happens");
			return;
		}
		player.setBatchEvent(new BatchEvent(player, 600, player.getInventory().countId(itemID)) {
			@Override
			public void action() {
				if (removeItem(owner, itemID, 1)) {
					showBubble(owner, item);
					sleep(300);
					owner.message("you fill the bucket with sand");
					addItem(owner, refilledID, 1);
				} else {
					interrupt();
				}
			}
		});
	}
}
