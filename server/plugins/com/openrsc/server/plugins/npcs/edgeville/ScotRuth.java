package com.openrsc.server.plugins.npcs.edgeville;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;

public class ScotRuth implements
	TalkToNpcListener {
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (p.getCache().hasKey("scotruth_to_chaos_altar")) {
			p.message("Thanks for yer business. The tunnel's just over there");
		} else {
			int tick = p.getWorld().getServer().getConfig().GAME_TICK;
			Functions.npcTalk(p, n, "Hey, " + p.getUsername() + "!",
				"You like savin' time? I can help",
				"Took me a while, but I just finished this here tunnel",
				"If yer lookin to reach the chaos altar, there's no better way",
				"For a small 200,000gp investment, I'll let ye use it forever",
				"What do ya say? Do we have a deal?"
				);
			int choice = Functions.showMenu(p, n, "Yes", "No, 200,000gp is a rip-off");
			if (choice == 0) {
				if (Functions.hasItem(p, ItemId.COINS.id(), 200000)) {
					Functions.npcTalk(p, n, "Excellent. Keep in mind ye'll appear in the deep wilderness",
						"and ye can't use this tunnel te come back");
					Functions.removeItem(p, ItemId.COINS.id(), 200000);
					p.getCache().store("scotruth_to_chaos_altar", true);
				} else {
					Functions.npcTalk(p, n, "Come back with the money and you've a deal, lad");
				}
			} else if (choice == 1) {
				Functions.npcTalk(p, n, "Suit yerself.");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.SCOTRUTH.id();
	}
}
