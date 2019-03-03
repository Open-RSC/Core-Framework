package com.openrsc.server.plugins.npcs.yanille;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.npcTalk;

import com.openrsc.server.Constants;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class HeadWizard implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (Constants.GameServer.WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id()) {
			if (getCurrentLevel(p, Skills.MAGIC) < 66) {
				npcTalk(p, n, "Hello, you need a magic level of 66 to get in here",
						"The magical energy in here is unsafe for those below that level");
			} else {
				npcTalk(p, n, "Hello welcome to the wizard's guild",
						"Only accomplished wizards are allowed in here",
						"Feel free to use any of our facilities");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return Constants.GameServer.WANT_MISSING_GUILD_GREETINGS && n.getID() == NpcId.HEAD_WIZARD.id();
	}

}
