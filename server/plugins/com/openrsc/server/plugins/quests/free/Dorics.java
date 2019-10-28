package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class Dorics implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener {

	/**
	 * Quest: Doric's Quest - fully working made by Fate 2013-09-10. GIVE DORIC
	 * THE ORES, ANVIL, DIALOGUES, AFTER DIALOGUES - 100% Replicated.
	 */

	@Override
	public int getQuestId() {
		return Quests.DORICS_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Doric's quest";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}
	
	@Override
	public void handleReward(Player player) {
		player.message("You have completed Dorics quest");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.DORICS_QUEST), true);
		player.message("@gre@You haved gained 1 quest points!");
	}

	private void doricDialogue(Player p, Npc n) {
		switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n,
					"Hello traveller, what brings you to my humble smithy?");
				int choice = showMenu(p, n,
					"I wanted to use your anvils",
					"Mind your own business, shortstuff",
					"I was just checking out the landscape",
					"What do you make here?");
				if (choice == 0) {
					npcTalk(p, n, "My anvils get enough work with my own use",
						"I make amulets, it takes a lot of work.",
						"If you could get me some more materials I could let you use them");
					int choice2 = showMenu(p, n, false, // Do not send to client
						"Yes I will get you materials",
						"No, hitting rocks is for the boring people, sorry.");
					if (choice2 == 0) {
						playerTalk(p, n, "Yes I will get you materials");
						npcTalk(p,
							n,
							"Well, clay is what I use more than anything. I make casts",
							"Could you get me 6 clay, and 4 copper ore, and 2 iron ore please?",
							"I could pay a little, and let you use my anvils");
						playerTalk(p, n,
							"Certainly, I will get them for you. goodbye");
						p.updateQuestStage(getQuestId(), 1);
					} else if (choice2 == 1) {
						playerTalk(p, n, "No, hitting rocks is for the boring people, sorry");
						npcTalk(p, n, "That is your choice, nice to meet you anyway");
					}

				} else if (choice == 1) {
					npcTalk(p, n,
						"How nice to meet someone with such pleasant manners",
						"Do come again when you need to shout at someone smaller than you");
				} else if (choice == 2) {
					npcTalk(p, n,
						"We have a fine town here, it suits us very well",
						"Please enjoy your travels. And do visit my friends in their mine");
				} else if (choice == 3) {
					npcTalk(p, n,
						"I make amulets. I am the best maker of them in Runescape");
					playerTalk(p, n, "Do you have any to sell?");
					npcTalk(p, n, "Not at the moment, sorry. Try again later");
				}
				break;
			case 1:
				npcTalk(p, n, "Have you got my materials yet traveller?");
				if (p.getInventory().countId(ItemId.CLAY.id()) >= 6
					&& p.getInventory().countId(ItemId.COPPER_ORE.id()) >= 4
					&& p.getInventory().countId(ItemId.IRON_ORE.id()) >= 2) {
					playerTalk(p, n, "I have everything you need");
					npcTalk(p, n, "Many thanks, pass them here please");
					p.message("You hand the clay, copper and iron to Doric");
					for (int i = 0; i < 6; i++)
						p.getInventory().remove(ItemId.CLAY.id(), 1);
					for (int i = 0; i < 4; i++)
						p.getInventory().remove(ItemId.COPPER_ORE.id(), 1);
					for (int i = 0; i < 2; i++)
						p.getInventory().remove(ItemId.IRON_ORE.id(), 1);

					npcTalk(p, n, "I can spare you some coins for your trouble");

					p.message("Doric hands you 180 coins");
					p.getInventory().add(new Item(ItemId.COINS.id(), 180));

					npcTalk(p, n, "Please use my anvils any time you want");
					p.sendQuestComplete(Quests.DORICS_QUEST);
					p.updateQuestStage(getQuestId(), -1);

				} else {
					playerTalk(p, n, "Sorry, I don't have them all yet");
					npcTalk(p, n, "Not to worry, stick at it",
						"Remember I need 6 Clay, 4 Copper ore and 2 Iron ore");
				}
				break;
			case -1:
				npcTalk(p, n, "Hello traveller, how is your Metalworking coming along?");
				playerTalk(p, n, "Not too bad thanks Doric");
				npcTalk(p, n, "Good, the love of metal is a thing close to my heart");
				break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.DORIC.id()) {
			doricDialogue(p, n);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.DORIC.id();
	}
}
