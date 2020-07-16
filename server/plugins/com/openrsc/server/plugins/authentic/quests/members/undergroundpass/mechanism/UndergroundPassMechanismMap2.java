package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles.UndergroundPassObstaclesMap2;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassMechanismMap2 implements UseLocTrigger {

	/**
	 * ITEMS_TO_FLAMES: Unicorn horn, coat of arms red and blue.
	 **/
	private static int[] ITEMS_TO_FLAMES = {ItemId.UNDERGROUND_PASS_UNICORN_HORN.id(), ItemId.COAT_OF_ARMS_RED.id(), ItemId.COAT_OF_ARMS_BLUE.id()};

	/**
	 * OBJECT IDs
	 **/
	private static int BOULDER = 867;

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST && item.getCatalogId() == ItemId.ROPE.id())
				|| (obj.getID() == UndergroundPassObstaclesMap2.PASSAGE && item.getCatalogId() == ItemId.PLANK.id())
				|| (obj.getID() == BOULDER && item.getCatalogId() == ItemId.RAILING.id())
				|| (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && inArray(item.getCatalogId(), ITEMS_TO_FLAMES))
				|| (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && item.getCatalogId() == ItemId.STAFF_OF_IBAN.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == UndergroundPassObstaclesMap2.WALL_GRILL_EAST && item.getCatalogId() == ItemId.ROPE.id()) {
			if (player.getX() == 763 && player.getY() == 3463) {
				player.message("you can't reach the grill from here");
			} else {
				mes("you tie the rope to the grill...");
				delay(3);
				player.message("..and poke it through to the otherside");
				if (!player.getCache().hasKey("rope_wall_grill")) {
					player.getCache().store("rope_wall_grill", true);
				}
			}
		}
		else if (item.getCatalogId() == ItemId.PLANK.id() && obj.getID() == UndergroundPassObstaclesMap2.PASSAGE) {
			player.message("you carefully place the planks over the pressure triggers");
			player.message("you walk across the wooden planks");
			player.getCarriedItems().remove(new Item(ItemId.PLANK.id()));
			player.teleport(735, 3489);
			delay(2);
			if (obj.getX() == 737) {
				player.teleport(732, 3489);
			} else if (obj.getX() == 733) {
				player.teleport(738, 3489);
			}
		}
		else if (obj.getID() == BOULDER && item.getCatalogId() == ItemId.RAILING.id()) {
			mes("you use the pole as leverage...");
			delay(3);
			mes("..and tip the bolder onto its side");
			delay(3);
			delloc(obj);
			addloc(obj.getWorld(), obj.getLoc(), 5000);
			player.message("it tumbles down the slope");
			if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 3) {
				player.updateQuestStage(Quests.UNDERGROUND_PASS, 4);
			}
		}
		else if (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && inArray(item.getCatalogId(), ITEMS_TO_FLAMES)) {
			mes("you throw the " + item.getDef(player.getWorld()).getName().toLowerCase() + " into the flames");
			delay(3);
			if (!atQuestStages(player, Quests.UNDERGROUND_PASS, 7, 8, -1)) {
				if (!player.getCache().hasKey("flames_of_zamorak1") && item.getCatalogId() == ItemId.UNDERGROUND_PASS_UNICORN_HORN.id()) {
					player.getCache().store("flames_of_zamorak1", true);
				}
				if (!player.getCache().hasKey("flames_of_zamorak2") && item.getCatalogId() == ItemId.COAT_OF_ARMS_RED.id()) {
					player.getCache().store("flames_of_zamorak2", true);
				}
				int stage = 0;
				if (item.getCatalogId() == ItemId.COAT_OF_ARMS_BLUE.id()) {
					if (!player.getCache().hasKey("flames_of_zamorak3")) {
						player.getCache().set("flames_of_zamorak3", 1);
					} else {
						stage = player.getCache().getInt("flames_of_zamorak3");
						if (stage < 2)
							player.getCache().set("flames_of_zamorak3", stage + 1);
					}
				}
			}
			player.getCarriedItems().remove(new Item(item.getCatalogId()));
			player.message("you hear a howl in the distance");
		}
		else if (obj.getID() == UndergroundPassObstaclesMap2.FLAMES_OF_ZAMORAK && item.getCatalogId() == ItemId.STAFF_OF_IBAN.id()) {
			mes("you hold the staff above the well");
			displayTeleportBubble(player, player.getX(), player.getY(), true);
			player.message("and feel the power of zamorak flow through you");
			player.getCache().set("Iban blast_casts", 25);
		}
	}
}
