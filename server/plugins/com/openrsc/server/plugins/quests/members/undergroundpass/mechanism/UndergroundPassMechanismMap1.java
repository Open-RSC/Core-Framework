package com.openrsc.server.plugins.quests.members.undergroundpass.mechanism;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassMechanismMap1 implements UseInvTrigger, UseLocTrigger {

	/**
	 * OBJECT IDs
	 **/
	private static int OLD_BRIDGE = 726;
	private static int STALACTITE_1 = 771;
	private static int STALACTITE_2 = 798;
	private static int SWAMP_CROSS = 754;


	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		String itemArrow1 = item1.getDef(player.getWorld()).getName().toLowerCase();
		String itemArrow2 = item2.getDef(player.getWorld()).getName().toLowerCase();
		return (item1.getCatalogId() == ItemId.DAMP_CLOTH.id() && itemArrow2.contains("arrows"))
				|| (itemArrow1.contains("arrows") && item2.getCatalogId() == ItemId.DAMP_CLOTH.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		String itemArrow1 = item1.getDef(player.getWorld()).getName().toLowerCase();
		String itemArrow2 = item2.getDef(player.getWorld()).getName().toLowerCase();
		if ((item1.getCatalogId() == ItemId.DAMP_CLOTH.id() && itemArrow2.contains("arrows"))
				|| (itemArrow1.contains("arrows") && item2.getCatalogId() == ItemId.DAMP_CLOTH.id())) {
			int idArrow = itemArrow2.contains("arrows") ? item2.getCatalogId() : item1.getCatalogId();
			player.message("you wrap the damp cloth around the arrow head");
			player.getCarriedItems().remove(new Item(ItemId.DAMP_CLOTH.id()));
			player.getCarriedItems().remove(new Item(idArrow));
			give(player, ItemId.ARROW.id(), 1);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (item.getCatalogId() == ItemId.ARROW.id() && obj.getID() == 97)
				|| (item.getCatalogId() == ItemId.LIT_ARROW.id() && obj.getID() == OLD_BRIDGE)
				|| (item.getCatalogId() == ItemId.ROPE.id() && (obj.getID() == STALACTITE_1 || obj.getID() == STALACTITE_2 || obj.getID() == STALACTITE_2 + 1))
				|| (item.getCatalogId() == ItemId.ROCKS.id() && obj.getID() == SWAMP_CROSS);
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.ARROW.id() && obj.getID() == 97) {
			player.message("you light the cloth wrapped arrow head");
			player.getCarriedItems().remove(new Item(ItemId.ARROW.id()));
			give(player, ItemId.LIT_ARROW.id(), 1);
		}
		else if (item.getCatalogId() == ItemId.LIT_ARROW.id() && obj.getID() == OLD_BRIDGE) {
			if (hasABow(player)) {
				player.getCarriedItems().remove(new Item(ItemId.LIT_ARROW.id()));
				if ((getCurrentLevel(player, Skills.RANGED) < 25) || (player.getY() != 3417 && player.getX() < 701)) {
					mes(player, "you fire the lit arrow at the bridge",
						"it burns out and has little effect");
				} else {
					mes(player, "you fire your arrow at the rope supporting the bridge");
					if (DataConversions.getRandom().nextInt(5) == 1) {
						player.message("the arrow just misses the rope");
					} else {
						if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 2) {
							player.updateQuestStage(Quests.UNDERGROUND_PASS, 3);
						}
						mes(player, "the arrow impales the wooden bridge, just below the rope support",
							"the rope catches alight and begins to burn",
							"the bridge swings down creating a walkway");
						player.getWorld().replaceGameObject(obj,
							new GameObject(obj.getWorld(), obj.getLocation(), 727, obj.getDirection(), obj
								.getType()));
						player.getWorld().delayedSpawnObject(obj.getLoc(), 10000);
						player.teleport(702, 3420);
						delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
						player.teleport(706, 3420);
						delay(player.getWorld().getServer().getConfig().GAME_TICK);
						player.teleport(709, 3420);
						player.message("you rush across the bridge");
					}
				}
			} else {
				player.message("first you'll need a bow");
			}
		}
		else if (item.getCatalogId() == ItemId.ROPE.id() && (obj.getID() == STALACTITE_1 || obj.getID() == STALACTITE_2 || obj.getID() == STALACTITE_2 + 1)) {
			mes(player, "you lasso the rope around the stalactite",
				"and pull yourself up");
			if (obj.getID() == STALACTITE_1) {
				player.teleport(695, 3435);
			} else if (obj.getID() == STALACTITE_2) {
				player.teleport(677, 3435);
			} else if (obj.getID() == STALACTITE_2 + 1) {
				player.teleport(682, 3436);
			}
			player.message("you climb from stalactite to stalactite and over the rocks");
		}
		else if (item.getCatalogId() == ItemId.ROCKS.id() && obj.getID() == SWAMP_CROSS) {
			mes(player, "you throw the rocks onto the swamp");
			player.message("and carefully tread from one to another");
			player.getCarriedItems().remove(new Item(ItemId.ROCKS.id()));
			GameObject object = new GameObject(player.getWorld(), Point.location(697, 3441), 774, 2, 0);
			player.getWorld().registerGameObject(object);
			player.getWorld().delayedRemoveObject(object, 10000);
			if (player.getX() <= 695) {
				player.teleport(698, 3441);
				delay(850);
				player.teleport(700, 3441);
			} else {
				player.teleport(698, 3441);
				delay(850);
				player.teleport(695, 3441);
			}
		}
	}

	private boolean hasABow(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item bow : player.getCarriedItems().getInventory().getItems()) {
				String bowName = bow.getDef(player.getWorld()).getName().toLowerCase();
				if (bowName.contains("bow")) {
					return true;
				}
			}
			return false;
		}
	}
}
