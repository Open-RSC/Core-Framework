package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public final class MonkOfEntrana implements OpLocTrigger,
	TalkNpcTrigger {

	final private int[] blockedItems = {
		// Arrows
		ItemId.BRONZE_ARROWS.id(),
		ItemId.IRON_ARROWS.id(),
		ItemId.STEEL_ARROWS.id(),
		ItemId.MITHRIL_ARROWS.id(),
		ItemId.ADAMANTITE_ARROWS.id(),
		ItemId.RUNE_ARROWS.id(),
		ItemId.ICE_ARROWS.id(),
		// Poison Arrows
		ItemId.POISON_BRONZE_ARROWS.id(),
		ItemId.POISON_IRON_ARROWS.id(),
		ItemId.POISON_STEEL_ARROWS.id(),
		ItemId.POISON_MITHRIL_ARROWS.id(),
		ItemId.POISON_ADAMANTITE_ARROWS.id(),
		ItemId.POISON_RUNE_ARROWS.id(),
		// Arrow Heads
		ItemId.BRONZE_ARROW_HEADS.id(),
		ItemId.IRON_ARROW_HEADS.id(),
		ItemId.STEEL_ARROW_HEADS.id(),
		ItemId.MITHRIL_ARROW_HEADS.id(),
		ItemId.ADAMANTITE_ARROW_HEADS.id(),
		ItemId.RUNE_ARROW_HEADS.id(),
		// Crossbow Bolts
		ItemId.CROSSBOW_BOLTS.id(),
		ItemId.POISON_CROSSBOW_BOLTS.id(),
		ItemId.OYSTER_PEARL_BOLTS.id(),
		ItemId.OYSTER_PEARL_BOLT_TIPS.id(),
		// Dwarf Cannon
		ItemId.DWARF_CANNON_BASE.id(),
		ItemId.DWARF_CANNON_STAND.id(),
		ItemId.DWARF_CANNON_BARRELS.id(),
		ItemId.DWARF_CANNON_FURNACE.id()
	};

	final private int[] allowedItems = {
		ItemId.ICE_GLOVES.id()
	};

	private boolean BLOCK_ITEM(Player player, Item item) {
		if (config().WANT_EQUIPMENT_TAB
			&& item.getCatalogId() == ItemId.BRONZE_PICKAXE.id()) return false;
		if (item.isWieldable(player.getWorld()) && !DataConversions.inArray(allowedItems, item.getCatalogId())) return true;
		if (DataConversions.inArray(blockedItems, item.getCatalogId())) return true;
		return false;
	}

	private boolean CANT_GO(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				if (BLOCK_ITEM(player, item)) return true;
			}
		}

		if (config().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = player.getCarriedItems().getEquipment().get(i);
				if (item == null) continue;
				if (BLOCK_ITEM(player, item)) return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc n) {
		return n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id() || n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		if (n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id()) {
			npcsay(player, n, "Are you looking to take passage to our holy island?",
					"If so your weapons and armour must be left behind");
			if (multi(player, n, "No I don't wish to go",
				"Yes, Okay I'm ready to go") == 1) {

				mes("The monk quickly searches you");
				if (CANT_GO(player)) {
					npcsay(player, n, "Sorry we cannow allow you on to our island",
						"Make sure you are not carrying weapons or armour please");
				} else {
					mes("You board the ship");
					player.teleport(418, 570, false);
					delay(3);
					mes("The ship arrives at Entrana");
				}
			}
		}
		else if (n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id()) {
			npcsay(player, n, "Are you looking to take passage back to port sarim?");
			if (multi(player, n, "No I don't wish to go",
				"Yes, Okay I'm ready to go") == 1) {

				mes("You board the ship");
				player.teleport(264, 660, false);
				delay(3);
				mes("The ship arrives at Port Sarim");
			}
			return;
		}
	}

	@Override
	public void onOpLoc(Player player, GameObject arg0, String arg1) {
		Npc monk = ifnearvisnpc(player, NpcId.MONK_OF_ENTRANA_PORTSARIM.id(), 10);
		if (monk != null) {
			monk.initializeTalkScript(player);
		} else {
			player.message("I need to speak to the monk before boarding the ship.");
		}

	}

	@Override
	public boolean blockOpLoc(Player arg2, GameObject arg0, String arg1) {
		return (arg0.getID() == 240 && arg0.getLocation().equals(Point.location(257, 661)))
			|| (arg0.getID() == 239 && arg0.getLocation().equals(Point.location(262, 661)))
			|| (arg0.getID() == 239 && arg0.getLocation().equals(Point.location(264, 661)))
			|| (arg0.getID() == 238 && arg0.getLocation().equals(Point.location(266, 661)));
	}
}
