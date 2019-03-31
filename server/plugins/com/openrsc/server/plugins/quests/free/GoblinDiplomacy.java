package com.openrsc.server.plugins.quests.free;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class GoblinDiplomacy implements QuestInterface, TalkToNpcListener,
	TalkToNpcExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.GOBLIN_DIPLOMACY;
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
		incQuestReward(player, Quests.questData.get(Quests.GOBLIN_DIPLOMACY), true);
		player.message("general wartface gives you a gold bar as thanks");
		player.getInventory().add(new Item(ItemId.GOLD_BAR.id(), 1));
	}

	public void onTalkToNpc(Player p, final Npc n) {
		final Npc otherGoblin = n.getID() == NpcId.GENERAL_WARTFACE.id() ? World.getWorld().getNpc(NpcId.GENERAL_BENTNOZE.id(),
			314, 330, 441, 457) : World.getWorld().getNpc(NpcId.GENERAL_WARTFACE.id(), 321, 445,
			326, 449);
		if (n.getID() == NpcId.GENERAL_WARTFACE.id() || n.getID() == NpcId.GENERAL_BENTNOZE.id()) {
			if (p.getQuestStage(this) == 0) {
				if (n.getID() == NpcId.GENERAL_WARTFACE.id()) {
					npcTalk(p, n, "green armour best");
					npcTalk(p, otherGoblin, "No no Red every time");
					npcTalk(p, n, "go away human, we busy");
				} else {
					npcTalk(p, n, "Red armour best");
					npcTalk(p, otherGoblin, "No no green every time");
					npcTalk(p, n, "go away human, we busy");
				}
			} else if (p.getQuestStage(this) == 1) {
				if (n.getID() == NpcId.GENERAL_WARTFACE.id()) {
					npcTalk(p, n, "green armour best");
					npcTalk(p, otherGoblin, "No no Red every time");
					npcTalk(p, n, "go away human, we busy");
				} else {
					npcTalk(p, n, "Red armour best");
					npcTalk(p, otherGoblin, "No no green every time");
					npcTalk(p, n, "go away human, we busy");
				}

				int option = showMenu(p, n, false, //do not send over
					"Why are you arguing about the colour of your armour?",
					"Wouldn't you prefer peace?",
					"Do you want me to pick an armour colour for you?");
				switch (option) {
					case 0: // yes
						playerTalk(p, n, "Why are you arguing about the colour of your armour?");
						npcTalk(p, n, "We decide to celebrate goblin new century",
							"By changing the colour of our armour",
							"Light blue get boring after a bit",
							"And we want change",
							"Problem is they want different change to us");
						break;
					case 1: // No
						playerTalk(p, n, "Wouldn't you prefer peace");
						npcTalk(p, n,
							"Yeah peace is good as long as it is peace wearing Green armour");
						npcTalk(p, otherGoblin, "But green to much like skin!",
							"Nearly make you look naked!");
						break;
					case 2:
						playerTalk(p, n, "Do you want me to pick an armour colour for you?",
							"different to either green or red");
						npcTalk(p, n, "Hmm me dunno what that'd look like",
							"You'd have to bring me some, so us could decide");
						npcTalk(p, otherGoblin, "Yep bring us orange armour");
						npcTalk(p, n, "Yep orange might be good");
						p.updateQuestStage(getQuestId(), 2);
				}
			} else if (p.getQuestStage(this) == 2) {
				npcTalk(p, n, "Oh it you");
				if (p.getInventory().hasItemId(ItemId.ORANGE_GOBLIN_ARMOUR.id())) {
					playerTalk(p, n, "I have some orange armour");
					message(p, "You give some goblin armour to the goblins");
					p.getInventory().remove(ItemId.ORANGE_GOBLIN_ARMOUR.id(), 1);
					npcTalk(p, n, "No I don't like that much");
					npcTalk(p, otherGoblin, "It clashes with my skin colour");
					npcTalk(p, n, "Try bringing us dark blue armour");
					p.updateQuestStage(getQuestId(), 3);
				} else {
					npcTalk(p, n, "Have you got some orange goblin armour yet?");
					playerTalk(p, n, "Err no");
					npcTalk(p, n, "Come back when you have some");
				}
			} else if (p.getQuestStage(this) == 3) {
				npcTalk(p, n, "Oh it you");
				if (p.getInventory().hasItemId(ItemId.BLUE_GOBLIN_ARMOUR.id())) {
					playerTalk(p, n, "I have some dark blue armour");
					message(p, "You give some goblin armour to the goblins");
					p.getInventory().remove(ItemId.BLUE_GOBLIN_ARMOUR.id(), 1);
					npcTalk(p, n, "Doesn't seem quite right");
					npcTalk(p, otherGoblin, "maybe if it was a bit lighter");
					npcTalk(p, n, "Yeah try light blue");
					playerTalk(p, n,
						"I thought that was the amour you were changing from",
						"But never mind, anything is worth a try");
					p.updateQuestStage(getQuestId(), 4);
				} else {
					npcTalk(p, n, "Have you got some Dark Blue goblin armour yet?");
					playerTalk(p, n, "Err no");
					npcTalk(p, n, "Come back when you have some");
				}
			} else if (p.getQuestStage(this) == 4) {
				if (p.getInventory().hasItemId(ItemId.GOBLIN_ARMOUR.id())) {
					playerTalk(p, n, "Ok I've got light blue armour");
					message(p, "You give some goblin armour to the goblins");
					p.getInventory().remove(ItemId.GOBLIN_ARMOUR.id(), 1);
					npcTalk(p, n, "That is rather nice");
					npcTalk(p, otherGoblin,
						"Yes I could see myself wearing somethin' like that");
					npcTalk(p, n, "It' a deal then", "Light blue it is",
						"Thank you for sorting our argument");

					p.sendQuestComplete(Constants.Quests.GOBLIN_DIPLOMACY);
				} else {
					npcTalk(p, n, "Have you got some Light Blue goblin armour yet?");
					playerTalk(p, n, "Err no");
					npcTalk(p, n, "Come back when you have some");
				}
			} else if (p.getQuestStage(this) == -1) { // COMPLETED
				npcTalk(p, n,
					"Now you've solved our argument we gotta think of something else to do");
				npcTalk(p, otherGoblin, "Yep, we bored now");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.GENERAL_WARTFACE.id() || n.getID() == NpcId.GENERAL_BENTNOZE.id();
	}
}
