package com.openrsc.server.plugins.authentic.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class MonkHealer implements TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player player, final Npc n) {
		npcsay(player, n, "Greetings traveller");
		int option = multi(player, n, false, //do not send over
			"Can you heal me? I'm injured", "Isn't this place built a bit out the way?");
		if (option == 0) {
			say(player, n, "Can you heal me?", "I'm injured");
			npcsay(player, n, "Ok");
			mes("The monk places his hands on your head");
			delay(3);
			mes("You feel a little better");
			delay(3);
			int newHp = getCurrentLevel(player, Skill.HITS.id()) + 5;
			boolean sendUpdate = player.getClientLimitations().supportsSkillUpdate;
			if (newHp > getMaxLevel(player, Skill.HITS.id())) {
				newHp = getMaxLevel(player, Skill.HITS.id());
			}
			player.getSkills().setLevel(Skill.HITS.id(), newHp, sendUpdate);
			if (!sendUpdate) {
				player.getSkills().sendUpdateAll();
			}
		} else if (option == 1) {
			say(player, n, "Isn't this place built a bit out the way?");
			npcsay(player, n, "We like it that way",
					"We get disturbed less",
					"We still get rather a large amount of travellers",
					"looking for sanctuary and healing here as it is");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MONK.id() || n.getID() == NpcId.ABBOT_LANGLEY.id();
	}
}
