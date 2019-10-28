package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MonksFriend implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener, InvUseOnNpcListener,
	InvUseOnNpcExecutiveListener {

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
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BROTHER_CEDRIC.id() || n.getID() == NpcId.BROTHER_OMAD.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == NpcId.BROTHER_OMAD.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
					playerTalk(p, n, "hello there");
					npcTalk(p, n, "...yawn...oh, hello...yawn..",
						"I'm sorry, I'm just so tired..",
						"I haven't slept in a week", "It's driving me mad");
					int firstMenu = showMenu(p, n, false, //do not send over
						"Why can't you sleep, what's wrong?",
						"sorry, i'm too busy to hear your problems");
					if (firstMenu == 0) {
						playerTalk(p, n, "Why can't you sleep, what's wrong?");
						npcTalk(p, n, "It's the brother Androe's son",
							"with his constant waaaaaah..waaaaaaaaah",
							" Androe said it's natural, but it's just annoying");
						playerTalk(p, n, "I suppose that's what kids do");
						npcTalk(p, n, "he was fine, up until last week",
							"thieves broke in",
							"They stole his favourite sleeping blanket",
							"now he won't rest until it's returned",
							"..and that means neither can I!");
						int secondMenu = showMenu(p, n, false, //do not send over
							"Can I help at all?",
							"I'm sorry to hear that, I hope you find his blanket");
						if (secondMenu == 0) {
							playerTalk(p, n, "can I help at all?");
							npcTalk(p, n, "please do, we are peaceful men",
								" but you could recover the blanket from the thieves");
							playerTalk(p, n, "where are they?");
							npcTalk(p, n,
								"they hide in a secret cave in the forest",
								"..it's hidden under a ring of stones",
								"please, bring back the blanket");
							p.updateQuestStage(getQuestId(), 1);
						} else if (secondMenu == 1) {
							playerTalk(p, n, "I'm sorry to hear that, I hope you find his blanket");
						}
					} else if (firstMenu == 1) {
						playerTalk(p, n, "Sorry, I'm too busy to hear your problems");
					}
					break;
				case 1:
					playerTalk(p, n, "Hello ");
					npcTalk(p, n, "...yawn...oh, hello again...yawn..",
						"..please tell me you have the blanket");
					if (removeItem(p, ItemId.BLANKET.id(), 1)) {
						playerTalk(p, n,
							"Yes I returned it from the clutches of the evil thieves");
						npcTalk(p, n, "Really, that's excellent, well done",
							"that should cheer up Androe's son",
							"and maybe I will be able to get some rest",
							"..yawn..i'm off to bed, farewell brave traveller.");
						message(p,
							"well done, you have completed part 1 of the monks friend quest");
						p.updateQuestStage(getQuestId(), 2);
					} else {
						playerTalk(p, n, "I'm afraid not");
						npcTalk(p, n, "I need some sleep");
					}
					break;
				case 2:
					playerTalk(p, n, "Hello, how are you");
					npcTalk(p, n, "much better now i'm sleeping well",
						"now I can organise the party");
					playerTalk(p, n, "what party?");
					npcTalk(p, n, "Androe's son's birthday party",
						"he's going to be one year old");
					playerTalk(p, n, "that's sweet");
					npcTalk(p, n, "it's also a great excuse for a drink",
						"now we just need brother Cedric to return",
						"with the wine");
					int thirdMenu = showMenu(p, n, "who's brother Cedric?",
						"enjoy it, i'll see you soon");
					if (thirdMenu == 0) {
						npcTalk(p, n, "Cedric lives here too",
							"we sent him out three days ago",
							"to collect wine, but he didn't return",
							"he most probably got drunk",
							"and lost in the forest",
							"I don't suppose you could look for him?",
							"then we can really party");
						int fourthMenu = showMenu(p, n,
							"I've no time for that, sorry",
							"where should I look?", "can I come?");
						if (fourthMenu == 0) {
							// NOTHING
						} else if (fourthMenu == 1) {
							npcTalk(p, n, "oh, he won't be far",
								"probably out in the forest");
							p.updateQuestStage(getQuestId(), 3);
						} else if (fourthMenu == 2) {
							npcTalk(p, n, "of course,",
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
					playerTalk(p, n, "Hi there!");
					npcTalk(p, n, "oh my, I need a drink",
						"where is that brother Cedric");
					break;
				case 6:
					playerTalk(p, n, "Hi Omad, Brother Cedric is on his way");
					npcTalk(p, n, "good,good,good", "now we can party");
					p.sendQuestComplete(Quests.MONKS_FRIEND);
					npcTalk(p, n, "I have little to repay you with",
						"but please, take these runestones");
					addItem(p, ItemId.LAW_RUNE.id(), 8);
					break;
				case -1:
					npcTalk(p, n, "Dum dee do la la", "Hiccup", "That's good wine");
					break;
			}
		}
		else if (n.getID() == NpcId.BROTHER_CEDRIC.id()) {
			switch (p.getQuestStage(this)) {
				case 0:
				case 1:
				case 2:
					playerTalk(p, n, "Hello");
					npcTalk(p, n, "honey,money,woman,wine..");
					playerTalk(p, n, "Are you ok?");
					npcTalk(p, n, " yesshh...hic up...beautiful..");
					playerTalk(p, n, "take care old monk");
					npcTalk(p, n, "la..di..da..hic..up..");
					message(p, "The monk has had too much to drink");
					break;
				case 3:
					playerTalk(p, n, "Brother Cedric are you okay?");
					npcTalk(p, n, "yeesshhh, i'm very, very....",
						"..drunk..hic..up..");
					playerTalk(p, n, "brother Omad needs the wine..",
						"..for the party");
					npcTalk(p, n, " oh dear, oh dear ",
						"I knew I had to do something",
						"pleashhh, find me some water",
						"once i'm sober i'll help you..",
						"..take the wine back.");
					p.updateQuestStage(getQuestId(), 4);
					break;
				case 4:
					playerTalk(p, n, "Are you okay?");
					npcTalk(p, n, "...hic up..oh my head..", "..I need some water.");
					break;
				case 5:
					playerTalk(p, n, "Hello Cedric");
					npcTalk(p, n, "want to help me fix the cart?");
					int cartMenu = showMenu(p, n, "Yes i'd be happy to",
						"No, not really");
					if (cartMenu == 0) {
						npcTalk(p, n, "i need some wood");
						if (removeItem(p, ItemId.LOGS.id(), 1)) {
							playerTalk(p, n, "here you go..", "I've got some wood");
							npcTalk(p, n, "well done, now i'll fix this cart",
								"you head back to Brother Omad",
								"Tell him i'm on my way", "I won't be long");
							p.updateQuestStage(getQuestId(), 6);
						}
					} else if (cartMenu == 1) {
						// NOTHING
					}
					break;
				case 6:
					playerTalk(p, n, "Hello Cedric");
					npcTalk(p, n, "hi, i'm almost done here",
						"go tell Omad that I..", "..won't be long");
					break;
				case -1:
					npcTalk(p, n, "Brother Oman sends you his thanks",
						"He won't be in a fit state to thank you in person any more");
					break;
			}
		}

	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.BROTHER_CEDRIC.id();
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		if (npc.getID() == NpcId.BROTHER_CEDRIC.id()) {
			if (player.getQuestStage(getQuestId()) == 4 && item.getID() == ItemId.BUCKET_OF_WATER.id()) {
				showBubble(player, item);
				playerTalk(player, npc, "Cedric, here, drink some water");
				npcTalk(player, npc, "oh yes, my head's starting to spin",
					"gulp...gulp");
				message(player, "Brother Cedric drinks the water");
				npcTalk(player, npc, "aah, that's better");
				message(player,
					"you throw the excess water over brother Cedric");
				npcTalk(player, npc, "now i just need to fix...",
					"..this cart..", "..and we can go party",
					".could you help?");
				player.updateQuestStage(getQuestId(), 5);
				int waterMenu = showMenu(player, npc,
					"No, i've helped enough monks today",
					"Yes i'd be happy to");
				if (waterMenu == 0) {
					npcTalk(player, npc, "in that case i'd better drink..",
						"..more wine. It help's me think.");
				} else if (waterMenu == 1) {
					npcTalk(player, npc, "i need some wood");
					if (removeItem(player, ItemId.LOGS.id(), 1)) {
						playerTalk(player, npc, "here you go..",
							"I've got some wood");
						npcTalk(player, npc,
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
