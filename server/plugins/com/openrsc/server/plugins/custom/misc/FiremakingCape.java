package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.RuneScript.*;

public class FiremakingCape implements OpInvTrigger {
	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() != ItemId.FIREMAKING_CAPE.id() || !command.equalsIgnoreCase("Combust")) return;

		if (player.getViewArea().getGameObject(player.getLocation()) != null) {
			player.playerServerMessage(MessageType.QUEST, "You can't light a fire here");
			return;
		}

		if (Formulae.getHeight(player.getLocation()) == 3) {
			mes("Without direct sunlight, your cape is unable to harness enough energy to start a fire");
			return;
		}

		Functions.thinkbubble(item);

		// Light fire
		mes("You concentrate the power of the sun to create a fire");

		// Remove logs and add fire scenery.
		final int duration = 90000;
		final GameObject fire = new GameObject(player.getWorld(), player.getLocation(), 97, 0, 0);
		player.getWorld().registerGameObject(fire);
		player.getWorld().getServer().getGameEventHandler().add(
			new SingleEvent(player.getWorld(), null, duration, "Firecape Fire Removal") {
				@Override
				public void action() {
					getWorld().registerItem(new GroundItem(
						getWorld(),
						ItemId.ASHES.id(),
						fire.getX(),
						fire.getY(),
						1, (Player) null));
					getWorld().unregisterGameObject(fire);
				}
			}
		);

	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.FIREMAKING_CAPE.id();
	}
}
