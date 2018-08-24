package com.openrsc.server.plugins.misc;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

public class StrangeBarrels implements ObjectActionListener, ObjectActionExecutiveListener {

	/**
	 * @author Davve
	 * What I have discovered from barrels is the food, potions, misc, weapon, runes, certificates and monsters.
	 * There are 8 ways that the barrel behave - more comment below.
	 * The barrel is smashed and then after 40 seconds it adds on a new randomized coord in the cave area.
	 */

	public static final int STRANGE_BARREL = 1178;

	public static final int[] FOOD = { 138, 179, 335, 261, 319, 262, 263, 861, };

	public static final int[] POTION = { 482, 224, 485, 476 };

	public static final int[] OTHER = { 237, 986, 988, 815, 10, 676, 156, 549, 172, 14, 987, 1259, 166, 774 };

	public static final int[] WEAPON = { 1013, 1076, 1080, 1078, 1024, 1015, 1068, 1075, 1077, 1079, 1069 };

	public static final int[] RUNES = { 34, 32, 33, 31 };

	public static final int[] CERTIFICATE = { 518, 519, 629, 534, 535, 631, 630, 713, 711 };

	public static final int[] MONSTER = { 190, 199, 57, 99, 768, 61, 43, 21, 23, 41, 46, 40, 47, 67, 104, 66, 45, 19, 70 };


	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == STRANGE_BARREL) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if(obj.getID() == STRANGE_BARREL) {
			p.setBusyTimer(600);
			int action = DataConversions.random(0, 4);
			if(action != 0) {
				p.message("You smash the barrel open.");
				removeObject(obj);
				Server.getServer().getEventHandler().add(new SingleEvent(null, 40000) { // 40 seconds
					public void action() {
						int newObjectX = DataConversions.random(467, 476);
						int newObjectY = DataConversions.random(3699, 3714);
						if(RegionManager.getRegion(Point.location(newObjectX, newObjectY)).getGameObject(Point.location(newObjectX, newObjectY)) != null) {
							registerObject(new GameObject(obj.getLoc()));
						} else {
							registerObject(new GameObject(Point.location(newObjectX, newObjectY), 1178, 0, 0));
						}
					}
				});
				/**
				 * Out comes a NPC only.
				 */
				if(action == 1) {
					spawnMonster(p, obj.getX(), obj.getY());
				}
				/**
				 * Out comes an item only.
				 */
				else if(action == 2) {
					spawnItem(p, obj.getX(), obj.getY());
				}
				/**
				 * Out comes both a NPC and an ITEM.
				 */
				else if(action == 3) {
					spawnItem(p, obj.getX(), obj.getY());
					spawnMonster(p, obj.getX(), obj.getY());
				}
				/**
				 * Smash the barrel and get randomly hit from 0-14 damage.
				 */
				else if(action == 4) {
					p.message("The barrel explodes...");
					p.message("...you take some damage...");
					displayTeleportBubble(p, obj.getX(), obj.getY(), true);
					p.damage((int) DataConversions.random(0, 14));
				}
			} else {
				/**
				 * Smash the barrel open but nothing happens.
				 */
				if(DataConversions.random(0, 1) != 1) {
					p.message("You smash the barrel open.");
					removeObject(obj);
					delayedSpawnObject(obj.getLoc(), 40000); // 40 seconds
				} else {
					if(DataConversions.random(0, 1) != 0) {
						p.message("You were unable to smash this barrel open.");
						message(p, 1300, "You hit the barrel at the wrong angle.",
								"You're heavily jarred from the vibrations of the blow.");
						int reduceAttack = DataConversions.random(1, 3);
						p.message("Your attack is reduced by " + reduceAttack + ".");
						p.getSkills().setLevel(ATTACK, p.getSkills().getLevel(ATTACK) - reduceAttack);
					} else {
						p.message("You were unable to smash this barrel open.");
					}
				} 
			}
		} 
	}

	private void spawnMonster(Player p, int x, int y) {
		int randomizeMonster = DataConversions.random(0, (MONSTER.length - 1));
		int selectedMonster = MONSTER[randomizeMonster];
		Npc monster = spawnNpc(selectedMonster, x, y, 60000 * 3); // 3 minutes
		sleep(600);
		if(monster != null) {
			monster.startCombat(p);
		}
	}

	private void spawnItem(Player p, int x, int y) {
		int randomizeReward = DataConversions.random(0, 100);
		int[] selectItemArray = OTHER;
		if(randomizeReward >= 0 && randomizeReward <= 14) { // 15%
			selectItemArray = FOOD;
		} else if(randomizeReward >= 15 && randomizeReward <= 29) { // 15%
			selectItemArray = POTION;
		} else if(randomizeReward >= 30 && randomizeReward <= 44) { // 15%
			selectItemArray = RUNES;
		} else if(randomizeReward >= 45 && randomizeReward <= 59) { // 15%
			selectItemArray = CERTIFICATE;
		} else if(randomizeReward >= 60 && randomizeReward <= 89) { // 30%
			selectItemArray = OTHER;
		} else if(randomizeReward >= 90 && randomizeReward <= 100) { // 11%
			selectItemArray = WEAPON;
		}
		int randomizeItem = DataConversions.random(0, (selectItemArray.length - 1));
		int selectedItem = selectItemArray[randomizeItem];
		if(selectedItem == 10) {
			createGroundItem(selectedItem, 100, x, y, p);
		} else {
			createGroundItem(selectedItem, 1, x, y, p);
		}
	}
}