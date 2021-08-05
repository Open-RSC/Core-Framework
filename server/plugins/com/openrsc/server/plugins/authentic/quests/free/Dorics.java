package com.openrsc.server.plugins.authentic.quests.free;

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
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Dorics implements QuestInterface, TalkNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.DORICS_QUEST;
	}

	@Override
	public String getQuestName() {
		return "Doric's quest";
	}

	@Override
	public int getQuestPoints() {
		return Quest.DORICS_QUEST.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("You have completed Dorics quest");
		final QuestReward reward = Quest.DORICS_QUEST.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void doricDialogue(Player player, Npc n) {
		switch (player.getQuestStage(this)) {
			case 0:
				npcsay(player, n,
					"Hello traveller, what brings you to my humble smithy?");
				int choice = multi(player, n,
					"I wanted to use your anvils",
					"Mind your own business, shortstuff",
					"I was just checking out the landscape",
					"What do you make here?");
				if (choice == 0) {
					npcsay(player, n, "My anvils get enough work with my own use",
						"I make amulets, it takes a lot of work.",
						"If you could get me some more materials I could let you use them");
					int choice2 = multi(player, n, false, // Do not send to client
						"Yes I will get you materials",
						"No, hitting rocks is for the boring people, sorry.");
					if (choice2 == 0) {
						say(player, n, "Yes I will get you materials");
						npcsay(player,
							n,
							"Well, clay is what I use more than anything. I make casts",
							"Could you get me 6 clay, and 4 copper ore and 2 iron ore please?",
							"I could pay a little, and let you use my anvils");
						say(player, n,
							"Certainly, I will get them for you. goodbye");
						player.updateQuestStage(getQuestId(), 1);
					} else if (choice2 == 1) {
						say(player, n, "No, hitting rocks is for the boring people, sorry");
						npcsay(player, n, "That is your choice, nice to meet you anyway");
					}

				} else if (choice == 1) {
					npcsay(player, n,
						"How nice to meet someone with such pleasant manners",
						"Do come again when you need to shout at someone smaller than you");
				} else if (choice == 2) {
					npcsay(player, n,
						"We have a fine town here, it suits us very well",
						"Please enjoy your travels. And do visit my friends in their mine");
				} else if (choice == 3) {
					npcsay(player, n,
						"I make amulets. I am the best maker of them in Runescape");
					say(player, n, "Do you have any to sell?");
					npcsay(player, n, "Not at the moment, sorry. Try again later");
				}
				break;
			case 1:
				npcsay(player, n, "Have you got my materials yet traveller?");
				if (player.getCarriedItems().getInventory().countId(ItemId.CLAY.id()) >= 6
					&& player.getCarriedItems().getInventory().countId(ItemId.COPPER_ORE.id()) >= 4
					&& player.getCarriedItems().getInventory().countId(ItemId.IRON_ORE.id()) >= 2) {
					say(player, n, "I have everything you need");
					npcsay(player, n, "Many thanks, pass them here please");
					player.message("You hand the clay, copper and iron to Doric");
					for (int i = 0; i < 6; i++)
						player.getCarriedItems().remove(new Item(ItemId.CLAY.id()));
					for (int i = 0; i < 4; i++)
						player.getCarriedItems().remove(new Item(ItemId.COPPER_ORE.id()));
					for (int i = 0; i < 2; i++)
						player.getCarriedItems().remove(new Item(ItemId.IRON_ORE.id()));

					npcsay(player, n, "I can spare you some coins for your trouble");

					player.message("Doric hands you 180 coins");
					player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), 180));

					npcsay(player, n, "Please use my anvils any time you want");
					player.sendQuestComplete(Quests.DORICS_QUEST);
					player.updateQuestStage(getQuestId(), -1);

				} else {
					say(player, n, "Sorry, I don't have them all yet");
					npcsay(player, n, "Not to worry, stick at it",
						"Remember I need 6 Clay, 4 Copper ore and 2 Iron ore");
				}
				break;
			case -1:
				npcsay(player, n, "Hello traveller, how is your Metalworking coming along?");
				say(player, n, "Not too bad thanks Doric");
				npcsay(player, n, "Good, the love of metal is a thing close to my heart");
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.DORIC.id()) {
			doricDialogue(player, n);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DORIC.id();
	}
}
