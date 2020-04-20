package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
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
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done. You have completed the cook's assistant quest");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.COOKS_ASSISTANT), true);
		player.message("@gre@You haved gained 1 quest point!");
	}

	private void cookDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcsay(p, n, "What am I to do?");
					int choice = multi(p, n, "What's wrong?",
						"Well you could give me all your money",
						"You don't look very happy", "Nice hat");
					if (choice == 0) {
						cookDialogue(p, n, Cook.TERRIBLE_MESS);
					} else if (choice == 1) {
						npcsay(p, n, "HaHa very funny");
					} else if (choice == 2) {
						npcsay(p, n, "No, i'm not");
						int choice2 = multi(p, n,
							"What's wrong?",
							"I'd take the rest of the day off if I were you");
						if (choice2 == 0) {
							cookDialogue(p, n, Cook.TERRIBLE_MESS);
						} else if (choice2 == 1) {
							npcsay(p, n, "No, that's the worst thing I could do - I'd get in terrible trouble");
							say(p, n, "What's wrong?");
							cookDialogue(p, n, Cook.TERRIBLE_MESS);
						}
					} else if (choice == 3) {
						npcsay(p, n, "Err thank you -it's a pretty ordinary cooks hat really");
					}
					break;
				case 1:
					npcsay(p, n, "How are you getting on with finding the ingredients?");
					if (p.getCarriedItems().hasCatalogID(ItemId.EGG.id())
						&& p.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id())
						&& p.getCarriedItems().hasCatalogID(ItemId.MILK.id())) {
						say(p, n,
							"I now have everything you need for your cake",
							"Milk, flour, and an egg!");
						npcsay(p, n, "I am saved thankyou!");
						Functions.mes(p, "You give some milk, an egg and some flour to the cook");
						p.getCarriedItems().remove(new Item(ItemId.EGG.id()));
						p.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
						p.getCarriedItems().remove(new Item(ItemId.MILK.id()));
						p.sendQuestComplete(Quests.COOKS_ASSISTANT);
						p.updateQuestStage(getQuestId(), -1);

					} else if (p.getCarriedItems().hasCatalogID(ItemId.EGG.id())
						|| p.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id())
						|| p.getCarriedItems().hasCatalogID(ItemId.MILK.id())) {

						say(p, n, "I have found some of the things you asked for:");
						if (p.getCarriedItems().hasCatalogID(ItemId.MILK.id()))
							say(p, n, "I have some milk");
						if (p.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id()))
							say(p, n, "I have some flour");
						if (p.getCarriedItems().hasCatalogID(ItemId.EGG.id()))
							say(p, n, "I have an egg");

						npcsay(p, n, "Great, but can you get the other ingredients as well?",
								"You still need to find");
						if (!p.getCarriedItems().hasCatalogID(ItemId.MILK.id()))
							npcsay(p, n, "Some milk");
						if (!p.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id()))
							npcsay(p, n, "Some flour");
						if (!p.getCarriedItems().hasCatalogID(ItemId.EGG.id()))
							npcsay(p, n, "An egg");

						say(p, n, "OK I'll try and find that for you");

					} else {
						say(p, n, "I'm afraid I don't have any yet!");
						npcsay(p, n, "Oh dear oh dear!",
							"I need flour, eggs, and milk",
							"Without them I am doomed!");
					}
					break;
				case -1:
					npcsay(p, n, "Hello friend, how is the adventuring going?");
					int choice4 = multi(p, n,
						"I am getting strong and mighty", "I keep on dying",
						"Nice hat", "Can I use your range?");
					if (choice4 == 0) {
						npcsay(p, n, "Glad to hear it");
					} else if (choice4 == 1) {
						npcsay(p, n,
							"Ah well at least you keep coming back to life!");
					} else if (choice4 == 2) {
						npcsay(p, n,
							"Err thank you -it's a pretty ordinary cooks hat really");
					} else if (choice4 == 3) {
						npcsay(p, n, "Go ahead", "It's a very good range",
							"It's easier to use than most other ranges");
					}
					break;
			}
		} else if (cID == Cook.TERRIBLE_MESS) {
			npcsay(p,
				n,
				"Ooh dear I'm in a terrible mess",
				"It's the duke's birthday today",
				"I'm meant to be making him a big cake for this evening",
				"Unfortunately, I've forgotten to buy some of the ingredients",
				"I'll never get them in time now",
				"I don't suppose you could help me?");
			int choice = multi(p, n, "Yes, I'll help you",
				"No, I don't feel like it. Maybe later");
			if (choice == 0) {
				npcsay(p, n, "Oh thank you, thank you",
					"I need milk, eggs and flour",
					"I'd be very grateful if you can get them to me");
				p.updateQuestStage(getQuestId(), 1);
			} else if (choice == 1) {
				npcsay(p, n, "OK, suit yourself");
			}
		}
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.COOK.id()) {
			cookDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.COOK.id();
	}

	class Cook {
		public static final int TERRIBLE_MESS = 0;
	}
}
