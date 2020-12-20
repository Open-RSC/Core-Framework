package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.AbstractShop;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;

import static com.openrsc.server.plugins.Functions.*;

public final class TeaSeller extends AbstractShop implements TakeObjTrigger {

	private final Shop shop = new Shop(false, 30000, 100, 60, 2, new Item(ItemId.CUP_OF_TEA.id(),
		20));

	@Override
	public boolean blockTakeObj(final Player player, final GroundItem i) {
		return i.getID() == ItemId.DISPLAY_TEA.id();
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.TEA_SELLER.id();
	}

	@Override
	public Shop[] getShops(World world) {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public Shop getShop() {
		return shop;
	}

	@Override
	public void onTakeObj(final Player player, final GroundItem i) {
		if (i.getID() == ItemId.DISPLAY_TEA.id()) {
			final Npc n = player.getWorld().getNpcById(NpcId.TEA_SELLER.id());
			if (n == null) {
				return;
			}
			npcsay(player, n, "Hey ! get your hands off that tea !",
				"That's for display purposes only",
				"Im not running a charity here !");
		}
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			return;
		}
		npcsay(player, n, "Greetings!",
			"Are you in need of refreshment ?");

		final String[] options = new String[]{"Yes please", "No thanks",
			"What are you selling ?"};
		int option = multi(player, n, options);
		switch (option) {
			case 0:
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
				break;
			case 1:
				npcsay(player, n, "Well, if you're sure",
					"You know where to come if you do !");
				break;
			case 2:
				npcsay(player, n, "Only the most delicious infusion",
					"Of the leaves of the tea plant",
					"Grown in the exotic regions of this world...",
					"Buy yourself a cup !");
				break;
		}
	}
}
