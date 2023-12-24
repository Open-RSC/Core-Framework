package com.openrsc.server.plugins.custom.minigames.mumsassistant;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Minigames;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.MiniGameInterface;
import com.openrsc.server.plugins.custom.minigames.ALumbridgeCarol;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Mum implements TalkNpcTrigger, MiniGameInterface {

	@Override
	public void onTalkNpc(Player player, Npc npc) {
		if (config().A_LUMBRIDGE_CAROL && ALumbridgeCarol.inPartyRoom(npc)) {
			ALumbridgeCarol.partyDialogue(player, npc);
			return;
		}

		npcsay(player, npc, "Hello, sweetie", "I hope your adventuring is going well");

		int stage;
		if (player.getCache().hasKey("mums_assistant")) {
			stage = player.getCache().getInt("mums_assistant");
		} else {
			stage = 0;
		}

		ArrayList<String> options = new ArrayList<String>();
		String sweater = "I've lost my Christmas sweater";
		String christmas = "Did you used to date the Duke?";
		String hello = "Hello mother, how are you today?";
		String whatIngredients = "What was I supposed to get you again?";
		String haveStuff = "I have the ingredients you needed";
		String pizzaBagel = "Could I please have another pizza bagel?";
		String bye = "Bye, have a good day";

		if (config().A_LUMBRIDGE_CAROL && ALumbridgeCarol.getStage(player) == ALumbridgeCarol.LETTER_DELIVERY) {
			options.add(christmas);
		}

		if (ALumbridgeCarol.getStage(player) == ALumbridgeCarol.COMPLETED
			&& !ALumbridgeCarol.hasSweater(player)) {
			options.add(sweater);
		}

		if (stage == 0) { // Not started
			options.add(hello);
		} else if (stage == 1) { // Started
			options.add(whatIngredients);
			// Check for items
			if (player.getCarriedItems().getInventory().countId(ItemId.CHEESE.id(), Optional.of(false)) > 0
				&& player.getCarriedItems().getInventory().countId(ItemId.TOMATO.id(), Optional.of(false)) > 0
				&& player.getCarriedItems().getInventory().countId(ItemId.PIZZA_BASE.id(), Optional.of(false)) > 0) {

				options.add(haveStuff);
			}
		} else if (stage == -1) { // Completed
			options.add(pizzaBagel);
		}

		options.add(bye);

		final String finalOptions[] = new String[options.size()];
		int option = multi(player, npc, options.toArray(finalOptions));

		if (option == -1) return;
		if (options.get(option).equals(hello)) {
			npcsay(player, npc,
				"Not very well, actually",
				"I was going to make you a snack, but it seems I don't have the ingredients",
				"And I have too much to do around the house to go out and get them");

			if (multi(player, npc, "I could get them for you", "That's okay, don't worry about it") == 0) {
				npcsay(player, npc,
					"Oh would you?",
					"That would be wonderful");
				say(player, npc, "What do you need?");
				npcsay(player, npc,
					"All I need is a tomato, a wedge of cheese, and some pizza dough",
					"If you need help, I'm sure you could ask some of your little friends where to find things");
				if (multi(player, npc, "Okay, I'll be right back", "Actually, maybe later") == 0) {
					// Start
					player.getCache().set("mums_assistant", 1);
				}
			}
		} else if (options.get(option).equals(whatIngredients)) {
			npcsay(player, npc, "Hehe, you can be so forgetful sometimes",
				"All I need is a tomato, a wedge of cheese, and some pizza dough",
				"Thanks again, dear!");
		} else if (options.get(option).equals(haveStuff)) {
			// Get items
			Item cheese = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(ItemId.CHEESE.id(), Optional.of(false))
			);
			Item tomato = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(ItemId.TOMATO.id(), Optional.of(false))
			);
			Item pizzaDough = player.getCarriedItems().getInventory().get(
				player.getCarriedItems().getInventory().getLastIndexById(ItemId.PIZZA_BASE.id(), Optional.of(false))
			);

			if (cheese == null || tomato == null || pizzaDough == null) return;

			npcsay(player, npc, "Oh sweetie, thank you so much");

			// Remove items
			mes("You hand your mum the wedge of cheese");
			delay(3);
			player.getCarriedItems().remove(cheese);
			mes("You hand your mum the tomato");
			delay(3);
			player.getCarriedItems().remove(tomato);
			mes("You hand your mum the pizza dough");
			delay(3);
			player.getCarriedItems().remove(pizzaDough);
			mes("She takes the ingredients and quickly whips up a plate of pizza bagels");
			delay(3);
			npcsay(player, npc, "Here you are dear");
			mes("Your mother hands you a pizza bagel");
			delay(3);
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIZZA_BAGEL.id()));
			say(player, npc, "Thank you");
			npcsay(player, npc, "You can come back whenever you'd like another",
				"Thanks again!");
			player.sendMiniGameComplete(this.getMiniGameId(), Optional.empty());

		} else if (options.get(option).equals(pizzaBagel)) {
			npcsay(player, npc,
				"Of course dear",
				"But make sure you go out and get some exercise",
				"We wouldn't want you to get fat");
			player.getCarriedItems().getInventory().add(new Item(ItemId.PIZZA_BAGEL.id()));
			mes("Your mum hands you a pizza bagel");
			delay(3);
			say(player, npc, "Thank you");
		} else if (options.get(option).equals(christmas)) {
			ALumbridgeCarol.mumDialogue(player, npc);
		} else if (options.get(option).equals(sweater)) {
			npcsay(player, npc, "Oh dear",
				"That's ok",
				"Luckily I've made you a spare");
			mes("Your mum hands you a new Christmas sweater");
			if (player.isMale()) {
				give(player, ItemId.RED_CHRISTMAS_SWEATER.id(), 1);
			} else {
				give(player, ItemId.FEMALE_RED_CHRISTMAS_SWEATER.id(), 1);
			}
			delay(3);
			npcsay(player, npc, "Don't forget that the material can be dyed very easily",
				"Stay warm!");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc npc) {
		return npc.getID() == NpcId.MUM.id();
	}

	@Override
	public int getMiniGameId() {
		return Minigames.MUMS_ASSISTANT;
	}

	@Override
	public String getMiniGameName() {
		return "Mum's Assistant (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		player.getCache().set("mums_assistant", -1);
	}
}
