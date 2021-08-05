package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteObjects implements OpLocTrigger, UseLocTrigger{

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
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return DataConversions.inArray(new int[] {HOUSE_EAST_CHEST_OPEN, HOUSE_EAST_CHEST_CLOSED, HOUSE_EAST_CUPBOARD_OPEN, HOUSE_EAST_CUPBOARD_CLOSED,
				HOUSE_WEST_CHESTS_OPEN, HOUSE_WEST_CHESTS_CLOSED, TENT_CHEST_OPEN, TENT_CHEST_LOCKED, HOUSE_BOOKCASE, SPECIMEN_TRAY,
				CLIMB_UP_ROPE_SMALL_CAVE, CLIMB_UP_ROPE_BIG_CAVE, BRICK, X_BARREL_OPEN}, obj.getID()) || inArray(obj.getID(), SIGNPOST)
				|| inArray(obj.getID(), SACKS) || inArray(obj.getID(), BURIED_SKELETON) || inArray(obj.getID(), BUSH);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == X_BARREL_OPEN) {
			player.message("You search the barrel");
			player.message("The barrel has a foul-smelling liquid inside...");
			say(player, null, "I can't pick this up with my bare hands!",
				"I'll need something to put it in");
		}
		else if (obj.getID() == BRICK) {
			say(player, null, "Hmmm, There's a room past these bricks",
				"If I could move them out of the way",
				"Then I could find out what's inside...");
		}
		else if (obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE || obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
			player.message("You climb the ladder");
			if (obj.getID() == CLIMB_UP_ROPE_BIG_CAVE) {
				player.teleport(25, 515);
			} else if (obj.getID() == CLIMB_UP_ROPE_SMALL_CAVE) {
				player.teleport(14, 506);
			}
		}
		else if (obj.getID() == TENT_CHEST_LOCKED) {
			player.message("The chest is locked");
		}
		else if (obj.getID() == TENT_CHEST_OPEN) {
			if (command.equalsIgnoreCase("Search")) {
				mes("You search the chest");
				delay(3);
				player.message("You find some unusual powder inside...");
				give(player, ItemId.UNIDENTIFIED_POWDER.id(), 1);
				player.getWorld().registerGameObject(
						new GameObject(obj.getWorld(), obj.getLocation(), TENT_CHEST_LOCKED, obj.getDirection(),
							obj.getType()));
			}
			//kosher special case - chest does not close on that command, player must search the chest
			else {
				player.message("Nothing interesting happens");
			}
		}
		else if (inArray(obj.getID(), BUSH)) {
			player.message("You search the bush");
			if (obj.getID() == BUSH[1]) {
				say(player, null, "Hey, something has been dropped here...");
				player.message("You find a rock sample!");
				give(player, ItemId.ROCK_SAMPLE_PURPLE.id(), 1);
			} else {
				player.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
			}
		}
		else if (obj.getID() == SPECIMEN_TRAY) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_JAR.id(), Optional.of(false))) {
				Npc workmanCheck = ifnearvisnpc(player, NpcId.WORKMAN.id(), 15);
				if (workmanCheck == null) {
					workmanCheck = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
				}
				npcsay(player, workmanCheck, "Oi! what are you doing ?");
				npcWalkFromPlayer(player, workmanCheck);
				int option = multi(player,
					"I am on an errand",
					"I am searching this tray");
				if (option == 0) {
					npcsay(player, workmanCheck, "Oh yeah? and whose errand is that then...",
						"Where is your specimen jar then?");
					say(player, workmanCheck, "Oh I dont have one");
					npcsay(player, workmanCheck, "And you reckon you have been sent on an errand...",
						"Without a specimen jar - no sorry I can't let you do that!");
				} else if (option == 1) {
					npcsay(player, workmanCheck, "Oh you are, are you ?",
						"Well, where's your specimen jar?");
					say(player, workmanCheck, "Ah, I don't have one...");
					npcsay(player, workmanCheck, "In that case how can you handle the specimens without it?",
						"As you should know, specimens are to be kept in sealed specimen jars",
						"To keep them safe and preserved...",
						"Next time bring it along!");
				}
				return;
			}

			int[] TRAY_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.CRACKED_ROCK_SAMPLE.id(), ItemId.IRON_DAGGER.id(), ItemId.BROKEN_ARROW.id(), ItemId.BROKEN_GLASS.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.COINS.id(), ItemId.A_LUMP_OF_CHARCOAL.id()};
			player.incExp(Skill.MINING.id(), 4, true);
			mes("You sift through the earth in the tray");
			delay(3);
			int randomize = DataConversions.random(0, (TRAY_ITEMS.length - 1));
			int chosenItem = TRAY_ITEMS[randomize];
			DigsiteDigAreas.doDigsiteItemMessages(player, chosenItem);
			if (chosenItem != ItemId.NOTHING.id()) {
				give(player, chosenItem, 1);
			}
		}
		else if (inArray(obj.getID(), SIGNPOST)) {
			if (obj.getID() == SIGNPOST[0]) {
				player.message("This site is for training purposes only");
			} else if (obj.getID() == SIGNPOST[1]) {
				player.message("Level 1 digs only");
			} else if (obj.getID() == SIGNPOST[2]) {
				player.message("Level 2 digs only");
			} else if (obj.getID() == SIGNPOST[3]) {
				player.message("Level 3 digs only");
			}
		}
		else if (inArray(obj.getID(), SACKS)) {
			if (obj.getID() == SACKS[0] && Formulae.getHeight(obj.getLocation()) == 3) {
				say(player, null, "There is nothing under the sack!");
				return;
			}
			player.playerServerMessage(MessageType.QUEST, "You search the sacks");
			if (obj.getID() == SACKS[0] || player.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_JAR.id(), Optional.of(false))) {
				player.playerServerMessage(MessageType.QUEST, "You find nothing of interest");
			} else if (obj.getID() == SACKS[1] && !player.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_JAR.id(), Optional.of(false))) {
				say(player, null, "Hey there's something under here");
				player.message("You find a specimen jar!");
				give(player, ItemId.SPECIMEN_JAR.id(), 1);
			}
		}
		else if (inArray(obj.getID(), BURIED_SKELETON)) {
			player.message("You search the skeleton");
			player.message("You find nothing of interest");
		}
		else if (obj.getID() == HOUSE_EAST_CHEST_CLOSED) {
			player.message("You open the chest");
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), HOUSE_EAST_CHEST_OPEN, obj.getDirection(), obj.getType()));
		}
		else if (obj.getID() == HOUSE_EAST_CHEST_OPEN) {
			if (command.equalsIgnoreCase("Search")) {
				player.message("You search the chest");
				player.message("You find a rock sample");
				give(player, ItemId.CRACKED_ROCK_SAMPLE.id(), 1);
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), HOUSE_EAST_CHEST_CLOSED, obj.getDirection(), obj.getType()));
			}
		}
		else if (obj.getID() == HOUSE_BOOKCASE) {
			player.message("You search through the bookcase");
			player.message("You find a book on chemicals");
			give(player, ItemId.BOOK_OF_EXPERIMENTAL_CHEMISTRY.id(), 1);
		}
		else if (obj.getID() == HOUSE_EAST_CUPBOARD_CLOSED) {
			openCupboard(obj, player, HOUSE_EAST_CUPBOARD_OPEN);
		}
		else if (obj.getID() == HOUSE_EAST_CUPBOARD_OPEN) {
			if (command.equalsIgnoreCase("search")) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.ROCK_PICK.id(), Optional.of(false))) {
					player.message("You find a rock pick");
					give(player, ItemId.ROCK_PICK.id(), 1);
				} else {
					player.message("You find nothing of interest");
				}
				closeCupboard(obj, player, HOUSE_EAST_CUPBOARD_CLOSED);
			}
		}
		else if (obj.getID() == HOUSE_WEST_CHESTS_OPEN || obj.getID() == HOUSE_WEST_CHESTS_CLOSED) {
			if (command.equalsIgnoreCase("Open")) {
				player.message("You open the chest");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), HOUSE_WEST_CHESTS_OPEN, obj.getDirection(), obj.getType()));
			} else if (command.equalsIgnoreCase("Close")) {
				player.message("You close the chest");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), HOUSE_WEST_CHESTS_CLOSED, obj.getDirection(), obj.getType()));
			} else if (command.equalsIgnoreCase("Search")) {
				player.message("You search the chest, but find nothing");
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == TENT_CHEST_LOCKED && item.getCatalogId() == ItemId.DIGSITE_CHEST_KEY.id()) || obj.getID() == X_BARREL
				|| obj.getID() == X_BARREL_OPEN || obj.getID() == BRICK || obj.getID() == SPECIMEN_TRAY
				|| (inArray(obj.getID(), BURIED_SKELETON) && item.getCatalogId() == ItemId.TROWEL.id())
				|| (obj.getID() == BRICK && item.getCatalogId() == ItemId.ROCK_PICK.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == TENT_CHEST_LOCKED && item.getCatalogId() == ItemId.DIGSITE_CHEST_KEY.id()) {
			changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), TENT_CHEST_OPEN, obj.getDirection(), obj.getType()));
			player.message("you use the key in the chest");
			player.message("you open the chest");
			player.getCarriedItems().remove(new Item(ItemId.DIGSITE_CHEST_KEY.id()));
			say(player, null, "Oops I dropped the key",
				"Never mind it's open now...");
		}
		else if (obj.getID() == X_BARREL) {
			switch (ItemId.getById(item.getCatalogId())) {
				case BRONZE_PICKAXE:
					say(player, null, "I better not - it might break it to pieces!");
					break;
				case ROCK_PICK:
					say(player, null, "The rockpick is too fat to fit in the gap...");
					break;
				case SPADE:
					say(player, null, "The spade is far too big to fit");
					break;
				case IRON_DAGGER:
					say(player, null, "The dagger's blade might break, I need something stronger");
					break;
				case BROKEN_ARROW:
					say(player, null, "It nearly fits, just a little too thin");
					break;
				case TROWEL:
					changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), X_BARREL_OPEN, obj.getDirection(), obj.getType()));
					say(player, null, "Great, it's opened it!");
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == X_BARREL_OPEN) {
			switch (ItemId.getById(item.getCatalogId())) {
				case PANNING_TRAY:
					say(player, null, "Not the best idea i've had...",
						"It's likely to spill everywhere in that!");
					break;
				case SPECIMEN_JAR:
					say(player, null, "Perhaps not, it might contaminate the samples");
					break;
				case JUG:
					say(player, null, "I had better not, someone might want to drink from this!");
					break;
				case VASE:
					say(player, null, "I'm not sure it's good for growing flowers!");
					break;
				case EMPTY_VIAL:
					player.message("You fill the vial with the liquid");
					player.message("You close the barrel");
					player.getCarriedItems().remove(new Item(ItemId.EMPTY_VIAL.id()));
					player.getCarriedItems().getInventory().add(new Item(ItemId.UNIDENTIFIED_LIQUID.id()));
					changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), X_BARREL, obj.getDirection(), obj.getType()));
					say(player, null, "I'm not sure what this stuff is",
						"I had better be very careful with it",
						"I had better not spill any I think...");
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == BRICK) {
			switch (ItemId.getById(item.getCatalogId())) {
				case EXPLOSIVE_COMPOUND:
					player.message("You pour the compound over the bricks");
					player.getCarriedItems().remove(new Item(ItemId.EXPLOSIVE_COMPOUND.id()));
					say(player, null, "I need some way to ignite this compound...");
					if (!player.getCache().hasKey("brick_ignite")) {
						player.getCache().store("brick_ignite", true);
					}
					break;
				case TINDERBOX:
					if (player.getCache().hasKey("brick_ignite")) {
						player.message("You strike the tinderbox");
						player.message("Fizz...");
						delay();
						say(player, null, "Whoa! this is going to blow!\"",
							"I'd better run!");
						delay(3);
						player.teleport(22, 3379);
						player.updateQuestStage(Quests.DIGSITE, 6);
						player.getCache().remove("brick_ignite");
						mes("\"Bang!!!\"");
						delay(3);
						say(player, null, "Wow that was a big explosion!",
							"...What's that noise I can hear ?",
							"...Sounds like bones moving or something");
					} else {
						say(player, null, "Now what am I trying to achieve here ?");
					}
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		else if (obj.getID() == SPECIMEN_TRAY) {
			switch (ItemId.getById(item.getCatalogId())) {
				case TROWEL:
				case SPADE:
					Npc workmanCheck = ifnearvisnpc(player, NpcId.WORKMAN.id(), 15);
					if (workmanCheck == null) {
						workmanCheck = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
					}
					if (item.getCatalogId() == ItemId.TROWEL.id()) {
						npcsay(player, workmanCheck, "Excuse me...",
							"No digging in the specimen trays please");
					} else if (item.getCatalogId() == ItemId.SPADE.id()) {
						npcsay(player, workmanCheck, "Oi! what do you think you are doing ?",
							"Don't you realize there are fragile specimens around here ?");
					}
					break;
				case SPECIMEN_JAR:
					// behavior unconfirmed on replay - believed to have similar as rs2+
					say(player, null, "I'm not sure if this will be useful or not");
					mes("You scoop some earth with the jar");
					delay(3);
					break;
				case ROCK_PICK:
				case SPECIMEN_BRUSH:
				default:
					player.message("Nothing interesting happens");
					break;
			}
		} else if (inArray(obj.getID(), BURIED_SKELETON) && item.getCatalogId() == ItemId.TROWEL.id()) {
			Npc workmanCheck = ifnearvisnpc(player, NpcId.WORKMAN.id(), 15);
			if (workmanCheck == null) {
				workmanCheck = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
			}
			npcsay(player, workmanCheck, "Hey! that's fragile!",
				"Stop poking it around with that trowel!");
			say(player, workmanCheck, "Oh okay, sorry");
		} else if (obj.getID() == BRICK && item.getCatalogId() == ItemId.ROCK_PICK.id()) {
			say(player, null, "That would be like cutting the lawn with nail scissors!",
				"It would take a year to chip away these rocks...");
		}
	}
}
