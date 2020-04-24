package com.openrsc.server.plugins.npcs.hemenster;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MasterFisher implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return player.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.MASTER_FISHER.id()) {
			if (getCurrentLevel(player, Skills.FISHING) < 68) {
				npcsay(player, n, "Hello only the top fishers are allowed in here");
				player.message("You need a fishing level of 68 to enter");
			} else {
				npcsay(player, n, "Hello, welcome to the fishing guild",
					"Please feel free to make use of any of our facilities");
			}
		}
	}
}
