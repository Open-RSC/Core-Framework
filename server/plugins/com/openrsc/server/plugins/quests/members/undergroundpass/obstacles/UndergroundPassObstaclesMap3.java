package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.Area;
import com.openrsc.server.model.world.Areas;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap3 implements ObjectActionListener, ObjectActionExecutiveListener {

	/** ITEM IDs **/
	public static int IBANS_CONSCIENCE = 1008;
	public static int KLANKS = 1006;
	public static int DEMONS_CHEST = 912;
	public static int ROBE_OF_ZAMORAK_TOP = 702;
	public static int ROBE_OF_ZAMORAK_BOTTOM = 703;

	/** OBJECT IDs **/
	public static int[] CAGES = { 888, 887 };
	public static int ZAMORAKIAN_TEMPLE_DOOR = 869;

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), CAGES)) {
			return true;
		}
		if (obj.getID() == DEMONS_CHEST) {
			return true;
		}
		if (obj.getID() == ZAMORAKIAN_TEMPLE_DOOR) {
			return true;
		}
		return false;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), CAGES)) {
			if (obj.getID() == CAGES[1]) {
				p.message("the man seems to be entranced");
				message(p, "the cage is locked");
				sleep(1600);
				message(p, "you search through the bottom of the cage");
				if (!p.getCache().hasKey("cons_on_doll")) {
					p.message("but the souless bieng bites into your arm");
					if (p.getInventory().wielding(KLANKS)) {
						p.message("klanks gaunlett protects you");
					} else {
						p.damage(((int) getCurrentLevel(p, HITS) / 10) + 5);
						playerTalk(p, null, "aaarrgghh");
					}
				}
				if (!hasItem(p, IBANS_CONSCIENCE) && !p.getCache().hasKey("cons_on_doll")) {
					p.message("you find the remains of a dove");
					addItem(p, IBANS_CONSCIENCE, 1);
				} else {
					p.message("you find nothing");
				}
			}
			if (obj.getID() == CAGES[0]) {
				p.message("the man seems to be entranced");
				message(p, "the cage is locked");
				sleep(1600);
				message(p, "you search through the bottom of the cage");
				if (p.getInventory().wielding(KLANKS)) {
					p.message("but the souless bieng bites into your arm");
					p.message("klanks gaunlett protects you");
				} else {
					p.message("but the souless bieng bites into your arm");
					p.damage(((int) getCurrentLevel(p, HITS) / 10) + 5);
					playerTalk(p, null, "aaarrgghh");
				}
				p.message("you find nothing");
			}
		}
		if (obj.getID() == DEMONS_CHEST) {
			message(p, "you attempt to open the chest");
			if (hasItem(p, 1009) && hasItem(p, 1010) && hasItem(p, 1009) && !p.getCache().hasKey("shadow_on_doll")) {
				message(p, "the three amulets glow red in your satchel");
				removeItem(p, 1009, 1);
				removeItem(p, 1010, 1);
				removeItem(p, 1011, 1);
				p.message("you place them on the chest and the chest opens");
				replaceObject(obj, new GameObject(obj.getLocation(), 911, obj.getDirection(), obj.getType()));
				delayedSpawnObject(obj.getLoc(), 2000);
				sleep(1000);
				p.message("inside you find a strange dark liquid");
				addItem(p, 1007, 1);
			} else {
				p.message("but it's magically sealed");
			}
		}
		if (obj.getID() == ZAMORAKIAN_TEMPLE_DOOR) {
			if (p.getX() <= 792) {
				if (p.getInventory().wielding(ROBE_OF_ZAMORAK_TOP)
						&& p.getInventory().wielding(ROBE_OF_ZAMORAK_BOTTOM)) {
					replaceObject(obj, new GameObject(obj.getLocation(), 914, obj.getDirection(), obj.getType()));
					delayedSpawnObject(obj.getLoc(), 3000);
					p.teleport(792, 3469);
					sleep(600);
					p.teleport(795, 3469);
					message(p, "you pull open the large doors");
					p.message("and walk into the temple");
					if (p.getCache().hasKey("poison_on_doll") && p.getCache().hasKey("cons_on_doll")
							&& p.getCache().hasKey("ash_on_doll") && p.getCache().hasKey("shadow_on_doll")) {
						p.message("Iban seems to sense danger");
						message(p, "@yel@Iban: who dares bring the witches magic into my temple");
						message(p, "his eyes fixate on you as he raises his arm");
						message(p, "@yel@Iban: an imposter dares desecrate this sacred place..",
								"@yel@Iban: ..home to the only true child of zamorak",
								"@yel@Iban: join the damned, mortal");
						p.message("iban raises his staff to the air");
						message(p, "a blast of energy comes from ibans staff");
						p.message("you are hit by ibans magic bolt");
						displayTeleportBubble(p, p.getX() + 1, p.getY(), true);
						p.damage(((int) getCurrentLevel(p, HITS) / 7) + 1);
						playerTalk(p, null, "aarrgh");
						message(p, "@yel@Iban:die foolish mortal");
						long start = System.currentTimeMillis();
						Area area = Areas.getArea("ibans_room");
						try {
							while (true) {
								/* Time-out fail, handle appropriately */
								if (System.currentTimeMillis() - start > 1000 * 60 * 2 && p.getLocation().inBounds(794,799,3467,3471)) {
									p.message("you're blasted out of the temple");
									p.message("@yel@Iban: and stay out");
									p.teleport(790, 3469);
									break;
								}
								/* If player has logged out or removed for whatever reason TODO */
								if(p.isRemoved()) {
									break;
								}
								/* ends it */
								if(p.getAttribute("iban_bubble_show", false)) {
									break;
								}
								/* Get random point on the area */
								Point blastPosition = new Point(
										DataConversions.random(area.getMinX(), area.getMaxX()),
										DataConversions.random(area.getMinY(), area.getMaxY()));
								ActionSender.sendTeleBubble(p, blastPosition.getX(), blastPosition.getY(), true);
								if (p.getLocation().withinRange(blastPosition, 1)) {
									/* Blast hit */
									p.damage(((int) getCurrentLevel(p, HITS) / 6) + 2);
									p.teleport(795, 3469); // insert the coords
									playerTalk(p, null, "aarrgh");
									p.message("you're blasted back to the door");
								}
								sleep(650);
							}
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						p.message("inside iban stands preaching at the alter");
					}
				} else {
					message(p, "The door refuses to open");
					p.message("only followers of zamorak may enter");
				}
			} else {
				replaceObject(obj, new GameObject(obj.getLocation(), 914, obj.getDirection(), obj.getType()));
				delayedSpawnObject(obj.getLoc(), 3000);
				p.teleport(794, 3469);
				sleep(600);
				p.teleport(791, 3469);
				sleep(1000);
				p.message("you pull open the large doors");
				p.message("and walk out of the temple");
			}
		}
	}
}