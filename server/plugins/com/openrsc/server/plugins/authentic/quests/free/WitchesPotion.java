package com.openrsc.server.plugins.authentic.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class WitchesPotion implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	KillNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.WITCHS_POTION;
	}

	@Override
	public String getQuestName() {
		return "Witch's potion";
	}

	@Override
	public int getQuestPoints() {
		return Quest.WITCHS_POTION.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the witches potion quest");
		final QuestReward reward = Quest.WITCHS_POTION.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	private void hettyDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			switch (player.getQuestStage(this)) {
				case 0:
					npcsay(player, n, "Greetings Traveller",
						"What could you want with an old woman like me?");
					int choice = multi(player, n,
						"I am in search of a quest",
						"I've heard that you are a witch");
					if (choice == 0) {
						npcsay(player, n, "Hmm maybe I can think of something for you",
							"Would you like to become more proficient in the dark arts?");
						int choice2 = multi(player, n, false, //do not send over
							"Yes help me become one with my darker side",
							"No I have my principles and honour",
							"What you mean improve my magic?");
						if (choice2 == 0) {
							say(player, n, "Yes help me become one with my darker side");
							hettyDialogue(player, n, Hetty.SOUNDOFIT_ALRIGHT);
						} else if (choice2 == 1) {
							say(player, n, "No, I have my principles and honour");
							npcsay(player, n, "Suit yourself, but you're missing out");
						} else if (choice2 == 2) {
							say(player, n, "What you mean improve my magic?");
							npcsay(player, n, "Yes improve your magic",
								"Do you have no sense of drama?");
							int choice4 = multi(player, n,
								"Yes I'd like to improve my magic",
								"No I'm not interested",
								"Show me the mysteries of the dark arts");
							if (choice4 == 0) {
								player.message("The witch sighs");
								hettyDialogue(player, n, Hetty.SOUNDOFIT_ALRIGHT);
							} else if (choice4 == 1) {
								npcsay(player, n, "Many aren't to start off with",
									"But I think you'll be drawn back to this place");
							} else if (choice4 == 2) {
								hettyDialogue(player, n, Hetty.SOUNDOFIT_ALRIGHT);
							}
						}
					} else if (choice == 1) {
						npcsay(player,
							n,
							"Yes it does seem to be getting fairly common knowledge",
							"I fear I may get a visit from the witch hunters of Falador before long");
					}
					break;
				case 1:
					npcsay(player, n, "So have you found the things for the potion");
					if (player.getCarriedItems().hasCatalogID(ItemId.RATS_TAIL.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.EYE_OF_NEWT.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.BURNTMEAT.id())
						&& player.getCarriedItems().hasCatalogID(ItemId.ONION.id())) {
						say(player, n, "Yes I have everthing");
						npcsay(player, n, "Excellent, can I have them then?");
						player.message("You pass the ingredients to Hetty");
						player.getCarriedItems().remove(new Item(ItemId.RATS_TAIL.id()));
						player.getCarriedItems().remove(new Item(ItemId.EYE_OF_NEWT.id()));
						player.getCarriedItems().remove(new Item(ItemId.BURNTMEAT.id()));
						player.getCarriedItems().remove(new Item(ItemId.ONION.id()));
						mes("Hetty put's all the ingredients in her cauldron");
						delay(3);
						mes("Hetty closes her eyes and begins to chant");
						delay(3);
						npcsay(player, n, "Ok drink from the cauldron");
						delay(3);
						player.updateQuestStage(getQuestId(), 2);
					} else {
						say(player, n, "No not yet");
						npcsay(player, n, "Well remember you need to get",
							"An eye of newt, a rat's tail,some burnt meat and an onion");
					}
					break;
				case 2:
					npcsay(player, n, "Greetings Traveller",
						"Well are you going to drink the potion or not?");
					break;
				case -1:
					npcsay(player, n, "Greetings Traveller",
						"How's your magic coming along?");
					say(player, n, "I'm practicing and slowly getting better");
					npcsay(player, n, "good good");
					break;
			}
		}
		switch (cID) {
			case Hetty.SOUNDOFIT_ALRIGHT:
				npcsay(player,
					n,
					"Ok I'm going to make a potion to help bring out your darker self",
					"So that you can perform acts of  dark magic with greater ease",
					"You will need certain ingredients");
				say(player, n, "What do I need");
				npcsay(player, n,
					"You need an eye of newt, a rat's tail, an onion and a piece of burnt meat");
				player.updateQuestStage(getQuestId(), 1);
				break;
		}
	}

	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.HETTY.id()) {
			hettyDialogue(player, n, -1);
		} /*else if (n.getID() == NpcId.RAT_WITCHES_POTION.id()) { // This is not proven to be authentic, the earliest reference for this is Moparscape Classic Punkrocker's quest version from July 2009
			if (p.getQuestStage(this) >= -1) {
				p.message("Rats can't talk!");
			}
		}*/
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (command.equals("drink from") && obj.getID() == 147
			&& obj.getX() == 316 && obj.getY() == 666) {
			if (player.getQuestStage(this) != 2) {
				say(player, null, "I'd rather not",
					"It doesn't look very tasty");
			} else {
				mes("You drink from the cauldron");
				delay(3);
				mes("You feel yourself imbued with power");
				delay(3);
				player.sendQuestComplete(Quests.WITCHS_POTION);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.HETTY.id() /*|| n.getID() == NpcId.RAT_WITCHES_POTION.id()*/;
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return obj.getID() == 147 && command.equals("drink from");
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.RAT_WITCHES_POTION.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (player.getQuestStage(this) >= 1) {
			player.getWorld().registerItem(new GroundItem(player.getWorld(), ItemId.RATS_TAIL.id(), n.getX(), n.getY(), 1, player));
		}
	}

	class Hetty {
		public static final int SOUNDOFIT_ALRIGHT = 0;
	}
}
