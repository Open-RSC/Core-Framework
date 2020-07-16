package com.openrsc.server.plugins.authentic.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;

import static com.openrsc.server.plugins.Functions.*;

public class Curator implements TalkNpcTrigger, UseNpcTrigger {
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.CURATOR.id();
	}

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		npcsay(player, n, "Welcome to the museum of Varrock");
		if (player.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_1.id()) && player.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			// curator authentically does not check if you already have a certificate in your inventory before triggering this
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) {
				say(player, n,
					"I have retrieved the shield of Arrav and I would like to claim my reward");
				npcsay(player, n, "The shield of Arrav?", "Let me see that");
				mes("The curator peers at the shield");
				delay(3);
				npcsay(player,
					n,
					"This is incredible",
					"That shield has been missing for about twenty five years",
					"Well give me the shield",
					"And I'll write you out a certificate",
					"Saying you have returned the shield",
					"So you can claim your reward from the king");
				say(
					player,
					n,
					"Can I have two certificates?",
					"I needed significant help from a friend to get the shield",
					"We'll split the reward");
				npcsay(player, n, "Oh ok");
				mes("You hand over the shield parts");
				delay(3);
				player.getCarriedItems().remove(new Item(ItemId.BROKEN_SHIELD_ARRAV_1.id()));
				player.getCarriedItems().remove(new Item(ItemId.BROKEN_SHIELD_ARRAV_2.id()));
				mes("The curator writes out two certificates");
				delay(3);
				give(player, ItemId.CERTIFICATE.id(), 1);
				give(player, ItemId.CERTIFICATE.id(), 1);
				npcsay(player, n, "Take these to the king",
					"And he'll pay you both handsomely");

				return;
			}
		} else if (player.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_1.id()) || player.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			if (player.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5 || player.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0) {
				// possible this triggers always, but confirmed that it does occur authentically after the quest is complete. (state < 0)
				say(player, n,
						"I have half the shield of Arrav here",
						"Can I get a reward");
				npcsay(player, n, "Well it might be worth a small reward",
						"The entire shield would me worth much much more");
				say(player, n,
						"Ok I'll hang onto it",
						"And see if I can find the other half");
				return;
			}
		}

		int option = multi(player, n, "Have you any interesting news?",
			"Do you know where I could find any treasure?");

		if (option == 0) {
			npcsay(player, n, "No, I'm only interested in old stuff");
		}

		else if (option == 1) {
			npcsay(player, n, "This museum is full of treasures");
			say(player, n, "No, I meant treasures for me");
			npcsay(player, n, "Any treasures this museum knows about",
				"It aquires");
		}
	}

	@Override
	public boolean blockUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.CURATOR.id() && (item.getCatalogId() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()
			|| item.getCatalogId() == ItemId.LEVEL_1_CERTIFICATE.id()
			|| item.getCatalogId() == ItemId.LEVEL_2_CERTIFICATE.id()
			|| item.getCatalogId() == ItemId.LEVEL_3_CERTIFICATE.id())) {
			return true;
		}
		return false;
	}

	@Override
	public void onUseNpc(Player player, Npc n, Item item) {
		if (n.getID() == NpcId.CURATOR.id()) {
			if (item.getCatalogId() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()) {
				say(player, n, "I have been given this by the examiner at the digsite",
					"Can you stamp this for me ?");
				npcsay(player, n, "What have we here ?",
					"A letter of recommendation indeed",
					"Normally I wouldn't do this",
					"But in this instance I don't see why not",
					"There you go, good luck student...");
				player.getCarriedItems().remove(new Item(ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()));
				give(player, ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
				npcsay(player, n, "Be sure to come back and show me your certificates",
					"I would like to see how you get on");
				say(player, n, "Okay, I will, thanks, see you later");
			} else if (item.getCatalogId() == ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id()) {
				npcsay(player, n, "No, I don't want it back, thankyou");
			} else if (item.getCatalogId() == ItemId.LEVEL_1_CERTIFICATE.id()) {
				say(player, n, "Look what I have been awarded");
				player.getCarriedItems().remove(new Item(ItemId.LEVEL_1_CERTIFICATE.id()));
				npcsay(player, n, "Well that's great, well done",
					"I'll take that for safekeeping",
					"Come and tell me when you are the next level");
			} else if (item.getCatalogId() == ItemId.LEVEL_2_CERTIFICATE.id()) {
				say(player, n, "Look, I am level 2 now...");
				npcsay(player, n, "Excellent work!");
				player.getCarriedItems().remove(new Item(ItemId.LEVEL_2_CERTIFICATE.id()));
				npcsay(player, n, "I'll take that for safekeeping",
					"Remember to come and see me when you have graduated");
			} else if (item.getCatalogId() == ItemId.LEVEL_3_CERTIFICATE.id()) {
				say(player, n, "Look at this certificate, curator...");
				npcsay(player, n, "Well well, a level 3 graduate!",
					"I'll keep your certificate safe for you",
					"I feel I must reward you for your work...",
					"What would you prefer, something to eat or drink ?");
				int menu = multi(player, n,
					"Something to eat please",
					"Something to drink please");
				if (menu == 0) {
					player.getCarriedItems().remove(new Item(ItemId.LEVEL_3_CERTIFICATE.id()));
					npcsay(player, n, "Very good, come and eat this cake I baked");
					say(player, n, "Yum, thanks!");
					give(player, ItemId.CHOCOLATE_CAKE.id(), 1);
				} else if (menu == 1) {
					player.getCarriedItems().remove(new Item(ItemId.LEVEL_3_CERTIFICATE.id()));
					npcsay(player, n, "Certainly, have this...");
					give(player, ItemId.FRUIT_BLAST.id(), 1);
					say(player, n, "A cocktail ?");
					npcsay(player, n, "It's a new recipie from the gnome kingdom",
						"You'll like it I'm sure");
					say(player, n, "Cheers!");
					npcsay(player, n, "Cheers!");
				}
			}
		}
	}
}
