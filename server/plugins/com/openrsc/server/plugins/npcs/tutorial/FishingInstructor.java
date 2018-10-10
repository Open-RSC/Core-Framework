package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

public class FishingInstructor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island fishing instructor
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 45) {
			npcTalk(p, n, "Go through the next door to continue with the tutorial now");
			return;
		}
		if(hasItem(p, 376) && !hasItem(p, 349)) {
			npcTalk(p, n, "Please come talk to me once you have caught a shrimp");
			return;
		}
		if(hasItem(p, 349)) {
			npcTalk(p, n, "Well done you can now continue with the tutorial",
					"first You can cook the shrimps on my fire here if you like");
			p.getCache().set("tutorial", 45);
			return;
		}
		if(p.getCache().getInt("tutorial") == 41 && !hasItem(p, 349)) {
			npcTalk(p, n, "Left click on that sparkling water",
					"then you can catch some shrimp if you have a net");
			playerTalk(p, n, "I have lost my net");
			npcTalk(p, n, "Hmm a good fisherman doesn't lose his net",
					"Ah well heres another one");
			addItem(p, 376, 1);
		} else {
			playerTalk(p, n, "Hi are you here to tell me how to catch fish?");
			npcTalk(p, n, "Yes that's right, you're a smart one",
					"Fishing is a useful skill",
					"You can sell high level fish for lots of money",
					"Or of course you can cook it and eat it to heal yourself",
					"Unfortunately you'll have to start off catching shrimps",
					"Till your fishing level gets higher",
					"you'll need this");
			p.message("the fishing instructor gives you a somewhat old looking net");
			addItem(p, 376, 1); // Add a net to the players inventory
			npcTalk(p, n, "Go catch some shrimp",
					"left click on the sparkling piece of water",
					"While you have the net in your inventory you might catch some fish");
			p.getCache().set("tutorial", 41);
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 479;
	}

}
