package com.openrsc.server.plugins.quests.members.digsite;

import com.openrsc.server.constants.*;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpInvTrigger;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.give;
import static com.openrsc.server.plugins.Functions.ifnearvisnpc;
import static com.openrsc.server.plugins.Functions.ifheld;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.mes;
import static com.openrsc.server.plugins.Functions.npcsay;
import static com.openrsc.server.plugins.Functions.npcWalkFromPlayer;
import static com.openrsc.server.plugins.Functions.say;
import static com.openrsc.server.plugins.Functions.thinkbubble;
import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.addnpc;

/**
 * @author Imposter/Fate
 */
public class DigsiteDigAreas implements OpLocTrigger, UseLocTrigger, OpInvTrigger {

	private static int[] SOIL = {1065, 1066, 1067};
	private static int ROCK = 1059;

	private int[] TRAINING_AREA_ITEMS = {ItemId.NOTHING.id(), ItemId.NOTHING_INTEREST.id(), ItemId.VASE.id(), ItemId.BROKEN_ARROW.id(), ItemId.COINS.id(), ItemId.CRACKED_ROCK_SAMPLE.id(), ItemId.A_LUMP_OF_CHARCOAL.id()};

	private int[] DIGSITE_LEVEL1_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.OPAL.id(), ItemId.OLD_BOOT.id(), ItemId.OLD_TOOTH.id(), ItemId.BROKEN_GLASS.id(), ItemId.COPPER_ORE.id(), ItemId.ROTTEN_APPLES.id(), ItemId.BUTTONS.id(), ItemId.RUSTY_SWORD.id(), ItemId.VASE.id()};

	private int[] DIGSITE_LEVEL2_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.PURPLEDYE.id(), ItemId.POT.id(), ItemId.CLAY.id(), ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id(), ItemId.RATS_TAIL.id(), ItemId.BROKEN_STAFF.id(), ItemId.DAMAGED_ARMOUR_2.id(), ItemId.JUG.id(), ItemId.OLD_BOOT.id()};

	private int[] DIGSITE_LEVEL3_ITEMS = {ItemId.NOTHING.id(), ItemId.BONES.id(), ItemId.DAMAGED_ARMOUR_1.id(), ItemId.BROKEN_STAFF.id(), ItemId.TALISMAN_OF_ZAROS.id(), ItemId.BROKEN_ARROW.id(), ItemId.BRONZE_SPEAR.id(), ItemId.PIE_DISH.id(), ItemId.BUTTONS.id(), ItemId.OLD_TOOTH.id(), ItemId.COINS.id(), ItemId.NEEDLE.id(), ItemId.CLAY.id(), ItemId.IRON_THROWING_KNIFE.id(), ItemId.MEDIUM_BLACK_HELMET.id(), ItemId.CERAMIC_REMAINS.id(), ItemId.BELT_BUCKLE.id(), ItemId.OLD_BOOT.id(), ItemId.PURPLEDYE.id()};

	private static boolean getLevel3Digsite(Player p) {
		// Top North DONE. + WEST MIDDLE AREA DONE
		return p.getLocation().inBounds(10, 495, 14, 499) || p.getLocation().inBounds(23, 518, 28, 524);
	}

	static void doDigsiteItemMessages(Player p, int item) {
		if (item == ItemId.NOTHING.id()) {
			p.message("You find nothing");
		} else if (item == ItemId.NOTHING_INTEREST.id()) {
			p.message("You find nothing of interest");
		} else if (item == ItemId.BONES.id()) {
			p.message("You find some bones");
		} else if (item == ItemId.PURPLEDYE.id()) {
			p.message("You find some purple dye");
		} else if (item == ItemId.POT.id()) {
			p.message("You find an old pot");
		} else if (item == ItemId.CLAY.id()) {
			p.message("You find some clay");
		} else if (item == ItemId.BROKEN_GLASS.id() || item == ItemId.BROKEN_GLASS_DIGSITE_LVL_2.id()) {
			p.message("You find some broken glass");
		} else if (item == ItemId.RATS_TAIL.id()) {
			p.message("You find a rat's tail");
		} else if (item == ItemId.BROKEN_STAFF.id()) {
			p.message("You find a broken staff");
		} else if (item == ItemId.DAMAGED_ARMOUR_1.id() || item == ItemId.DAMAGED_ARMOUR_2.id()) {
			p.message("You find some old armour");
		} else if (item == ItemId.JUG.id()) {
			p.message("You find an old jug");
		} else if (item == ItemId.OLD_BOOT.id()) {
			p.message("You find an old boot");
		} else if (item == ItemId.VASE.id()) {
			p.message("You find an old vase");
		} else if (item == ItemId.COINS.id()) {
			if (getLevel3Digsite(p)) {
				p.message("You find some coins");
			} else {
				p.message("You find a coin");
			}
		} else if (item == ItemId.CRACKED_ROCK_SAMPLE.id()) {
			p.message("You find a broken rock sample");
		} else if (item == ItemId.A_LUMP_OF_CHARCOAL.id()) {
			p.message("You find some charcoal");
		} else if (item == ItemId.BROKEN_ARROW.id()) {
			p.message("You find a broken arrow");
		} else if (item == ItemId.OPAL.id()) {
			p.message("You find an opal");
		} else if (item == ItemId.OLD_TOOTH.id()) {
			p.message("You find an old tooth");
		} else if (item == ItemId.COPPER_ORE.id()) {
			p.message("You find some copper ore");
		} else if (item == ItemId.ROTTEN_APPLES.id()) {
			p.message("You find a rotten apple");
		} else if (item == ItemId.BUTTONS.id()) {
			p.message("You find some buttons");
		} else if (item == ItemId.RUSTY_SWORD.id()) {
			p.message("You find a rusty sword");
		} else if (item == ItemId.TALISMAN_OF_ZAROS.id()) {
			p.message("You find a strange talisman");
		} else if (item == ItemId.BRONZE_SPEAR.id()) {
			p.message("You find a bronze spear");
		} else if (item == ItemId.PIE_DISH.id()) {
			p.message("You find a pie dish");
		} else if (item == ItemId.NEEDLE.id()) {
			p.message("You find a needle");
		} else if (item == ItemId.IRON_THROWING_KNIFE.id()) {
			p.message("You find a throwing knife");
		} else if (item == ItemId.MEDIUM_BLACK_HELMET.id()) {
			p.message("You find an black helmet");
		} else if (item == ItemId.CERAMIC_REMAINS.id()) {
			p.message("You find some old pottery");
		} else if (item == ItemId.BELT_BUCKLE.id()) {
			p.message("You find a belt buckle");
		} else if (item == ItemId.IRON_DAGGER.id()) {
			p.message("You find a dagger");
		}
	}

	private boolean getTrainingAreas(Player p) {
		// EAST DONE + WEST DONE
		return p.getLocation().inBounds(13, 526, 17, 529) || p.getLocation().inBounds(24, 526, 27, 529);
	}

	private boolean getLevel2Digsite(Player p) {
		// WEST MIDDLE SMALL WINCH AREA DONE + EAST NORTH DONE + WEST NORTH DONE
		return p.getLocation().inBounds(24, 514, 26, 516) || p.getLocation().inBounds(14, 506, 15, 509) || p.getLocation().inBounds(20, 505, 27, 509);
	}

	private boolean getLevel1Digsite(Player p) {
		// MIDDLE DONE + EAST MIDDLE DONE
		return p.getLocation().inBounds(19, 516, 21, 526) || p.getLocation().inBounds(13, 516, 17, 524);
	}

	private boolean getDigsite(Player p) {
		return p.getLocation().inBounds(5, 492, 42, 545); // ENTIRE DIGSITE AREA used for spade
	}

	private void doSpade(Player p, Item item, GameObject obj) {
		Npc workmanCheck = ifnearvisnpc(p, NpcId.WORKMAN.id(), 15);
		if (workmanCheck != null) {
			Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
			if (item.getCatalogId() == ItemId.SPADE.id() && item.getDef(p.getWorld()).getCommand() != null
				&& item.getDef(p.getWorld()).getCommand()[0].equalsIgnoreCase("Dig") && obj == null) {
				if (workman != null) {
					npcsay(p, workman, "Oi! what do you think you are doing ?");
					npcWalkFromPlayer(p, workman);
					npcsay(p, workman, "Don't you realize there are fragile specimens around here ?");
					workman.remove();
				}
			} else if (item.getCatalogId() == ItemId.SPADE.id() && inArray(obj.getID(), SOIL) && obj != null) {
				if (workman != null) {
					npcsay(p, workman, "Oi! dont use that spade!");
					npcWalkFromPlayer(p, workman);
					npcsay(p, workman, "What are you trying to do, destroy everything of value ?");
					workman.remove();
				}
			}
		}
	}

	private void rockPickOnSite(Player p, Item item, GameObject obj) {
		if (item.getCatalogId() == ItemId.ROCK_PICK.id() && inArray(obj.getID(), SOIL)) {
			if (!getLevel2Digsite(p)) {
				Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
				if (workman != null) {
					npcsay(p, workman, "No no, rockpicks should only be used");
					npcWalkFromPlayer(p, workman);
					npcsay(p, workman, "To dig in a level 2 site...");
					workman.remove();
				}
				return;
			}
			if (p.getQuestStage(Quests.DIGSITE) < 4 && getLevel2Digsite(p)) {
				Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
				if (workman != null) {
					npcsay(p, workman, "Sorry, you haven't passed level 2 earth sciences exam");
					npcWalkFromPlayer(p, workman);
					npcsay(p, workman, "I can't let you dig here");
					workman.remove();
				}
				return;
			}

			if (p.getQuestStage(Quests.DIGSITE) >= 4 && getLevel2Digsite(p)) {
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
						&& p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to do any more digging");
						return;
					}
				}
				Functions.thinkbubble(p, new Item(ItemId.ROCK_PICK.id()));
				p.incExp(Skills.MINING, 70, true);
				mes(p, "You dig through the earth");
				delay(1500);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL2_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL2_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if (selectedItem != -1) {
					give(p, selectedItem, 1);
				}
			}
		}
	}

	private void trowelOnSite(Player p, Item item, GameObject obj) {
		if (item.getCatalogId() == ItemId.TROWEL.id() && inArray(obj.getID(), SOIL)) {
			if (getTrainingAreas(p)) {
				Functions.thinkbubble(p, new Item(ItemId.TROWEL.id()));
				p.incExp(Skills.MINING, 50, true);
				mes(p, "You dig with the trowel...");
				delay(1500);
				int randomize = DataConversions.random(0, (TRAINING_AREA_ITEMS.length - 1));
				int selectedItem = TRAINING_AREA_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if (selectedItem != -1 || selectedItem != -2) {
					give(p, selectedItem, 1);
				}
			}
			if (getLevel1Digsite(p)) {
				if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.LEATHER_GLOVES.id())
					&& !p.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())
					&& !p.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())
					&& !p.getCarriedItems().getEquipment().hasEquipped(ItemId.STEEL_GAUNTLETS.id())
					&& !p.getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_CHAOS.id())
					&& !p.getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_COOKING.id())
					&& !p.getCarriedItems().getEquipment().hasEquipped(ItemId.GAUNTLETS_OF_GOLDSMITHING.id())) {
					Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
					if (workman != null) {
						npcsay(p, workman, "Hey, where are your gloves ?");
						npcWalkFromPlayer(p, workman);
						Functions.say(p, workman, "Err...I haven't got any");
						npcsay(p, workman, "Well get some and put them on first!");
						workman.remove();
					}
					return;
				}
				if (!p.getCarriedItems().getEquipment().hasEquipped(ItemId.BOOTS.id())) {
					Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
					if (workman != null) {
						npcsay(p, workman, "Oi, no boots!");
						npcWalkFromPlayer(p, workman);
						npcsay(p, workman, "No boots no digging!");
						workman.remove();
					}
					return;
				}
				if (p.getWorld().getServer().getConfig().WANT_FATIGUE) {
					if (p.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 1
						&& p.getFatigue() >= p.MAX_FATIGUE) {
						p.message("You are too tired to do any more digging");
						return;
					}
				}
				Functions.thinkbubble(p, new Item(ItemId.TROWEL.id()));
				p.incExp(Skills.MINING, 60, true);
				mes(p, "You dig through the earth");
				delay(1500);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL1_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL1_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if (selectedItem != -1) {
					give(p, selectedItem, 1);
				}
			}
			if (getLevel2Digsite(p)) {
				Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
				if (workman != null) {
					npcsay(p, workman, "Sorry, you must use a rockpick");
					npcWalkFromPlayer(p, workman);
					npcsay(p, workman, "To dig in a level 2 site...");
					workman.remove();
				} else {
					p.message("No rockpicks should only be used in a level 2 site...");
				}
			}
			if (getLevel3Digsite(p)) {
				if (!p.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_JAR.id(), Optional.of(false))) { // HAS SPECIMEN JAR
					Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
					if (workman != null) {
						npcsay(p, workman, "Ahem! I don't see your sample jar");
						npcWalkFromPlayer(p, workman);
						npcsay(p, workman, "You must carry one to be able to dig here...");
						Functions.say(p, workman, "Oh, okay");
						workman.remove();
					} else {
						p.message("You need a sample jar to dig here");
					}
					return;
				}
				if (!p.getCarriedItems().hasCatalogID(ItemId.SPECIMEN_BRUSH.id(), Optional.of(false))) { // HAS SPECIMEN BRUSH
					Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
					if (workman != null) {
						npcsay(p, workman, "Wait just a minute!");
						npcWalkFromPlayer(p, workman);
						npcsay(p, workman, "I can't let you dig here",
							"Unless you have a specimen brush with you",
							"Rules is rules!");
						workman.remove();
					} else {
						p.message("you can't dig here unless you have a specimen brush with you");
					}
					return;
				}
				if (p.getQuestStage(Quests.DIGSITE) < 5) {
					Npc workman = addnpc(p.getWorld(), NpcId.WORKMAN.id(), p.getX(), p.getY(), 30000);
					if (workman != null) {
						npcsay(p, workman, "Sorry, you haven't passed level 3 earth sciences exam");
						npcWalkFromPlayer(p, workman);
						npcsay(p, workman, "I can't let you dig here");
						workman.remove();
					}
					return;
				}
				Functions.thinkbubble(p, new Item(ItemId.TROWEL.id()));
				p.incExp(Skills.MINING, 80, true);
				mes(p, "You dig through the earth");
				delay(1500);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL3_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL3_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if (selectedItem != -1) {
					if (selectedItem == ItemId.COINS.id()) {
						give(p, ItemId.COINS.id(), (DataConversions.random(0, 1) == 1 ? 5 : 10));
					} else {
						give(p, selectedItem, 1);
					}
				}
			}
		}
	}

	@Override
	public boolean blockUseLoc(GameObject obj, Item item, Player p) {
		return inArray(obj.getID(), SOIL) || (obj.getID() == ROCK && item.getCatalogId() == ItemId.ROCK_PICK.id());
	}

	@Override
	public void onUseLoc(GameObject obj, Item item, Player p) {
		if (inArray(obj.getID(), SOIL)) {
			switch (ItemId.getById(item.getCatalogId())) {
				case TROWEL:
					trowelOnSite(p, item, obj);
					break;
				case SPADE:
					doSpade(p, item, obj);
					break;
				case ROCK_PICK:
					rockPickOnSite(p, item, obj);
					break;
				case PANNING_TRAY:
					Functions.say(p, null, "No I'd better not - it may damage the tray...");
					break;
				default:
					p.message("Nothing interesting happens");
					break;
			}
		}
		if (obj.getID() == ROCK && item.getCatalogId() == ItemId.ROCK_PICK.id()) {
			p.message("You chip at the rock with the rockpick");
			p.message("You take the pieces of cracked rock");
			give(p, ItemId.CRACKED_ROCK_SAMPLE.id(), 1);
		}
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), SOIL);
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), SOIL)) {
			p.playerServerMessage(MessageType.QUEST, "You examine the patch of soil");
			p.message("You see nothing on the surface");
			Functions.say(p, null, "I think I need something to dig with");
		}
	}

	@Override
	public boolean blockOpInv(Item item, Player p, String command) {
		return item.getCatalogId() == ItemId.SPADE.id() && getDigsite(p);
	}

	@Override
	public void onOpInv(Item item, Player p, String command) {
		if (item.getCatalogId() == ItemId.SPADE.id() && getDigsite(p)) {
			doSpade(p, item, null);
		}
	}
}
