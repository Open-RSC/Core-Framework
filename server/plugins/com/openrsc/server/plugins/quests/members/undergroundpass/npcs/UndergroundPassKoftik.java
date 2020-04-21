package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassKoftik implements QuestInterface, TalkNpcTrigger {
	/**
	 * Note: King Lathas (Quest starer) is located in the Biohazard quest template
	 **/

	@Override
	public int getQuestId() {
		return Quests.UNDERGROUND_PASS;
	}

	@Override
	public String getQuestName() {
		return "Underground pass (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@You haved gained 5 quest points!");
		int[] questData = p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.UNDERGROUND_PASS);
		//keep order kosher
		int[] skillIDs = {Skills.AGILITY, Skills.ATTACK};
		for (int i = 0; i < skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			incQuestReward(p, questData, i == (skillIDs.length - 1));
		}
		p.message("you have completed the underground pass quest");
		p.getCache().set("Iban blast_casts", 25);
		p.getCache().remove("advised_koftik");
	}

	/**
	 * fast dialogues
	 **/
	public static void koftikEnterCaveDialogue(Player p, Npc n) {
		say(p, n, "hello there, are you the kings scout?");
		npcsay(p, n, "that i am brave adventurer",
			"King lathas informed me that you need to cross these mountains",
			"i'm afraid you'll have to go through the ancient underground pass");
		say(p, n, "That's ok, i've travelled through many a cave in my time");
		npcsay(p, n, "these caves are different..they're filled with the spirit of Zamorak",
			"You can feel it as you wind your way round the stalactites..",
			"an icy chill that penetrate's the very fabric of your being",
			"not so many travellers come down here these days...",
			"...but there are some who are still foolhardy enough");
		p.updateQuestStage(Quests.UNDERGROUND_PASS, 2);
		int menu = multi(p, n,
			"i'll take my chances",
			"tell me more");
		if (menu == 0) {
			npcsay(p, n, "ok traveller, i'll catch up with you by the bridge");
		} else if (menu == 1) {
			npcsay(p, n, "I remember seeing one such warrior. Going by the name of Randas...",
				"..he stood tall and proud like an elven king...",
				"..that same pride made him vulnerable to Zamorak's calls...",
				"..Randas' worthy desire to be a great and mighty warrior...",
				"..also made him corruptible to Zamorak's promises of glory",
				"..Zamorak showed him a way to achieve his goals, by appealing...",
				"..to that most base and dark nature that resides in all of us");
			say(p, n, "what happened to him?");
			npcsay(p, n, "no one knows");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.KOFTIK_ARDOUGNE.id(), NpcId.KOFTIK_CAVE1.id(), NpcId.KOFTIK_CAVE2.id(), NpcId.KOFTIK_CAVE3.id(), NpcId.KOFTIK_CAVE4.id(), NpcId.KOFTIK_RECOVERED.id());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KOFTIK_ARDOUGNE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 1:
					koftikEnterCaveDialogue(p, n);
					break;
				case 2:
					npcsay(p, n, "i know it's scary in there",
						"but you'll have to go in alone",
						"i'll catch up as soon as i can");
					break;
				case 3:
				case 4:
					say(p, n, "hello koftik");
					if (p.getCache().hasKey("orb_of_light1") && p.getCache().hasKey("orb_of_light2") && p.getCache().hasKey("orb_of_light3") && p.getCache().hasKey("orb_of_light4") || p.getQuestStage(this) == 4) {
						npcsay(p, n, "it scares me in there",
							"the voices, don't you hear them?");
						say(p, n, "you'll be ok koftik");
						return;
					}
					npcsay(p, n, "once your over the bridge keep going...",
						"..straight ahead, i'll meet you further up");
					break;
				// nothing interesting on stage 5,6,7
				case 5:
				case 6:
				case 7:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					say(p, n, "thanks for getting me out koftik");
					npcsay(p, n, "always a pleasure squire",
						"have you informed the king about iban?");
					int menu = multi(p, n,
						"no, not yet",
						"yes, i've told him");
					if (menu == 0) {
						npcsay(p, n, "traveller this is no time to linger",
							"the king must know that ibans dead",
							"this is a truly historical moment for ardounge");
					} else if (menu == 1) {
						npcsay(p, n, "good to hear, the sooner we find king Tyras..",
							"the better");
					}
					break;
				case -1:
					say(p, n, "hello koftik");
					npcsay(p, n, "hello adventurer, how's things?");
					say(p, n, "not bad, yourself?");
					npcsay(p, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE1.id()) {
			switch (p.getQuestStage(this)) {
				case 2:
					say(p, n, "koftik, how can we cross the bridge?");
					npcsay(p, n, "i'm not sure, seems as if others were here before us though");
					if (!p.getCarriedItems().hasCatalogID(ItemId.DAMP_CLOTH.id(), Optional.empty())) {
						npcsay(p, n, "i found this cloth amongst the charred remains of arrows");
						say(p, n, "charred arrows?");
						npcsay(p, n, "they must have been trying to burn something");
						say(p, n, "or someone!");
						give(p, ItemId.DAMP_CLOTH.id(), 1);
					}
					say(p, n, "interesting, we better keep our eyes open");
					npcsay(p, n, "There also seems to the remains of a diary");
					int menu = multi(p, n, false, //do not send over
							"not to worry, probably just kid litter", "what does it say?");
					if (menu == 0) {
						say(p, n, "not to worry, probably just litter");
						npcsay(p, n, "well..maybe?");
					} else if (menu == 1) {
						say(p, n, "what does it say?");
						mes(p, "@red@it seems to be written by the adventurer Randas, it reads...",
							"@red@It began as a whisper in my ears. Dismissing the sounds...",
							"@red@..as the whistling of the wind, I steeled myself against...",
							"@red@..these forces and continued on my way",
							"@red@But the whispers became moans...",
							"@red@at once fearsome and enticing like the call of some beautiful siren",
							"@red@Join us! The voices cried, Join us!",
							"@red@Your greatness lies within you, but only Zamorak can unlock your potential..");
						say(p, n, "it sounds like randas was losing it");
					}
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case -1:
					say(p, n, "hi koftik");
					if (!p.getCarriedItems().hasCatalogID(ItemId.DAMP_CLOTH.id(), Optional.empty())) {
						give(p, ItemId.DAMP_CLOTH.id(), 1);
						p.message("koftik gives you a damp cloth");
					}
					break;
				case 8:
					say(p, n, "thanks for getting me out koftik");
					npcsay(p, n, "always a pleasure squire");
					if (!p.getCarriedItems().hasCatalogID(ItemId.DAMP_CLOTH.id(), Optional.empty())) {
						give(p, ItemId.DAMP_CLOTH.id(), 1);
						p.message("koftik gives you a damp cloth");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE2.id()) {
			switch (p.getQuestStage(this)) {
				case 3:
				case 4:
					say(p, n, "hello koftik");
					npcsay(p, n, "how are you bearing adventurer?");
					say(p, n, "i'm still alive, and you?");
					npcsay(p, n, "cold, i can feel it in my blood, so cold");
					p.message("koftik seems to be poorly");
					say(p, n, "where do we go now koftik?");
					npcsay(p, n, "straight on again, more winding passages",
						"more lethal traps, more blood and more pain",
						"blood..pain.. hee hee,  more blood.. hee hee");
					say(p, n, "are you sure you're ok?");
					npcsay(p, n, "erm..yes..i'll be fine, just go ahead i'll catch up");
					break;
				//nothing interesting happens on stage 5,6,7
				case 5:
				case 6:
				case 7:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					say(p, n, "thanks for getting me out koftik");
					npcsay(p, n, "always a pleasure squire");
					break;
				case -1:
					say(p, n, "hello koftik");
					npcsay(p, n, "hello adventurer, how's things?");
					say(p, n, "not bad, yourself?");
					npcsay(p, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE3.id()) {
			switch (p.getQuestStage(this)) {
				case 3:
				case 4:
					say(p, n, "hello koftik");
					if (p.getQuestStage(this) == 4) {
						npcsay(p, n, "are you ok?, i heard a rumble further down the cavern",
							"i thought the whole place was going to cave in");
						say(p, n, "im fine");
					} else {
						npcsay(p, n, "keep back foul beast of the nigh.. ,wait, it's you!");
						say(p, n, "as far as i know");
					}
					npcsay(p, n, "i assumed you were dead, or worse");
					say(p, n, "i've managed to survive so far");
					npcsay(p, n, "the passsage ahead's blocked ,but you should be able to get through",
						"i'll follow behind",
						"aaaaaarrgghhh");
					say(p, n, "what's wrong?");
					npcsay(p, n, "it's the voices, can't you hear them?",
						"they wont leave be",
						"i feel him calling to me");
					break;
				//nothing interesting happens on stage 5,6,7
				case 5:
				case 6:
				case 7:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					say(p, n, "thanks for getting me out koftik");
					npcsay(p, n, "always a pleasure squire");
					break;
				case -1:
					say(p, n, "hello koftik");
					npcsay(p, n, "hello adventurer, how's things?");
					say(p, n, "not bad, yourself?");
					npcsay(p, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE4.id()) {
			p.message("The Koftik does not appear interested in talking");
		}
		else if (n.getID() == NpcId.KOFTIK_RECOVERED.id()) {
			switch (p.getQuestStage(this)) {
				case 8:
					npcsay(p, n, "traveller, where am i?, i can't remeber a thing");
					say(p, n, "we were losing you to ibans influence");
					npcsay(p, n, "what?..of corse, the voices",
						"but they've stopped, what happened?");
					say(p, n, "ibans dead, i destroyed him");
					npcsay(p, n, "you've done well, now we must inform the king",
						"he'll have to send in some high mages to...",
						"reserrect the well of voyage",
						"follow me, i'll lead you out");
					say(p, n, "at last!, i've had enough of caves");
					mes(p, "koftik leads you back up through the winding caverns");
					p.teleport(714, 581);
					p.message("and back to the cave entrance");
					break;
			}
		}
	}
}

