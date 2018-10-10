package com.openrsc.server.plugins.npcs.brimhaven;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class BoatFromBrimhaven implements TalkToNpcExecutiveListener,
		TalkToNpcListener, ObjectActionListener, ObjectActionExecutiveListener {

	public static final int OFFICER = 317;
	
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "You need to be searched before you can board");
		int menu = showMenu(p, n, "Why?", "Search away I have nothing to hide",
				"You're not putting your hands on my things");
		if (menu == 0) {
			npcTalk(p, n,
					"Because Kandarin has banned the import of intoxicating spirits");
			int menu2 = showMenu(p, n, "Search away I have nothing to hide",
					"You're not putting your hands on my things");
			if (menu2 == 0) {
				payDialogue(p, n);
			} else if (menu2 == 1) {
				npcTalk(p, n, "You're not getting on this ship then");
			}
		} else if (menu == 1) {
			payDialogue(p, n);
		} else if (menu == 2) {
			npcTalk(p, n, "You're not getting on this ship then");
		}
	}

	public void payDialogue(Player p, Npc n) {
		if (hasItem(p, 318)) {
			message(p, "The custom officer searches you...");
			npcTalk(p, n,
					"What is this we found here? I'm going to have to confiscate that");
			p.getInventory().remove(318, -1);
		} else {
			npcTalk(p, n, "Well you've got some odd stuff, but it's all legal");
			npcTalk(p, n, "Now you need to pay a boarding charge of 30 gold");
		}
		int pay = showMenu(p, n, "Ok", "Oh, I'll not bother then");
		if (pay == 0) {
			if (p.getInventory().remove(10, 30) > -1) { // enough money
				message(p, "You pay 30 gold", "You board the ship");
				p.teleport(538, 617, false);
				sleep(800);
				message(p, "The ship arrives at Ardougne");
			} else {
				playerTalk(p, n, "Oh dear I don't seem to have enough money");
			}
		} else if (pay == 1) {
			// NOTHING
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 317;
	}


	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == 320 || (obj.getID() == 321))  {
			if(command.equals("board")) {
				if(p.getX() != 467 ) {
					return;
			}
			Npc official = getNearestNpc(p, OFFICER, 5);
				if(official != null) {		
					official.initializeTalkScript(p);
					} else {
						p.message("I need to speak to the customs official before boarding the ship.");
				}
			}
		}		
	}
	@Override
	public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 320 && arg0.getLocation().equals(Point.location(468, 651)))
				|| (arg0.getID() == 321 && arg0.getLocation().equals(Point.location(468, 646)));
	}
}
