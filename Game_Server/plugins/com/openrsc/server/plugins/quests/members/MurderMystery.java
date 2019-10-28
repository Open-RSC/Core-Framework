package com.openrsc.server.plugins.quests.members;


import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

/**
 * @author Davve
 * MURDER MYSTERY 2014-01-11
 */

public class MurderMystery implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, PickupListener, PickupExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener, ObjectActionListener, ObjectActionExecutiveListener, InvUseOnItemListener, InvUseOnItemExecutiveListener {

	@Override
	public int getQuestId() {
		return Quests.MURDER_MYSTERY;
	}

	@Override
	public String getQuestName() {
		return "Murder Mystery (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("@gre@You haved gained 3 quest points!");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.MURDER_MYSTERY), true);
		p.message("You have completed the Murder Mystery Quest");
	}
	
	private static void sinclairSuspectDialogue(Player p, Npc n) {
		playerTalk(p, n, "I'm here to help the guards with their investigation");
		if (n.getID() == NpcId.CAROL_SINCLAIR.id()) {
			npcTalk(p, n, "Well, ask what you want to know then");
		} else if (n.getID() == NpcId.ELIZABETH_SINCLAIR.id()) {
			npcTalk(p, n, "What's so important you need to bother me with then?");
		} else if (n.getID() == NpcId.ANNA_SINCLAIR.id()) {
			npcTalk(p, n, "Oh really? what do you want to know then?");
		} else if (n.getID() == NpcId.FRANK_SINCLAIR.id()) {
			npcTalk(p, n, "Good for you. Now what do you want?",
				"And can you spare me any money? I'm a little short...");
		} else if (n.getID() == NpcId.BOB_SINCLAIR.id()) {
			npcTalk(p, n, "I suppose I had better talk to you then.");
		} else if (n.getID() == NpcId.DAVID_SINCLAIR.id()) {
			npcTalk(p, n, "And? Make this quick, I have better things to",
				"do than be interrogated by halfwits all day");
		}
		int menu;
		if (p.getCache().hasKey("poison_opt") && p.getCache().hasKey("thread")) {
			menu = showMenu(p, n, "Who do you think was responsible?", "Where were you when the murder happened?", "Do you recognise this thread?", "Why did you buy poison the other day?");
		} else if (p.getCache().hasKey("poison_opt") && !p.getCache().hasKey("thread")) {
			menu = showMenu(p, n, "Who do you think was responsible?", "Where were you when the murder happened?", "Why did you buy poison the other day?");
		} else if (p.getCache().hasKey("thread")) {
			menu = showMenu(p, n, "Who do you think was responsible?", "Where were you when the murder happened?", "Do you recognise this thread?");
		} else {
			menu = showMenu(p, n, "Who do you think was responsible?", "Where were you when the murder happened?");
		}


		if (menu == 0) {
			if (n.getID() == NpcId.CAROL_SINCLAIR.id()) {
				npcTalk(p, n, "I don't know. I think its very convenient",
					"that you have arrived here so soon after it happened.",
					"Maybe it was you");
			} else if (n.getID() == NpcId.ELIZABETH_SINCLAIR.id()) {
				npcTalk(p, n, "Could have been anyone. The old man was an",
					"idiot. Hes been asking for it for years.");
			} else if (n.getID() == NpcId.ANNA_SINCLAIR.id()) {
				npcTalk(p, n, "It was clearly an intruder.");
				playerTalk(p, n, "Well, I don't think it was");
				npcTalk(p, n, "It was one of our lazy servants then");
			} else if (n.getID() == NpcId.FRANK_SINCLAIR.id()) {
				npcTalk(p, n, "I don't know.",
					"You don't know how long it takes an inheritance",
					"to come through do you? I could really use that",
					"money pretty soon...");
			} else if (n.getID() == NpcId.BOB_SINCLAIR.id()) {
				npcTalk(p, n, "I don't really care as long as noone thinks its me",
					"Maybe that strange poison seller who headed towards the seers village.");
			} else if (n.getID() == NpcId.DAVID_SINCLAIR.id()) {
				npcTalk(p, n, "I don't really know or care",
					"Frankly, the old man deserved to die",
					"There was a suspicious red headed man who came",
					"to the house the other day selling poison now I",
					"think about it. Last I saw he was headed towards",
					"the tavern in the Seers village.");
			}
		} else if (menu == 1) {
			if (n.getID() == NpcId.CAROL_SINCLAIR.id()) {
				npcTalk(p, n, "Why? Are you accusing me of something?",
					"You seem to have a very high opinion of yourself",
					"I was in my room if you must know, alone.");
			} else if (n.getID() == NpcId.ELIZABETH_SINCLAIR.id()) {
				npcTalk(p, n, "I was out");
				playerTalk(p, n, "Care to be any more specific?");
				npcTalk(p, n, "not really. I don't have to justify myself to the likes of you.",
					"I know the king personally you know. Now are we finished here?");
			} else if (n.getID() == NpcId.ANNA_SINCLAIR.id()) {
				npcTalk(p, n, "in the library. Noone else was there so",
					"you'll just have to take my word for it");
			} else if (n.getID() == NpcId.FRANK_SINCLAIR.id()) {
				npcTalk(p, n, "I don't know, somewhere around here probably.",
					"Could you spare me a few coins?",
					"I'll be able to pay you double tomorrow",
					"its just theres this poker night tonight in town...");
			} else if (n.getID() == NpcId.BOB_SINCLAIR.id()) {
				npcTalk(p, n, "I was walking by myself in the garden.");
				playerTalk(p, n, "And can anyone vouch for that?");
				npcTalk(p, n, "No. But I was.");
			} else if (n.getID() == NpcId.DAVID_SINCLAIR.id()) {
				npcTalk(p, n, "that is none of your business.",
					"Are we finished now, or are you just going",
					"to stand there irritating me with your",
					"idiotic questions all day?");
			}
		} else if (menu == 2 && p.getCache().hasKey("thread")) {
			if (n.getID() == NpcId.CAROL_SINCLAIR.id() && !p.getInventory().hasItemId(ItemId.THREAD_RED.id())) {
				p.message("you show Carol the thread found at the crime scene");
				npcTalk(p, n, "Its some thread. Sorry, do you have a point here?",
					"Or do you just enjoy wasting peoples time?");
			} else if (n.getID() == NpcId.CAROL_SINCLAIR.id() && p.getInventory().hasItemId(ItemId.THREAD_RED.id())) {
				p.message("You show her the thread from the study window");
				npcTalk(p, n, "Its some red thread... it kind of looks like the",
					"Same material as my trousers. But obviously its not.");

			} else if (n.getID() == NpcId.ELIZABETH_SINCLAIR.id() && !p.getInventory().hasItemId(ItemId.THREAD_BLUE.id())) {
				p.message("You show her the thread from the study window");
				npcTalk(p, n, "Its some thread. You're not very good",
					"at this whole investigation thing are you?");
			} else if (n.getID() == NpcId.ELIZABETH_SINCLAIR.id() && p.getInventory().hasItemId(ItemId.THREAD_BLUE.id())) {
				p.message("You show her the thread from the study window");
				npcTalk(p, n, "Looks like a Blue thread to me.",
					" If you can't work that out for yourself I",
					"don't hold much hope of you solving this crime.");
				playerTalk(p, n, "It looks a lot like the material your trousers",
					"are made of doesn't it?");
				npcTalk(p, n, "I suppose it does. So what?");

			} else if (n.getID() == NpcId.ANNA_SINCLAIR.id() && !p.getInventory().hasItemId(ItemId.THREAD_GREEN.id())) {
				p.message("You show Anna the thread from the study");
				npcTalk(p, n, "Not really, no. Thread is fairly common");
			} else if (n.getID() == NpcId.ANNA_SINCLAIR.id() && p.getInventory().hasItemId(ItemId.THREAD_GREEN.id())) {
				p.message("You show Anna the thread from the study");
				npcTalk(p, n, "Its some Green thread. Its not exactly uncommon is it?",
					"My trousers are made of the same material");

			} else if (n.getID() == NpcId.FRANK_SINCLAIR.id() && !p.getInventory().hasItemId(ItemId.THREAD_BLUE.id())) {
				p.message("Frank examines the thread from the crime scene");
				npcTalk(p, n, "It looks like thread to me, but I'm not exactly",
					"an expert. Is it worth something?",
					"Can I have it? Actually, can you spare me a few gold?");
			} else if (n.getID() == NpcId.FRANK_SINCLAIR.id() && p.getInventory().hasItemId(ItemId.THREAD_BLUE.id())) {
				p.message("Frank examines the thread from the crime scene");
				npcTalk(p, n, "it kind of looks like the same material as",
					"my trousers are made of... same colour anyway",
					"think its worth anything? Can I have it? Or just some money?");

			} else if (n.getID() == NpcId.BOB_SINCLAIR.id() && !p.getInventory().hasItemId(ItemId.THREAD_RED.id())) {
				p.message("you show him the thread you discovered");
				npcTalk(p, n, "Its some thread. great clue. No, really.");
			} else if (n.getID() == NpcId.BOB_SINCLAIR.id() && p.getInventory().hasItemId(ItemId.THREAD_RED.id())) {
				p.message("you show him the thread you discovered");
				npcTalk(p, n, "Its some red thread. I suppose you think",
					"thats some kind of clue? It looks like",
					"the material my trousers are made of");

			} else if (n.getID() == NpcId.DAVID_SINCLAIR.id() && !p.getInventory().hasItemId(ItemId.THREAD_GREEN.id())) {
				p.message("You show him the thread you found on the study window");
				npcTalk(p, n, "No. Can I go yet? your face irritates me.");
			} else if (n.getID() == NpcId.DAVID_SINCLAIR.id() && p.getInventory().hasItemId(ItemId.THREAD_GREEN.id())) {
				p.message("You show him the thread you found on the study window");
				npcTalk(p, n, "Its some Green thread, like my trousers are made of.",
					"Are you finished? I'm not sure which I dislike more",
					"about you, your face or your general bad odour");
			}
		} else if (menu == 3 || menu == 2 && !p.getCache().hasKey("thread")) {
			if (n.getID() == NpcId.CAROL_SINCLAIR.id()) {
				npcTalk(p, n, "I don't see what on earth it has to",
					"do with you, but the drain outside was",
					"blocked, and as nobody else here has the",
					"intelligence to even unblock a simple drain",
					"I felt I had to do it myself");
				if (p.getCache().hasKey("murder_carol")) {
					p.getCache().store("p_carol", true);
					p.getCache().store("poison_opt2", true);
				}

			} else if (n.getID() == NpcId.ELIZABETH_SINCLAIR.id()) {
				npcTalk(p, n, "there was a nest of mosquitos under the fountain",
					"in the garden, which I killed with poison the other day.",
					"You can see for yourself if you're capable",
					"of managing that, which I somehow doubt");
				playerTalk(p, n, "I hate mosquitos");
				npcTalk(p, n, "Doesn't everyone?");
				if (p.getCache().hasKey("murder_eliz")) {
					p.getCache().store("p_eliza", true);
					p.getCache().store("poison_opt2", true);
				}
			} else if (n.getID() == NpcId.ANNA_SINCLAIR.id()) {
				npcTalk(p, n, "That useless Gardener Stanford has let his",
					"Compost heap fester. Its an eyesore to the garden",
					"So I bought some poison from a travelling salesman",
					"So that I could kill off some of the wildlife living in it");
				if (p.getCache().hasKey("murder_anna")) {
					p.getCache().store("p_anna", true);
					p.getCache().store("poison_opt2", true);
				}
			} else if (n.getID() == NpcId.FRANK_SINCLAIR.id()) {
				npcTalk(p, n, "Would you like to buy some? I'm kind of strapped",
					"for cash right now, I'll sell it to you cheap, its hardly",
					"been used at all, I just used a bit to clean that family",
					"crest outside up a bit. Do you think I can get much money",
					"For the family crest, actually? Its cleaned up a bit now");
				if (p.getCache().hasKey("murder_frank")) {
					p.getCache().store("p_frank", true);
					p.getCache().store("poison_opt2", true);
				}
			} else if (n.getID() == NpcId.BOB_SINCLAIR.id()) {
				npcTalk(p, n, "what's it to you anyway?",
					"If you absolutely must know, we had a problem",
					"with the beehive in the garden, and as all of our",
					"servants are so pathetically useless, I decided",
					"I would deal with it myself. So I did.");
				if (p.getCache().hasKey("murder_bob")) {
					p.getCache().store("p_bob", true);
					p.getCache().store("poison_opt2", true);
				}
			} else if (n.getID() == NpcId.DAVID_SINCLAIR.id()) {
				npcTalk(p, n, "There was a nest of spiders upstairs between the",
					"Two Servants quarters. Obviously I had to kill them before",
					"our pathetic servants whined at my father some more",
					"Honestly, its like they expect to be treated like royalty",
					"If I had my way I would fire the whole workshy lot of them");
				if (p.getCache().hasKey("murder_david")) {
					p.getCache().store("p_david", true);
					p.getCache().store("poison_opt2", true);
				}
			}
		}
	}

