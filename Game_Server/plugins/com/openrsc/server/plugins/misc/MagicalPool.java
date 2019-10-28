package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MagicalPool implements ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 1166 || obj.getID() == 1155;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 1155) {
			/*
			if (!player.canUsePool()) {
				player.message("You have just died, you must wait for "
										+ player.secondsUntillPool()
										+ " seconds before using this pool again");
				return;
			}
			while (System.currentTimeMillis()
					- player.getLastMoved() < 10000
					&& player.getLocation().inWilderness()) {
				player.message("You must stand still for 10 seconds before using portal");
				return;
			}
			while (System.currentTimeMillis()
					- player.getCombatTimer() < 10000
					&& player.getLocation().inWilderness()) {
				player.message("You must be out of combat for 10 seconds before using portal");
				return;
			}
			int option = showMenu(player, "Edgeville", "Varrock",
					"Castle (dangerous)", "Graveyard (dangerous)", "Hobgoblins (dangerous)", "Altar (dangerous)",
					"Dragon Maze (dangerous)", "Mage Arena (dangerous)", "Rune rocks (dangerous)", "Red dragons (dangerous)", "Further underground mage arena");
			
			if (option == 0) {
				player.teleport(215, 436);
			} else if (option == 1) {
				player.teleport(111, 505);
			} else if (option == 2) {
				player.teleport(272, 354);
			} else if (option == 3) {
				player.teleport(187, 297);
			} else if (option == 4) {
				player.teleport(218, 271);
			} else if (option == 5) {
				player.teleport(316, 199);
			} else if (option == 6) {
				player.teleport(271, 195);
			} else if (option == 7) {
				player.teleport(224, 110);
			} else if (option == 8) {
				player.teleport(264, 148);
			} else if(option == 9) {
				player.teleport(143, 173);
			} else if(option == 10) {
			*/
			if (player.getCache().hasKey("mage_arena") && player.getCache().getInt("mage_arena") >= 2) {
				movePlayer(player, 471, 3385);
				player.message("you are teleported further under ground");
			} else {
				message(player, 1200, "you step into the pool");
				message(player, 1200, "you wet your boots");
			}
		}
		if (obj.getID() == 1166) {
			message(player, 1200, "you step into the sparkling water");
			message(player, 1200, "you feel energy rush through your veins");
			movePlayer(player, 447, 3373);
			player.message("you are teleported to kolodions cave");
		}
	}
}
