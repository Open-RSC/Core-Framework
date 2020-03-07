package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

public class Brimstail implements TalkNpcTrigger, OpLocTrigger {

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.BRIMSTAIL.id();
	}

	@Override
	public void onTalkNpc(Player p, final Npc n) {
		p.setBusy(true);
		Functions.say(p, "Hello");
		Functions.delay(1920);
		p.message("The gnome is chanting");
		Functions.delay(1920);
		p.message("he does not respond");
		p.setBusy(false);
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
		return obj.getID() == 667;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		p.setBusy(true);
		p.message("you enter the cave");
		Functions.delay(1920);
		p.message("it leads to a ladder");
		Functions.delay(1920);
		p.message("you climb down");
		p.teleport(730, 3334, false);
		p.setBusy(false);
	}
}
