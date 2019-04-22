package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.hasItemAtAll;
import static com.openrsc.server.plugins.Functions.incQuestReward;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

public class FishingContest implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, ObjectActionListener,
	ObjectActionExecutiveListener, InvUseOnObjectListener,
	InvUseOnObjectExecutiveListener, InvUseOnNpcListener,
	InvUseOnNpcExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.FISHING_CONTEST;
	}

	@Override
	public String getQuestName() {
		return "Fishing contest (members)";
	}
	
	@Override
	public boolean isMembers() {
		return true;
	}
	
	@Override
	public void handleReward(final Player p) {
		p.updateQuestStage(Constants.Quests.FISHING_CONTEST, -1);
		p.message("Well done you have completed the fishing competition quest");
		p.message("@gre@You haved gained 1 quest point!");
		int[] questData = Quests.questData.get(Quests.FISHING_CONTEST);
		if (p.getSkills().getMaxStat(Skills.FISHING) <= 23) {
			questData[Quests.MAPIDX_BASE] = 900;
			incQuestReward(p, questData, true);
		} else if (p.getSkills().getMaxStat(Skills.FISHING) >= 24) {
			questData[Quests.MAPIDX_BASE] = 1700;
			incQuestReward(p, questData, true);
		}
	}
	
	private void addCatchCache(final Player p, int catchId) {
		String catchString = "";
		if (p.getCache().hasKey("contest_catches")) {
			catchString = p.getCache().getString("contest_catches") + "-";
		}
		catchString += catchId;
		p.getCache().store("contest_catches", catchString);
	}

	private void bigDaveDialogue(final Player p, final Npc n) {
		npcTalk(p, n, "Oi whaddya think ya doin'", "I'm fishin' here",
			"Now beat it");
	}

	@Override
	public boolean blockInvUseOnObject(final GameObject obj,
									   final Item item, final Player player) {
		return obj.getID() == 355 || obj.getID() == 350;
	}

	@Override
	public boolean blockObjectAction(final GameObject obj,
									 final String command, final Player player) {
		//353 - big dave's spot, 354 - joshua's spot
		return obj.getID() == 358 || obj.getID() == 352 || obj.getID() == 351
				|| obj.getID() == 359 || obj.getID() == 353 || obj.getID() == 354;
	}

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		// joshua and big dave were not interested in talking directly
		return DataConversions.inArray(new int[] {NpcId.MOUNTAIN_DWARF.id(), NpcId.BONZO.id(), NpcId.SINISTER_STRANGER.id(),
				NpcId.GRANDPA_JACK.id()}, n.getID());
	}

	private void bonzoDialogue(final Player p, final Npc n, final boolean isDirectTalk) {
		Npc sinister = getNearestNpc(p, NpcId.SINISTER_STRANGER.id(), 10);
		switch (p.getQuestStage(this)) {
			// quest completed
			case -1:
				if (!isDirectTalk) {
					message(p, "you have already won the fishing competition");
				} else {
					npcTalk(p, n, "Hello champ",
						"So any hints on how to fish so well");
					playerTalk(p, n, "I think I'll keep them to myself");
				}
				break;
			default: // EVERY OTHER QUEST STAGE
				if (p.getCache().hasKey("paid_contest_fee")) {
					String catches[] = null;
					boolean hasCarp = false;

					if (p.getCache().hasKey("contest_catches")) {
						catches = p.getCache().getString("contest_catches").split("-");
					}

					for (String aCatch : catches) {
						hasCarp |= (Integer.valueOf(aCatch) == ItemId.RAW_GIANT_CARP.id() && hasItem(p, ItemId.RAW_GIANT_CARP.id()));
					}

					npcTalk(p, n, "so how are you doing so far?");
					if (hasCarp) {
						//do not send over
						final int contestStartedMenu = showMenu(p, n, false,
							"I have this big fish,is it enough to win?",
							"I think I might still be able to find a bigger fish");
						if (contestStartedMenu == 0) {
							playerTalk(p, n, "I have this big fish", "Is it enough to win?");
							npcTalk(p, n, "Well we'll just wait till time is up");
							p.message("You wait");
							sleep(2000);
							bonzoTimesUpDialogue(p, n);
						} else if (contestStartedMenu == 1) {
							playerTalk(p, n, "I think I might still be able to find a bigger fish");
							npcTalk(p, n, "Ok, good luck");
						}
					} else {
						playerTalk(p, n, "I think I might still be able to find a bigger fish");
						npcTalk(p, n, "Ok, good luck");
					}

					return;
				} else {
					// with trophy does not allow to enter competition
					if (hasItem(p, ItemId.HEMENSTER_FISHING_TROPHY.id())) {
						npcTalk(p, n, "Hello champ",
							"So any hints on how to fish so well");
						playerTalk(p, n, "I think I'll keep them to myself");
						return;
					}

					if (isDirectTalk) {
						npcTalk(p, n, "Roll up, roll up",
							"Enter the great Hemenster fishing competition",
							"only 5gp entrance fee");
					} else {
						npcTalk(p, n, "Hey you need to pay to join the competition first",
							"only 5gp entrance fee");
					}
					final int first = showMenu(p, n,
						"I'll give that a go then",
						"No thanks, I'll just watch the fun");
					if (first == 0) {
						npcTalk(p, n, "Marvelous");
						if (p.getInventory().countId(ItemId.COINS.id()) >= 5) {
							p.message("You pay bonzo 5 coins");
							removeItem(p, ItemId.COINS.id(), 5);
							npcTalk(p, n, "Ok we've got all the fishermen",
								"It's time to roll",
								"Ok nearly everyone is in there place already",
								"You fish in the spot by the oak tree",
								"And the Sinister stranger you fish by the pipes");
							if (!p.getCache().hasKey("garlic_activated")) {
								p.message("Your fishing competition spot is beside the oak tree");
							} else {
								npcTalk(p, sinister,
									"Arrgh what is that ghastly smell",
									"I think I will move over here instead");
								sinister.teleport(570, 495);
								npcTalk(p, n,
									"Hmm you'd better go and take the area by the pipes then");
								p.message("Your fishing competition spot is beside the pipes");
							}
							p.getCache().store("paid_contest_fee", true);

						} else {
							message(p, "I don't have the 5gp though");
							npcTalk(p, n, "No pay, no play");
						}
					} else if (first == 1) {
						// NOTHING
					}
				}
				break;
		}
	}

	private void bonzoTimesUpDialogue(final Player p, final Npc n) {
		String catches[] = null;
		boolean hadCarp = false;

		if (p.getCache().hasKey("contest_catches")) {
			catches = p.getCache().getString("contest_catches").split("-");
		}

		npcTalk(p, n, "Okay folks times up",
			"Lets see who caught the biggest fish");
		message(p, "You hand over your catch");
		for (String aCatch : catches) {
			hadCarp |= (Integer.valueOf(aCatch) == ItemId.RAW_GIANT_CARP.id() && hasItem(p, ItemId.RAW_GIANT_CARP.id()));
			removeItem(p, Integer.valueOf(aCatch), 1);
		}
		p.getCache().remove("contest_catches");
		p.getCache().remove("paid_contest_fee");

		if (hadCarp) {
			npcTalk(p, n, "We have a new winner");
			npcTalk(p, n, "The heroic looking person",
				"who was fishing by the pipes",
				"Has caught the biggest carp",
				"I've seen since Grandpa Jack used to compete");
			p.message("you are given the Hemenster fishing trophy");
			addItem(p, ItemId.HEMENSTER_FISHING_TROPHY.id(), 1);
			p.updateQuestStage(getQuestId(), 3);
		}
		// select another one from chance
		else {
			int chance_stranger = 80;
			int chance_dave = 15;
			int rol = DataConversions.random(0, 100);
			npcTalk(p, n, "And the winner is...");
			if (chance_stranger > rol) {
				npcTalk(p, n, "The stranger in black");
			} else if (chance_dave > rol - 80) {
				npcTalk(p, n, "local favourite- Big Dave");
			} else {
				npcTalk(p, n, "the surprising Joshua");
			}
		}
	}

	private void grandpaJackDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
			case 1:
			case 2:
				npcTalk(p, n, "Hello young man", "Come to visit old Grandpa Jack?",
					"I can tell ye stories for sure",
					"I used to be the best fisherman these parts have seen");
				int first = showMenu(p, n,
					"Tell me a story then",
					"Are you entering the fishing competition?",
					"Sorry I don't have time now");
				if (first == 0) {
					npcTalk(p,
						n,
						"Well when I were a young man",
						"We used to take fishing trips over to Catherby",
						"The fishing over there - now that was something",
						"Anyway we decided to do a bit of fishing with our nets",
						"I wasn't having the best of days",
						"Tuning up nothing but old boots and bits of seaweed",
						"Then my net suddenly got really heavy",
						"I pulled it up",
						"To my amazement I'd caught this little chest thing",
						"even more amazing was when I opened it",
						"It contained a diamond the size of a radish",
						"That's the best catch I've ever had!");
				} else if (first == 1) {
					npcTalk(p, n, "Ah the Hemenster fishing competition",
						"I know all about that",
						"I won that four years straight",
						"I'm to old for that lark now though");
					//do not send over
					final int second = showMenu(p, n, false,
						"I don't suppose you could give me any hints?",
						"That's less competition for me then");
					if (second == 0) {
						playerTalk(p, n, "I don't suppose you could give me any hints?");
						npcTalk(p,
							n,
							"Well you sometimes get these really big fish",
							"In the water just by the outflow pipes",
							"Think they're some kind of carp",
							"try to get a spot round there",
							"The best sort of bait for them is red vine worms",
							"I used to get those from McGruber's wood, north of here",
							"dig around in the red vines up there");
						if (p.getQuestStage(getQuestId()) != 2) {
							p.updateQuestStage(getQuestId(), 2);
						}
					} else if (second == 1) {
						playerTalk(p, n, "That's less competition for me then\"");
					}
				} else if (first == 2) {
					npcTalk(p, n, "sigh", "Young people - always in such a rush");
				}
				break;
			default:
				npcTalk(p, n, "Hello young man", "Come to visit old Grandpa Jack?",
					"I can tell ye stories for sure",
					"I used to be the best fisherman these parts have seen");
				first = showMenu(p, n,
					"Tell me a story then",
					"Sorry I don't have time now");
				if (first == 0) {
					npcTalk(p,
						n,
						"Well when I were a young man",
						"We used to take fishing trips over to Catherby",
						"The fishing over there - now that was something",
						"Anyway we decided to do a bit of fishing with our nets",
						"I wasn't having the best of days",
						"Tuning up nothing but old boots and bits of seaweed",
						"Then my net suddenly got really heavy",
						"I pulled it up",
						"To my amazement I'd caught this little chest thing",
						"even more amazing was when I opened it",
						"It contained a diamond the size of a radish",
						"That's the best catch I've ever had!");
				} else if (first == 1) {
					npcTalk(p, n, "sigh", "Young people - always in such a rush");
				}
				break;
		}
	}

	private void joshuaDialogue(final Player p, final Npc n) {
		npcTalk(p, n, "This is my fishing spot", "Ya don't wanna be fishing 'ere mate",
			"Cos I'll break your knuckles");
	}

	private void mountainDwarfDialogue(final Player p, final Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "hmmph what do you want");

				//do not send over
				final int first = showMenu(p, n, false,
					"I was wondering what was down those stairs?",
					"I was just stopping to say hello");

				if (first == 0) {
					playerTalk(p, n, "I was just wondering what was down those stairs?");
					npcTalk(p, n, "You can't go down there");
					//do not send over
					final int second = showMenu(p, n, false,
						"I didn't want to anyway", "Why not?",
						"I'm bigger than you let me by");
					if (second == 0) {
						playerTalk(p, n, "I didn't want to anyway");
						npcTalk(p, n, "Good");
					} else if (second == 1) {
						playerTalk(p, n, "Why not?");
						npcTalk(p, n, "This is the home of the mountain dwarves",
							"How would you like it if I wanted to take a short cut through your home");
						//do not send over
						final int third = showMenu(p, n, false,
							"Ooh is this a short cut to somewhere",
							"Oh sorry I hadn't realised it was private",
							"If you were my friend I wouldn't mind it");
						if (third == 0) {
							playerTalk(p, n, "Ooh is this a short cut to somewhere?");
							npcTalk(p, n, "Well it is easier to go this way",
								"Than through passes full of wolves");
						} else if (third == 1) {
							playerTalk(p, n, "Oh sorry I hadn't realised it was private");
						} else if (third == 2) {
							playerTalk(p, n, "If you were my friend I wouldn't mind");
							npcTalk(p, n, "Yes, but I don't even know you");
							//do not send over
							final int fourth = showMenu(p, n, false,
								"Well lets be friends",
								"You're a grumpy little man aren't you?");
							if (fourth == 0) {
								playerTalk(p, n, "Well lets be friends");
								npcTalk(p, n, "I don't make friends easily",
									"People need to earn my trust first");

								//do not send over
								final int fifth = showMenu(p, n, false,
									"And how am I meant to do that?",
									"You're a grumpy little man aren't you?");
								if (fifth == 0) {
									playerTalk(p, n, "And how am I meant to do that?");
									npcTalk(p,
										n,
										"My we are the persistant one aren't we",
										"Well theres a certain gold artifact we're after",
										"We dwarves are big fans of gold",
										"This artifact is the first prize at the hemenster fishing competition",
										"Fortunately we have acquired a pass to enter that competition",
										"Unfortunately Dwarves don't make good fishermen");
									//do not send over
									final int six = showMenu(p, n, false,
										"Fortunately I'm alright at fishing",
										"I'm not much of a fisherman either");
									if (six == 0) {
										playerTalk(p, n, "fortunately I'm alright at fishing");
										npcTalk(p,
											n,
											"Okay I entrust you with our competition pass",
											"go to Hemenster and do us proud");
										addItem(p, ItemId.FISHING_COMPETITION_PASS.id(), 1);
										p.updateQuestStage(getQuestId(), 1);
									} else if (six == 1) {
										playerTalk(p, n, "I'm not much of a fisherman either");
										npcTalk(p, n, "what good are you?");
									}
								} else if (fifth == 1) {
									playerTalk(p, n, "You're a grumpy little man aren't you");
									npcTalk(p, n, " Don't you know it");
								}

							} else if (fourth == 1) {
								playerTalk(p, n, "You're a grumpy little man aren't you");
								npcTalk(p, n, " Don't you know it");
							}
						}
					} else if (second == 2) {
						playerTalk(p, n, "I'm bigger than you", "Let me by");
						npcTalk(p, n, "Go away",
							"You're not going to bully your way in here");
					}
				} else if (first == 1) {
					playerTalk(p, n, "I was just stopping to say hello");
					npcTalk(p, n, "Hello then");
				}

				break;
			case 1:
			case 2:
				npcTalk(p, n, "Have you won yet?");
				if (!hasItemAtAll(p, ItemId.FISHING_COMPETITION_PASS.id())) {
					//do not send over
					final int opts = showMenu(p, n, false,
						"No I need another competition pass",
						"No it takes preparation to win fishing competitions");
					if (opts == 0) {
						playerTalk(p, n, "I need another competition pass");
						npcTalk(p, n, "Hmm its a good job they sent us spares",
							"there you go");
						addItem(p, ItemId.FISHING_COMPETITION_PASS.id(), 1);
					} else if (opts == 1) {
						playerTalk(p, n, "No it takes preparation to win fishing competitions");
						npcTalk(p, n, "Maybe that's where we are going wrong when we try fishing");
					}
				} else {
					playerTalk(p, n, "No not yet");
				}

				break;
			case 3:
				npcTalk(p, n, "Have you won yet?");
				playerTalk(p, n, "Yes I have");
				npcTalk(p, n, "Well done, so where is the trophy?");
				if (hasItem(p, ItemId.HEMENSTER_FISHING_TROPHY.id())) {
					playerTalk(p, n, "I have it right here");
					message(p, "you give the trophy to the dwarf");
					removeItem(p, ItemId.HEMENSTER_FISHING_TROPHY.id(), 1);
					npcTalk(p, n, "Okay we will let you in now");
					p.sendQuestComplete(Quests.FISHING_CONTEST);
				} else {
					playerTalk(p, n, "I don't have it with me");
				}
				break;
			case -1:
				npcTalk(p, n, "Welcome oh great fishing champion",
					"Feel free to pop by any time");
				break;
		}
	}

	@Override
	public void onInvUseOnObject(final GameObject obj, final Item item,
								 final Player player) {

		if (obj.getID() == 355 && item.getID() == ItemId.SPADE.id()) { // teleport coords:
			// 567, 451
			message(player, "you dig in amoungst the vines",
				"You find a red vine worm");
			addItem(player, ItemId.RED_VINE_WORMS.id(), 1);
		}
		else if (obj.getID() == 350 && item.getID() == ItemId.GARLIC.id()) {
			//stashing garlics in pipes should not check if other
			//garlics have been stashed
			if (!player.getCache().hasKey("paid_contest_fee")) {
				message(player, "You stash the garlic in the pipe");
				player.getInventory().remove(ItemId.GARLIC.id(), 1);
				if (!player.getCache().hasKey("garlic_activated")) {
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
			Npc bonzo = getNearestNpc(p, NpcId.BONZO.id(), 15);
			Npc morris = getNearestNpc(p, NpcId.MORRIS.id(), 15);
			if (p.getX() <= 564) {

				if (morris != null) {
					npcTalk(p, morris, "competition pass please");
					if (hasItem(p, ItemId.FISHING_COMPETITION_PASS.id())) {
						message(p, "You show Morris your pass");
						npcTalk(p, morris, "Move on through");
						doGate(p, obj, 357);
					} else {
						int m = showMenu(p, morris,
							"I don't have one of them",
							"What do I need that for?");
						if (m == 1) {
							npcTalk(p, morris,
								"This is the entrance to the Hementster fishing competition");
							npcTalk(p, morris, "It's a high class competition");
							npcTalk(p, morris, "Invitation only");
						}
					}
				} else
					System.err.println("morris is null");
			} else if (p.getX() >= 565) {
				if (p.getQuestStage(getQuestId()) == 3) {
					doGate(p, obj, 357);
					return;
				}
				if (bonzo != null && p.getCache().hasKey("paid_contest_fee")) {
					npcTalk(p, bonzo,
						"so you're calling it quits here for now?");
					int leaveMenu = showMenu(p, bonzo,
						"Yes I'll compete again another day",
						"Actually I'll go back and catch some more");
					if (leaveMenu == 0) {
						p.getCache().remove("paid_contest_fee");
						p.getCache().remove("contest_catches");
						doGate(p, obj, 357);
					} else if (leaveMenu == 1) {
						npcTalk(p, bonzo, "Good luck");
					}
				} else {
					doGate(p, obj, 357);
					return;
				}
			}
		}
		Npc sinister = getNearestNpc(p, NpcId.SINISTER_STRANGER.id(), 10);
		Npc bonzo = getNearestNpc(p, NpcId.BONZO.id(), 15);
		if (obj.getID() == 351) {
			if (hasItem(p, ItemId.HEMENSTER_FISHING_TROPHY.id())) {
				p.message("you have already won the fishing competition");
				return;
			} else if (bonzo != null && !p.getCache().hasKey("paid_contest_fee")) {
				bonzoDialogue(p, bonzo, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) > 0 && !p.getCache().hasKey("garlic_activated")) {
				//cases: not enough level
				//no bait
				//else do catch
				if (p.getSkills().getLevel(Skills.FISHING) < 10) {
					p.message("You need at least level 10 fishing to lure these fish");
				} else if (!hasItem(p, ItemId.FISHING_ROD.id())) {
					// probably non-kosher
					p.message("I don't have the equipment to catch a fish");
				} else if (!hasItem(p, ItemId.FISHING_BAIT.id()) && !hasItem(p, ItemId.RED_VINE_WORMS.id())) {
					p.message("you have no bait to catch fish here");
				}
				// fishing using worm gives raw sardine
				else if (hasItem(p, ItemId.RED_VINE_WORMS.id())) {
					p.message("You catch a sardine");
					p.getInventory().add(new Item(ItemId.RAW_SARDINE.id()));
					p.getInventory().remove(ItemId.RED_VINE_WORMS.id(), 1);
					addCatchCache(p, ItemId.RAW_SARDINE.id());
				} else if (hasItem(p, ItemId.FISHING_BAIT.id())) {
					p.message("You catch some shrimps");
					p.getInventory().add(new Item(ItemId.RAW_SHRIMP.id()));
					p.getInventory().remove(ItemId.FISHING_BAIT.id(), 1);
					addCatchCache(p, ItemId.RAW_SHRIMP.id());
				}

				if (p.getCache().hasKey("contest_catches")) {
					int numCatches = p.getCache().getString("contest_catches").split("-").length;
					if (numCatches > 2 && bonzo != null) {
						bonzoTimesUpDialogue(p, bonzo);
					}
				}
			} else {
				npcTalk(p, sinister, "I think you will find that is my spot");
			}
		}
		else if (obj.getID() == 352) {
			if (hasItem(p, ItemId.HEMENSTER_FISHING_TROPHY.id())) {
				p.message("you have already won the fishing competition");
				return;
			} else if (bonzo != null && !p.getCache().hasKey("paid_contest_fee")) {
				bonzoDialogue(p, bonzo, false);
				return;
			}
			if (p.getQuestStage(getQuestId()) > 0 && p.getCache().hasKey("garlic_activated")) {
				//cases: not enough level
				//no rod
				//no bait
				//else do catch
				if (p.getSkills().getLevel(Skills.FISHING) < 10) {
					p.message("You need at least level 10 fishing to lure these fish");
				} else if (!hasItem(p, ItemId.FISHING_ROD.id())) {
					// probably non-kosher
					p.message("I don't have the equipment to catch a fish");
				} else if (!hasItem(p, ItemId.FISHING_BAIT.id()) && !hasItem(p, ItemId.RED_VINE_WORMS.id())) {
					p.message("you have no bait to catch fish here");
				}
				// fishing using worm gives raw carp
				else if (hasItem(p, ItemId.RED_VINE_WORMS.id())) {
					p.message("You catch a giant carp");
					p.getInventory().add(new Item(ItemId.RAW_GIANT_CARP.id()));
					p.getInventory().remove(ItemId.RED_VINE_WORMS.id(), 1);
					addCatchCache(p, ItemId.RAW_GIANT_CARP.id());
				} else if (hasItem(p, ItemId.FISHING_BAIT.id())) {
					p.message("You catch a sardine");
					p.getInventory().add(new Item(ItemId.RAW_SARDINE.id()));
					p.getInventory().remove(ItemId.FISHING_BAIT.id(), 1);
					addCatchCache(p, ItemId.RAW_SARDINE.id());
				}

				if (p.getCache().hasKey("contest_catches")) {
					int numCatches = p.getCache().getString("contest_catches").split("-").length;
					if (numCatches > 2 && bonzo != null) {
						bonzoTimesUpDialogue(p, bonzo);
					}
				}
			} else {
				npcTalk(p, sinister, "I think you will find that is my spot");
				playerTalk(p, sinister, "Can't you go to another spot?");
				npcTalk(p, sinister, "I like this place",
					"I like to savour the aroma coming from these pipes");
			}
		}
		else if (obj.getID() == 353) {
			Npc dave = getNearestNpc(p, NpcId.BIG_DAVE.id(), 10);
			bigDaveDialogue(p, dave);
		}
		else if (obj.getID() == 354) {
			Npc joshua = getNearestNpc(p, NpcId.JOSHUA.id(), 10);
			joshuaDialogue(p, joshua);
		}
		else if (obj.getID() == 359) {
			if (p.getQuestStage(getQuestId()) == -1) {
				p.message("You go down the stairs");
				if (obj.getX() == 426 && obj.getY() == 458) {
					p.teleport(426, 3294, false);
				} else {
					p.teleport(385, 3301, false);
				}
			} else {
				// from player's position
				Npc dwarf = getNearestNpc(p, NpcId.MOUNTAIN_DWARF.id(), 25);
				//final Npc dwarf = World.getWorld().getNpc(355, 375, 395, 445,
				//		475);
				if (dwarf != null) {
					mountainDwarfDialogue(p, dwarf);
				}
			}
		}
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.MOUNTAIN_DWARF.id()) {
			mountainDwarfDialogue(p, n);
		}
		else if (n.getID() == NpcId.BONZO.id()) {
			bonzoDialogue(p, n, true);
		}
		else if (n.getID() == NpcId.SINISTER_STRANGER.id()) {
			sinisterDialogue(p, n, -1);
		}
		else if (n.getID() == NpcId.GRANDPA_JACK.id()) {
			grandpaJackDialogue(p, n);
		}
	}

	private void sinisterDialogue(final Player p, final Npc n, final int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case 1:
				case 2:
					npcTalk(p, n, "..");
					//do not send over
					final int first = showMenu(p, n, false, "..?",
						"Who are you?", "so you like fishing?");
					if (first == 0) {
						playerTalk(p, n, "..?");
						npcTalk(p, n, " ...");
					} else if (first == 1) {
						playerTalk(p, n, "Who are you?");
						npcTalk(p, n, "My name is Vlad",
							"I come from far avay, vere the sun is not so bright");
						final int second = showMenu(p, n,
							"You're a vampire aren't you?",
							"Is it nice there?");
						if (second == 0) {
							sinisterDialogue(p, n, SINISTER.VAMPIRE);
						} else if (second == 1) {
							npcTalk(p, n, "It is vonderful",
								"the vomen are beautiful",
								"and the nights are long");
							//do not send over
							final int third = showMenu(p, n, false,
								"You're a vampire aren't you?",
								"So you like fishing?",
								"Well good luck with the fishing");
							if (third == 0) {
								playerTalk(p, n, "You're a vampire aren't you?");
								sinisterDialogue(p, n, SINISTER.VAMPIRE);
							} else if (third == 1) {
								playerTalk(p, n, "So you like fishing");
								sinisterDialogue(p, n, SINISTER.FISHING);
							} else if (third == 2) {
								playerTalk(p, n, "Well good luck with the fishing");
								npcTalk(p, n, "Luck has nothing to do vith it",
									"It is all in the technique");
							}
						}

					} else if (first == 2) {
						playerTalk(p, n, "So you like fishing");
						sinisterDialogue(p, n, SINISTER.FISHING);
					}
					break;
			}
		}
		switch (cID) {
			case SINISTER.VAMPIRE:
				npcTalk(p, n, "Just because I can't stand the smell of garlic",
					"and I don't like bright sunlight",
					"Doesn't necessarily mean I'm a vampire");
				break;
			case SINISTER.FISHING:
				npcTalk(p, n, "My doctor told be to take up a velaxing hobby",
					"vhen I am stressed I tend to get a little..", "..thirsty");
				//do not send over
				final int third = showMenu(p, n, false,
					"You're a vampire aren't you?",
					"If you get thirsty you should drink something",
					"Well good look with the fishing");
				if (third == 0) {
					playerTalk(p, n, "You're a vampire aren't you?");
					sinisterDialogue(p, n, SINISTER.VAMPIRE);
				} else if (third == 1) {
					playerTalk(p, n, "If you get thirsty", "You should drink something");
					npcTalk(p, n, "I think I may do that soon");
				} else if (third == 2) {
					playerTalk(p, n, "Well good luck with the fishing");
					npcTalk(p, n, "Luck has nothing to do vith it",
						"It is all in the technique");
				}
				break;
		}
	}

	@Override
	public boolean blockInvUseOnNpc(Player p, Npc n, Item i) {
		//garlic on sinister stranger
		return n.getID() == NpcId.SINISTER_STRANGER.id() && i.getID() == ItemId.GARLIC.id();
	}

	@Override
	public void onInvUseOnNpc(Player p, Npc n, Item i) {
		if (n.getID() == NpcId.SINISTER_STRANGER.id() && i.getID() == ItemId.GARLIC.id()) {
			npcTalk(p, n, "urrggh get zat horrible ving avay from me",
				"How do people like to eat that stuff",
				"I can't stand even to be near it for ten seconds");
		}
	}

	class SINISTER {
		private static final int FISHING = 0;
		private static final int VAMPIRE = 1;
	}
}
