package com.openrsc.server.plugins.npcs.tutorial;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.showMenu;
import static com.openrsc.server.plugins.Functions.sleep;

import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

public class boatman implements TalkToNpcExecutiveListener, TalkToNpcListener {
	/**
	 * @author Davve
	 * Tutorial island boat man - last npc before main land (Lumbridge)
	 */
	@Override
	public void onTalkToNpc(Player p, Npc n) {
		npcTalk(p, n, "Hello my job is to take you to the main game area",
				"It's only a short row",
				"I shall take you to the small town of Lumbridge",
				"In the kingdom of Misthalin");
		int menu = showMenu(p, n, "Ok I'm ready to go", "I'm not done here yet");
		if(menu == 0) {
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
			for(int i = 0;i < 8;i++) {
				p.getInventory().add(new Item(132, 1));
			}
			npcTalk(p, n, "Lets go then");
			p.message("You have completed the tutorial");
			p.teleport(120, 648, false);
			if(p.getCache().hasKey("tutorial")) {
				p.getCache().remove("tutorial");
			}
			sleep(2000);
			p.message("The boat arrives in Lumbridge");
			World.getWorld().sendWorldAnnouncement("New adventurer @gre@" + p.getUsername() + "@whi@ has arrived in lumbridge!");
		} else if(menu == 1) {
			// MISSING DIALOGUE.
		}
		
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 497;
	}

}
