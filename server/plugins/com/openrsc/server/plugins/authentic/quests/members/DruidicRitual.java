package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

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
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KAQEMEEX.id() || n.getID() == NpcId.SANFEW.id();
	}

	private void kaqemeexDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "What brings you to our holy Monument");
					int first = multi(player, n, "Who are you?",
						"I'm in search of a quest", "Did you build this?");
					if (first == 0) {
						npcsay(player, n, "We are the druids of Guthix",
							"We worship our God at our famous stone circles");
						int third = multi(
							player, n, false, //do not send over
							"What about the stone circle full of dark wizards?",
							"So whats so good about Guthix",
							"Well I'll be on my way now");
						if (third == 0) {
							say(player, n, "What about the stone circle full of dark wizards?");
							kaqemeexDialogue(player, n, kaqemeex.STONE_CIRCLE);
						} else if (third == 1) {
							say(player, n, "So what's so good abou Guthix?");
							npcsay(player, n, "Guthix is very important to this world",
								"He is the God of nature and balance",
								"He is in the trees and he is in the rock");
						} else if (third == 2) {
							say(player, n, "Well I'll be on my way now");
							kaqemeexDialogue(player, n, kaqemeex.ON_MY_WAY_NOW);
						}
					} else if (first == 1) {
						kaqemeexDialogue(player, n, kaqemeex.SEARCH_OF_QUEST);
					} else if (first == 2) {
						npcsay(player,
							n,
							"Well I didn't build it personally",
							"Our forebearers did",
							"The first druids of Guthix built many stone circles 800 years ago",
							"Only 2 that we know of remain",
							"And this is the only 1 we can use any more");
						int second = multi(
							player,
							n,
							"What about the stone circle full of dark wizards?",
							"I'm in search of a quest",
							"Well I'll be on my way now");
						if (second == 0) {
							kaqemeexDialogue(player, n, kaqemeex.STONE_CIRCLE);
						} else if (second == 1) {
							kaqemeexDialogue(player, n, kaqemeex.SEARCH_OF_QUEST);
						} else if (second == 2) {
							kaqemeexDialogue(player, n, kaqemeex.ON_MY_WAY_NOW);
						}
					}
					break;
				case 1:
				case 2:
					say(player, n, "Hello again");
					npcsay(player,
						n,
						"You need to speak to Sanfew in the village south of here",
						"To continue with your quest");
					break;
				case 3:
					npcsay(player, n, "I've heard you were very helpful to Sanfew");
					npcsay(player, n,
						"I will teach you the herblaw you need to know now");
					player.sendQuestComplete(Quests.DRUIDIC_RITUAL);
					break;
				case -1:
					npcsay(player, n, "Hello how is the herblaw going?");
					int endMenu = multi(player, n, "Very well thankyou", "I need more practice at it");
					if (endMenu == 0) {
						// NOTHING
					} else if (endMenu == 1) {
						// NOTHING
					}
					break;
			}
		}
		switch (cID) {
			case kaqemeex.SEARCH_OF_QUEST:
				npcsay(player,
					n,
					"I think I may have a worthwhile quest for you actually",
					"I don't know if you are familair withe the stone circle south of Varrock");
				kaqemeexDialogue(player, n, kaqemeex.STONE_CIRCLE);
				break;
			case kaqemeex.STONE_CIRCLE:
				npcsay(player,
					n,
					"That used to be our stone circle",
					"Unfortunatley  many years ago dark wizards cast a wicked spell on it",
					"Corrupting it for their own evil purposes",
					"and making it useless for us",
					"We need someone who will go on a quest for us",
					"to help us purify the circle of Varrock");
				int four = multi(player, n, false, //do not send over
					"Ok, I will try and help",
					"No that doesn't sound very interesting",
					"So is there anything in this for me?");
				if (four == 0) {
					say(player, n, "Ok I will try and help");
					npcsay(player, n, "Ok go and speak to our Elder druid, Sanfew",
						"He lives in our village to the south of here",
						"He knows better what we need than I");
					player.updateQuestStage(getQuestId(), 1);
				} else if (four == 1) {
					say(player, n, "No that doesn't sound very interesting");
					npcsay(player, n,
						"Well suit yourself, we'll have to find someone else");
				} else if (four == 2) {
					say(player, n, "So is there anything in this for me?");
					npcsay(player, n, "We are skilled in the art of herblaw",
						"We can teach you some of our skill if you complete your quest");
					int five = multi(player, n, false, //do not send over
						"Ok, I will try and help",
						"No that doesn't sound very interesting");
					if (five == 0) {
						say(player, n, "Ok I will try and help");
						npcsay(player, n, "Ok go and speak to our Elder druid, Sanfew");
						player.updateQuestStage(getQuestId(), 1);
					} else if (five == 1) {
						say(player, n, "No that doesn't sound very interesting");
						npcsay(player, n,
							"Well suit yourself, we'll have to find someone else");
					}
				}
				break;
			case kaqemeex.ON_MY_WAY_NOW:
				npcsay(player, n, "good bye");
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.KAQEMEEX.id()) {
			kaqemeexDialogue(player, n, -1);
		}
		else if (n.getID() == NpcId.SANFEW.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "What can I do for you young 'un?");
					int first = multi(
						player,
						n, "I've heard you druids might be able to teach me herblaw",
						"Actually I don't need to speak to you");
					if (first == 0) {
						npcsay(player,
							n,
							"You should go to speak to kaqemeex",
							"He is probably our best teacher of herblaw at the moment",
							"I believe he is at our stone circle to the north of here");
					} else if (first == 1) {
						mes("Sanfew grunts");
						delay(3);
					}
					break;
				case 1:
					npcsay(player, n, "What can I do for you young 'un?");
					first = multi(
						player,
						n, "I've been sent to help purify the varrock stone circle",
						"Actually I don't need to speak to you");
					if (first == 0) {
						npcsay(player,
							n,
							"Well what I'm struggling with",
							"Is the meats I needed for the sacrifice to Guthix",
							"I need the raw meat from 4 different animals",
							"Which all need to be dipped in the cauldron of thunder");
						int second = multi(player, n, false, //do not send over
							"Where can I find this cauldron?",
							"Ok I'll do that then");
						if (second == 0) {
							say(player, n, "Where can I find this cauldron");
							npcsay(player, n,
								"It is in the mysterious underground halls",
								"which are somewhere in the woods to the south of here");
							player.updateQuestStage(getQuestId(), 2);
						} else if (second == 1) {
							say(player, n, "Ok I'll do that then");
							player.updateQuestStage(getQuestId(), 2);
						}
					} else if (first == 1) {
						mes("Sanfew grunts");
						delay(3);
					}
					break;
				case 2:
					npcsay(player, n, "Have you got what I need yet?");
					if (player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_CHICKEN_MEAT.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_BEAR_MEAT.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_RAT_MEAT.id(), Optional.of(false))
						&& player.getCarriedItems().hasCatalogID(ItemId.ENCHANTED_BEEF.id(), Optional.of(false))) {
						say(player, n, "Yes I have everything");
						mes("You give the meats to Sanfew");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_CHICKEN_MEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_BEAR_MEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_RAT_MEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ENCHANTED_BEEF.id()));
						npcsay(player,
							n,
							"thank you, that has brought us much closer to reclaiming our stone circle",
							"Now go and talk to kaqemeex",
							"He will show you what you need to know about herblaw");
						player.updateQuestStage(getQuestId(), 3);
					} else {
						say(player, n, "no not yet");
						int menu = multi(player, n,
							"What was I meant to be doing again?",
							"I'll get on with it");
						if (menu == 0) {
							npcsay(player, n,
								"I need the raw meat from 4 different animals",
								"Which all need to be dipped in the cauldron of thunder");
							int secondMenu = multi(player, n, false, //do not send over
								"Where can I find this cauldron?",
								"Ok I'll do that then");
							if (secondMenu == 0) {
								say(player, n, "Where can I find this cauldron");
								npcsay(player,
									n,
									"It is in the mysterious underground halls",
									"which are somewhere in the woods to the south of here");
							} else if (secondMenu == 1) {
								say(player, n, "Ok I'll do that then");
							}
						} else if (menu == 1) {
							// NOTHING
						}
					}
					break;
				case 3:
				case -1:
					npcsay(player, n, "What can I do for you young 'un?");
					int finalMenu = multi(
						player,
						n,
						"Have you any more work for me, to help reclaim the circle?",
						"Actually I don't need to speak to you");
					if (finalMenu == 0) {
						npcsay(player, n, "Not at the moment",
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
				doDoor(obj, player);
			}
		}
		else if (obj.getID() == 64 && (obj.getY() == 3336 || obj.getY() == 3332)) {
			doDoor(obj, player);
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

				give(player, ItemId.ENCHANTED_CHICKEN_MEAT.id(), 1);
			}
			else if (item.getCatalogId() == ItemId.RAW_BEAR_MEAT.id()) {
				mes("You dip the bear meat in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_BEAR_MEAT.id()));

				give(player, ItemId.ENCHANTED_BEAR_MEAT.id(), 1);
			}
			else if (item.getCatalogId() == ItemId.RAW_RAT_MEAT.id()) {
				mes("You dip the rat meat in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_RAT_MEAT.id()));

				give(player, ItemId.ENCHANTED_RAT_MEAT.id(), 1);
			}
			else if (item.getCatalogId() == ItemId.RAW_BEEF.id()) {
				mes("You dip the beef in the cauldron");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.RAW_BEEF.id()));

				give(player, ItemId.ENCHANTED_BEEF.id(), 1);
			}
		}
	}

	class kaqemeex {
		public static final int STONE_CIRCLE = 0;
		public static final int ON_MY_WAY_NOW = 1;
		public static final int SEARCH_OF_QUEST = 2;
	}
}
