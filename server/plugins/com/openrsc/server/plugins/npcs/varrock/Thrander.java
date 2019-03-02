package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;

public class Thrander implements TalkToNpcListener, TalkToNpcExecutiveListener, InvUseOnNpcListener, InvUseOnNpcExecutiveListener {

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.THRANDER.id();
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello I'm Thrander the smith",
			"I'm an expert in armour modification",
			"Give me your armour designed for men",
			"And I can convert it into something more comfortable for a women",
			"And visa versa"
		);
	}

	@Override
	public boolean blockInvUseOnNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.THRANDER.id() && getNewID(item) != ItemId.NOTHING.id();
	}

	@Override
	public void onInvUseOnNpc(Player player, Npc npc, Item item) {
		if (inArray(item.getID(), ItemId.BRONZE_PLATE_MAIL_TOP.id(), ItemId.IRON_PLATE_MAIL_TOP.id(), ItemId.STEEL_PLATE_MAIL_TOP.id(),
			ItemId.BLACK_PLATE_MAIL_TOP.id(), ItemId.MITHRIL_PLATE_MAIL_TOP.id(), ItemId.ADAMANTITE_PLATE_MAIL_TOP.id(), ItemId.RUNE_PLATE_MAIL_TOP.id(),
			ItemId.BRONZE_PLATE_MAIL_BODY.id(), ItemId.IRON_PLATE_MAIL_BODY.id(), ItemId.STEEL_PLATE_MAIL_BODY.id(),
			ItemId.BLACK_PLATE_MAIL_BODY.id(), ItemId.MITHRIL_PLATE_MAIL_BODY.id(), ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(), ItemId.RUNE_PLATE_MAIL_BODY.id(),
			ItemId.BRONZE_PLATED_SKIRT.id(), ItemId.IRON_PLATED_SKIRT.id(), ItemId.STEEL_PLATED_SKIRT.id(),
			ItemId.BLACK_PLATED_SKIRT.id(), ItemId.MITHRIL_PLATED_SKIRT.id(), ItemId.ADAMANTITE_PLATED_SKIRT.id(), ItemId.RUNE_SKIRT.id(),
			ItemId.BRONZE_PLATE_MAIL_LEGS.id(), ItemId.IRON_PLATE_MAIL_LEGS.id(), ItemId.STEEL_PLATE_MAIL_LEGS.id(),
			ItemId.BLACK_PLATE_MAIL_LEGS.id(), ItemId.MITHRIL_PLATE_MAIL_LEGS.id(), ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(), ItemId.RUNE_PLATE_MAIL_LEGS.id())) {
			int newID = getNewID(item);
			Item changedItem = getItem(newID);
			String itemLower, changedItemLower;
			itemLower = item.getDef().getName().toLowerCase();
			changedItemLower = changedItem.getDef().getName().toLowerCase();
			if (removeItem(player, item.getID(), 1)) {
				if (itemLower.contains("top") || itemLower.contains("body")) {
					message(player, npc, 1300, "You give Thrander a " + itemLower,
							"Thrander hammers it for a bit");
					player.message("Thrander gives you a " + changedItemLower);
				} else if (item.getDef().getName().toLowerCase().contains("skirt")) {
					String metal = itemLower.substring(0, itemLower.indexOf(' '));
					message(player, npc, 1300, "You give Thrander a " + metal + " plated skirt",
							"Thrander hammers it for a bit");
					player.message("Thrander gives you some " + changedItemLower);
				} else if (item.getDef().getName().toLowerCase().contains("legs")) {
					String metal = itemLower.substring(0, itemLower.indexOf(' '));
					message(player, npc, 1300, "You give Thrander some " + itemLower,
							"Thrander hammers it for a bit");
					player.message("Thrander gives you a " + metal + " plated skirt");
				}
				addItem(player, newID, 1);
			}
		}
	}

	public int getNewID(Item item) {
		int newID = ItemId.NOTHING.id();
		switch (ItemId.getById(item.getID())) {
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
			default:
				break;
		}
		return newID;
	}

}
