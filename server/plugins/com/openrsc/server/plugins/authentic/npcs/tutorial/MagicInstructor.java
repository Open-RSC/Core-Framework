package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class MagicInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island magic instructor
	 */
	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 70) {
			npcsay(player, n, "there's good magic potential in this one",
					"Yes definitely something I can work with");
				int menu = multi(player, n, "Hmm are you talking about me?", "teach me some magic");
				if (menu == 0) {
					npcsay(player, n, "Yes that is the one of which I speak");
					optionDialogue(player, n);
				} else if (menu == 1) {
					npcsay(player, n, "Teacher, yes I am one of them");
					optionDialogue(player, n);
				}
		} else if(player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 75) {
			say(player, n, "I don't have the runes to cast wind strike");
			npcsay(player, n, "How do you expect to do magic without runes?",
				"Ok I shall have to provide you with runes");
			player.message("The instructor gives you some runes");
			give(player, ItemId.AIR_RUNE.id(), 12);
			give(player, ItemId.MIND_RUNE.id(), 8);
			give(player, ItemId.WATER_RUNE.id(), 3);
			give(player, ItemId.EARTH_RUNE.id(), 2);
			give(player, ItemId.BODY_RUNE.id(), 1);
			npcsay(player, n, "Ok look at your spell list now",
				"You will see you have the runes for the spell",
				"And it shows up yellow in your list");
			player.getCache().set("tutorial", 76);
		} else if(player.getCache().hasKey("tutorial")
			&& (player.getCache().getInt("tutorial") == 76 || player.getCache().getInt("tutorial") == 77)) {
			Npc chicken = ifnearvisnpc(player, NpcId.CHICKEN.id(), 10);
			if (chicken == null) {
				// if no chicken around invokes one, see: https://youtu.be/EIOVbx6usE0?t=1095
				npcsay(player, n, "I think we need a chicken");
				player.message("The wizard waves his arms around and chants");
				chicken = addnpc(player.getWorld(), NpcId.CHICKEN.id(), 218, 755);
				ActionSender.sendTeleBubble(player, 218, 755, true);
				for (Player pe : player.getViewArea().getPlayersInView()) {
					ActionSender.sendTeleBubble(pe, 218, 755, true);
				}
			} else {
				if (player.getCache().getInt("tutorial") == 76) {
					npcsay(player, chicken, "cluck");
					npcsay(player, n, "Aha a chicken",
						"An Ideal wind strike target",
						"ok click on the wind strike spell in your spell list",
						"then click on the chicken to chose it as a target");
					player.getCache().set("tutorial", 77);
				} else {
					npcsay(player, n, "To shoot a wind strike at a chicken",
						"select the book icon in the menu bar",
						"then click on the yellow wind strike text",
						"then left click on the chicken to cast the spell");
					npcsay(player, chicken, "cluck");
					player.getCache().set("tutorial", 78);
				}
			}
		} else {
			// it is authentic that there is no real check on whether or not the user has cast magic.
			// the trigger for advancing is as shown here... just talk to the Magic Instructor a bunch.
			npcsay(player, n, "Well done",
					"As you get a higher magic level",
					"You will be able to cast all sorts of interesting spells",
					"Now go through the next door");
			if (player.getCache().getInt("tutorial") < 80)
				player.getCache().set("tutorial", 80);
		}
	}

	private void optionDialogue(Player player, Npc n) {
		npcsay(player, n, "Ok move your mouse over the book icon on the menu bar",
			"this is your magic menu",
			"You will see at level 1 magic you can only cast wind strike",
			"move your mouse over the wind strike text",
			"If you look at the bottom of the magic window",
			"You will see more information about the spell",
			"runes required for the spell have two numbers over them",
			"The first number is how many runes you have",
			"The second is how many runes the spell requires",
			"Speak to me again when you have checked this");
		player.getCache().set("tutorial", 75);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MAGIC_INSTRUCTOR.id();
	}

}
