package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.say;

import com.openrsc.server.constants.NpcId;

public class ControlsGuide implements TalkNpcTrigger {
	/**
	 * @author Davve
	 * Tutorial island second room guide
	 */
	@Override
	public void onTalkNpc(Player p, Npc n) {
		npcsay(p, n, "Hello I'm here to tell you more about the game's controls",
			"Most of your options and character information",
			"can be accesed by the menus in the top right corner of the screen",
			"moving your mouse over the map icon",
			"which is the second icon from the right",
			"gives you a view of the area you are in",
			"clicking on this map is an effective way of walking around",
			"though if the route is blocked, for example by a closed door",
			"then your character won't move",
			"Also notice the compass on the map which may be of help to you");
		Functions.say(p, n, "Thankyou for your help");
		npcsay(p, n, "Now carry on to speak to the combat instructor");
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") < 15)
			p.getCache().set("tutorial", 15);
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.CONTROLS_GUIDE.id();
	}

}
