package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.Constants;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.Skills.SKILLS;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.closeCupboard;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.openCupboard;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.replaceObject;
import static com.openrsc.server.plugins.Functions.sleep;

public class DigsiteObjects implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener {

	private static final int[] SIGNPOST = {1060, 1061, 1062, 1063};
	/* Objects */
	private static final int HOUSE_EAST_CHEST_OPEN = 1105;
	private static final int HOUSE_EAST_CHEST_CLOSED = 1104;
	private static final int HOUSE_EAST_CUPBOARD_OPEN = 1078;
	private static final int HOUSE_EAST_CUPBOARD_CLOSED = 1074;
	private static final int HOUSE_WEST_CHESTS_OPEN = 17;
	private static final int HOUSE_WEST_CHESTS_CLOSED = 18;
	private static final int TENT_CHEST_OPEN = 1084;
	private static final int TENT_CHEST_LOCKED = 1085;
	private static final int HOUSE_BOOKCASE = 1090;
	private static final int[] SACKS = {1075, 1076};
	private static final int[] BUSH = {1072, 1073};

	private static final int[] BURIED_SKELETON = {1057, 1049};

	private static final int SPECIMEN_TRAY = 1052;

	private static final int CLIMB_UP_ROPE_SMALL_CAVE = 1097;
	private static final int CLIMB_UP_ROPE_BIG_CAVE = 1098;

