package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class CaptainBarnaby implements OpLocTrigger,
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Do you want to go on a trip to Karamja?",
			"The trip will cost you 30 gold");
		String[] menu = new String[]{
			"I'd rather go to Crandor Isle",
			"Yes please", "No thankyou"
		};
		if (player.getQuestStage(Quests.DRAGON_SLAYER) == -1 || player.getCache().hasKey("ned_hired")) {
			menu = new String[]{ // Crandor option is not needed.
				"Yes please", "No thankyou"
			};
			int choice = multi(player, n, menu);
			if (choice >= 0) {
				travel(player, n, choice + 1);
			}
		} else {
			int choice = multi(player, n, menu);
			travel(player, n, choice);
		}
	}

	public void travel(final Player player, final Npc n, int option) {
		if (option == 0) {
			npcsay(player, n, "No I need to stay alive",
				"I have a wife and family to support");
		} else if (option == 1) {
			if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30)) > -1) {
				mes("You pay 30 gold");
				delay(3);
				mes("You board the ship");
				delay(3);
				player.teleport(467, 651, false);
				delay(2);
				mes("The ship arrives at Karamja");
				delay(3);
			} else {
				say(player, n, "Oh dear I don't seem to have enough money");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CAPTAIN_BARNABY.id();
	}


	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 157) {
			if (command.equals("board")) {
				if (player.getY() != 616) {
					return;
				}

				Npc captain = ifnearvisnpc(player, NpcId.CAPTAIN_BARNABY.id(), 5);
				if (captain != null) {
					captain.initializeTalkScript(player);
				} else {
					player.message("I need to speak to the captain before boarding the ship.");
				}
			}
		}
	}

	@Override
	public boolean blockOpLoc(Player arg2, GameObject arg0, String arg1) {
		return (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(536, 617)))
			|| (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(531, 617)));
	}
}
