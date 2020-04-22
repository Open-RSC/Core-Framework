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
	public void onOpNpc(Npc n, String command, Player p) {
		if (command.equalsIgnoreCase("Trade") && p.getWorld().getServer().getConfig().RIGHT_CLICK_TRADE) {
			Shop shop = getShop();
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

	@Override
	public boolean blockOpNpc(Npc n, String command, Player p) {
		return blockTalkNpc(p, n);
	}
}