	private static final int BRICK = 1096;
	private static final int X_BARREL = 1082;
	private static final int X_BARREL_OPEN = 1083;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return DataConversions.inArray(new int[] {HOUSE_EAST_CHEST_OPEN, HOUSE_EAST_CHEST_CLOSED, HOUSE_EAST_CUPBOARD_OPEN, HOUSE_EAST_CUPBOARD_CLOSED,
				HOUSE_WEST_CHESTS_OPEN, HOUSE_WEST_CHESTS_CLOSED, TENT_CHEST_OPEN, TENT_CHEST_LOCKED, HOUSE_BOOKCASE, SPECIMEN_TRAY,
				CLIMB_UP_ROPE_SMALL_CAVE, CLIMB_UP_ROPE_BIG_CAVE, BRICK, X_BARREL_OPEN}, obj.getID()) || inArray(obj.getID(), SIGNPOST)
				|| inArray(obj.getID(), SACKS) || inArray(obj.getID(), BURIED_SKELETON) || inArray(obj.getID(), BUSH);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == X_BARREL_OPEN) {
			p.message("You search the barrel");
			p.message("The barrel has a foul-smelling liquid inside...");
			playerTalk(p, null, "I can't pick this up with my bare hands!",
				"I'll need something to put it in");
		}
		else if (obj.getID() == BRICK) {
			playerTalk(p, null, "Hmmm, There's a room past these bricks",
				"If I could move them out of the way",
				"Then I could find out what's inside...");
		}
		else if (obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE || obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
			p.message("You climb the ladder");
			if (obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
				p.teleport(25, 515);
			} else if (obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE) {
				p.teleport(14, 506);
			}
		}
		else if (obj.getID() == TENT_CHEST_LOCKED) {
			p.message("The chest is locked");
		}
		else if (obj.getID() == TENT_CHEST_OPEN) {
			if (command.equalsIgnoreCase("Search")) {
				message(p, "You search the chest");
				p.message("You find some unusual powder inside...");
				addItem(p, ItemId.UNIDENTIFIED_POWDER.id(), 1);
				World.getWorld().registerGameObject(
						new GameObject(obj.getLocation(), TENT_CHEST_LOCKED, obj.getDirection(),
							obj.getType()));
			}
			//kosher special case - chest does not close on that command, player must search the chest
			else {
				p.message("Nothing interesting happens");
			}
		}
		else if (inArray(obj.getID(), BUSH)) {
			p.message("You search the bush");
			if (obj.getID() == BUSH[1]) {
				playerTalk(p, null, "Hey, something has been dropped here...");
				p.message("You find a rock sample!");
				addItem(p, ItemId.ROCK_SAMPLE_PURPLE.id(), 1);
			} else {
				p.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
			}
		}
		else if (obj.getID() == SPECIMEN_TRAY) {
			int[] TRAY_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.CRACKED_ROCK_SAMPLE.id(), ItemId.IRON_DAGGER.id(), ItemId.BROKEN_ARROW.id(), ItemId.BROKEN_GLASS.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.COINS.id(), ItemId.A_LUMP_OF_CHARCOAL.id()};
			p.incExp(SKILLS.MINING.id(), 4, true);
			message(p, "You sift through the earth in the tray");
			int randomize = DataConversions.random(0, (TRAY_ITEMS.length - 1));
			int chosenItem = TRAY_ITEMS[randomize];
			DigsiteDigAreas.doDigsiteItemMessages(p, chosenItem);
			if (chosenItem != ItemId.NOTHING.id()) {
				addItem(p, chosenItem, 1);
			}
		}
		else if (inArray(obj.getID(), SIGNPOST)) {
			if (obj.getID() == SIGNPOST[0]) {
				p.message("This site is for training purposes only");
			} else if (obj.getID() == SIGNPOST[1]) {
				p.message("Level 1 digs only");
			} else if (obj.getID() == SIGNPOST[2]) {
				p.message("Level 2 digs only");
			} else if (obj.getID() == SIGNPOST[3]) {
				p.message("Level 3 digs only");
			}
		}
		else if (inArray(obj.getID(), SACKS)) {
			p.playerServerMessage(MessageType.QUEST, "You search the sacks");
			if (obj.getID() == SACKS[0] || hasItem(p, ItemId.SPECIMEN_JAR.id())) {
				p.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
			} else if (obj.getID() == SACKS[1] && !hasItem(p, ItemId.SPECIMEN_JAR.id())) {
				playerTalk(p, null, "Hey there's something under here");
				p.message("You find a specimen jar!");
				addItem(p, ItemId.SPECIMEN_JAR.id(), 1);
			}
		}
		else if (inArray(obj.getID(), BURIED_SKELETON)) {
			p.message("You search the skeleton");
			p.message("You find nothing of interest");
		}
		else if (obj.getID() == HOUSE_EAST_CHEST_CLOSED) {
			p.message("You open the chest");
			replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_EAST_CHEST_OPEN, obj.getDirection(), obj.getType()));
		}
		else if (obj.getID() == HOUSE_EAST_CHEST_OPEN) {
			if (command.equalsIgnoreCase("Search")) {
				p.message("You search the chest");
				p.message("You find a rock sample");
				addItem(p, ItemId.CRACKED_ROCK_SAMPLE.id(), 1);
				replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_EAST_CHEST_CLOSED, obj.getDirection(), obj.getType()));
			}
		}
		else if (obj.getID() == HOUSE_BOOKCASE) {
			p.message("You search through the bookcase");
			p.message("You find a book on chemicals");
			addItem(p, ItemId.BOOK_OF_EXPERIMENTAL_CHEMISTRY.id(), 1);
		}
		else if (obj.getID() == HOUSE_EAST_CUPBOARD_CLOSED) {
			openCupboard(obj, p, HOUSE_EAST_CUPBOARD_OPEN);
		}
		else if (obj.getID() == HOUSE_EAST_CUPBOARD_OPEN) {
			if (command.equalsIgnoreCase("search")) {
				if (!hasItem(p, ItemId.ROCK_PICK.id())) {
					p.message("You find a rock pick");
					addItem(p, ItemId.ROCK_PICK.id(), 1);
				} else {
					p.message("You find nothing of interest");
				}
				closeCupboard(obj, p, HOUSE_EAST_CUPBOARD_CLOSED);
			}
		}
		else if (obj.getID() == HOUSE_WEST_CHESTS_OPEN || obj.getID() == HOUSE_WEST_CHESTS_CLOSED) {
			if (command.equalsIgnoreCase("Open")) {
				p.message("You open the chest");
				replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_WEST_CHESTS_OPEN, obj.getDirection(), obj.getType()));
			} else if (command.equalsIgnoreCase("Close")) {
				p.message("You close the chest");
				replaceObject(obj, new GameObject(obj.getLocation(), HOUSE_WEST_CHESTS_CLOSED, obj.getDirection(), obj.getType()));
			} else if (command.equalsIgnoreCase("Search")) {
				p.message("You search the chest, but find nothing");
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		return (obj.getID() == TENT_CHEST_LOCKED && item.getID() == ItemId.DIGSITE_CHEST_KEY.id()) || obj.getID() == X_BARREL
				|| obj.getID() == X_BARREL_OPEN || obj.getID() == BRICK;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == TENT_CHEST_LOCKED && item.getID() == ItemId.DIGSITE_CHEST_KEY.id()) {
			replaceObject(obj, new GameObject(obj.getLocation(), TENT_CHEST_OPEN, obj.getDirection(), obj.getType()));
			p.message("you use the key in the chest");
			p.message("you open the chest");
			removeItem(p, ItemId.DIGSITE_CHEST_KEY.id(), 1);
			playerTalk(p, null, "Oops I dropped the key",
				"Never mind it's open now...");
		}
		else if (obj.getID() == X_BARREL) {
			switch (ItemId.getById(item.getID())) {
				case BRONZE_PICKAXE:
					playerTalk(p, null, "I better not - it might break it to pieces!");
					break;
				case ROCK_PICK:
					playerTalk(p, null, "The rockpick is too fat to fit in the gap...");
					break;
				case SPADE:
					playerTalk(p, null, "The spade is far too big to fit");
					break;
				case IRON_DAGGER:
					playerTalk(p, null, "The dagger's blade might break, I need something stronger");
					break;
				case BROKEN_ARROW:
					playerTalk(p, null, "It nearly fits, just a little too thin");
					break;
				case TROWEL:
					replaceObject(obj, new GameObject(obj.getLocation(), X_BARREL_OPEN, obj.getDirection(), obj.getType()));
					playerTalk(p, null, "Great, it's opened it!");
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == X_BARREL_OPEN) {
			switch (ItemId.getById(item.getID())) {
				case PANNING_TRAY:
					playerTalk(p, null, "Not the best idea i've had...",
						"It's likely to spill everywhere in that!");
					break;
				case SPECIMEN_JAR:
					playerTalk(p, null, "Perhaps not, it might contaminate the samples");
					break;
				case JUG:
					playerTalk(p, null, "I had better not, someone might want to drink from this!");
					break;
				case EMPTY_VIAL:
					p.message("You fill the vial with the liquid");
					p.message("You close the barrel");
					p.getInventory().replace(ItemId.EMPTY_VIAL.id(), ItemId.UNIDENTIFIED_LIQUID.id());
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
		else if (obj.getID() == BRICK) {
			switch (ItemId.getById(item.getID())) {
				case EXPLOSIVE_COMPOUND:
					p.message("You pour the compound over the bricks");
					removeItem(p, ItemId.EXPLOSIVE_COMPOUND.id(), 1);
					playerTalk(p, null, "I need some way to ignite this compound...");
					if (!p.getCache().hasKey("brick_ignite")) {
						p.getCache().store("brick_ignite", true);
					}
					break;
				case TINDERBOX:
					if (p.getCache().hasKey("brick_ignite")) {
						p.message("You strike the tinderbox");
						p.message("Fizz...");
						sleep(300);
						playerTalk(p, null, "Whoa! this is going to blow!\"",
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
						playerTalk(p, null, "Now what am I trying to achieve here ?");
					}
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
	}
}
