package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;

import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.mes;
import static com.openrsc.server.plugins.RuneScript.*;

public class AgilityCape implements OpInvTrigger {

	private boolean canTeleport(Player player) {
		if (player.getLocation().wildernessLevel() >= 30 || player.getLocation().isInFisherKingRealm()
			|| player.getLocation().isInsideGrandTreeGround()
			|| (player.getLocation().inModRoom() && !player.isAdmin())) {
			mes("A mysterious force blocks your teleport!");
			return false;
		}

		if (player.getCarriedItems().getInventory().countId(ItemId.ANA_IN_A_BARREL.id()) > 0) {
			mes("You can't teleport while holding Ana,");
			delay(3);
			mes("It's just too difficult to concentrate.");
			delay(3);
			return false;
		}
		return true;
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() != ItemId.AGILITY_CAPE.id()) return;

		if (canTeleport(player)) {
			mes("You turn on the spot");
			delay(3);
			if (ifrandom(2)) {
				mes("And trip!");
				player.damage(1);
				say("ouch");
				mes("Aren't you supposed to be good at balancing?");
				return;
			}
			player.teleport(591, 765, true);
			mes("You teleport to the Yanille agility dungeon");
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.AGILITY_CAPE.id();
	}
}
