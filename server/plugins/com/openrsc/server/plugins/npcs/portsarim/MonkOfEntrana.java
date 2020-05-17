package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Equipment;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Arrays;

import static com.openrsc.server.plugins.Functions.*;

public final class MonkOfEntrana implements OpLocTrigger,
	TalkNpcTrigger {

	private String[] blockedItems = new String[]{
		"arrow", "axe", "staff", "bow", "mail", "plate",
		"bolts", "cannon", "helmet", "mace", "scimitar",
		"shield", "spear", "2-handed", "long", "short",
		"amulet", "ring", "cape", "gauntlet", "boot",
		"necklace", "silverlight", "excalibur", "dagger",
		"throwing", "sword"
	};

	private String[] exceptions = new String[]{ "fish", "pickaxe" };

	private boolean BLOCK_ITEM(String itemName, boolean equipmentTab) {
		// Checks to see if the item is in the blocked list
		boolean blocked = Arrays.stream(blockedItems).parallel().anyMatch(itemName::contains);
		// If it is, checks to see if it is on the exception list
		// If it isn't, we block the item.
		if (blocked && !Arrays.stream(exceptions).parallel().anyMatch(itemName::contains)) {
			return true;
		}
		// Block item if it is a pickaxe that isn't bronze.
		if (equipmentTab && itemName.contains("pickaxe") && !itemName.contains("bronze")) {
			return true;
		}
		return false;
	}

	private boolean CANT_GO(Player player) {
		boolean equipmentTab = player.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB;
		synchronized(player.getCarriedItems().getInventory().getItems()) {
			for (Item item : player.getCarriedItems().getInventory().getItems()) {
				String name = item.getDef(player.getWorld()).getName().toLowerCase();
				if (BLOCK_ITEM(name, equipmentTab))
					return true;
			}
		}

		if (equipmentTab) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = player.getCarriedItems().getEquipment().get(i);
				if (item == null)
					continue;
				String name = item.getDef(player.getWorld()).getName().toLowerCase();
				if (BLOCK_ITEM(name, true))
					return true;
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
					delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
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
				delay(player.getWorld().getServer().getConfig().GAME_TICK * 3);
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
