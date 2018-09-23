package com.openrsc.server.plugins.npcs.karamja;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.model.entity.GameObject;

public final class BoatFromKaramja implements TalkToNpcExecutiveListener,
		TalkToNpcListener, ObjectActionListener, ObjectActionExecutiveListener  {

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		int option = showMenu(p, n, "Can I board this ship?",
				"Does Karamja have any unusual customs then?");
		if (option == 0) {
			npcTalk(p, n, "You need to be searched before you can board");
			int sub_opt = showMenu(p, n, "Why?",
					"You're not putting your hands on my things!",
					"Search away I have nothing to hide");
			if (sub_opt == 0) {
				npcTalk(p, n,
						"Because asgarnia has banned the import of intoxicating spirits");
			} else if (sub_opt == 1) {
				npcTalk(p, n, "You're not getting on this ship then");
			} else if (sub_opt == 2) {
				if (hasItem(p, 318, 1)) {
					message(p, "The customs officer confiscates your rum");
					removeItem(p, 318, 1);
				} else {
					npcTalk(p,
							n,
							"Well you've got some odd stuff, but it's all legal",
							"Now you need to pay a boarding charge of 30 gold");
					if (showMenu(p, n, "Ok", "Oh, I'll not bother then") == 0) {
						if (removeItem(p, 10, 30)) {
							message(p, "You pay 30 gold", "You board the ship");
							movePlayer(p, 269, 648, true);
							p.message("You arrive at Port Sarim");
						} else { // not enough money
							playerTalk(p, n,
									"Oh dear it seems i don't have enough money");
						}
					}
				}
			}
		} else if (option == 1) {
			npcTalk(p, n, "I'm not that sort of customs officer");
		}
	}
	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 161 || (obj.getID() == 162) || (obj.getID() == 163))  {
			if(command.equals("board")) {
				if(p.getY() != 713 ) {
					return;
			}
			Npc officer = getNearestNpc(p, 163, 4);
				if(officer != null) {
					officer.initializeTalkScript(p);
					} else {
						p.message("I need to speak to the customs officer before boarding the ship.");
				}
			}
		}
	}
	
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 163;
	}
	@Override
	public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 161 && arg0.getLocation().equals(Point.location(326, 710)))
				|| (arg0.getID() == 163 && arg0.getLocation().equals(Point.location(319, 710)))
				|| (arg0.getID() == 162 && arg0.getLocation().equals(Point.location(324, 710)));
	}
}
