package com.openrsc.server.plugins.npcs.shilo;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class CartDriver implements TalkNpcTrigger, OpLocTrigger {

	public static final int TRAVEL_CART = 768;

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.CART_DRIVER_SHILO.id();
	}

	private void cartRide(Player p, Npc n) {
		npcsay(p, n, "I am offering a cart ride to Brimhaven if you're interested!",
			"It will cost 500 Gold");
		int menu = multi(p, n, false, //do not send over
			"Yes, that sounds great!",
			"No thanks.");
		if (menu == 0) {
			say(p, n, "Yes please, I'd like to go to Brimhaven!");
			if (ifheld(p, ItemId.COINS.id(), 500)) {
				npcsay(p, n, "Great!",
					"Just hop into the cart then and we'll go!");
				p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 500));
				mes(p, 1000, "You Hop into the cart and the driver urges the horses on.");
				p.teleport(468, 662);
				mes(p, 1200, "You take a taxing journey through the jungle to Brimhaven.");
				mes(p, 1200, "You feel fatigued from the journey, but at least");
				mes(p, 1200, "you didn't have to walk all that distance.");
			} else {
				npcsay(p, n, "Sorry, but it looks as if you don't have enough money.",
					"Come back and see me when you have enough for the ride.");
			}
		} else if (menu == 1) {
			say(p, n, "No thanks.");
			npcsay(p, n, "Ok Bwana, let me know if you change your mind.");
		}
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.CART_DRIVER_SHILO.id()) {
			say(p, n, "Hello!");
			npcsay(p, n, "Hello Bwana!");
			cartRide(p, n);
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return obj.getID() == TRAVEL_CART;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == TRAVEL_CART) {
			if (command.equalsIgnoreCase("Board")) {
				p.message("This looks like a sturdy travelling cart.");
				Npc driver = ifnearvisnpc(p, NpcId.CART_DRIVER_SHILO.id(), 10);
				if (driver != null) {
					driver.teleport(p.getX(), p.getY());
					delay(600); // 1 tick.
					npcWalkFromPlayer(p, driver);
					p.message("A nearby man walks over to you.");
					cartRide(p, driver);
				} else {
					p.message("The cart driver is currently busy.");
				}
			} else if (command.equalsIgnoreCase("Look")) {
				p.message("A sturdy travelling cart built for long trips through jungle areas.");
			}
		}
	}
}
