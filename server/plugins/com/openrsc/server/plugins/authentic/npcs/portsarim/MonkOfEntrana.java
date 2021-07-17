package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.external.ItemDefinition;
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
		// ice gloves for heroes quest
		ItemId.ICE_GLOVES.id(),
		// basic staves
		ItemId.STAFF.id(),
		ItemId.MAGIC_STAFF.id(),
		ItemId.STAFF_OF_AIR.id(),
		ItemId.STAFF_OF_EARTH.id(),
		ItemId.STAFF_OF_FIRE.id(),
		ItemId.STAFF_OF_EARTH.id()
	};

	private boolean BLOCK_ITEM(Player player, Item item) {
		if (DataConversions.inArray(allowedItems, item.getCatalogId())) return false;
		if (DataConversions.inArray(blockedItems, item.getCatalogId())) return true;
		ItemDefinition def = item.getDef(player.getWorld());
		if (def.isWieldable()) {
			// allow anything in neck and cape slot
			if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_NECK.getIndex()
				|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_CAPE.getIndex()) return false;
			// don't allow anything with a ranged level requirement
			if (def.getRequiredSkillIndex() == Skill.RANGED.id()) return true;
			// allow anything without melee combat stats and armor, otherwise block
			if (def.getWeaponPowerBonus() == 0 && def.getWeaponAimBonus() == 0 && def.getArmourBonus() == 0)  return false;
				else return true;
		}
		// default: allow
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
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			return;
		}
		if (n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id()) {
			npcsay(player, n, "Are you looking to take passage to our holy island?",
					"If so your weapons and armour must be left behind");
			if (multi(player, n, "No I don't wish to go",
				"Yes, Okay I'm ready to go") == 1) {

				mes("The monk quickly searches you");
				delay(3);
				if (CANT_GO(player)) {
					npcsay(player, n, "Sorry we cannow allow you on to our island",
						"Make sure you are not carrying weapons or armour please");
				} else {
					mes("You board the ship");
					delay(3);
					player.teleport(418, 570, false);
					delay(3);
					mes("The ship arrives at Entrana");
					delay(3);
				}
			}
		}
		else if (n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id()) {
			npcsay(player, n, "Are you looking to take passage back to port sarim?");
			if (multi(player, n, "No I don't wish to go",
				"Yes, Okay I'm ready to go") == 1) {

				mes("You board the ship");
				delay(3);
				player.teleport(264, 660, false);
				delay(3);
				mes("The ship arrives at Port Sarim");
				delay(3);
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
