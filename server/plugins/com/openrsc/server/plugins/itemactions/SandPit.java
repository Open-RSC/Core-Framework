package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;

import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.sleep;

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
		player.setBatchEvent(new BatchEvent(player.getWorld(), player, 600, "Fill Bucket with Sand", player.getInventory().countId(itemID), true) {
			@Override
			public void action() {
				if (getOwner().getInventory().hasInInventory(itemID)) {
					showBubble(getOwner(), item);
					sleep(300);
					getOwner().message("you fill the bucket with sand");
					getOwner().getInventory().replace(itemID, refilledID,true);
				} else {
					interrupt();
				}
			}
		});
	}
}
