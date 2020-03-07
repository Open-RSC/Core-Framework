package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class boatman implements TalkNpcTrigger {
	/**
	 * @author Davve
	 * Tutorial island boat man - last npc before main land (Lumbridge)
	 */
	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hello my job is to take you to the main game area",
			"It's only a short row",
			"I shall take you to the small town of Lumbridge",
			"In the kingdom of Misthalin");
		int menu = multi(p, n, "Ok I'm ready to go", "I'm not done here yet");
		if (menu == 0) {
			npcsay(p, n, "Lets go then");
			p.message("You have completed the tutorial");
			p.teleport(120, 648, false);
			if (p.getCache().hasKey("tutorial")) {
				p.getCache().remove("tutorial");
			}
			delay(2000);
			p.message("The boat arrives in Lumbridge");
			p.getWorld().sendWorldAnnouncement("New adventurer @gre@" + p.getUsername() + "@whi@ has arrived in lumbridge!");
			ActionSender.sendPlayerOnTutorial(p);
		} else if (menu == 1) {
			npcsay(p, n, "Ok come back when you are ready");
		}

	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BOATMAN.id();
	}

}
