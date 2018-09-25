package com.openrsc.server.plugins.quests.members.digsite;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.InvActionListener;
import com.openrsc.server.plugins.listeners.action.InvUseOnObjectListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnObjectExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
/**
 * 
 * @author Imposter/Fate
 *
 */
public class DigsiteDigAreas implements ObjectActionListener, ObjectActionExecutiveListener, InvUseOnObjectListener, InvUseOnObjectExecutiveListener, InvActionListener, InvActionExecutiveListener {

	public static int[] SOIL = { 1065, 1066, 1067 };
	public static int ROCK = 1059;

	public static final int TROWEL = 1145;
	public static final int ROCK_PICK = 1114;
	public static final int SPADE = 211;
	public static final int PANNING_TRAY = 1111;

	public static int WORKMAN = 722;

	public int[] TRAINING_AREA_ITEMS = { -1, -2, 1168, 1165, 10, 1150, 983 };

	public int[] DIGSITE_LEVEL1_ITEMS = { -1, 20, 894, 1155, 1162, 778, 150, 801, 1166, 1159, 1168 };

	public int[] DIGSITE_LEVEL2_ITEMS = { -1, 20, 516, 135, 149, 1170, 271, 1167, 1158, 140, 1155 };

	public int[] DIGSITE_LEVEL3_ITEMS = { -1, 20, 1157, 1167, 1175, 1165, 827, 251, 1166, 1162, 10, 39, 149, 1075, 470, 1169, 1151, 1155, 516};

	private boolean getTrainingAreas(Player p) {
		if(p.getLocation().inBounds(13, 526, 17, 529)) { // EAST DONE
			return true;
		}
		if(p.getLocation().inBounds(24, 526, 27, 529)) { // WEST DONE
			return true;
		}
		return false;
	}

	private static boolean getLevel3Digsite(Player p) {
		if(p.getLocation().inBounds(10, 495, 14, 499)) { // Top North DONE.
			return true;
		}
		if(p.getLocation().inBounds(23, 518, 28, 524)) { // WEST MIDDLE AREA DONE
			return true;
		}
		return false;
	}

	private boolean getLevel2Digsite(Player p) {
		if(p.getLocation().inBounds(24, 514, 26, 516)) { // WEST MIDDLE SMALL WINCH AREA DONE
			return true;
		}
		if(p.getLocation().inBounds(14, 506, 15, 509)) { // EAST NORTH DONE
			return true;
		}
		if(p.getLocation().inBounds(20, 505, 27, 509)) { // WEST NORTH DONE
			return true;
		}
		return false;
	}

	private boolean getLevel1Digsite(Player p) {
		if(p.getLocation().inBounds(19, 516, 21, 526)) { // MIDDLE DONE
			return true;
		}
		if(p.getLocation().inBounds(13, 516, 17, 524)) { // EAST MIDDLE DONE
			return true;
		}
		return false;
	}

	private boolean getDigsite(Player p) {
		if(p.getLocation().inBounds(5, 492, 42, 545)) { // ENTIRE DIGSITE AREA used for spade
			return true;
		}
		return false;
	}

	private void doSpade(Player p, Item item, GameObject obj) {
		Npc workmanCheck = getNearestNpc(p, WORKMAN, 15);
		if(workmanCheck != null) {
			Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
			if(item.getID() == SPADE && item.getDef().getCommand().equalsIgnoreCase("Dig") && obj == null) {
				if(workman != null) {
					npcTalk(p, workman, "Oi! what do you think you are doing ?");
					npcWalkFromPlayer(p, workman);
					npcTalk(p, workman, "Don't you realize there are fragile specimens around here ?");
					workman.remove();
				}
			} else if(item.getID() == SPADE && inArray(obj.getID(), SOIL) && obj != null) {
				if(workman != null) {
					npcTalk(p, workman, "Oi! dont use that spade!");
					npcWalkFromPlayer(p, workman);
					npcTalk(p, workman, "What are you trying to do, destroy everything of value ?");
					workman.remove();
				}
			}
		}
	}

