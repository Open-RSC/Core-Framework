package com.openrsc.server.plugins.misc;

import com.openrsc.server.Constants;
import com.openrsc.server.Server;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.replaceObject;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

public class RandomObjects implements ObjectActionExecutiveListener, ObjectActionListener {

	@Override
	public void onObjectAction(final GameObject object, String command, Player owner) {
		if (command.equals("search") && object.getID() == 17) {
			owner.message(
				"You search the chest, but find nothing");
			return;
		}
		switch (object.getID()) {
			/* Brimhaven Jungle tree swings TODO: Possibly should give agility XP ? */
			/* Stones */
			case 701:
				message(owner, "You jump onto the rock");
				if (object.getX() == 346 && object.getY() == 807) {
					owner.teleport(346, 807, false);
				} else if (object.getX() == 347 && object.getY() == 806) {
					owner.teleport(347, 808, false);
				}
				message(owner, "And cross the water without problems");
				break;
			case 79:
				if (command.equals("close")) {
					owner.setBusyTimer(600);
					owner.message("You slide the cover back over the manhole");
					replaceObject(object, new GameObject(object.getLocation(), 78, object.getDirection(), object.getType()));
				} else {
					owner.message("Nothing interesting happens");
				}
				break;
			case 78:
				if (command.equals("open")) {
					owner.setBusyTimer(600);
					owner.message("You slide open the manhole cover");
					replaceObject(object, new GameObject(object.getLocation(), 79, object.getDirection(), object.getType()));
				}
				break;
			case 203:
				if (command.equals("close"))
					World.getWorld().replaceGameObject(object,
						new GameObject(object.getLocation(), 202, object
							.getDirection(), object.getType()));
				else
					owner.message("the coffin is empty.");
				break;
			case 202:
				World.getWorld().replaceGameObject(object,
					new GameObject(object.getLocation(), 203, object
						.getDirection(), object.getType()));
				break;
			case 613: // Shilo cart
				if (object.getX() != 384 || object.getY() != 851) {
					return;
				}
				if (owner.getX() >= 386) {
					message(owner, "You climb up onto the cart.",
						"You nimbly jump from one side of the cart...");
					owner.teleport(383, 852);
					owner.playerServerMessage(MessageType.QUEST, "...to the other and climb down again.");
					return;
				}
				if (owner.getQuestStage(Constants.Quests.SHILO_VILLAGE) != -1) {
					message(owner, "You approach the cart and see undead creatures gathering by the village gates.",
						"There is a note attached to the cart.",
						"The note says,",
						"@gre@Danger deadly green mist do not enter if you value your life");
					Npc mosol = getNearestNpc(owner, 539, 15);
					if (mosol != null) {
						npcTalk(owner, mosol, "You must be a maniac to go in there!");
					}
				} else {
					owner.message("It looks as if you can climb across.");
				}
				message(owner, "You search the cart.",
					"You may be able to climb across the cart.",
					"Would you like to try?");
				int menu = showMenu(owner,
					"Yes, I am am very nimble and agile!",
					"No, I am happy where I am thanks!");
				if (menu == 0) {
					message(owner, "You climb up onto the cart",
						"You nimbly jump from one side of the cart to the other.");
					owner.teleport(386, 852);
					owner.playerServerMessage(MessageType.QUEST, "And climb down again");
				} else if (menu == 1) {
					message(owner, "You think better of clambering over the cart, you might get dirty.");
					playerTalk(owner, null, "I'd probably have just scraped my knees up as well.");
				}
				break;
			case 643: // Gnome tree stone
				if (object.getX() != 416 || object.getY() != 161) {
					return;
				}
				owner.setBusy(true);
				owner.message(
					"You twist the stone tile to one side");
				Server.getServer().getEventHandler().add(
					new ShortEvent(owner) {
						public void action() {
							owner.message(
								"It reveals a ladder, you climb down");
							owner.teleport(703, 3284, false);
							owner.setBusy(false);
						}
					});
				break;
			case 417:
				owner.message("you enter the cave");
				owner.teleport(617, 3479);
				owner.message("it leads downwards to the sewer");
				break;
			case 241:
			case 242:
			case 243:
				message(owner, "You board the ship");
				owner.teleport(263, 660, false);
				sleep(2200);
				owner.message("The ship arrives at Port Sarim");
				break;
		}

		if (object.getX() == 94 && object.getY() == 521 && object.getID() == 60) {
			int x = owner.getX() == 94 ? 93 : 94, y = owner.getY();
			owner.teleport(x, y, false);
		}
		// ARDOUGNE WALL GATEWAY FOR BIOHAZARD ETC...
		if (object.getID() == 450) {
			message(owner, "you pull on the large wooden doors");
			if (owner.getQuestStage(Constants.Quests.BIOHAZARD) == -1) {
				owner.message("you open it and walk through");
				Npc gateMourner = getNearestNpc(owner, 451, 15);
				if (gateMourner != null) {
					npcTalk(owner, gateMourner, "go through");
				}
				if (owner.getX() >= 624) {
					owner.teleport(620, 589);
				} else {
					owner.teleport(626, 588);
				}
			} else {
				owner.message("but it will not open");
			}
		}
		if (object.getID() == 400) {
			owner.message("The plant takes a bite at you!");
			owner.damage(getCurrentLevel(owner, Skills.HITPOINTS) / 10 + 2);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player player) {
		if (obj.getID() == 417) {
			return true;
		}
		if ((obj.getID() == 78 && command.equals("open")) || (obj.getID() == 79 && command.equals("close"))) {
			return true;
		}
		if (obj.getID() == 613 || obj.getID() == 643) {
			return true;
		}
		if (obj.getID() == 202 || obj.getID() == 203 || inArray(obj.getID(), 241, 242, 243))
			return true;
		if (obj.getLocation().getX() == 94 && obj.getLocation().getY() == 521
			&& obj.getID() == 60) {
			if (Constants.GameServer.MEMBER_WORLD) {
				return true;
			}
		}
		if (obj.getID() == 400) {
			return true;
		}
		if (obj.getID() == 450) {
			return true;
		}
		return false;
	}

}
