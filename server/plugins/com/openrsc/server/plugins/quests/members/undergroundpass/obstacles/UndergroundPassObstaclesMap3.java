package com.openrsc.server.plugins.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.Area;
import com.openrsc.server.model.world.Areas;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.util.rsc.DataConversions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class UndergroundPassObstaclesMap3 implements OpLocTrigger {
	private static final Logger LOGGER = LogManager.getLogger(UndergroundPassObstaclesMap3.class);
	/**
	 * OBJECT IDs
	 **/
	public static int[] CAGES = {888, 887};
	public static int ZAMORAKIAN_TEMPLE_DOOR = 869;
	public static final int DEMONS_CHEST_OPEN = 911;
	public static final int DEMONS_CHEST_CLOSED = 912;

	public static final int [] PIT_COORDS = {802, 3469};
	public static final Area boundArea = new Area(PIT_COORDS[0] - 24, PIT_COORDS[0] + 24, PIT_COORDS[1] - 24, PIT_COORDS[1] + 24);

	@Override
	public boolean blockOpLoc(GameObject obj, String command, Player p) {
		return inArray(obj.getID(), CAGES) || obj.getID() == DEMONS_CHEST_CLOSED || obj.getID() == ZAMORAKIAN_TEMPLE_DOOR;
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		if (inArray(obj.getID(), CAGES)) {
			if (obj.getID() == CAGES[1]) {
				p.message("the man seems to be entranced");
				mes(p, "the cage is locked");
				delay(1600);
				mes(p, "you search through the bottom of the cage");
				if (!p.getCache().hasKey("cons_on_doll")) {
					p.message("but the souless bieng bites into your arm");
					if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())) {
						p.message("klanks gaunlett protects you");
					} else {
						p.damage(((int) getCurrentLevel(p, Skills.HITS) / 10) + 5);
						say(p, null, "aaarrgghh");
					}
				}
				if (!p.getCarriedItems().hasCatalogID(ItemId.IBANS_CONSCIENCE.id(), Optional.of(false)) && !p.getCache().hasKey("cons_on_doll")) {
					p.message("you find the remains of a dove");
					give(p, ItemId.IBANS_CONSCIENCE.id(), 1);
				} else {
					//kosher was separated lol
					if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())) {
						p.message("but you find find nothing");
					} else {
						p.message("you find nothing");
					}
				}
			}
			else if (obj.getID() == CAGES[0]) {
				p.message("the man seems to be entranced");
				mes(p, "the cage is locked");
				delay(1600);
				mes(p, "you search through the bottom of the cage");
				p.message("but the souless bieng bites into your arm");
				if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())) {
					p.message("klanks gaunlett protects you");
					p.message("but you find find nothing");
				} else {
					p.damage(((int) getCurrentLevel(p, Skills.HITS) / 10) + 5);
					say(p, null, "aaarrgghh");
					p.message("you find nothing");
				}
			}
		}
		else if (obj.getID() == DEMONS_CHEST_CLOSED) {
			mes(p, "you attempt to open the chest");
			if (p.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_OTHAINIAN.id(), Optional.of(false))
				&& p.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_DOOMION.id(), Optional.of(false))
				&& p.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_HOLTHION.id(), Optional.of(false)) && !p.getCache().hasKey("shadow_on_doll")) {
				mes(p, "the three amulets glow red in your satchel");
				p.getCarriedItems().remove(new Item(ItemId.AMULET_OF_OTHAINIAN.id()));
				p.getCarriedItems().remove(new Item(ItemId.AMULET_OF_DOOMION.id()));
				p.getCarriedItems().remove(new Item(ItemId.AMULET_OF_HOLTHION.id()));
				p.message("you place them on the chest and the chest opens");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), DEMONS_CHEST_OPEN, obj.getDirection(), obj.getType()));
				addloc(obj.getWorld(), obj.getLoc(), 2000);
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				p.message("inside you find a strange dark liquid");
				give(p, ItemId.IBANS_SHADOW.id(), 1);
			} else {
				p.message("but it's magically sealed");
			}
		}
		else if (obj.getID() == ZAMORAKIAN_TEMPLE_DOOR) {
			if (p.getX() <= 792) {
				if (p.getQuestStage(Quests.UNDERGROUND_PASS) == -1 &&
					!p.getWorld().getServer().getConfig().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE) {
					mes(p, "the temple is in ruins...");
					p.message("...you cannot enter");
					return;
				}
				if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_TOP.id())
					&& p.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id())) {
					changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 914, obj.getDirection(), obj.getType()));
					addloc(obj.getWorld(), obj.getLoc(), 3000);
					p.teleport(792, 3469);
					delay(p.getWorld().getServer().getConfig().GAME_TICK);
					p.teleport(795, 3469);
					mes(p, "you pull open the large doors");
					p.message("and walk into the temple");
					if (p.getQuestStage(Quests.UNDERGROUND_PASS) == 7 || (p.getCache().hasKey("poison_on_doll") && p.getCache().hasKey("cons_on_doll")
						&& p.getCache().hasKey("ash_on_doll") && p.getCache().hasKey("shadow_on_doll"))) {
						if (p.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
							p.updateQuestStage(Quests.UNDERGROUND_PASS, 7);
						}
						p.message("Iban seems to sense danger");
						mes(p, "@yel@Iban: who dares bring the witches magic into my temple");
						mes(p, "his eyes fixate on you as he raises his arm");
						mes(p, "@yel@Iban: an imposter dares desecrate this sacred place..",
							"@yel@Iban: ..home to the only true child of zamorak",
							"@yel@Iban: join the damned, mortal");
						p.message("iban raises his staff to the air");
						mes(p, "a blast of energy comes from ibans staff");
						p.message("you are hit by ibans magic bolt");
						displayTeleportBubble(p, p.getX() + 1, p.getY(), true);
						p.damage(((int) getCurrentLevel(p, Skills.HITS) / 7) + 1);
						say(p, null, "aarrgh");
						mes(p, "@yel@Iban:die foolish mortal");
						long start = System.currentTimeMillis();
						Area area = Areas.getArea("ibans_room");
						try {
							while (true) {
								/* Time-out fail, handle appropriately */
								if (System.currentTimeMillis() - start > 1000 * 60 * 2 && p.getLocation().inBounds(794, 3467, 799, 3471)) {
									p.message("you're blasted out of the temple");
									p.message("@yel@Iban: and stay out");
									p.teleport(790, 3469);
									break;
								}
								/* If player has logged out or not region area */
								if (p.isRemoved() || !p.getLocation().inBounds(boundArea.getMinX(), boundArea.getMinY(),
										boundArea.getMaxX(), boundArea.getMaxY())) {
									break;
								}
								/* ends it */
								if (p.getAttribute("iban_bubble_show", false)) {
									break;
								}
								/* Get random point on the area */
								Point blastPosition = new Point(
									DataConversions.random(area.getMinX(), area.getMaxX()),
									DataConversions.random(area.getMinY(), area.getMaxY()));
								ActionSender.sendTeleBubble(p, blastPosition.getX(), blastPosition.getY(), true);
								if (p.getLocation().withinRange(blastPosition, 1)) {
									/* Blast hit */
									p.damage(((int) getCurrentLevel(p, Skills.HITS) / 6) + 2);
									p.teleport(795, 3469); // insert the coords
									say(p, null, "aarrgh");
									p.message("you're blasted back to the door");
								}
								delay(p.getWorld().getServer().getConfig().GAME_TICK);
							}
						} catch (Exception e) {
							LOGGER.catching(e);
						}
					} else {
						p.message("inside iban stands preaching at the alter");
					}
				} else {
					mes(p, "The door refuses to open");
					p.message("only followers of zamorak may enter");
				}
			} else {
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 914, obj.getDirection(), obj.getType()));
				addloc(obj.getWorld(), obj.getLoc(), p.getWorld().getServer().getConfig().GAME_TICK * 5);
				p.teleport(794, 3469);
				delay(p.getWorld().getServer().getConfig().GAME_TICK);
				p.teleport(791, 3469);
				delay(p.getWorld().getServer().getConfig().GAME_TICK * 2);
				p.message("you pull open the large doors");
				p.message("and walk out of the temple");
			}
		}
	}
}
