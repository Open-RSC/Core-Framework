package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.Constants;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap2 implements ObjectActionListener, ObjectActionExecutiveListener, WallObjectActionListener, WallObjectActionExecutiveListener {

	/** ITEM IDs **/
	public static int SALMON = 357;
	public static int MEAT_PIE = 259;
	public static int UNICORN_HORN = 997;

	/** OBJECT IDs **/
	public static int[] PILE_OF_MUD_MAP_LEVEL_2 = { 841, 843, 844, 845, 846, 847 };
	public static int CRATE = 868;
	public static int[] RAILINGS = { 167, 170, 169, 168 };
	public static int[] DUG_UP_SOIL = { 839, 840 };
	public static int LEDGE = 837;
	public static int WALL_GRILL_EAST = 836;
	public static int WALL_GRILL_WEST = 838;
	public static int[] ROCKS = { 849, 850, 851, 852, 860, 853, 854, 855, 859, 857, 858 };
	public static int HIJACK_ROCK = 856;
	public static int PASSAGE = 873;
	public static int CAGE_REMAINS = 871;
	public static int GATE_OF_IBAN = 722;
	public static int FLAMES_OF_ZAMORAK = 830;
	public static int GATE_OF_ZAMORAK = 875;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), PILE_OF_MUD_MAP_LEVEL_2)) {
			return true;
		}
		if(obj.getID() == CRATE) {
			return true;
		}
		if(inArray(obj.getID(), DUG_UP_SOIL)) {
			return true;
		}
		if(obj.getID() == LEDGE) {
			return true;
		}
		if(obj.getID() == WALL_GRILL_EAST || obj.getID() == WALL_GRILL_WEST) {
			return true;
		}
		if(inArray(obj.getID(), ROCKS) || obj.getID() == HIJACK_ROCK) {
			return true;
		}
		if(obj.getID() == PASSAGE) {
			return true;
		}
		if(obj.getID() == CAGE_REMAINS) {
			return true;
		}
		if(obj.getID() == GATE_OF_IBAN || obj.getID() == FLAMES_OF_ZAMORAK || obj.getID() == GATE_OF_ZAMORAK) {
			return true;
		}
		return false;
	}
	// 753, 3475 

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(inArray(obj.getID(), PILE_OF_MUD_MAP_LEVEL_2)) {
			if(obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[0]) {
				message(p, "you climb the pile of mud...",
						"it leads to a small tunnel...");
				p.teleport(727, 3448);
				p.message("..ending at the well entrance");
			} 
			if(obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[1]) {
				message(p,"you climb the pile of mud");
				p.teleport(753, 3481);
			}
			if(obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[2]) {
				message(p,"you climb the pile of mud");
				p.teleport(753, 3475);
			}
			if(obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[3]) {
				message(p,"you climb the pile of mud");
				p.teleport(743, 3483);
			} 
			if(obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[4]) {
				message(p,"you climb the pile of mud");
				p.teleport(740, 3476);
			}
			if(obj.getID() == PILE_OF_MUD_MAP_LEVEL_2[5]) {
				message(p,"you climb the pile of mud");
				p.teleport(735, 3478);
			}
		}
		if(obj.getID() == CRATE) {
			message(p, "you search the crate");
			if(!p.getCache().hasKey("crate_food")) {
				p.message("inside you find some food");
				addItem(p, SALMON, 2);
				addItem(p, MEAT_PIE, 2);
				p.getCache().store("crate_food", true);
			} else {
				p.message("but you find nothing");
			}
		}
		if(inArray(obj.getID(), DUG_UP_SOIL)) {
			message(p,"under the soil is a tunnel");
			p.message("would you like to enter?");
			int menu = showMenu(p,
					"no, im scared of small spaces","yep, let's do it");
			if(menu == 1) {
				message(p,"you climb into the small tunnel");
				if(obj.getID() == DUG_UP_SOIL[1])
					p.teleport(745, 3457);
				else
					p.teleport(747, 3470);
				p.message("and crawl into a small dark passage");
			}
		}
		if(obj.getID() == LEDGE) {
			if(command.equalsIgnoreCase("climb up")) {
				p.message("you climb the ledge");
				if(DataConversions.getRandom().nextInt(10) <= 1) {
					p.message("but you slip");
					p.damage((int)(getCurrentLevel(p, HITS) / 42) + 1);
					playerTalk(p, null, "aargh");
				} else
					p.teleport(764, 3463);
			} else {
				p.message("you take a few paces back...");
				p.message("and run torwards the ledge...");
				p.teleport(764, 3461);
				sleep(1600);
				p.message("you land way short of the other platform");
				p.damage((int)(getCurrentLevel(p, HITS) / 5) + 5);
				p.teleport(764, 3467);
				playerTalk(p,null, "ooof");
			}
		}
		if(obj.getID() == WALL_GRILL_EAST) {
			if(!p.getCache().hasKey("rope_wall_grill")) {
				message(p,"the wall grill is too high");
				p.message("you can't quite reach");
			} else {
				message(p, "you use the rope tied to the grill to pull yourself up");
				p.message("you then climb across the grill to the otherside");
				p.teleport(762, 3472);
			}
		}
		if(obj.getID() == WALL_GRILL_WEST) {
			message(p,"you climb across the grill to the otherside");
			p.teleport(766, 3463);
		}
		if(inArray(obj.getID(), ROCKS)) {
			switch(obj.getID()) {
			case 859:
			case 858:
				doRock(obj, p, (int)(getCurrentLevel(p, HITS) / 5) + 5, false, 5); // fall side 5.
				break;
			case 854:
			case 853:
			case 855:
			case 857:
				doRock(obj, p, (int)(getCurrentLevel(p, HITS) / 5) + 5, false, 4); // fall side 4.
				break;
			case 852:
				doRock(obj, p, (int)(getCurrentLevel(p, HITS) / 5) + 5, false, 3); // fall side 3.
				break;
			case 851:
				doRock(obj, p, (int)(getCurrentLevel(p, HITS) / 5) + 5, false, 2); // fall side 2.
				break;
			default:
				doRock(obj, p, (int)(getCurrentLevel(p, HITS) / 5) + 5, false, 1); // fall side 1
				break;
			}
		}
		if(obj.getID() == HIJACK_ROCK) {
			p.setBusyTimer(650);
			p.message("you climb onto the rock");
			if(DataConversions.getRandom().nextInt(5) == 4) {
				p.message("but you slip");
				p.damage((int)(getCurrentLevel(p, HITS) / 5) + 5);
				p.teleport(734, 3483);
				playerTalk(p, null, "aargh");
			} else {
				if(p.getX() == 734) {
					p.teleport(735, 3479);
				} else {
					p.teleport(734, 3480);
				}
				p.message("and step down the other side");
			}
		}
		if(obj.getID() == PASSAGE) {
			message(p, "you walk down the passage way");
			p.message("you step on a pressure trigger");
			p.message("it's a trap");
			if(obj.getX() == 737 || obj.getX() == 735) {
				p.teleport(737,  3489);
			} else if(obj.getX() == 733) {
				p.teleport(733,  3489);
			} 
			World.getWorld().replaceGameObject(obj, 
					new GameObject(obj.getLocation(), 826, obj.getDirection(), obj
							.getType()));
			World.getWorld().delayedSpawnObject(obj.getLoc(), 5000);
			p.damage((int)(getCurrentLevel(p, HITS) / 5) + 5);
			playerTalk(p,null, "aaarghh");
		}
		if(obj.getID() == CAGE_REMAINS) {
			if(p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) >= 5 || p.getQuestStage(Constants.Quests.UNDERGROUND_PASS) == -1) {
				message(p, "you search the cage remains");
				p.message("nothing remains");
				return;
			} 
			if(!hasItem(p, UNICORN_HORN)) {
				sleep(1600);
				p.message("all that remains is a damaged horn");
				addItem(p, UNICORN_HORN, 1);
			} else {
				p.message("nothing remains");
			}
		}
		if(obj.getID() == GATE_OF_IBAN) {
			p.message("you pull on the great door");
			if((p.getCache().hasKey("flames_of_zamorak1") && p.getCache().hasKey("flames_of_zamorak2") && (p.getCache().hasKey("flames_of_zamorak3") && p.getCache().getInt("flames_of_zamorak3") >= 2)) || atQuestStage(p, Constants.Quests.UNDERGROUND_PASS, 7) || atQuestStage(p, Constants.Quests.UNDERGROUND_PASS, -1)) {
				message(p, "from behind the door you hear cry's and moans");
				p.message("the door slowly creeks open");
				replaceObject(obj, new GameObject(obj.getLocation(), 723, obj.getDirection(), obj
						.getType()));
				delayedSpawnObject(obj.getLoc(), 3000);
				p.teleport(766, 3417);
				sleep(1000);
				p.teleport(770, 3417);
				p.message("you walk into the darkness");
			} else {
				p.message("the door refuses to open");
			}
		}
		if(obj.getID() == GATE_OF_ZAMORAK) {
			replaceObject(obj, new GameObject(obj.getLocation(), 723, obj.getDirection(), obj
					.getType()));
			delayedSpawnObject(obj.getLoc(), 3000);
			p.teleport(766, 3417);
			message(p, "you open the huge wooden door");
			p.teleport(763, 3417);
			p.message("and walk through");
		}
		if(obj.getID() == FLAMES_OF_ZAMORAK) {
			message(p, "you search the stone structure");
			p.message("on the side you find an old inscription");
			p.message("it reads...");
			ActionSender.sendBox(p, "@red@While I sense the soft beating of a good heart I will not open% %@red@Feed me three crests of the blessed warriors, and the%@red@creatures remains% %@red@Throw them to me as an offering, a gift of hatred, a token% %@red@Then finally rejoice as all goodness dies in my flames", true); 
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		if(inArray(obj.getID(), RAILINGS)) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if(inArray(obj.getID(), RAILINGS)) {
			if(click == 0) {
				if(obj.getID() == 168) {
					message(p, "the cage door has been sealed shut");
					p.message("the poor unicorn can't escape");
					return;
				}
				p.message("you attempt to pick the lock");
				if(obj.getID() == 169 && getCurrentLevel(p, THIEVING) < 50) {
					p.message("you need a level of 50 thieving to pick this lock");
					return;
				}
				p.setBusyTimer(1600);
				p.message("You manage to pick the lock");
				p.message("you walk through");
				if(obj.getDirection() == 0) {
					if(obj.getY() == p.getY())
						p.teleport(obj.getX(), obj.getY() - 1);
					else
						p.teleport(obj.getX(), obj.getY());
				}
				if(obj.getDirection() == 1) {
					if(obj.getX() == p.getX())
						p.teleport(obj.getX() - 1, obj.getY());
					else
						p.teleport(obj.getX(), obj.getY());
				}
				p.incExp(THIEVING, 15, true);
				sleep(1600);
				p.message("the cage slams shut behind you");
			} else if(click == 1) {
				if(obj.getID() == 168) {
					message(p,"you search the cage");
					if(!hasItem(p, 995)) {
						p.message("you find a loose railing lying on the floor");
						addItem(p, 995, 1);
					} else 
						p.message("but you find nothing");
					return;
				}
				p.message("the cage has been locked");
			}
		}
	}
}
