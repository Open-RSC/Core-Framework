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
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.Arrays;

import static com.openrsc.server.plugins.Functions.*;

public final class MonkOfEntrana implements OpLocTrigger,
	TalkNpcTrigger {

	private String[] blockedItems = new String[]{
		"arrow", "axe", "staff", "bow", "mail", "plate",
		"bolts", "cannon", "helmet", "mace", "simitar",
		"shield", "spear", "2-handed", "long", "short",
		"amulet", "ring", "cape", "gauntlet", "boot",
		"necklace", "silverlight", "excalibur"
	};

	private boolean CHECK_ITEM(String itemName) {
		return Arrays.stream(blockedItems).parallel().anyMatch(itemName::contains);
	}

	private boolean CAN_GO(Player p) {
		synchronized(p.getCarriedItems().getInventory().getItems()) {
			for (Item item : p.getCarriedItems().getInventory().getItems()) {
				String name = item.getDef(p.getWorld()).getName().toLowerCase();
				if (CHECK_ITEM(name))
					return true;
			}
		}

		if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
			Item item;
			for (int i = 0; i < Equipment.SLOT_COUNT; i++) {
				item = p.getCarriedItems().getEquipment().get(i);
				if (item == null)
					continue;
				String name = item.getDef(p.getWorld()).getName().toLowerCase();
				if (CHECK_ITEM(name))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean blockTalkNpc(final Player p, final Npc n) {
		return n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id() || n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id();
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		if (n.getID() == NpcId.MONK_OF_ENTRANA_PORTSARIM.id()) {
			npcTalk(p, n, "Are you looking to take passage to our holy island?",
					"If so your weapons and armour must be left behind");
				final Menu defaultMenu = new Menu();
				defaultMenu.addOption(new Option("No I don't wish to go") {
					@Override
					public void action() {
					}
				});
				defaultMenu.addOption(new Option("Yes, Okay I'm ready to go") {
					@Override
					public void action() {
						message(p, "The monk quickly searches you");
						if (CAN_GO(p)) {
							npcTalk(p, n, "Sorry we cannow allow you on to our island",
								"Make sure you are not carrying weapons or armour please");
						} else {
							message(p, "You board the ship");
							p.teleport(418, 570, false);
							sleep(2200);
							p.message("The ship arrives at Entrana");
						}
					}
				});
				defaultMenu.showMenu(p);
		}
		else if (n.getID() == NpcId.MONK_OF_ENTRANA_UNRELEASED.id()) {
			npcTalk(p, n, "Are you looking to take passage back to port sarim?");
			final Menu defaultMenu = new Menu();
			defaultMenu.addOption(new Option("No I don't wish to go") {
				@Override
				public void action() {
				}
			});
			defaultMenu.addOption(new Option("Yes, Okay I'm ready to go") {
				@Override
				public void action() {
					message(p, "You board the ship");
					p.teleport(264, 660, false);
					sleep(2200);
					p.message("The ship arrives at Port Sarim");
				}
			});
			defaultMenu.showMenu(p);
			return;
		}
	}

	@Override
	public void onOpLoc(GameObject arg0, String arg1, Player p) {
		Npc monk = getNearestNpc(p, NpcId.MONK_OF_ENTRANA_PORTSARIM.id(), 10);
		if (monk != null) {
			monk.initializeTalkScript(p);
		} else {
			p.message("I need to speak to the monk before boarding the ship.");
		}

	}

	@Override
	public boolean blockOpLoc(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 240 && arg0.getLocation().equals(Point.location(257, 661)))
			|| (arg0.getID() == 239 && arg0.getLocation().equals(Point.location(262, 661)))
			|| (arg0.getID() == 239 && arg0.getLocation().equals(Point.location(264, 661)))
			|| (arg0.getID() == 238 && arg0.getLocation().equals(Point.location(266, 661)));
	}
}
