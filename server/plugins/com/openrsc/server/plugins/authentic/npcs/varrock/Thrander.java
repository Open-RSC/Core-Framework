package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.RuneScript.*;

public class Thrander implements TalkNpcTrigger, UseNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.THRANDER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay("Hello I'm Thrander the smith",
			"I'm an expert in armour modification",
			"Give me your armour designed for men",
			"And I can convert it into something more comfortable for a women",
			"And visa versa"
		);
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.THRANDER.id() && (isExchangeable(player, item) || isDragon(player, item));
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (isExchangeable(player, item)) {

			int newId = getNewId(item);
			Item newItem = new Item(newId);

			String itemNameLower = item.getDef(player.getWorld()).getName().toLowerCase();
			String newItemNameLower = newItem.getDef(player.getWorld()).getName().toLowerCase();

			// From replays, "Steel" seems to always be capitalized, rune is always lowercase. Unsure about other metals
			String metal = itemNameLower.substring(0, itemNameLower.indexOf(' '));
			if (metal.equals("steel")) {
				itemNameLower = itemNameLower.substring(0, 1).toUpperCase() + itemNameLower.substring(1);
				newItemNameLower = newItemNameLower.substring(0, 1).toUpperCase() + newItemNameLower.substring(1);
			}

			remove(item.getCatalogId(), 1);

			if (itemNameLower.contains("top") || itemNameLower.contains("body")) {
				mes("You give Thrander a " + itemNameLower);
				delay(3);
				mes("Thrander hammers it for a bit");
				delay(3);
				mes("Thrander gives you a " + newItemNameLower);
			} else if (itemNameLower.contains("skirt")) {
				mes("You give Thrander a " + metal + " skirt");
				delay(3);
				mes("Thrander hammers it for a bit");
				delay(3);
				mes("Thrander gives you some " + newItemNameLower);
			} else if (itemNameLower.contains("legs")) {
				mes("You give Thrander some " + itemNameLower);
				delay(3);
				mes("Thrander hammers it for a bit");
				delay(3);
				mes("Thrander gives you a " + metal + " skirt");
			}

			give(newId, 1);

		} else if (isDragon(player, item)) {
			npcsay("Where did you get that?",
				"I'm sorry",
				"But I can't work this kind of metal");
		} else {
			mes("Nothing interesting happens");
		}
	}

	public int getNewId(Item item) {
		int newId = ItemId.NOTHING.id();
		switch (ItemId.getById(item.getCatalogId())) {
			case BRONZE_PLATE_MAIL_TOP:
				newId = ItemId.BRONZE_PLATE_MAIL_BODY.id();
				break;
			case IRON_PLATE_MAIL_TOP:
				newId = ItemId.IRON_PLATE_MAIL_BODY.id();
				break;
			case STEEL_PLATE_MAIL_TOP:
				newId = ItemId.STEEL_PLATE_MAIL_BODY.id();
				break;
			case BLACK_PLATE_MAIL_TOP:
				newId = ItemId.BLACK_PLATE_MAIL_BODY.id();
				break;
			case MITHRIL_PLATE_MAIL_TOP:
				newId = ItemId.MITHRIL_PLATE_MAIL_BODY.id();
				break;
			case ADAMANTITE_PLATE_MAIL_TOP:
				newId = ItemId.ADAMANTITE_PLATE_MAIL_BODY.id();
				break;
			case RUNE_PLATE_MAIL_TOP:
				newId = ItemId.RUNE_PLATE_MAIL_BODY.id();
				break;
			case BRONZE_PLATE_MAIL_BODY:
				newId = ItemId.BRONZE_PLATE_MAIL_TOP.id();
				break;
			case IRON_PLATE_MAIL_BODY:
				newId = ItemId.IRON_PLATE_MAIL_TOP.id();
				break;
			case STEEL_PLATE_MAIL_BODY:
				newId = ItemId.STEEL_PLATE_MAIL_TOP.id();
				break;
			case BLACK_PLATE_MAIL_BODY:
				newId = ItemId.BLACK_PLATE_MAIL_TOP.id();
				break;
			case MITHRIL_PLATE_MAIL_BODY:
				newId = ItemId.MITHRIL_PLATE_MAIL_TOP.id();
				break;
			case ADAMANTITE_PLATE_MAIL_BODY:
				newId = ItemId.ADAMANTITE_PLATE_MAIL_TOP.id();
				break;
			case RUNE_PLATE_MAIL_BODY:
				newId = ItemId.RUNE_PLATE_MAIL_TOP.id();
				break;
			case BRONZE_PLATED_SKIRT:
				newId = ItemId.BRONZE_PLATE_MAIL_LEGS.id();
				break;
			case IRON_PLATED_SKIRT:
				newId = ItemId.IRON_PLATE_MAIL_LEGS.id();
				break;
			case STEEL_PLATED_SKIRT:
				newId = ItemId.STEEL_PLATE_MAIL_LEGS.id();
				break;
			case BLACK_PLATED_SKIRT:
				newId = ItemId.BLACK_PLATE_MAIL_LEGS.id();
				break;
			case MITHRIL_PLATED_SKIRT:
				newId = ItemId.MITHRIL_PLATE_MAIL_LEGS.id();
				break;
			case ADAMANTITE_PLATED_SKIRT:
				newId = ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id();
				break;
			case RUNE_SKIRT:
				newId = ItemId.RUNE_PLATE_MAIL_LEGS.id();;
				break;
			case BRONZE_PLATE_MAIL_LEGS:
				newId = ItemId.BRONZE_PLATED_SKIRT.id();
				break;
			case IRON_PLATE_MAIL_LEGS:
				newId = ItemId.IRON_PLATED_SKIRT.id();
				break;
			case STEEL_PLATE_MAIL_LEGS:
				newId = ItemId.STEEL_PLATED_SKIRT.id();
				break;
			case BLACK_PLATE_MAIL_LEGS:
				newId = ItemId.BLACK_PLATED_SKIRT.id();
				break;
			case MITHRIL_PLATE_MAIL_LEGS:
				newId = ItemId.MITHRIL_PLATED_SKIRT.id();
				break;
			case ADAMANTITE_PLATE_MAIL_LEGS:
				newId = ItemId.ADAMANTITE_PLATED_SKIRT.id();
				break;
			case RUNE_PLATE_MAIL_LEGS:
				newId = ItemId.RUNE_SKIRT.id();
				break;
			case BRONZE_CHAIN_MAIL_TOP:
				newId = ItemId.BRONZE_CHAIN_MAIL_BODY.id();
				break;
			case IRON_CHAIN_MAIL_TOP:
				newId = ItemId.IRON_CHAIN_MAIL_BODY.id();
				break;
			case STEEL_CHAIN_MAIL_TOP:
				newId = ItemId.STEEL_CHAIN_MAIL_BODY.id();
				break;
			case BLACK_CHAIN_MAIL_TOP:
				newId = ItemId.BLACK_CHAIN_MAIL_BODY.id();
				break;
			case MITHRIL_CHAIN_MAIL_TOP:
				newId = ItemId.MITHRIL_CHAIN_MAIL_BODY.id();
				break;
			case ADAMANTITE_CHAIN_MAIL_TOP:
				newId = ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id();
				break;
			case RUNE_CHAIN_MAIL_TOP:
				newId = ItemId.RUNE_CHAIN_MAIL_BODY.id();
				break;
			case BRONZE_CHAIN_MAIL_BODY:
				newId = ItemId.BRONZE_CHAIN_MAIL_TOP.id();
				break;
			case IRON_CHAIN_MAIL_BODY:
				newId = ItemId.IRON_CHAIN_MAIL_TOP.id();
				break;
			case STEEL_CHAIN_MAIL_BODY:
				newId = ItemId.STEEL_CHAIN_MAIL_TOP.id();
				break;
			case BLACK_CHAIN_MAIL_BODY:
				newId = ItemId.BLACK_CHAIN_MAIL_TOP.id();
				break;
			case MITHRIL_CHAIN_MAIL_BODY:
				newId = ItemId.MITHRIL_CHAIN_MAIL_TOP.id();
				break;
			case ADAMANTITE_CHAIN_MAIL_BODY:
				newId = ItemId.ADAMANTITE_CHAIN_MAIL_TOP.id();
				break;
			case RUNE_CHAIN_MAIL_BODY:
				newId = ItemId.RUNE_CHAIN_MAIL_TOP.id();
				break;
			case IRONMAN_PLATE_TOP:
				newId = ItemId.IRONMAN_PLATEBODY.id();
				break;
			case IRONMAN_PLATED_SKIRT:
				newId = ItemId.IRONMAN_PLATELEGS.id();
				break;
			case ULTIMATE_IRONMAN_PLATE_TOP:
				newId = ItemId.ULTIMATE_IRONMAN_PLATEBODY.id();
				break;
			case ULTIMATE_IRONMAN_PLATED_SKIRT:
				newId = ItemId.ULTIMATE_IRONMAN_PLATELEGS.id();
				break;
			case HARDCORE_IRONMAN_PLATE_TOP:
				newId = ItemId.HARDCORE_IRONMAN_PLATEBODY.id();
				break;
			case HARDCORE_IRONMAN_PLATED_SKIRT:
				newId = ItemId.HARDCORE_IRONMAN_PLATELEGS.id();
				break;
			case IRONMAN_PLATEBODY:
				newId = ItemId.IRONMAN_PLATE_TOP.id();
				break;
			case IRONMAN_PLATELEGS:
				newId = ItemId.IRONMAN_PLATED_SKIRT.id();
				break;
			case ULTIMATE_IRONMAN_PLATEBODY:
				newId = ItemId.ULTIMATE_IRONMAN_PLATE_TOP.id();
				break;
			case ULTIMATE_IRONMAN_PLATELEGS:
				newId = ItemId.ULTIMATE_IRONMAN_PLATED_SKIRT.id();
				break;
			case HARDCORE_IRONMAN_PLATEBODY:
				newId = ItemId.HARDCORE_IRONMAN_PLATE_TOP.id();
				break;
			case HARDCORE_IRONMAN_PLATELEGS:
				newId = ItemId.HARDCORE_IRONMAN_PLATED_SKIRT.id();
				break;
			default:
				break;
		}
		return newId;
	}

	private boolean isExchangeable(final Player player, final Item item) {
		final boolean isAuthenticItem = Functions.inArray(item.getCatalogId(), ItemId.BRONZE_PLATE_MAIL_TOP.id(), ItemId.IRON_PLATE_MAIL_TOP.id(), ItemId.STEEL_PLATE_MAIL_TOP.id(),
			ItemId.BLACK_PLATE_MAIL_TOP.id(), ItemId.MITHRIL_PLATE_MAIL_TOP.id(), ItemId.ADAMANTITE_PLATE_MAIL_TOP.id(), ItemId.RUNE_PLATE_MAIL_TOP.id(),
			ItemId.BRONZE_PLATE_MAIL_BODY.id(), ItemId.IRON_PLATE_MAIL_BODY.id(), ItemId.STEEL_PLATE_MAIL_BODY.id(),
			ItemId.BLACK_PLATE_MAIL_BODY.id(), ItemId.MITHRIL_PLATE_MAIL_BODY.id(), ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), ItemId.RUNE_PLATE_MAIL_BODY.id(),
			ItemId.BRONZE_PLATED_SKIRT.id(), ItemId.IRON_PLATED_SKIRT.id(), ItemId.STEEL_PLATED_SKIRT.id(),
			ItemId.BLACK_PLATED_SKIRT.id(), ItemId.MITHRIL_PLATED_SKIRT.id(), ItemId.ADAMANTITE_PLATED_SKIRT.id(), ItemId.RUNE_SKIRT.id(),
			ItemId.BRONZE_PLATE_MAIL_LEGS.id(), ItemId.IRON_PLATE_MAIL_LEGS.id(), ItemId.STEEL_PLATE_MAIL_LEGS.id(),
			ItemId.BLACK_PLATE_MAIL_LEGS.id(), ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), ItemId.RUNE_PLATE_MAIL_LEGS.id());

		boolean isChainmail = false;
		if (player.getConfig().WANT_CUSTOM_SPRITES) {
			isChainmail = Functions.inArray(item.getCatalogId(), ItemId.BRONZE_CHAIN_MAIL_TOP.id(), ItemId.IRON_CHAIN_MAIL_TOP.id(),
				ItemId.STEEL_CHAIN_MAIL_TOP.id(), ItemId.BLACK_CHAIN_MAIL_TOP.id(), ItemId.MITHRIL_CHAIN_MAIL_TOP.id(), ItemId.ADAMANTITE_CHAIN_MAIL_TOP.id(),
				ItemId.RUNE_CHAIN_MAIL_TOP.id(), ItemId.BRONZE_CHAIN_MAIL_BODY.id(), ItemId.IRON_CHAIN_MAIL_BODY.id(),
				ItemId.STEEL_CHAIN_MAIL_BODY.id(), ItemId.BLACK_CHAIN_MAIL_BODY.id(), ItemId.MITHRIL_CHAIN_MAIL_BODY.id(), ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(),
				ItemId.RUNE_CHAIN_MAIL_BODY.id());
		}

		boolean isIronmanItem = false;
		if (player.getConfig().SPAWN_IRON_MAN_NPCS) {
			isIronmanItem = Functions.inArray(item.getCatalogId(), ItemId.IRONMAN_PLATEBODY.id(), ItemId.IRONMAN_PLATELEGS.id(), ItemId.ULTIMATE_IRONMAN_PLATEBODY.id(), ItemId.ULTIMATE_IRONMAN_PLATELEGS.id(), ItemId.HARDCORE_IRONMAN_PLATEBODY.id(), ItemId.HARDCORE_IRONMAN_PLATELEGS.id(), ItemId.IRONMAN_PLATE_TOP.id(), ItemId.IRONMAN_PLATED_SKIRT.id(), ItemId.ULTIMATE_IRONMAN_PLATE_TOP.id(), ItemId.ULTIMATE_IRONMAN_PLATED_SKIRT.id(), ItemId.HARDCORE_IRONMAN_PLATE_TOP.id(), ItemId.HARDCORE_IRONMAN_PLATED_SKIRT.id());
		}

		return isAuthenticItem || isChainmail || isIronmanItem;
	}

	private boolean isDragon(final Player player, final Item item) {
		if (!player.getConfig().WANT_CUSTOM_SPRITES) return false;

		return Functions.inArray(item.getCatalogId(), ItemId.DRAGON_SCALE_MAIL.id(), ItemId.DRAGON_SCALE_MAIL_TOP.id(),
			ItemId.DRAGON_PLATE_MAIL_BODY.id(), ItemId.DRAGON_PLATE_MAIL_TOP.id(), ItemId.DRAGON_PLATE_MAIL_LEGS.id(),
			ItemId.DRAGON_PLATED_SKIRT.id());
	}

}
