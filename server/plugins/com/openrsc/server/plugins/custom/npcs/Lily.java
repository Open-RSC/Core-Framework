package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.*;

public class Lily implements TalkNpcTrigger {

    @Override
	public void onTalkNpc(Player player, Npc npc) {
		npcsay("Hello my lovely!",
            "How can I help you?");
        
        ArrayList<String> options = new ArrayList<String>();
        options.add("What are you doing here?");
        options.add("Could you tell me about harvesting?");
        if (player.getSkills().getMaxStat(Skill.HARVESTING.id()) >= 99) {
            options.add("Can you tell me about the cape you have on?");
        }

        int option = multi(options.toArray(new String[0]));

        switch (option) {
            case 0:
                npcsay("I'm just out here enjoying nature!",
                    "I love being with all the plants and animals...",
                    "... and smelling the fresh air!",
                    "I also love helping new adventurers learn harvesting!");
                break;
            case 1:
                break;
            case 2:
                break;
        }
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.LILY.id();
	}
}
