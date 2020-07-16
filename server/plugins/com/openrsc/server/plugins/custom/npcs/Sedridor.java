package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.custom.quests.members.RuneMysteries;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;



public class Sedridor implements TalkNpcTrigger, OpNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player,n,"Welcome, adventurer, to the world-renowned Wizards' Tower",
			"How many I help you?");

		ArrayList<String> menu = new ArrayList<>();
		menu.add("Nothing, thanks. I'm just looking around");
		if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) == -1)
			menu.add("Teleport me to the rune stone mine");
		else if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) < 2)
			menu.add("What are you doing down here?");
		else
			menu.add("Rune Mysteries");
		if (config().WANT_RUNECRAFT && player.getQuestStage(Quests.RUNE_MYSTERIES) == 1)
			menu.add("I'm looking for the head wizard.");
		int choice = multi(player,n, menu.toArray(new String[menu.size()]));
		if (choice > 0) {
			RuneMysteries.sedridorDialog(player,n, choice);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SEDRIDOR.id();
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		Npc sedridor = player.getWorld().getNpc(n.getID(),
			player.getX() - 2, player.getX() + 2,
			player.getY() - 2, player.getY() + 2);
		if (sedridor == null) return;
		RuneMysteries.sedridorDialog(player,n, 0);
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		return (n.getID() == 803 &&
			player.getConfig().WANT_RUNECRAFT &&
			player.getQuestStage(Quests.RUNE_MYSTERIES) == Quests.QUEST_STAGE_COMPLETED &&
			command.equalsIgnoreCase("teleport"));
	}
}
