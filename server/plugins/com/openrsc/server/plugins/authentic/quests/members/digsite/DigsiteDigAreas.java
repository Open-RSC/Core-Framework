package com.openrsc.server.plugins.authentic.quests.members.digsite;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class DigsiteDigAreas implements OpLocTrigger, UseLocTrigger, OpInvTrigger {

	private static int[] SOIL = {1065, 1066, 1067};
	private static int ROCK = 1059;

	private int[] TRAINING_AREA_ITEMS = {ItemId.NOTHING.id(), ItemId.NOTHING_INTEREST.id(), ItemId.VASE.id(), ItemId.BROKEN_ARROW.id(), ItemId.COINS.id(), ItemId.CRACKED_ROCK_SAMPLE.id(), ItemId.A_LUMP_OF_CHARCOAL.id()};

	private int[] DIGSITE_LEVEL1_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.OPAL.id(), ItemId.OLD_BOOT.id(), ItemId.OLD_TOOTH.id(), ItemId.BROKEN_GLASS.id(), ItemId.COPPER_ORE.id(), ItemId.ROTTEN_APPLES.id(), ItemId.BUTTONS.id(), ItemId.RUSTY_SWORD.id(), ItemId.VASE.id()};

	private int[] DIGSITE_LEVEL2_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.PURPLEDYE.id(), ItemId.POT.id(), ItemId.CLAY.id(), ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id(), ItemId.RATS_TAIL.id(), ItemId.BROKEN_STAFF.id(), ItemId.DAMAGED_ARMOUR_2.id(), ItemId.JUG.id(), ItemId.OLD_BOOT.id()};

	private int[] DIGSITE_LEVEL3_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.DAMAGED_ARMOUR_1.id(), ItemId.BROKEN_STAFF.id(), ItemId.TALISMAN_OF_ZAROS.id(), ItemId.BROKEN_ARROW.id(), ItemId.BRONZE_SPEAR.id(), ItemId.PIE_DISH.id(), ItemId.BUTTONS.id(), ItemId.OLD_TOOTH.id(), ItemId.COINS.id(), ItemId.NEEDLE.id(), ItemId.CLAY.id(), ItemId.IRON_THROWING_KNIFE.id(), ItemId.MEDIUM_BLACK_HELMET.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.BELT_BUCKLE.id(), ItemId.OLD_BOOT.id(), ItemId.PURPLEDYE.id()};

	private static boolean getLevel3Digsite(Player player) {
		// Top North DONE. + WEST MIDDLE AREA DONE
		return player.getLocation().inBounds(10, 495, 14, 499) || player.getLocation().inBounds(23, 518, 28, 524);
	}

	static void doDigsiteItemMessages(Player player, int item) {
		if (item == ItemId.NOTHING.id()) {
			player.message("You find nothing");
		} else if (item == ItemId.NOTHING_INTEREST.id()) {
			player.message("You find nothing of interest");
		} else if (item == ItemId.BONES.id()) {
			player.message("You find some bones");
		} else if (item == ItemId.PURPLEDYE.id()) {
			player.message("You find some purple dye");
		} else if (item == ItemId.POT.id()) {
			player.message("You find an old pot");
		} else if (item == ItemId.CLAY.id()) {
			player.message("You find some clay");
		} else if (item == ItemId.BROKEN_GLASS.id() || item == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id()) {
			player.message("You find some broken glass");
		} else if (item == ItemId.RATS_TAIL.id()) {
			player.message("You find a rat's tail");
		} else if (item == ItemId.BROKEN_STAFF.id()) {
			player.message("You find a broken staff");
		} else if (item == ItemId.DAMAGED_ARMOUR_1.id() || item == ItemId.DAMAGED_ARMOUR_2.id()) {
			player.message("You find some old armour");
		} else if (item == ItemId.JUG.id()) {
			player.message("You find an old jug");
		} else if (item == ItemId.OLD_BOOT.id()) {
			player.message("You find an old boot");
		} else if (item == ItemId.VASE.id()) {
			player.message("You find an old vase");
		} else if (item == ItemId.COINS.id()) {
			if (getLevel3Digsite(player)) {
				player.message("You find some coins");
			} else {
				player.message("You find a coin");
			}
		} else if (item == ItemId.CRACKED_ROCK_SAMPLE.id()) {
			player.message("You find a broken rock sample");
		} else if (item == ItemId.A_LUMP_OF_CHARCOAL.id()) {
			player.message("You find some charcoal");
		} else if (item == ItemId.BROKEN_ARROW.id()) {
			player.message("You find a broken arrow");
		} else if (item == ItemId.OPAL.id()) {
			player.message("You find an opal");
		} else if (item == ItemId.OLD_TOOTH.id()) {
			player.message("You find an old tooth");
		} else if (item == ItemId.COPPER_ORE.id()) {
			player.message("You find some copper ore");
		} else if (item == ItemId.ROTTEN_APPLES.id()) {
			player.message("You find a rotten apple");
		} else if (item == ItemId.BUTTONS.id()) {
			player.message("You find some buttons");
		} else if (item == ItemId.RUSTY_SWORD.id()) {
			player.message("You find a rusty sword");
		} else if (item == ItemId.TALISMAN_OF_ZAROS.id()) {
			player.message("You find a strange talisman");
		} else if (item == ItemId.BRONZE_SPEAR.id()) {
			player.message("You find a bronze spear");
		} else if (item == ItemId.PIE_DISH.id()) {
			player.message("You find a pie dish");
		} else if (item == ItemId.NEEDLE.id()) {
			player.message("You find a needle");
		} else if (item == ItemId.IRON_THROWING_KNIFE.id()) {
			player.message("You find a throwing knife");
		} else if (item == ItemId.MEDIUM_BLACK_HELMET.id()) {
			player.message("You find an black helmet");
		} else if (item == ItemId.CERAMIC_REMAINS.id()) {
			player.message("You find some old pottery");
		} else if (item == ItemId.BELT_BUCKLE.id()) {
			player.message("You find a belt buckle");
		} else if (item == ItemId.IRON_DAGGER.id()) {
			player.message("You find a dagger");
		}
	}

	private boolean getTrainingAreas(Player player) {
		// EAST DONE + WEST DONE
		return player.getLocation().inBounds(13, 526, 17, 529) || player.getLocation().inBounds(24, 526, 27, 529);
	}

	private boolean getLevel2Digsite(Player player) {
		// WEST MIDDLE SMALL WINCH AREA DONE + EAST NORTH DONE + WEST NORTH DONE
		return player.getLocation().inBounds(24, 514, 26, 516) || player.getLocation().inBounds(14, 506, 15, 509) || player.getLocation().inBounds(20, 505, 27, 509);
	}

	private boolean getLevel1Digsite(Player player) {
		// MIDDLE DONE + EAST MIDDLE DONE
		return player.getLocation().inBounds(19, 516, 21, 526) || player.getLocation().inBounds(13, 516, 17, 524);
	}

	private boolean getDigsite(Player player) {
		return player.getLocation().inBounds(5, 492, 42, 545); // ENTIRE DIGSITE AREA used for spade
	}

	private void doSpade(Player player, Item item, GameObject obj) {
		Npc workmanCheck = ifnearvisnpc(player, NpcId.WORKMAN.id(), 15);
		if (workmanCheck != null) {
			Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
			if (item.getCatalogId() == ItemId.SPADE.id() && item.getDef(player.getWorld()).getCommand() != null
				&& item.getDef(player.getWorld()).getCommand()[0].equalsIgnoreCase("Dig") && obj == null) {
				if (workman != null) {
					npcsay(player, workman, "Oi! what do you think you are doing ?");
					npcWalkFromPlayer(player, workman);
					npcsay(player, workman, "Don't you realize there are fragile specimens around here ?");
					workman.remove();
				}
			} else if (item.getCatalogId() == ItemId.SPADE.id() && inArray(obj.getID(), SOIL) && obj != null) {
				if (workman != null) {
					npcsay(player, workman, "Oi! dont use that spade!");
					npcWalkFromPlayer(player, workman);
					npcsay(player, workman, "What are you trying to do, destroy everything of value ?");
					workman.remove();
				}
			}
		}
	}

	private void rockPickOnSite(Player player, Item item, GameObject obj) {
		if (item.getCatalogId() == ItemId.ROCK_PICK.id() && inArray(obj.getID(), SOIL)) {
			if (!getLevel2Digsite(player)) {
				Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
				if (workman != null) {
					npcsay(player, workman, "No no, rockpicks should only be used");
					npcWalkFromPlayer(player, workman);
					npcsay(player, workman, "To dig in a level 2 site...");
					workman.remove();
				}
				return;
			}
			if (player.getQuestStage(Quests.DIGSITE) < 4 && getLevel2Digsite(player)) {
				Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
				if (workman != null) {
					npcsay(player, workman, "Sorry, you haven't passed level 2 earth sciences exam");
					npcWalkFromPlayer(player, workman);
					npcsay(player, workman, "I can't let you dig here");
					workman.remove();
				}
				return;
			}

			if (player.getQuestStage(Quests.DIGSITE) >= 4 && getLevel2Digsite(player)) {
				if (config().WANT_FATIGUE) {
					if (config().STOP_SKILLING_FATIGUED >= 1
						&& player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to do any more digging");
						return;
					}
				}
				thinkbubble(new Item(ItemId.ROCK_PICK.id()));
				player.incExp(Skill.MINING.id(), 70, true);
				mes("You dig through the earth");
				delay(3);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL2_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL2_ITEMS[randomize];
				doDigsiteItemMessages(player, selectedItem);
				if (selectedItem != -1) {
					give(player, selectedItem, 1);
				}
			}
		}
	}

	private void trowelOnSite(Player player, Item item, GameObject obj) {
		if (item.getCatalogId() == ItemId.TROWEL.id() && inArray(obj.getID(), SOIL)) {
			if (getTrainingAreas(player)) {
				thinkbubble(new Item(ItemId.TROWEL.id()));
				player.incExp(Skill.MINING.id(), 50, true);
				mes("You dig with the trowel...");
				delay(3);
				int randomize = DataConversions.random(0, (TRAINING_AREA_ITEMS.length - 1));
				int selectedItem = TRAINING_AREA_ITEMS[randomize];
				doDigsiteItemMessages(player, selectedItem);
				if (selectedItem != -1 || selectedItem != -2) {
					give(player, selectedItem, 1);
				}
			}
			if (getLevel1Digsite(player)) {
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_GLOVES.id())
					&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())
					&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())
					&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.STEEL_GAUNTLETS.id())
					&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_CHAOS.id())
					&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_COOKING.id())
					&& !player.getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_GOLDSMITHING.id())) {
					Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
					if (workman != null) {
						npcsay(player, workman, "Hey, where are your gloves ?");
						npcWalkFromPlayer(player, workman);
						say(player, workman, "Err...I haven't got any");
						npcsay(player, workman, "Well get some and put them on first!");
						workman.remove();
					}
					return;
				}
				if (!player.getCarriedItems().getEquipment().hasEquipped(ItemId.BOOTS.id())) {
					Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
					if (workman != null) {
						npcsay(player, workman, "Oi, no boots!");
						npcWalkFromPlayer(player, workman);
						npcsay(player, workman, "No boots no digging!");
						workman.remove();
					}
					return;
				}
				if (config().WANT_FATIGUE) {
					if (config().STOP_SKILLING_FATIGUED >= 1
						&& player.getFatigue() >= player.MAX_FATIGUE) {
						player.message("You are too tired to do any more digging");
						return;
					}
				}
				thinkbubble(new Item(ItemId.TROWEL.id()));
				player.incExp(Skill.MINING.id(), 60, true);
				mes("You dig through the earth");
				delay(3);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL1_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL1_ITEMS[randomize];
				doDigsiteItemMessages(player, selectedItem);
				if (selectedItem != -1) {
					give(player, selectedItem, 1);
				}
			}
			if (getLevel2Digsite(player)) {
				Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
				if (workman != null) {
					npcsay(player, workman, "Sorry, you must use a rockpick");
					npcWalkFromPlayer(player, workman);
					npcsay(player, workman, "To dig in a level 2 site...");
					workman.remove();
				} else {
					player.message("No rockpicks should only be used in a level 2 site...");
				}
			}
			if (getLevel3Digsite(player)) {
				if (!player.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_JAR.id(), Optional.of(false))) { // HAS SPECIMEN JAR
					Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
					if (workman != null) {
						npcsay(player, workman, "Ahem! I don't see your sample jar");
						npcWalkFromPlayer(player, workman);
						npcsay(player, workman, "You must carry one to be able to dig here...");
						say(player, workman, "Oh, okay");
						workman.remove();
					} else {
						player.message("You need a sample jar to dig here");
					}
					return;
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_BRUSH.id(), Optional.of(false))) { // HAS SPECIMEN BRUSH
					Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
					if (workman != null) {
						npcsay(player, workman, "Wait just a minute!");
						npcWalkFromPlayer(player, workman);
						npcsay(player, workman, "I can't let you dig here",
							"Unless you have a specimen brush with you",
							"Rules is rules!");
						workman.remove();
					} else {
						player.message("you can't dig here unless you have a specimen brush with you");
					}
					return;
				}
				if (player.getQuestStage(Quests.DIGSITE) < 5) {
					Npc workman = addnpc(player.getWorld(), NpcId.WORKMAN.id(), player.getX(), player.getY(), 30000);
					if (workman != null) {
						npcsay(player, workman, "Sorry, you haven't passed level 3 earth sciences exam");
						npcWalkFromPlayer(player, workman);
						npcsay(player, workman, "I can't let you dig here");
						workman.remove();
					}
					return;
				}
				thinkbubble(new Item(ItemId.TROWEL.id()));
				player.incExp(Skill.MINING.id(), 80, true);
				mes("You dig through the earth");
				delay(3);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL3_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL3_ITEMS[randomize];
				doDigsiteItemMessages(player, selectedItem);
				if (selectedItem != -1) {
					if (selectedItem == ItemId.COINS.id()) {
						give(player, ItemId.COINS.id(), (DataConversions.random(0, 1) == 1 ? 5 : 10));
					} else {
						give(player, selectedItem, 1);
					}
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return inArray(obj.getID(), SOIL) || (obj.getID() == ROCK && item.getCatalogId() == ItemId.ROCK_PICK.id());
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (inArray(obj.getID(), SOIL)) {
			switch (ItemId.getById(item.getCatalogId())) {
				case TROWEL:
					trowelOnSite(player, item, obj);
					break;
				case SPADE:
					doSpade(player, item, obj);
					break;
				case ROCK_PICK:
					rockPickOnSite(player, item, obj);
					break;
				case PANNING_TRAY:
					say(player, null, "No I'd better not - it may damage the tray...");
					break;
				default:
					player.message("Nothing interesting happens");
					break;
			}
		}
		if (obj.getID() == ROCK && item.getCatalogId() == ItemId.ROCK_PICK.id()) {
			player.message("You chip at the rock with the rockpick");
			player.message("You take the pieces of cracked rock");
			give(player, ItemId.CRACKED_ROCK_SAMPLE.id(), 1);
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), SOIL);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), SOIL)) {
			player.playerServerMessage(MessageType.QUEST, "You examine the patch of soil");
			player.message("You see nothing on the surface");
			say(player, null, "I think I need something to dig with");
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.SPADE.id() && getDigsite(player);
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (item.getCatalogId() == ItemId.SPADE.id() && getDigsite(player)) {
			doSpade(player, item, null);
		}
	}
}
