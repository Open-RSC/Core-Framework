package com.openrsc.server.plugins.npcs.varrock;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.Functions;
import com.openrsc.server.plugins.triggers.UseNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.plugins.menu.Menu;
import com.openrsc.server.plugins.menu.Option;

import static com.openrsc.server.plugins.Functions.*;

public class Curator implements TalkNpcTrigger, UseNpcTrigger {
	public boolean blockTalkNpc(final Player player, final Npc npc) {
		return npc.getID() == NpcId.CURATOR.id();
	}

	@Override
	public void onTalkNpc(final Player p, final Npc n) {
		npcsay(p, n, "Welcome to the museum of Varrock");
		if (p.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_1.id()) && p.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			// curator authentically does not check if you already have a certificate in your inventory before triggering this
			if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5) {
				say(p, n,
					"I have retrieved the shield of Arrav and I would like to claim my reward");
				npcsay(p, n, "The shield of Arrav?", "Let me see that");
				Functions.mes(p, "The curator peers at the shield");
				npcsay(p,
					n,
					"This is incredible",
					"That shield has been missing for about twenty five years",
					"Well give me the shield",
					"And I'll write you out a certificate",
					"Saying you have returned the shield",
					"So you can claim your reward from the king");
				say(
					p,
					n,
					"Can I have two certificates?",
					"I needed significant help from a friend to get the shield",
					"We'll split the reward");
				npcsay(p, n, "Oh ok");
				Functions.mes(p, "You hand over the shield parts");
				p.getCarriedItems().remove(new Item(ItemId.BROKEN_SHIELD_ARRAV_1.id()));
				p.getCarriedItems().remove(new Item(ItemId.BROKEN_SHIELD_ARRAV_2.id()));
				Functions.mes(p, "The curator writes out two certificates");
				give(p, ItemId.CERTIFICATE.id(), 1);
				give(p, ItemId.CERTIFICATE.id(), 1);
				npcsay(p, n, "Take these to the king",
					"And he'll pay you both handsomely");

				return;
			}
		} else if (p.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_1.id()) || p.getCarriedItems().hasCatalogID(ItemId.BROKEN_SHIELD_ARRAV_2.id())) {
			if (p.getQuestStage(Quests.SHIELD_OF_ARRAV) == 5 || p.getQuestStage(Quests.SHIELD_OF_ARRAV) < 0) {
				// possible this triggers always, but confirmed that it does occur authentically after the quest is complete. (state < 0)
				say(p, n,
						"I have half the shield of Arrav here",
						"Can I get a reward");
				npcsay(p, n, "Well it might be worth a small reward",
						"The entire shield would me worth much much more");
				say(p, n,
						"Ok I'll hang onto it",
						"And see if I can find the other half");
				return;
			}
		}
		Menu defaultMenu = new Menu();
		defaultMenu.addOption(new Option("Have you any interesting news?") {
			@Override
			public void action() {
				npcsay(p, n, "No, I'm only interested in old stuff");
			}
		});
		defaultMenu.addOption(new Option(
			"Do you know where I could find any treasure?") {
			@Override
			public void action() {
				npcsay(p, n, "This museum is full of treasures");
				say(p, n, "No, I meant treasures for me");
				npcsay(p, n, "Any treasures this museum knows about",
					"It aquires");
			}
		});
		defaultMenu.showMenu(p);
	}

	@Override
	public boolean blockUseNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.CURATOR.id() && (item.getCatalogId() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()
			|| item.getCatalogId() == ItemId.LEVEL_1_CERTIFICATE.id()
			|| item.getCatalogId() == ItemId.LEVEL_2_CERTIFICATE.id()
			|| item.getCatalogId() == ItemId.LEVEL_3_CERTIFICATE.id())) {
			return true;
		}
		return false;
	}

	@Override
	public void onUseNpc(Player p, Npc n, Item item) {
		if (n.getID() == NpcId.CURATOR.id()) {
			if (item.getCatalogId() == ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()) {
				say(p, n, "I have been given this by the examiner at the digsite",
					"Can you stamp this for me ?");
				npcsay(p, n, "What have we here ?",
					"A letter of recommendation indeed",
					"Normally I wouldn't do this",
					"But in this instance I don't see why not",
					"There you go, good luck student...");
				p.getCarriedItems().remove(new Item(ItemId.UNSTAMPED_LETTER_OF_RECOMMENDATION.id()));
				give(p, ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id(), 1);
				npcsay(p, n, "Be sure to come back and show me your certificates",
					"I would like to see how you get on");
				say(p, n, "Okay, I will, thanks, see you later");
			} else if (item.getCatalogId() == ItemId.STAMPED_LETTER_OF_RECOMMENDATION.id()) {
				npcsay(p, n, "No, I don't want it back, thankyou");
			} else if (item.getCatalogId() == ItemId.LEVEL_1_CERTIFICATE.id()) {
				say(p, n, "Look what I have been awarded");
				p.getCarriedItems().remove(new Item(ItemId.LEVEL_1_CERTIFICATE.id()));
				npcsay(p, n, "Well that's great, well done",
					"I'll take that for safekeeping",
					"Come and tell me when you are the next level");
			} else if (item.getCatalogId() == ItemId.LEVEL_2_CERTIFICATE.id()) {
				say(p, n, "Look, I am level 2 now...");
				npcsay(p, n, "Excellent work!");
				p.getCarriedItems().remove(new Item(ItemId.LEVEL_2_CERTIFICATE.id()));
				npcsay(p, n, "I'll take that for safekeeping",
					"Remember to come and see me when you have graduated");
			} else if (item.getCatalogId() == ItemId.LEVEL_3_CERTIFICATE.id()) {
				say(p, n, "Look at this certificate, curator...");
				npcsay(p, n, "Well well, a level 3 graduate!",
					"I'll keep your certificate safe for you",
					"I feel I must reward you for your work...",
					"What would you prefer, something to eat or drink ?");
				int menu = multi(p, n,
					"Something to eat please",
					"Something to drink please");
				if (menu == 0) {
					p.getCarriedItems().remove(new Item(ItemId.LEVEL_3_CERTIFICATE.id()));
					npcsay(p, n, "Very good, come and eat this cake I baked");
					say(p, n, "Yum, thanks!");
					give(p, ItemId.CHOCOLATE_CAKE.id(), 1);
				} else if (menu == 1) {
					p.getCarriedItems().remove(new Item(ItemId.LEVEL_3_CERTIFICATE.id()));
					npcsay(p, n, "Certainly, have this...");
					give(p, ItemId.FRUIT_BLAST.id(), 1);
					say(p, n, "A cocktail ?");
					npcsay(p, n, "It's a new recipie from the gnome kingdom",
						"You'll like it I'm sure");
					say(p, n, "Cheers!");
					npcsay(p, n, "Cheers!");
				}
			}
		}
	}
}
