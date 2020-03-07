package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class BorderGuard implements
	TalkNpcTrigger, OpLocTrigger {

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == -1
			|| p.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 3) {
			say(p, n, "Can I come through this gate?");
			npcsay(p, n,
				"You may pass for free, you are a friend of Al Kharid");
			p.message("The gate swings open");
			if (p.getX() > 91)
				p.teleport(90, 649, false);
			else
				p.teleport(93, 649, false);
			return;
		}
		say(p, n, "Can I come through this gate?");
		npcsay(p, n, "You must pay a toll of 10 gold coins to pass");
		int option = multi(p, n, false, "No thankyou, I'll walk round",
			"Who does my money go to?", "yes ok");
		switch (option) {
			case 0: // no thanks
				say(p, n, "No thankyou");
				npcsay(p, n, "Ok suit yourself");
				break;
			case 1: // who does money go to
				say(p, n, "Who does my money go to?");
				npcsay(p, n, "The money goes to the city of Al Kharid");
				break;
			case 2:
				say(p, n, "Yes ok");
				if (p.getCarriedItems().remove(ItemId.COINS.id(), 10) > -1) {
					p.message("You pay the guard");
					npcsay(p, n, "You may pass");
					p.message("The gate swings open");
					if (p.getX() > 91) {
						walkThenTeleport(p, 92, 649, 91, 649, false);
					}
					else {
						walkThenTeleport(p, 91, 649, 92, 649, false);
					}
				} else {
					say(p, n,
						"Oh dear I don't actually seem to have enough money");
				}
				break;
		}
	}

	public static void walkThenTeleport(final Player player, final int x1, final int y1, final int x2, final int y2, final boolean bubble) {
		player.walk(x1, y1);
		while (!player.getWalkingQueue().finished()) {
			delay(1);
		}
		player.teleport(x2, y2, bubble);
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BORDER_GUARD_ALKHARID.id() || n.getID() == NpcId.BORDER_GUARD_LUMBRIDGE.id();
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player player) {
		if (obj.getID() == 180 && command.equals("open")) {
			player.message("You need to talk to the border guard");
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return obj.getID() == 180 && command.equals("open");
	}
}
