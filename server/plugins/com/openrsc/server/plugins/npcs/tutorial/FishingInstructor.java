package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FishingInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island fishing instructor
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 40) {
			say(p, n, "Hi are you here to tell me how to catch fish?");
			npcsay(p, n, "Yes that's right, you're a smart one",
				"Fishing is a useful skill",
				"You can sell high level fish for lots of money",
				"Or of course you can cook it and eat it to heal yourself",
				"Unfortunately you'll have to start off catching shrimps",
				"Till your fishing level gets higher",
				"you'll need this");
			p.message("the fishing instructor gives you a somewhat old looking net");
			give(p, ItemId.NET.id(), 1); // Add a net to the players inventory
			npcsay(p, n, "Go catch some shrimp",
				"left click on that sparkling piece of water",
				"While you have the net in your inventory you might catch some fish");
			p.getCache().set("tutorial", 41);
		} else if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 41) {
			npcsay(p, n, "Left click on that splashing sparkling water",
					"then you can catch some shrimp");
			if (!p.getCarriedItems().hasCatalogID(ItemId.NET.id(), Optional.of(false))) {
				say(p, n, "I have lost my net");
				npcsay(p, n, "Hmm a good fisherman doesn't lose his net",
					"Ah well heres another one");
				give(p, ItemId.NET.id(), 1);
			}
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 42) {
			npcsay(p, n, "Well done you can now continue with the tutorial",
					"first You can cook the shrimps on my fire here if you like");
			p.getCache().set("tutorial", 45);
		} else {
			npcsay(p, n, "Go through the next door to continue with the tutorial now");
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.FISHING_INSTRUCTOR.id();
	}

}
