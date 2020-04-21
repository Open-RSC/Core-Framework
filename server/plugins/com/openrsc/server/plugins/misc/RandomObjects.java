package com.openrsc.server.plugins.misc;

import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.ShortEvent;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class RandomObjects implements OpLocTrigger {

	@Override
	public void onOpLoc(final GameObject object, String command, Player owner) {
		if (command.equals("search") && object.getID() == 17) {
			owner.message(
				"You search the chest, but find nothing");
			return;
		}
		switch (object.getID()) {
			case 79:
				if (command.equals("close")) {
					owner.setBusyTimer(owner.getWorld().getServer().getConfig().GAME_TICK);
					owner.playerServerMessage(MessageType.QUEST, "You slide the cover back over the manhole");
					changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 78, object.getDirection(), object.getType()));
				} else {
					owner.message("Nothing interesting happens");
				}
				break;
			case 78:
				if (command.equals("open")) {
					owner.setBusyTimer(owner.getWorld().getServer().getConfig().GAME_TICK);
					owner.playerServerMessage(MessageType.QUEST, "You slide open the manhole cover");
					changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 79, object.getDirection(), object.getType()));
				}
				break;
			case 203:
				if (command.equals("close"))
					changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 202, object.getDirection(), object.getType()));
				else
					owner.message("the coffin is empty.");
				break;
			case 202:
				changeloc(object, new GameObject(object.getWorld(), object.getLocation(), 203, object.getDirection(), object.getType()));
				break;
			case 613: // Shilo cart
				if (object.getX() != 384 || object.getY() != 851) {
					return;
				}
				if (owner.getX() >= 386) {
					Functions.mes(owner, "You climb up onto the cart.",
						"You nimbly jump from one side of the cart...");
					owner.teleport(383, 852);
					owner.playerServerMessage(MessageType.QUEST, "...to the other and climb down again.");
					return;
				}
				if (command.toLowerCase().equals("search") || owner.getQuestStage(Quests.SHILO_VILLAGE) == -1) {
					Functions.mes(owner, "It looks as if you can climb across.",
							"You search the cart.");
					if (owner.getFatigue() >= owner.MAX_FATIGUE) {
						owner.message("You are too fatigued to attempt climb across");
						return;
					}
					Functions.mes(owner, "You may be able to climb across the cart.",
							"Would you like to try?");
						int menu = multi(owner,
							"Yes, I am am very nimble and agile!",
							"No, I am happy where I am thanks!");
						if (menu == 0) {
							Functions.mes(owner, "You climb up onto the cart",
								"You nimbly jump from one side of the cart to the other.");
							owner.teleport(386, 852);
							owner.playerServerMessage(MessageType.QUEST, "And climb down again");
						} else if (menu == 1) {
							Functions.mes(owner, "You think better of clambering over the cart, you might get dirty.");
							say(owner, null, "I'd probably have just scraped my knees up as well.");
						}
				} else {
					Functions.mes(owner, "You approach the cart and see undead creatures gathering by the village gates.",
							"There is a note attached to the cart.",
							"The note says,",
							"@gre@Danger deadly green mist do not enter if you value your life");
					Npc mosol = ifnearvisnpc(owner, NpcId.MOSOL.id(), 15);
					if (mosol != null) {
						npcsay(owner, mosol, "You must be a maniac to go in there!");
					}
				}
				break;
			case 643: // Gnome tree stone
				if (object.getX() != 416 || object.getY() != 161) {
					return;
				}
				owner.setBusy(true);
				owner.message("You twist the stone tile to one side");
				if (owner.getQuestStage(Quests.GRAND_TREE) == -1) {
					owner.getWorld().getServer().getGameEventHandler().add(
						new ShortEvent(owner.getWorld(), owner, "Gnome Tree Stone") {
							public void action() {
								owner.message("It reveals a ladder, you climb down");
								owner.teleport(703, 3284, false);
								owner.setBusy(false);
							}
					});
				} else {
					owner.message("but nothing happens");
					owner.setBusy(false);
				}
				break;
			case 417: // CAVE ENTRANCE HAZEEL CULT
				owner.message("you enter the cave");
				owner.teleport(617, 3479);
				owner.message("it leads downwards to the sewer");
				break;
			case 241:
			case 242:
			case 243:
				Functions.mes(owner, "You board the ship");
				owner.teleport(263, 660, false);
				delay(2200);
				owner.message("The ship arrives at Port Sarim");
				break;
			case 1241:
				if (owner.getCache().hasKey("scotruth_to_chaos_altar")) {
					owner.message("You step into the tunnel...");
					owner.teleport(331,213, false);
					delay(4*owner.getWorld().getServer().getConfig().GAME_TICK);
					owner.message("And find your way into the wilderness");
				} else {
					owner.message("You don't have permission to use this");
				}
				break;
			case 1242:
				owner.message("You enter the rowboat...");
				delay(owner.getWorld().getServer().getConfig().GAME_TICK * 3);
				owner.teleport(206,449);
				owner.message("And stop in Edgeville");
				break;
		}
		// SMUGGLING GATE VARROCK
		if (object.getX() == 94 && object.getY() == 521 && object.getID() == 60) {
			int x = owner.getX() == 94 ? 93 : 94, y = owner.getY();
			owner.teleport(x, y, false);
		}
		// ARDOUGNE WALL GATEWAY FOR BIOHAZARD ETC...
		if (object.getID() == 450) {
			Functions.mes(owner, "you pull on the large wooden doors");
			if (owner.getQuestStage(Quests.BIOHAZARD) == -1) {
				owner.message("you open it and walk through");
				Npc gateMourner = ifnearvisnpc(owner, NpcId.MOURNER_BYENTRANCE.id(), 15);
				if (gateMourner != null) {
					npcsay(owner, gateMourner, "go through");
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
			owner.playerServerMessage(MessageType.QUEST, "The plant takes a bite at you!");
			owner.damage(getCurrentLevel(owner, Skills.HITS) / 10 + 2);
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player player) {
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
			if (player.getWorld().getServer().getConfig().MEMBER_WORLD) {
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
