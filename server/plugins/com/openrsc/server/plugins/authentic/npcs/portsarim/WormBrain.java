package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.OpBoundTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class WormBrain implements OpBoundTrigger {

	@Override
	public boolean blockOpBound(Player player, GameObject obj, Integer click) {
		return player.getConfig().WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665;
	}

	@Override
	public void onOpBound(final Player player, GameObject obj, Integer click) {
		if (config().WANT_BARTER_WORMBRAINS && obj.getID() == 30
				&& obj.getX() == 283 && obj.getY() == 665) {
			final Npc n = ifnearvisnpc(player, NpcId.WORMBRAIN.id(), 10);
			if (n != null) {
				mes("...you knock on the cell door");
				delay(3);
				npcsay(player, n, "Whut you want?");

				ArrayList<String> options = new ArrayList<>();
				if (player.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))) {
					options.add("I believe you've got a piece of a map that I need");
				}
				options.add("What are you in for?");
				options.add("Sorry, thought this was a zoo");

				String[] finalOptions = new String[options.size()];
				int option = multi(player, n, options.toArray(finalOptions));

				if (option == 0) {
					if (player.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))) {
						dragonSlayerConversation(player, n);
					}
					else {
						defaultConversation(player, n);
					}
				}

				else if (option == 1) {
					if (player.getQuestStage(Quests.DRAGON_SLAYER) >= 2 && !player.getCarriedItems().hasCatalogID(ItemId.MAP_PIECE_1.id(), Optional.of(false))) {
						defaultConversation(player, n);
					}
				}
			}
		}
	}

	private void defaultConversation(Player player, Npc npc) {
		npcsay(player, npc, "Me not sure. Me pick some stuff up and take it away");
		say(player, npc, "Well, did the stuff belong to you?");
		npcsay(player, npc, "Umm...no");
		say(player, npc, "Well, that would be why then");
		npcsay(player, npc, "Oh, right");
	}

	private void dragonSlayerConversation(Player player, Npc npc) {
		npcsay(player, npc, "So? Why should I be giving it to you? What you do for Wormbrain?");
		int option = multi(player, npc,
			"I'm not going to do anything for you. Forget it",
			"I'll let you live. I could just kill you",
			"I suppose I could pay you for the map piece ...",
			"Where did you get the map piece from?"
		);

		if (option ==  0) {
			npcsay(player, npc, "Be dat way then");
		}
		else if (option == 1) {
			npcsay(player, npc, "Ha! Me in here and you out dere. You not get map piece");
		}
		else if (option == 2) {
			say(player, npc, "Say, 500 coins?");
			npcsay(player, npc, "Me not stooped, it worth at least 10,000 coins!");
			dragonSlayerPaymentOption(player, npc);
		}
		else if (option == 3) {
			npcsay(player, npc, "We rob house of stupid wizard. She very old, not put up much fight at all. Hahaha!");
			say(player, npc, "Uh ... Hahaha");
			npcsay(player, npc, "Her house full of pictures of a city on island and old pictures of people",
				"Me not recognise island",
				"Me find map piece",
				"Me not know what it is, but it in locked box so me figure it important",
				"But, by the time me get box open, other goblins gone",
				"Then me not run fast enough and guards catch me",
				"But now you want map piece so must be special! What do for me to get it?"
			);
		}
	}

	private void dragonSlayerPaymentOption(Player player, Npc npc) {
		int option = multi(player, npc,
			"You must be joking! Forget it",
			"Aright then, 10,000 it is"
		);
		if (option == 0) {
			npcsay(player, npc, "Fine, you not get map piece");
		}
		else if (option == 1) {
			if (ifheld(player, ItemId.COINS.id(), 10000)) {
				if (player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 10000)) == -1) return;
				player.message("You buy the map piece from Wormbrain");
				npcsay(player, npc, "Fank you very much! Now me can bribe da guards, hehehe");
				give(player, ItemId.MAP_PIECE_1.id(), 1);
			} else {
				say(player, npc, "Oops, I don't have enough on me");
				npcsay(player, npc, "Comes back when you has enough");
			}
		}
	}
}
