package com.openrsc.server.plugins.authentic.npcs.edgeville;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;

import static com.openrsc.server.plugins.Functions.*;

public class OziachsRunePlateShop extends AbstractShop {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.RUNE_PLATE_MAIL_BODY.id(),
		2));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.OZIACH.id() && player.getQuestStage(Quests.DRAGON_SLAYER) == -1;
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		say(player, n, "I have slain the dragon");
		npcsay(player, n, "Well done");
		final int option = multi(player, n, "Can I buy a rune plate mail body now please?", "Thank you");
		if (option == 0) {
			player.setAccessingShop(shop);
			ActionSender.showShop(player, shop);
		}
	}
}
