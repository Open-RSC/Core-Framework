package com.openrsc.server.plugins.npcs.alkharid;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public final class BorderGuard implements TalkToNpcExecutiveListener,
	TalkToNpcListener, ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (p.getQuestStage(Constants.Quests.PRINCE_ALI_RESCUE) == -1
			|| p.getQuestStage(Constants.Quests.PRINCE_ALI_RESCUE) == 3) {
			playerTalk(p, n, "Can I come through this gate?");
			npcTalk(p, n,
				"You may pass for free, you are a friend of Al Kharid");
			p.message("The gate swings open");
			if (p.getX() > 91)
				p.teleport(90, 649, false);
			else
				p.teleport(93, 649, false);
			return;
		}
		playerTalk(p, n, "Can I come through this gate?");
		npcTalk(p, n, "You must pay a toll of 10 gold coins to pass");
		int option = showMenu(p, n, false, "No thankyou, I'll walk round",
			"Who does my money go to?", "yes ok");
		switch (option) {
			case 0: // no thanks
				playerTalk(p, n, "No thankyou");
				npcTalk(p, n, "Ok suit yourself");
				break;
			case 1: // who does money go to
				playerTalk(p, n, "Who does my money go to?");
				npcTalk(p, n, "The money goes to the city of Al Kharid");
				break;
			case 2:
				playerTalk(p, n, "Yes ok");
				if (p.getInventory().remove(ItemId.COINS.id(), 10) > -1) {
					p.message("You pay the guard");
					npcTalk(p, n, "You may pass");
					p.message("The gate swings open");
					if (p.getX() > 91) {
						p.walkThenTeleport(92, 649, 91, 649, false);
					}
					else {
						p.walkThenTeleport(91, 649, 92, 649, false);
					}
				} else {
					playerTalk(p, n,
						"Oh dear I don't actually seem to have enough money");
				}
				break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BORDER_GUARD_ALKHARID.id() || n.getID() == NpcId.BORDER_GUARD_LUMBRIDGE.id();
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 180 && command.equals("open")) {
			player.message("You need to talk to the border guard");
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return obj.getID() == 180 && command.equals("open");
	}
}
