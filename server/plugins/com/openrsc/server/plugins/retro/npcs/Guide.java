package com.openrsc.server.plugins.retro.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Guide implements TalkNpcTrigger {
	private void GuideDialogue(Player player, Npc npc, int cID) {
		if (npc.getID() == NpcId.GUIDE.id() || npc.getID() == NpcId.GUIDE_FEMALE.id()) {
			switch(cID) {
				case GUIDE.WHERE_START:
					npcsay(player, npc, "You are at Lumbridge castle.",
						"There are not many creatures close to here that will attack you,",
						"but there are always some.",
						"When you go further away from the castle you will meet tougher",
						"and stronger creatures, some will attack you if you go too close.");

					int optStart = multi(player, npc, false,
						"Are there any quests near here, and what will I need?",
						"What are the best places to go to?",
						"What are all the people and creatures around here? What do they do?",
						"How do I become a better warrior?");

					if (optStart == 0) {
						say(player, npc, "Are there any quests near here, and what will I need?");
						GuideDialogue(player, npc, GUIDE.QUESTS_NEARBY);
					} else if (optStart == 1) {
						say(player, npc, "What are the best places to go to?");
						GuideDialogue(player, npc, GUIDE.PLACES_TO_GO);
					} else if (optStart == 2) {
						say(player, npc, "What are all the people and creatures around here?",
							"What do they do?");
						GuideDialogue(player, npc, GUIDE.PEOPLE_AND_CREATURES);
					} else if (optStart == 3) {
						say(player, npc, "How do I become a better warrior?");
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					}
					break;

				case GUIDE.GOOD_WARRIOR:
					npcsay(player, npc, "To be a good warrior takes practice, and good equipment.",
						"Skill is only gained by fighting.",
						"The more you win fights, the better you will become.",
						"The tougher the fights you win, the quicker you will progress.");

					int optWarrior = multi(player, npc,
						"How can I improve my weapons and my armour?",
						"How can I choose who is best to fight?",
						"What other skills will help me fight well?",
						"How can I avoid dying?");

					if (optWarrior == 0) {
						GuideDialogue(player, npc, GUIDE.IMPROVE_WEAPS_N_ARMOUR);
					} else if (optWarrior == 1) {
						GuideDialogue(player, npc, GUIDE.BEST_TO_FIGHT);
					} else if (optWarrior == 2) {
						GuideDialogue(player, npc, GUIDE.SKILLS_HELP);
					} else if (optWarrior == 3) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					}
					break;

				case GUIDE.GETTING_KILLED:
					npcsay(player, npc, "You are probably attacking people that are too strong for you.",
						"Or going too near to dangerous creatures");

					int optKilled = multi(player, npc,
						"How can I avoid dying?",
						"How can I see who is best to attack?",
						"What is my strength, and how can I improve it?",
						"You have been a great help, thankyou");

					if (optKilled == 0) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					} else if (optKilled == 1) {
						GuideDialogue(player, npc, GUIDE.BEST_TO_ATTACK);
					} else if (optKilled == 2) {
						GuideDialogue(player, npc, GUIDE.IMPROVE_STRENGTH);
					} else if (optKilled == 3) {
						GuideDialogue(player, npc, GUIDE.BEEN_GREAT_HELP);
					}
					break;

				case GUIDE.QUESTS_NEARBY:
					npcsay(player, npc, "A quest is simply a task to help someone or to prove your strength",
						"There are a few quests near here to be done.",
						"If you complete the quests you can gain experience and treasure",
						"To find a quest, talk to anyone you can.",
						"There are some items about that will help you too.");

					int optQuests = multi(player, npc, false,
						"What are the best places to go to?",
						"What are all the people and creatures around here? What do they do?",
						"How do I become a better warrior?",
						"How can I avoid dying?");

					if (optQuests == 0) {
						say(player, npc, "What are the best places to go to?");
						GuideDialogue(player, npc, GUIDE.PLACES_TO_GO);
					} else if (optQuests == 1) {
						say(player, npc, "What are all the people and creatures around here?",
							"What do they do?");
						GuideDialogue(player, npc, GUIDE.PEOPLE_AND_CREATURES);
					} else if (optQuests == 2) {
						say(player, npc, "How do I become a better warrior?");
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					} else if (optQuests == 3) {
						say(player, npc, "How can I avoid dying?");
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					}
					break;

				case GUIDE.PLACES_TO_GO:
					npcsay(player, npc, "The city of Varrock is the main trading and living place.",
						"It is where you can get anything you can afford,",
						"but be careful, there are thieves and worse in Varrock",
						"Eventually, all roads lead to the city.",
						"There is a farm on the way to Varrock, just don't annoy the farmer!",
						"There is a store near here that can sell you basic items.",
						"The store is always open, so you can buy when you need something",
						"There is a church, A mill, a graveyard, and a few other places to go",
						"very close to here. You should find something useful in most of them");

					int optPlaces = multi(player, npc, false,
						"Are there any quests near here, and what will I need?",
						"What are all the people and creatures around here? What do they do?",
						"How do I become a better warrior?",
						"How can I avoid dying?");

					if (optPlaces == 0) {
						say(player, npc, "Are there any quests near here, and what will I need?");
						GuideDialogue(player, npc, GUIDE.QUESTS_NEARBY);
					} else if (optPlaces == 1) {
						say(player, npc, "What are all the people and creatures around here?",
							"What do they do?");
						GuideDialogue(player, npc, GUIDE.PEOPLE_AND_CREATURES);
					} else if (optPlaces == 2) {
						say(player, npc, "How do I become a better warrior?");
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					} else if (optPlaces == 3) {
						say(player, npc, "How can I avoid dying?");
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					}
					break;

				case GUIDE.PEOPLE_AND_CREATURES:
					npcsay(player, npc, "Some characters here are other games players,",
						"Some are creatures and people that live in runescape",
						"You can talk and trade and fight with most of them, if you want to");

					int optPeople = multi(player, npc,
						"Why would I want to fight?",
						"Would I benefit from speaking to other players?",
						"Should I attack the other players?",
						"How do I become a better warrior?");

					if (optPeople == 0) {
						GuideDialogue(player, npc, GUIDE.WHY_FIGHT);
					} else if (optPeople == 1) {
						GuideDialogue(player, npc, GUIDE.SPEAK_PLAYERS);
					} else if (optPeople == 2) {
						GuideDialogue(player, npc, GUIDE.ATTACK_PLAYERS);
					} else if (optPeople == 3) {
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					}
					break;

				case GUIDE.IMPROVE_WEAPS_N_ARMOUR:
					npcsay(player, npc, "To get better armour and weapons you will need to visit the stores.",
						"In Varrock there are many weapon and armour makers who will sell to you.",
						"They will also buy any weapons and armour you do not need,",
						"so keep any you want to sell.");

					int optWeaps = multi(player, npc,
						"How can I choose who is best to fight?",
						"What other skills will help me fight well?",
						"How can I avoid dying?",
						"Thanks, I just want to go kill things now");

					if (optWeaps == 0) {
						GuideDialogue(player, npc, GUIDE.BEST_TO_FIGHT);
					} else if (optWeaps == 1) {
						GuideDialogue(player, npc, GUIDE.SKILLS_HELP);
					} else if (optWeaps == 2) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					} else if (optWeaps == 3) {
						GuideDialogue(player, npc, GUIDE.KILL_THINGS_NOW);
					}
					break;

				case GUIDE.BEST_TO_FIGHT:
					npcsay(player, npc, "Be careful that you do not fight anyone too strong.",
						"The Palace guards around here are very tough for beginners,",
						"They will probably kill you instantly. Better to fight weaker people.",
						"There are some goblins just over the bridge that are not too tough",
						"Mind you, some goblins are strong. Always check the strength",
						"You can always run away, providing you have time to before you die.");

					int optFight = multi(player, npc,
						"How can I improve my weapons and my armour?",
						"What other skills will help me fight well?",
						"How can I avoid dying?",
						"Thanks, I just want to go kill things now");

					if (optFight == 0) {
						GuideDialogue(player, npc, GUIDE.IMPROVE_WEAPS_N_ARMOUR);
					} else if (optFight == 1) {
						GuideDialogue(player, npc, GUIDE.SKILLS_HELP);
					} else if (optFight == 2) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					} else if (optFight == 3) {
						GuideDialogue(player, npc, GUIDE.KILL_THINGS_NOW);
					}
					break;

				case GUIDE.SKILLS_HELP:
					npcsay(player, npc, "If you really want to be a good warrior,",
						"you will find other skills are useful.",
						"If you improve your magic you will find many battle spells",
						"Cooking is also a good skill to have.",
						"As feeding yourself will make you stronger when you need it.",
						"If you can make your own weapons then you will find it much cheaper.");

					int optSkills = multi(player, npc,
						"How can I improve my weapons and my armour?",
						"How can I choose who is best to fight?",
						"How can I avoid dying?",
						"Thanks, I just want to go kill things now");

					if (optSkills == 0) {
						GuideDialogue(player, npc, GUIDE.IMPROVE_WEAPS_N_ARMOUR);
					} else if (optSkills == 1) {
						GuideDialogue(player, npc, GUIDE.BEST_TO_FIGHT);
					} else if (optSkills == 2) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					} else if (optSkills == 3) {
						GuideDialogue(player, npc, GUIDE.KILL_THINGS_NOW);
					}
					break;

				case GUIDE.AVOID_DYING:
					npcsay(player, npc, "To start with, the castle area is safe",
						"If you venture out, check your map for other creatures close",
						"And if you think they may attack, move away",
						"You will find which creatures and people attack, and which don't",
						"If you get attacked, try to run back where you came from",
						"most creatures won't chase you outside their own area");

					int optDying = multi(player, npc,
						"How can I see who is best to attack?",
						"What is my strength, and how can I improve it?",
						"You have been a great help, thankyou");

					if (optDying == 0) {
						GuideDialogue(player, npc, GUIDE.BEST_TO_ATTACK);
					} else if (optDying == 1) {
						GuideDialogue(player, npc, GUIDE.IMPROVE_STRENGTH);
					} else if (optDying == 2) {
						GuideDialogue(player, npc, GUIDE.BEEN_GREAT_HELP);
					}
					break;

				case GUIDE.BEST_TO_ATTACK:
					npcsay(player, npc, "When you put the mouse over a character, see if you can attack.",
						"If the attack choice is red then it will be hard to win.",
						"If the attack is green You should win unless you are already injured,",
						"Try the goblins over the bridge, most people can beat them",
						"Just be sure you are wearing your armour and wielding your best weapon");

					int optAttack = multi(player, npc,
						"How can I avoid dying?",
						"What is my strength, and how can I improve it?",
						"You have been a great help, thankyou");

					if (optAttack == 0) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					} else if (optAttack == 1) {
						GuideDialogue(player, npc, GUIDE.IMPROVE_STRENGTH);
					} else if (optAttack == 2) {
						GuideDialogue(player, npc, GUIDE.BEEN_GREAT_HELP);
					}
					break;

				case GUIDE.IMPROVE_STRENGTH:
					npcsay(player, npc, "When you fight, your hit level is displayed in green over your head",
						"This is your hit level, as in the statistics box",
						"When you get injured, this will get lower. It will rise again",
						"if you stay out of trouble. Eating will also help it return",
						"As you win more fights, your hit level will increase");

					int optStrength = multi(player, npc,
						"How can I avoid dying?",
						"How can I see who is best to attack?",
						"You have been a great help, thankyou");

					if (optStrength == 0) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					} else if (optStrength == 1) {
						GuideDialogue(player, npc, GUIDE.BEST_TO_ATTACK);
					} else if (optStrength == 2) {
						GuideDialogue(player, npc, GUIDE.BEEN_GREAT_HELP);
					}
					break;

				case GUIDE.BEEN_GREAT_HELP:
					npcsay(player, npc, "Go and use what you have learnt",
						"Soon you will be swarming the dungeons with the boldest");
					break;

				case GUIDE.WHY_FIGHT:
					npcsay(player, npc, "If you fight when you can win, and search everywhere you go,",
						"you will find your strength and treasure will increase.",
						"Many characters drop treasure as they die.");

					int optWhyFight = multi(player, npc,
						"Would I benefit from speaking to other players?",
						"Should I attack the other players?",
						"How do I become a better warrior?",
						"How can I avoid dying?");

					if (optWhyFight == 0) {
						GuideDialogue(player, npc, GUIDE.SPEAK_PLAYERS);
					} else if (optWhyFight == 1) {
						GuideDialogue(player, npc, GUIDE.ATTACK_PLAYERS);
					} else if (optWhyFight == 2) {
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					} else if (optWhyFight == 3) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					}
					break;

				case GUIDE.SPEAK_PLAYERS:
					npcsay(player, npc, "You can type to speak to other players. Anyone close to you will hear.",
						"Just be careful about paying for player advice, Its not usually worth it.",
						"You can spot the real players because you can trade directly with them.",
						"Other players will have knowledge of the game, many will be happy to help");

					int optSpeak = multi(player, npc,
						"Why would I want to fight?",
						"Should I attack the other players?",
						"How do I become a better warrior?",
						"How can I avoid dying?");

					if (optSpeak == 0) {
						GuideDialogue(player, npc, GUIDE.WHY_FIGHT);
					} else if (optSpeak == 1) {
						GuideDialogue(player, npc, GUIDE.ATTACK_PLAYERS);
					} else if (optSpeak == 2) {
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					} else if (optSpeak == 3) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					}
					break;

				case GUIDE.ATTACK_PLAYERS:
					npcsay(player, npc, "To be fair, you cannot attack just any of the other players",
						"You can fight with people about the same strength as you, and anyone tougher",
						"If you die, you lose everything in your back pack,",
						"so do not be persuaded to attack any player who asks you to",
						"They will just be after your treasure. You can't run away immediately");

					int optAttackP = multi(player, npc,
						"Why would I want to fight?",
						"Would I benefit from speaking to other players?",
						"How do I become a better warrior?",
						"How can I avoid dying?");

					if (optAttackP == 0) {
						GuideDialogue(player, npc, GUIDE.WHY_FIGHT);
					} else if (optAttackP == 1) {
						GuideDialogue(player, npc, GUIDE.SPEAK_PLAYERS);
					} else if (optAttackP == 2) {
						GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
					} else if (optAttackP == 3) {
						GuideDialogue(player, npc, GUIDE.AVOID_DYING);
					}
					break;

				case GUIDE.KILL_THINGS_NOW:
					npcsay(player, npc, "Okay, just take care of yourself.",
						"However tough you get, there is always something tougher");
					break;
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		npcsay(player, npc, "Hello Adventurer, can I guide you on your journeys?");

		int option = multi(player, npc, false, // do not send over
			"I just got here, where should I start?",
			"I just want to fight, how can I be a good warrior?",
			"I keep getting killed. It's annoying me. What should I do?",
			"I am happy to just try things on my own, thanks");

		if (option == 0) {
			say(player, npc, "I just got here, where should I start?");
			GuideDialogue(player, npc, GUIDE.WHERE_START);
		} else if (option == 1) {
			say(player, npc, "How do I become a better warrior?");
			GuideDialogue(player, npc, GUIDE.GOOD_WARRIOR);
		} else if (option == 2) {
			// https://web.archive.org/web/20010805112653/http://runescapeplace.homestead.com:80/files/0101rune.jpg
			// this line of dialogue got slightly revised grammar
			say(player, npc, "I keep getting killed. It's annoying me. What should I do?");
			GuideDialogue(player, npc, GUIDE.GETTING_KILLED);
		} else if (option == 3) {
			say(player, npc, "I am happy to just try things on my own, thanks");
			npcsay(player, npc, "I wish you luck on your travels, adventurer",
				"I am usually here if you think I can help you");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.GUIDE.id() || npc.getID() == NpcId.GUIDE_FEMALE.id();
	}

	class GUIDE {
		private static final int WHERE_START = 0;
		private static final int GOOD_WARRIOR = 1;
		private static final int GETTING_KILLED = 2;
		private static final int QUESTS_NEARBY = 3;
		private static final int PLACES_TO_GO = 4;
		private static final int PEOPLE_AND_CREATURES = 5;
		private static final int IMPROVE_WEAPS_N_ARMOUR = 6;
		private static final int BEST_TO_FIGHT = 7;
		private static final int SKILLS_HELP = 8;
		private static final int AVOID_DYING = 9;
		private static final int BEST_TO_ATTACK = 10;
		private static final int IMPROVE_STRENGTH = 11;
		private static final int BEEN_GREAT_HELP = 12;
		private static final int WHY_FIGHT = 13;
		private static final int SPEAK_PLAYERS = 14;
		private static final int ATTACK_PLAYERS = 15;
		private static final int KILL_THINGS_NOW = 16;
	}
}
