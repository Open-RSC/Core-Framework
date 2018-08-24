package com.openrsc.server.plugins.minigames.fishingtrawler;

import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;

public class BailingBucket implements InvActionExecutiveListener, InvActionListener {

	@Override
	public void onInvAction(Item item, Player player) {
		if (player.isBusy())
			return;
		if (World.getWorld().getFishingTrawler().getShipAreaWater().inBounds(player.getLocation())
				|| World.getWorld().getFishingTrawler().getShipArea().inBounds(player.getLocation())) {
			player.setBusyTimer(650);
			player.message("you bail a little water...");
			sleep(650);
			World.getWorld().getFishingTrawler().bailWater();
		} else {
			// player.message("");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player player) {
		return item.getID() == 1282;
	}

}
