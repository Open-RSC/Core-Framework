package com.openrsc.server.plugins.npcs.portsarim;

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

public final class PortSarimSailor implements OpLocTrigger,
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
				mes(player, "You pay 30 gold", "You board the ship");
				player.teleport(324, 713, false);
				delay(config().GAME_TICK * 2);
				mes(player, "The ship arrives at Karamja");
			} else {
				say(player, n, "Oh dear I don't seem to have enough money");
			}
		}
	}


	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CAPTAIN_TOBIAS.id() || n.getID() == NpcId.SEAMAN_THRESNOR.id() || n.getID() == NpcId.SEAMAN_LORRIS.id();
	}

	@Override
	public void onOpLoc(Player player, GameObject arg0, String arg1) {
		Npc sailor = ifnearvisnpc(player, NpcId.CAPTAIN_TOBIAS.id(), 5);
		if (sailor != null) {
			sailor.initializeTalkScript(player);
		} else {
			player.message("I need to speak to the captain before boarding the ship.");
		}

	}

	@Override
	public boolean blockOpLoc(Player arg2, GameObject arg0, String arg1) {
		return (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(265, 645)))
			|| (arg0.getID() == 156 && arg0.getLocation().equals(Point.location(265, 650)))
			|| (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(265, 652)));
	}
}

