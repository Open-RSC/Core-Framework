package com.openrsc.server.plugins.npcs.ardougne.east;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

public final class CaptainBarnaby implements ObjectActionListener, ObjectActionExecutiveListener, TalkToNpcExecutiveListener,
TalkToNpcListener {
public static final int BARNABY = 316;
@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		npcTalk(p, n, "Do you want to go on a trip to Karamja?",
				"The trip will cost you 30 gold");		
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Yes please") {
			@Override
			public void action() {
				if (p.getInventory().remove(10, 30) > -1) {
					message(p, "You pay 30 gold", "You board the ship");
					p.teleport(467, 651, false);
					sleep(1000);
					message(p, "The ship arrives at Karamja");
				} else {
					playerTalk(p, n,
							"Oh dear I don't seem to have enough money");
					}
			}
		});
		defaultMenu.addOption(new Option("No thankyou") {
			@Override
			public void action() {
				npcTalk(p, n, "No I need to stay alive");
				npcTalk(p, n, "I have a wife and family to support");
				}
			});
			defaultMenu.showMenu(p);
			}

		@Override
		public boolean blockTalkToNpc(Player p, Npc n) {
			return n.getID() == 316;
			}


		@Override
			public void onObjectAction(GameObject obj, String command, Player p) {
			if(obj.getID() == 157) {
				if(command.equals("board")) {
					if(p.getY() != 616 ) {				
						return;
						}
					Npc captain = getNearestNpc(p, BARNABY, 5);
					if(captain != null) {
						captain.initializeTalkScript(p);
						} else {							
							p.message("I need to speak to the captain before boarding the ship.");
						}
				}
			}
		}
		@Override
			public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
				return (arg0.getID() == 157 && arg0.getLocation().equals(Point.location(536, 617)))
					|| (arg0.getID() == 155 && arg0.getLocation().equals(Point.location(531, 617)));				
			}
		}