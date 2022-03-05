package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.*;

public class Guildmaster implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.GUILDMASTER.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {

		ArrayList<String> masterOptions = new ArrayList<>();
		masterOptions.add("What is this place?");
		boolean canStartQuest = player.getConfig().BASED_MAP_DATA >= 23 && player.getClientVersion() >= 73;
		if (canStartQuest) {
			masterOptions.add("Do you know know where I could get a rune plate mail body?");
		}
		String[] finalMasterOptions = new String[masterOptions.size()];

		int option = 0;
		if (finalMasterOptions.length > 1) {
			option = multi(player, n, false, //do not send over
				masterOptions.toArray(finalMasterOptions));
		}

		if (option == 0) {
			say(player, n, "What is this place?");
			npcsay(player,
				n,
				"This is the champions' guild",
				"Only Adventurers who have proved themselves worthy",
				"by  gaining influence from quests are allowed in here",
				"As the number of quests in the world rises",
				"So will the requirements to get in here",
				"But so will the rewards");

		} else if (option == 1 && canStartQuest) {
			say(player, n, "Do you know where I could get a rune plate mail body?");
			npcsay(player,
				n,
				"I have a friend called Oziach who lives by the cliffs",
				"He has a supply of rune plate mail",
				"He may sell you some if you're lucky, he can be a little strange sometimes though");
			if (player.getQuestStage(Quests.DRAGON_SLAYER) == 0) {
				player.updateQuestStage(Quests.DRAGON_SLAYER, 1);
			}
		}
	}

}
