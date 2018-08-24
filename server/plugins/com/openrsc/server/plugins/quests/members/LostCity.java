package com.openrsc.server.plugins.quests.members;

import static com.openrsc.server.plugins.Functions.CRAFTING;
import static com.openrsc.server.plugins.Functions.PRAYER;
import static com.openrsc.server.plugins.Functions.WOODCUT;
import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.atQuestStage;
import static com.openrsc.server.plugins.Functions.atQuestStages;
import static com.openrsc.server.plugins.Functions.attack;
import static com.openrsc.server.plugins.Functions.completeQuest;
import static com.openrsc.server.plugins.Functions.doDoor;
import static com.openrsc.server.plugins.Functions.getCurrentLevel;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.getQuestStage;
import static com.openrsc.server.plugins.Functions.getWoodcutAxe;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.inArray;
import static com.openrsc.server.plugins.Functions.isNpcNearby;
import static com.openrsc.server.plugins.Functions.isWielding;
import static com.openrsc.server.plugins.Functions.kill;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.movePlayer;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;
import static com.openrsc.server.plugins.Functions.setCurrentLevel;
import static com.openrsc.server.plugins.Functions.setQuestStage;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;
import static com.openrsc.server.plugins.Functions.spawnNpc;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.listeners.action.InvUseOnItemListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.PlayerKilledNpcListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.InvUseOnItemExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerAttackNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.PlayerKilledNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;

/**
 * Rewritten in Java.
 * 
 * @author n0m
 * 
 */
