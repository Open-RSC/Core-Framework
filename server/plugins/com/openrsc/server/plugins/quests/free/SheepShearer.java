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

public class SheepShearer implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener {

	/**
	 * Quest: Sheep Shearer - fully working made by Fate 2013-09-10. GIVE BALLS
	 * OF WOOL ALL AT ONCE INSTEAD OF ONE BY ONE(THIS PLAYER PREFER THIS),
	 * DIALOGUES, AFTER DIALOGUES - 100% Replicated.
	 */

	@Override
	public int getQuestId() {
		return Quests.SHEEP_SHEARER;
	}

	@Override
	public String getQuestName() {
		return "Sheep shearer";
	}
	
	@Override
	public boolean isMembers() {
		return false;
	}
	
	@Override
	public void handleReward(Player player) {
		player.getInventory().add(new Item(ItemId.COINS.id(), 60));
		player.message("Well done you have completed the sheep shearer quest");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.SHEEP_SHEARER), true);
		player.message("@gre@You haved gained 1 quest point!");
	}

	private void fredDialogue(Player p, Npc n, int cID) {
		if (cID == -1) {
			switch (p.getQuestStage(this)) {
				case 0:
					npcTalk(p,
						n,
						"What are you doing on my land?",
						"You're not the one who keeps leaving all my gates open?",
						"And letting out all my sheep?");
					int choice = showMenu(p, n,
						"I'm looking for a quest",
						"I'm looking for something to kill", "I'm lost");
					if (choice == 0) {
						npcTalk(p,
							n,
							"You're after a quest, you say?",
							"Actually I could do with a bit of help",
							"My sheep are getting mighty woolly",
							"If you could shear them",
							"And while your at it spin the wool for me too",
							"Yes, that's it. Bring me 20 balls of wool",
							"And I'm sure I could sort out some sort of payment",
							"Of course, there's the small matter of the thing");
						int choice1 = showMenu(p, n,
							"Yes okay. I can do that",
							"That doesn't sound a very exciting quest",
							"What do you mean, the thing?");
						if (choice1 == 0) {
							npcTalk(p, n, "Ok I'll see you when you have some wool");
							p.updateQuestStage(getQuestId(), 1);
						} else if (choice1 == 1) {
							npcTalk(p,
								n,
								"Well what do you expect if you ask a farmer for a quest?",
								"Now are you going to help me or not?");
							int choice2 = showMenu(p, n,
								"Yes okay. I can do that",
								"No I'll give it a miss");
							if (choice2 == 0) {
								npcTalk(p, n,
									"Ok I'll see you when you have some wool");
								p.updateQuestStage(getQuestId(), 1);
							}
						} else if (choice1 == 2) {
							npcTalk(p, n, "I wouldn't worry about it",
								"Something ate all the previous shearers",
								"They probably got unlucky",
								"So are you going to help me?");
							int choice2 = showMenu(p, n,
								"Yes okay. I can do that",
								"Erm I'm a bit worried about this thing");
							if (choice2 == 0) {
								npcTalk(p, n,
									"Ok I'll see you when you have some wool");
								p.getCache().set("sheep_shearer_wool_count", 0);
								p.updateQuestStage(getQuestId(), 1);
							} else if (choice2 == 1) {
								npcTalk(p,
									n,
									"I'm sure it's nothing to worry about",
									"It's possible the other shearers aren't dead at all",
									"And are just hiding in the woods or something");
								playerTalk(p, n, "I'm not convinced");
							}
						}
					} else if (choice == 1) {
						fredDialogue(p, n, Fred.KILL);
					} else if (choice == 2) {
						fredDialogue(p, n, Fred.LOST);
					}
					break;
				case 1:
					npcTalk(p, n, "How are you doing getting those balls of wool?");
					int totalWool = 0;
					int woolCount = p.getInventory().countId(ItemId.BALL_OF_WOOL.id());
					if (p.getCache().hasKey("sheep_shearer_wool_count")) {
						totalWool = p.getCache().getInt("sheep_shearer_wool_count");
						if (totalWool + woolCount > 20) {
							woolCount = 20 - totalWool;
							totalWool = 20;
						} else {
							totalWool = woolCount + totalWool;
						}
					} else {
						p.getCache().set("sheep_shearer_wool_count", 0);
						if (woolCount > 20) {
							woolCount = 20;
							totalWool = 20;
						} else if (woolCount < 0) {
							woolCount = 0;
						} else {
							totalWool = woolCount;
						}
					}

					if (woolCount > 0) {
						playerTalk(p, n, "I have some");
						npcTalk(p, n, "Give em here then");
						for (int i = 0; i < woolCount; ++i) {
							p.getInventory().remove(ItemId.BALL_OF_WOOL.id(), 1);
							message(p, 600, "You give Fred a ball of wool");
						}
						if (totalWool >= 20) {
							playerTalk(p, n, "Thats all of them");
							npcTalk(p, n, "I guess I'd better pay you then");
							p.message("The farmer hands you some coins");
							p.sendQuestComplete(Quests.SHEEP_SHEARER);
							p.updateQuestStage(getQuestId(), -1);
							p.getCache().remove("sheep_shearer_wool_count");
						} else {
							playerTalk(p, n, "That's all I've got so far");
							p.getCache().set("sheep_shearer_wool_count", totalWool);
							npcTalk(p, n, "I need more before I can pay you");
							playerTalk(p, n, "Ok I'll work on it");
						}
					} else if (hasItem(p, ItemId.WOOL.id())) {
						playerTalk(p, n, "Well I've got some wool",
							"I've not managed to make it into a ball though");
						npcTalk(p, n, "Well go find a spinning wheel then",
							"And get spinning");
					} else {
						playerTalk(p, n, "I haven't got any at the moment");
						npcTalk(p, n, "Ah well at least you haven't been eaten");
					}
					break;
				case -1:
					npcTalk(p, n, "What are you doing on my land?");
					int choice3 = showMenu(p, n,
						"I'm looking for something to kill", "I'm lost");
					if (choice3 == 0) {
						fredDialogue(p, n, Fred.KILL);
					} else if (choice3 == 1) {
						fredDialogue(p, n, Fred.LOST);
					}
					break;
			}
		}
		switch (cID) {
			case Fred.KILL:
				npcTalk(p, n, "What on my land?",
					"Leave my livestock alone you scoundrel");
				break;
			case Fred.LOST:
				npcTalk(p, n, "How can you be lost?",
					"Just follow the road east and south",
					"You'll end up in Lumbridge fairly quickly");
				break;
		}

	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.FRED_THE_FARMER.id()) {
			fredDialogue(p, n, -1);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.FRED_THE_FARMER.id();
	}

	class Fred {
		static final int KILL = 0;
		static final int LOST = 1;
	}

}
