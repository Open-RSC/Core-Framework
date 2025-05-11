package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.constants.IronmanMode;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
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
	private final static int IRON_MAN = NpcId.IRONMAN.id();
	private final static int ULTIMATE_IRON_MAN = NpcId.ULTIMATE_IRONMAN.id();
	private final static int HARDCORE_IRON_MAN = NpcId.HARDCORE_IRONMAN.id();

	private final int[] ironmanArmourPieces = new int[]{
		ItemId.IRONMAN_HELM.id(), ItemId.IRONMAN_PLATEBODY.id(), ItemId.IRONMAN_PLATE_TOP.id(),
		ItemId.IRONMAN_PLATELEGS.id(), ItemId.IRONMAN_PLATED_SKIRT.id(),
		ItemId.ULTIMATE_IRONMAN_HELM.id(), ItemId.ULTIMATE_IRONMAN_PLATEBODY.id(), ItemId.ULTIMATE_IRONMAN_PLATE_TOP.id(),
		ItemId.ULTIMATE_IRONMAN_PLATELEGS.id(), ItemId.ULTIMATE_IRONMAN_PLATED_SKIRT.id(),
		ItemId.HARDCORE_IRONMAN_HELM.id(), ItemId.HARDCORE_IRONMAN_PLATEBODY.id(), ItemId.HARDCORE_IRONMAN_PLATE_TOP.id(),
		ItemId.HARDCORE_IRONMAN_PLATELEGS.id(), ItemId.HARDCORE_IRONMAN_PLATED_SKIRT.id()
	};

	@Override
	public void onTakeObj(Player player, GroundItem item) {
		if (DataConversions.inArray(ironmanArmourPieces, item.getID())) {
			player.message("I'd better speak to an Ironman Npc for a replacement");
		}
	}

	@Override
	public void onTalkNpc(final Player player, final Npc npc) {
		if (!player.getConfig().SPAWN_IRON_MAN_NPCS) {
			player.setSuspiciousPlayer(true, "trying to talk to ironman npc when config is disabled.");
			return;
		}

		if (player.getAttribute("ironman_delete", false)) { // Change mode/status
			player.setAttribute("ironman_delete", false);

			if (player.getCache().hasKey("bank_pin")) {
				mes(npc, "Enter your Bank PIN to downgrade your Ironman status.");
				delay(2);
				if (!validatebankpin(player, npc)) {
					ActionSender.sendIronManInterface(player);
					return;
				}
			}

			final int mode = player.getAttribute("ironman_mode_new");
			if (mode == -1) return;
			mes( "Are you sure you wish to downgrade your Ironman status?");
			// Confirmation for downgrading Ironman status
			final int confirmation = multi(player,
				"Yes, I am sure, please downgrade my Ironman status.", // 0
				"No, I changed my mind, keep my Ironman status." // 1
			);

			switch (confirmation) {
				case 0: // Confirm downgrade
					player.setIronMan(mode);
					player.setAttribute("ironman_mode", mode);
					player.message("You have downgraded your Ironman status.");
					break;
				case 1: // Cancel downgrade
				default:
					player.message("You have chosen to keep your current Ironman status.");
					break;
			}

			return;
		}

		if (player.getAttribute("ironman_pin", false)) { // Change deactivation status
			player.setAttribute("ironman_pin", false);

			mes(npc, "You'll need to set a Bank PIN for that.");
			delay(2);

			final int menu = multi(player,
				"Okay, let me set a PIN.", // 0
				"No, I don't want a Bank PIN." // 1
			);

			switch (menu) {
				case 0:
					if (!setbankpin(player, npc)) return;
					player.setIronManRestriction(0);
					ActionSender.sendIronManMode(player);
					ActionSender.sendIronManInterface(player);
					break;
				case 1:
					ActionSender.sendIronManInterface(player);
					break;
				default:
					break;
			}
			return;
		}

		final String greeting;

		if (player.isIronMan(IronmanMode.Ironman.id())) {
			greeting = "Hail, Ironman!";
		} else if (player.isIronMan(IronmanMode.Ultimate.id())) {
			greeting = "Hail, Ultimate Ironman!";
		} else if (player.isIronMan(IronmanMode.Hardcore.id())) {
			greeting = "Hail, Hardcore Ironman!";
		} else {
			greeting = String.format("Hello, %s. We're the Ironman tutors.", player.getUsername());
		}

		npcsay(player, npc, greeting);
		npcsay(player, npc, "What can we do for you?");

		final int menu = multi(player, npc,
			"Tell me about Iron Men.", // 0
			"I'd like to " + (player.getLocation().onTutorialIsland() ? "change" : "review") + " my Ironman mode.", // 1
			"Have you any armour for me, please?", // 2
			"I'm fine, thanks." // 3
		);

		switch (menu) {
			case 0:
				npcsay(player, npc, "When you play as an Ironman, you do everything",
					"for yourself. You don't trade with other players, or take",
					"their items, or accept their help.",
					"As an Ironman, you choose to have these restrictions",
					"imposed on you, so everyone knows you're doing it",
					"properly.",
					"If you think you have what it takes, you can choose to",
					"become a Hardcore Ironman",
					"In addition to the standard restrictions,",
					"Hardcore Iron Men only have one life.",
					"In the event of a dangerious death, your Hardcore Iron Men status",
					"will be downgraded to that of a standard Ironman, and your",
					"stats will be frozen on the Hardcore Ironman hiscores.",
					"For the ultimate challenge, you can choose to become",
					"an Ultimate Ironman.",
					"In addition to the standard restrictions, Ultimate Iron",
					"Men are blocked from using the bank, and they drop all",
					"their items when they die.",
					"While you're on Tutorial Island, you can switch freely",
					"between being a standard Ironman, an Ultimate Ironman,",
					"a Hardcore Ironman or a normal player.",
					"Once you've left this island, you'll be able to find us in",
					"Lumbridge, but we'll only let you switch your",
					"restrictions downwards, not upwards.",
					"So we will let Hardcore Iron Men or Ultimate Iron Men",
					"downgrade to a standard Iron Men,",
					"and we'll let either Ironman types of Ironman become normal players.");
				break;
			case 1:
				ActionSender.sendIronManInterface(player);
				break;
			case 2:
				armourOption(player, npc);
				break;
			default:
				break;
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
			npcsay(player, n, "You're not an Ironman.", "Our armour is only for them.");
		} else {
			if (player.getLocation().onTutorialIsland()) {
				npcsay(player, n, "We'll give you your armour once you're off this island.",
					"Come and see us in Lumbridge.");
			} else {
				if (missingArmour(player, ArmourPart.ANY)) {
					if (missingArmour(player, ArmourPart.HELM)) {
						int itemId = getArmourId(ArmourPart.HELM, player.getIronMan());
						give(player, itemId, 1);
						mes(n.getDef().getName() + " gives you a " + new Item(itemId).getDef(player.getWorld()).getName());
						delay(3);
					}

					if (missingArmour(player, ArmourPart.BODY)) {
						int itemId;
						if (!player.isMale()) {
							npcsay(player, n, "Would you prefer a platebody or a plate top?");
							int option = multi(player, n, "Platebody please",
								"Plate top please");
							if (option == 0) {
								itemId = getArmourId(ArmourPart.BODY, player.getIronMan());
							} else if (option == 1) {
								itemId = getArmourId(ArmourPart.TOP, player.getIronMan());
							} else {
								return;
							}
						} else {
							itemId = getArmourId(ArmourPart.BODY, player.getIronMan());
						}
						give(player, itemId, 1);
						mes(n.getDef().getName() + " gives you a " + new Item(itemId).getDef(player.getWorld()).getName());
						delay(3);
					}

					if (missingArmour(player, ArmourPart.LEGS)) {
						int itemId;
						npcsay(player, n, "Would you prefer platelegs or a plated skirt?");
						int option = multi(player, n, "Platelegs please",
							"Plated skirt please");
						if (option == 0) {
							itemId = getArmourId(ArmourPart.LEGS, player.getIronMan());
						} else if (option == 1) {
							itemId = getArmourId(ArmourPart.SKIRT, player.getIronMan());
						} else {
							return;
						}
						give(player, itemId, 1);
						mes(n.getDef().getName() + " gives you a " + new Item(itemId).getDef(player.getWorld()).getName());
						delay(3);
					}
					npcsay(player, n, "There you go. Wear it with pride.");
				} else {
					npcsay(player, n, "I think you've already got the whole set.");
				}
			}
		}
	}

	private boolean missingArmour(final Player player, final ArmourPart part) {
		boolean missingHelm = false;
		boolean missingBody = false;
		boolean missingLegs = false;

		if (part == ArmourPart.HELM || part == ArmourPart.ANY) {
			missingHelm = !ifbankorheld(player, getArmourId(ArmourPart.HELM, player.getIronMan()));
		}

		if (part == ArmourPart.BODY || part == ArmourPart.ANY) {
			missingBody = !ifbankorheld(player, getArmourId(ArmourPart.BODY, player.getIronMan()))
				&& !ifbankorheld(player, getArmourId(ArmourPart.TOP, player.getIronMan()));
		}

		if (part == ArmourPart.LEGS || part == ArmourPart.ANY) {
			missingLegs = !ifbankorheld(player, getArmourId(ArmourPart.LEGS, player.getIronMan()))
				&& !ifbankorheld(player, getArmourId(ArmourPart.SKIRT, player.getIronMan()));
		}

		return missingHelm || missingBody || missingLegs;
	}

	private int getArmourId(final ArmourPart part, final int ironmanMode) {
		switch (part) {
			case HELM:
				if (ironmanMode == IronmanMode.Ironman.id()) {
					return ItemId.IRONMAN_HELM.id();
				} else if (ironmanMode == IronmanMode.Ultimate.id()) {
					return ItemId.ULTIMATE_IRONMAN_HELM.id();
				} else if (ironmanMode == IronmanMode.Hardcore.id()) {
					return ItemId.HARDCORE_IRONMAN_HELM.id();
				}
				break;
			case BODY:
				if (ironmanMode == IronmanMode.Ironman.id()) {
					return ItemId.IRONMAN_PLATEBODY.id();
				} else if (ironmanMode == IronmanMode.Ultimate.id()) {
					return ItemId.ULTIMATE_IRONMAN_PLATEBODY.id();
				} else if (ironmanMode == IronmanMode.Hardcore.id()) {
					return ItemId.HARDCORE_IRONMAN_PLATEBODY.id();
				}
				break;
			case TOP:
				if (ironmanMode == IronmanMode.Ironman.id()) {
					return ItemId.IRONMAN_PLATE_TOP.id();
				} else if (ironmanMode == IronmanMode.Ultimate.id()) {
					return ItemId.ULTIMATE_IRONMAN_PLATE_TOP.id();
				} else if (ironmanMode == IronmanMode.Hardcore.id()) {
					return ItemId.HARDCORE_IRONMAN_PLATE_TOP.id();
				}
				break;
			case LEGS:
				if (ironmanMode == IronmanMode.Ironman.id()) {
					return ItemId.IRONMAN_PLATELEGS.id();
				} else if (ironmanMode == IronmanMode.Ultimate.id()) {
					return ItemId.ULTIMATE_IRONMAN_PLATELEGS.id();
				} else if (ironmanMode == IronmanMode.Hardcore.id()) {
					return ItemId.HARDCORE_IRONMAN_PLATELEGS.id();
				}
				break;
			case SKIRT:
				if (ironmanMode == IronmanMode.Ironman.id()) {
					return ItemId.IRONMAN_PLATED_SKIRT.id();
				} else if (ironmanMode == IronmanMode.Ultimate.id()) {
					return ItemId.ULTIMATE_IRONMAN_PLATED_SKIRT.id();
				} else if (ironmanMode == IronmanMode.Hardcore.id()) {
					return ItemId.HARDCORE_IRONMAN_PLATED_SKIRT.id();
				}
		}

		return -1;
	}

	enum ArmourPart {
		ANY, HELM, BODY, TOP, LEGS, SKIRT
	}

	@Override
public boolean blockTakeObj(Player player, GroundItem i) {
		return DataConversions.inArray(ironmanArmourPieces, i.getID());
	}
}
