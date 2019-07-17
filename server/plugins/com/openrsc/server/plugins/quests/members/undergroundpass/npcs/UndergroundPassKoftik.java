package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

public class UndergroundPassKoftik implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener {
	/**
	 * Note: King Lathas (Quest starer) is located in the Biohazard quest template
	 **/
	
	@Override
	public int getQuestId() {
		return Constants.Quests.UNDERGROUND_PASS;
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
		int[] questData = Quests.questData.get(Quests.UNDERGROUND_PASS);
		//keep order kosher
		int[] skillIDs = {SKILLS.AGILITY.id(), SKILLS.ATTACK.id()};
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
		playerTalk(p, n, "hello there, are you the kings scout?");
		npcTalk(p, n, "that i am brave adventurer",
			"King lathas informed me that you need to cross these mountains",
			"i'm afraid you'll have to go through the ancient underground pass");
		playerTalk(p, n, "That's ok, i've travelled through many a cave in my time");
		npcTalk(p, n, "these caves are different..they're filled with the spirit of Zamorak",
			"You can feel it as you wind your way round the stalactites..",
			"an icy chill that penetrate's the very fabric of your being",
			"not so many travellers come down here these days...",
			"...but there are some who are still foolhardy enough");
		p.updateQuestStage(Constants.Quests.UNDERGROUND_PASS, 2);
		int menu = showMenu(p, n,
			"i'll take my chances",
			"tell me more");
		if (menu == 0) {
			npcTalk(p, n, "ok traveller, i'll catch up with you by the bridge");
		} else if (menu == 1) {
			npcTalk(p, n, "I remember seeing one such warrior. Going by the name of Randas...",
				"..he stood tall and proud like an elven king...",
				"..that same pride made him vulnerable to Zamorak's calls...",
				"..Randas' worthy desire to be a great and mighty warrior...",
				"..also made him corruptible to Zamorak's promises of glory",
				"..Zamorak showed him a way to achieve his goals, by appealing...",
				"..to that most base and dark nature that resides in all of us");
			playerTalk(p, n, "what happened to him?");
			npcTalk(p, n, "no one knows");
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return inArray(n.getID(), NpcId.KOFTIK_ARDOUGNE.id(), NpcId.KOFTIK_CAVE1.id(), NpcId.KOFTIK_CAVE2.id(), NpcId.KOFTIK_CAVE3.id(), NpcId.KOFTIK_CAVE4.id(), NpcId.KOFTIK_RECOVERED.id());
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.KOFTIK_ARDOUGNE.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 1:
					koftikEnterCaveDialogue(p, n);
					break;
				case 2:
					npcTalk(p, n, "i know it's scary in there",
						"but you'll have to go in alone",
						"i'll catch up as soon as i can");
					break;
				case 3:
				case 4:
					playerTalk(p, n, "hello koftik");
					if (p.getCache().hasKey("orb_of_light1") && p.getCache().hasKey("orb_of_light2") && p.getCache().hasKey("orb_of_light3") && p.getCache().hasKey("orb_of_light4") || p.getQuestStage(this) == 4) {
						npcTalk(p, n, "it scares me in there",
							"the voices, don't you hear them?");
						playerTalk(p, n, "you'll be ok koftik");
						return;
					}
					npcTalk(p, n, "once your over the bridge keep going...",
						"..straight ahead, i'll meet you further up");
					break;
				// nothing interesting on stage 5,6,7
				case 5:
				case 6:
				case 7:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					playerTalk(p, n, "thanks for getting me out koftik");
					npcTalk(p, n, "always a pleasure squire",
						"have you informed the king about iban?");
					int menu = showMenu(p, n,
						"no, not yet",
						"yes, i've told him");
					if (menu == 0) {
						npcTalk(p, n, "traveller this is no time to linger",
							"the king must know that ibans dead",
							"this is a truly historical moment for ardounge");
					} else if (menu == 1) {
						npcTalk(p, n, "good to hear, the sooner we find king Tyras..",
							"the better");
					}
					break;
				case -1:
					playerTalk(p, n, "hello koftik");
					npcTalk(p, n, "hello adventurer, how's things?");
					playerTalk(p, n, "not bad, yourself?");
					npcTalk(p, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE1.id()) {
			switch (p.getQuestStage(this)) {
				case 2:
					playerTalk(p, n, "koftik, how can we cross the bridge?");
					npcTalk(p, n, "i'm not sure, seems as if others were here before us though");
					if (!hasItem(p, ItemId.DAMP_CLOTH.id())) {
						npcTalk(p, n, "i found this cloth amongst the charred remains of arrows");
						playerTalk(p, n, "charred arrows?");
						npcTalk(p, n, "they must have been trying to burn something");
						playerTalk(p, n, "or someone!");
						addItem(p, ItemId.DAMP_CLOTH.id(), 1);
					}
					playerTalk(p, n, "interesting, we better keep our eyes open");
					npcTalk(p, n, "There also seems to the remains of a diary");
					int menu = showMenu(p, n, false, //do not send over
							"not to worry, probably just kid litter", "what does it say?");
					if (menu == 0) {
						playerTalk(p, n, "not to worry, probably just litter");
						npcTalk(p, n, "well..maybe?");
					} else if (menu == 1) {
						playerTalk(p, n, "what does it say?");
						message(p, "@red@it seems to be written by the adventurer Randas, it reads...",
							"@red@It began as a whisper in my ears. Dismissing the sounds...",
							"@red@..as the whistling of the wind, I steeled myself against...",
							"@red@..these forces and continued on my way",
							"@red@But the whispers became moans...",
							"@red@at once fearsome and enticing like the call of some beautiful siren",
							"@red@Join us! The voices cried, Join us!",
							"@red@Your greatness lies within you, but only Zamorak can unlock your potential..");
						playerTalk(p, n, "it sounds like randas was losing it");
					}
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case -1:
					playerTalk(p, n, "hi koftik");
					if (!hasItem(p, ItemId.DAMP_CLOTH.id())) {
						addItem(p, ItemId.DAMP_CLOTH.id(), 1);
						p.message("koftik gives you a damp cloth");
					}
					break;
				case 8:
					playerTalk(p, n, "thanks for getting me out koftik");
					npcTalk(p, n, "always a pleasure squire");
					if (!hasItem(p, ItemId.DAMP_CLOTH.id())) {
						addItem(p, ItemId.DAMP_CLOTH.id(), 1);
						p.message("koftik gives you a damp cloth");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE2.id()) {
			switch (p.getQuestStage(this)) {
				case 3:
				case 4:
					playerTalk(p, n, "hello koftik");
					npcTalk(p, n, "how are you bearing adventurer?");
					playerTalk(p, n, "i'm still alive, and you?");
					npcTalk(p, n, "cold, i can feel it in my blood, so cold");
					p.message("koftik seems to be poorly");
					playerTalk(p, n, "where do we go now koftik?");
					npcTalk(p, n, "straight on again, more winding passages",
						"more lethal traps, more blood and more pain",
						"blood..pain.. hee hee,  more blood.. hee hee");
					playerTalk(p, n, "are you sure you're ok?");
					npcTalk(p, n, "erm..yes..i'll be fine, just go ahead i'll catch up");
					break;
				//nothing interesting happens on stage 5,6,7
				case 5:
				case 6:
				case 7:
					p.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					playerTalk(p, n, "thanks for getting me out koftik");
					npcTalk(p, n, "always a pleasure squire");
					break;
				case -1:
					playerTalk(p, n, "hello koftik");
					npcTalk(p, n, "hello adventurer, how's things?");
					playerTalk(p, n, "not bad, yourself?");
					npcTalk(p, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE3.id()) {
			switch (p.getQuestStage(this)) {
				case 3:
				case 4:
					playerTalk(p, n, "hello koftik");
					if (p.getQuestStage(this) == 4) {
						npcTalk(p, n, "are you ok?, i heard a rumble further down the cavern",
							"i thought the whole place was going to cave in");
						playerTalk(p, n, "im fine");
					} else {
						npcTalk(p, n, "keep back foul beast of the nigh.. ,wait, it's you!");
						playerTalk(p, n, "as far as i know");
					}
					npcTalk(p, n, "i assumed you were dead, or worse");
					playerTalk(p, n, "i've managed to survive so far");
					npcTalk(p, n, "the passsage ahead's blocked ,but you should be able to get through",
						"i'll follow behind",
						"aaaaaarrgghhh");
					playerTalk(p, n, "what's wrong?");
					npcTalk(p, n, "it's the voices, can't you hear them?",
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
					playerTalk(p, n, "thanks for getting me out koftik");
					npcTalk(p, n, "always a pleasure squire");
					break;
				case -1:
					playerTalk(p, n, "hello koftik");
					npcTalk(p, n, "hello adventurer, how's things?");
					playerTalk(p, n, "not bad, yourself?");
					npcTalk(p, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE4.id()) {
			p.message("The Koftik does not appear interested in talking");
		}
		else if (n.getID() == NpcId.KOFTIK_RECOVERED.id()) {
			switch (p.getQuestStage(this)) {
				case 8:
					npcTalk(p, n, "traveller, where am i?, i can't remeber a thing");
					playerTalk(p, n, "we were losing you to ibans influence");
					npcTalk(p, n, "what?..of corse, the voices",
						"but they've stopped, what happened?");
					playerTalk(p, n, "ibans dead, i destroyed him");
					npcTalk(p, n, "you've done well, now we must inform the king",
						"he'll have to send in some high mages to...",
						"reserrect the well of voyage",
						"follow me, i'll lead you out");
					playerTalk(p, n, "at last!, i've had enough of caves");
					message(p, "koftik leads you back up through the winding caverns");
					p.teleport(714, 581);
					p.message("and back to the cave entrance");
					break;
			}
		}
	}
}

