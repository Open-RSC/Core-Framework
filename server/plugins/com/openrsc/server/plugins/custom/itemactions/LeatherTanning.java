package com.openrsc.server.plugins.custom.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.SceneryId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Arrays;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LeatherTanning implements UseInvTrigger,
	UseLocTrigger {

	private final int[] rawMeatList = new int[]{
		ItemId.RAW_BEEF.id(),
		ItemId.RAW_BEAR_MEAT.id(),
		ItemId.RAW_RAT_MEAT.id()
	};

	private final int[] leanMeatList = new int[] {
		ItemId.RAW_CHICKEN.id(),
		ItemId.LEAN_BEEF.id(),
		ItemId.LEAN_BEAR_MEAT.id(),
		ItemId.LEAN_RAT_MEAT.id()
	};

	private final int[] furnaceList = new int[]{
		SceneryId.FURNACE.id(),
		SceneryId.FURNACE_UNDERGROUND_PASS.id(),
		SceneryId.POTTERY_OVEN.id(),
		SceneryId.LAVA_FORGE.id()
	};

	private final int[] rangeList = new int[]{
		SceneryId.RANGE.id(),
		SceneryId.COOKS_RANGE.id(),
		SceneryId.RANGE_CARNILLEAN.id(),
		SceneryId.RANGE_TUTORIAL_ISLAND.id()
	};

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		// Using a hammer on some cow hide -> check for animal fat -> consume the animal fat, replace the cow hide with treated hide
		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		boolean hasHammer = player.getCarriedItems().getInventory().contains(new Item(ItemId.HAMMER.id()));
		if (item1ID == ItemId.HAMMER.id() && item2ID == ItemId.COW_HIDE.id()) {
			makeTreatedHide(player, item2);
		} else if (item2ID == ItemId.HAMMER.id() && item1ID == ItemId.COW_HIDE.id()) {
			makeTreatedHide(player, item1);
		} else if (item1ID == ItemId.KNIFE.id() && Arrays.stream(rawMeatList).anyMatch(x -> x == item2ID)) {
			trimFatOffMeat(player, item2);
		} else if (item2ID == ItemId.KNIFE.id() && Arrays.stream(rawMeatList).anyMatch(x -> x == item1ID)) {
			trimFatOffMeat(player, item1);
		} else if (item1ID == ItemId.KNIFE.id() && Arrays.stream(leanMeatList).anyMatch(x -> x == item2ID)) {
			trimFatOffLeanMeat(player, item2);
		} else if (item2ID == ItemId.KNIFE.id() && Arrays.stream(leanMeatList).anyMatch(x -> x == item1ID)) {
			trimFatOffLeanMeat(player, item1);
		} else if (item1ID == ItemId.ANIMAL_FAT.id() && item2ID == ItemId.COW_HIDE.id() && hasHammer) {
			makeTreatedHide(player, item2);
		} else if (item2ID == ItemId.ANIMAL_FAT.id() && item1ID == ItemId.COW_HIDE.id() && hasHammer) {
			makeTreatedHide(player, item1);
		} else if (item1ID == ItemId.ANIMAL_FAT.id() && item2ID == ItemId.COW_HIDE.id() && !hasHammer) {
			player.message("you need a hammer to do that");
		} else if (item2ID == ItemId.ANIMAL_FAT.id() && item1ID == ItemId.COW_HIDE.id() && !hasHammer) {
			player.message("you need a hammer to do that");
		} else if (item1ID == ItemId.HAMMER.id() && item2ID == ItemId.ANIMAL_FAT.id()) {
			player.getCarriedItems().remove(item2);
			player.message("you smash the animal fat, sending it everywhere. yuck");
		} else if (item2ID == ItemId.HAMMER.id() && item1ID == ItemId.ANIMAL_FAT.id()) {
			player.getCarriedItems().remove(item1);
			player.message("you smash the animal fat, sending it everywhere. yuck");
		}
	}


	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (!player.getConfig().WANT_CUSTOM_LEATHER) {
			return false;
		}

		int item1ID = item1.getCatalogId();
		int item2ID = item2.getCatalogId();
		if (compareItemsIds(item1, item2, ItemId.COW_HIDE.id(), ItemId.HAMMER.id())) {
			return true;
		} else if (compareItemsIds(item1, item2, ItemId.COW_HIDE.id(), ItemId.ANIMAL_FAT.id())) {
			return true;
		} else if (compareItemsIds(item1, item2, ItemId.HAMMER.id(), ItemId.ANIMAL_FAT.id())) {
			return true;
		} else if (item1ID == ItemId.KNIFE.id() && Arrays.stream(rawMeatList).anyMatch(x -> x == item2ID)) {
			return true;
		} else if (item2ID == ItemId.KNIFE.id() && Arrays.stream(rawMeatList).anyMatch(x -> x == item1ID)) {
			return true;
		} else if (item1ID == ItemId.KNIFE.id() && Arrays.stream(leanMeatList).anyMatch(x -> x == item2ID)) {
			return true;
		} else if (item2ID == ItemId.KNIFE.id() && Arrays.stream(leanMeatList).anyMatch(x -> x == item1ID)) {
			return true;
		}
		return false;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		// Using the treated hide on a fire -> consume the treated hide, replace with leather
		if (item.getCatalogId() == ItemId.TREATED_HIDE.id() && Arrays.stream(furnaceList).anyMatch(x -> x == obj.getID())) {
			player.message("the furnace is too hot and will damage the hide");
			player.message("a fire would be best to dry it");
			return;
		}
		if (item.getCatalogId() == ItemId.TREATED_HIDE.id() && Arrays.stream(rangeList).anyMatch(x -> x == obj.getID())) {
			player.message("the range is too hot and will damage the hide");
			player.message("a fire would be best to dry it");
			return;
		}
		if (!checkTreatedHideOnFire(obj, item, player)) return;
		beginTanningHide(item, player);
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		if (item.getCatalogId() == ItemId.TREATED_HIDE.id() && Arrays.stream(rangeList).anyMatch(x -> x == obj.getID())) {
			return true;
		}

		if (item.getCatalogId() == ItemId.TREATED_HIDE.id() && Arrays.stream(furnaceList).anyMatch(x -> x == obj.getID())) {
			return true;
		}

		return checkTreatedHideOnFire(obj, item, player);
	}

	private boolean checkTreatedHideOnFire(final GameObject obj, final Item item, final Player player) {
		// Check that the treated hide isn't noted
		if (item.getItemStatus().getNoted()) return false;

		boolean isFire = obj.getID() == SceneryId.FIRE.id() || obj.getID() == SceneryId.FIREPLACE.id();
		boolean isTreatedHide = item.getCatalogId() == ItemId.TREATED_HIDE.id();
		if (!isFire || !isTreatedHide) return false;

		// Check that the player is within range of the Fire
		return player.withinRange(obj, 1);
	}

	private void beginTanningHide(final Item item, final Player player) {
		if (checkFatigue(player)) return;

		Item treatedHide = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);

		if (treatedHide == null) return;

		int repeat = 1;
		if (player.getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(treatedHide.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		batchTanningHide(treatedHide, player);
	}

	private void batchTanningHide(final Item hide, final Player player) {

		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(hide.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		player.getCarriedItems().remove(item);

		thinkbubble(item);
		player.getCarriedItems().remove(item);
		player.playerServerMessage(MessageType.QUEST, "You let the treated hide dry in the fire");
		delay(3);
		player.getCarriedItems().getInventory().add(new Item(ItemId.LEATHER.id(), 1));
		player.incExp(Skill.CRAFTING.id(), 25, true);

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchTanningHide(item, player);
		}
	}

	private void makeTreatedHide(Player player, final Item hide) {
		if (hide.getCatalogId() != ItemId.COW_HIDE.id()) {
			player.message("Nothing interesting happens");
			return;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.ANIMAL_FAT.id(), Optional.of(false)) < 1) {
			player.message("You need some animal fat to treat the hide");
			return;
		}

		int repeat = 1;
		if (player.getConfig().BATCH_PROGRESSION) {
			repeat = Math.min(
				player.getCarriedItems().getInventory().countId(hide.getCatalogId(), Optional.of(false)),
				player.getCarriedItems().getInventory().countId(ItemId.ANIMAL_FAT.id(), Optional.of(false))
			);
		}

		startbatch(repeat);
		batchTreatedHide(player, hide);
	}

	private void batchTreatedHide(Player player, Item hide) {
		// Check for animal fat again
		if (player.getCarriedItems().getInventory().countId(ItemId.ANIMAL_FAT.id(), Optional.of(false)) < 1) {
			player.message("You need some animal fat to treat the hide");
			return;
		}

		// Resulting item
		Item treatedHide = new Item(ItemId.TREATED_HIDE.id(), 1);

		// Make sure player can receive the result
		if (!canReceive(player, treatedHide)) {
			player.message("Your client does not support the desired object");
			return;
		}

		// Fatigue check
		if (checkFatigue(player)) return;

		// Get the hide in their inventory
		Item item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(hide.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		player.getCarriedItems().remove(item);
		delay();
		player.message("You beat the animal fat into the hide");
		player.getCarriedItems().getInventory().add(treatedHide);
		player.getCarriedItems().remove(new Item(ItemId.ANIMAL_FAT.id()));
		player.incExp(Skill.CRAFTING.id(), 10, true);

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchTreatedHide(player, hide);
		}
	}

	private void trimFatOffLeanMeat(Player player, Item item) {
		player.message("This meat is too lean to trim any fat off");
	}

	private void trimFatOffMeat(Player player, Item item) {
		int repeat = 1;
		if (player.getConfig().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
		}

		startbatch(repeat);
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchTrimFat(player, item);
		}
	}

	private void batchTrimFat(Player player, Item item) {
		int itemId = item.getCatalogId();

		Item animalFat = new Item(ItemId.ANIMAL_FAT.id(), 1);

		// Resulting meat
		Item leanMeat;
		if (itemId == ItemId.RAW_BEEF.id()) {
			leanMeat = new Item(ItemId.LEAN_BEEF.id(), 1);
		} else if (itemId == ItemId.RAW_BEAR_MEAT.id()) {
			leanMeat = new Item(ItemId.LEAN_BEAR_MEAT.id(), 1);
		} else if (itemId == ItemId.RAW_RAT_MEAT.id()) {
			leanMeat = new Item(ItemId.LEAN_RAT_MEAT.id(), 1);
		} else {
			return;
		}

		// Make sure player can receive the results
		if (!canReceive(player, leanMeat) || !canReceive(player, animalFat)) {
			player.message("Your client does not support the desired object");
			return;
		}

		// Get the raw meat in their inventory
		Item meat = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (meat == null) return;

		player.getCarriedItems().remove(meat);
		delay();
		player.message("You carefully trim the fat off the meat");
		player.getCarriedItems().getInventory().add(animalFat);
		player.getCarriedItems().getInventory().add(leanMeat);

		updatebatch();
		if (!ifinterrupted() && !isbatchcomplete()) {
			delay();
			batchTrimFat(player, item);
		}
	}

	private boolean checkFatigue(Player player) {
		if (player.getConfig().WANT_FATIGUE
			&& player.getConfig().STOP_SKILLING_FATIGUED >= 2
			&& player.getFatigue() >= player.MAX_FATIGUE) {
			player.message("You are too tired to craft");
			return true;
		}
		return false;
	}
}
