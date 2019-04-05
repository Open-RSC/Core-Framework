package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.quests.free.ShieldOfArrav.isBlackArmGang;

public class King implements TalkToNpcListener, TalkToNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.KING.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (hasItem(p, ItemId.CERTIFICATE.id())) {
			playerTalk(p, n, "Your majesty", "I have come to claim the reward",
				"For the return of the shield of Arrav");
			if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) == 5) {
				message(p, "You show the certificate to the king");
				npcTalk(p, n, "My goodness",
					"This is the claim for a reward put out by my father",
					"I never thought I'd see anyone claim this reward",
					"I see you are claiming half the reward",
					"So that would come to 600 gold coins");
				message(p, "You hand over a certificate",
					"The king gives you 600 coins");
				removeItem(p, ItemId.CERTIFICATE.id(), 1);
				p.sendQuestComplete(Constants.Quests.SHIELD_OF_ARRAV);
				if (isBlackArmGang(p))
					p.updateQuestStage(Constants.Quests.SHIELD_OF_ARRAV, -2);
				return;
			} else if (p.getQuestStage(Constants.Quests.SHIELD_OF_ARRAV) >= 0) {
				npcTalk(p, n, "The name on this certificate isn't yours!",
					"I can't give you the reward",
					"Unless you do the quest yourself");
			} else {
				npcTalk(p, n, "You have already claimed the reward",
					"You can't claim it twice");
				message(p, "Why don't you give this certificate",
					"To whoever helped you get the shield");
			}
			return;
		} else if (hasItem(p, ItemId.BROKEN_SHIELD_ARRAV_1.id()) && hasItem(p, ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			playerTalk(p, n, "Your majesty",
				"I have recovered the shield of Arrav",
				"I would like to claim the reward");
			npcTalk(p, n, "The shield of Arrav, eh?",
				"Yes, I do recall my father putting a reward out for that",
				"Very well",
				"Go get the authenticity of the shield verified",
				"By the curator at the museum",
				"And I will grant you your reward");
			return;
		}
		playerTalk(p, n, "Greetings, your majesty");
		npcTalk(p, n, "Do you have anything of import to say?");
		playerTalk(p, n, "Not really");
		npcTalk(p, n, "You will have to excuse me then", "I am very busy",
			"I have a kingdom to run");
	}
}
