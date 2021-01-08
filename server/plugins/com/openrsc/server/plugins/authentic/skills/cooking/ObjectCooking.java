package com.openrsc.server.plugins.authentic.skills.cooking;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.external.ItemCookingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Arrays;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ObjectCooking implements UseLocTrigger {
	@Override
	public void onUseLoc(Player owner, GameObject object, Item item) {
		Npc cook = ifnearvisnpc(owner, NpcId.COOK.id(), 20);
		if (cook != null && owner.getQuestStage(Quests.COOKS_ASSISTANT) != -1
			&& object.getID() == 119) {
			npcsay(owner, cook, "Hey! Who said you could use that?");
		} else
			handleCooking(item, owner, object);
	}

	private void handleCooking(final Item item, Player player,
							   final GameObject object) {

		// Tutorial Meat
		if (object.getID() == 491) {
			if (item.getCatalogId() == ItemId.RAW_RAT_MEAT.id()) {
				thinkbubble(item);
				player.playSound("cooking");
				player.playerServerMessage(MessageType.QUEST, "You cook the meat on the stove...");
				if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 25) {
					player.playerServerMessage(MessageType.QUEST, "You accidentally burn the meat");
					player.getCarriedItems().remove(new Item(ItemId.RAW_RAT_MEAT.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BURNTMEAT.id()));
					delay();
					mes("sometimes you will burn food");
					delay(3);
					mes("As your cooking level increases this will happen less");
					delay(3);
					mes("Now speak to the cooking instructor again");
					delay(3);
					player.getCache().set("tutorial", 30);
				} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 30) {
					final ItemCookingDef cookingDef = item.getCookingDef(player.getWorld());
					player.playerServerMessage(MessageType.QUEST, "The meat is now nicely cooked");
					mes("Now speak to the cooking instructor again");
					delay(3);
					player.incExp(Skills.COOKING, cookingDef.getExp(), true);
					player.getCache().set("tutorial", 31);
					player.getCarriedItems().remove(new Item(ItemId.RAW_RAT_MEAT.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.COOKEDMEAT.id()));

				} else {
					//per-wiki says rest of meats are burned
					player.playerServerMessage(MessageType.QUEST, "You accidentally burn the meat");
					player.getCarriedItems().remove(new Item(ItemId.RAW_RAT_MEAT.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.BURNTMEAT.id()));
				}
			} else {
				player.message("Nothing interesting happens");
			}
			return;
		}

		// Raw Oomlie Meat (Always burn)
		else if (item.getCatalogId() == ItemId.RAW_OOMLIE_MEAT.id()) {
			if (object.getID() == 97 || object.getID() == 274) {
				mes("You cook the meat on the fire...");
				delay(3);
			}
			else {
				mes("You cook the meat on the stove...");
				delay(3);
			}
			delay(2);
			player.getCarriedItems().remove(new Item(ItemId.RAW_OOMLIE_MEAT.id()));
			give(player, ItemId.BURNTMEAT.id(), 1);
			mes("This meat is too delicate to cook like this.");
			delay(2);
			mes("Perhaps you can wrap something around it to protect it from the heat.");
			delay(2);
		}

		// Poison (Hazeel Cult)
		else if (item.getCatalogId() == ItemId.POISON.id() && object.getID() == 435 && object.getX() == 618 && object.getY() == 3453) {
			if (player.getQuestStage(Quests.THE_HAZEEL_CULT) == 3 && player.getCache().hasKey("evil_side")) {
				mes("you poor the poison into the hot pot");
				delay(3);
				mes("the poison desolves into the soup");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.POISON.id()));
				player.updateQuestStage(Quests.THE_HAZEEL_CULT, 4);
			} else {
				player.message("nothing interesting happens");
			}
		} else if (item.getCatalogId() == ItemId.UNCOOKED_SWAMP_PASTE.id()) {
			cookMethod(player, ItemId.UNCOOKED_SWAMP_PASTE.id(), ItemId.SWAMP_PASTE.id(), false, "you warm the paste over the fire", "it thickens into a sticky goo");
		} else if (item.getCatalogId() == ItemId.SEAWEED.id()) { // Seaweed (Glass)
			cookMethod(player, ItemId.SEAWEED.id(), ItemId.SODA_ASH.id(), true, "You put the seaweed on the "
				+ object.getGameObjectDef().getName().toLowerCase(), "The seaweed burns to ashes");
		} else if (item.getCatalogId() == ItemId.COOKEDMEAT.id()) { // Cooked meat to get burnt meat
			if (player.getQuestStage(Quests.WITCHS_POTION) != -1) {
				thinkbubble(item);
				mes(cookingOnMessage(player, item, object, false));
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.COOKEDMEAT.id()));
				give(player, ItemId.BURNTMEAT.id(), 1);
				player.playerServerMessage(MessageType.QUEST, "you burn the meat");
			} else {
				player.message("Nothing interesting happens");
			}
		} else {
			final ItemCookingDef cookingDef = item.getCookingDef(player.getWorld());
			if (cookingDef == null) {
				player.message("Nothing interesting happens");
				return;
			}
			if (player.getSkills().getLevel(Skills.COOKING) < cookingDef.getReqLevel()) {
				String itemName = item.getDef(player.getWorld()).getName().toLowerCase();
				itemName = itemName.startsWith("raw ") ? itemName.substring(4) :
					itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName;
				player.playerServerMessage(MessageType.QUEST, "You need a cooking level of " + cookingDef.getReqLevel() + " to cook " + itemName);
				return;
			}
			if (object.getID() == 11) {
				if (!player.withinRange(object, 2)) {
					return;
				}
			} else {
				if (!player.withinRange(object, 1)) {
					return;
				}
			}
			// Some need a RANGE not a FIRE
			boolean needOven = false;
			int timeToCook = 3;
			if (isOvenFood(item)) {
				needOven = true;
				timeToCook = 5;
			}
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.COOKING_CAPE.id()))
				timeToCook *= 0.7;
			if ((object.getID() == 97 || object.getID() == 274) && needOven) {
				player.playerServerMessage(MessageType.QUEST, "You need a proper oven to cook this");
				return;
			}

			if (item.getCatalogId() == ItemId.RAW_OOMLIE_MEAT_PARCEL.id())
				player.message("You prepare to cook the Oomlie meat parcel.");
			else
				player.message(cookingOnMessage(player, item, object, needOven));

			int repeat = 1;
			if (config().BATCH_PROGRESSION) {
				repeat = player.getCarriedItems().getInventory().countId(item.getCatalogId(), Optional.of(false));
			}

			startbatch(repeat);
			batchCooking(player, item, timeToCook, cookingDef, object);
		}
	}

	private void batchCooking(Player player, Item item, int timeToCook, ItemCookingDef cookingDef, GameObject gameObject) {
		if (gameObject.isRemoved()) return;

		if (player.getSkills().getLevel(Skills.COOKING) < cookingDef.getReqLevel()) {
			String itemName = item.getDef(player.getWorld()).getName().toLowerCase();
			itemName = itemName.startsWith("raw ") ? itemName.substring(4) :
				itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName;
			player.playerServerMessage(MessageType.QUEST, "You need a cooking level of " + cookingDef.getReqLevel() + " to cook " + itemName);
			return;
		}
		Item cookedFood = new Item(cookingDef.getCookedId());
		if (config().WANT_FATIGUE) {
			if (config().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to cook this food");
				return;
			}
		}
		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;
		thinkbubble(item);
		player.playSound("cooking");
		delay(timeToCook);
		if (player.getCarriedItems().remove(item) > -1) {
			if (!Formulae.burnFood(player, item.getCatalogId(), player.getSkills().getLevel(Skills.COOKING))
					|| item.getCatalogId() == ItemId.RAW_LAVA_EEL.id()
					|| (item.getCatalogId() == ItemId.UNCOOKED_PITTA_BREAD.id() && player.getSkills().getLevel(Skills.COOKING) >= 58)) {
				player.getCarriedItems().getInventory().add(cookedFood);
				player.message(cookedMessage(player, cookedFood, isOvenFood(item)));
				player.incExp(Skills.COOKING, cookingDef.getExp(), true);
			} else {
				player.getCarriedItems().getInventory().add(new Item(cookingDef.getBurnedId()));
				if (cookedFood.getCatalogId() == ItemId.COOKEDMEAT.id()) {
					player.playerServerMessage(MessageType.QUEST, "You accidentally burn the meat");
				} else {
					String food = cookedFood.getDef(player.getWorld()).getName().toLowerCase();
					food = food.contains("pie") ? "pie" : food;
					player.playerServerMessage(MessageType.QUEST, "You accidentally burn the " + food);
				}
			}

			// Repeat
			updatebatch();
			if (!ifinterrupted() && !ifbatchcompleted()) {
				delay();
				batchCooking(player, item, timeToCook, cookingDef, gameObject);
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		int[] ids = new int[]{97, 11, 119, 274, 435, 491};
		Arrays.sort(ids);
		if ((item.getCatalogId() == ItemId.RAW_OOMLIE_MEAT.id() || item.getCatalogId() == ItemId.SEAWEED.id()
				|| item.getCatalogId() == ItemId.UNCOOKED_SWAMP_PASTE.id() || item.getCatalogId() == ItemId.COOKEDMEAT.id())
				&& Arrays.binarySearch(ids, obj.getID()) >= 0) {
			return true;
		}
		if (item.getCatalogId() == ItemId.POISON.id() && obj.getID() == 435 && obj.getX() == 618 && obj.getY() == 3453) {
			return true;
		}
		// Gnome Cooking Items
		if (item.getCatalogId() == ItemId.GNOMEBATTA_DOUGH.id() || item.getCatalogId() == ItemId.GNOMEBOWL_DOUGH.id()
			|| item.getCatalogId() == ItemId.GNOMECRUNCHIE_DOUGH.id() || item.getCatalogId() == ItemId.GNOMEBATTA.id()
			|| item.getCatalogId() == ItemId.GNOMEBOWL.id() || item.getCatalogId() == ItemId.GNOMECRUNCHIE.id()
			|| item.getCatalogId() == ItemId.FULL_COCKTAIL_GLASS.id()) {
			return false;
		}
		final ItemCookingDef cookingDef = item.getCookingDef(player.getWorld());
		return cookingDef != null && Arrays.binarySearch(ids, obj.getID()) >= 0;
	}

	private void cookMethod(final Player player, final int itemID, final int product, final boolean hasBubble, final String... messages) {
		int repeat = 1;
		if (config().BATCH_PROGRESSION) {
			repeat = player.getCarriedItems().getInventory().countId(itemID);
		}

		startbatch(repeat);
		batchInedibleCooking(player, itemID, product, hasBubble, messages);
	}

	private void batchInedibleCooking(Player player, int itemID, int product, boolean hasBubble, String... messages) {
		if (player.getCarriedItems().hasCatalogID(itemID, Optional.of(false))) {
			if (hasBubble)
				thinkbubble(new Item(itemID));
			player.playSound("cooking");
			mes(messages);
			player.getCarriedItems().remove(new Item(itemID));
			give(player, product, 1);
		} else {
			player.message("You don't have all the ingredients");
			return;
		}

		// TODO: Add back when `mes` is changed to not use a timer (if it ever is).
		// delay();
		updatebatch();
		if (!ifinterrupted() && !ifbatchcompleted()) {
			batchInedibleCooking(player, itemID, product, hasBubble, messages);
		}
	}

	private String cookingOnMessage(Player player, Item item, GameObject object, boolean needsOven) {
		String itemName = item.getDef(player.getWorld()).getName().toLowerCase();
		itemName = itemName.startsWith("raw ") ? itemName.substring(4) :
			itemName.contains("pie") ? "pie" :
				itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName;
		boolean isGenMeat = isGeneralMeat(item);
		if (isGenMeat)
			itemName = "meat";
		String message = "You cook the " + itemName + " on the " +
			((object.getID() == 97 || object.getID() == 274) ? "fire" : "stove") +
			(isGenMeat ? "..." : "");
		if (needsOven) {
			message = "You cook the " + itemName + " in the oven...";
		}

		return message;

	}

	private String cookedMessage(Player player, Item cookedFood, boolean needsOven) {
		String message = "The " + cookedFood.getDef(player.getWorld()).getName().toLowerCase() + " is now nicely cooked";
		if (cookedFood.getCatalogId() == ItemId.COOKEDMEAT.id()) {
			message = "The meat is now nicely cooked";
		}
		if (needsOven) {
			String cookedPastryFood = cookedFood.getDef(player.getWorld()).getName().toLowerCase();
			cookedPastryFood = cookedPastryFood.contains("pie") ? "pie" : cookedPastryFood;
			message = "You remove the " + cookedPastryFood + " from the oven";
		}
		return message;

	}

	private boolean isOvenFood(Item item) {
		return DataConversions.inArray(new int[]{
			ItemId.BREAD_DOUGH.id(), // Bread
			ItemId.UNCOOKED_APPLE_PIE.id(), // Apple Pie
			ItemId.UNCOOKED_MEAT_PIE.id(), // Meat Pie
			ItemId.UNCOOKED_REDBERRY_PIE.id(), // Redberry Pie
			ItemId.UNCOOKED_PIZZA.id(), // Pizza
			ItemId.UNCOOKED_CAKE.id(), // Cake
			ItemId.UNCOOKED_PITTA_BREAD.id(), // Pitta Bread
		}, item.getCatalogId());
	}

	private boolean isGeneralMeat(Item item) {
		return DataConversions.inArray(new int[]{ItemId.COOKEDMEAT.id(), ItemId.RAW_CHICKEN.id(),
				ItemId.RAW_BEAR_MEAT.id(), ItemId.RAW_RAT_MEAT.id(), ItemId.RAW_BEEF.id()}, item.getCatalogId());
	}
}
