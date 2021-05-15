package com.openrsc.server.plugins.authentic.npcs.yanille;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class WizardFrumscone implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.WIZARD_FRUMSCONE.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.WIZARD_FRUMSCONE.id()) {
			npcsay(player, n, "Do you like my magic zombies",
				"Feel free to kill them",
				"Theres plenty more where these came from");
			if (config().WANT_CUSTOM_SPRITES
				&& getMaxLevel(player, Skill.MAGIC.id()) >= 99) {
				if (multi(player, n, "Does your cape have any magical properties?",
					"I was going to kill them with or without your permission") == 0) {

					npcsay(player, n, "Yes it does",
						"Only masters of magic can harness its power",
						"It seems that you are ready for such power",
						"It will only cost you 99,000 coins.");
					if (multi(player, n, "I am ready", "I am not ready") == 0) {
						if (player.getCarriedItems().getInventory().countId(ItemId.COINS.id()) >= 99000) {
							mes("Wizard Frumscone takes your coins");
							delay(3);
							if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 99000)) > -1) {
								mes("And hands you a Magic cape");
								delay(3);
								give(player, ItemId.MAGIC_CAPE.id(), 1);
								npcsay(player, n, "You have now been bestowed with great power",
									"This cape will allow you to cast some spells without using runes");
							}
						} else {
							npcsay(player, n, "You do not have enough coins to unlock your full power");
						}
					}
				}
			}
		}
	}
}
