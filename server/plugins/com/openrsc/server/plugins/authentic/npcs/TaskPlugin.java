package com.openrsc.server.plugins.authentic.npcs;

import com.openrsc.server.content.achievement.Achievement;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.ArrayList;

public class TaskPlugin implements TalkNpcTrigger, OpLocTrigger {

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		ArrayList<Achievement> availableTasks = player.getWorld().getServer().getAchievementSystem().getAvailableQuestsForEntity(player, obj);
		if (availableTasks.size() > 1) {
			player.message("You can get multiple tasks from this object");
			Menu menu = new Menu();
			for (Achievement task : availableTasks) {
				menu.addOption(new Option(task.getName()) {
					@Override
					public void action() {
						//AchievementSystem.triggerTask(player, obj, task);
					}
				});
			}
			menu.showMenu(player);
		} else if (availableTasks.size() == 1) {
			//AchievementSystem.triggerTask(player, obj, availableTasks.get(0));
		}

	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		ArrayList<Achievement> availableTasks = player.getWorld().getServer().getAchievementSystem().getAvailableQuestsForEntity(player, n);
		if (availableTasks.size() > 1) {
			player.message("You can get multiple tasks from this character");
			Menu menu = new Menu();
			for (Achievement task : availableTasks) {
				menu.addOption(new Option(task.getName()) {
					@Override
					public void action() {
						//AchievementSystem.triggerTask(p, n, task);
					}
				});
			}
			menu.showMenu(player);
		} else if (availableTasks.size() == 1) {
			//AchievementSystem.triggerTask(p, n, availableTasks.get(0));
		}
	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return player.getWorld().getServer().getAchievementSystem().getAvailableQuestsForEntity(player, obj).size() > 0;
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return player.getWorld().getServer().getAchievementSystem().getAvailableQuestsForEntity(player, n).size() > 0;
	}
}
