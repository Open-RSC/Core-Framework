package com.openrsc.server.plugins.npcs;

import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class MonkHealer implements TalkToNpcListener, TalkToNpcExecutiveListener {
	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "Greetings traveller");
		int option = showMenu(p, n, "Can you heal me? I'm injured", "Greetings");
		if (option == 0) {
			npcTalk(p, n, "Ok");
			message(p, "The monk places his hands on your head", "You feel a little better");
			int newHp = getCurrentLevel(p, Skills.HITPOINTS) + 10;
			if (newHp > getMaxLevel(p, Skills.HITPOINTS)) {
				newHp = getMaxLevel(p, Skills.HITPOINTS);
			}
			p.getSkills().setLevel(Skills.HITPOINTS, newHp);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 93 || n.getID() == 174;
	}
}
