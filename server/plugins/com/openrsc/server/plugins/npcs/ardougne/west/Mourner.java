package com.openrsc.server.plugins.npcs.ardougne.west;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Mourner implements TalkToNpcExecutiveListener, TalkToNpcListener {

	public static final int MOURNER_444 = NpcId.MOURNER_BYALRENA.id();
	public static final int MOURNER_491 = NpcId.MOURNER_BYENTRANCE2.id();
	public static final int MOURNER_451 = NpcId.MOURNER_BYENTRANCE.id();
	public static final int MOURNER_445 = NpcId.MOURNER_WESTARDOUGNE.id();
	public static final int HEAD_MOURNER = NpcId.HEAD_MOURNER.id();
	public static final int DOOR_MOURNER = NpcId.MOURNER_DOOR.id();
	public static final int ATTACK_MOURNER = NpcId.MOURNER_ATTACK.id();
	public static final int ILL_MOURNER = NpcId.MOURNER_ILL.id();

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == MOURNER_444) {
			if (p.getQuestStage(Quests.PLAGUE_CITY) != -1) {
				playerTalk(p, n, "hello there");
				npcTalk(p, n, "Do you a have problem traveller?");
				playerTalk(p, n, "no i just wondered why your wearing that outfit",
					"is it fancy dress?");
				npcTalk(p, n, "no it's for protection");
				playerTalk(p, n, "protection from what");
				npcTalk(p, n, "the plague of course");
			} else if (p.getQuestStage(Quests.PLAGUE_CITY) == -1
				&& p.getQuestStage(Quests.BIOHAZARD) == -1) {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "what are you up to?");
				playerTalk(p, n, "nothing");
				npcTalk(p, n, "i don't trust you");
				playerTalk(p, n, "you don't have to");
				npcTalk(p, n, "if i find that you attempting to cross the wall",
					"I'll make sure you never return");
			}
		}
		if (n.getID() == MOURNER_451) {
			if (p.getQuestStage(Quests.PLAGUE_CITY) != -1) {
				playerTalk(p, n, "hello there");
				npcTalk(p, n, "can I help you?");
				playerTalk(p, n, "what are you doing?");
				npcTalk(p, n, "I'm guarding the border to west ardougne",
					"no one except us mourners can pass through");
				playerTalk(p, n, "why?");
				npcTalk(p, n, "the plague of course",
					"we can't risk cross contamination");
				int menu = showMenu(p, n,
					"What brought the plague to ardougne?",
					"What are the symptoms of the plague?",
					"Ok then see you around");
				if (menu == 0) {
					npcTalk(p, n, "it's all down to king tyras of west ardougne",
						"rather than protecting his people",
						"he spends his time in the lands to the west",
						"when he returned last he brought the plague with him",
						"then left before the problem became serious");
					playerTalk(p, n, "does he know how bad the situation is now?");
					npcTalk(p, n, "if he did he wouldn't care",
						"i believe he wants his people to suffer",
						"he's an evil man");
					playerTalk(p, n, "isn't that treason?");
					npcTalk(p, n, "he's not my king");
				} else if (menu == 1) {
					npcTalk(p, n, "the first signs are typical flu symptoms",
						"these tend to be followed by severe nightmares",
						"horrifying hallucinations which drive many to madness");
					playerTalk(p, n, "sounds nasty");
					npcTalk(p, n, "it gets worse",
						"next the victims blood supply changes into a thick black tar like liquid",
						"at this point they're past help",
						"their skin is cold to the touch",
						"the victim is now brain dead",
						"their body however lives on driven by the virus",
						"roaming like a zombie",
						"spreading itself further wherever possible");
					playerTalk(p, n, "I think I've heard enough");
				} else if (menu == 2) {
					npcTalk(p, n, "maybe");
				}
			} else if (p.getQuestStage(Quests.PLAGUE_CITY) == -1
				&& p.getQuestStage(Quests.BIOHAZARD) == -1) {
				playerTalk(p, n, "hi");
				npcTalk(p, n, "what are you up to?");
				playerTalk(p, n, "just sight seeing");
				npcTalk(p, n, "this is no place for sight seeing",
					"don't you know there's been a plague outbreak?");
				playerTalk(p, n, "yes i had heard");
				npcTalk(p, n, "then i suggest you leave as soon as you can");
				int menu = showMenu(p, n,
					"What brought the plague to ardougne?",
					"What are the symptoms of the plague?",
					"thanks for the advice");
				if (menu == 0) {
					npcTalk(p, n, "it's all down to king tyras of west ardougne",
						"rather than protecting his people",
						"he spends his time in the lands to the west",
						"when he returned last he brought the plague with him",
						"then left before the problem became serious");
					playerTalk(p, n, "does he know how bad the situation is now?");
					npcTalk(p, n, "if he did he wouldn't care",
						"i believe he wants his people to suffer",
						"he's an evil man");
					playerTalk(p, n, "isn't that treason?");
					npcTalk(p, n, "he's not my king");
				} else if (menu == 1) {
					npcTalk(p, n, "the first signs are typical flu symptoms",
						"these tend to be followed by severe nightmares",
						"horrifying hallucinations which drive many to madness");
					playerTalk(p, n, "sounds nasty");
					npcTalk(p, n, "it gets worse",
						"next the victims blood supply changes into a thick black tar like liquid",
						"at this point they're past help",
						"their skin is cold to the touch",
						"the victim is now brain dead",
						"their body however lives on driven by the virus",
						"roaming like a zombie",
						"spreading itself further wherever possible");
					playerTalk(p, n, "I think I've heard enough");

				}
			}
		}
		if (n.getID() == MOURNER_445 || n.getID() == HEAD_MOURNER) {
			if (n.getID() == HEAD_MOURNER) {
				npcTalk(p, n, "How did you did get into West Ardougne?",
					"Ah well you'll have to stay",
					"Can't risk you spreading the plague outside");
			} else {
				npcTalk(p, n, "hmm how did you did get over here?",
					"You're not one of this rabble",
					"Ah well you'll have to stay",
					"Can't risk you going back now");
			}
			int menu = showMenu(p, n,
				"so what's a mourner?",
				"I've not got the plague though");
			if (menu == 0) {
				npcTalk(p, n, "We're working for King Luthas of East ardougne",
					"Trying to contain the accursed plague sweeping west Ardougne",
					"We also do our best to ease these peoples suffering",
					"We're nicknamed mourners",
					"because we spend a lot of time at plague victims funerals",
					"no one else is allowed to risk the funerals",
					"It's a demanding job",
					"And we get little thanks from the people here");
			} else if (menu == 1) {
				npcTalk(p, n, "Can't risk you being a carrier",
					"that protective clothing you have",
					"isn't regulation issue",
					"It won't meet safety standards");

			}
		}
		if (n.getID() == DOOR_MOURNER) {
			if (p.getCache().hasKey("rotten_apples")) {
				playerTalk(p, n, "hello there");
				npcTalk(p, n, "oh dear oh dear",
					"i feel terrible, i think it was the stew");
				playerTalk(p, n, "you should be more careful with your ingredients");
				if (!p.getInventory().wielding(802)) {
					npcTalk(p, n, "i need a doctor",
						"the nurses' hut is to the south west",
						"go now and bring us a doctor, that's an order");
				} else {
					npcTalk(p, n, "there is one mourner who's really sick resting upstairs",
						"you should see to him first");
					playerTalk(p, n, "ok i'll see what i can do");
				}
			} else {
				p.message("the mourner doesn't feel like talking");
			}
		}
		if (n.getID() == ATTACK_MOURNER) {
			if (!p.getInventory().wielding(802)) {
				npcTalk(p, n, "how did you get in here?",
					"this is a restricted area");
				n.setChasing(p);
			} else {
				playerTalk(p, n, "hello");
				npcTalk(p, n, "hello doc, i feel terrible",
					"i think it was the stew");
				playerTalk(p, n, "be more careful with your ingredients next time");
			}
		}
		if (n.getID() == ILL_MOURNER) {
			if (p.getQuestStage(Quests.BIOHAZARD) > 4) {
				message(p, "the mourner is sick",
					"he doesn't feel like talking");
				return;
			}
			playerTalk(p, n, "hello there");
			npcTalk(p, n, "you're here at last",
				"i don't know what i've eaten",
				"but i feel like i'm on death's door");
			playerTalk(p, n, "hmm... interesting, sounds like food poisoning");
			npcTalk(p, n, "yes, i'd figured that out already",
				"what can you give me to help");
			int menu = showMenu(p, n,
				"just hold your breath and count to ten",
				"the best i can do is pray for you",
				"there's nothing i can do, it's fatal");
			if (menu == 0) {
				npcTalk(p, n, "what, how will that help?",
					"what kind of doctor are you?");
				playerTalk(p, n, "erm .. i'm new, i just started");
				npcTalk(p, n, "you're no doctor");
				n.startCombat(p);
			} else if (menu == 1) {
				npcTalk(p, n, "prey for me?",
					"you're no doctor",
					"an impostor");
				n.startCombat(p);
			} else if (menu == 2) {
				npcTalk(p, n, "no, i'm too young to die",
					"i've never even had a girlfriend");
				playerTalk(p, n, "that's life for you");
				npcTalk(p, n, "wait a minute, where's your equipment?");
				playerTalk(p, n, "it's..erm , at home");
				npcTalk(p, n, "you're no doctor");
				n.startCombat(p);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == MOURNER_451 || n.getID() == MOURNER_444 || n.getID() == MOURNER_445 || n.getID() == HEAD_MOURNER || n.getID() == DOOR_MOURNER || n.getID() == ATTACK_MOURNER || n.getID() == ILL_MOURNER) {
			return true;
		}
		return false;
	}

}
