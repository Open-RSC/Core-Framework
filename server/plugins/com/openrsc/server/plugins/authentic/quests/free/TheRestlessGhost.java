package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class TheRestlessGhost implements QuestInterface, TakeObjTrigger,
	TalkNpcTrigger, OpLocTrigger,
	UseLocTrigger {

	private static final int GHOST_COFFIN_OPEN = 40;
	private static final int GHOST_COFFIN_CLOSED = 39;

	@Override
	public int getQuestId() {
		return Quests.THE_RESTLESS_GHOST;
	}

	@Override
	public String getQuestName() {
		return "The restless ghost";
	}

	@Override
	public int getQuestPoints() {
		return Quest.THE_RESTLESS_GHOST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		if (player.getConfig().INFLUENCE_INSTEAD_QP) {
			player.message("You have completed the ghost quest");
		} else {
			player.message("You have completed the restless ghost quest");
		}
		final QuestReward reward = Quest.THE_RESTLESS_GHOST.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void ghostDialogue(Player player, Npc n, int cID) {
		if (n.getID() == NpcId.GHOST_RESTLESS.id()) {
			if (player.getQuestStage(this) == -1) {
				player.message("The ghost doesn't appear interested in talking");
				return;
			}
			if (cID == -1) {
				if (player.getQuestStage(this) == 3) {
					say(player, n, "Hello ghost, how are you?");
					npcsay(player, n, "How are you doing finding my skull?");
					if (!player.getCarriedItems().hasCatalogID(ItemId.QUEST_SKULL.id(), Optional.of(false))) {
						say(player, n, "Sorry, I can't find it at the moment");
						npcsay(player,
							n,
							"Ah well keep on looking",
							"I'm pretty sure it's somewhere in the tower south west from here",
							"There's a lot of levels to the tower, though",
							"I suppose it might take a little while to find");
						// kosher: this condition made player need to restart skull process incl. skeleton fight
						player.getCache().remove("tried_grab_skull");
					} else {
						say(player, n, "I have found it");
						npcsay(player,
							n,
							"Hurrah now I can stop being a ghost",
							"You just need to put it in my coffin over there",
							"And I will be free");
					}
					return;
				}
				if (player.getQuestStage(this) == 0
					|| !player.getCarriedItems().getEquipment().hasEquipped(ItemId.AMULET_OF_GHOSTSPEAK.id())) {
					say(player, n, "Hello ghost, how are you?");
					npcsay(player, n, "Wooo wooo wooooo");
					int choice = multi(player, n,
						"Sorry I don't speak ghost",
						"Ooh that's interesting",
						"Any hints where I can find some treasure?");
					if (choice == 0) {
						ghostDialogue(player, n, Ghost.DONTSPEAK);
					} else if (choice == 1) {
						npcsay(player, n, "Woo wooo", "Woooooooooooooooooo");
						int choice2 = multi(player, n,
							"Did he really?", "Yeah that's what I thought");
						if (choice2 == 0) {
							npcsay(player, n, "Woo");
							int choice3 = multi(player, n,
								"My brother had exactly the same problem",
								"Goodbye. Thanks for the chat");
							if (choice3 == 0) {
								npcsay(player, n, "Woo Wooooo",
									"Wooooo Woo woo woo");
								int choice4 = multi(
									player,
									n,
									"Goodbye. Thanks for the chat",
									"You'll have to give me the recipe some time");
								if (choice4 == 0) {
									ghostDialogue(player, n, Ghost.GOODBYE);
								} else if (choice4 == 1) {
									npcsay(player, n, "Wooooooo woo woooooooo");
									int choice6 = multi(player, n,
										"Goodbye. Thanks for the chat",
										"Hmm I'm not sure about that");
									if (choice6 == 0) {
										ghostDialogue(player, n, Ghost.GOODBYE);
									} else if (choice6 == 1) {
										ghostDialogue(player, n, Ghost.NOTSURE);
									}
								}
							} else if (choice3 == 1) {
								npcsay(player, n, "Wooo wooo",
									"Wooo woooooooooooooooo");
								int choice7 = multi(player, n,
									"Goodbye. Thanks for the chat",
									"Hmm I'm not sure about that");
								if (choice7 == 0) {
									ghostDialogue(player, n, Ghost.GOODBYE);
								} else if (choice7 == 1) {
									ghostDialogue(player, n, Ghost.NOTSURE);
								}
							}
						} else if (choice2 == 1) {
							npcsay(player, n, "Wooo woooooooooooooo");
							int choice5 = multi(player, n,
								"Goodbye. Thanks for the chat",
								"Hmm I'm not sure about that");
							if (choice5 == 0) {
								ghostDialogue(player, n, Ghost.GOODBYE);
							} else if (choice5 == 1) {
								ghostDialogue(player, n, Ghost.NOTSURE);
							}
						}
					} else if (choice == 2) {
						npcsay(player, n, "Wooooooo woo!");
						int choice8 = multi(player, n, false, //do not send over
							"Sorry I don't speak ghost",
							"Thank you. You've been very helpful");
						if (choice8 == 0) {
							say(player, n, "Sorry I don't speak ghost");
							ghostDialogue(player, n, Ghost.DONTSPEAK);
						} else if (choice8 == 1) {
							say(player, n, "Thank you. You've been very helpfull");
							npcsay(player, n, "Wooooooo");
						}
					}
				} else {
					say(player, n, "Hello ghost, how are you?");
					npcsay(player, n, "Not very good actually");
					say(player, n, "What's the problem then?");
					npcsay(player, n, "Did you just understand what I said?");
					int choice = multi(player, n, false, //do not send over
						"Yep, now tell me what the problem is",
						"No, you sound like you're speaking nonsense to me",
						"Wow, this amulet works");
					if (choice == 0) {
						say(player, n, "Yep, now tell me what the problem is");
						npcsay(player, n,
							"Wow this is incredible, I didn't expect any one to understand me again");
						say(player, n, "Yes, yes I can understand you",
							"But have you any idea why you're doomed to be a ghost?");
						npcsay(player, n, "I'm not sure");
						say(
							player,
							n,
							"I've been told a certain task may need to be completed",
							"So you can rest in peace");
						npcsay(player, n, "I should think it is probably because ",
							"A warlock has come along and stolen my skull",
							"If you look inside my coffin there",
							"you'll find my corpse without a head on it");
						say(player, n,
							"Do you know where this warlock might be now?");
						npcsay(player,
							n,
							"I think it was one of the warlocks who lives in the big tower",
							"In the sea southwest from here");
						say(player, n,
							"Ok I will try and get the skull back for you, so you can rest in peace.");
						npcsay(player,
							n,
							"Ooh thank you. That would be such a great relief",
							"It is so dull being a ghost");
						player.updateQuestStage(Quests.THE_RESTLESS_GHOST, 3);
					} else if (choice == 1) {
						say(player, n, "No");
						npcsay(player, n,
							"Oh that's a pity. You got my hopes up there");
						say(player, n, "Yeah, it is pity. Sorry");
						npcsay(player, n, "Hang on a second. You can understand me");
						int choice2 = multi(player, n, "No I can't", "Yep clever aren't I");
						if (choice2 == 0) {
							npcsay(player, n,
								"I don't know, the first person I can speak to in ages is a moron");
						} else if (choice2 == 1) {
							npcsay(player, n, "I'm impressed",
								"You must be very powerfull",
								"I don't suppose you can stop me being a ghost?");
							int choice3 = multi(player, n, false, //do not send over
								"Yes, Ok. Do you know why you're a ghost?",
								"No, you're scary");
							if (choice3 == 0) {
								say(player, n, "Yes, Ok do you know why you're a ghost?");
								ghostDialogue(player, n, Ghost.WHY);
							} else if (choice3 == 1) {
								say(player, n, "No, you're scary");
								ghostDialogue(player, n, Ghost.SCARY);
							}
						}
					} else if (choice == 2) {
						say(player, n, "Wow, this amulet works");
						npcsay(player,
							n,
							"Oh its your amulet that's doing it. I did wonder",
							"I don't suppose you can help me? I don't like being a ghost");
						int choice3 = multi(player, n, false, //do not send over
							"Yes, Ok. Do you know why you're a ghost?",
							"No, you're scary");
						if (choice3 == 0) {
							say(player, n, "Yes, Ok do you know why you're a ghost?");
							ghostDialogue(player, n, Ghost.WHY);
						} else if (choice3 == 1) {
							say(player, n, "No, you're scary");
							ghostDialogue(player, n, Ghost.SCARY);
						}
					}
				}
				return;
			}
			switch (cID) {
				case Ghost.DONTSPEAK:
					npcsay(player, n, "Woo woo?");
					say(player, n, "Nope still don't understand you");
					npcsay(player, n, "Woooooooo");
					say(player, n, "Never mind");
					break;
				case Ghost.GOODBYE:
					npcsay(player, n, "Wooo wooo");
					break;
				case Ghost.NOTSURE:
					npcsay(player, n, "Wooo woo");
					say(player, n, "Well if you insist");
					npcsay(player, n, "Wooooooooo");
					say(player, n, "Ah well, better be off now");
					npcsay(player, n, "Woo");
					say(player, n, "Bye");
					break;
				case Ghost.WHY:
					npcsay(player, n,
						"No, I just know I can't do anything much like this");
					say(
						player,
						n,
						"I've been told a certain task may need to be completed",
						"So you can rest in peace");
					npcsay(player, n, "I should think it is probably because ",
						"a warlock has come along and stolen my skull",
						"If you look inside my coffin there",
						"you'll find my corpse without a head on it");
					say(player, n, "Do you know where this warlock might be now?");
					npcsay(player,
						n,
						"I think it was one of the warlocks who lives in the big tower",
						"In the sea southwest from here");
					say(player, n,
						"Ok I will try and get the skull back for you, so you can rest in peace.");
					npcsay(player, n,
						"Ooh thank you. That would be such a great relief",
						"It is so dull being a ghost");
					player.updateQuestStage(Quests.THE_RESTLESS_GHOST, 3);
					break;
				case Ghost.SCARY:
					break;
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.GHOST_RESTLESS.id()) {
			ghostDialogue(player, n, -1);
		}
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == GHOST_COFFIN_OPEN || obj.getID() == GHOST_COFFIN_CLOSED) {
			if (command.equalsIgnoreCase("open")) {
				openGenericObject(obj, player, GHOST_COFFIN_OPEN, "You open the coffin");
			} else if (command.equalsIgnoreCase("close")) {
				closeGenericObject(obj, player, GHOST_COFFIN_CLOSED, "You close the coffin");
			} else {
				if (obj.getX() == 975 && player.getConfig().DEATH_ISLAND) {
					player.message("There's a small pillow and a book inside");
					delay(3);
					player.message("The book's title is \"Basics of Caring for Livestock\"");
					return;
				}
				if (obj.getX() == 116 && config().MICE_TO_MEET_YOU_EVENT) {
					player.message("There's a small pillow and a book inside");
					delay(3);
					player.message("The book's title is \"Basics of Saving and Investing\"");
					return;
				}
				if (player.getQuestStage(this) > 0) {
					player.message("There's a skeleton without a skull in here");
				} else if (player.getQuestStage(this) == -1) {
					player.message("Theres a nice and complete skeleton in here!");
				} else {
					player.message("You search the coffin and find some human remains");
				}
			}
		}
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == GHOST_COFFIN_OPEN && player.getQuestStage(this) == 3
			&& item.getCatalogId() == ItemId.QUEST_SKULL.id()) {
			mes("You put the skull in the coffin");
			delay(3);
			player.getCarriedItems().remove(new Item(ItemId.QUEST_SKULL.id()));
			//on completion cache key no longer needed
			player.getCache().remove("tried_grab_skull");
			Npc npc = ifnearvisnpc(player, NpcId.GHOST_RESTLESS.id(), 8);
			if (npc != null) {
				npc.remove();
			}
			mes("The ghost has vanished");
			delay(3);
			mes("You think you hear a faint voice in the air");
			delay(3);
			mes("Thank you");
			delay(3);
			player.sendQuestComplete(Quests.THE_RESTLESS_GHOST);
			return;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GHOST_RESTLESS.id();
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return item.getCatalogId() == ItemId.QUEST_SKULL.id() && obj.getID() == GHOST_COFFIN_OPEN;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == GHOST_COFFIN_OPEN || obj.getID() == GHOST_COFFIN_CLOSED;
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.QUEST_SKULL.id();
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.QUEST_SKULL.id()) {
			// spawn-place
			if (i.getX() == 218 && i.getY() == 3521) {
				if (player.getQuestStage(Quests.THE_RESTLESS_GHOST) != 3) {
					say(player, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
					return;
				} else if (!player.getCache().hasKey("tried_grab_skull")) {
					player.getCache().store("tried_grab_skull", true);
					player.getWorld().unregisterItem(i);
					give(player, ItemId.QUEST_SKULL.id(), 1);
					Npc skeleton = ifnearvisnpc(player, NpcId.SKELETON_RESTLESS.id(), 10);
					if (skeleton == null) {
						//spawn skeleton and give message
						player.message("Out of nowhere a skeleton appears");
						skeleton = addnpc(player.getWorld(), NpcId.SKELETON_RESTLESS.id(), 216, 3520);
						skeleton.setShouldRespawn(false);
						skeleton.setChasing(player);
					} else {
						skeleton.setChasing(player);
					}

				}
				// allow if player had at least one time tried grab skull
				else {
					player.getWorld().unregisterItem(i);
					give(player, ItemId.QUEST_SKULL.id(), 1);
				}

			} else {
				say(player, null, "That skull is scary", "I've got no reason to take it", "I think I'll leave it alone");
				return;
			}
		}
	}

	class Ghost {
		public static final int DONTSPEAK = 0;
		public static final int GOODBYE = 1;
		public static final int NOTSURE = 2;
		public static final int WHY = 3;
		public static final int SCARY = 4;
	}
}
