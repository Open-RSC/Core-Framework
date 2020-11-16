package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Thrander implements TalkNpcTrigger, UseNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.THRANDER.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "Hello I'm Thrander the smith",
			"I'm an expert in armour modification",
			"Give me your armour designed for men",
			"And I can convert it into something more comfortable for a women",
			"And visa versa"
		);
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.THRANDER.id() && getNewID(item) != ItemId.NOTHING.id();
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		if (inArray(item.getCatalogId(), ItemId.BRONZE_PLATE_MAIL_TOP.id(), ItemId.IRON_PLATE_MAIL_TOP.id(), ItemId.STEEL_PLATE_MAIL_TOP.id(),
			ItemId.BLACK_PLATE_MAIL_TOP.id(), ItemId.MITHRIL_PLATE_MAIL_TOP.id(), ItemId.ADAMANTITE_PLATE_MAIL_TOP.id(), ItemId.RUNE_PLATE_MAIL_TOP.id(),
			ItemId.BRONZE_PLATE_MAIL_BODY.id(), ItemId.IRON_PLATE_MAIL_BODY.id(), ItemId.STEEL_PLATE_MAIL_BODY.id(),
			ItemId.BLACK_PLATE_MAIL_BODY.id(), ItemId.MITHRIL_PLATE_MAIL_BODY.id(), ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), ItemId.RUNE_PLATE_MAIL_BODY.id(),
			ItemId.BRONZE_PLATED_SKIRT.id(), ItemId.IRON_PLATED_SKIRT.id(), ItemId.STEEL_PLATED_SKIRT.id(),
			ItemId.BLACK_PLATED_SKIRT.id(), ItemId.MITHRIL_PLATED_SKIRT.id(), ItemId.ADAMANTITE_PLATED_SKIRT.id(), ItemId.RUNE_SKIRT.id(),
			ItemId.BRONZE_PLATE_MAIL_LEGS.id(), ItemId.IRON_PLATE_MAIL_LEGS.id(), ItemId.STEEL_PLATE_MAIL_LEGS.id(),
			ItemId.BLACK_PLATE_MAIL_LEGS.id(), ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), ItemId.RUNE_PLATE_MAIL_LEGS.id(),
			ItemId.DRAGON_PLATE_MAIL_BODY.id(), ItemId.DRAGON_PLATE_MAIL_TOP.id(), ItemId.DRAGON_PLATE_MAIL_LEGS.id(), ItemId.DRAGON_PLATED_SKIRT.id())) {
			int newID = getNewID(item);
			Item changedItem = new Item(newID);
			String itemLower, changedItemLower;
			itemLower = item.getDef(player.getWorld()).getName().toLowerCase();
			changedItemLower = changedItem.getDef(player.getWorld()).getName().toLowerCase();
			if (player.getCarriedItems().remove(new Item(item.getCatalogId())) != -1) {
				if (itemLower.contains("top") || itemLower.contains("body")) {
					mes(npc, "You give Thrander a " + itemLower,
							"Thrander hammers it for a bit");
					delay(2);
					player.message("Thrander gives you a " + changedItemLower);
				} else if (item.getDef(player.getWorld()).getName().toLowerCase().contains("skirt")) {
					String metal = itemLower.substring(0, itemLower.indexOf(' '));
					mes(npc, "You give Thrander a " + metal + " plated skirt",
							"Thrander hammers it for a bit");
					delay(2);
					player.message("Thrander gives you some " + changedItemLower);
				} else if (item.getDef(player.getWorld()).getName().toLowerCase().contains("legs")) {
					String metal = itemLower.substring(0, itemLower.indexOf(' '));
					mes(npc, "You give Thrander some " + itemLower,
							"Thrander hammers it for a bit");
					delay(2);
					player.message("Thrander gives you a " + metal + " plated skirt");
				}
				give(player, newID, 1);
			}
		}
	}

	public int getNewID(Item item) {
		int newID = ItemId.NOTHING.id();
		switch (ItemId.getById(item.getCatalogId())) {
			case BRONZE_PLATE_MAIL_TOP:
				newID = ItemId.BRONZE_PLATE_MAIL_BODY.id();
				break;
			case IRON_PLATE_MAIL_TOP:
				newID = ItemId.IRON_PLATE_MAIL_BODY.id();
				break;
			case STEEL_PLATE_MAIL_TOP:
				newID = ItemId.STEEL_PLATE_MAIL_BODY.id();
				break;
			case BLACK_PLATE_MAIL_TOP:
				newID = ItemId.BLACK_PLATE_MAIL_BODY.id();
				break;
			case MITHRIL_PLATE_MAIL_TOP:
				newID = ItemId.MITHRIL_PLATE_MAIL_BODY.id();
				break;
			case ADAMANTITE_PLATE_MAIL_TOP:
				newID = ItemId.ADAMANTITE_PLATE_MAIL_BODY.id();
				break;
			case RUNE_PLATE_MAIL_TOP:
				newID = ItemId.RUNE_PLATE_MAIL_BODY.id();
				break;
			case DRAGON_PLATE_MAIL_TOP:
				newID = ItemId.DRAGON_PLATE_MAIL_BODY.id();
				break;
			case BRONZE_PLATE_MAIL_BODY:
				newID = ItemId.BRONZE_PLATE_MAIL_TOP.id();
				break;
			case IRON_PLATE_MAIL_BODY:
				newID = ItemId.IRON_PLATE_MAIL_TOP.id();
				break;
			case STEEL_PLATE_MAIL_BODY:
				newID = ItemId.STEEL_PLATE_MAIL_TOP.id();
				break;
			case BLACK_PLATE_MAIL_BODY:
				newID = ItemId.BLACK_PLATE_MAIL_TOP.id();
				break;
			case MITHRIL_PLATE_MAIL_BODY:
				newID = ItemId.MITHRIL_PLATE_MAIL_TOP.id();
				break;
			case ADAMANTITE_PLATE_MAIL_BODY:
				newID = ItemId.ADAMANTITE_PLATE_MAIL_TOP.id();
				break;
			case RUNE_PLATE_MAIL_BODY:
				newID = ItemId.RUNE_PLATE_MAIL_TOP.id();
				break;
			case DRAGON_PLATE_MAIL_BODY:
				newID = ItemId.DRAGON_PLATE_MAIL_TOP.id();
				break;
			case BRONZE_PLATED_SKIRT:
				newID = ItemId.BRONZE_PLATE_MAIL_LEGS.id();
				break;
			case IRON_PLATED_SKIRT:
				newID = ItemId.IRON_PLATE_MAIL_LEGS.id();
				break;
			case STEEL_PLATED_SKIRT:
				newID = ItemId.STEEL_PLATE_MAIL_LEGS.id();
				break;
			case BLACK_PLATED_SKIRT:
				newID = ItemId.BLACK_PLATE_MAIL_LEGS.id();
				break;
			case MITHRIL_PLATED_SKIRT:
				newID = ItemId.MITHRIL_PLATE_MAIL_LEGS.id();
				break;
			case ADAMANTITE_PLATED_SKIRT:
				newID = ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id();
				break;
			case RUNE_SKIRT:
				newID = ItemId.RUNE_PLATE_MAIL_LEGS.id();;
				break;
			case DRAGON_PLATED_SKIRT:
				newID = ItemId.DRAGON_PLATE_MAIL_LEGS.id();
				break;
			case BRONZE_PLATE_MAIL_LEGS:
				newID = ItemId.BRONZE_PLATED_SKIRT.id();
				break;
			case IRON_PLATE_MAIL_LEGS:
				newID = ItemId.IRON_PLATED_SKIRT.id();
				break;
			case STEEL_PLATE_MAIL_LEGS:
				newID = ItemId.STEEL_PLATED_SKIRT.id();
				break;
			case BLACK_PLATE_MAIL_LEGS:
				newID = ItemId.BLACK_PLATED_SKIRT.id();
				break;
			case MITHRIL_PLATE_MAIL_LEGS:
				newID = ItemId.MITHRIL_PLATED_SKIRT.id();
				break;
			case ADAMANTITE_PLATE_MAIL_LEGS:
				newID = ItemId.ADAMANTITE_PLATED_SKIRT.id();
				break;
			case RUNE_PLATE_MAIL_LEGS:
				newID = ItemId.RUNE_SKIRT.id();
				break;
			case DRAGON_PLATE_MAIL_LEGS:
				newID = ItemId.DRAGON_PLATED_SKIRT.id();
				break;
			default:
				break;
		}
		return newID;
	}

}
