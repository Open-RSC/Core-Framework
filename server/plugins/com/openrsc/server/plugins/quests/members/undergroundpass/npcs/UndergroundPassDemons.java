package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassDemons implements PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener {

	public static int[] DEMONS = { 645, 646, 647 };
	public static int AMULET_OF_OTHAINIAN = 1009;
	public static int AMULET_OF_DOOMION = 1010;
	public static int AMULET_OF_HOLTHION = 1011;

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		if(inArray(n.getID(), DEMONS)) {	
			return true;
		}
		return false;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if(inArray(n.getID(), DEMONS)) {
			n.killedBy(p);
			if(!p.getCache().hasKey("doll_of_iban") && p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) != 6) {
				p.message("the demon slumps to the floor");
				teleportPlayer(p, n);
			} else {
				teleportPlayer(p, n);
				message(p, "the demon slumps to the floor");
				if(!hasItem(p, n.getID() + 364)) {
					p.message("around it's neck you find a strange looking amulet");
					addItem(p, n.getID() + 364 , 1); // will give correct ammys for all.
				}
			}
		}
	}
	private void teleportPlayer(Player p, Npc n) {
		if(n.getID() == DEMONS[0]) {
			p.teleport(796, 3541);
		} else if(n.getID() == DEMONS[1]) {
			p.teleport(807, 3541);
		} else if(n.getID() == DEMONS[2]) {
			p.teleport(807, 3528);
		}
	}
}
