package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.NpcCommandListener;
import com.openrsc.server.plugins.listeners.action.PickupListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.NpcCommandExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PickupExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public class IronMan implements TalkToNpcExecutiveListener,
	TalkToNpcListener, NpcCommandListener, NpcCommandExecutiveListener,
	PickupListener, PickupExecutiveListener {
	private static final Logger LOGGER = LogManager.getLogger(IronMan.class);
	private static int IRON_MAN = NpcId.IRONMAN.id();
	private static int ULTIMATE_IRON_MAN = NpcId.ULTIMATE_IRONMAN.id();
	private static int HARDCORE_IRON_MAN = NpcId.HARDCORE_IRONMAN.id();

	private int[] ironmanArmourPieces = new int[]{
		ItemId.IRONMAN_HELM.id(), ItemId.IRONMAN_PLATEBODY.id(), ItemId.IRONMAN_PLATELEGS.id(),
		ItemId.ULTIMATE_IRONMAN_HELM.id(), ItemId.ULTIMATE_IRONMAN_PLATEBODY.id(), ItemId.ULTIMATE_IRONMAN_PLATELEGS.id(),
		ItemId.HARDCORE_IRONMAN_HELM.id(), ItemId.HARDCORE_IRONMAN_PLATEBODY.id(), ItemId.HARDCORE_IRONMAN_PLATELEGS.id()
	};

	@Override
	public void onPickup(Player player, GroundItem item) {
		if (DataConversions.inArray(ironmanArmourPieces, item.getID())) {
			player.message("I'd better speak to an Ironman Npc for a replacement");
		}
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (!p.getWorld().getServer().getConfig().SPAWN_IRON_MAN_NPCS) return;

		if (n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN) {
			if (p.getAttribute("ironman_delete", false)) {
				if (p.getCache().hasKey("bank_pin")) {
					message(p, n, 1000, "Enter your Bank PIN to downgrade your Iron Man status.");

					if (!validateBankPin(p)) {
						ActionSender.sendBox(p, "Incorrect bank pin", false);
						p.setAttribute("ironman_delete", false);
						ActionSender.sendIronManInterface(p);
						return;
					}
					p.setAttribute("ironman_delete", false);
					p.message("You have correctly entered your PIN");
					int id = p.getAttribute("ironman_mode");
					if (id != -1) {
						p.setIronMan(id);
					}
					p.message("You have downgraded your ironman status");
					ActionSender.sendIronManMode(p);
					ActionSender.sendIronManInterface(p);
				}
				return;
			} else if (p.getAttribute("ironman_pin", false)) {
				message(p, n, 1000, "You'll need to set a Bank PIN for that.");
				int menu = showMenu(p,
					"Okay, let me set a PIN.",
					"No, I don't want a Bank PIN.");
				if (menu != -1) {
					if (menu == 0) {
						if (!p.getCache().hasKey("bank_pin")) {
							if(setBankPin(p)) {
								p.setIronManRestriction(0);
								ActionSender.sendIronManMode(p);
								ActionSender.sendIronManInterface(p);
							}

							p.setAttribute("ironman_pin", false);
						}
					} else if (menu == 1) {
						ActionSender.sendIronManInterface(p);
						p.setAttribute("ironman_pin", false);
					}
				} else {
					p.setAttribute("ironman_pin", false);
				}
				return;
			}
			if (p.isIronMan(IronmanMode.Ironman.id())) {
				npcTalk(p, n, "Hail, Iron Man!");
			} else if (p.isIronMan(IronmanMode.Ultimate.id())) {
				npcTalk(p, n, "Hail, Ultimate Iron Man!");
			} else if (p.isIronMan(IronmanMode.Hardcore.id())) {
				npcTalk(p, n, "Hail, Hardcore Iron Man!");
			} else {
				npcTalk(p, n, "Hello, " + p.getUsername() + ". We're the Iron Man tutors.");
			}
			npcTalk(p, n, "What can we do for you?");
			int menu = showMenu(p, n,
				"Tell me about Iron Men.",
				"I'd like to " + (p.getLocation().onTutorialIsland() ? "change" : "review") + " my Iron Man mode.",
				"Have you any armour for me, please?",
				"I'm fine, thanks.");
			if (menu == 0) {
				npcTalk(p, n, "When you play as an Iron Man, you do everything",
					"for yourself. You don't trade with other players, or take",
					"their items, or accept their help.",
					"As an Iron Man, you choose to have these restrictions",
					"imposed on you, so everyone knows you're doing it",
					"properly.",
					"If you think you have what it takes, you can choose to",
					"become a Hardcore Iron Man",
					"In addition to the standard restrictions,",
					"Hardcore Iron Men only have one life.",
					"In the event of a dangerious death, your Hardcore Iron Men status",
					"will be downgraded to that of a standard Iron Man, and your",
					"stats will be frozen on the Hardcore Iron Man hiscores.",
					"For the ultimate challenge, you can choose to become",
					"an Ultimate Iron Man.",
					"In addition to the standard restrictions, Ultimate Iron",
					"Men are blocked from using the bank, and they drop all",
					"their items when they die.",
					"While you're on Tutorial Island, you can switch freely",
					"between being a standard Iron Man, an Ultimate Iron Man,",
					"a Hardcore Iron Man or a normal player.",
					"Once you've left this island, you'll be able to find us in",
					"Lumbridge, but we'll only let you switch your",
					"restrictions downwards, not upwards.",
					"So we will let Hardcore Iron Men or Ultimate Iron Men",
					"downgrade to a standard Iron Men,",
					"and we'll let either Iron Man types of Iron Man become normal players.");
			} else if (menu == 1) {
				ActionSender.sendIronManInterface(p);
			} else if (menu == 2) {
				armourOption(p, n);
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN;
	}

	@Override
	public boolean blockNpcCommand(Npc n, String command, Player p) {
		return n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN && command.equalsIgnoreCase("Armour");
	}

	@Override
	public void onNpcCommand(Npc n, String command, Player p) {
		if (!p.getWorld().getServer().getConfig().SPAWN_IRON_MAN_NPCS) return;
		if (n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN && command.equalsIgnoreCase("Armour")) {
			armourOption(p, n);
		}
	}

	private void armourOption(Player p, Npc n) {
		if ((!p.isIronMan(IronmanMode.Ironman.id())) && (!p.isIronMan(IronmanMode.Ultimate.id()) && (!p.isIronMan(IronmanMode.Hardcore.id())))) {
			npcTalk(p, n, "You're not an Iron Man.", "Our armour is only for them.");
		} else {
			if (p.getLocation().onTutorialIsland()) {
				npcTalk(p, n, "We'll give you your armour once you're off this island.",
					"Come and see us in Lumbridge.");
			} else {
				if (!hasItemAtAll(p, expectedArmour(p, ArmourPart.HELM)) ||
					!hasItemAtAll(p, expectedArmour(p, ArmourPart.BODY)) ||
					!hasItemAtAll(p, expectedArmour(p, ArmourPart.LEGS))) {
					if (p.getIronMan() == IronmanMode.Ironman.id()) {
						if (!hasItemAtAll(p, ItemId.IRONMAN_HELM.id()))
							addItem(p, ItemId.IRONMAN_HELM.id(), 1);
						if (!hasItemAtAll(p, ItemId.IRONMAN_PLATEBODY.id()))
							addItem(p, ItemId.IRONMAN_PLATEBODY.id(), 1);
						if (!hasItemAtAll(p, ItemId.IRONMAN_PLATELEGS.id()))
							addItem(p, ItemId.IRONMAN_PLATELEGS.id(), 1);
					} else if (p.getIronMan() == IronmanMode.Ultimate.id()) {
						if (!hasItemAtAll(p, ItemId.ULTIMATE_IRONMAN_HELM.id()))
							addItem(p, ItemId.ULTIMATE_IRONMAN_HELM.id(), 1);
						if (!hasItemAtAll(p, ItemId.ULTIMATE_IRONMAN_PLATEBODY.id()))
							addItem(p, ItemId.ULTIMATE_IRONMAN_PLATEBODY.id(), 1);
						if (!hasItemAtAll(p, ItemId.ULTIMATE_IRONMAN_PLATELEGS.id()))
							addItem(p, ItemId.ULTIMATE_IRONMAN_PLATELEGS.id(), 1);
					} else if (p.getIronMan() == IronmanMode.Hardcore.id()) {
						if (!hasItemAtAll(p, ItemId.HARDCORE_IRONMAN_HELM.id()))
							addItem(p, ItemId.HARDCORE_IRONMAN_HELM.id(), 1);
						if (!hasItemAtAll(p, ItemId.HARDCORE_IRONMAN_PLATEBODY.id()))
							addItem(p, ItemId.HARDCORE_IRONMAN_PLATEBODY.id(), 1);
						if (!hasItemAtAll(p, ItemId.HARDCORE_IRONMAN_PLATELEGS.id()))
							addItem(p, ItemId.HARDCORE_IRONMAN_PLATELEGS.id(), 1);
					}
					npcTalk(p, n, "There you go. Wear it with pride.");
				} else {
					npcTalk(p, n, "I think you've already got the whole set.");
				}
			}
		}
	}

	int expectedArmour(Player p, ArmourPart part) {
		if (p.getIronMan() == IronmanMode.Ironman.id()) {
			switch(part) {
				case HELM:
					return ItemId.IRONMAN_HELM.id();
				case BODY:
					return ItemId.IRONMAN_PLATEBODY.id();
				case LEGS:
					return ItemId.IRONMAN_PLATELEGS.id();
			}
		} else if (p.getIronMan() == IronmanMode.Ultimate.id()) {
			switch(part) {
				case HELM:
					return ItemId.ULTIMATE_IRONMAN_HELM.id();
				case BODY:
					return ItemId.ULTIMATE_IRONMAN_PLATEBODY.id();
				case LEGS:
					return ItemId.ULTIMATE_IRONMAN_PLATELEGS.id();
			}
		} else if (p.getIronMan() == IronmanMode.Hardcore.id()) {
			switch(part) {
				case HELM:
					return ItemId.HARDCORE_IRONMAN_HELM.id();
				case BODY:
					return ItemId.HARDCORE_IRONMAN_PLATEBODY.id();
				case LEGS:
					return ItemId.HARDCORE_IRONMAN_PLATELEGS.id();
			}
		}
		return ItemId.NOTHING.id();
	}

	enum ArmourPart {
		HELM, BODY, LEGS,
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		return DataConversions.inArray(ironmanArmourPieces, i.getID());
	}
}
