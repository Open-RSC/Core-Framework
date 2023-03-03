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

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SheepShearer implements QuestInterface, TalkNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.SHEEP_SHEARER;
	}

	@Override
	public String getQuestName() {
		return "Sheep shearer";
	}

	@Override
	public int getQuestPoints() {
		return Quest.SHEEP_SHEARER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.getCarriedItems().getInventory().add(new Item(ItemId.COINS.id(), 60));
		player.message("Well done you have completed the sheep shearer quest");
		final QuestReward reward = Quest.SHEEP_SHEARER.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void fredDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player,
						n,
						"What are you doing on my land?",
						"You're not the one who keeps leaving all my gates open?",
						"And letting out all my sheep?");
					int choice = multi(player, n,
						"I'm looking for a quest",
						"I'm looking for something to kill", "I'm lost");
					if (choice == 0) {
						npcsay(player,
							n,
							"You're after a quest, you say?",
							"Actually I could do with a bit of help",
							"My sheep are getting mighty woolly",
							"If you could shear them",
							"And while your at it spin the wool for me too",
							"Yes, that's it. Bring me 20 balls of wool",
							"And I'm sure I could sort out some sort of payment",
							"Of course, there's the small matter of the thing");
						int choice1 = multi(player, n,
							"Yes okay. I can do that",
							"That doesn't sound a very exciting quest",
							"What do you mean, the thing?");
						if (choice1 == 0) {
							npcsay(player, n, "Ok I'll see you when you have some wool");
							player.updateQuestStage(getQuestId(), 1);
						} else if (choice1 == 1) {
							npcsay(player,
								n,
								"Well what do you expect if you ask a farmer for a quest?",
								"Now are you going to help me or not?");
							int choice2 = multi(player, n,
								"Yes okay. I can do that",
								"No I'll give it a miss");
							if (choice2 == 0) {
								npcsay(player, n,
									"Ok I'll see you when you have some wool");
								player.updateQuestStage(getQuestId(), 1);
							}
						} else if (choice1 == 2) {
							npcsay(player, n, "I wouldn't worry about it",
								"Something ate all the previous shearers",
								"They probably got unlucky",
								"So are you going to help me?");
							int choice2 = multi(player, n,
								"Yes okay. I can do that",
								"Erm I'm a bit worried about this thing");
							if (choice2 == 0) {
								npcsay(player, n,
									"Ok I'll see you when you have some wool");
								player.getCache().set("sheep_shearer_wool_count", 0);
								player.updateQuestStage(getQuestId(), 1);
							} else if (choice2 == 1) {
								npcsay(player,
									n,
									"I'm sure it's nothing to worry about",
									"It's possible the other shearers aren't dead at all",
									"And are just hiding in the woods or something");
								say(player, n, "I'm not convinced");
							}
						}
					} else if (choice == 1) {
						fredDialogue(player, n, Fred.KILL);
					} else if (choice == 2) {
						fredDialogue(player, n, Fred.LOST);
					}
					break;
				case 1:
					npcsay(player, n, "How are you doing getting those balls of wool?");
					int totalWool = 0;
					int woolCount = player.getCarriedItems().getInventory().countId(ItemId.BALL_OF_WOOL.id());
					if (player.getCache().hasKey("sheep_shearer_wool_count")) {
						totalWool = player.getCache().getInt("sheep_shearer_wool_count");
						if (totalWool + woolCount > 20) {
							woolCount = 20 - totalWool;
							totalWool = 20;
						} else {
							totalWool = woolCount + totalWool;
						}
					} else {
						player.getCache().set("sheep_shearer_wool_count", 0);
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
						say(player, n, "I have some");
						npcsay(player, n, "Give em here then");
						for (int i = 0; i < woolCount; ++i) {
							player.getCarriedItems().remove(new Item(ItemId.BALL_OF_WOOL.id()));
							mes("You give Fred a ball of wool");
							delay(4);
						}
						if (totalWool >= 20) {
							say(player, n, "Thats all of them");
							npcsay(player, n, "I guess I'd better pay you then");
							player.message("The farmer hands you some coins");
							player.sendQuestComplete(Quests.SHEEP_SHEARER);
							player.updateQuestStage(getQuestId(), -1);
							player.getCache().remove("sheep_shearer_wool_count");
						} else {
							say(player, n, "That's all I've got so far");
							player.getCache().set("sheep_shearer_wool_count", totalWool);
							npcsay(player, n, "I need more before I can pay you");
							say(player, n, "Ok I'll work on it");
						}
					} else if (player.getCarriedItems().hasCatalogID(ItemId.WOOL.id(), Optional.of(false))) {
						say(player, n, "Well I've got some wool",
							"I've not managed to make it into a ball though");
						npcsay(player, n, "Well go find a spinning wheel then",
							"And get spinning");
					} else {
						say(player, n, "I haven't got any at the moment");
						npcsay(player, n, "Ah well at least you haven't been eaten");
					}
					break;
				case -1:
					npcsay(player, n, "What are you doing on my land?");
					int choice3 = multi(player, n,
						"I'm looking for something to kill", "I'm lost");
					if (choice3 == 0) {
						fredDialogue(player, n, Fred.KILL);
					} else if (choice3 == 1) {
						fredDialogue(player, n, Fred.LOST);
					}
					break;
			}
		}
		switch (cID) {
			case Fred.KILL:
				npcsay(player, n, "What on my land?",
					"Leave my livestock alone you scoundrel");
				break;
			case Fred.LOST:
				npcsay(player, n, "How can you be lost?",
					"Just follow the road east and south",
					"You'll end up in Lumbridge fairly quickly");
				break;
		}

	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.FRED_THE_FARMER.id()) {
			fredDialogue(player, n, -1);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.FRED_THE_FARMER.id();
	}

	class Fred {
		static final int KILL = 0;
		static final int LOST = 1;
	}

}
