package com.openrsc.server.plugins.npcs.grandtree;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.event.rsc.GameStateEvent;
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
		p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0,"Talk to Brimstail") {
			public void init() {
				addState(0, () -> {
					getPlayerOwner().setBusy(true);
					Functions.playerTalk(getPlayerOwner(), "Hello");
					return nextState(3);
				});
				addState(1, () -> {
					getPlayerOwner().message("The gnome is chanting");
					return nextState(3);
				});
				addState(2, () -> {
					getPlayerOwner().message("he does not respond");
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		});
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		return obj.getID() == 667;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		p.getWorld().getServer().getGameEventHandler().add(new GameStateEvent(p.getWorld(), p, 0,"Enter Brimstail Cave") {
			public void init() {
				addState(0, () -> {
					getPlayerOwner().setBusy(true);
					getPlayerOwner().message("you enter the cave");
					return nextState(3);
				});
				addState(1, () -> {
					getPlayerOwner().message("it leads to a ladder");
					return nextState(3);
				});
				addState(2, () -> {
					getPlayerOwner().message("you climb down");
					getPlayerOwner().teleport(730, 3334, false);
					getPlayerOwner().setBusy(false);
					return null;
				});
			}
		});
	}
}
