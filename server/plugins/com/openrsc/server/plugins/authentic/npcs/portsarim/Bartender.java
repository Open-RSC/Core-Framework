package com.openrsc.server.plugins.authentic.npcs.portsarim;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.ArrayList;
import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class Bartender implements TalkNpcTrigger {

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.BARTENDER_PORTSARIM.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		ArrayList<String> options = new ArrayList<>();
		options.add("Could i buy a beer please?");
		if (player.getQuestStage(Quests.GOBLIN_DIPLOMACY) == 0) {
			options.add("Not very busy in here today is it?");
		} else {
			options.add("Have you heard any more rumours in here?");
		}
		if (player.getCache().hasKey("barcrawl") && !player.getCache().hasKey("barsix")
			&& player.getCarriedItems().hasCatalogID(ItemId.BARCRAWL_CARD.id(), Optional.of(false))) {
			options.add("I'm doing Alfred Grimhand's barcrawl");
		}
		String[] finalOptions = new String[options.size()];
		int option = multi(player, n, options.toArray(finalOptions));

		if (option == 0) {
			npcsay(player, n, "Sure that will be 2 gold coins please");
			if (ifheld(player, ItemId.COINS.id(), 2)) {
				say(player, n, "Ok here you go thanks");
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 2));
				player.message("you buy a pint of beer");
				give(player, ItemId.BEER.id(), 1);
			} else {
				player.message("You dont have enough coins for the beer");
			}
		} else if (option == 1) {
			if (player.getQuestStage(Quests.GOBLIN_DIPLOMACY) == 0) {
				npcsay(player,
					n,
					"No it was earlier",
					"There was a guy in here saying the goblins up by the mountain are arguing again",
					"Of all things about the colour of their armour.",
					"Knowing the goblins, it could easily turn into a full blown war",
					"Which wouldn't be good",
					"Goblin wars make such a mess of the countryside");
				say(player, n,
					"Well if I have time I'll see if I can go and knock some sense into them");
				player.updateQuestStage(Quests.GOBLIN_DIPLOMACY, 1);
			} else {
				npcsay(player, n, "No it hasn't been very busy lately");
			}
		} else if (option == 2) {
			npcsay(player, n, "Are you sure you look a bit skinny for that");
			say(player, n,
				"Just give me whatever drink I need to drink here");
			npcsay(player, n,
				"Ok one black skull ale coming up, 8 coins please");
			if (ifheld(player, ItemId.COINS.id(), 8)) {
				player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 8));
				mes("You buy a black skull ale");
				delay(3);
				mes("You drink your black skull ale");
				delay(3);
				mes("Your vision blurs");
				delay(3);
				mes("The bartender signs your card");
				delay(3);
				player.getCache().store("barsix", true);
				say(player, n, "hiccup", "hiccup");
			} else {
				say(player, n, "I don't have 8 coins with me");
			}
		}
	}
}
