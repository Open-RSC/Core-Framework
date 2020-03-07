package com.openrsc.server.plugins.npcs.barbarian;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;

import com.openrsc.server.constants.NpcId;

public class Barbarians implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BARBARIAN.id() || n.getID() == NpcId.GUNTHOR_THE_BRAVE.id();
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		Functions.say(p, n, "Hello");
		int randomDiag = DataConversions.random(0, 10);
		if (randomDiag == 0) {
			npcsay(p, n, "Go away",
				"This is our village");
		} else if (randomDiag == 1) {
			npcsay(p, n, "Hello");
		} else if (randomDiag == 2) {
			npcsay(p, n, "Wanna fight?");
			n.startCombat(p);
		} else if (randomDiag == 3) {
			npcsay(p, n, "Who are you?");
			Functions.say(p, n, "I'm a bold adventurer");
			npcsay(p, n, "You don't look very strong");
		} else if (randomDiag == 4) {
			p.message("The barbarian grunts");
		} else if (randomDiag == 5) {
			npcsay(p, n, "Good day, my dear fellow");
		} else if (randomDiag == 6) {
			npcsay(p, n, "ug");
		} else if (randomDiag == 7) {
			npcsay(p, n, "I'm a little busy right now",
				"We're getting ready for our next barbarian raid");
		} else if (randomDiag == 8) {
			npcsay(p, n, "Beer?");
		} else if (randomDiag == 9) {
			p.message("The barbarian ignores you");
		} else if (randomDiag == 10) {
			npcsay(p, n, "Grr");
		}
	}
}
