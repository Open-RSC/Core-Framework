package com.openrsc.server.plugins.npcs.varrock;

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

public final class ThessaliasClothes extends AbstractShop implements TakeObjTrigger {

	private final Shop shop = new Shop(false, 30000, 100, 55, 3, new Item(ItemId.WHITE_APRON.id(),
		3), new Item(ItemId.LEATHER_ARMOUR.id(), 12), new Item(ItemId.LEATHER_GLOVES.id(), 10), new Item(ItemId.BOOTS.id(), 10),
		new Item(ItemId.BROWN_APRON.id(), 1), new Item(ItemId.PINK_SKIRT.id(), 5), new Item(ItemId.BLACK_SKIRT.id(), 3),
		new Item(ItemId.BLUE_SKIRT.id(), 2), new Item(ItemId.RED_CAPE.id(), 4), new Item(ItemId.SILK.id(), 5),
		new Item(ItemId.PRIEST_ROBE.id(), 3), new Item(ItemId.PRIEST_GOWN.id(), 3));

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.THESSALIA.id();
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
		say(player, n, "Hello");
		npcsay(player, n, "Do you want to buy any fine clothes?");

		String[] options;
		int extraOptions = 0;
		boolean ears = player.getCarriedItems().hasCatalogID(ItemId.BUNNY_EARS.id()) || player.getBank().countId(ItemId.BUNNY_EARS.id()) > 0;
		boolean scythe = player.getCarriedItems().hasCatalogID(ItemId.SCYTHE.id()) || player.getBank().countId(ItemId.SCYTHE.id()) > 0;
		if (player.getCache().hasKey("bunny_ears") && player.getCache().hasKey("scythe") && !scythe && !ears) {
			options = new String[]{
				"I have lost my scythe can I get another one please?",
				"I have lost my bunny ears can I get some more please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 3;
		} else if (player.getCache().hasKey("scythe") && !scythe) {
			options = new String[]{
				"I have lost my scythe can I get another one please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 2;
		} else if (player.getCache().hasKey("bunny_ears") && !ears) {
			options = new String[]{
				"I have lost my bunny ears can I get some more please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 1;
		} else {
			options = new String[]{
				"What have you got?",
				"No, thank you"
			};
		}
		int option = multi(player, n, options);
		if (extraOptions > 0) {
			int item = 0;
			switch (extraOptions) {
				case 0:
					break;
				case 1: // Bunny Ears
					item = 1;
					break;
				case 2: // Scythe
					item = 2;
					break;
				case 3:
					if (option == 0) item = 2; // Scythe
					else if (option == 1) item = 1; // Ears
					break;

			}
			if (item == 1) {
				npcsay(player, n, "Ohh you poor dear, I have some more here");
				player.message("Thessalia gives you some new bunny ears");
				give(player, ItemId.BUNNY_EARS.id(), 1);
			} else if (item == 2) {
				npcsay(player, n, "Ohh you poor dear, I have another here");
				player.message("Thessalia gives you a new scythe");
				give(player, ItemId.SCYTHE.id(), 1);
			}

		} else {
			if (option == 0) {
				player.setAccessingShop(shop);
				ActionSender.showShop(player, shop);
			}
		}
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.BUNNY_EARS.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.BUNNY_EARS.id()) || player.getBank().countId(ItemId.BUNNY_EARS.id()) > 0) {
					player.message("You don't need another set of bunny ears");
					player.message("You only have one head");
					return;
				}
			}
			if(!player.getCache().hasKey("bunny_ears") || player.getCache().getInt("bunny_ears") == 0) {
				player.getCache().put("bunny_ears", 1);
			}
		}
		else if (i.getID() == ItemId.SCYTHE.id()) {
			if(!player.isAdmin()) {
				if (player.getCarriedItems().hasCatalogID(ItemId.SCYTHE.id()) || player.getBank().countId(ItemId.SCYTHE.id()) > 0) {
					player.message("You don't need another scythe");
					player.message("You already have one");
					return;
				}
			}
			if(!player.getCache().hasKey("scythe") || player.getCache().getInt("scythe") == 0) {
				player.getCache().put("scythe", 1);
			}
		}

		player.groundItemTake(i);
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		return i.getID() == ItemId.BUNNY_EARS.id() || i.getID() == ItemId.SCYTHE.id();
	}
}
