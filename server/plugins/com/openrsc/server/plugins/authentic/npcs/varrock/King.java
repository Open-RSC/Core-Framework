package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.authentic.quests.free.ShieldOfArrav.isBlackArmGang;

public class King implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.KING.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCarriedItems().hasCatalogID(ItemId.CERTIFICATE.id(), Optional.of(false))) {
			say(player, n, "Your majesty", "I have come to claim the reward",
				"For the return of the shield of Arrav");
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) {
				mes("You show the certificate to the king");
				delay(3);
				npcsay(player, n, "My goodness",
					"This is the claim for a reward put out by my father",
					"I never thought I'd see anyone claim this reward",
					"I see you are claiming half the reward",
					"So that would come to 600 gold coins");
				mes("You hand over a certificate");
				delay(3);
				mes("The king gives you 600 coins");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.CERTIFICATE.id()));
				player.sendQuestComplete(Quests.SHIELD_OF_ARRAV);
				if (isBlackArmGang(player))
					player.updateQuestStage(Quests.SHIELD_OF_ARRAV, -2);
				return;
			} else if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) >= 0) {
				npcsay(player, n, "The name on this certificate isn't yours!",
					"I can't give you the reward",
					"Unless you do the quest yourself");
			} else {
				npcsay(player, n, "You have already claimed the reward",
					"You can't claim it twice");
				mes("Why don't you give this certificate");
				delay(3);
				mes("To whoever helped you get the shield");
				delay(3);
			}
			return;
		} else if (player.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_1.id(), Optional.of(false))
			&& player.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_2.id(), Optional.of(false))) {
			say(player, n, "Your majesty",
				"I have recovered the shield of Arrav",
				"I would like to claim the reward");
			npcsay(player, n, "The shield of Arrav, eh?",
				"Yes, I do recall my father putting a reward out for that",
				"Very well",
				"Go get the authenticity of the shield verified",
				"By the curator at the museum",
				"And I will grant you your reward");
			return;
		}
		say(player, n, "Greetings, your majesty");
		npcsay(player, n, "Do you have anything of import to say?");
		say(player, n, "Not really");
		npcsay(player, n, "You will have to excuse me then", "I am very busy",
			"I have a kingdom to run");
	}
}
