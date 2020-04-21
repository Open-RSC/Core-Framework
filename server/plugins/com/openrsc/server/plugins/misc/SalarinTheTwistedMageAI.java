package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
import com.openrsc.server.plugins.triggers.SpellNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class SalarinTheTwistedMageAI implements SpellNpcTrigger {

	/*
	 * Player maging Salarin the twisted AI - Just to degenerate ATTACK AND STRENGTH if over 2 in said skill.
	 */

	@Override
	public boolean blockSpellNpc(Player player, Npc n) {
		return n.getID() == NpcId.SALARIN_THE_TWISTED.id() && (player.getSkills().getLevel(Skills.ATTACK) > 2 || player.getSkills().getLevel(Skills.STRENGTH) > 2);
	}

	@Override
	public void onSpellNpc(Player player, Npc n) {
		if (n.getID() == NpcId.SALARIN_THE_TWISTED.id() && (player.getSkills().getLevel(Skills.ATTACK) > 2 || player.getSkills().getLevel(Skills.STRENGTH) > 2)) {
			if (!player.withinRange(n, 5))
				return;
			n.getUpdateFlags().setChatMessage(new ChatMessage(n, "Amshalaraz Nithcosh dimarilo", player));
			delay(player.getWorld().getServer().getConfig().GAME_TICK);
			player.message("You suddenly feel much weaker");
			player.getSkills().setLevel(Skills.ATTACK, 0);
			player.getSkills().setLevel(Skills.STRENGTH, 0);
		}
	}
}
