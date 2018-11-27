package com.openrsc.server.plugins.misc;

import com.openrsc.server.event.rsc.impl.BallProjectileEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnPlayerListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnPlayerExecutiveListener;
import com.openrsc.server.Server;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class GnomeBall implements InvUseOnPlayerListener, InvUseOnPlayerExecutiveListener {

	@Override
	public void onInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		if (item.getID() == ItemId.GNOME_BALL.id()) {
			if (otherPlayer.isIronMan(1) || otherPlayer.isIronMan(2) || otherPlayer.isIronMan(3)) {
				player.message(otherPlayer.getUsername() + " is an Iron Man. He stands alone.");
			} else if (!otherPlayer.getInventory().full()) {
				Server.getServer().getGameEventHandler().add(new BallProjectileEvent(player, otherPlayer, 3) {
					@Override
					public void doSpell() {
						if (otherPlayer.isPlayer()) {
							player.getInventory().remove(item);
							player.message("You have passed the gnome ball to " + otherPlayer.getUsername());
							otherPlayer.getInventory().add(item);
							otherPlayer.message(player.getUsername() + " has passed you the gnome ball");
						}
					}
				});
			} else {
				player.message(otherPlayer.getUsername() + " has a full inventory and cannot hold the ball");
				otherPlayer.message(player.getUsername() + " tried to pass you a Gnome Ball but your inventory is full");
			}
		}
	}

	@Override
	public boolean blockInvUseOnPlayer(Player player, Player otherPlayer, Item item) {
		return item.getID() == ItemId.GNOME_BALL.id();
	}
}
