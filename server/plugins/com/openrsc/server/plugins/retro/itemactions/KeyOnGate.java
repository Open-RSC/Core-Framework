package com.openrsc.server.plugins.retro.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;

import static com.openrsc.server.plugins.Functions.doGate;
import static com.openrsc.server.plugins.Functions.inArray;

public class KeyOnGate implements UseLocTrigger {
	private static final int[] VALID_KEYS = { ItemId.PHOENIX_GANG_KEY.id(), ItemId.PHOENIX_GANG_WEAPON_KEY.id() };
	private static final int POH_GATE = 93;

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return player.getConfig().HAS_PLAYER_OWNED_HOUSES && obj.getID() == POH_GATE && !item.getNoted()
			&& inArray(item.getCatalogId(), VALID_KEYS);
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (player.getConfig().HAS_PLAYER_OWNED_HOUSES && obj.getID() == POH_GATE && !item.getNoted()
			&& inArray(item.getCatalogId(), VALID_KEYS)) {
			player.message("you go through the gate");
			doGate(player, obj);
		}
	}
}
