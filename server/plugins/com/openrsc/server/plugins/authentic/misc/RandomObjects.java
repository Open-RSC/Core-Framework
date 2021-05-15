package com.openrsc.server.plugins.authentic.misc;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class RandomObjects implements OpLocTrigger {

	@Override
	public void onOpLoc(Player player, final GameObject object, String command) {
		if (command.equals("search") && object.getID() == 17) {
			player.message(
				"You search the chest, but find nothing");
			return;
		}
		switch (object.getID()) {
			case 79:
				if (command.equals("close")) {
					player.playerServerMessage(MessageType.QUEST, "You slide the cover back over the manhole");
					changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 78, object.getDirection(), object.getType()));
				} else {
					player.message("Nothing interesting happens");
				}
				break;
			case 78:
				if (command.equals("open")) {
					player.playerServerMessage(MessageType.QUEST, "You slide open the manhole cover");
					changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 79, object.getDirection(), object.getType()));
				}
				break;
			case 203:
				if (command.equals("close"))
					changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 202, object.getDirection(), object.getType()));
				else
					player.message("the coffin is empty.");
				break;
			case 202:
				changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 203, object.getDirection(), object.getType()));
				break;
			case 613: // Shilo cart
				if (object.getX() != 384 || object.getY() != 851) {
					return;
				}
				if (player.getX() >= 386) {
					mes("You climb up onto the cart.");
					delay(3);
					mes("You nimbly jump from one side of the cart...");
					delay(3);
					player.teleport(383, 852);
					player.playerServerMessage(MessageType.QUEST, "...to the other and climb down again.");
					return;
				}
				if (command.toLowerCase().equals("search") || player.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					mes("It looks as if you can climb across.");
					delay(3);
					mes("You search the cart.");
					delay(3);
					if (player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too fatigued to attempt climb across");
						return;
					}
					mes("You may be able to climb across the cart.");
					delay(3);
					mes("Would you like to try?");
					delay(3);
						int menu = multi(player,
							"Yes, I am am very nimble and agile!",
							"No, I am happy where I am thanks!");
						if (menu == 0) {
							mes("You climb up onto the cart");
							delay(3);
							mes("You nimbly jump from one side of the cart to the other.");
							delay(3);
							player.teleport(386, 852);
							player.playerServerMessage(MessageType.QUEST, "And climb down again");
						} else if (menu == 1) {
							mes("You think better of clambering over the cart, you might get dirty.");
							delay(3);
							say(player, null, "I'd probably have just scraped my knees up as well.");
						}
				} else {
					mes("You approach the cart and see undead creatures gathering by the village gates.");
					delay(3);
					mes("There is a note attached to the cart.");
					delay(3);
					mes("The note says,");
					delay(3);
					mes("@gre@Danger deadly green mist do not enter if you value your life");
					delay(3);
					Npc mosol = ifnearvisnpc(player, NpcId.MOSOL.id(), 15);
					if (mosol != null) {
						npcsay(player, mosol, "You must be a maniac to go in there!");
					}
				}
				break;
			case 643: // Gnome tree stone
				if (object.getX() != 416 || object.getY() != 161) {
					return;
				}
				player.message("You twist the stone tile to one side");
				if (player.getQuestStage(Quests.GRAND_TREE) == -1) {
					delay(2);
					player.message("It reveals a ladder, you climb down");
					player.teleport(703, 3284, false);
				} else {
					player.message("but nothing happens");
				}
				break;
			case 417: // CAVE ENTRANCE HAZEEL CULT
				player.message("you enter the cave");
				player.teleport(617, 3479);
				player.message("it leads downwards to the sewer");
				break;
			case 241:
			case 242:
			case 243:
				mes("You board the ship");
				delay(3);
				player.teleport(263, 660, false);
				delay(4);
				player.message("The ship arrives at Port Sarim");
				break;
			case 1241:
				if (player.getCache().hasKey("scotruth_to_chaos_altar")) {
					player.message("You step into the tunnel...");
					player.teleport(331,213, false);
					delay(4);
					player.message("And find your way into the wilderness");
				} else {
					player.message("You don't have permission to use this");
				}
				break;
			case 1242:
				player.message("You enter the rowboat...");
				delay(3);
				player.teleport(206,449);
				player.message("And stop in Edgeville");
				break;
		}
		// SMUGGLING GATE VARROCK
		if (object.getX() == 94 && object.getY() == 521 && object.getID() == 60) {
			int x = player.getX() == 94 ? 93 : 94, y = player.getY();
			player.teleport(x, y, false);
		}
		// ARDOUGNE WALL GATEWAY FOR BIOHAZARD ETC...
		if (object.getID() == 450) {
			mes("you pull on the large wooden doors");
			delay(3);
			if (player.getQuestStage(Quests.BIOHAZARD) == -1) {
				player.message("you open it and walk through");
				Npc gateMourner = ifnearvisnpc(player, NpcId.MOURNER_BYENTRANCE.id(), 15);
				if (gateMourner != null) {
					npcsay(player, gateMourner, "go through");
				}
				if (player.getX() >= 624) {
					player.teleport(620, 589);
				} else {
					player.teleport(626, 588);
				}
			} else {
				player.message("but it will not open");
			}
		}
		if (object.getID() == 400) {
			player.playerServerMessage(MessageType.QUEST, "The plant takes a bite at you!");
			player.damage(getCurrentLevel(player, Skill.HITS.id()) / 10 + 2);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
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
			if (player.getConfig().MEMBER_WORLD) {
				return true;
			}
		}
		if (obj.getID() == 400) {
			return true;
		}
		if (obj.getID() == 450) {
			return true;
		}
		if (obj.getID() == 1241) { // Scotruth to chaos altar shortcut
			return true;
		}
		if (obj.getID() == 1242) { //Rowboat with a travel option (used for Lum->Edge)
			return true;
		}
		return false;
	}

}
