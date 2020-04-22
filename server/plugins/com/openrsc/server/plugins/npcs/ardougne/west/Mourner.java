package com.openrsc.server.plugins.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Mourner implements TalkNpcTrigger {

	public static final int MOURNER_444 = NpcId.MOURNER_BYALRENA.id();
	public static final int MOURNER_491 = NpcId.MOURNER_BYENTRANCE2.id();
	public static final int MOURNER_451 = NpcId.MOURNER_BYENTRANCE.id();
	public static final int MOURNER_445 = NpcId.MOURNER_WESTARDOUGNE.id();
	public static final int HEAD_MOURNER = NpcId.HEAD_MOURNER.id();
	public static final int DOOR_MOURNER = NpcId.MOURNER_DOOR.id();
	public static final int ATTACK_MOURNER = NpcId.MOURNER_ATTACK.id();
	public static final int ILL_MOURNER = NpcId.MOURNER_ILL.id();

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == MOURNER_444) {
			if (player.getQuestStage(Quests.PLAGUE_CITY) != -1) {
				say(player, n, "hello there");
				npcsay(player, n, "Do you a have problem traveller?");
				say(player, n, "no i just wondered why your wearing that outfit",
					"is it fancy dress?");
				npcsay(player, n, "no it's for protection");
				say(player, n, "protection from what");
				npcsay(player, n, "the plague of course");
			} else if (player.getQuestStage(Quests.PLAGUE_CITY) == -1
				&& player.getQuestStage(Quests.BIOHAZARD) == -1) {
				say(player, n, "hello");
				npcsay(player, n, "what are you up to?");
				say(player, n, "nothing");
				npcsay(player, n, "i don't trust you");
				say(player, n, "you don't have to");
				npcsay(player, n, "if i find that you attempting to cross the wall",
					"I'll make sure you never return");
			}
		}
		if (n.getID() == MOURNER_451) {
			if (player.getQuestStage(Quests.PLAGUE_CITY) != -1) {
				say(player, n, "hello there");
				npcsay(player, n, "can I help you?");
				say(player, n, "what are you doing?");
				npcsay(player, n, "I'm guarding the border to west ardougne",
					"no one except us mourners can pass through");
				say(player, n, "why?");
				npcsay(player, n, "the plague of course",
					"we can't risk cross contamination");
				int menu = multi(player, n,
					"What brought the plague to ardougne?",
					"What are the symptoms of the plague?",
					"Ok then see you around");
				if (menu == 0) {
					npcsay(player, n, "it's all down to king tyras of west ardougne",
						"rather than protecting his people",
						"he spends his time in the lands to the west",
						"when he returned last he brought the plague with him",
						"then left before the problem became serious");
					say(player, n, "does he know how bad the situation is now?");
					npcsay(player, n, "if he did he wouldn't care",
						"i believe he wants his people to suffer",
						"he's an evil man");
					say(player, n, "isn't that treason?");
					npcsay(player, n, "he's not my king");
				} else if (menu == 1) {
					npcsay(player, n, "the first signs are typical flu symptoms",
						"these tend to be followed by severe nightmares",
						"horrifying hallucinations which drive many to madness");
					say(player, n, "sounds nasty");
					npcsay(player, n, "it gets worse",
						"next the victims blood supply changes into a thick black tar like liquid",
						"at this point they're past help",
						"their skin is cold to the touch",
						"the victim is now brain dead",
						"their body however lives on driven by the virus",
						"roaming like a zombie",
						"spreading itself further wherever possible");
					say(player, n, "I think I've heard enough");
				} else if (menu == 2) {
					npcsay(player, n, "maybe");
				}
			} else if (player.getQuestStage(Quests.PLAGUE_CITY) == -1
				&& player.getQuestStage(Quests.BIOHAZARD) == -1) {
				say(player, n, "hi");
				npcsay(player, n, "what are you up to?");
				say(player, n, "just sight seeing");
				npcsay(player, n, "this is no place for sight seeing",
					"don't you know there's been a plague outbreak?");
				say(player, n, "yes i had heard");
				npcsay(player, n, "then i suggest you leave as soon as you can");
				int menu = multi(player, n,
					"What brought the plague to ardougne?",
					"What are the symptoms of the plague?",
					"thanks for the advice");
				if (menu == 0) {
					npcsay(player, n, "it's all down to king tyras of west ardougne",
						"rather than protecting his people",
						"he spends his time in the lands to the west",
						"when he returned last he brought the plague with him",
						"then left before the problem became serious");
					say(player, n, "does he know how bad the situation is now?");
					npcsay(player, n, "if he did he wouldn't care",
						"i believe he wants his people to suffer",
						"he's an evil man");
					say(player, n, "isn't that treason?");
					npcsay(player, n, "he's not my king");
				} else if (menu == 1) {
					npcsay(player, n, "the first signs are typical flu symptoms",
						"these tend to be followed by severe nightmares",
						"horrifying hallucinations which drive many to madness");
					say(player, n, "sounds nasty");
					npcsay(player, n, "it gets worse",
						"next the victims blood supply changes into a thick black tar like liquid",
						"at this point they're past help",
						"their skin is cold to the touch",
						"the victim is now brain dead",
						"their body however lives on driven by the virus",
						"roaming like a zombie",
						"spreading itself further wherever possible");
					say(player, n, "I think I've heard enough");

				}
			}
		}
		if (n.getID() == MOURNER_445 || n.getID() == HEAD_MOURNER) {
			if (n.getID() == HEAD_MOURNER) {
				npcsay(player, n, "How did you did get into West Ardougne?",
					"Ah well you'll have to stay",
					"Can't risk you spreading the plague outside");
			} else {
				npcsay(player, n, "hmm how did you did get over here?",
					"You're not one of this rabble",
					"Ah well you'll have to stay",
					"Can't risk you going back now");
			}
			int menu = multi(player, n,
				"so what's a mourner?",
				"I've not got the plague though");
			if (menu == 0) {
				npcsay(player, n, "We're working for King Luthas of East ardougne",
					"Trying to contain the accursed plague sweeping west Ardougne",
					"We also do our best to ease these peoples suffering",
					"We're nicknamed mourners",
					"because we spend a lot of time at plague victims funerals",
					"no one else is allowed to risk the funerals",
					"It's a demanding job",
					"And we get little thanks from the people here");
			} else if (menu == 1) {
				npcsay(player, n, "Can't risk you being a carrier",
					"that protective clothing you have",
					"isn't regulation issue",
					"It won't meet safety standards");

			}
		}
		if (n.getID() == DOOR_MOURNER) {
			if (player.getCache().hasKey("rotten_apples")) {
				say(player, n, "hello there");
				npcsay(player, n, "oh dear oh dear",
					"i feel terrible, i think it was the stew");
				say(player, n, "you should be more careful with your ingredients");
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
					npcsay(player, n, "i need a doctor",
						"the nurses' hut is to the south west",
						"go now and bring us a doctor, that's an order");
				} else {
					npcsay(player, n, "there is one mourner who's really sick resting upstairs",
						"you should see to him first");
					say(player, n, "ok i'll see what i can do");
				}
			} else {
				player.message("the mourner doesn't feel like talking");
			}
		}
		if (n.getID() == ATTACK_MOURNER) {
			if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
				npcsay(player, n, "how did you get in here?",
					"this is a restricted area");
				n.setChasing(player);
			} else {
				say(player, n, "hello");
				npcsay(player, n, "hello doc, i feel terrible",
					"i think it was the stew");
				say(player, n, "be more careful with your ingredients next time");
			}
		}
		if (n.getID() == ILL_MOURNER) {
			if (player.getQuestStage(Quests.BIOHAZARD) > 4) {
				mes(player, "the mourner is sick",
					"he doesn't feel like talking");
				return;
			}
			say(player, n, "hello there");
			npcsay(player, n, "you're here at last",
				"i don't know what i've eaten",
				"but i feel like i'm on death's door");
			say(player, n, "hmm... interesting, sounds like food poisoning");
			npcsay(player, n, "yes, i'd figured that out already",
				"what can you give me to help");
			int menu = multi(player, n,
				"just hold your breath and count to ten",
				"the best i can do is pray for you",
				"there's nothing i can do, it's fatal");
			if (menu == 0) {
				npcsay(player, n, "what, how will that help?",
					"what kind of doctor are you?");
				say(player, n, "erm .. i'm new, i just started");
				npcsay(player, n, "you're no doctor");
				n.startCombat(player);
			} else if (menu == 1) {
				npcsay(player, n, "prey for me?",
					"you're no doctor",
					"an impostor");
				n.startCombat(player);
			} else if (menu == 2) {
				npcsay(player, n, "no, i'm too young to die",
					"i've never even had a girlfriend");
				say(player, n, "that's life for you");
				npcsay(player, n, "wait a minute, where's your equipment?");
				say(player, n, "it's..erm , at home");
				npcsay(player, n, "you're no doctor");
				n.startCombat(player);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		if (n.getID() == MOURNER_451 || n.getID() == MOURNER_444 || n.getID() == MOURNER_445 || n.getID() == HEAD_MOURNER || n.getID() == DOOR_MOURNER || n.getID() == ATTACK_MOURNER || n.getID() == ILL_MOURNER) {
			return true;
		}
		return false;
	}

}
