package com.openrsc.server.plugins.authentic.npcs.catherby;

import com.openrsc.server.constants.Quests;
import com.openrsc.server.external.Gauntlets;
import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;
import static com.openrsc.server.plugins.authentic.quests.members.FamilyCrest.getGauntletEnchantment;

public class Chef implements TalkNpcTrigger {

	@Override
	public void onTalkNpc(final Player player, final Npc n) {
		switch (player.getQuestStage(Quests.FAMILY_CREST)) {
			case -1:
				if (player.getCache().hasKey("famcrest_gauntlets")
					&& player.getCache().getInt("famcrest_gauntlets") != Gauntlets.STEEL.id()) {
					npcsay(player, n, "I hear you have bought the completed crest to my father",
						"Impressive work I must say");
					return;
				}
				npcsay(player, n, "I hear you have brought the completed crest to my father",
					"Impressive work I must say");
				if (player.getCarriedItems().hasCatalogID(ItemId.STEEL_GAUNTLETS.id(), Optional.of(false)) && getGauntletEnchantment(player) == Gauntlets.STEEL.id()) {
					say(player, n, "My Father says you can improve these gauntlets for me");
					npcsay(player, n, "Yes that is true",
						"I can change them to gauntlets of cooking",
						"Wearing them means you will burn your lobsters, swordish and shark less");
					int menu = multi(player, n,
						"Yes please do that for me",
						"I'll see what your brothers have to offer first");
					if (menu == 0) {
						mes("Caleb holds the gauntlets and closes his eyes");
						delay(3);
						mes("Caleb concentrates");
						delay(3);
						mes("Caleb hands the gauntlets to you");
						delay(3);
						Item itemToRemove = player.getCarriedItems().getEquipment().get(
							player.getCarriedItems().getEquipment().searchEquipmentForItem(
								ItemId.STEEL_GAUNTLETS.id()));
						if (itemToRemove == null) {
							itemToRemove = player.getCarriedItems().getInventory().get(
								player.getCarriedItems().getInventory().getLastIndexById(
									ItemId.STEEL_GAUNTLETS.id(), Optional.of(false)));
						}
						if (itemToRemove == null) return;
						player.getCarriedItems().remove(itemToRemove);
						player.getCarriedItems().getInventory().add(new Item(ItemId.GAUNTLETS_OF_COOKING.id()));
						player.getCache().set("famcrest_gauntlets", Gauntlets.COOKING.id());
					} else if (menu == 1) {
						npcsay(player, n, "Ok suit yourself");
					}
				}
				return;
			case 0:
			case 1:
				npcsay(player, n, "Who are you? What are you after?");
				String[] menuOps = new String[]{
					"Are you Caleb Fitzharmon?",
					"Nothing, I will be on my way", "I see you are a chef, will you cook me anything?"
				};
				if (player.getQuestStage(Quests.FAMILY_CREST) == 0) {
					menuOps = new String[]{
						"Nothing, I will be on my way", "I see you are a chef, will you cook me anything?"
					};
					int choice = multi(player, n, false, menuOps);
					if (choice >= 0) {
						initialDialogue(player, n, choice + 1);
					}
				} else {
					int choice = multi(player, n, false, menuOps);
					initialDialogue(player, n, choice);
				}
				break;
			case 2:
				npcsay(player, n, "How is the fish collecting going?");
				if (!player.getCarriedItems().hasCatalogID(ItemId.SWORDFISH.id(), Optional.of(false))
					|| !player.getCarriedItems().hasCatalogID(ItemId.BASS.id(), Optional.of(false))
					|| !player.getCarriedItems().hasCatalogID(ItemId.TUNA.id(), Optional.of(false))
					|| !player.getCarriedItems().hasCatalogID(ItemId.SALMON.id(), Optional.of(false))
					|| !player.getCarriedItems().hasCatalogID(ItemId.SHRIMP.id(), Optional.of(false))) {
					say(player, n, "I haven't got all the fish yet");
					npcsay(player, n, "Remember I want cooked swordfish, bass, tuna, salmon and shrimp");
				} else {
					say(player, n, "Yes i have all of that now");
					mes("You give all of the fish to Caleb");
					delay(3);
					player.getCarriedItems().remove(new Item(ItemId.SWORDFISH.id()));
					player.getCarriedItems().remove(new Item(ItemId.BASS.id()));
					player.getCarriedItems().remove(new Item(ItemId.TUNA.id()));
					player.getCarriedItems().remove(new Item(ItemId.SALMON.id()));
					player.getCarriedItems().remove(new Item(ItemId.SHRIMP.id()));
					player.message("Caleb gives you his piece of the crest");
					give(player, ItemId.CREST_FRAGMENT_ONE.id(), 1);
					player.getCache().store("skipped_menu", true);
					player.updateQuestStage(Quests.FAMILY_CREST, 3);
					int m = multi(player, n,
						"Err what happened to the rest of it?",
						"Thankyou very much");
					if (m == 0) {
						npcsay(player, n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
						say(player, n, "So do you know where I could find any of your brothers?");
						npcsay(player, n, "Well we haven't really kept in touch",
							"What with all falling out over the crest",
							"I did hear from my brother Avan about a year ago though",
							"He said he was a living in a town in the desert",
							"Ask around the desert and you may find him",
							"My brother has very expensive tastes",
							"He may not give up the crest easily");
						player.getCache().remove("skipped_menu");
					}
				}
				return;
			case 3:
				if (player.getCache().hasKey("skipped_menu")) {
					npcsay(player, n, "Hello again, I'm just putting the finishing touches to my salad");
					int menu = multi(player, n, false, //do not send over
						"Err what happened to the rest of the crest?", "Good luck with that then");
					if (menu == 0) {
						say(player, n, "Err what happened to the rest of the crest?");
						npcsay(player, n, "Well we had a bit of a fight over it",
							"We all wanted to be the heir of our fathers lands",
							"we each ended up with a piece of the crest",
							"none of us wanted to give their piece of the crest up to any of the others",
							"And none of us wanted to face our father",
							"coming home without a complete crest");
						say(player, n, "So do you know where I could find any of your brothers?");
						npcsay(player, n, "Well we haven't really kept in touch",
							"What with all falling out over the crest",
							"I did hear from my brother Avan about a year ago though",
							"He said he was a living in a town in the desert",
							"Ask around the desert and you may find him",
							"My brother has very expensive tastes",
							"He may not give up the crest easily");
						player.getCache().remove("skipped_menu");
					} else if (menu == 1) {
						say(player, n, "Good look with that then");
					}
					return;
				}
				say(player, n, "Where did you say I could find Avan?");
				npcsay(player, n, "He said he was a living in a town in the desert",
					"Ask around the desert and you may find him");
				return;
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				npcsay(player, n, "How are you doing getting the rest of the crest?");
				if (player.getCarriedItems().hasCatalogID(ItemId.FAMILY_CREST.id(), Optional.of(false))) {
					say(player, n, "I have found it");
					npcsay(player, n, "Well done, take it to my father");
				} else if (!player.getCarriedItems().hasCatalogID(ItemId.CREST_FRAGMENT_ONE.id(), Optional.of(false))) {
					int menu = multi(player, n,
						"I am still working on it",
						"I have lost the piece you gave me");
					if (menu == 0) {
						npcsay(player, n, "Well good luck in your quest");
					} else if (menu == 1) {
						npcsay(player, n, "Ah well here is another one");
						give(player, ItemId.CREST_FRAGMENT_ONE.id(), 1);
					}
				} else {
					say(player, n, "I am still working on it");
					npcsay(player, n, "Well good luck in your quest");
				}
				return;
		}
	}

	public void initialDialogue(final Player player, final Npc n, int option) {
		if (option == 0) {
			say(player, n, "Are you Caleb Fitzharmon?");
			npcsay(player, n, "I am he, and who might you be?");
			say(player, n, "I have been sent by your father",
				"He wants me to retrieve the Fitzharmon family crest");
			npcsay(player, n, "Ah, yes hmm well I do have a bit of it yes");
			option = multi(player, n,
				"Err what happened to the rest of crest?",
				"So can I have your bit?"
			);

			if (option == 0) {
				npcsay(player, n, "Well we had a bit of a fight over it",
					"We all wanted to be the heir of our fathers lands",
					"we each ended up with a piece of the crest",
					"none of us wanted to give their piece of the crest up to any of the others",
					"And none of us wanted to face our father",
					"coming home without a complete crest");
				say(player, n, "So can I have your bit?");
				HAVE_YOUR_BIT(player, n);
			}
			else if (option == 1) {
				HAVE_YOUR_BIT(player, n);
			}
		} else if (option == 1) {
			say(player, n, "Nothing I will be on my way");
		} else if (option == 2) {
			say(player, n, "I see you are a chef", "Will you cook me anything?");
			npcsay(player, n, "I would, but I am very busy",
				"Trying to prepare my special fish salad",
				"Which I hope will significantly increase my renown as a master chef");
		}
	}

	private void HAVE_YOUR_BIT(Player player, Npc n) {
		npcsay(player, n, "Well I am the oldest son, by rights it is mine");
		say(player, n, "It's not a lot of use to you without the rest of it though");
		npcsay(player, n, "Well true",
			"So I'll tell you what I'll do",
			"I am struggling to complete my seafood salad",
			"I don't seem to be able to get hold of the ingredients I need",
			"Help me and I'll help you");
		say(player, n, "What are you missing exactly?");
		npcsay(player, n, "I need cooked swordfish,bass,tuna,salmon and shrimp");
		int menu = multi(player, n,
			"Ok I will get those",
			"Why don't you just give me the crest?");
		if (menu == 0) {
			player.updateQuestStage(Quests.FAMILY_CREST, 2);
		} else if (menu == 1) {
			npcsay(player, n, "No I don't want to just give it away");
		}
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return n.getID() == NpcId.CHEF.id();
	}

}
