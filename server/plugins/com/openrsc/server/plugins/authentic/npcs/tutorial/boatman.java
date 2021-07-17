package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class boatman implements TalkNpcTrigger {
	/**
	 * Tutorial island boat man - last npc before main land (Lumbridge)
	 */
	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Hello my job is to take you to the main game area",
			"It's only a short row",
			"I shall take you to the small town of Lumbridge",
			"In the kingdom of Misthalin");
		int menu = multi(player, n, "Ok I'm ready to go", "I'm not done here yet");
		if (menu == 0) {
			npcsay(player, n, "Lets go then");
			player.message("You have completed the tutorial");
			player.teleport(player.getConfig().RESPAWN_LOCATION_X, player.getConfig().RESPAWN_LOCATION_Y, false);
			if (player.getCache().hasKey("tutorial")) {
				player.getCache().remove("tutorial");
			}
			delay(3);
			player.message("The boat arrives in Lumbridge");
			player.getWorld().sendWorldAnnouncement("New adventurer @gre@" + player.getUsername() + "@whi@ has arrived in lumbridge!");
			ActionSender.sendPlayerOnTutorial(player);
		} else if (menu == 1) {
			npcsay(player, n, "Ok come back when you are ready");
		}

	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BOATMAN.id();
	}

}
