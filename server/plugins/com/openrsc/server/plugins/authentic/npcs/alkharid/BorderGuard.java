package com.openrsc.server.plugins.authentic.npcs.alkharid;

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

public final class BorderGuard implements
	TalkNpcTrigger, OpLocTrigger {

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (player.getQuestStage(Quests.PRINCE_ALI_RESCUE) == -1
			|| player.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 3) {
			say(player, n, "Can I come through this gate?");
			npcsay(player, n,
				"You may pass for free, you are a friend of Al Kharid");
			player.message("The gate swings open");
			if (player.getX() > 91)
				player.teleport(90, 649, false);
			else
				player.teleport(93, 649, false);
			return;
		}
		say(player, n, "Can I come through this gate?");
		npcsay(player, n, "You must pay a toll of 10 gold coins to pass");
		int option = multi(player, n, false, "No thankyou, I'll walk round",
			"Who does my money go to?", "yes ok");
		switch (option) {
			case 0: // no thanks
				say(player, n, "No thankyou");
				npcsay(player, n, "Ok suit yourself");
				break;
			case 1: // who does money go to
				say(player, n, "Who does my money go to?");
				npcsay(player, n, "The money goes to the city of Al Kharid");
				break;
			case 2:
				say(player, n, "Yes ok");
				if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 10)) > -1) {
					player.message("You pay the guard");
					npcsay(player, n, "You may pass");
					player.message("The gate swings open");
					if (player.getX() > 91) {
						walkThenTeleport(player, 92, 649, 91, 649, false);
					}
					else {
						walkThenTeleport(player, 91, 649, 92, 649, false);
					}
				} else {
					say(player, n,
						"Oh dear I don't actually seem to have enough money");
				}
				break;
		}
	}

	public static void walkThenTeleport(final Player player, final int x1, final int y1, final int x2, final int y2, final boolean bubble) {
		player.walk(x1, y1);
		while (!player.getWalkingQueue().finished()) {
			delay();
		}
		player.teleport(x2, y2, bubble);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BORDER_GUARD_ALKHARID.id() || n.getID() == NpcId.BORDER_GUARD_LUMBRIDGE.id();
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 180 && command.equals("open")) {
			player.message("You need to talk to the border guard");
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 180 && command.equals("open");
	}
}
