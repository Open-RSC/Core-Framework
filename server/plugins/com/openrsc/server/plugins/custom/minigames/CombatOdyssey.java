package com.openrsc.server.plugins.custom.minigames;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.content.minigame.combatodyssey.Task;
import com.openrsc.server.content.minigame.combatodyssey.Tier;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.*;
import com.openrsc.server.util.rsc.DataConversions;
import com.openrsc.server.util.rsc.MessageType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

import static com.openrsc.server.plugins.RuneScript.*;

public class CombatOdyssey implements DropObjTrigger, KillNpcTrigger, OpInvTrigger, TalkNpcTrigger,
	UseInvTrigger, UseNpcTrigger {
	// INTRODUCTION STAGES
	public static final int NOT_STARTED = 0;
	public static final int TALKED_TO_RADIMUS = 1;
	public static final int MET_BIGGUM = 2;
	public static final int IN_PROGRESS = -1;

	// Task info enums
	private static final int CURRENT_TIER = 0;
	private static final int CURRENT_TASK = 1;
	private static final int CURRENT_KILLS = 2;

	private static final int[] biggumFavoriteFood = new int[]{
		ItemId.RAW_CHICKEN.id(),
		ItemId.RAW_OOMLIE_MEAT.id(),
		ItemId.RAW_OOMLIE_MEAT_PARCEL.id(),
		ItemId.COOKED_OOMLIE_MEAT_PARCEL.id()
	};

	public static boolean biggumMissing() {
		if (!ifheld(ItemId.BIGGUM_FLODROT.id(), 1)) {
			mes("You need Biggum Flodrot to continue the Odyssey!");
			delay(3);
			mes("You can probably find him at the Legend's Guild");
			return true;
		}
		return false;
	}

	public static void recoverBiggum(Player player) {
		mes("A small goblin scampers across the floor and jumps into your backpack");
		give(ItemId.BIGGUM_FLODROT.id(), 1);
		delay(3);
	}

	public static void meetBiggum(Player player) {
		Functions.thinkbubble(new Item(ItemId.BIGGUM_FLODROT.id()));
		mes("A small goblin scampers across the floor and jumps into your backpack");
		give(ItemId.BIGGUM_FLODROT.id(), 1);
		delay(3);
		biggumSay(player, "Oi! Psst! You there, big human person!");
		say("Huh? Get out of my backpack you awful creature!");
		biggumSay(player, "Am absolutely not awful! Am here to make proposition");
		say("What sort of proposition could a backpack goblin possibly make?");
		biggumSay(player, "You goin' on a big long killing spree, yes?",
			"Me looking for big long adventure, me join big dumb human");
		say("And why should I let you join me?");
		biggumSay(player, "Big long killing spree has many tasks",
			"Many things to keep track of, yes?",
			"Biggum keep track of dirty, boring paperwork for you",
			"And big dumb human brings Biggum on big long killing spree");
		say("I suppose that could work...",
			"what's in it for you?");
		biggumSay(player, "Me greatest adventurer of ironclaw tribe",
			"Only greatest adventures good enough for Biggum Flodrot");
		say("Very well then, where should we start?");
		biggumSay(player, "Now go talk with goblin generals in village of north Fallington",
			"They tell big dumb human what first things to kill");
		setIntroStage(player, MET_BIGGUM);
	}

	public static void radimusDialog(Player player, Npc npc) {
		int introStage = getIntroStage(player);
		switch (introStage) {
			case NOT_STARTED:
				npcsay("Hello there! How are you enjoying the Legends Guild?");
				if (getPrestige(player) < 1) {
					say("It's great!");
					npcsay("I have a task for you that is truly fit for a legend",
						"You will fight beasts in the highest mountains and the darkest caves",
						"From everyday foes to obscure, forgotten monsters",
						"A combat odyssey if you will",
						"If you do this, I will reward you an item truly fit for a legend",
						"Something no one has worn for hundreds of years, not even me",
						"What say you?");
					if (multi("That sounds like just the task for me!",
						"That sounds like more than I am currently able to handle") != 0) return;
					npcsay("Excellent!",
						"You may go and talk to Siegfried upstairs",
						"He will get you started");
					setIntroStage(player, TALKED_TO_RADIMUS);
				} else {
					say("Can I do the odyssey again?");
					npcsay("Certainly!",
						"You may start it again by speaking to your goblin friend in the garden",
						"You two seem to make quite the team");
					// We won't set the intro stage here, that way the player can't pick up Biggum the OG way
				}
				break;
			case TALKED_TO_RADIMUS:
				npcsay("You should go and talk to Siegfried upstairs",
					"He will get you started");
				break;
			case MET_BIGGUM:
				npcsay("Hope everything is going well with your quest!");
				break;
			case IN_PROGRESS:
				// We've already checked if the player has biggum and if the tier is completed
				int currentTier = getCurrentTier(player);
				int newTier;
				switch (currentTier) {
					case 9:
						newTier = 10;
						assignNewTier(player, newTier);
						npcsay("You have come far, legend!",
							"You're on the final leg of this long voyage");
						if (getPrestige(player) < 1) {
							say("Well this gobli--");
							biggumSay(player, "Shhh, dumb human not talk about Biggum!");
						}
						npcsay("I shall send you on your last few missions myself",
							"For now, you must go kill");
						npcsay(player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
						npcsay("First though, take this",
							"I'm sure it will help");
						giveRewards(player, npc);
						npcsay("Return to me when you are done");
						break;
					case 10:
						newTier = 11;
						assignNewTier(player, newTier);
						npcsay("Ah, back at last!",
							"Let's make it a bit more challenging for you, shall we?",
							"You now have to kill");
						npcsay(player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
						npcsay("And of course return to me once completed");
						break;
					case 11:
						newTier = 12;
						assignNewTier(player, newTier);
						npcsay("I am beginning to believe we shall see this to the end, my friend!",
							"Your final mission is to kill");
						npcsay(player.getWorld().getCombatOdyssey().getTier(newTier).getTasksAndCounts());
						npcsay("Come back when it's done, and you will have your reward");
						break;
					case 12:
						assignNewTier(player, 13);
						npcsay("You've done it!",
							"You've truly earned your place in the halls of legend",
							"Though I must confess, I have one last thing for you to do",
							"There is one savage beast yet left to kill",
							"You must go kill...",
							"The three-headed dragon of Jarn!",
							"...",
							"Hahaha, I speak merely in jest",
							"That foul creature was killed by Arrav ages ago",
							"There is however a beast in the depths of the wilderness",
							"It is known as the king black dragon",
							"Kill this monster once, and you will be done");
						say("I certainly hope so...");
						break;
					case 13:
						npcsay("Well done, legend!",
							"You've completed the combat odyssey!",
							"As promised, a reward truly fit for a legend",
							"As a matter of fact, you will get to pick between two",
							"Dragon Plate Mail Legs or a Dragon Plated Skirt");
						int choice = multi("Dragon Plate Mail Legs", "Dragon Plated Skirt");
						if (choice == -1) return;
						int itemToGive = -1;
						if (choice == 0) {
							itemToGive = ItemId.DRAGON_PLATE_MAIL_LEGS.id();
						} else {
							itemToGive = ItemId.DRAGON_PLATED_SKIRT.id();
						}
						npcsay("Here you are",
							"You truly deserve them");

						// We will remove these now so that the player can't use exploits to get another item.
						// Removing these allows the player to repeat the odyssey.
						player.getCache().remove("co_tier_progress", "combat_odyssey");

						give(itemToGive, 1);
						mes("Radimus gives you your reward");
						delay(3);
						if (getPrestige(player) < 1) {
							biggumSay(player, "Biggum join human on big long killing spree",
								"Biggum complete quest Radimus gave",
								"Biggum legend now as Radimus promise");
							mes("Radimus looks surprised");
							delay(3);
							npcsay("You actually managed to kill one of everything?",
								"The legends guild has never had a goblin before",
								"But I suppose it's only fair if you completed such a hard quest");
							say("Uhh...",
								"Biggum actually didn't do any killing...",
								"It was-");
							npcsay("Congratulations, Biggum Flodrot!",
								"The legends guild welcomes you as its newest member");
							say("Well, whatever I guess...");
							remove(ItemId.BIGGUM_FLODROT.id(), 1);
							mes("Biggum scampers away");
							delay(3);
							mes("Probably to go retrieve his very own Cape of legends");
							delay(3);
						} else {
							npcsay("And I haven't forgotten about you, Biggum!",
								"Now that you are a member of the Legend's Guild...",
								"...I will offer you the same reward",
								"Here is your very own pair of dragon plate legs!");
							mes("Radimus presents Biggum with his reward");
							delay(3);
							biggumSay(player, "Biggum off to trade reward for delicious chicken",
								"Human speak to Biggum to do big long killing spree again",
								"See ya chump");
							remove(ItemId.BIGGUM_FLODROT.id(), 1);
							mes("Biggum scampers away");
							delay(3);
						}
						int prestige = incrementPrestige(player);
						player.playerServerMessage(MessageType.QUEST, "@gre@You have completed the Odyssey " + prestige + (prestige > 1 ? " times!" : " time!"));
						player.playerServerMessage(MessageType.QUEST, "@gre@Speak to Radimus if you'd like to do the Odyssey again");
						break;
				}
				break;
		}
	}

	private void directToTierMaster(Player player) {
		int currentTier = getCurrentTier(player);
		switch (currentTier) {
			case 0:
				biggumSay(player, "Speak to goblin generals of north Fallington");
				break;
			case 1:
				biggumSay(player, "Speak to Thormac the wizzy");
				break;
			case 2:
				biggumSay(player, "Speak to ogre Grew of feldip hills");
				break;
			case 3:
			case 4:
				biggumSay(player, "Speak to sinister dark mage man in west Ardington");
				break;
			case 5:
			case 6:
				biggumSay(player, "Speak to small dumb gnome Hazelmere");
				break;
			case 7:
				biggumSay(player, "Speak to Sigbert adventure man");
				break;
			case 8:
				biggumSay(player, "Speak to big hero Achetties");
				break;
			case 9:
			case 10:
			case 11:
			case 12:
				if (getPrestige(player) < 1) {
					biggumSay(player, "Go speak to Radimus, but not talk about Biggum!");
				} else {
					biggumSay(player, "Go speak to Radimus");
				}
				break;
			case 13:
				biggumSay(player, "Go claim big shiny reward from Radimus");
				break;
		}
	}

	@Override
	public void onDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		mes("Biggum Flodrot drops to the ground and scampers away");
		delay(3);
		if (getPrestige(player) >= 1) {
			mes("He has likely gone back to the Legend's Guild courtyard");
		} else {
			mes("You can probably find him where you first met");
		}
		remove(ItemId.BIGGUM_FLODROT.id(), 1);
	}

	@Override
	public boolean blockDropObj(Player player, Integer invIndex, Item item, Boolean fromInventory) {
		return item.getCatalogId() == ItemId.BIGGUM_FLODROT.id();
	}

	@Override
	public void onKillNpc(Player player, Npc npc) {
		// Double-check the Npc we've killed is the one we're currently tasked with
		int[] taskNpcs = getTaskNpcs(player);
		if (taskNpcs == null || taskNpcs.length == 0) {
			return;
		}
		if (DataConversions.inArray(taskNpcs, npc.getID())) {
			incrementTaskKills(player);
			int currentTask = getCurrentTask(player);

			// If we've killed enough NPCs and the task hasn't already been marked completed, we will do so
			if (isTaskCompleted(player) && !isTaskAlreadyComplete(player, currentTask)) {
				completeTask(player, currentTask);
				player.message("@gre@You hear Biggum trying to get your attention from your backpack");
				Functions.thinkbubble(new Item(ItemId.BIGGUM_FLODROT.id()));
			}
		}
	}

	@Override
	public boolean blockKillNpc(Player player, Npc npc) {
		int[] taskNpcs = getTaskNpcs(player);
		if (taskNpcs == null || taskNpcs.length == 0) {
			return false;
		}
		return DataConversions.inArray(taskNpcs, npc.getID());
	}

	@Override
	public void onOpInv(Player player, Integer invIndex, Item item, String command) {
		if (!command.equalsIgnoreCase("talk")) return;

		// If we're still in the intro, we will just direct the player to the first tiermaster
		if (getIntroStage(player) == MET_BIGGUM) {
			say("Where should we start?");
			biggumSay(player, "Go talk with goblin generals in village of north Fallington",
				"They tell big dumb human what first things to kill");
			return;
		}

		// Next we want to check if the player has completed their task
		if (isTaskCompleted(player)) {
			// If they have, and they're done with the entire tier, we want to direct them to the next
			// tier master so they can get their next set of tasks.
			if (isTierCompleted(player)) {
				biggumSay(player, "Human has finished all tasks");
				directToTierMaster(player);
			} else {
				// If the player hasn't finished the tier, we'll just give them a new task within the tier.
				assignNewTask(player);

				Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
				if (tierData == null) return;
				Task newTaskData = tierData.getTask(getCurrentTask(player));
				if (newTaskData == null) return;

				biggumSay(player, "Human done here",
					"Now kill " + newTaskData.getKills() + " " + newTaskData.getDescription());
			}
			return;
		}

		// If the player hasn't completed their task, we assume they're still on a task
		// (Biggum will be removed from the player at the end of the odyssey, so the player can't have
		// Biggum without a task)
		// We'll check for it anyway though. We don't want to check for this up top, because the player doesn't
		// get the co_tier_progress value until after they get their first task.
		if (!player.getCache().hasKey("combat_odyssey")
			|| !player.getCache().hasKey("co_tier_progress")) {
			biggumSay(player, "Human is not doing combat odyssey!",
				"What is Biggum sticking around for?");
			remove(ItemId.BIGGUM_FLODROT.id(), 1);
			mes("Biggum jumps out of your backpack and scampers away");
			return;
		}

		Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
		if (tierData == null) return;
		Task taskData = tierData.getTask(getCurrentTask(player));
		if (taskData == null) return;

		int option;
		if (player.isDev() && getIntroStage(player) == IN_PROGRESS) {
			option = multi(false,
				"What is my current task?",
				"What can you tell me about my current task?",
				"What tasks do I have left to do?",
				"Show me developer options");
		} else {
			option = multi("What is my current task?",
				"What can you tell me about my current task?",
				"What tasks do I have left to do?");
		}

		if (option == 0) {
			int currentKills = getCurrentKills(player);
			biggumSay(player, "Human needs to kill " + taskData.getKills() + " " + taskData.getDescription());
			if (currentKills > 0) {
				biggumSay(player, "Human has already killed " + currentKills);
			}
		} else if (option == 1) {
			if (getPrestige(player) > 0) {
				if (taskData.getDescription().equalsIgnoreCase("Pit Scorpions")) {
					biggumSay(player, "Biggum has tried these in many goblin foods",
						"Very crunchy but not very spicy");
					return;
				} else if (taskData.getDescription().equalsIgnoreCase("Shadow Warriors")) {
					biggumSay(player, "Biggum bonked one of these and only got a broken shield",
						"Biggum traded it for delicious chicken",
						"Best day ever");
					return;
				}
			}
			biggumSay(player, taskData.getMonsterInfoDialog());
		} else if (option == 2) {
			StringBuilder tasksRemaining = new StringBuilder();
			tasksRemaining.append("@yel@Human still has to kill @whi@ % % ");
			boolean firstTask = true;
			for (Task task : tierData.getTasks()) {
				if (isTaskAlreadyComplete(player, task.getTaskId())) {
					continue;
				}

				if (!firstTask) {
					tasksRemaining.append(", ");
				}

				firstTask = false;

				if (task.getTaskId() == getCurrentTask(player)) {
					tasksRemaining.append(task.getKills() - getCurrentKills(player));
				} else {
					tasksRemaining.append(task.getKills());
				}
				tasksRemaining.append(" ");
				tasksRemaining.append(task.getDescription());
			}

			ActionSender.sendBox(player, tasksRemaining.toString(), true);
		} else if (option == 3 && player.isDev()) {
			int devOption = multi(false,
				"Set me 1 kill away from task completion",
				"Mark my task as complete",
				"Mark my current tier as complete",
				"Complete the Odyssey");
			if (devOption == 0) {
				int taskKills = taskData.getKills();
				int newKills = taskData.getKills() - 1;
				updateCurrentTaskInfo(player, newKills, CURRENT_KILLS);
				biggumSay(player, "Human has been set to " + newKills + "/" + taskKills);
			} else if (devOption == 1) {
				// Update the killcount to the goal
				updateCurrentTaskInfo(player, taskData.getKills(), CURRENT_KILLS);
				// Mark the task as complete within the tier
				completeTask(player, taskData.getTaskId());
				biggumSay(player, "Task complete",
					"Speak to Biggum for next task");
			} else if (devOption == 2) {
				// Make sure the current task is complete, otherwise this could mess with some stuff
				updateCurrentTaskInfo(player, taskData.getKills(), CURRENT_KILLS);
				// Do the math to mark the tier as complete
				player.getCache().store("co_tier_progress", (long)(Math.pow(2, tierData.getTotalTasks()) - 1));
				biggumSay(player, "Biggum has marked tier completed",
					"Speak to Biggum to know where to go next");
			} else if (devOption == 3) {
				// Skip ahead to tier 13 and mark it as complete
				updateCurrentTaskInfo(player, 13, CURRENT_TIER);
				updateCurrentTaskInfo(player, 0, CURRENT_TASK);
				updateCurrentTaskInfo(player, 1, CURRENT_KILLS);
				player.getCache().store("co_tier_progress", 1);
				biggumSay(player, "Human has now completed final tier",
					"Speak to Biggum to know where to go next");
			}
		}
	}

	@Override
	public boolean blockOpInv(Player player, Integer invIndex, Item item, String command) {
		return item.getCatalogId() == ItemId.BIGGUM_FLODROT.id();
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() != NpcId.BIGGUM_FLODROT.id()) return;

		if (!player.canSeeBiggum()) {
			if (player.isAdmin()) {
				// Admins can still see Biggum
				say("Hello!");

				if (ifheld(ItemId.BIGGUM_FLODROT.id(), 1)) {
					npcsay("What does human want?",
						"We should go kill many things");
					say("Wait...",
						"How are you standing here");
					npcsay("What does human mean?");
					say("How are you standing here in this courtyard...",
						"...while you're also in my backpack?");
					mes("The bass drops as you open your backpack to show Biggum to Biggum");
					delay(3);
					npcsay("Wha-",
						"Nooooo",
						"noooo",
						"This isn't right",
						"Go away!");
					mes("Biggum looks like his mind is going to explode");
					delay(3);
					return;
				}

				final int stage = getIntroStage(player);
				if (stage == NOT_STARTED || stage == TALKED_TO_RADIMUS) {
					// Player has never met Biggum
					npcsay("Why human talking to Biggum?",
						"Biggum doesn't know human");
					say("Why is a goblin wearing a legend's cape?");
				} else {
					npcsay("What does human want?",
						"We should go kill many things");
					say("Why are you wearing a legend's cape?");
				}
				npcsay("Biggum isn't wearing a legend's-");
				mes("Biggum looks around to his back and notices his cape");
				delay(3);
				npcsay("Biggum does not know",
					"Biggum is not member of guild");
				mes("Biggum stares past you, looking very confused");
			}
			return;
		}

		if (getIntroStage(player) == IN_PROGRESS || getIntroStage(player) == MET_BIGGUM) {
			say("Are you ready to continue?");
			npcsay("Yes yes");
			mes("Biggum hops into your backpack");
			give(ItemId.BIGGUM_FLODROT.id(), 1);
			return;
		}

		int option = multi("What happened to your cape?",
			"You kinda cheated to get into the legends guild, didn't you?",
			"Let's do Radimus' odyssey again");
		if (option == 0) {
			npcsay("Wha? Oh...",
				"Biggum tore hole on accident",
				"While fighting... dragon!",
				"Yes that's right, Biggum fight big scary dragon",
				"Also made Biggum's cape dirty");
		} else if (option == 1) {
			say("You didn't do any of the kills");
			npcsay("Biggum help big dumb human do all kills",
				"Even if Biggum not swing the sword himself",
				"Biggum legendary at making sure things dead");
			say("I suppose",
				"But Radimus thinks you did it on your own");
			npcsay("Biggum big famous goblin legend now",
				"Could easily do it himself",
				"Now leave Biggum alone, not want to talk more nonsense");
		} else if (option == 2) {
			npcsay("Yes yes",
				"Biggum always ready for big long adventure",
				"First go speak with generals of village of north Fallington");
			mes("Biggum hops into your backpack");
			give(ItemId.BIGGUM_FLODROT.id(), 1);
			setIntroStage(player, MET_BIGGUM);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.BIGGUM_FLODROT.id();
	}

	@Override
	public void onUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		Item otherItem;
		if (item1.getCatalogId() == ItemId.BIGGUM_FLODROT.id()) {
			otherItem = item2;
		} else if (item2.getCatalogId() == ItemId.BIGGUM_FLODROT.id()) {
			otherItem = item1;
		} else {
			return;
		}

		biggumSay(player, "Biggum's favourite!");
		mes("Biggum gobbles up the meat quickly");
		remove(otherItem.getCatalogId(), 1);
	}

	@Override
	public boolean blockUseInv(Player player, Integer invIndex, Item item1, Item item2) {
		Item otherItem;
		if (item1.getCatalogId() == ItemId.BIGGUM_FLODROT.id()) {
			otherItem = item2;
		} else if (item2.getCatalogId() == ItemId.BIGGUM_FLODROT.id()) {
			otherItem = item1;
		} else {
			return false;
		}

		return DataConversions.inArray(biggumFavoriteFood, otherItem.getCatalogId());
	}

	@Override
	public void onUseNpc(Player player, Npc npc, Item item) {
		npcsay("Biggum's favourite!");
		mes("Biggum gobbles up the meat quickly");
		remove(item.getCatalogId(), 1);
	}

	@Override
	public boolean blockUseNpc(Player player, Npc npc, Item item) {
		return npc.getID() == NpcId.BIGGUM_FLODROT.id() && DataConversions.inArray(biggumFavoriteFood, item.getCatalogId());
	}

	public static void giveRewards(Player player, Npc npc) {
		Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
		if (tierData == null) return;

		for (Pair<Integer, Integer> reward : tierData.getRewards()) {
			int itemId = reward.getKey();
			int amount = reward.getValue();
			String itemName = new Item(itemId).getDef(player.getWorld()).getName();
			String npcName = npc.getDef().getName();

			give(reward.getKey(), reward.getValue());
			mes(npcName + " hands you " + amount + " " + itemName);
			delay(3);
		}
	}

	private static int[] getTaskNpcs(Player player) {
		Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
		if (tierData == null) return null;
		Task taskData = tierData.getTask(getCurrentTask(player));
		if (taskData == null) return null;
		return taskData.getNpcIds();
	}

	public static int getCurrentTierMasterId(Player player) {
		Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
		if (tierData == null) return -1;

		return tierData.getTierMasterId();
	}

	/**
	 * Determines if the killcount has been achieved for the current task
	 * @param player
	 * @return True if the killcount has been satisfied, false otherwise
	 */
	public static boolean isTaskCompleted(Player player) {
		Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
		if (tierData == null) return false;
		Task taskData = tierData.getTask(getCurrentTask(player));
		if (taskData == null) return false;

		return getCurrentKills(player) >= taskData.getKills();
	}

	public static void incrementTaskKills(Player player) {
		updateCurrentTaskInfo(player, getCurrentTaskInfo(player, CURRENT_KILLS) + 1, CURRENT_KILLS);
	}

	/**
	 * Assigns a new tier of tasks and a new task within that new tier to the player
	 * @param player
	 * @param tierId The tier
	 */
	public static void assignNewTier(Player player, int tierId) {
		updateCurrentTaskInfo(player, tierId, CURRENT_TIER);
		resetTierProgress(player);
		assignNewTask(player);
	}

	/**
	 * Assigns a new, random task within the specified tier
	 * @param player
	 */
	public static void assignNewTask(Player player) {
		int tierId = getCurrentTier(player);

		Tier tierData = player.getWorld().getCombatOdyssey().getTier(tierId);
		if (tierData == null) return;

		// Try to get a new task
		int newTaskId = DataConversions.random(0, tierData.getTotalTasks() - 1);
		ArrayList<Integer> triedTasks = new ArrayList<Integer>();

		// If the player has already done this task, we need to keep looping until we get one we haven't done yet
		while (isTaskAlreadyComplete(player, newTaskId)) {
			triedTasks.add(newTaskId);

			// We should never reach this. We should always check if the tier is complete before trying to assign a new task within the tier.
			if (triedTasks.size() == tierData.getTotalTasks()) {
				return;
			}

			// Grab a new, random task. One that we haven't tried yet
			do {
				newTaskId = DataConversions.random(0, tierData.getTotalTasks() - 1);
			} while (triedTasks.contains(newTaskId));
		}

		updateCurrentTaskInfo(player, newTaskId, CURRENT_TASK);
	}

	/**
	 * Marks the current task within the tier as completed
	 * @param player
	 * @param taskId The task to mark complete
	 */
	private static void completeTask(Player player, int taskId) {
		long tierProgress = player.getCache().getLong("co_tier_progress");
		long newProgress = tierProgress | (1L << taskId);
		player.getCache().store("co_tier_progress", newProgress);
	}

	/**
	 * Determines if all the tasks in the current tier have been completed
	 * @param player
	 * @return True if all the tasks have been completed, false otherwise
	 */
	public static boolean isTierCompleted(Player player) {
		if (!player.getCache().hasKey("co_tier_progress")) {
			return false;
		}

		Tier tierData = player.getWorld().getCombatOdyssey().getTier(getCurrentTier(player));
		if (tierData == null) return false;

		int totalTasks = tierData.getTotalTasks();
		long currentTaskProgress = player.getCache().getLong("co_tier_progress");

		return currentTaskProgress == (long)(Math.pow(2, totalTasks) - 1);
	}

	/**
	 * Checks to see if a task has already been completed so that it won't be assigned again
	 * @param player
	 * @param taskId The task to check
	 * @return True if the task has been completed already, false otherwise
	 */
	private static boolean isTaskAlreadyComplete(Player player, int taskId) {
		if (!player.getCache().hasKey("co_tier_progress")) {
			return false;
		}

		long tierProgress = player.getCache().getLong("co_tier_progress");
		return (tierProgress & (1L << taskId)) != 0;
	}

	private static void resetTierProgress(Player player) {
		player.getCache().store("co_tier_progress", 0);
	}

	private static void updateCurrentTaskInfo(Player player, int newValue, int save) {
		switch (save) {
			case CURRENT_TIER:
				player.getCache().store("combat_odyssey", newValue + ":-1:0");
				break;
			case CURRENT_TASK:
				player.getCache().store("combat_odyssey", getCurrentTier(player) + ":" + newValue + ":0");
				break;
			case CURRENT_KILLS:
				player.getCache().store("combat_odyssey", getCurrentTier(player) + ":" + getCurrentTask(player) + ":" + newValue);
				break;
		}
	}

	public static int getCurrentTier(Player player) {
		return getCurrentTaskInfo(player, CURRENT_TIER);
	}

	public static int getCurrentTask(Player player) {
		return getCurrentTaskInfo(player, CURRENT_TASK);
	}

	public static int getCurrentKills(Player player) {
		return getCurrentTaskInfo(player, CURRENT_KILLS);
	}

	/**
	 * Retrieve the player's current task info from the cache
	 * @param player
	 * @param retrieve Which data to retrieve.
	 *                 0: The current tier
	 *                 1: The current task
	 *                 2: The current killcount
	 * @return The queried information regarding the player's current task
	 */
	private static int getCurrentTaskInfo(Player player, int retrieve) {
		if (!player.getCache().hasKey("combat_odyssey")) {
			return -1;
		}

		String currentTaskInfo = player.getCache().getString("combat_odyssey");
		String[] data = currentTaskInfo.split(":");
		if (data.length < 3) {
			return -1;
		}
		return Integer.parseInt(data[retrieve]);
	}

	/**
	 * Returns the stage of the Odyssey. If we aren't in the intro,
	 * the parseInt function will throw an error and the function will return -1
	 * @param player
	 * @return The current stage of the Odyssey intro
	 */
	public static int getIntroStage(Player player) {
		if (!player.getCache().hasKey("combat_odyssey")) {
			return 0;
		}

		try {
			String value = player.getCache().getString("combat_odyssey");
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			return IN_PROGRESS;
		}
	}

	private static void setIntroStage(Player player, Integer stage) {
		player.getCache().store("combat_odyssey", stage.toString());
	}

	/**
	 * Special function for handling dialog for Biggum. Default delay of 5 ticks between messages.
	 * Ensures his dialog will remain in the Quest history tab
	 * @param player The player
	 * @param message The message(s) for Biggum to say
	 */
	public static void biggumSay(Player player, String... message) {
		biggumSay(player, 5, message);
	}

	/**
	 * Special function for handling dialog for Biggum
	 * Ensures his dialog will remain in the Quest history tab
	 * @param player The player
	 * @param tickDelay The delay between Biggum's messages
	 * @param message The message(s) for Biggum to say
	 */
	public static void biggumSay(Player player, int tickDelay, String... message) {
		for (String mes : message) {
			player.playerServerMessage(MessageType.QUEST, "@yel@Biggum Flodrot: " + mes);
			delay(tickDelay);
		}
	}

	public static int getPrestige(Player player) {
		if (!player.getCache().hasKey("co_prestige")) {
			return 0;
		}
		return player.getCache().getInt("co_prestige");
	}

	/**
	 * Adds one to the player's current Odyssey Prestige and returns the new value
	 * @param player
	 * @return The new Odyssey Prestige for the player
	 */
	private static int incrementPrestige(Player player) {
		int newPrestige = getPrestige(player) + 1;
		player.getCache().set("co_prestige", newPrestige);
		return newPrestige;
	}
}
