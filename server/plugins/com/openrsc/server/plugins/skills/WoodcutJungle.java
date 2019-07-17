package com.openrsc.server.plugins.skills;

import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.showBubble;
import static com.openrsc.server.plugins.Functions.sleep;

public class WoodcutJungle implements ObjectActionListener,
	ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener {

	private static int[] JUNGLE_TREES = {1086, 1100, 1099, 1092, 1091};

	private static int JUNGLE_VINE = 204;

	private static int JUNGLE_TREE_STUMP = 1087;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), JUNGLE_TREES)) {
			return true;
		}
		if (obj.getID() == JUNGLE_TREE_STUMP) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), JUNGLE_TREES)) {
			handleJungleWoodcut(obj, p);
		}
		if (obj.getID() == JUNGLE_TREE_STUMP) {
			p.teleport(obj.getX(), obj.getY());
		}
	}

	private void handleJungleWoodcut(GameObject obj, Player p) {
		if (p.getFatigue() >= p.MAX_FATIGUE) {
			p.message("You are too tired to cut the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree"));

			// Shilo side of the jungle
			if (p.getY() < 866) {
				return;
			} else if (p.getY() >= 866) { // Khazari side, force out.
				p.message("It takes you some time, but you eventually make your way");
				p.message("out of the Khazari jungle.");
				cutJungle(0, obj, p, true);
				return;
			}
		}

		//if(!hasItem(p, 1163) && !hasItem(p, 1233) && p.getQuestStage(Constants.Quests.LEGENDS_QUEST) != -1) { // the radimus scrolls.
		//	message(p, 1900, "This jungle is far too thick, you'll need a special map to go further.");
		//	return;
		//}

		if (getCurrentLevel(p, SKILLS.WOODCUT.id()) < 50) {
			p.message("You need a woodcutting level of 50 to axe this tree");
			return;
		}

		if (!hasItem(p, ItemId.MACHETTE.id())) {
			message(p, 1900, "This jungle is very thick, you'll need a machette to cut through.");
			return;
		}

		int axeId = -1;
		if (obj.getID() != JUNGLE_VINE) {
			for (final int a : Formulae.woodcuttingAxeIDs) {
				if (p.getInventory().countId(a) > 0) {
					axeId = a;
					break;
				}
			}
			if (axeId < 0) {
				p.message("You need an axe to chop this tree down");
				return;
			}
		} else {
			axeId = ItemId.MACHETTE.id();
		}

		p.setBusy(true);
		showBubble(p, new Item(axeId));
		message(p, 1300, "You swing your " + EntityHandler.getItemDef(axeId).getName().toLowerCase() + " at the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree") + "...");
		if (p.getFatigue() >= 74920) {
			if (p.getFatigue() < p.MAX_FATIGUE) {
				p.message("You are getting very tired, you may get stuck if you continue into the jungle.");
			}
		}

		cutJungle(axeId, obj, p, false);
	}

	private void cutJungle(int axeId, GameObject obj, Player p, boolean force) {
		if (force || getLog(50, p.getSkills().getLevel(SKILLS.WOODCUT.id()), axeId)) {
			GameObject jungleObject = p.getViewArea().getGameObject(obj.getID(), obj.getX(), obj.getY());
			if (jungleObject != null && jungleObject.getID() == obj.getID()) {
				if (obj.getID() == JUNGLE_VINE) {
					World.getWorld().unregisterGameObject(jungleObject);
					World.getWorld().delayedSpawnObject(obj.getLoc(), 5500); // 5.5 seconds.
					if (!force)
						message(p, 1200, "You hack your way through the jungle.");
				} else {
					World.getWorld().replaceGameObject(obj, new GameObject(obj.getLocation(), JUNGLE_TREE_STUMP, obj.getDirection(), obj.getType()));
					World.getWorld().delayedSpawnObject(obj.getLoc(), 60 * 1000); // 1 minute.
				}

				if (!force)
					p.incExp(SKILLS.WOODCUT.id(), 20, true);
			}
			if (DataConversions.random(0, 10) == 8) {
				final Item log = new Item(ItemId.LOGS.id());
				p.getInventory().add(log);
				p.message("You get some wood");
			}
			p.teleport(obj.getX(), obj.getY());
			if (p.getY() > 871) {
				if (obj.getID() == JUNGLE_VINE)
					sleep(4000);
				p.message("You manage to hack your way into the Kharazi Jungle.");
			}
		} else {
			p.message("You slip and fail to hit the " + (obj.getID() == JUNGLE_VINE ? "jungle vines" : "tree"));
		}
		p.setBusy(false);
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		return obj.getID() == JUNGLE_VINE;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == JUNGLE_VINE) {
			handleJungleWoodcut(obj, p);
		}
	}

	/**
	 * How much of a bonus does the woodcut axe give?
	 */
	public int calcAxeBonus(int axeId) {
		int axeBonus = 0;
		switch (ItemId.getById(axeId)) {
			case BRONZE_AXE:
				axeBonus = 0;
				break;
			case IRON_AXE:
				axeBonus = 1;
				break;
			case STEEL_AXE:
				axeBonus = 2;
				break;
			case BLACK_AXE:
				axeBonus = 3;
				break;
			case MITHRIL_AXE:
				axeBonus = 4;
				break;
			case ADAMANTITE_AXE:
				axeBonus = 8;
				break;
			case RUNE_AXE:
				axeBonus = 16;
				break;
			default:
				break;
		}
		return axeBonus;
	}

	/**
	 * Should we get a log from the tree?
	 */
	private boolean getLog(int reqLevel, int woodcutLevel, int axeId) {
		return Formulae.calcGatheringSuccessful(reqLevel, woodcutLevel, calcAxeBonus(axeId));
	}
}
