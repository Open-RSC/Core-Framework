package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.removeItem;

public class UndergroundPassSmearDollOfIban implements InvUseOnItemListener, InvUseOnItemExecutiveListener {

	/**
	 * A underground pass class for preparing the doll of iban.
	 * Smearing (using items on the doll of iban) to finally complete it.
	 **/

	@Override
	public boolean blockInvUseOnItem(Player p, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.IBANS_ASHES.id(), ItemId.A_DOLL_OF_IBAN.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.IBANS_CONSCIENCE.id(), ItemId.A_DOLL_OF_IBAN.id())
				|| Functions.compareItemsIds(item1, item2, ItemId.IBANS_SHADOW.id(), ItemId.A_DOLL_OF_IBAN.id());
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if (Functions.compareItemsIds(item1, item2, ItemId.IBANS_ASHES.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			p.message("you rub the ashes into the doll");
			removeItem(p, ItemId.IBANS_ASHES.id(), 1);
			if (!p.getCache().hasKey("ash_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("ash_on_doll", true);
			}
		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.IBANS_CONSCIENCE.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			message(p, "you crumble the doves skeleton into dust");
			p.message("and rub it into the doll");
			removeItem(p, ItemId.IBANS_CONSCIENCE.id(), 1);
			if (!p.getCache().hasKey("cons_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("cons_on_doll", true);
			}
		}
		else if (Functions.compareItemsIds(item1, item2, ItemId.IBANS_SHADOW.id(), ItemId.A_DOLL_OF_IBAN.id())) {
			message(p, "you pour the strange liquid over the doll");
			p.message("it seeps into the cotton");
			removeItem(p, ItemId.IBANS_SHADOW.id(), 1);
			if (!p.getCache().hasKey("shadow_on_doll") && p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
				p.getCache().store("shadow_on_doll", true);
			}
		}
	}
}
