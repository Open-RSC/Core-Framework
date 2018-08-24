package com.openrsc.server.plugins.npcs.portsarim;

import static com.openrsc.server.plugins.Functions.addItem;
import static com.openrsc.server.plugins.Functions.getNearestNpc;
import static com.openrsc.server.plugins.Functions.hasItem;
import static com.openrsc.server.plugins.Functions.message;
import static com.openrsc.server.plugins.Functions.npcTalk;
import static com.openrsc.server.plugins.Functions.playerTalk;
import static com.openrsc.server.plugins.Functions.removeItem;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

public final class WormBrain implements WallObjectActionListener, WallObjectActionExecutiveListener {

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		if (obj.getID() == 30 && obj.getX() == 283 && obj.getY() == 665) {
			return true;
		} 
		return false;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, final Player p) {
		if (obj.getID() == 30 && obj.getX() == 283 && obj.getY() == 665) {
			final Npc n = getNearestNpc(p, 192, 10);
			message(p, "...you knock on the cell door");
			npcTalk(p, n, "Whut you want?");
			Menu defaultMenu = new Menu();
			if (p.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !hasItem(p, 416)) {
				defaultMenu.addOption(new Option("I believe you've got a piece of map that I need") {
					@Override
					public void action() {
						npcTalk(p, n, "So? Why should I be giving it to you? What you do for Wormbrain?");
						new Menu().addOptions(
								new Option("I'm not going to do anything for you. Forget it") {
									public void action() {
										npcTalk(p, n, "Me keep map piece, you no get map piece");
									}
								},
								new Option("I'll let you live. I could just kill you") {
									@Override
									public void action() {
										npcTalk(p, n, "Ha! Me in here you out dere. You not get map piece");
									}
								}, new Option("I suppose I could pay you for the map piece...") {
									@Override
									public void action() {
										playerTalk(p, n, "Say, 10,000 coins?");
										npcTalk(p, n, "Me not stoopid, it worth at least 1,000,000 coins!");
										new Menu().addOptions(
												new Option("You must be joking! Forget it") {
													public void action() {
														npcTalk(p, n, "Me keep map piece, you no get map piece");
													}
												}, new Option("Alright then, 1,000,000 it is") {
													@Override
													public void action() {
														if(hasItem(p, 10, 1000000)) {
															removeItem(p, 10, 1000000);
															p.message("You buy the map piece from Wormbrain");
															npcTalk(p, n, "Fank you very much! Now me can bribe da guards, hehehe");
															addItem(p, 416, 1);
														} else {
															playerTalk(p, n, "Oops, I don't have enough on me");
															npcTalk(p, n, "Comes back when you has enough");
														}
													}
												}).showMenu(p);
									}
								}, new Option("Where did you get the map piece from?") {
									@Override
									public void action() {
										npcTalk(p, n, "Found it when me pick some stuff up, me kept it");
									}
								}).showMenu(p);
					}
				});
			}
			defaultMenu.addOption(new Option("What are you in for?") {
				@Override
				public void action() {
					npcTalk(p, n, "Me not sure. Me pick some stuff up and take it away");
					playerTalk(p, n, "Well, did the stuff belong to you?");
					npcTalk(p, n, "Umm...no");
					playerTalk(p, n, "Well, that would be why then");
					npcTalk(p, n, "Oh, right");
				}
			});
			defaultMenu.addOption(new Option("Sorry, thought this was a zoo") {
				@Override
				public void action() {
					// Nothing
				}
			});
			defaultMenu.showMenu(p);
		}
	}
}