public class LostCity implements QuestInterface, TalkToNpcListener,
		TalkToNpcExecutiveListener, ObjectActionListener,
		ObjectActionExecutiveListener, PlayerAttackNpcExecutiveListener,
		PlayerKilledNpcListener, PlayerKilledNpcExecutiveListener,
		InvUseOnItemListener, InvUseOnItemExecutiveListener,
		WallObjectActionListener, WallObjectActionExecutiveListener {
	/* Objects */
	final int LEPROCHAUN_TREE = 237, ENTRANA_LADDER = 244, DRAMEN_TREE = 245,
			MAGIC_DOOR = 65, ZANARIS_DOOR = 66;
	/* Npcs */
	final int ADVENTURER_CLERIC = 207, ADVENTURER_WIZARD = 208,
			ADVENTURER_WARRIOR = 209, ADVENTURER_ARCHER = 210,
			LEPRECHAUN = 211, MONK_OF_ENTRANA = 213, TREE_SPIRIT = 216;

	/**
	 * Quest stage 1: Talked to adventurer Quest stage 2: Talked to leprechaun
	 * Quest stage 3: Chopped the spirit tree Quest stage 4: Defeated the spirit
	 * tree.
	 */
	@Override
	public int getQuestId() {
		return 18;
	}

	@Override
	public String getQuestName() {
		return "Lost City (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.incQuestPoints(3);
		player.message("Well done you have completed the Lost City of Zanaris quest");
		player.message("@gre@You have gained 3 quest points!");
	}

	@Override
	public boolean blockObjectAction(GameObject obj, String command,
			Player player) {
		return inArray(obj.getID(), LEPROCHAUN_TREE, ENTRANA_LADDER,
				DRAMEN_TREE);
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		switch (obj.getID()) {
		case 244:
			Npc monk = getNearestNpc(p, MONK_OF_ENTRANA, 10);
			if (monk != null)
				monk.initializeTalkScript(p);
			break;
		case 237:
			if (atQuestStage(p, this, 0)) {
				p.message("There is nothing in this tree");
			} else if (getQuestStage(p, this) >= 1
					&& getQuestStage(p, this) <= 3) {
				Npc leprechaun = getNearestNpc(p, 211, 15);
				if (leprechaun == null) {
					leprechaun = spawnNpc(211, p.getX(), p.getY() + 1, 60000 * 3);
					leprechaun.initializeTalkScript(p);
				}
			} else {
				p.message("There is nothing in this tree");
			}
			break;
		case 245:
			if (atQuestStages(p, this, 4, 3, 2, -1)) {
				if (getCurrentLevel(p, WOODCUT) < 36) {
					message(p,
							"You are not a high enough woodcutting level to chop down this tree",
							"You need a woodcutting level of 36");
					return;
				}
				if (getWoodcutAxe(p) == -1) {
					p.message("You need an axe to chop this tree down");
					return;
				}

				/*
				 * New method I made, quite useful, no need for OR checks
				 * anymore
				 */
				if (atQuestStages(p, this, 4, -1)) {
					message(p, "You attempt to chop the tree",
							"You manage to cut off a dramen branch");
					addItem(p, 510, 1);
					return;
				}
				if (isNpcNearby(p, 216)) {
					Npc spawnedTreeSpirit = getNearestNpc(p, 216, 15);
					/*
					 * Check if the spawned tree spirit contains spawnedFor
					 * attribute
					 */
					if(spawnedTreeSpirit != null) {
						if (spawnedTreeSpirit.getAttribute("spawnedFor") != null) {
							/* Check if the spawned tree spirit was spawned for us */
							if (spawnedTreeSpirit.getAttribute("spawnedFor")
									.equals(p)) {
								attack(spawnedTreeSpirit, p);
								return;
							}
						}
					}
				}
				message(p, "You attempt to chop the tree");
				Npc treeSpirit = spawnNpc(216, p.getX() + 1, p.getY() + 1,
						300000, p);
				if (treeSpirit == null) {
					return;
				}
				sleep(2000);
				attack(treeSpirit, p);
				if (atQuestStages(p, this, 2)) {
					setQuestStage(p, this, 3);
				}
			}
			break;
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		/* Another new method I made, really useful. */
		return inArray(n.getID(), ADVENTURER_ARCHER, ADVENTURER_CLERIC,
				ADVENTURER_WARRIOR, ADVENTURER_WIZARD, LEPRECHAUN,
				MONK_OF_ENTRANA);
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == LEPRECHAUN) {
			if (atQuestStage(p, this, 1)) {
				npcTalk(p, n, "Ay you big elephant", "you have caught me",
						"What would you be wanting with Old Shamus then");
				playerTalk(p, n, "I want to find zanaris");
				npcTalk(p, n, "Zanaris?",
						"You need to go in the funny little shed",
						"in the middle of the swamp");
				playerTalk(p, n, "Oh I thought zanaris was a city");
				npcTalk(p, n, "it is");
				showMenu(p, n, "How does it fit in a shed then?",
						"I've been in that shed, I didn't see a city");
				/* Regardless the option, the dialogue is the same. */
				npcTalk(p, n, "Silly person", "the city isn't in the shed",
						"the shed is a portal to Zanaris");
				playerTalk(p, n,
						"So I just want into the shed and end up in Zanaris?");
				npcTalk(p, n, "Oh I didn't say?",
						"You need to be carrying around a dramenwood staff",
						"otherwise you do just end up in a shed");
				playerTalk(p, n, "so where would I get a staff?");
				npcTalk(p, n, "Dramenwood branches are crafted from branches",
						"these staffs are cut from the Dramen tree",
						"located somewhere in a cave on the island of entrana",
						"I believe the monks of Entrana have recently",
						"start running a ship from port sarim to Entrana");
				setQuestStage(p, this, 2);
				p.message("The leprechaun magically disappears");
				n.remove();
			}
		}
		if (inArray(n.getID(), ADVENTURER_ARCHER, ADVENTURER_CLERIC,
				ADVENTURER_WARRIOR, ADVENTURER_WIZARD)) {
			if (atQuestStage(p, this, 0)) {
				npcTalk(p, n, "hello traveler");
				int option = showMenu(p, n,
						"what are you camped out here for?",
						"Do you know any good adventures I can go on?");
				if (option == 0) {
					npcTalk(p, n, "we're looking for Zanaris");
					int sub_option = showMenu(p, n, "Who's Zanaris",
							"What's Zanaris",
							"what makes you think its out here");
					if (sub_option == 0 || sub_option == 2) {
						if (sub_option == 0)
							npcTalk(p, n, "here Zanaris isn't a person",
									"It's a magical hidden city");
						else
							npcTalk(p, n, "Don't you know of the legends?",
									"of the magical city, hidden in the swamp");
						ZANARIS_MENU(p, n);
					} else if (sub_option == 1) {
						npcTalk(p, n,
								"I don't think we want other people competing with us to finish");
						int next_option = showMenu(p, n, "Please tell me",
								"Oh well never mind");
						if (next_option == 0) {
							npcTalk(p, n, "No");
						}
					}
				} else if (option == 1) {
					npcTalk(p, n, "No sorry I don't");
				} 
			} else if (atQuestStage(p, this, 1)) {
				playerTalk(
						p,
						n,
						"So let me get this straight",
						"I need to search the trees near here for a leprechaun?",
						"and he will tell me where Zanaris is?");
				npcTalk(p, n, "That is what the legends and rumours are, yes");
			} else if (atQuestStage(p, this, 2)) {
				playerTalk(p, n, "thankyou for your information",
						"it has helped me a lot in my quest to find Zanaris");
				npcTalk(p, n, "so what have you found out?",
						"Where is Zanaris?");
				playerTalk(p, n, "I think I will keep that to myself");
			}
		} else if (n.getID() == MONK_OF_ENTRANA) {
			npcTalk(p, n, "Be careful going down there",
					"You are unarmed, and there is much evilness lurking",
					"The evilness seems to block off our contact with our god",
					"Our prayers seem to have less effect down there",
					"Oh also you won't be able to come back this way",
					"This ladder only goes one way",
					"The only way out is a portal which leads deep into the wilderness");
			int option = showMenu(p, n,
					"I don't think I'm strong enough to enter then",
					"Well that is a risk I will have to take");
			if (option == 1) {
				p.message("You climb down the ladder");
				sleep(1000);
				movePlayer(p, 427, 3380, true);
				/* What is the point of this? */
				if (getCurrentLevel(p, PRAYER) <= 3)
					setCurrentLevel(p, PRAYER, 1);
				else
					setCurrentLevel(p, PRAYER, 3);
			}
		}
	}

	public void ZANARIS_MENU(Player p, Npc n) {
		int next_option = showMenu(p, n,
				"If it's hidden how are planning to find it",
				"There's no such thing");
		if (next_option == 0) {
			npcTalk(p, n, "well we dont wan't to tell others that",
					"we all want the glory to find it ourselves");
			int after_option = showMenu(p, n, "please tell me",
					"looks like you don't know either if you're sitting around here");
			if (after_option == 0) {
				npcTalk(p, n, "No");
			} else if (after_option == 1) {
				npcTalk(p,
						n,
						"of course we know",
						"We haven't worked out which tree the stupid leprechaun is hiding in",
						"oops didn't mean to tell you that");
				playerTalk(p, n, "So a leprechaun knows where Zanaris is?");
				npcTalk(p, n, "eerm", "yes");
				playerTalk(p, n, "and he's in a tree somewhere around here",
						"thankyou very much");
				setQuestStage(p, this, 1);
			}
		} else if (next_option == 1) {
			npcTalk(p, n, "hehe thats what you think");
		}
	}

	@Override
	public boolean blockPlayerAttackNpc(Player p, Npc n) {
		if (n.getID() == 216) {
			if (n.getAttribute("spawnedFor", null) != null) {
				if (!n.getAttribute("spawnedFor").equals(p)) {
					p.message("That npc is not after you.");
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean blockPlayerKilledNpc(Player p, Npc n) {
		return n.getID() == TREE_SPIRIT;
	}

	@Override
	public void onPlayerKilledNpc(Player p, Npc n) {
		if (atQuestStage(p, this, 3)) {
			kill(n, p);
			setQuestStage(p, this, 4);
		}
	}

	@Override
	public boolean blockInvUseOnItem(Player player, Item item1, Item item2) {
		return item1.getID() == 13 && item2.getID() == 510
				|| item1.getID() == 510 && item2.getID() == 13;
	}

	@Override
	public void onInvUseOnItem(Player p, Item item1, Item item2) {
		if (hasItem(p, 510, 1)) {
			if (getCurrentLevel(p, CRAFTING) < 31) {
				message(p,
						"You are not a high enough crafting level to craft this staff",
						"You need a crafting level of 31");
				return;
			}
			removeItem(p, 510, 1);
			message(p, "You craft a dramen staff out of the branch");
			addItem(p, 509, 1);
		}
	}

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click,
			Player player) {
		return inArray(obj.getID(), MAGIC_DOOR, ZANARIS_DOOR);
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == MAGIC_DOOR) {
			movePlayer(p, 109, 245);
			sleep(500);
			p.message("you go through the door and find yourself somewhere else");
		} else if (obj.getID() == ZANARIS_DOOR) {
			if (isWielding(p, 509) && atQuestStages(p, this, 4, -1)) {
				p.setBusy(true);
				message(p, "The world starts to shimmer",
							"You find yourself in different surroundings");
				if (getQuestStage(p, this) != -1) {
					movePlayer(p, 126, 3518, true);
					completeQuest(p, this);
				} else {
					movePlayer(p, 126, 3518, true);
				}
				p.setBusy(false);
			} else {
				doDoor(obj, p);
				p.message("you go through the door and find yourself in a shed.");
			}
		}

	}
}
