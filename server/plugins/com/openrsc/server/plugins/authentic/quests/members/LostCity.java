package com.openrsc.server.plugins.authentic.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.shared.constants.Quest;
import com.openrsc.server.plugins.shared.model.QuestReward;
import com.openrsc.server.plugins.shared.model.XPReward;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.Formulae;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.RuneScript.changeleveldown;

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
	public int getQuestPoints() {
		return Quest.LOST_CITY.reward().getQuestPoints();
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		final QuestReward reward = Quest.LOST_CITY.reward();
		for (XPReward xpReward : reward.getXpRewards()) {
			incStat(player, xpReward.getSkill().id(), xpReward.getBaseXP(), xpReward.getVarXP());
		}
		incQP(player, reward.getQuestPoints(), !player.isUsingClientBeforeQP());
		player.message("Well done you have completed the Lost City of Zanaris quest");
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return inArray(obj.getID(), LEPROCHAUN_TREE, ENTRANA_LADDER,
			DRAMEN_TREE);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		switch (obj.getID()) {
			case 244:
				Npc monk = ifnearvisnpc(player, NpcId.MONK_OF_ENTRANA_ENTRANA.id(), 10);
				if (monk != null)
					monk.initializeTalkScript(player);
				break;
			case 237:
				if (atQuestStage(player, this, 0) || !player.getWorld().getServer().getConfig().MEMBER_WORLD) {
					player.message("There is nothing in this tree");
				} else if (getQuestStage(player, this) >= 1
					&& getQuestStage(player, this) <= 3) {
					Npc leprechaun = ifnearvisnpc(player, NpcId.LEPRECHAUN.id(), 15);
					if (leprechaun != null) {
						player.message("There is nothing in this tree");
					} else {
						player.message("A Leprechaun jumps down from the tree and runs off");
						final Npc lepr = addnpc(player.getWorld(), NpcId.LEPRECHAUN.id(), 172, 661, (int)TimeUnit.SECONDS.toMillis(180));
						lepr.walk(173, 661);
						try {
							delay();
							lepr.walk(177, 661 + DataConversions.random(0, 10) - 5);
						} catch (Exception e) {
							LOGGER.catching(e);
						}

					}
				} else {
					player.message("There is nothing in this tree");
				}
				break;
			case 245:
				if (atQuestStages(player, this, 4, 3, 2, -1)) {
					if (getCurrentLevel(player, Skill.WOODCUTTING.id()) < 36) {
						mes("You are not a high enough woodcutting level to chop down this tree");
						delay(3);
						mes("You need a woodcutting level of 36");
						delay(3);
						return;
					}
					if (getWoodcutAxe(player) == -1) {
						player.message("You need an axe to chop down this tree");
						return;
					}

					if (atQuestStages(player, this, 4, -1)) {
						mes("You cut a branch from the Dramen tree");
						delay(3);
						give(player, ItemId.DRAMEN_BRANCH.id(), 1);
						return;
					}

					Npc spawnedTreeSpirit = ifnearvisnpc(player, NpcId.TREE_SPIRIT.id(), 15);
					if (atQuestStages(player, this, 2)) {
						setQuestStage(player, this, 3);
					}
					if (spawnedTreeSpirit != null) {
						npcsay(player, spawnedTreeSpirit, "Stop",
							"I am the spirit of the Dramen Tree",
							"You must come through me before touching that tree");
						return;
					}
					// spawns independent on player position
					Npc treeSpirit = addnpc(player.getWorld(), NpcId.TREE_SPIRIT.id(), 412, 3403);
					if (treeSpirit == null) {
						return;
					}
					delay(3);
					npcsay(player, treeSpirit, "Stop",
						"I am the spirit of the Dramen Tree",
						"You must come through me before touching that tree");
				} else {
					mes("the tree seems to have a ominous aura to it",
						"you do not feel like chopping it down");
					delay(3);
				}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(), NpcId.ADVENTURER_WARRIOR.id(),
				NpcId.ADVENTURER_WIZARD.id(), NpcId.LEPRECHAUN.id(), NpcId.MONK_OF_ENTRANA_ENTRANA.id()}, n.getID());
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (!player.getWorld().getServer().getConfig().MEMBER_WORLD) {
			freePlayerDialogue(player, n);
			return;
		}
		if (n.getID() == NpcId.LEPRECHAUN.id()) {
			if (atQuestStage(player, this, 0)) {
				npcsay(player, n, "Ay you big elephant", "You have caught me",
					"What would you be wanting with old Shamus then?");
				say(player, n, "I'm not sure");
				npcsay(player, n, "Well you'll have to catch me again when you are");
				player.message("The leprechaun magically disapeers");
				n.remove();
			} else if (atQuestStage(player, this, 1)) {
				npcsay(player, n, "Ay you big elephant", "You have caught me",
					"What would you be wanting with old Shamus then?");
				say(player, n, "I want to find Zanaris");
				npcsay(player, n, "Zanaris?",
					"You need to go in the funny little shed",
					"in the middle of the swamp");
				say(player, n, "Oh I thought Zanaris was a city");
				npcsay(player, n, "It is");
				int menu = multi(player, n, false, //do not send over
					"How does it fit in a shed then?",
					"I've been in that shed, I didn't see a city");
				if (menu == 0) {
					say(player, n, "How does it fit in a shed then?");
					npcsay(player, n, "Silly person",
						"The city isn't in the shed",
						"The shed is a portal to Zanaris");
					say(player, n, "So I just walk into the shed and end up in Zanaris?");
				} else if (menu == 1) {
					say(player, n, "I've been in that shed",
						"I didn't see a city");
				}
				npcsay(player, n, "Oh didn't I say?",
					"You need to be carrying a Dramenwood staff",
					"Otherwise you do just end up in a shed");
				say(player, n, "So where would I get a staff?");
				npcsay(player, n, "Dramenwood staffs are crafted from branches",
					"These staffs are cut from the Dramen tree",
					"located somewhere in a cave on the island of Entrana",
					"I believe the monks of Entrana have recetnly",
					"Started running a ship from port sarim to Entrana");
				setQuestStage(player, this, 2);
				player.message("The leprechaun magically disapeers");
				n.remove();
			} else if (atQuestStages(player, this, 4, 3, 2, -1)) {
				npcsay(player, n, "Ay you big elephant",
					"You have caught me",
					"What would you be wanting with old Shamus then?");
				int menu = multi(player, n, "I'm not sure", "How do I get to Zanaris again?");
				if (menu == 0) {
					npcsay(player, n, "I dunno, what stupid people",
						"Who go to all the trouble to catch leprechaun's",
						"When they don't even know what they want");
					player.message("The leprechaun magically disapeers");
					n.remove();
				} else if (menu == 1) {
					npcsay(player, n, "You need to enter the shed in the middle of the swamp",
						"While holding a dramenwood staff",
						"Made from a branch",
						"Cut from the dramen tree on the island of Entrana");
					player.message("The leprechaun magically disapeers");
					n.remove();
				}
			}
		}
		else if (DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(),
				NpcId.ADVENTURER_WARRIOR.id(), NpcId.ADVENTURER_WIZARD.id()}, n.getID())) {
			if (atQuestStage(player, this, 0)) {
				npcsay(player, n, "hello traveller");
				int option = multi(player, n, false, //do not send over
					"What are you camped out here for?",
					"Do you know any good adventures I can go on?");
				if (option == 0) {
					say(player, n, "What are you camped here for?");
					npcsay(player, n, "We're looking for Zanaris");
					int sub_option = multi(player, n, false, //do not send over
						"Who's Zanaris?",
						"what's Zanaris?",
						"What makes you think it's out here");
					if (sub_option == 0 || sub_option == 2) {
						if (sub_option == 0) {
							say(player, n, "Who's Zanaris?");
							npcsay(player, n, "hehe Zanaris isn't a person",
								"It's a magical hidden city");
						}
						else {
							say(player, n, "what makes you think it's out here?");
							npcsay(player, n, "Don't you know the legends?",
								"Of the magical city, hidden in the swamp");
						}
						ZANARIS_MENU(player, n);
					} else if (sub_option == 1) {
						say(player, n, "what's Zanaris?");
						npcsay(player, n,
							"I don't think we want other people competing with us to find it");
						int next_option = multi(player, n, "Please tell me",
							"Oh well never mind");
						if (next_option == 0) {
							npcsay(player, n, "No");
						}
					}
				} else if (option == 1) {
					say(player, n, "Do you know any good adventures I can go on");
					npcsay(player, n, "Well we're on an adventure now",
						"Mind you this is our adventure",
						"We don't want to share it - find your own");
					int insist = multi(player, n, "Please tell me",
						"I don't think you've found a good adventure at all");
					if (insist == 0) {
						npcsay(player, n, "No");
					} else if (insist == 1) {
						npcsay(player, n, "We're on one of the greatest adventures I'll have you know",
							"Searching for Zanaris isn't a walk in the park");
						int sub_option = multi(player, n, false, //do not send over
							"Who's Zanaris?",
							"what's Zanaris?",
							"What makes you think it's out here");
						if (sub_option == 0 || sub_option == 2) {
							if (sub_option == 0) {
								npcsay(player, n, "hehe Zanaris isn't a person",
									"It's a magical hidden city");
							}
							else {
								npcsay(player, n, "Don't you know the legends?",
									"Of the magical city, hidden in the swamp");
							}
							ZANARIS_MENU(player, n);
						} else if (sub_option == 1) {
							say(player, n, "what's Zanaris?");
							npcsay(player, n,
								"I don't think we want other people competing with us to find it");
							int next_option = multi(player, n, "Please tell me",
								"Oh well never mind");
							if (next_option == 0) {
								npcsay(player, n, "No");
							}
						}
					}
				}
			} else if (atQuestStage(player, this, 1)) {
				say(
					player,
					n,
					"So let me get this straight",
					"I need to search the trees near here for a leprechaun?",
					"And he will tell me where Zanaris is?");
				npcsay(player, n, "That is what the legends and rumours are,yes");
			} else if (atQuestStages(player, this, 4, 3, 2, -1)) {
				say(player, n, "thankyou for your information",
					"It has helped me a lot in my quest to find Zanaris");
				npcsay(player, n, "So what have you found out?",
					"Where is Zanaris?");
				say(player, n, "I think I will keep that to myself");
			}
		} else if (n.getID() == NpcId.MONK_OF_ENTRANA_ENTRANA.id()) {
			npcsay(player, n, "Be careful going in there",
				"You are unarmed, and there is much evilness lurking down there",
				"The evilness seems to block off our contact with our gods",
				"Our prayers seem to have less effect down there",
				"Oh also you won't be able to come back this way",
				"This ladder only goes one way",
				"The only way out is a portal which leads deep into the wilderness");
			int option = multi(player, n,
				"I don't think I'm strong enough to enter then",
				"Well that is a risk I will have to take");
			if (option == 1) {
				player.message("You climb down the ladder");
				delay(2);
				changeleveldown();

				if (getCurrentLevel(player, Skill.PRAYER.id()) <= 3)
					setCurrentLevel(player, Skill.PRAYER.id(), 1);
				else if (getCurrentLevel(player, Skill.PRAYER.id()) <= 39)
					setCurrentLevel(player, Skill.PRAYER.id(), 2);
				else
					setCurrentLevel(player, Skill.PRAYER.id(), 3);
			}
		}
	}

	// All recreated/reconstructed
	private void freePlayerDialogue(Player player, Npc n) {
		if (DataConversions.inArray(new int[] {NpcId.ADVENTURER_ARCHER.id(), NpcId.ADVENTURER_CLERIC.id(),
			NpcId.ADVENTURER_WARRIOR.id(), NpcId.ADVENTURER_WIZARD.id()}, n.getID())) {
			npcsay(player, n, "hello adventurer",
				"can't talk here",
				"meet me in a members world so we can talk");
		}
	}

	public void ZANARIS_MENU(Player player, Npc n) {
		int next_option = multi(player, n,
			"If it's hidden how are you planning to find it",
			"There's no such thing");
		if (next_option == 0) {
			npcsay(player, n, "Well we don't want to tell others that",
				"We want all the glory of finding it for ourselves");
			int after_option = multi(player, n, false, //do not send over
				"please tell me",
				"looks like you don't know either if you're sitting around here");
			if (after_option == 0) {
				say(player, n, "Please tell me");
				npcsay(player, n, "No");
			} else if (after_option == 1) {
				say(player, n, "looks like you don't know either if you're sitting around here");
				npcsay(player,
					n,
					"Of course we know",
					"We haven't worked out which tree the stupid leprechaun is in yet",
					"Oops I didn't mean to tell you that");
				say(player, n, "So a Leprechaun knows where Zanaris is?");
				npcsay(player, n, "Eerm", "yes");
				say(player, n, "And he's in a tree somewhere around here",
					"thankyou very much");
				setQuestStage(player, this, 1);
			}
		} else if (next_option == 1) {
			npcsay(player, n, "Well when we find which tree the leprechaun is in",
				"You can eat those words",
				"Oops I didn't mean to tell you that");
			say(player, n, "So a Leprechaun knows where Zanaris is?");
			npcsay(player, n, "Eerm", "yes");
			say(player, n, "And he's in a tree somewhere around here",
				"thankyou very much");
			setQuestStage(player, this, 1);
		}
	}

	@Override
	public void onAttackNpc(Player player, Npc affectedmob) {

	}

	@Override
	public boolean blockAttackNpc(Player player, Npc n) {
		if (n.getID() == NpcId.TREE_SPIRIT.id() && !atQuestStage(player, this, 3)) {
			return true; // We return true here only because we want to block the default attack action.
		}
		return false;
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return n.getID() == NpcId.TREE_SPIRIT.id();
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		if (atQuestStage(player, this, 3)) {
			setQuestStage(player, this, 4);
		}
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		return compareItemsIds(item1, item2, ItemId.KNIFE.id(), ItemId.DRAMEN_BRANCH.id());
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		if (player.getCarriedItems().hasCatalogID(ItemId.DRAMEN_BRANCH.id(), Optional.of(false))) {
			if (getCurrentLevel(player, Skill.CRAFTING.id()) < 31) {
				mes("You are not a high enough crafting level to craft this staff");
				delay(3);
				mes("You need a crafting level of 31");
				delay(3);
				return;
			}
			player.getCarriedItems().remove(new Item(ItemId.DRAMEN_BRANCH.id()));
			mes("you carve the branch into a staff");
			delay(3);
			give(player, ItemId.DRAMEN_STAFF.id(), 1);
		}
	}

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return inArray(obj.getID(), MAGIC_DOOR, ZANARIS_DOOR);
	}

	@Override
	public void onOpBound(Player player, GameObject obj, Integer click) {
		if (obj.getID() == MAGIC_DOOR) {
			player.teleport(109, 245, true);
			delay();
			player.message("you go through the door and find yourself somewhere else");
		} else if (obj.getID() == ZANARIS_DOOR) {
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.DRAMEN_STAFF.id()) && atQuestStages(player, this, 4, -1)
			&& player.getWorld().getServer().getConfig().MEMBER_WORLD) {
				mes("The world starts to shimmer");
				delay(3);
				mes("You find yourself in different surroundings");
				delay(3);
				if (getQuestStage(player, this) != -1) {
					teleport(player, 126, 3518);
					completeQuest(player, this);
				} else {
					teleport(player, 126, 3518);
				}
			} else {
				doDoor(obj, player);
				player.message("you go through the door and find yourself in a shed.");
			}
		}
	}

	public static int getWoodcutAxe(Player player) {
		int axeId = -1;

		for (final int a : Formulae.woodcuttingAxeIDs) {
			if (config().WANT_EQUIPMENT_TAB) {
				if (player.getCarriedItems().getEquipment().searchEquipmentForItem(a) != -1) {
					axeId = a;
					break;
				}
			}

			if (player.getCarriedItems().getInventory().countId(a) > 0) {
				axeId = a;
				break;
			}
		}
		return axeId;
	}
}
