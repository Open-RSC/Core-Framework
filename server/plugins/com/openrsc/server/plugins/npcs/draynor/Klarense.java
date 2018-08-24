package com.openrsc.server.plugins.npcs.draynor;

import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

public class Klarense implements TalkToNpcExecutiveListener, TalkToNpcListener {

	@Override
	public void onTalkToNpc(final Player p, final Npc n) {
		if (!p.getCache().hasKey("owns_ship")) {
			defaultDialogue(p, n);
		} else {
			ownsShipDialogue(p, n);
		}
	}

	private void ownsShipDialogue(final Player p, final Npc n) {
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option(
				"So would you like to sail this ship to Crandor Isle for me?") {
			@Override
			public void action() {
				npcTalk(p, n, " No not me, I'm frightened of dragons");
			}
		});
		defaultMenu.addOption(new Option("So what needs fixing on this ship?") {
			@Override
			public void action() {
				playerTalk(p, n, "So what needs fixing on this ship?");
				npcTalk(p, n,
						" Well the big gaping hole in the hold is the main problem");
				npcTalk(p, n, " you'll need a few planks");
				npcTalk(p, n, " Hammered in with steel nails");
			}
		});
		defaultMenu.addOption(new Option(
				"What are you going to do now you don't have a ship?") {
			@Override
			public void action() {
				npcTalk(p, n, " Oh I'll be fine");
				npcTalk(p, n, " I've got work as Port Sarim's first life guard");
			}
		});
		defaultMenu.showMenu(p);
	}

	private void defaultDialogue(final Player p, final Npc n) {
		npcTalk(p, n,
				" You're interested in a trip on the Lumbridge Lady are you?");
		npcTalk(p, n,
				" I admit she looks fine, but she isn't seaworthy right now");
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option(
				"Do you know when she will be seaworthy?") {
			@Override
			public void action() {
				npcTalk(p, n, " No not really");
				npcTalk(p, n,
						" Port Sarim's shipbuilders aren't very efficient");
				npcTalk(p, n, " So it could be quite a while");
			}
		});
		if (p.getQuestStage(Quests.DRAGON_SLAYER) == 2) {
			defaultMenu.addOption(new Option(
					"Would you take me to Crandor Isle when it's ready?") {
				@Override
				public void action() {
					npcTalk(p, n, " Well even if I knew how to get there");
					npcTalk(p, n, " I wouldn't like to risk it");
					npcTalk(p, n,
							" Especially after to goin to all the effort of fixing the old girl up");
				}
			});
			defaultMenu.addOption(new Option("I don't suppose I could buy it") {
				@Override
				public void action() {
					npcTalk(p, n, " I guess you could");
					npcTalk(p, n,
							" I'm sure the work needed to do on it wouldn't be too expensive");
					npcTalk(p, n, " How does 2000 gold sound for a price?");
					new Menu()
							.addOptions(
									new Option("Yep sounds good") {
										@Override
										public void action() {
											if (p.getInventory().countId(10) >= 2000) {
												npcTalk(p, n,
														"Ok, she's all yours.");
												p.getCache().store("owns_ship",
														true);
												p.getInventory().remove(10,
														2000);
											}
										}
									},
									new Option(
											"I'm not paying that much for a broken boat") {
										@Override
										public void action() {
											npcTalk(p, n,
													" That's Ok, I didn't particularly want to sell anyway");
										}
									}).showMenu(p);
				}
			});
		}
		defaultMenu.addOption(new Option("Ah well, never mind") {
			@Override
			public void action() {
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getDef().getName().equals("Klarense");
	}

}
