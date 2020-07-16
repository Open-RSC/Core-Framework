package com.openrsc.server.plugins.authentic.npcs.ardougne.east;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class CombatGuards implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(), NpcId.GUARD_TRAINING_CAMP_OGRE.id(), NpcId.GUARD_TRAINING_CAMP_DUMMY.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.GUARD_TRAINING_CAMP_DUMMY.id()) {
			say(player, n, "hello");
			npcsay(player, n, "hello soldier");
			say(player, n, "i'm more of an adventurer really");
			npcsay(player, n, "in this day and age we're all soldiers",
				"no time to waste gassing - fight, fight, fight");
		} else if (n.getID() == NpcId.GUARD_TRAINING_CAMP_OGRE.id()) {
			say(player, n, "hello");
			npcsay(player, n, "well hello brave warrior",
				"these ogres have been terrorising the area",
				"they've eaten four children last week alone");
			say(player, n, "brutes");
			npcsay(player, n, "so we decided to use them for target practice",
				"a fair punishment");
			say(player, n, "indeed");
		}
	}
}
