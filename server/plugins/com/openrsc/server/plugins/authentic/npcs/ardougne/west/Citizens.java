package com.openrsc.server.plugins.authentic.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Citizens implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		int menu;
		Npc citizen;
		switch(NpcId.getById(npc.getID())) {
			case CITIZEN_TIRED:
				say(player, npc, "good day");
				npcsay(player, npc, "We don't have good days here anymore",
					"Curse King Tyras");
				menu = multi(player, npc,
					"Oh ok bad day then",
					"Why what has he done?",
					"I'm looking for a woman called Elena");
				if (menu == 0) {
					// just makes reply of player
				} else if (menu == 1) {
					npcsay(player, npc, "His army curses are city with this plague",
						"Then wanders off again",
						"leaving us to clear up the pieces");
				} else if (menu == 2) {
					npcsay(player, npc, "Not heard of her");
				}
				break;
			case CITIZEN_FRIGHTENED:
				say(player, npc, "good day");
				npcsay(player, npc, "an outsider!",
					"Can you get me out of this hell hole?");
				say(player, npc, "Sorry that is not what I am here to do");
				break;
			case CITIZEN_FRUSTRATED:
				say(player, npc, "Hello how's it going");
				npcsay(player, npc, "Bah Those mourners they're meant to be helping us",
					"but I think they're doing more harm here than good",
					"They won't even let me send a letter out to my family");
				menu = multi(player, npc,
					"Have you seen a lady called Elena around here?",
					"You should stand up to them more");
				if (menu == 0) {
					npcsay(player, npc, "Yes I've seen her",
						"Very helpful person",
						"Not for the last few days though",
						"I thought maybe she'd gone home");
				} else if (menu == 1) {
					npcsay(player, npc, "Oh I'm not one to cause a fuss");
				}
				break;
			case CITIZEN_ANGRY:
				say(player, npc, "Hello there");
				npcsay(player, npc, "Go away",
					"People from the outside shut us in like animals",
					"I have nothing to say to you");
				break;
			case CITIZEN_DISILLUSIONED:
				say(player, npc, "Hello, how's it going?");
				npcsay(player, npc, "Life is tough");
				menu = multi(player, npc,
					"Yes living in a plague city must be hard",
					"I'm sorry to hear that",
					"I'm looking for a lady called Elena");
				if (menu == 0) {
					npcsay(player, npc, "Plague?",
						"pah that's no excuse for the treatment we've received",
						"Its obvious pretty quickly if someone has the plague",
						"I'm thinking about making a break for it",
						"I'm perfectly healthy",
						"Not gonna infect anyone");
				} else if (menu == 1) {
					npcsay(player, npc, "Well aint much either you or me can do about it");
				} else if (menu == 2) {
					npcsay(player, npc, "I've not heard of her",
						"Old Jethick knows lots of people",
						"Maybe he'll no where you can find her");
				}
				break;
			case RECRUITER:
				npcsay(player, npc, "Citizens of West Ardougne",
					"who will join the Royal army of Ardougne?",
					"It is a very noble cause",
					"Fight alongside king Tyras",
					"Crusading in the darklands of the west");
				boolean treason = false;
				// if no citizens found nearby doesn't get to say
				citizen = ifnearvisnpc(player, NpcId.CITIZEN_TIRED.id(), 10);
				if (citizen != null) {
					npcsay(player, citizen, "Go away - we don't support your army");
					treason = true;
				}
				citizen = ifnearvisnpc(player, NpcId.CITIZEN_FRUSTRATED.id(), 10);
				if (citizen != null) {
					npcsay(player, citizen, "Plaguebringer!");
					treason = true;
				}
				citizen = ifnearvisnpc(player, NpcId.CITIZEN_ANGRY.id(), 10);
				if (citizen != null) {
					npcsay(player, citizen, "King Tyras is scum");
					treason = true;
				}
				// unknown if all 3 citizens in scene weren't found if the line below would have been said
				if (treason) {
					npcsay(player, npc, "Tyras will be informed of these words of treason");
					delay();
					mes("Someone throws a tomato at the recruiter");
					delay(3);
					player.getWorld().registerItem(new GroundItem(
						player.getWorld(),
						ItemId.TOMATO.id(),
						DataConversions.random(npc.getX() - 1, npc.getX() + 1),
						DataConversions.random(npc.getY() - 1, npc.getY() + 1),
						1, player));
				}
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.CITIZEN_TIRED.id(), NpcId.CITIZEN_FRIGHTENED.id(), NpcId.CITIZEN_FRUSTRATED.id(),
			NpcId.CITIZEN_ANGRY.id(), NpcId.CITIZEN_DISILLUSIONED.id(), NpcId.RECRUITER.id());
	}
}
