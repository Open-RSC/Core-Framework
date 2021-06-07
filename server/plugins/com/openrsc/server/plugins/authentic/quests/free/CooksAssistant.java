package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class CooksAssistant implements QuestInterface, TalkNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.COOKS_ASSISTANT;
	}

	@Override
	public String getQuestName() {
		return "Cook's assistant";
	}

	@Override
	public int getQuestPoints() {
		return Quest.COOKS_ASSISTANT.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done. You have completed the cook's assistant quest");
		final QuestReward reward = Quest.COOKS_ASSISTANT.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void cookDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "What am I to do?");
					int choice = multi(player, n, "What's wrong?",
						"Well you could give me all your money",
						"You don't look very happy", "Nice hat");
					if (choice == 0) {
						cookDialogue(player, n, Cook.TERRIBLE_MESS);
					} else if (choice == 1) {
						npcsay(player, n, "HaHa very funny");
					} else if (choice == 2) {
						npcsay(player, n, "No, i'm not");
						int choice2 = multi(player, n,
							"What's wrong?",
							"I'd take the rest of the day off if I were you");
						if (choice2 == 0) {
							cookDialogue(player, n, Cook.TERRIBLE_MESS);
						} else if (choice2 == 1) {
							npcsay(player, n, "No, that's the worst thing I could do - I'd get in terrible trouble");
							say(player, n, "What's wrong?");
							cookDialogue(player, n, Cook.TERRIBLE_MESS);
						}
					} else if (choice == 3) {
						npcsay(player, n, "Err thank you -it's a pretty ordinary cooks hat really");
					}
					break;
				case 1:
					npcsay(player, n, "How are you getting on with finding the ingredients?");
					if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.MILK.id())) {
						say(player, n,
							"I now have everything you need for your cake",
							"Milk, flour, and an egg!");
						npcsay(player, n, "I am saved thankyou!");
						mes("You give some milk, an egg and some flour to the cook");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.EGG.id()));
						player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
						player.getCarriedItems().remove(new Item(ItemId.MILK.id()));
						player.sendQuestComplete(Quests.COOKS_ASSISTANT);
						player.updateQuestStage(getQuestId(), -1);

					} else if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id())
						|| player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id())
						|| player.getCarriedItems().hasCatalogID(ItemId.MILK.id())) {

						say(player, n, "I have found some of the things you asked for:");
						if (player.getCarriedItems().hasCatalogID(ItemId.MILK.id()))
							say(player, n, "I have some milk");
						if (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id()))
							say(player, n, "I have some flour");
						if (player.getCarriedItems().hasCatalogID(ItemId.EGG.id()))
							say(player, n, "I have an egg");

						npcsay(player, n, "Great, but can you get the other ingredients as well?",
								"You still need to find");
						if (!player.getCarriedItems().hasCatalogID(ItemId.MILK.id()))
							npcsay(player, n, "Some milk");
						if (!player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id()))
							npcsay(player, n, "Some flour");
						if (!player.getCarriedItems().hasCatalogID(ItemId.EGG.id()))
							npcsay(player, n, "An egg");

						say(player, n, "OK I'll try and find that for you");

					} else {
						say(player, n, "I'm afraid I don't have any yet!");
						npcsay(player, n, "Oh dear oh dear!",
							"I need flour, eggs, and milk",
							"Without them I am doomed!");
					}
					break;
				case -1:
					npcsay(player, n, "Hello friend, how is the adventuring going?");
					int choice4 = multi(player, n,
						"I am getting strong and mighty", "I keep on dying",
						"Nice hat", "Can I use your range?");
					if (choice4 == 0) {
						npcsay(player, n, "Glad to hear it");
					} else if (choice4 == 1) {
						npcsay(player, n,
							"Ah well at least you keep coming back to life!");
					} else if (choice4 == 2) {
						npcsay(player, n,
							"Err thank you -it's a pretty ordinary cooks hat really");
					} else if (choice4 == 3) {
						npcsay(player, n, "Go ahead", "It's a very good range",
							"It's easier to use than most other ranges");
					}
					break;
			}
		} else if (cID == Cook.TERRIBLE_MESS) {
			npcsay(player,
				n,
				"Ooh dear I'm in a terrible mess",
				"It's the duke's birthday today",
				"I'm meant to be making him a big cake for this evening",
				"Unfortunately, I've forgotten to buy some of the ingredients",
				"I'll never get them in time now",
				"I don't suppose you could help me?");
			int choice = multi(player, n, "Yes, I'll help you",
				"No, I don't feel like it. Maybe later");
			if (choice == 0) {
				npcsay(player, n, "Oh thank you, thank you",
					"I need milk, eggs and flour",
					"I'd be very grateful if you can get them to me");
				player.updateQuestStage(getQuestId(), 1);
			} else if (choice == 1) {
				npcsay(player, n, "OK, suit yourself");
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.COOK.id()) {
			cookDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COOK.id();
	}

	class Cook {
		public static final int TERRIBLE_MESS = 0;
	}
}
