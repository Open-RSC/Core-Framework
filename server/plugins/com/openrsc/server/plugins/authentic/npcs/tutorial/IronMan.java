package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpNpcTrigger;
import com.openrsc.server.plugins.triggers.TakeObjTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.openrsc.server.plugins.Functions.*;

public class IronMan implements
	TalkNpcTrigger, OpNpcTrigger,
	TakeObjTrigger {
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
	public void onTakeObj(Player player, GroundItem item) {
		if (DataConversions.inArray(ironmanArmourPieces, item.getID())) {
			player.message("I'd better speak to an Ironman Npc for a replacement");
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (!config().SPAWN_IRON_MAN_NPCS) return;

		if (n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN) {
			if (player.getAttribute("ironman_delete", false)) {
				if (player.getCache().hasKey("bank_pin")) {
					mes(n, "Enter your Bank PIN to downgrade your Iron Man status.");
					delay(2);

					if (!validatebankpin(player, n)) {
						ActionSender.sendBox(player, "Incorrect bank pin", false);
						player.setAttribute("ironman_delete", false);
						ActionSender.sendIronManInterface(player);
						return;
					}
					player.setAttribute("ironman_delete", false);
					player.message("You have correctly entered your PIN");
					int id = player.getAttribute("ironman_mode");
					if (id != -1) {
						player.setIronMan(id);
					}
					player.message("You have downgraded your ironman status");
					ActionSender.sendIronManMode(player);
					ActionSender.sendIronManInterface(player);
				}
				return;
			} else if (player.getAttribute("ironman_pin", false)) {
				mes(n, "You'll need to set a Bank PIN for that.");
				delay(2);
				int menu = multi(player,
					"Okay, let me set a PIN.",
					"No, I don't want a Bank PIN.");
				if (menu != -1) {
					if (menu == 0) {
						if (!player.getCache().hasKey("bank_pin")) {
							if(setbankpin(player, n)) {
								player.setIronManRestriction(0);
								ActionSender.sendIronManMode(player);
								ActionSender.sendIronManInterface(player);
							}

							player.setAttribute("ironman_pin", false);
						}
					} else if (menu == 1) {
						ActionSender.sendIronManInterface(player);
						player.setAttribute("ironman_pin", false);
					}
				} else {
					player.setAttribute("ironman_pin", false);
				}
				return;
			}
			if (player.isIronMan(IronmanMode.Ironman.id())) {
				npcsay(player, n, "Hail, Iron Man!");
			} else if (player.isIronMan(IronmanMode.Ultimate.id())) {
				npcsay(player, n, "Hail, Ultimate Iron Man!");
			} else if (player.isIronMan(IronmanMode.Hardcore.id())) {
				npcsay(player, n, "Hail, Hardcore Iron Man!");
			} else {
				npcsay(player, n, "Hello, " + player.getUsername() + ". We're the Iron Man tutors.");
			}
			npcsay(player, n, "What can we do for you?");
			int menu = multi(player, n,
				"Tell me about Iron Men.",
				"I'd like to " + (player.getLocation().onTutorialIsland() ? "change" : "review") + " my Iron Man mode.",
				"Have you any armour for me, please?",
				"I'm fine, thanks.");
			if (menu == 0) {
				npcsay(player, n, "When you play as an Iron Man, you do everything",
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
				ActionSender.sendIronManInterface(player);
			} else if (menu == 2) {
				armourOption(player, n);
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN;
	}

	@Override
	public boolean blockOpNpc(Player player, Npc n, String command) {
		return n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN && command.equalsIgnoreCase("Armour");
	}

	@Override
	public void onOpNpc(Player player, Npc n, String command) {
		if (!config().SPAWN_IRON_MAN_NPCS) return;
		if (n.getID() == IRON_MAN || n.getID() == ULTIMATE_IRON_MAN || n.getID() == HARDCORE_IRON_MAN && command.equalsIgnoreCase("Armour")) {
			armourOption(player, n);
		}
	}

	private void armourOption(Player player, Npc n) {
		if ((!player.isIronMan(IronmanMode.Ironman.id())) && (!player.isIronMan(IronmanMode.Ultimate.id()) && (!player.isIronMan(IronmanMode.Hardcore.id())))) {
			npcsay(player, n, "You're not an Iron Man.", "Our armour is only for them.");
		} else {
			if (player.getLocation().onTutorialIsland()) {
				npcsay(player, n, "We'll give you your armour once you're off this island.",
					"Come and see us in Lumbridge.");
			} else {
				if (!ifbankorheld(player, expectedArmour(player, ArmourPart.HELM)) ||
					!ifbankorheld(player, expectedArmour(player, ArmourPart.BODY)) ||
					!ifbankorheld(player, expectedArmour(player, ArmourPart.LEGS))) {
					if (player.getIronMan() == IronmanMode.Ironman.id()) {
						if (!ifbankorheld(player, ItemId.IRONMAN_HELM.id()))
							give(player, ItemId.IRONMAN_HELM.id(), 1);
						if (!ifbankorheld(player, ItemId.IRONMAN_PLATEBODY.id()))
							give(player, ItemId.IRONMAN_PLATEBODY.id(), 1);
						if (!ifbankorheld(player, ItemId.IRONMAN_PLATELEGS.id()))
							give(player, ItemId.IRONMAN_PLATELEGS.id(), 1);
					} else if (player.getIronMan() == IronmanMode.Ultimate.id()) {
						if (!ifbankorheld(player, ItemId.ULTIMATE_IRONMAN_HELM.id()))
							give(player, ItemId.ULTIMATE_IRONMAN_HELM.id(), 1);
						if (!ifbankorheld(player, ItemId.ULTIMATE_IRONMAN_PLATEBODY.id()))
							give(player, ItemId.ULTIMATE_IRONMAN_PLATEBODY.id(), 1);
						if (!ifbankorheld(player, ItemId.ULTIMATE_IRONMAN_PLATELEGS.id()))
							give(player, ItemId.ULTIMATE_IRONMAN_PLATELEGS.id(), 1);
					} else if (player.getIronMan() == IronmanMode.Hardcore.id()) {
						if (!ifbankorheld(player, ItemId.HARDCORE_IRONMAN_HELM.id()))
							give(player, ItemId.HARDCORE_IRONMAN_HELM.id(), 1);
						if (!ifbankorheld(player, ItemId.HARDCORE_IRONMAN_PLATEBODY.id()))
							give(player, ItemId.HARDCORE_IRONMAN_PLATEBODY.id(), 1);
						if (!ifbankorheld(player, ItemId.HARDCORE_IRONMAN_PLATELEGS.id()))
							give(player, ItemId.HARDCORE_IRONMAN_PLATELEGS.id(), 1);
					}
					npcsay(player, n, "There you go. Wear it with pride.");
				} else {
					npcsay(player, n, "I think you've already got the whole set.");
				}
			}
		}
	}

	int expectedArmour(Player player, ArmourPart part) {
		if (player.getIronMan() == IronmanMode.Ironman.id()) {
			switch(part) {
				case HELM:
					return ItemId.IRONMAN_HELM.id();
				case BODY:
					return ItemId.IRONMAN_PLATEBODY.id();
				case LEGS:
					return ItemId.IRONMAN_PLATELEGS.id();
			}
		} else if (player.getIronMan() == IronmanMode.Ultimate.id()) {
			switch(part) {
				case HELM:
					return ItemId.ULTIMATE_IRONMAN_HELM.id();
				case BODY:
					return ItemId.ULTIMATE_IRONMAN_PLATEBODY.id();
				case LEGS:
					return ItemId.ULTIMATE_IRONMAN_PLATELEGS.id();
			}
		} else if (player.getIronMan() == IronmanMode.Hardcore.id()) {
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
	public boolean blockTakeObj(Player player, GroundItem i) {
		return DataConversions.inArray(ironmanArmourPieces, i.getID());
	}
}
