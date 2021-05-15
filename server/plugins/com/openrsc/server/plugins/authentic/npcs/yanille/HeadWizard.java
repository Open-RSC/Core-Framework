package com.openrsc.server.plugins.authentic.npcs.yanille;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class HeadWizard implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (config().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id()) {
			if (getCurrentLevel(player, Skill.MAGIC.id()) < 66) {
				npcsay(player, n, "Hello, you need a magic level of 66 to get in here",
						"The magical energy in here is unsafe for those below that level");
			} else {
				npcsay(player, n, "Hello welcome to the wizard's guild",
						"Only accomplished wizards are allowed in here",
						"Feel free to use any of our facilities");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return player.getConfig().WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id();
	}

}