	private void whoYouSuspect(Player p, Npc n) {
		p.message("You tell the guard who you suspect of the crime");
		npcTalk(p, n, "Great work, show me the evidence",
			"and we'll take them to the dungeons",
			"you *DO* have evidence of their crime, right?");
		playerTalk(p, n, "uh....");
		npcTalk(p, n, "tch. You wouldn't last a day in the guards",
			"with sloppy thinking like that.",
			"come see me when you have some proof of your accusations");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.GUARD_SINCLAIR_MANSION.id(), NpcId.POISON_SALESMAN.id(),
				NpcId.DAVID_SINCLAIR.id(), NpcId.ANNA_SINCLAIR.id(), NpcId.FRANK_SINCLAIR.id(),
				NpcId.BOB_SINCLAIR.id(), NpcId.ELIZABETH_SINCLAIR.id(), NpcId.CAROL_SINCLAIR.id(),
				NpcId.HOBBES_THE_BUTLER.id(), NpcId.LOUISA_THE_COOK.id(), NpcId.STANFORD_THE_GARDENER.id(),
				NpcId.PIERRE_THE_DOG_HANDLER.id(), NpcId.DONOVAN_THE_HANDYMAN.id(), NpcId.MARY_THE_MAID.id(),
				NpcId.MAN_SINCLAIR_MANSION.id()}, n.getID());
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		/** Quest starter **/
		if (n.getID() == NpcId.GUARD_SINCLAIR_MANSION.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "What's going on here?");
					npcTalk(p, n, "Oh, its terrible.",
						"Lord Sinclair has been murdered",
						"And we don't have any clues as to",
						"who or why. We're totally baffled",
						"If you can help us",
						"we will be very grateful");
					int menu = showMenu(p, n,
						"Sure, I'll help",
						"You should do your own dirty work");
					if (menu == 0) {
						npcTalk(p, n, "thanks a lot!");
						playerTalk(p, n, "What should I be doing to help?");
						npcTalk(p, n, "Look around and investigate who might be responsible",
							"the sarge said every murder leaves clues to who done it",
							"but frankly we're out of our depth here");
						p.updateQuestStage(this, 1); // QUEST STARTED: PERMISSION TO INVESTIGATE.
						int random_murder = DataConversions.random(1, 6);
						if (random_murder == 1) {
							p.getCache().store("murder_david", true);
						} else if (random_murder == 2) {
							p.getCache().store("murder_anna", true);
						} else if (random_murder == 3) {
							p.getCache().store("murder_carol", true);
						} else if (random_murder == 4) {
							p.getCache().store("murder_bob", true);
						} else if (random_murder == 5) {
							p.getCache().store("murder_frank", true);
						} else if (random_murder == 6) {
							p.getCache().store("murder_eliz", true);
						}
					} else if (menu == 1) {
						npcTalk(p, n, "get lost then, this is private property.",
							"...unless you'd like to be taken for questioning yourself");
					}

					break;
				case 1:
					int opt = showMenu(p, n,
						"What should I be doing to help again?",
						"How did Lord Sinclair die?",
						"I know who did it!");

					if (opt == 0) {
						npcTalk(p, n, "Look around and investigate who might be responsible",
							"the sarge said every murder leaves clues to who done it",
							"but frankly we're out of our depth here");
					} else if (opt == 1) {
						npcTalk(p, n, "well its all very mysterious.",
							"Mary the maid found the body in the study next to his bedroom",
							"on the east wing of the ground floor, the door was found locked,",
							"from the inside, and he seemed to have been stabbed",
							"but there was an odd smell in the room. Frankly, I'm stumped");
					} else if (opt == 2) {
						/*
						 * Cases
						 * 3 pieces - completes quest
						 * 2 pieces - menu dialogue
						 * 1 piece - jump straight away
						 * 0 - guesses
						 */
						//0 pieces
						if (!p.getCache().hasKey("thread") && !p.getCache().hasKey("evidence") && !p.getCache().hasKey("culprit")) {
							npcTalk(p, n, "Really? That was quick work! Who?");
							int variableD = showMenu(p, n, "It was an intruder!", "the butler did it!", "It was one of the servants", "It was one of his family");
							if (variableD == 0) {
								npcTalk(p, n, "Thats what we were thinking too.",
									"That someone broke in, to steal something",
									"was discovered by Lord Sinclair, stabbed him and ran.",
									"Its odd that apparently nothing was stolen though.",
									"Find out something has been stolen, and the case is closed",
									"But the murdered man was a friend of the king",
									"and its more than my jobs worth not to investigate fully");
							} else if (variableD == 1) {
								npcTalk(p, n, "I hope you have proof to that effect.",
									"we have to arrest someone for this and it seems to me that",
									"only the actual murderer would gain by falsely accusing someone");
								//kosher: small pause to continue dialogue
								sleep(1500);
								npcTalk(p, n, "although having said that",
									"the butler is kind of shifty looking...");
							} else if (variableD == 2) {
								npcTalk(p, n, "Oh really? Which one?");
								int variableA = showMenu(p, n,
									"It was one of the women",
									"It was one of the men");
								if (variableA == 0) {
									npcTalk(p, n, "Oh really? Which one?");
									//do not send over
									int variableB = showMenu(p, n, false,
										"it was so obviously Louisa The Cook",
										"It must have been Mary The Maid");
									if (variableB >= 0) {
										whoYouSuspect(p, n);
									}
								} else if (variableA == 1) {
									npcTalk(p, n, "Oh really? Which one?");
									//do not send over
									int variableC = showMenu(p, n, false,
										"it can only be Donovan the Handyman",
										"Pierre the Dog Handler. No question.",
										"Hobbes the Butler. the butler *always* did it",
										"you must know it was Stanford The Gardener");
									if (variableC >= 0 && variableC != 2) {
										whoYouSuspect(p, n);
									}
									//butler has the same dialogue
									else if (variableC == 2) {
										playerTalk(p, n, "the butler did it!");
										npcTalk(p, n, "I hope you have proof to that effect.",
											"we have to arrest someone for this and it seems to me that",
											"only the actual murderer would gain by falsely accusing someone");
										//kosher: small pause to continue dialogue
										sleep(1500);
										npcTalk(p, n, "although having said that",
											"the butler is kind of shifty looking...");
									}
								}
							} else if (variableD == 3) {
								npcTalk(p, n, "Oh really? Which one?");
								int family = showMenu(p, n,
									"It was one of the women",
									"It was one of the men");
								if (family == 0) {
									npcTalk(p, n, "Oh really? Which one?");
									//do not send over
									int variableI = showMenu(p, n, false,
										"I know it was Anna",
										"I am so sure it was Carol",
										"Ill bet you anything it was Elizabeth");
									if (variableI >= 0) {
										whoYouSuspect(p, n);
									}
								} else if (family == 1) {
									npcTalk(p, n, "Oh really? Which one?");
									//do not send over
									int variableE = showMenu(p, n, false,
										"I'm certain it was Bob",
										"It was David. No doubt about it.",
										"If it wasn't Frank I'll eat my shoes");
									if (variableE >= 0) {
										whoYouSuspect(p, n);
									}
								}
							}
						}
						//3 pieces
						else if (p.getCache().hasKey("thread") && p.getCache().hasKey("evidence") && p.getCache().hasKey("culprit")) {
							p.getCache().remove("poison_opt");
							playerTalk(p, n, "I have conclusive Proof who the killer was");
							npcTalk(p, n, "You do? thats excellent work. Lets hear it then");
							playerTalk(p, n, "I don't think it was an intruder, and I don't think Lord",
								"Sinclair was killed by being stabbed.");
							npcTalk(p, n, "hmmm? really? why not?");
							playerTalk(p, n, "nobody heard the guard dog barking, which it would have if",
								"it had been an intruder who was responsible.",
								"nobody heard any signs of a struggle either.",
								"I think the knife was there to throw suspicion away from the real culprit.");
							npcTalk(p, n, "Yes, that makes sense. But who did do it then?");
							if (p.getCache().hasKey("murder_david")) {
								p.message("You prove to the guard the thread matches Davids clothes");
							} else if (p.getCache().hasKey("murder_anna")) {
								p.message("You prove to the guard the thread matches Annas clothes");
							} else if (p.getCache().hasKey("murder_carol")) {
								p.message("You prove to the guard the thread matches Carols clothes");
							} else if (p.getCache().hasKey("murder_bob")) {
								p.message("You prove to the guard the thread matches Bobs clothes");
							} else if (p.getCache().hasKey("murder_frank")) {
								p.message("You prove to the guard the thread matches Franks clothes");
							} else if (p.getCache().hasKey("murder_eliz")) {
								p.message("You prove to the guard the thread matches Elizabeths clothes");
							}
							npcTalk(p, n, "Yes, I'd have to agree with that... but we need more evidence");
							if (p.getCache().hasKey("murder_david")) {
								p.message("You prove to the guard David did not use poison on the spiders nest");
							} else if (p.getCache().hasKey("murder_anna")) {
								p.message("You prove to the guard Anna did not use poison on the compost heap");
							} else if (p.getCache().hasKey("murder_carol")) {
								p.message("You prove to the guard Carol did not use poison on the drain");
							} else if (p.getCache().hasKey("murder_bob")) {
								p.message("You prove to the guard Bob did not use poison on the beehive");
							} else if (p.getCache().hasKey("murder_frank")) {
								p.message("You prove to the guard Frank did not use poison on the Sinclair Crest");
							} else if (p.getCache().hasKey("murder_eliz")) {
								p.message("You prove to the guard Elizabeth did not use poison on the fountain");
							}
							npcTalk(p, n, "Excellent work - have you considered a career as a detective?",
								"But i'm afraid its still not quite enough...");
							if (p.getCache().hasKey("murder_david")) {
								p.message("You match Davids fingerprints with those on the dagger");
							} else if (p.getCache().hasKey("murder_anna")) {
								p.message("You match Annas fingerprints with those on the dagger");
							} else if (p.getCache().hasKey("murder_carol")) {
								p.message("You match Carols fingerprints with those on the dagger");
							} else if (p.getCache().hasKey("murder_bob")) {
								p.message("You match Bobs fingerprints with those on the dagger");
							} else if (p.getCache().hasKey("murder_frank")) {
								p.message("You match Franks fingerprints with those on the dagger");
							} else if (p.getCache().hasKey("murder_eliz")) {
								p.message("You match Elizabeths fingerprints with those on the dagger");
							}
							p.message("Found in the body of Lord Sinclair");
							npcTalk(p, n, "...",
								"Yes. theres no doubt about it.");
							String objPronoun = "";
							if (p.getCache().hasKey("murder_david") && p.getCache().hasKey("p_david2")) {
								npcTalk(p, n, "It must have been David who killed his father");
								p.getCache().remove("murder_david");
								objPronoun = "him";
							} else if (p.getCache().hasKey("murder_anna") && p.getCache().hasKey("p_anna2")) {
								npcTalk(p, n, "It must have been Anna who killed her father");
								p.getCache().remove("murder_anna");
								objPronoun = "her";
							} else if (p.getCache().hasKey("murder_carol") && p.getCache().hasKey("p_carol2")) {
								npcTalk(p, n, "It must have been Carol who killed her father");
								p.getCache().remove("murder_carol");
								objPronoun = "her";
							} else if (p.getCache().hasKey("murder_bob") && p.getCache().hasKey("p_bob2")) {
								npcTalk(p, n, "It must have been Bob who killed his father");
								p.getCache().remove("murder_bob");
								objPronoun = "him";
							} else if (p.getCache().hasKey("murder_frank") && p.getCache().hasKey("p_frank2")) {
								npcTalk(p, n, "It must have been Frank who killed his father");
								p.getCache().remove("murder_frank");
								objPronoun = "him";
							} else if (p.getCache().hasKey("murder_eliz") && p.getCache().hasKey("p_eliza2")) {
								npcTalk(p, n, "It must have been Elizabeth who killed her father");
								p.getCache().remove("murder_eliz");
								objPronoun = "her";
							}
							npcTalk(p, n, "All of the guards must congratulate you on your",
								"Excellent work in helping us to solve this case",
								"We don't have many murders here in RuneScape",
								"And i'm afraid we wouldn't have been able to solve it",
								"by ourselves. We will hold " + objPronoun + " here under house arrest",
								"Until such time as we can bring " + objPronoun + " to trial",
								"You have our gratitude, and I'm sure the rest of the",
								"families as well, in helping to apprehend the murderer",
								"I'll just take the evidence from you now");
							p.message("You hand over all the evidence");
							//remove murder mystery related items:
							int itemIds[] = {ItemId.THREAD_GREEN.id(), ItemId.THREAD_BLUE.id(), ItemId.THREAD_RED.id(),
								//threads + fingerprints + scene items
								ItemId.MURDER_SCENE_POT.id(), ItemId.A_SILVER_DAGGER.id(), ItemId.MURDERERS_FINGERPRINT.id(),
								ItemId.ANNAS_FINGERPRINT.id(), ItemId.BOBS_FINGERPRINT.id(), ItemId.CAROLS_FINGERPRINT.id(),
								ItemId.DAVIDS_FINGERPRINT.id(), ItemId.ELIZABETHS_FINGERPRINT.id(), ItemId.FRANKS_FINGERPRINT.id(), ItemId.UNIDENTIFIED_FINGERPRINT.id(),
								//original family items
								ItemId.ANNAS_SILVER_NECKLACE.id(), ItemId.BOBS_SILVER_TEACUP.id(), ItemId.CAROLS_SILVER_BOTTLE.id(),
								ItemId.DAVIDS_SILVER_BOOK.id(), ItemId.ELIZABETHS_SILVER_NEEDLE.id(), ItemId.FRANKS_SILVER_POT.id(),
								//coated with flour
								ItemId.ANNAS_SILVER_NECKLACE_FLOUR.id(), ItemId.BOBS_SILVER_TEACUP_FLOUR.id(), ItemId.CAROLS_SILVER_BOTTLE_FLOUR.id(),
								ItemId.DAVIDS_SILVER_BOOK_FLOUR.id(), ItemId.ELIZABETHS_SILVER_NEEDLE_FLOUR.id(), ItemId.FRANKS_SILVER_POT_FLOUR.id()};
							int amt;
							//removes all
							for (int itemId : itemIds) {
								amt = Math.max(p.getInventory().countId(itemId), 0);
								p.getInventory().remove(itemId, amt);
							}
							p.sendQuestComplete(Quests.MURDER_MYSTERY);
							npcTalk(p, n, "Please accept this reward from the family!");
							p.message("You received 2000 gold!");
							addItem(p, ItemId.COINS.id(), 2000);
							p.getCache().remove("evidence", "culprit", "p_anna", "p_bob", "p_carol", "p_eliza", "p_david", "p_frank");
							p.getCache().remove("p_anna2", "p_bob2", "p_carol2", "p_eliza2", "p_david2", "p_frank2");
							p.getCache().remove("murder_anna", "murder_bob", "murder_frank", "murder_eliz", "murder_david");
							p.getCache().remove("thread", "poison_opt", "poison_opt2");
						}
						//1 piece
						else if ((p.getCache().hasKey("thread") ^ p.getCache().hasKey("evidence") ^ p.getCache().hasKey("culprit"))
							^ (p.getCache().hasKey("thread") && p.getCache().hasKey("evidence") && p.getCache().hasKey("culprit"))) {
							if (p.getCache().hasKey("thread")) {
								threadDialogue(p, n);
								return;
							} else if (p.getCache().hasKey("evidence")) {
								evidenceDialogue(p, n);
								return;
							} else if (p.getCache().hasKey("culprit")) {
								fingerprintDialogue(p, n);
								return;
							}
						}
						//2 pieces
						else {
							if (p.getCache().hasKey("thread") && p.getCache().hasKey("evidence")) {
								//do not send over
								int subopt = showMenu(p, n, false,
									"I have proof that it wasn't any of the servants",
									"I have proof one of the family lied about the poison");
								if (subopt == 0) {
									threadDialogue(p, n);
									return;
								} else if (subopt == 1) {
									evidenceDialogue(p, n);
									return;
								}
							} else if (p.getCache().hasKey("thread") && p.getCache().hasKey("culprit")) {
								int subopt = showMenu(p, n, false,
									"I have proof that it wasn't any of the servants",
									"I have the fingerprints of the culprit");
								if (subopt == 0) {
									threadDialogue(p, n);
									return;
								} else if (subopt == 1) {
									fingerprintDialogue(p, n);
									return;
								}
							} else if (p.getCache().hasKey("evidence") && p.getCache().hasKey("culprit")) {
								int subopt = showMenu(p, n, false,
									"I have proof one of the family lied about the poison",
									"I have the fingerprints of the culprit");
								if (subopt == 0) {
									evidenceDialogue(p, n);
									return;
								} else if (subopt == 1) {
									fingerprintDialogue(p, n);
									return;
								}
							}
						}
					}

					break;
				case -1:
					npcTalk(p, n, "Excellent work on solving the murder",
						"All of the guards I know are very impressed",
						"And don't worry, we have the murderer under guard",
						"until they can be taken to trial");
					break;
			}
		}
		/** START SINCLAIRS **/
		else if (n.getID() == NpcId.BOB_SINCLAIR.id() || n.getID() == NpcId.FRANK_SINCLAIR.id() || n.getID() == NpcId.DAVID_SINCLAIR.id()) {
			if (p.getQuestStage(this) == 0) {
				p.message("he is ignoring you");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Apparently you aren't as stupid as you look");
			} else {
				sinclairSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.ANNA_SINCLAIR.id() || n.getID() == NpcId.ELIZABETH_SINCLAIR.id() || n.getID() == NpcId.CAROL_SINCLAIR.id()) {
			if (p.getQuestStage(this) == 0) {
				p.message("she is ignoring you");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Apparently you aren't as stupid as you look");
			} else {
				sinclairSuspectDialogue(p, n);
			}
		}
		/** START OTHER NPCS **/
		else if (n.getID() == NpcId.HOBBES_THE_BUTLER.id()) {
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "This is private property! Please leave!");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Thank you for all your help in solving the murder");
			} else {
				otherSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.LOUISA_THE_COOK.id()) {
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "I'm far too upset to talk to random people right now");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Thank you for all your help in solving the murder");
			} else {
				otherSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.STANFORD_THE_GARDENER.id()) {
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "Have you no shame? we are all grieving at the moment");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Thank you for all your help in solving the murder");
			} else {
				otherSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.PIERRE_THE_DOG_HANDLER.id()) {
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "The Guards told me not to talk to anyone");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Thank you for all your help in solving the murder");
			} else {
				otherSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.MAN_SINCLAIR_MANSION.id()) {
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "Theres some kind of commotion up at the Sinclair place",
					"I hear. Not surprising all things considered");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "I heard you solved the murder",
					"Was I of any help to you at all?");
			} else {
				playerTalk(p, n, "I'm investigating the murder up at the Sinclair place");
				npcTalk(p, n, "Murder is it?",
					"Well, i'm not really surprised...");
				int menu = showMenu(p, n,
					"What can you tell me about the Sinclairs?",
					"Who do you think was responsible?",
					"Why do the Sinclairs live so far from town?",
					"I think the butler did it",
					"I am so confused about who did it");
				if (menu == 0) {
					npcTalk(p, n, "Well, what do you want to know?");
					int menu2 = showMenu(p, n,
						"Tell me about Lord Sinclair",
						"what can you tell me about his sons?",
						"what can you tell me about his daughters?");
					if (menu2 == 0) {
						npcTalk(p, n, "Old Lord Sinclair was a great man with a lot of",
							"respect in these parts. More than his worthless",
							"children have anyway");
						playerTalk(p, n, "His children? They have something to gain by his death?");
						npcTalk(p, n, "yes. you could say that. not that im one to gossip");
					} else if (menu2 == 1) {
						npcTalk(p, n, "His sons eh? They all have their own skeletons",
							"In their cupboards. You'll have to be more specific.",
							"Who are you interested in exactly?");
						int menu3 = showMenu(p, n,
							"Tell me about Bob",
							"Tell me about David",
							"Tell me about Frank");
						if (menu3 == 0) {
							npcTalk(p, n, "Bob is an odd character indeed...",
								"I'm not one to gossip, but I heard",
								"Bob is addicted to Tea. He can't make it through the day",
								"Without having at least 20 cups!",
								"You might not think thats such a big thing,",
								"But he has spent thousands of gold to feed his habit",
								"At one point he stole a lot of silverware from the kitchen",
								"and pawned it just so he could afford to buy his daily",
								"tea allowance. If his father ever found out, he would",
								"be in so much trouble... he might even get disowned");
						} else if (menu3 == 1) {
							npcTalk(p, n, "David... oh david...",
								"not many people know this, but David really",
								"has an anger problem. Hes always screaming and shouting",
								"at the household servants when hes angry, and they live",
								"in a state of fear, always walking on eggshells around him",
								"but none of them have the courage to talk to his father about",
								"his behaviour. If they did Lord Sinclair would almost certainly",
								"kick him out of the house, as some of the servants have",
								"been there longer than he has, and he definitely",
								"has no right to treat them like he does... but",
								"I'm not one to gossip about people.");
						} else if (menu3 == 2) {
							npcTalk(p, n, "I'm not one to talk ill of people behind their back",
								"but frank is a real piece of work. He is an absolutely",
								"terrible gambler... he can't pass 2 dogs in the street",
								"without putting a bet on which one will bark first",
								"He has already squandered all of his allowance, and I heard",
								"he had stolen a number of paintings of his Fathers to sell",
								"to try and cover his debts, but he still owes a lot of",
								"people a lot of money. If his Father ever found out, he would",
								"stop his income, and then he would be in serious trouble");
						}
					} else if (menu2 == 2) {
						npcTalk(p, n, "His daughters eh? They're all nasty pieces of work",
							"which of them specifically did you want to know about?");
						int menu4 = showMenu(p, n,
							"Tell me about Anna",
							"Tell me about Carol",
							"Tell me about Elizabeth");
						if (menu4 == 0) {
							npcTalk(p, n, "Anna... ah yes...",
								"Anna has 2 great loves:",
								"Sewing and Gardening. But one thing",
								"she has kept secret is that she once had",
								"an affair with Stanford the gardener",
								"and tried to get him fired when they broke up",
								"by killing all of the flowers in the garden",
								"If her father ever found out she had done that",
								"He would be so furious he would probably disown her");
						} else if (menu4 == 1) {
							npcTalk(p, n, "Oh Carol... she is such a fool",
								"You didn't hear this from me, but I heard",
								"a while ago she was conned out of a lot of money",
								"by a travelling salesman who sold her a box full",
								"of beans by telling her they were magic. But they weren't.",
								"She sold some rare books from the library to cover her debts",
								"But her father would be incredibly annoyed",
								"If he ever found out - he might even throw her out of the house");
						} else if (menu4 == 2) {
							npcTalk(p, n, "Elizabeth? Elizabeth has a strange problem",
								"She cannot help herself, but is always stealing small",
								"objects - its pretty sad that she is rich enough to afford",
								"to buy things, but would rather steal them instead.",
								"Now, I don't want to spread stories, but I heard",
								"She even stole a silver needle from her father that",
								"had great sentimental value for him. He was devestated when",
								"it was lost, and cried for a week thinking he had lost it",
								"If he ever found out that it was her who had stolen it",
								"He would go absolutely mental, maybe even disowning her");
						}
					}
				} else if (menu == 1) {
					npcTalk(p, n, "well, I guess it could have been an intruder",
						"but with that big guard dog of theirs",
						"I seriously doubt it.",
						"I suspect it was someone closer to home...",
						"Especially as I heard that that poison salesman",
						"in the seers village made a big sale to one",
						"of the family the other day.");
				} else if (menu == 2) {
					npcTalk(p, n, "Well, they used to live in the big castle",
						"but old Lord Sinclair gave it up so that those",
						"strange knights could live there instead",
						"So the king built him a new house to the North",
						"Its more cramped than his old place, but he seemed to like it",
						"his children were furious at him for doing it though");
				} else if (menu == 3) {
					npcTalk(p, n, "And I think you've been reading too many",
						"cheap detective novels",
						"Hobbes is kind of uptight, but his loyalty",
						"to Old Lord Sinclair is beyond question");
				} else if (menu == 4) {
					playerTalk(p, n, "think you could give me any hints?");
					int hint = DataConversions.random(0, 4);
					switch (hint) {
						case 0:
							npcTalk(p, n, "well, I dont know if its related",
								"But I heard from that Poison Salesman in town",
								"That he sold some poison to one of the family the other day",
								"I don't think he has any stock left now though...");
							break;
						case 1:
							npcTalk(p, n, "Well I don't know how much help this is",
								"but I heard that their guard dog will bark loudly at anyone",
								"it doesn't recognise",
								"maybe you should find out if anyone heard anything suspicious?");
							break;
						case 2:
							npcTalk(p, n, "Well, this might be of some help to you",
								"My father was in the guards when he was younger",
								"and he always said that there isn't a crime that can't be",
								"solved through careful examination of the crime scene",
								"and all surrounding areas");
							break;
						case 3:
							npcTalk(p, n, "I don't know how much help this is to you",
								"but my dad was in the guard once",
								"and he told me that the marks on your hands",
								"Are totally unique. He called them 'finger prints'",
								"He said you can find them easily on any shiny metallic surface",
								"By using a fine powder to mark out where the marks are",
								"and then using some sticky paper to lift the print from the object",
								"I bet if you could find a way to get everyones 'finger prints'",
								"you could solve the crime pretty easily");
							break;
						case 4:
							npcTalk(p, n, "My father used to be in the guard.",
								"He always wrote himself notes on a piece of paper",
								"so he could keep track of information easily.",
								"Maybe you should try that?",
								"Don't forget to thank me if I help you solve the case!");
							break;
					}
				}
			}
		}
		else if (n.getID() == NpcId.DONOVAN_THE_HANDYMAN.id()) {
			if (p.getQuestStage(this) == 0) {
				npcTalk(p, n, "I have no interest in talking to gawkers");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Thank you for all your help in solving the murder");
			} else {
				otherSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.MARY_THE_MAID.id()) {
			if (p.getQuestStage(this) == 0) {
				p.message("she is ignoring you");
			} else if (p.getQuestStage(this) == -1) {
				npcTalk(p, n, "Thank you for all your help in solving the murder");
			} else {
				otherSuspectDialogue(p, n);
			}
		}
		else if (n.getID() == NpcId.POISON_SALESMAN.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "Hi.");
					npcTalk(p, n, "I'm afraid I'm all sold out of poison at the moment.",
						"People know a bargain when they see it!");
					break;
				case 1:
					playerTalk(p, n, "I'm investigating the murder at the Sinclair house.");
					npcTalk(p, n, "There was a murder at the Sinclair House???",
						"Thats terrible! And I was only there the other day too",
						"They bought the last of my Patented Multi Purpose Poison!");
					int menu;
					if (hasItem(p, ItemId.MURDER_SCENE_POT.id())) {
						menu = showMenu(p, n,
							"Patented Multi Purpose Poison?",
							"Who did you sell Poison to at the house?",
							"Can I buy some Poison?",
							"I have this pot I found at the murder scene...");
					} else {
						menu = showMenu(p, n,
							"Patented Multi Purpose Poison?",
							"Who did you sell Poison to at the house?",
							"Can I buy some Poison?");
					}
					if (menu == 0) {
						npcTalk(p, n, "Aaaaah... a miracle of modern apothecarys, this exclusive",
							"concoction has been tested on all known forms of life",
							"and been proven to kill them all in varying dilutions",
							"from cockroaches to king dragons",
							"so incredibly versatile, it can be used as pest",
							"control, a cleansing agent, drain cleaner, metal polish",
							"and washes whiter than white, all with our uniquely",
							"fragrant concoction that is immediately recognisable",
							"across the land as Peter Potters Patented Poison potion");
						message(p, "The salesman stops for breath");
						npcTalk(p, n, "I'd love to sell you some but I've sold out recently",
							"Thats just how good it is! Three hundred and Twenty",
							"Eight people in this area alone cannot be wrong!",
							"Nine out of Ten poisoners prefer it in controlled tests!",
							"Can I help you with anything else?",
							"Perhaps I can take your name and add it to our mailing list",
							"Of poison users? We will only send you information related to",
							"the use of poison and other Peter Potter Products");
						playerTalk(p, n, "uh... no, its ok");
					} else if (menu == 1) {
						npcTalk(p, n, "Well, Peter Potters Patented Multi Purpose Poison",
							"is a product of such obvious quality that I am",
							"glad to say I managed to sell a bottle to each of the",
							"Sinclairs - Anna, Bob, Carol, David, Elizabeth and Frank",
							"all bought a bottle - in fact they bought the last of my supplies",
							"Maybe I can take your name and address, and I will",
							"personally come and visit you when stocks return?");
						if (!p.getCache().hasKey("poison_opt")) {
							p.getCache().store("poison_opt", true);
						}
						playerTalk(p, n, "uh... no, its ok");
					} else if (menu == 2) {
						npcTalk(p, n, "I'm afraid I am totally out of stock at the moment",
							"After my successful trip to the Sinclair's House the other day",
							"but don't worry, our factories are working overtime",
							"to produce Peter Potters Patented Multi Purpose Poison",
							"possibly the finest multi purpose poison and cleaner yet",
							"available to the general market. And its unique fragrance",
							"makes it the number one choice for cleaners, and exterminators",
							"the whole country over");
					} else if (menu == 3) {
						p.message("You show the poison salesman the pot you found at");
						p.message("The murder scene with the unusual smell");
						npcTalk(p, n, "hmmm... yes, that smells exactly like my",
							"Patented Multi Purpose Poison, but I don't see how it could be",
							"It quite clearly says on the label of all bottles",
							"not to be taken internally - extremely poisonous");
						playerTalk(p, n, "Perhaps someone else put it in his wine?");
						npcTalk(p, n, "yes... I suppose that could have happened...");
					}
					break;
			}
		}

	}

	private void otherSuspectDialogue(Player p, Npc n) {
		playerTalk(p, n, "I'm here to help the guards with their investigation");
		npcTalk(p, n, "How can I help?");
		int menu;
		if (p.getCache().hasKey("poison_opt")) {
			menu = showMenu(p, n,
				"Who do you think is responsible?",
				"Where were you at the time of the murder?",
				"Did you hear any suspicious noises at all?",
				"Do you know why so much poison was bought recently?");
		} else {
			menu = showMenu(p, n,
				"Who do you think is responsible?",
				"Where were you at the time of the murder?",
				"Did you hear any suspicious noises at all?");
		}
		if (menu == 0) {
			if (n.getID() == NpcId.HOBBES_THE_BUTLER.id()) {
				npcTalk(p, n, "Well, in my considered opinion it must be",
					"David. The man is nothing more than a bully",
					"And I happen to know that poor Lord Sinclair",
					"and David had a massive argument about the way",
					"he treats the staff in the living room the",
					"other day. I did not intend to overhear their conversation",
					"But they were shouting so loudly I could not help but",
					"Overhear it. David definitely used the words",
					"'I am going to kill you!' as well",
					"I think he should be the prime suspect.",
					"He has a nasty temper that one.");
			} else if (n.getID() == NpcId.STANFORD_THE_GARDENER.id()) {
				npcTalk(p, n, "It was Anna. She is seriously unbalanced.",
					"She trashed the garden once then tried to blame it on me!",
					"I bet it was her. Its just the kind of thing she'd do",
					"She really hates me and was arguing with Lord Sinclair",
					"about trashing the garden a few days ago.");
			} else if (n.getID() == NpcId.PIERRE_THE_DOG_HANDLER.id()) {
				npcTalk(p, n, "honestly? I think it was Carol.",
					"I saw her in a huge argument with Lord Sinclair",
					"in the library the other day. It was something",
					"to do with stolen books. She definitely seemed",
					"upset enough to have done it afterwards");
			} else if (n.getID() == NpcId.LOUISA_THE_COOK.id()) {
				npcTalk(p, n, "Elizabeth.",
					"Her father confronted her about her",
					"constant petty thieving, and was",
					"devestated to find she had stolen a silver",
					"needle which meant a lot to him.",
					"You could hear their argument from Lumbridge!");
			} else if (n.getID() == NpcId.MARY_THE_MAID.id()) {
				npcTalk(p, n, "Oh I don't know...",
					"Frank was acting kind of funny...",
					"After that big argument him and the Lord",
					"had the other day by the beehive... so",
					"I guess maybe him... but its really scary",
					"to think someone here might have been responsible.",
					"I actually hope it was a burglar");
			} else if (n.getID() == NpcId.DONOVAN_THE_HANDYMAN.id()) {
				npcTalk(p, n, "Oh... I really couldn't say.",
					"I wouldn't really want to point any fingers at anybody",
					"If I had to make a guess I'd have to say it was probably",
					"Bob though. I saw him arguing with Lord Sinclair about",
					"some missing silverware from the Kitchen",
					"It was a very heated argument.");
			}
		} else if (menu == 1) {
			if (n.getID() == NpcId.HOBBES_THE_BUTLER.id()) {
				npcTalk(p, n, "I was assisting the cook with the evening meal",
					"I gave Mary His Lordships dinner, and sent her",
					"to take it to him, then heard the scream as she",
					"found the body.");
			} else if (n.getID() == NpcId.STANFORD_THE_GARDENER.id()) {
				npcTalk(p, n, "Right here, by my little shed.",
					"Its very cosy to sit and think in");
			} else if (n.getID() == NpcId.PIERRE_THE_DOG_HANDLER.id()) {
				npcTalk(p, n, "I was in town at the inn. When I got back",
					"The house was swarming with guards who told",
					"me what had happened. Sorry.");
			} else if (n.getID() == NpcId.LOUISA_THE_COOK.id()) {
				npcTalk(p, n, "I was right here with Hobbes and Mary.",
					"You can't suspect me surely!");
			} else if (n.getID() == NpcId.MARY_THE_MAID.id()) {
				npcTalk(p, n, "I was with hobbes and Louisa in the Kitchen",
					"helping to prepare Lord Sinclair's meal, and then",
					"when I took it to his study...",
					"I saw... oh, it was horrible... he was....");
				message(p, "She seems to be on the verge of crying.",
					"You decide not to push her anymore for details.");
			} else if (n.getID() == NpcId.DONOVAN_THE_HANDYMAN.id()) {
				npcTalk(p, n, "Me? I was sound asleep here in the servants",
					"Quarters. Its very hard work as a handyman",
					"around here, theres always something to do");
			}
		} else if (menu == 2) {
			if (n.getID() == NpcId.HOBBES_THE_BUTLER.id()) {
				npcTalk(p, n, "how do you mean suspicious?");
				playerTalk(p, n, "Any sounds of a struggle with Lord Sinclair?");
				npcTalk(p, n, "No, I definitely didn't hear anything like that.");
				playerTalk(p, n, "How about the guard dog barking at all?");
				npcTalk(p, n, "You know, now you come to mention it",
					"I don't believe I did. I suppose that is",
					"Proof enough that it could not have been an",
					"intruder who is responsible.");
			} else if (n.getID() == NpcId.STANFORD_THE_GARDENER.id()) {
				npcTalk(p, n, "Not that I remember.");
				playerTalk(p, n, "So no sounds of a struggle between Lord Sinclair and an intruder?");
				npcTalk(p, n, "Not to the best of my recollection");
				playerTalk(p, n, "How about the guard dog barking?");
				npcTalk(p, n, "Not that I can recall");
			} else if (n.getID() == NpcId.PIERRE_THE_DOG_HANDLER.id()) {
				npcTalk(p, n, "well, like what?");
				playerTalk(p, n, "Any sounds of a struggle with Lord Sinclair?");
				npcTalk(p, n, "No, I don't remember hearing anything like that.");
				playerTalk(p, n, "How about the guard dog barking at all?");
				npcTalk(p, n, "I hear him bark all the time.",
					"its one of his favorite things to do.",
					"I can't say I did the night of the murder though",
					"As I wasn't close enough to hear either way");
			} else if (n.getID() == NpcId.LOUISA_THE_COOK.id()) {
				npcTalk(p, n, "suspicious? what do you mean suspicious?");
				playerTalk(p, n, "Any sounds of a struggle with an intruder for example?");
				npcTalk(p, n, "No, I'm sure I don't recall any such thing.");
				playerTalk(p, n, "How about the guard dog barking at an intruder?");
				npcTalk(p, n, "No, I didn't.",
					"If you don't have anything else to ask can",
					"You go and leave me alone now? I have a lot",
					"Of cooking to do for this evening.");
			} else if (n.getID() == NpcId.MARY_THE_MAID.id()) {
				npcTalk(p, n, "I don't really remember hearing anything out of the ordinary");
				playerTalk(p, n, "no sounds of a struggle then?");
				npcTalk(p, n, "No, I don't remember hearing anything like that.");
				playerTalk(p, n, "How about the guard dog barking?");
				npcTalk(p, n, "Oh that horrible dog is always barking at nothing",
					"but I don't think I did...");
			} else if (n.getID() == NpcId.DONOVAN_THE_HANDYMAN.id()) {
				npcTalk(p, n, "hmmm..... No, I didn't, but I sleep very soundly at night.");
				playerTalk(p, n, "So you didn't hear any sounds of a struggle or any",
					"barking from the guard dog next to his study window?");
				npcTalk(p, n, "Now you mention it, no. it is odd I didn't hear anything",
					"like that. But I do sleep very soundly as I said and",
					"wouldn't necessarily have heard it if there was any such noise");
			}
		} else if (menu == 3) {
			if (n.getID() == NpcId.HOBBES_THE_BUTLER.id()) {
				npcTalk(p, n, "Well, I do know that Elizabeth was extremely",
					"annoyed by the mosquito nest under the fountain",
					"in the garden, and was going to do something about",
					"it. I suspect any poison she bought would have been",
					"to get rid of it. A Good job too,",
					"I hate mosquitos.");
				playerTalk(p, n, "Yeah, so do I");
				npcTalk(p, n, "you'd really have to ask her though.");
			} else if (n.getID() == NpcId.STANFORD_THE_GARDENER.id()) {
				npcTalk(p, n, "Well, Bob mentioned to me the other day",
					"he wanted to get rid of the bees in that hive",
					"over there. I think I saw him buying poison",
					"from that poison salesman the other day",
					"I assume it was to sort out those bees",
					"you'd really have to ask him though.");
			} else if (n.getID() == NpcId.PIERRE_THE_DOG_HANDLER.id()) {
				npcTalk(p, n, "Well, I know David said that he was",
					"going to do something about the spiders nest thats",
					"between the two servants quarters upstairs",
					"He made a big deal about it to Mary the Maid, calling",
					"her useless and incompetent. I felt quite sorry",
					"for her actually.",
					"you'd really have to ask him though.");
			} else if (n.getID() == NpcId.LOUISA_THE_COOK.id()) {
				npcTalk(p, n, "I told Carol to buy some from that strange",
					"poison salesman and clean the drains before they",
					"began to smell any worse. She was the one who",
					"blocked them in the first place with a load",
					"of beans that she bought for some reason.",
					"There were far too many to eat, and they",
					"were almost rotten when she bought them anyway",
					"you'd really have to ask her though.");
			} else if (n.getID() == NpcId.MARY_THE_MAID.id()) {
				npcTalk(p, n, "I overheard Anna saying to Stanford",
					"that if he didn't do something about the",
					"state of his compost heap, she was going to.",
					"She really doesn't get on well with Stanford",
					"I really have no idea why",
					"you'd really have to ask her though.");
			} else if (n.getID() == NpcId.DONOVAN_THE_HANDYMAN.id()) {
				npcTalk(p, n, "Well, I do know Frank bought some poison",
					"recently to clean the family crest thats outside",
					"Its very old and rusty, and I couldn't clean it",
					"myself, so he said he would buy some cleaner and",
					"clean it himself. He probably just got some from that",
					"Poison Salesman who came to the door the other day",
					"you'd really have to ask him though.");
			}
		}
	}

	public void threadDialogue(Player p, Npc n) {
		playerTalk(p, n, "I have proof that it wasn't any of the servants");
		p.message("you show the guard the thread you found on the window");
		playerTalk(p, n, "All the servants dress in black so",
			"it couldn't have been one of them");
		npcTalk(p, n, "Thats some good work there. I guess it wasn't a servant.",
			"You still havent proved who did do it though");
	}

	public void evidenceDialogue(Player p, Npc n) {
		if (p.getCache().hasKey("p_anna2")) {
			playerTalk(p, n, "I have proof that Anna is lying about the poison");
			npcTalk(p, n, "Oh really? How did you get that?");
			p.message("you tell the guard about the compost heap");
		} else if (p.getCache().hasKey("p_carol2")) {
			playerTalk(p, n, "I have proof that Carol is lying about the poison");
			npcTalk(p, n, "Oh really? How did you get that?");
			p.message("you tell the guard about the drain");
		} else if (p.getCache().hasKey("p_eliza2")) {
			playerTalk(p, n, "I have proof that Elizabeth is lying about the poison");
			npcTalk(p, n, "Oh really? How did you get that?");
			p.message("you tell the guard about the mosquitos at the fountain");
		} else if (p.getCache().hasKey("p_bob2")) {
			playerTalk(p, n, "I have proof that Bob is lying about the poison");
			npcTalk(p, n, "Oh really? How did you get that?");
			p.message("you tell the guard about the beehive");
		} else if (p.getCache().hasKey("p_frank2")) {
			playerTalk(p, n, "I have proof that Frank is lying about the poison");
			npcTalk(p, n, "Oh really? How did you get that?");
			p.message("you tell the guard about the tarnished family crest");
		} else if (p.getCache().hasKey("p_david2")) {
			playerTalk(p, n, "I have proof that David is lying about the poison");
			npcTalk(p, n, "Oh really? How did you get that?");
			p.message("you tell the guard about the spiders nest");
		}
		npcTalk(p, n, "Hmm. thats some good detective work there.",
			"We need more evidence before we can close the case though",
			"Keep up the good work");
	}

	public void fingerprintDialogue(Player p, Npc n) {
		playerTalk(p, n, "I have the fingerprints of the culprit");
		if (p.getCache().hasKey("murder_david")) {
			playerTalk(p, n, "I have Davids' Fingerprints here.");
		} else if (p.getCache().hasKey("murder_bob")) {
			playerTalk(p, n, "I have Bobs' Fingerprints here.");
		} else if (p.getCache().hasKey("murder_anna")) {
			playerTalk(p, n, "I have Annas' Fingerprints here.");
		} else if (p.getCache().hasKey("murder_eliz")) {
			playerTalk(p, n, "I have Elizabeths' Fingerprints here.");
		} else if (p.getCache().hasKey("murder_frank")) {
			playerTalk(p, n, "I have Franks' Fingerprints here.");
		} else if (p.getCache().hasKey("murder_carol")) {
			playerTalk(p, n, "I have Carols' Fingerprints here.");
		}
		playerTalk(p, n, "You can see for yourself they match the",
			"Fingerprints on the murder weapon exactly");
		p.message("You show the guard the finger prints evidence");
		npcTalk(p, n, "...");
		npcTalk(p, n, "I'm impressed. How on earth did you think",
			"of something like that? I've never heard",
			"of such a technique for finding criminals before",
			"This will come in very handy in the future",
			"But we can't arrest someone on just this.",
			"I'm afraid you'll still need to find more evidence",
			"Before we can close this case completely");
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return i.getID() == ItemId.A_SILVER_DAGGER.id() || i.getID() == ItemId.MURDER_SCENE_POT.id();
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.A_SILVER_DAGGER.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
					p.message("This knife doesn't seem sturdy enough to have killed Lord Sinclair");
					if (!hasItem(p, ItemId.A_SILVER_DAGGER.id())) {
						addItem(p, ItemId.A_SILVER_DAGGER.id(), 1);
					} else {
						p.message("You already have the murderweapon");
					}
					break;
				case -1:
					p.message("you cannot take the flimsy dagger.");
					p.message("The guards will need it as Evidence.");
					break;
			}
		}
		else if (i.getID() == ItemId.MURDER_SCENE_POT.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
					p.message("It seems like Lord Sinclair was drinking from this before he died");
					if (!hasItem(p, ItemId.MURDER_SCENE_POT.id())) {
						addItem(p, ItemId.MURDER_SCENE_POT.id(), 1);
					} else {
						p.message("You already have the sickly smelling pot");
					}
					break;
				case -1:
					p.message("you cannot take the strange smelling pot.");
					p.message("The guards will need it as Evidence.");
					break;
			}
		}

	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player player) {
		/** WINDOW FOR THREAD **/
		return obj.getID() == 205;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 205) {
			switch (p.getQuestStage(this)) {
				case 0:
				case -1:
					p.message("You need the guards permission to do that");
					break;
				case 1:
					message(p, "Some thread seems to have been caught",
						"on a loose nail on the window");
					if (!p.getCache().hasKey("thread") && !p.getInventory().hasItemId(ItemId.THREAD_GREEN.id())
						&& !p.getInventory().hasItemId(ItemId.THREAD_RED.id()) && !p.getInventory().hasItemId(ItemId.THREAD_BLUE.id())) {
						if (p.getCache().hasKey("murder_david")) {
							addItem(p, ItemId.THREAD_GREEN.id(), 1);
						} else if (p.getCache().hasKey("murder_anna")) {
							addItem(p, ItemId.THREAD_GREEN.id(), 1);
						} else if (p.getCache().hasKey("murder_carol")) {
							addItem(p, ItemId.THREAD_RED.id(), 1);
						} else if (p.getCache().hasKey("murder_frank")) {
							addItem(p, ItemId.THREAD_BLUE.id(), 1);
						} else if (p.getCache().hasKey("murder_eliz")) {
							addItem(p, ItemId.THREAD_BLUE.id(), 1);
						} else if (p.getCache().hasKey("murder_bob")) {
							addItem(p, ItemId.THREAD_RED.id(), 1);
						}
						message(p, "You take the thread");
						p.getCache().store("thread", true);
						if (!p.getCache().hasKey("thread")) {
							p.getCache().store("thread", true);
						}
						return;
					} else if (p.getCache().hasKey("thread") && !p.getInventory().hasItemId(ItemId.THREAD_GREEN.id())
						&& !p.getInventory().hasItemId(ItemId.THREAD_RED.id()) && !p.getInventory().hasItemId(ItemId.THREAD_BLUE.id())) {
						if (p.getCache().hasKey("murder_david")) {
							addItem(p, ItemId.THREAD_GREEN.id(), 1);
						} else if (p.getCache().hasKey("murder_anna")) {
							addItem(p, ItemId.THREAD_GREEN.id(), 1);
						} else if (p.getCache().hasKey("murder_carol")) {
							addItem(p, ItemId.THREAD_RED.id(), 1);
						} else if (p.getCache().hasKey("murder_frank")) {
							addItem(p, ItemId.THREAD_BLUE.id(), 1);
						} else if (p.getCache().hasKey("murder_eliz")) {
							addItem(p, ItemId.THREAD_BLUE.id(), 1);
						} else if (p.getCache().hasKey("murder_bob")) {
							addItem(p, ItemId.THREAD_RED.id(), 1);
						}
						message(p, "Lucky for you theres some thread left",
							"You should be less careless in future");
					} else {
						p.message("You have already taken the thread");
					}
					break;
			}
		}

	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		// BARRELS / SACKS / FLOUR BARREL
		return DataConversions.inArray(new int[] {1133, 1132, 1136, 1137, 1135, 1134}, obj.getID()) || obj.getID() == 1139 || obj.getID() == 1138
				// COMPOST / FOUNTAIN / BEEHIVE / DRAIN / GATE TO DOG / SINCLAIR CREST / SPIDER NEST WEB
				|| DataConversions.inArray(new int[] {1126, 1130, 1127, 1128, 1140, 1131, 1129}, obj.getID());
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 1133 ||
			obj.getID() == 1132 ||
			obj.getID() == 1136 ||
			obj.getID() == 1137 ||
			obj.getID() == 1135 ||
			obj.getID() == 1134 ||
			obj.getID() == 1139 ||
			obj.getID() == 1126 ||
			obj.getID() == 1130 ||
			obj.getID() == 1127 ||
			obj.getID() == 1128 ||
			obj.getID() == 1140 ||
			obj.getID() == 1138 ||
			obj.getID() == 1131 ||
			obj.getID() == 1129) {
			switch (p.getQuestStage(this)) {
				case 0:
				case -1:
					p.message("You need the guards permission to do that");
					break;
				case 1:
					if (obj.getID() == 1133) {
						p.message("Theres something shiny hidden at the bottom");
						if (!hasItem(p, ItemId.BOBS_SILVER_TEACUP.id())) {
							p.message("You take Bobs silver cup");
							addItem(p, ItemId.BOBS_SILVER_TEACUP.id(), 1);
						} else {
							p.message("You already have Bobs cup");
						}
					} else if (obj.getID() == 1132) {
						p.message("Theres something shiny hidden at the bottom");
						if (!hasItem(p, ItemId.ANNAS_SILVER_NECKLACE.id())) {
							p.message("You take Annas Silver Necklace");
							addItem(p, ItemId.ANNAS_SILVER_NECKLACE.id(), 1);
						} else {
							p.message("You already have Annas Necklace");
						}
					} else if (obj.getID() == 1136) {
						p.message("Theres something shiny hidden at the bottom");
						if (!hasItem(p, ItemId.ELIZABETHS_SILVER_NEEDLE.id())) {
							p.message("You take Elizabeths silver needle");
							addItem(p, ItemId.ELIZABETHS_SILVER_NEEDLE.id(), 1);
						} else {
							p.message("You already have Elizabeths Needle");
						}
					} else if (obj.getID() == 1137) {
						p.message("Theres something shiny hidden at the bottom");
						if (!hasItem(p, ItemId.FRANKS_SILVER_POT.id())) {
							p.message("You take franks silver pot");
							addItem(p, ItemId.FRANKS_SILVER_POT.id(), 1);
						} else {
							p.message("You already have Franks pot");
						}
					} else if (obj.getID() == 1135) {
						p.message("Theres something shiny hidden at the bottom");
						if (!hasItem(p, ItemId.DAVIDS_SILVER_BOOK.id())) {
							p.message("You take Davids silver book");
							addItem(p, ItemId.DAVIDS_SILVER_BOOK.id(), 1);
						} else {
							p.message("You already have Davids book");
						}
					} else if (obj.getID() == 1134) {
						p.message("Theres something shiny hidden at the bottom");
						if (!hasItem(p, ItemId.CAROLS_SILVER_BOTTLE.id())) {
							p.message("You take Carols silver bottle");
							addItem(p, ItemId.CAROLS_SILVER_BOTTLE.id(), 1);
						} else {
							p.message("You already have Carols bottle");
						}
					} else if (obj.getID() == 1139) {
						p.message("Theres some flypaper in there.");
						p.message("Do you take it?");
						int sack = showMenu(p,
							"Yes, it might be useful",
							"No, I don't see any need for it");
						if (sack == 0) {
							p.message("You take a piece of fly paper");
							p.message("There is still plenty of fly paper left");
							addItem(p, ItemId.FLYPAPER.id(), 1);
						} else if (sack == 1) {
							p.message("you leave the paper in the sack");
						}
					} else if (obj.getID() == 1126) {
						if (p.getCache().hasKey("poison_opt2") && p.getCache().hasKey("murder_anna")) {
							message(p, "The compost is teeming with maggots",
								"Somebody should really do something about it",
								"Its certainly clear nobodies used poison here.");
							p.getCache().store("evidence", true);
							p.getCache().store("p_anna2", true);
						} else if (p.getCache().hasKey("poison_opt2") && !p.getCache().hasKey("murder_anna")) {
							message(p, "There is a faint smell of poison behind the smell of the compost");
						} else {
							p.message("Its a heap of Compost");
						}

					} else if (obj.getID() == 1130) {
						if (p.getCache().hasKey("poison_opt2") && p.getCache().hasKey("murder_eliz")) {
							message(p, "The fountain is swarming with mosquitos",
								"Theres a nest of them underneath the fountain");
							playerTalk(p, null, "I hate mosquitos, they're so annoying");
							message(p, "Its certainly clear nobodies used poison here.");
							p.getCache().store("evidence", true);
							p.getCache().store("p_eliza2", true);
						} else if (p.getCache().hasKey("poison_opt2") && !p.getCache().hasKey("murder_eliz")) {
							message(p, "There are a lot of dead mosquitos around",
								"the base of the fountain. A faint smell of",
								"poison is in the air, but the water seems clean");
						} else {
							p.message("A fountain with large numbers of insects around the base");
						}

					} else if (obj.getID() == 1127) {
						if (p.getCache().hasKey("poison_opt2") && p.getCache().hasKey("murder_bob")) {
							message(p, "The beehive buzzes with activity",
								"These bees definitely don't seem poisoned at all");
							p.getCache().store("evidence", true);
							p.getCache().store("p_bob2", true);
						} else if (p.getCache().hasKey("poison_opt2") && !p.getCache().hasKey("murder_bob")) {
							message(p, "The hive is empty. There are a few dead bees and",
								"a faint smell of poison");
						} else {
							p.message("Its a very old beehive");
						}

					} else if (obj.getID() == 1128) {
						if (p.getCache().hasKey("poison_opt2") && p.getCache().hasKey("murder_carol")) {
							message(p, "The drain is totally blocked",
								"It really stinks. No, it *Really* smells bad.",
								"Its certainly clear nobodies cleaned it recently.");
							p.getCache().store("evidence", true);
							p.getCache().store("p_carol2", true);
						} else if (p.getCache().hasKey("poison_opt2") && !p.getCache().hasKey("murder_carol")) {
							message(p, "The drain seems to have been recently cleaned",
								"You can still smell the faint aroma of poison");
						} else {
							p.message("Its the drains from the kitchen");
						}

					} else if (obj.getID() == 1140) {
						message(p, "As you approach the gate the Guard Dog starts barking loudly at you",
							"There is no way an intruder could have committed the murder",
							"It must have been someone the dog knew to get past it quietly");
					} else if (obj.getID() == 1138) {
						p.message("A barrel full of finely sifted flour");
						if (!hasItem(p, ItemId.POT.id()) && !hasItem(p, ItemId.MURDER_SCENE_POT.id())) {
							p.message("You need something to put the flour in");
						} else if (hasItem(p, ItemId.POT.id())) {
							p.message("You take some flour from the barrel");
							p.getInventory().replace(ItemId.POT.id(), ItemId.POT_OF_FLOUR.id());

							p.message("Theres still plenty of flour left");
						} else if (hasItem(p, ItemId.MURDER_SCENE_POT.id())) {
							message(p, "You probably shouldn't use evidence from a crime",
								"scene to keep flour in...");
						}
					} else if (obj.getID() == 1131) {
						if (p.getCache().hasKey("poison_opt2") && p.getCache().hasKey("murder_frank")) {
							message(p, "It looks like the Sinclair Family Crest",
								"but it is very dirty.",
								"you can barely make it out under all of the grime",
								"Its certainly clear nobodies cleaned it recently.");
							p.getCache().store("evidence", true);
							p.getCache().store("p_frank2", true);
						} else if (p.getCache().hasKey("poison_opt2") && !p.getCache().hasKey("murder_frank")) {
							message(p, "The sinclair family crest",
								"its shiny and freshly polished",
								"And has a slight smell of poison");
						} else {
							p.message("The Sinclair Family Crest is hung up here");
						}

					} else if (obj.getID() == 1129) {
						if (p.getCache().hasKey("poison_opt2") && p.getCache().hasKey("murder_david")) {
							message(p, "There is a spiders nest here",
								"You estimate there must be at least a few hundred spiders ready to hatch",
								"Its certainly clear nobodies used poison here.");
							p.getCache().store("evidence", true);
							p.getCache().store("p_david2", true);
						} else if (p.getCache().hasKey("poison_opt2") && !p.getCache().hasKey("murder_david")) {
							message(p, "A faint smell of poison and a few dead spiders",
								"is all that remains of the spiders nest");
						} else {
							p.message("It looks like a Spiders Nest of some kind");
						}

					}
					break;
			}
		}

	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.A_SILVER_DAGGER.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.ANNAS_SILVER_NECKLACE.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.BOBS_SILVER_TEACUP.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.CAROLS_SILVER_BOTTLE.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.DAVIDS_SILVER_BOOK.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.ELIZABETHS_SILVER_NEEDLE.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.FRANKS_SILVER_POT.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.MURDER_SCENE_POT.id(), ItemId.POT_OF_FLOUR.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.A_SILVER_DAGGER_FLOUR.id(), ItemId.FLYPAPER.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.ANNAS_SILVER_NECKLACE_FLOUR.id(), ItemId.FLYPAPER.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.BOBS_SILVER_TEACUP_FLOUR.id(), ItemId.FLYPAPER.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.CAROLS_SILVER_BOTTLE_FLOUR.id(), ItemId.FLYPAPER.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.DAVIDS_SILVER_BOOK_FLOUR.id(), ItemId.FLYPAPER.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.ELIZABETHS_SILVER_NEEDLE_FLOUR.id(), ItemId.FLYPAPER.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.FRANKS_SILVER_POT_FLOUR.id(), ItemId.FLYPAPER.id())
				|| (item1.getID() == ItemId.UNIDENTIFIED_FINGERPRINT.id() &&
				DataConversions.inArray(new int[] {ItemId.ANNAS_FINGERPRINT.id(), ItemId.BOBS_FINGERPRINT.id(), ItemId.CAROLS_FINGERPRINT.id(),
						ItemId.DAVIDS_FINGERPRINT.id(), ItemId.ELIZABETHS_FINGERPRINT.id(), ItemId.FRANKS_FINGERPRINT.id()}, item2.getID()))
				|| (item2.getID() == ItemId.UNIDENTIFIED_FINGERPRINT.id() &&
				DataConversions.inArray(new int[] {ItemId.ANNAS_FINGERPRINT.id(), ItemId.BOBS_FINGERPRINT.id(), ItemId.CAROLS_FINGERPRINT.id(),
						ItemId.DAVIDS_FINGERPRINT.id(), ItemId.ELIZABETHS_FINGERPRINT.id(), ItemId.FRANKS_FINGERPRINT.id()}, item1.getID()));
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.A_SILVER_DAGGER.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle a small amount of flour on the murderweapon");
			p.message("the murderweapon is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.A_SILVER_DAGGER.id(), ItemId.A_SILVER_DAGGER_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.A_SILVER_DAGGER_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the floury dagger");
			p.message("You have a clean impression of the murderers finger prints");
			p.getInventory().replace(ItemId.A_SILVER_DAGGER_FLOUR.id(), ItemId.A_SILVER_DAGGER.id());
			addItem(p, ItemId.UNIDENTIFIED_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.ANNAS_SILVER_NECKLACE.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle the flour on Annas Necklace");
			p.message("the necklace is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.ANNAS_SILVER_NECKLACE.id(), ItemId.ANNAS_SILVER_NECKLACE_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.ANNAS_SILVER_NECKLACE_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the flour covered Necklace");
			p.message("You have a clean impression of Annas finger prints");
			p.getInventory().replace(ItemId.ANNAS_SILVER_NECKLACE_FLOUR.id(), ItemId.ANNAS_SILVER_NECKLACE.id());
			addItem(p, ItemId.ANNAS_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.BOBS_SILVER_TEACUP.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle the flour on Bobs Cup");
			p.message("the cup is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.BOBS_SILVER_TEACUP.id(), ItemId.BOBS_SILVER_TEACUP_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.BOBS_SILVER_TEACUP_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the flour covered Cup");
			p.message("You have a clean impression of Bobs finger prints");
			p.getInventory().replace(ItemId.BOBS_SILVER_TEACUP_FLOUR.id(), ItemId.BOBS_SILVER_TEACUP.id());
			addItem(p, ItemId.BOBS_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.CAROLS_SILVER_BOTTLE.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle the flour on Carols Bottle");
			p.message("the bottle is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.CAROLS_SILVER_BOTTLE.id(), ItemId.CAROLS_SILVER_BOTTLE_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.CAROLS_SILVER_BOTTLE_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the flour covered Bottle");
			p.message("You have a clean impression of Carols finger prints");
			p.getInventory().replace(ItemId.CAROLS_SILVER_BOTTLE_FLOUR.id(), ItemId.CAROLS_SILVER_BOTTLE.id());
			addItem(p, ItemId.CAROLS_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.DAVIDS_SILVER_BOOK.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle the flour on Davids Book");
			p.message("the book is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.DAVIDS_SILVER_BOOK.id(), ItemId.DAVIDS_SILVER_BOOK_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.DAVIDS_SILVER_BOOK_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the flour covered Book");
			p.message("You have a clean impression of Davids finger prints");
			p.getInventory().replace(ItemId.DAVIDS_SILVER_BOOK_FLOUR.id(), ItemId.DAVIDS_SILVER_BOOK.id());
			addItem(p, ItemId.DAVIDS_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.ELIZABETHS_SILVER_NEEDLE.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle the flour on Elizabeths Needle");
			p.message("the needle is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.ELIZABETHS_SILVER_NEEDLE.id(), ItemId.ELIZABETHS_SILVER_NEEDLE_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.ELIZABETHS_SILVER_NEEDLE_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the flour covered Needle");
			p.message("You have a clean impression of Elizabeths finger prints");
			p.getInventory().replace(ItemId.ELIZABETHS_SILVER_NEEDLE_FLOUR.id(), ItemId.ELIZABETHS_SILVER_NEEDLE.id());
			addItem(p, ItemId.ELIZABETHS_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.FRANKS_SILVER_POT.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle the flour on Franks Pot");
			p.message("the pot is now coated with a thin layer of flour");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());
			p.getInventory().replace(ItemId.FRANKS_SILVER_POT.id(), ItemId.FRANKS_SILVER_POT_FLOUR.id());

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.FRANKS_SILVER_POT_FLOUR.id(), ItemId.FLYPAPER.id())) {
			p.message("You use the flypaper on the flour covered Pot");
			p.message("You have a clean impression of Franks finger prints");
			p.getInventory().replace(ItemId.FRANKS_SILVER_POT_FLOUR.id(), ItemId.FRANKS_SILVER_POT.id());
			addItem(p, ItemId.FRANKS_FINGERPRINT.id(), 1);
			removeItem(p, ItemId.FLYPAPER.id(), 1);

		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.MURDER_SCENE_POT.id(), ItemId.POT_OF_FLOUR.id())) {
			p.message("You sprinkle a small amount of flour on the strange smelling pot");
			p.message("The surface isn't shiny enough to take a fingerprint from");
			p.getInventory().replace(ItemId.POT_OF_FLOUR.id(), ItemId.POT.id());

		}
		else if ((item1.getID() == ItemId.UNIDENTIFIED_FINGERPRINT.id() &&
				DataConversions.inArray(new int[] {ItemId.ANNAS_FINGERPRINT.id(), ItemId.BOBS_FINGERPRINT.id(), ItemId.CAROLS_FINGERPRINT.id(),
						ItemId.DAVIDS_FINGERPRINT.id(), ItemId.ELIZABETHS_FINGERPRINT.id(), ItemId.FRANKS_FINGERPRINT.id()}, item2.getID()))
				|| (item2.getID() == ItemId.UNIDENTIFIED_FINGERPRINT.id() &&
				DataConversions.inArray(new int[] {ItemId.ANNAS_FINGERPRINT.id(), ItemId.BOBS_FINGERPRINT.id(), ItemId.CAROLS_FINGERPRINT.id(),
						ItemId.DAVIDS_FINGERPRINT.id(), ItemId.ELIZABETHS_FINGERPRINT.id(), ItemId.FRANKS_FINGERPRINT.id()}, item1.getID()))) {
			if (item1.getID() == ItemId.DAVIDS_FINGERPRINT.id() || item2.getID() == ItemId.DAVIDS_FINGERPRINT.id()) {
				if (p.getCache().hasKey("murder_david")) {
					p.message("The fingerprints are an exact match to Davids");
					p.getInventory().replace(ItemId.UNIDENTIFIED_FINGERPRINT.id(), ItemId.MURDERERS_FINGERPRINT.id());
					if (!p.getCache().hasKey("culprit"))
						p.getCache().store("culprit", true);
				} else {
					p.message("They don't seem to be the same");
					removeItem(p, ItemId.DAVIDS_FINGERPRINT.id(), 1);
					p.message("I guess that clears David of the crime");
					sleep(800);
					p.message("You destroy the useless fingerprint");
				}
			} else if (item1.getID() == ItemId.BOBS_FINGERPRINT.id() || item2.getID() == ItemId.BOBS_FINGERPRINT.id()) {
				if (p.getCache().hasKey("murder_bob")) {
					p.message("The fingerprints are an exact match to Bobs");
					p.getInventory().replace(ItemId.UNIDENTIFIED_FINGERPRINT.id(), ItemId.MURDERERS_FINGERPRINT.id());
					if (!p.getCache().hasKey("culprit"))
						p.getCache().store("culprit", true);
				} else {
					p.message("They don't seem to be the same");
					removeItem(p, ItemId.BOBS_FINGERPRINT.id(), 1);
					p.message("I guess that clears Bob of the crime");
					sleep(800);
					p.message("You destroy the useless fingerprint");
				}
			} else if (item1.getID() == ItemId.ELIZABETHS_FINGERPRINT.id() || item2.getID() == ItemId.ELIZABETHS_FINGERPRINT.id()) {
				if (p.getCache().hasKey("murder_eliz")) {
					p.message("The fingerprints are an exact match to Elizabeths");
					p.getInventory().replace(ItemId.UNIDENTIFIED_FINGERPRINT.id(), ItemId.MURDERERS_FINGERPRINT.id());
					if (!p.getCache().hasKey("culprit"))
						p.getCache().store("culprit", true);
				} else {
					p.message("They don't seem to be the same");
					removeItem(p, ItemId.ELIZABETHS_FINGERPRINT.id(), 1);
					p.message("I guess that clears Elizabeth of the crime");
					sleep(800);
					p.message("You destroy the useless fingerprint");
				}
			} else if (item1.getID() == ItemId.ANNAS_FINGERPRINT.id() || item2.getID() == ItemId.ANNAS_FINGERPRINT.id()) {
				if (p.getCache().hasKey("murder_anna")) {
					p.message("The fingerprints are an exact match to Annas");
					p.getInventory().replace(ItemId.UNIDENTIFIED_FINGERPRINT.id(), ItemId.MURDERERS_FINGERPRINT.id());
					if (!p.getCache().hasKey("culprit"))
						p.getCache().store("culprit", true);
				} else {
					p.message("They don't seem to be the same");
					removeItem(p, ItemId.ANNAS_FINGERPRINT.id(), 1);
					p.message("I guess that clears Anna of the crime");
					sleep(800);
					p.message("You destroy the useless fingerprint");
				}
			} else if (item1.getID() == ItemId.CAROLS_FINGERPRINT.id() || item2.getID() == ItemId.CAROLS_FINGERPRINT.id()) {
				if (p.getCache().hasKey("murder_carol")) {
					p.message("The fingerprints are an exact match to Carols");
					p.getInventory().replace(ItemId.UNIDENTIFIED_FINGERPRINT.id(), ItemId.MURDERERS_FINGERPRINT.id());
					if (!p.getCache().hasKey("culprit"))
						p.getCache().store("culprit", true);
				} else {
					p.message("They don't seem to be the same");
					removeItem(p, ItemId.CAROLS_FINGERPRINT.id(), 1);
					p.message("I guess that clears Carol of the crime");
					sleep(800);
					p.message("You destroy the useless fingerprint");
				}
			} else if (item1.getID() == ItemId.FRANKS_FINGERPRINT.id() || item2.getID() == ItemId.FRANKS_FINGERPRINT.id()) {
				if (p.getCache().hasKey("murder_frank")) {
					p.message("The fingerprints are an exact match to Franks");
					p.getInventory().replace(ItemId.UNIDENTIFIED_FINGERPRINT.id(), ItemId.MURDERERS_FINGERPRINT.id());
					if (!p.getCache().hasKey("culprit"))
						p.getCache().store("culprit", true);
				} else {
					p.message("They don't seem to be the same");
					removeItem(p, ItemId.FRANKS_FINGERPRINT.id(), 1);
					p.message("I guess that clears Frank of the crime");
					sleep(800);
					p.message("You destroy the useless fingerprint");
				}
			}

		}
	}
}
