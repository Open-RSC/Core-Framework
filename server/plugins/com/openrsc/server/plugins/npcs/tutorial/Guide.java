package com.openrsc.server.plugins.npcs.tutorial;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class Guide implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island guide first room
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
	
		npcTalk(p,n, "Hello, if you're using android client,",
		"use volume buttons to rotate screen",
		"touch and hold to open right click menu",
		"If you find that hard to use",
		"you can set Mouse Buttons to One",
		"from the wrench menu on top right of the screen",
		"If you find android client too hard to play with",
		"You can always play on PC",
		"visit our website to learn more",
		"before we continue further, would you like to skip this tutorial?");
		
		int o = showMenu(p,n, "Yes!", "No");
		if(o == 0) {
			if(p.getCache().hasKey("tutorial")) {
				p.getCache().remove("tutorial");
			}
			p.teleport(122, 647);
			if (!p.getInventory().hasItemId(70)) { // bronze long sword
				p.getInventory().add(new Item(70, 1));
			}
			if (!p.getInventory().hasItemId(108)) { // bronze large
				p.getInventory().add(new Item(108, 1));
			}
			if (!p.getInventory().hasItemId(117)) { // bronze chain
				p.getInventory().add(new Item(117, 1));
			}
			if (!p.getInventory().hasItemId(206)) { // bronze legs
				p.getInventory().add(new Item(206, 1));
			}
			if (!p.getInventory().hasItemId(4)) { // wooden shield
				p.getInventory().add(new Item(4, 1));
			}
			if (!p.getInventory().hasItemId(376)) { // net
				p.getInventory().add(new Item(376, 1));
			}
			if (!p.getInventory().hasItemId(156)) { // bronze pickaxe
				p.getInventory().add(new Item(156, 1));
			}
			if (!p.getInventory().hasItemId(33)) { // air runes
				p.getInventory().add(new Item(33, 12));
			}
			if (!p.getInventory().hasItemId(35)) { // mind runes
				p.getInventory().add(new Item(35, 8));
			}
			if (!p.getInventory().hasItemId(32)) { // water runes
				p.getInventory().add(new Item(32, 3));
			}
			if (!p.getInventory().hasItemId(34)) { // earth runes
				p.getInventory().add(new Item(34, 2));
			}
			if (!p.getInventory().hasItemId(36)) { // body runes
				p.getInventory().add(new Item(36, 1));
			}
			if (!p.getInventory().hasItemId(1263)) { // sleeping bag
				p.getInventory().add(new Item(1263, 1));
			}
			
			if (!p.getInventory().hasItemId(11)) { // sleeping bag
				p.getInventory().add(new Item(11, 25));
			}
			if (!p.getInventory().hasItemId(188)) { // sleeping bag
				p.getInventory().add(new Item(188, 1));
			}
			
			for(int i = 0;i < 10;i++) {
				p.getInventory().add(new Item(132, 1));
			}
			
			World.getWorld().sendWorldAnnouncement("New adventurer @gre@" + p.getUsername() + "@whi@ has arrived in lumbridge!");
			ActionSender.sendBox(p, "Welcome to Open RSC! % To speak on general chat type ::g <message> % @gre@The most populated city is Edgeville %"
										+ "We hope you enjoy playing! Have fun!", false);
		} else if(o == 1) {
			if(p.getCache().hasKey("tutorial") && p.getCache().getInt("tutorial") == 10) {
				npcTalk(p, n, "Please proceed through the next door");
				return;
			}
			npcTalk(p, n, "Welcome to the world of runescape",
					"My job is to help newcomers find their feet here");
			playerTalk(p, n, "Ah good, let's get started");
			npcTalk(p, n, "When speaking to characters such as myself",
					"Sometimes options will appear in the top left corner of the screen",
					"left click on one of them to continue the conversation");
			int menu = showMenu(p, n, "So what else can you tell me?", "What other controls do I have?");
			if(menu == 0) {
				npcTalk(p, n, "I suggest you go through the door now",
						"There are several guides and advisors on the island",
						"Speak to them",
						"They will teach you about the various aspects of the game");
				ActionSender.sendBox(p, "Use the quest history tab at the bottom of the screen to reread things said to you by ingame characters", false);
				p.getCache().set("tutorial", 10);
			} else if(menu == 1) {
				npcTalk(p, n, "I suggest you talk to the controls guide through the door");
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 476;
	}

}
