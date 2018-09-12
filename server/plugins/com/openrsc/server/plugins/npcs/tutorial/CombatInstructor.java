package com.openrsc.server.plugins.npcs.tutorial;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class CombatInstructor implements TalkToNpcExecutiveListener, TalkToNpcListener, PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {
	/**
	 * @author Davve
	 * Tutorial island combat instructor
	 * Level-7 rat not the regular rat!!!!!!!
	 * YOUTUBE: NO XP GIVEN IN ANY COMBAT STAT BY KILLING THE RAT
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(!hasItem(p, 4, 1) && (!hasItem(p, 70, 1)) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 15) {
			npcTalk(p, n, "Aha a new recruit",
					"I'm here to teach you the basics of fighting",
					"First of all you need weapons");
			addItem(p, 70, 1); // Add bronze long sword to the players inventory
			addItem(p, 4, 1); // Add wooden shield to the players inventory
			message(p, "The instructor gives you a sword and shield");
			npcTalk(p, n, "look after these well",
					"These items will now have appeared in your inventory",
					"You can access them by selecting the bag icon in the menu bar",
					"which can be found in the top right hand corner of the screen",
					"To wield your weapon and shield left click on them within your inventory",
					"their box will go red to show you are wearing them");
			p.message("when you have done this speak to the combat instructor again");
		} 
		else if(p.getInventory().wielding(4) && p.getInventory().wielding(70) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 15) {
			npcTalk(p, n, "Today we're going to be killing giant rats");
			Npc rat = getNearestNpc(p, 473, 10);
			if(rat != null) {
				npcTalk(p, rat, "squeek");
			}
			npcTalk(p, n, "move your mouse over a rat you will see it is level 7", 
					"You will see that it's level is written in green",
					"If it is green this means you have a strong chance of killing it",
					"creatures with their name in red should probably be avoided",
					"As this indicates they are tougher than you",
					"left click on the rat to attack it");
		}
		else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 20) {
			npcTalk(p, n,"Well done you're a born fighter",
					"As you kill things",
					"Your combat experience will go up",
					"this experience will slowly cause you to get tougher",
					"eventually you will be able to take on stronger enemies",
					"Such as those found in dungeons",
					"Now continue to the building to the northeast");
			p.getCache().set("tutorial", 25);
		}
		else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 25) {
			npcTalk(p, n, "Please proceed through the next door");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 474;
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == 473) {
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(n.getID() == 473) {
			n.resetCombatEvent();
			n.remove();
			// GIVE NO XP ACCORDING TO YOUTUBE VIDEOS FOR COMBAT SINCE IT WAS HEAVILY ABUSED IN REAL RSC TO TRAIN ON THOSE RATS.
			if((p.getInventory().wielding(4) || p.getInventory().wielding(70)) && 
					p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 15) { // either
				message(p, "Well done you've killed the rat",
						"Now speak to the combat instructor again");
				p.getCache().set("tutorial", 20);
			}
		}
	}
}
