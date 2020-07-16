package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassSmearDollOfIban implements UseInvTrigger {

	/**
	 * A underground pass class for preparing the doll of iban.
	 * Smearing (using items on the doll of iban) to finally complete it.
	 **/

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.IBANS_ASHES.id(), ItemId.A_DOLL_OF_IBAN.id())
				|| compareItemsIds(item1, item2, ItemId.IBANS_CONSCIENCE.id(), ItemId.A_DOLL_OF_IBAN.id())
				|| compareItemsIds(item1, item2, ItemId.IBANS_SHADOW.id(), ItemId.A_DOLL_OF_IBAN.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (compareItemsIds(item1, item2, ItemId.IBANS_ASHES.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			player.message("you rub the ashes into the doll");
			player.getCarriedItems().remove(new Item(ItemId.IBANS_ASHES.id()));
			if (!player.getCache().hasKey("ash_on_doll") && player.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				player.getCache().store("ash_on_doll", true);
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.IBANS_CONSCIENCE.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			mes("you crumble the doves skeleton into dust");
			delay(3);
			player.message("and rub it into the doll");
			player.getCarriedItems().remove(new Item(ItemId.IBANS_CONSCIENCE.id()));
			if (!player.getCache().hasKey("cons_on_doll") && player.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				player.getCache().store("cons_on_doll", true);
			}
		}
		else if (compareItemsIds(item1, item2, ItemId.IBANS_SHADOW.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			mes("you pour the strange liquid over the doll");
			delay(3);
			player.message("it seeps into the cotton");
			player.getCarriedItems().remove(new Item(ItemId.IBANS_SHADOW.id()));
			if (!player.getCache().hasKey("shadow_on_doll") && player.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				player.getCache().store("shadow_on_doll", true);
			}
		}
	}
}
