package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MagicInstructor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island magic instructor
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getSkills().getExperience(6) > 0 && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 80) {
			npcTalk(p, n, "Please proceed through the next door");
			return;
		}
		if(p.getSkills().getExperience(6) > 0 && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 78) {
			npcTalk(p, n, "Well done",
					"As you get a higher magic level",
					"You will be able to cast all sorts of interesting spells",
					"Now go through the next door");
			p.getCache().set("tutorial", 80);
			return;
		}
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 76) {
			Npc chicken = getNearestNpc(p, 3, 10);
			if(chicken != null) {
				npcTalk(p, chicken, "cluck");
			}
			npcTalk(p, n, "Aha a chicken",
					"An ideal wind strike target",
					"ok click on the wind strike spell in your spell list",
					"then click on the chicken to chose it as a target");
			p.getCache().set("tutorial", 78);
			return;
		}
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 75 && p.getSkills().getExperience(6) <= 0) {
			playerTalk(p, n, "I don't have the runes to cast wind strike");
			npcTalk(p, n, "How do you expect to do magic without runes?",
					"Ok I shall have to provide you with runes");
			p.message("The instructor gives you some runes");
			addItem(p, 33, 12);
			addItem(p, 35, 8);
			addItem(p, 32, 3);
			addItem(p, 34, 2);
			addItem(p, 36, 1);
			npcTalk(p, n, "Ok look at your spell list now",
					"You will see you have the runes for the spell",
					"And it shows up yellow in your list");
			p.getCache().set("tutorial", 76);
			return;
		}
		npcTalk(p, n, "there's good magic potential in this one",
				"Yes definitely something I can work with");
		int menu = showMenu(p, n, "Hmm are you talking about me?", "teach me some magic");
		if(menu == 0) {
			npcTalk(p, n, "Yes that is the one of which I speak");
			optionDialogue(p, n);
		} else if(menu == 1) {
			npcTalk(p, n, "Teacher, yes I am one of them");
			optionDialogue(p, n);
		}
	
	}
	
	private void optionDialogue(Player p, Npc n) {
		npcTalk(p, n, "Ok move your mouse over the book icon on the menu bar",
				"this is your magic menu",
				"You will see at level 1 magic you can only cast wind strike",
				"move your mouse over the wind strike text",
				"If you look at the bottom of the magic window",
				"You will see more information about the spell",
				"runes required for the spell have two numbers over them",
				"The first number is how many runes you have",
				"The second is how many runes the spell requires",
				"Speak to me again when you have checked this");
		p.getCache().set("tutorial", 75);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 494;
	}

}
