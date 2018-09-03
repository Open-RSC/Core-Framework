package com.openrsc.server.plugins.quests.members;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.hasItemAtAll;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class FishingContest implements QuestInterface,TalkToNpcListener,
		TalkToNpcExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, InvUseOnObjectListener,
		InvUseOnObjectExecutiveListener {

	class SINISTER {
		private static final int FISHING = 0;
	}

	private void bigDaveDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case 1:
		case 2:
			playerTalk(p, n, "Hey what have you caught?");
			npcTalk(p, n, "Go away! This is my spot!");
			break;
		}
	}

	@Override
	public boolean blockInvUseOnObject(final GameObject obj,
			final Item item, final Player player) {
		if (obj.getID() == 355) {
			return true;
		}
		if (obj.getID() == 350) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockObjectAction(final GameObject obj,
			final String command, final Player player) {
		if (obj.getID() == 358) {
			return true;
		}
		if (obj.getID() == 352 || obj.getID() == 351) {
			return true;
		}
		if (obj.getID() == 359) {
			return true;
		}
		return false;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == 355) {
			return true;
		}
		if (n.getID() == 347) {
			return true;
		}
		if (n.getID() == 346) {
			return true;
		}
		if (n.getID() == 345) {
			return true;
		}
		if (n.getID() == 354) {
			return true;
		}
		if (n.getID() == 353) {
			return true;
		}
		return false;
	}

	private void bonzoDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case 1:
		case 2: // ALL QUEST STAGES SAME DIALOGUE
			if (p.getCache().hasKey("paid_contest_fee")) {
				npcTalk(p, n, "so how are you doing so far?");
				final int contestStartedMenu = showMenu(p, n, new String[] {
						"I have this big fish,is it enough to win?",
						"I think I might still be able to find a bigger fish" });
				if (contestStartedMenu == 0) {
					npcTalk(p, n, "Well we'll just wait till time is up");
					p.message("You wait");
					sleep(2000);
					npcTalk(p, n, "Okay folks times up",
							"Lets see who caught the biggest fish");
					message(p, "You hand over your catch");
					npcTalk(p, n, "We have a new winner");
					if (hasItem(p, 717)) {
						removeItem(p, 717, 1);
						npcTalk(p, n, "The heroic looking person",
								"who was fishing by the pipes",
								"Has caught the biggest carp",
								"I've seen since Grandpa Jack used to compete");
						p.message("you are given the Hemenster fishing trophy");
						addItem(p, 720, 1);
						p.updateQuestStage(getQuestId(), 3);
					} else {
						npcTalk(p, n, "The vampire looking person",
								"who was fishing by the oak tree");
					}
				} else if (contestStartedMenu == 1) {
					npcTalk(p, n, " Ok, good luck");
				}
				return;
			}
			npcTalk(p, n, "Roll up, roll up",
					"Enter the great Hemenster fishing competition",
					"only 5gp entrance fee");
			final int first = showMenu(p, n, new String[] {
					"I'll give that a go then",
					"No thanks, I'll just watch the fun" });
			if (first == 0) {
				if (p.getInventory().countId(10) >= 5) {
					npcTalk(p, n, "Marvelous");
					p.message("You pay bonzo 5 coins");
					removeItem(p, 10, 5);
					npcTalk(p, n, "Ok we've got all the fishermen",
							"It's time to roll",
							"Ok nearly everyone is in there place already",
							"You fish in the spot by the oak tree",
							"And the Sinister stranger you fish by the pipes");
					p.message("Your fishing competition spot is beside the oak tree");
					p.getCache().store("paid_contest_fee", true);
				} else {
					p.message("seems like I don't have enough coins to participate");
				}
			} else if (first == 1) {
				// NOTHING
			}
			break;
		}
	}

	@Override
	public int getQuestId() {
		return Constants.Quests.FISHING_CONTEST;
	}

	@Override
	public String getQuestName() {
		return "Fishing contest (members)";
	}

	private void grandphaJackDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case 1:
		case 2:
			npcTalk(p, n, "Hello young man", "Come to visit old Grandpa Jack?",
					"I can tell ye stories for sure",
					"I used to be the best fisherman these parts have seen");
			final int first = showMenu(p, n, new String[] {
					"Tell me a story then",
					"Are you entering the fishing competition?",
					"Sorry I don't have time now" });
			if (first == 0) {
				npcTalk(p,
						n,
						"Well when I was a young man",
						"We used to take fishing trips over to Catherby",
						"The fishing over there - now that was something",
						"Anyway we decided to do a bit of fishing with our nets",
						"I wasn't having the best of days",
						"Turning up nothing but old boots and bits of seaweed",
						"Then my net suddenly got really heavy",
						"I pulled it up",
						"And to my amazement I'd caught this little chest thing",
						"even more amazing was when I opened it",
						"It contained a diamond the size of a radish",
						"That's the best catch I've ever had!");
			} else if (first == 1) {
				npcTalk(p, n, "Ah the Hemenster fishing competition",
						"I know all about that",
						"I won that four years straight",
						"I'm to old for that lark now though");
				final int second = showMenu(p, n, new String[] {
						" I don't suppose you could give me any hints?",
						" That's less competition for me then" });
				if (second == 0) {
					npcTalk(p,
							n,
							"Well you sometimes get these really big fish",
							"In the water just by the outflow pipes",
							"Think they're some kind of carp",
							"try to get a spot round there",
							"The best sort of bait for them is red vine worms",
							"I used to get those from McGruber's wood, just north of here",
							"dig around in the red vines up there");
					if (p.getQuestStage(getQuestId()) != 2) {
						p.updateQuestStage(getQuestId(), 2);
					}
				} else if (second == 1) {
					// NOTHING
				}
			} else if (first == 2) {
				npcTalk(p, n, "sigh", "Young people - always in such a rush");
			}
			break;
		}
	}

	@Override
	public void handleReward(final Player p) {
		p.updateQuestStage(Constants.Quests.FISHING_CONTEST, -1);
		p.message("Well done you have completed the fishing competition quest");
		p.message("@gre@You have gained 1 quest point!");
		p.incQuestPoints(1);
		if(p.getSkills().getMaxStat(10) <= 23) {
			p.incQuestExp(10, (p.getSkills().getMaxStat(10) - 10) * 300 + 3900);
		} else if(p.getSkills().getMaxStat(10) >= 24) {
			p.incQuestExp(10, (p.getSkills().getMaxStat(10) - 24) * 300 + 8900);
		}
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	private void joshuaDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case 1:
		case 2:
			playerTalk(p, n, "Have you caught a big one yet?");
			npcTalk(p, n, "I hope I can catch one as big as Grandpa Jack!");
			break;
		}
	}

	private void mountainDwarfDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
		case 0:
			npcTalk(p, n, "hmmph what do you want");

			final int first = showMenu(p, n, new String[] {
					"I was wondering what was down those stairs?",
					"I was just stopping to say hello",
					"I'm bigger than you let me by" });

			if (first == 0) {
				npcTalk(p, n, " You can't go down there");
				final int second = showMenu(p, n, new String[] {
						"I didn't want to anyway", "Why not?",
						"I'm bigger than you let me by" });
				if (second == 0) {
					npcTalk(p, n, "Good");
				} else if (second == 1) {
					npcTalk(p, n, "This is the home of the mountain dwarves",
							"How would you like it if I wanted to take a short cut through your home?");
					final int third = showMenu(p, n, new String[] {
							"Ooh is this a short cut to somewhere",
							"Oh sorry I hadn't realised it was private",
							"If you were my friend I wouldn't mind it" });
					if (third == 0) {
						npcTalk(p, n, "Well it is easier to go this way",
								"Than through passes full of wolves");
					} else if (third == 1) {
						// NOTHING
					} else if (third == 2) {
						npcTalk(p, n, "Yes, but I don't even know you");
						final int fourth = showMenu(p, n, new String[] {
								"Well lets be friends",
								"You're a grumpy little man aren't you?" });
						if (fourth == 0) {
							npcTalk(p, n, "I don't make friends easily",
									"People need to earn my trust first");

							final int fifth = showMenu(p, n, new String[] {
									"And how am I meant to do that?",
									"You're a grumpy little man aren't you?" });
							if (fifth == 0) {
								npcTalk(p,
										n,
										"My we are the persistant one aren't we",
										"Well theres a certain gold artifact we're after",
										"We dwarves are big fans of gold",
										"This artifact is the first prize at the hemenster fishing competition",
										"Fortunately we have acquired a pass to enter that competition",
										"Unfortunately Dwarves don't make good fishermen");
								final int six = showMenu(p, n, new String[] {
										"Fortunately I'm alright at fishing",
										"I'm not much of a fisherman either" });
								if (six == 0) {
									npcTalk(p,
											n,
											"Okay I entrust you with our competition pass",
											"go to Hemenster and make us proud");
									addItem(p, 719, 1);
									p.updateQuestStage(getQuestId(), 1);
								} else if (six == 1) {
									npcTalk(p, n, "Well that's too bad");
								}
							} else if (fifth == 1) {
								npcTalk(p, n, " Don't you know it");
							}

						} else if (fourth == 1) {
							npcTalk(p, n, " Don't you know it");
						}
					}
				} else if (second == 2) {
					npcTalk(p, n, "Go away",
							"You're not gonna bully your way in here");
				}
			} else if (first == 1) {
				npcTalk(p, n, "Hello then");
			} else if (first == 2) {
				npcTalk(p, n, "Go away",
						"You're not gonna bully your way in here");
			}

			break;
		case 1:
		case 2:
			if(!hasItemAtAll(p, 719)) {
				playerTalk(p,n, "I lost the pass...");
				npcTalk(p,n, "Try not to lose it this time");
				message(p, "The dwarf hands you a new pass");
				addItem(p, 719, 1);
				return;
			}
			npcTalk(p, n, "Have you won yet?");
			playerTalk(p, n, "No, not yet");
			npcTalk(p, n, "Well come back once you win");
		
			break;
		case 3:
			npcTalk(p, n, "Have you won yet?");
			playerTalk(p, n, "Yes I have");
			npcTalk(p, n, "Well done, so where is the trophy?");
			if (hasItem(p, 720)) {
				playerTalk(p, n, "I have it right here");
				message(p, "you give the trophy to the dwarf");
				removeItem(p, 720, 1);
				npcTalk(p, n, "Okay we will let you in now");
				p.sendQuestComplete(Quests.FISHING_CONTEST);
			} else {
				p.message("seems like you don't have the tropyhy with you");
			}
			break;
		case -1:
			npcTalk(p, n, "Welcome oh great fishing champion",
					"Feel free to pop by any time!");
			break;
		}
	}

	@Override
	public void onInvUseOnObject(final GameObject obj, final Item item,
			final Player player) {

		if (obj.getID() == 355 && item.getID() == 211) { // teleport coords:
															// 567, 451
			if (player.getQuestStage(getQuestId()) == 2) {
				message(player, "you dig in amongst the vines",
						"You find a red vine worm");
				addItem(player, 715, 1);
			}
		}
		if (obj.getID() == 350 && item.getID() == 218) {
			if (player.getQuestStage(getQuestId()) == 2
					&& player.getCache().hasKey("paid_contest_fee")) {
				message(player, "You stash the garlic in the pipe");
				player.getInventory().remove(218, 1);
				Npc sinister = getNearestNpc(player, 346, 10);
				Npc bonzo = getNearestNpc(player, 347, 10);
				if (sinister != null && bonzo != null) {
					npcTalk(player, sinister,
							"Arrgh what is that ghastly smell",
							"I think I will move over here instead");
					sinister.teleport(570, 495);
					npcTalk(player, bonzo,
							"Hmm you'd better go and take the area by the pipes then");
					player.message("your fishing competition spot has been moved beside the pipes");
					player.getCache().store("garlic_activated", true);
				}
			} else {
				player.message("Nothing interesting happens");
			}
		}
	}

	@Override
	public void onObjectAction(final GameObject obj, final String command,
			final Player p) {

		if (obj.getID() == 358) {
			Npc bonzo = getNearestNpc(p,347, 15);
			Npc morris = getNearestNpc(p,349, 15);
			if (p.getX() <= 564) {

				if (morris != null) {
					npcTalk(p, morris, "competition pass please");
					if (hasItem(p, 719) && p.getQuestStage(this) > 0) {
						message(p, "You show Morris your pass");
						npcTalk(p, morris, "Move on through");
						doGate(p, obj, 357);
					} else {
						int m = showMenu(p, morris,
								" I don't have one of them",
								" What do I need that for?");
						if (m == 1) {
							npcTalk(p, morris,
									" This is the entrance to the Hementster fishing competition");
							npcTalk(p, morris, " It's a high class competition");
							npcTalk(p, morris, " Invitation only");
						}
					}
				} else
					System.err.println("morris is null");
			} else if (p.getX() >= 565) {
				if (p.getQuestStage(getQuestId()) == 3) {
					doGate(p, obj, 357);
					return;
				}
				if (bonzo != null) {
					npcTalk(p, bonzo,
							"so you're calling it quits here for now?");
					int leaveMenu = showMenu(p, bonzo,  
							"Yes I'll compete again another day",
							"Actually I'll go back and catch some more");
					if (leaveMenu == 0) {
						if(p.getCache().hasKey("paid_contest_fee")) {
							p.getCache().remove("paid_contest_fee");
						}
						if(p.getCache().hasKey("garlic_activated")) {
						p.getCache().remove("garlic_activated");
						}
						doGate(p, obj, 357);
					} else if (leaveMenu == 1) {
						npcTalk(p, bonzo, "Good luck");
					}
				} else {
					return;
				}
			}
		}
		if (obj.getID() == 351) {
			Npc bonzo = getNearestNpc(p,347, 10);
			if (bonzo != null && !p.getCache().hasKey("paid_contest_fee")) {
				npcTalk(p, bonzo,
						"Hey you need to pay to join the competition first",
						"only 5gp entrance fee");
			}
			if (p.getQuestStage(getQuestId()) >= 1 && !hasItem(p, 715)) {
				p.message("you have no bait to catch fish here");
			} else {
				p.message("I need somehow get rid of the sinister and use his spot");
			}
		}
		if (obj.getID() == 352) {
			Npc sinister = getNearestNpc(p,346, 10);
			Npc bonzo = getNearestNpc(p,347, 10);
			if (bonzo != null && !p.getCache().hasKey("paid_contest_fee")) {
				npcTalk(p, bonzo,
						"Hey you need to pay to join the competition first",
						"only 5gp entrance fee");
				return;
			} else if (sinister != null && p.getQuestStage(getQuestId()) >= 1
					&& !p.getCache().hasKey("garlic_activated")) {
				npcTalk(p, sinister, "I think you will find that is my spot");
				playerTalk(p, sinister, "Can't you go to another spot?");
				npcTalk(p, sinister, "I like this place",
						"I like to savour the aroma coming from these pipes");
				return;
			} else if (p.getCache().hasKey("garlic_activated")) {
				if (hasItem(p, 715) && hasItem(p, 377)) {
					p.message("You catch a giant carp");
					p.getInventory().add(new Item(717));
					p.getInventory().remove(715, 1);
					
				} else {
					p.message("I don't have the equipment to catch a fish");
				}
			}
		}
		if (obj.getID() == 359) {
			if (p.getQuestStage(getQuestId()) == -1) {
				p.message("You go down the stairs");
				if(obj.getX() == 426 && obj.getY() == 458) {
					p.teleport(426, 3294, false);
				} else {
					p.teleport(385, 3301, false);
				}
			} else {
				final Npc dwarf = World.getWorld().getNpc(355, 375, 395, 445,
						475);
				if (dwarf != null) {
					npcTalk(p, dwarf, "Have you won yet?");
					if (p.getQuestStage(getQuestId()) == 3) {
						playerTalk(p, dwarf, "Yes I have");
						npcTalk(p, dwarf, "Well done, so where is the trophy?");
						if (hasItem(p, 720)) {
							playerTalk(p, dwarf, "I have it right here");
							message(p, "you give the trophy to the dwarf");
							removeItem(p, 720, 1);
							npcTalk(p, dwarf, "Okay we will let you in now");
							p.sendQuestComplete(Quests.FISHING_CONTEST);
						} else {
							p.message("seems like you don't have the tropyhy with you");
						}
					} else {
						playerTalk(p, dwarf, "No, not yet");
						npcTalk(p, dwarf, "Well come back once you win");
					}
				}
			}
		}
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == 355) { // MOUNTAIN DWARF
			mountainDwarfDialogue(p, n);
		}
		if (n.getID() == 347) { // BONZO
			bonzoDialogue(p, n);
		}
		if (n.getID() == 346) {
			sinisterDialogue(p, n, -1); // SINISTER
		}
		if (n.getID() == 345) {
			grandphaJackDialogue(p, n); // GRANPHA
		}
		if (n.getID() == 354) {
			joshuaDialogue(p, n); // JOSHUA
		}
		if (n.getID() == 353) {
			bigDaveDialogue(p, n); // BIG DAVE
		}
	}

	private void sinisterDialogue(final Player p, final Npc n, final int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 1:
			case 2:
				npcTalk(p, n, "..");
				final int first = showMenu(p, n, new String[] { "..?",
						"Who are you?", "so you like fishing?" });
				if (first == 0) {
					npcTalk(p, n, " ...");
				} else if (first == 1) {
					npcTalk(p, n, "My name is Vlad",
							"I come from far avay, vere the sun is not so bright");
					final int second = showMenu(p, n, new String[] {
							"You're a vampire aren't you?",
							"Is it nice there?", "so you like fishing?" });
					if (second == 0) {
						npcTalk(p,
								n,
								"Just because I can't stand the smell of garlic",
								"and I don't like bright sunlight",
								"Doesn't necessarily mean I'm a vampire");
					} else if (second == 1) {
						npcTalk(p, n, "Ves tis' very nice vere");
					} else if (second == 2) {
						sinisterDialogue(p, n, SINISTER.FISHING);
					}

				} else if (first == 2) {
					sinisterDialogue(p, n, SINISTER.FISHING);
				}
				break;
			}
		}
		switch (cID) {
		case SINISTER.FISHING:
			npcTalk(p, n, "My doctor told be to take up a velaxing hobby",
					"vhen I am stressed I tend to get a little..", "..thirsty");
			final int third = showMenu(p, n, new String[] {
					"If you get thirsty you should drink something",
					"Well good luck with the fishing" });
			if (third == 0) {
				npcTalk(p, n, "I think I may do that soon");
			} else if (third == 1) {
				npcTalk(p, n, "Luck has nothing to do vith it",
						"It is all in the technique");
			}
			break;
		}
	}
}
