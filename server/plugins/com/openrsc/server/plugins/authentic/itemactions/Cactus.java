package com.openrsc.server.plugins.authentic.itemactions;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.*;

public class Cactus implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 35 && item.getCatalogId() == ItemId.KNIFE.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject object, final Item item) {
		if (item.getCatalogId() != ItemId.KNIFE.id()) {
			player.message("Nothing interesting happens");
			return;
		}
		player.message("You use your woodcutting skill to extract some water from the cactus.");
		int[] skins = {ItemId.WATER_SKIN_MOSTLY_FULL.id(), ItemId.WATER_SKIN_MOSTLY_EMPTY.id(),
				ItemId.WATER_SKIN_MOUTHFUL_LEFT.id(), ItemId.EMPTY_WATER_SKIN.id()};
		thinkbubble(item);
		delay(2);
		for (int s : skins) {
			Item toRemove = new Item(s, 1);
			if (player.getCarriedItems().remove(toRemove) > -1) {
				boolean fail = Formulae.cutCacti();
				if (fail) {
					player.message("You make a mistake and fail to fill your waterskin.");
					player.incExp(Skill.WOODCUTTING.id(), 4, true);
					player.getCarriedItems().getInventory().add(new Item(s, 1));
					return;
				}

				player.message("You collect some precious water in your waterskin.");

				// Add new skin to inventory
				int newSkin = ItemId.EMPTY_WATER_SKIN.id();
				if (s == ItemId.WATER_SKIN_MOSTLY_FULL.id()) newSkin = ItemId.FULL_WATER_SKIN.id();
				else newSkin = s - 1; // More full is one less id number
				player.getCarriedItems().getInventory().add(new Item(newSkin, 1));

				// Add dried cacti
				Point loc = object.getLocation();
				final GameObject cacti = new GameObject(player.getWorld(), loc, 1028, 0, 0);
				player.getWorld().registerGameObject(cacti);

				// Remove healthy cacti
				player.getWorld().unregisterGameObject(object);
				player.incExp(Skill.WOODCUTTING.id(), 100, true); // Woodcutting XP

				// Swap cacti back after 30 seconds.
				player.getWorld().getServer().getGameEventHandler().add(
					new SingleEvent(player.getWorld(), null, config().GAME_TICK * 50, "Cactus Respawn") {

						@Override
						public void action() {
							if (cacti != null) {
								player.getWorld().registerGameObject(new GameObject(player.getWorld(), loc, 35, 0, 0));
								player.getWorld().unregisterGameObject(cacti);
							}
						}
					}
				);
			} else continue; // None of this skin in the inventory, try next.

			return; // Completed action
		}
		player.message("You need to have a non-full waterskin to contain the fluid.");
	}
}
