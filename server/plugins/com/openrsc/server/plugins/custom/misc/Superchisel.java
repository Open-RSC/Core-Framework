package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseInvTrigger;

import static com.openrsc.server.plugins.Functions.compareItemsIds;
import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.mes;

public class Superchisel implements OpInvTrigger, UseInvTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		mes("You twiddle the superchisel");
		delay(2);
		mes("You remember your mother scolding you...");
		delay(3);
		mes("@yel@Mum: Stop fidgeting! It's a bad habit!");
		// TODO: probably do some epic mod tool thing here
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.SUPERCHISEL.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		mes("It does kind of look like those would go together, eh?");
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.BALL_OF_WOOL.id(), ItemId.SUPERCHISEL.id());
	}
}
