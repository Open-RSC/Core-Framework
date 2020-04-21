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
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == MOURNER_444) {
			if (p.getQuestStage(Quests.PLAGUE_CITY) != -1) {
				say(p, n, "hello there");
				npcsay(p, n, "Do you a have problem traveller?");
				say(p, n, "no i just wondered why your wearing that outfit",
					"is it fancy dress?");
				npcsay(p, n, "no it's for protection");
				say(p, n, "protection from what");
				npcsay(p, n, "the plague of course");
			} else if (p.getQuestStage(Quests.PLAGUE_CITY) == -1
				&& p.getQuestStage(Quests.BIOHAZARD) == -1) {
				say(p, n, "hello");
				npcsay(p, n, "what are you up to?");
				say(p, n, "nothing");
				npcsay(p, n, "i don't trust you");
				say(p, n, "you don't have to");
				npcsay(p, n, "if i find that you attempting to cross the wall",
					"I'll make sure you never return");
			}
		}
		if (n.getID() == MOURNER_451) {
			if (p.getQuestStage(Quests.PLAGUE_CITY) != -1) {
				say(p, n, "hello there");
				npcsay(p, n, "can I help you?");
				say(p, n, "what are you doing?");
				npcsay(p, n, "I'm guarding the border to west ardougne",
					"no one except us mourners can pass through");
				say(p, n, "why?");
				npcsay(p, n, "the plague of course",
					"we can't risk cross contamination");
				int menu = multi(p, n,
					"What brought the plague to ardougne?",
					"What are the symptoms of the plague?",
					"Ok then see you around");
				if (menu == 0) {
					npcsay(p, n, "it's all down to king tyras of west ardougne",
						"rather than protecting his people",
						"he spends his time in the lands to the west",
						"when he returned last he brought the plague with him",
						"then left before the problem became serious");
					say(p, n, "does he know how bad the situation is now?");
					npcsay(p, n, "if he did he wouldn't care",
						"i believe he wants his people to suffer",
						"he's an evil man");
					say(p, n, "isn't that treason?");
					npcsay(p, n, "he's not my king");
				} else if (menu == 1) {
					npcsay(p, n, "the first signs are typical flu symptoms",
						"these tend to be followed by severe nightmares",
						"horrifying hallucinations which drive many to madness");
					say(p, n, "sounds nasty");
					npcsay(p, n, "it gets worse",
						"next the victims blood supply changes into a thick black tar like liquid",
						"at this point they're past help",
						"their skin is cold to the touch",
						"the victim is now brain dead",
						"their body however lives on driven by the virus",
						"roaming like a zombie",
						"spreading itself further wherever possible");
					say(p, n, "I think I've heard enough");
				} else if (menu == 2) {
					npcsay(p, n, "maybe");
				}
			} else if (p.getQuestStage(Quests.PLAGUE_CITY) == -1
				&& p.getQuestStage(Quests.BIOHAZARD) == -1) {
				say(p, n, "hi");
				npcsay(p, n, "what are you up to?");
				say(p, n, "just sight seeing");
				npcsay(p, n, "this is no place for sight seeing",
					"don't you know there's been a plague outbreak?");
				say(p, n, "yes i had heard");
				npcsay(p, n, "then i suggest you leave as soon as you can");
				int menu = multi(p, n,
					"What brought the plague to ardougne?",
					"What are the symptoms of the plague?",
					"thanks for the advice");
				if (menu == 0) {
					npcsay(p, n, "it's all down to king tyras of west ardougne",
						"rather than protecting his people",
						"he spends his time in the lands to the west",
						"when he returned last he brought the plague with him",
						"then left before the problem became serious");
					say(p, n, "does he know how bad the situation is now?");
					npcsay(p, n, "if he did he wouldn't care",
						"i believe he wants his people to suffer",
						"he's an evil man");
					say(p, n, "isn't that treason?");
					npcsay(p, n, "he's not my king");
				} else if (menu == 1) {
					npcsay(p, n, "the first signs are typical flu symptoms",
						"these tend to be followed by severe nightmares",
						"horrifying hallucinations which drive many to madness");
					say(p, n, "sounds nasty");
					npcsay(p, n, "it gets worse",
						"next the victims blood supply changes into a thick black tar like liquid",
						"at this point they're past help",
						"their skin is cold to the touch",
						"the victim is now brain dead",
						"their body however lives on driven by the virus",
						"roaming like a zombie",
						"spreading itself further wherever possible");
					say(p, n, "I think I've heard enough");

				}
			}
		}
		if (n.getID() == MOURNER_445 || n.getID() == HEAD_MOURNER) {
			if (n.getID() == HEAD_MOURNER) {
				npcsay(p, n, "How did you did get into West Ardougne?",
					"Ah well you'll have to stay",
					"Can't risk you spreading the plague outside");
			} else {
				npcsay(p, n, "hmm how did you did get over here?",
					"You're not one of this rabble",
					"Ah well you'll have to stay",
					"Can't risk you going back now");
			}
			int menu = multi(p, n,
				"so what's a mourner?",
				"I've not got the plague though");
			if (menu == 0) {
				npcsay(p, n, "We're working for King Luthas of East ardougne",
					"Trying to contain the accursed plague sweeping west Ardougne",
					"We also do our best to ease these peoples suffering",
					"We're nicknamed mourners",
					"because we spend a lot of time at plague victims funerals",
					"no one else is allowed to risk the funerals",
					"It's a demanding job",
					"And we get little thanks from the people here");
			} else if (menu == 1) {
				npcsay(p, n, "Can't risk you being a carrier",
					"that protective clothing you have",
					"isn't regulation issue",
					"It won't meet safety standards");

			}
		}
		if (n.getID() == DOOR_MOURNER) {
			if (p.getCache().hasKey("rotten_apples")) {
				say(p, n, "hello there");
				npcsay(p, n, "oh dear oh dear",
					"i feel terrible, i think it was the stew");
				say(p, n, "you should be more careful with your ingredients");
				if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
					npcsay(p, n, "i need a doctor",
						"the nurses' hut is to the south west",
						"go now and bring us a doctor, that's an order");
				} else {
					npcsay(p, n, "there is one mourner who's really sick resting upstairs",
						"you should see to him first");
					say(p, n, "ok i'll see what i can do");
				}
			} else {
				p.message("the mourner doesn't feel like talking");
			}
		}
		if (n.getID() == ATTACK_MOURNER) {
			if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.DOCTORS_GOWN.id())) {
				npcsay(p, n, "how did you get in here?",
					"this is a restricted area");
				n.setChasing(p);
			} else {
				say(p, n, "hello");
				npcsay(p, n, "hello doc, i feel terrible",
					"i think it was the stew");
				say(p, n, "be more careful with your ingredients next time");
			}
		}
		if (n.getID() == ILL_MOURNER) {
			if (p.getQuestStage(Quests.BIOHAZARD) > 4) {
				mes(p, "the mourner is sick",
					"he doesn't feel like talking");
				return;
			}
			say(p, n, "hello there");
			npcsay(p, n, "you're here at last",
				"i don't know what i've eaten",
				"but i feel like i'm on death's door");
			say(p, n, "hmm... interesting, sounds like food poisoning");
			npcsay(p, n, "yes, i'd figured that out already",
				"what can you give me to help");
			int menu = multi(p, n,
				"just hold your breath and count to ten",
				"the best i can do is pray for you",
				"there's nothing i can do, it's fatal");
			if (menu == 0) {
				npcsay(p, n, "what, how will that help?",
					"what kind of doctor are you?");
				say(p, n, "erm .. i'm new, i just started");
				npcsay(p, n, "you're no doctor");
				n.startCombat(p);
			} else if (menu == 1) {
				npcsay(p, n, "prey for me?",
					"you're no doctor",
					"an impostor");
				n.startCombat(p);
			} else if (menu == 2) {
				npcsay(p, n, "no, i'm too young to die",
					"i've never even had a girlfriend");
				say(p, n, "that's life for you");
				npcsay(p, n, "wait a minute, where's your equipment?");
				say(p, n, "it's..erm , at home");
				npcsay(p, n, "you're no doctor");
				n.startCombat(p);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		if (n.getID() == MOURNER_451 || n.getID() == MOURNER_444 || n.getID() == MOURNER_445 || n.getID() == HEAD_MOURNER || n.getID() == DOOR_MOURNER || n.getID() == ATTACK_MOURNER || n.getID() == ILL_MOURNER) {
			return true;
		}
		return false;
	}

}
