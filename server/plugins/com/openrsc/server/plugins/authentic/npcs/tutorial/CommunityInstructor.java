package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.NpcId;

import java.util.ArrayList;

public class CommunityInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island community instructor
	 */

	private final String HOW_TO_COMMUNICATE = "How can I communicate with other players?";
	private final String ARE_THERE_RULES = "Are there rules on ingame behaviour?";
	private final String GOODBYE = "goodbye then";
	private final String GLOBAL_CHAT = "Is there a global chat?";

	@Override
	public void onTalkNpc(Player player, Npc n) {
		npcsay(player, n, "You're almost ready to go out into the main game area",
			"When you get out there",
			"You will be able to interact with thousands of other players");

		ArrayList<String> options = createMultiMenu(GOODBYE);
		int menu = multi(player, n, options.toArray(new String[0]));

		if (menu == 0) {
			communicateDialogue(player, n);
		} else if (menu == 1) {
			behaviourDialogue(player, n);
		} else if (config().WANT_GLOBAL_CHAT && menu == 2) {
			globalChatDialog(player, n);
		}
	}

	private void communicateDialogue(Player player, Npc n) {
		npcsay(player, n, "typing in the game window will bring up chat",
			"Which players in the nearby area will be able to see",
			"If you want to speak to a particular friend anywhere in the game",
			"You will be able to select the smiley face icon",
			"then click to add a friend, and type in your friend's name",
			"If that player is logged in on the same world as you",
			"their name will go green",
			"If they are logged in on a different world their name will go yellow",
			"clicking on their name will allow you to send a message");

		ArrayList<String> options = createMultiMenu(HOW_TO_COMMUNICATE);
		int menu = multi(player, n, options.toArray(new String[0]));

		if (options.get(menu).equals(ARE_THERE_RULES)) {
			behaviourDialogue(player, n);
		} else if (options.get(menu).equals(GOODBYE)) {
			npcsay(player, n, "Good luck");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 100) {
				player.getCache().set("tutorial", 100);
			}
		} else if (config().WANT_GLOBAL_CHAT && options.get(menu).equals(GLOBAL_CHAT)) {
			globalChatDialog(player, n);
		}
	}

	private void behaviourDialogue(Player player, Npc n) {
		npcsay(player, n, "Yes you should read the rules of conduct on our front page",
			"To make sure you do nothing to get yourself banned",
			"but as general guide always try to be courteous to people in game",
			"Remember the people in the game are real people somewhere",
			"With real feelings",
			"If you go round being abusive or causing trouble",
			"your character could quickly be the one in trouble");

		ArrayList<String> options = createMultiMenu(ARE_THERE_RULES);
		int menu = multi(player, n, options.toArray(new String[0]));

		if (options.get(menu).equals(HOW_TO_COMMUNICATE)) {
			communicateDialogue(player, n);
		} else if (options.get(menu).equals(GOODBYE)) {
			npcsay(player, n, "Good luck");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 100) {
				player.getCache().set("tutorial", 100);
			}
		} else if (config().WANT_GLOBAL_CHAT && options.get(menu).equals(GLOBAL_CHAT)) {
			globalChatDialog(player, n);
		}
	}

	private void globalChatDialog(Player player, Npc npc) {
		final int globalCooldownSeconds = config().GLOBAL_MESSAGE_COOLDOWN / 1000;

		// Figure out global chat minutes/second cooldown
		final int minutes = globalCooldownSeconds / 60;
		final int seconds = globalCooldownSeconds % 60;

		// Figure out a String for the time
		String cooldown = "";
		if (globalCooldownSeconds > 0) {
			if (minutes > 0) {
				cooldown += minutes + " minutes";
				if (seconds > 0) {
					cooldown += " and " + seconds + " seconds";
				}
			} else {
				cooldown += seconds + " seconds";
			}
		}

		final int globalTotalLevel = config().GLOBAL_MESSAGE_TOTAL_LEVEL_REQ;

		npcsay(player, npc, "Why yes",
			"You can talk to everyone on the server at once",
			"All you have to do is start your message with ::g",
			"There are a couple caveats though",
			"Firstly, you cannot use global chat until you leave this island");
		if (globalTotalLevel > 0) {
			npcsay(player, npc, "Secondly, you must reach a total skill level of " + globalTotalLevel + " to speak in global chat");
		}
		npcsay(player, npc, "And lastly, remember that everyone can see your messages!",
			"Make sure that you're following all the rules and behaving yourself",
			"Nobody wants to see spam in global chat either, I'm sure you'll agree");
		if (globalCooldownSeconds > 0) {
			npcsay(player, npc, "To help alleviate this...",
				"...players can only send global chat messages every " + cooldown);
		}

		ArrayList<String> options = createMultiMenu(GLOBAL_CHAT);
		int menu = multi(player, npc, options.toArray(new String[0]));

		if (options.get(menu).equals(HOW_TO_COMMUNICATE)) {
			communicateDialogue(player, npc);
		} else if (options.get(menu).equals(ARE_THERE_RULES)) {
			behaviourDialogue(player, npc);
		} else if (options.get(menu).equals(GOODBYE)) {
			npcsay(player, npc, "Good luck");
			if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") < 100) {
				player.getCache().set("tutorial", 100);
			}
		}
	}

	/**
	 * Create the array for the multi menu
	 * @param justSelected The dialog choice that was just selected.
	 * @return The array of options to use for the multi menu
	 */
	private ArrayList<String> createMultiMenu(String justSelected) {
		ArrayList<String> options = new ArrayList<String>();
		// Add the authentic options
		options.add(HOW_TO_COMMUNICATE);
		options.add(ARE_THERE_RULES);
		options.add(GOODBYE);

		/*
		Add the option to ask about global chat if the config is enabled.
		We only want to add this option if global chat itself is enabled, not just the global friend
		This way, we can keep the authentic feel of Preservation-style worlds that choose to still
		enable global friend.
		 */
		if (config().WANT_GLOBAL_CHAT) {
			options.add(2, GLOBAL_CHAT);
		}

		// Remove the option that was just selected
		options.remove(justSelected);

		return options;
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.COMMUNITY_INSTRUCTOR.id();
	}

}
