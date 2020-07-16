package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class LadyOfTheWaves implements OpLocTrigger {

	private static final int SHIP_LADY_OF_THE_WAVES_FRONT = 780;
	private static final int SHIP_LADY_OF_THE_WAVES_BACK = 781;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == SHIP_LADY_OF_THE_WAVES_FRONT || obj.getID() == SHIP_LADY_OF_THE_WAVES_BACK;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == SHIP_LADY_OF_THE_WAVES_FRONT || obj.getID() == SHIP_LADY_OF_THE_WAVES_BACK) {
			player.message("This ship looks like it might take you somewhere.");
			player.message("The captain shouts down,");
			player.message("@yel@Captain: Where would you like to go?");
			int menu = multi(player,
				"Khazard Port",
				"Port Sarim",
				"No where thanks!");
			if (menu == 0) {
				sail(player, menu);
			} else if (menu == 1) {
				sail(player, menu);
			} else if (menu == 2) {
				say(player, null, "No where thanks!");
				player.message("@yel@Captain: Ok, come back if you change your mind.");
			}
		}
	}

	private void sail(Player player, int option) {
		if (player.getCarriedItems().hasCatalogID(ItemId.SHIP_TICKET.id(), Optional.of(false))) {
			player.getCarriedItems().remove(new Item(ItemId.SHIP_TICKET.id()));
			mes("@yel@Captain: Thanks for the ticket, let's set sail!");
			delay(2);
			mes("You board the ship and it sails off.");
			delay(2);
			if (option == 0) {
				player.teleport(545, 703);
				player.message("Before you know it, you're in Khazard Port.");
			} else if (option == 1) {
				player.teleport(269, 640);
				player.message("Before you know it, you're in Port Sarim.");
			}
		} else {
			mes("The captain shakes his head.");
			delay(2);
			mes("@yel@Captain: Sorry Bwana, but you need a ticket!");
			delay(2);
			mes("@yel@Captain: You can get one in Shilo Village ");
			delay(2);
			mes("@yel@Captain: Just above the fishing shop. ");
			delay(2);
		}
	}
}
