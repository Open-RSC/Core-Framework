package com.openrsc.server.plugins.skills;

import com.openrsc.server.Constants;
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

import java.util.Arrays;

import static com.openrsc.server.plugins.Functions.*;

public class ObjectCooking implements InvUseOnObjectListener, InvUseOnObjectExecutiveListener {
	@Override
	public void onInvUseOnObject(GameObject object, Item item, Player owner) {
		Npc cook = getNearestNpc(owner, 7, 20);
		if(cook != null && owner.getQuestStage(Constants.Quests.COOKS_ASSISTANT) != -1
				&& object.getID() == 119) {
			cook.face(owner);
			owner.face(cook);
			npcTalk(owner, cook, "Hey! Who said you could use that?");
		}
		else
			handleCooking(item, owner, object);
		return;
	}

	private void handleCooking(final Item item, Player p,
			final GameObject object) {

		// Tutorial Meat
		if(p.getLocation().onTutorialIsland() && item.getID() == 503 && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") >= 0  &&  p.getCache().getInt("tutorial") <= 31) {
			p.setBusy(true);
			showBubble(p, item);
			p.playSound("cooking");
			p.message("You cook the meat on the stove...");
			if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
				p.message("You accidentally burn the meat");
				p.getInventory().replace(503, 134);

				message(p, "sometimes you will burn food",
						"As your cooking level increases this will happen less",
						"Now speak to the cooking instructor again");
				p.getCache().set("tutorial", 30);
			} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 30) {
				p.message("The meat is now nicely cooked");
				message(p, "Now speak to the cooking instructor again");
				p.getCache().set("tutorial", 31);
				p.getInventory().replace(503, 132);

			}
			p.setBusy(false);
			return;
		}

		// Raw Oomlie Meat (Always burn)
		else if (item.getID() == 1268) {
			if (object.getID() == 97 || object.getID() == 274)
				message(p, 1200, "You cook the meat on the fire...");
			else
				message(p, 1200, "You cook the meat on the stove...");
			removeItem(p, 1268, 1); 
			addItem(p, 134, 1);
			message(p, 1200, "This meat is too delicate to cook like this.");
			message(p, 1200, "Perhaps you can wrap something around it to protect it from the heat.");
		}

		// Poison (Hazeel Cult)
		else if(item.getID() == 177 && object.getID() == 435 && object.getX() == 618 && object.getY() == 3453) {
			if(p.getQuestStage(Constants.Quests.THE_HAZEEL_CULT) == 3 && p.getCache().hasKey("evil_side")) {
				message(p, "you poor the poison into the hot pot",
						"the poison desolves into the soup");
				removeItem(p, 177, 1);
				p.updateQuestStage(Constants.Quests.THE_HAZEEL_CULT, 4);
			} else {
				p.message("nothing interesting happens");
			}
		}
		else if (item.getID() == 784) { // Uncooked swamp paste
			cookMethod(p, 784, 785, "you warm the paste over the fire", "it thickens into a sticky goo");
		}
		else if (item.getID() == 622) { // Seaweed (Glass)
			cookMethod(p, 622, 624, "You put the seaweed on the "
					+ object.getGameObjectDef().getName().toLowerCase(), "The seaweed burns to ashes");
		} else {
			final ItemCookingDef cookingDef = item.getCookingDef();
			if (cookingDef == null) {
				p.message("Nothing interesting happens");
				return;
			}
			if (p.getSkills().getLevel(7) < cookingDef.getReqLevel()) {
				String itemName = item.getDef().getName().toLowerCase();
				itemName = itemName.startsWith("raw ") ? itemName.substring(4) : 
					itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName ;
				p.message("You need a cooking level of " + cookingDef.getReqLevel() + " to cook " + itemName);
				return;
			}
			if (!p.withinRange(object, 2)) { 
				return;
			}
			// Some need a RANGE not a FIRE
			boolean needOven = false;
			int timeToCook = 1800;
			if(isOvenFood(item)) {
				needOven = true;
				timeToCook = 3000;
			}
			if ((object.getID() == 97 || object.getID() == 274) && needOven) {
				p.message("You need a proper oven to cook this");
				return;
			}

			if (item.getID() == 1280)
				p.message("You prepare to cook the Oomlie meat parcel.");
			else
				p.message(cookingOnMessage(p, item, object, needOven));
			showBubble(p, item);
			p.setBatchEvent(new BatchEvent(p, timeToCook, Formulae.getRepeatTimes(p, 7)) {
				@Override
				public void action() {
					Item cookedFood = new Item(cookingDef.getCookedId());
					if (owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too tired to cook this food");
						interrupt();
						return;
					}
					showBubble(owner, item);
					owner.playSound("cooking");
					if (owner.getInventory().remove(item) > -1) {
						if (!Formulae.burnFood(owner, item.getID(), owner.getSkills().getLevel(7)) || item.getID() == 591) {
							owner.getInventory().add(cookedFood);
							owner.message(cookedMessage(p, cookedFood, isOvenFood(item)));
							owner.incExp(7, cookingDef.getExp(), true);
						} else {
							owner.getInventory().add(new Item(cookingDef.getBurnedId()));
							if(cookedFood.getID() == 132) {
								owner.message("You accidentally burn the meat");
							}
							else {
								String food = cookedFood.getDef().getName().toLowerCase();
								food = food.contains("pie") ? "pie" : food;
								owner.message("You accidentally burn the " + food);
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
		int[] ids = new int[]{ 97, 11, 119, 274, 435, 491};
		Arrays.sort(ids);
		if ((item.getID() == 1268 || item.getID() == 622 || item.getID() == 784) && Arrays.binarySearch(ids, obj.getID()) >= 0) {
			return true;
		}
		if(item.getID() == 177 && obj.getID() == 435 && obj.getX() == 618 && obj.getY() == 3453) {
			return true;
		}
		final ItemCookingDef cookingDef = item.getCookingDef();
		return cookingDef != null && Arrays.binarySearch(ids, obj.getID()) >= 0;
	}

	public void cookMethod(Player p, int itemID, int product, String... messages) {
		if(hasItem(p, itemID, 1)) {
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
		String itemName = item.getDef().getName().toLowerCase();
		itemName = itemName.startsWith("raw ") ? itemName.substring(4) : 
			itemName.contains("pie") ? "pie" : 
				itemName.startsWith("uncooked ") ? itemName.substring(9) : itemName ;
		boolean isGenMeat = isGeneralMeat(item);
		if(isGenMeat)
			itemName = "meat";
		String message = "You cook the " + itemName + " on the " + 
			((object.getID() == 97 || object.getID() == 274) ? "fire" : "stove") + 
			(isGenMeat ? "..." : "");
		if(needsOven) {
			message = "You cook the " + itemName + " in the oven...";
		}

		return message;
		
	}
	
	private String cookedMessage(Player p, Item cookedFood, boolean needsOven) {
		String message = "The " + cookedFood.getDef().getName().toLowerCase() + " is now nicely cooked";
		if(cookedFood.getID() == 132) {
			message = "The meat is now nicely cooked";
		}
		if(needsOven) {
			String cookedPastryFood = cookedFood.getDef().getName().toLowerCase();
			cookedPastryFood = cookedPastryFood.contains("pie") ? "pie" : cookedPastryFood;
			message = "You remove the " + cookedPastryFood + " from the oven";
		}
		return message;
		
	}
	
	private boolean isOvenFood(Item item) {
		return DataConversions.inArray(new int[] {
				137, // Bread
				254, // Apple Pie
				255, // Meat Pie
				256, // Redberry Pie
				324, // Pizza
				339, // Cake
				1104, // Pitta Bread
		}, item.getID());
	}
	
	private boolean isGeneralMeat(Item item) {
		return DataConversions.inArray(new int[] {133, 502, 503, 504}, item.getID());
	}
}
