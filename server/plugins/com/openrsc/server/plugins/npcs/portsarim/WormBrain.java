package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class WormBrain implements OpBoundTrigger {

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player p) {
		return p.getWorld().getServer().getConfig().WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, final Player p) {
		if (p.getWorld().getServer().getConfig().WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665) {
			final Npc n = ifnearvisnpc(p, NpcId.WORMBRAIN.id(), 10);
			Functions.mes(p, "...you knock on the cell door");
			npcsay(p, n, "Whut you want?");
			Menu defaultMenu = new Menu();
			if (p.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !p.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))) {
				defaultMenu.addOption(new Option("I believe you've got a piece of a map that I need") {
					@Override
					public void action() {
						npcsay(p, n, "So? Why should I be giving it to you? What you do for Wormbrain?");
						new Menu().addOptions(
							new Option("I'm not going to do anything for you. Forget it") {
								public void action() {
									npcsay(p, n, "Be dat way then");
								}
							},
							new Option("I'll let you live. I could just kill you") {
								@Override
								public void action() {
									npcsay(p, n, "Ha! Me in here and you out dere. You not get map piece");
								}
							}, new Option("I suppose I could pay you for the map piece ...") {
								@Override
								public void action() {
									say(p, n, "Say, 500 coins?");
									npcsay(p, n, "Me not stooped, it worth at least 10,000 coins!");
									new Menu().addOptions(
										new Option("You must be joking! Forget it") {
											public void action() {
												npcsay(p, n, "Fine, you not get map piece");
											}
										}, new Option("Aright then, 10,000 it is") {
											@Override
											public void action() {
												if (ifheld(p, ItemId.COINS.id(), 10000)) {
													remove(p, ItemId.COINS.id(), 10000);
													p.message("You buy the map piece from Wormbrain");
													npcsay(p, n, "Fank you very much! Now me can bribe da guards, hehehe");
													give(p, ItemId.MAP_PIECE_1.id(), 1);
												} else {
													say(p, n, "Oops, I don't have enough on me");
													npcsay(p, n, "Comes back when you has enough");
												}
											}
										}).showMenu(p);
								}
							}, new Option("Where did you get the map piece from?") {
								@Override
								public void action() {
									npcsay(p, n, "We rob house of stupid wizard. She very old, not put up much fight at all. Hahaha!");
									say(p, n, "Uh ... Hahaha");
									npcsay(p, n, "Her house full of pictures of a city on island and old pictures of people",
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
					npcsay(p, n, "Me not sure. Me pick some stuff up and take it away");
					say(p, n, "Well, did the stuff belong to you?");
					npcsay(p, n, "Umm...no");
					say(p, n, "Well, that would be why then");
					npcsay(p, n, "Oh, right");
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
