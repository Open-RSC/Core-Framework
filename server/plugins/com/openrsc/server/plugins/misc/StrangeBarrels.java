package com.openrsc.server.plugins.misc;

import com.openrsc.server.Server;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.Skills;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.region.RegionManager;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.util.rsc.DataConversions;

import static com.openrsc.server.plugins.Functions.createGroundItem;
import static com.openrsc.server.plugins.Functions.delayedSpawnObject;
import static com.openrsc.server.plugins.Functions.displayTeleportBubble;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.registerObject;
import static com.openrsc.server.plugins.Functions.removeObject;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

public class StrangeBarrels implements ObjectActionListener, ObjectActionExecutiveListener {

	/**
	 * @author Davve
	 * What I have discovered from barrels is the food, potions, misc, weapon, runes, certificates and monsters.
	 * There are 8 ways that the barrel behave - more comment below.
	 * The barrel is smashed and then after 40 seconds it adds on a new randomized coord in the cave area.
	 */

	private static final int STRANGE_BARREL = 1178;

	private static final int[] FOOD = {
		ItemId.BREAD.id(),
		ItemId.SPINACH_ROLL.id(),
		ItemId.SLICE_OF_CAKE.id(),
		ItemId.HALF_A_MEAT_PIE.id(),
		ItemId.CHEESE.id(),
		ItemId.HALF_A_REDBERRY_PIE.id(),
		ItemId.HALF_AN_APPLE_PIE.id(),
		ItemId.FRESH_PINEAPPLE.id()
	};

	private static final int[] POTION = {
		ItemId.ONE_DEFENSE_POTION.id(),
		ItemId.ONE_STRENGTH_POTION.id(),
		ItemId.ONE_RESTORE_PRAYER_POTION.id(),
		ItemId.ONE_ATTACK_POTION.id()
	};

	private static final int[] OTHER = {
		ItemId.ROPE.id(),
		ItemId.ROCKS.id(),
		ItemId.SHIP_TICKET.id(),
		ItemId.UNIDENTIFIED_SNAKE_WEED.id(),
		ItemId.COINS.id(),
		ItemId.BOW_STRING.id(),
		ItemId.BRONZE_PICKAXE.id(),
		ItemId.CASKET.id(),
		ItemId.GOLD_BAR.id(),
		ItemId.LOGS.id(),
		ItemId.PARAMAYA_REST_TICKET.id(),
		ItemId.STEEL_PICKAXE.id(),
		ItemId.TINDERBOX.id(),
		ItemId.LIT_TORCH.id()
	};

	private static final int[] WEAPON = {
		ItemId.BRONZE_THROWING_DART.id(),
		ItemId.BRONZE_THROWING_KNIFE.id(),
		ItemId.RUNE_THROWING_KNIFE.id(),
		ItemId.MITHRIL_THROWING_KNIFE.id(),
		ItemId.STEEL_THROWING_DART.id(),
		ItemId.IRON_THROWING_DART.id(),
		ItemId.MITHRIL_THROWING_DART.id(),
		ItemId.IRON_THROWING_KNIFE.id(),
		ItemId.STEEL_THROWING_KNIFE.id(),
		ItemId.ADAMANTITE_THROWING_KNIFE.id(),
		ItemId.ADAMANTITE_THROWING_DART.id()
	};

	private static final int[] RUNES = {
		ItemId.EARTH_RUNE.id(),
		ItemId.WATER_RUNE.id(),
		ItemId.AIR_RUNE.id(),
		ItemId.FIRE_RUNE.id()
	};

	private static final int[] CERTIFICATE = {
		ItemId.COAL_CERTIFICATE.id(),
		ItemId.MITHRIL_ORE_CERTIFICATE.id(),
		ItemId.RAW_BASS_CERTIFICATE.id(),
		ItemId.RAW_LOBSTER_CERTIFICATE.id(),
		ItemId.SWORDFISH_CERTIFICATE.id(),
		ItemId.RAW_SHARK_CERTIFICATE.id(),
		ItemId.SHARK_CERTIFICATE.id(),
		ItemId.WILLOW_LOGS_CERTIFICATE.id(),
		ItemId.YEW_LOGS_CERTIFICATE.id()
	};

