package com.openrsc.server.plugins.custom.skills.harvesting;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.SceneryId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.content.EnchantedCrowns;
import com.openrsc.server.content.SkillCapes;
import com.openrsc.server.external.ObjectHarvestingDef;
import com.openrsc.server.model.TimePoint;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.custom.minigames.ABoneToPick;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.openrsc.server.plugins.Functions.*;

public final class Harvesting implements OpLocTrigger {

	enum HarvestingEvents {
		NEGLECTED(-1),
		NONE(0),
		WATER(1),
		SOIL(2);

		private final int id;

		HarvestingEvents(int id) {
			this.id = id;
		}

		public int getID() {
			return id;
		}
	}

	private static class ItemLevelXPTrio {
		private int itemId;
		private int level;
		private int xp;
		ItemLevelXPTrio(int itemId, int level, int xp) {
			this.itemId = itemId;
			this.level = level;
			this.xp = xp;
		}

		int getItemId() { return itemId; }

		int getLevel() {
			return level;
		}

		int getXp() {
			return xp;
		}
	}

	enum HerbsProduce {
		HERB(1274, new ItemLevelXPTrio(ItemId.UNIDENTIFIED_GUAM_LEAF.id(), 9, 50),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_MARRENTILL.id(),14, 60),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_TARROMIN.id(), 19, 72),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_HARRALANDER.id(), 26, 96),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_RANARR_WEED.id(), 32, 122),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_IRIT_LEAF.id(), 44, 194),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_AVANTOE.id(), 50, 246),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_KWUARM.id(), 56, 312),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_CADANTINE.id(), 67, 480),
			new ItemLevelXPTrio(ItemId.UNIDENTIFIED_DWARF_WEED.id(), 79, 768)),
		SEAWEED(1280, new ItemLevelXPTrio(ItemId.SEAWEED.id(), 23, 84),
			new ItemLevelXPTrio(ItemId.EDIBLE_SEAWEED.id(), 23, 84)),
		LIMPWURTROOT(1281, new ItemLevelXPTrio(ItemId.LIMPWURT_ROOT.id(), 42, 144)),
		SNAPEGRASS(1273, new ItemLevelXPTrio(ItemId.SNAPE_GRASS.id(), 61, 328));

		private int objId;
		private ArrayList<ItemLevelXPTrio> produceTable;
		HerbsProduce(int objId, ItemLevelXPTrio... produce) {
			this.objId = objId;
			produceTable = new ArrayList<>();
			produceTable.addAll(Arrays.asList(produce));
		}

		public static HerbsProduce find(int objId) {
			for (HerbsProduce h : HerbsProduce.values()) {
				if (h.objId == objId) {
					return h;
				}
			}
			return null;
		}

		public ItemLevelXPTrio get(int itemId) {
			for (ItemLevelXPTrio i : produceTable) {
				if (i.itemId == itemId) {
					return i;
				}
			}
			return null;
		}

	}

	private final int[] itemsFruitTree = new int[]{
		ItemId.LEMON.id(), ItemId.LIME.id(), ItemId.RED_APPLE.id(),
		ItemId.ORANGE.id(), ItemId.GRAPEFRUIT.id(),
	};

	private final int[] itemsRegPalm = new int[]{
		ItemId.BANANA.id(), ItemId.COCONUT.id(),
	};

	private final int[] itemsOtherPalm = new int[]{
		ItemId.PAPAYA.id(),
	};

	private final int[] itemsBush = new int[]{
		ItemId.REDBERRIES.id(), ItemId.CADAVABERRIES.id(), ItemId.DWELLBERRIES.id(),
		ItemId.JANGERBERRIES.id(), ItemId.WHITE_BERRIES.id(),
	};

	private final int[] itemsAllotments = new int[]{
		ItemId.CABBAGE.id(), ItemId.RED_CABBAGE.id(), ItemId.WHITE_PUMPKIN.id(),
		ItemId.POTATO.id(), ItemId.ONION.id(), ItemId.GARLIC.id()
	};

	private int chanceAskSoil = 5;
	private int chanceAskWatering = 7;

	public static int getTool(Player player, GameObject obj) {
		String objName = obj.getGameObjectDef().getName();
		int expectedTool;
		if (objName.toLowerCase().contains("tree") || objName.toLowerCase().contains("palm") || objName.toLowerCase().contains("pineapple")) {
			expectedTool = ItemId.FRUIT_PICKER.id();
		} else {
			expectedTool = ItemId.HAND_SHOVEL.id();
		}
		return player.getCarriedItems().hasCatalogID(expectedTool, Optional.of(false)) ? expectedTool : ItemId.NOTHING.id();
	}

	@Override
	public void onOpLoc(Player player, final GameObject object, String command) {
		// Harvest of Xmas Tree
		if (object.getID() == 1238) {
			startbatch(10);
			handleXmasHarvesting(player, object);
		} else if (object.getID() == SceneryId.PUMPKIN.id()) {
			if (ABoneToPick.getStage(player) != ABoneToPick.COMPLETED) {
				if (!config().A_BONE_TO_PICK) {
					mes("These aren't yours; you should probably leave them be");
				} else {
					ABoneToPick.pumpkinPatchDialogue(player);
				}
			} else {
				handleHarvesting(object, player, player.click);
			}
		} else if (command.equalsIgnoreCase("clip")) {
			handleClipHarvesting(object, player, player.click);
		} else {
			handleHarvesting(object, player, player.click);
		}
	}

	private void handleXmasHarvesting(Player player, GameObject object) {
		player.playerServerMessage(MessageType.QUEST, "You attempt to grab a present...");
		delay(4);

		final Item present = new Item(ItemId.PRESENT.id());
		if (getProduce(1, 99)) {
			//check if the tree still has gifts
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You fail to take from the tree");
				return;
			} else {
				player.getCarriedItems().getInventory().add(present);
				player.playerServerMessage(MessageType.QUEST, "You get a nice looking present");
			}
			if (DataConversions.random(1, 1000) <= 100) {
				obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				int depletedId = 1239;
				if (obj != null && obj.getID() == object.getID()) {
					GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), depletedId, object.getDirection(), object.getType());
					player.getWorld().replaceGameObject(object, newObject);
					player.getWorld().delayedSpawnObject(obj.getLoc(), 300 * 1000);
				}
				return;
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to take from the tree");
			if (!isbatchcomplete()) {
				GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			handleXmasHarvesting(player, object);
		}
	}

	private void handleClipHarvesting(final GameObject object, final Player player,
								  final int click) {
		if (!harvestingChecks(object, player)) return;

		GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
		final String objName = obj.getGameObjectDef().getName().toLowerCase();
		final HerbsProduce prodEnum = HerbsProduce.find(object.getID());
		int reqLevel = prodEnum != null ? prodEnum.produceTable.get(0).getLevel() : 1;

		if (!objName.contains("herb") && player.getSkills().getLevel(Skill.HARVESTING.id()) < reqLevel) {
			player.playerServerMessage(MessageType.QUEST, "You need at least level " + reqLevel
				+ " harvesting to clip from the " + objName);
			return;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.HERB_CLIPPERS.id(), Optional.of(false)) <= 0) {
			player.playerServerMessage(MessageType.QUEST,
				"You need some "
					+ player.getWorld().getServer().getEntityHandler()
					.getItemDef(ItemId.HERB_CLIPPERS.id())
					.getName().toLowerCase()
					+ " to clip from this havesting spot");
			return;
		}

		int repeat = 1;
		if (config().BATCH_PROGRESSION){
			repeat = Formulae.getRepeatTimes(player, Skill.HARVESTING.id());
		}
		startbatch(repeat);
		batchClipping(player, object, objName, prodEnum);
	}

	private void batchClipping(Player player, GameObject object, String objName, HerbsProduce prodEnum) {
		thinkbubble(new Item(ItemId.HERB_CLIPPERS.id()));
		player.playerServerMessage(MessageType.QUEST, "You attempt to clip from the spot...");
		delay(4);

		// herb uses herb drop table
		// seaweed 1/4 chance to be edible
		int prodId = !objName.contains("herb")
			? (objName.contains("sea weed") && DataConversions.random(1, 4) == 1 ? prodEnum.produceTable.get(1).getItemId()
			: prodEnum.produceTable.get(0).getItemId() ) : Formulae.calculateHerbDrop();
		int reqLevel = prodEnum.produceTable.get(0).getLevel();
		final Item produce = new Item(prodId);
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST, "You are too tired to get produce");
				return;
			}
		}
		if (!objName.contains("herb") && player.getSkills().getLevel(Skill.HARVESTING.id()) < reqLevel) {
			player.playerServerMessage(MessageType.QUEST, "You need at least level " + reqLevel
				+ " harvesting to clip from the " + objName);
			return;
		}

		if (getProduce(prodEnum.get(prodId).getLevel(), player.getSkills().getLevel(Skill.HARVESTING.id()))) {
			//check if the object is still up
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You fail to clip the plant");
				return;
			} else {
				player.getCarriedItems().getInventory().add(produce);
				player.playerServerMessage(MessageType.QUEST, "You get " + (objName.contains("herb") ? "a herb"
					: "some " + (objName.contains(" ") ? objName.substring(objName.lastIndexOf(" ") + 1) : "produce")));

				if (SkillCapes.shouldActivate(player, ItemId.HARVESTING_CAPE)) {
					player.playerServerMessage(MessageType.QUEST, "@or2@Your Harvesting cape activates, yielding double produce");
					player.getCarriedItems().getInventory().add(produce);
				}

				player.incExp(Skill.HARVESTING.id(), prodEnum.get(prodId).getXp(), true);

				if (EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_THE_ITEMS)) {
					player.playerServerMessage(MessageType.QUEST, "Your crown shines and an extra item appears on the ground");
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), produce.getCatalogId(), player.getX(), player.getY(), 1, player), player.getConfig().GAME_TICK * 50);
					EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_THE_ITEMS);
				}
			}
			if (DataConversions.random(1, 100) <= (!objName.contains("herb") ? 20 : 10)) {
				obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				int depId = 1270;
				if (obj != null && obj.getID() == object.getID()) {
					GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), depId, object.getDirection(), object.getType());
					player.getWorld().replaceGameObject(object, newObject);
					player.getWorld().delayedSpawnObject(obj.getLoc(), DataConversions.random(60, 240) * 1000);
				}
				return;
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to clip the plant");
			if (!isbatchcomplete()) {
				GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			batchClipping(player, object, objName, prodEnum);
		}
	}

	private void handleHarvesting(final GameObject object, final Player player, final int click) {
		if (!harvestingChecks(object, player)) {
			player.message("I can't get close enough.");
			return;
		}

		final ObjectHarvestingDef def = player.getWorld().getServer().getEntityHandler().getObjectHarvestingDef(object.getID());

		final int toolId = getTool(player, object);

		int repeat = 1;
		if (config().BATCH_PROGRESSION){
			repeat = Formulae.getRepeatTimes(player, Skill.HARVESTING.id());
		}
		startbatch(repeat);
		batchHarvest(player, toolId, object, def);
	}

	private void batchHarvest(Player player, int toolId, GameObject object, ObjectHarvestingDef def) {
		final AtomicInteger evt = new AtomicInteger(checkCare(object, player));
		if (toolId != ItemId.NOTHING.id()) thinkbubble(new Item(toolId));
		player.playerServerMessage(MessageType.QUEST, "You attempt to get some produce...");
		delay(4);

		// Player is on Death Island
		if (player.getConfig().DEATH_ISLAND && player.getX() > 957 && player.getX() < 1000 && player.getY() > 153 && player.getY() < 190) {
			ActionSender.sendRemoveProgressBar(player);
			switch (object.getID()) {
				case 1264: // Pumpkin
					player.playerServerMessage(MessageType.QUEST, "@whi@Death: Hey, those are my pumpkins!");
					break;
				case 1266: // Onion
					player.playerServerMessage(MessageType.QUEST, "@whi@Death: Hey, those are my onions!");
					break;
				case 1256: // Redberry bush
					player.playerServerMessage(MessageType.QUEST, "@whi@Death: Hey, those are my redberries!");
					break;
				default:
					player.playerServerMessage(MessageType.QUEST, "@whi@Death: Hey, that's my produce!");
					break;
			}
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "@whi@Death: Don't you know how rude it is to just harvest someone else's crops?");
			delay(5);
			player.playerServerMessage(MessageType.QUEST, "@yel@" + player.getUsername() + ": Why are you growing White Pumpkins?");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "@yel@" + player.getUsername() + ": The pies won't be orange if you use those.");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "@whi@Death: I can't actually figure out how to grow the orange ones");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "@whi@Death: But I can get the right colour by dyeing it.");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "@yel@" + player.getUsername() + ": Please tell me you don't put onions and redberries in your pumpkin pies.");
			delay(3);
			player.playerServerMessage(MessageType.QUEST, "@whi@Death: Haven't got a complaint yet! You only need a little to dye it.");
			return;
		}

		final Item produce = new Item(def.getProdId());
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 1
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.playerServerMessage(MessageType.QUEST, "You are too tired to get produce");
				return;
			}
		}
		if (player.getSkills().getLevel(Skill.HARVESTING.id()) < def.getReqLevel()) {
			player.playerServerMessage(MessageType.QUEST,"You need a harvesting level of " + def.getReqLevel() + " to get produce from here");
			return;
		}

		if (toolId == ItemId.NOTHING.id() && DataConversions.random(0, 1) == 1) {
			player.playerServerMessage(MessageType.QUEST, "You accidentally damage the produce and throw it away");
		} else if (evt.get() == HarvestingEvents.NEGLECTED.getID()) {
			player.playerServerMessage(MessageType.QUEST, "But the spot seems weak, you decide to wait");
		} else if (getProduce(def.getReqLevel(), player.getSkills().getLevel(Skill.HARVESTING.id()))) {
			//check if the object is still up
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			if (obj == null) {
				player.playerServerMessage(MessageType.QUEST, "You fail to obtain some usable produce");
				return;
			} else {
				String itemName = produce.getDef(player.getWorld()).getName().toLowerCase();
				player.getCarriedItems().getInventory().add(produce);
				// if player did soil (or have an active one) they get small chance for another produce
				if (DataConversions.random(1, chanceAskSoil * 3) == 1
					&& evt.get() == HarvestingEvents.SOIL.getID()) {
					player.getCarriedItems().getInventory().add(produce);
				}
				player.playerServerMessage(MessageType.QUEST, "You get " +
					(itemName.endsWith("s") ? "some " : (startsWithVowel(itemName) ? "an " : "a ")) + itemName);

				if (SkillCapes.shouldActivate(player, ItemId.HARVESTING_CAPE)) {
					player.playerServerMessage(MessageType.QUEST, "@or2@Your Harvesting cape activates, yielding a second " + itemName);
					player.getCarriedItems().getInventory().add(produce);
				}

				player.incExp(Skill.HARVESTING.id(), def.getExp(), true);

				if (EnchantedCrowns.shouldActivate(player, ItemId.CROWN_OF_THE_ITEMS)) {
					player.playerServerMessage(MessageType.QUEST, "Your crown shines and an extra item appears on the ground");
					player.getWorld().registerItem(
						new GroundItem(player.getWorld(), produce.getCatalogId(), player.getX(), player.getY(), 1, player), player.getConfig().GAME_TICK * 50);
					EnchantedCrowns.useCharge(player, ItemId.CROWN_OF_THE_ITEMS);
				}
			}
			if (DataConversions.random(1, 100) <= def.getExhaust()) {
				obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				int depId = 1270;
				int prodId = def.getProdId();
				if (DataConversions.inArray(itemsFruitTree, prodId)) {
					depId = 1252; //exhausted tree
				} else if (DataConversions.inArray(itemsRegPalm, prodId)) {
					depId = 1253; //exhausted palm
				} else if (DataConversions.inArray(itemsOtherPalm, prodId)) {
					depId = 1254; //exhausted palm2
				} else if (prodId == ItemId.FRESH_PINEAPPLE.id()) {
					depId = 1255; //exhausted pineapple
				} else if (DataConversions.inArray(itemsBush, prodId)) {
					depId = 1261; //depleted bush
				} else if (prodId == ItemId.TOMATO.id()) {
					depId = 1271; //depleted tomato
				} else if (prodId == ItemId.CORN.id()) {
					depId = 1272; //depleted corn
				} else if (prodId == ItemId.DRAGONFRUIT.id()) {
					depId = 1294; //depleted dragonfruit
				}
				if (obj != null && obj.getID() == object.getID()) {
					// if player did water (or have an active one) they get small chance not to deplete node
					if (DataConversions.random(1, chanceAskWatering * 3) == 1
						&& evt.get() == HarvestingEvents.WATER.getID()) {
					}
					else if (def.getRespawnTime() > 0) {
						GameObject newObject = new GameObject(player.getWorld(), object.getLocation(), depId, object.getDirection(), object.getType());
						player.getWorld().replaceGameObject(object, newObject);
						player.getWorld().delayedSpawnObject(obj.getLoc(), def.getRespawnTime() * 1000);
					}
				}
				return;
			}
		} else {
			player.playerServerMessage(MessageType.QUEST, "You fail to obtain some usable produce");
			if (!isbatchcomplete()) {
				GameObject checkObj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
				if (checkObj == null) {
					return;
				}
			}
		}

		// Repeat
		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			GameObject obj = player.getViewArea().getGameObject(object.getID(), object.getX(), object.getY());
			batchHarvest(player, toolId, obj, def);
		}
	}

	private boolean harvestingChecks(final GameObject obj, final Player player) {
		boolean canReach = player.withinRange(obj, 1);
		return canReach;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return command.equalsIgnoreCase("harvest") ||
			command.equalsIgnoreCase("clip") || (command.equals("collect") && obj.getID() == 1238);
	}

	private int checkCare(GameObject obj, Player player) {
		long timestamp = System.currentTimeMillis() + 3 * 60000;
		if (DataConversions.random(1, chanceAskWatering) == 1) {
			if (player.getAttribute("watered", null) == null
				|| expiredAction(obj, player, "watered")) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.WATERING_CAN.id(), Optional.of(false))) {
					return HarvestingEvents.NEGLECTED.getID();
				}
				player.playerServerMessage(MessageType.QUEST, "You water the harvesting spot");
				player.setAttribute("watered", new TimePoint(obj.getX(), obj.getY(), timestamp));
				updateUsesWateringCan(player);
			}
			return HarvestingEvents.WATER.getID();
		} else if (DataConversions.random(1, chanceAskSoil) == 1) {
			if (player.getAttribute("soiled", null) == null
				|| expiredAction(obj, player, "soiled")) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.SOIL.id(), Optional.of(false))) {
					return HarvestingEvents.NEGLECTED.getID();
				}
				player.playerServerMessage(MessageType.QUEST, "You add soil to the spot");
				player.setAttribute("soiled", new TimePoint(obj.getX(), obj.getY(), timestamp));
				player.getCarriedItems().remove(new Item(ItemId.SOIL.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.BUCKET.id()));
			}
			return HarvestingEvents.SOIL.getID();
		}
		return HarvestingEvents.NONE.getID();
	}

	private void updateUsesWateringCan(Player player) {
		if (!player.getCache().hasKey("uses_wcan")) {
			player.getCache().set("uses_wcan", 1);
		} else {
			int uses = player.getCache().getInt("uses_wcan");
			if (uses >= 4) {
				player.getCarriedItems().remove(new Item(ItemId.WATERING_CAN.id()));
				player.getCarriedItems().getInventory().add(new Item(ItemId.EMPTY_WATERING_CAN.id()));
				player.getCache().remove("uses_wcan");
			} else {
				player.getCache().put("uses_wcan", uses + 1);
			}
		}
	}

	private boolean expiredAction(GameObject obj, Player player, String key) {
		Object testObj = player.getAttribute(key);
		if (!(testObj instanceof TimePoint)) {
			return true;
		} else {
			TimePoint tp = (TimePoint) testObj;
			//expired or from distinct place
			return System.currentTimeMillis() - tp.getTimestamp() > 0 || !obj.getLocation().equals(tp.getLocation());
		}
	}

	private boolean getProduce(int reqLevel, int harvestingLevel) {
		return Formulae.calcGatheringSuccessfulLegacy(reqLevel, harvestingLevel, 0);
	}
}
