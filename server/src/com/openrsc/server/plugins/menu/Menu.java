package com.openrsc.server.plugins.menu;

import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;

import java.util.ArrayList;

import static com.openrsc.server.plugins.Functions.delay;
import static com.openrsc.server.plugins.Functions.say;

/**
 * This system is for adding a new menu item on NPC under certain circumstances.
 * If this system is used, the whole starting menu needs to be done using this.
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
	public Menu addOption(final Option option) {
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
	public Menu addOptions(final Option... opts) {
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
	public void showMenu(final Player player) {
		String[] option = new String[options.size()];
		int i = 0;
		for (Option opt : options) {
			option[i] = opt.getOption();
			i++;
		}
		player.setMenu(this);
		ActionSender.sendMenu(player, option);
		long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start <= 19500 && player.getMenu() != null && player.getOption() == -1) {
			delay();
		}

		doReply(player);
	}

	public int size() {
		return options.size();
	}

	private void doReply(final Player player) {
		final int i = player.getOption();
		if(i >= 0 && i <= options.size()) {
			Option option = options.get(i);
			if (option != null) {
				say(player, null, option.getOption());
				option.action();
			}
		}
	}

	public final void handleReply(final Player player, final int i) {
		player.setOption(i);
		player.resetMenuHandler();
	}
}
