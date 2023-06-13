package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.quests.free.PeelingTheOnion;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.custom.quests.members.RuneMysteries;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;



public class Sedridor implements TalkNpcTrigger, OpNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (handlePeelingTheOnion(player, npc)) {
			return;
		}

		npcsay(player,npc,"Welcome, adventurer, to the world-renowned Wizards' Tower",
			"How many I help you?");

		ArrayList<String> menu = new ArrayList<>();
		// option 0
		menu.add("Nothing, thanks. I'm just looking around");

		// option 1
		if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) == -1)
			menu.add("Teleport me to the rune stone mine");
		else if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) < 2)
			menu.add("What are you doing down here?");
		else
			menu.add("Rune Mysteries");

		// option 2
		boolean isRuneMysteries = true;
		if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) == 1) {
			menu.add("I'm looking for the head wizard.");
		} else if (config().WANT_CUSTOM_QUESTS) {
			isRuneMysteries = false;
			switch(player.getQuestStage(Quests.PEELING_THE_ONION)) {
				case PeelingTheOnion.STATE_NOT_BEGUN:
					if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) == -1) {
						menu.add("Do you have any other quests for me?");
					} else {
						menu.add("Do you have anything you need doing?");
					}
					break;
				case PeelingTheOnion.STATE_STARTED_QUEST_WITH_KRESH:
					menu.add("Have you been sending people to bother an ogre?");
					break;
				case PeelingTheOnion.STATE_STARTED_QUEST_WITH_SEDRIDOR:
				case PeelingTheOnion.STATE_SEDRIDOR_SUGGESTED_YOU_VISIT_MAKE_OVER_MAGE:
					menu.add("What was I supposed to do again?");
					break;
				case PeelingTheOnion.STATE_STARTED_QUEST_WITH_SEDRIDOR_CONFRONTED_KRESH:
					menu.add("I've been to see the ogre");
					break;
				case PeelingTheOnion.STATE_PLAYER_CONSIDERS_OGRE:
					menu.add("I've reconsidered and I'm ready to become an ogre...");
					break;
				default:
					isRuneMysteries = true;
			}
		}

		int choice = multi(player,npc, menu.toArray(new String[menu.size()]));
		if (choice <= 0) return;
		if (isRuneMysteries || choice == 1) {
			RuneMysteries.sedridorDialog(player, npc, choice);
		} else {
			PeelingTheOnion.sedridorDialogue(player, npc);
		}
	}

	private boolean handlePeelingTheOnion(Player player, Npc npc) {
		if (config().WANT_CUSTOM_QUESTS && player.getQuestStage(Quests.PEELING_THE_ONION) >= PeelingTheOnion.STATE_A_NEW_OGRE) {
			PeelingTheOnion.sedridorDialogue(player, npc);
			return true;
		}
		if (player.getCache().hasKey("sedridor_post_kresh_quest_dialogue")) {
			player.getCache().remove("sedridor_post_kresh_quest_dialogue");
			PeelingTheOnion.sedridorDialogue(player, npc);
			return true;
		}
		return false;
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.SEDRIDOR.id();
	}

	@Override
	public void onOpNpc(Player player, Npc npc, String command) {
		Npc sedridor = player.getWorld().getNpc(npc.getID(),
			player.getX() - 2, player.getX() + 2,
			player.getY() - 2, player.getY() + 2);
		if (sedridor == null) return;
		RuneMysteries.sedridorDialog(player,npc, 0);
	}

	@Override
	public boolean blockOpNpc(Player player, Npc npc, String command) {
		return (npc.getID() == NpcId.SEDRIDOR.id() &&
			player.getConfig().WANT_RUNECRAFT &&
			player.getQuestStage(Quests.RUNE_MYSTERIES) == Quests.QUEST_STAGE_COMPLETED &&
			command.equalsIgnoreCase("teleport"));
	}
}
