package com.openrsc.server.plugins.quests.members.undergroundpass.mechanism;

import static com.openrsc.server.plugins.Functions.RANGED;
import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getMaxLevel;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.Constants;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public class UndergroundPassMechanismMap1 implements InvUseOnItemListener, InvUseOnItemExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	/** ITEM IDs **/
	public static int DAMP_CLOTH = 989;
	public static int ARROW = 984;
	public static int LIT_ARROW = 985;
	public static int ROPE = 237;
	public static int ROCKS = 986;

	/** OBJECT IDs **/
	public static int OLD_BRIDGE = 726;
	public static int STALACTITE_1 = 771;
	public static int STALACTITE_2 = 798;
	public static int SWAMP_CROSS = 754;


	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		String itemArrow = item2.getDef().getName().toLowerCase();
		if(item1.getID() == DAMP_CLOTH && itemArrow.contains("arrows")) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnItem(Player player, Item item1, Item item2) {
		String itemArrow = item2.getDef().getName().toLowerCase();
		if(item1.getID() == DAMP_CLOTH && itemArrow.contains("arrows")) {
			player.message("you wrap the damp cloth around the arrow head");
			removeItem(player, DAMP_CLOTH, 1);
			removeItem(player, item2.getID(), 1);
			addItem(player, ARROW, 1);
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		if(item.getID() == ARROW && obj.getID() == 97 && obj.getX() == 701 && obj.getY() == 3420) {
			return true;
		}
		if(item.getID() == LIT_ARROW && obj.getID() == OLD_BRIDGE) {
			return true;
		}
		if(item.getID() == ROPE && (obj.getID() == STALACTITE_1 || obj.getID() == STALACTITE_2 || obj.getID() == STALACTITE_2 + 1)) {
			return true;
		}
		if(item.getID() == ROCKS && obj.getID() == SWAMP_CROSS) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player player) {
		if(item.getID() == ARROW && obj.getID() == 97 && obj.getX() == 701 && obj.getY() == 3420) {
			player.message("you light the cloth wrapped arrow head");
			removeItem(player, ARROW, 1);
			addItem(player, LIT_ARROW, 1);
		}
		if(item.getID() == LIT_ARROW && obj.getID() == OLD_BRIDGE) {
			if(hasABow(player)) {
				removeItem(player, LIT_ARROW, 1);
				if((getMaxLevel(player, RANGED) < 25) || (player.getY() != 3417 && player.getX() < 701)) {
					message(player, "you fire the lit arrow at the bridge",
							"it burns out and has little effect");
				} else {
					message(player, "you fire your arrow at the rope supporting the bridge");
					if(DataConversions.getRandom().nextInt(5) == 1) {
						player.message("the arrow just misses the rope");
					} else {
						if(player.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == 2) {
							player.updateQuestStage(Constants.Quests.UNDERGROUND_PASS, 3);
						}
						message(player,"the arrow impales the wooden bridge, just below the rope support",
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
		if(item.getID() == ROPE && (obj.getID() == STALACTITE_1 || obj.getID() == STALACTITE_2 || obj.getID() == STALACTITE_2 + 1)) {
			message(player, "you lasso the rope around the stalactite",
					"and pull yourself up");
			if(obj.getID() == STALACTITE_1) {
				player.teleport(695, 3435);
			} else if(obj.getID() == STALACTITE_2) {
				player.teleport(677, 3435);
			} else if(obj.getID() == STALACTITE_2 + 1) {
				player.teleport(682, 3436);
			}
			player.message("you climb from stalactite to stalactite and over the rocks");
		}
		if(item.getID() == ROCKS && obj.getID() == SWAMP_CROSS) {
			message(player, "you throw the rocks onto the swamp");
			player.message("and carefully tread from one to another");
			removeItem(player, ROCKS, 1);
			GameObject object = new GameObject(Point.location(697, 3441), 774, 2, 0);
			World.getWorld().registerGameObject(object);
			World.getWorld().delayedRemoveObject(object, 10000);
			if(player.getX() <= 695) {
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
			if(bowName.contains("bow")) {
				return true;
			} 
		}
		return false;
	}
}