	private void rockPickOnSite(Player p, Item item, GameObject obj) {
		if(item.getID() == ROCK_PICK && inArray(obj.getID(), SOIL)) {
			if(!getLevel2Digsite(p)) {
				Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
				if(workman != null) {
					npcTalk(p, workman, "No no, rockpicks should only be used");
					npcWalkFromPlayer(p, workman);
					npcTalk(p, workman, "To dig in a level 2 site...");
					workman.remove();
				}
				return;
			}
			if(p.getQuestStage(Constants.Quests.DIGSITE) < 4 && getLevel2Digsite(p)) {
				Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
				if(workman != null) {
					npcTalk(p, workman, "Sorry, you haven't passed level 2 earth sciences exam");
					npcWalkFromPlayer(p, workman);
					npcTalk(p, workman, "I can't let you dig here");
					workman.remove();
				}
				return;
			}

			if(p.getQuestStage(Constants.Quests.DIGSITE) >= 4 && getLevel2Digsite(p)) {
				if(p.getFatigue() >= 69750) {
					p.message("You are too tired to do any more digging");
					return;
				}
				showBubble(p, new Item(ROCK_PICK));
				p.incExp(MINING, 70, true);
				message(p, "You dig through the earth");
				sleep(1500);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL2_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL2_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if(selectedItem != -1) {
					addItem(p, selectedItem, 1);
				}
				return;
			}
		}
	}

