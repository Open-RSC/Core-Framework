package com.openrsc.server.content.achievement;

import com.openrsc.server.Constants;
import com.openrsc.server.content.achievement.Achievement.TaskReward;
import com.openrsc.server.content.achievement.Achievement.TaskType;
import com.openrsc.server.external.EntityHandler;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.Entity;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.GroundItem;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.sql.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public class AchievementSystem {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int ACHIEVEMENT_COMPLETED = 2;
	private static final int ACHIEVEMENT_STARTED = 1;

	private static final LinkedList<Achievement> loadedAchievements = new LinkedList<Achievement>();

	public static void loadAchievements() {
		loadedAchievements.clear();

		try {
			PreparedStatement fetchAchievement = DatabaseConnection.getDatabase()
				.prepareStatement("SELECT `id`, `name`, `description`, `extra`, `added` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "achievements` ORDER BY `id` ASC");
			PreparedStatement fetchRewards = DatabaseConnection.getDatabase()
				.prepareStatement("SELECT `item_id`, `amount`, `guaranteed`, `reward_type` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "achievement_reward` WHERE `achievement_id` = ?");
			PreparedStatement fetchTasks = DatabaseConnection.getDatabase()
				.prepareStatement("SELECT `type`, `do_id`, `do_amount` FROM `" + Constants.GameServer.MYSQL_TABLE_PREFIX + "achievement_task` WHERE `achievement_id` = ?");

			ResultSet result = fetchAchievement.executeQuery();
			while (result.next()) {
				ArrayList<AchievementReward> rewards = new ArrayList<AchievementReward>();
				fetchRewards.setInt(1, result.getInt("id"));

				ResultSet rewardResult = fetchRewards.executeQuery();
				while (rewardResult.next()) {
					TaskReward rewardType = TaskReward.valueOf(TaskReward.class, rewardResult.getString("reward_type"));
					rewards.add(new AchievementReward(rewardType, rewardResult.getInt("item_id"), rewardResult.getInt("amount"),
						rewardResult.getInt("guaranteed") == 1 ? true : false));
				}
				rewardResult.close();

				ArrayList<AchievementTask> tasks = new ArrayList<AchievementTask>();
				fetchTasks.setInt(1, result.getInt("id"));

				ResultSet taskResult = fetchTasks.executeQuery();
				while (taskResult.next()) {
					TaskType type = TaskType.valueOf(TaskType.class, taskResult.getString("type"));
					tasks.add(new AchievementTask(type, taskResult.getInt("do_id"), taskResult.getInt("do_amount")));
				}
				taskResult.close();

				Achievement achievement = new Achievement(tasks, rewards, result.getInt("id"),
					result.getString("name"), result.getString("description"), result.getString("extra"));
				loadedAchievements.add(achievement);
			}
		} catch (SQLException e) {
			LOGGER.catching(e);
		}
	}

	public static LinkedList<Achievement> getAchievements() {
		return loadedAchievements;
	}

	public static ArrayList<Achievement> getAvailableQuestsForEntity(Player player, Entity e) {
		ArrayList<Achievement> tasksAvailable = new ArrayList<Achievement>();
		for (Achievement task : loadedAchievements) {
			if (e.getID() == task.getStartID()) {
				if (!isPlayerCanStartQuest(player, task)) {
					continue;
				}
				switch (task.getAchievementType()) {
					case TALK_TO_NPC:
						if (e.isNpc()) {
							if (!tasksAvailable.contains(task)) {
								tasksAvailable.add(task);
							}
						}
						break;
					case PICK_UP_ITEM:
						if (e instanceof GroundItem && task.getStartID() == e.getID()
							&& isPlayerCanStartQuest(player, task)) {
							if (!tasksAvailable.contains(task)) {
								tasksAvailable.add(task);
							}
						}
						break;
					case USE_OBJECT:
						if (e instanceof GameObject && task.getStartID() == e.getID()
							&& isPlayerCanStartQuest(player, task)) {
							if (!tasksAvailable.contains(task)) {
								tasksAvailable.add(task);
							}
						}
						break;
				}
			}
		}
		return tasksAvailable;
	}

	public static boolean isPlayerCanStartQuest(Player player, Achievement task) {
		if (playerCompletedQuest(player, task)) {
			return false;
		}
		return true;
	}

	/*public static void triggerTask(Player p, Entity e, Achievement quest) {
		if (!playerStartedQuest(p, quest)) {
			Functions.message(p, "Would you like to start the quest: @cya@" + quest.getName());
			int option = Functions.showMenu(p, (Npc) e, "Yes", "No thanks");
			if (option == 0) {
				setQuestStage(p, quest, ACHIEVEMENT_STARTED);
			}
		} else if (e.getID() == quest.getEndID()) {
			if (playerCanFinishQuest(p, quest)) {
				if (e.isNpc()) {
					Functions.npcTalk(p, (Npc) e, quest.getEndQuestDialogue().split(";"));
				} else {
					Functions.message(p, quest.getEndQuestDialogue().split(";"));
				}
				for (AchievementTask tasks : quest.getTasks()) {
					if (tasks.getTask() == TaskType.GATHER_ITEM) {
						Functions.removeItem(p, tasks.getId(), tasks.getAmount());
					}
				}

				//Choice rewards 
				Menu itemRewards = new Menu();
				Menu expRewards = new Menu();

				for (int rewardIndex = 0; rewardIndex < quest.getRewards().size(); rewardIndex++) {
					AchievementReward reward = quest.getRewards().get(rewardIndex);
					if (reward.isGuaranteed()) {
						continue;
					}
					final int currentReward = rewardIndex;
					if (reward.getRewardType() == TaskReward.ITEM) {
						itemRewards.addOption(new Option(
								reward.getAmount() + " of " + EntityHandler.getItemDef(reward.getId()).getName()) {
							@Override
							public void action() {
								p.setAttribute("simpletask[" + quest.getId() + "]_reward_item", currentReward);
							}
						});
					} else if (reward.getRewardType() == TaskReward.EXPERIENCE) {
						expRewards.addOption(
								new Option(reward.getAmount() + " of " + Formulae.statArray[reward.getId()] + " xp") {
									@Override
									public void action() {
										p.setAttribute("simpletask[" + quest.getId() + "]_reward_xp", currentReward);
									}
								});
					}
				}
				
				boolean hasItemRewards = false;
				boolean hasExpRewards = false;

				if (itemRewards.size() > 0) {
					p.message("@cya@Please choose one item reward from the list");
					hasItemRewards = true;
					itemRewards.showMenu(p);
					if (p.getAttribute("simpletask[" + quest.getId() + "]_reward_item", (int) -1) == -1) {
						p.message("Please re-select rewards");
						return;
					}
				}
				if (expRewards.size() > 0) {
					p.message("@cya@Please choose one experience reward from the list");
					hasExpRewards = true;
					expRewards.showMenu(p);
					if (p.getAttribute("simpletask[" + quest.getId() + "]_reward_xp", (int) -1) == -1) {
						p.message("Please re-select rewards");
						return;
					}
				}
				if (hasItemRewards) {
					int chosenItemReward = p.getAttribute("simpletask[" + quest.getId() + "]_reward_item", (int) -1);
					AchievementReward rewardItem = quest.getRewards().get(chosenItemReward);
					Functions.addItem(p, rewardItem.getId(), rewardItem.getAmount());
				}
				if (hasExpRewards) {
					int chosenXpReward = p.getAttribute("simpletask[" + quest.getId() + "]_reward_xp", (int) -1);
					AchievementReward rewardExp = quest.getRewards().get(chosenXpReward);
					p.incExp1x(rewardExp.getId(), rewardExp.getAmount());
				}
				// Give guaranteed stuff first
				for (AchievementReward reward : quest.getRewards()) {
					if (reward.isGuaranteed()) {
						if (reward.getRewardType() == TaskReward.EXPERIENCE) {
							p.message("This quest rewards " + "(" + reward.getAmount() + " of "
									+ Formulae.statArray[reward.getId()] + " xp), do you accept this reward?");
							int wantXP = Functions.showMenu(p, (Npc) e, "Yes ( " + reward.getAmount() + " of "
									+ Formulae.statArray[reward.getId()] + " xp)", "No thanks");
							if (wantXP == 0) {
								p.incExp1x(reward.getId(), reward.getAmount());
							}
						} else if (reward.getRewardType() == TaskReward.ITEM) {
							Functions.addItem(p, reward.getId(), reward.getAmount());
						}
					}
				}
				setQuestStage(p, quest, ACHIEVEMENT_COMPLETED);
				for (AchievementTask task : quest.getTasks()) {
					p.getCache().remove("simpletask[" + task.getId() + "]_task_" + task.getTask().toString());
				}
				p.message("@gre@Congratulations you have completed " + quest.getName() + " quest");
			} else {
				if (e.isNpc()) {
					Functions.npcTalk(p, (Npc) e, quest.getDuringQuestDialogue().split(";"));
				} else {
					Functions.message(p, quest.getDuringQuestDialogue().split(";"));
				}
				ActionSender.sendBox(p, getTaskProgressText(p, quest.getId()), false);
			}
		}
	}*/

	public static String getTaskProgressText(Player p, int id) {
		String questInfo = "Task Progress: %";

		Achievement quest = loadedAchievements.get(id);
		if (quest == null) {
			return "Quest not found";
		}
		for (AchievementTask task : quest.getTasks()) {
			String taskHeader = "";
			if (task.getTask() == TaskType.KILL_NPC) {
				taskHeader = "Slay " + task.getAmount() + " of monster " + EntityHandler.getNpcDef(task.getId()).name
					+ ": ";
			} else if (task.getTask() == TaskType.GATHER_ITEM) {
				taskHeader = "Gather " + task.getAmount() + " of item "
					+ EntityHandler.getItemDef(task.getId()).getName() + ": ";
			} else if (task.getTask() == TaskType.DO_QUEST) {
				taskHeader = "Complete Quest " + World.getWorld().getQuest(task.getId()).getQuestName() + ": ";
			}
			int taskProgress = getTaskProgress(p, task);
			questInfo += (taskProgress == task.getAmount() ? "@gre@" : "@red@") + taskHeader + " "
				+ getTaskProgress(p, task) + "/" + task.getAmount() + "@whi@ %";
		}
		return questInfo;
	}

	public static int getTaskProgress(Player p, AchievementTask task) {
		if (task.getTask() == TaskType.DO_QUEST) {
			return p.getQuestStage(task.getId()) == -1 ? 1 : 0;
		}
		if (!p.getCache().hasKey("simpletask[" + task.getId() + "]_task_" + task.getTask().toString())) {
			return 0;
		}
		return p.getCache().getInt("simpletask[" + task.getId() + "]_task_" + task.getTask().toString());
	}

	public static void setQuestStage(Player p, int questID, int stage) {
		p.getCache().set("simpletask[" + questID + "]_stage", (int) stage);
	}

	public static void setQuestStage(Player p, Achievement quest, int stage) {
		p.getCache().set("simpletask[" + quest.getId() + "]_stage", (int) stage);
	}

	public static boolean playerCompletedQuest(Player p, Achievement quest) {
		if (p.getCache().hasKey("simpletask[" + quest.getId() + "]_stage")) {
			return p.getCache().getInt("simpletask[" + quest.getId() + "]_stage") == ACHIEVEMENT_COMPLETED;
		}
		return false;
	}

	public static boolean playerCompletedQuest(Player p, int id) {
		if (p.getCache().hasKey("simpletask[" + id + "]_stage")) {
			return p.getCache().getInt("simpletask[" + id + "]_stage") == ACHIEVEMENT_COMPLETED;
		}
		return false;
	}

	public static boolean playerCanFinishQuest(Player p, Achievement quest) {
		int completedTasks = 0;
		for (AchievementTask task : quest.getTasks()) {
			if (getTaskProgress(p, task) >= task.getAmount()) {
				completedTasks++;
			}
		}
		if (quest.getTasks().size() == completedTasks) {
			return true;
		}
		return false;
	}

	private static boolean playerStartedQuest(Player p, Achievement quest) {
		if (p.getCache().hasKey("simpletask[" + quest.getId() + "]_stage")) {
			return p.getCache().getInt("simpletask[" + quest.getId() + "]_stage") == ACHIEVEMENT_STARTED;
		}
		return false;
	}

	public static void checkAndIncGatherItemTasks(Player p, Item item) {
		for (Achievement quest : loadedAchievements) {
			if (!playerStartedQuest(p, quest))
				continue;

			for (AchievementTask task : quest.getTasks()) {
				if (task.getTask() == TaskType.GATHER_ITEM) {
					if (task.getId() == item.getID() && getTaskProgress(p, task) < task.getAmount()) {
						int newAmount = getTaskProgress(p, task) + item.getAmount();
						p.getCache().set("simpletask[" + task.getId() + "]_task_" + task.getTask().toString(),
							newAmount);
						if (newAmount == task.getAmount()) {
							p.message("@gre@You have completed task gather item " + item.getDef().getName() + "x"
								+ newAmount + "!");
						}
					}
				}
			}
		}
	}

	public static void checkAndIncSlayNpcTasks(Player p, Npc npc) {
		for (Achievement quest : loadedAchievements) {
			if (!playerStartedQuest(p, quest))
				continue;

			for (AchievementTask task : quest.getTasks()) {
				if (task.getTask() == TaskType.KILL_NPC) {
					if (task.getId() == npc.getID() && getTaskProgress(p, task) < task.getAmount()) {
						int newAmount = getTaskProgress(p, task) + 1;
						p.getCache().set("simpletask[" + task.getId() + "]_task_" + task.getTask().toString(),
							newAmount);
						if (newAmount == task.getAmount()) {
							p.message("@gre@You have completed slay npc" + npc.getDef().getName() + "x" + newAmount
								+ "!");
						}
					}
				}
			}
		}
	}

	public static void achievementListGUI(Player p, int achievement, int status) {
		com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
		s.setID(50);
		s.writeByte((byte) 2);
		s.writeInt(achievement);
		s.writeByte((byte) status);
		p.write(s.toPacket());
	}

	public static void achievementListGUI(Player p) {
		try {
			com.openrsc.server.net.PacketBuilder s = new com.openrsc.server.net.PacketBuilder();
			LinkedList<Achievement> availableTasks = AchievementSystem.getAchievements();
			s.setID(50);
			s.writeByte((byte) 1);
			s.writeShort(availableTasks.size());
			for (Achievement task : availableTasks) {
				s.writeInt(task.getId());
				s.writeByte((byte) p.getAchievementStatus(task.getId()));
				s.writeString(task.getName());
				s.writeString(task.getTitle());
				s.writeString((task.getDesc() != null ? task.getDesc() : "No description.."));
				//task desc?
			}
			p.write(s.toPacket());
		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
