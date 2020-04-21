package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
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
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done you have completed the monks friend quest");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.MONKS_FRIEND), true);
		p.message("@gre@You haved gained 1 quest point!");
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BROTHER_CEDRIC.id() || n.getID() == NpcId.BROTHER_OMAD.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.BROTHER_OMAD.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					say(p, n, "hello there");
					npcsay(p, n, "...yawn...oh, hello...yawn..",
						"I'm sorry, I'm just so tired..",
						"I haven't slept in a week", "It's driving me mad");
					int firstMenu = multi(p, n, false, //do not send over
						"Why can't you sleep, what's wrong?",
						"sorry, i'm too busy to hear your problems");
					if (firstMenu == 0) {
						say(p, n, "Why can't you sleep, what's wrong?");
						npcsay(p, n, "It's the brother Androe's son",
							"with his constant waaaaaah..waaaaaaaaah",
							" Androe said it's natural, but it's just annoying");
						say(p, n, "I suppose that's what kids do");
						npcsay(p, n, "he was fine, up until last week",
							"thieves broke in",
							"They stole his favourite sleeping blanket",
							"now he won't rest until it's returned",
							"..and that means neither can I!");
						int secondMenu = multi(p, n, false, //do not send over
							"Can I help at all?",
							"I'm sorry to hear that, I hope you find his blanket");
						if (secondMenu == 0) {
							say(p, n, "can I help at all?");
							npcsay(p, n, "please do, we are peaceful men",
								" but you could recover the blanket from the thieves");
							say(p, n, "where are they?");
							npcsay(p, n,
								"they hide in a secret cave in the forest",
								"..it's hidden under a ring of stones",
								"please, bring back the blanket");
							p.updateQuestStage(getQuestId(), 1);
						} else if (secondMenu == 1) {
							say(p, n, "I'm sorry to hear that, I hope you find his blanket");
						}
					} else if (firstMenu == 1) {
						say(p, n, "Sorry, I'm too busy to hear your problems");
					}
					break;
				case 1:
					say(p, n, "Hello ");
					npcsay(p, n, "...yawn...oh, hello again...yawn..",
						"..please tell me you have the blanket");
					if (p.getCarriedItems().remove(new Item(ItemId.BLANKET.id())) != -1) {
						say(p, n,
							"Yes I returned it from the clutches of the evil thieves");
						npcsay(p, n, "Really, that's excellent, well done",
							"that should cheer up Androe's son",
							"and maybe I will be able to get some rest",
							"..yawn..i'm off to bed, farewell brave traveller.");
						mes(p,
							"well done, you have completed part 1 of the monks friend quest");
						p.updateQuestStage(getQuestId(), 2);
					} else {
						say(p, n, "I'm afraid not");
						npcsay(p, n, "I need some sleep");
					}
					break;
				case 2:
					say(p, n, "Hello, how are you");
					npcsay(p, n, "much better now i'm sleeping well",
						"now I can organise the party");
					say(p, n, "what party?");
					npcsay(p, n, "Androe's son's birthday party",
						"he's going to be one year old");
					say(p, n, "that's sweet");
					npcsay(p, n, "it's also a great excuse for a drink",
						"now we just need brother Cedric to return",
						"with the wine");
					int thirdMenu = multi(p, n, "who's brother Cedric?",
						"enjoy it, i'll see you soon");
					if (thirdMenu == 0) {
						npcsay(p, n, "Cedric lives here too",
							"we sent him out three days ago",
							"to collect wine, but he didn't return",
							"he most probably got drunk",
							"and lost in the forest",
							"I don't suppose you could look for him?",
							"then we can really party");
						int fourthMenu = multi(p, n,
							"I've no time for that, sorry",
							"where should I look?", "can I come?");
						if (fourthMenu == 0) {
							// NOTHING
						} else if (fourthMenu == 1) {
							npcsay(p, n, "oh, he won't be far",
								"probably out in the forest");
							p.updateQuestStage(getQuestId(), 3);
						} else if (fourthMenu == 2) {
							npcsay(p, n, "of course,",
								"but we need the wine first");
							p.updateQuestStage(getQuestId(), 3);
						}
					} else if (thirdMenu == 1) {
						// NOTHING
					}

					break;
				case 3:
				case 4:
				case 5:
					say(p, n, "Hi there!");
					npcsay(p, n, "oh my, I need a drink",
						"where is that brother Cedric");
					break;
				case 6:
					say(p, n, "Hi Omad, Brother Cedric is on his way");
					npcsay(p, n, "good,good,good", "now we can party");
					p.sendQuestComplete(Quests.MONKS_FRIEND);
					npcsay(p, n, "I have little to repay you with",
						"but please, take these runestones");
					give(p, ItemId.LAW_RUNE.id(), 8);
					break;
				case -1:
					npcsay(p, n, "Dum dee do la la", "Hiccup", "That's good wine");
					break;
			}
		}
		else if (n.getID() == NpcId.BROTHER_CEDRIC.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					say(p, n, "Hello");
					npcsay(p, n, "honey,money,woman,wine..");
					say(p, n, "Are you ok?");
					npcsay(p, n, " yesshh...hic up...beautiful..");
					say(p, n, "take care old monk");
					npcsay(p, n, "la..di..da..hic..up..");
					mes(p, "The monk has had too much to drink");
					break;
				case 3:
					say(p, n, "Brother Cedric are you okay?");
					npcsay(p, n, "yeesshhh, i'm very, very....",
						"..drunk..hic..up..");
					say(p, n, "brother Omad needs the wine..",
						"..for the party");
					npcsay(p, n, " oh dear, oh dear ",
						"I knew I had to do something",
						"pleashhh, find me some water",
						"once i'm sober i'll help you..",
						"..take the wine back.");
					p.updateQuestStage(getQuestId(), 4);
					break;
				case 4:
					say(p, n, "Are you okay?");
					npcsay(p, n, "...hic up..oh my head..", "..I need some water.");
					break;
				case 5:
					say(p, n, "Hello Cedric");
					npcsay(p, n, "want to help me fix the cart?");
					int cartMenu = multi(p, n, "Yes i'd be happy to",
						"No, not really");
					if (cartMenu == 0) {
						npcsay(p, n, "i need some wood");
						if (p.getCarriedItems().remove(new Item(ItemId.LOGS.id())) != -1) {
							say(p, n, "here you go..", "I've got some wood");
							npcsay(p, n, "well done, now i'll fix this cart",
								"you head back to Brother Omad",
								"Tell him i'm on my way", "I won't be long");
							p.updateQuestStage(getQuestId(), 6);
						}
					} else if (cartMenu == 1) {
						// NOTHING
					}
					break;
				case 6:
					say(p, n, "Hello Cedric");
					npcsay(p, n, "hi, i'm almost done here",
						"go tell Omad that I..", "..won't be long");
					break;
				case -1:
					npcsay(p, n, "Brother Oman sends you his thanks",
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
				thinkbubble(player, item);
				say(player, npc, "Cedric, here, drink some water");
				npcsay(player, npc, "oh yes, my head's starting to spin",
					"gulp...gulp");
				mes(player, "Brother Cedric drinks the water");
				npcsay(player, npc, "aah, that's better");
				mes(player,
					"you throw the excess water over brother Cedric");
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
