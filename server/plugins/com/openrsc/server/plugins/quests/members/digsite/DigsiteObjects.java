package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteObjects implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	/* Objects */
	public static int HOUSE_EAST_CHEST_CLOSED = 1104;
	public static int HOUSE_EAST_CHEST_OPEN = 1105;

	public static int HOUSE_BOOKCASE = 1090;

	public static int HOUSE_EAST_CUPBOARD_CLOSED = 1074;
	public static int HOUSE_EAST_CUPBOARD_OPEN = 1078;

	public static int HOUSE_WEST_CHESTS_CLOSED = 18;
	public static int HOUSE_WEST_CHESTS_OPEN = 17;

	public static final int[] SIGNPOST = { 1060, 1061, 1062, 1063 };
	public static int[] SACKS = { 1075, 1076 };
	public static int[] BUSH = { 1072, 1073 };

	public static int[] BURIED_SKELETON = { 1057, 1049 };

	public static int TENT_LOCKED_CHEST = 1085;
	public static int TENT_OPEN_CHEST = 1084;
	public static int SPECIMEN_TRAY = 1052;

	public static int CLIMB_UP_ROPE_SMALL_CAVE = 1097;
	public static int CLIMB_UP_ROPE_BIG_CAVE = 1098;

	public static int BRICK = 1096;
	public static int X_BARREL = 1082;
	public static int X_BARREL_OPEN = 1083;

	/* Items */
	public static int CRACKED_ROCK_SAMPLE = 1150;
	public static int BOOK_OF_EXPERIMENTAL_CHEMISTRY = 1141;
	public static int ROCK_PICK = 1114;
	public static int SPECIMEN_JAR = 1116;
	public static int ROCK_SAMPLE = 1149;

	/* NPCS */
	public static int WORKMAN = 722;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == HOUSE_EAST_CHEST_CLOSED || obj.getID() == HOUSE_EAST_CHEST_OPEN) {
			return true;
		}
		if(obj.getID() == HOUSE_BOOKCASE) {
			return true;
		}
		if(obj.getID() == HOUSE_EAST_CUPBOARD_CLOSED || obj.getID() == HOUSE_EAST_CUPBOARD_OPEN) {
			return true;
		}
		if(obj.getID() == HOUSE_WEST_CHESTS_CLOSED || obj.getID() == HOUSE_WEST_CHESTS_OPEN) {
			return true;
		}
		if(inArray(obj.getID(), SIGNPOST)) {
			return true;
		}
		if(inArray(obj.getID(), SACKS)) {
			return true;
		}
		if(inArray(obj.getID(), BURIED_SKELETON)) {
			return true;
		}
		if(obj.getID() == TENT_LOCKED_CHEST || obj.getID() == TENT_OPEN_CHEST) {
			return true;
		}
		if(obj.getID() == SPECIMEN_TRAY) {
			return true;
		}
		if(inArray(obj.getID(), BUSH)) {
			return true;
		}
		if(obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE || obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
			return true;
		}
		if(obj.getID() == BRICK || obj.getID() == X_BARREL_OPEN) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == X_BARREL_OPEN) {
			p.message("You search the barrel");
			p.message("The barrel has a foul-smelling liquid inside...");
			playerTalk(p, null, "I can't pick this up with my bare hands!",
					"I'll need something to put it in");
		}
		if(obj.getID() == BRICK) {
			playerTalk(p, null, "Hmmm, There's a room past these bricks",
					"If I could move them out of the way",
					"Then I could find out what's inside...");
		}
		if(obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE || obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
			p.message("You climb the ladder");
			if(obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
				p.teleport(25, 515);
			} else if(obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE) {
				p.teleport(14, 506);
			}
		}
		if(obj.getID() == TENT_LOCKED_CHEST) {
			p.message("The chest is locked");
		}
		if(obj.getID() == TENT_OPEN_CHEST) {
			if(command.equalsIgnoreCase("Search")) {
				message(p, "You search the chest");
				p.message("You find some unusual powder inside...");
				addItem(p, 1171, 1);
			}
		}
		if(inArray(obj.getID(), BUSH)) {
			p.message("You search the bush");
			if(obj.getID() == BUSH[1]) {
				playerTalk(p, null, "Hey, something has been dropped here...");
				p.message("You find a rock sample!");
				addItem(p, ROCK_SAMPLE, 1);
			} else {
				p.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
			}
		}
		if(obj.getID() == SPECIMEN_TRAY) {
			int[] TRAY_ITEMS = { -1, 20, 1150, 28, 1165, 778, 1169, 10, 983 };
			p.incExp(MINING, 4, true);
			message(p, "You sift through the earth in the tray");
			int randomize = DataConversions.random(0, (TRAY_ITEMS.length - 1));
			int chosenItem = TRAY_ITEMS[randomize];
			DigsiteDigAreas.doDigsiteItemMessages(p, chosenItem);
			if(chosenItem != -1) {
				addItem(p, chosenItem, 1);
			}
		}
		if(inArray(obj.getID(), SIGNPOST)) {
			if(obj.getID() == SIGNPOST[0]) {
				p.message("This site is for training purposes only");
			} else if(obj.getID() == SIGNPOST[1]) {
				p.message("Level 1 digs only");
			} else if(obj.getID() == SIGNPOST[2]) {
				p.message("Level 2 digs only");
			} else if(obj.getID() == SIGNPOST[3]) {
				p.message("Level 3 digs only");
			}
		}
		if(inArray(obj.getID(), SACKS)) {
			p.playerServerMessage(MessageType.QUEST, "You search the sacks");
			if(obj.getID() == SACKS[0] || hasItem(p, SPECIMEN_JAR)) {
				p.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
			} else if(obj.getID() == SACKS[1] && !hasItem(p, SPECIMEN_JAR)) {
				playerTalk(p, null, "Hey there's something under here");
				p.message("You find a specimen jar!");
				addItem(p, SPECIMEN_JAR, 1);
			}
		}
		if(inArray(obj.getID(), BURIED_SKELETON)) {
			p.message("You search the skeleton");
			p.message("You find nothing of interest");
		}
		if(obj.getID() == HOUSE_EAST_CHEST_CLOSED) {
			p.message("You open the chest");
			replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_EAST_CHEST_OPEN, obj.getDirection(), obj.getType()));
		}
		if(obj.getID() == HOUSE_EAST_CHEST_OPEN) {
			if(command.equalsIgnoreCase("Search")) {
				p.message("You search the chest");
				p.message("You find a rock sample");
				addItem(p, CRACKED_ROCK_SAMPLE, 1);
				replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_EAST_CHEST_CLOSED, obj.getDirection(), obj.getType()));
			}
		}
		if(obj.getID() == HOUSE_BOOKCASE) {
			p.message("You search through the bookcase");
			p.message("You find a book on chemicals");
			addItem(p, BOOK_OF_EXPERIMENTAL_CHEMISTRY, 1);
		}
		if(obj.getID() == HOUSE_EAST_CUPBOARD_CLOSED) {
			openCupboard(obj, p, 1078);
		}
		if(obj.getID() == HOUSE_EAST_CUPBOARD_OPEN) {
			if(command.equalsIgnoreCase("search")) {
				if(!hasItem(p, ROCK_PICK)) {
					p.message("You find a rock pick");
					addItem(p, ROCK_PICK, 1);
				} else {
					p.message("You find nothing of interest");
				}
				closeCupboard(obj, p, 1074);
			}
		}
		if(obj.getID() == HOUSE_WEST_CHESTS_CLOSED || obj.getID() == HOUSE_WEST_CHESTS_OPEN) {
			if(command.equalsIgnoreCase("Open")) {
				p.message("You open the chest");
				replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_WEST_CHESTS_OPEN, obj.getDirection(), obj.getType()));
			} else if(command.equalsIgnoreCase("Close")) {
				p.message("You close the chest");
				replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_WEST_CHESTS_CLOSED, obj.getDirection(), obj.getType()));
			} else if(command.equalsIgnoreCase("Search")) {
				p.message("You search the chest, but find nothing");
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TENT_LOCKED_CHEST && item.getID() == 1164) {
			return true;
		}
		if(obj.getID() == X_BARREL || obj.getID() == X_BARREL_OPEN) {
			return true;
		}
		if(obj.getID() == BRICK) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(obj.getID() == TENT_LOCKED_CHEST && item.getID() == 1164) {
			replaceObject(obj, new GameObject(obj.getLocation(), TENT_OPEN_CHEST, obj.getDirection(), obj.getType()));
			p.message("you use the key in the chest");
			p.message("you open the chest");
			removeItem(p, 1164, 1);
			playerTalk(p, null, "Oops I dropped the key",
					"Never mind it's open now...");
		}
		if(obj.getID() == X_BARREL) {
			switch(item.getID()) {
			case 156: // bronze pickaxe
				playerTalk(p, null, "I better not - it might break it to pieces!");
				break;
			case 1114: // rock pick
				playerTalk(p, null, "The rockpick is too fat to fit in the gap...");
				break;
			case 211: // spade
				playerTalk(p, null, "The spade is far too big to fit");
				break;
			case 28: // iron dagger
				playerTalk(p, null, "The dagger's blade might break, I need something stronger");
				break;
			case 1165: // broken arrows
				playerTalk(p, null, "It nearly fits, just a little too thin");
				break;
			case 1145: // trowel
				replaceObject(obj, new GameObject(obj.getLocation(), X_BARREL_OPEN, obj.getDirection(), obj.getType()));
				playerTalk(p, null, "Great, it's opened it!");
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
		if(obj.getID() == X_BARREL_OPEN) {
			switch(item.getID()) {
			case 1111: // empty panning tray
				playerTalk(p, null, "Not the best idea i've had...",
						"It's likely to spill everywhere in that!");
				break;
			case 1116: // sample jar
				playerTalk(p, null, "Perhaps not, it might contaminate the samples");
				break;
			case 140: // jug
				playerTalk(p, null, "I had better not, someone might want to drink from this!");
				break;
			case 465: // empty vial
				p.message("You fill the vial with the liquid");
				p.message("You close the barrel");
				p.getInventory().replace(465, 1232);
				replaceObject(obj, new GameObject(obj.getLocation(), X_BARREL, obj.getDirection(), obj.getType()));
				playerTalk(p, null, "I'm not sure what this stuff is",
						"I had better be very careful with it",
						"I had better not spill any I think...");
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
		if(obj.getID() == BRICK) {
			switch(item.getID()) {
			case 1176: // explosive compound
				p.message("You pour the compound over the bricks");
				removeItem(p, 1176, 1);
				playerTalk(p, null, "I need some way to ignite this compound...");
				if(!p.getCache().hasKey("brick_ignite")) {
					p.getCache().store("brick_ignite", true);
				}
				break;
			case 166: // tinder box
				if(p.getCache().hasKey("brick_ignite")) {
					p.message("You strike the tinderbox");
					p.message("Fizz...");
					sleep(300);
					playerTalk(p, null, "Whoa! this is going to blow!",
							"I'd better run!");
					sleep(1500);
					p.teleport(22, 3379);
					p.updateQuestStage(Constants.Quests.DIGSITE, 6);
					p.getCache().remove("brick_ignite");
					message(p, "\"Bang!!!\"");
					playerTalk(p, null, "Wow that was a big explosion!",
							"...What's that noise I can hear ?",
							"...Sounds like bones moving or something");
				} else {
					npcTalk(p, null, "Now what am I trying to achieve here ?");
				}
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
	}
}
