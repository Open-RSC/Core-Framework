package com.openrsc.server.plugins.authentic.quests.members.undergroundpass.obstacles;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.event.DelayedEvent;
import com.openrsc.server.event.rsc.DuplicationStrategy;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.entity.update.ChatMessage;
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
	public static final Area boundArea = new Area(794, 800, 3467, 3471);

	private final int BASE_TICK = 640;

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), CAGES) || obj.getID() == DEMONS_CHEST_CLOSED || obj.getID() == ZAMORAKIAN_TEMPLE_DOOR;
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (inArray(obj.getID(), CAGES)) {
			if (obj.getID() == CAGES[1]) {
				player.message("the man seems to be entranced");
				mes("the cage is locked");
				delay(3);
				Npc souless = ifnearvisnpc(player, NpcId.SOULESS_HUMAN.id(), 6);
				if (souless != null) {
					npcsay(player, souless, "kuluf ali monopiate");
				}
				mes("you search through the bottom of the cage");
				delay(3);
				if (!player.getCache().hasKey("cons_on_doll")) {
					player.message("but the souless bieng bites into your arm");
					if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())) {
						player.message("klanks gaunlett protects you");
					} else {
						player.damage(((int) getCurrentLevel(player, Skill.HITS.id()) / 10) + 5);
						say(player, null, "aaarrgghh");
					}
				}
				if (!player.getCarriedItems().hasCatalogID(ItemId.IBANS_CONSCIENCE.id(), Optional.of(false)) && !player.getCache().hasKey("cons_on_doll")) {
					player.message("you find the remains of a dove");
					give(player, ItemId.IBANS_CONSCIENCE.id(), 1);
				} else {
					//kosher was separated lol
					if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())) {
						player.message("but you find find nothing");
					} else {
						player.message("you find nothing");
					}
				}
			}
			else if (obj.getID() == CAGES[0]) {
				player.message("the man seems to be entranced");
				mes("the cage is locked");
				delay(3);
				Npc souless = ifnearvisnpc(player, NpcId.SOULESS_HUMAN.id(), 6);
				if (souless != null) {
					npcsay(player, souless, "kuluf ali monopiate");
				}
				mes("you search through the bottom of the cage");
				delay(3);
				player.message("but the souless bieng bites into your arm");
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KLANKS_GAUNTLETS.id())) {
					player.message("klanks gaunlett protects you");
					player.message("but you find find nothing");
				} else {
					player.damage(((int) getCurrentLevel(player, Skill.HITS.id()) / 10) + 5);
					say(player, null, "aaarrgghh");
					player.message("you find nothing");
				}
			}
		}
		else if (obj.getID() == DEMONS_CHEST_CLOSED) {
			mes("you attempt to open the chest");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_OTHAINIAN.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_DOOMION.id(), Optional.of(false))
				&& player.getCarriedItems().hasCatalogID(ItemId.AMULET_OF_HOLTHION.id(), Optional.of(false)) && !player.getCache().hasKey("shadow_on_doll")) {
				mes("the three amulets glow red in your satchel");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.AMULET_OF_OTHAINIAN.id()));
				player.getCarriedItems().remove(new Item(ItemId.AMULET_OF_DOOMION.id()));
				player.getCarriedItems().remove(new Item(ItemId.AMULET_OF_HOLTHION.id()));
				player.message("you place them on the chest and the chest opens");
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), DEMONS_CHEST_OPEN, obj.getDirection(), obj.getType()));
				addloc(obj.getWorld(), obj.getLoc(), 2000);
				delay(2);
				player.message("inside you find a strange dark liquid");
				give(player, ItemId.IBANS_SHADOW.id(), 1);
			} else {
				player.message("but it's magically sealed");
			}
		}
		else if (obj.getID() == ZAMORAKIAN_TEMPLE_DOOR) {
			if (player.getX() <= 792) {
				if (player.getQuestStage(Quests.UNDERGROUND_PASS) == -1 &&
					!config().LOCKED_POST_QUEST_REGIONS_ACCESSIBLE) {
					mes("the temple is in ruins...");
					delay(3);
					player.message("...you cannot enter");
					return;
				}
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_TOP.id())
					&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.ROBE_OF_ZAMORAK_BOTTOM.id())) {
					changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 914, obj.getDirection(), obj.getType()));
					addloc(obj.getWorld(), obj.getLoc(), 3000);
					player.teleport(792, 3469);
					delay();
					player.teleport(795, 3469);
					mes("you pull open the large doors");
					delay(3);
					player.message("and walk into the temple");
					if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 7 || (player.getCache().hasKey("poison_on_doll") && player.getCache().hasKey("cons_on_doll")
						&& player.getCache().hasKey("ash_on_doll") && player.getCache().hasKey("shadow_on_doll"))) {
						if (player.getQuestStage(Quests.UNDERGROUND_PASS) == 6) {
							player.updateQuestStage(Quests.UNDERGROUND_PASS, 7);
						}
						player.message("Iban seems to sense danger");
						mes("@yel@Iban: who dares bring the witches magic into my temple");
						delay(3);
						mes("his eyes fixate on you as he raises his arm");
						delay(3);
						mes("@yel@Iban: an imposter dares desecrate this sacred place..");
						delay(3);
						mes("@yel@Iban: ..home to the only true child of zamorak");
						delay(3);
						mes("@yel@Iban: join the damned, mortal");
						delay(3);
						player.message("iban raises his staff to the air");
						mes("a blast of energy comes from ibans staff");
						delay(3);
						player.message("you are hit by ibans magic bolt");
						displayTeleportBubble(player, player.getX() + 1, player.getY(), true);
						player.damage((int)Math.floor(getCurrentLevel(player, Skill.HITS.id())/10.0) + 4 + DataConversions.random(-1,1));
						say(player, null, "aarrgh");
						mes("@yel@Iban:die foolish mortal");
						delay(3);
						long start = System.currentTimeMillis();
						Area area = Areas.getArea("ibans_room");
						int delayMs = BASE_TICK;
						player.getWorld().getServer().getGameEventHandler().add(new DelayedEvent(player.getWorld(), player, delayMs, "Iban's chamber event", DuplicationStrategy.ONE_PER_MOB) {
							@Override
							public void run() {
								/* Time-out fail, handle appropriately */
								if (System.currentTimeMillis() - start > 1000 * 60 * 2 && getOwner().getLocation().inBounds(boundArea.getMinX(), boundArea.getMinY(),
									boundArea.getMaxX(), boundArea.getMaxY())) {
									getOwner().message("you're blasted out of the temple");
									getOwner().message("@yel@Iban: and stay out");
									getOwner().teleport(790, 3469);
									stop();
								}
								/* If player has logged out or not region area */
								else if (getOwner().isRemoved() || !getOwner().getLocation().inBounds(boundArea.getMinX(), boundArea.getMinY() - 3,
									boundArea.getMaxX() + 4, boundArea.getMaxY() + 3)) {
									stop();
								}
								/* ends it */
								else if (getOwner().getAttribute("iban_bubble_show", false)) {
									stop();
								}
								else {
									/* Get random point on the area */
									Point blastPosition = new Point(
										DataConversions.random(area.getMinX(), area.getMaxX()),
										DataConversions.random(area.getMinY(), area.getMaxY()));
									ActionSender.sendTeleBubble(getOwner(), blastPosition.getX(), blastPosition.getY(), true);
									if (getOwner().getLocation().withinRange(blastPosition, 1)) {
										/* Blast hit */
										int ibanDmg = (int)Math.floor(getCurrentLevel(getOwner(), Skill.HITS.id())/10.0) + 4 + DataConversions.random(-1,1);
										boolean willDie = ibanDmg >= getCurrentLevel(getOwner(), Skill.HITS.id());
										getOwner().damage(ibanDmg);
										if(!willDie) {
											getOwner().teleport(795, 3469); // insert the coords
											getOwner().getUpdateFlags().setChatMessage(new ChatMessage(getOwner(), "aarrgh"));
											getOwner().message("you're blasted back to the door");
										}
									}
								}
							}
						});
					} else {
						player.message("inside iban stands preaching at the alter");
					}
				} else {
					mes("The door refuses to open");
					delay(3);
					player.message("only followers of zamorak may enter");
				}
			} else {
				changeloc(obj, new GameObject(obj.getWorld(), obj.getLocation(), 914, obj.getDirection(), obj.getType()));
				addloc(obj.getWorld(), obj.getLoc(), config().GAME_TICK * 5);
				player.teleport(794, 3469);
				delay();
				player.teleport(791, 3469);
				delay(2);
				player.message("you pull open the large doors");
				player.message("and walk out of the temple");
			}
		}
	}
}
