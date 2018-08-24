package com.openrsc.server.plugins.npcs.tutorial;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class MiningInstructor implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island mining instructor
	 */

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 55) {
			npcTalk(p, n, "Please proceed through the next door");
			return;
		}
		if(!hasItem(p, 156) && p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 50) {
			playerTalk(p, n, "There's tin ore in that rock");
			npcTalk(p, n, "Yes, that's what's in there",
					"Ok you need to get that tin out of the rock",
					"First of all you need a pick",
					"And here we have a pick");
			message(p, "The instructor somehow produces a large pickaxe from inside his jacket",
					"The instructor gives you the pickaxe");
			addItem(p, 156, 1); // Add a bronze pickaxe to the players inventory
			npcTalk(p, n, "Now hit those rocks");
			return;
		}
		if(hasItem(p, 156) && !hasItem(p, 202)) {
			npcTalk(p, n, "to mine a rock just left click on it",
					"If you have a pickaxe in your inventory you might get some ore");
			return;
		}
		if(hasItem(p, 156) && hasItem(p, 202)) {
			npcTalk(p, n, "very good",
					"If at a later date you find a rock with copper ore",
					"You can take the copper ore and tin ore to a furnace",
					"use them on the furnace to make bronze bars",
					"which you can then either sell",
					"or use on anvils with a hammer",
					"To make weapons",
					"as your mining and smithing levels grow",
					"you will be able to mine various exciting new metals",
					"now go through the next door to speak to the bankers");
			p.getCache().set("tutorial", 55);
			return;
		}
		playerTalk(p, n, "Good day to you");
		npcTalk(p, n, "hello i'm a veteran miner!",
				"I'm here to show you how to mine",
				"If you want to quickly find out what is in a rock you can prospect it",
				"right click on this rock here",
				"And select prospect");
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 482;
	}

}
