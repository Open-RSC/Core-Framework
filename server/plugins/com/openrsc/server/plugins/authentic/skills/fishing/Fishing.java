package com.openrsc.server.plugins.authentic.skills.fishing;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.ObjectFishDef;
import com.openrsc.server.external.ObjectFishingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Fishing implements OpLocTrigger {
	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Fishing.class);

	private ObjectFishDef getFish(ObjectFishingDef objectFishDef, int fishingLevel) {
		ArrayList<ObjectFishDef> fish = new ArrayList<ObjectFishDef>();

		for (ObjectFishDef def : objectFishDef.getFishDefs()) {
			if (fishingLevel >= def.getReqLevel() && Formulae.calcGatheringSuccessful(def.getReqLevel(), fishingLevel)) {
				fish.add(def);
			}
		}
		if (fish.size() <= 0) {
			return null;
		}
		return fish.get(DataConversions.random(0, fish.size() - 1));
	}

	@Override
	public void onOpLoc(Player player, final GameObject object, String command) {
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			handleFishing(object, player, player.click, command);
		}
	}

	private void handleFishing(final GameObject object, Player player, final int click, final String command) {
		final ObjectFishingDef def = player.getWorld().getServer().getEntityHandler().getObjectFishingDef(object.getID(), click);

		if (!player.withinRange(object, 1)) {
			return;
		}
		if (def == null) { // This shouldn't happen
			return;
		}
		if (checkFatigue(player)) return;

		if (object.getID() == 493 && player.getSkills().getExperience(Skills.FISHING) >= 200) {
			mes("that's enough fishing for now");
			delay(3);
			mes("go through the next door to continue the tutorial");
			delay(3);
			return;
		}
		if (player.getSkills().getLevel(Skills.FISHING) < def.getReqLevel(player.getWorld())) {
			player.playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel(player.getWorld()) + " "
				+ fishingRequirementString(object, command) + " "
				+ (!command.contains("cage") ? "these fish"
				: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
				.substring(4) + "s"));
			return;
		}
		final int netId = def.getNetId();
		if (player.getCarriedItems().getInventory().countId(netId, Optional.of(false)) <= 0) {
			player.playerServerMessage(MessageType.QUEST,
				"You need a "
					+ player.getWorld().getServer().getEntityHandler()
					.getItemDef(
						netId)
					.getName().toLowerCase()
					+ " to " + (command.equals("lure") || command.equals("bait") ? command : def.getBaitId() > 0 ? "bait" : "catch") + " "
					+ (!command.contains("cage") ? "these fish"
					: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
					.substring(4) + "s"));
			return;
		}
		final int baitId = def.getBaitId();
		if (baitId >= 0) {
			if (player.getCarriedItems().getInventory().countId(baitId, Optional.of(false)) <= 0) {
				player.playerServerMessage(MessageType.QUEST,
					"You don't have any " + player.getWorld().getServer().getEntityHandler().getItemDef(baitId).getName().toLowerCase() + " left");
				return;
			}
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skills.FISHING);
		}

		startbatch(repeat);
		batchFishing(player, netId, def, object, command);
	}

	private void batchFishing(Player player, int netId, ObjectFishingDef def, GameObject object, String command) {
		player.playerServerMessage(MessageType.QUEST, "You attempt to catch " + tryToCatchFishString(def));
		player.playSound("fish");
		thinkbubble(new Item(netId));
		delay(4);

		if (player.getSkills().getLevel(Skills.FISHING) < def.getReqLevel(player.getWorld())) {
			player.playerServerMessage(MessageType.QUEST, "You need at least level " + def.getReqLevel(player.getWorld()) + " "
				+ fishingRequirementString(object, command) + " "
				+ (!command.contains("cage") ? "these fish"
				: player.getWorld().getServer().getEntityHandler().getItemDef(def.getFishDefs()[0].getId()).getName().toLowerCase()
				.substring(4) + "s"));
			return;
		}
		final int baitId = def.getBaitId();
		if (baitId >= 0) {
			if (player.getCarriedItems().getInventory().countId(baitId, Optional.of(false)) <= 0) {
				player.playerServerMessage(MessageType.QUEST, "You don't have any " + player.getWorld().getServer().getEntityHandler().getItemDef(baitId).getName().toLowerCase()
					+ " left");
				return;
			}
		}

		if (checkFatigue(player)) return;

		List<ObjectFishDef> fishLst = new ArrayList<ObjectFishDef>();
		ObjectFishDef aFishDef = getFish(def, player.getSkills().getLevel(Skills.FISHING));
		if (aFishDef != null) fishLst.add(aFishDef);
		if (fishLst.size() > 0) {
			//check if the spot is still active
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
				return;
			} else {
				if (baitId >= 0) {
					int idx = player.getCarriedItems().getInventory().getLastIndexById(baitId, Optional.of(false));
					Item bait = player.getCarriedItems().getInventory().get(idx);
					if (bait == null) return;
					player.getCarriedItems().remove(new Item(bait.getCatalogId(), 1, false, bait.getItemId()));
				}
				if (netId == ItemId.BIG_NET.id()) {
					//big net spot may get 4 items but 1 already gotten
					int max = bigNetRand() - 1;
					for (int i = 0; i < max; i++) {
						aFishDef = getFish(def, player.getSkills().getLevel(Skills.FISHING));
						if (aFishDef != null) fishLst.add(aFishDef);
					}
					if (DataConversions.random(0, 200) == 100) {
						player.playerServerMessage(MessageType.QUEST, "You catch a casket");
						give(player, ItemId.CASKET.id(), 1);
						player.incExp(Skills.FISHING, 40, true);
					}
					for (Iterator<ObjectFishDef> iter = fishLst.iterator(); iter.hasNext();) {
						ObjectFishDef fishDef = iter.next();
						Item fish = new Item(fishDef.getId());
						player.playerServerMessage(MessageType.QUEST, "You catch " + (fish.getCatalogId() == ItemId.BOOTS.id() || fish.getCatalogId() == ItemId.SEAWEED.id() || fish.getCatalogId() == ItemId.LEATHER_GLOVES.id() ? "some" : fish.getCatalogId() == ItemId.OYSTER.id() ? "an" : "a") + " "
							+ fish.getDef(player.getWorld()).getName().toLowerCase().replace("raw ", "").replace("leather ", "") + (fish.getCatalogId() == ItemId.OYSTER.id() ? " shell" : ""));
						player.getCarriedItems().getInventory().add(fish);
						player.incExp(Skills.FISHING, fishDef.getExp(), true);
					}
				} else {
					Item fish = new Item(fishLst.get(0).getId());
					// Skill cape perk. Will convert a shark to either a manta ray or a turtle.
					String capeColor = "";
					if (fish.getCatalogId() == ItemId.RAW_SHARK.id()) {
						Item newFish = new Item(SkillCapes.shouldActivateInt(player, ItemId.FISHING_CAPE));
						if (newFish.getCatalogId() != -1) {
							player.getBank().add(newFish, false);
							capeColor = "@dcy@";
							player.playerServerMessage(MessageType.QUEST, capeColor + "Because of your prowess in fishing");
						}
					}
					player.playerServerMessage(MessageType.QUEST, capeColor + "You catch " + (netId == ItemId.NET.id() ? "some" : "a") + " "
						+ fish.getDef(player.getWorld()).getName().toLowerCase().replace("raw ", "") + (fish.getCatalogId() == ItemId.RAW_SHRIMP.id() ? "s" : "")
						+ (fish.getCatalogId() == ItemId.RAW_SHARK.id() ? "!" : ""));
					player.getCarriedItems().getInventory().add(fish);
					player.incExp(Skills.FISHING, fishLst.get(0).getExp(), true);
					if (object.getID() == 493 && player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 41)
						player.getCache().set("tutorial", 42);
				}
			}
			if (config().FISHING_SPOTS_DEPLETABLE && DataConversions.random(1, 250) <= def.getDepletion()) {
				obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (obj != null && obj.getID() == object.getID() && def.getRespawnTime() > 0) {
					GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), 668, object.getDirection(), object.getType());
					player.getWorld().replaceGameObject(object, newObject);
					player.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * config().GAME_TICK, true);
				}
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to catch anything");
			if (object.getID() == 493 && player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 41) {
				player.message("keep trying, you'll catch something soon");
			}
			if (object.getID() != 493 && !ifbatchcompleted()) {
				GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			delay();
			batchFishing(player, netId, def, object, command);
		}
	}

	private int bigNetRand() {
		int roll = DataConversions.random(0, 30);
		if (roll <= 23) {
			return 1;
		} else if (roll <= 27) {
			return 2;
		} else if (roll <= 29) {
			return 3;
		} else {
			return 4;
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		//special hemenster fishing spots
		if (obj.getID() == 351 || obj.getID() == 352 || obj.getID() == 353 || obj.getID() == 354)
			return false;
		if (command.equals("lure") || command.equals("bait") || command.equals("net") || command.equals("harpoon")
			|| command.equals("cage")) {
			return true;
		}
		return false;
	}

	private String fishingRequirementString(GameObject obj, String command) {
		String name = "";
		if (command.equals("bait")) {
			name = "fishing to bait";
		} else if (command.equals("lure")) {
			name = "fishing to lure";
		} else if (command.equals("net")) {
			name = "fishing to net";
		} else if (command.equals("harpoon")) {
			name = "fishing to harpoon";
		} else if (command.equals("cage")) {
			name = "fishing to catch";
		}
		return name;
	}

	private String tryToCatchFishString(ObjectFishingDef def) {
		String name = "";
		if (def.getNetId() == ItemId.NET.id()) {
			name = "some fish";
		} else if (def.getNetId() == ItemId.LOBSTER_POT.id()) {
			name = "a lobster";
		} else {
			name = "a fish";
		}
		return name;
	}

	private boolean checkFatigue(Player player) {
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST,"You are too tired to catch this fish");
				return true;
			}
		}
		return false;
	}
}
