package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
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
	public boolean isMembers() {
		return false;
	}

	@Override
	public void handleReward(Player player) {
		player.message("Well done you have completed the goblin diplomacy quest");
		player.message("@gre@You haved gained 5 quest points!");
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.GOBLIN_DIPLOMACY), true);
		player.message("general wartface gives you a gold bar as thanks");
		player.getCarriedItems().getInventory().add(new Item(ItemId.GOLD_BAR.id(), 1));
	}

	public void onTalkNpc(Player p, final Npc n) {
		final Npc otherGoblin = n.getID() == NpcId.GENERAL_WARTFACE.id() ? p.getWorld().getNpc(NpcId.GENERAL_BENTNOZE.id(),
			314, 330, 441, 457) : p.getWorld().getNpc(NpcId.GENERAL_WARTFACE.id(), 321, 445,
			326, 449);
		if (n.getID() == NpcId.GENERAL_WARTFACE.id() || n.getID() == NpcId.GENERAL_BENTNOZE.id()) {
			if (p.getQuestStage(this) == 0) {
				if (n.getID() == NpcId.GENERAL_WARTFACE.id()) {
					npcsay(p, n, "green armour best");
					npcsay(p, otherGoblin, "No no Red every time");
					npcsay(p, n, "go away human, we busy");
				} else {
					npcsay(p, n, "Red armour best");
					npcsay(p, otherGoblin, "No no green every time");
					npcsay(p, n, "go away human, we busy");
				}
			} else if (p.getQuestStage(this) == 1) {
				if (n.getID() == NpcId.GENERAL_WARTFACE.id()) {
					npcsay(p, n, "green armour best");
					npcsay(p, otherGoblin, "No no Red every time");
					npcsay(p, n, "go away human, we busy");
				} else {
					npcsay(p, n, "Red armour best");
					npcsay(p, otherGoblin, "No no green every time");
					npcsay(p, n, "go away human, we busy");
				}

				int option = multi(p, n, false, //do not send over
					"Why are you arguing about the colour of your armour?",
					"Wouldn't you prefer peace?",
					"Do you want me to pick an armour colour for you?");
				switch (option) {
					case 0: // yes
						say(p, n, "Why are you arguing about the colour of your armour?");
						npcsay(p, n, "We decide to celebrate goblin new century",
							"By changing the colour of our armour",
							"Light blue get boring after a bit",
							"And we want change",
							"Problem is they want different change to us");
						break;
					case 1: // No
						say(p, n, "Wouldn't you prefer peace");
						npcsay(p, n,
							"Yeah peace is good as long as it is peace wearing Green armour");
						npcsay(p, otherGoblin, "But green to much like skin!",
							"Nearly make you look naked!");
						break;
					case 2:
						say(p, n, "Do you want me to pick an armour colour for you?",
							"different to either green or red");
						npcsay(p, n, "Hmm me dunno what that'd look like",
							"You'd have to bring me some, so us could decide");
						npcsay(p, otherGoblin, "Yep bring us orange armour");
						npcsay(p, n, "Yep orange might be good");
						p.updateQuestStage(getQuestId(), 2);
				}
			} else if (p.getQuestStage(this) == 2) {
				npcsay(p, n, "Oh it you");
				if (p.getCarriedItems().hasCatalogID(ItemId.ORANGE_GOBLIN_ARMOUR.id())) {
					say(p, n, "I have some orange armour");
					mes(p, "You give some goblin armour to the goblins");
					p.getCarriedItems().remove(new Item(ItemId.ORANGE_GOBLIN_ARMOUR.id()));
					npcsay(p, n, "No I don't like that much");
					npcsay(p, otherGoblin, "It clashes with my skin colour");
					npcsay(p, n, "Try bringing us dark blue armour");
					p.updateQuestStage(getQuestId(), 3);
				} else {
					npcsay(p, n, "Have you got some orange goblin armour yet?");
					say(p, n, "Err no");
					npcsay(p, n, "Come back when you have some");
				}
			} else if (p.getQuestStage(this) == 3) {
				npcsay(p, n, "Oh it you");
				if (p.getCarriedItems().hasCatalogID(ItemId.BLUE_GOBLIN_ARMOUR.id())) {
					say(p, n, "I have some dark blue armour");
					mes(p, "You give some goblin armour to the goblins");
					p.getCarriedItems().remove(new Item(ItemId.BLUE_GOBLIN_ARMOUR.id()));
					npcsay(p, n, "Doesn't seem quite right");
					npcsay(p, otherGoblin, "maybe if it was a bit lighter");
					npcsay(p, n, "Yeah try light blue");
					say(p, n,
						"I thought that was the amour you were changing from",
						"But never mind, anything is worth a try");
					p.updateQuestStage(getQuestId(), 4);
				} else {
					npcsay(p, n, "Have you got some Dark Blue goblin armour yet?");
					say(p, n, "Err no");
					npcsay(p, n, "Come back when you have some");
				}
			} else if (p.getQuestStage(this) == 4) {
				if (p.getCarriedItems().hasCatalogID(ItemId.GOBLIN_ARMOUR.id())) {
					say(p, n, "Ok I've got light blue armour");
					mes(p, "You give some goblin armour to the goblins");
					p.getCarriedItems().remove(new Item(ItemId.GOBLIN_ARMOUR.id()));
					npcsay(p, n, "That is rather nice");
					npcsay(p, otherGoblin,
						"Yes I could see myself wearing somethin' like that");
					npcsay(p, n, "It' a deal then", "Light blue it is",
						"Thank you for sorting our argument");

					p.sendQuestComplete(Quests.GOBLIN_DIPLOMACY);
				} else {
					npcsay(p, n, "Have you got some Light Blue goblin armour yet?");
					say(p, n, "Err no");
					npcsay(p, n, "Come back when you have some");
				}
			} else if (p.getQuestStage(this) == -1) { // COMPLETED
				npcsay(p, n,
					"Now you've solved our argument we gotta think of something else to do");
				npcsay(p, otherGoblin, "Yep, we bored now");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.GENERAL_WARTFACE.id() || n.getID() == NpcId.GENERAL_BENTNOZE.id();
	}
}
