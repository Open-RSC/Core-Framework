package com.openrsc.server.plugins.listeners.action;


import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;

public interface TalkToNpcListener {
	/**
	 * Called when a player talks to a npc
	 *
	 * @param p
	 * @param n
	 */
	public void onTalkToNpc(Player p, Npc n);

}
