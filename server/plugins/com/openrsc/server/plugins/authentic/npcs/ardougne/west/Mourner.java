package com.openrsc.server.plugins.authentic.npcs.ardougne.west;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

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

	private void headMournerDialogue(Player player, Npc n, int chosenOption) {
		if (chosenOption == 0) {
			say(player, n, "I need clearance to enter a plague house");
			say(player, n, "It's in the southeast corner of west ardougne");
			npcsay(player, n, "You must be nuts, absolutely not");
			int submenu = multi(player, n, "There's a kidnap victim inside",
				"I've got a gasmask though",
				"Yes I'm utterly crazy");
			if (submenu == 0) {
				npcsay(player, n, "Well they're as good as dead already then",
					"No point trying to save them");
			} else if (submenu == 1) {
				npcsay(player, n, "It's not regulation",
					"Anyway you're not properly trained to deal with the plague");
				say(player, n, "How do I get trained");
				npcsay(player, n, "It requires a strict 18 months of training");
				say(player, n, "I don't have that sort of time");
			} else if (submenu == 2) {
				npcsay(player, n, "You waste my time",
					"I have much work to do");
			}
		} else if (chosenOption == 1) {
			say(player, n, "So what's a mourner?");
			npcsay(player, n, "We're working for King Luthas of East ardougne",
				"Trying to contain the accursed plague sweeping west Ardougne",
				"We also do our best to ease these peoples suffering",
				"We're nicknamed mourners",
				"because we spend a lot of time at plague victims funerals",
				"no one else is allowed to risk the funerals",
				"It's a demanding job",
				"And we get little thanks from the people here");
		} else if (chosenOption == 2) {
			say(player, n, "I've not got the plague though");
			npcsay(player, n, "Can't risk you being a carrier",
				"that protective clothing you have",
				"isn't regulation issue",
				"It won't meet safety standards");
		} else if (chosenOption == 3) {
			say(player, n, "I'm looking for a woman named Elena");
			npcsay(player, n, "ah yes I've heard of her",
				"A missionary I believe",
				"She must be mad coming over here voluntarily",
				"I hear rumours she has probably caught the plague now",
				"Very tragic stupid waste of life");
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == MOURNER_444) {
			switch (player.getQuestStage(Quests.PLAGUE_CITY)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "Do you a have problem traveller?");
					say(player, n, "no i just wondered why your wearing that outfit",
						"is it fancy dress?");
					npcsay(player, n, "no it's for protection");
					say(player, n, "protection from what");
					npcsay(player, n, "the plague of course");
					break;
				case 1:
					say(player, n, "hello");
					npcsay(player, n, "what do you want?");
					int menu = multi(player, n,
						"who are you?",
						"nothing just being polite");
					if (menu == 0) {
						npcsay(player, n, "I'm a mourner",
							"it's my job to help heal the plague victims of west ardougne",
							"and to make sure the disease is contained");
						say(player, n, "who pays you?");
						npcsay(player, n, "we feel as the kings henchmen it's our duty to help the people of ardougne");
						say(player, n, "very noble of you");
						npcsay(player, n, "if you come down with any symptoms such as a flu or nightmares",
							"let me know immediately");
					} else if (menu == 1) {
						npcsay(player, n, "hmm ok then",
							"be on your way");
					}
					break;
				case 2:
					if (player.getCache().hasKey("soil_soften")) {
						say(player, n, "hello");
						npcsay(player, n, "what are you up to with old man Edmond?");
						say(player, n, "nothing, we've just been chatting");
						npcsay(player, n, "what about, his daughter?");
						say(player, n, "oh, you know about that then");
						npcsay(player, n, "we know about everything that goes on in ardougne",
							"we have to if we are to contain the plague");
						say(player, n, "have you seen his daughter recently");
						npcsay(player, n, "i imagine she's caught the plague",
							"either way she won't be allowed out of west Ardougne",
							"The risk is to great");
						return;
					}
					say(player, n, "hello");
					npcsay(player, n, "are you ok");
					say(player, n, "yes I'm fine thanks");
					npcsay(player, n, "have you experienced any plague symptoms?");
					int menuOpt = multi(player, n, false, //do not send over
						"What are the symptoms?",
						"No i feel fine",
						"No, but tell me where did the plague come from?");
					if (menuOpt == 0) {
						say(player, n, "What are the symptoms?");
						npcsay(player, n, "firstly you'll come down with a heavy flu",
							"this is usually followed by horrifying nightmares");
						say(player, n, "i used to have nightmares when i was younger");
						npcsay(player, n, "not like these i assure you",
							"soon after a thick black liquid will seep from your nose and eyes");
						say(player, n, "yuck!");
						npcsay(player, n, "when it get's to this stage there's nothing we can do for you");
					} else if (menuOpt == 1) {
						say(player, n, "no i feel fine");
						npcsay(player, n, "well if you take a turn for the worse let me know straight away");
						say(player, n, "can you cure it then?");
						npcsay(player, n, "no", "but you will have to be treated");
						say(player, n, "treated?");
						npcsay(player, n, "we have to take measures to contain the disease",
							"that's why you must let us know immediately if you take a turn for the worst");
					} else if (menuOpt == 2) {
						say(player, n, "no, but tell me where did the plague come from");
						npcsay(player, n, "many put it down to the low living standards of the west ardougnians",
							"however this is not the case",
							"the truth is the king Tyras of west ardougne",
							"unknowingly brought the plague into his kingdom",
							"when returning from one of his visits to the darklands in the north west");
					}
					break;
				case 3:
					say(player, n, "hello there");
					npcsay(player, n, "been digging have we?");
					say(player, n, "what do you mean!");
					npcsay(player, n, "your hands are covered in mud");
					say(player, n, "oh that",
						"I've just been helping Edmond with his allotment");
					npcsay(player, n, "funny, you don't look like the gardening type");
					say(player, n, "oh no, i love gardening",
						"it's my favourite pass time");
					break;
				case 4:
					say(player, n, "hello there");
					npcsay(player, n, "what are you up to?");
					say(player, n, "what do you mean?");
					npcsay(player, n, "you and that Edmond fella",
						"you're looking very suspicious");
					say(player, n, "we're just gardening",
						"have you heard any news about west ardougne?");
					npcsay(player, n, "just the usual",
						"everyone's sick or dying",
						"I'm furious at king tyras for bringing this plague to our lands");
					break;
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case -1:
					say(player, n, "hello");
					npcsay(player, n, "what are you up to?");
					say(player, n, "nothing");
					npcsay(player, n, "i don't trust you");
					say(player, n, "you don't have to");
					npcsay(player, n, "if i find that you attempting to cross the wall",
						"I'll make sure you never return");
					break;
			}
		}
		if (n.getID() == MOURNER_451) {
			int menu;
			switch (player.getQuestStage(Quests.PLAGUE_CITY)) {
				case 0:
				case 1:
					say(player, n, "hello there");
					npcsay(player, n, "can I help you?");
					say(player, n, "what are you doing?");
					npcsay(player, n, "I'm guarding the border to west ardougne",
						"no one except us mourners can pass through");
					say(player, n, "why?");
					npcsay(player, n, "the plague of course",
						"we can't risk cross contamination");
					menu = multi(player, n, false, //do not send over
						"What brought the plague to ardougne?",
						"What are the symptoms of the plague?",
						"Ok then see you around");
					if (menu == 0) {
						say(player, n, "what brought the plague to ardougne?");
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
						say(player, n, "what are the symptoms of the plague?");
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
						say(player, n, "ok then see you around");
						npcsay(player, n, "maybe");
					}
					break;
				case 2:
					say(player, n, "hello there");
					npcsay(player, n, "can i help you?");
					say(player, n, "just being polite");
					npcsay(player, n, "I'm not here to chat",
						"sorry, what is it you do?",
						"i protect people like you from the plague");
					say(player, n, "how?");
					npcsay(player, n, "by making sure no one crosses the wall");
					say(player, n, "what if they do");
					npcsay(player, n, "then they must be treated immediately");
					say(player, n, "treated?");
					npcsay(player, n, "any west ardougnians which cross the wall",
						"must be detained and disposed of safely");
					say(player, n, "sound's like nasty work");
					npcsay(player, n, "some find it hard",
						"personally i quite enjoy it");
					menu = multi(player, n, false, //not actually known but guessed
						"what brought the plague to ardougne",
						"what are the symptoms of the plague",
						"you're a very sick man");
					if (menu == 0) {
						say(player, n, "what brought the plague to ardougne?");
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
						say(player, n, "what are the symptoms of the plague?");
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
						say(player, n, "you're a very sick man");
						npcsay(player, n, "What? I'm pretty sure I haven't caught the plague yet");
					}
					break;
				case 3:
					say(player, n, "hello");
					npcsay(player, n, "what do you want");
					say(player, n, "so how did you get into this line of work?");
					npcsay(player, n, "as king lanthas's henchmen",
						"it is our duty to protect his people");
					say(player, n, "i thought that was the job of the paladins");
					npcsay(player, n, "their swords and armour have no effect on the plague");
					menu = multi(player, n, false, //not actually known but possible
						"what brought the plague to ardougne?",
						"what are the symptoms of the plague?",
						"well keep up the good work");
					if (menu == 0) {
						say(player, n, "what brought the plague to ardougne?");
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
						say(player, n, "what are the symptoms of the plague?");
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
						say(player, n, "well keep up the good work");
						npcsay(player, n, "will do");
					}
					break;
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10:
				case 11:
				case -1:
					say(player, n, "hi");
					npcsay(player, n, "what are you up to?");
					say(player, n, "just sight seeing");
					npcsay(player, n, "this is no place for sight seeing",
						"don't you know there's been a plague outbreak?");
					say(player, n, "yes i had heard");
					npcsay(player, n, "then i suggest you leave as soon as you can");
					menu = multi(player, n, false, //do not send over
						"What brought the plague to ardougne?",
						"What are the symptoms of the plague?",
						"thanks for the advice");
					if (menu == 0) {
						say(player, n, "what brought the plague to ardougne?");
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
						say(player, n, "what are the symptoms of the plague?");
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
						say(player, n, "thanks for the advice");
					}
					break;
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
			ArrayList<String> options = new ArrayList<>();
			if (player.getQuestStage(Quests.PLAGUE_CITY) >= 9 && n.getID() == HEAD_MOURNER) {
				options.add("I need clearance to enter a plague house");
			}
			options.add("so what's a mourner?");
			options.add("I've not got the plague though");
			if (player.getQuestStage(Quests.PLAGUE_CITY) >= 0) {
				options.add("I'm looking for a woman named Elena");
			}
			String[] finalOptions = new String[options.size()];
			int menu = multi(player, n, false, //do not send over
				options.toArray(finalOptions));
			if (player.getQuestStage(Quests.PLAGUE_CITY) >= 9 && n.getID() == HEAD_MOURNER) {
				headMournerDialogue(player, n, menu);
			}
			else {
				if (menu >= 0) {
					headMournerDialogue(player, n, menu+1);
				}
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
			} else if (player.getQuestStage(Quests.BIOHAZARD) != 0) {
				say(player, n, "hello", "are these the mourner quarters?");
				npcsay(player, n, "yes, why?, what do you want?");
				say(player, n, "i need to go inside");
				npcsay(player, n, "they'll be busy feasting all day");
				say(player, n, "really, even with the food shortages in west ardounge");
				npcsay(player, n, "we've no food shortage, just the civilians");
				int menu = multi(player, n, "can i join the feast?",
					"you should be ashamed of yourself",
					"well, enjoy your meal");
				if (menu == 0) {
					npcsay(player, n, "don't be so obsurd");
					say(player, n, "but why not?");
					npcsay(player, n, "because i don't like your face");
				} else if (menu == 1) {
					say(player, n, "there are families here starving, you should be protecting them");
					npcsay(player, n, "that sounds like a lot of hard work",
						"i tell you what, i'll give it some consideration while i'm enjoying my stew");
				} else if (menu == 2) {
					npcsay(player, n, "we will, oh and if you get hungry...",
						"..there are some rotten apples around the corner - help yourself!");
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
				mes("the mourner is sick");
				delay(3);
				mes("he doesn't feel like talking");
				delay(3);
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
