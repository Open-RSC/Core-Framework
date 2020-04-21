package com.openrsc.server.plugins.npcs.ardougne.east;

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
				mes(p, "You pay 30 gold", "You board the ship");
				p.teleport(467, 651, false);
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				mes(p, "The ship arrives at Karamja");
			} else {
				say(p, n, "Oh dear I don't seem to have enough money");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.CAPTAIN_BARNABY.id();
	}


	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (obj.getID() == 157) {
			if (command.equals("board")) {
				if (p.getY() != 616) {
					return;
				}

				Npc captain = ifnearvisnpc(p, NpcId.CAPTAIN_BARNABY.id(), 5);
				if (captain != null) {
					captain.initializeTalkScript(p);
				} else {
					p.message("I need to speak to the captain before boarding the ship.");
				}
			}
		}
	}

	@Override
	public boolean blockOpLoc(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(536, 617)))
			|| (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(531, 617)));
	}
}
