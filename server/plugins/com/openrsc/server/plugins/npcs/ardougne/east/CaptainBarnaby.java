package com.openrsc.server.plugins.npcs.ardougne.east;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class CaptainBarnaby implements TalkToNpcExecutiveListener,
		TalkToNpcListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Do you want to go on a trip to Karamja?",
				"The trip will cost you 30 gold");
		int karamja = showMenu(p, n, "Yes please", "No thankyou");
		if (karamja == 0) {
			if (p.getInventory().remove(10, 30) > -1) { // enough money
				message(p, "You pay 30 gold", "You board the ship");
				p.teleport(467, 651, false);
				sleep(1000);
				p.message("The ship arrives at Karamja");
			} else {
				playerTalk(p, n, "Oh dear I don't seem to have enough money");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 316;
	}

}
