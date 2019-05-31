package com.openrsc.server.plugins.quests.members.undergroundpass.mechanism;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.sleep;

public class UndergroundPassMechanismMap1 implements InvUseOnItemListener, InvUseOnItemExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	/**
	 * OBJECT IDs
	 **/
	private static int OLD_BRIDGE = 726;
	private static int STALACTITE_1 = 771;
	private static int STALACTITE_2 = 798;
	private static int SWAMP_CROSS = 754;


	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		String itemArrow1 = item1.getDef().getName().toLowerCase();
		String itemArrow2 = item2.getDef().getName().toLowerCase();
		return (item1.getID() == ItemId.DAMP_CLOTH.id() && itemArrow2.contains("arrows"))
				|| (itemArrow1.contains("arrows") && item2.getID() == ItemId.DAMP_CLOTH.id());
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		String itemArrow1 = item1.getDef().getName().toLowerCase();
		String itemArrow2 = item2.getDef().getName().toLowerCase();
		if ((item1.getID() == ItemId.DAMP_CLOTH.id() && itemArrow2.contains("arrows"))
				|| (itemArrow1.contains("arrows") && item2.getID() == ItemId.DAMP_CLOTH.id())) {
			int idArrow = itemArrow2.contains("arrows") ? item2.getID() : item1.getID();
			player.message("you wrap the damp cloth around the arrow head");
			removeItem(player, ItemId.DAMP_CLOTH.id(), 1);
			removeItem(player, idArrow, 1);
			addItem(player, ItemId.ARROW.id(), 1);
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		return (item.getID() == ItemId.ARROW.id() && obj.getID() == 97)
				|| (item.getID() == ItemId.LIT_ARROW.id() && obj.getID() == OLD_BRIDGE)
				|| (item.getID() == ItemId.ROPE.id() && (obj.getID() == STALACTITE_1 || obj.getID() == STALACTITE_2 || obj.getID() == STALACTITE_2 + 1))
				|| (item.getID() == ItemId.ROCKS.id() && obj.getID() == SWAMP_CROSS);
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if (item.getID() == ItemId.ARROW.id() && obj.getID() == 97) {
			player.message("you light the cloth wrapped arrow head");
			removeItem(player, ItemId.ARROW.id(), 1);
			addItem(player, ItemId.LIT_ARROW.id(), 1);
		}
		else if (item.getID() == ItemId.LIT_ARROW.id() && obj.getID() == OLD_BRIDGE) {
			if (hasABow(player)) {
				removeItem(player, ItemId.LIT_ARROW.id(), 1);
				if ((getCurrentLevel(player, Skills.RANGED) < 25) || (player.getY() != 3417 && player.getX() < 701)) {
					message(player, "you fire the lit arrow at the bridge",
						"it burns out and has little effect");
				} else {
					message(player, "you fire your arrow at the rope supporting the bridge");
					if (DataConversions.getRandom().nextInt(5) == 1) {
						player.message("the arrow just misses the rope");
					} else {
						if (player.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 2) {
							player.updateQuestStage(Constants.Quests.UNDERGROUND_PASS, 3);
						}
						message(player, "the arrow impales the wooden bridge, just below the rope support",
							"the rope catches alight and begins to burn",
							"the bridge swings down creating a walkway");
						World.getWorld().replaceGameObject(obj,
							new GameObject(obj.getLocation(), 727, obj.getDirection(), obj
								.getType()));
						World.getWorld().delayedSpawnObject(obj.getLoc(), 10000);
						player.teleport(702, 3420);
						sleep(1000);
						player.teleport(706, 3420);
						sleep(650);
						player.teleport(709, 3420);
						player.message("you rush across the bridge");
					}
				}
			} else {
				player.message("first you'll need a bow");
			}
		}
		else if (item.getID() == ItemId.ROPE.id() && (obj.getID() == STALACTITE_1 || obj.getID() == STALACTITE_2 || obj.getID() == STALACTITE_2 + 1)) {
			message(player, "you lasso the rope around the stalactite",
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
		else if (item.getID() == ItemId.ROCKS.id() && obj.getID() == SWAMP_CROSS) {
			message(player, "you throw the rocks onto the swamp");
			player.message("and carefully tread from one to another");
			removeItem(player, ItemId.ROCKS.id(), 1);
			GameObject object = new GameObject(Point.location(697, 3441), 774, 2, 0);
			World.getWorld().registerGameObject(object);
			World.getWorld().delayedRemoveObject(object, 10000);
			if (player.getX() <= 695) {
				player.teleport(698, 3441);
				sleep(850);
				player.teleport(700, 3441);
			} else {
				player.teleport(698, 3441);
				sleep(850);
				player.teleport(695, 3441);
			}
		}
	}

	private boolean hasABow(Player p) {
		for (Item bow : p.getInventory().getItems()) {
			String bowName = bow.getDef().getName().toLowerCase();
			if (bowName.contains("bow")) {
				return true;
			}
		}
		return false;
	}
}