	//TODO CHECK IDS ON AMBIGUOUS NPCS
	private static final int[] MONSTER = {NpcId.CHAOS_DWARF.id(), NpcId.DARK_WARRIOR.id(), NpcId.DARKWIZARD_LVL13.id(),
			NpcId.DEADLY_RED_SPIDER.id(), NpcId.DEATH_WING.id(), NpcId.GIANT.id(), NpcId.GIANT_BAT.id(), NpcId.MUGGER.id(),
			NpcId.GIANT_SPIDER_LVL8.id(), NpcId.ZOMBIE_LVL24_GEN.id(), NpcId.SKELETON_LVL25.id(), NpcId.SKELETON_LVL21.id(),
			NpcId.RAT_LVL13.id(), NpcId.HOBGOBLIN_LVL32.id(), NpcId.MOSS_GIANT.id(), NpcId.BLACK_KNIGHT.id(), NpcId.SKELETON_LVL31.id(),
			NpcId.RAT_LVL8.id(), NpcId.SCORPION.id()};

	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		return obj.getID() == STRANGE_BARREL;
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == STRANGE_BARREL) {
			p.setBusyTimer(600);
			int action = DataConversions.random(0, 4);
			if (action != 0) {
				p.message("You smash the barrel open.");
				removeObject(obj);
				Server.getServer().getEventHandler().add(new SingleEvent(null, 40000, "Smash Strange Barrel") { // 40 seconds
					public void action() {
						int newObjectX = DataConversions.random(467, 476);
						int newObjectY = DataConversions.random(3699, 3714);
						if (RegionManager.getRegion(Point.location(newObjectX, newObjectY)).getGameObject(Point.location(newObjectX, newObjectY)) != null) {
							registerObject(new GameObject(obj.getLoc()));
						} else {
							registerObject(new GameObject(Point.location(newObjectX, newObjectY), 1178, 0, 0));
						}
					}
				});
				/*
				  Out comes a NPC only.
				 */
				if (action == 1) {
					spawnMonster(p, obj.getX(), obj.getY());
				}
				/*
				  Out comes an item only.
				 */
				else if (action == 2) {
					spawnItem(p, obj.getX(), obj.getY());
				}
				/*
				  Out comes both a NPC and an ITEM.
				 */
				else if (action == 3) {
					spawnItem(p, obj.getX(), obj.getY());
					spawnMonster(p, obj.getX(), obj.getY());
				}
				/*
				  Smash the barrel and get randomly hit from 0-14 damage.
				 */
				else if (action == 4) {
					p.message("The barrel explodes...");
					p.message("...you take some damage...");
					displayTeleportBubble(p, obj.getX(), obj.getY(), true);
					p.damage(DataConversions.random(0, 14));
				}
			} else {
				/*
				  Smash the barrel open but nothing happens.
				 */
				if (DataConversions.random(0, 1) != 1) {
					p.message("You smash the barrel open.");
					removeObject(obj);
					delayedSpawnObject(obj.getLoc(), 40000); // 40 seconds
				} else {
					if (DataConversions.random(0, 1) != 0) {
						p.message("You were unable to smash this barrel open.");
						message(p, 1300, "You hit the barrel at the wrong angle.",
							"You're heavily jarred from the vibrations of the blow.");
						int reduceAttack = DataConversions.random(1, 3);
						p.message("Your attack is reduced by " + reduceAttack + ".");
						p.getSkills().setLevel(Skills.ATTACK, p.getSkills().getLevel(Skills.ATTACK) - reduceAttack);
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
		if (monster != null) {
			monster.startCombat(p);
		}
	}

	private void spawnItem(Player p, int x, int y) {
		int randomizeReward = DataConversions.random(0, 100);
		int[] selectItemArray = OTHER;
		if (randomizeReward >= 0 && randomizeReward <= 14) { // 15%
			selectItemArray = FOOD;
		} else if (randomizeReward >= 15 && randomizeReward <= 29) { // 15%
			selectItemArray = POTION;
		} else if (randomizeReward >= 30 && randomizeReward <= 44) { // 15%
			selectItemArray = RUNES;
		} else if (randomizeReward >= 45 && randomizeReward <= 59) { // 15%
			selectItemArray = CERTIFICATE;
		} else if (randomizeReward >= 60 && randomizeReward <= 89) { // 30%
			selectItemArray = OTHER;
		} else if (randomizeReward >= 90 && randomizeReward <= 100) { // 11%
			selectItemArray = WEAPON;
		}
		int randomizeItem = DataConversions.random(0, (selectItemArray.length - 1));
		int selectedItem = selectItemArray[randomizeItem];
		if (selectedItem == 10) {
			createGroundItem(selectedItem, 100, x, y, p);
		} else {
			createGroundItem(selectedItem, 1, x, y, p);
		}
	}
}
