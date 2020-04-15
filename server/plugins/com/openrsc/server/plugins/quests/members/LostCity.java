package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.SingleEvent;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

/**
 * Rewritten in Java.
 *
 * @author n0m
 */
public class LostCity implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	KillNpcTrigger,
	UseInvTrigger,
	OpBoundTrigger,
	AttackNpcTrigger {
	private static final Logger LOGGER = LogManager.getLogger(LostCity.class);

	/* Objects */
	final int LEPROCHAUN_TREE = 237, ENTRANA_LADDER = 244, DRAMEN_TREE = 245,
		MAGIC_DOOR = 65, ZANARIS_DOOR = 66;

	/**
	 * Quest stage 1: Talked to adventurer Quest stage 2: Talked to leprechaun
	 * Quest stage 3: Chopped the spirit tree Quest stage 4: Defeated the spirit
	 * tree.
	 */
	@Override
	public int getQuestId() {
		return Quests.LOST_CITY;
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
		incQuestReward(player, player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.LOST_CITY), true);
		player.message("@gre@You haved gained 3 quest points!");
		player.message("Well done you have completed the Lost City of Zanaris quest");
	}

	@Override
	public boolean blockOpLoc(GameObject obj, String command,
							  Player player) {
		return inArray(obj.getID(), LEPROCHAUN_TREE, ENTRANA_LADDER,
			DRAMEN_TREE);
	}

	@Override
	public void onOpLoc(GameObject obj, String command, Player p) {
		switch (obj.getID()) {
			case 244:
				Npc monk = ifnearvisnpc(p, NpcId.MONK_OF_ENTRANA_ENTRANA.id(), 10);
				if (monk != null)
					monk.initializeTalkScript(p);
				break;
			case 237:
				if (atQuestStage(p, this, 0)) {
					p.message("There is nothing in this tree");
				} else if (getQuestStage(p, this) >= 1
					&& getQuestStage(p, this) <= 3) {
					Npc leprechaun = ifnearvisnpc(p, NpcId.LEPRECHAUN.id(), 15);
					if (leprechaun != null) {
						p.message("There is nothing in this tree");
					} else {
						p.message("A Leprechaun jumps down from the tree and runs off");
						final Npc lepr = addnpc(p.getWorld(), NpcId.LEPRECHAUN.id(), 172, 661, 60000 * 3);
						p.setBusyTimer(1800);
						lepr.walk(173, 661);
						try {
							p.getWorld().getServer().getGameEventHandler().add(new SingleEvent(p.getWorld(),null, 600, "Lost City Leprechaun", true) {
								@Override
								public void action() {
									lepr.walk(177, 661 + DataConversions.random(0, 10) - 5);
								}
							});
						} catch (Exception e) {
							LOGGER.catching(e);
						}

					}
				} else {
					p.message("There is nothing in this tree");
				}
				break;
			case 245:
				if (atQuestStages(p, this, 4, 3, 2, -1)) {
					if (getCurrentLevel(p, Skills.WOODCUT) < 36) {
						Functions.mes(p,
							"You are not a high enough woodcutting level to chop down this tree",
							"You need a woodcutting level of 36");
						return;
					}
					if (getWoodcutAxe(p) == -1) {
						p.message("You need an axe to chop down this tree");
						return;
					}

					/*
					 * New method I made, quite useful, no need for OR checks
					 * anymore
					 */
					if (atQuestStages(p, this, 4, -1)) {
						Functions.mes(p, "You cut a branch from the Dramen tree");
						give(p, ItemId.DRAMEN_BRANCH.id(), 1);
						return;
					}
					Npc spawnedTreeSpirit = ifnearvisnpc(p, NpcId.TREE_SPIRIT.id(), 15);
					/*
					 * Check if the spawned tree spirit contains spawnedFor
					 * attribute
					 */
					if (spawnedTreeSpirit != null) {
						if (spawnedTreeSpirit.getAttribute("spawnedFor") != null) {
							/* Check if the spawned tree spirit was spawned for us */
							if (spawnedTreeSpirit.getAttribute("spawnedFor")
								.equals(p)) {
								npcsay(p, spawnedTreeSpirit, "Stop",
									"I am the spirit of the Dramen Tree",
									"You must come through me before touching that tree");
								return;
							}
						}
					}
					Npc treeSpirit = addnpc(NpcId.TREE_SPIRIT.id(), p.getX() + 1, p.getY() + 1,
						300000, p);
					if (treeSpirit == null) {
						return;
					}
					delay(2000);
					npcsay(p, treeSpirit, "Stop",
						"I am the spirit of the Dramen Tree",
						"You must come through me before touching that tree");
					if (atQuestStages(p, this, 2)) {
						setQuestStage(p, this, 3);
					}
				}
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(), NpcId.ADVENTURER_WARRIOR.id(),
				NpcId.ADVENTURER_WIZARD.id(), NpcId.LEPRECHAUN.id(), NpcId.MONK_OF_ENTRANA_ENTRANA.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (n.getID() == NpcId.LEPRECHAUN.id()) {
			if (atQuestStage(p, this, 0)) {
				npcsay(p, n, "Ay you big elephant", "You have caught me",
					"What would you be wanting with old Shamus then?");
				say(p, n, "I'm not sure");
				npcsay(p, n, "Well you'll have to catch me again when you are");
				p.message("The leprechaun magically disapeers");
				n.remove();
			} else if (atQuestStage(p, this, 1)) {
				npcsay(p, n, "Ay you big elephant", "You have caught me",
					"What would you be wanting with old Shamus then?");
				say(p, n, "I want to find Zanaris");
				npcsay(p, n, "Zanaris?",
					"You need to go in the funny little shed",
					"in the middle of the swamp");
				say(p, n, "Oh I thought Zanaris was a city");
				npcsay(p, n, "It is");
				int menu = multi(p, n, false, //do not send over
					"How does it fit in a shed then?",
					"I've been in that shed, I didn't see a city");
				if (menu == 0) {
					say(p, n, "How does it fit in a shed then?");
					npcsay(p, n, "The city isn't in the shed",
						"The shed is a portal to Zanaris");
					say(p, n, "So I just walk into the shed and end up in Zanaris?");
				} else if (menu == 1) {
					say(p, n, "I've been in that shed",
						"I didn't see a city");
				}
				npcsay(p, n, "Oh didn't I say?",
					"You need to be carrying a Dramenwood staff",
					"Otherwise you do just end up in a shed");
				say(p, n, "So where would I get a staff?");
				npcsay(p, n, "Dramenwood staffs are crafted from branches",
					"These staffs are cut from the Dramen tree",
					"located somewhere in a cave on the island of Entrana",
					"I believe the monks of Entrana have recetnly",
					"Started running a ship from port sarim to Entrana");
				setQuestStage(p, this, 2);
				p.message("The leprechaun magically disapeers");
				n.remove();
			} else if (atQuestStages(p, this, 4, 3, 2, -1)) {
				npcsay(p, n, "Ay you big elephant",
					"You have caught me",
					"What would you be wanting with old Shamus then?");
				int menu = multi(p, n, "I'm not sure", "How do I get to Zanaris again?");
				if (menu == 0) {
					npcsay(p, n, "I dunno, what stupid people",
						"Who go to all the trouble to catch leprechaun's",
						"When they don't even know what they want");
					p.message("The leprechaun magically disapeers");
					n.remove();
				} else if (menu == 1) {
					npcsay(p, n, "You need to enter the shed in the middle of the swamp",
						"While holding a dramenwood staff",
						"Made from a branch",
						"Cut from the dramen tree on the island of Entrana");
					p.message("The leprechaun magically disapeers");
					n.remove();
				}
			}
		}
		else if (DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(),
				NpcId.ADVENTURER_WARRIOR.id(), NpcId.ADVENTURER_WIZARD.id()}, n.getID())) {
			if (atQuestStage(p, this, 0)) {
				npcsay(p, n, "hello traveller");
				int option = multi(p, n, false, //do not send over
					"What are you camped out here for?",
					"Do you know any good adventures I can go on?");
				if (option == 0) {
					say(p, n, "What are you camped here for?");
					npcsay(p, n, "We're looking for Zanaris");
					int sub_option = multi(p, n, "Who's Zanaris?",
						"what's Zanaris?",
						"What makes you think it's out here");
					if (sub_option == 0 || sub_option == 2) {
						if (sub_option == 0)
							npcsay(p, n, "hehe Zanaris isn't a person",
								"It's a magical hidden city");
						else
							npcsay(p, n, "Don't you know of the legends?",
								"of the magical city, hidden in the swamp");
						ZANARIS_MENU(p, n);
					} else if (sub_option == 1) {
						npcsay(p, n,
							"I don't think we want other people competing with us to find it");
						int next_option = multi(p, n, "Please tell me",
							"Oh well never mind");
						if (next_option == 0) {
							npcsay(p, n, "No");
						}
					}
				} else if (option == 1) {
					say(p, n, "Do you know any good adventures I can go on");
					npcsay(p, n, "Well we're on an adventure now",
						"Mind you this is our adventure",
						"We don't want to share it - find your own");
					int insist = multi(p, n, "Please tell me",
						"I don't think you've found a good adventure at all");
					if (insist == 0) {
						npcsay(p, n, "No");
					} else if (insist == 1) {
						npcsay(p, n, "We're on one of the greatest adventures I'll have you know",
							"Searching for Zanaris isn't a walk in the park");
						int sub_option = multi(p, n, "Who's Zanaris?",
							"what's Zanaris?",
							"What makes you think it's out here");
						if (sub_option == 0 || sub_option == 2) {
							if (sub_option == 0)
								npcsay(p, n, "hehe Zanaris isn't a person",
									"It's a magical hidden city");
							else
								npcsay(p, n, "Don't you know of the legends?",
									"of the magical city, hidden in the swamp");
							ZANARIS_MENU(p, n);
						} else if (sub_option == 1) {
							npcsay(p, n,
								"I don't think we want other people competing with us to find it");
							int next_option = multi(p, n, "Please tell me",
								"Oh well never mind");
							if (next_option == 0) {
								npcsay(p, n, "No");
							}
						}
					}
				}
			} else if (atQuestStage(p, this, 1)) {
				say(
					p,
					n,
					"So let me get this straight",
					"I need to search the trees near here for a leprechaun?",
					"And he will tell me where Zanaris is?");
				npcsay(p, n, "That is what the legends and rumours are,yes");
			} else if (atQuestStages(p, this, 4, 3, 2, -1)) {
				say(p, n, "thankyou for your information",
					"It has helped me a lot in my quest to find Zanaris");
				npcsay(p, n, "So what have you found out?",
					"Where is Zanaris?");
				say(p, n, "I think I will keep that to myself");
			}
		} else if (n.getID() == NpcId.MONK_OF_ENTRANA_ENTRANA.id()) {
			npcsay(p, n, "Be careful going in there",
				"You are unarmed, and there is much evilness lurking down there",
				"The evilness seems to block off our contact with our gods",
				"Our prayers seem to have less effect down there",
				"Oh also you won't be able to come back this way",
				"This ladder only goes one way",
				"The only way out is a portal which leads deep into the wilderness");
			int option = multi(p, n,
				"I don't think I'm strong enough to enter then",
				"Well that is a risk I will have to take");
			if (option == 1) {
				p.message("You climb down the ladder");
				delay(1000);
				teleport(p, 427, 3380);
				/* What is the point of this? */
				if (getCurrentLevel(p, Skills.PRAYER) <= 3)
					setCurrentLevel(p, Skills.PRAYER, 1);
				else if (getCurrentLevel(p, Skills.PRAYER) <= 39)
					setCurrentLevel(p, Skills.PRAYER, 2);
				else
					setCurrentLevel(p, Skills.PRAYER, 3);
			}
		}
	}

	public void ZANARIS_MENU(Player p, Npc n) {
		int next_option = multi(p, n,
			"If it's hidden how are you planning to find it",
			"There's no such thing");
		if (next_option == 0) {
			npcsay(p, n, "Well we don't want to tell others that",
				"We want all the glory of finding it for ourselves");
			int after_option = multi(p, n, false, //do not send over
				"please tell me",
				"looks like you don't know either if you're sitting around here");
			if (after_option == 0) {
				say(p, n, "Please tell me");
				npcsay(p, n, "No");
			} else if (after_option == 1) {
				say(p, n, "looks like you don't know either if you're sitting around here");
				npcsay(p,
					n,
					"Of course we know",
					"We haven't worked out which tree the stupid leprechaun is in yet",
					"Oops I didn't mean to tell you that");
				say(p, n, "So a Leprechaun knows where Zanaris is?");
				npcsay(p, n, "Eerm", "yes");
				say(p, n, "And he's in a tree somewhere around here",
					"thankyou very much");
				setQuestStage(p, this, 1);
			}
		} else if (next_option == 1) {
			npcsay(p, n, "Well when we find which tree the leprechaun is in",
				"You can eat those words",
				"Oops I didn't mean to tell you that");
			say(p, n, "So a Leprechaun knows where Zanaris is?");
			npcsay(p, n, "Eerm", "yes");
			say(p, n, "And he's in a tree somewhere around here",
				"thankyou very much");
			setQuestStage(p, this, 1);
		}
	}

	@Override
	public void onAttackNpc(Player p, Npc affectedmob) {
		if (affectedmob.getID() == NpcId.TREE_SPIRIT.id()) {
			if (affectedmob.getAttribute("spawnedFor", null) != null) {
				if (!affectedmob.getAttribute("spawnedFor").equals(p)) {
					p.message("That npc is not after you.");
				}
			}
		}
	}

	@Override
	public boolean blockAttackNpc(Player p, Npc n) {
		if (n.getID() == NpcId.TREE_SPIRIT.id()) {
			if (n.getAttribute("spawnedFor", null) != null) {
				if (!n.getAttribute("spawnedFor").equals(p)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean blockKillNpc(Player p, Npc n) {
		return false;
	}

	@Override
	public void onKillNpc(Player p, Npc n) {
		if (atQuestStage(p, this, 3)) {
			setQuestStage(p, this, 4);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Item item1, Item item2) {
		return Functions.compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.DRAMEN_BRANCH.id());
	}

	@Override
	public void onUseInv(Player p, Item item1, Item item2) {
		if (p.getCarriedItems().hasCatalogID(ItemId.DRAMEN_BRANCH.id(), Optional.of(false))) {
			if (getCurrentLevel(p, Skills.CRAFTING) < 31) {
				Functions.mes(p,
					"You are not a high enough crafting level to craft this staff",
					"You need a crafting level of 31");
				return;
			}
			p.getCarriedItems().remove(new Item(ItemId.DRAMEN_BRANCH.id()));
			Functions.mes(p, "you carve the branch into a staff");
			give(p, ItemId.DRAMEN_STAFF.id(), 1);
		}
	}

	@Override
	public boolean blockOpBound(GameObject obj, Integer click,
								Player player) {
		return inArray(obj.getID(), MAGIC_DOOR, ZANARIS_DOOR);
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, Player p) {
		if (obj.getID() == MAGIC_DOOR) {
			p.teleport(109, 245, true);
			delay(500);
			p.message("you go through the door and find yourself somewhere else");
		} else if (obj.getID() == ZANARIS_DOOR) {
			if (p.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAMEN_STAFF.id()) && atQuestStages(p, this, 4, -1)) {
				p.setBusy(true);
				Functions.mes(p, "The world starts to shimmer",
					"You find yourself in different surroundings");
				if (getQuestStage(p, this) != -1) {
					teleport(p, 126, 3518);
					completeQuest(p, this);
				} else {
					teleport(p, 126, 3518);
				}
				p.setBusy(false);
			} else {
				doDoor(obj, p);
				p.message("you go through the door and find yourself in a shed.");
			}
		}
	}

	public static int getWoodcutAxe(Player p) {
		int axeId = -1;

		for (final int a : Formulae.woodcuttingAxeIDs) {
			if (p.getWorld().getServer().getConfig().WANT_EQUIPMENT_TAB) {
				if (p.getCarriedItems().getEquipment().searchEquipmentForItem(a) != -1) {
					axeId = a;
					break;
				}
			}

			if (p.getCarriedItems().getInventory().countId(a) > 0) {
				axeId = a;
				break;
			}
		}
		return axeId;
	}
}
