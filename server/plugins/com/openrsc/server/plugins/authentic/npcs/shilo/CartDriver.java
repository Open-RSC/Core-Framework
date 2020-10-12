package com.openrsc.server.plugins.authentic.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class CartDriver implements TalkNpcTrigger, OpLocTrigger {

	public static final int TRAVEL_CART_SHILO = 768;
	public static final int TRAVEL_CART_BRIMHAVEN = 769;

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CART_DRIVER_SHILO.id() || n.getID() == NpcId.CART_DRIVER_BRIMHAVEN.id();
	}

	private void cartRideShilo(Player player, Npc n) {
		npcsay(player, n, "I am offering a cart ride to Brimhaven if you're interested!",
			"It will cost 500 Gold");
		int menu = multi(player, n, false, //do not send over
			"Yes, that sounds great!",
			"No thanks.");
		if (menu == 0) {
			say(player, n, "Yes please, I'd like to go to Brimhaven!");
			if (ifheld(player, ItemId.COINS.id(), 500)) {
				npcsay(player, n, "Great!",
					"Just hop into the cart then and we'll go!");
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 500));
				mes("You Hop into the cart and the driver urges the horses on.");
				delay(2);
				player.teleport(468, 662);
				mes("You take a taxing journey through the jungle to Brimhaven.");
				delay(2);
				mes("You feel fatigued from the journey, but at least");
				delay(2);
				mes("you didn't have to walk all that distance.");
				delay(2);
			} else {
				npcsay(player, n, "Sorry, but it looks as if you don't have enough money.",
					"Come back and see me when you have enough for the ride.");
			}
		} else if (menu == 1) {
			say(player, n, "No thanks.");
			npcsay(player, n, "Ok Bwana, let me know if you change your mind.");
		}
	}

	private void cartRideBrimhaven(Player player, Npc n) {
		// only if player has completed Shilo Village
		if (player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
			npcsay(player, n, "I am offering a cart ride to Shilo Village if you're interested!",
				"It will cost 500 Gold");
			int menu = multi(player, n, false, //do not send over
				"Yes, that sounds great!",
				"No thanks.");
			if (menu == 0) {
				say(player, n, "Yes please, I'd like to go to Shilo Village!");
				if (ifheld(player, ItemId.COINS.id(), 500)) {
					npcsay(player, n, "Great!",
						"Just hop into the cart then and we'll go!");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 500));
					mes("You Hop into the cart and the driver urges the horses on.");
					delay(2);
					player.teleport(417, 855);
					mes("You take a taxing journey through the jungle to Shilo Village.");
					delay(2);
					mes("You feel fatigued from the journey, but at least");
					delay(2);
					mes("you didn't have to walk all that distance.");
					delay(2);
				} else {
					npcsay(player, n, "Sorry, but it looks as if you don't have enough money.",
						"Come back and see me when you have enough for the ride.");
				}
			} else if (menu == 1) {
				say(player, n, "No thanks.");
				npcsay(player, n, "Ok Bwana, let me know if you change your mind.");
			}
		} else {
			npcsay(player, n, "We used to run cart trips down to Shilo Village in south Karamja",
				"Since the troubles we had",
				"we've had to stop them though",
				"too many people got killed");
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.CART_DRIVER_SHILO.id()) {
			say(player, n, "Hello!");
			npcsay(player, n, "Hello Bwana!");
			cartRideShilo(player, n);
		} else if (n.getID() == NpcId.CART_DRIVER_BRIMHAVEN.id()) {
			say(player, n, "Hello!");
			npcsay(player, n, "Hello Bwana!");
			cartRideBrimhaven(player, n);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == TRAVEL_CART_SHILO || obj.getID() == TRAVEL_CART_BRIMHAVEN;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == TRAVEL_CART_SHILO) {
			if (command.equalsIgnoreCase("Board")) {
				player.message("This looks like a sturdy travelling cart.");
				Npc driver = ifnearvisnpc(player, NpcId.CART_DRIVER_SHILO.id(), 10);
				if (driver != null) {
					driver.teleport(player.getX(), player.getY());
					delay(); // 1 tick.
					npcWalkFromPlayer(player, driver);
					player.message("A nearby man walks over to you.");
					cartRideShilo(player, driver);
				} else {
					player.message("The cart driver is currently busy.");
				}
			} else if (command.equalsIgnoreCase("Look")) {
				player.message("A sturdy travelling cart built for long trips through jungle areas.");
			}
		} else if (obj.getID() == TRAVEL_CART_BRIMHAVEN) {
			if (command.equalsIgnoreCase("Board")) {
				player.message("This looks like a sturdy travelling cart.");
				Npc driver = ifnearvisnpc(player, NpcId.CART_DRIVER_BRIMHAVEN.id(), 10);
				if (driver != null) {
					driver.teleport(player.getX(), player.getY());
					delay(); // 1 tick.
					npcWalkFromPlayer(player, driver);
					player.message("A nearby man walks over to you.");
					cartRideBrimhaven(player, driver);
				} else {
					player.message("The cart driver is currently busy.");
				}
			} else if (command.equalsIgnoreCase("Look")) {
				player.message("A sturdy travelling cart built for long trips through jungle areas.");
			}
		}
	}
}
