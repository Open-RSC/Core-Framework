package com.openrsc.server.plugins.custom.npcs;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class ScotRuth implements
	TalkNpcTrigger {
	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("scotruth_to_chaos_altar")) {
			player.message("Thanks for yer business. The tunnel's just over there");
		} else {
			int tick = config().GAME_TICK;
			npcsay(player, n, "Hey, " + player.getUsername() + "!",
				"You like savin' time? I can help",
				"Took me a while, but I just finished this here tunnel",
				"If yer lookin to reach the chaos altar, there's no better way",
				"For a small 200,000gp investment, I'll let ye use it forever",
				"What do ya say? Do we have a deal?"
				);
			int choice = multi(player, n, "Yes", "No, 200,000gp is a rip-off");
			if (choice == 0) {
				if (ifheld(player, ItemId.COINS.id(), 200000)) {
					npcsay(player, n, "Excellent. Keep in mind ye'll appear in the deep wilderness",
						"and ye can't use this tunnel te come back");
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 200000));
					player.getCache().store("scotruth_to_chaos_altar", true);
				} else {
					npcsay(player, n, "Come back with the money and you've a deal, lad");
				}
			} else if (choice == 1) {
				npcsay(player, n, "Suit yerself.");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.SCOTRUTH.id();
	}
}
