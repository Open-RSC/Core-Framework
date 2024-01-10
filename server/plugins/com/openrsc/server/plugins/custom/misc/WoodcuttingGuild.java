package com.openrsc.server.plugins.custom.misc;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Skill;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import static com.openrsc.server.plugins.Functions.validatebankpin;
import static com.openrsc.server.plugins.RuneScript.*;

public class WoodcuttingGuild implements TalkNpcTrigger {
	public static void frontGate(Player player) {
		if (ifnearvisnpc(NpcId.FORESTER_WOODCUTTING_GUILD.id())) {
			foresterDialogue(player);
		} else {
			mes("The gate is locked");
			delay(3);
			mes("The forester should be able to help you get in");
		}
	}

	private static void foresterDialogue(Player player) {
		npcsay("You need to pay a toll of 1000 gold before you can go in there");
		int option = multi("Who does the money go to?",
			"Alright here you go",
			"No way");
		if (option == 0) {
			npcsay("This land is owned by Mr. McGrubor",
				"The money goes to him for its upkeep");
		} else if (option == 1) {
			if (ifheld(ItemId.COINS.id(), 1000)) {
				mes("You hand the gold to the forester");
				Item itemToRemove = new Item(ItemId.COINS.id(), 1000);
				if (player.getCarriedItems().remove(itemToRemove) == -1) return;
				delay(3);
				if (player.getSkills().getLevel(Skill.WOODCUTTING.id()) >= 55) {
					mes("The gate swings open and you walk through");
					player.teleport(560, 472);
				} else {
					mes("The forester puts out his hand to stop you");
					delay(3);
					npcsay("Hold on a minute",
						"You aren't skilled enough to go in there");
					say("Well what about my gold?");
					npcsay("Consider it a donation");
					mes("You need to have a woodcutting level of 55 to enter");
				}
			} else {
				say("Oh dear I don't seem to have enough money");
				npcsay("Well then you aren't going in");
			}
		}
	}

	private static void openBank(Player player) {
		if(validatebankpin(player, null)) {
			player.setAccessingBank(true);
			ActionSender.showBank(player);
		}
	}

	private static boolean takeFromBank(Player player) {
		if (player.getBank().countId(ItemId.COINS.id()) >= 1000) {
			player.getBank().remove(ItemId.COINS.id(), 1000, false);
			return true;
		}
		return false;
	}

	public static void mcGruborDialogue(Player player) {
		if (player.getAttribute("mcgrubor_bank", false)) {
			if (takeFromBank(player)) {
				mes("You open the bank chest and Mr. McGrubor takes his fee");
				delay(3);
				openBank(player);
			} else {
				mes("You don't have enough coins in your bank!");
				player.setAttribute("mcgrubor_bank", false);
			}
			return;
		}

		npcsay("If you want to use my chest it'll cost you",
			"I've gotta pay my workers to take the stuff over to the bank",
			"Plus a small convenience fee on top of course");
		int option = multi(false,
			"Use bank chest - 1000 gold",
			"No thanks!",
			"Take fee from bank until logout");
		if (option == 0) {
			say("Alright");
			if (ifheld(ItemId.COINS.id(), 1000)) {
				mes("You hand McGrubor the coins");
				Item itemToRemove = new Item(ItemId.COINS.id(), 1000);
				if (player.getCarriedItems().remove(itemToRemove) == -1) return;
				delay(3);
				mes("You open the bank chest");
				delay(3);
				openBank(player);
			} else {
				npcsay("Looks like you don't have enough coins with you",
					"Best start walking to the bank then");
			}
		} else if (option == 1) {
			say("No thanks!");
		}else if (option == 2) {
			say("I'm going to be here for a while",
				"You can just take the fee from my bank");
			if (takeFromBank(player)) {
				player.setAttribute("mcgrubor_bank", true);
				mes("You open the bank chest and Mr. McGrubor takes his fee");
				delay(3);
				openBank(player);
			} else {
				npcsay("What are you talking about?",
					"You don't have enough gold in your bank");
			}
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (npc.getID() == NpcId.FORESTER_WOODCUTTING_GUILD.id()) {
			int option = multi("Can I go in?",
				"What is this place?");
			if (option == 0) {
				foresterDialogue(player);
			} else if (option == 1) {
				npcsay("This is McGrubor's wood",
					"For a small fee you can go in and cut the trees");
			}
		} else if (npc.getID() == NpcId.MCGRUBOR.id()) {
			int option = multi("Can I use your bank chest?",
				"Your guard dogs keep attacking me");
			if (option == 0) {
				mcGruborDialogue(player);
			} else if (option == 1) {
				npcsay("They're just doing what I trained them to do",
					"A dog isn't gonna know if you paid to be in here or not",
					"If you don't like it you can leave");
			}
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.FORESTER_WOODCUTTING_GUILD.id()
			|| npc.getID() == NpcId.MCGRUBOR.id();
	}
}
