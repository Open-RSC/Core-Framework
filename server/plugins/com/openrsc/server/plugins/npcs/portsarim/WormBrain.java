package com.openrsc.server.plugins.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class WormBrain implements OpBoundTrigger {

	@Override
	public boolean blockOpBound(GameObject obj, Integer click, Player player) {
		return player.getWorld().getServer().getConfig().WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665;
	}

	@Override
	public void onOpBound(GameObject obj, Integer click, final Player player) {
		if (player.getWorld().getServer().getConfig().WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665) {
			final Npc n = ifnearvisnpc(player, NpcId.WORMBRAIN.id(), 10);
			mes(player, "...you knock on the cell door");
			npcsay(player, n, "Whut you want?");
			Menu defaultMenu = new Menu();
			if (player.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))) {
				defaultMenu.addOption(new Option("I believe you've got a piece of a map that I need") {
					@Override
					public void action() {
						npcsay(player, n, "So? Why should I be giving it to you? What you do for Wormbrain?");
						new Menu().addOptions(
							new Option("I'm not going to do anything for you. Forget it") {
								public void action() {
									npcsay(player, n, "Be dat way then");
								}
							},
							new Option("I'll let you live. I could just kill you") {
								@Override
								public void action() {
									npcsay(player, n, "Ha! Me in here and you out dere. You not get map piece");
								}
							}, new Option("I suppose I could pay you for the map piece ...") {
								@Override
								public void action() {
									say(player, n, "Say, 500 coins?");
									npcsay(player, n, "Me not stooped, it worth at least 10,000 coins!");
									new Menu().addOptions(
										new Option("You must be joking! Forget it") {
											public void action() {
												npcsay(player, n, "Fine, you not get map piece");
											}
										}, new Option("Aright then, 10,000 it is") {
											@Override
											public void action() {
												if (ifheld(player, ItemId.COINS.id(), 10000)) {
													player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 10000));
													player.message("You buy the map piece from Wormbrain");
													npcsay(player, n, "Fank you very much! Now me can bribe da guards, hehehe");
													give(player, ItemId.MAP_PIECE_1.id(), 1);
												} else {
													say(player, n, "Oops, I don't have enough on me");
													npcsay(player, n, "Comes back when you has enough");
												}
											}
										}).showMenu(player);
								}
							}, new Option("Where did you get the map piece from?") {
								@Override
								public void action() {
									npcsay(player, n, "We rob house of stupid wizard. She very old, not put up much fight at all. Hahaha!");
									say(player, n, "Uh ... Hahaha");
									npcsay(player, n, "Her house full of pictures of a city on island and old pictures of people",
											"Me not recognise island",
											"Me find map piece",
											"Me not know what it is, but it in locked box so me figure it important",
											"But, by the time me get box open, other goblins gone",
											"Then me not run fast enough and guards catch me",
											"But now you want map piece so must be special! What do for me to get it?");
								}
							}).showMenu(player);
					}
				});
			}
			defaultMenu.addOption(new Option("What are you in for?") {
				@Override
				public void action() {
					npcsay(player, n, "Me not sure. Me pick some stuff up and take it away");
					say(player, n, "Well, did the stuff belong to you?");
					npcsay(player, n, "Umm...no");
					say(player, n, "Well, that would be why then");
					npcsay(player, n, "Oh, right");
				}
			});
			defaultMenu.addOption(new Option("Sorry, thought this was a zoo") {
				@Override
				public void action() {
					// Nothing
				}
			});
			defaultMenu.showMenu(player);
		}
	}
}
