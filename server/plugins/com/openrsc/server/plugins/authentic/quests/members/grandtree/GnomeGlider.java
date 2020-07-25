package com.openrsc.server.plugins.authentic.quests.members.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeGlider implements TalkNpcTrigger, OpLocTrigger {

	@Override
	public boolean blockOpLoc(Player n, GameObject obj, String command) {
		return obj.getID() == 618;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		mes("only the gnomes can fly these");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.GNOME_PILOT_GRANDTREE.id(), NpcId.GNOME_PILOT_KARAMJA_BROKEN.id(),
				NpcId.GNOME_PILOT_KARAMJA.id(), NpcId.GNOME_PILOT_VARROCK.id(), NpcId.GNOME_PILOT_ALKHARID.id(), NpcId.GNOME_PILOT_WHITEMOUNTAIN.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GNOME_PILOT_VARROCK.id()) {
			say(player, n, "hello again");
			npcsay(player, n, "well hello adventurer",
				"as you can see we crashed on impact",
				"i don't think it'll fly again",
				"sorry but you'll have to walk");
		}
		else if (n.getID() == NpcId.GNOME_PILOT_KARAMJA.id() || n.getID() == NpcId.GNOME_PILOT_ALKHARID.id() || n.getID() == NpcId.GNOME_PILOT_WHITEMOUNTAIN.id()) {
			if (player.getQuestStage(Quests.GRAND_TREE) == -1) {
				say(player, n, "hello again");
				npcsay(player, n, "well hello adventurer");
				npcsay(player, n, "would you like to go to the tree gnome stronghold?");
				int travelBackMenu = multi(player, n,
					"ok then",
					"no thanks");
				if (travelBackMenu == 0) {
					npcsay(player, n, "ok, hold on tight");
					mes("you both hold onto the wooden beam");
					delay(3);
					mes("you take a few steps backand rush forwards");
					delay(3);
					mes("the glider just lifts of the ground");
					delay(3);
					player.teleport(221, 3567);
					say(player, n, "whhaaaaaaaaaagghhh");
					player.teleport(414, 2995);
				}
				return;
			}
			say(player, n, "hello");
			npcsay(player, n, "hello traveller");
		}
		else if (n.getID() == NpcId.GNOME_PILOT_GRANDTREE.id()) {
			say(player, n, "hello");
			if (player.getQuestStage(Quests.GRAND_TREE) == -1) {
				npcsay(player, n, "well hello again traveller");
				npcsay(player, n, "can i take you somewhere?");
				npcsay(player, n, "i can fly like the birds");
				int menu = multi(player, n, false, //do not send over
					"karamja",
					"varrock",
					"Al kharid",
					"white wolf mountain",
					"I'll stay here thanks");
				if (menu == 0) {
					say(player, n, "take me to karamja");
					npcsay(player, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					mes("you hold on tight to the glider's wooden beam");
					delay(3);
					mes("the pilot leans back and then pushes the glider forward");
					delay(3);
					mes("you float softly off the grand tree");
					delay(3);
					player.teleport(221, 3567);
					say(player, n, "whhaaaaaaaaaagghhh");
					player.teleport(389, 753);
					say(player, n, "ouch");
				} else if (menu == 1) {
					say(player, n, "take me to Varrock");
					npcsay(player, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					mes("you hold on tight to the glider's wooden beam");
					delay(3);
					mes("the pilot leans back and then pushes the glider forward");
					delay(3);
					mes("you float softly off the grand tree");
					delay(3);
					player.teleport(221, 3567);
					say(player, n, "whhaaaaaaaaaagghhh");
					player.teleport(58, 504);
					say(player, n, "ouch");
				} else if (menu == 2) {
					say(player, n, "take me to Al kharid");
					npcsay(player, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					mes("you hold on tight to the glider's wooden beam");
					delay(3);
					mes("the pilot leans back and then pushes the glider forward");
					delay(3);
					mes("you float softly off the grand tree");
					delay(3);
					player.teleport(221, 3567);
					say(player, n, "whhaaaaaaaaaagghhh");
					player.teleport(88, 664);
					say(player, n, "ouch");
				} else if (menu == 3) {
					say(player, n, "take me to White wolf mountain");
					npcsay(player, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					mes("you hold on tight to the glider's wooden beam");
					delay(3);
					mes("the pilot leans back and then pushes the glider forward");
					delay(3);
					mes("you float softly off the grand tree");
					delay(3);
					player.teleport(221, 3567);
					say(player, n, "whhaaaaaaaaaagghhh");
					player.teleport(400, 461);
					say(player, n, "ouch");
				} else if (menu == 4) {
					say(player, n, "i'll stay here thanks");
					npcsay(player, n, "no worries, let me know if you change your mind");
				}
				return;
			} else if (player.getQuestStage(Quests.GRAND_TREE) >= 8 && player.getQuestStage(Quests.GRAND_TREE) <= 9) {
				npcsay(player, n, "hi, the king said that you need to leave");
				say(player, n, "yes, apparently humans are invading");
				npcsay(player, n, "i find that hard to believe",
					"i have lots of human friends");
				say(player, n, "it seems a bit strange to me");
				npcsay(player, n, "well, would you like me to take you somewhere?");
				int menu = multi(player, n,
					"actually yes, take me to karamja",
					"no thanks i'm going to hang around");
				if (menu == 0) {
					npcsay(player, n, "ok, your the boss, jump on",
						"hold on tight, it'll be a rough ride");
					mes("you hold on tight to the glider's wooden beam");
					delay(3);
					mes("the pilot leans back and then pushes the glider forward");
					delay(3);
					mes("you float softly off the grand tree");
					delay(3);
					player.teleport(221, 3567);
					say(player, n, "whhaaaaaaaaaagghhh");
					player.teleport(425, 764);
					say(player, n, "ouch");
					Npc GNOME_PILOT = ifnearvisnpc(player, NpcId.GNOME_PILOT_KARAMJA_BROKEN.id(), 5);
					if (GNOME_PILOT != null) {
						npcsay(player, GNOME_PILOT, "ouch");
						player.message("you crash in south karamja");
						npcsay(player, GNOME_PILOT, "sorry about that, are you ok");
						say(player, GNOME_PILOT, "i seem to be fine, can't say the same for your glider");
						npcsay(player, GNOME_PILOT, "i don't think i can fix this",
							"looks like we'll be heading back by foot",
							"i hope you find what you came for adventurer");
						say(player, GNOME_PILOT, "me too, take care little man");
						npcsay(player, GNOME_PILOT, "traveller watch out");
					}
					Npc JOGRE = ifnearvisnpc(player, NpcId.JOGRE.id(), 15);
					if (JOGRE != null) {
						npcsay(player, JOGRE, "grrrrr");
						JOGRE.setChasing(player);
					}
				} else if (menu == 1) {
					npcsay(player, n, "ok, i'll be here if you need me");
				}
				return;
			}
			npcsay(player, n, "hello traveller");
		}
		else if (n.getID() == NpcId.GNOME_PILOT_KARAMJA_BROKEN.id()) {
			player.message("The Gnome pilot does not appear interested in talking");
		}
	}
}
