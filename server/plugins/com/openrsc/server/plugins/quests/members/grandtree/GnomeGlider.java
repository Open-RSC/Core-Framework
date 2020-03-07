package com.openrsc.server.plugins.quests.members.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeGlider implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.GNOME_PILOT_GRANDTREE.id(), NpcId.GNOME_PILOT_KARAMJA_BROKEN.id(),
				NpcId.GNOME_PILOT_KARAMJA.id(), NpcId.GNOME_PILOT_VARROCK.id(), NpcId.GNOME_PILOT_ALKHARID.id(), NpcId.GNOME_PILOT_WHITEMOUNTAIN.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.GNOME_PILOT_VARROCK.id()) {
			say(p, n, "hello again");
			npcsay(p, n, "well hello adventurer",
				"as you can see we crashed on impact",
				"i don't think it'll fly again",
				"sorry but you'll have to walk");
		}
		else if (n.getID() == NpcId.GNOME_PILOT_KARAMJA.id() || n.getID() == NpcId.GNOME_PILOT_ALKHARID.id() || n.getID() == NpcId.GNOME_PILOT_WHITEMOUNTAIN.id()) {
			if (p.getQuestStage(Quests.GRAND_TREE) == -1) {
				say(p, n, "hello again");
				npcsay(p, n, "well hello adventurer");
				npcsay(p, n, "would you like to go to the tree gnome stronghold?");
				int travelBackMenu = multi(p, n,
					"ok then",
					"no thanks");
				if (travelBackMenu == 0) {
					npcsay(p, n, "ok, hold on tight");
					Functions.mes(p, "you both hold onto the wooden beam",
						"you take a few steps backand rush forwards",
						"the glider just lifts of the ground");
					p.teleport(221, 3567);
					say(p, n, "whhaaaaaaaaaagghhh");
					p.teleport(414, 2995);
				}
				return;
			}
			say(p, n, "hello");
			npcsay(p, n, "hello traveller");
		}
		else if (n.getID() == NpcId.GNOME_PILOT_GRANDTREE.id()) {
			say(p, n, "hello");
			if (p.getQuestStage(Quests.GRAND_TREE) == -1) {
				npcsay(p, n, "well hello again traveller");
				npcsay(p, n, "can i take you somewhere?");
				npcsay(p, n, "i can fly like the birds");
				int menu = multi(p, n, false, //do not send over
					"karamja",
					"varrock",
					"Al kharid",
					"white wolf mountain",
					"I'll stay here thanks");
				if (menu == 0) {
					say(p, n, "take me to karamja");
					npcsay(p, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					Functions.mes(p, "you hold on tight to the glider's wooden beam",
						"the pilot leans back and then pushes the glider forward",
						"you float softly off the grand tree");
					p.teleport(221, 3567);
					say(p, n, "whhaaaaaaaaaagghhh");
					p.teleport(389, 753);
					say(p, n, "ouch");
				} else if (menu == 1) {
					say(p, n, "take me to Varrock");
					npcsay(p, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					Functions.mes(p, "you hold on tight to the glider's wooden beam",
						"the pilot leans back and then pushes the glider forward",
						"you float softly off the grand tree");
					p.teleport(221, 3567);
					say(p, n, "whhaaaaaaaaaagghhh");
					p.teleport(58, 504);
					say(p, n, "ouch");
				} else if (menu == 2) {
					say(p, n, "take me to Al kharid");
					npcsay(p, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					Functions.mes(p, "you hold on tight to the glider's wooden beam",
						"the pilot leans back and then pushes the glider forward",
						"you float softly off the grand tree");
					p.teleport(221, 3567);
					say(p, n, "whhaaaaaaaaaagghhh");
					p.teleport(88, 664);
					say(p, n, "ouch");
				} else if (menu == 3) {
					say(p, n, "take me to White wolf mountain");
					npcsay(p, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					Functions.mes(p, "you hold on tight to the glider's wooden beam",
						"the pilot leans back and then pushes the glider forward",
						"you float softly off the grand tree");
					p.teleport(221, 3567);
					say(p, n, "whhaaaaaaaaaagghhh");
					p.teleport(400, 461);
					say(p, n, "ouch");
				} else if (menu == 4) {
					say(p, n, "i'll stay here thanks");
					npcsay(p, n, "no worries, let me know if you change your mind");
				}
				return;
			} else if (p.getQuestStage(Quests.GRAND_TREE) >= 8 && p.getQuestStage(Quests.GRAND_TREE) <= 9) {
				npcsay(p, n, "hi, the king said that you need to leave");
				say(p, n, "yes, apparently humans are invading");
				npcsay(p, n, "i find that hard to believe",
					"i have lots of human friends");
				say(p, n, "it seems a bit strange to me");
				npcsay(p, n, "well, would you like me to take you somewhere?");
				int menu = multi(p, n,
					"actually yes, take me to karamja",
					"no thanks i'm going to hang around");
				if (menu == 0) {
					npcsay(p, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					Functions.mes(p, "you hold on tight to the glider's wooden beam",
						"the pilot leans back and then pushes the glider forward",
						"you float softly off the grand tree");
					p.teleport(221, 3567);
					say(p, n, "whhaaaaaaaaaagghhh");
					p.teleport(425, 764);
					say(p, n, "ouch");
					Npc GNOME_PILOT = ifnearvisnpc(p, NpcId.GNOME_PILOT_KARAMJA_BROKEN.id(), 5);
					npcsay(p, GNOME_PILOT, "ouch");
					p.message("you crash in south karamja");
					npcsay(p, GNOME_PILOT, "sorry about that, are you ok");
					say(p, GNOME_PILOT, "i seem to be fine, can't say the same for your glider");
					npcsay(p, GNOME_PILOT, "i don't think i can fix this",
						"looks like we'll be heading back by foot",
						"i hope you find what you came for adventurer");
					say(p, GNOME_PILOT, "me too, take care little man");
					npcsay(p, GNOME_PILOT, "traveller watch out");
					Npc JOGRE = ifnearvisnpc(p, NpcId.JOGRE.id(), 15);
					if (JOGRE != null) {
						npcsay(p, JOGRE, "grrrrr");
						JOGRE.setChasing(p);
					}
				} else if (menu == 1) {
					npcsay(p, n, "ok, i'll be here if you need me");
				}
				return;
			}
			npcsay(p, n, "hello traveller");
		}
		else if (n.getID() == NpcId.GNOME_PILOT_KARAMJA_BROKEN.id()) {
			p.message("The Gnome pilot does not appear interested in talking");
		}
	}
}
