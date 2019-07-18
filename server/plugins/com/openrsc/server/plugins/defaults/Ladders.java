package com.openrsc.server.plugins.defaults;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.TelePoint;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;


public class Ladders {

	public boolean blockObjectAction(GameObject obj, String command,
									 Player player) {
		return (command.equals("climb-down") || command.equals("go down") || command
			.equals("climb down"))
			|| command.equals("climb-up")
			|| command.equals("go up")
			|| command.equals("pull");
	}

	public void onObjectAction(GameObject obj, String command, Player player) {
		player.setBusyTimer(650);
		if (obj.getID() == 487 && !Constants.GameServer.MEMBER_WORLD) {
			player.message(player.MEMBER_MESSAGE);
			return;
		} else if (obj.getID() == 79 && obj.getX() == 243 && obj.getY() == 95) {
			player.message("Are you sure you want to go down to this lair?");
			int menu = showMenu(player, "Yes I take the risk!", "No stay up here.");
			if (menu == 0) {
				player.message("You climb down the manhole and land in a water lair");
				player.teleport(98, 2931);
			} else if (menu == 1) {
				player.message("You decide to stay.");
			}
			//player.message("The new dungeon is available in a couple of minutes");
			//player.message("We are doing the decoration, please stay tuned.");
			return;
		} else if (obj.getID() == 5 && (obj.getX() == 98 && obj.getY() == 2930 || obj.getX() == 137 && obj.getY() == 2932)) {
			player.teleport(243, 96);
			player.message("You climb up the ladder");
			return;
		} else if (obj.getID() == 629) {
			player.teleport(576, 3580);
			player.message("You go up the stairs");
			return;
		} else if (obj.getID() == 621) {
			player.teleport(606, 3556);
			player.message("You go up the stairs");
			return;
		}
		TelePoint telePoint = EntityHandler.getObjectTelePoint(obj
			.getLocation(), command);
		if (telePoint != null) {
			player.teleport(telePoint.getX(), telePoint.getY(), false);
		} else if (obj.getID() == 487) {
			player.message("You pull the lever");
			player.teleport(567, 3330);
			sleep(600);
			if (player.getX() == 567 && player.getY() == 3330) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
		} else if (obj.getID() == 488) {
			player.message("You pull the lever");
			player.teleport(282, 3019);
			sleep(600);
			if (player.getX() == 282 && player.getY() == 3019) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
		} else if (obj.getID() == 349) {
			player.playerServerMessage(MessageType.QUEST, "You pull the lever");
			player.teleport(621, 596);
			sleep(600);
			if (player.getX() == 621 && player.getY() == 596) {
				displayTeleportBubble(player, player.getX(), player.getY(), false);
			}
		} else if (obj.getID() == 348) {
			boolean skip = player.getCache().hasKey("hide_wild_lever_warn")
					&& player.getCache().getBoolean("hide_wild_lever_warn");
			boolean teleport = false;
			if (!skip) {
				player.playerServerMessage(MessageType.QUEST, "warning pulling this lever will teleport you deep into the wilderness");
				player.playerServerMessage(MessageType.QUEST, "Are you sure you wish to pull it?");
				int menu = showMenu(player, "Yes I'm brave", "Eeep the wilderness no thankyou", "Yes please, don't show this message again");
				if (menu == 0 || menu == 2) {
					if (menu == 2) player.getCache().store("hide_wild_lever_warn", true);
					teleport = true;
				}
			}
			if (skip || teleport) {
				player.message("you pull the lever");
				player.teleport(180, 128);
				displayTeleportBubble(player, player.getX(), player.getY(), false);
				sleep(600);
				if (player.getX() == 180 && player.getY() == 128) {
					displayTeleportBubble(player, player.getX(), player.getY(), false);
				}
			}
		} else if (obj.getID() == 776) {
			if (hasItem(player, ItemId.PARAMAYA_REST_TICKET.id())) {
				player.message("The barman takes your ticket and allows you up to");
				player.message("the dormitory.");
				player.teleport(395, 2713);
				player.message("You climb up the ladder");
			} else {
				Npc kaleb = getNearestNpc(player, NpcId.KALEB.id(), 10);
				if (kaleb != null) {
					player.message("You need a ticket to access the dormitory");
					npcTalk(player, kaleb, "You can buy a ticket to the dormitory from me.",
						"And have a lovely nights rest.");
				} else {
					player.message("Kaleb is busy at the moment.");
				}
			}
		} else if (obj.getID() == 198 && obj.getX() == 251 && obj.getY() == 468) { // Prayer
			// Guild
			// Ladder
			if (!player.getCache().hasKey("prayer_guild")) {
				player.setBusy(true);
				Npc abbot = World.getWorld().getNpc(NpcId.ABBOT_LANGLEY.id(), 249, 252, 458, 468);
				if (abbot != null) {
					npcTalk(player, abbot, "Only members of our order can go up there");
					int op = showMenu(player, abbot, false, "Well can i join your order?",
						"Oh sorry");
					if (op == 0) {
						playerTalk(player, abbot, "Well can I join your order?");
						if (getCurrentLevel(player, SKILLS.PRAYER.id()) >= 31) {
							npcTalk(player, abbot, "Ok I see you are someone suitable for our order",
								"You may join");
							player.getCache().set("prayer_guild", 1);
							player.teleport(251, 1411, false);
							player.message("You climb up the ladder");
						} else {
							npcTalk(player, abbot, "No I feel you are not devout enough");
							Server.getServer().getEventHandler().add(
								new ShortEvent(player, "Prayer Guild Ladder") {
									public void action() {
										owner.setBusy(false);
										owner.message(
											"You need a prayer level of 31");
									}
								}
							);
						}
					} else if (op == 1) {
						playerTalk(player, abbot, "Oh Sorry");
					}
				} else {
					player.message("Abbot Langley is busy at the moment.");
				}
			} else {
				player.teleport(251, 1411, false);
				player.message("You climb up the ladder");
			}
		} else if (obj.getID() == 223 && obj.getX() == 274 && obj.getY() == 566) { // Mining
			// Guild
			// Ladder
			if (getCurrentLevel(player, SKILLS.MINING.id()) < 60) {
				player.setBusy(true);
				Npc dwarf = World.getWorld().getNpc(NpcId.DWARF_MINING_GUILD.id(), 272, 277, 563, 567);
				if (dwarf != null) {
					npcYell(player, dwarf,
						"Sorry only the top miners are allowed in there");
				}
				Server.getServer().getEventHandler().add(
					new ShortEvent(player, "Mining Guild Ladder") {
						public void action() {
							owner.setBusy(false);
							owner.message(
								"You need a mining level of 60 to enter");
						}
					});
			} else {
				player.teleport(274, 3397, false);
			}
		} else if (obj.getID() == 223 && obj.getX() == 312 && obj.getY() == 3348) { // ladder to black hole
			if (!hasItem(player, ItemId.DISK_OF_RETURNING.id())) {
				message(player, "you seem to be missing a disk to use the ladder");
			} else {
				message(player, 1200, "You climb down the ladder");
				int offX = DataConversions.random(0,4) - 2;
				int offY = DataConversions.random(0,4) - 2;
				player.teleport(305 + offX, 3300 + offY);
				ActionSender.sendPlayerOnBlackHole(player);
			}
		} else if (obj.getID() == 342 && obj.getX() == 611 && obj.getY() == 601) {
			Npc paladinGuard = getNearestNpc(player, NpcId.PALADIN.id(), 4);
			if (paladinGuard != null) {
				npcYell(player, paladinGuard, "Stop right there");
				paladinGuard.setChasing(player);
				sleep(1000);
				if (player.inCombat()) {
					return;
				}
				int[] coords = coordModifier(player, true, obj);
				player.teleport(coords[0], coords[1], false);
				player.message(
					"You " + command.replace("-", " ") + " the "
						+ obj.getGameObjectDef().getName().toLowerCase());
			}
		} else if (obj.getID() == 249 && obj.getX() == 98 && obj.getY() == 3537) { // lost city (Zanaris) ladder
			Npc ladderAttendant = World.getWorld().getNpc(NpcId.FAIRY_LADDER_ATTENDANT.id(), 99, 99, 3537, 3537);
			if (ladderAttendant != null) {
				npcTalk(player, ladderAttendant, "This ladder leaves Zanaris",
					"It leads to near Al Kharid in your mortal realm",
					"You won't be able to return this way",
					"Are you sure you have sampled your fill of delights from our market?");
				int m = showMenu(player, ladderAttendant, "I think I'll stay down here a bit longer", "Yes, I'm ready to leave");
				if (m == 1) {
					player.message("You climb up the ladder");
					player.teleport(98, 706, false);
				}
			}
		} else if (obj.getID() == 1187 && obj.getX() == 446 && obj.getY() == 3367) {
			player.teleport(222, 110, false);
		} else if (obj.getID() == 331 && obj.getX() == 150 && obj.getY() == 558) {
			player.teleport(151, 1505, false);
		} else if (obj.getID() == 6 && obj.getX() == 282 && obj.getY() == 185 && !Constants.GameServer.MEMBER_WORLD) {
			player.message(player.MEMBER_MESSAGE);
		} else if (obj.getID() == 6 && obj.getX() == 148 && obj.getY() == 1507) {
			player.teleport(148, 563, false);
		} else if (obj.getID() == 630 && obj.getX() == 576 && obj.getY() == 3577) {
			// TODO ????
		} else if (command.equals("climb-up") || command.equals("climb up")
			|| command.equals("go up")) {
			int[] coords = coordModifier(player, true, obj);
			player.teleport(coords[0], coords[1], false);
			player.message(
				"You " + command.replace("-", " ") + " the "
					+ obj.getGameObjectDef().getName().toLowerCase());
		} else if (command.equals("climb-down") || command.equals("climb down")
			|| command.equals("go down")) {
			int[] coords = coordModifier(player, false, obj);
			player.teleport(coords[0], coords[1], false);
			player.message(
				"You " + command.replace("-", " ") + " the "
					+ obj.getGameObjectDef().getName().toLowerCase());
		}
	}

	private int[] coordModifier(Player player, boolean up, GameObject object) {
		if (object.getGameObjectDef().getHeight() <= 1) {
			return new int[]{player.getX(),
				Formulae.getNewY(player.getY(), up)};
		}
		int[] coords = {object.getX(), Formulae.getNewY(object.getY(), up)};
		switch (object.getDirection()) {
			case 0:
				coords[1] -= (up ? -object.getGameObjectDef().getHeight() : 1);
				break;
			case 2:
				coords[0] -= (up ? -object.getGameObjectDef().getHeight() : 1);
				break;
			case 4:
				coords[1] += (up ? -1 : object.getGameObjectDef().getHeight());
				break;
			case 6:
				coords[0] += (up ? -1 : object.getGameObjectDef().getHeight());
				break;
		}
		return coords;
	}

}
