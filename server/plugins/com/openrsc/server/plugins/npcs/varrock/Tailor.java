package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class Tailor implements ShopInterface,
	TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 130, 40, 2, new Item(ItemId.CHEFS_HAT.id(),
		0), new Item(ItemId.BLUE_WIZARDSHAT.id(), 3), new Item(ItemId.YELLOW_CAPE.id(), 1), new Item(ItemId.GREY_WOLF_FUR.id(), 3),
		new Item(ItemId.FUR.id(), 3), new Item(ItemId.NEEDLE.id(), 3), new Item(ItemId.THREAD.id(), 100),
		new Item(ItemId.LEATHER_GLOVES.id(), 10), new Item(ItemId.BOOTS.id(), 10), new Item(ItemId.PRIEST_ROBE.id(), 3),
		new Item(ItemId.PRIEST_GOWN.id(), 3), new Item(ItemId.BROWN_APRON.id(), 1), new Item(ItemId.PINK_SKIRT.id(), 5),
		new Item(ItemId.BLACK_SKIRT.id(), 3), new Item(ItemId.BLUE_SKIRT.id(), 2), new Item(ItemId.RED_CAPE.id(), 4),
		new Item(ItemId.EYE_PATCH.id(), 3));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.TAILOR.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Now you look like someone who goes to a lot of fancy dress parties");
		playerTalk(p, n, "Errr... what are you saying exactly?");
		npcTalk(p, n, "I'm just saying that perhaps you would like to peruse my selection of garments");
		int opt = showMenu(p, n, false, //do not send over
			"I think I might just leave the perusing for now thanks",
			"OK,lets see what you've got then");
		if (opt == 0) {
			playerTalk(p, n, "I think I might just leave the perusing for now thanks");
		} else if (opt == 1) {
			playerTalk(p, n, "OK,let's see what you've got then");
			p.setAccessingShop(shop);
			ActionSender.showShop(p, shop);
		}
	}

}
