package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.RuneScript.*;

public class PeterSkippin implements TalkNpcTrigger, KillNpcTrigger {
	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() != NpcId.PETER_SKIPPIN.id()) return;

		npcsay("Oh hey",
			"Welcome to RuneScape");
		if (multi("I'd like to skip the tutorial", "Thank you") != 0) return;
		npcsay("Ok");

		if (player.getIronMan() == IronmanMode.Hardcore.id()) {
			npcsay("You look like a pretty hardcore person",
				"I'll let you handle skipping the tutorial on your own",
				"You can either select the option from the Settings menu",
				"Or type \"::skiptutorial\" in your chatbox");
		} else {
			npcattack();
			say("What are you doing?!");
			npcsay("Sending you to Lumbridge",
				"Have fun kid");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.PETER_SKIPPIN.id();
	}

	@Override
	public void onKillNpc(Player player, Npc npc) {
		if (npc.getID() != NpcId.PETER_SKIPPIN.id()) return;

		// Peter Skippin is invincible
		npc.getSkills().setLevel(Skill.HITS.id(), npc.getDef().getHits());
		npc.killed = false;
		npcsay("I'm the most non-competitive",
			"So I win");
		return;
	}

	@Override
	public boolean blockKillNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.PETER_SKIPPIN.id();
	}
}
