package com.openrsc.server.plugins.skills;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.custom.BatchEvent;
import com.openrsc.server.external.ItemCookingDef;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Arrays;

import static com.openrsc.server.plugins.Functions.*;

public class ObjectCooking implements InvUseOnObjectListener, InvUseOnObjectExecutiveListener {
	@Override
	public void onInvUseOnObject(GameObject object, Item item, Player owner) {
		Npc cook = getNearestNpc(owner, 7, 20);
		if (cook != null && owner.getQuestStage(Quests.COOKS_ASSISTANT) != -1
			&& object.getID() == 119) {
			cook.face(owner);
			owner.face(cook);
			npcTalk(owner, cook, "Hey! Who said you could use that?");
		} else
			handleCooking(item, owner, object);
	}

	private void handleCooking(final Item item, Player p,
							   final GameObject object) {

		// Tutorial Meat
		if(object.getID() == 491) {
			if (item.getID() == ItemId.RAW_RAT_MEAT.id()) {
				p.setBusy(true);
				showBubble(p, item);
				p.playSound("cooking");
				p.playerServerMessage(MessageType.QUEST, "You cook the meat on the stove...");
				if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
					p.playerServerMessage(MessageType.QUEST, "You accidentally burn the meat");
					p.getInventory().replace(ItemId.RAW_RAT_MEAT.id(), ItemId.BURNTMEAT.id());
					message(p, "sometimes you will burn food",
							"As your cooking level increases this will happen less",
							"Now speak to the cooking instructor again");
					p.getCache().set("tutorial", 30);
				} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 30) {
					final ItemCookingDef cookingDef = item.getCookingDef(p.getWorld());
					p.playerServerMessage(MessageType.QUEST, "The meat is now nicely cooked");
					message(p, "Now speak to the cooking instructor again");
					p.incExp(Skills.COOKING, cookingDef.getExp(), true);
					p.getCache().set("tutorial", 31);
					p.getInventory().replace(ItemId.RAW_RAT_MEAT.id(), ItemId.COOKEDMEAT.id());

				} else {
					//per-wiki says rest of meats are burned
					p.playerServerMessage(MessageType.QUEST, "You accidentally burn the meat");
					p.getInventory().replace(ItemId.RAW_RAT_MEAT.id(), ItemId.BURNTMEAT.id());
				}
				p.setBusy(false);
			} else {
				p.message("Nothing interesting happens");
			}
			return;
		}

		// Raw Oomlie Meat (Always burn)
		else if (item.getID() == ItemId.RAW_OOMLIE_MEAT.id()) {
			if (object.getID() == 97 || object.getID() == 274)
				message(p, 1200, "You cook the meat on the fire...");
			else
				message(p, 1200, "You cook the meat on the stove...");
			removeItem(p, ItemId.RAW_OOMLIE_MEAT.id(), 1);
			addItem(p, ItemId.BURNTMEAT.id(), 1);
			message(p, 1200, "This meat is too delicate to cook like this.");
			message(p, 1200, "Perhaps you can wrap something around it to protect it from the heat.");
		}

		// Poison (Hazeel Cult)
		else if (item.getID() == ItemId.POISON.id() && object.getID() == 435 && object.getX() == 618 && object.getY() == 3453) {
			if (p.getQuestStage(Quests.THE_HAZEEL_CULT) == 3 && p.getCache().hasKey("evil_side")) {
				message(p, "you poor the poison into the hot pot",
					"the poison desolves into the soup");
				removeItem(p, ItemId.POISON.id(), 1);
				p.updateQuestStage(Quests.THE_HAZEEL_CULT, 4);
			} else {
				p.message("nothing interesting happens");
			}
		} else if (item.getID() == ItemId.UNCOOKED_SWAMP_PASTE.id()) {
			cookMethod(p, ItemId.UNCOOKED_SWAMP_PASTE.id(), ItemId.SWAMP_PASTE.id(), false, "you warm the paste over the fire", "it thickens into a sticky goo");
		} else if (item.getID() == ItemId.SEAWEED.id()) { // Seaweed (Glass)
			cookMethod(p, ItemId.SEAWEED.id(), ItemId.SODA_ASH.id(), true, "You put the seaweed on the "
				+ object.getGameObjectDef().getName().toLowerCase(), "The seaweed burns to ashes");
		} else if (item.getID() == ItemId.COOKEDMEAT.id()) { // Cooked meat to get burnt meat
			if (p.getQuestStage(Quests.WITCHS_POTION) != -1) {
				showBubble(p, item);
				message(p, 1800, cookingOnMessage(p, item, object, false));
				removeItem(p, ItemId.COOKEDMEAT.id(), 1);
				addItem(p, ItemId.BURNTMEAT.id(), 1);
				p.playerServerMessage(MessageType.QUEST, "you burn the meat");
			} else {
				p.message("Nothing interesting happens");
			}
		} else {
			final ItemCookingDef cookingDef = item.getCookingDef(p.getWorld());
			if (cookingDef == null) {
				p.message("Nothing interesting happens");
				return;
			}
			if (p.getSkills().getLevel(Skills.COOKING) < cookingDef.getReqLevel()) {
				String itemName = item.getDef(p.getWorld()).getName().toLowerCase();
				itemName = itemName.startsWith("raw ") ? itemName.substring(4) :
					itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName;
				p.playerServerMessage(MessageType.QUEST, "You need a cooking level of " + cookingDef.getReqLevel() + " to cook " + itemName);
				return;
			}
			if(object.getID() == 11){
				if (!p.withinRange(object, 2)) {
					return;
				}
			} else {
				if (!p.withinRange(object, 1)) {
					return;
				}
			}
			// Some need a RANGE not a FIRE
			boolean needOven = false;
			int timeToCook = 1800;
			if (isOvenFood(item)) {
				needOven = true;
				timeToCook = 3000;
			}
			if ((object.getID() == 97 || object.getID() == 274) && needOven) {
				p.playerServerMessage(MessageType.QUEST, "You need a proper oven to cook this");
				return;
			}

			if (item.getID() == ItemId.RAW_OOMLIE_MEAT_PARCEL.id())
				p.message("You prepare to cook the Oomlie meat parcel.");
			else
				p.message(cookingOnMessage(p, item, object, needOven));
			showBubble(p, item);
			p.setBatchEvent(new BatchEvent(p.getWorld(), p, timeToCook, "Cooking on Object", p.getInventory().countId(item.getID()), false) {
				@Override
				public void action() {
					if (getOwner().getSkills().getLevel(Skills.COOKING) < cookingDef.getReqLevel()) {
						String itemName = item.getDef(getWorld()).getName().toLowerCase();
						itemName = itemName.startsWith("raw ") ? itemName.substring(4) :
							itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName;
						getOwner().playerServerMessage(MessageType.QUEST, "You need a cooking level of " + cookingDef.getReqLevel() + " to cook " + itemName);
						interrupt();
						return;
					}
					Item cookedFood = new Item(cookingDef.getCookedId());
					if (getWorld().getServer().getConfig().WANT_FATIGUE) {
						if (getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
							&& getOwner().getFatigue() >= getOwner().MAX_FATIGUE) {
							getOwner().message("You are too tired to cook this food");
							interrupt();
							return;
						}
					}
					showBubble(getOwner(), item);
					getOwner().playSound("cooking");
					if (getOwner().getInventory().remove(item) > -1) {
						if (!Formulae.burnFood(getOwner(), item.getID(), getOwner().getSkills().getLevel(Skills.COOKING))
								|| item.getID() == ItemId.RAW_LAVA_EEL.id()
								|| (item.getID() == ItemId.UNCOOKED_PITTA_BREAD.id() && getOwner().getSkills().getLevel(Skills.COOKING) >= 58)) {
							getOwner().getInventory().add(cookedFood);
							getOwner().message(cookedMessage(p, cookedFood, isOvenFood(item)));
							getOwner().incExp(Skills.COOKING, cookingDef.getExp(), true);
						} else {
							getOwner().getInventory().add(new Item(cookingDef.getBurnedId()));
							if (cookedFood.getID() == ItemId.COOKEDMEAT.id()) {
								getOwner().playerServerMessage(MessageType.QUEST, "You accidentally burn the meat");
							} else {
								String food = cookedFood.getDef(p.getWorld()).getName().toLowerCase();
								food = food.contains("pie") ? "pie" : food;
								getOwner().playerServerMessage(MessageType.QUEST, "You accidentally burn the " + food);
							}
						}
					} else {
						interrupt();
					}
				}
			});
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player player) {
		int[] ids = new int[]{97, 11, 119, 274, 435, 491};
		Arrays.sort(ids);
		if ((item.getID() == ItemId.RAW_OOMLIE_MEAT.id() || item.getID() == ItemId.SEAWEED.id()
				|| item.getID() == ItemId.UNCOOKED_SWAMP_PASTE.id() || item.getID() == ItemId.COOKEDMEAT.id())
				&& Arrays.binarySearch(ids, obj.getID()) >= 0) {
			return true;
		}
		if (item.getID() == ItemId.POISON.id() && obj.getID() == 435 && obj.getX() == 618 && obj.getY() == 3453) {
			return true;
		}
		final ItemCookingDef cookingDef = item.getCookingDef(player.getWorld());
		return cookingDef != null && Arrays.binarySearch(ids, obj.getID()) >= 0;
	}

	private void cookMethod(Player p, int itemID, int product, boolean hasBubble, String... messages) {
		if (hasItem(p, itemID, 1)) {
			if (hasBubble)
				showBubble(p, new Item(itemID));
			p.playSound("cooking");
			message(p, messages);
			removeItem(p, itemID, 1);
			addItem(p, product, 1);
		} else {
			p.message("You don't have all the ingredients");
		}
	}

	private String cookingOnMessage(Player p, Item item, GameObject object, boolean needsOven) {
		String itemName = item.getDef(p.getWorld()).getName().toLowerCase();
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

	private String cookedMessage(Player p, Item cookedFood, boolean needsOven) {
		String message = "The " + cookedFood.getDef(p.getWorld()).getName().toLowerCase() + " is now nicely cooked";
		if (cookedFood.getID() == ItemId.COOKEDMEAT.id()) {
			message = "The meat is now nicely cooked";
		}
		if (needsOven) {
			String cookedPastryFood = cookedFood.getDef(p.getWorld()).getName().toLowerCase();
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
		}, item.getID());
	}

	private boolean isGeneralMeat(Item item) {
		return DataConversions.inArray(new int[]{ItemId.COOKEDMEAT.id(), ItemId.RAW_CHICKEN.id(),
				ItemId.RAW_BEAR_MEAT.id(), ItemId.RAW_RAT_MEAT.id(), ItemId.RAW_BEEF.id()}, item.getID());
	}
}
