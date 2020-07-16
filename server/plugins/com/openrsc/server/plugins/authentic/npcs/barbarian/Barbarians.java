package com.openrsc.server.plugins.authentic.npcs.barbarian;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Barbarians implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARBARIAN.id() || n.getID() == NpcId.GUNTHOR_THE_BRAVE.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		say(player, n, "Hello");
		int randomDiag = DataConversions.random(0, 11);
		if (randomDiag == 0) {
			npcsay(player, n, "Go away",
				"This is our village");
		} else if (randomDiag == 1) {
			npcsay(player, n, "Hello");
		} else if (randomDiag == 2) {
			npcsay(player, n, "Wanna fight?");
			n.startCombat(player);
		} else if (randomDiag == 3) {
			npcsay(player, n, "Who are you?");
			say(player, n, "I'm a bold adventurer");
			npcsay(player, n, "You don't look very strong");
		} else if (randomDiag == 4) {
			player.message("The barbarian grunts");
		} else if (randomDiag == 5) {
			npcsay(player, n, "Good day, my dear fellow");
		} else if (randomDiag == 6) {
			npcsay(player, n, "ug");
		} else if (randomDiag == 7) {
			npcsay(player, n, "I'm a little busy right now",
				"We're getting ready for our next barbarian raid");
		} else if (randomDiag == 8) {
			npcsay(player, n, "Beer?");
		} else if (randomDiag == 9) {
			player.message("The barbarian ignores you");
		} else if (randomDiag == 10) {
			npcsay(player, n, "Grr");
		} else if (randomDiag == 11) {
			npcsay(player, n, "Bones?");
			player.message("The barbarian gives you some bones");
			give(player, ItemId.BONES.id(), 1);
			say(player, n, "Err, thanks");
		}
	}
}
