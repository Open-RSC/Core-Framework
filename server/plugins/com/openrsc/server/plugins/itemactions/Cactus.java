package com.openrsc.server.plugins.itemactions;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.showBubble;

public class Cactus implements InvUseOnObjectListener,
InvUseOnObjectExecutiveListener {
	
	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return obj.getID() == 35 && item.getID() == ItemId.KNIFE.id();
	}
	
	@Override
	public void onInvUseOnObject(GameObject object, final Item item, Player player) {
		if (item.getID() != ItemId.KNIFE.id()) {
			player.message("Nothing interesting happens");
			return;
		}
		player.message("You use your woodcutting skill to extract some water from the cactus.");
		int[] skins = {ItemId.WATER_SKIN_MOSTLY_FULL.id(), ItemId.WATER_SKIN_MOSTLY_EMPTY.id(),
				ItemId.WATER_SKIN_MOUTHFUL_LEFT.id(), ItemId.EMPTY_WATER_SKIN.id()};
		showBubble(player, item);
		player.setBusy(true);
		player.getWorld().getServer().getGameEventHandler()

			.add(new ShortEvent(player.getWorld(), player, "Cactus Fill Waterskin") {
				public void action() {
					for (int s : skins) {
						if (getOwner().getInventory().remove(s, 1) > -1) {
							boolean fail = Formulae.cutCacti();
							if (fail) {
								getOwner().message("You make a mistake and fail to fill your waterskin.");
								getOwner().incExp(Skills.WOODCUT, 4, true);
								getOwner().getInventory().add(new Item(s, 1));
								getOwner().setBusy(false);
								return;
							}

							getOwner().message("You collect some precious water in your waterskin.");

							// Add new skin to inventory
							int newSkin = ItemId.EMPTY_WATER_SKIN.id();
							if (s == ItemId.WATER_SKIN_MOSTLY_FULL.id()) newSkin = ItemId.FULL_WATER_SKIN.id();
							else newSkin = s - 1; // More full is one less id number
							getOwner().getInventory().add(new Item(newSkin, 1));

							// Add dried cacti
							Point loc = object.getLocation();
							final GameObject cacti = new GameObject(getOwner().getWorld(), loc, 1028, 0, 0);
							getOwner().getWorld().registerGameObject(cacti);

							// Remove healthy cacti
							getOwner().getWorld().unregisterGameObject(object);
							getOwner().incExp(Skills.WOODCUT, 100, true); // Woodcutting XP

							// Swap cacti back after 30 seconds.
							getOwner().getWorld().getServer().getGameEventHandler().add(
								new SingleEvent(getOwner().getWorld(), null, 30000, "Cactus Respawn") {

									@Override
									public void action() {
										if (cacti != null) {
											getOwner().getWorld().registerGameObject(new GameObject(getOwner().getWorld(), loc, 35, 0, 0));
											getOwner().getWorld().unregisterGameObject(cacti);
										}
									}
								}
							);
						} else continue; // None of this skin in the inventory, try next.

						getOwner().setBusy(false);
						return; // Completed action
					}
					getOwner().message("You need to have a non-full waterskin to contain the fluid.");
					getOwner().setBusy(false);
					return;
				}
			});
	}
}
