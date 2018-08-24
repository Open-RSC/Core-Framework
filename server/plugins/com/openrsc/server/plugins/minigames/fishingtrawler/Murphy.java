package com.openrsc.server.plugins.minigames.fishingtrawler;

import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.showMenu;

import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

public class Murphy implements TalkToNpcListener, TalkToNpcExecutiveListener {

	/**
	 * IMPORTANT NOTES:
	 * 
	 * START EAST: 272, 741 START WEST: 320, 741 GO UNDER EAST: 251, 730 GO
	 * UNDER WEST: 296, 729 - NPC: 734 FAIL - AFTER GO UNDER EAST: 254, 759
	 * (SHARED) FAIL - AFTER GO UNDER WEST AND/OR QUIT MINI-GAME: 302, 759 GO
	 * BACK FROM FAIL LOCATION: 550, 711
	 */

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == 733 || n.getID() == 734 || n.getID() == 739;
	}

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		if (n.getID() == 733) { // Murphy on land
			if(p.isIronMan(1) || p.isIronMan(2) || p.isIronMan(3)) {
				p.message("As an Iron Man, you cannot use the Trawler.");
				return;
			}
			if (!p.getCache().hasKey("fishingtrawler")) {
				playerTalk(p, n, "good day to you sir");
				npcTalk(p, n, "well hello my brave adventurer");
				playerTalk(p, n, "what are you up to?");
				npcTalk(p, n, "getting ready to go fishing of course");
				npcTalk(p, n, "there's no time to waste");
				npcTalk(p, n, "i've got all the supplies i need from the shop at the end of the pier");
				npcTalk(p, n, "they sell good rope, although their bailing buckets aren't too effective");
				showStartOption(p, n, true, true, true);
			} else {
				playerTalk(p, n, "hello again murphy");
				npcTalk(p, n, "good day to you land lover");
				if(p.getCache().hasKey("fishing_trawler_reward")) {
					npcTalk(p,n, "It looks like your net is full from last trip");
					return;
				}
				npcTalk(p, n, "fancy hitting the high seas again?");
				int option = showMenu(p, n, "no thanks, i still feel ill from last time", "yes, lets do it");
				if (option == 0) {
					npcTalk(p, n, "hah..softy");
				} else if (option == 1) {
					letsGo(p, n);
				}
			}
		} else if (n.getID() == 734) {// Murphy on the boat.
			onship(n, p);
		} else if(n.getID() == 739) {
			npcTalk(p,n, "did you change your mind?");
			int opt = showMenu(p,n, "Yes, I want out", "No");
			if(opt == 0) {
				World.getWorld().getFishingTrawler().getWaitingShip().removePlayer(p);
				
			}
		}
	}

	private void showStartOption(Player p, Npc n, boolean b, boolean c, boolean d) {
		Menu menu = new Menu();
		if (b) {
			menu.addOption(new Option("what fish do you catch?") {
				@Override
				public void action() {
					npcTalk(p, n, "i get all sorts, anything that lies on the sea bed");
					npcTalk(p, n, "you never know what you're going to get until...");
					npcTalk(p, n, "...you pull up the net");
					showStartOption(p, n, false, true, true);
				}
			});
		}
		if (c) {
			menu.addOption(new Option("your boat doesn't look too safe") {
				@Override
				public void action() {
					npcTalk(p, n, "that's because it's not, the dawn thing's full of holes");
					playerTalk(p, n, "oh, so i suppose you can't go out for a while");
					npcTalk(p, n, "oh no, i don't let a few holes stop an experienced sailor like me");
					npcTalk(p, n, "i could sail these seas in a barrel	");
					npcTalk(p, n, "i'll be going out soon enough");
					showStartOption(p, n, true, false, true);
				}
			});
		}
		if (d) {
			menu.addOption(new Option("could i help?") {
				@Override
				public void action() {
					npcTalk(p, n, "well of course you can");
					npcTalk(p, n, "i'll warn you though, the seas are merciless");
					npcTalk(p, n, "and with out fishing experience you won't catch much");
					message(p, "you need a fishing level of 15 or above to catch any fish on the trawler");
					npcTalk(p, n, "on occasions the net rip's, so you'll need some rope to repair it");
					playerTalk(p, n, "rope...ok");
					npcTalk(p, n, "there's also a slight problem with leaks");
					playerTalk(p, n, "leaks!");
					npcTalk(p, n, "nothing some swamp paste won't fix");
					playerTalk(p, n, "swamp paste?");
					npcTalk(p, n, "oh, and one more thing...");
					npcTalk(p, n, "..i hope you're a good swimmer");
					int gooption = showMenu(p, n, "actually, i think i'll leave it", "i'll be fine, lets go",
							"what's swamp paste?");
					switch (gooption) {
					case 0:
						break;
					case 1:
						playerTalk(p, n, "i'll be fine, lets go");
						letsGo(p, n);
						break;
					case 2:
						npcTalk(p, n, "swamp tar mixed with flour...");
						npcTalk(p, n, "...which is then heated over a fire");
						playerTalk(p, n, "where can i find swamp tar?");
						npcTalk(p, n, "unfortunately the only supply of swamp tar is in the swamps below lumbridge");
						break;
					}
				}
			});
		}
		menu.showMenu(p);
	}

	protected void letsGo(Player p, Npc n) {
		npcTalk(p, n, "good stuff, jump aboard");
		npcTalk(p, n, "ok m hearty, keep your eys pealed");
		npcTalk(p, n, "i need you to clog up those holes quick time");
		playerTalk(p, n, "i'm ready and waiting");
		p.getCache().store("fishingtrawler", true);
		World.getWorld().getFishingTrawler().getWaitingShip().addPlayer(p);
		
//		npcTalk(p, n, "would you like to sail east or west?");
//		Menu goMenu = new Menu();
//		goMenu.addOptions(new Option("east please") {
//			@Override
//			public void action() {
//				npcTalk(p, n, "good stuff, jump aboard");
//				npcTalk(p, n, "ok m hearty, keep your eys pealed");
//				npcTalk(p, n, "i need you to clog up those holes quick time");
//				playerTalk(p, n, "i'm ready and waiting");
//				p.teleport(272, 741, true);
//			}
//		}, new Option("west please") {
//			@Override
//			public void action() {
//				npcTalk(p, n, "good stuff, jump aboard");
//				npcTalk(p, n, "ok m hearty, keep your eys pealed");
//				npcTalk(p, n, "i need you to clog up those holes quick time");
//				playerTalk(p, n, "i'm ready and waiting");
//				p.teleport(320, 741, true);
//			}
//		});
//		goMenu.showMenu(p);
	}

	void onship(Npc n, Player p) {
		npcTalk(p, n, "whoooahh sailor");
		int option = showMenu(p, n, "i've had enough,  take me back", "how you doing murphy?");
		if (option == 0) {
			npcTalk(p, n, "haa .. the soft land lovers lost there see legs have they?");
			playerTalk(p, n, "something like that");
			npcTalk(p, n, "we're too far out now, it'd be dangerous");
			option = showMenu(p, n, "I insist murphy, take me back", "Ok then murphy, just keep us afloat");
			if (option == 0) {
				npcTalk(p, n, "ok, ok, i'll try, but don't say i didn't warn you");
				message(p, 1900, "murphy sharply turns the large ship", "the boats gone under", "you're lost at sea!");
				World.getWorld().getFishingTrawler().quitPlayer(p);
			}
		}
		if (option == 1) {
			npcTalk(p, n, "get those fishey's");
		}
	}
}