	private void trowelOnSite(Player p, Item item, GameObject obj) {
		if(item.getID() == TROWEL && inArray(obj.getID(), SOIL)) {
			if(getTrainingAreas(p)) {
				showBubble(p, new Item(TROWEL));
				p.incExp(MINING, 50, true);
				message(p, "You dig with the trowel...");
				sleep(1500);
				int randomize = DataConversions.random(0, (TRAINING_AREA_ITEMS.length - 1));
				int selectedItem = TRAINING_AREA_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if(selectedItem != -1 || selectedItem != -2) {
					addItem(p, selectedItem, 1);
				}
			}
			if(getLevel1Digsite(p)) {
				if(!p.getInventory().wielding(16) 
						&& !p.getInventory().wielding(556) 
						&& !p.getInventory().wielding(1006)
						&& !p.getInventory().wielding(698)
						&& !p.getInventory().wielding(701)
						&& !p.getInventory().wielding(700)
						&& !p.getInventory().wielding(699)) {
					Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
					if(workman != null) {
						npcTalk(p, workman, "Hey, where are your gloves ?");
						npcWalkFromPlayer(p, workman);
						playerTalk(p, workman, "Err...I haven't got any");
						npcTalk(p, workman, "Well get some and put them on first!");
						workman.remove();
					}
					return;
				}
				if(!p.getInventory().wielding(17)) {
					Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
					if(workman != null) {
						npcTalk(p, workman, "Oi, no boots!");
						npcWalkFromPlayer(p, workman);
						npcTalk(p, workman, "No boots no digging!");
						workman.remove();
					}
					return;
				}
				if(p.getFatigue() >= 69750) {
					p.message("You are too tired to do any more digging");
					return;
				}
				showBubble(p, new Item(TROWEL));
				p.incExp(MINING, 60, true);
				message(p, "You dig through the earth");
				sleep(1500);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL1_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL1_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if(selectedItem != -1) {
					addItem(p, selectedItem, 1);
				}
			}
			if(getLevel2Digsite(p)) {
				Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
				if(workman != null) {
					npcTalk(p, workman, "Sorry, you must use a rockpick");
					npcWalkFromPlayer(p, workman);
					npcTalk(p, workman, "To dig in a level 2 site...");
					workman.remove();
				} else {
					p.message("No rockpicks should only be used in a level 2 site...");
				}
			}
			if(getLevel3Digsite(p)) {
				if(!hasItem(p, 1116)) { // HAS SPECIMEN JAR
					Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
					if(workman != null) {
						npcTalk(p, workman, "Ahem! I don't see your sample jar");
						npcWalkFromPlayer(p, workman);
						npcTalk(p, workman, "You must carry one to be able to dig here...");
						playerTalk(p, workman, "Oh, okay");
						workman.remove();
					} else {
						p.message("You need a sample jar to dig here");
					}
					return;
				}
				if(!hasItem(p, 1115)) { // HAS SPECIMEN BRUSH
					Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
					if(workman != null) {
						npcTalk(p, workman, "Wait just a minute!");
						npcWalkFromPlayer(p, workman);
						npcTalk(p, workman, "I can't let you dig here",
								"Unless you have a specimen brush with you",
								"Rules is rules!");
						workman.remove();
					} else {
						p.message("you dig here unless you have a specimen brush with you");
					}
					return;
				}
				if(p.getQuestStage(Constants.Quests.DIGSITE) < 5) {
					Npc workman = spawnNpc(WORKMAN, p.getX(), p.getY(), 30000);
					if(workman != null) {
						npcTalk(p, workman, "Sorry, you haven't passed level 3 earth sciences exam");
						npcWalkFromPlayer(p, workman);
						npcTalk(p, workman, "I can't let you dig here");
						workman.remove();
					}
					return;
				}
				showBubble(p, new Item(TROWEL));
				p.incExp(MINING, 80, true);
				message(p, "You dig through the earth");
				sleep(1500);
				int randomize = DataConversions.random(0, (DIGSITE_LEVEL3_ITEMS.length - 1));
				int selectedItem = DIGSITE_LEVEL3_ITEMS[randomize];
				doDigsiteItemMessages(p, selectedItem);
				if(selectedItem != -1) {
					if(selectedItem == 10) {
						addItem(p, 10, (DataConversions.random(0, 1) == 1 ? 5 : 10));
					} else {
						addItem(p, selectedItem, 1);
					}
				}
			}
		}
	}

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if(inArray(obj.getID(), SOIL)) {
			return true;
		}
		if(obj.getID() == ROCK && item.getID() == ROCK_PICK) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if(inArray(obj.getID(), SOIL)) {
			switch(item.getID()) {
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
				playerTalk(p, null, "No I'd better not - it may damage the tray...");
				break;
			default:
				p.message("Nothing interesting happens");
				break;
			}
		}
		if(obj.getID() == ROCK && item.getID() == ROCK_PICK) {
			p.message("You chip at the rock with the rockpick");
			p.message("You take the pieces of cracked rock");
			addItem(p, 1150, 1);
		}
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), SOIL)) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), SOIL)) {
			p.playerServerMessage(MessageType.QUEST, "You examine the patch of soil");
			p.message("You see nothing on the surface");
			playerTalk(p, null, "I think I need something to dig with");
		}
	}

	@Override
	public boolean blockInvAction(Item item, Player p) {
		if(item.getID() == SPADE && getDigsite(p)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvAction(Item item, Player p) {
		if(item.getID() == SPADE && getDigsite(p)) {
			doSpade(p, item, null);
		}
	}

	public static void doDigsiteItemMessages(Player p, int item) {
		if(item == -1) {
			p.message("You find nothing");
		} else if(item == -2) {
			p.message("You find nothing of interest");
		} else if(item == 20) {
			p.message("You find some bones");
		} else if(item == 516) {
			p.message("You find some purple dye");
		} else if(item == 135) {
			p.message("You find an old pot");
		} else if(item == 149) {
			p.message("You find some clay");
		} else if(item == 1170 || item == 778) {
			p.message("You find some broken glass");
		} else if(item == 271) {
			p.message("You find a rat's tail");
		} else if(item == 1167) {
			p.message("You find a broken staff");
		} else if(item == 1158 || item == 1157) {
			p.message("You find some old armour");
		} else if(item == 140) {
			p.message("You find an old jug");
		} else if(item == 1155) {
			p.message("You find an old boot");
		} else if(item == 1168) {
			p.message("You find an old vase");
		} else if(item == 10) {
			if(getLevel3Digsite(p)) {
				p.message("You find some coins");
			} else {
				p.message("You find a coin");
			}
		} else if(item == 1150) {
			p.message("You find a broken rock sample");
		} else if(item == 983) {
			p.message("You find some charcoal");
		} else if(item == 1165) {
			p.message("You find a broken arrow");
		} else if(item == 894) {
			p.message("You find an opal");
		} else if(item == 1162) {
			p.message("You find an old tooth");
		} else if(item == 150) {
			p.message("You find some copper ore");
		} else if(item == 801) {
			p.message("You find a rotten apple");
		} else if(item == 1166) {
			p.message("You find some buttons");
		} else if(item == 1159) {
			p.message("You find a rusty sword");
		} else if(item == 1175) {
			p.message("You find a strange talisman");
		} else if(item == 827) {
			p.message("You find a bronze spear");
		} else if(item == 251) {
			p.message("You find a pie dish");
		} else if(item == 39) {
			p.message("You find a needle");
		} else if(item == 1075) {
			p.message("You find a throwing knife");
		} else if(item == 470) {
			p.message("You find an black helmet");
		} else if(item == 1169) {
			p.message("You find some old pottery");
		} else if(item == 1151) {
			p.message("You find a belt buckle");
		} else if(item == 28) {
			p.message("You find a dagger");
		}
	}
}
