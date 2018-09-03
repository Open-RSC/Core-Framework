package com.openrsc.server.plugins.quests.free;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class CooksAssistant implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener {
	/**
	 * Quest: Cook's assistant - fully working made by Fate 2013-09-10. COOKS
	 * RANGE, COOK, DIALOGUES, COOKING FOOD, AFTER DIALOGUES - 100% Replicated.
	 * 
	 * EDIT: 2107-03-15 
	 * -Cooks range is handled in the objectcooking class.
	 * -Cooks range are used in Gnome stronhold area for Blurberry & Gnome restaurant minigames.
	 */

	class Cook {
		public static final int TERRIBLE_MESS = 0;
	}

	/**
	 * Npc's associated with this quest.
	 */
	private static final int COOK = 7;

	@Override
	public int getQuestId() {
		return Constants.Quests.COOKS_ASSISTANT;
	}

	@Override
	public String getQuestName() {
		return "Cook's assistant";
	}

	private void cookDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
			case 0:
				npcTalk(p, n, "What am I to do?");
				int choice = showMenu(p, n, "What's wrong?",
						"Well you could give me all your money",
						"You don't look very happy", "Nice hat");
				if (choice == 0) {
					cookDialogue(p, n, Cook.TERRIBLE_MESS);
				} else if (choice == 1) {
					npcTalk(p, n, "HaHa very funny");
				} else if (choice == 2) {
					npcTalk(p, n, "No, i'm not");
					int choice2 = showMenu(p, n,
							"What's wrong?",
							"I'd take the rest of the day off if I were you");
					if (choice2 == 0) {
						cookDialogue(p, n, Cook.TERRIBLE_MESS);
					} else if (choice2 == 1) {
						npcTalk(p, n, "No, that's the worst thing I could do - I'd get in terrible trouble");
						playerTalk(p, n, "What's wrong?");
						npcTalk(p,
								n,
								"Ooh dear i'm in a terrible mess",
								"It's the duke's birthday today",
								"I'm meant to be making him a big cake for this evening",
								"Unfortunately, i've forgotten to buy some of the ingredients",
								"I'll never get them in time now",
								"I don't suppose you could help me?");
						int choice3 = showMenu(p, n,
								"Yes, I'll help you",
								"No, I don't feel like it. Maybe later");
						if (choice3 == 0) {
							npcTalk(p, n, "Oh thank you, thank you",
									"I need milk, eggs and flour",
									"I'd be very grateful if you can get them to me");
							p.updateQuestStage(getQuestId(), 1);
						} else if (choice3 == 1) {
							npcTalk(p, n, "OK, suit yourself");
						}
					}
				} else if (choice == 3) {
					npcTalk(p, n, "Err thank you -it's a pretty ordinary cooks hat really");
				}
				break;
			case 1:
				npcTalk(p, n, "How are you getting on with finding the ingredients?");
				if (p.getInventory().hasItemId(19)
						&& p.getInventory().hasItemId(136)
						&& p.getInventory().hasItemId(22)) {
					playerTalk(p, n,
							"I now have everything you need for your cake",
							"Milk, flour, and an egg!");
					npcTalk(p, n, "I am saved thankyou!");
					message(p, "You give some milk, an egg and some flour to the cook");
					p.sendQuestComplete(Constants.Quests.COOKS_ASSISTANT);
					p.updateQuestStage(getQuestId(), -1);
				} else {
					playerTalk(p, n, "I'm afraid i don't have any yet!");
					npcTalk(p, n, "Oh dear oh dear!",
							"I need flour, eggs, and milk",
							"Without them i am doomed!");
				}
				break;
			case -1:
				npcTalk(p, n, "Hello friend, how is the adventuring going?");
				int choice4 = showMenu(p, n,
						"I am getting strong and mighty", "I keep on dying",
						"Nice hat", "Can I use your range?");
				if (choice4 == 0) {
					npcTalk(p, n, "Glad to hear it");
				} else if (choice4 == 1) {
					npcTalk(p, n,
							"Ah well at least you keep coming back to life!");
				} else if (choice4 == 2) {
					npcTalk(p, n,
							"Err thank you -it's a pretty ordinary cooks hat really");
				} else if (choice4 == 3) {
					npcTalk(p, n, "Go ahead", "It's a very good range",
							"It's easier to use than most other ranges");
				}
				break;
			}
		}
		switch (cID) {
		case Cook.TERRIBLE_MESS:
			npcTalk(p,
					n,
					"Ooh dear i'm in a terrible mess",
					"It's the duke's birthday today",
					"I'm meant to be making him a big cake for this evening",
					"Unfortunately, i've forgotten to buy some of the ingredients",
					"I'll never get them in time now",
					"I don't suppose you could help me?");
			int choice = showMenu(p, n, "Yes, I'll help you",
					"No, I don't feel like it. Maybe later");
			if (choice == 0) {
				npcTalk(p, n, "Oh thank you, thank you",
						"I need milk, eggs and flour",
						"I'd be very grateful if you can get them to me");
				p.updateQuestStage(getQuestId(), 1);
			} else if (choice == 1) {
				npcTalk(p, n, "OK, suit yourself");
			}
			break;
		}
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == COOK) {
			cookDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == COOK) {
			return true;
		}
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done. You have completed the cook's assistant quest");
		player.incQuestExp(7, player.getSkills().getMaxStat(7) * 200 + 1000);
		player.incQuestPoints(1);
		player.message("@gre@You have gained 1 quest point!");
		player.getInventory().remove(19, 1);
		player.getInventory().remove(136, 1);
		player.getInventory().remove(22, 1);
	}

	@Override
	public boolean isMembers() {
		return false;
	}
}
