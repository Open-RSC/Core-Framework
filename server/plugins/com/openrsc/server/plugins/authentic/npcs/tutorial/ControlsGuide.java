package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

public class ControlsGuide implements TalkNpcTrigger {
	/**
	 * Tutorial island second room guide
	 */
	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Hello I'm here to tell you more about the game's controls",
			"Most of your options and character information",
			"can be accesed by the menus in the top right corner of the screen",
			"moving your mouse over the map icon",
			"which is the second icon from the right",
			"gives you a view of the area you are in",
			"clicking on this map is an effective way of walking around",
			"though if the route is blocked, for example by a closed door",
			"then your character won't move",
			"Also notice the compass on the map which may be of help to you");
		say(player, n, "Thankyou for your help");
		npcsay(player, n, "Now carry on to speak to the combat instructor");
		if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 15)
			player.getCache().set("tutorial", 15);
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CONTROLS_GUIDE.id();
	}

}
