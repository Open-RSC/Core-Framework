package com.openrsc.server.plugins.authentic.npcs.yanille;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.minigames.CombatOdyssey;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class SigbertTheAdventurer implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SIGBERT_THE_ADVENTURER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SIGBERT_THE_ADVENTURER.id()) {
			if (config().WANT_COMBAT_ODYSSEY
				&& CombatOdyssey.getCurrentTier(player) == 7
				&& CombatOdyssey.isTierCompleted(player)) {
				if (CombatOdyssey.biggumMissing()) return;
				int newTier = 8;
				CombatOdyssey.assignNewTier(player, newTier);
				npcsay(player, n, "You're doing the combat odyssey I assume",
					"Well you've made it this far, i guess you have a chance",
					"You now have to kill");
				npcsay(player, n, player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
				npcsay(player, n, "Go see Achetties when you're done");
				return;
			}
			npcsay(player, n, "I'd be very careful going up there friend");
			int menu = multi(player, n,
				"Why what's up there?",
				"Fear not I am very strong");
			if (menu == 0) {
				npcsay(player, n, "Salarin the twisted",
					"One of Kanadarin's most dangerous chaos druids",
					"I tried to take him on and then suddenly felt immensly week",
					"I here he's susceptable to attacks from the mind",
					"However I have no idea what that means",
					"So it's not much help to me");
			} else if (menu == 1) {
				npcsay(player, n, "You might find you are not so strong shortly");
			}
		}
	}
}
