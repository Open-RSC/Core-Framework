package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.RuneScript.*;

public class DruidicRitual implements QuestInterface, TalkNpcTrigger,
	OpBoundTrigger,
	UseLocTrigger {

	@Override
	public int getQuestId() {
		return Quests.DRUIDIC_RITUAL;
	}

	@Override
	public String getQuestName() {
		return "Druidic ritual (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.DRUIDIC_RITUAL.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the druidic ritual quest");
		final QuestReward reward = Quest.DRUIDIC_RITUAL.reward();
		Functions.incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			Functions.incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.KAQEMEEX.id() || npc.getID() == NpcId.SANFEW.id();
	}

	private void kaqemeexDialogue(Player player, Npc npc, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay("What brings you to our holy Monument");
					int first = multi("Who are you?",
						"I'm in search of a quest", "Did you build this?");
					if (first == 0) {
						npcsay("We are the druids of Guthix",
							"We worship our God at our famous stone circles");
						int third = multi(false, //do not send over
							"What about the stone circle full of dark wizards?",
							"So whats so good about Guthix",
							"Well I'll be on my way now");
						if (third == 0) {
							say("What about the stone circle full of dark wizards?");
							kaqemeexDialogue(player, npc, kaqemeex.STONE_CIRCLE);
						} else if (third == 1) {
							say("So what's so good abou Guthix?");
							npcsay("Guthix is very important to this world",
								"He is the God of nature and balance",
								"He is in the trees and he is in the rock");
						} else if (third == 2) {
							say("Well I'll be on my way now");
							kaqemeexDialogue(player, npc, kaqemeex.ON_MY_WAY_NOW);
						}
					} else if (first == 1) {
						kaqemeexDialogue(player, npc, kaqemeex.SEARCH_OF_QUEST);
					} else if (first == 2) {
						npcsay("Well I didn't build it personally",
							"Our forebearers did",
							"The first druids of Guthix built many stone circles 800 years ago",
							"Only 2 that we know of remain",
							"And this is the only 1 we can use any more");
						int second = multi("What about the stone circle full of dark wizards?",
							"I'm in search of a quest",
							"Well I'll be on my way now");
						if (second == 0) {
							kaqemeexDialogue(player, npc, kaqemeex.STONE_CIRCLE);
						} else if (second == 1) {
							kaqemeexDialogue(player, npc, kaqemeex.SEARCH_OF_QUEST);
						} else if (second == 2) {
							kaqemeexDialogue(player, npc, kaqemeex.ON_MY_WAY_NOW);
						}
					}
					break;
				case 1:
				case 2:
					say("Hello again");
					npcsay("You need to speak to Sanfew in the village south of here",
						"To continue with your quest");
					break;
				case 3:
					npcsay("I've heard you were very helpful to Sanfew");
					npcsay("I will teach you the herblaw you need to know now");
					player.sendQuestComplete(Quests.DRUIDIC_RITUAL);
					break;
				case -1:
					npcsay("Hello how is the herblaw going?");
					int endMenu = multi("Very well thankyou", "I need more practice at it");
					if (endMenu == 0) {
						// Herblaw cape
						if (Functions.config().WANT_CUSTOM_SPRITES && player.getSkills().getMaxStat(Skill.HERBLAW.id()) >= 99) {
							npcsay("Very well indeed!",
								"It looks like you have mastered Herblaw!",
								"For this achievement, I'd be willing to sell you a Herblaw cape",
								"Like the one I'm wearing",
								"I can sell you one for 99,000 coins");
							int choice = 1;
							// Loop until they either agree to buy the cape, decline, or leave
							while (choice == 1) {
								npcsay("Would you like one?");
								choice = multi("Definitely!", "What does it do?", "No thankyou");
								if (choice == 1) {
									npcsay("This cape is blessed by Guthix",
										"It gives you a chance to save ingredients while mixing potions",
										"Comes in quite handy");
								}
							}
							if (choice == 0) {
								if (ifheld(ItemId.COINS.id(), 99000)) {
									mes("You give Kaqemeex the gold");
									remove(ItemId.COINS.id(), 99000);
									delay(3);
									mes("He gives you a Herblaw cape in return");
									give(ItemId.HERBLAW_CAPE.id(), 1);
									delay(3);
									npcsay("May Guthix guide you");
								} else {
									npcsay("I'm sorry friend, it looks like you don't have enough coins on you.");
								}
							}

						}
					} else if (endMenu == 1) {
						// NOTHING
					}
					break;
			}
		}
		switch (cID) {
			case kaqemeex.SEARCH_OF_QUEST:
				npcsay("I think I may have a worthwhile quest for you actually",
					"I don't know if you are familair withe the stone circle south of Varrock");
				kaqemeexDialogue(player, npc, kaqemeex.STONE_CIRCLE);
				break;
			case kaqemeex.STONE_CIRCLE:
				npcsay("That used to be our stone circle",
					"Unfortunatley  many years ago dark wizards cast a wicked spell on it",
					"Corrupting it for their own evil purposes",
					"and making it useless for us",
					"We need someone who will go on a quest for us",
					"to help us purify the circle of Varrock");
				int four = multi(false, //do not send over
					"Ok, I will try and help",
					"No that doesn't sound very interesting",
					"So is there anything in this for me?");
				if (four == 0) {
					say("Ok I will try and help");
					npcsay("Ok go and speak to our Elder druid, Sanfew",
						"He lives in our village to the south of here",
						"He knows better what we need than I");
					player.updateQuestStage(getQuestId(), 1);
				} else if (four == 1) {
					say("No that doesn't sound very interesting");
					npcsay("Well suit yourself, we'll have to find someone else");
				} else if (four == 2) {
					say("So is there anything in this for me?");
					npcsay("We are skilled in the art of herblaw",
						"We can teach you some of our skill if you complete your quest");
					int five = multi(false, //do not send over
						"Ok, I will try and help",
						"No that doesn't sound very interesting");
					if (five == 0) {
						say("Ok I will try and help");
						npcsay("Ok go and speak to our Elder druid, Sanfew");
						player.updateQuestStage(getQuestId(), 1);
					} else if (five == 1) {
						say("No that doesn't sound very interesting");
						npcsay("Well suit yourself, we'll have to find someone else");
					}
				}
				break;
			case kaqemeex.ON_MY_WAY_NOW:
				npcsay("good bye");
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.KAQEMEEX.id()) {
			kaqemeexDialogue(player, npc, -1);
		}
		else if (npc.getID() == NpcId.SANFEW.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay("What can I do for you young 'un?");
					int first = multi("I've heard you druids might be able to teach me herblaw",
						"Actually I don't need to speak to you");
					if (first == 0) {
						npcsay("You should go to speak to kaqemeex",
							"He is probably our best teacher of herblaw at the moment",
							"I believe he is at our stone circle to the north of here");
					} else if (first == 1) {
						mes("Sanfew grunts");
						delay(3);
					}
					break;
				case 1:
					npcsay("What can I do for you young 'un?");
					first = multi("I've been sent to help purify the varrock stone circle",
						"Actually I don't need to speak to you");
					if (first == 0) {
						npcsay("Well what I'm struggling with",
							"Is the meats I needed for the sacrifice to Guthix",
							"I need the raw meat from 4 different animals",
							"Which all need to be dipped in the cauldron of thunder");
						int second = multi(false, //do not send over
							"Where can I find this cauldron?",
							"Ok I'll do that then");
						if (second == 0) {
							say("Where can I find this cauldron");
							npcsay("It is in the mysterious underground halls",
								"which are somewhere in the woods to the south of here");
							player.updateQuestStage(getQuestId(), 2);
						} else if (second == 1) {
							say("Ok I'll do that then");
							player.updateQuestStage(getQuestId(), 2);
						}
					} else if (first == 1) {
						mes("Sanfew grunts");
						delay(3);
					}
					break;
				case 2:
					npcsay("Have you got what I need yet?");
					if (player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_CHICKEN_MEAT.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_BEAR_MEAT.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_RAT_MEAT.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_BEEF.id(), Optional.of(false))) {
						say("Yes I have everything");
						mes("You give the meats to Sanfew");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_CHICKEN_MEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_BEAR_MEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_RAT_MEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_BEEF.id()));
						npcsay("thank you, that has brought us much closer to reclaiming our stone circle",
							"Now go and talk to kaqemeex",
							"He will show you what you need to know about herblaw");
						player.updateQuestStage(getQuestId(), 3);
					} else {
						say("no not yet");
						int menu = multi("What was I meant to be doing again?",
							"I'll get on with it");
						if (menu == 0) {
							npcsay("I need the raw meat from 4 different animals",
								"Which all need to be dipped in the cauldron of thunder");
							int secondMenu = multi(false, //do not send over
								"Where can I find this cauldron?",
								"Ok I'll do that then");
							if (secondMenu == 0) {
								say("Where can I find this cauldron");
								npcsay("It is in the mysterious underground halls",
									"which are somewhere in the woods to the south of here");
							} else if (secondMenu == 1) {
								say("Ok I'll do that then");
							}
						} else if (menu == 1) {
							// NOTHING
						}
					}
					break;
				case 3:
				case -1:
					npcsay("What can I do for you young 'un?");
					int finalMenu = multi("Have you any more work for me, to help reclaim the circle?",
						"Actually I don't need to speak to you");
					if (finalMenu == 0) {
						npcsay("Not at the moment",
							"I need to make some more preparations myself now");
					} else if (finalMenu == 1) {
						mes("Sanfew grunts");
						delay(3);
					}
					break;
			}
		}

	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == 63 && obj.getY() == 3332) || (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332));
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 63 && obj.getY() == 3332) {
			Npc suit = player.getWorld().getNpc(NpcId.SUIT_OF_ARMOUR.id(), 374, 374, 3330, 3334);
			if (suit != null && !(player.getX() <= 373)) {
				player.message("Suddenly the suit of armour comes to life!");
				suit.setChasing(player);
			} else {
				Functions.doDoor(obj, player);
			}
		}
		else if (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332)) {
			Functions.doDoor(obj, player);
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 236 &&
				(item.getCatalogId() == ItemId.RAW_CHICKEN.id() || item.getCatalogId() == ItemId.RAW_RAT_MEAT.id()
				|| item.getCatalogId() == ItemId.RAW_BEEF.id() || item.getCatalogId() == ItemId.RAW_BEAR_MEAT.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 236 &&
				(item.getCatalogId() == ItemId.RAW_CHICKEN.id() || item.getCatalogId() == ItemId.RAW_RAT_MEAT.id()
				|| item.getCatalogId() == ItemId.RAW_BEEF.id() || item.getCatalogId() == ItemId.RAW_BEAR_MEAT.id())) {
			if (player.getQuestStage(this) <= 0) {
				player.playerServerMessage(MessageType.QUEST,"Nothing interesting happens");
				return;
			}

			if (item.getCatalogId() == ItemId.RAW_CHICKEN.id()) {
				mes("You dip the chicken in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_CHICKEN.id()));

				give(ItemId.ENCHANTED_CHICKEN_MEAT.id(), 1);
			}
			else if (item.getCatalogId() == ItemId.RAW_BEAR_MEAT.id()) {
				mes("You dip the bear meat in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_BEAR_MEAT.id()));

				give(ItemId.ENCHANTED_BEAR_MEAT.id(), 1);
			}
			else if (item.getCatalogId() == ItemId.RAW_RAT_MEAT.id()) {
				mes("You dip the rat meat in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_RAT_MEAT.id()));

				give(ItemId.ENCHANTED_RAT_MEAT.id(), 1);
			}
			else if (item.getCatalogId() == ItemId.RAW_BEEF.id()) {
				mes("You dip the beef in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_BEEF.id()));

				give(ItemId.ENCHANTED_BEEF.id(), 1);
			}
		}
	}

	class kaqemeex {
		public static final int STONE_CIRCLE = 0;
		public static final int ON_MY_WAY_NOW = 1;
		public static final int SEARCH_OF_QUEST = 2;
	}
}
