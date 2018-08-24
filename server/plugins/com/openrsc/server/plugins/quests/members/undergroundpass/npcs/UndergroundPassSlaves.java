package com.openrsc.server.plugins.quests.members.undergroundpass.npcs;

import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class UndergroundPassSlaves implements TalkToNpcListener,
TalkToNpcExecutiveListener {

	public static int[] SLAVES = { 634, 635, 636, 637, 638, 639, 640 };
	
	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if(inArray(n.getID(), SLAVES)) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(inArray(n.getID(), SLAVES)) {
			p.message("the man seems to be in a weak state of mind");
			switch(n.getID()) {
			case 634:
				playerTalk(p,n, "hello");
				npcTalk(p,n, "Eating me...they keep eating me!",
						"Eating me from the inside. Please stop  them eating me",
						"... I can feel them wriggling around inside me right now",
						"... please  stop them!");
				n.startCombat(p);
				break;
			case 635:
				playerTalk(p,n, "hi");
				npcTalk(p,n, "Blood, blood... never enough blood to go round",
						"When I lift up my arm like this,it all pours back into my body",
						"I hope it remembers to go back inside my arm",
						"And don't even ask me about my legs..",
						"how much blood are they going to need?",
						"Blood is important",
						"We must offer it to Zamorak every day as proof of our devotion",
						"I just hope I don't run out.");
				p.message("the prisoner has clearly been here too long");
				break;
			case 636:
				playerTalk(p,n, "hello, are you ok?");
				npcTalk(p,n, "Oh yes, you're a fine one. Nice red cheeks, shiny hair",
						"Let's see now, some potatoes, some cabbage,",
						"maybe half a clove of garlic",
						"...yes  I think you'd  make a fine soup",
						"You don't mind do you?");
				playerTalk(p,n, "actually i do");
				npcTalk(p,n, "You're welcome to have some with me of course.");
				n.startCombat(p);
				break;
			case 637:
				playerTalk(p,n, "hi");
				npcTalk(p,n, "Mwaarrr fnnntchh. Gbpp dng sktd delp?");
				playerTalk(p,n, "pardon?");
				npcTalk(p,n, "Kjp lar falut");
				playerTalk(p,n, "sorry, i dont under..");
				npcTalk(p,n, "Mwaarrr fnnntchh. Gbpp dng sktd delp?",
						"GBPP DNG SKTD DELP! GBPP DNG SKTD DELP!");
				n.startCombat(p);
				break;
			case 638:
				playerTalk(p,n, "hi");
				npcTalk(p,n, "Kill the villagers, burn them all- every last one",
						"I want nothing to survive");
				playerTalk(p,n, "you're ill");
				npcTalk(p,n, "What's that - you've never smelt it before",
						"Well, let's just say that it's an acquired taste");
				n.startCombat(p);
				break;
			case 639:
				playerTalk(p,n, "hello");
				npcTalk(p,n, "Danger, everywhere danger! But not from man nor beast-",
						"no this is the danger that is inside you.",
						"Bring it out, nurture it, cherish it",
						"Stroke it like you would stroke a wounded bird-",
						"then strangle it before it takes hold of your very being",
						"Don't say I didn't warn you");
				break;
			case 640:
				playerTalk(p,n, "hello");
				npcTalk(p,n, "What's that...is that a dagger I see before me?",
						"Why should I bear the slings and arrows of outrageous fortune",
						"for what is a man but this quintessence of dust?",
						"And so I say goodnight sweet prince. The rest is silence.");
				break;
			}
		}
	}
}
