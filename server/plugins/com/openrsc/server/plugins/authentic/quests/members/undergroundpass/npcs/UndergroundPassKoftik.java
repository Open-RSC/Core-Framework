package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
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
	public int getQuestPoints() {
		return Quest.UNDERGROUND_PASS.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.UNDERGROUND_PASS.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.message("you have completed the underground pass quest");
		player.getCache().set("Iban blast_casts", 25);
		player.getCache().remove("advised_koftik");
	}

	/**
	 * fast dialogues
	 **/
	public static void koftikEnterCaveDialogue(Player player, Npc n) {
		say(player, n, "hello there, are you the kings scout?");
		npcsay(player, n, "that i am brave adventurer",
			"King lathas informed me that you need to cross these mountains",
			"i'm afraid you'll have to go through the ancient underground pass");
		say(player, n, "That's ok, i've travelled through many a cave in my time");
		npcsay(player, n, "these caves are different..they're filled with the spirit of Zamorak",
			"You can feel it as you wind your way round the stalactites..",
			"an icy chill that penetrate's the very fabric of your being",
			"not so many travellers come down here these days...",
			"...but there are some who are still foolhardy enough");
		player.updateQuestStage(Quests.UNDERGROUND_PASS, 2);
		int menu = multi(player, n,
			"i'll take my chances",
			"tell me more");
		if (menu == 0) {
			npcsay(player, n, "ok traveller, i'll catch up with you by the bridge");
		} else if (menu == 1) {
			npcsay(player, n, "I remember seeing one such warrior. Going by the name of Randas...",
				"..he stood tall and proud like an elven king...",
				"..that same pride made him vulnerable to Zamorak's calls...",
				"..Randas' worthy desire to be a great and mighty warrior...",
				"..also made him corruptible to Zamorak's promises of glory",
				"..Zamorak showed him a way to achieve his goals, by appealing...",
				"..to that most base and dark nature that resides in all of us");
			say(player, n, "what happened to him?");
			npcsay(player, n, "no one knows");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.KOFTIK_ARDOUGNE.id(), NpcId.KOFTIK_CAVE1.id(), NpcId.KOFTIK_CAVE2.id(), NpcId.KOFTIK_CAVE3.id(), NpcId.KOFTIK_CAVE4.id(), NpcId.KOFTIK_RECOVERED.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KOFTIK_ARDOUGNE.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					player.message("koftik doesn't seem interested in talking");
					break;
				case 1:
					koftikEnterCaveDialogue(player, n);
					break;
				case 2:
					npcsay(player, n, "i know it's scary in there",
						"but you'll have to go in alone",
						"i'll catch up as soon as i can");
					break;
				case 3:
				case 4:
					say(player, n, "hello koftik");
					if (player.getCache().hasKey("orb_of_light1") && player.getCache().hasKey("orb_of_light2") && player.getCache().hasKey("orb_of_light3") && player.getCache().hasKey("orb_of_light4") || player.getQuestStage(this) == 4) {
						npcsay(player, n, "it scares me in there",
							"the voices, don't you hear them?");
						say(player, n, "you'll be ok koftik");
						return;
					}
					npcsay(player, n, "once your over the bridge keep going...",
						"..straight ahead, i'll meet you further up");
					break;
				// nothing interesting on stage 5,6,7
				case 5:
				case 6:
				case 7:
					player.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					say(player, n, "thanks for getting me out koftik");
					npcsay(player, n, "always a pleasure squire",
						"have you informed the king about iban?");
					int menu = multi(player, n,
						"no, not yet",
						"yes, i've told him");
					if (menu == 0) {
						npcsay(player, n, "traveller this is no time to linger",
							"the king must know that ibans dead",
							"this is a truly historical moment for ardounge");
					} else if (menu == 1) {
						npcsay(player, n, "good to hear, the sooner we find king Tyras..",
							"the better");
					}
					break;
				case -1:
					say(player, n, "hello koftik");
					npcsay(player, n, "hello adventurer, how's things?");
					say(player, n, "not bad, yourself?");
					npcsay(player, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE1.id()) {
			switch (player.getQuestStage(this)) {
				case 2:
					say(player, n, "koftik, how can we cross the bridge?");
					npcsay(player, n, "i'm not sure, seems as if others were here before us though");
					if (!player.getCarriedItems().hasCatalogID(ItemId.DAMP_CLOTH.id(), Optional.empty())) {
						npcsay(player, n, "i found this cloth amongst the charred remains of arrows");
						say(player, n, "charred arrows?");
						npcsay(player, n, "they must have been trying to burn something");
						say(player, n, "or someone!");
						give(player, ItemId.DAMP_CLOTH.id(), 1);
					}
					say(player, n, "interesting, we better keep our eyes open");
					npcsay(player, n, "There also seems to the remains of a diary");
					int menu = multi(player, n, false, //do not send over
							"not to worry, probably just kid litter", "what does it say?");
					if (menu == 0) {
						say(player, n, "not to worry, probably just litter");
						npcsay(player, n, "well..maybe?");
					} else if (menu == 1) {
						say(player, n, "what does it say?");
						mes("@red@it seems to be written by the adventurer Randas, it reads...");
						delay(3);
						mes("@red@It began as a whisper in my ears. Dismissing the sounds...");
						delay(3);
						mes("@red@..as the whistling of the wind, I steeled myself against...");
						delay(3);
						mes("@red@..these forces and continued on my way");
						delay(3);
						mes("@red@But the whispers became moans...");
						delay(3);
						mes("@red@at once fearsome and enticing like the call of some beautiful siren");
						delay(3);
						mes("@red@Join us! The voices cried, Join us!");
						delay(3);
						mes("@red@Your greatness lies within you, but only Zamorak can unlock your potential..");
						delay(3);
						say(player, n, "it sounds like randas was losing it");
					}
					break;
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case -1:
					say(player, n, "hi koftik");
					if (!player.getCarriedItems().hasCatalogID(ItemId.DAMP_CLOTH.id(), Optional.empty())) {
						give(player, ItemId.DAMP_CLOTH.id(), 1);
						player.message("koftik gives you a damp cloth");
					}
					break;
				case 8:
					say(player, n, "thanks for getting me out koftik");
					npcsay(player, n, "always a pleasure squire");
					if (!player.getCarriedItems().hasCatalogID(ItemId.DAMP_CLOTH.id(), Optional.empty())) {
						give(player, ItemId.DAMP_CLOTH.id(), 1);
						player.message("koftik gives you a damp cloth");
					}
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE2.id()) {
			switch (player.getQuestStage(this)) {
				case 3:
				case 4:
					say(player, n, "hello koftik");
					npcsay(player, n, "how are you bearing adventurer?");
					say(player, n, "i'm still alive, and you?");
					npcsay(player, n, "cold, i can feel it in my blood, so cold");
					player.message("koftik seems to be poorly");
					say(player, n, "where do we go now koftik?");
					npcsay(player, n, "straight on again, more winding passages",
						"more lethal traps, more blood and more pain",
						"blood..pain.. hee hee,  more blood.. hee hee");
					say(player, n, "are you sure you're ok?");
					npcsay(player, n, "erm..yes..i'll be fine, just go ahead i'll catch up");
					break;
				//nothing interesting happens on stage 5,6,7
				case 5:
				case 6:
				case 7:
					player.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					say(player, n, "thanks for getting me out koftik");
					npcsay(player, n, "always a pleasure squire");
					break;
				case -1:
					say(player, n, "hello koftik");
					npcsay(player, n, "hello adventurer, how's things?");
					say(player, n, "not bad, yourself?");
					npcsay(player, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE3.id()) {
			switch (player.getQuestStage(this)) {
				case 3:
				case 4:
					say(player, n, "hello koftik");
					if (player.getQuestStage(this) == 4) {
						npcsay(player, n, "are you ok?, i heard a rumble further down the cavern",
							"i thought the whole place was going to cave in");
						say(player, n, "im fine");
						npcsay(player, n, "i assumed you were dead, or worse");
						say(player, n, "i've managed to survive so far");
						npcsay(player, n, "the passsage ahead's blocked ,but you should be able to get through",
							"i'll follow behind",
							"aaaaaarrgghhh");
						say(player, n, "what's wrong?");
						npcsay(player, n, "it's the voices, can't you hear them",
							"they wont leave me be",
							"i feel him calling to me");
					} else {
						npcsay(player, n, "keep back foul beast of the nigh.. ,wait, it's you!");
						say(player, n, "as far as i know");
						npcsay(player, n, "i assumed you were dead, or worse");
						say(player, n, "i've managed to survive so far");
						npcsay(player, n, "the passsage ahead's blocked ,but you should be able to get through",
							"i'll follow behind",
							"aaaaaarrgghhh");
						say(player, n, "what's wrong?");
						npcsay(player, n, "it's the voices, can't you hear them?",
							"they wont leave be",
							"i feel him calling to me");
					}
					break;
				//nothing interesting happens on stage 5,6,7
				case 5:
				case 6:
				case 7:
					player.message("koftik doesn't seem interested in talking");
					break;
				case 8:
					say(player, n, "thanks for getting me out koftik");
					npcsay(player, n, "always a pleasure squire");
					break;
				case -1:
					say(player, n, "hello koftik");
					npcsay(player, n, "hello adventurer, how's things?");
					say(player, n, "not bad, yourself?");
					npcsay(player, n, "im good, just keeping an eye out");
					break;
			}
		}
		else if (n.getID() == NpcId.KOFTIK_CAVE4.id()) {
			player.message("The Koftik does not appear interested in talking");
		}
		else if (n.getID() == NpcId.KOFTIK_RECOVERED.id()) {
			switch (player.getQuestStage(this)) {
				case 8:
					npcsay(player, n, "traveller, where am i?, i can't remeber a thing");
					say(player, n, "we were losing you to ibans influence");
					npcsay(player, n, "what?..of corse, the voices",
						"but they've stopped, what happened?");
					say(player, n, "ibans dead, i destroyed him");
					npcsay(player, n, "you've done well, now we must inform the king",
						"he'll have to send in some high mages to...",
						"reserrect the well of voyage",
						"follow me, i'll lead you out");
					say(player, n, "at last!, i've had enough of caves");
					mes("koftik leads you back up through the winding caverns");
					delay(3);
					player.teleport(714, 581);
					player.message("and back to the cave entrance");
					break;
			}
		}
	}
}

