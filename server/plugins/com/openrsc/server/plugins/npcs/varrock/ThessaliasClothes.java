package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.Shop;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.ShopInterface;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public final class ThessaliasClothes implements PickupListener, PickupExecutiveListener,
	ShopInterface, TalkToNpcExecutiveListener, TalkToNpcListener {

	private final Shop shop = new Shop(false, 30000, 100, 55, 3, new Item(ItemId.WHITE_APRON.id(),
		3), new Item(ItemId.LEATHER_ARMOUR.id(), 12), new Item(ItemId.LEATHER_GLOVES.id(), 10), new Item(ItemId.BOOTS.id(), 10),
		new Item(ItemId.BROWN_APRON.id(), 1), new Item(ItemId.PINK_SKIRT.id(), 5), new Item(ItemId.BLACK_SKIRT.id(), 3),
		new Item(ItemId.BLUE_SKIRT.id(), 2), new Item(ItemId.RED_CAPE.id(), 4), new Item(ItemId.SILK.id(), 5),
		new Item(ItemId.PRIEST_ROBE.id(), 3), new Item(ItemId.PRIEST_GOWN.id(), 3));

	@Override
	public boolean blockTalkToNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.THESSALIA.id();
	}

	@Override
	public Shop[] getShops() {
		return new Shop[]{shop};
	}

	@Override
	public boolean isMembers() {
		return false;
	}

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		playerTalk(p, n, "Hello");
		npcTalk(p, n, "Do you want to buy any fine clothes?");

		String[] options;
		int extraOptions = 0;
		boolean ears = p.getInventory().hasItemId(1156) || p.getBank().countId(1156) > 0;
		boolean scythe = p.getInventory().hasItemId(1289) || p.getBank().countId(1289) > 0;
		if (p.getCache().hasKey("bunny_ears") && p.getCache().hasKey("scythe") && !scythe && !ears) {
			options = new String[]{
				"I have lost my scythe can I get another one please?",
				"I have lost my bunny ears can I get some more please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 3;
		} else if (p.getCache().hasKey("scythe") && !scythe) {
			options = new String[]{
				"I have lost my scythe can I get another one please?",
				"What have you got?",
				"No, thank you"
			};
			extraOptions = 2;
		} else if (p.getCache().hasKey("bunny_ears") && !ears) {
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
		int option = showMenu(p, n, options);
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
				npcTalk(p, n, "Ohh you poor dear, I have some more here");
				p.message("Thessalia gives you some new bunny ears");
				addItem(p, ItemId.BUNNY_EARS.id(), 1);
			} else if (item == 2) {
				npcTalk(p, n, "Ohh you poor dear, I have another here");
				p.message("Thessalia gives you a new scythe");
				addItem(p, ItemId.SCYTHE.id(), 1);
			}

		} else {
			if (option == 0) {
				p.setAccessingShop(shop);
				ActionSender.showShop(p, shop);
			}
		}
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == ItemId.BUNNY_EARS.id()) {
			if(!p.isAdmin()) {
				if (p.getInventory().hasItemId(ItemId.BUNNY_EARS.id()) || p.getBank().countId(ItemId.BUNNY_EARS.id()) > 0) {
					p.message("You don't need another set of bunny ears");
					p.message("You only have one head");
					return;
				}
			}
			if(!p.getCache().hasKey("bunny_ears") || p.getCache().getInt("bunny_ears") == 0) {
				p.getCache().put("bunny_ears", 1);
			}
		}
		else if (i.getID() == ItemId.SCYTHE.id()) {
			if(!p.isAdmin()) {
				if (p.getInventory().hasItemId(ItemId.SCYTHE.id()) || p.getBank().countId(ItemId.SCYTHE.id()) > 0) {
					p.message("You don't need another scythe");
					p.message("You already have one");
					return;
				}
			}
			if(!p.getCache().hasKey("scythe") || p.getCache().getInt("scythe") == 0) {
				p.getCache().put("scythe", 1);
			}
		}

		p.groundItemTake(i);
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return i.getID() == ItemId.BUNNY_EARS.id() || i.getID() == ItemId.SCYTHE.id();
	}
}
