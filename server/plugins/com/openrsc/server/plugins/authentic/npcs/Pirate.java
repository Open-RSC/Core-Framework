package com.openrsc.server.plugins.authentic.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class Pirate implements TalkNpcTrigger {
	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return inArray(n.getID(),
			NpcId.PIRATE_LVL27.id(), NpcId.PIRATE_LVL30.id());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		int mood = DataConversions.getRandom().nextInt(25);

		say(player, n, "Hello");

		if (mood == 0)
			npcsay(player, n, "shiver me timbers");
		else if (mood == 1)
			npcsay(player, n, "I'm the scourge of the seven seas");
		else if (mood == 2)
			npcsay(player, n, "Arrrh ye lily livered landlubber");
		else if (mood == 3)
			npcsay(player, n, "Ahoy there");
		else if (mood == 4)
			npcsay(player, n, "Avast me hearties");
		else if (mood == 5)
			npcsay(player, n, "Arrh, I be in search of buried treasure");
		else if (mood == 6)
			npcsay(player, n, "Arrh be off with ye");
		else if (mood == 7)
			npcsay(player, n, "A pox on ye");
		else if (mood == 8)
			npcsay(player, n, "Keel haul them I say");
		else if (mood == 9)
			npcsay(player, n, "Yo ho ho me hearties");
		else if (mood == 10)
			npcsay(player, n, "Splice the mainbrace");
		else if (mood == 11)
			npcsay(player, n, "Avast behind");
		else if (mood == 12)
			npcsay(player, n, "Arrh ye scury sea dog");
		else if (mood == 13)
			npcsay(player, n, "3 days at port for resupply then out on the high sea");
		else if (mood == 14)
			npcsay(player, n, "Yo ho ho and bottle of alchopop");
		else if (mood == 15)
			npcsay(player, n, "Batton down the hatches there's a storm a brewin");
		else if (mood == 16) {
			npcsay(player, n, "I think ye'll be taking a long walk off a short plank");
			n.startCombat(player);
		}
		else if (mood == 17)
			npcsay(player, n, "Arrh");
		else if (mood == 18)
			npcsay(player, n, "Good day to you my dear sir");
		else if (mood == 19)
			npcsay(player, n, "Yo ho ho and a bottle of a rum");
		else if (mood == 20)
			npcsay(player, n, "Great blackbeard's beard");
		else if (mood == 21)
			npcsay(player, n, "Arrh I'll keel haul ye");
		else if (mood == 22)
			npcsay(player, n, "Arrh arrh");
		else if (mood == 23)
			npcsay(player, n, "Man overboard");
		else if (mood == 24) {
			npcsay(player, n, "avast behind");
			say(player, n, "I'm not that fat");
		}
	}
}
