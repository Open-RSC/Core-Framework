package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
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
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player p) {
		p.message("Well done. You have completed the Imp catcher quest");
		incQuestReward(p, p.getWorld().getServer().getConstants().getQuests().questData.get(Quests.IMP_CATCHER), true);
		p.message("@gre@You haved gained 1 quest point!");
	}

	/**
	 * // COORDS FOR KARAMJA CAVE: 425, 684
	 */
	@Override
	public void onTalkNpc(Player p, final Npc n) {
		if (n.getID() == NpcId.WIZARD_MIZGOG.id()) {
			if (p.getQuestStage(this) == 1) {
				npcsay(p, n, "So how are you doing finding my beads?");
				if (!p.getCarriedItems().hasCatalogID(ItemId.RED_BEAD.id(), Optional.of(false))
					&& !p.getCarriedItems().hasCatalogID(ItemId.YELLOW_BEAD.id(), Optional.of(false))
					&& !p.getCarriedItems().hasCatalogID(ItemId.BLACK_BEAD.id(), Optional.of(false))
					&& !p.getCarriedItems().hasCatalogID(ItemId.WHITE_BEAD.id(), Optional.of(false))) { // NEED TO GET INFO PROBABLY SAME
					// AS PRINCE ALI.
					say(p, n, "I've not found any yet");
					npcsay(p,
						n,
						"Well get on with it",
						"I've lost a white bead, a red bead, a black bead and a yellow bead",
						"Go kill some imps");
					return;
				}
				if (p.getCarriedItems().hasCatalogID(ItemId.RED_BEAD.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.YELLOW_BEAD.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.BLACK_BEAD.id(), Optional.of(false))
					&& p.getCarriedItems().hasCatalogID(ItemId.WHITE_BEAD.id(), Optional.of(false))) {
					say(p, n, "I've got all four beads",
						"It was hard work I can tell you");
					npcsay(p, n, "Give them here and I'll sort out a reward");
					Functions.mes(p, "You give four coloured beads to Wizard Mizgog");
					p.getCarriedItems().remove(new Item(ItemId.RED_BEAD.id()));
					p.getCarriedItems().remove(new Item(ItemId.YELLOW_BEAD.id()));
					p.getCarriedItems().remove(new Item(ItemId.BLACK_BEAD.id()));
					p.getCarriedItems().remove(new Item(ItemId.WHITE_BEAD.id()));
					npcsay(p, n, "Here's you're reward then",
						"An Amulet of accuracy");
					Functions.mes(p, "The Wizard hands you an amulet");
					give(p, ItemId.AMULET_OF_ACCURACY.id(), 1);
					p.sendQuestComplete(Quests.IMP_CATCHER);
				} else if (p.getCarriedItems().hasCatalogID(ItemId.RED_BEAD.id(), Optional.of(false))
					|| p.getCarriedItems().hasCatalogID(ItemId.YELLOW_BEAD.id(), Optional.of(false))
					|| p.getCarriedItems().hasCatalogID(ItemId.BLACK_BEAD.id(), Optional.of(false))
					|| p.getCarriedItems().hasCatalogID(ItemId.WHITE_BEAD.id(), Optional.of(false))) {
					say(p, n, "I have found some of your beads");
					npcsay(p, n, "Come back when you have them all",
						"The four colours of beads I need",
						"Are red,yellow,black and white",
						"Go chase some imps");
				}

				return;
			} else if (p.getQuestStage(this) == 0) {
				npcsay(p, n, "Hello there");
				int choice = multi(p, n, "Give me a quest!",
					"Most of your friends are pretty quiet aren't they?");
				if (choice == 0) {
					npcsay(p, n, "Give me a quest what?");
					int choice2 = multi(p, n,
						"Give me a quest please",
						"Give me a quest or else",
						"Just stop messing around and give me a quest");
					if (choice2 == 0) {
						npcsay(p,
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
						say(p, n, "I'll try");
						p.updateQuestStage(Quests.IMP_CATCHER, 1);
					} else if (choice2 == 1) {
						npcsay(p, n, "Or else what? You'll attack me?",
							"Hahaha");
					} else if (choice2 == 2) {
						npcsay(p, n,
							"Ah now you're assuming I have one to give");
					}
				} else if (choice == 1) {
					npcsay(p, n,
						"Yes they've mostly got their heads in the clouds",
						"Thinking about magic");
				}
			} else if (p.getQuestStage(this) == -1) { // Complete
				say(p, n, "Most of your friends are pretty quiet aren't they?");
				npcsay(p, n, "Yes they've mostly got their heads in the clouds",
					"Thinking about magic");
				int choice = multi(p, n,
					"Got any more quests?",
					"Most of your friends are pretty quiet aren't they?");
				if (choice == 0)
					npcsay(p, n, "No Everything is good with the world today");
				else if (choice == 1)
					npcsay(p, n, "Yes they've mostly got their heads in the clouds",
						"Thinking about magic");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.WIZARD_MIZGOG.id();
	}
}
