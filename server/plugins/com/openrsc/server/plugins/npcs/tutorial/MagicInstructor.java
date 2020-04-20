package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.ifnearvisnpc;
import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.multi;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class MagicInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island magic instructor
	 */
	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 70) {
			npcsay(p, n, "there's good magic potential in this one",
					"Yes definitely something I can work with");
				int menu = Functions.multi(p, n, "Hmm are you talking about me?", "teach me some magic");
				if (menu == 0) {
					npcsay(p, n, "Yes that is the one of which I speak");
					optionDialogue(p, n);
				} else if (menu == 1) {
					npcsay(p, n, "Teacher, yes I am one of them");
					optionDialogue(p, n);
				}
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 75) {
			Functions.say(p, n, "I don't have the runes to cast wind strike");
			npcsay(p, n, "How do you expect to do magic without runes?",
				"Ok I shall have to provide you with runes");
			p.message("The instructor gives you some runes");
			give(p, ItemId.AIR_RUNE.id(), 12);
			give(p, ItemId.MIND_RUNE.id(), 8);
			give(p, ItemId.WATER_RUNE.id(), 3);
			give(p, ItemId.EARTH_RUNE.id(), 2);
			give(p, ItemId.BODY_RUNE.id(), 1);
			npcsay(p, n, "Ok look at your spell list now",
				"You will see you have the runes for the spell",
				"And it shows up yellow in your list");
			p.getCache().set("tutorial", 76);
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 76) {
			Npc chicken = ifnearvisnpc(p, NpcId.CHICKEN.id(), 10);
			if (chicken != null) {
				npcsay(p, chicken, "cluck");
			}
			npcsay(p, n, "Aha a chicken",
				"An Ideal wind strike target",
				"ok click on the wind strike spell in your spell list",
				"then click on the chicken to chose it as a target");
			p.getCache().set("tutorial", 77);
		} else {
			npcsay(p, n, "Well done",
					"As you get a higher magic level",
					"You will be able to cast all sorts of interesting spells",
					"Now go through the next door");
			if (p.getCache().getInt("tutorial") < 80)
				p.getCache().set("tutorial", 80);
		}
	}

	private void optionDialogue(Player p, Npc n) {
		npcsay(p, n, "Ok move your mouse over the book icon on the menu bar",
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
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.MAGIC_INSTRUCTOR.id();
	}

}
