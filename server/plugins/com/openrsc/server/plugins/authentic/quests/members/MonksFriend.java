package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MonksFriend implements QuestInterface, TalkNpcTrigger,
	UseNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.MONKS_FRIEND;
	}

	@Override
	public String getQuestName() {
		return "Monk's friend (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.MONKS_FRIEND.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the monks friend quest");
		final QuestReward reward = Quest.MONKS_FRIEND.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BROTHER_CEDRIC.id() || n.getID() == NpcId.BROTHER_OMAD.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.BROTHER_OMAD.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hello there");
					npcsay(player, n, "...yawn...oh, hello...yawn..",
						"I'm sorry, I'm just so tired..",
						"I haven't slept in a week", "It's driving me mad");
					int firstMenu = multi(player, n, false, //do not send over
						"Why can't you sleep, what's wrong?",
						"sorry, i'm too busy to hear your problems");
					if (firstMenu == 0) {
						say(player, n, "Why can't you sleep, what's wrong?");
						npcsay(player, n, "It's the brother Androe's son",
							"with his constant waaaaaah..waaaaaaaaah",
							" Androe said it's natural, but it's just annoying");
						say(player, n, "I suppose that's what kids do");
						npcsay(player, n, "he was fine, up until last week",
							"thieves broke in",
							"They stole his favourite sleeping blanket",
							"now he won't rest until it's returned",
							"..and that means neither can I!");
						int secondMenu = multi(player, n, false, //do not send over
							"Can I help at all?",
							"I'm sorry to hear that, I hope you find his blanket");
						if (secondMenu == 0) {
							say(player, n, "can I help at all?");
							npcsay(player, n, "please do, we are peaceful men",
								" but you could recover the blanket from the thieves");
							say(player, n, "where are they?");
							npcsay(player, n,
								"they hide in a secret cave in the forest",
								"..it's hidden under a ring of stones",
								"please, bring back the blanket");
							player.updateQuestStage(getQuestId(), 1);
						} else if (secondMenu == 1) {
							say(player, n, "I'm sorry to hear that, I hope you find his blanket");
						}
					} else if (firstMenu == 1) {
						say(player, n, "Sorry, I'm too busy to hear your problems");
					}
					break;
				case 1:
					say(player, n, "Hello ");
					npcsay(player, n, "...yawn...oh, hello again...yawn..",
						"..please tell me you have the blanket");
					if (player.getCarriedItems().remove(new Item(ItemId.BLANKET.id())) != -1) {
						say(player, n,
							"Yes I returned it from the clutches of the evil thieves");
						npcsay(player, n, "Really, that's excellent, well done",
							"that should cheer up Androe's son",
							"and maybe I will be able to get some rest",
							"..yawn..i'm off to bed, farewell brave traveller.");
						mes("well done, you have completed part 1 of the monks friend quest");
						delay(3);
						player.updateQuestStage(getQuestId(), 2);
					} else {
						say(player, n, "I'm afraid not");
						npcsay(player, n, "I need some sleep");
					}
					break;
				case 2:
					say(player, n, "Hello, how are you");
					npcsay(player, n, "much better now i'm sleeping well",
						"now I can organise the party");
					say(player, n, "what party?");
					npcsay(player, n, "Androe's son's birthday party",
						"he's going to be one year old");
					say(player, n, "that's sweet");
					npcsay(player, n, "it's also a great excuse for a drink",
						"now we just need brother Cedric to return",
						"with the wine");
					int thirdMenu = multi(player, n, "who's brother Cedric?",
						"enjoy it, i'll see you soon");
					if (thirdMenu == 0) {
						npcsay(player, n, "Cedric lives here too",
							"we sent him out three days ago",
							"to collect wine, but he didn't return",
							"he most probably got drunk",
							"and lost in the forest",
							"I don't suppose you could look for him?",
							"then we can really party");
						int fourthMenu = multi(player, n,
							"I've no time for that, sorry",
							"where should I look?", "can I come?");
						if (fourthMenu == 0) {
							// NOTHING
						} else if (fourthMenu == 1) {
							npcsay(player, n, "oh, he won't be far",
								"probably out in the forest");
							player.updateQuestStage(getQuestId(), 3);
						} else if (fourthMenu == 2) {
							npcsay(player, n, "of course,",
								"but we need the wine first");
							player.updateQuestStage(getQuestId(), 3);
						}
					} else if (thirdMenu == 1) {
						// NOTHING
					}

					break;
				case 3:
				case 4:
				case 5:
					say(player, n, "Hi there!");
					npcsay(player, n, "oh my, I need a drink",
						"where is that brother Cedric");
					break;
				case 6:
					say(player, n, "Hi Omad, Brother Cedric is on his way");
					npcsay(player, n, "good,good,good", "now we can party");
					player.sendQuestComplete(Quests.MONKS_FRIEND);
					npcsay(player, n, "I have little to repay you with",
						"but please, take these runestones");
					give(player, ItemId.LAW_RUNE.id(), 8);
					break;
				case -1:
					npcsay(player, n, "Dum dee do la la", "Hiccup", "That's good wine");
					break;
			}
		}
		else if (n.getID() == NpcId.BROTHER_CEDRIC.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(player, n, "Hello");
					npcsay(player, n, "honey,money,woman,wine..");
					say(player, n, "Are you ok?");
					npcsay(player, n, " yesshh...hic up...beautiful..");
					say(player, n, "take care old monk");
					npcsay(player, n, "la..di..da..hic..up..");
					mes("The monk has had too much to drink");
					delay(3);
					break;
				case 3:
					say(player, n, "Brother Cedric are you okay?");
					npcsay(player, n, "yeesshhh, i'm very, very....",
						"..drunk..hic..up..");
					say(player, n, "brother Omad needs the wine..",
						"..for the party");
					npcsay(player, n, " oh dear, oh dear ",
						"I knew I had to do something",
						"pleashhh, find me some water",
						"once i'm sober i'll help you..",
						"..take the wine back.");
					player.updateQuestStage(getQuestId(), 4);
					break;
				case 4:
					say(player, n, "Are you okay?");
					npcsay(player, n, "...hic up..oh my head..", "..I need some water.");
					break;
				case 5:
					say(player, n, "Hello Cedric");
					npcsay(player, n, "want to help me fix the cart?");
					int cartMenu = multi(player, n, "Yes i'd be happy to",
						"No, not really");
					if (cartMenu == 0) {
						npcsay(player, n, "i need some wood");
						if (player.getCarriedItems().remove(new Item(ItemId.LOGS.id())) != -1) {
							say(player, n, "here you go..", "I've got some wood");
							npcsay(player, n, "well done, now i'll fix this cart",
								"you head back to Brother Omad",
								"Tell him i'm on my way", "I won't be long");
							player.updateQuestStage(getQuestId(), 6);
						}
					} else if (cartMenu == 1) {
						// NOTHING
					}
					break;
				case 6:
					say(player, n, "Hello Cedric");
					npcsay(player, n, "hi, i'm almost done here",
						"go tell Omad that I..", "..won't be long");
					break;
				case -1:
					npcsay(player, n, "Brother Oman sends you his thanks",
						"He won't be in a fit state to thank you in person any more");
					break;
			}
		}

	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.BROTHER_CEDRIC.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.BROTHER_CEDRIC.id()) {
			if (player.getQuestStage(getQuestId()) == 4 && item.getCatalogId() == ItemId.BUCKET_OF_WATER.id()) {
				thinkbubble(item);
				delay(2);
				player.getCarriedItems().remove(item);
				say(player, npc, "Cedric, here, drink some water");
				npcsay(player, npc, "oh yes, my head's starting to spin",
					"gulp...gulp");
				mes("Brother Cedric drinks the water");
				delay(3);
				npcsay(player, npc, "aah, that's better");
				mes("you throw the excess water over brother Cedric");
				delay(3);
				npcsay(player, npc, "now i just need to fix...",
					"..this cart..", "..and we can go party",
					".could you help?");
				player.updateQuestStage(getQuestId(), 5);
				int waterMenu = multi(player, npc,
					"No, i've helped enough monks today",
					"Yes i'd be happy to");
				if (waterMenu == 0) {
					npcsay(player, npc, "in that case i'd better drink..",
						"..more wine. It help's me think.");
				} else if (waterMenu == 1) {
					npcsay(player, npc, "i need some wood");
					if (player.getCarriedItems().remove(new Item(ItemId.LOGS.id())) != -1) {
						say(player, npc, "here you go..",
							"I've got some wood");
						npcsay(player, npc,
							"well done, now i'll fix this cart",
							"you head back to Brother Omad",
							"Tell him i'm on my way", "I won't be long");
						player.updateQuestStage(getQuestId(), 6);
					}
				}
			}
		}
	}
}
