package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FishingContest implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger,
	UseNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.FISHING_CONTEST;
	}

	@Override
	public String getQuestName() {
		return "Fishing contest (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.FISHING_CONTEST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(final Player player) {
		player.updateQuestStage(Quests.FISHING_CONTEST, -1);
		player.message("Well done you have completed the fishing competition quest");
		final QuestReward reward = Quest.FISHING_CONTEST.reward();
		final int extraXP = player.getSkills().getMaxStat(Skill.FISHING.id()) >= 24 ? 800 : 0;
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP() + extraXP, xpReward.getVarXP());
		}
	}

	private void addCatchCache(final Player player, int catchId) {
		String catchString = "";
		if (player.getCache().hasKey("contest_catches")) {
			catchString = player.getCache().getString("contest_catches") + "-";
		}
		catchString += catchId;
		player.getCache().store("contest_catches", catchString);
	}

	private void bigDaveDialogue(final Player player, final Npc n) {
		npcsay(player, n, "Oi whaddya think ya doin'", "I'm fishin' here",
			"Now beat it");
	}

	@Override
	public boolean blockUseLoc(final Player player, final GameObject obj,
							   final Item item) {
		return obj.getID() == SceneryId.VINE_RED_FISHING_CONTEST.id() || obj.getID() == SceneryId.PIPE_FISHING_CONTEST.id();
	}

	@Override
	public boolean blockOpLoc(final Player player, final GameObject obj,
							  final String command) {
		return obj.getID() == SceneryId.GATE_WOODEN_FISHING_CONTEST_CLOSED.id() || obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_CARPS_SPOT.id() || obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_REGULAR_SPOT.id()
				|| obj.getID() == SceneryId.STAIRS_STONE_WHITE_WOLF_PASS_DOWN.id() || obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_DAVE_SPOT.id() || obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_JOSHUA_SPOT.id();
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		// joshua and big dave were not interested in talking directly
		return DataConversions.inArray(new int[] {NpcId.MOUNTAIN_DWARF.id(), NpcId.BONZO.id(), NpcId.SINISTER_STRANGER.id(),
				NpcId.GRANDPA_JACK.id()}, n.getID());
	}

	private void bonzoDialogue(final Player player, final Npc n, final boolean isDirectTalk) {
		Npc sinister = ifnearvisnpc(player, NpcId.SINISTER_STRANGER.id(), 10);
		switch (player.getQuestStage(this)) {
			// quest completed
			case -1:
				if (!isDirectTalk) {
					mes("you have already won the fishing competition");
					delay(3);
				} else {
					npcsay(player, n, "Hello champ",
						"So any hints on how to fish so well");
					say(player, n, "I think I'll keep them to myself");
				}
				break;
			default: // EVERY OTHER QUEST STAGE
				if (player.getCache().hasKey("paid_contest_fee")) {
					String catches[] = {};
					boolean hasCarp = false;

					if (player.getCache().hasKey("contest_catches")) {
						catches = player.getCache().getString("contest_catches").split("-");
					}

					for (String aCatch : catches) {
						hasCarp |= (Integer.valueOf(aCatch) == ItemId.RAW_GIANT_CARP.id() && player.getCarriedItems().hasCatalogID(ItemId.RAW_GIANT_CARP.id(), Optional.of(false)));
					}

					npcsay(player, n, "so how are you doing so far?");
					if (hasCarp) {
						//do not send over
						final int contestStartedMenu = multi(player, n, false,
							"I have this big fish,is it enough to win?",
							"I think I might still be able to find a bigger fish");
						if (contestStartedMenu == 0) {
							say(player, n, "I have this big fish", "Is it enough to win?");
							npcsay(player, n, "Well we'll just wait till time is up");
							player.message("You wait");
							delay(3);
							bonzoTimesUpDialogue(player, n);
						} else if (contestStartedMenu == 1) {
							say(player, n, "I think I might still be able to find a bigger fish");
							npcsay(player, n, "Ok, good luck");
						}
					} else {
						say(player, n, "I think I might still be able to find a bigger fish");
						npcsay(player, n, "Ok, good luck");
					}

					return;
				} else {
					// with trophy does not allow to enter competition
					if (player.getCarriedItems().hasCatalogID(ItemId.HEMENSTER_FISHING_TROPHY.id(), Optional.of(false))) {
						npcsay(player, n, "Hello champ",
							"So any hints on how to fish so well");
						say(player, n, "I think I'll keep them to myself");
						return;
					}

					if (isDirectTalk) {
						npcsay(player, n, "Roll up, roll up",
							"Enter the great Hemenster fishing competition",
							"only 5gp entrance fee");
					} else {
						npcsay(player, n, "Hey you need to pay to join the competition first",
							"only 5gp entrance fee");
					}
					final int first = multi(player, n,
						"I'll give that a go then",
						"No thanks, I'll just watch the fun");
					if (first == 0) {
						npcsay(player, n, "Marvelous");
						if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 5) {
							player.message("You pay bonzo 5 coins");
							player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
							npcsay(player, n, "Ok we've got all the fishermen",
								"It's time to roll",
								"Ok nearly everyone is in there place already",
								"You fish in the spot by the oak tree",
								"And the Sinister stranger you fish by the pipes");
							if (!player.getCache().hasKey("garlic_activated")) {
								player.message("Your fishing competition spot is beside the oak tree");
							} else {
								if (sinister != null) {
									npcsay(player, sinister,
										"Arrgh what is that ghastly smell",
										"I think I will move over here instead");
									sinister.teleport(570, 495);
								}
								npcsay(player, n,
									"Hmm you'd better go and take the area by the pipes then");
								player.message("Your fishing competition spot is beside the pipes");
							}
							player.getCache().store("paid_contest_fee", true);

						} else {
							mes("I don't have the 5gp though");
							delay(3);
							npcsay(player, n, "No pay, no play");
						}
					} else if (first == 1) {
						// NOTHING
					}
				}
				break;
		}
	}

	private void bonzoTimesUpDialogue(final Player player, final Npc n) {
		String catches[] = {};
		boolean hadCarp = false;

		if (player.getCache().hasKey("contest_catches")) {
			catches = player.getCache().getString("contest_catches").split("-");
		}

		npcsay(player, n, "Okay folks times up",
			"Lets see who caught the biggest fish");
		mes("You hand over your catch");
		delay(3);
		for (String aCatch : catches) {
			hadCarp |= (Integer.valueOf(aCatch) == ItemId.RAW_GIANT_CARP.id() && player.getCarriedItems().hasCatalogID(ItemId.RAW_GIANT_CARP.id(), Optional.of(false)));
			player.getCarriedItems().remove(new Item(Integer.valueOf(aCatch)));
		}
		player.getCache().remove("contest_catches");
		player.getCache().remove("paid_contest_fee");

		if (hadCarp) {
			npcsay(player, n, "We have a new winner");
			npcsay(player, n, "The heroic looking person",
				"who was fishing by the pipes",
				"Has caught the biggest carp",
				"I've seen since Grandpa Jack used to compete");
			player.message("you are given the Hemenster fishing trophy");
			give(player, ItemId.HEMENSTER_FISHING_TROPHY.id(), 1);
			player.updateQuestStage(getQuestId(), 3);
		}
		// select another one from chance
		else {
			int chance_stranger = 80;
			int chance_dave = 15;
			int rol = DataConversions.random(0, 100);
			npcsay(player, n, "And the winner is...");
			if (chance_stranger > rol) {
				npcsay(player, n, "The stranger in black");
			} else if (chance_dave > rol - 80) {
				npcsay(player, n, "local favourite- Big Dave");
			} else {
				npcsay(player, n, "the surprising Joshua");
			}
		}
	}

	private void grandpaJackDialogue(final Player player, final Npc n) {
		switch (player.getQuestStage(this)) {
			case 1:
			case 2:
				npcsay(player, n, player.getText("FishingContestGrandpaJackHelloYoungOne"), "Come to visit old Grandpa Jack?",
					"I can tell ye stories for sure",
					"I used to be the best fisherman these parts have seen");
				int first = multi(player, n,
					"Tell me a story then",
					"Are you entering the fishing competition?",
					"Sorry I don't have time now");
				if (first == 0) {
					npcsay(player,
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
					npcsay(player, n, "Ah the Hemenster fishing competition",
						"I know all about that",
						"I won that four years straight",
						"I'm to old for that lark now though");
					//do not send over
					final int second = multi(player, n, false,
						"I don't suppose you could give me any hints?",
						"That's less competition for me then");
					if (second == 0) {
						say(player, n, "I don't suppose you could give me any hints?");
						npcsay(player,
							n,
							"Well you sometimes get these really big fish",
							"In the water just by the outflow pipes",
							"Think they're some kind of carp",
							"try to get a spot round there",
							"The best sort of bait for them is red vine worms",
							"I used to get those from McGruber's wood, north of here",
							"dig around in the red vines up there");
						if (player.getQuestStage(getQuestId()) != 2) {
							player.updateQuestStage(getQuestId(), 2);
						}
					} else if (second == 1) {
						say(player, n, "That's less competition for me then\"");
					}
				} else if (first == 2) {
					npcsay(player, n, "sigh", "Young people - always in such a rush");
				}
				break;
			default:
				npcsay(player, n, player.getText("FishingContestGrandpaJackHelloYoungOne"), "Come to visit old Grandpa Jack?",
					"I can tell ye stories for sure",
					"I used to be the best fisherman these parts have seen");
				first = multi(player, n,
					"Tell me a story then",
					"Sorry I don't have time now");
				if (first == 0) {
					npcsay(player,
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
					npcsay(player, n, "sigh", "Young people - always in such a rush");
				}
				break;
		}
	}

	private void joshuaDialogue(final Player player, final Npc n) {
		npcsay(player, n, "This is my fishing spot", "Ya don't wanna be fishing 'ere mate",
			"Cos I'll break your knuckles");
	}

	private void goDownDialogue(final Player player, final Npc n) {
		npcsay(player, n, "This is the home of the mountain dwarves",
			"How would you like it if I wanted to take a short cut through your home");
		//do not send over
		final int third = multi(player, n, false,
			"Ooh is this a short cut to somewhere",
			"Oh sorry I hadn't realised it was private",
			"If you were my friend I wouldn't mind it");
		if (third == 0) {
			say(player, n, "Ooh is this a short cut to somewhere?");
			npcsay(player, n, "Well it is easier to go this way",
				"Than through passes full of wolves");
		} else if (third == 1) {
			say(player, n, "Oh sorry I hadn't realised it was private");
		} else if (third == 2) {
			say(player, n, "If you were my friend I wouldn't mind");
			npcsay(player, n, "Yes, but I don't even know you");
			//do not send over
			final int fourth = multi(player, n, false,
				"Well lets be friends",
				"You're a grumpy little man aren't you?");
			if (fourth == 0) {
				say(player, n, "Well lets be friends");
				npcsay(player, n, "I don't make friends easily",
					"People need to earn my trust first");

				//do not send over
				final int fifth = multi(player, n, false,
					"And how am I meant to do that?",
					"You're a grumpy little man aren't you?");
				if (fifth == 0) {
					say(player, n, "And how am I meant to do that?");
					npcsay(player,
						n,
						"My we are the persistant one aren't we",
						"Well theres a certain gold artifact we're after",
						"We dwarves are big fans of gold",
						"This artifact is the first prize at the hemenster fishing competition",
						"Fortunately we have acquired a pass to enter that competition",
						"Unfortunately Dwarves don't make good fishermen");
					//do not send over
					final int six = multi(player, n, false,
						"Fortunately I'm alright at fishing",
						"I'm not much of a fisherman either");
					if (six == 0) {
						say(player, n, "fortunately I'm alright at fishing");
						npcsay(player,
							n,
							"Okay I entrust you with our competition pass",
							"go to Hemenster and do us proud");
						give(player, ItemId.FISHING_COMPETITION_PASS.id(), 1);
						player.updateQuestStage(getQuestId(), 1);
					} else if (six == 1) {
						say(player, n, "I'm not much of a fisherman either");
						npcsay(player, n, "what good are you?");
					}
				} else if (fifth == 1) {
					say(player, n, "You're a grumpy little man aren't you");
					npcsay(player, n, " Don't you know it");
				}

			} else if (fourth == 1) {
				say(player, n, "You're a grumpy little man aren't you");
				npcsay(player, n, " Don't you know it");
			}
		}
	}

	private void mountainDwarfDialogue(final Player player, final Npc n) {
		switch (player.getQuestStage(this)) {
			case 0:
				npcsay(player, n, "hmmph what do you want");

				//do not send over
				final int first = multi(player, n, false,
					"I was wondering what was down those stairs?",
					"I was just stopping to say hello");

				if (first == 0) {
					say(player, n, "I was just wondering what was down those stairs?");
					npcsay(player, n, "You can't go down there");
					//do not send over
					final int second = multi(player, n, false,
						"I didn't want to anyway", "Why not?",
						"I'm bigger than you let me by");
					if (second == 0) {
						say(player, n, "I didn't want to anyway");
						npcsay(player, n, "Good");
					} else if (second == 1) {
						say(player, n, "Why not?");
						goDownDialogue(player, n);
					} else if (second == 2) {
						say(player, n, "I'm bigger than you", "Let me by");
						npcsay(player, n, "Go away",
							"You're not going to bully your way in here");
					}
				} else if (first == 1) {
					say(player, n, "I was just stopping to say hello");
					npcsay(player, n, "Hello then");
				}

				break;
			case 1:
			case 2:
				npcsay(player, n, "Have you won yet?");
				if (!ifbankorheld(player, ItemId.FISHING_COMPETITION_PASS.id())) {
					//do not send over
					final int opts = multi(player, n, false,
						"No I need another competition pass",
						"No it takes preparation to win fishing competitions");
					if (opts == 0) {
						say(player, n, "I need another competition pass");
						npcsay(player, n, "Hmm its a good job they sent us spares",
							"there you go");
						give(player, ItemId.FISHING_COMPETITION_PASS.id(), 1);
					} else if (opts == 1) {
						say(player, n, "No it takes preparation to win fishing competitions");
						npcsay(player, n, "Maybe that's where we are going wrong when we try fishing");
					}
				} else {
					say(player, n, "No not yet");
				}

				break;
			case 3:
				npcsay(player, n, "Have you won yet?");
				say(player, n, "Yes I have");
				npcsay(player, n, "Well done, so where is the trophy?");
				if (player.getCarriedItems().hasCatalogID(ItemId.HEMENSTER_FISHING_TROPHY.id(), Optional.of(false))) {
					say(player, n, "I have it right here");
					mes("you give the trophy to the dwarf");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.HEMENSTER_FISHING_TROPHY.id()));
					npcsay(player, n, "Okay we will let you in now");
					player.sendQuestComplete(Quests.FISHING_CONTEST);
				} else {
					say(player, n, "I don't have it with me");
				}
				break;
			case -1:
				npcsay(player, n, "Welcome oh great fishing champion",
					"Feel free to pop by any time");
				break;
		}
	}

	@Override
	public void onUseLoc(final Player player, final GameObject obj, final Item item) {

		if (obj.getID() == SceneryId.VINE_RED_FISHING_CONTEST.id() && item.getCatalogId() == ItemId.SPADE.id()) { // teleport coords:
			// 567, 451
			mes("you dig in amoungst the vines");
			delay(3);
			mes("You find a red vine worm");
			delay(3);
			give(player, ItemId.RED_VINE_WORMS.id(), 1);
		}
		else if (obj.getID() == SceneryId.PIPE_FISHING_CONTEST.id() && item.getCatalogId() == ItemId.GARLIC.id()) {
			Npc sinister = ifnearvisnpc(player, NpcId.SINISTER_STRANGER.id(), 10);
			Npc bonzo = ifnearvisnpc(player , NpcId.BONZO.id(), 15);

			//stashing garlics in pipes should not check if other
			//garlics have been stashed
			mes("You stash the garlic in the pipe");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.GARLIC.id()));
			if (player.getCache().hasKey("paid_contest_fee") && !player.getCache().hasKey("garlic_activated")) {
				if (sinister != null) {
					npcsay(player, sinister,
						"Arrgh what is that ghastly smell",
						"I think I will move over here instead");
					sinister.teleport(570, 495);
				}
				if (bonzo != null) {
					npcsay(player, bonzo,
						"Hmm you'd better go and take the area by the pipes then");
				}
				player.message("Your fishing competition spot has been moved to beside the pipes");
			}
			if (!player.getCache().hasKey("garlic_activated")) {
				player.getCache().store("garlic_activated", true);
			}
		}
	}

	@Override
	public void onOpLoc(final Player player, final GameObject obj, final String command) {

		if (obj.getID() == SceneryId.GATE_WOODEN_FISHING_CONTEST_CLOSED.id()) {
			Npc bonzo = ifnearvisnpc(player, NpcId.BONZO.id(), 15);
			Npc morris = ifnearvisnpc(player, NpcId.MORRIS.id(), 15);
			if (player.getX() <= 564) {

				if (morris != null) {
					npcsay(player, morris, "competition pass please");
					if (player.getCarriedItems().hasCatalogID(ItemId.FISHING_COMPETITION_PASS.id(), Optional.of(false))) {
						mes("You show Morris your pass");
						delay(3);
						npcsay(player, morris, "Move on through");
						doGate(player, obj, SceneryId.GATE_WOODEN_FISHING_CONTEST_KARAMJA_GLIDER_OPEN.id());
					} else {
						ArrayList<String> menuOptions = new ArrayList<>();
						menuOptions.add("I don't have one of them");
						menuOptions.add("What do I need that for?");

						if (player.getQuestStage(getQuestId()) == -1 && player.getWorld().getServer().getConfig().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE) {
							menuOptions.add("I just want to fish around");
						}

						String[] choiceOptions = new String[menuOptions.size()];
						int m = multi(player, morris,
							menuOptions.toArray(choiceOptions));
						if (m == 1) {
							npcsay(player, morris,
								"This is the entrance to the Hementster fishing competition");
							npcsay(player, morris, "It's a high class competition");
							npcsay(player, morris, "Invitation only");
						} else if (m == 2 && player.getQuestStage(getQuestId()) == -1 && choiceOptions.length > 2) {
							npcsay(player, morris, "You are in luck champ",
								"there are currently no competitions",
								"feel free to use your usual fishing spot");
							doGate(player, obj, SceneryId.GATE_WOODEN_FISHING_CONTEST_KARAMJA_GLIDER_OPEN.id());
							if (!player.getCache().hasKey("usable_carp_spot")) {
								player.getCache().store("usable_carp_spot", true);
							}
						}
					}
				} else
					System.err.println("morris is null");
			} else if (player.getX() >= 565) {
				if (player.getQuestStage(getQuestId()) == 3) {
					doGate(player, obj, SceneryId.GATE_WOODEN_FISHING_CONTEST_KARAMJA_GLIDER_OPEN.id());
					return;
				}
				if (bonzo != null && player.getCache().hasKey("paid_contest_fee")) {
					npcsay(player, bonzo,
						"so you're calling it quits here for now?");
					int leaveMenu = multi(player, bonzo,
						"Yes I'll compete again another day",
						"Actually I'll go back and catch some more");
					if (leaveMenu == 0) {
						player.getCache().remove("paid_contest_fee");
						player.getCache().remove("contest_catches");
						doGate(player, obj, SceneryId.GATE_WOODEN_FISHING_CONTEST_KARAMJA_GLIDER_OPEN.id());
					} else if (leaveMenu == 1) {
						npcsay(player, bonzo, "Good luck");
					}
				} else {
					doGate(player, obj, SceneryId.GATE_WOODEN_FISHING_CONTEST_KARAMJA_GLIDER_OPEN.id());
					return;
				}
			}
		}
		Npc sinister = ifnearvisnpc(player, NpcId.SINISTER_STRANGER.id(), 10);
		Npc bonzo = ifnearvisnpc(player, NpcId.BONZO.id(), 15);
		if (obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_REGULAR_SPOT.id()) {
			// spot by tree (normal fish)
			if (player.getCarriedItems().hasCatalogID(ItemId.HEMENSTER_FISHING_TROPHY.id(), Optional.of(false))) {
				player.message("you have already won the fishing competition");
				return;
			} else if (bonzo != null && !player.getCache().hasKey("paid_contest_fee")) {
				bonzoDialogue(player, bonzo, false);
				return;
			}
			if (player.getQuestStage(getQuestId()) > 0 && !player.getCache().hasKey("garlic_activated")) {
				//cases: not enough level
				//no bait
				//else do catch
				if (player.getSkills().getLevel(Skill.FISHING.id()) < 10) {
					player.message("You need at least level 10 fishing to lure these fish");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.FISHING_ROD.id(), Optional.of(false))) {
					// probably non-kosher
					player.message("I don't have the equipment to catch a fish");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.FISHING_BAIT.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.RED_VINE_WORMS.id(), Optional.of(false))) {
					player.message("you have no bait to catch fish here");
				}
				// fishing using worm gives raw sardine
				else if (player.getCarriedItems().hasCatalogID(ItemId.RED_VINE_WORMS.id(), Optional.of(false))) {
					player.message("You catch a sardine");
					player.getCarriedItems().getInventory().add(new Item(ItemId.RAW_SARDINE.id()));
					player.getCarriedItems().remove(new Item(ItemId.RED_VINE_WORMS.id()));
					addCatchCache(player, ItemId.RAW_SARDINE.id());
				} else if (player.getCarriedItems().hasCatalogID(ItemId.FISHING_BAIT.id(), Optional.of(false))) {
					player.message("You catch some shrimps");
					player.getCarriedItems().getInventory().add(new Item(ItemId.RAW_SHRIMP.id()));
					player.getCarriedItems().remove(new Item(ItemId.FISHING_BAIT.id()));
					addCatchCache(player, ItemId.RAW_SHRIMP.id());
				}

				if (player.getCache().hasKey("contest_catches")) {
					int numCatches = player.getCache().getString("contest_catches").split("-").length;
					if (numCatches > 2 && bonzo != null) {
						bonzoTimesUpDialogue(player, bonzo);
					}
				}
			} else {
				if (sinister != null) {
					npcsay(player, sinister, "I think you will find that is my spot");
				}
			}
		}
		else if (obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_CARPS_SPOT.id()) {
			// spot by pipe (with carps)
			if (!player.getCache().hasKey("usable_carp_spot")) {
				// regular and post quest if !config().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE
				if (player.getCarriedItems().hasCatalogID(ItemId.HEMENSTER_FISHING_TROPHY.id(), Optional.of(false))) {
					player.message("you have already won the fishing competition");
					return;
				} else if (bonzo != null && !player.getCache().hasKey("paid_contest_fee")) {
					bonzoDialogue(player, bonzo, false);
					return;
				}
			}
			if ((player.getQuestStage(getQuestId()) > 0 && player.getCache().hasKey("garlic_activated"))
				|| (player.getQuestStage(getQuestId()) == -1 && player.getCache().hasKey("usable_carp_spot"))) {
				//cases: not enough level
				//no rod
				//no bait
				//else do catch
				if (player.getSkills().getLevel(Skill.FISHING.id()) < 10) {
					player.message("You need at least level 10 fishing to lure these fish");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.FISHING_ROD.id(), Optional.of(false))) {
					// probably non-kosher
					player.message("I don't have the equipment to catch a fish");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.FISHING_BAIT.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.RED_VINE_WORMS.id(), Optional.of(false))) {
					player.message("you have no bait to catch fish here");
				}
				// fishing using worm gives raw carp
				else if (player.getCarriedItems().hasCatalogID(ItemId.RED_VINE_WORMS.id(), Optional.of(false))) {
					player.message("You catch a giant carp");
					player.getCarriedItems().getInventory().add(new Item(ItemId.RAW_GIANT_CARP.id()));
					player.getCarriedItems().remove(new Item(ItemId.RED_VINE_WORMS.id()));
					if (player.getQuestStage(getQuestId()) > 0) {
						addCatchCache(player, ItemId.RAW_GIANT_CARP.id());
					}
				} else if (player.getCarriedItems().hasCatalogID(ItemId.FISHING_BAIT.id(), Optional.of(false))) {
					player.message("You catch a sardine");
					player.getCarriedItems().getInventory().add(new Item(ItemId.RAW_SARDINE.id()));
					player.getCarriedItems().remove(new Item(ItemId.FISHING_BAIT.id()));
					if (player.getQuestStage(getQuestId()) > 0) {
						addCatchCache(player, ItemId.RAW_SARDINE.id());
					}
				}

				if (player.getQuestStage(getQuestId()) > 0 && player.getCache().hasKey("contest_catches")) {
					int numCatches = player.getCache().getString("contest_catches").split("-").length;
					if (numCatches > 2 && bonzo != null) {
						bonzoTimesUpDialogue(player, bonzo);
					}
				}
			} else if (player.getQuestStage(getQuestId()) == -1) {
				player.message("you have already won the fishing competition");
			} else {
				npcsay(player, sinister, "I think you will find that is my spot");
				say(player, sinister, "Can't you go to another spot?");
				npcsay(player, sinister, "I like this place",
					"I like to savour the aroma coming from these pipes");
			}
		}
		else if (obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_DAVE_SPOT.id()) {
			Npc dave = ifnearvisnpc(player, NpcId.BIG_DAVE.id(), 10);
			if (dave != null) {
				bigDaveDialogue(player, dave);
			}
		}
		else if (obj.getID() == SceneryId.FISH_BAIT_FISHING_CONTEST_JOSHUA_SPOT.id()) {
			Npc joshua = ifnearvisnpc(player, NpcId.JOSHUA.id(), 10);
			if (joshua != null) {
				joshuaDialogue(player, joshua);
			}
		}
		else if (obj.getID() == SceneryId.STAIRS_STONE_WHITE_WOLF_PASS_DOWN.id()) {
			if (player.getQuestStage(getQuestId()) == -1) {
				player.message("You go down the stairs");
				if (obj.getX() == 426 && obj.getY() == 458) {
					player.teleport(426, 3294, false);
				} else {
					player.teleport(385, 3301, false);
				}
			} else {
				// from player's position
				Npc dwarf = ifnearvisnpc(player, NpcId.MOUNTAIN_DWARF.id(), 25);
				//final Npc dwarf = getWorld().getNpc(355, 375, 395, 445,
				//		475);
				if (dwarf != null) {
					if (player.getQuestStage(this) == 0) {
						npcsay(player, dwarf, "Hoi there, halt",
							"You can't come in here");
						int stairMenu = multi(player, dwarf, false, //do not send over
							"why not?",
							"Oh sorry I hadn't realised it was private",
							"I'm bigger than you let me by");
						if (stairMenu == 0) {
							npcsay(player, dwarf, "Why not?");
							goDownDialogue(player, dwarf);
						} else if (stairMenu == 1) {
							say(player, dwarf, "Oh sorry I hadn't realised it was private");
						} else if (stairMenu == 2) {
							say(player, dwarf, "I'm bigger than you",
								"Let me by");
							npcsay(player, dwarf, "Go away", "You're not going to bully your way in here");
						}
					} else {
						mountainDwarfDialogue(player, dwarf);
					}
				}
			}
		}
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.MOUNTAIN_DWARF.id()) {
			mountainDwarfDialogue(player, n);
		}
		else if (n.getID() == NpcId.BONZO.id()) {
			bonzoDialogue(player, n, true);
		}
		else if (n.getID() == NpcId.SINISTER_STRANGER.id()) {
			sinisterDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.GRANDPA_JACK.id()) {
			grandpaJackDialogue(player, n);
		}
	}

	private void sinisterDialogue(final Player player, final Npc n, final int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 1:
				case 2:
				case 3:
				case -1:
					npcsay(player, n, "..");
					//do not send over
					final int first = multi(player, n, false, "..?",
						"Who are you?", "so you like fishing?");
					if (first == 0) {
						say(player, n, "..?");
						npcsay(player, n, " ...");
					} else if (first == 1) {
						say(player, n, "Who are you?");
						npcsay(player, n, "My name is Vlad",
							"I come from far avay, vere the sun is not so bright");
						final int second = multi(player, n,
							"You're a vampire aren't you?",
							"Is it nice there?");
						if (second == 0) {
							sinisterDialogue(player, n, SINISTER.VAMPIRE);
						} else if (second == 1) {
							npcsay(player, n, "It is vonderful",
								"the vomen are beautiful",
								"and the nights are long");
							//do not send over
							final int third = multi(player, n, false,
								"You're a vampire aren't you?",
								"So you like fishing?",
								"Well good luck with the fishing");
							if (third == 0) {
								say(player, n, "You're a vampire aren't you?");
								sinisterDialogue(player, n, SINISTER.VAMPIRE);
							} else if (third == 1) {
								say(player, n, "So you like fishing");
								sinisterDialogue(player, n, SINISTER.FISHING);
							} else if (third == 2) {
								say(player, n, "Well good luck with the fishing");
								npcsay(player, n, "Luck has nothing to do vith it",
									"It is all in the technique");
							}
						}

					} else if (first == 2) {
						say(player, n, "So you like fishing");
						sinisterDialogue(player, n, SINISTER.FISHING);
					}
					break;
			}
		}
		switch (cID) {
			case SINISTER.VAMPIRE:
				npcsay(player, n, "Just because I can't stand the smell of garlic",
					"and I don't like bright sunlight",
					"Doesn't necessarily mean I'm a vampire");
				break;
			case SINISTER.FISHING:
				npcsay(player, n, "My doctor told be to take up a velaxing hobby",
					"vhen I am stressed I tend to get a little..", "..thirsty");
				//do not send over
				final int third = multi(player, n, false,
					"You're a vampire aren't you?",
					"If you get thirsty you should drink something",
					"Well good look with the fishing");
				if (third == 0) {
					say(player, n, "You're a vampire aren't you?");
					sinisterDialogue(player, n, SINISTER.VAMPIRE);
				} else if (third == 1) {
					say(player, n, "If you get thirsty", "You should drink something");
					npcsay(player, n, "I think I may do that soon");
				} else if (third == 2) {
					say(player, n, "Well good luck with the fishing");
					npcsay(player, n, "Luck has nothing to do vith it",
						"It is all in the technique");
				}
				break;
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item i) {
		//garlic on sinister stranger
		return n.getID() == NpcId.SINISTER_STRANGER.id() && i.getCatalogId() == ItemId.GARLIC.id();
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item i) {
		if (n.getID() == NpcId.SINISTER_STRANGER.id() && i.getCatalogId() == ItemId.GARLIC.id()) {
			npcsay(player, n, "urrggh get zat horrible ving avay from me",
				"How do people like to eat that stuff",
				"I can't stand even to be near it for ten seconds");
		}
	}

	class SINISTER {
		private static final int FISHING = 0;
		private static final int VAMPIRE = 1;
	}
}
