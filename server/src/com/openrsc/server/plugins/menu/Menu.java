package com.openrsc.server.plugins.menu;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.Functions;

import java.util.ArrayList;

/**
 * This system is for adding a new menu item on NPC under certain circumstances.
 * If this system is used, the whole starting menu needs to be done using this.
 *
 * @author n0m
 */
public class Menu {

	private ArrayList<Option> options = new ArrayList<Option>();

	/**
	 * Adds a single option to the menu. Usage: Menu defaultMenu = new Menu();
	 * defaultMenu.addOption(new Option("Hello, this is a menu item") { public
	 * void action() {
	 * <p>
	 * } }
	 *
	 * @param option
	 * @return
	 */
	public Menu addOption(Option option) {
		options.add(option);
		return this;
	}

	/**
	 * Adds multiple options at once. defaultMenu.addOptions(new
	 * Option("Hello, this is a menu item") { public void action() {
	 * <p>
	 * } }, new Option("Hello, this is a menu item") { public void action() {
	 * <p>
	 * } });
	 *
	 * @param opts
	 * @return
	 */
	public Menu addOptions(Option... opts) {
		for (Option i : opts) {
			options.add(i);
		}
		return this;
	}

	/**
	 * Builds and displays the menu to the player.
	 *
	 * @param player
	 */
	public void showMenu(Player player) {
		String[] option = new String[options.size()];
		int i = 0;
		for (Option opt : options) {
			option[i] = opt.getOption();
			i++;
		}
		player.setMenu(this);
		ActionSender.sendMenu(player, option);
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start <= 19500
			&& player.getMenu() != null) {
			if (player.getInteractingNpc() != null)
				player.getInteractingNpc().setBusyTimer(3000);
			Functions.sleep(100);
		}
	}


	public int size() {
		return options.size();
	}

	public void handleReply(Player player, int i) {
		Option option = options.get(i);
		if (option != null) {
			Functions.playerTalk(player, player.getInteractingNpc(), option.getOption());
			option.action();
		}
		player.resetMenuHandler();
	}
}
