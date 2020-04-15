package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class PortSarimSailor implements OpLocTrigger,
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Do you want to go on a trip to Karamja?",
			"The trip will cost you 30 gold");
		String[] menu = new String[]{
			"I'd rather go to Crandor Isle",
			"Yes please", "No thankyou"
		};
		if (p.getQuestStage(Quests.DRAGON_SLAYER) == -1 || p.getCache().hasKey("ned_hired")) {
			menu = new String[]{ // Crandor option is not needed.
				"Yes please", "No thankyou"
			};
			int choice = multi(p, n, menu);
			if (choice >= 0) {
				travel(p, n, choice + 1);
			}
		} else {
			int choice = multi(p, n, menu);
			travel(p, n, choice);
		}
	}

	public void travel(final Player p, final Npc n, int option) {
		if (option == 0) {
			npcsay(p, n, "No I need to stay alive",
				"I have a wife and family to support");
		} else if (option == 1) {
			if (p.getCarriedItems().remove(new Item(ItemId.COINS.id(), 30)) > -1) {
				Functions.mes(p, "You pay 30 gold", "You board the ship");
				p.teleport(324, 713, false);
				delay(1000);
				Functions.mes(p, "The ship arrives at Karamja");
			} else {
				say(p, n, "Oh dear I don't seem to have enough money");
			}
		}
	}


	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.CAPTAIN_TOBIAS.id() || n.getID() == NpcId.SEAMAN_THRESNOR.id() || n.getID() == NpcId.SEAMAN_LORRIS.id();
	}

	@Override
	public void onOpLoc(GameObject arg0, String arg1, Player p) {
		Npc sailor = ifnearvisnpc(p, NpcId.CAPTAIN_TOBIAS.id(), 5);
		if (sailor != null) {
			sailor.initializeTalkScript(p);
		} else {
			p.message("I need to speak to the captain before boarding the ship.");
		}

	}

	@Override
	public boolean blockOpLoc(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(265, 645)))
			|| (arg0.getID() == 156 && arg0.getLocation().equals(Point.location(265, 650)))
			|| (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(265, 652)));
	}
}

