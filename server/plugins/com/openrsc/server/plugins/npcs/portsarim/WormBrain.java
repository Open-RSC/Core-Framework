package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.Constants.Quests;
import com.openrsc.server.external.ItemId;
import com.openrsc.server.external.NpcId;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.WallObjectActionListener;
import com.openrsc.server.plugins.listeners.executive.WallObjectActionExecutiveListener;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.Constants;

public final class WormBrain implements WallObjectActionListener, WallObjectActionExecutiveListener {

	@Override
	public boolean blockWallObjectAction(GameObject obj, Integer click, Player p) {
		return Constants.GameServer.WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665;
	}

	@Override
	public void onWallObjectAction(GameObject obj, Integer click, final Player p) {
		if (Constants.GameServer.WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665) {
			final Npc n = getNearestNpc(p, NpcId.WORMBRAIN.id(), 10);
			message(p, "...you knock on the cell door");
			npcTalk(p, n, "Whut you want?");
			Menu defaultMenu = new Menu();
			if (p.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !hasItem(p, ItemId.MAP_PIECE_1.id())) {
				defaultMenu.addOption(new Option("I believe you've got a piece of a map that I need") {
					@Override
					public void action() {
						npcTalk(p, n, "So? Why should I be giving it to you? What you do for Wormbrain?");
						new Menu().addOptions(
							new Option("I'm not going to do anything for you. Forget it") {
								public void action() {
									npcTalk(p, n, "Be dat way then");
								}
							},
							new Option("I'll let you live. I could just kill you") {
								@Override
								public void action() {
									npcTalk(p, n, "Ha! Me in here and you out dere. You not get map piece");
								}
							}, new Option("I suppose I could pay you for the map piece ...") {
								@Override
								public void action() {
									playerTalk(p, n, "Say, 500 coins?");
									npcTalk(p, n, "Me not stooped, it worth at least 10,000 coins!");
									new Menu().addOptions(
										new Option("You must be joking! Forget it") {
											public void action() {
												npcTalk(p, n, "Fine, you not get map piece");
											}
										}, new Option("Aright then, 10,000 it is") {
											@Override
											public void action() {
												if (hasItem(p, ItemId.COINS.id(), 10000)) {
													removeItem(p, ItemId.COINS.id(), 10000);
													p.message("You buy the map piece from Wormbrain");
													npcTalk(p, n, "Fank you very much! Now me can bribe da guards, hehehe");
													addItem(p, ItemId.MAP_PIECE_1.id(), 1);
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
									npcTalk(p, n, "We rob house of stupid wizard. She very old, not put up much fight at all. Hahaha!");
									playerTalk(p, n, "Uh ... Hahaha");
									npcTalk(p, n, "Her house full of pictures of a city on island and old pictures of people",
											"Me not recognise island",
											"Me find map piece",
											"Me not know what it is, but it in locked box so me figure it important",
											"But, by the time me get box open, other goblins gone",
											"Then me not run fast enough and guards catch me",
											"But now you want map piece so must be special! What do for me to get it?");
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
