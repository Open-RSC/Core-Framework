package com.openrsc.server.plugins.npcs.varrock;

import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class King implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 42;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (hasItem(p, 53) && hasItem(p, 54)) {
			playerTalk(p, n, "Your majesty",
					"I have recovered the shield of Arrav",
					"I would like to claim the reward");
			npcTalk(p, n, "The shield of Arrav, eh?",
					"Yes, I do recall my father putting a reward out for that",
					"Very well",
					"Go get the authenticity of the shield verified",
					"By the curator at the museum",
					"And I will grant you your reward");
			return;// Can u test it from beginning to end? sure thing btw, need
					// todo the door.. :/ which one
		} else if (hasItem(p, 61)
				&& p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 5) {
			playerTalk(p, n, "Your majesty", "I have come to claim the reward",
					"For the return of the shield of Arrav");
			message(p, "You show the certificate to the king");
			npcTalk(p, n, "My goodness",
					"This is the claim for a reward put out by my father",
					"I never thought I'd see anyone claim this reward",
					"I see you are claiming half the reward",
					"So that would come to 600 gold coins");
			message(p, "You hand the certificate",
					"The king gives you 600 coins");
			removeItem(p, 61, 1);
			p.sendQuestComplete(Constants.Quests.SHIELD_OF_ARRAV);
			return;
		}
		playerTalk(p, n, "Greetings your majesty");
		npcTalk(p, n, "Do you have anything of import to say?");
		playerTalk(p, n, "Not really");
		npcTalk(p, n, "You will have to excuse me then", "I am very busy",
				"I have a kingdom to run");
	}
}
