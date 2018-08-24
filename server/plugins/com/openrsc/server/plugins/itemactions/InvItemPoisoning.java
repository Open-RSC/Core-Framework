package com.openrsc.server.plugins.itemactions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.removeItem;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemDefinition;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

public class InvItemPoisoning implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return item1.getID() == 572 || item2.getID() == 572;
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		if(item1.getID() == 572) {
			applyPoison(player, item2);
		}
		else if(item2.getID() == 572) {
			applyPoison(player, item1);
		}
	}
	
	
	public void applyPoison(Player player, Item item) {
		int makeAmount = 1;
		
		if(item.getDef().isStackable()) {
			makeAmount = hasItem(player, item.getID(), 15) ? 15 : player.getInventory().countId(item.getID());
		}
		Item poisonedItem = getPoisonedItem(item.getDef().getName());
		if(poisonedItem != null) {
			if(removeItem(player, 572, 1) && removeItem(player, item.getID(), makeAmount)) {
				player.message("You apply poison to your " + item.getDef().getName());
				addItem(player, poisonedItem.getID(), makeAmount);
			}
		} else {
			player.message("Nothing interesting happens");
		}
	}
	
	private Item getPoisonedItem(String name) {
		String poisonedVersion = "Poisoned " + name;
		String poisonedVersion2 = "Poison " + name;
		for(int i = 0; i < EntityHandler.items.length;i++) {
			ItemDefinition def = EntityHandler.getItemDef(i);
			if(def.getName().equalsIgnoreCase(poisonedVersion) || def.getName().equalsIgnoreCase(poisonedVersion2)) {
				return new Item(i);
			}
		}
		return null;
	}
}
