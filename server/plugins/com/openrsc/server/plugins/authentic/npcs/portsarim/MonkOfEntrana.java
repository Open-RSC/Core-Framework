package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.SceneryId;
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

	// This list is only entrana-blocked items with catalog id < 1290 (authentic items)
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

		// Woodcutting Axes
		ItemId.BRONZE_AXE.id(),
		ItemId.IRON_AXE.id(),
		ItemId.STEEL_AXE.id(),
		ItemId.BLACK_AXE.id(),
		ItemId.MITHRIL_AXE.id(),
		ItemId.ADAMANTITE_AXE.id(),
		ItemId.RUNE_AXE.id(),

		// Battle Axes
		ItemId.BRONZE_BATTLE_AXE.id(),
		ItemId.IRON_BATTLE_AXE.id(),
		ItemId.STEEL_BATTLE_AXE.id(),
		ItemId.BLACK_BATTLE_AXE.id(),
		ItemId.MITHRIL_BATTLE_AXE.id(),
		ItemId.ADAMANTITE_BATTLE_AXE.id(),
		ItemId.RUNE_BATTLE_AXE.id(),
		ItemId.DRAGON_AXE.id(),

		// Battle Staves
		ItemId.BATTLESTAFF.id(),
		ItemId.BATTLESTAFF_OF_AIR.id(),
		ItemId.BATTLESTAFF_OF_WATER.id(),
		ItemId.BATTLESTAFF_OF_EARTH.id(),
		ItemId.BATTLESTAFF_OF_FIRE.id(),
		ItemId.ENCHANTED_BATTLESTAFF_OF_AIR.id(),
		ItemId.ENCHANTED_BATTLESTAFF_OF_WATER.id(),
		ItemId.ENCHANTED_BATTLESTAFF_OF_EARTH.id(),
		ItemId.ENCHANTED_BATTLESTAFF_OF_FIRE.id(),

		// Bows (can be made but not brought)
		ItemId.LONGBOW.id(),
		ItemId.OAK_LONGBOW.id(),
		ItemId.WILLOW_LONGBOW.id(),
		ItemId.MAPLE_LONGBOW.id(),
		ItemId.YEW_LONGBOW.id(),
		ItemId.MAGIC_LONGBOW.id(),
		ItemId.SHORTBOW.id(),
		ItemId.OAK_SHORTBOW.id(),
		ItemId.WILLOW_SHORTBOW.id(),
		ItemId.MAPLE_SHORTBOW.id(),
		ItemId.YEW_SHORTBOW.id(),
		ItemId.MAGIC_SHORTBOW.id(),

		// Chain Mail Body
		ItemId.LEATHER_ARMOUR.id(), // unconfirmed
		ItemId.BRONZE_CHAIN_MAIL_BODY.id(),
		ItemId.IRON_CHAIN_MAIL_BODY.id(),
		ItemId.STEEL_CHAIN_MAIL_BODY.id(),
		ItemId.BLACK_CHAIN_MAIL_BODY.id(),
		ItemId.MITHRIL_CHAIN_MAIL_BODY.id(),
		ItemId.ADAMANTITE_CHAIN_MAIL_BODY.id(),
		ItemId.RUNE_CHAIN_MAIL_BODY.id(),

		// Cross Bows
		ItemId.PHOENIX_CROSSBOW.id(),
		ItemId.CROSSBOW.id(),

		// Crossbow Bolts
		ItemId.CROSSBOW_BOLTS.id(),
		ItemId.POISON_CROSSBOW_BOLTS.id(),
		ItemId.OYSTER_PEARL_BOLTS.id(),
		ItemId.OYSTER_PEARL_BOLT_TIPS.id(),

		// Daggers
		ItemId.BRONZE_DAGGER.id(),
		ItemId.IRON_DAGGER.id(),
		ItemId.STEEL_DAGGER.id(),
		ItemId.BLACK_DAGGER.id(),
		ItemId.MITHRIL_DAGGER.id(),
		ItemId.ADAMANTITE_DAGGER.id(),
		ItemId.RUNE_DAGGER.id(),

		// Poisoned Daggers
		ItemId.POISONED_BRONZE_DAGGER.id(),
		ItemId.POISONED_IRON_DAGGER.id(),
		ItemId.POISONED_STEEL_DAGGER.id(),
		ItemId.POISONED_BLACK_DAGGER.id(),
		ItemId.POISONED_MITHRIL_DAGGER.id(),
		ItemId.POISONED_ADAMANTITE_DAGGER.id(),
		ItemId.POISONED_RUNE_DAGGER.id(),

		// Dwarf Cannon
		ItemId.DWARF_CANNON_BASE.id(),
		ItemId.DWARF_CANNON_STAND.id(),
		ItemId.DWARF_CANNON_BARRELS.id(),
		ItemId.DWARF_CANNON_FURNACE.id(),

		// Helmets
		ItemId.MEDIUM_BRONZE_HELMET.id(),
		ItemId.MEDIUM_IRON_HELMET.id(),
		ItemId.MEDIUM_STEEL_HELMET.id(),
		ItemId.MEDIUM_BLACK_HELMET.id(),
		ItemId.MEDIUM_MITHRIL_HELMET.id(),
		ItemId.MEDIUM_ADAMANTITE_HELMET.id(),
		ItemId.MEDIUM_RUNE_HELMET.id(),
		ItemId.DRAGON_MEDIUM_HELMET.id(), // probably
		ItemId.LARGE_BRONZE_HELMET.id(),
		ItemId.LARGE_IRON_HELMET.id(),
		ItemId.LARGE_STEEL_HELMET.id(),
		ItemId.LARGE_BLACK_HELMET.id(),
		ItemId.LARGE_MITHRIL_HELMET.id(),
		ItemId.LARGE_ADAMANTITE_HELMET.id(),
		ItemId.LARGE_RUNE_HELMET.id(),

		// Maces
		ItemId.BRONZE_MACE.id(),
		ItemId.IRON_MACE.id(),
		ItemId.STEEL_MACE.id(),
		ItemId.BLACK_MACE.id(),
		ItemId.MITHRIL_MACE.id(),
		ItemId.ADAMANTITE_MACE.id(),
		ItemId.RUNE_MACE.id(),

		// Plate mail bodies
		ItemId.BRONZE_PLATE_MAIL_BODY.id(),
		ItemId.IRON_PLATE_MAIL_BODY.id(),
		ItemId.STEEL_PLATE_MAIL_BODY.id(),
		ItemId.BLACK_PLATE_MAIL_BODY.id(),
		ItemId.MITHRIL_PLATE_MAIL_BODY.id(),
		ItemId.ADAMANTITE_PLATE_MAIL_BODY.id(),
		ItemId.RUNE_PLATE_MAIL_BODY.id(),

		// Plate mail legs
		ItemId.BRONZE_PLATE_MAIL_LEGS.id(),
		ItemId.IRON_PLATE_MAIL_LEGS.id(),
		ItemId.STEEL_PLATE_MAIL_LEGS.id(),
		ItemId.BLACK_PLATE_MAIL_LEGS.id(),
		ItemId.MITHRIL_PLATE_MAIL_LEGS.id(),
		ItemId.ADAMANTITE_PLATE_MAIL_LEGS.id(),
		ItemId.RUNE_PLATE_MAIL_LEGS.id(),

		// plate mail tops
		ItemId.BRONZE_PLATE_MAIL_TOP.id(),
		ItemId.IRON_PLATE_MAIL_TOP.id(),
		ItemId.STEEL_PLATE_MAIL_TOP.id(),
		ItemId.BLACK_PLATE_MAIL_TOP.id(),
		ItemId.MITHRIL_PLATE_MAIL_TOP.id(),
		ItemId.ADAMANTITE_PLATE_MAIL_TOP.id(),
		ItemId.RUNE_PLATE_MAIL_TOP.id(),

		// plated skirts
		ItemId.BRONZE_PLATED_SKIRT.id(),
		ItemId.IRON_PLATED_SKIRT.id(),
		ItemId.STEEL_PLATED_SKIRT.id(),
		ItemId.BLACK_PLATED_SKIRT.id(),
		ItemId.MITHRIL_PLATED_SKIRT.id(),
		ItemId.ADAMANTITE_PLATED_SKIRT.id(),
		ItemId.RUNE_SKIRT.id(),

		// scimitars
		ItemId.BRONZE_SCIMITAR.id(),
		ItemId.IRON_SCIMITAR.id(),
		ItemId.STEEL_SCIMITAR.id(),
		ItemId.BLACK_SCIMITAR.id(),
		ItemId.MITHRIL_SCIMITAR.id(),
		ItemId.ADAMANTITE_SCIMITAR.id(),
		ItemId.RUNE_SCIMITAR.id(),

		// shields
		ItemId.BRONZE_SQUARE_SHIELD.id(),
		ItemId.IRON_SQUARE_SHIELD.id(),
		ItemId.STEEL_SQUARE_SHIELD.id(),
		ItemId.BLACK_SQUARE_SHIELD.id(),
		ItemId.MITHRIL_SQUARE_SHIELD.id(),
		ItemId.ADAMANTITE_SQUARE_SHIELD.id(),
		ItemId.RUNE_SQUARE_SHIELD.id(),
		ItemId.DRAGON_SQUARE_SHIELD.id(), // probably
		ItemId.BRONZE_KITE_SHIELD.id(),
		ItemId.IRON_KITE_SHIELD.id(),
		ItemId.STEEL_KITE_SHIELD.id(),
		ItemId.BLACK_KITE_SHIELD.id(),
		ItemId.MITHRIL_KITE_SHIELD.id(),
		ItemId.ADAMANTITE_KITE_SHIELD.id(),
		ItemId.RUNE_KITE_SHIELD.id(),
		ItemId.WOODEN_SHIELD.id(),
		ItemId.ANTI_DRAGON_BREATH_SHIELD.id(),

		// Spears & poisoned
		ItemId.BRONZE_SPEAR.id(),
		ItemId.IRON_SPEAR.id(),
		ItemId.STEEL_SPEAR.id(),
		ItemId.MITHRIL_SPEAR.id(),
		ItemId.ADAMANTITE_SPEAR.id(),
		ItemId.RUNE_SPEAR.id(),
		ItemId.POISONED_BRONZE_SPEAR.id(),
		ItemId.POISONED_IRON_SPEAR.id(),
		ItemId.POISONED_STEEL_SPEAR.id(),
		ItemId.POISONED_MITHRIL_SPEAR.id(),
		ItemId.POISONED_ADAMANTITE_SPEAR.id(),
		ItemId.POISONED_RUNE_SPEAR.id(),

		// 2h, short, long swords
		ItemId.BRONZE_2_HANDED_SWORD.id(),
		ItemId.IRON_2_HANDED_SWORD.id(),
		ItemId.STEEL_2_HANDED_SWORD.id(),
		ItemId.BLACK_2_HANDED_SWORD.id(),
		ItemId.MITHRIL_2_HANDED_SWORD.id(),
		ItemId.ADAMANTITE_2_HANDED_SWORD.id(),
		ItemId.RUNE_2_HANDED_SWORD.id(),
		ItemId.BRONZE_SHORT_SWORD.id(),
		ItemId.IRON_SHORT_SWORD.id(),
		ItemId.STEEL_SHORT_SWORD.id(),
		ItemId.BLACK_SHORT_SWORD.id(),
		ItemId.MITHRIL_SHORT_SWORD.id(),
		ItemId.ADAMANTITE_SHORT_SWORD.id(),
		ItemId.RUNE_SHORT_SWORD.id(),
		ItemId.BRONZE_LONG_SWORD.id(),
		ItemId.IRON_LONG_SWORD.id(),
		ItemId.STEEL_LONG_SWORD.id(),
		ItemId.BLACK_LONG_SWORD.id(),
		ItemId.MITHRIL_LONG_SWORD.id(),
		ItemId.ADAMANTITE_LONG_SWORD.id(),
		ItemId.RUNE_LONG_SWORD.id(),
		ItemId.DRAGON_SWORD.id(),

		// Throwing Dart & tips & poisoned (untested)
		ItemId.BRONZE_THROWING_DART.id(),
		ItemId.IRON_THROWING_DART.id(),
		ItemId.STEEL_THROWING_DART.id(),
		ItemId.MITHRIL_THROWING_DART.id(),
		ItemId.ADAMANTITE_THROWING_DART.id(),
		ItemId.RUNE_THROWING_DART.id(),
		ItemId.BRONZE_DART_TIPS.id(),
		ItemId.IRON_DART_TIPS.id(),
		ItemId.STEEL_DART_TIPS.id(),
		ItemId.MITHRIL_DART_TIPS.id(),
		ItemId.ADAMANTITE_DART_TIPS.id(),
		ItemId.RUNE_DART_TIPS.id(),
		ItemId.POISONED_BRONZE_THROWING_DART.id(),
		ItemId.POISONED_IRON_THROWING_DART.id(),
		ItemId.POISONED_STEEL_THROWING_DART.id(),
		ItemId.POISONED_MITHRIL_THROWING_DART.id(),
		ItemId.POISONED_ADAMANTITE_THROWING_DART.id(),
		ItemId.POISONED_RUNE_THROWING_DART.id(),

		// Throwing knife & poisoned (untested)
		ItemId.BRONZE_THROWING_KNIFE.id(),
		ItemId.IRON_THROWING_KNIFE.id(),
		ItemId.STEEL_THROWING_KNIFE.id(),
		ItemId.BLACK_THROWING_KNIFE.id(),
		ItemId.MITHRIL_THROWING_KNIFE.id(),
		ItemId.ADAMANTITE_THROWING_KNIFE.id(),
		ItemId.RUNE_THROWING_KNIFE.id(),
		ItemId.POISONED_BRONZE_THROWING_KNIFE.id(),
		ItemId.POISONED_IRON_THROWING_KNIFE.id(),
		ItemId.POISONED_STEEL_THROWING_KNIFE.id(),
		ItemId.POISONED_BLACK_THROWING_KNIFE.id(),
		ItemId.POISONED_MITHRIL_THROWING_KNIFE.id(),
		ItemId.POISONED_ADAMANTITE_THROWING_KNIFE.id(),
		ItemId.POISONED_RUNE_THROWING_KNIFE.id(),

		// Quest Weapons (mostly untested)
		ItemId.SILVERLIGHT.id(),
		ItemId.STAKE.id(),
		ItemId.EXCALIBUR.id(), // confirmed blocked
		ItemId.FALADIAN_KNIGHTS_SWORD.id(),
		ItemId.MACE_OF_ZAMORAK.id(), // unreleased
		ItemId.KHAZARD_HELMET.id(),
		ItemId.KHAZARD_CHAINMAIL.id(),
		ItemId.BLOODY_AXE_OF_ZAMORAK.id(), // unreleased
		ItemId.CARNILLEAN_ARMOUR.id(), // can't wear it anyway but maybe the monk would say something
		ItemId.CATTLE_PROD.id(),
		ItemId.STAFF_OF_IBAN.id(), // unconfirmed
		ItemId.STAFF_OF_ARMADYL.id(),
		ItemId.PROTOTYPE_THROWING_DART.id(),
		ItemId.PROTOTYPE_DART_TIP.id(),
		/* Assuming they forgot to block quest items after some point...
		ItemId.MACHETTE.id(),
		ItemId.A_SILVER_DAGGER.id(),
		ItemId.STAFF_OF_ZAMORAK.id(),
		ItemId.STAFF_OF_GUTHIX.id(),
		ItemId.STAFF_OF_SARADOMIN.id(),
		ItemId.DAGGER.id(),
		ItemId.DARK_DAGGER.id(),
		ItemId.GLOWING_DARK_DAGGER.id(),
		 */

		// Scythe was allowed.
	};

	final private int[] blockedItemsOnlyOnCabbage = {
		ItemId.PRESENT.id(), // weapons can come out on cabbage config
		ItemId.BRONZE_PICKAXE.id(),
		ItemId.IRON_PICKAXE.id(),
		ItemId.STEEL_PICKAXE.id(),
		ItemId.MITHRIL_PICKAXE.id(),
		ItemId.ADAMANTITE_PICKAXE.id(),
		ItemId.RUNE_PICKAXE.id()
	};

	final private int[] blockedItemsCustom = {
		ItemId.HALLOWEEN_CRACKER.id(), // weapons can come out
		ItemId.DRAGON_ARROWS.id(),
		ItemId.POISON_DRAGON_ARROWS.id(),
		ItemId.DRAGON_BOLTS.id(),
		ItemId.POISON_DRAGON_BOLTS.id()
	};

	private boolean itemIsBlocked(Player player, Item item) {
		if (player.getConfig().WANT_CUSTOM_SPRITES && DataConversions.inArray(blockedItemsOnlyOnCabbage, item.getCatalogId())) {
			// Disallow certain items with different behaviours than authentic only on Cabbage config
			return true;
		}
		if (item.getCatalogId() <= ItemId.SCYTHE.id()) {
			// non-custom items, the block list was probably done manually, since Scythe was tested to be an allowed item, and it gives stats.
			// another surprising tested-allowed-item is the Staff of fire.
			// See https://gitlab.com/open-runescape-classic/core/-/issues/2831 for total list of known tested items
            return DataConversions.inArray(blockedItems, item.getCatalogId());
        } else {
			// for custom items, we will follow some general rules
			ItemDefinition def = item.getDef(player.getWorld());
			if (def.isWieldable()) {
				// allow anything in neck and cape slot
				if (def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_NECK.getIndex()
					|| def.getWieldPosition() == Equipment.EquipmentSlot.SLOT_CAPE.getIndex()) return false;
				// don't allow anything with a ranged level requirement
				if (def.getRequiredSkillIndex() == Skill.RANGED.id()) return true;
				// allow anything without melee combat stats and armor, otherwise block
                return def.getWeaponPowerBonus() != 0 || def.getWeaponAimBonus() != 0 || def.getArmourBonus() != 0;
			}
			return DataConversions.inArray(blockedItemsCustom, item.getCatalogId());
		}
	}

	private boolean playerNotAllowedOnEntrana(Player player) {
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				if (itemIsBlocked(player, item)) return true;
			}
		}

		if (config().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = player.getCarriedItems().getEquipment().get(i);
				if (item == null) continue;
				if (itemIsBlocked(player, item)) return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id() || npc.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			return;
		}
		if (npc.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id()) {
			npcsay(player, npc, "Are you looking to take passage to our holy island?",
					"If so your weapons and armour must be left behind");
			if (multi(player, npc, "No I don't wish to go",
				"Yes, Okay I'm ready to go") == 1) {
				mes("The monk quickly searches you");
				delay(5);
				if (playerNotAllowedOnEntrana(player)) {
					npcsay(player, npc, "Sorry we cannow allow you on to our island",
						"Make sure you are not carrying weapons or armour please");
				} else {
					mes("You board the ship");
					delay(5);
					player.teleport(418, 570, false);
					delay(3);
					mes("The ship arrives at Entrana");
				}
			}
		}
		else if (npc.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id()) {
			// This code does not run, it is hypothetical & not based on reality.
			// There is not a spawn of this monk anywhere.
			npcsay(player, npc, "Are you looking to take passage back to port sarim?");
			if (multi(player, npc, "No I don't wish to go",
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
	public void onOpLoc(Player player, GameObject gameObject, String command) {
		Npc monk = ifnearvisnpc(player, NpcId.MONK_OF_ENTRANA_PORTSARIM.id(), 10);
		if (monk != null) {
			monk.initializeTalkScript(player);
		} else {
			player.message("I need to speak to the monk before boarding the ship.");
		}

	}

	@Override
	public boolean blockOpLoc(Player player, GameObject gameObject, String command) {
		return gameObject.getID() == SceneryId.SHIP_BACK_ENTRANA.id() ||
			gameObject.getID() == SceneryId.SHIP_MIDDLE_ENTRANA.id() ||
			gameObject.getID() == SceneryId.SHIP_FRONT_ENTRANA.id();
	}
}
