package com.openrsc.server.plugins.itemactions;


import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.showBubble;

public class Cactus implements InvUseOnObjectListener,
InvUseOnObjectExecutiveListener {
	
	/**
	 * World instance
	 */
	public static final World world = World.getWorld();
	
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
		Server.getServer().getGameEventHandler()

			.add(new ShortEvent(player, "Cactus Fill Waterskin") {
				public void action() {
					for (int s : skins) {
						if (getOwner().getInventory().remove(s, 1) > -1) {
							boolean fail = Formulae.cutCacti();
							if (fail) {
								getOwner().message("You make a mistake and fail to fill your waterskin.");
								getOwner().incExp(SKILLS.WOODCUT.id(), 4, true);
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
							final GameObject cacti = new GameObject(loc, 1028, 0, 0);
							world.registerGameObject(cacti);

							// Remove healthy cacti
							world.unregisterGameObject(object);
							getOwner().incExp(SKILLS.WOODCUT.id(), 100, true); // Woodcutting XP

							// Swap cacti back after 30 seconds.
							Server.getServer().getGameEventHandler().add(
								new SingleEvent(null, 30000, "Cactus Respawn") {

									@Override
									public void action() {
										if (cacti != null) {
											World.getWorld().registerGameObject(new GameObject(loc, 35, 0, 0));
											World.getWorld().unregisterGameObject(cacti);
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
