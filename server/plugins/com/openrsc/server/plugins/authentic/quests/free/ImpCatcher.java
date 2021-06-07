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

public class ImpCatcher implements QuestInterface, TalkNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.IMP_CATCHER;
	}

	@Override
	public String getQuestName() {
		return "Imp catcher";
	}

	@Override
	public int getQuestPoints() {
		return Quest.IMP_CATCHER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done. You have completed the Imp catcher quest");
		final QuestReward reward = Quest.IMP_CATCHER.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
	}

	/**
	 * // COORDS FOR KARAMJA CAVE: 425, 684
	 */
	@Override
	public void onTalkNpc(Player player, final Npc n) {
		if (n.getID() == NpcId.WIZARD_MIZGOG.id()) {
			if (player.getQuestStage(this) == 1) {
				npcsay(player, n, "So how are you doing finding my beads?");
				if (!player.getCarriedItems().hasCatalogID(ItemId.RED_BEAD.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.YELLOW_BEAD.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.BLACK_BEAD.id(), Optional.of(false))
					&& !player.getCarriedItems().hasCatalogID(ItemId.WHITE_BEAD.id(), Optional.of(false))) { // NEED TO GET INFO PROBABLY SAME
					// AS PRINCE ALI.
					say(player, n, "I've not found any yet");
					npcsay(player,
						n,
						"Well get on with it",
						"I've lost a white bead, a red bead, a black bead and a yellow bead",
						"Go kill some imps");
					return;
				}
				if (player.getCarriedItems().hasCatalogID(ItemId.RED_BEAD.id(), Optional.of(false))
					&& player.getCarriedItems().hasCatalogID(ItemId.YELLOW_BEAD.id(), Optional.of(false))
					&& player.getCarriedItems().hasCatalogID(ItemId.BLACK_BEAD.id(), Optional.of(false))
					&& player.getCarriedItems().hasCatalogID(ItemId.WHITE_BEAD.id(), Optional.of(false))) {
					say(player, n, "I've got all four beads",
						"It was hard work I can tell you");
					npcsay(player, n, "Give them here and I'll sort out a reward");
					mes("You give four coloured beads to Wizard Mizgog");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.RED_BEAD.id()));
					player.getCarriedItems().remove(new Item(ItemId.YELLOW_BEAD.id()));
					player.getCarriedItems().remove(new Item(ItemId.BLACK_BEAD.id()));
					player.getCarriedItems().remove(new Item(ItemId.WHITE_BEAD.id()));
					npcsay(player, n, "Here's you're reward then",
						"An Amulet of accuracy");
					mes("The Wizard hands you an amulet");
					delay(3);
					give(player, ItemId.AMULET_OF_ACCURACY.id(), 1);
					player.sendQuestComplete(Quests.IMP_CATCHER);
				} else if (player.getCarriedItems().hasCatalogID(ItemId.RED_BEAD.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.YELLOW_BEAD.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.BLACK_BEAD.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.WHITE_BEAD.id(), Optional.of(false))) {
					say(player, n, "I have found some of your beads");
					npcsay(player, n, "Come back when you have them all",
						"The four colours of beads I need",
						"Are red,yellow,black and white",
						"Go chase some imps");
				}

				return;
			} else if (player.getQuestStage(this) == 0) {
				npcsay(player, n, "Hello there");
				int choice = multi(player, n, "Give me a quest!",
					"Most of your friends are pretty quiet aren't they?");
				if (choice == 0) {
					npcsay(player, n, "Give me a quest what?");
					int choice2 = multi(player, n,
						"Give me a quest please",
						"Give me a quest or else",
						"Just stop messing around and give me a quest");
					if (choice2 == 0) {
						npcsay(player,
							n,
							"Well seeing as you asked nicely",
							"I could do with some help",
							"The wizard Grayzag next door decided he didn't like me",
							"So he cast of spell of summoning",
							"And summoned hundreds of little imps",
							"These imps stole all sorts of my things",
							"Most of these things I don't really care about",
							"They're just eggs and balls of string and things",
							"But they stole my 4 magical beads",
							"There was a red one, a yellow one, a black one and a white one",
							"These imps have now spread out all over the kingdom",
							"Could you get my beads back for me");
						say(player, n, "I'll try");
						player.updateQuestStage(Quests.IMP_CATCHER, 1);
					} else if (choice2 == 1) {
						npcsay(player, n, "Or else what? You'll attack me?",
							"Hahaha");
					} else if (choice2 == 2) {
						npcsay(player, n,
							"Ah now you're assuming I have one to give");
					}
				} else if (choice == 1) {
					npcsay(player, n,
						"Yes they've mostly got their heads in the clouds",
						"Thinking about magic");
				}
			} else if (player.getQuestStage(this) == -1) { // Complete
				say(player, n, "Most of your friends are pretty quiet aren't they?");
				npcsay(player, n, "Yes they've mostly got their heads in the clouds",
					"Thinking about magic");
				int choice = multi(player, n,
					"Got any more quests?",
					"Most of your friends are pretty quiet aren't they?");
				if (choice == 0)
					npcsay(player, n, "No Everything is good with the world today");
				else if (choice == 1)
					npcsay(player, n, "Yes they've mostly got their heads in the clouds",
						"Thinking about magic");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WIZARD_MIZGOG.id();
	}
}
