package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Brimstail implements TalkToNpcExecutiveListener, TalkToNpcListener, ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.BRIMSTAIL.id();
	}

	@Override
	public void onTalkToNpc(Player p, final Npc n) {
		p.setBusy(true);
		Functions.playerTalk(p, "Hello");
		Functions.sleep(1920);
		p.message("The gnome is chanting");
		Functions.sleep(1920);
		p.message("he does not respond");
		p.setBusy(false);
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 667;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		p.setBusy(true);
		p.message("you enter the cave");
		Functions.sleep(1920);
		p.message("it leads to a ladder");
		Functions.sleep(1920);
		p.message("you climb down");
		p.teleport(730, 3334, false);
		p.setBusy(false);
	}
}
