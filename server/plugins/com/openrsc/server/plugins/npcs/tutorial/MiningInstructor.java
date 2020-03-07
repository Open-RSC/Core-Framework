package com.openrsc.server.plugins.npcs.tutorial;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class MiningInstructor implements TalkNpcTrigger {
	/**
	 * @author Davve
	 * Tutorial island mining instructor
	 */

	@Override
	public void onTalkNpc(Player p, Npc n) {
		if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 45) {
			playerTalk(p, n, "Good day to you");
			npcTalk(p, n, "hello I'm a veteran miner!",
				"I'm here to show you how to mine",
				"If you want to quickly find out what is in a rock you can prospect it",
				"right click on this rock here",
				"And select prospect");
			p.getCache().set("tutorial", 49);
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 49) {
			playerTalk(p, n, "Hello again");
			npcTalk(p, n, "You haven't prospected that rock yet",
				"Right click on it and select prospect");
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 50) {
			playerTalk(p, n, "There's tin ore in that rock");
			npcTalk(p, n, "Yes, thats what's in there",
				"Ok you need to get that tin out of the rock",
				"First of all you need a pick",
				"And here we have a pick");
			message(p, "The instructor somehow produces a large pickaxe from inside his jacket",
					"The instructor gives you the pickaxe");
			addItem(p, ItemId.BRONZE_PICKAXE.id(), 1); // Add a bronze pickaxe to the players inventory
			npcTalk(p, n, "Now hit those rocks");
			p.getCache().set("tutorial", 51);
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 51) {
			if (!p.getCarriedItems().hasCatalogID(ItemId.BRONZE_PICKAXE.id(), Optional.of(false))) {
				playerTalk(p, n, "I have lost my pickaxe");
				message(p, "The instructor somehow produces a large pickaxe from inside his jacket",
					"The instructor gives you the pickaxe");
				addItem(p, ItemId.BRONZE_PICKAXE.id(), 1); // Add a bronze pickaxe to the players inventory
			}
			npcTalk(p, n, "to mine a rock just left click on it",
					"If you have a pickaxe in your inventory you might get some ore");
		} else if (p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") >= 52) {
			if (p.getCache().getInt("tutorial") == 52)
				npcTalk(p, n, "very good");
			npcTalk(p, n, "If at a later date you find a rock with copper ore",
					"You can take the copper ore and tin ore to a furnace",
					"use them on the furnace to make bronze bars",
					"which you can then either sell",
					"or use on anvils with a hammer",
					"To make weapons",
					"as your mining and smithing levels grow",
					"you will be able to mine various exciting new metals",
					"now go through the next door to speak to the bankers");
			if (p.getCache().getInt("tutorial") == 52)
				p.getCache().set("tutorial", 55);
		}
	}

	@Override
	public boolean blockTalkNpc(Player p, Npc n) {
		return n.getID() == NpcId.MINING_INSTRUCTOR.id();
	}

}
