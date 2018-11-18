package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.Constants;
import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.Point;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.*;
import com.openrsc.server.plugins.listeners.executive.*;

import static com.openrsc.server.plugins.Functions.*;

public class ClockTower implements QuestInterface,TalkToNpcListener,
TalkToNpcExecutiveListener, ObjectActionListener,
ObjectActionExecutiveListener, InvUseOnObjectListener,
InvUseOnObjectExecutiveListener, WallObjectActionListener,
WallObjectActionExecutiveListener, InvUseOnGroundItemListener,
InvUseOnGroundItemExecutiveListener, PickupListener,
PickupExecutiveListener {

	@Override
	public int getQuestId() {
		return Constants.Quests.CLOCK_TOWER;
	}

	@Override
	public String getQuestName() {
		return "Clock tower (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player p) {
		incQuestReward(p, Quests.questData.get(Quests.CLOCK_TOWER), true);
		p.message("@gre@You haved gained 1 quest point!");
		p.getCache().remove("rats_dead");
		p.getCache().remove("1st_cog");
		p.getCache().remove("2nd_cog");
		p.getCache().remove("3rd_cog");
		p.getCache().remove("4th_cog");
		addItem(p, 10, 500);
	}

	/**
	 * NPCS: #366 Brother Kojo
	 */

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		if (n.getID() == 366) {
			return true;
		}
		return false;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 366) {
			switch (p.getQuestStage(this)) {
			case 0:
				playerTalk(p, n, "Hello Monk");
				npcTalk(p, n, "Hello traveller, I'm Brother Kojo",
						"Do you know the time?");
				playerTalk(p, n, "No... Sorry");
				npcTalk(p, n, "Oh dear, oh dear, I must fix the clock",
						"The town people are becoming angry",
						"Please could you help?");
				int menu = showMenu(p, n, "Ok old monk what can I do?",
						"Not now old monk");
				if (menu == 0) {
					npcTalk(p, n, "Oh thank you kind sir",
							"In the cellar below you'll find four cogs",
							"They're too heavy for me, but you should",
							"Be able to carry them one at a time",
							"One goes on each floor",
							"But I can't remember which goes where");
					playerTalk(p, n, "I'll do my best");
					npcTalk(p, n,
							"Be careful, strange beasts dwell in the cellars");
					setQuestStage(p, this, 1);
				} else if (menu == 1) {
					npcTalk(p, n, "Ok then");
				}
				break;
			case 1:
				if (p.getCache().hasKey("1st_cog")
						&& p.getCache().hasKey("2nd_cog")
						&& p.getCache().hasKey("3rd_cog")
						&& p.getCache().hasKey("4th_cog")) {
					playerTalk(p, n, "I have replaced all the cogs");
					npcTalk(p, n, "Really..? wait, listen");
					p.message("Tick Tock, Tick Tock");
					npcTalk(p, n, "Well done, well done");
					p.message("Tick Tock, Tick Tock");
					npcTalk(p, n, "Yes yes yes, you've done it",
							"You are clever");
					p.message("You have completed the clock tower quest");
					npcTalk(p, n, "That will please the village folk",
							"Please take these coins as a reward");
					p.sendQuestComplete(Constants.Quests.CLOCK_TOWER);
					return;
				}
				playerTalk(p, n, "Hello again");
				npcTalk(p, n, "Oh hello, are you having trouble?",
						"The cogs are in four rooms below us",
						"Place one cog on a pole on each",
						"Of the four tower levels");
				break;
			case -1:
				playerTalk(p, n, "Hello again Brother Kojo");
				npcTalk(p, n, "Oh hello there traveller",
						"You've done a grand job with the clock",
						"It's just like new");
				break;
			}
		}
	}

	/**
	 * Objects: #362 Clock pole blue #363 Clock pole red #364 Clock pole purple
	 * #365 Clock pole black
	 * 
	 * #372 Gates open for first large cog (rats cage) #371 Gates closed #374
	 * Second Lever (rats cage) #373 First Lever (rats cage)
	 * 
	 */
	@Override
	public boolean blockObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 362 || obj.getID() == 363 || obj.getID() == 364
				|| obj.getID() == 365) {
			return true;
		}
		if (obj.getID() == 373 || obj.getID() == 374) {
			return true;
		}
		if (obj.getID() == 371 && obj.getY() == 3475) {
			return true;
		}
		return false;
	}

	private boolean closed = false;

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 362 || obj.getID() == 363 || obj.getID() == 364
				|| obj.getID() == 365) {
			switch (p.getQuestStage(this)) {
			case 0:
			case 1:
				if (p.getCache().hasKey("1st_cog") && obj.getID() == 364
				&& obj.getX() == 581 && obj.getY() == 2525) {
					p.message("There's a large cog on this pole");
					return;
				} else if (p.getCache().hasKey("2nd_cog") && obj.getID() == 365
						&& obj.getX() == 581 && obj.getY() == 639) {
					p.message("There's a large cog on this pole");
					return;
				} else if (p.getCache().hasKey("3rd_cog") && obj.getID() == 362
						&& obj.getX() == 580 && obj.getY() == 3470) {
					p.message("There's a large cog on this pole");
					return;
				} else if (p.getCache().hasKey("4th_cog") && obj.getID() == 363
						&& obj.getX() == 582 && obj.getY() == 1582) {
					p.message("There's a large cog on this pole");
					return;
				}
				p.message("A large pole, a cog is missing");
				break;
			case -1:
				p.message("The clock is now working");
				break;
			}
		}
		if (obj.getID() == 373 || obj.getID() == 374) {
			if (closed) {
				p.message("The gate swings open");
				GameObject firstGate = new GameObject(
						obj.getID() == 373 ? Point.location(594, 3475) : Point
								.location(590, 3475), 372, 0, 0);
				World.getWorld().registerGameObject(firstGate);
				closed = false;
				if (p.getCache().hasKey("foodtrough")) {
					message(p, "The rats are eating the poison",
							"In their panic the rats bend and twist",
							"The cage bars with their teeth",
							"They're becoming weak, some have collapsed",
							"The rats are slowly dying");
					for (Npc rats : p.getViewArea().getNpcsInView()) {
						if (rats.getID() == 367) {
							rats.remove();
						}
					}
					p.getCache().remove("foodtrough");
					p.getCache().store("rats_dead", true);
				}
			} else {
				p.message("The gate creaks shut");
				GameObject secondGate = new GameObject(
						obj.getID() == 373 ? Point.location(594, 3475) : Point
								.location(590, 3475), 371, 0, 0);
				World.getWorld().registerGameObject(secondGate);
				closed = true;
			}
		}
		if (obj.getID() == 371 && obj.getY() == 3475) {
			p.message("The gate is locked");
			p.message("The gate will not open from here");
		}
	}

	/**
	 * InvUseObjects: #375 Foodtrough #731 Rat Poison used for killing rats (put
	 * poison in the trough) #730 Large cog #364 Purple clock pole (attaching)
	 */

	@Override
	public boolean blockInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 375 && item.getID() == 731) {
			return true;
		}
		if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj
				.getID() == 365)
				&& (item.getID() == 730 || item.getID() == 728
				|| item.getID() == 727 || item.getID() == 729)) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnObject(GameObject obj, Item item, Player p) {
		if (obj.getID() == 375 && item.getID() == 731) {
			p.message("You pour the rat poison into the feeding trough");
			removeItem(p, 731, 1);
			p.getCache().store("foodtrough", true);
		}
		/** TOP PURPLE POLE OTHERWISE NOT FIT MESSAGE - 1st cog **/
		if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj
				.getID() == 365)
				&& item.getID() == 730) {
			if (obj.getID() == 364 && obj.getX() == 581 && obj.getY() == 2525) {
				if (atQuestStage(p, this, 1) && !p.getCache().hasKey("1st_cog")) {
					p.message("The cog fits perfectly");
					removeItem(p, 730, 1);
					p.getCache().store("1st_cog", true);
				} else if (atQuestStage(p, this, -1)
						|| p.getCache().hasKey("1st_cog")) {
					p.message("You have already placed a cog here");
				}
			} else {
				p.message("The cog doesn't fit");
			}
		}
		/** GROUND FLOOR BLACK POLE OTHERWISE NOT FIT MESSAGE - 2nd cog **/
		if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj
				.getID() == 365)
				&& item.getID() == 728) {
			if (obj.getID() == 365 && obj.getX() == 581 && obj.getY() == 639) {
				if (atQuestStage(p, this, 1) && !p.getCache().hasKey("2nd_cog")) {
					p.message("The cog fits perfectly");
					removeItem(p, 728, 1);
					p.getCache().store("2nd_cog", true);
				} else if (atQuestStage(p, this, -1)
						|| p.getCache().hasKey("2nd_cog")) {
					p.message("You have already placed a cog here");
				}
			} else {
				p.message("The cog doesn't fit");
			}
		}
		/** BOTTOM FLOOR BLUE POLE OTHERWISE NOT FIT MESSAGE - 3rd cog **/
		if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj
				.getID() == 365)
				&& item.getID() == 727) {
			if (obj.getID() == 362 && obj.getX() == 580 && obj.getY() == 3470) {
				if (atQuestStage(p, this, 1) && !p.getCache().hasKey("3rd_cog")) {
					p.message("The cog fits perfectly");
					removeItem(p, 727, 1);
					p.getCache().store("3rd_cog", true);
				} else if (atQuestStage(p, this, -1)
						|| p.getCache().hasKey("3rd_cog")) {
					p.message("You have already placed a cog here");
				}
			} else {
				p.message("The cog doesn't fit");
			}
		}
		/** SECOND FLOOR RED POLE OTHERWISE NOT FIT MESSAGE - 4th cog **/
		if ((obj.getID() == 364 || obj.getID() == 363 || obj.getID() == 362 || obj
				.getID() == 365)
				&& item.getID() == 729) {
			if (obj.getID() == 363 && obj.getX() == 582 && obj.getY() == 1582) {
				if (atQuestStage(p, this, 1) && !p.getCache().hasKey("4th_cog")) {
					p.message("The cog fits perfectly");
					removeItem(p, 729, 1);
					p.getCache().store("4th_cog", true);
				} else if (atQuestStage(p, this, -1)
						|| p.getCache().hasKey("4th_cog")) {
					p.message("You have already placed a cog here");
				}
			} else {
				p.message("The cog doesn't fit");
			}
		}

	}

	/**
	 * Wallobjects: #111 rat cage cell
	 */
	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 111) {
			return true;
		}
		if (obj.getID() == 22 && obj.getX() == 584 && obj.getY() == 3457) {
			return true;
		}
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 111) {
			if (p.getCache().hasKey("rats_dead") || atQuestStage(p, this, -1)) {
				p.message("In a panic to escape, the rats have..");
				sleep(500);
				p.message("..bent the bars, you can just crawl through");
				if (p.getX() >= 583) {
					p.setLocation(Point.location(582, 3476), true);
				} else {
					p.setLocation(Point.location(583, 3476), true);
				}
			}
		}
		if (obj.getID() == 22 && obj.getX() == 584 && obj.getY() == 3457) {
			p.message("You just went through a secret door");
			doDoor(obj, p, 16);
		}
	}

	@Override
	public boolean blockInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
		if (myItem.getID() == 50 && item.getID() == 728) {
			return true;
		}
		return false;
	}

	@Override
	public void onInvUseOnGroundItem(Item myItem, GroundItem item, Player p) {
		if (myItem.getID() == 50 && item.getID() == 728) {
			message(p, "You pour water over the cog",
					"The cog quickly cools down");
			if (hasItem(p, 728) || hasItem(p, 730) || hasItem(p, 727)
					|| hasItem(p, 729)) {
				p.message("You can only carry one");
			} else {
				p.message("You take the cog");
				addItem(p, 728, 1);
				removeItem(p, 50, 1);
			}
		}
	}

	@Override
	public boolean blockPickup(Player p, GroundItem i) {
		if (i.getID() == 730 || i.getID() == 727 || i.getID() == 729) {
			if (hasItem(p, 730) || hasItem(p, 728) || hasItem(p, 727)
					|| hasItem(p, 729)) {
				p.message("The cogs are heavy, you can only carry one");
				return true;
			}
			return false;
		}
		if (i.getID() == 728) {
			return true;
		}
		return false;
	}

	@Override
	public void onPickup(Player p, GroundItem i) {
		if (i.getID() == 728) {
			if(p.getInventory().hasItemId(556) && p.getInventory().wielding(556)) {
				message(p, "The ice gloves cool down the cog",
						"You can carry it now");
				if (hasItem(p, 728) || hasItem(p, 730) || hasItem(p, 727)
						|| hasItem(p, 729)) {
					p.message("You can only carry one");
				} else {
					p.message("You take the cog");
					addItem(p, 728, 1);
				}
			} else if (hasItem(p, 50)) {
				message(p, "You pour water over the cog",
						"The cog quickly cools down");
				if (hasItem(p, 728) || hasItem(p, 730) || hasItem(p, 727)
						|| hasItem(p, 729)) {
					p.message("You can only carry one");
				} else {
					p.message("You take the cog");
					addItem(p, 728, 1);
					removeItem(p, 50, 1);
				}
			} else {
				message(p,
						"The cog is red hot from the flames, too hot to carry",
						"A large old cog");
				if (hasItem(p, 728) || hasItem(p, 730) || hasItem(p, 727)
						|| hasItem(p, 729)) {
					p.message("You can only carry one");
				}
			}
		}
	}
}
