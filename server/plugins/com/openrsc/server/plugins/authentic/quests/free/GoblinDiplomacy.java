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

public class GoblinDiplomacy implements QuestInterface, TalkNpcTrigger {

	@Override
	public int getQuestId() {
		return Quests.GOBLIN_DIPLOMACY;
	}

	@Override
	public String getQuestName() {
		return "Goblin diplomacy";
	}

	@Override
	public int getQuestPoints() {
		return Quest.GOBLIN_DIPLOMACY.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the goblin diplomacy quest");
		final QuestReward reward = Quest.GOBLIN_DIPLOMACY.reward();
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		player.message("general wartface gives you a gold bar as thanks");
		player.getCarriedItems().getInventory().add(new Item(ItemId.GOLD_BAR.id(), 1));
	}

	public void onTalkNpc(Player player, final Npc n) {
		final Npc otherGoblin = n.getID() == NpcId.GENERAL_WARTFACE.id() ? player.getWorld().getNpc(NpcId.GENERAL_BENTNOZE.id(),
			314, 330, 441, 457) : player.getWorld().getNpc(NpcId.GENERAL_WARTFACE.id(), 321, 445,
			326, 449);
		if (n.getID() == NpcId.GENERAL_WARTFACE.id() || n.getID() == NpcId.GENERAL_BENTNOZE.id()) {
			if (player.getQuestStage(this) == 0) {
				if (n.getID() == NpcId.GENERAL_WARTFACE.id()) {
					npcsay(player, n, "green armour best");
					npcsay(player, otherGoblin, "No no Red every time");
					npcsay(player, n, "go away human, we busy");
				} else {
					npcsay(player, n, "Red armour best");
					npcsay(player, otherGoblin, "No no green every time");
					npcsay(player, n, "go away human, we busy");
				}
			} else if (player.getQuestStage(this) == 1) {
				if (n.getID() == NpcId.GENERAL_WARTFACE.id()) {
					npcsay(player, n, "green armour best");
					npcsay(player, otherGoblin, "No no Red every time");
					npcsay(player, n, "go away human, we busy");
				} else {
					npcsay(player, n, "Red armour best");
					npcsay(player, otherGoblin, "No no green every time");
					npcsay(player, n, "go away human, we busy");
				}

				int option = multi(player, n, false, //do not send over
					"Why are you arguing about the colour of your armour?",
					"Wouldn't you prefer peace?",
					"Do you want me to pick an armour colour for you?");
				switch (option) {
					case 0: // yes
						say(player, n, "Why are you arguing about the colour of your armour?");
						npcsay(player, n, "We decide to celebrate goblin new century",
							"By changing the colour of our armour",
							"Light blue get boring after a bit",
							"And we want change",
							"Problem is they want different change to us");
						break;
					case 1: // No
						say(player, n, "Wouldn't you prefer peace");
						npcsay(player, n,
							"Yeah peace is good as long as it is peace wearing Green armour");
						npcsay(player, otherGoblin, "But green to much like skin!",
							"Nearly make you look naked!");
						break;
					case 2:
						say(player, n, "Do you want me to pick an armour colour for you?",
							"different to either green or red");
						npcsay(player, n, "Hmm me dunno what that'd look like",
							"You'd have to bring me some, so us could decide");
						npcsay(player, otherGoblin, "Yep bring us orange armour");
						npcsay(player, n, "Yep orange might be good");
						player.updateQuestStage(getQuestId(), 2);
				}
			} else if (player.getQuestStage(this) == 2) {
				npcsay(player, n, "Oh it you");
				if (player.getCarriedItems().hasCatalogID(ItemId.ORANGE_GOBLIN_ARMOUR.id())) {
					say(player, n, "I have some orange armour");
					mes("You give some goblin armour to the goblins");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.ORANGE_GOBLIN_ARMOUR.id()));
					npcsay(player, n, "No I don't like that much");
					npcsay(player, otherGoblin, "It clashes with my skin colour");
					npcsay(player, n, "Try bringing us dark blue armour");
					player.updateQuestStage(getQuestId(), 3);
				} else {
					npcsay(player, n, "Have you got some orange goblin armour yet?");
					say(player, n, "Err no");
					npcsay(player, n, "Come back when you have some");
				}
			} else if (player.getQuestStage(this) == 3) {
				npcsay(player, n, "Oh it you");
				if (player.getCarriedItems().hasCatalogID(ItemId.BLUE_GOBLIN_ARMOUR.id())) {
					say(player, n, "I have some dark blue armour");
					mes("You give some goblin armour to the goblins");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.BLUE_GOBLIN_ARMOUR.id()));
					npcsay(player, n, "Doesn't seem quite right");
					npcsay(player, otherGoblin, "maybe if it was a bit lighter");
					npcsay(player, n, "Yeah try light blue");
					say(player, n,
						"I thought that was the amour you were changing from",
						"But never mind, anything is worth a try");
					player.updateQuestStage(getQuestId(), 4);
				} else {
					npcsay(player, n, "Have you got some Dark Blue goblin armour yet?");
					say(player, n, "Err no");
					npcsay(player, n, "Come back when you have some");
				}
			} else if (player.getQuestStage(this) == 4) {
				if (player.getCarriedItems().hasCatalogID(ItemId.GOBLIN_ARMOUR.id())) {
					say(player, n, "Ok I've got light blue armour");
					mes("You give some goblin armour to the goblins");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.GOBLIN_ARMOUR.id()));
					npcsay(player, n, "That is rather nice");
					npcsay(player, otherGoblin,
						"Yes I could see myself wearing somethin' like that");
					npcsay(player, n, "It' a deal then", "Light blue it is",
						"Thank you for sorting our argument");

					player.sendQuestComplete(Quests.GOBLIN_DIPLOMACY);
				} else {
					npcsay(player, n, "Have you got some Light Blue goblin armour yet?");
					say(player, n, "Err no");
					npcsay(player, n, "Come back when you have some");
				}
			} else if (player.getQuestStage(this) == -1) { // COMPLETED
				npcsay(player, n,
					"Now you've solved our argument we gotta think of something else to do");
				npcsay(player, otherGoblin, "Yep, we bored now");
			}
		} else if (n.getID() == NpcId.GOBLIN_RED_ARMOUR_LVL13.id()) {
			npcsay(player, n, "Red Armour best");
			int gobopt = multi(player, n, "Err Ok", "Why is red best?");
			if (gobopt == 1) {
				npcsay(player, n, "Cos General Bentnoze says so",
					"And he bigger than me");
			}
		} else if (n.getID() == NpcId.GOBLIN_GREEN_ARMOUR_LVL13.id()) {
			npcsay(player, n, "green Armour best");
			int gobopt = multi(player, n, "Err Ok", "Why is green best?");
			if (gobopt == 1) {
				npcsay(player, n, "I forgot now",
					"but General Wartface says it is",
					"So it must be");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GENERAL_WARTFACE.id() || n.getID() == NpcId.GENERAL_BENTNOZE.id()
			|| n.getID() == NpcId.GOBLIN_RED_ARMOUR_LVL13.id()
			|| n.getID() == NpcId.GOBLIN_GREEN_ARMOUR_LVL13.id();
	}
}
