package com.openrsc.server.plugins.authentic.npcs.lumbridge;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.custom.minigames.ALumbridgeCarol;
import com.openrsc.server.plugins.custom.quests.members.RuneMysteries;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class DukeOfLumbridge implements
	TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (config().A_LUMBRIDGE_CAROL && ALumbridgeCarol.inPartyRoom(n)) {
			ALumbridgeCarol.partyDialogue(player, n);
			return;
		}

		npcsay(player, n, "Greetings welcome to my castle");

		ArrayList<String> menu = new ArrayList<String>();

		String quests = "Have you any quests for me?";
		String money = "Where can I find money?";
		String runeMysteries = "Rune mysteries";
		String antiDragonBreathShield = "I seek a shield that will protect me from dragon breath";
		String christmas = "";

		menu.add(quests);
		menu.add(money);
		if (config().WANT_RUNECRAFT) {
			if (player.getQuestStage(Quests.RUNE_MYSTERIES) > 0) {
				menu.add(runeMysteries);
			}
		}
		if (player.getQuestStage(Quests.DRAGON_SLAYER) >= 2 || player.getQuestStage(Quests.DRAGON_SLAYER) < 0
			&& !player.getCarriedItems().hasCatalogID(ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), Optional.empty())) {
			menu.add(0, antiDragonBreathShield);
		}
		if (config().A_LUMBRIDGE_CAROL) {
			int stage = ALumbridgeCarol.getStage(player);
			if (stage == ALumbridgeCarol.NOT_STARTED) {
				christmas = "You look like you haven't slept";
			} else if (stage == ALumbridgeCarol.GHOST_STORY) {
				christmas = "What did the spirits want?";
			} else if (stage == ALumbridgeCarol.READ_BOOK) {
				christmas = "I read your journal";
			} else if (stage == ALumbridgeCarol.RECEIVED_PARCHMENT) {
				if (player.getCarriedItems().hasCatalogID(ItemId.APOLOGY_LETTER.id())) {
					christmas = "I have finished writing the letter";
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.DUKE_PARCHMENT.id())) {
					christmas = "I lost the parchment you gave me";
				}
			} else if (stage == ALumbridgeCarol.LETTER_DELIVERY) {
				christmas = "About the apology letter...";
			} else if (stage == ALumbridgeCarol.DELIVERED_LETTER) {
				christmas = "I delivered your letter";
			} else if (stage == ALumbridgeCarol.FIND_TRAMP) {
				christmas = "What am I doing again?";
			} else if (stage == ALumbridgeCarol.FOUND_TRAMP || stage == ALumbridgeCarol.HELPED_TRAMP) {
				christmas = "I found your old cook";
			} else if (stage == ALumbridgeCarol.FIND_SHILOP) {
				christmas = "What am I doing again?";
			} else if (stage == ALumbridgeCarol.HELPED_SHILOP) {
				christmas = "I helped out Shilop";
			} else if (stage == ALumbridgeCarol.PARTY_TIME) {
				christmas = "Where is the Christmas party again?";
			}

			if (!christmas.equals("")) {
				menu.add(christmas);
			}
		}

		int choice = multi(player, n, false, menu.toArray(new String[menu.size()]));

		if (choice == -1) {
			return;
		} else if (menu.get(choice).equals(antiDragonBreathShield)) {
			say(player, n, "I seek a shield that will protect me from dragon's breath");
			npcsay(player, n, "A knight going on a dragon quest hmm?",
				"A most worthy cause",
				"Guard this well my friend"
			);
			mes("The duke hands you a shield");
			delay(3);
			give(player, ItemId.ANTI_DRAGON_BREATH_SHIELD.id(), 1);
		} else if (menu.get(choice).equals(quests)) {
			say(player, n, "Have you any quests for me?");

			if (!config().WANT_RUNECRAFT) {
				npcsay(player, n, "All is well for me");
				return;
			}

			RuneMysteries.dukeDialog(player.getQuestStage(Quests.RUNE_MYSTERIES), player, n);
		} else if (menu.get(choice).equals(money)) {
			say(player, n, "Where can I find money?");
			npcsay(player, n, "I've heard the blacksmiths are prosperous amoung the peasantry");
			npcsay(player, n, "Maybe you could try your hand at that");
		} else if (menu.get(choice).equals(runeMysteries)) {
			RuneMysteries.dukeDialog(player.getQuestStage(Quests.RUNE_MYSTERIES), player, n);
		} else if (menu.get(choice).equals(christmas)) {
			ALumbridgeCarol.dukeDialogue(player, n, christmas);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.DUKE_OF_LUMBRIDGE.id();
	}

}
