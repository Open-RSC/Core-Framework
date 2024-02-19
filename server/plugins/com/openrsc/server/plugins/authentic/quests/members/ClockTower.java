package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class ClockTower implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger,
	OpBoundTrigger,
	UseObjTrigger,
	TakeObjTrigger {

	@Override
	public int getQuestId() {
		return Quests.CLOCK_TOWER;
	}

	@Override
	public String getQuestName() {
		return "Clock tower (members)";
	}

	@Override
	public int getQuestPoints() {
		return Quest.CLOCK_TOWER.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.CLOCK_TOWER.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.getCache().remove("rats_dead");
		player.getCache().remove("1st_cog");
		player.getCache().remove("2nd_cog");
		player.getCache().remove("3rd_cog");
		player.getCache().remove("4th_cog");
		give(player, ItemId.COINS.id(), 500);
	}

	/**
	 * NPCS: #366 Brother Kojo
	 */

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BROTHER_KOJO.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.BROTHER_KOJO.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "Hello Monk");
					npcsay(player, n, "Hello traveller, I'm Brother Kojo",
						"Do you know the time?");
					say(player, n, "No... Sorry");
					npcsay(player, n, "Oh dear, oh dear, I must fix the clock",
						"The town people are becoming angry",
						"Please could you help?");
					int menu = multi(player, n, "Ok old monk what can I do?",
						"Not now old monk");
					if (menu == 0) {
						npcsay(player, n, player.getText("ClockTowerKojoOhThankYou"),
							"In the cellar below you'll find four cogs",
							"They're too heavy for me, but you should",
							"Be able to carry them one at a time",
							"One goes on each floor",
							"But I can't remember which goes where");
						say(player, n, "I'll do my best");
						npcsay(player, n,
							"Be careful, strange beasts dwell in the cellars");
						setQuestStage(player, this, 1);
					} else if (menu == 1) {
						npcsay(player, n, "Ok then");
					}
					break;
				case 1:
					if (player.getCache().hasKey("1st_cog")
						&& player.getCache().hasKey("2nd_cog")
						&& player.getCache().hasKey("3rd_cog")
						&& player.getCache().hasKey("4th_cog")) {
						say(player, n, "I have replaced all the cogs");
						npcsay(player, n, "Really..? wait, listen");
						player.message("Tick Tock, Tick Tock");
						npcsay(player, n, "Well done, well done");
						player.message("Tick Tock, Tick Tock");
						npcsay(player, n, "Yes yes yes, you've done it",
							"You are clever");
						player.message("You have completed the clock tower quest");
						npcsay(player, n, "That will please the village folk",
							"Please take these coins as a reward");
						player.sendQuestComplete(Quests.CLOCK_TOWER);
						return;
					}
					say(player, n, "Hello again");
					npcsay(player, n, "Oh hello, are you having trouble?",
						"The cogs are in four rooms below us",
						"Place one cog on a pole on each",
						"Of the four tower levels");
					break;
				case -1:
					say(player, n, "Hello again Brother Kojo");
					npcsay(player, n, "Oh hello there traveller",
						"You've done a grand job with the clock",
						"It's just like new");
					break;
			}
		}
	}

	/**
	 * Objects: #362 Clock pole blue #363 Clock pole red #364 Clock pole purple
	 * #365 Clock pole black
	 * <p>
	 * #372 Gates open for first large cog (rats cage) #371 Gates closed #374
	 * Second Lever (rats cage) #373 First Lever (rats cage)
	 */
	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == 362 || obj.getID() == 363 || obj.getID() == 364 || obj.getID() == 365)
				|| (obj.getID() == 373 || obj.getID() == 374) || (obj.getID() == 371 && obj.getY() == 3475);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if (obj.getID() == 362 || obj.getID() == 363 || obj.getID() == 364 || obj.getID() == 365) {
			switch (player.getQuestStage(this)) {
				case 0:
				case 1:
					if (player.getCache().hasKey("1st_cog") && obj.getID() == 364
						&& obj.getX() == 581 && obj.getY() == 2525) {
						player.message("There's a large cog on this pole");
						return;
					} else if (player.getCache().hasKey("2nd_cog") && obj.getID() == 365
						&& obj.getX() == 581 && obj.getY() == 639) {
						player.message("There's a large cog on this pole");
						return;
					} else if (player.getCache().hasKey("3rd_cog") && obj.getID() == 362
						&& obj.getX() == 580 && obj.getY() == 3470) {
						player.message("There's a large cog on this pole");
						return;
					} else if (player.getCache().hasKey("4th_cog") && obj.getID() == 363
						&& obj.getX() == 582 && obj.getY() == 1582) {
						player.message("There's a large cog on this pole");
						return;
					}
					player.message("A large pole, a cog is missing");
					break;
				case -1:
					player.message("The clock is now working");
					break;
			}
		}
		else if (obj.getID() == 373 || obj.getID() == 374) {
			GameObject dynGate, statGate, newGate;
			boolean correctSetup = false;
			if (obj.getID() == 373) {
				dynGate = player.getWorld().getRegionManager().getRegion(Point.location(594, 3475)).getGameObject(Point.location(594, 3475), player);
				statGate = player.getWorld().getRegionManager().getRegion(Point.location(590, 3475)).getGameObject(Point.location(590, 3475), player);
				//outer gate was open + inner gate is open
				correctSetup = (dynGate.getID() == 372) && (statGate.getID() == 372);
			} else {
				dynGate = player.getWorld().getRegionManager().getRegion(Point.location(590, 3475)).getGameObject(Point.location(590, 3475), player);
				statGate = player.getWorld().getRegionManager().getRegion(Point.location(594, 3475)).getGameObject(Point.location(594, 3475), player);
				//inner gate was closed + outer gate is closed
				correctSetup = (dynGate.getID() == 371) && (statGate.getID() == 371);
			}
			//gate closed
			if (dynGate.getID() == 371) {
				player.message("The gate swings open");
				newGate = new GameObject(player.getWorld(), dynGate.getLocation(), 372, 0, 0);
				player.getWorld().registerGameObject(newGate);
			}
			//gate open
			else {
				player.message("The gate creaks shut");
				newGate = new GameObject(player.getWorld(), dynGate.getLocation(), 371, 0, 0);
				player.getWorld().registerGameObject(newGate);
			}

			if (player.getCache().hasKey("foodtrough") && correctSetup) {
				mes("In their panic the rats bend and twist");
				delay(3);
				mes("The cage bars with their teeth");
				delay(3);
				mes("They're becoming weak, some have collapsed");
				delay(3);
				mes("The rats are eating the poison");
				delay(3);
				mes("They're becoming weak, some have collapsed");
				delay(3);
				mes("The rats are slowly dying");
				delay(3);
				for (Npc rats : player.getViewArea().getNpcsInView()) {
					if (rats.getID() == NpcId.DUNGEON_RAT.id()) {
						rats.remove();
					}
				}
				player.getCache().remove("foodtrough");
				player.getCache().store("rats_dead", true);
			}
		}
		else if (obj.getID() == 371 && obj.getY() == 3475) {
			player.message("The gate is locked");
			if (player.getConfig().WANT_FIXED_BROKEN_MECHANICS && player.getX() == obj.getX() - 1) {
				// Custom behaviour, to prevent players getting locked in the dungeon rat cage.
				// Getting trapped by another player was not captured in replays,
				// but according to player MOONBOLT, it was authentic & you could really trap people here in RSC.
				//
				// It has been a problem on the live server lately,
				// since this bug effectively allows anyone to ban a player who happens to be between the gates.
				delay(2);
				player.message("but it looks like you might be able to climb over");
				player.message("Would you like to give it a shot?");
				int climbGate = multi(player, "Yes, climb the gate", "No, I'll stay here for now");
				if (climbGate == -1) return;
				if (climbGate == 0) {
					player.message("you place your feet on the horizontal bars of the gate and try to lift yourself up");
					delay(3);
					if (Formulae.cutWeb()) {
						player.message("You hop the gate");
						player.setLocation(new Point(obj.getX(), player.getY()));
					} else {
						if (player.getSkills().getLevel(Skill.HITS.id()) > 5) {
							player.message("You cut yourself on the pointy bars of the gate...");
							player.damage(1);
						} else {
							player.message("you scrape against the top of the ceiling");
						}
						player.setLocation(new Point(obj.getX(), player.getY()));
						player.message("but manage to make it over");
					}
				} else {
					player.message("You decide to wait instead.");
				}
			} else {
				player.message("The gate will not open from here");
			}
		}
	}

	/**
	 * InvUseObjects: #375 Foodtrough #731 Rat Poison used for killing rats (put
	 * poison in the trough) #730 Large cog #364 Purple clock pole (attaching)
	 */

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return (obj.getID() == 375 && item.getCatalogId() == ItemId.RAT_POISON.id()) ||
				((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj.getID() == 365)
				&& (item.getCatalogId() == ItemId.LARGE_COG_PURPLE.id() || item.getCatalogId() == ItemId.LARGE_COG_BLACK.id()
				|| item.getCatalogId() == ItemId.LARGE_COG_BLUE.id() || item.getCatalogId() == ItemId.LARGE_COG_RED.id()));
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 375 && item.getCatalogId() == ItemId.RAT_POISON.id()) {
			player.message("You pour the rat poison into the feeding trough");
			player.getCarriedItems().remove(new Item(ItemId.RAT_POISON.id()));
			player.getCache().store("foodtrough", true);
		}
		/** TOP PURPLE POLE OTHERWISE NOT FIT MESSAGE - 1st cog **/
		else if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj.getID() == 365)
			&& item.getCatalogId() == ItemId.LARGE_COG_PURPLE.id()) {
			if (obj.getID() == 364 && obj.getX() == 581 && obj.getY() == 2525) {
				if (atQuestStage(player, this, 1) && !player.getCache().hasKey("1st_cog")) {
					player.message("The cog fits perfectly");
					player.getCarriedItems().remove(new Item(ItemId.LARGE_COG_PURPLE.id()));
					player.getCache().store("1st_cog", true);
				} else if (atQuestStage(player, this, -1)
					|| player.getCache().hasKey("1st_cog")) {
					player.message("You have already placed a cog here");
				}
			} else {
				player.message("The cog doesn't fit");
			}
		}
		/** GROUND FLOOR BLACK POLE OTHERWISE NOT FIT MESSAGE - 2nd cog **/
		else if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj.getID() == 365)
			&& item.getCatalogId() == ItemId.LARGE_COG_BLACK.id()) {
			if (obj.getID() == 365 && obj.getX() == 581 && obj.getY() == 639) {
				if (atQuestStage(player, this, 1) && !player.getCache().hasKey("2nd_cog")) {
					player.message("The cog fits perfectly");
					player.getCarriedItems().remove(new Item(ItemId.LARGE_COG_BLACK.id()));
					player.getCache().store("2nd_cog", true);
				} else if (atQuestStage(player, this, -1)
					|| player.getCache().hasKey("2nd_cog")) {
					player.message("You have already placed a cog here");
				}
			} else {
				player.message("The cog doesn't fit");
			}
		}
		/** BOTTOM FLOOR BLUE POLE OTHERWISE NOT FIT MESSAGE - 3rd cog **/
		else if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj.getID() == 365)
			&& item.getCatalogId() == ItemId.LARGE_COG_BLUE.id()) {
			if (obj.getID() == 362 && obj.getX() == 580 && obj.getY() == 3470) {
				if (atQuestStage(player, this, 1) && !player.getCache().hasKey("3rd_cog")) {
					player.message("The cog fits perfectly");
					player.getCarriedItems().remove(new Item(ItemId.LARGE_COG_BLUE.id()));
					player.getCache().store("3rd_cog", true);
				} else if (atQuestStage(player, this, -1)
					|| player.getCache().hasKey("3rd_cog")) {
					player.message("You have already placed a cog here");
				}
			} else {
				player.message("The cog doesn't fit");
			}
		}
		/** SECOND FLOOR RED POLE OTHERWISE NOT FIT MESSAGE - 4th cog **/
		else if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj.getID() == 365)
			&& item.getCatalogId() == ItemId.LARGE_COG_RED.id()) {
			if (obj.getID() == 363 && obj.getX() == 582 && obj.getY() == 1582) {
				if (atQuestStage(player, this, 1) && !player.getCache().hasKey("4th_cog")) {
					player.message("The cog fits perfectly");
					player.getCarriedItems().remove(new Item(ItemId.LARGE_COG_RED.id()));
					player.getCache().store("4th_cog", true);
				} else if (atQuestStage(player, this, -1)
					|| player.getCache().hasKey("4th_cog")) {
					player.message("You have already placed a cog here");
				}
			} else {
				player.message("The cog doesn't fit");
			}
		}

	}

	/**
	 * Wallobjects: #111 rat cage cell
	 */
	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return (obj.getID() == 111) || (obj.getID() == 22 && obj.getX() == 584 && obj.getY() == 3457);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == 111) {
			if (player.getCache().hasKey("rats_dead") || atQuestStage(player, this, -1)) {
				player.message("In a panic to escape, the rats have..");
				delay();
				player.message("..bent the bars, you can just crawl through");
				if (player.getX() >= 583) {
					player.setLocation(Point.location(582, 3476), true);
				} else {
					player.setLocation(Point.location(583, 3476), true);
				}
			}
		}
		else if (obj.getID() == 22 && obj.getX() == 584 && obj.getY() == 3457) {
			player.playSound("secretdoor");
			player.message("You just went through a secret door");
			doDoor(obj, player, 16);
		}
	}

	@Override
	public boolean blockUseObj(Player player, GroundItem item, Item myItem) {
		return myItem.getCatalogId() == ItemId.BUCKET_OF_WATER.id() && item.getID() == ItemId.LARGE_COG_BLACK.id();
	}

	@Override
	public void onUseObj(Player player, GroundItem item, Item myItem) {
		if (myItem.getCatalogId() == ItemId.BUCKET_OF_WATER.id() && item.getID() == ItemId.LARGE_COG_BLACK.id()) {
			mes("You pour water over the cog");
			delay(3);
			mes("The cog quickly cools down");
			delay(3);
			if (player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLACK.id(), Optional.empty())
				|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_PURPLE.id(), Optional.empty())
				|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLUE.id(), Optional.empty())
				|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_RED.id(), Optional.empty())) {
				player.message("You can only carry one");
			} else {
				player.message("You take the cog");
				give(player, ItemId.LARGE_COG_BLACK.id(), 1);
				player.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id()));
			}
		}
	}

	@Override
	public boolean blockTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.LARGE_COG_PURPLE.id() || i.getID() == ItemId.LARGE_COG_BLUE.id() || i.getID() == ItemId.LARGE_COG_RED.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_PURPLE.id(), Optional.empty())
				|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLACK.id(), Optional.empty())
				|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLUE.id(), Optional.empty())
				|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_RED.id(), Optional.empty())) {
				player.message("The cogs are heavy, you can only carry one");
				return true;
			}
			return false;
		}
		else if (i.getID() == ItemId.LARGE_COG_BLACK.id()) {
			return true;
		}
		return false;
	}

	@Override
	public void onTakeObj(Player player, GroundItem i) {
		if (i.getID() == ItemId.LARGE_COG_BLACK.id()) {
			if (player.getCarriedItems().hasCatalogID(ItemId.ICE_GLOVES.id()) && player.getCarriedItems().getEquipment().hasEquipped(ItemId.ICE_GLOVES.id())) {
				mes("The ice gloves cool down the cog");
				delay(3);
				mes("You can carry it now");
				delay(3);
				if (player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLACK.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_PURPLE.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLUE.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_RED.id(), Optional.empty())) {
					player.message("You can only carry one");
				} else {
					player.message("You take the cog");
					give(player, ItemId.LARGE_COG_BLACK.id(), 1);
				}
			} else if (player.getCarriedItems().hasCatalogID(ItemId.BUCKET_OF_WATER.id(), Optional.of(false))) {
				mes("You pour water over the cog");
				delay(3);
				mes("The cog quickly cools down");
				delay(3);
				if (player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLACK.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_PURPLE.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLUE.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_RED.id(), Optional.empty())) {
					player.message("You can only carry one");
				} else {
					player.message("You take the cog");
					give(player, ItemId.LARGE_COG_BLACK.id(), 1);
					player.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id()));
				}
			} else {
				mes("The cog is red hot from the flames, too hot to carry");
				delay(3);
				mes("The cogs are heavy");
				delay(3);
				if (player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLACK.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_PURPLE.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_BLUE.id(), Optional.empty())
					|| player.getCarriedItems().hasCatalogID(ItemId.LARGE_COG_RED.id(), Optional.empty())) {
					player.message("You can only carry one");
				}
			}
		}
	}
}
