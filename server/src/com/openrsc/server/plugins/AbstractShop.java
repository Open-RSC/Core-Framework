package com.openrsc.server.plugins;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.MessageType;

public abstract class AbstractShop implements OpNpcTrigger, TalkNpcTrigger {

	public abstract Shop[] getShops(World world);

	public abstract boolean isMembers();

	public abstract Shop getShop();

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		Npc storeOwner = player.getWorld().getNpc(n.getID(),
			player.getX() - 2, player.getX() + 2,
			player.getY() - 2, player.getY() + 2);
		if (storeOwner == null) return;
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD
			&& player.getWorld().getServer().getEntityHandler().getNpcDef(n.getID()).isMembers()) {
			player.message("you must be on a members' world to do that");
			return;
		}
		if (command.equalsIgnoreCase("Trade") && player.getConfig().RIGHT_CLICK_TRADE) {
			if (!player.getQolOptOut()) {
				Shop shop = getShop();
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			} else {
				player.playerServerMessage(MessageType.QUEST, "Right click trading is a QoL feature which you are opted out of.");
				player.playerServerMessage(MessageType.QUEST, "Consider using an original RSC client so that you don't see the option.");
			}
		}
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		return blockTalkNpc(player, n);
	}
}
