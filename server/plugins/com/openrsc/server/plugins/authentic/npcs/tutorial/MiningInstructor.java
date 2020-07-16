package com.openrsc.server.plugins.authentic.npcs.tutorial;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class MiningInstructor implements TalkNpcTrigger {
	/**
	 * Tutorial island mining instructor
	 */

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 45) {
			say(player, n, "Good day to you");
			npcsay(player, n, "hello I'm a veteran miner!",
				"I'm here to show you how to mine",
				"If you want to quickly find out what is in a rock you can prospect it",
				"right click on this rock here",
				"And select prospect");
			player.getCache().set("tutorial", 49);
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 49) {
			say(player, n, "Hello again");
			npcsay(player, n, "You haven't prospected that rock yet",
				"Right click on it and select prospect");
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 50) {
			say(player, n, "There's tin ore in that rock");
			npcsay(player, n, "Yes, thats what's in there",
				"Ok you need to get that tin out of the rock",
				"First of all you need a pick",
				"And here we have a pick");
			mes("The instructor somehow produces a large pickaxe from inside his jacket");
			delay(3);
			mes("The instructor gives you the pickaxe");
			delay(3);
			give(player, ItemId.BRONZE_PICKAXE.id(), 1); // Add a bronze pickaxe to the players inventory
			npcsay(player, n, "Now hit those rocks");
			player.getCache().set("tutorial", 51);
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") == 51) {
			if (!player.getCarriedItems().hasCatalogID(ItemId.BRONZE_PICKAXE.id(), Optional.of(false))) {
				say(player, n, "I have lost my pickaxe");
				mes("The instructor somehow produces a large pickaxe from inside his jacket");
				delay(3);
				mes("The instructor gives you the pickaxe");
				delay(3);
				give(player, ItemId.BRONZE_PICKAXE.id(), 1); // Add a bronze pickaxe to the players inventory
			}
			npcsay(player, n, "to mine a rock just left click on it",
					"If you have a pickaxe in your inventory you might get some ore");
		} else if (player.getCache().hasKey("tutorial") && player.getCache().getInt("tutorial") >= 52) {
			if (player.getCache().getInt("tutorial") == 52)
				npcsay(player, n, "very good");
			npcsay(player, n, "If at a later date you find a rock with copper ore",
					"You can take the copper ore and tin ore to a furnace",
					"use them on the furnace to make bronze bars",
					"which you can then either sell",
					"or use on anvils with a hammer",
					"To make weapons",
					"as your mining and smithing levels grow",
					"you will be able to mine various exciting new metals",
					"now go through the next door to speak to the bankers");
			if (player.getCache().getInt("tutorial") == 52)
				player.getCache().set("tutorial", 55);
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.MINING_INSTRUCTOR.id();
	}

}
