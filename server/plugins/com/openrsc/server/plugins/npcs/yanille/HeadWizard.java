package com.openrsc.server.plugins.npcs.yanille;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class HeadWizard implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id()) {
			if (getCurrentLevel(p, Skills.MAGIC) < 66) {
				npcsay(p, n, "Hello, you need a magic level of 66 to get in here",
						"The magical energy in here is unsafe for those below that level");
			} else {
				npcsay(p, n, "Hello welcome to the wizard's guild",
						"Only accomplished wizards are allowed in here",
						"Feel free to use any of our facilities");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return p.getWorld().getServer().getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id();
	}

}
