package com.openrsc.server.plugins;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

public abstract class AbstractShop implements OpNpcTrigger, TalkNpcTrigger {

	public abstract Shop[] getShops(World world);

	public abstract boolean isMembers();

	public abstract Shop getShop();

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		if (command.equalsIgnoreCase("Trade") && player.getConfig().RIGHT_CLICK_TRADE) {
			Shop shop = getShop();
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		return blockTalkNpc(player, n);
	}
}
