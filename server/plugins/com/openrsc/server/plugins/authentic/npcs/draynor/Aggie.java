package com.openrsc.server.plugins.authentic.npcs.draynor;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public final class Aggie implements TalkNpcTrigger {

	private static final int SKIN_PASTE = 0;
	private static final int FROGS = 1;
	private static final int MADWITCH = 2;

	private static final int RED_DYE = 4;
	private static final int DONT_HAVE = 5;
	private static final int WITHOUT_DYE = 6;
	private static final int YELLOW_DYE = 7;
	private static final int BLUE_DYE = 8;
	private static final int DYES = 9;
	private static final int MAKEME = 10;
	private static final int HAPPY = 11;

	@Override
	public void onTalkNpc(Player player, final Npc npc) {
		aggieDialogue(player, npc, -1);
	}

	public void aggieDialogue(Player player, Npc n, int cID) {
		if (cID == -1) {
			npcsay(player, n, "What can I help you with?");
			if (player.getQuestStage(Quests.PRINCE_ALI_RESCUE) == 2) {
				int choice = multi(player, n,
					"Could you think of a way to make pink skin paste",
					"What could you make for me",
					"Cool, do you turn people into frogs?",
					"You mad old witch, you can't help me",
					"Can you make dyes for me please");
				if (choice == 0) {
					aggieDialogue(player, n, Aggie.SKIN_PASTE);
				} else if (choice == 1) {
					aggieDialogue(player, n, Aggie.MAKEME);
				} else if (choice == 2) {
					aggieDialogue(player, n, Aggie.FROGS);
				} else if (choice == 3) {
					aggieDialogue(player, n, Aggie.MADWITCH);
				} else if (choice == 4) {
					aggieDialogue(player, n, Aggie.DYES);
				}
			} else {
				int choiceOther = multi(player, n,
					"What could you make for me",
					"Cool, do you turn people into frogs?",
					"You mad old witch, you can't help me",
					"Can you make dyes for me please");
				if (choiceOther == 0) {
					aggieDialogue(player, n, Aggie.MAKEME);
				} else if (choiceOther == 1) {
					aggieDialogue(player, n, Aggie.FROGS);
				} else if (choiceOther == 2) {
					aggieDialogue(player, n, Aggie.MADWITCH);
				} else if (choiceOther == 3) {
					aggieDialogue(player, n, Aggie.DYES);
				}
			}

			return;
		}
		switch (cID) {
			case Aggie.DYES:
				npcsay(player, n,
					"What sort of dye would you like? Red, yellow or Blue?");
				int menu13 = multi(player, n,
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am");
				if (menu13 == 0) {
					aggieDialogue(player, n, Aggie.RED_DYE);
				} else if (menu13 == 1) {
					aggieDialogue(player, n, Aggie.YELLOW_DYE);
				} else if (menu13 == 2) {
					aggieDialogue(player, n, Aggie.BLUE_DYE);
				} else if (menu13 == 3) {
					aggieDialogue(player, n, Aggie.HAPPY);
				}
				break;
			case Aggie.SKIN_PASTE:
				if (player.getCarriedItems().hasCatalogID(ItemId.ASHES.id(), Optional.of(false))
					&& (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.FLOUR.id(), Optional.of(false)))
					&& (player.getCarriedItems().hasCatalogID(ItemId.BUCKET_OF_WATER.id(), Optional.of(false))
					|| player.getCarriedItems().hasCatalogID(ItemId.JUG_OF_WATER.id(), Optional.of(false)))
					&& player.getCarriedItems().hasCatalogID(ItemId.REDBERRIES.id(), Optional.of(false))) {
					npcsay(player, n,
						"Yes I can, you have the ingredients for it already");
					npcsay(player, n, "Would you like me to mix you some?");
					int menu = multi(player, n, false, //do not send over
						"Yes please, mix me some skin paste",
						"No thankyou, I don't need paste");
					if (menu == 0) {
						say(player, n, "Yes please, mix me some skin paste");
						npcsay(player, n,
							"That should be simple, hand the things to Aggie then");
						mes("You hand ash, flour, water and redberries to Aggie");
						delay(3);
						mes("She tips it into a cauldron and mutters some words");
						delay(3);
						player.getCarriedItems().remove(new Item(ItemId.ASHES.id()));
						if (player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id())) == -1) {
							player.getCarriedItems().remove(new Item(ItemId.FLOUR.id()));
						}
						if (player.getCarriedItems().remove(new Item(ItemId.BUCKET_OF_WATER.id())) == -1) {
							player.getCarriedItems().remove(new Item(ItemId.JUG_OF_WATER.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.REDBERRIES.id()));
						npcsay(player, n,
							"Tourniquet, Fenderbaum, Tottenham, MonsterMunch, MarbleArch");
						mes("Aggie hands you the skin paste");
						delay(3);
						give(player, ItemId.PASTE.id(), 1);
						npcsay(player, n, "There you go dearie, your skin potion",
							"That will make you look good at the Varrock dances");
					} else if (menu == 1) {
						say(player, n, "No thank you, I don't need skin paste");
						npcsay(player, n, "Okay dearie, thats always your choice");
					}
				} else {
					npcsay(player,
						n,
						"Why, its one of my most popular potions",
						"The women here, they like to have smooth looking skin",
						"(and I must admit, some of the men buy it too)",
						"I can make it for you, just get me whats needed");
					say(player, n, "What do you need to make it?");
					npcsay(player, n, "Well deary, you need a base for the paste",
						"That's a mix of ash, flour and water",
						"Then you need red berries to colour it as you want",
						"bring me those four items and I will make you some");
				}
				break;
			case Aggie.FROGS:
				npcsay(player,
					n,
					"Oh, not for years, but if you meet a talking chicken,",
					"You have probably met the professor in the Manor north of here",
					"A few years ago it was flying fish, that machine is a menace");
				break;
			case Aggie.MADWITCH:
				npcsay(player, n, "Oh, you like to call a witch names, do you?");
				if (ifheld(player, ItemId.COINS.id(), 20)) {
					mes("Aggie waves her hands about, and you seem to be 20 coins poorer");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 20));
					npcsay(player, n,
						"Thats a fine for insulting a witch, you should learn some respect");
				} else if (player.getCarriedItems().hasCatalogID(ItemId.POT_OF_FLOUR.id(), Optional.of(false))) {
					mes("Aggie waves her hands near you, and you seem to have lost some flour");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.POT_OF_FLOUR.id()));
					npcsay(player, n, "Thankyou for your kind present of flour",
						"I am sure you never meant to insult me");
				} else {
					npcsay(player, n,
						"You should be careful about insulting a Witch",
						"You never know what shape you could wake up in");
				}
				break;
			case Aggie.MAKEME:
				npcsay(player,
					n,
					"I mostly just make what I find pretty",
					"I sometimes make dye for the womens clothes, brighten the place up",
					"I can make red,yellow and blue dyes would u like some");
				int menu2 = multi(player, n,
					"What do you need to make some red dye please",
					"What do you need to make some yellow dye please",
					"What do you need to make some blue dye please",
					"No thanks, I am happy the colour I am");
				if (menu2 == 0) {
					aggieDialogue(player, n, Aggie.RED_DYE);
				} else if (menu2 == 1) {
					aggieDialogue(player, n, Aggie.YELLOW_DYE);
				} else if (menu2 == 2) {
					aggieDialogue(player, n, Aggie.BLUE_DYE);
				} else if (menu2 == 3) {
					aggieDialogue(player, n, Aggie.HAPPY);
				}
				break;
			case Aggie.YELLOW_DYE:
				npcsay(player,
					n,
					"Yellow is a strange colour to get, comes from onion skins",
					"I need 2 onions, and 5 coins to make yellow");
				int menu4 = multi(player, n, false, //do not send over
					"Okay, make me some yellow dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu4 == 0) {
					if (!ifheld(player, ItemId.ONION.id(), 2)) {
						mes("You don't have enough onions to make the yellow dye!");
						delay(3);
					} else if (!ifheld(player, ItemId.COINS.id(), 5)) {
						mes("You don't have enough coins to pay for the dye!");
						delay(3);
					} else {
						say(player, n, "Okay, make me some yellow dye please");
						mes("You hand the onions and payment to Aggie");
						delay(3);
						for (int i = 0; i < 2; i++) {
							player.getCarriedItems().remove(new Item(ItemId.ONION.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						mes("she takes a yellow bottle from nowhere and hands it to you");
						delay(3);
						give(player, ItemId.YELLOWDYE.id(), 1);
					}
				} else if (menu4 == 1) {
					say(player, n, "I don't think I have all the ingredients yet");
					aggieDialogue(player, n, Aggie.DONT_HAVE);
				} else if (menu4 == 2) {
					say(player, n, "I can do without dye at that price");
					aggieDialogue(player, n, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.RED_DYE:
				npcsay(player, n, "3 lots of Red berries, and 5 coins, to you");
				int menu3 = multi(player, n, false, //do not send over
					"Okay, make me some red dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu3 == 0) {
					if (!ifheld(player, ItemId.REDBERRIES.id(), 3)) {
						mes("You don't have enough berries to make the red dye!");
						delay(3);
					} else if (!ifheld(player, ItemId.COINS.id(), 5)) {
						mes("You don't have enough coins to pay for the dye!");
						delay(3);
					} else {
						say(player, n, "Okay, make me some red dye please");
						mes("You hand the berries and payment to Aggie");
						delay(3);
						for (int i = 0; i < 3; i++) {
							player.getCarriedItems().remove(new Item(ItemId.REDBERRIES.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						mes("she takes a red bottle from nowhere and hands it to you");
						delay(3);
						give(player, ItemId.REDDYE.id(), 1);
					}
				} else if (menu3 == 1) {
					say(player, n, "I don't think I have all the ingredients yet");
					aggieDialogue(player, n, Aggie.DONT_HAVE);
				} else if (menu3 == 2) {
					say(player, n, "I can do without dye at that price");
					aggieDialogue(player, n, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.BLUE_DYE:
				npcsay(player, n, "2 woad leaves, and 5 coins, to you");
				int menu6 = multi(player, n, false, //do not send over
					"Okay, make me some blue dye please",
					"I don't think I have all the ingredients yet",
					"I can do without dye at that price");
				if (menu6 == 0) {
					if (!ifheld(player, ItemId.WOAD_LEAF.id(), 2)) {
						mes("You don't have enough woad leaves to make the blue dye!");
						delay(3);
					} else if (!ifheld(player, ItemId.COINS.id(), 5)) {
						mes("You don't have enough coins to pay for the dye!");
						delay(3);
					} else {
						say(player, n, "Okay, make me some blue dye please");
						mes("You hand the woad leaves and payment to Aggie");
						delay(3);
						for (int i = 0; i < 2; i++) {
							player.getCarriedItems().remove(new Item(ItemId.WOAD_LEAF.id()));
						}
						player.getCarriedItems().remove(new Item(ItemId.COINS.id(), 5));
						mes("she takes a blue bottle from nowhere and hands it to you");
						delay(3);
						give(player, ItemId.BLUEDYE.id(), 1);
					}
				} else if (menu6 == 1) {
					say(player, n, "I don't think I have all the ingredients yet");
					say(player, n, "Where on earth am I meant to find woad leaves?");
					npcsay(player, n, "I'm not entirely sure",
						"I used to go and nab the stuff from the public gardens in Falador",
						"It hasn't been growing there recently though");
				} else if (menu6 == 2) {
					say(player, n, "I can do without dye at that price");
					aggieDialogue(player, n, Aggie.WITHOUT_DYE);
				}
				break;
			case Aggie.DONT_HAVE:
				npcsay(player,
					n,
					"You know what you need to get now, come back when you have them",
					"goodbye for now");
				break;
			case Aggie.WITHOUT_DYE:
				npcsay(player,
					n,
					"Thats your choice, but I would think you have killed for less",
					"I can see it in your eyes");
				break;
			case Aggie.HAPPY:
				npcsay(player, n, "You are easily pleased with yourself then",
					"when you need dyes, come to me");
				break;
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.AGGIE.id();
	}
}
