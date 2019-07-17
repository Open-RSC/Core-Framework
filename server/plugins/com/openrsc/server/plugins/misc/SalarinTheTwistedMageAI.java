package com.openrsc.server.plugins.misc;

import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.listeners.action.PlayerMageNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerMageNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.external.NpcId;

public class SalarinTheTwistedMageAI implements PlayerMageNpcListener, PlayerMageNpcExecutiveListener {

	/*
	 * Player maging Salarin the twisted AI - Just to degenerate ATTACK AND STRENGTH if over 2 in said skill.
	 */

	@Override
	public boolean blockPlayerMageNpc(Player p, Npc n) {
		return n.getID() == NpcId.SALARIN_THE_TWISTED.id() && (p.getSkills().getLevel(SKILLS.ATTACK.id()) > 2 || p.getSkills().getLevel(SKILLS.STRENGTH.id()) > 2);
	}

	@Override
	public void onPlayerMageNpc(Player p, Npc n) {
		if (n.getID() == NpcId.SALARIN_THE_TWISTED.id() && (p.getSkills().getLevel(SKILLS.ATTACK.id()) > 2 || p.getSkills().getLevel(SKILLS.STRENGTH.id()) > 2)) {
			if (!p.withinRange(n, 5))
				return;
			n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Amshalaraz Nithcosh dimarilo", p));
			sleep(600);
			p.message("You suddenly feel much weaker");
			p.getSkills().setLevel(SKILLS.ATTACK.id(), 0);
			p.getSkills().setLevel(SKILLS.STRENGTH.id(), 0);
		}
	}
}
